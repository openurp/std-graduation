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

package org.openurp.std.graduation.audit.web.action

import java.time.Instant
import org.beangle.data.dao.OqlBuilder
import org.beangle.security.Securities
import org.beangle.web.action.annotation.{mapping, param}
import org.beangle.web.action.view.{Status, View}
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.std.model.Student
import org.openurp.std.graduation.model.{DegreeApply, DegreeResult, GraduateSession}
import org.openurp.std.graduation.audit.web.helper.DocHelper
import org.openurp.edu.program.domain.ProgramProvider
import org.openurp.starter.edu.helper.ProjectSupport

class StdDegreeApplyAction extends EntityAction[DegreeApply] with ProjectSupport {

  var programProvider: ProgramProvider = _

  def index(): View = {
    val student = getStudent
    val grs = getDegreeResult(student, None)
    put("program", programProvider.getProgram(student))
    grs match {
      case Some(gr) =>
        val daQuery = OqlBuilder.from(classOf[DegreeApply], "da")
        daQuery.where("da.std=:std and da.session=:session", student, gr.session)
        entityDao.search(daQuery).headOption match {
          case Some(da) =>
            put("degreeApply", da)
            forward()
          case None =>
            put("degreeResult", gr)
            forward("welcome")
        }
      case None =>
        forward("welcome")
    }
  }

  private def getDegreeResult(std: Student, session: Option[GraduateSession]): Option[DegreeResult] = {
    val builder = OqlBuilder.from(classOf[DegreeResult], "dr")
    builder.where("dr.std=:std", std)
    builder.where("dr.passed=true")
    session foreach { s =>
      builder.where("dr.session=:session", s)
    }
    //builder.where("not exists(from " + classOf[Graduation].getName + " g where g.std=dr.std and g.degree is not null)")
    builder.orderBy("dr.session.graduateOn desc")
    entityDao.search(builder).headOption
  }

  def apply(): View = {
    val student = getStudent
    val session = entityDao.get(classOf[GraduateSession], longId("session"))
    val daQuery = OqlBuilder.from(classOf[DegreeApply], "da")
    daQuery.where("da.std=:std and da.session=:session", student, session)
    val da = entityDao.search(daQuery).headOption match {
      case Some(da) => da
      case None =>
        val da = new DegreeApply
        da.session = session
        da.std = student
        da.degree = programProvider.getProgram(student).get.degree.get
        da
    }
    getDegreeResult(student, Some(session)) foreach { dr =>
      da.gpa = dr.gpa
    }
    da.updatedAt = Instant.now
    entityDao.saveOrUpdate(da)
    redirect("index", "info.save.success")
  }

  @mapping("download/{id}")
  def download(@param("id") id: String): View = {
    val apply = entityDao.get(classOf[DegreeApply], id.toLong)
    val std = getStudent
    if (std == apply.std) {
      val bytes = DocHelper.toDoc(apply)
      val title = s"${std.user.code}_${std.user.name}学位申请表"
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
      redirect("index", "info.remove.success")
    } else {
      redirect("index", "删除失败")
    }
  }

  private def getStudent: Student = {
    val builder = OqlBuilder.from(classOf[Student], "s")
    builder.where("s.user.code=:code", Securities.user)
    entityDao.search(builder).head
  }

}
