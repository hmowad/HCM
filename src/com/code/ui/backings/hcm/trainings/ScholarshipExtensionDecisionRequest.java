package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionDetailData;
import com.code.enums.FlagsEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@ManagedBean(name = "scholarshipExtensionDecisionRequest")
@ViewScoped
public class ScholarshipExtensionDecisionRequest extends TrainingAndScholarshipBase {
    TrainingTransactionData trainingTransaction;
    TrainingTransactionDetailData trainingTransactionDetail;
    private long selectedEmployeeId;
    private long selectedEmpCategoryId;
    long trainingTypeId;
    Integer[] statusIds;
    boolean modify;

    public ScholarshipExtensionDecisionRequest() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_scholarshipExtensionDecisionRequest"));
	    trainingTypeId = TrainingTypesEnum.SCHOLARSHIP.getCode();
	    statusIds = new Integer[] { TraineeStatusEnum.SCHOLARSHIP.getCode(), TraineeStatusEnum.SCHOLARSHIP_EXTENSION.getCode() };
	    this.processId = WFProcessesEnum.SCHOLARSHIP_EXTENSION.getCode();

	    modify = this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode()) || this.getRole().equals(WFTaskRolesEnum.REVIEWER_EMP.getCode());
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		internalCopies = new ArrayList<EmployeeData>();
	    } else {
		wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
		selectedEmployeeId = wfTraining.getEmployeeId();
		getScholarShipDetails(wfTraining.getBasedOnTrainingTransactionId());
		internalCopies = EmployeesService.getEmployeesByIdsString(wfTraining.getInternalCopies());
	    }

	    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode())) {
		selectedReviewerEmpId = 0L;
		reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
	    }
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void constructWFTraining() {
	try {

	    getScholarShipDetails(null);
	    wfTraining = TrainingEmployeesWorkFlow.constructWFTrainingForScholarshipDecision(trainingTransaction.getId(), beneficiary.getEmpId(), beneficiary.getCategoryId(), processId);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void getScholarShipDetails(Long trainingTransactionId) throws BusinessException {
	beneficiary = EmployeesService.getEmployeeData(selectedEmployeeId);
	List<TrainingTransactionData> trainingTransactions;
	if (trainingTransactionId == null) {
	    trainingTransactions = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { trainingTypeId }, statusIds, FlagsEnum.ALL.getCode(), selectedEmployeeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());

	    if (trainingTransactions.isEmpty()) {
		wfTraining = null;
		trainingTransaction = null;
		trainingTransactionDetail = null;
		throw new BusinessException("error_noScholarshipForEmployee");
	    }
	} else
	    trainingTransactions = TrainingEmployeesService.getTrainingTransactionsDataByIds(new Long[] { trainingTransactionId });

	trainingTransaction = trainingTransactions.get(0);
	List<TrainingTransactionDetailData> transactionDetails = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(trainingTransaction.getId());
	// if (!transactionDetails.isEmpty())// TODO delete this
	trainingTransactionDetail = transactionDetails.get(0);

    }

    public void manipulateEndDate() {
	try {
	    wfTraining.setEndDate(HijriDateService.addSubHijriMonthsDays(trainingTransaction.getStudyEndDate(), wfTraining.getMonthsCount() == null ? 0 : wfTraining.getMonthsCount(), wfTraining.getDaysCount() == null ? 0 : wfTraining.getDaysCount()));

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));

	}

    }

    public TrainingTransactionData getTrainingTransaction() {
	return trainingTransaction;
    }

    public void setTrainingTransaction(TrainingTransactionData trainingTransaction) {
	this.trainingTransaction = trainingTransaction;
    }

    public TrainingTransactionDetailData getTrainingTransactionDetail() {
	return trainingTransactionDetail;
    }

    public void setTrainingTransactionDetail(TrainingTransactionDetailData trainingTransactionDetail) {
	this.trainingTransactionDetail = trainingTransactionDetail;
    }

    public long getSelectedEmployeeId() {
	return selectedEmployeeId;
    }

    public void setSelectedEmployeeId(long selectedEmployeeId) {
	this.selectedEmployeeId = selectedEmployeeId;
    }

    public long getSelectedEmpCategoryId() {
	return selectedEmpCategoryId;
    }

    public void setSelectedEmpCategoryId(long selectedEmpCategoryId) {
	this.selectedEmpCategoryId = selectedEmpCategoryId;
    }

    public boolean isModify() {
	return modify;
    }

}
