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

import org.beangle.commons.collection.Collections
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.beangle.doc.transfer.exporter.ExportContext
import org.beangle.web.action.annotation.{mapping, param}
import org.beangle.web.action.support.ActionSupport
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.{EntityAction, ExportSupport}
import org.openurp.base.model.Project
import org.openurp.code.edu.model.EducationLevel
import org.openurp.code.std.model.{StdType, StudentStatus}
import org.openurp.edu.grade.model.{AuditCourseLevel, AuditCourseResult, AuditGroupResult, AuditPlanResult}
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.model.GraduateResult
import org.openurp.std.graduation.service.GraduateService
import org.openurp.std.graduation.web.helper.AuditPlanResultPropertyExtractor

import java.time.LocalDate
import scala.collection.mutable

class ProgressAction extends ActionSupport, EntityAction[AuditPlanResult], ProjectSupport, ExportSupport[AuditPlanResult] {

  var entityDao: EntityDao = _
  var graduateService: GraduateService = _

  override def simpleEntityName: String = "result"

  def index(): View = {
    given project: Project = getProject

    put("project", project)
    val batches = graduateService.getBatches(project)
    put("departs", getDeparts)
    put("stdTypes", getCodes(classOf[StdType]))
    put("levels", getCodes(classOf[EducationLevel]))
    put("statuses", getCodes(classOf[StudentStatus]))
    put("batches", batches)
    forward()
  }

  protected override def getQueryBuilder: OqlBuilder[AuditPlanResult] = {
    val builder = super.getQueryBuilder
    queryByDepart(builder, "result.std.state.department")
    getBoolean("stdActive") foreach { active =>
      val nowAt = LocalDate.now
      if (active) builder.where("result.std.beginOn <= :now and result.std.endOn >= :now and result.std.registed = true  and result.std.state.inschool=true", nowAt)
      else builder.where("result.std.beginOn > :now or result.std.endOn < :now or result.std.registed=false or result.std.state.inschool=false", nowAt)
    }
    get("predicted", "") match {
      case "passed" => builder.where("result.predicted=true")
      case "unpassed" => builder.where("result.predicted=false")
      case "takingPassed" => builder.where("result.predicted=false and result.owedCredits3=0")
      case "takingFailed" => builder.where("result.owedCredits3>0")
      case _ =>
    }
    getFloat("requiredCredits.from") foreach { f => builder.where("result.requiredCredits >= :cr1", f) }
    getFloat("requiredCredits.to") foreach { f => builder.where("result.requiredCredits <= :cr2", f) }

    getFloat("passedCredits.from") foreach { f => builder.where("result.passedCredits >= :cr3", f) }
    getFloat("passedCredits.to") foreach { f => builder.where("result.passedCredits <= :cr4", f) }

    getLong("batch.id") foreach { batchId =>
      builder.where(s"exists(from ${classOf[GraduateResult].getName} gr where gr.std=result.std and gr.batch.id=:batchId)", batchId)
    }
    builder
  }

  @mapping(value = "{id}")
  def info(@param("id") id: String): View = {
    val result = entityDao.get(classOf[AuditPlanResult], id.toLong)
    put("result", result)
    put("sg", SeqHelper.getGenerator)
    put("passedLevel", AuditCourseLevel.Passed)
    put("predictedLevel", AuditCourseLevel.Predicted)
    put("takingLevel", AuditCourseLevel.Taking)
    forward()
  }

  def failCourses(): View = {
    val results = entityDao.find(classOf[AuditPlanResult], getLongIds("result"))
    val courseResults = Collections.newBuffer[AuditCourseResult]
    results.foreach { result =>
      result.topGroupResults foreach { c => collectFailCourses(c, courseResults) }
    }
    put("courseResults", courseResults)
    forward()
  }

  private def collectFailCourses(gr: AuditGroupResult, results: mutable.Buffer[AuditCourseResult]): Unit = {
    if (!gr.passed) {
      results ++= gr.courseResults filter (_.passed == false)
    }
    gr.children foreach { c => collectFailCourses(c, results) }
  }

  def search(): View = {
    put("results", entityDao.search(getQueryBuilder))
    forward()
  }

  override protected def configExport(context: ExportContext): Unit = {
    super.configExport(context)
    context.extractor = new AuditPlanResultPropertyExtractor
  }

}
