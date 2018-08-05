package com.code.ui.backings.hcm.trainings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.workflow.hcm.trainings.WFTrainingCourseData;
import com.code.enums.MilitaryCivillianEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;
import com.code.services.workflow.hcm.TrainingCoursesWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "addTrainingCourseNameRequest")
@ViewScoped
public class AddTrainingCourseNameRequest extends WFBaseBacking {

    private WFTrainingCourseData wfTrainingCourseName;
    private boolean modifyAdmin = false;

    public AddTrainingCourseNameRequest() throws BusinessException {
	try {
	    super.init();
	    wfTrainingCourseName = new WFTrainingCourseData();
	    if (getRequest().getParameter("mode") != null) {
		wfTrainingCourseName.setCourseType(Integer.parseInt(getRequest().getParameter("mode")));
		if (wfTrainingCourseName.getCourseType() == MilitaryCivillianEnum.Civillian.getCode()) {
		    setScreenTitle(getMessage("title_addCivillianCourseNameRequest"));
		    this.processId = WFProcessesEnum.ADD_CIVILLAIN_TRAINING_COURSE_NAME_REQUEST.getCode();
		} else {
		    setScreenTitle(getMessage("title_addMilitaryCourseNameRequest"));
		    this.processId = WFProcessesEnum.ADD_MILITARY_TRAINING_COURSE_NAME_REQUEST.getCode();
		}
	    }

	    if (!this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfTrainingCourseName = TrainingCoursesWorkFlow.getWFTrainingCourseDataByInstanceId(this.instance.getInstanceId());
		attachments = this.instance.getAttachments();
		modifyAdmin = TrainingCoursesEventsWorkFlow.isEmployeeAuthorizedForPosition(WFPositionsEnum.PROGRAMS_CURRICULUM_UNIT_MANAGER.getCode(), this.loginEmpData.getPhysicalUnitId(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), new Object[] {}));
	}
    }

    public String initWFTrainingCourseName() {
	try {
	    TrainingCoursesWorkFlow.initWFTrainingCoursesNames(requester, wfTrainingCourseName, this.attachments, this.processId, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingCourseNameSM() {
	try {

	    TrainingCoursesWorkFlow.doWFTrainingCoursesNamesSM(requester, instance, wfTrainingCourseName, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingCourseNameSM() {
	try {
	    TrainingCoursesWorkFlow.doWFTrainingCoursesNamesSM(requester, instance, wfTrainingCourseName, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveWFTrainingCourseNameSSM() {
	try {
	    TrainingCoursesWorkFlow.doWFTrainingCoursesNamesSSM(requester, instance, wfTrainingCourseName, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectWFTrainingCourseNameSSM() {
	try {
	    TrainingCoursesWorkFlow.doWFTrainingCoursesNamesSSM(requester, instance, wfTrainingCourseName, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String closeProcess() {
	try {
	    TrainingCoursesEventsWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public boolean isModifyAdmin() {
	return modifyAdmin;
    }

    public void setModifyAdmin(boolean modifyAdmin) {
	this.modifyAdmin = modifyAdmin;
    }

    public WFTrainingCourseData getWfTrainingCourseName() {
	return wfTrainingCourseName;
    }

    public void setWfTrainingCourseName(WFTrainingCourseData wfTrainingCourseName) {
	this.wfTrainingCourseName = wfTrainingCourseName;
    }

}
