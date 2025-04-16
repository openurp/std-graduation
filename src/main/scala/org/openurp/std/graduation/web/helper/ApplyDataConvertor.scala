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
import org.beangle.commons.lang.Strings
import org.openurp.std.graduation.model.DegreeApply

import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, ZoneId}

object ApplyDataConvertor {

  def convert(apply: DegreeApply): collection.Map[String, String] = {
    val std = apply.std
    val data = Collections.newMap[String, String]
    data.put("school", std.project.school.name)
    data.put("code", std.code)
    data.put("name", std.name)
    data.put("gender", std.gender.name)

    std.state foreach { state =>
      data.put("depart", state.department.name)
      val directionName = state.direction.map(_.name).getOrElse("")
      //双学士学位特殊一点
      if (directionName.contains("双学士学位")) {
        data.put("major", state.major.name + s"（${directionName}）")
        data.put("direction", "")
        data.put("degree", Strings.replace(apply.degree.name, "学士", " 管理学学士"))
      } else {
        data.put("major", state.major.name)
        data.put("direction", directionName)
        data.put("degree", apply.degree.name)
      }
      data.put("squad", state.squad.map(_.name).getOrElse(""))
    }
    import java.text.NumberFormat
    val nf = NumberFormat.getNumberInstance
    nf.setMinimumFractionDigits(0)
    nf.setMaximumFractionDigits(1)
    data.put("duration", nf.format(std.duration) + "年")

    val formatter = DateTimeFormatter.ofPattern("yyyy年M月d日")

    data.put("studyOn", std.beginOn.format(formatter))
    data.put("graduateOn", apply.batch.graduateOn.format(formatter))

    val formatter2 = DateTimeFormatter.ofPattern("yyyy 年 M 月 d 日")
    //教务处审核时间
    var auditOn1 = apply.batch.graduateOn.minusDays(1)
    while (auditOn1.getDayOfWeek == DayOfWeek.SATURDAY || auditOn1.getDayOfWeek == DayOfWeek.SUNDAY) {
      auditOn1 = auditOn1.minusDays(1)
    }
    //学院审核时间
    var auditOn0 = auditOn1.minusDays(2)
    while (auditOn0.getDayOfWeek == DayOfWeek.SATURDAY || auditOn0.getDayOfWeek == DayOfWeek.SUNDAY) {
      auditOn0 = auditOn0.minusDays(1)
    }
    // 申请日期，申请日期不得晚于学院的审核日期
    var applyOn = apply.updatedAt.atZone(ZoneId.systemDefault()).toLocalDate
    if (applyOn.isAfter(auditOn0)) {
      applyOn = auditOn0.minusDays(1)
    }

    data.put("applyOn", applyOn.format(formatter))
    data.put("auditOn0", auditOn0.format(formatter2))
    data.put("auditOn1", auditOn1.format(formatter2))
    data.put("auditOn2", apply.batch.graduateOn.format(formatter2))
    data
  }
}
