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
package org.openurp.edu.graduation.audit.web.action

import org.beangle.data.dao.OqlBuilder
import org.beangle.data.transfer.exporter.ExportSetting
import org.beangle.webmvc.api.annotation.ignore
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.beangle.webmvc.entity.helper.PopulateHelper
import org.openurp.edu.base.web.ProjectSupport
import org.openurp.edu.graduation.audit.model.{DegreeApply, GraduateSession}
import org.openurp.edu.graduation.audit.web.helper.{ApplyDataConvertor, DocHelper}

class DegreeApplyAuditAction extends RestfulAction[DegreeApply] with ProjectSupport {

  override def indexSetting(): Unit = {
    val query = OqlBuilder.from(classOf[GraduateSession], "gs")
    put("sessions", entityDao.search(query))
    put("departs", getDeparts)
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
    import scala.jdk.javaapi.CollectionConverters._
    val applies = sorted.map(x => asJava(ApplyDataConvertor.convert(x)))
    setting.context.put("school", getProject.school.name)
    setting.context.put("applies", applies)
  }
}
