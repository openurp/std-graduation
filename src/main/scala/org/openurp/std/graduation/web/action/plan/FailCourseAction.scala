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

package org.openurp.std.graduation.web.action.plan

import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.{EntityAction, ExportSupport}
import org.openurp.base.model.Project
import org.openurp.edu.grade.model.AuditCourseResult
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.model.GraduateResult
import org.openurp.std.graduation.service.GraduateService

import java.time.LocalDate

/** 查询统计未通过的课程审核结果
 */
class FailCourseAction extends ActionSupport, EntityAction[AuditCourseResult], ProjectSupport, ExportSupport[AuditCourseResult] {

  var entityDao: EntityDao = _
  var graduateService: GraduateService = _

  def index(): View = {
    given project: Project = getProject

    put("project", project)
    val batches = graduateService.getBatches(project)
    put("departs", getDeparts)
    put("batches", batches)
    forward()
  }

  def search(): View = {
    put("results", entityDao.search(getQueryBuilder))
    forward()
  }

  protected override def getQueryBuilder: OqlBuilder[AuditCourseResult] = {
    val builder = super.getQueryBuilder
    builder.where("result.passed = false")
    builder.where("result.groupResult.passed = false")
    queryByDepart(builder, "result.groupResult.planResult.std.state.department")
    getBoolean("stdActive") foreach { active =>
      val nowAt = LocalDate.now
      if (active) builder.where("result.groupResult.planResult.std.beginOn <= :now and result.groupResult.planResult.std.endOn >= :now and result.groupResult.planResult.std.registed = true  and result.groupResult.planResult.std.state.inschool=true", nowAt)
      else builder.where("result.groupResult.planResult.std.beginOn > :now or result.groupResult.planResult.std.endOn < :now or result.groupResult.planResult.std.registed=false or result.groupResult.planResult.std.state.inschool=false", nowAt)
    }
    getLong("batch.id") foreach { batchId =>
      builder.where(s"exists(from ${classOf[GraduateResult].getName} gr where gr.std=result.groupResult.planResult.std and gr.batch.id=:batchId)", batchId)
    }
    builder
  }

  override def simpleEntityName: String = "result"

}
