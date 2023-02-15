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

package org.openurp.std.graduation.archive.web.helper

import org.beangle.commons.bean.orderings.MultiPropertyOrdering
import org.beangle.commons.collection.Collections
import org.beangle.commons.collection.page.SinglePage
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.base.std.model.Squad
import org.openurp.std.graduation.model.{DegreeResult, GraduateResult, GraduateBatch}
import org.openurp.base.std.model.Graduate

class SquadStatHelper(entityDao: EntityDao) {

  /** 统计毕业证书
   *
   * @param batch
   * @param batches
   * @return
   */
  def statCertificate(batch: GraduateBatch, batches: Iterable[Int], passed: Option[Boolean], deferred: Option[Boolean]):
  (collection.Seq[Squad], collection.Map[Squad, Any]) = {
    val query = OqlBuilder.from[Array[Any]](classOf[Graduate].getName, "g")
      .where("g.std.project=:project", batch.project)
      .where("g.graduateOn = :graduateOn", batch.graduateOn)

    passed foreach { p =>
      if (p) query.where("g.certificateNo is not null")
      else query.where("g.certificateNo is null")
    }
    deferred foreach { d =>
      if (d) query.where("g.std.state.grade != g.std.state.squad.grade")
      else query.where("g.std.state.grade=g.std.state.squad.grade") //非延长学生
    }
    if (batches.nonEmpty) {
      query.where("g.batchNo in(:batches)", batches)
    }
    query.join("g.std.state.squad", "adc")
    deferred foreach { df =>
      if (df) query.where("g.std.graduateOn <= g.graduateOn")
      else query.where("g.std.graduateOn = g.graduateOn")
    }
    query.groupBy("adc.id")
    query.select("adc.id,count(*)")
    val datas = entityDao.search(query).toBuffer
    buildMap(datas)
  }

  /** 统计学位证书
   *
   * @param batch
   * @param batches
   * @return
   */
  def statDiploma(batch: GraduateBatch, batches: Iterable[Int], deferred: Option[Boolean]):
  (collection.Seq[Squad], collection.Map[Squad, Any]) = {
    val query = OqlBuilder.from[Array[Any]](classOf[Graduate].getName, "g")
      .where("g.project=:project", batch.project)
      .where("g.degreeAwardOn = :graduateOn", batch.graduateOn)
    query.where("g.diplomaNo is not null")
    if (batches.nonEmpty) {
      query.where(
        "exists(from " + classOf[DegreeResult].getName + " dr where dr.batch=:batch and dr.std=g.std and gr.batchNo in(:batches))")
      query.param("batch", batch)
      query.param("batches", batches)
    }
    deferred foreach { df =>
      if (df) query.where("g.std.graduateOn <= g.graduateOn")
      else query.where("g.std.graduateOn = g.graduateOn")
    }
    query.join("g.std.state.squad", "adc")
    query.groupBy("adc.id")
    query.select("adc.id,count(*)")
    val datas = entityDao.search(query).toBuffer
    buildMap(datas)
  }

  private def buildMap(datas: collection.Seq[Array[Any]]): (collection.Seq[Squad], collection.Map[Squad, Any]) = {
    val squadMap = Collections.newMap[Squad, Any]
    for (d <- datas) {
      squadMap.put(entityDao.get(classOf[Squad], d(0).asInstanceOf[Number].longValue()), d(1))
    }
    val squads = squadMap.keys.toBuffer.sorted(new MultiPropertyOrdering("department.code,code"))
    val page = new SinglePage(1, squads.size, squads.size, squads)
    (page, squadMap)
  }

}
