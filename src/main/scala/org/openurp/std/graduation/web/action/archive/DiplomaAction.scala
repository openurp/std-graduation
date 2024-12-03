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
import org.beangle.webmvc.context.Params
import org.beangle.webmvc.support.ActionSupport
import org.beangle.webmvc.view.View
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.std.model.{Graduate, Squad, Student}
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.web.helper.SquadStatHelper
import org.openurp.std.graduation.model.{DegreeResult, GraduateBatch}

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

  def report(): View = {
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
    forward()
  }

  def detail(): View = {
    val batchId = getLongId("batch")
    val squadIds = getLongIds("squad")
    val query: OqlBuilder[DegreeResult] = OqlBuilder.from(classOf[DegreeResult], "ar")
      .where("ar.batch.id=:batchId", batchId)
      .where("ar.passed=true")
    query.join("ar.std.state.squad", "adc")
    query.where("adc.id in(:classIds)", squadIds)
    val ars = entityDao.search(query)
    val res = Collections.newMap[Squad, mutable.Buffer[DegreeResult]]
    for (ar <- ars) {
      val adc: Squad = ar.std.state.get.squad.get
      val adArs = res.getOrElseUpdate(adc, Collections.newBuffer[DegreeResult])
      adArs += ar
    }
    val nres = Collections.newMap[String, mutable.Buffer[DegreeResult]]
    res foreach { case (k, v) =>
      nres.put(k.id.toString, v)
    }
    val squads = res.keys.toBuffer.sorted(PropertyOrdering.by("department.code,code"))
    val graduateMap = Collections.newMap[Student, Graduate]
    for (gr <- ars) {
      val gBuilder = OqlBuilder.from(classOf[Graduate], "graduate")
      gBuilder.where("graduate.std=:std", gr.std)
      val graduations = entityDao.search(gBuilder)
      for (graduation <- graduations) {
        graduateMap.put(gr.std, graduation)
      }
    }
    put("graduationMap", graduateMap)
    put("squads", squads)
    put("res", nres)
    forward()
  }

}
