package com.code.ui.backings.hcm.trainings;

import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.security.SecurityService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@ManagedBean(name = "militaryTrainingApologyRequest")
@ViewScoped
public class MilitaryTrainingApologyRequest extends TrainingAndScholarshipBase {

    private List<TrainingCourseEventData> courseEvents;
    private boolean regionAdmin = false;
    long trainingTypeId;
    Integer[] statusIds;

    public MilitaryTrainingApologyRequest() {
	try {
	    super.init();
	    if (getRequest().getParameter("mode") != null)
		mode = Integer.parseInt(getRequest().getParameter("mode"));
	    if (mode == 1) {// EXTERNAL
		setScreenTitle(getMessage("title_externalMilitaryTrainingApologyRequest"));
		this.processId = WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_APOLOGY_REQUEST.getCode();
		trainingTypeId = TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode();
		statusIds = new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode(), TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.JOINED.getCode() };
	    }

	    else {// INTERNAL
		setScreenTitle(getMessage("title_internalMilitaryTrainingApologyRequest"));
		this.processId = WFProcessesEnum.MILITARY_INTERNAL_TRAINING_APOLOGY_REQUEST.getCode();
		trainingTypeId = TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode();
		statusIds = new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.JOINED.getCode() };
	    }
	    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {

		constructWFTraining();
		if (mode == 1) {
		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_EXTERNAL_MILITARY_APOLOGY_REQUEST.getCode(), MenuActionsEnum.TRN_MILITARY_REQUEST_REGION_ADMIN.getCode()))
			regionAdmin = true;
		} else {
		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_INTERNAL_MILITARY_APOLOGY_REQUEST.getCode(), MenuActionsEnum.TRN_MILITARY_REQUEST_REGION_ADMIN.getCode()))
			regionAdmin = true;
		}

	    } else {
		// getwfTraining by instanceId
		wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
		this.processId = this.instance.getProcessId();
		selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(wfTraining.getCourseEventId());
		courseEvents = Arrays.asList(selectedCourseEvent);
	    }

	    if (requester.getEmpId().equals(wfTraining.getEmployeeId()))
		beneficiary = requester;
	    else
		beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());

	    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.EXTRA_SECONDARY_MANAGER_REDIRECT.getCode())) {
		selectedReviewerEmpId = 0L;
		reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void constructWFTraining() {
	try {
	    wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(
		    (wfTraining == null || wfTraining.getEmployeeId() == null) ? this.loginEmpData.getEmpId() : wfTraining.getEmployeeId(),
		    (wfTraining == null || wfTraining.getCategoryId() == null) ? this.loginEmpData.getCategoryId() : wfTraining.getCategoryId(),
		    trainingTypeId, processId);
	    if (wfTraining.getCategoryId() != CategoriesEnum.OFFICERS.getCode() && wfTraining.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()) {
		// Rollback ajax update
		wfTraining.setEmployeeId(beneficiary.getEmpId());
		wfTraining.setCategoryId(beneficiary.getCategoryId());
		this.setServerSideErrorMessages(getParameterizedMessage("error_militaryTrainingNotAllowedForCivilians", new Object[] { "" }));
		return;
	    }
	    beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
	    courseEvents = TrainingCoursesEventsService.getCoursesEventsDataForNomination(beneficiary.getEmpId(), trainingTypeId, statusIds, FlagsEnum.ALL.getCode());
	    wfTraining.setCourseEventId(-1l);
	    selectedCourseEvent = null;
	    if (courseEvents.isEmpty())
		throw new BusinessException("error_noEmployeeNominationForCourseEvent");
	    if (courseEvents.size() == 1) {
		wfTraining.setCourseEventId(courseEvents.get(0).getId());
		selectedCourseEvent = courseEvents.get(0);
	    }
	    loadNominationAttachments();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectCourse() {
	try {
	    selectedCourseEvent = wfTraining.getCourseEventId() == -1 ? null : TrainingCoursesEventsService.getCourseEventById(wfTraining.getCourseEventId());
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

    public List<TrainingCourseEventData> getCourseEvents() {
	return courseEvents;
    }

    public void setCourseEvents(List<TrainingCourseEventData> courseEvents) {
	this.courseEvents = courseEvents;
    }

    public boolean isRegionAdmin() {
	return regionAdmin;
    }

    public void setRegionAdmin(boolean regionAdmin) {
	this.regionAdmin = regionAdmin;
    }

}