/*
 * Copyright (C) 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.std.graduation.web.action.archive

import org.beangle.commons.bean.orderings.PropertyOrdering
import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.security.Securities
import org.beangle.webmvc.context.Params
import org.beangle.webmvc.support.ActionSupport
import org.beangle.webmvc.support.action.EntityAction
import org.beangle.webmvc.view.View
import org.openurp.base.std.model.{Graduate, Squad}
import org.openurp.starter.web.helper.ProjectProfile
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.model.GraduateBatch
import org.openurp.std.graduation.web.helper.{MathHelper, SquadStatHelper}

import java.time.LocalDate
import scala.collection.mutable

/** 毕业证签收表
 *
 */
class CertificateAction extends ActionSupport, EntityAction[Graduate], ProjectSupport {

  var entityDao: EntityDao = _

  def index(): View = {
    val query = OqlBuilder.from(classOf[GraduateBatch], "batch")
    query.where("batch.project = :project", getProject)
    query.orderBy("batch.graduateOn desc,batch.name desc")
    val batches = entityDao.search(query)
    put("batches", batches)
    forward()
  }

  def search(): View = {
    val batchId = Params.getLong("batch.id").get
    val batch = entityDao.get(classOf[GraduateBatch], batchId)
    val helper = new SquadStatHelper(entityDao)
    val batches = Strings.splitToInt(get("batchNo", ""))
    val rs = helper.statCertificate(batch, batches, Some(true), Some(false))
    put("squads", rs._1)
    put("squadMap", rs._2)
    put("graduateBatch", batch)
    forward()
  }

  /** 班级证书明细 */
  def detail(): View = {
    collectDetails()
    forward()
  }

  /** 学生签名表
   * */
  def signature(): View = {
    collectDetails()
    val project = getProject
    ProjectProfile.set(project)
    forward(if project.lle then "signature_lle" else "signature")
  }

  private def collectDetails(): Unit = {
    val squadIds = getLongIds("squad")
    val batchId = getLongId("batch")
    val batch = entityDao.get(classOf[GraduateBatch], batchId)
    put("batch", batch)
    val query = OqlBuilder.from(classOf[Graduate], "g")
      .where("g.graduateOn =:graduateOn", batch.graduateOn)
    query.join("g.std.state.squad", "adc")
    query.where("g.certificateNo is not null")
    //query.where("g.std.state.grade = g.std.state.squad.grade") //非延长生

    query.where("adc.id in(:classIds)", squadIds)
    val batches = Strings.splitToInt(get("batchNo", ""))
    if (batches.nonEmpty) {
      query.where("g.batchNo in(:batches)", batches)
    }
    val grs = entityDao.search(query)
    val res = Collections.newMap[Squad, mutable.Buffer[Graduate]]
    for (ar <- grs) {
      val adc = ar.std.state.get.squad.get
      val adArs = res.getOrElseUpdate(adc, Collections.newBuffer[Graduate])
      adArs += ar
    }
    val nres = Collections.newMap[String, mutable.Buffer[Graduate]]
    res foreach { case (k, v) =>
      nres.put(k.id.toString, v)
    }
    val squads = res.keys.toBuffer.sorted(PropertyOrdering.by("department.code,code"))
    put("squads", squads)
    put("res", nres)
  }

  /** 班级汇总
   * 辅导员签收表
   */
  def stat(): View = {
    val batchId = Params.getLong("batch.id").get
    val batch = entityDao.get(classOf[GraduateBatch], batchId)
    val helper = new SquadStatHelper(entityDao)
    val batches = Strings.splitToInt(get("batchNo", ""))
    val rs = helper.statCertificate(batch, batches, Some(true), Some(false))
    put("squads", rs._1)
    put("squadMap", rs._2)
    put("graduateBatch", batch)
    val project = getProject
    ProjectProfile.set(project)
    forward(if project.lle then "stat_lle" else "stat")
  }

