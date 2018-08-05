package com.code.ui.components.util;

import java.util.HashSet;
import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.services.hcm.EmployeesService;

@FacesComponent("com.code.ui.components.util.UIDecisionCopies")
public class UIDecisionCopies extends UINamingContainer {

    private String selectedEmpsIds;

    @SuppressWarnings("unchecked")
    public void deleteInternalCopy(EmployeeData e) {
	List<EmployeeData> internalCopies = (List<EmployeeData>) getAttributes().get("internal");
	internalCopies.remove(e);
    }

    @SuppressWarnings("unchecked")
    public void addInternalCopy() {
	try {
	    List<EmployeeData> internalCopies = (List<EmployeeData>) getAttributes().get("internal");
	    HashSet<Long> empsIds = new HashSet<Long>();

	    for (EmployeeData emp : internalCopies)
		empsIds.add(emp.getEmpId());

	    for (EmployeeData emp : EmployeesService.getEmployeesByIdsString(selectedEmpsIds)) {
		if (!empsIds.contains(emp.getEmpId())) {
		    internalCopies.add(emp);
		    empsIds.add(emp.getEmpId());
		}
	    }
	} catch (Exception e1) {

	}
    }

    public String getSelectedEmpsIds() {
	return selectedEmpsIds;
    }

    public void setSelectedEmpsIds(String selectedEmpsIds) {
	this.selectedEmpsIds = selectedEmpsIds;
    }
}