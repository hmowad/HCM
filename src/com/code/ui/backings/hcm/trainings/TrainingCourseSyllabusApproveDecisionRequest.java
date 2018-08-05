package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.trainings.TrainingCourseDecision;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingCourseData;
import com.code.enums.FlagsEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesService;
import com.code.services.util.CommonService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.services.workflow.hcm.TrainingCoursesWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "trainingCourseSyllabusApproveDecisionRequest")
@ViewScoped
public class TrainingCourseSyllabusApproveDecisionRequest extends WFBaseBacking {

    private WFTrainingCourseData wfTrainingCourse;
    private String selectedCourseName = "";
    private long preparatorEmployeeId = FlagsEnum.ALL.getCode();
    private long selectedCommitteeEmployeeId = FlagsEnum.ALL.getCode();
    private List<EmployeeData> committeEmployeesList;

    public TrainingCourseSyllabusApproveDecisionRequest() throws BusinessException {
	try {
	    super.init();
	    setScreenTitle(getMessage("title_trainingCourseSyllabusApproveDecisionRequest"));
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfTrainingCourse = new WFTrainingCourseData();
		this.processId = WFProcessesEnum.MILITARY_TRAINING_COURSE_SYLLABUS_APPROVE.getCode();
	    } else {
		wfTrainingCourse = TrainingCoursesWorkFlow.getWFTrainingCourseDataByInstanceId(this.instance.getInstanceId());
		selectedCourseName = wfTrainingCourse.getCourseName();
		attachments = this.instance.getAttachments();

	    }
	    if (this.getRole().equals(WFTaskRolesEnum.COMMITTEE_PRESIDENT.getCode())) {
		committeEmployeesList = new ArrayList<EmployeeData>();
		committeEmployeesList.addAll(TrainingCoursesService.getTrainingCurriculumCommitteeMembers());
	    }
	    if (!this.getRole().equals(WFTaskRolesEnum.COMMITTEE_MEMBER.getCode()) && !this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		prevTasks = BaseWorkFlow.getWFInstanceCompletedTasksData(currentTask.getInstanceId(), currentTask.getTaskId(), FlagsEnum.ALL.getCode() + "");
	    }
	    if (this.getRole().equals(WFTaskRolesEnum.COMMITTEE_MEMBER.getCode())) {
		for (int i = 0; i < prevTasks.size(); i++) {
		    if (prevTasks.get(i).getAssigneeWfRole().equals(WFTaskRolesEnum.COMMITTEE_MEMBER.getCode()))
			prevTasks.remove(i--);
		}
	    }
	} catch (Exception e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), new Object[] {}));
	}
    }

    public void addCommitteeEmployee() {
	try {
	    for (EmployeeData emp : committeEmployeesList) {
		if (emp.getEmpId() == selectedCommitteeEmployeeId) {
		    throw new BusinessException("label_existingEmployee");
		}
	    }
	    committeEmployeesList.add(EmployeesService.getEmployeeData(selectedCommitteeEmployeeId));

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), new Object[] {}));
	}
    }

    public void deleteCommitteeEmployee(EmployeeData emp) {
	committeEmployeesList.remove(emp);
    }

    public String initWFTrainingCourse() {
	try {
	    TrainingCoursesWorkFlow.initWFTrainingCourseSyllabusAttachments(requester, wfTrainingCourse, preparatorEmployeeId, this.attachments, this.processId, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String prepareWFTrainingCourse() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsPreparation(requester, instance, wfTrainingCourse, attachments, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingCourseCS() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCS(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingCourseCS() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCS(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnWFTrainingCourseCS() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCS(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.RETURN_TO_PREPARATOR.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // COMMI_PRESIDENT("CommitteePresident"),
    public String approveWFTrainingCourseCP() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCP(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.APPROVE.getCode(), null);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingCourseCP() {
	try {

	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCP(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.REJECT.getCode(), null);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnWFTrainingCourseCP() {
	try {

	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCP(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.RETURN_TO_COMMITTEE_SECRETARY.getCode(), null);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String sendForAdviceWFTrainingCourseCP() {
	List<Long> empIdList = new ArrayList<Long>();
	for (EmployeeData emp : committeEmployeesList)
	    empIdList.add(emp.getEmpId());
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCP(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.SEND_FOR_ADVISE.getCode(), empIdList);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingCourseCM() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCM(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.RECOMMEND_APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnForReviewWFTrainingCourseCM() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsCM(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.RECOMMEND_REVIEW.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingCourseSM() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsSM(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingCourseSM() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsSM(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String returnWFTrainingCourseSM() {
	try {
	    TrainingCoursesWorkFlow.doWFCourseSyllabusAttachmentsSM(requester, instance, wfTrainingCourse, attachments, currentTask, WFActionFlagsEnum.RETURN_TO_COMMITTEE_PRESIDENT.getCode());
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
	    TrainingCourseDecision decision = TrainingCoursesService.getTrainingCoursesDecisions(wfTrainingCourse.getCourseId(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.TRN_SYLLABUS_ATTACHMENT_APPROVE_DECISION.getCode(), TransactionClassesEnum.TRAININGS.getCode()).getId(), null, null, prevTasks.get(prevTasks.size() - 1).getHijriActionDate()).get(0);
	    byte[] bytes = TrainingCoursesService.getTrainingCourseDecisionSyllabusBytes(decision.getId(), decision.getTransactionTypeId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
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

    public long getPreparatorEmployeeId() {
	return preparatorEmployeeId;
    }

    public void setPreparatorEmployeeId(long preparatorEmployeeId) {
	this.preparatorEmployeeId = preparatorEmployeeId;
    }

    public long getSelectedCommitteeEmployeeId() {
	return selectedCommitteeEmployeeId;
    }

    public void setSelectedCommitteeEmployeeId(long selectedCommitteeEmployeeId) {
	this.selectedCommitteeEmployeeId = selectedCommitteeEmployeeId;
    }

    public List<EmployeeData> getCommitteEmployeesList() {
	return committeEmployeesList;
    }

    public void setCommitteEmployeesList(List<EmployeeData> committeEmployeesList) {
	this.committeEmployeesList = committeEmployeesList;
    }

}
