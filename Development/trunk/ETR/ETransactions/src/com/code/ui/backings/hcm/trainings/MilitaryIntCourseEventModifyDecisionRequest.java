package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventDecisionEmployee;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingCourseEventData;
import com.code.enums.CategoriesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;

@ManagedBean(name = "militaryIntCourseEventModifyDecisionRequest")
@ViewScoped
public class MilitaryIntCourseEventModifyDecisionRequest extends TrainingCoursesBase {

    private List<Rank> ranksList;
    private String warningMessage;

    public MilitaryIntCourseEventModifyDecisionRequest() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_militaryIntCourseEventModifyDecisionRequest"));

	    wfTrainingCourseEventsList = new ArrayList<>();
	    wfTrainingCourseEventsForExternalEmpsList = new ArrayList<>();
	    this.processId = WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode();

	    ranksList = CommonService.getRanks(null, new Long[] { CategoriesEnum.OFFICERS.getCode(), CategoriesEnum.SOLDIERS.getCode() });

	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) { // Requester
		internalCopies = new ArrayList<EmployeeData>();
	    } else {
		wfTrainingCourseEventsList = TrainingCoursesEventsWorkFlow.getWFTrainingCourseEventsByInstanceId(this.instance.getInstanceId());
		selectedCourseEventId = wfTrainingCourseEventsList.get(0).getCourseEventId();
		selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
		internalCopies = EmployeesService.getEmployeesByIdsString(wfTrainingCourseEventsList.get(0).getInternalCopies());
		externalCopies = wfTrainingCourseEventsList.get(0).getExternalCopies();

		for (int i = 0; i < wfTrainingCourseEventsList.size(); i++) {
		    if (wfTrainingCourseEventsList.get(i).getTrainingTransactionId() == null) {
			wfTrainingCourseEventsForExternalEmpsList.add(wfTrainingCourseEventsList.get(i));
			wfTrainingCourseEventsList.remove(wfTrainingCourseEventsList.get(i));
			i--;
		    }
		}
		calculateWarningMessage();
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void selectCourseEvent() {
	try {
	    selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
	    List<TrainingTransactionData> acceptedNominatedEmployeesTransactionsList = TrainingEmployeesService.getJoinedAndAcceptedEmployeesInCourseEventTransactionsList(selectedCourseEventId);

	    wfTrainingCourseEventsList = new ArrayList<>();
	    for (TrainingTransactionData trainingTransaction : acceptedNominatedEmployeesTransactionsList)
		wfTrainingCourseEventsList.add(TrainingCoursesEventsWorkFlow.constructWFTrainingCourseEventData(selectedCourseEvent, trainingTransaction, null));

	    List<TrainingCourseEventDecisionEmployee> externalEmployees = TrainingCoursesEventsService.getLastValidTrnCourseEventDecisionExternalEmployees(selectedCourseEvent.getId());
	    wfTrainingCourseEventsForExternalEmpsList = new ArrayList<>();
	    for (TrainingCourseEventDecisionEmployee externalEmployee : externalEmployees) {
		constructWFTrainingCourseForExternalEmp(externalEmployee);
	    }
	    calculateWarningMessage();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void constructWFTrainingCourseForExternalEmp() {
	try {
	    wfTrainingCourseEventsForExternalEmpsList.add(TrainingCoursesEventsWorkFlow.constructWFTrainingCourseEventData(selectedCourseEvent, null, null));

	    calculateWarningMessage();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void constructWFTrainingCourseForExternalEmp(TrainingCourseEventDecisionEmployee externalEmployee) {
	try {
	    WFTrainingCourseEventData wfTrainingCourseEvent = TrainingCoursesEventsWorkFlow.constructWFTrainingCourseEventData(selectedCourseEvent, null, externalEmployee);
	    wfTrainingCourseEventsForExternalEmpsList.add(wfTrainingCourseEvent);

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteWFTrainingCourseFromExternalEmpsList(WFTrainingCourseEventData wfTrainingCourseEvent) {
	try {
	    if (wfTrainingCourseEventsForExternalEmpsList.size() + wfTrainingCourseEventsList.size() == 1)
		throw new BusinessException("error_mustHaveAtLeastOnlyOne");

	    if (wfTrainingCourseEvent.getInstanceId() != null) {
		TrainingCoursesEventsWorkFlow.deleteWFTrainingCourseEvents(wfTrainingCourseEvent);
	    }
	    wfTrainingCourseEventsForExternalEmpsList.remove(wfTrainingCourseEvent);

	    calculateWarningMessage();
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void calculateWarningMessage() {
	int totalNominatedEmps = wfTrainingCourseEventsList.size() + wfTrainingCourseEventsForExternalEmpsList.size();

	if (selectedCourseEvent != null && totalNominatedEmps < selectedCourseEvent.getCandidatesMin()) {
	    warningMessage = getParameterizedMessage("error_candidatesLessThanMin", new Object[] { totalNominatedEmps, selectedCourseEvent.getCandidatesMin() });
	} else if (selectedCourseEvent != null && totalNominatedEmps > selectedCourseEvent.getCandidatesMax()) {
	    warningMessage = getParameterizedMessage("error_candidatesGreaterThanMax", new Object[] { totalNominatedEmps, selectedCourseEvent.getCandidatesMax() });
	} else {
	    warningMessage = "";
	}
    }

    public void printTrainingCourseEventDecisionBytes() {
	try {
	    String decisionDate = HijriDateService.getHijriDateString(prevTasks.get(prevTasks.size() - 1).getHijriActionDate());
	    long transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_MODIFY_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	    WFTrainingCourseEventData wfTrainingCourseEventData = wfTrainingCourseEventsList.isEmpty() ? wfTrainingCourseEventsForExternalEmpsList.get(0) : wfTrainingCourseEventsList.get(0);
	    byte[] bytes = TrainingCoursesEventsService.getTrainingCourseEventDecisionBytes(TrainingCoursesEventsService.getTrainingCourseEventDecision(wfTrainingCourseEventData.getCourseEventId(), transactionTypeId, null, null, decisionDate).getId(), transactionTypeId);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public Integer getCandidatesCount() {
	if (wfTrainingCourseEventsList.size() + wfTrainingCourseEventsForExternalEmpsList.size() == 0)
	    return null;

	return wfTrainingCourseEventsList.size() + wfTrainingCourseEventsForExternalEmpsList.size();
    }

    public List<Rank> getRanksList() {
	return ranksList;
    }

    public String getWarningMessage() {
	return warningMessage;
    }

    public void setWarningMessage(String warningMessage) {
	this.warningMessage = warningMessage;
    }
}