  /** 延长生签收表
   */
  def deferred(): View = {
    val batchId = getLongId("batch")
    val batch = entityDao.get(classOf[GraduateBatch], batchId)
    val query = OqlBuilder.from(classOf[Graduate], "g")
      .where("g.graduateOn =:graduateOn", batch.graduateOn)
    query.join("g.std.state.squad", "adc")
    query.where("g.std.graduationDeferred=true") //延长生
    query.where("g.certificateNo is not null")
    val batches = Strings.splitToInt(get("batchNo", ""))
    if (batches.nonEmpty) {
      query.where("g.batchNo in (:batches)", batches)
    }
    put("res", entityDao.search(query))
    put("batch", batch)
    forward()
  }


  /** 班级材料归档
   *
   * @return
   */
  def squadArchives(): View = {
    val batchId = getLongId("batch")
    val batch = entityDao.get(classOf[GraduateBatch], batchId)

    val query = OqlBuilder.from[Array[AnyRef]](classOf[Graduate].getName, "g")
      .where("g.graduateOn =:graduateOn", batch.graduateOn) //按照毕业日期进行查询，有可能学位授予日期会晚于这个日期
    query.join("g.std.state.squad", "adc")
    query.groupBy("adc.id");
    query.select("adc.id,count(*)")
    var datas = entityDao.search(query)
    for (d <- datas) {
      d(0) = entityDao.get(classOf[Squad], d(0).asInstanceOf[Long])
    }
    datas = datas.sorted(PropertyOrdering.by("[0].department.code,[0].code"))
    put("datas", datas)
    put("graduateBatch", batch)

    val archiveCategory = get("archiveCategory", "毕业文凭签收表")
    val startArchiveNo = getInt("startArchiveNo", 1)
    val archiver = get("archiver", Securities.user)
    val archiveOn = getDate("archiveOn").getOrElse(LocalDate.parse((batch.graduateOn.getYear + 1).toString + "-06-30"))

    put("archiveCategory", archiveCategory);
    put("startArchiveNo", startArchiveNo);
    put("archiver", archiver);
    put("archiveOn", archiveOn);
    put("math", MathHelper)
    forward()
  }

  /** 学生材料归档
   *
   * @return
   */
  def stdArchives(): View = {
    val batchId = getLongId("batch")
    val batch = entityDao.get(classOf[GraduateBatch], batchId)

    val query = OqlBuilder.from(classOf[Graduate], "g")
      .where("g.graduateOn =:graduateOn", batch.graduateOn) //按照毕业日期进行查询，有可能学位授予日期会晚于这个日期
    var datas = entityDao.search(query)
    val results = Collections.newMap[String, mutable.Buffer[Graduate]]
    val squads = Collections.newSet[Squad]
    val overGraduated = Collections.newSet[Graduate]
    for (ar <- datas) {
      if (ar.std.graduationDeferred) overGraduated.add(ar)
      else {
        ar.std.state.get.squad foreach { squad =>
          squads.add(squad)
          val classResults = results.getOrElseUpdate(squad.id.toString, new mutable.ArrayBuffer[Graduate])
          classResults.addOne(ar)
        }
      }
    }
    datas = datas.sorted(PropertyOrdering.by("std.department.code,std.squad.code"))
    put("overGraduated", overGraduated)
    put("squads", squads)
    put("res", results)
    put("graduateBatch", batch)

    val archiveCategory = get("archiveCategory", "毕业学生成绩单")
    val startArchiveNo = getInt("startArchiveNo", 1)
    val archiver = get("archiver", Securities.user)
    val archiveOn = getDate("archiveOn").getOrElse(LocalDate.parse((batch.graduateOn.getYear + 1).toString + "-06-30"))

    put("archiveCategory", archiveCategory)
    put("startArchiveNo", startArchiveNo)
    put("archiver", archiver)
    put("archiveOn", archiveOn)
    forward()
  }
}
