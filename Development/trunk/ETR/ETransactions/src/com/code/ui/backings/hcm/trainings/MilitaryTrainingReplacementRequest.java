package com.code.ui.backings.hcm.trainings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeQualificationsData;
import com.code.dal.orm.hcm.trainings.TrainingCourseEventData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TraineeStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.UnitsAncestorsLevelsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;

@ManagedBean(name = "militaryTrainingReplacementRequest")
@ViewScoped
public class MilitaryTrainingReplacementRequest extends TrainingAndScholarshipBase {

    private List<TrainingCourseEventData> coursesEvents;
    private EmployeeData selectedReplacementEmp;
    private List<TrainingTransactionData> employeeNominatedOrJoinedTransactionsList;
    private List<TrainingTransactionData> employeeFinishedTransactionsList;
    private EmployeeQualificationsData employeeQualificationsData;
    private String[] trainingPurposesArray;
    private String privilegeUnitHkey;

    private boolean regionAdmin = false;

    public MilitaryTrainingReplacementRequest() {
	try {
	    super.init();
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 0: // internal replacement
		    setScreenTitle(getMessage("title_internalMilitaryTrainingReplacementRequest"));
		    this.processId = WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode();

		    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
			if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_INTERNAL_MILITARY_REPLACEMENT_REQUEST.getCode(), MenuActionsEnum.TRN_MILITARY_REQUEST_REGION_ADMIN.getCode()))
			    regionAdmin = true;

			privilegeUnitHkey = UnitsService.getAncestorUnderPresidencyByLevel(requester.getPhysicalUnitId(), requester.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode() : UnitsAncestorsLevelsEnum.LEVEL_THREE.getCode()).gethKey();

			wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(this.loginEmpData.getEmpId(), this.loginEmpData.getCategoryId(), TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode(), processId);
			beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
			getCourses();
		    } else {
			wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
			this.processId = this.instance.getProcessId();
			selectReplacementEmp();
			selectTrainingCourse();

		    }
		    break;
		case 1:// external replacement
		    setScreenTitle(getMessage("title_externalMilitaryTrainingReplacementRequest"));
		    this.processId = WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REPLACEMENT_REQUEST.getCode();
		    if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {
			if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.TRN_EXTERNAL_MILITARY_REPLACEMENT_REQUEST.getCode(), MenuActionsEnum.TRN_MILITARY_REQUEST_REGION_ADMIN.getCode()))
			    regionAdmin = true;

			privilegeUnitHkey = UnitsService.getAncestorUnderPresidencyByLevel(requester.getPhysicalUnitId(), requester.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode() : UnitsAncestorsLevelsEnum.LEVEL_THREE.getCode()).gethKey();

			wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(this.loginEmpData.getEmpId(), this.loginEmpData.getCategoryId(), TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode(), processId);
			beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
			getCourses();
		    } else {
			wfTraining = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId()).get(0);
			this.processId = this.instance.getProcessId();
			selectReplacementEmp();
			selectTrainingCourse();

		    }
		    break;
		default:

		}
		if (requester.getEmpId().equals(wfTraining.getEmployeeId()))
		    beneficiary = requester;
		else
		    beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());

		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.EXTRA_SECONDARY_MANAGER_REDIRECT.getCode())) {
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		}

	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void constructWFTraining() {
	try {
	    if (wfTraining.getCategoryId() != CategoriesEnum.OFFICERS.getCode() && wfTraining.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()) {
		// Rollback ajax update
		wfTraining.setEmployeeId(beneficiary.getEmpId());
		wfTraining.setCategoryId(beneficiary.getCategoryId());
		this.setServerSideErrorMessages(getParameterizedMessage("error_militaryTrainingNotAllowedForCivilians", new Object[] { "" }));
		return;
	    }
	    wfTraining = TrainingEmployeesWorkFlow.constructWFTraining(wfTraining.getEmployeeId(), wfTraining.getCategoryId(), wfTraining.getTrainingTypeId(), processId);
	    beneficiary = EmployeesService.getEmployeeData(wfTraining.getEmployeeId());
	    getCourses();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getCourses() throws BusinessException {
	Integer[] statusIds;
	long trainingTypeId;
	if (mode == 0) {
	    trainingTypeId = TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode();
	    statusIds = new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.JOINED.getCode() };
	} else {
	    trainingTypeId = TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode();
	    statusIds = new Integer[] { TraineeStatusEnum.NOMINATION_ACCEPTED_FROM_FRONTIER_GAURDS.getCode(), TraineeStatusEnum.NOMINATION_ACCEPTED.getCode(), TraineeStatusEnum.JOINED.getCode() };
	}
	coursesEvents = TrainingCoursesEventsService.getCoursesEventsDataForNomination(this.beneficiary == null ? this.loginEmpData.getEmpId() : this.beneficiary.getEmpId(), trainingTypeId, statusIds, FlagsEnum.ALL.getCode());
	if (coursesEvents.isEmpty())
	    throw new BusinessException("error_noEmployeeNominationForCourseEvent");
    }

    public void selectReplacementEmp() throws BusinessException {
	selectedReplacementEmp = EmployeesService.getEmployeeData(wfTraining.getReplacementEmployeeId());
	if (selectedReplacementEmp.getCategoryId() != CategoriesEnum.OFFICERS.getCode() && selectedReplacementEmp.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()) {
	    this.setServerSideErrorMessages(getParameterizedMessage("error_militaryTrainingNotAllowedForCivilians", new Object[] { "" }));
	    wfTraining.setReplacementEmployeeId(null);
	    selectedReplacementEmp = null;
	    return;
	}
	employeeQualificationsData = EmployeesService.getEmployeeQualificationsByEmpId(wfTraining.getReplacementEmployeeId());
	employeeNominatedOrJoinedTransactionsList = TrainingEmployeesService.getJoinedOrNominatedTransactionsCoursesForEmployee(wfTraining.getReplacementEmployeeId());
	employeeFinishedTransactionsList = TrainingEmployeesService.getFinishedMilitaryTransactionsForEmployee(wfTraining.getReplacementEmployeeId());
    }

    public void selectTrainingCourse() throws BusinessException {
	selectedCourseEvent = (wfTraining.getCourseEventId() == null || wfTraining.getCourseEventId() == 0) ? null : TrainingCoursesEventsService.getCourseEventById(wfTraining.getCourseEventId());
    }

    public List<TrainingCourseEventData> getCoursesEvents() {
	return coursesEvents;
    }

    public void setCoursesEvents(List<TrainingCourseEventData> coursesEvents) {
	this.coursesEvents = coursesEvents;
    }

    public EmployeeData getSelectedReplacementEmp() {
	return selectedReplacementEmp;
    }

    public void setSelectedReplacementEmp(EmployeeData selectedReplacementEmp) {
	this.selectedReplacementEmp = selectedReplacementEmp;
    }

    public EmployeeQualificationsData getEmployeeQualificationsData() {
	return employeeQualificationsData;
    }

    public void setEmployeeQualificationsData(EmployeeQualificationsData employeeQualificationsData) {
	this.employeeQualificationsData = employeeQualificationsData;
    }

    public boolean isRegionAdmin() {
	return regionAdmin;
    }

    public void setRegionAdmin(boolean regionAdmin) {
	this.regionAdmin = regionAdmin;
    }

    public String[] getTrainingPurposesArray() {
	trainingPurposesArray = wfTraining.getTrainingPurpose() == null ? "".split(",") : wfTraining.getTrainingPurpose().split(",");
	return trainingPurposesArray;
    }

    public void setTrainingPurposesArray(String[] trainingPurposesArray) {
	this.trainingPurposesArray = trainingPurposesArray;
	if (trainingPurposesArray == null || trainingPurposesArray.length == 0)
	    wfTraining.setTrainingPurpose(null);
	else {
	    StringBuilder trainingPurpose = new StringBuilder();
	    for (int i = 0; i < trainingPurposesArray.length; i++)
		trainingPurpose.append(trainingPurposesArray[i]).append(",");
	    if (trainingPurpose.length() != 0)
		trainingPurpose.deleteCharAt(trainingPurpose.length() - 1);
	    wfTraining.setTrainingPurpose(trainingPurpose.toString());
	}
    }

    public List<TrainingTransactionData> getEmployeeFinishedTransactionsList() {
	return employeeFinishedTransactionsList;
    }

    public void setEmployeeFinishedTransactionsList(List<TrainingTransactionData> employeeFinishedTransactionsList) {
	this.employeeFinishedTransactionsList = employeeFinishedTransactionsList;
    }

    public List<TrainingTransactionData> getEmployeeNominatedOrJoinedTransactionsList() {
	return employeeNominatedOrJoinedTransactionsList;
    }

    public void setEmployeeNominatedOrJoinedTransactionsList(List<TrainingTransactionData> employeeNominatedOrJoinedTransactionsList) {
	this.employeeNominatedOrJoinedTransactionsList = employeeNominatedOrJoinedTransactionsList;
    }

    public String getPrivilegeUnitHkey() {
	return privilegeUnitHkey;
    }
}
