package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.enums.FlagsEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "militaryCourseEventJoiningRegistration")
@ViewScoped
public class MilitaryCourseEventJoiningRegistration extends BaseBacking {
    private long trainingType; // 1 for internal , 2 for external
    private long trainingUnitId = FlagsEnum.ALL.getCode();
    private String errorMessage;
    private boolean hasPrivilage;
    private TrainingCourseEventData selectedCourseEvent;
    private long courseEventId;
    private List<TrainingTransactionData> trainingTransactionsList;

    private final int pageSize = 10;

    public MilitaryCourseEventJoiningRegistration() {
	if (getRequest().getParameter("trainingType") != null) {
	    trainingType = Integer.parseInt(getRequest().getParameter("trainingType"));
	}
	try {
	    if (trainingType == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode()) {
		TrainingUnitData trainingUnit = TrainingSetupService.getTrainingUnitByPhysicalUnitHKey(this.loginEmpData.getPhysicalUnitHKey());
		if (trainingUnit == null) {
		    errorMessage = getMessage("label_trnTrainingUnitSpecialistPrivilege");
		} else {
		    hasPrivilage = true;
		    trainingUnitId = trainingUnit.getUnitId();
		}
	    } else {
		hasPrivilage = true;
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void selectcourseEvent() {
	try {
	    trainingTransactionsList = TrainingEmployeesService.getTrainingTransactionsForJoiningRegistration(new Long[] { trainingType }, new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.JOINED.getCode() }, courseEventId);

	    if (trainingTransactionsList.isEmpty()) {
		selectedCourseEvent = null;
		throw new BusinessException("error_noAcceptedOrJoinedEmployees");
	    }

	    selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(courseEventId);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateTrainingTransactionJoiningDate(TrainingTransactionData transaction) {
	try {
	    TrainingEmployeesService.updateTrainingTransactionTrainingJoiningDate(transaction.getId(), transaction.getTrainingJoiningDate(), this.loginEmpData.getEmpId(), transaction.getStatus());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void toggleTransactionStatus(TrainingTransactionData transaction) {
	if (transaction.getStatus() == TraineeStatusEnum.JOINED.getCode()) {
	    transaction.setStatus(TraineeStatusEnum.NOMINATION_ACCEPTED.getCode());
	    transaction.setTrainingJoiningDate(null);
	} else {
	    transaction.setStatus(TraineeStatusEnum.JOINED.getCode());
	    transaction.setTrainingJoiningDate(selectedCourseEvent.getActualStartDate());
	}
    }

    public long getTrainingType() {
	return trainingType;
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    public TrainingCourseEventData getSelectedCourseEvent() {
	return selectedCourseEvent;
    }

    public void setSelectedCourseEvent(TrainingCourseEventData selectedCourseEvent) {
	this.selectedCourseEvent = selectedCourseEvent;
    }

    public long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(long courseEventId) {
	this.courseEventId = courseEventId;
    }

    public List<TrainingTransactionData> getTrainingTransactionsList() {
	return trainingTransactionsList;
    }

    public void setTrainingTransactionsList(List<TrainingTransactionData> trainingTransactionsList) {
	this.trainingTransactionsList = trainingTransactionsList;
    }

    public int getPageSize() {
	return pageSize;
    }

    public long getTrainingUnitId() {
	return trainingUnitId;
    }

    public boolean isHasPrivilage() {
	return hasPrivilage;
    }

}
