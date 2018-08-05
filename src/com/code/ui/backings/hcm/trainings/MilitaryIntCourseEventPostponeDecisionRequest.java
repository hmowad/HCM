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
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;

@ManagedBean(name = "militaryIntCourseEventPostponeDecisionRequest")
@ViewScoped
public class MilitaryIntCourseEventPostponeDecisionRequest extends TrainingCoursesBase {

    public MilitaryIntCourseEventPostponeDecisionRequest() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_militaryIntCourseEventPostponeDecisionRequest"));
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) { // Requester
		internalCopies = new ArrayList<EmployeeData>();
		this.processId = WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_POSTPONE_REQUEST.getCode();
		wfTrainingCourseEvent = new WFTrainingCourseEventData();
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

    public void calculateNewEndDate() throws BusinessException {
	if (this.selectedCourseEvent != null)
	    this.wfTrainingCourseEvent.setNewEndDateString(TrainingCoursesEventsService.manipulateDateNeglectWeekend(HijriDateService.addSubStringHijriDays(this.wfTrainingCourseEvent.getNewStartDateString(), HijriDateService.hijriDateDiff(this.selectedCourseEvent.getActualStartDate(), this.selectedCourseEvent.getActualEndDate()))));
    }

    public void selectCourseEvent() throws BusinessException {
	selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(wfTrainingCourseEvent.getCourseEventId());
	wfTrainingCourseEvent.setNewStartDate(this.selectedCourseEvent.getActualStartDate());
	calculateNewEndDate();
    }

    public void printTrainingCourseEventPostponeDecisionBytes() {
	try {
	    long transactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_COURSE_EVENT_POSTPONE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId();
	    byte[] bytes = TrainingCoursesEventsService.getTrainingCourseEventDecisionBytes(TrainingCoursesEventsService.getTrainingCourseEventDecision(wfTrainingCourseEvent.getCourseEventId(), transactionTypeId, null, wfTrainingCourseEvent.getNewStartDateString(), null).getId(), transactionTypeId);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }
}
