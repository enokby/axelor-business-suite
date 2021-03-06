/**
 * Axelor Business Solutions
 *
 * Copyright (C) 2017 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.hr.service;

import com.axelor.apps.hr.db.Employee;
import com.axelor.auth.db.User;
import com.axelor.meta.schema.actions.ActionView;

public class HRMenuValidateService {

	public void createValidateDomain(User user, Employee employee, ActionView.ActionViewBuilder actionView) {

		actionView.domain("self.company = :_activeCompany AND  self.statusSelect = 2")
				.context("_activeCompany", user.getActiveCompany());

		if(employee == null || !employee.getHrManager())  {
			if (employee == null || employee.getManager() == null) {
				actionView.domain(actionView.get().getDomain() + " AND (self.user = :_user OR self.user.employee.manager = :_user)")
						.context("_user", user);
			}
			else {
				actionView.domain(actionView.get().getDomain() + " AND self.user.employee.manager = :_user")
						.context("_user", user);
			}
		}
	}


}
