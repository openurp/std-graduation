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
package org.openurp.std.graduation.audit.web.action

import java.time.Instant

import org.beangle.data.dao.OqlBuilder
import org.beangle.security.Securities
import org.beangle.webmvc.api.annotation.mapping
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.EntityAction
import org.openurp.base.edu.model.Student
import org.openurp.edu.grade.course.service.impl.BestGradeFilter
import org.openurp.std.graduation.model.{GraduateResult, GraduateSession}
import org.openurp.std.graduation.degree2nd.model.Degree2ndApply
import org.openurp.starter.edu.helper.ProjectSupport

class StdDegree2ndApplyAction extends EntityAction[Degree2ndApply] with ProjectSupport {

  var bestGradeFilter: BestGradeFilter = _

  def index(): View = {
    val student = getStudent
    val grs = getGraduateResult(student, None)
    grs match {
      case Some(gr) =>
        val daQuery = OqlBuilder.from(classOf[Degree2ndApply], "da")
        daQuery.where("da.std=:std and da.session=:session", student, gr.session)
        entityDao.search(daQuery).headOption match {
          case Some(da) =>
            put("degree2ndApply", da)
            forward()
          case None =>
            put("graduateResult", gr)
            forward("welcome")
        }
      case None =>
        forward("welcome")
    }
  }

  private def getGraduateResult(std: Student, session: Option[GraduateSession]): Option[GraduateResult] = {
    val builder = OqlBuilder.from(classOf[GraduateResult], "gr")
    builder.where("gr.std=:std", std)
    //builder.where("gr.passed=true")
    session foreach { s =>
      builder.where("gr.session=:session", s)
    }
    builder.orderBy("gr.session.graduateOn desc")
    entityDao.search(builder).headOption
  }

  def apply(): View = {
    val student = getStudent
    val session = entityDao.get(classOf[GraduateSession], longId("session"))
    val daQuery = OqlBuilder.from(classOf[Degree2ndApply], "da")
    daQuery.where("da.std=:std and da.session=:session", student, session)
    val da = entityDao.search(daQuery).headOption match {
      case Some(da) => da
      case None =>
        val da = new Degree2ndApply
        da.session = session
        da.std = student
        da
    }
    val rs = new CommonGradeHelper(entityDao, bestGradeFilter).getGpaAndDetail(student)
    da.gpa = rs._1
    da.gradeDetail = rs._2
    da.updatedAt = Instant.now
    entityDao.saveOrUpdate(da)
    redirect("index", "info.save.success")
  }

  @mapping(method = "delete")
  def remove(): View = {
    val id = getLong("id").get
    val da = entityDao.get(classOf[Degree2ndApply], id)
    val std = getStudent
    if (da.std == std) {
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
