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

import org.beangle.web.action.view.View
import org.openurp.edu.grade.model.AuditPlanResult
import org.openurp.edu.grade.service.AuditPlanService

class DepartAction extends ProgressAction {
  var auditPlanService: AuditPlanService = _

  def audit(): View = {
    val results = entityDao.find(classOf[AuditPlanResult], getLongIds("result"))
    auditPlanService.batchAudit(results.map(_.std), Map.empty)
    redirect("search", "审核成功")
  }
}
