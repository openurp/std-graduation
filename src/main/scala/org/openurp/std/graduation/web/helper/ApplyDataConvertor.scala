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

package org.openurp.std.graduation.web.helper

import org.beangle.commons.collection.Collections
import org.openurp.std.graduation.model.DegreeApply

import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, ZoneId}

object ApplyDataConvertor {

  def convert(apply:DegreeApply):collection.Map[String,String]={
    val std = apply.std
    val data = Collections.newMap[String, String]
    data.put("school", std.project.school.name)
    data.put("code", std.code)
    data.put("name", std.name)
    data.put("gender", std.gender.name)

    std.state foreach { state =>
      data.put("depart", state.department.name)
      data.put("major", state.major.name)
      data.put("direction", state.direction.map(_.name).getOrElse(""))
      data.put("squad", state.squad.map(_.name).getOrElse(""))
    }
    import java.text.NumberFormat
    val nf = NumberFormat.getNumberInstance
    nf.setMinimumFractionDigits(0)
    nf.setMaximumFractionDigits(1)
    data.put("duration", nf.format(std.duration)+"年")

    val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
    data.put("degree", apply.degree.name)
    data.put("studyOn", std.studyOn.format(formatter))
    data.put("graduateOn", apply.batch.graduateOn.format(formatter))
    //FIXME 晚于评定日期apply.batch.graduateOn
    data.put("applyOn", apply.updatedAt.atZone(ZoneId.systemDefault()).toLocalDate.format(formatter))

    val formatter2 = DateTimeFormatter.ofPattern("yyyy 年 M 月 d 日")
    var auditOn =  apply.batch.graduateOn.minusDays(1)
    while(auditOn.getDayOfWeek  == DayOfWeek.SATURDAY || auditOn.getDayOfWeek  == DayOfWeek.SUNDAY ){
      auditOn = auditOn.minusDays(1)
    }
    data.put("auditOn1", auditOn.format(formatter2))
    data.put("auditOn2", apply.batch.graduateOn.format(formatter2))
    data
  }
}
