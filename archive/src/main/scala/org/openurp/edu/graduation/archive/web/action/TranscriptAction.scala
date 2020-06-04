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
package org.openurp.edu.graduation.archive.web.action

import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.context.Params
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.EntityAction
import org.openurp.edu.base.web.ProjectSupport
import org.openurp.edu.graduation.archive.web.helper.GraduateStatHelper
import org.openurp.edu.graduation.audit.model.{GraduateResult, GraduateSession}

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
    new GraduateStatHelper(entityDao).statBySquad()
    forward()
  }

  def detail: View = {
    val sessionId = longId("session")
    val squadIds = longIds("squad")
    val query: OqlBuilder[GraduateResult] = OqlBuilder.from(classOf[GraduateResult], "ar")
      .where("ar.session.id=:sessionId", sessionId)
    query.join("ar.std.state.squad", "adc")
    query.where("adc.id in(:classIds)", squadIds)
    Params.getBoolean("passed") foreach { f =>
      query.where("ar.passed=" + f.toString)
    }
    Params.getInt("batch") foreach { batch =>
      query.where("ar.batch=:batch", batch)
    }
    val grs = entityDao.search(query)
    put("grs", grs)
    put("graduateSession", entityDao.get(classOf[GraduateSession], sessionId))
    forward()
  }
}
