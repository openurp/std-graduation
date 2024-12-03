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

package org.openurp.std.graduation.web.action

import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.data.model.pojo.TemporalOn
import org.beangle.ems.app.rule.{Rule, RuleEngine}
import org.beangle.webmvc.support.action.RestfulAction
import org.beangle.webmvc.view.View
import org.openurp.code.edu.model.EducationLevel
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.config.AuditSetting

class SettingAction extends RestfulAction[AuditSetting], ProjectSupport {

  val graduateAuditBussinessCode = "std_graduate_audit"
  val degreeAuditBussinessCode = "std_degree_audit"

  override protected def editSetting(setting: AuditSetting): Unit = {
    val project = getProject
    val graduateRules = RuleEngine.list(graduateAuditBussinessCode, project.id.toString)
    val degreeRules = RuleEngine.list(degreeAuditBussinessCode, project.id.toString)

    put("levels", project.levels)
    if (setting.gruleIds == null) {
      put("gruleIds", List.empty)
    } else {
      put("gruleIds", Strings.split(setting.gruleIds, ",").toIndexedSeq)
    }
    if (setting.druleIds.isEmpty) {
      put("druleIds", List.empty)
    } else {
      put("druleIds", Strings.split(setting.druleIds.get, ",").toIndexedSeq)
    }
    put("graduateRules", graduateRules)
    put("degreeRules", degreeRules)
    super.editSetting(setting)
  }

  override def search(): View = {
    val settings = entityDao.search(getQueryBuilder)
    val grules = Collections.newMap[AuditSetting, Iterable[Rule]]
    val drules = Collections.newMap[AuditSetting, Iterable[Rule]]
    settings foreach { setting =>
      grules.put(setting, RuleEngine.list(setting.gruleIds))
      setting.druleIds match {
        case Some(druleIds) => drules.put(setting, RuleEngine.list(druleIds))
        case None => drules.put(setting, List.empty)
      }
    }
    put("grules", grules)
    put("drules", drules)
    put("settings", settings)
    forward()
  }

  override protected def saveAndRedirect(setting: AuditSetting): View = {
    val project = getProject

    val levels = entityDao.find(classOf[EducationLevel], getAll("setting.levels.id", classOf[Int]))
    setting.gruleIds = getAll("setting.grules.id", classOf[Long]).mkString(",")
    val druleIds = getAll("setting.drules.id", classOf[Long])
    if (druleIds.isEmpty) {
      setting.druleIds = None
    } else {
      setting.druleIds = Some(druleIds.mkString(","))
    }
    setting.levels.clear()
    setting.levels.addAll(levels)
    setting.project = getProject

    val exited = entityDao.findBy(classOf[AuditSetting], "project", project).toBuffer
    if (setting.persisted) {
      exited.subtractOne(setting)
    }
    val overlapped = exited.exists(s => Collections.intersection(s.levels, setting.levels).nonEmpty && overlap(s, setting))
    if (overlapped) {
      redirect("edit", "配置时间冲突，请重新选择")
    } else {
      super.saveAndRedirect(setting)
    }
  }

  def overlap(o1: TemporalOn, o2: TemporalOn): Boolean = {
    if (o1.endOn.isEmpty && o2.endOn.isEmpty) {
      true
    } else if (o1.endOn.isEmpty) {
      o2.endOn.get.isAfter(o1.beginOn)
    } else if (o2.endOn.isEmpty) {
      o2.endOn.get.isBefore(o1.beginOn)
    } else {
      o1.beginOn.isBefore(o2.endOn.get) && o2.beginOn.isBefore(o1.endOn.get)
    }
  }

  override protected def simpleEntityName: String = "setting"
}
