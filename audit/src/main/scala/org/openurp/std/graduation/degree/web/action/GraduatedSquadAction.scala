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

package org.openurp.std.graduation.degree.web.action

import org.beangle.commons.bean.orderings.MultiPropertyOrdering
import org.beangle.commons.collection.Collections
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.std.model.{Graduate, Squad, Student}
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.model.{GraduateBatch, GraduateResult}

import scala.collection.mutable

class GraduatedSquadAction extends EntityAction[GraduateResult] with ProjectSupport {

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
    val batchId = longId("batch")
    val batch = entityDao.get(classOf[GraduateBatch], batchId)
    val query = OqlBuilder.from[Array[Any]](classOf[GraduateResult].getName, "ar")
      .where("ar.batch=:batch", batch)
    query.join("ar.std.state.squad", "adc")
    query.groupBy("adc.id")
    query.select("adc.id,count(*)")
    var datas = entityDao.search(query).toBuffer
    for (d <- datas) {
      d(0) = entityDao.get(classOf[Squad], d(0).asInstanceOf[Number].longValue())
    }
    datas = datas.sorted(new MultiPropertyOrdering("[0].department.code,[0].code"))
    put("datas", datas)
    put("graduateBatch", batch)
    forward()
  }

  def diploma(): View = {
    val batchId = longId("batch")
    val squadIds = longIds("squad")
    val query = OqlBuilder.from(classOf[GraduateResult], "ar")
      .where("ar.batch.id=:batchId", batchId)
    query.join("ar.std.state.squad", "adc")
    query.where("adc.id in(:classIds)", squadIds)
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
    val graduateMap = Collections.newMap[Student, Graduate]
    for (gr <- ars) {
      val gBuilder = OqlBuilder.from(classOf[Graduate], "graduate")
      gBuilder.where("graduate.std=:std", gr.std)
      val graduations = entityDao.search(gBuilder)
      for (graduation <- graduations) {
        graduateMap.put(gr.std, graduation)
      }
    }
    put("graduateMap", graduateMap)
    put("squads", squads)
    put("res", nres)
    forward()
  }

  def test(): View = {
    forward()
  }

}
