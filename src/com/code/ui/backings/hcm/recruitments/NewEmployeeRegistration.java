package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.GraduationGroupPlace;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeePhoto;
import com.code.dal.orm.hcm.employees.EmployeeQualificationsData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.trainings.QualificationLevel;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.GendersEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RanksEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.RecruitmentsWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "newEmployeeRegistration")
@ViewScoped
public class NewEmployeeRegistration extends BaseBacking implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;

    private boolean hasPrivilege;

    private Long categoryId;

    private EmployeeData employee;
    EmployeePhoto empPhoto;
    private EmployeeQualificationsData employeeQualificationsData;

    private boolean updateMode;
    private boolean viewOnly;
    private boolean employeeHasRecruitmentRequest;
    private boolean yaqeenFlag = false;

    private int pageSize = 10;

    private List<Category> categories;
    private List<QualificationLevel> qualificationLevels;

    // For officers
    private List<Region> officerRecruitmentRegions;
    private List<GraduationGroupPlace> officersGraduationGroupPlaces;

    private List<Rank> soldierRecruitmentRanks;

    public NewEmployeeRegistration() {
	try {
	    hasPrivilege = true;
	    if (getRequest().getParameter("employeeId") != null)
		updateModeInit();

	    else if (getRequest().getParameter("mode") != null)
		insertModeInit();

	    else
		throw new BusinessException("error_URLError");

	    commonInit();
	} catch (BusinessException e) {
	    hasPrivilege = false;
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void insertModeInit() throws BusinessException {
	updateMode = false;
	mode = Integer.parseInt(getRequest().getParameter("mode"));

	if (mode == 1) {
	    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_OFFICER_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_INSERT_OFFICERS.getCode())) {
		categoryId = CategoriesEnum.OFFICERS.getCode();
	    } else {
		throw new BusinessException("error_notAuthorized");
	    }
	} else if (mode == 2) {
	    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_SOLDIER_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_INSERT_SOLDIERS.getCode())) {
		categoryId = CategoriesEnum.SOLDIERS.getCode();
	    } else {
		throw new BusinessException("error_notAuthorized");
	    }
	} else if (mode == 3) {
	    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_CIVILIAN_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_INSERT_CIVILIANS.getCode())) {
		// !!
	    } else {
		throw new BusinessException("error_notAuthorized");
	    }

	} else {
	    throw new BusinessException("error_URLError");
	}

    }

    private void updateModeInit() throws BusinessException {
	updateMode = true;
	employee = EmployeesService.getEmployeeData(Long.parseLong(getRequest().getParameter("employeeId")));
	if (employee == null)
	    throw new BusinessException("error_employeeDataError");

	employeeQualificationsData = EmployeesService.getEmployeeQualificationsByEmpId(employee.getEmpId());

	categoryId = employee.getCategoryId();
	loadEmployeeImage();

	if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
	    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_OFFICER_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_MODIFY_OFFICERS.getCode()))
		viewOnly = false;
	    else if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_OFFICER_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_SHOW_OFFICERS.getCode()))
		viewOnly = true;
	    else
		throw new BusinessException("error_notAuthorized");

	    mode = CategoryModesEnum.OFFICERS.getCode();
	} else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {
	    if ((SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_SOLDIER_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_MODIFY_SOLDIERS.getCode()) && GendersEnum.MALE.getCode().equals(employee.getGender())) ||
		    (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_SOLDIER_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_MODIFY_FEMALE_SOLDIERS.getCode()) && GendersEnum.FEMALE.getCode().equals(employee.getGender())))
		viewOnly = false;
	    else if ((SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_SOLDIER_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_SHOW_SOLDIERS.getCode()) && GendersEnum.MALE.getCode().equals(employee.getGender())) ||
		    (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_SOLDIER_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_SHOW_FEMALE_SOLDIERS.getCode()) && GendersEnum.FEMALE.getCode().equals(employee.getGender())))
		viewOnly = true;
	    else
		throw new BusinessException("error_notAuthorized");

	    mode = CategoryModesEnum.SOLDIERS.getCode();
	} else {
	    if ((SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_CIVILIAN_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_MODIFY_CIVILIANS.getCode()) && GendersEnum.MALE.getCode().equals(employee.getGender())) ||
		    (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_CIVILIAN_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_MODIFY_FEMALE_CIVILIANS.getCode()) && GendersEnum.FEMALE.getCode().equals(employee.getGender())))
		viewOnly = false;
	    else if ((SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_CIVILIAN_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_SHOW_CIVILIANS.getCode()) && GendersEnum.MALE.getCode().equals(employee.getGender())) ||
		    (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.REC_NEW_CIVILIAN_REGISTRATION.getCode(), MenuActionsEnum.REC_EMPS_REGISTRATION_SHOW_FEMALE_CIVILIANS.getCode()) && GendersEnum.FEMALE.getCode().equals(employee.getGender())))
		viewOnly = true;
	    else
		throw new BusinessException("error_notAuthorized");

	    mode = CategoryModesEnum.CIVILIANS.getCode();
	}

	if (employeeHasRecruitmentRequest()) {
	    viewOnly = true;
	    employeeHasRecruitmentRequest = true;
	}
    }

    private void commonInit() throws BusinessException {
	switch (mode) {
	case 1:
	    setScreenTitle(getMessage("title_newOfficerRegistration"));
	    officerRecruitmentRegions = CommonService.getAllRegions();
	    officersGraduationGroupPlaces = CommonService.getAllGraduationGroupPlaces();

	    break;
	case 2:
	    setScreenTitle(getMessage("title_newSoldierRegistration"));
	    soldierRecruitmentRanks = CommonService.getRanks(null, getCategoriesIdsArrayByMode(mode));
	    for (int i = 0; i < soldierRecruitmentRanks.size(); i++) {
		if (soldierRecruitmentRanks.get(i).getId() == RanksEnum.STUDENT_SOLDIER.getCode())
		    soldierRecruitmentRanks.remove(i);
	    }

	    break;
	case 3:
	    setScreenTitle(getMessage("title_newPersonRegistration"));
	    categories = CommonService.getPersonsCategories();

	    break;
	default:
	    throw new BusinessException("error_URLError");
	}

	qualificationLevels = TrainingSetupService.getAllQualificationLevels();

	if (employee == null)
	    resetForm();
    }

    public void resetForm() {
	employee = new EmployeeData();
	employee.setExceptionalRecruitmentFlag(FlagsEnum.OFF.getCode());
	employeeQualificationsData = new EmployeeQualificationsData();
	employee.setCategoryId(categoryId);
	yaqeenFlag = false;
	// reset category drop down list and employee country
	if (mode == 3) {
	    employee.setCategoryId(CategoriesEnum.PERSONS.getCode());
	}
    }

    public boolean employeeHasRecruitmentRequest() {
	try {
	    if (RecruitmentsWorkFlow.countRunningRequests(new Long[] { employee.getEmpId() }, null, null) > 0)
		return true;
	    return false;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	    return false;
	}
    }

    public void loadEmployeeImage() {
	try {
	    empPhoto = EmployeesService.getEmployeePhotoByEmpId(employee.getEmpId());
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void recruitmentRankListener() {
	if (employee.getRecruitmentRankId() != null) {
	    if (employee.getRecruitmentRankId().intValue() == RanksEnum.SOLDIER.getCode() || employee.getRecruitmentRankId().intValue() == RanksEnum.FIRST_SOLDIER.getCode()) {
		employee.setStatusId(EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode());
		employee.setStatusDesc(getMessage("label_newStudent"));
	    } else {
		employee.setStatusId(EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode());
		employee.setStatusDesc(getMessage("label_newStudentRankedSoldier"));
		employee.setRecTrainingUnitId(null);
		employee.setRecTrainingUnitFullName(null);
	    }
	} else {
	    employee.setStatusId(null);
	    employee.setStatusDesc(null);
	}
    }

    public void genderListener() {
	employee.setRecTrainingUnitId(null);
	employee.setRecTrainingUnitFullName(null);
	employee.setRecTrainingJoiningDate(null);
    }

    public void save() {
	try {
	    employee.getEmployee().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    employeeQualificationsData.getEmployeeQualifications().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    if (employee.getEmpId() == null) {
		EmployeesService.addEmployee(employee, employeeQualificationsData, null);
	    } else {
		EmployeesService.updateEmployeeAndHisQualifications(employee, employeeQualificationsData);
	    }
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public String getGraduationPlaceFullDesc() {
	if (employeeQualificationsData.getRecGraduationPlaceDetailDesc() != null) {
	    return employeeQualificationsData.getRecGraduationPlaceDetailDesc() + " \\ " + employeeQualificationsData.getRecGraduationPlaceDesc() + " \\ " + employeeQualificationsData.getRecStudyCountryName();
	}

	return "";
    }

    public void yaqeenInformationRetrieval() {
	try {
	    EmployeesService.updateEmployeeDataFromYaqeen(employee, this.loginEmpData.getSocialID(), getClientIpAddress());
	    yaqeenFlag = true;
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

    public EmployeeQualificationsData getEmployeeQualificationsData() {
	return employeeQualificationsData;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public List<Region> getOfficerRecruitmentRegions() {
	return officerRecruitmentRegions;
    }

    public void setOfficerRecruitmentRegions(List<Region> officerRecruitmentRegions) {
	this.officerRecruitmentRegions = officerRecruitmentRegions;
    }

    public List<QualificationLevel> getQualificationLevels() {
	return qualificationLevels;
    }

    public void setQualificationLevels(List<QualificationLevel> qualificationLevels) {
	this.qualificationLevels = qualificationLevels;
    }

    public List<Rank> getSoldierRecruitmentRanks() {
	return soldierRecruitmentRanks;
    }

    public void setSoldierRecruitmentRanks(List<Rank> soldierRecruitmentRanks) {
	this.soldierRecruitmentRanks = soldierRecruitmentRanks;
    }

    public List<Category> getCategories() {
	return categories;
    }

    public void setCategories(List<Category> categories) {
	this.categories = categories;
    }

    public boolean isUpdateMode() {
	return updateMode;
    }

    public boolean isViewOnly() {
	return viewOnly;
    }

    public boolean isEmployeeHasRecruitmentRequest() {
	return employeeHasRecruitmentRequest;
    }

    public void setEmployeeHasRecruitmentRequest(boolean employeeHasRecruitmentRequest) {
	this.employeeHasRecruitmentRequest = employeeHasRecruitmentRequest;
    }

    public List<GraduationGroupPlace> getOfficersGraduationGroupPlaces() {
	return officersGraduationGroupPlaces;
    }

    public boolean isHasPrivilege() {
	return hasPrivilege;
    }

    public void setHasPrivilege(boolean hasPrivilege) {
	this.hasPrivilege = hasPrivilege;
    }

    public EmployeePhoto getEmpPhoto() {
	return empPhoto;
    }

    public void setEmpPhoto(EmployeePhoto empPhoto) {
	this.empPhoto = empPhoto;
    }

    public boolean isYaqeenFlag() {
	return yaqeenFlag;
    }

    public void setYaqeenFlag(boolean yaqeenFlag) {
	this.yaqeenFlag = yaqeenFlag;
    }

}
