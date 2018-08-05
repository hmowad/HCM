package com.code.ui.backings.hcm.units;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.units.OrganizationTargetTaskDetailData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.OrganizationTargetsTasksService;

@ManagedBean(name = "organizationTargetsTasksCancellation")
@SessionScoped
public class OrganizationTargetsTasksCancellation extends OrganizationTargetsTasksBase {

    private List<OrganizationTargetTaskDetailData> selectedUnitsOriginalTargetTaskDetails;

    @Override
    public void reset() {
	super.reset();
	selectedUnitsOriginalTargetTaskDetails = new ArrayList<OrganizationTargetTaskDetailData>();
    }

    protected void addFirstUnitToSelectedUnits() {
	try {
	    int organizationTargetTaskUnitsCount = OrganizationTargetsTasksService.countActiveTargetTaskDetailsForUnit(selectedUnitData.getId()).intValue();
	    if (organizationTargetTaskUnitsCount == 0) {
		this.setServerSideErrorMessages(getMessage("error_cannotAddUnitWithoutTargetOrTask"));
		return;
	    }

	    selectedUnitsOriginalTargetTaskDetails = OrganizationTargetsTasksService.getIdenticalTargetsTasksDetails(selectedUnitData.getId(), organizationTargetTaskUnitsCount);

	    // Based on the list is ordered; the first organizationTargetTaskUnitsCount objects could be considered as representable for all the details.
	    for (int i = 0; i < organizationTargetTaskUnitsCount; ++i) {
		if (selectedUnitsOriginalTargetTaskDetails.get(i).getOrganizationTargetTaskFlag() == FlagsEnum.ON.getCode())
		    targets.add(selectedUnitsOriginalTargetTaskDetails.get(i));
		else
		    tasks.add(selectedUnitsOriginalTargetTaskDetails.get(i));
	    }
	    // Based on the list is ordered; we can select the units by jumping up with organizationTargetTaskUnitsCount steps.
	    for (int i = 0; i < selectedUnitsOriginalTargetTaskDetails.size(); i += organizationTargetTaskUnitsCount) {
		UnitData selectedTargetTaskUnit = new UnitData();
		selectedTargetTaskUnit.setId(selectedUnitsOriginalTargetTaskDetails.get(i).getUnitId());
		selectedTargetTaskUnit.setFullName(selectedUnitsOriginalTargetTaskDetails.get(i).getUnitFullName());
		selectedTargetTaskUnit.setUnitTypeId(selectedUnitsOriginalTargetTaskDetails.get(i).getUnitTypeId());
		selectedUnits.add(selectedTargetTaskUnit);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    protected void addUnitToSelectedUnits() {
	if (checkSelectedUnitWithinUnits(selectedUnits, selectedUnitData.getId()))
	    this.setServerSideErrorMessages(getMessage("error_repeatedUnit"));
	else if (checkSelectedUnitWithinUnits(removedUnits, selectedUnitData.getId())) {
	    selectedUnits.add(selectedUnitData);
	    removedUnits.remove(selectedUnitData);
	} else
	    this.setServerSideErrorMessages(getMessage("error_targetTasksWithDifferentDetails"));
    }

    @Override
    public void save() {
	try {
	    OrganizationTargetsTasksService.cancelOrganizationTargetsTasks(decisionNumber, decisionDate, selectedUnits, selectedUnitsOriginalTargetTaskDetails, this.loginEmpData.getEmpId());
	    super.save();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

}
