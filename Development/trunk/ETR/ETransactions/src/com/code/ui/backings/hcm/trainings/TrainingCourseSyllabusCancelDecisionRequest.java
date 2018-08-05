package com.code.ui.backings.hcm.trainings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseDecision;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingCourseData;
import com.code.enums.NavigationEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.TrainingCoursesWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "trainingCourseSyllabusCancelDecisionRequest")
@ViewScoped
public class TrainingCourseSyllabusCancelDecisionRequest extends WFBaseBacking {

    private WFTrainingCourseData wfTrainingCourse;
    private String selectedCourseName = "";

    public TrainingCourseSyllabusCancelDecisionRequest() throws BusinessException {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_trainingCourseSyllabusCancelDecisionRequest"));
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfTrainingCourse = new WFTrainingCourseData();
		this.processId = WFProcessesEnum.MILITARY_TRAINING_COURSE_SYLLABUS_CANCEL.getCode();
	    } else {
		wfTrainingCourse = TrainingCoursesWorkFlow.getWFTrainingCourseDataByInstanceId(this.instance.getInstanceId());
		selectedCourseName = wfTrainingCourse.getCourseName();
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), new Object[] {}));
	}
    }

    public String initWFTrainingCourse() {
	try {
	    TrainingCoursesWorkFlow.initWFTrainingCourseSyllabusAttachments(requester, wfTrainingCourse, null, this.attachments, this.processId, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingCourseCS() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCS(requester, instance, wfTrainingCourse, null, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingCourseCS() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCS(requester, instance, wfTrainingCourse, null, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingCourseCP() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCP(requester, instance, wfTrainingCourse, null, currentTask, WFActionFlagsEnum.APPROVE.getCode(), null);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingCourseCP() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCP(requester, instance, wfTrainingCourse, null, currentTask, WFActionFlagsEnum.REJECT.getCode(), null);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnWFTrainingCourseCP() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCP(requester, instance, wfTrainingCourse, null, currentTask, WFActionFlagsEnum.RETURN_TO_COMMITTEE_SECRETARY.getCode(), null);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingCourseSM() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsSM(requester, instance, wfTrainingCourse, null, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingCourseSM() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsSM(requester, instance, wfTrainingCourse, null, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnWFTrainingCourseSM() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsSM(requester, instance, wfTrainingCourse, null, currentTask, WFActionFlagsEnum.RETURN_TO_COMMITTEE_SECRETARY.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String closeProcess() {
	try {
	    TrainingCoursesWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public void print() {
	try {
	    TrainingCourseDecision decision = TrainingCoursesService.getTrainingCoursesDecisions(wfTrainingCourse.getCourseId(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_CANCEL_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId(), null, null, prevTasks.get(prevTasks.size() - 1).getHijriActionDate()).get(0);
	    byte[] bytes = null;
	    bytes = TrainingCoursesService.getTrainingCourseDecisionSyllabusBytes(decision.getId(), decision.getTransactionTypeId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), new Object[] {}));
	}
    }

    public WFTrainingCourseData getWfTrainingCourse() {
	return wfTrainingCourse;
    }

    public void setWfTrainingCourse(WFTrainingCourseData wfTrainingCourse) {
	this.wfTrainingCourse = wfTrainingCourse;
    }

    public String getSelectedCourseName() {
	return selectedCourseName;
    }

    public void setSelectedCourseName(String selectedCourseName) {
	this.selectedCourseName = selectedCourseName;
    }

}
