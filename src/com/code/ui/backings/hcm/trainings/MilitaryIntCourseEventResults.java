package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rankings;
import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingCourseEventData;
import com.code.enums.FlagsEnum;
import com.code.enums.GradesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingCoursesService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;

@ManagedBean(name = "militaryIntCourseEventResults")
@ViewScoped
public class MilitaryIntCourseEventResults extends TrainingCoursesBase {

    private List<TrainingTransactionData> trainingTransactionsList;
    private TrainingTransactionData selectedTrainingTransaction;
    private TrainingCourseData selectedCourseData;
    private TrainingUnitData trainingUnit;
    private List<Rankings> rankings;

    public MilitaryIntCourseEventResults() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_coursesEventsResultsRegistration"));
	    wfTrainingCourseEvent = new WFTrainingCourseEventData();
	    trainingTransactionsList = new ArrayList<>();
	    rankings = TrainingSetupService.getRankings(FlagsEnum.ALL.getCode());
	    this.processId = WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_RESULTS.getCode();

	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) { // Requester
		trainingUnit = TrainingSetupService.getTrainingUnitByPhysicalUnitHKey(this.loginEmpData.getPhysicalUnitHKey());
		if (trainingUnit != null)
		    hasAccessPrivillege = true;
	    } else {
		hasAccessPrivillege = true;
		wfTrainingCourseEvent = TrainingCoursesEventsWorkFlow.getWFTrainingCourseEventsByInstanceId(this.instance.getInstanceId()).get(0);
		selectedCourseEventId = wfTrainingCourseEvent.getCourseEventId();
		selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
		selectedCourseData = TrainingCoursesService.getTrainingCoursesById(selectedCourseEvent.getCourseId());

		trainingTransactionsList = TrainingEmployeesService.getJoinedAndFininshedEmployeesInCourseEventTransactionsList(selectedCourseEventId);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
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

	    wfTrainingCourseEvent = TrainingCoursesEventsWorkFlow.constructWFTrainingCourseEventData(selectedCourseEvent, null, null);

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
	    TrainingCoursesEventsWorkFlow.validateRunningWFTrainingCourseEvents(wfTrainingCourseEvent.getCourseEventId(), wfTrainingCourseEvent.getInstanceId());
	    TrainingEmployeesService.saveTrainingTransactionResult(selectedTrainingTransaction, selectedCourseEventId);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
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

    public TrainingCourseData getSelectedCourseData() {
	return selectedCourseData;
    }

    public void setSelectedCourseData(TrainingCourseData selectedCourseData) {
	this.selectedCourseData = selectedCourseData;
    }

    public TrainingUnitData getTrainingUnit() {
	return trainingUnit;
    }

    public void setTrainingUnit(TrainingUnitData trainingUnit) {
	this.trainingUnit = trainingUnit;
    }
}
