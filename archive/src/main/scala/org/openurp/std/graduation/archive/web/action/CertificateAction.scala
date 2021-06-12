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
import org.openurp.base.edu.model.{Squad, Student}
import org.openurp.starter.edu.helper.ProjectSupport
import org.openurp.std.graduation.archive.web.helper.SquadStatHelper
import org.openurp.std.graduation.model.{GraduateResult, GraduateSession}
import org.openurp.std.info.model.Graduation

import scala.collection.mutable

class CertificateAction extends EntityAction[GraduateResult] with ProjectSupport {

  def index: View = {
    val query = OqlBuilder.from(classOf[GraduateSession], "session")
    query.where("session.project = :project", getProject)
    query.orderBy("session.graduateOn desc,session.name desc")
    val sessions = entityDao.search(query)
    put("sessions", sessions)
    forward()
  }

  def report: View = {
    val sessionId = Params.getLong("session.id").get
    val session = entityDao.get(classOf[GraduateSession], sessionId)
    val helper = new SquadStatHelper(entityDao)
    val batches = Strings.splitToInt(get("batch", ""))
    val rs = helper.statCertificate(session, batches)
    put("squads", rs._1)
    put("squadMap", rs._2)
    put("graduateSession", session)
    forward()
  }

  def search: View = {
    val sessionId = Params.getLong("session.id").get
    val session = entityDao.get(classOf[GraduateSession], sessionId)
    val helper = new SquadStatHelper(entityDao)
    val batches = Strings.splitToInt(get("batch", ""))
    val rs = helper.statCertificate(session, batches)
    put("squads", rs._1)
    put("squadMap", rs._2)
    put("graduateSession", session)
    forward()
  }

  def detail: View = {
    collectDetails()
    forward()
  }

  def signatureReport: View = {
    collectDetails()
    forward()
  }

  private def collectDetails(): Unit = {
    val sessionId = longId("session")
    val squadIds = longIds("squad")
    val query: OqlBuilder[GraduateResult] = OqlBuilder.from(classOf[GraduateResult], "ar")
      .where("ar.session.id=:sessionId", sessionId)
    query.join("ar.std.state.squad", "adc")
    query.where("ar.passed=true")
    query.where("adc.id in(:classIds)", squadIds)
    val batches = Strings.splitToInt(get("batch", ""))
    if (batches.nonEmpty) {
      query.where("ar.batch in(:batches)", batches)
    }
    val ars = entityDao.search(query)
    val res = Collections.newMap[Squad, mutable.Buffer[GraduateResult]]
    for (ar <- ars) {
      val adc: Squad = ar.std.state.get.squad.get
      val adArs = res.getOrElseUpdate(adc, Collections.newBuffer[GraduateResult])
      adArs += ar
    }
    val nres = Collections.newMap[String, mutable.Buffer[GraduateResult]]
    res foreach { case (k, v) =>
      nres.put(k.id.toString, v)
    }
    val squads = res.keys.toBuffer.sorted(new MultiPropertyOrdering("department.code,code"))
    val graduationMap = Collections.newMap[Student, Graduation]
    for (gr <- ars) {
      val gBuilder = OqlBuilder.from(classOf[Graduation], "graduation")
      gBuilder.where("graduation.std=:std", gr.std)
      val graduations = entityDao.search(gBuilder)
      for (graduation <- graduations) {
        graduationMap.put(gr.std, graduation)
      }
    }
    put("graduationMap", graduationMap)
    put("squads", squads)
    put("res", nres)
  }

}
