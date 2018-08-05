package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.QualificationLevel;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionDetailData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.enums.FlagsEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@ManagedBean(name = "scholarshipCancelDecisionRequest")
@ViewScoped
public class ScholarshipCancelDecisionRequest extends TrainingAndScholarshipBase {

    private List<QualificationLevel> qualificationLevels;
    private List<TrainingTransactionData> scholarships;
    private TrainingTransactionDetailData selectedTransactionDetail;
    private Long transactionId;

    public ScholarshipCancelDecisionRequest() {

	try {
	    super.init();
	    setScreenTitle(getMessage("title_scholarshipCancelDecisionRequest"));
	    setQualificationLevels(TrainingSetupService.getAllQualificationLevels());
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.processId = WFProcessesEnum.SCHOLARSHIP_CANCEL.getCode();
		wfTraining = new WFTrainingData();
		setScholarships(new ArrayList<>());
		internalCopies = new ArrayList<EmployeeData>();
	    } else {
		wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
		this.processId = this.instance.getProcessId();
		beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
		scholarships = TrainingEmployeesService.getTrainingTransactionsDataByIds(new Long[] { wfTraining.getBasedOnTrainingTransactionId() });
		transactionId = scholarships.get(0).getId();
		internalCopies = EmployeesService.getEmployeesByIdsString(wfTraining.getInternalCopies());
		selectedTransactionDetail = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(transactionId).get(0);
		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode())) {
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		}
	    }

	} catch (BusinessException e) {
	    e.printStackTrace();
	}

    }

    public void selectEmployee() {
	try {

	    beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
	    scholarships = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.SCHOLARSHIP.getCode() }, new Integer[] { TraineeStatusEnum.SCHOLARSHIP.getCode(), TraineeStatusEnum.SCHOLARSHIP_EXTENSION.getCode(), TraineeStatusEnum.SCHOLARSHIP_TERMINATION.getCode() }, FlagsEnum.ALL.getCode(), beneficiary.getEmpId(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	    if (scholarships.isEmpty())
		throw new BusinessException("error_noScholarshipForEmployee");
	    transactionId = scholarships.get(0).getId();
	    selectedTransactionDetail = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(transactionId).get(0);
	    wfTraining = TrainingEmployeesWorkFlow.constructWFTrainingForScholarshipDecision(transactionId, beneficiary.getEmpId(), beneficiary.getCategoryId(), this.processId);

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<TrainingTransactionData> getScholarships() {
	return scholarships;
    }

    public void setScholarships(List<TrainingTransactionData> scholarships) {
	this.scholarships = scholarships;
    }

    public List<QualificationLevel> getQualificationLevels() {
	return qualificationLevels;
    }

    public void setQualificationLevels(List<QualificationLevel> qualificationLevels) {
	this.qualificationLevels = qualificationLevels;
    }

    public TrainingTransactionDetailData getSelectedTransactionDetail() {
	return selectedTransactionDetail;
    }

    public void setSelectedTransactionDetail(TrainingTransactionDetailData selectedTransactionDetail) {
	this.selectedTransactionDetail = selectedTransactionDetail;
    }

}
