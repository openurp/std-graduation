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

import org.beangle.commons.bean.orderings.MultiPropertyOrdering
import org.beangle.commons.collection.Collections
import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.edu.base.model.Student
import org.openurp.edu.grade.course.model.CourseGrade
import org.openurp.edu.grade.course.service.impl.BestGradeFilter
import org.openurp.edu.grade.model.Grade

class CommonGradeHelper(entityDao: EntityDao, filter: BestGradeFilter) {

  def getGpaAndDetail(std: Student): (Float, String) = {
    calcGpa(getGrades(std))
  }

  private def getGrades(std: Student): Iterable[CourseGrade] = {
    val query = OqlBuilder.from(classOf[CourseGrade], "cg")
    query.where("cg.std=:std", std)
    query.where("cg.status=:status", Grade.Status.Published)
    query.where("cg.gp is not null")
    query.where("not exists(from " + classOf[CourseGrade].getName +
      " cg2 where cg2.std=cg.std and cg2.course=cg.course and cg2.id <> cg.id and cg2.gp > cg.gp)")
    query.where("cg.courseType.id in(40,14,990)")

    var grades = entityDao.search(query)
    grades = filter.filter(grades).toSeq
    grades.groupBy(_.course).values.map(_.head).toBuffer.sorted(new MultiPropertyOrdering("semester.id,course.id"))
  }

  private def calcGpa(grades: Iterable[CourseGrade]): (Float, String) = {
    var gpSum = 0d
    var creditSum = 0f

    var sb = Collections.newBuffer[String]
    for (grade <- grades) {
      gpSum += grade.gp.get * grade.course.credits
      creditSum += grade.course.credits
      sb += s"${grade.semester.schoolYear} ${grade.semester.name} ${grade.course.name} ${grade.course.credits}分 成绩${grade.scoreText.getOrElse("")} 绩点 ${grade.gp.getOrElse("")}"
    }
    if (creditSum <= 0) {
      (0f, sb.mkString("\n"))
    } else {
      ((gpSum / creditSum).toFloat, sb.mkString("\n"))
    }
  }

}
