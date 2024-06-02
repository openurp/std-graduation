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

import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.ems.app.web.WebBusinessLogger
import org.beangle.security.Securities
import org.beangle.web.action.annotation.{mapping, param}
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.{Status, View}
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.std.model.Student
import org.openurp.edu.program.domain.ProgramProvider
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.model.{DegreeApply, DegreeResult, GraduateBatch}
import org.openurp.std.graduation.web.helper.DegreeDocHelper

import java.time.Instant

class StdApplyAction extends ActionSupport, EntityAction[DegreeApply], ProjectSupport {
  var entityDao: EntityDao = _
  var programProvider: ProgramProvider = _
  var businessLogger: WebBusinessLogger = _

  def index(): View = {
    val student = getStudent
    val grs = getDegreeResult(student, None)
    put("program", programProvider.getProgram(student))
    grs match {
      case Some(gr) =>
        val daQuery = OqlBuilder.from(classOf[DegreeApply], "da")
        daQuery.where("da.std=:std and da.batch=:batch", student, gr.batch)
        entityDao.search(daQuery).headOption match {
          case Some(da) =>
            put("apply", da)
            forward()
          case None =>
            put("degreeResult", gr)
            forward("welcome")
        }
      case None =>
        forward("welcome")
    }
  }

  private def getDegreeResult(std: Student, batch: Option[GraduateBatch]): Option[DegreeResult] = {
    val builder = OqlBuilder.from(classOf[DegreeResult], "dr")
    builder.where("dr.std=:std", std)
    builder.where("dr.passed=true")
    batch foreach { s =>
      builder.where("dr.batch=:batch", s)
    }
    //builder.where("not exists(from " + classOf[Graduation].getName + " g where g.std=dr.std and g.degree is not null)")
    builder.orderBy("dr.batch.graduateOn desc")
    entityDao.search(builder).headOption
  }

  def doApply(): View = {
    val student = getStudent
    val batch = entityDao.get(classOf[GraduateBatch], getLongId("batch"))
    val daQuery = OqlBuilder.from(classOf[DegreeApply], "da")
    daQuery.where("da.std=:std and da.batch=:batch", student, batch)
    val da = entityDao.search(daQuery).headOption match {
      case Some(da) => da
      case None =>
        val da = new DegreeApply
        da.batch = batch
        da.std = student
        da.degree = programProvider.getProgram(student).get.degree.get
        da
    }
    getDegreeResult(student, Some(batch)) foreach { dr =>
      da.gpa = dr.gpa.getOrElse(0)
    }
    da.updatedAt = Instant.now
    entityDao.saveOrUpdate(da)
    businessLogger.info("申请了学位", student.id, Map("degree.name" -> da.degree.name, "gpa" -> da.gpa.toString))
    redirect("index", "info.save.success")
  }

  @mapping("download/{id}")
  def download(@param("id") id: String): View = {
    val apply = entityDao.get(classOf[DegreeApply], id.toLong)
    val std = getStudent
    if (std == apply.std) {
      val bytes = DegreeDocHelper.toDoc(apply)
      val title = s"${std.code}_${std.name}学位申请表"
      val filename = new String(title.getBytes, "ISO8859-1")
      response.setHeader("Content-disposition", "attachment; filename=" + filename + ".docx")
      response.setHeader("Content-Length", bytes.length.toString)
      val out = response.getOutputStream
      out.write(bytes)
      out.flush()
      out.close()
      null
    } else {
      Status.NotFound
    }
  }

  @mapping(method = "delete")
  def remove(): View = {
    val id = getLong("id").get
    val da = entityDao.get(classOf[DegreeApply], id)
    val std = getStudent
    if (da.std == std && !da.passed.getOrElse(false)) {
      entityDao.remove(da)
      businessLogger.info("删除学位申请", da.id, Map("degree.name" -> da.degree.name, "gpa" -> da.gpa.toString))
      redirect("index", "info.remove.success")
    } else {
      redirect("index", "删除失败")
    }
  }

  private def getStudent: Student = {
    val builder = OqlBuilder.from(classOf[Student], "s")
    builder.where("s.code=:code", Securities.user)
    entityDao.search(builder).head
  }

}
