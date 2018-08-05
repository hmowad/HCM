package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.GradesEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "studyEnrollmentResultsRegiseration")
@ViewScoped
public class StudyEnrollmentResultsRegiseration extends BaseBacking {

    private TrainingTransactionData transactionData;
    private EmployeeData beneficiary;
    private List<TrainingTransactionData> beneficiaryTransactionsList;
    private long selectedTransactionId;

    public StudyEnrollmentResultsRegiseration() {
	setScreenTitle(getMessage("title_studyEnrollmentResultsRegiseration"));

	transactionData = new TrainingTransactionData();
	beneficiaryTransactionsList = new ArrayList<>();

    }

    public TrainingTransactionData getTransactionData() {
	return transactionData;
    }

    public void setTransactionData(TrainingTransactionData transactionData) {
	this.transactionData = transactionData;
    }

    public EmployeeData getBeneficiary() {
	return beneficiary;
    }

    public void setBeneficiary(EmployeeData beneficiary) {
	this.beneficiary = beneficiary;
    }

    public void selectEmployee() {
	try {

	    beneficiary = EmployeesService.getEmployeeData(transactionData.getEmployeeId());
	    beneficiaryTransactionsList = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.STUDY_ENROLLMENT.getCode() }, new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode() }, FlagsEnum.ALL.getCode(), beneficiary.getEmpId(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	    if (beneficiaryTransactionsList.isEmpty())
		throw new BusinessException("error_noStudyEnrollementForEmployee");
	    transactionData = beneficiaryTransactionsList.get(0);
	    if (transactionData.getSuccessFlag() == null) {
		transactionData.setSuccessFlag(FlagsEnum.ON.getCode());
		transactionData.setQualificationGrade(GradesEnum.NOT_AVAILABLE.getCode());
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectTransaction() {
	try {
	    transactionData = TrainingEmployeesService.getTrainingTransactionsDataByIds(new Long[] { selectedTransactionId }).get(0);
	    if (transactionData.getSuccessFlag() == null) {
		transactionData.setSuccessFlag(FlagsEnum.ON.getCode());
		transactionData.setQualificationGrade(GradesEnum.NOT_AVAILABLE.getCode());
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    transactionData.getTrainingTransaction().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    if (!transactionData.getSuccessFlagBoolean())
		transactionData.setStudyGraduationDate(null);
	    TrainingEmployeesService.saveTrainingTransactionResult(transactionData, FlagsEnum.ALL.getCode());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void successFlagChangeListener() {
	transactionData.setQualificationGrade(GradesEnum.NOT_AVAILABLE.getCode());
    }

    public List<TrainingTransactionData> getBeneficiaryTransactionsList() {
	return beneficiaryTransactionsList;
    }

    public void setBeneficiaryTransactionsList(List<TrainingTransactionData> beneficiaryTransactionsList) {
	this.beneficiaryTransactionsList = beneficiaryTransactionsList;
    }

    public long getSelectedTransactionId() {
	return selectedTransactionId;
    }

    public void setSelectedTransactionId(long selectedTransactionId) {
	this.selectedTransactionId = selectedTransactionId;
    }
}
