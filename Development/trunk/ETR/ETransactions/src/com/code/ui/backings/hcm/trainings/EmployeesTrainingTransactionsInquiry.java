package com.code.ui.backings.hcm.trainings;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeesTrainingTransactionsInquiry")
@ViewScoped
public class EmployeesTrainingTransactionsInquiry extends BaseBacking {

    private final int pageSize = 10;
    private boolean employeeMenuActionGranted;
    private Date fromDate = null;
    private Date toDate = null;
    private String trainingCourseName = null;
    private Long[] checkedTrainingTransactionsTypes = new Long[] { TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(), TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode(), TrainingTypesEnum.SCHOLARSHIP.getCode(), TrainingTypesEnum.STUDY_ENROLLMENT.getCode(), TrainingTypesEnum.MORNING_COURSE.getCode(), TrainingTypesEnum.EVENING_COURSE.getCode() };
    private String unitName = loginEmpData.getPhysicalUnitFullName();
    private int traineeSuccessFlag = FlagsEnum.ALL.getCode();
    private Integer traineeStatus = FlagsEnum.ALL.getCode();
    private Long categoryId = (long) FlagsEnum.ALL.getCode();
    private Long[] categoryIds = null;
    private List<TrainingTransactionData> trainingTransactionEmployee;

    public EmployeesTrainingTransactionsInquiry() {
	try {
	    employeeMenuActionGranted = SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_EMPLOYEES_TRAINING_TRANSACTIONS_INQUIRY.getCode(), MenuActionsEnum.TRN_EMPLOYEES_TRAINING_TRANSACTIONS_INQUIRY_ADMIN.getCode());
	    fromDate = HijriDateService.addSubHijriMonthsDays(HijriDateService.getHijriSysDate(), -12, 0);
	    toDate = HijriDateService.addSubHijriMonthsDays(HijriDateService.getHijriSysDate(), 12, 0);
	    search();
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    public void reset() {
	try {
	    fromDate = HijriDateService.addSubHijriMonthsDays(HijriDateService.getHijriSysDate(), -12, 0);
	    toDate = HijriDateService.addSubHijriMonthsDays(HijriDateService.getHijriSysDate(), 12, 0);
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
	trainingCourseName = null;
	checkedTrainingTransactionsTypes = new Long[] { TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(), TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode(), TrainingTypesEnum.SCHOLARSHIP.getCode(), TrainingTypesEnum.STUDY_ENROLLMENT.getCode(), TrainingTypesEnum.MORNING_COURSE.getCode(), TrainingTypesEnum.EVENING_COURSE.getCode() };
	unitName = loginEmpData.getPhysicalUnitFullName();
	traineeSuccessFlag = FlagsEnum.ALL.getCode();
	traineeStatus = FlagsEnum.ALL.getCode();
	categoryId = (long) FlagsEnum.ALL.getCode();
	categoryIds = null;

	search();
    }

    public void search() {
	try {
	    if (categoryId == (long) FlagsEnum.ALL.getCode())
		categoryIds = CommonService.getAllCategoriesIdsArray();
	    else if (categoryId == CategoriesEnum.OFFICERS.getCode())
		categoryIds = CommonService.getOfficerCategoriesIdsArray();
	    else if (categoryId == CategoriesEnum.SOLDIERS.getCode())
		categoryIds = CommonService.getSoldiersCategoriesIdsArray();
	    else
		categoryIds = CommonService.getCivilCategoriesIdsArray();

	    trainingTransactionEmployee = TrainingEmployeesService.getEmployeesTrainingTransactions(checkedTrainingTransactionsTypes, traineeStatus == FlagsEnum.ALL.getCode() ? null : new Integer[] { traineeStatus }, traineeSuccessFlag, unitName, categoryIds, trainingCourseName, fromDate, toDate);
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    public boolean isEmployeeMenuActionGranted() {
	return employeeMenuActionGranted;
    }

    public void setEmployeeMenuActionGranted(boolean employeeMenuActionGranted) {
	this.employeeMenuActionGranted = employeeMenuActionGranted;
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

    public String getTrainingCourseName() {
	return trainingCourseName;
    }

    public void setTrainingCourseName(String trainingCourseName) {
	this.trainingCourseName = trainingCourseName;
    }

    public Long[] getCheckedTrainingTransactionsTypes() {
	return checkedTrainingTransactionsTypes;
    }

    public void setCheckedTrainingTransactionsTypes(Long[] checkedTrainingTransactionsTypes) {
	this.checkedTrainingTransactionsTypes = checkedTrainingTransactionsTypes;
    }

    public String getUnitName() {
	return unitName;
    }

    public void setUnitName(String unitName) {
	this.unitName = unitName;
    }

    public int getTraineeSuccessFlag() {
	return traineeSuccessFlag;
    }

    public void setTraineeSuccessFlag(int traineeSuccessFlag) {
	this.traineeSuccessFlag = traineeSuccessFlag;
    }

    public Integer getTraineeStatus() {
	return traineeStatus;
    }

    public void setTraineeStatus(Integer traineeStatus) {
	this.traineeStatus = traineeStatus;
    }

    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    public Long[] getCategoryIds() {
	return categoryIds;
    }

    public void setCategoryIds(Long[] categoryIds) {
	this.categoryIds = categoryIds;
    }

    public List<TrainingTransactionData> getTrainingTransactionEmployee() {
	return trainingTransactionEmployee;
    }

    public void setTrainingTransactionEmployee(List<TrainingTransactionData> trainingTransactionEmployee) {
	this.trainingTransactionEmployee = trainingTransactionEmployee;
    }

    public int getPageSize() {
	return pageSize;
    }
}
