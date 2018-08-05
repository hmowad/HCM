package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventDecisionData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.dal.orm.security.EmployeeDecisionPrivilege;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "coursesEventsDecisionsInquiry")
@ViewScoped
public class CoursesEventsDecisionsInquiry extends BaseBacking {

    private int mode;
    // 1 training specialist in general directorate (All training units)
    // 2 training specialist in region (only login employee region training units)
    // 1,2 training unit specialist (if in general sees all training units, if region sees only his training unit)
    private TrainingUnitData trainingUnit; // for mode 2
    private int rows = 10;

    public boolean hasPrivilege;
    public String errorMessage;

    private long trainingYearId;
    private long trainingUnitId;
    private long transactionTypeId;
    private long courseEventId;
    private String courseEventName;
    private Date fromDate;
    private Date toDate;

    private List<TrainingYear> trainingYears;
    private List<TrainingUnitData> trainingUnits;

    private List<TrainingCourseEventDecisionData> transactions;
    private List<EmployeeDecisionPrivilege> decisionsPrivileges;
    private boolean disableTrainingUnitsMenu;

    public CoursesEventsDecisionsInquiry() {

	try {
	    TrainingUnitData trainingUnit = null;
	    decisionsPrivileges = SecurityService.getEmployeesDecisionsPrivileges(this.loginEmpData.getEmpId(), WFProcessesGroupsEnum.TRAINING_AND_SCHOLARSHIP.getCode(), FlagsEnum.ALL.getCode());
	    hasPrivilege = decisionsPrivileges.size() == 0 ? false : true;
	    disableTrainingUnitsMenu = true;
	    if (hasPrivilege) {
		if (loginEmpData.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
		    mode = 1;
		    trainingUnits = TrainingSetupService.getAllTrainingUnitsData();
		} else {
		    trainingUnit = TrainingSetupService.getTrainingUnitByPhysicalUnitHKey(loginEmpData.getPhysicalUnitHKey());
		    mode = 2;
		    if (trainingUnit != null) {
			trainingUnits = new ArrayList<>();
			trainingUnitId = trainingUnit.getUnitId();
			trainingUnits.add(trainingUnit);
			disableTrainingUnitsMenu = false;
		    } else {
			trainingUnits = TrainingSetupService.getTrainingUnitsByRegionId(loginEmpData.getPhysicalRegionId());
		    }
		}
	    } else
		errorMessage = getMessage("label_onlyTrainingPlanningUnitAndRegionsTrainingSpecialistsAndTrainingUnitsEmployeesPrivilge");

	    reset();
	    trainingYears = TrainingSetupService.getApprovedTrainingYears();
	} catch (BusinessException e) {
	}
    }

    public void reset() {
	fromDate = null;
	toDate = null;
	courseEventName = null;
	transactionTypeId = courseEventId = trainingYearId = FlagsEnum.ALL.getCode();
	transactions = new ArrayList<>();
	if (mode == 1) {
	    trainingUnitId = FlagsEnum.ALL.getCode();
	}
    }

    public void search() {
	try {
	    transactions = TrainingCoursesEventsService.getTrainingCoursesEventDecisionsForInquiry(trainingYearId, trainingUnitId, courseEventId, transactionTypeId, fromDate, toDate);
	} catch (BusinessException e) {

	}
    }

    public void print(TrainingCourseEventDecisionData transaction) {
	try {
	    byte[] bytes = TrainingCoursesEventsService.getTrainingCourseEventDecisionBytes(transaction.getId(), transaction.getTransactionTypeId());
	    super.print(bytes);
	} catch (BusinessException e) {
	}
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public TrainingUnitData getTrainingUnit() {
	return trainingUnit;
    }

    public void setTrainingUnit(TrainingUnitData trainingUnit) {
	this.trainingUnit = trainingUnit;
    }

    public int getRows() {
	return rows;
    }

    public void setRows(int rows) {
	this.rows = rows;
    }

    public long getTrainingYearId() {
	return trainingYearId;
    }

    public void setTrainingYearId(long trainingYearId) {
	this.trainingYearId = trainingYearId;
    }

    public long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    public long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
    }

    public long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(long courseEventId) {
	this.courseEventId = courseEventId;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    public List<TrainingYear> getTrainingYears() {
	return trainingYears;
    }

    public void setTrainingYears(List<TrainingYear> trainingYears) {
	this.trainingYears = trainingYears;
    }

    public List<TrainingUnitData> getTrainingUnits() {
	return trainingUnits;
    }

    public void setTrainingUnits(List<TrainingUnitData> trainingUnits) {
	this.trainingUnits = trainingUnits;
    }

    public List<TrainingCourseEventDecisionData> getTransactions() {
	return transactions;
    }

    public void setTransactions(List<TrainingCourseEventDecisionData> transactions) {
	this.transactions = transactions;
    }

    public String getCourseEventName() {
	return courseEventName;
    }

    public void setCourseEventName(String courseEventName) {
	this.courseEventName = courseEventName;
    }

    public boolean isHasPrivilege() {
	return hasPrivilege;
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public boolean isDisableTrainingUnitsMenu() {
	return disableTrainingUnitsMenu;
    }

    public void setDisableTrainingUnitsMenu(boolean disableTrainingUnitsMenu) {
	this.disableTrainingUnitsMenu = disableTrainingUnitsMenu;
    }

}
