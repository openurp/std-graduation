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

package org.openurp.std.graduation.web.action.graduate

import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.text.{DateFormatter, TemporalFormatter}
import org.beangle.commons.lang.{ClassLoaders, Strings}
import org.beangle.data.dao.OqlBuilder
import org.beangle.doc.transfer.exporter.ExportContext
import org.beangle.ems.app.rule.RuleEngine
import org.beangle.web.servlet.util.RequestUtils
import org.beangle.webmvc.annotation.mapping
import org.beangle.webmvc.context.ActionContext
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.beangle.webmvc.view.View
import org.openurp.base.hr.model.President
import org.openurp.base.model.{Campus, Project}
import org.openurp.code.edu.model.{EducationLevel, EducationResult}
import org.openurp.code.std.model.StdType
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.model.{GraduateBatch, GraduateResult}
import org.openurp.std.graduation.service.{GraduateAuditService, GraduateService}
import org.openurp.std.graduation.web.helper.GraduateResultExtractor
import org.openurp.std.info.model.Examinee

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util
import java.util.List

/** 管理部门毕业审核
 */
class AuditAction extends RestfulAction[GraduateResult], ProjectSupport, ExportSupport[GraduateResult] {
  var graduateAuditService: GraduateAuditService = _
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

  override protected def getQueryBuilder: OqlBuilder[GraduateResult] = {
    getLong("result.batch.id") foreach { batchId =>
      put("batch", entityDao.get(classOf[GraduateBatch], batchId))
    }
    val query = super.getQueryBuilder
    query.where("result.std.project=:project", getProject)
    query
  }

  def audit(): View = {
    val results = entityDao.find(classOf[GraduateResult], getLongIds("result"))
    RuleEngine.clearLocalCache()
    results foreach { result =>
      graduateAuditService.audit(result)
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
    val cnt = graduateAuditService.initResults(Strings.split(stdCodes).toList, batch)
    redirect("search", "&result.batch.id=" + batch.id, s"添加了${cnt}个学生")
  }

  /** 将学生从毕业审核批次中删除
   */
  @mapping(method = "delete")
  override def remove(): View = {
    val query = OqlBuilder.from(classOf[GraduateResult], "result")
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
    val cnt = graduateAuditService.initResults(batch)
    redirect("search", s"添加了${cnt}个学生")
  }

  /** 删除延期学生
   *
   * @return
   */
  def removeDeferred(): View = {
    val query = OqlBuilder.from(classOf[GraduateResult], "result")
    query.where("result.batch.id=:batchId", getLongId("result.batch"))
    query.where("result.published = false")
    val results = entityDao.search(query).filter(x => x.std.graduateOn.isAfter(x.batch.graduateOn.plusDays(30)))
    entityDao.remove(results)
    redirect("search", s"删除了${results.size}个学生的审核结果")
  }

  /**
   * 考试院报表
   *
   * @return
   */
  def ksyData(): View = {
    val project = getProject
    val builder = OqlBuilder.from(classOf[GraduateResult], "result")
    this.populateConditions(builder)
    val results = entityDao.search(builder)
    var batch = results.head.batch
    val items = Collections.newBuffer[collection.Map[String, Object]]
    val dateFormater = DateTimeFormatter.ofPattern("yyyyMMdd")
    val president = entityDao.findBy(classOf[President], "school", project.school).filter(x => x.within(batch.graduateOn))
    val presidentName = if president.nonEmpty then president.head.name else "校长"
    for (result <- results) {
      val data = Collections.newMap[String, Object]
      data.put("result", result.educationResult)
      data.put("std", result.std)
      data.put("studyOn", dateFormater.format(result.std.beginOn))
      data.put("graduateOn", dateFormater.format(result.std.graduateOn))
      result.std.person.birthday foreach { b =>
        data.put("birthday", dateFormater.format(b))
      }
      data.put("president", presidentName)
      val ebuilder = OqlBuilder.from(classOf[Examinee], "e").where("e.std=:std", result.std)
      val examinees = entityDao.search(ebuilder)
      if (examinees.nonEmpty) data.put("examinee", examinees.head)
      items.addOne(data)
    }
    val ctx = ExportContext.template(ClassLoaders.getResource("org/openurp/std/graduation/graduate/ksy_format.xlsx").get)
    ctx.put("items", items)

    val response = ActionContext.current.response
    RequestUtils.setContentDisposition(response, ctx.buildFileName(get("fileName")))
    ctx.writeTo(response.getOutputStream)
    null
  }

  /**
   * 设置发布状态
   *
   * @return
   */
  def publish(): View = {
    val resultIds = getLongIds("result")
    val published = getBoolean("publish", true)
    val results = entityDao.find(classOf[GraduateResult], resultIds)
    graduateAuditService.publish(results, published)
    redirect("search", "info.action.success")
  }

  override protected def configExport(context: ExportContext): Unit = {
    super.configExport(context)
    context.registerFormatter(classOf[LocalDate],new TemporalFormatter("yyyyMMdd"))
    context.extractor = new GraduateResultExtractor(entityDao)
  }

}
