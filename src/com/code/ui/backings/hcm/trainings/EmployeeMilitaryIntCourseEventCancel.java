package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.enums.FlagsEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "employeeMilitaryIntCourseEventCancel")
@ViewScoped
public class EmployeeMilitaryIntCourseEventCancel extends WFBaseBacking {
    private WFTrainingData wfTraining;
    private TrainingCourseEventData selectedCourseEvent;
    private List<TrainingCourseEventData> courseEvents;
    private List<EmployeeData> internalCopies;
    String errorMessage;
    boolean hasPrivilege = true;
    long trainingTypeId;
    Integer[] statusIds;

    public EmployeeMilitaryIntCourseEventCancel() {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_employeeMilitaryIntCourseEventCancel"));
	    this.processId = WFProcessesEnum.EMPLOYEE_MILITARY_INTERNAL_COURSE_EVENT_CANCEL.getCode();
	    trainingTypeId = TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode();
	    statusIds = new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.JOINED.getCode() };

	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		TrainingUnitData trainingUnit = TrainingSetupService.getTrainingUnitByPhysicalUnitHKey(this.loginEmpData.getPhysicalUnitHKey());
		if (trainingUnit == null) {
		    errorMessage = getMessage("label_trnTrainingUnitSpecialistPrivilege");
		    hasPrivilege = false;
		}

		wfTraining = new WFTrainingData();
		internalCopies = new ArrayList<EmployeeData>();
	    } else {
		wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
		internalCopies = EmployeesService.getEmployeesByIdsString(wfTraining.getInternalCopies());
		selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(wfTraining.getCourseEventId());
		courseEvents = Arrays.asList(selectedCourseEvent);
		beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
	    }

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void constructWFTraining() {
	try {
	    wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(
		    wfTraining.getEmployeeId(),
		    wfTraining.getCategoryId(),
		    trainingTypeId, processId);
	    beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
	    courseEvents = TrainingCoursesEventsService.getCoursesEventsDataForNomination(beneficiary.getEmpId(), trainingTypeId, statusIds, loginEmpData.getPhysicalRegionId());
	    wfTraining.setCourseEventId((long) FlagsEnum.ALL.getCode());
	    if (courseEvents.isEmpty())
		throw new BusinessException("error_noCoursesForEmployee");
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectCourse() {
	try {
	    selectedCourseEvent = wfTraining.getCourseEventId() == FlagsEnum.ALL.getCode() ? null : TrainingCoursesEventsService.getCourseEventById(wfTraining.getCourseEventId());
	    loadNominationAttachments();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void loadNominationAttachments() throws BusinessException {
	if (selectedCourseEvent != null) {
	    TrainingTransactionData transactionData = TrainingEmployeesService.getTrainingTransactionsData(new Long[] { trainingTypeId }, statusIds, FlagsEnum.ALL.getCode(), wfTraining.getEmployeeId(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), wfTraining.getCourseEventId()).get(0);
	    wfTraining.setAttachments(transactionData.getAttachments());
	} else
	    wfTraining.setAttachments(null);
    }

    public void print() {
	try {
	    long trainingTransactionId = TrainingEmployeesService.getTrainingTransactionsData(
		    new Long[] { TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() },
		    new Integer[] { TraineeStatusEnum.CANCELLED.getCode() },
		    FlagsEnum.ALL.getCode(), beneficiary.getEmpId(), FlagsEnum.ALL.getCode(),
		    FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(),
		    selectedCourseEvent.getId()).get(0).getId();
	    long trainingTransactionDetailId = TrainingEmployeesService.getTrainingTransactionDetailDataByTrainingTransactionId(trainingTransactionId).get(0).getId();
	    byte[] bytes = TrainingEmployeesService.getTraineeCourseCancellationDecisionBytes(trainingTransactionDetailId);
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<TrainingCourseEventData> getCourseEvents() {
	return courseEvents;
    }

    public void setCourseEvents(List<TrainingCourseEventData> courseEvents) {
	this.courseEvents = courseEvents;
    }

    public WFTrainingData getWfTraining() {
	return wfTraining;
    }

    public void setWfTraining(WFTrainingData wfTraining) {
	this.wfTraining = wfTraining;
    }

    public TrainingCourseEventData getSelectedCourseEvent() {
	return selectedCourseEvent;
    }

    public void setSelectedCourseEvent(TrainingCourseEventData selectedCourseEvent) {
	this.selectedCourseEvent = selectedCourseEvent;
    }

    public String initEmployeeCourseEventCancel() {
	try {
	    String internalCopiesTemp = EmployeesService.getEmployeesIdsString(internalCopies);
	    wfTraining.setInternalCopies(internalCopiesTemp);
	    TrainingEmployeesWorkFlow.initEmployeeCourseEventCancel(requester, wfTraining, selectedCourseEvent, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveEmployeeCourseEventCancelSM() {
	try {

	    TrainingEmployeesWorkFlow.doEmployeeCourseEventCancelSM(requester, instance, wfTraining, selectedCourseEvent, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnEmployeeCourseEventCancelSM() {
	try {

	    TrainingEmployeesWorkFlow.doEmployeeCourseEventCancelSM(requester, instance, wfTraining, selectedCourseEvent, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectEmployeeCourseEventCancelSM() {
	try {

	    TrainingEmployeesWorkFlow.doEmployeeCourseEventCancelSM(requester, instance, wfTraining, selectedCourseEvent, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveEmployeeCourseEventCancelRE() {
	try {
	    String internalCopiesTemp = EmployeesService.getEmployeesIdsString(internalCopies);
	    wfTraining.setInternalCopies(internalCopiesTemp);
	    TrainingEmployeesWorkFlow.doEmployeeCourseEventCancelRE(requester, instance, wfTraining, selectedCourseEvent, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectEmployeeCourseEventCancelRE() {
	try {
	    TrainingEmployeesWorkFlow.doEmployeeCourseEventCancelRE(requester, instance, wfTraining, selectedCourseEvent, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String closeProcess() {
	try {
	    TrainingEmployeesWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

    public boolean isHasPrivilege() {
	return hasPrivilege;
    }

    public void setHasPrivilege(boolean hasPrivilege) {
	this.hasPrivilege = hasPrivilege;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }

}