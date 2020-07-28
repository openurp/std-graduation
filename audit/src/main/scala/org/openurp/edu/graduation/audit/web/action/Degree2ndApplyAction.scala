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
package org.openurp.edu.graduation.audit.web.action

import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.RestfulAction
import org.openurp.edu.web.ProjectSupport
import org.openurp.edu.grade.course.service.impl.BestGradeFilter
import org.openurp.edu.graduation.audit.model.GraduateSession
import org.openurp.edu.graduation.degree2nd.model.Degree2ndApply

class Degree2ndApplyAction extends RestfulAction[Degree2ndApply] with ProjectSupport {

  var bestGradeFilter: BestGradeFilter = _

  override def indexSetting(): Unit = {
    val query = OqlBuilder.from(classOf[GraduateSession], "gs")
    put("sessions", entityDao.search(query))
    put("departs", getDeparts)
  }

  def recalc(): View = {
    val ids = longIds("degree2ndApply")
    val applies = entityDao.find(classOf[Degree2ndApply], ids)
    val helper = new CommonGradeHelper(entityDao, bestGradeFilter)
    applies foreach { apply =>
      val rs = helper.getGpaAndDetail(apply.std)
      apply.gpa = rs._1
      apply.gradeDetail = rs._2
    }
    entityDao.saveOrUpdate(applies)
    redirect("search", "info.save.success")
  }

}
