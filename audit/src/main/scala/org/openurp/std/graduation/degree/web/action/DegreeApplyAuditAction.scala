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

import org.beangle.data.dao.OqlBuilder
import org.beangle.data.orm.hibernate.QuerySupport
import org.beangle.data.transfer.exporter.ExportSetting
import org.beangle.web.action.annotation.ignore
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.RestfulAction
import org.beangle.webmvc.support.helper.{PopulateHelper, QueryHelper}
import org.openurp.starter.edu.helper.ProjectSupport
import org.openurp.std.graduation.degree.web.helper.{ApplyDataConvertor, DocHelper}
import org.openurp.std.graduation.model.{DegreeApply, GraduateSession}

/** 学位申请审核
 */
class DegreeApplyAuditAction extends RestfulAction[DegreeApply] with ProjectSupport {

  override def indexSetting(): Unit = {
    val query = OqlBuilder.from(classOf[GraduateSession], "session")
    query.where("session.project = :project", getProject)
    query.orderBy("session.graduateOn desc,session.name desc")
    val sessions = entityDao.search(query)
    put("sessions", sessions)
    put("departs", getDeparts)
  }

  override def getQueryBuilder: OqlBuilder[DegreeApply] = {
    val query = super.getQueryBuilder
    QueryHelper.dateBetween(query, "degreeApply", "updatedAt", "applyOn", "applyOn")
    query
  }

  def download(): View = {
    val apply = entityDao.get(classOf[DegreeApply], longId("degreeApply"))
    val bytes = DocHelper.toDoc(apply)
    val std = apply.std
    val title = s"${std.user.code}_${std.user.name}学位申请表"
    val filename = new String(title.getBytes, "ISO8859-1")
    response.setHeader("Content-disposition", "attachment; filename=" + filename + ".docx")
    response.setHeader("Content-Length", bytes.length.toString)
    val out = response.getOutputStream
    out.write(bytes)
    out.flush()
    out.close()
    null
  }

  @ignore
  override def configExport(setting: ExportSetting): Unit = {
    val selectIds = ids(simpleEntityName, PopulateHelper.getType(entityType).id.clazz)
    val items =
      if (selectIds.isEmpty) {
        val builder = getQueryBuilder
        entityDao.search(builder.limit(null))
      } else {
        entityDao.findBy(entityType, "id", selectIds)
      }
    val sorted = items.toBuffer.sortWith((x, y) => x.std.user.code.compare(y.std.user.code) < 0)
    import scala.jdk.javaapi.CollectionConverters.*
    val applies = sorted.map(x => asJava(ApplyDataConvertor.convert(x)))
    setting.context.put("school", getProject.school.name)
    setting.context.put("applies", applies)
  }
}
