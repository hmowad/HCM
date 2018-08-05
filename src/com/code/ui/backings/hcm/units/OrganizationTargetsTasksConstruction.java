package com.code.ui.backings.hcm.units;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.units.OrganizationTargetTaskDetailData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.OrganizationTargetsTasksService;

@ManagedBean(name = "organizationTargetsTasksConstruction")
@SessionScoped
public class OrganizationTargetsTasksConstruction extends OrganizationTargetsTasksBase {

    private boolean completionFlag;

    @Override
    public void reset() {
	super.reset();
	completionFlag = false;
    }

    protected void addFirstUnitToSelectedUnits() {
	try {
	    int organizationTargetTaskUnitsCount = OrganizationTargetsTasksService.countActiveTargetTaskDetailsForUnit(selectedUnitData.getId()).intValue();
	    if (organizationTargetTaskUnitsCount == 0) {
		selectedUnits.add(selectedUnitData);
	    } else {
		completionFlag = true;
		List<OrganizationTargetTaskDetailData> selectedUnitsOriginalTargetTaskDetails = OrganizationTargetsTasksService.getIdenticalTargetsTasksDetails
			(selectedUnitData.getId(), organizationTargetTaskUnitsCount);

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

		decisionNumber = selectedUnitsOriginalTargetTaskDetails.get(0).getDecisionNumber();
		decisionDate = selectedUnitsOriginalTargetTaskDetails.get(0).getDecisionDate();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addUnitToSelectedUnits() {
	try {

	    if (checkSelectedUnitWithinUnits(selectedUnits, selectedUnitData.getId())) {
		this.setServerSideErrorMessages(getMessage("error_repeatedUnit"));
		return;
	    }

	    if (completionFlag) {
		if (checkSelectedUnitWithinUnits(removedUnits, selectedUnitData.getId())) {
		    selectedUnits.add(selectedUnitData);
		    removedUnits.remove(selectedUnitData);
		} else {
		    this.setServerSideErrorMessages(getMessage("error_targetTasksWithDifferentDetails"));
		    return;
		}
	    } else {
		// validate that the selected unit has the same type.
		if (!selectedUnitData.getUnitTypeId().equals(selectedUnits.get(0).getUnitTypeId())) {
		    this.setServerSideErrorMessages(getMessage("error_allUnitsShouldHaveSameType"));
		    return;
		}
		// validate that the selected unit doesn't have active targets tasks.
		if (!OrganizationTargetsTasksService.getActiveOrganizationTargetsTasks(new Long[] { selectedUnitData.getId() }).isEmpty()) {
		    this.setServerSideErrorMessages(getMessage("error_cantAddUnitHasTargetOrTask"));
		    return;
		}
		selectedUnits.add(selectedUnitData);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    @Override
    public void deleteUnit(UnitData detail) {

	selectedUnits.remove(detail);
	if (completionFlag)
	    removedUnits.add(detail);

	if (selectedUnits.isEmpty())
	    reset();
    }

    public void save() {
	try {
	    OrganizationTargetsTasksService.constructOrganizationTargetTask(completionFlag, decisionNumber, decisionDate, selectedUnits, targets, tasks, this.loginEmpData.getEmpId());
	    super.save();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public boolean getCompletionFlag() {
	return completionFlag;
    }

}
