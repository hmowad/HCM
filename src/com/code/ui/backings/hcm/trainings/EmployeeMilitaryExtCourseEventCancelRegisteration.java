package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeeMilitaryExtCourseEventCancel")
@ViewScoped
public class EmployeeMilitaryExtCourseEventCancelRegisteration extends BaseBacking {
    private TrainingTransactionData trainingTransaction;
    private TrainingCourseEventData selectedCourseEvent;
    private List<TrainingCourseEventData> courseEvents;
    protected EmployeeData beneficiary;
    long trainingTypeId;
    Integer[] statusIds;
    boolean savedFlag = false;
    private long employeeId;
    private long courseEventId;

    public EmployeeMilitaryExtCourseEventCancelRegisteration() {
	super.init();
	setScreenTitle(getMessage("title_employeeMilitaryExtCourseEventCancelRegisteration"));
	trainingTypeId = TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode();
	statusIds = new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode(), TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.JOINED.getCode() };
    }

    public void selectEmployee() {
	try {
	    beneficiary = EmployeesService.getEmployeeData(employeeId);
	    courseEvents = TrainingCoursesEventsService.getCoursesEventsDataForNomination(beneficiary.getEmpId(), trainingTypeId, statusIds, FlagsEnum.ALL.getCode());
	    if (courseEvents.isEmpty())
		throw new BusinessException("error_noCoursesForEmployee");
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectCourse() {
	try {
	    selectedCourseEvent = courseEventId == FlagsEnum.ALL.getCode() ? null : TrainingCoursesEventsService.getCourseEventById(courseEventId);
	    if (selectedCourseEvent == null)
		trainingTransaction = null;
	    else {
		List<TrainingTransactionData> trainingTransactions = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { trainingTypeId }, statusIds, FlagsEnum.ALL.getCode(), employeeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), courseEventId);
		if (!trainingTransactions.isEmpty()) {
		    trainingTransaction = trainingTransactions.get(0);
		    trainingTransaction.setReasons(null);
		} else
		    trainingTransaction = null;
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    TrainingEmployeesService.updateTraineeStatusForTraineeExtCourseEventCancellation(trainingTransaction.getId(), trainingTransaction.getReasonType(), trainingTransaction.getReasons(), this.loginEmpData.getEmpId());
	    savedFlag = true;
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	selectedCourseEvent = null;
	trainingTransaction = null;
	courseEvents = null;
	beneficiary = null;
	savedFlag = false;
	employeeId = FlagsEnum.ALL.getCode();
	courseEventId = FlagsEnum.ALL.getCode();
    }

    public TrainingTransactionData getTrainingTransaction() {
	return trainingTransaction;
    }

    public void setTrainingTransaction(TrainingTransactionData trainingTransaction) {
	this.trainingTransaction = trainingTransaction;
    }

    public EmployeeData getBeneficiary() {
	return beneficiary;
    }

    public void setBeneficiary(EmployeeData beneficiary) {
	this.beneficiary = beneficiary;
    }

    public List<TrainingCourseEventData> getCourseEvents() {
	return courseEvents;
    }

    public void setCourseEvents(List<TrainingCourseEventData> courseEvents) {
	this.courseEvents = courseEvents;
    }

    public TrainingCourseEventData getSelectedCourseEvent() {
	return selectedCourseEvent;
    }

    public void setSelectedCourseEvent(TrainingCourseEventData selectedCourseEvent) {
	this.selectedCourseEvent = selectedCourseEvent;
    }

    public boolean isSavedFlag() {
	return savedFlag;
    }

    public void setSavedFlag(boolean savedFlag) {
	this.savedFlag = savedFlag;
    }

    public long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(long employeeId) {
	this.employeeId = employeeId;
    }

    public long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(long courseEventId) {
	this.courseEventId = courseEventId;
    }

}