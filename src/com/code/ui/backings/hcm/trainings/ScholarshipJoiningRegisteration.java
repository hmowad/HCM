package com.code.ui.backings.hcm.trainings;

import java.io.Serializable;
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
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "scholarshipJoiningRegisteration")
@ViewScoped
public class ScholarshipJoiningRegisteration extends BaseBacking implements Serializable {

    private List<TrainingTransactionData> scholarships;
    private TrainingTransactionData scholarshipTransactionData;
    private TrainingTransactionDetailData scholarshipTransactionDetail;
    private EmployeeData employeeData;

    public ScholarshipJoiningRegisteration() {

	super.init();
	setScreenTitle(getMessage("title_scholarshipJoiningRegisteration"));
	employeeData = new EmployeeData();
	scholarships = new ArrayList<>();

    }

    public void selectEmployee() {
	try {
	    employeeData = EmployeesService.getEmployeeData(employeeData.getEmpId());
	    scholarships = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.SCHOLARSHIP.getCode() }, new Integer[] { TraineeStatusEnum.SCHOLARSHIP.getCode(), TraineeStatusEnum.SCHOLARSHIP_EXTENSION.getCode(), TraineeStatusEnum.SCHOLARSHIP_TERMINATION.getCode() }, FlagsEnum.ALL.getCode(), employeeData.getEmpId(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	    if (scholarships.isEmpty())
		throw new BusinessException("error_noScholarshipForEmployee");
	    scholarshipTransactionData = scholarships.get(0);
	    scholarshipTransactionDetail = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(scholarshipTransactionData.getId()).get(0);

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateTrainingTransactionJoiningDate() {
	try {
	    TrainingEmployeesService.updateTrainingTransactionTrainingJoiningDate(scholarshipTransactionData.getId(), scholarshipTransactionData.getTrainingJoiningDate(), this.loginEmpData.getEmpId(), scholarshipTransactionData.getStatus());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
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

    public EmployeeData getEmployeeData() {
	return employeeData;
    }

    public void setEmployeeData(EmployeeData employeeData) {
	this.employeeData = employeeData;
    }

    public TrainingTransactionData getScholarshipTransactionData() {
	return scholarshipTransactionData;
    }

    public void setScholarshipTransactionData(TrainingTransactionData scholarshipTransactionData) {
	this.scholarshipTransactionData = scholarshipTransactionData;
    }

    public TrainingTransactionDetailData getScholarshipTransactionDetail() {
	return scholarshipTransactionDetail;
    }

    public void setScholarshipTransactionDetail(TrainingTransactionDetailData scholarshipTransactionDetail) {
	this.scholarshipTransactionDetail = scholarshipTransactionDetail;
    }

}
