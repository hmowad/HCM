package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.GradesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingCoursesService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "morningTrainingResultsRegisteration")
@ViewScoped
public class MorningTrainingResultsRegisteration extends BaseBacking {
    int pageSize = 10;
    private TrainingTransactionData selectedTrainingTransaction;
    private TrainingCourseEventData selectedCourseEvent;
    private TrainingCourseData selectedCourseData;
    private long selectedCourseEventId;
    private List<TrainingTransactionData> trainingTransactions;

    long trainingTypeId;
    Integer[] statusIds;

    public MorningTrainingResultsRegisteration() {

	super.init();
	setScreenTitle(getMessage("title_morningTrainingResultsRegisteration"));

	trainingTypeId = TrainingTypesEnum.MORNING_COURSE.getCode();
	statusIds = new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode() };

	selectedCourseEventId = FlagsEnum.ALL.getCode();

    }

    public void selectTransaction(TrainingTransactionData t) {
	selectedTrainingTransaction = t;
	if (selectedTrainingTransaction.getSuccessFlag() == null) {
	    selectedTrainingTransaction.setSuccessFlag(FlagsEnum.ON.getCode());
	    selectedTrainingTransaction.setQualificationGrade(GradesEnum.NOT_AVAILABLE.getCode());
	}
    }

    public void successFlagChangeListener() {
	selectedTrainingTransaction.setQualificationGrade(GradesEnum.NOT_AVAILABLE.getCode());
    }

    public void selectCourse() {
	try {
	    selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
	    setSelectedCourseData(TrainingCoursesService.getTrainingCoursesById(selectedCourseEvent.getCourseId()));
	    trainingTransactions = TrainingEmployeesService.getTrainingTransactionsDataByPhysicalRegionId(new Long[] { trainingTypeId }, statusIds, FlagsEnum.ALL.getCode(), selectedCourseEventId, loginEmpData.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? FlagsEnum.ALL.getCode() : loginEmpData.getPhysicalRegionId());
	    selectedTrainingTransaction = null;
	    if (trainingTransactions.isEmpty())
		throw new BusinessException("error_courseEventHasNoTrainees");
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    selectedTrainingTransaction.getTrainingTransaction().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    TrainingEmployeesService.saveTrainingTransactionResult(selectedTrainingTransaction, selectedCourseEventId);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public TrainingCourseEventData getSelectedCourseEvent() {
	return selectedCourseEvent;
    }

    public void setSelectedCourseEvent(TrainingCourseEventData selectedCourseEvent) {
	this.selectedCourseEvent = selectedCourseEvent;
    }

    public TrainingCourseData getSelectedCourseData() {
	return selectedCourseData;
    }

    public void setSelectedCourseData(TrainingCourseData selectedCourseData) {
	this.selectedCourseData = selectedCourseData;
    }

    public long getSelectedCourseEventId() {
	return selectedCourseEventId;
    }

    public void setSelectedCourseEventId(long selectedCourseEventId) {
	this.selectedCourseEventId = selectedCourseEventId;
    }

    public TrainingTransactionData getSelectedTrainingTransaction() {
	return selectedTrainingTransaction;
    }

    public void setSelectedTrainingTransaction(TrainingTransactionData selectedTrainingTransaction) {
	this.selectedTrainingTransaction = selectedTrainingTransaction;
    }

    public List<TrainingTransactionData> getTrainingTransactions() {
	return trainingTransactions;
    }

    public void setTrainingTransactions(List<TrainingTransactionData> trainingTransactions) {
	this.trainingTransactions = trainingTransactions;
    }

    public int getPageSize() {
	return pageSize;
    }

}