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

import org.beangle.webmvc.view.View
import org.openurp.base.std.model.Student
import org.openurp.edu.grade.model.{AuditCourseLevel, AuditPlanResult}
import org.openurp.edu.grade.service.AuditPlanService

class DepartAction extends ProgressAction {
  var auditPlanService: AuditPlanService = _

  def audit(): View = {
    val results = entityDao.find(classOf[AuditPlanResult], getLongIds("result"))
    auditPlanService.batchAudit(results.map(_.std), Map.empty)
    redirect("search", "审核成功")
  }

  /** 查看最新结果
   *
   * @return
   */
  def lastest(): View = {
    val std = entityDao.get(classOf[Student], getLongId("student"))
    val persist = getBoolean("persist", false)
    val result = auditPlanService.audit(std, Map.empty, persist)
    put("result", result)
    put("sg", SeqHelper.getGenerator)
    put("passedLevel", AuditCourseLevel.Passed)
    put("predictedLevel", AuditCourseLevel.Predicted)
    put("takingLevel", AuditCourseLevel.Taking)
    forward("info")
  }
}
