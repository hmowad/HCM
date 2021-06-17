package com.code.ui.backings.hcm.employees;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.RankTitle;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeePhoto;
import com.code.dal.orm.hcm.employees.EmployeeQualificationsData;
import com.code.dal.orm.hcm.employees.medicalstuff.EmployeeMedicalStaffData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.payroll.Degree;
import com.code.dal.orm.hcm.trainings.QualificationLevel;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryClassificationEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.GendersEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PayrollsService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "employeesDataView")
@ViewScoped
public class EmployeesDataView extends BaseBacking implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;
    private EmployeeData employee;
    private EmployeePhoto empPhoto;
    private EmployeeQualificationsData employeeQualificationsData;
    private JobData jobData;
    private String officersGraduationGroupPlace;
    private List<RankTitle> officersRanksTitles;
    private List<QualificationLevel> qualificationLevels;
    private List<Degree> degrees;
    private List<Rank> salaryRanks;
    private List<Category> categories;
    private int pageSize = 10;
    private boolean viewOnly;
    private boolean viewSaveBtnForYaqeen;
    private boolean nonSaudiDoctorContractorFlag;
    private EmployeeMedicalStaffData employeeMedicalStaffData;

    private boolean yaqeenIntegrationEnabledFlag;

    public EmployeesDataView() {
	init();
    }

    public void init() {
	try {

	    String idParam = getRequest().getParameter("employeeId");
	    employee = EmployeesService.getEmployeeData(loginEmpData.getEmpId());
	    employeeQualificationsData = EmployeesService.getEmployeeQualificationsByEmpId(loginEmpData.getEmpId());
	    viewOnly = true;
	    viewSaveBtnForYaqeen = socialIdNeedToBeUpdated();
	    yaqeenIntegrationEnabledFlag = (ETRConfigurationService.getYaqeenEnabledFlag() != null && ETRConfigurationService.getYaqeenEnabledFlag().equals(FlagsEnum.ON.getCode())) ? true : false;

	    EmployeeData empParam;
	    EmployeeQualificationsData employeeQualificationsDataParam;

	    if (idParam != null) {
		Long empId = Long.parseLong(idParam);
		empParam = EmployeesService.getEmployeeData(empId);
		employeeQualificationsDataParam = EmployeesService.getEmployeeQualificationsByEmpId(empId);
		if (empParam == null)
		    throw new BusinessException("error_employeeDataError");

		if (loginEmpData.getPhysicalRegionId() != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() && !(loginEmpData.getPhysicalRegionId().equals(empParam.getPhysicalRegionId()) || loginEmpData.getPhysicalRegionId().equals(empParam.getOfficialRegionId()))) {
		    employee = null;
		    employeeQualificationsData = null;
		    throw new BusinessException("error_employeeDataError");
		}
		if (empParam.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode()) {
		    if (empParam.getCategoryClassificationId() != null && empParam.getCategoryClassificationId() == CategoryClassificationEnum.NON_SAUDI_DOCOTRS_CONTRACTORS.getCode()) {
			nonSaudiDoctorContractorFlag = true;
		    } else {
			nonSaudiDoctorContractorFlag = false;
		    }
		}

		switch (empParam.getCategoryId().intValue()) {
		case 1:
		    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_MODIFY_OFFICERS.getCode())) {
			employee = empParam;
			employeeQualificationsData = employeeQualificationsDataParam;
			viewOnly = false;
		    } else if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_SHOW_OFFICERS.getCode())) {
			employee = empParam;
			employeeQualificationsData = employeeQualificationsDataParam;
		    }
		    employee.setJobModifiedFlag(FlagsEnum.ON.getCode());

		    break;

		case 2:
		    if ((SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_MODIFY_SOLDIERS.getCode()) && GendersEnum.MALE.getCode().equals(empParam.getGender())) ||
			    (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_MODIFY_FEMALE_SOLDIERS.getCode()) && GendersEnum.FEMALE.getCode().equals(empParam.getGender()))) {
			employee = empParam;
			employeeQualificationsData = employeeQualificationsDataParam;
			viewOnly = false;
		    } else if ((SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_SHOW_SOLDIERS.getCode()) && GendersEnum.MALE.getCode().equals(empParam.getGender())) ||
			    (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_SHOW_FEMALE_SOLDIERS.getCode()) && GendersEnum.FEMALE.getCode().equals(empParam.getGender()))) {
			employee = empParam;
			employeeQualificationsData = employeeQualificationsDataParam;
		    }

		    break;

		case 3:
		case 4:
		case 5:
		case 6:
		case 9:
		    if ((SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_MODIFY_CIVILIANS.getCode()) && GendersEnum.MALE.getCode().equals(empParam.getGender())) ||
			    (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_MODIFY_FEMALE_CIVILIANS.getCode()) && GendersEnum.FEMALE.getCode().equals(empParam.getGender()))) {
			employee = empParam;
			employeeQualificationsData = employeeQualificationsDataParam;
			viewOnly = false;
		    } else if ((SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_SHOW_CIVILIANS.getCode()) && GendersEnum.MALE.getCode().equals(empParam.getGender())) ||
			    (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_DATA_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_VIEW_SHOW_FEMALE_CIVILIANS.getCode()) && GendersEnum.FEMALE.getCode().equals(empParam.getGender()))) {
			employee = empParam;
			employeeQualificationsData = employeeQualificationsDataParam;
		    }
		    break;

		default:
		    throw new BusinessException("error_employeeDataError");
		}
	    }
	    employeeMedicalStaffData = EmployeesService.getEmployeeMedicalStaffDataByEmpId(employee.getEmpId());
	    salaryRanks = CommonService.getRanks(null, new Long[] { employee.getCategoryId() });

	    switch (employee.getCategoryId().intValue()) {
	    case 1:
		setScreenTitle(getMessage("title_officerData"));
		if (employee.getGraduationGroupPlace() != null)
		    officersGraduationGroupPlace = CommonService.getGraduationGroupPlaceById(employee.getGraduationGroupPlace()).getDescription();

		officersRanksTitles = CommonService.getAllRanksTitles();
		mode = 1;
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldierData"));
		mode = 2;
		break;
	    case 3:
	    case 4:
	    case 5:
	    case 6:
	    case 9:
		setScreenTitle(getMessage("title_personData"));
		mode = 3;
		categories = CommonService.getPersonsCategories();
		break;
	    default:
		throw new BusinessException("error_employeeDataError");
	    }

	    degrees = PayrollsService.getAllDegrees();
	    qualificationLevels = TrainingSetupService.getAllQualificationLevels();

	    loadEmployeeImage();

	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    employee.getEmployee().setSystemUser(this.loginEmpData.getEmpId() + ""); // For auditing.
	    employeeQualificationsData.getEmployeeQualifications().setSystemUser(this.loginEmpData.getEmpId() + ""); // For auditing.
	    if (nonSaudiDoctorContractorFlag)
		employee.setCategoryClassificationId(CategoryClassificationEnum.NON_SAUDI_DOCOTRS_CONTRACTORS.getCode());
	    else
		employee.setCategoryClassificationId(null);

	    EmployeesService.updateEmployeeAndHisQualifications(employee, employeeQualificationsData);
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    viewSaveBtnForYaqeen = socialIdNeedToBeUpdated();
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void loadEmployeeImage() {
	try {
	    empPhoto = EmployeesService.getEmployeePhotoByEmpId(employee.getEmpId());
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String getRecGraduationPlaceFullDesc() {
	if (employeeQualificationsData.getRecGraduationPlaceDetailDesc() != null) {
	    return employeeQualificationsData.getRecGraduationPlaceDetailDesc() + " \\ " + employeeQualificationsData.getRecGraduationPlaceDesc() + " \\ " + employeeQualificationsData.getRecStudyCountryName();
	}

	return "";
    }

    public String getCurGraduationPlaceFullDesc() {
	if (employeeQualificationsData.getCurGraduationPlaceDetailDesc() != null) {
	    return employeeQualificationsData.getCurGraduationPlaceDetailDesc() + " \\ " + employeeQualificationsData.getCurGraduationPlaceDesc() + " \\ " + employeeQualificationsData.getCurStudyCountryName();
	}

	return "";
    }

    public void updateEmployeeDataFromYaqeen() {
	try {
	    EmployeesService.updateEmployeeDataFromYaqeen(employee, this.loginEmpData.getSocialID(), getClientIpAddress());
	    if (socialIdNeedToBeUpdated())
		super.setServerSideErrorMessages(getMessage("error_renewYourSocialId"));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public boolean socialIdNeedToBeUpdated() {
	try {
	    return ((loginEmpData.getEmpId().equals(employee.getEmpId())) && (employee.getSocialIDExpiryDate() == null || EmployeesService.isSocialIdExpired(employee) || EmployeesService.isSocialIdExpiryDateInRenewalPeriodWarning(employee)));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
	return false;
    }

    public JobData getJobData() {
	return jobData;
    }

    public void setJobData(JobData jobData) {
	this.jobData = jobData;
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

    public List<QualificationLevel> getQualificationLevels() {
	return qualificationLevels;
    }

    public void setQualificationLevels(List<QualificationLevel> qualificationLevels) {
	this.qualificationLevels = qualificationLevels;
    }

    public boolean isViewOnly() {
	return viewOnly;
    }

    public void setViewOnly(boolean viewOnly) {
	this.viewOnly = viewOnly;
    }

    public List<Category> getCategories() {
	return categories;
    }

    public void setCategories(List<Category> categories) {
	this.categories = categories;
    }

    public List<Degree> getDegrees() {
	return degrees;
    }

    public void setDegrees(List<Degree> degrees) {
	this.degrees = degrees;
    }

    public List<Rank> getSalaryRanks() {
	return salaryRanks;
    }

    public void setSalaryRanks(List<Rank> salaryRanks) {
	this.salaryRanks = salaryRanks;
    }

    public List<RankTitle> getOfficersRanksTitles() {
	return officersRanksTitles;
    }

    public String getOfficersGraduationGroupPlace() {
	return officersGraduationGroupPlace;
    }

    public void setOfficersGraduationGroupPlace(String officersGraduationGroupPlace) {
	this.officersGraduationGroupPlace = officersGraduationGroupPlace;
    }

    public EmployeePhoto getEmpPhoto() {
	return empPhoto;
    }

    public void setEmpPhoto(EmployeePhoto empPhoto) {
	this.empPhoto = empPhoto;
    }

    public boolean isNonSaudiDoctorContractorFlag() {
	return nonSaudiDoctorContractorFlag;
    }

    public void setNonSaudiDoctorContractorFlag(boolean nonSaudiDoctorContractorFlag) {
	this.nonSaudiDoctorContractorFlag = nonSaudiDoctorContractorFlag;
    }

    public EmployeeMedicalStaffData getEmployeeMedicalStaffData() {
	return employeeMedicalStaffData;
    }

    public void setEmployeeMedicalStaffData(EmployeeMedicalStaffData employeeMedicalStaffData) {
	this.employeeMedicalStaffData = employeeMedicalStaffData;
    }

    public boolean isViewSaveBtnForYaqeen() {
	return viewSaveBtnForYaqeen;
    }

    public void setViewSaveBtnForYaqeen(boolean viewSaveBtnForYaqeen) {
	this.viewSaveBtnForYaqeen = viewSaveBtnForYaqeen;
    }

    public boolean isYaqeenIntegrationEnabledFlag() {
	return yaqeenIntegrationEnabledFlag;
    }

    public void setYaqeenIntegrationEnabledFlag(boolean yaqeenIntegrationEnabledFlag) {
	this.yaqeenIntegrationEnabledFlag = yaqeenIntegrationEnabledFlag;
    }

}
