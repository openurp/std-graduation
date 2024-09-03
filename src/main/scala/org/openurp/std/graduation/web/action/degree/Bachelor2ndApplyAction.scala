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

import org.beangle.data.dao.OqlBuilder
import org.beangle.web.action.view.View
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.openurp.base.model.Project
import org.openurp.edu.grade.service.filters.BestGradeFilter
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.model.{Bachelor2ndApply, GraduateBatch}
import org.openurp.std.graduation.web.helper.CommonGradeHelper

class Bachelor2ndApplyAction extends RestfulAction[Bachelor2ndApply], ExportSupport[Bachelor2ndApply], ProjectSupport {

  var bestGradeFilter: BestGradeFilter = _

  override def indexSetting(): Unit = {
    given project: Project = getProject

    val query = OqlBuilder.from(classOf[GraduateBatch], "gs")
    put("batches", entityDao.search(query))
    put("departs", getDeparts)
  }

  def recalc(): View = {
    val ids = getLongIds("apply")
    val applies = entityDao.find(classOf[Bachelor2ndApply], ids)
    val helper = new CommonGradeHelper(entityDao, bestGradeFilter)
    applies foreach { apply =>
      val rs = helper.getGpaAndDetail(apply.std)
      apply.gpa = rs._1
      apply.gradeDetail = rs._2
    }
    entityDao.saveOrUpdate(applies)
    redirect("search", "info.save.success")
  }

  override protected def simpleEntityName: String = "apply"

}
