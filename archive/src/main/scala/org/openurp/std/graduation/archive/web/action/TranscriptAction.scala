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

package org.openurp.std.graduation.archive.web.action

import org.beangle.commons.lang.Strings
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.security.Securities
import org.beangle.web.action.context.Params
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.model.User
import org.openurp.base.std.model.Graduate
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.archive.web.helper.SquadStatHelper
import org.openurp.std.graduation.model.{GraduateResult, GraduateBatch}

class TranscriptAction extends EntityAction[GraduateResult] with ProjectSupport {

  var entityDao: EntityDao = _

  def index(): View = {
    val query = OqlBuilder.from(classOf[GraduateBatch], "batch")
    query.where("batch.project = :project", getProject)
    query.orderBy("batch.graduateOn desc")
    val batches = entityDao.search(query)
    put("batches", batches)
    forward()
  }

  def search(): View = {
    val batchId = Params.getLong("batch.id").get
    val batch = entityDao.get(classOf[GraduateBatch], batchId)
    val helper = new SquadStatHelper(entityDao)
    val batches = Strings.splitToInt(get("batchNo", ""))
    val rs = helper.statCertificate(batch, batches, getBoolean("passed"), None)
    put("squads", rs._1)
    put("squadMap", rs._2)
    put("graduateBatch", batch)
    forward()
  }

  def detail(): View = {
    val batchId = longId("batch")
    val squadIds = longIds("squad")
    val batch = entityDao.get(classOf[GraduateBatch], batchId)
    val query = OqlBuilder.from(classOf[Graduate], "g")
      .where("g.graduateOn=:graduateOn", batch.graduateOn)
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
    put("graduateBatch", batch)
    val builder = OqlBuilder.from(classOf[User], "user")
      .where(" user.code=:code", Securities.user)
    put("user", entityDao.search(builder).headOption)
    forward()
  }
}
