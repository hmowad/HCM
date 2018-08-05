package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "militaryExtCourseEventAcceptanceRegisteration")
@ViewScoped
public class MilitaryExtCourseEventAcceptanceRegisteration extends BaseBacking {
    private TrainingCourseEventData selectedCourseEvent;
    private long courseEventId;
    private List<TrainingTransactionData> trainingTransactionsList;

    private final int pageSize = 10;

    public MilitaryExtCourseEventAcceptanceRegisteration() {
    }

    public void selectcourseEvent() {
	try {
	    selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(courseEventId);
	    trainingTransactionsList = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode() }, new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode(), TraineeStatusEnum.NOMINATION_REJECTED_FROM_PARTY.getCode() }, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(),
		    FlagsEnum.ALL.getCode(), selectedCourseEvent.getId());
	    if (trainingTransactionsList.isEmpty()) {
		throw new BusinessException("error_courseEventHasNoTrainees");
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateTrainingTransaction(TrainingTransactionData transaction) {
	try {
	    if (transaction.getStatus() == null)
		throw new BusinessException("error_nominationStatusMustBeAcceptedOrRejected");
	    TrainingEmployeesService.updateExternalMilitaryTraniningTransactionAcceptance(transaction.getId(), loginEmpData.getEmpId(), transaction.getStatus());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void toggleTransactionStatus(TrainingTransactionData transaction) {
	if (transaction.getStatus() == TraineeStatusEnum.NOMINATION_ACCEPTED.getCode()) {
	    transaction.setStatus(TraineeStatusEnum.NOMINATION_REJECTED_FROM_PARTY.getCode());
	} else {
	    transaction.setStatus(TraineeStatusEnum.NOMINATION_ACCEPTED.getCode());
	}
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
}
