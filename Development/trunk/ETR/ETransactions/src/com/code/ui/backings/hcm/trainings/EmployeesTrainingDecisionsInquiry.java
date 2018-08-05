package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingTransactionDetailData;
import com.code.dal.orm.security.EmployeeDecisionPrivilege;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeesTrainingDecisionsInquiry")
@ViewScoped
public class EmployeesTrainingDecisionsInquiry extends BaseBacking {
    private Date fromDate;
    private Date toDate;
    private long empId;
    private String empName;
    private Long transactionTypeId;

    private int pageSize = 10;

    private long trainingUnitId; // for mode 3

    private int mode;
    // 1 training specialist in general directorate (has access to all employees) or region (has access to his region employees only),
    // 2 training unit specialist (has access to all employees who took courses in his training unit)
    // 3 if Manager see all his employees in his unit hierarchy, if not manager he sees himself only

    private List<TrainingTransactionDetailData> transactionDetails;
    private List<EmployeeDecisionPrivilege> decisionsPrivileges;

    public EmployeesTrainingDecisionsInquiry() {
	try {
	    reset();

	    decisionsPrivileges = SecurityService.getEmployeesDecisionsPrivileges(this.loginEmpData.getEmpId(), WFProcessesGroupsEnum.TRAINING_AND_SCHOLARSHIP.getCode(), FlagsEnum.ALL.getCode());

	    if (!decisionsPrivileges.isEmpty()) {
		mode = 1;
	    } else {
		if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_EMPLOYEES_TRAINING_DECISIONS_INQUIRY.getCode(), MenuActionsEnum.TRN_UNIT_SPECIALIST.getCode())) {
		    trainingUnitId = TrainingSetupService.getTrainingUnitByPhysicalUnitHKey(loginEmpData.getPhysicalUnitHKey()).getUnitId();
		    mode = 2;
		} else {
		    mode = 3;
		    if (loginEmpData.getIsManager().equals(FlagsEnum.OFF.getCode())) {
			empId = loginEmpData.getEmpId();
			empName = loginEmpData.getName();
		    }
		}
	    }

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	fromDate = null;
	toDate = null;
	transactionTypeId = (long) FlagsEnum.ALL.getCode();
	transactionDetails = new ArrayList<>();
	if (loginEmpData.getIsManager().equals(FlagsEnum.ON.getCode()) || mode != 3) {
	    empId = (long) FlagsEnum.ALL.getCode();
	    empName = null;
	}
    }

    public void search() {

	try {
	    if (mode == 1 && !SecurityService.isDecisionPrivilegeGranted(this.loginEmpData, empId, decisionsPrivileges))
		throw new BusinessException("error_notAuthorized");

	    Long[] transactionTypeIds;
	    if (transactionTypeId != FlagsEnum.ALL.getCode()) {
		transactionTypeIds = new Long[] { transactionTypeId };
	    } else {
		transactionTypeIds = new Long[] { CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_RESULTS.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_TRAINEE_COURSE_EVENT_CANCEL.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId() };
	    }
	    transactionDetails = TrainingEmployeesService.getTrainingTransactionDetailsForEmployeesDecision(transactionTypeIds, mode == 2 ? trainingUnitId : FlagsEnum.ALL.getCode(), empId, mode == 3 ? loginEmpData.getPhysicalUnitHKey() : null, fromDate, toDate);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print(TrainingTransactionDetailData transactionDetail) {
	try {
	    byte[] bytes;
	    if (transactionDetail.getTransactionTypeId().longValue() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_RESULTS.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId())
		bytes = TrainingEmployeesService.getTraineeCertificateBytes(transactionDetail.getId());
	    else
		bytes = TrainingEmployeesService.getTraineeCourseCancellationDecisionBytes(transactionDetail.getId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
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

    public long getEmpId() {
	return empId;
    }

    public void setEmpId(long empId) {
	this.empId = empId;
    }

    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    public Long getTransactionTypeId() {
	return transactionTypeId;
    }

    public void setTransactionTypeId(Long transactionTypeId) {
	this.transactionTypeId = transactionTypeId;
    }

    public long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public List<TrainingTransactionDetailData> getTransactionDetails() {
	return transactionDetails;
    }

    public void setTransactionDetails(List<TrainingTransactionDetailData> transactionDetails) {
	this.transactionDetails = transactionDetails;
    }

}
