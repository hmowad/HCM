package com.code.ui.backings.hcm.units;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.code.dal.orm.hcm.organization.units.OrganizationTargetTaskDetailData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.util.HijriDateService;
import com.code.ui.util.UnitTreeNode;

public abstract class OrganizationTargetsTasksBase extends UnitTreeBase {

    protected String decisionNumber;
    protected Date decisionDate;

    protected List<UnitData> selectedUnits;
    protected List<UnitData> removedUnits;

    protected List<OrganizationTargetTaskDetailData> targets;
    protected List<OrganizationTargetTaskDetailData> tasks;

    // protected Integer organizationTargetTaskUnitsCount;
    // protected List<OrganizationTargetTaskDetailData> selectedUnitOriginalTargetTaskDetailsData;

    private boolean done;
    private final int pageSize = 10;

    public void initialize() {
	super.init();
	reset();
    }

    public void reset() {
	try {
	    decisionNumber = null;
	    decisionDate = HijriDateService.getHijriSysDate();

	    selectedUnits = new ArrayList<UnitData>();
	    removedUnits = new ArrayList<UnitData>();

	    targets = new ArrayList<OrganizationTargetTaskDetailData>();
	    tasks = new ArrayList<OrganizationTargetTaskDetailData>();
	    // selectedUnitOriginalTargetTaskDetailsData = new ArrayList<OrganizationTargetTaskDetailData>();

	    done = false;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public final void selectUnit(UnitTreeNode unitItem) {
	super.click(unitItem);

	if (selectedUnits.isEmpty())
	    addFirstUnitToSelectedUnits();
	else
	    addUnitToSelectedUnits();
    }

    protected abstract void addFirstUnitToSelectedUnits();

    protected abstract void addUnitToSelectedUnits();

    protected final boolean checkSelectedUnitWithinUnits(List<UnitData> units, Long unitId) {
	for (UnitData unitData : units) {
	    if (unitData.getId().equals(unitId))
		return true;
	}
	return false;
    }

    public void deleteUnit(UnitData detail) {

	selectedUnits.remove(detail);
	removedUnits.add(detail);

	if (selectedUnits.isEmpty())
	    reset();
    }

    public void addTargetTask(Integer targetTaskType) {
	OrganizationTargetTaskDetailData targetTask = new OrganizationTargetTaskDetailData();
	// 1 means target and 0 means task
	if (FlagsEnum.ON.getCode() == targetTaskType) {
	    targetTask.setOrganizationTargetTaskFlag(FlagsEnum.ON.getCode());
	    targets.add(targetTask);
	} else {
	    targetTask.setOrganizationTargetTaskFlag(FlagsEnum.OFF.getCode());
	    tasks.add(targetTask);
	}
    }

    public void deleteTargetTask(OrganizationTargetTaskDetailData targetTask) {
	if (FlagsEnum.ON.getCode() == targetTask.getOrganizationTargetTaskFlag())
	    targets.remove(targetTask);
	else
	    tasks.remove(targetTask);
    }

    public void save() {
	done = true;
	this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
    }

    public List<UnitData> getSelectedUnits() {
	return selectedUnits;
    }

    public void setSelectedUnits(List<UnitData> selectedUnits) {
	this.selectedUnits = selectedUnits;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    public String getDecisionDateString() {
	return HijriDateService.getHijriDateString(decisionDate);
    }

    public List<OrganizationTargetTaskDetailData> getTargets() {
	return targets;
    }

    public void setTargets(List<OrganizationTargetTaskDetailData> targets) {
	this.targets = targets;
    }

    public List<OrganizationTargetTaskDetailData> getTasks() {
	return tasks;
    }

    public void setTasks(List<OrganizationTargetTaskDetailData> tasks) {
	this.tasks = tasks;
    }

    // public Integer getOrganizationTargetTaskUnitsCount() {
    // return organizationTargetTaskUnitsCount;
    // }
    //
    // public void setOrganizationTargetTaskUnitsCount(Integer organizationTargetTaskUnitsCount) {
    // this.organizationTargetTaskUnitsCount = organizationTargetTaskUnitsCount;
    // }
    //
    // public List<OrganizationTargetTaskDetailData> getSelectedUnitOriginalTargetTaskDetailsData() {
    // return selectedUnitOriginalTargetTaskDetailsData;
    // }
    //
    // public void setSelectedUnitOriginalTargetTaskDetailsData(List<OrganizationTargetTaskDetailData> selectedUnitOriginalTargetTaskDetailsData) {
    // this.selectedUnitOriginalTargetTaskDetailsData = selectedUnitOriginalTargetTaskDetailsData;
    // }

    public boolean isDone() {
	return done;
    }

    public int getPageSize() {
	return pageSize;
    }
}
