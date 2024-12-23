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
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.model.{DegreeResult, GraduateBatch}
import org.openurp.std.graduation.web.helper.{MathHelper, SquadStatHelper}

import java.time.LocalDate
import scala.collection.mutable

/** 学位证签收表
 *
 */
class DiplomaAction extends ActionSupport, EntityAction[DegreeResult], ProjectSupport {

  var entityDao: EntityDao = _

  def index(): View = {
    val query = OqlBuilder.from(classOf[GraduateBatch], "batch")
    query.where("batch.project = :project", getProject)
    query.orderBy("batch.graduateOn desc,batch.name desc")
    val batches = entityDao.search(query)
    put("batches", batches)
    forward()
  }

  def stat(): View = {
    val batchId = Params.getLong("batch.id").get
    val batch = entityDao.get(classOf[GraduateBatch], batchId)
    val helper = new SquadStatHelper(entityDao)
    val batches = Strings.splitToInt(get("batchNo", ""))
    val rs = helper.statDiploma(batch, batches, Some(false))
    put("squads", rs._1)
    put("squadMap", rs._2)
    put("graduateBatch", batch)
    forward()
  }

  def search(): View = {
    val batchId = Params.getLong("batch.id").get
    val batch = entityDao.get(classOf[GraduateBatch], batchId)
    val helper = new SquadStatHelper(entityDao)
    val batches = Strings.splitToInt(get("batchNo", ""))
    val rs = helper.statDiploma(batch, batches, Some(false))
    put("squads", rs._1)
    put("squadMap", rs._2)
    put("graduateBatch", batch)
    put("project", getProject)
    forward()
  }

  def signature(): View = {
    val batchId = getLongId("batch")
    val batch = entityDao.get(classOf[GraduateBatch], batchId)
    put("batch", batch)
    val squadIds = getLongIds("squad")

    val query = OqlBuilder.from(classOf[Graduate], "g")
      .where("g.graduateOn =:graduateOn", batch.graduateOn) //按照毕业日期进行查询，有可能学位授予日期会晚于这个日期
    query.join("g.std.state.squad", "adc")
    query.where("g.diplomaNo is not null")
    query.where("adc.id in(:classIds)", squadIds)
    val ars = entityDao.search(query)

    val res = Collections.newMap[Squad, mutable.Buffer[Graduate]]
    for (ar <- ars) {
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
    forward()
  }

  /** 班级归档资料
   *
   * @return
   */
  def squadArchives(): View = {
    val batchId = getLongId("batch")
    val batch = entityDao.get(classOf[GraduateBatch], batchId)

    val query = OqlBuilder.from[Array[AnyRef]](classOf[Graduate].getName, "g")
      .where("g.graduateOn =:graduateOn", batch.graduateOn) //按照毕业日期进行查询，有可能学位授予日期会晚于这个日期
    query.where("g.diplomaNo is not null")
    query.join("g.std.state.squad", "adc")
    query.groupBy("adc.id")
    query.select("adc.id,count(*)")
    var datas = entityDao.search(query)
    for (d <- datas) {
      d(0) = entityDao.get(classOf[Squad], d(0).asInstanceOf[Long])
    }
    datas = datas.sorted(PropertyOrdering.by("[0].department.code,[0].code"))
    put("datas", datas)
    put("graduateBatch", batch)
    val archiveCategory = get("archiveCategory", "学士学位文凭签收表")
    val startArchiveNo = getInt("startArchiveNo", 1)
    val archiver = get("archiver", Securities.user)
    val archiveOn = getDate("archiveOn").getOrElse(LocalDate.parse((batch.graduateOn.getYear + 1).toString + "-06-30"))
    put("archiveCategory", archiveCategory)
    put("startArchiveNo", startArchiveNo)
    put("archiver", archiver)
    put("archiveOn", archiveOn)
    put("math", MathHelper)
    forward()
  }

  /** 按照班级打印学位授予书
   *
   * @return
   */
  def awardingCertificate(): View = {
    val batchId = getLongId("batch")
    val batch = entityDao.get(classOf[GraduateBatch], batchId)
    val squadIds = getLongIds("squad")
    val query = OqlBuilder.from(classOf[Graduate].getName, "g")
      .where("g.graduateOn =:graduateOn", batch.graduateOn) //按照毕业日期进行查询，有可能学位授予日期会晚于这个日期
    query.where("g.diplomaNo is not null")
    query.join("g.std.state.squad", "adc")
    query.where("adc.id in(:classIds)", squadIds)
    put("graduates", entityDao.search(query))
    forward()
  }

}
