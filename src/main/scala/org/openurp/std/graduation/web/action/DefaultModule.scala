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

import org.beangle.cdi.bind.BindModule

class DefaultModule extends BindModule {

  override protected def binding(): Unit = {
    bind(classOf[degree.StdApplyAction])
    bind(classOf[degree.ApplyAuditAction])
    bind(classOf[degree.Bachelor2ndApplyAction])
    bind(classOf[degree.StdBachelor2ndApplyAction])

    bind(classOf[archive.CertificateAction])
    bind(classOf[archive.DiplomaAction])
    bind(classOf[archive.TranscriptAction])
    bind(classOf[archive.FileAction])

    bind(classOf[plan.FailCourseAction])
    bind(classOf[plan.ProgressAction])
    bind(classOf[plan.DepartAction])

    bind(classOf[graduate.AuditAction])
  }

}
