package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rankings;
import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.GradesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingCoursesService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "militaryExtCourseEventResultsRegistration")
@ViewScoped
public class MilitaryExtCourseEventResultsRegistration extends BaseBacking {

    private TrainingCourseEventData selectedCourseEvent;
    private TrainingCourseData selectedCourseData;
    private long selectedCourseEventId;
    private List<TrainingTransactionData> trainingTransactionsList;
    private TrainingTransactionData selectedTrainingTransaction;
    private List<Rankings> rankings;
    final int pageSize = 10;

    public MilitaryExtCourseEventResultsRegistration() {
	try {
	    setScreenTitle(getMessage("title_coursesEventsResultsRegistration"));
	    rankings = TrainingSetupService.getRankings(FlagsEnum.ALL.getCode());
	} catch (Exception e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectCourseEvent() {
	try {
	    selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
	    selectedCourseData = TrainingCoursesService.getTrainingCoursesById(selectedCourseEvent.getCourseId());
	    trainingTransactionsList = TrainingEmployeesService.getJoinedAndFininshedEmployeesInCourseEventTransactionsList(selectedCourseEventId);
	    selectedTrainingTransaction = null;

	    if (trainingTransactionsList.isEmpty()) {
		selectedCourseEvent = null;
		selectedCourseData = null;
		throw new BusinessException("error_noEmployeesJoinedInCourseEvent");
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectTrainingTransaction(TrainingTransactionData trainingTransaction) {
	selectedTrainingTransaction = trainingTransaction;
	if (selectedTrainingTransaction.getSuccessFlag() == null) {
	    selectedTrainingTransaction.setSuccessFlag(FlagsEnum.ON.getCode());
	    selectedTrainingTransaction.setQualificationGrade(GradesEnum.NOT_AVAILABLE.getCode());
	}
    }

    public void saveTrainingTransaction() {
	try {
	    selectedTrainingTransaction.getTrainingTransaction().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    if (!selectedTrainingTransaction.getSuccessFlagBoolean()) {
		selectedTrainingTransaction.setSuccessRankingDesc(null);
		selectedTrainingTransaction.setSuccessRanking(null);
	    }
	    TrainingEmployeesService.saveTrainingTransactionResult(selectedTrainingTransaction, selectedCourseEventId);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public String calculateRankingDesc() {
	if (selectedTrainingTransaction.getSuccessRanking() == null)
	    return "";
	if (selectedTrainingTransaction.getSuccessRanking().equals(0)) {
	    setServerSideErrorMessages(getMessage("error_successRankingCannotBeZeroOrLess"));
	    return "";
	}
	if (selectedTrainingTransaction.getSuccessRanking() > rankings.size()) {
	    setServerSideErrorMessages(getMessage("error_successRankingCannotBeHigherThanThousand"));
	    return "";
	}
	String successRankDesc = rankings.get(selectedTrainingTransaction.getSuccessRanking() - 1).getDescription();
	selectedTrainingTransaction.setSuccessRankingDesc(successRankDesc);
	return successRankDesc;
    }

    public void successFlagChangeListener() {
	selectedTrainingTransaction.setQualificationGrade(GradesEnum.NOT_AVAILABLE.getCode());
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

    public List<TrainingTransactionData> getTrainingTransactionsList() {
	return trainingTransactionsList;
    }

    public void setTrainingTransactionsList(List<TrainingTransactionData> trainingTransactionsList) {
	this.trainingTransactionsList = trainingTransactionsList;
    }

    public TrainingTransactionData getSelectedTrainingTransaction() {
	return selectedTrainingTransaction;
    }

    public void setSelectedTrainingTransaction(TrainingTransactionData selectedTrainingTransaction) {
	this.selectedTrainingTransaction = selectedTrainingTransaction;
    }

    public int getPageSize() {
	return pageSize;
    }
}
