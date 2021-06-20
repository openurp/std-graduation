/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.std.graduation.archive.web.action

import org.beangle.commons.bean.orderings.MultiPropertyOrdering
import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.context.Params
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.EntityAction
import org.openurp.base.edu.model.Squad
import org.openurp.starter.edu.helper.ProjectSupport
import org.openurp.std.graduation.archive.web.helper.SquadStatHelper
import org.openurp.std.graduation.model.GraduateSession
import org.openurp.std.info.model.Graduation

import scala.collection.mutable

class CertificateAction extends EntityAction[Graduation] with ProjectSupport {

  def index: View = {
    val query = OqlBuilder.from(classOf[GraduateSession], "session")
    query.where("session.project = :project", getProject)
    query.orderBy("session.graduateOn desc,session.name desc")
    val sessions = entityDao.search(query)
    put("sessions", sessions)
    forward()
  }


  def search: View = {
    val sessionId = Params.getLong("session.id").get
    val session = entityDao.get(classOf[GraduateSession], sessionId)
    val helper = new SquadStatHelper(entityDao)
    val batches = Strings.splitToInt(get("batchNo", ""))
    val rs = helper.statCertificate(session, Some(true), batches)
    put("squads", rs._1)
    put("squadMap", rs._2)
    put("graduateSession", session)
    forward()
  }

  /** 班级证书明细 */
  def detail: View = {
    collectDetails()
    forward()
  }

  /** 学生签名表
   * */
  def signature: View = {
    collectDetails()
    forward()
  }

  /** 班级汇总
   * 辅导员签收表
   */
  def stat: View = {
    val sessionId = Params.getLong("session.id").get
    val session = entityDao.get(classOf[GraduateSession], sessionId)
    val helper = new SquadStatHelper(entityDao)
    val batches = Strings.splitToInt(get("batchNo", ""))
    val rs = helper.statCertificate(session, Some(true), batches)
    put("squads", rs._1)
    put("squadMap", rs._2)
    put("graduateSession", session)
    forward()
  }

  /** 延长生签收表
   */
  def deferred(): View = {
    val sessionId = longId("session")
    val session = entityDao.get(classOf[GraduateSession], sessionId)
    val query = OqlBuilder.from(classOf[Graduation], "g")
      .where("g.graduateOn =:graduateOn", session.graduateOn)
    query.join("g.std.state.squad", "adc")
    query.where("g.std.graduateOn < :graduateOn", session.graduateOn) //延长生
    query.where("g.certificateNo is not null")
    val batches = Strings.splitToInt(get("batchNo", ""))
    if (batches.nonEmpty) {
      query.where("g.batchNo in (:batches)", batches)
    }
    put("res", entityDao.search(query))
    put("session", session)
    forward()
  }

  private def collectDetails(): Unit = {
    val squadIds = longIds("squad")
    val sessionId = longId("session")
    val session = entityDao.get(classOf[GraduateSession], sessionId)
    val query = OqlBuilder.from(classOf[Graduation], "g")
      .where("g.graduateOn =:graduateOn", session.graduateOn)
    query.join("g.std.state.squad", "adc")
    query.where("g.certificateNo is not null")
    query.where("g.std.state.grade = g.std.state.squad.grade") //非延长生

    query.where("adc.id in(:classIds)", squadIds)
    val batches = Strings.splitToInt(get("batchNo", ""))
    if (batches.nonEmpty) {
      query.where("g.batchNo in(:batches)", batches)
    }
    val grs = entityDao.search(query)
    val res = Collections.newMap[Squad, mutable.Buffer[Graduation]]
    for (ar <- grs) {
      val adc: Squad = ar.std.state.get.squad.get
      val adArs = res.getOrElseUpdate(adc, Collections.newBuffer[Graduation])
      adArs += ar
    }
    val nres = Collections.newMap[String, mutable.Buffer[Graduation]]
    res foreach { case (k, v) =>
      nres.put(k.id.toString, v)
    }
    val squads = res.keys.toBuffer.sorted(new MultiPropertyOrdering("department.code,code"))
    put("squads", squads)
    put("res", nres)
  }

}
