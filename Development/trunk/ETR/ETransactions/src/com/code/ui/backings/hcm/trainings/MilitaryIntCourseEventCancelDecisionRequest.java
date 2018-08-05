package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingCourseEventData;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;

@ManagedBean(name = "militaryIntCourseEventCancelDecisionRequest")
@ViewScoped
public class MilitaryIntCourseEventCancelDecisionRequest extends TrainingCoursesBase {

    public MilitaryIntCourseEventCancelDecisionRequest() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_militaryIntCourseEventCancelDecisionRequest"));
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) { // Requester
		internalCopies = new ArrayList<EmployeeData>();
		wfTrainingCourseEvent = new WFTrainingCourseEventData();
		this.processId = WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_CANCEL_REQUEST.getCode();
	    } else {
		this.processId = this.instance.getProcessId();
		wfTrainingCourseEvent = TrainingCoursesEventsWorkFlow.getWFTrainingCourseEventsByInstanceId(this.instance.getInstanceId()).get(0);
		selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(wfTrainingCourseEvent.getCourseEventId());
		internalCopies = EmployeesService.getEmployeesByIdsString(wfTrainingCourseEvent.getInternalCopies());
		externalCopies = wfTrainingCourseEvent.getExternalCopies();
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public void selectCourseEvent() throws BusinessException {
	selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(wfTrainingCourseEvent.getCourseEventId());
    }

    public void printTrainingCourseEventCancelDecisionBytes() {
	try {
	    long transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_CANCEL_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	    byte[] bytes = TrainingCoursesEventsService.getTrainingCourseEventDecisionBytes(TrainingCoursesEventsService.getTrainingCourseEventDecision(wfTrainingCourseEvent.getCourseEventId(), transactionTypeId, null, null, null).getId(), transactionTypeId);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }
}
