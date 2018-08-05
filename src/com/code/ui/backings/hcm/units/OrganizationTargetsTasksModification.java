package com.code.ui.backings.hcm.units;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.units.OrganizationTargetTaskDetailData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.enums.OperationsTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.OrganizationTargetsTasksService;

@ManagedBean(name = "organizationTargetsTasksModification")
@SessionScoped
public class OrganizationTargetsTasksModification extends OrganizationTargetsTasksBase {

    protected void addFirstUnitToSelectedUnits() {
	try {
	    int organizationTargetTaskUnitsCount = OrganizationTargetsTasksService.countActiveTargetTaskDetailsForUnit(selectedUnitData.getId()).intValue();
	    if (organizationTargetTaskUnitsCount == 0) {
		this.setServerSideErrorMessages(getMessage("error_cannotAddUnitWithoutTargetOrTask"));
		return;
	    }

	    List<OrganizationTargetTaskDetailData> selectedUnitsOriginalTargetTaskDetails = OrganizationTargetsTasksService.getIdenticalTargetsTasksDetails(selectedUnitData.getId(), organizationTargetTaskUnitsCount);

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
	    this.setServerSideErrorMessages(getMessage("error_cannotAddTheSameUnitAgain"));
	else if (checkSelectedUnitWithinUnits(removedUnits, selectedUnitData.getId())) {
	    selectedUnits.add(selectedUnitData);
	    removedUnits.remove(selectedUnitData);
	}
	else
	    this.setServerSideErrorMessages(getMessage("error_targetTasksWithDifferentDetails"));
    }

    public void saveTargetTask(OrganizationTargetTaskDetailData targetTaskDetail) {
	try {
	    if (targetTaskDetail.getId() == null)
		OrganizationTargetsTasksService.modifyOrganizationTargetTask(decisionNumber, decisionDate, selectedUnits, OperationsTypesEnum.INSERT.getCode(),
			targetTaskDetail, targets, tasks, this.loginEmpData.getEmpId());
	    else
		OrganizationTargetsTasksService.modifyOrganizationTargetTask(decisionNumber, decisionDate, selectedUnits, OperationsTypesEnum.UPDATE.getCode(),
			targetTaskDetail, targets, tasks, this.loginEmpData.getEmpId());

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    @Override
    public void deleteTargetTask(OrganizationTargetTaskDetailData targetTaskDetail) {
	try {
	    if (targetTaskDetail.getId() != null)
		OrganizationTargetsTasksService.modifyOrganizationTargetTask(decisionNumber, decisionDate, selectedUnits, OperationsTypesEnum.DELETE.getCode(),
			targetTaskDetail, targets, tasks, this.loginEmpData.getEmpId());

	    super.deleteTargetTask(targetTaskDetail);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

}
