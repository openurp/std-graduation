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
package org.openurp.edu.graduation.archive.web.helper

import org.beangle.commons.bean.orderings.MultiPropertyOrdering
import org.beangle.commons.collection.Collections
import org.beangle.commons.collection.page.SinglePage
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.webmvc.api.context.{ActionContext, Params}
import org.openurp.edu.base.model.Squad
import org.openurp.edu.graduation.audit.model.{DegreeResult, GraduateResult, GraduateSession}

class DegreeStatHelper(entityDao: EntityDao) {

    def statBySquad(): Unit = {
    val sessionId= Params.getLong("session.id").get
    val session = entityDao.get(classOf[GraduateSession], sessionId)
    val query = OqlBuilder.from[Array[Any]](classOf[DegreeResult].getName, "ar")
      .where("ar.session=:session", session)
    query.join("ar.std.state.squad", "adc")
    query.groupBy("adc.id")
    query.select("adc.id,count(*)")
    val datas = entityDao.search(query).toBuffer
    val squadMap = Collections.newMap[Squad, Any]
    for (d <- datas) {
      squadMap.put(entityDao.get(classOf[Squad], d(0).asInstanceOf[Number].longValue()), d(1))
    }
    val squads = squadMap.keys.toBuffer.sorted(new MultiPropertyOrdering("department.code,code"))
    val page = new SinglePage(1, squads.size, squads.size, squads)
    ActionContext.current.attribute("squads", page)
    ActionContext.current.attribute("squadMap", squadMap)
    ActionContext.current.attribute("graduateSession", session)
  }

}
