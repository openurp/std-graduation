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

package org.openurp.std.graduation.web.action.degree

import org.beangle.data.dao.OqlBuilder
import org.beangle.doc.transfer.exporter.ExportContext
import org.beangle.web.action.annotation.ignore
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.beangle.webmvc.support.helper.QueryHelper
import org.openurp.base.model.Project
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.model.{DegreeApply, GraduateBatch}
import org.openurp.std.graduation.web.helper.{ApplyDataConvertor, DegreeDocHelper}

/** 学位申请审核
 */
class ApplyAuditAction extends RestfulAction[DegreeApply], ExportSupport[DegreeApply], ProjectSupport {

  override def indexSetting(): Unit = {
    given project: Project = getProject

    val query = OqlBuilder.from(classOf[GraduateBatch], "batch")
    query.where("batch.project = :project", getProject)
    query.orderBy("batch.graduateOn desc,batch.name desc")
    val batches = entityDao.search(query)
    put("batches", batches)
    put("departs", getDeparts)
  }

  override def getQueryBuilder: OqlBuilder[DegreeApply] = {
    val query = super.getQueryBuilder
    QueryHelper.dateBetween(query, null, "updatedAt", "applyOn", "applyOn")
    query
  }

  def download(): View = {
    val apply = entityDao.get(classOf[DegreeApply], getLongId("apply"))
    val bytes = DegreeDocHelper.toDoc(apply)
    val std = apply.std
    val title = s"${std.code}_${std.name}学位申请表"
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
  override def configExport(context: ExportContext): Unit = {
    val selectIds = getLongIds(simpleEntityName)
    val items =
      if (selectIds.isEmpty) {
        val builder = getQueryBuilder
        entityDao.search(builder.limit(null))
      } else {
        entityDao.findBy(classOf[DegreeApply], "id", selectIds)
      }
    val sorted = items.toBuffer.sortWith((x, y) => x.std.code.compare(y.std.code) < 0)
    import scala.jdk.javaapi.CollectionConverters.*
    val applies = sorted.map(x => asJava(ApplyDataConvertor.convert(x)))
    context.put("school", getProject.school.name)
    context.put("applies", applies)
  }

  override protected def simpleEntityName: String = "apply"
}
