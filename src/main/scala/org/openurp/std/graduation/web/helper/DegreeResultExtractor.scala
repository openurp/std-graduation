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

import org.beangle.commons.bean.DefaultPropertyExtractor
import org.beangle.data.dao.EntityDao
import org.openurp.std.graduation.model.DegreeResult
import org.openurp.std.info.model.Examinee

class DegreeResultExtractor(entityDao: EntityDao) extends DefaultPropertyExtractor {

  override def get(bean: Object, name: String): Any = {
    if (name == "examinee.code") {
      val std = bean.asInstanceOf[DegreeResult].std
      entityDao.findBy(classOf[Examinee], "std", std) match {
        case Nil => ""
        case xs => xs.head.code
      }
    } else {
      super.get(bean, name)
    }
  }
}
