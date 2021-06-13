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

import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.security.Securities
import org.beangle.webmvc.api.context.Params
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.EntityAction
import org.openurp.base.model.User
import org.openurp.starter.edu.helper.ProjectSupport
import org.openurp.std.graduation.archive.web.helper.SquadStatHelper
import org.openurp.std.graduation.model.{GraduateResult, GraduateSession}
import org.openurp.std.info.model.Graduation

class TranscriptAction extends EntityAction[GraduateResult] with ProjectSupport {

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
    val rs = helper.statCertificate(session, getBoolean("passed"), batches)
    put("squads", rs._1)
    put("squadMap", rs._2)
    put("graduateSession", session)
    forward()
  }

  def detail: View = {
    val sessionId = longId("session")
    val squadIds = longIds("squad")
    val session = entityDao.get(classOf[GraduateSession], sessionId)
    val query = OqlBuilder.from(classOf[Graduation], "g")
      .where("g.graduateOn=:graduateOn", session.graduateOn)
    query.join("g.std.state.squad", "adc")
    query.where("adc.id in(:classIds)", squadIds)
    val batches = Strings.splitToInt(get("batchNo", ""))
    if (batches.nonEmpty) {
      query.where("g.batchNo in  (:batches)", batches)
    }
    getBoolean("passed") foreach { p =>
      if (p) query.where("g.certificateNo is not null")
      else query.where("g.certificateNo is null")
    }
    val gs = entityDao.search(query)
    put("gs", gs)
    put("graduateSession", session)
    val builder = OqlBuilder.from(classOf[User], "user")
      .where(" user.code=:code", Securities.user)
    put("user", entityDao.search(builder).headOption)
    forward()
  }
}
