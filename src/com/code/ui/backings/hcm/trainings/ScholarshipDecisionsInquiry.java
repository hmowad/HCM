package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingTransactionDetailData;
import com.code.dal.orm.security.EmployeeDecisionPrivilege;
import com.code.enums.FlagsEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "scholarshipDecisionsInquiry")
@ViewScoped
public class ScholarshipDecisionsInquiry extends BaseBacking {

    private final int pageSize = 10;
    private long selectedEmployeeId;
    private int decisionType;
    private String decisionNumber;
    private String selectedEmployeeName;

    List<TrainingTransactionDetailData> trainingTransactionDetails;
    long trainingTypeId;
    Integer[] statusIds;
    boolean trainingUnitManagerFlag;
    private List<EmployeeDecisionPrivilege> decisionsPrivileges;

    public ScholarshipDecisionsInquiry() {
	try {
	    setScreenTitle(getMessage("title_scholarshipDecisions"));
	    trainingTypeId = TrainingTypesEnum.SCHOLARSHIP.getCode();
	    statusIds = new Integer[] { TraineeStatusEnum.SCHOLARSHIP.getCode(), TraineeStatusEnum.SCHOLARSHIP_EXTENSION.getCode(), TraineeStatusEnum.SCHOLARSHIP_CANCELLATION.getCode(), TraineeStatusEnum.SCHOLARSHIP_TERMINATION.getCode() };
	    decisionsPrivileges = SecurityService.getEmployeesDecisionsPrivileges(this.loginEmpData.getEmpId(), WFProcessesGroupsEnum.TRAINING_AND_SCHOLARSHIP.getCode(), FlagsEnum.ALL.getCode());
	    if (decisionsPrivileges.size() == 0) {
		trainingUnitManagerFlag = false;
		if (loginEmpData.getIsManager() != FlagsEnum.ON.getCode()) {
		    selectedEmployeeId = this.loginEmpData.getEmpId();
		    selectedEmployeeName = this.loginEmpData.getName();
		}
	    } else
		trainingUnitManagerFlag = true;
	    reset();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	trainingTransactionDetails = null;
	decisionType = FlagsEnum.ALL.getCode();
	decisionNumber = null;
	if (trainingUnitManagerFlag || loginEmpData.getIsManager() == FlagsEnum.ON.getCode()) {
	    selectedEmployeeId = FlagsEnum.ALL.getCode();
	    selectedEmployeeName = null;
	}
    }

    public void search() {
	try {
	    if (trainingUnitManagerFlag && !SecurityService.isDecisionPrivilegeGranted(this.loginEmpData, selectedEmployeeId, decisionsPrivileges))
		throw new BusinessException("error_notAuthorized");

	    trainingTransactionDetails = TrainingEmployeesService.getTrainingTransactionDetailForScholarshipInquery(new Long[] { trainingTypeId }, statusIds, selectedEmployeeId, decisionType, decisionNumber);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print(TrainingTransactionDetailData trainingTransactionDetail) {
	try {
	    byte[] bytes = TrainingEmployeesService.getScholarshipDecisionBytes(trainingTransactionDetail.getId(), trainingTransactionDetail.getTransactionTypeId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public long getSelectedEmployeeId() {
	return selectedEmployeeId;
    }

    public void setSelectedEmployeeId(long selectedEmployeeId) {
	this.selectedEmployeeId = selectedEmployeeId;
    }

    public List<TrainingTransactionDetailData> getTrainingTransactionDetails() {
	return trainingTransactionDetails;
    }

    public void setTrainingTransactionDetails(List<TrainingTransactionDetailData> trainingTransactionDetails) {
	this.trainingTransactionDetails = trainingTransactionDetails;
    }

    public int getPageSize() {
	return pageSize;
    }

    public int getDecisionType() {
	return decisionType;
    }

    public void setDecisionType(int decisionType) {
	this.decisionType = decisionType;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public String getSelectedEmployeeName() {
	return selectedEmployeeName;
    }

    public void setSelectedEmployeeName(String selectedEmployeeName) {
	this.selectedEmployeeName = selectedEmployeeName;
    }

    public boolean isTrainingUnitManagerFlag() {
	return trainingUnitManagerFlag;
    }

}