package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.UnitsAncestorsLevelsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.UnitsService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@ManagedBean(name = "morningTrainingRequest")
@ViewScoped
public class MorningTrainingRequest extends TrainingAndScholarshipBase {

    private long selectedEmployeeId;
    private long selectedEmpCategoryId;

    private long selectedCourseEventId = FlagsEnum.ALL.getCode();;

    private String reasons;
    private String attachments;

    private String privilegeUnitHkey;

    boolean modify = false;

    public MorningTrainingRequest() {

	try {
	    super.init();
	    setScreenTitle(getMessage("title_morningTrainingRequest"));
	    privilegeUnitHkey = UnitsService.getAncestorUnderPresidencyByLevel(requester.getPhysicalUnitId(), requester.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode() : UnitsAncestorsLevelsEnum.LEVEL_THREE.getCode()).gethKey();
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfTrainingList = new ArrayList<>();
		this.processId = WFProcessesEnum.MORNING_TRAINING_REQUEST.getCode();
		selectedCourseEvent = new TrainingCourseEventData();
		// TODO add it to the requester if not officer then error
	    } else {

		wfTrainingList = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId());
		this.processId = this.instance.getProcessId();

		selectedCourseEventId = wfTrainingList.get(0).getCourseEventId();
		selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(wfTrainingList.get(0).getCourseEventId());

		reasons = wfTrainingList.get(0).getReasons();
		attachments = wfTrainingList.get(0).getAttachments();

	    }
	    modify = this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode()) || this.getRole().equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()) || this.getRole().equals(WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode());
	    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode())) {
		selectedReviewerEmpId = 0L;
		reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void selectCourseEvent() {
	try {
	    selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void constructWFTraining() {
	try {
	    for (WFTrainingData w : wfTrainingList) {
		if (w.getEmployeeId() == selectedEmployeeId) {
		    throw new BusinessException("error_empDuplicateSameProcess");
		}
	    }

	    WFTrainingData wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(selectedEmployeeId, selectedEmpCategoryId, TrainingTypesEnum.MORNING_COURSE.getCode(), processId);

	    wfTrainingList.add(wfTraining);

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String initWorkFlow() {
	for (WFTrainingData wfTraining : wfTrainingList) {
	    wfTraining.setCourseEventId(selectedCourseEvent.getId());
	    wfTraining.setReasons(reasons);
	    wfTraining.setAttachments(attachments);
	}
	return initNomination();
    }

    public String reviewWorkFlowRE() {
	for (WFTrainingData wfTraining : wfTrainingList) {
	    wfTraining.setCourseEventId(selectedCourseEvent.getId());
	    wfTraining.setReasons(reasons);
	    wfTraining.setAttachments(attachments);
	}
	return approveNominationRE();
    }

    public String reviewWorkFlowSRE() {
	for (WFTrainingData wfTraining : wfTrainingList) {
	    wfTraining.setCourseEventId(selectedCourseEvent.getId());
	    wfTraining.setReasons(reasons);
	    wfTraining.setAttachments(attachments);
	}
	return approveNominationSRE();
    }

    public void deleteWFTraining(WFTrainingData wfTraining) {
	try {
	    if (wfTraining.getInstanceId() != null)
		TrainingEmployeesWorkFlow.deleteWFTraining(wfTraining);
	    wfTrainingList.remove(wfTraining);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public long getSelectedEmployeeId() {
	return selectedEmployeeId;
    }

    public void setSelectedEmployeeId(long selectedEmployeeId) {
	this.selectedEmployeeId = selectedEmployeeId;
    }

    public long getSelectedEmpCategoryId() {
	return selectedEmpCategoryId;
    }

    public void setSelectedEmpCategoryId(long selectedEmpCategoryId) {
	this.selectedEmpCategoryId = selectedEmpCategoryId;
    }

    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    public boolean isModify() {
	return modify;
    }

    public String getPrivilegeUnitHkey() {
	return privilegeUnitHkey;
    }

    public void setPrivilegeUnitHkey(String privilegeUnitHkey) {
	this.privilegeUnitHkey = privilegeUnitHkey;
    }

    public long getSelectedCourseEventId() {
	return selectedCourseEventId;
    }

    public void setSelectedCourseEventId(long selectedCourseEventId) {
	this.selectedCourseEventId = selectedCourseEventId;
    }

}
