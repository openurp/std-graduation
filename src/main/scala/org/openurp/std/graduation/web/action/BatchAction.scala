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

import org.beangle.webmvc.support.action.RestfulAction
import org.openurp.base.model.Project
import org.openurp.base.std.model.GraduateSeason
import org.openurp.starter.web.support.ProjectSupport
import org.openurp.std.graduation.model.GraduateBatch

class BatchAction extends RestfulAction[GraduateBatch],ProjectSupport{

  override protected def simpleEntityName: String = "batch"

  override def indexSetting(): Unit = {
    put("seasons",getSeasons(getProject))
  }

  override protected def editSetting(entity: GraduateBatch): Unit = {
    val project = getProject
    put("seasons", getSeasons(project))
    put("project",project)
    super.editSetting(entity)
  }

  private def getSeasons(project:Project):Seq[GraduateSeason]={
    entityDao.findBy(classOf[GraduateSeason],"project",project)
  }

}
