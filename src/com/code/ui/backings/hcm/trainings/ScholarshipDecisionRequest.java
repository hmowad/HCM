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
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@ManagedBean(name = "scholarshipDecisionRequest")
@ViewScoped
public class ScholarshipDecisionRequest extends TrainingAndScholarshipBase {

    private List<QualificationLevel> qualificationLevels;
    private List<TrainingTransactionData> scholarships;
    private Long transactionId;

    public ScholarshipDecisionRequest() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_scholarshipDecisionRequest"));
	    qualificationLevels = TrainingSetupService.getAllQualificationLevels();

	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		this.processId = WFProcessesEnum.SCHOLARSHIP.getCode();
		wfTraining = new WFTrainingData();
		internalCopies = new ArrayList<EmployeeData>();

	    } else {
		wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
		beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
		transactionId = wfTraining.getBasedOnTrainingTransactionId();
		this.processId = this.instance.getProcessId();
		internalCopies = EmployeesService.getEmployeesByIdsString(wfTraining.getInternalCopies());
		scholarships = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.SCHOLARSHIP.getCode() }, new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode() }, FlagsEnum.ALL.getCode(), beneficiary.getEmpId(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());

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
	    scholarships = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.SCHOLARSHIP.getCode() }, new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode() }, FlagsEnum.ALL.getCode(), beneficiary.getEmpId(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	    selectScholarship();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectScholarship() throws BusinessException {
	wfTraining = TrainingEmployeesWorkFlow.constructWFTrainingForScholarshipDecision(transactionId, beneficiary.getEmpId(), beneficiary.getCategoryId(), this.processId);
    }

    public void manipulateScholarShipEndDate() {
	try {
	    if (wfTraining.getStartDateString() != null) {
		wfTraining.setEndDateString(HijriDateService.addSubStringHijriMonthsDays(wfTraining.getStartDateString(), wfTraining.getMonthsCount() == null ? 0 : wfTraining.getMonthsCount(), wfTraining.getDaysCount() == null ? FlagsEnum.ALL.getCode() : wfTraining.getDaysCount()));
	    } else
		wfTraining.setEndDateString(null);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    long decisionType = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_NEW_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	    List<TrainingTransactionDetailData> transactionDetails = TrainingEmployeesService.getTrainingTransactionDetailForScholarshipInqueryByMinistryDecisionNumberAndMinistryDecisonDate(new Long[] { TrainingTypesEnum.SCHOLARSHIP.getCode() },
		    new Integer[] { TraineeStatusEnum.SCHOLARSHIP.getCode(), TraineeStatusEnum.SCHOLARSHIP_EXTENSION.getCode(), TraineeStatusEnum.SCHOLARSHIP_CANCELLATION.getCode(), TraineeStatusEnum.SCHOLARSHIP_TERMINATION.getCode() },
		    beneficiary.getEmpId(), decisionType, null, wfTraining.getMinistryDecisionNumber(), wfTraining.getMinistryDecisionDate());
	    // List<TrainingTransactionDetailData> transactionDetails = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(transactionId);
	    TrainingTransactionDetailData trainingTransactionDetail = transactionDetails.get(0);
	    printScholarshipDecision(trainingTransactionDetail.getId(), trainingTransactionDetail.getTransactionTypeId());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<QualificationLevel> getQualificationLevels() {
	return qualificationLevels;
    }

    public void setQualificationLevels(List<QualificationLevel> qualificationLevels) {
	this.qualificationLevels = qualificationLevels;
    }

    public List<TrainingTransactionData> getScholarships() {
	return scholarships;
    }

    public void setScholarships(List<TrainingTransactionData> scholarships) {
	this.scholarships = scholarships;
    }

    public Long getTransactionId() {
	return transactionId;
    }

    public void setTransactionId(Long transactionId) {
	this.transactionId = transactionId;
    }

}