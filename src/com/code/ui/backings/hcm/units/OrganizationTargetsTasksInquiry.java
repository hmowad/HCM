package com.code.ui.backings.hcm.units;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.units.OrganizationTargetTaskDetailData;
import com.code.dal.orm.hcm.organization.units.OrganizationTargetTaskTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.OrganizationTargetsTasksService;
import com.code.services.hcm.UnitsService;
import com.code.ui.util.UnitTreeNode;

@ManagedBean(name = "organizationTargetsTasksInquiry")
@SessionScoped
public class OrganizationTargetsTasksInquiry extends UnitTreeBase {

    private List<OrganizationTargetTaskDetailData> targets;
    private List<OrganizationTargetTaskDetailData> tasks;

    private List<OrganizationTargetTaskTransactionData> targetsTasksTransactionData;

    private final int pageSize = 10;

    public void init() {
	super.init();

	searchTargetsTasks(false);
    }

    public void reset() {
	targets = new ArrayList<OrganizationTargetTaskDetailData>();
	tasks = new ArrayList<OrganizationTargetTaskDetailData>();
	targetsTasksTransactionData = new ArrayList<OrganizationTargetTaskTransactionData>();
    }

    public void search() {
	super.search();

	searchTargetsTasks(false);
    }

    public void selectUnit(UnitTreeNode unitItem) {
	super.click(unitItem);

	searchTargetsTasks(true);
    }

    public void searchTargetsTasks(boolean showErrorMessage) {
	try {
	    reset();

	    if (isAuthorized()) {
		targetsTasksTransactionData = OrganizationTargetsTasksService.getOrganizationTargetsTasksTransactions(selectedUnitData.getId());

		if (!targetsTasksTransactionData.isEmpty()) {
		    List<OrganizationTargetTaskDetailData> organizationTargetTaskDetailsData = OrganizationTargetsTasksService.getActiveOrganizationTargetTaskDetails(selectedUnitData.getId());
		    for (OrganizationTargetTaskDetailData targetTask : organizationTargetTaskDetailsData) {
			if (targetTask.getUnitId().equals(selectedUnitData.getId())) {
			    if (FlagsEnum.ON.getCode() == targetTask.getOrganizationTargetTaskFlag().intValue())
				targets.add(targetTask);
			    else
				tasks.add(targetTask);
			}
		    }
		}
		// else {
		// this.setServerSideErrorMessages(getMessage("error_selectedUnitDoesnotHaveTargetOrTasks"));
		// }
	    } else {
		if (showErrorMessage)
		    this.setServerSideErrorMessages(getMessage("error_selectedUnitIsNotInTheSameRegion"));
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public boolean isAuthorized() {
	return this.loginEmpData.getPhysicalRegionId().equals(selectedUnitData.getRegionId())
		|| this.loginEmpData.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
    }

    public void printUnitTargetTaskDetails(boolean isHierarchical) {
	try {
	    if (!isAuthorized()) {
		this.setServerSideErrorMessages(getMessage("error_selectedUnitIsNotInTheSameRegion"));
		return;
	    }

	    byte[] bytes = OrganizationTargetsTasksService.getUnitsTargetsTasksReport(isHierarchical ? UnitsService.getHKeyPrefix(selectedUnitData.gethKey()) : selectedUnitData.gethKey());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<OrganizationTargetTaskDetailData> getTargets() {
	return targets;
    }

    public List<OrganizationTargetTaskDetailData> getTasks() {
	return tasks;
    }

    public List<OrganizationTargetTaskTransactionData> getTargetsTasksTransactionData() {
	return targetsTasksTransactionData;
    }

    public int getPageSize() {
	return pageSize;
    }

}
