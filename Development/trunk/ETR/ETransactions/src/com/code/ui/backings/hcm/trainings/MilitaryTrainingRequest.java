package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeQualificationsData;
import com.code.dal.orm.hcm.trainings.TrainingTransactionData;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingData;
import com.code.enums.CategoriesEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TrainingCourseEventStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.enums.TrainingYearStatusEnum;
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

@ManagedBean(name = "militaryTrainingRequest")
@ViewScoped
public class MilitaryTrainingRequest extends TrainingAndScholarshipBase {

    private EmployeeQualificationsData employeeQualificationsData;

    private List<TrainingTransactionData> employeeNominatedOrJoinedTransactionsList;
    private List<TrainingTransactionData> employeeFinishedTransactionsList;
    private WFTrainingData selectedWFTraining;
    private boolean regionAdmin;
    private String privilegeUnitHkey;
    private long selectedEmployeeId;
    private long selectedEmpCategoryId;
    private String[] trainingPurposesArray;
    private Long selectedCourseEventId;

    public MilitaryTrainingRequest() {

	try {
	    super.init();
	    wfTrainingList = new ArrayList<>();
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {

	    case 0: // Internal request
		setScreenTitle(getMessage("title_internalMilitaryNominationTraining"));

		if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {

		    this.processId = WFProcessesEnum.MILITARY_INTERNAL_TRAINING_REQUEST.getCode();
		    handleCourseEventParamater();
		} else {
		    wfTrainingList = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId());
		    this.processId = this.instance.getProcessId();
		    selectedCourseEventId = wfTrainingList.get(0).getCourseEventId();
		    selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
		}
		break;

	    case 1: // External request
		setScreenTitle(getMessage("title_externalMilitaryNominationTraining"));

		if (this.getRole().equals(WFTaskRolesEnum.REQUESTER.getCode())) {

		    this.processId = WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REQUEST.getCode();
		    handleCourseEventParamater();
		} else {
		    wfTrainingList = TrainingEmployeesWorkFlow.getWFTrainingDataByInstanceId(this.instance.getInstanceId());
		    this.processId = this.instance.getProcessId();
		    selectedCourseEventId = wfTrainingList.get(0).getCourseEventId();
		    selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
		}
		break;
	    default:
	    }
	    if (SecurityService.isEmployeeMenuActionGranted(requester.getEmpId(), processId == WFProcessesEnum.MILITARY_EXTERNAL_TRAINING_REQUEST.getCode() ? MenuCodesEnum.TRN_EXTERNAL_MILITARY_TRAINING_REQUEST.getCode() : MenuCodesEnum.TRN_INTERNAL_MILITARY_TRAINING_REQUEST.getCode(), MenuActionsEnum.TRN_MILITARY_REQUEST_REGION_ADMIN.getCode()))
		regionAdmin = true;
	    else
		privilegeUnitHkey = UnitsService.getAncestorUnderPresidencyByLevel(requester.getPhysicalUnitId(), requester.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode() : UnitsAncestorsLevelsEnum.LEVEL_THREE.getCode()).gethKey();

	    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.EXTRA_SECONDARY_MANAGER_REDIRECT.getCode())) {
		selectedReviewerEmpId = 0L;
		reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void handleCourseEventParamater() throws BusinessException {
	if (getRequest().getParameter("courseEventId") != null) {

	    selectedCourseEventId = Long.parseLong(getRequest().getParameter("courseEventId"));
	    selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
	    boolean nominationAllowed = true;
	    if (loginEmpData.getCategoryId() != CategoriesEnum.OFFICERS.getCode())
		nominationAllowed = false;
	    if (selectedCourseEvent == null || (selectedCourseEvent.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() && mode == 1) || (selectedCourseEvent.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode() && mode == 0))
		nominationAllowed = false;
	    if (selectedCourseEvent != null) {
		if (selectedCourseEvent.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() && selectedCourseEvent.getTrainingYearStatus() != TrainingYearStatusEnum.TRAINING_PLAN_APPROVED.getCode())
		    nominationAllowed = false;
		if (selectedCourseEvent.getTrainingTypeId() == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() && (selectedCourseEvent.getStatus() == TrainingCourseEventStatusEnum.COURSE_EVENT_CANCEL_DECISION_ISSUED.getCode() ||
			selectedCourseEvent.getStatus() == TrainingCourseEventStatusEnum.COURSE_EVENT_HELD.getCode()))
		    nominationAllowed = false;
		if (selectedCourseEvent.getTrainingTypeId() == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode() &&
			selectedCourseEvent.getStatus() != TrainingCourseEventStatusEnum.PLANNED_TO_BE_HELD.getCode())
		    nominationAllowed = false;
	    }
	    if (!nominationAllowed) {
		selectedCourseEventId = null;
		selectedCourseEvent = null;
	    }
	}
    }

    public void selectCourseEventHandling() {
	try {
	    selectedCourseEvent = TrainingCoursesEventsService.getCourseEventById(selectedCourseEventId);
	    for (WFTrainingData wfTraining : wfTrainingList)
		wfTraining.setCourseEventId(selectedCourseEventId);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void constructWFTraining() {
	try {
	    if (selectedEmpCategoryId != CategoriesEnum.OFFICERS.getCode() && selectedEmpCategoryId != CategoriesEnum.SOLDIERS.getCode()) {
		this.setServerSideErrorMessages(getParameterizedMessage("error_militaryTrainingNotAllowedForCivilians", new Object[] { "" }));
		return;
	    }

	    for (WFTrainingData w : wfTrainingList) {
		if (w.getEmployeeId() == selectedEmployeeId) {
		    throw new BusinessException("error_empDuplicateSameProcess");
		}
	    }

	    selectedWFTraining = TrainingEmployeesWorkFlow.constructWFTraining(selectedEmployeeId, selectedEmpCategoryId, mode == 0 ? TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode() : TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode(), processId);
	    selectedWFTraining.setCourseEventId(selectedCourseEventId);
	    wfTrainingList.add(selectedWFTraining);
	    trainingPurposesArray = selectedWFTraining.getTrainingPurpose() == null ? null : selectedWFTraining.getTrainingPurpose().split(",");
	    beneficiary = EmployeesService.getEmployeeData(selectedWFTraining.getEmployeeId());
	    employeeQualificationsData = EmployeesService.getEmployeeQualificationsByEmpId(selectedWFTraining.getEmployeeId());
	    employeeNominatedOrJoinedTransactionsList = TrainingEmployeesService.getJoinedOrNominatedTransactionsCoursesForEmployee(selectedWFTraining.getEmployeeId());
	    employeeFinishedTransactionsList = TrainingEmployeesService.getFinishedMilitaryTransactionsForEmployee(selectedWFTraining.getEmployeeId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectWFTraining(long employeeId) throws BusinessException {

	for (int i = 0; i < wfTrainingList.size(); i++) {
	    if (wfTrainingList.get(i).getEmployeeId() == employeeId) {
		selectedWFTraining = wfTrainingList.get(i);
		break;
	    }
	}

	beneficiary = EmployeesService.getEmployeeData(employeeId);

	trainingPurposesArray = selectedWFTraining.getTrainingPurpose() == null ? null : selectedWFTraining.getTrainingPurpose().split(",");
	employeeQualificationsData = EmployeesService.getEmployeeQualificationsByEmpId(selectedWFTraining.getEmployeeId());
	employeeNominatedOrJoinedTransactionsList = TrainingEmployeesService.getJoinedOrNominatedTransactionsCoursesForEmployee(selectedWFTraining.getEmployeeId());
	employeeFinishedTransactionsList = TrainingEmployeesService.getFinishedMilitaryTransactionsForEmployee(selectedWFTraining.getEmployeeId());
    }

    public void deleteWFTraining(WFTrainingData wfTrainingToBeDeleted) {
	try {
	    wfTrainingList.remove(wfTrainingToBeDeleted);

	    if (wfTrainingToBeDeleted.getInstanceId() != null) {
		TrainingEmployeesWorkFlow.deleteWFTraining(wfTrainingToBeDeleted);
	    }

	    selectedWFTraining = null;
	    beneficiary = null;

	    trainingPurposesArray = null;
	    employeeQualificationsData = null;
	    employeeNominatedOrJoinedTransactionsList = null;
	    employeeFinishedTransactionsList = null;
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String getGraduationPlaceFullDesc() {
	if (employeeQualificationsData != null && employeeQualificationsData.getCurGraduationPlaceDetailDesc() != null) {
	    return employeeQualificationsData.getCurGraduationPlaceDetailDesc() + " \\ " + employeeQualificationsData.getCurGraduationPlaceDesc() + " \\ " + employeeQualificationsData.getCurStudyCountryName();
	}
	return "";
    }

    public EmployeeQualificationsData getEmployeeQualificationsData() {
	return employeeQualificationsData;
    }

    public List<TrainingTransactionData> getEmployeeNominatedOrJoinedTransactionsList() {
	return employeeNominatedOrJoinedTransactionsList;
    }

    public List<TrainingTransactionData> getEmployeeFinishedTransactionsList() {
	return employeeFinishedTransactionsList;
    }

    public WFTrainingData getSelectedWFTraining() {
	return selectedWFTraining;
    }

    public void setSelectedWFTraining(WFTrainingData selectedWFTraining) {
	this.selectedWFTraining = selectedWFTraining;
    }

    public boolean isRegionAdmin() {
	return regionAdmin;
    }

    public String getPrivilegeUnitHkey() {
	return privilegeUnitHkey;
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

    public String[] getTrainingPurposesArray() {
	return trainingPurposesArray;
    }

    public void setTrainingPurposesArray(String[] trainingPurposesArray) {
	this.trainingPurposesArray = trainingPurposesArray;
	if (trainingPurposesArray == null || trainingPurposesArray.length == 0)
	    selectedWFTraining.setTrainingPurpose(null);
	else {
	    StringBuilder trainingPurpose = new StringBuilder();
	    for (int i = 0; i < trainingPurposesArray.length; i++)
		trainingPurpose.append(trainingPurposesArray[i]).append(",");
	    if (trainingPurpose.length() != 0)
		trainingPurpose.deleteCharAt(trainingPurpose.length() - 1);
	    selectedWFTraining.setTrainingPurpose(trainingPurpose.toString());
	}
    }

    public Long getSelectedCourseEventId() {
	return selectedCourseEventId;
    }

    public void setSelectedCourseEventId(Long selectedCourseEventId) {
	this.selectedCourseEventId = selectedCourseEventId;
    }
}
