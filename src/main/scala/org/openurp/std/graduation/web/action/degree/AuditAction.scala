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

import org.beangle.commons.lang.Strings
import org.beangle.commons.lang.text.DateFormatter
import org.beangle.data.dao.OqlBuilder
import org.beangle.doc.transfer.exporter.ExportContext
import org.beangle.ems.app.rule.RuleEngine
import org.beangle.webmvc.annotation.mapping
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.beangle.webmvc.view.View
import org.openurp.base.model.{Campus, Project}
import org.openurp.code.edu.model.{EducationLevel, EducationResult}
import org.openurp.code.std.model.StdType
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.model.{DegreeResult, GraduateBatch}
import org.openurp.std.graduation.service.{DegreeAuditService, GraduateService}
import org.openurp.std.graduation.web.helper.DegreeResultExtractor

import java.time.LocalDate

/** 管理部门毕业审核
 */
class AuditAction extends RestfulAction[DegreeResult], ProjectSupport, ExportSupport[DegreeResult] {
  var degreeAuditService: DegreeAuditService = _
  var graduateService: GraduateService = _

  override protected def simpleEntityName: String = "result"

  override protected def indexSetting(): Unit = {
    given project: Project = getProject

    put("departs", getDeparts)
    put("stdTypes", getCodes(classOf[StdType]))
    put("levels", getCodes(classOf[EducationLevel]))
    put("eduResults", getCodes(classOf[EducationResult]))
    put("campuses", findInSchool(classOf[Campus]))
    val batches = graduateService.getBatches(project)
    put("batches", batches)
    if (batches.isEmpty) {
      addMessage("本项目下缺少毕业审核批次，请新建一个")
    }
  }

  def addSetting(): View = {
    forward()
  }

  override protected def getQueryBuilder: OqlBuilder[DegreeResult] = {
    getLong("result.batch.id") foreach { batchId =>
      put("batch", entityDao.get(classOf[GraduateBatch], batchId))
    }
    val query = super.getQueryBuilder
    query.where("result.std.project=:project", getProject)
    query
  }

  def audit(): View = {
    RuleEngine.clearLocalCache()
    val results = entityDao.find(classOf[DegreeResult], getLongIds("result"))
    results foreach { result =>
      degreeAuditService.audit(result)
    }
    redirect("search", "审核成功")
  }

  /** 添加名单
   *
   * @return
   */
  def add(): View = {
    val batch = entityDao.get(classOf[GraduateBatch], getLongId("result.batch"))
    val stdCodes = get("codes", "")
    val cnt = degreeAuditService.initResults(Strings.split(stdCodes).toList, batch)
    redirect("search", "&result.batch.id=" + batch.id, s"添加了${cnt}个学生")
  }

  /** 将学生从毕业审核批次中删除
   */
  @mapping(method = "delete")
  override def remove(): View = {
    val query = OqlBuilder.from(classOf[DegreeResult], "result")
    val resultIds = getLongIds("result")
    query.where("result.id in (:ids) and result.published = false", resultIds)
    val results = entityDao.search(query)
    entityDao.remove(results)
    redirect("search", s"删除了${results.size}个学生的审核结果")
  }

  /** 根据预毕业时间，将学生添加到毕业审核批次
   */
  def init(): View = {
    val batch = entityDao.get(classOf[GraduateBatch], getLongId("result.batch"))
    val cnt = degreeAuditService.initResults(batch)
    redirect("search", s"添加了${cnt}个学生")
  }

  /**
   * 设置发布状态
   *
   * @return
   */
  def publish(): View = {
    val resultIds = getLongIds("result")
    val published = getBoolean("publish", true)
    val results = entityDao.find(classOf[DegreeResult], resultIds)
    degreeAuditService.publish(results, published)
    redirect("search", "info.action.success")
  }

  override protected def configExport(context: ExportContext): Unit = {
    super.configExport(context)
    context.registerFormatter(classOf[LocalDate],new DateFormatter("yyyyMMdd"))
    context.extractor = new DegreeResultExtractor(entityDao)
  }
}
