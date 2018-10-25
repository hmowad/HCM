package com.code.ui.backings.hcm.recruitments;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.GraduationGroupPlace;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeQualificationsData;
import com.code.dal.orm.hcm.payroll.Degree;
import com.code.dal.orm.hcm.recruitments.RecruitmentTransactionData;
import com.code.dal.orm.hcm.trainings.QualificationLevel;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RecruitmentTypeEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.buswfcoop.EmployeesJobsConflictValidator;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PayrollsService;
import com.code.services.hcm.RecruitmentsService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "recruitmentByExternalMove")
@ViewScoped
public class RecruitmentByExternalMove extends BaseBacking {
    private int mode;
    private int employeeDataMode;
    private EmployeeData employee;
    private EmployeeQualificationsData employeeQualificationsData;
    private RecruitmentTransactionData recruitment;
    private List<GraduationGroupPlace> officersGraduationGroupPlaces;
    private List<Rank> officersRanks;
    private List<Rank> recruitmentRanks;
    private List<QualificationLevel> qualificationLevels;
    private List<Category> categories;
    private List<Degree> degrees;

    public RecruitmentByExternalMove() {
	super();
	try {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersMoveFromExternalParty"));
		officersRanks = CommonService.getRanks(null, getCategoriesIdsArrayByMode(mode));
		for (int i = officersRanks.size() - 1; i >= 0; i--) {
		    if (officersRanks.get(i).getId() == RanksEnum.CADET.getCode()) {
			officersRanks.remove(i);
			break;
		    }
		}
		recruitmentRanks = CommonService.getRanks(null, getCategoriesIdsArrayByMode(mode));
		for (int i = recruitmentRanks.size() - 1; i >= 0; i--) {
		    if (recruitmentRanks.get(i).getId() == RanksEnum.CADET.getCode()) {
			recruitmentRanks.remove(i);
			break;
		    }
		}
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersMoveFromExternalParty"));
		recruitmentRanks = CommonService.getRanks(null, getCategoriesIdsArrayByMode(mode));
		for (int i = recruitmentRanks.size() - 1; i >= 0; i--) {
		    if (recruitmentRanks.get(i).getId() == RanksEnum.STUDENT_SOLDIER.getCode()) {
			recruitmentRanks.remove(i);
			break;
		    }
		}
		break;
	    case 3:
		setScreenTitle(getMessage("title_civiliansMoveFromExternalParty"));
		recruitmentRanks = CommonService.getRanks(null, getCategoriesIdsArrayByMode(mode));
		categories = CommonService.getPersonsCategories();
		for (Category category : categories) {
		    if (category.getId() == CategoriesEnum.CONTRACTORS.getCode()) {
			categories.remove(category);
			break;
		    }

		}
		break;
	    default:
		throw new BusinessException("error_general");
	    }

	    officersGraduationGroupPlaces = CommonService.getAllGraduationGroupPlaces();
	    qualificationLevels = TrainingSetupService.getAllQualificationLevels();
	    degrees = PayrollsService.getAllDegrees();

	    reset();

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getEmployeeData() {
	try {
	    recruitment = new RecruitmentTransactionData();
	    employee = EmployeesService.getEmployeeData(employee.getEmpId());

	    if (employee == null)
		throw new BusinessException("error_general");
	    // TODO check on moved externally also
	    else {
		if (employee.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode()) {
		    recruitment = new RecruitmentTransactionData();
		    throw new BusinessException("error_empShouldNotBeContractor");
		}
		if (employee.getStatusId() != EmployeeStatusEnum.SERVICE_TERMINATED.getCode() && employee.getStatusId() != EmployeeStatusEnum.MOVED_EXTERNALLY.getCode()) {
		    recruitment = new RecruitmentTransactionData();
		    throw new BusinessException("error_empShouldBeTerminatedOrMovedExternally");
		}
	    }
	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { employee.getEmpId() }, null, TransactionClassesEnum.RETIREMENTS.getCode(), false, null);
	    employeeQualificationsData = new EmployeeQualificationsData();
	    RecruitmentsService.constructReRecruitmentTransaction(recruitment, employee, RecruitmentTypeEnum.RECRUITMENT_BY_EXTERNAL_MOVE.getCode());
	} catch (BusinessException e) {
	    resetForm();
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void reset() {
	resetForm();
	employeeDataMode = FlagsEnum.OFF.getCode();
    }

    public void resetForm() {
	employee = new EmployeeData();
	if (mode != CategoryModesEnum.CIVILIANS.getCode()) {
	    employee.setCategoryId(new Long(mode));
	}
	employeeQualificationsData = new EmployeeQualificationsData();
	recruitment = new RecruitmentTransactionData();
    }

    public String getJobCodeAndNameString() {
	if ((recruitment.getTransJobCode() == null || recruitment.getTransJobCode().isEmpty()) &&
		(recruitment.getTransJobName() == null || recruitment.getTransJobName().isEmpty())) {
	    return "";
	} else if (recruitment.getTransJobCode() == null || recruitment.getTransJobCode().isEmpty()) {
	    return recruitment.getTransJobName();
	} else if (recruitment.getTransJobName() == null || recruitment.getTransJobName().isEmpty()) {
	    return recruitment.getTransJobCode();
	} else {
	    return recruitment.getTransJobCode() + " - " + recruitment.getTransJobName();

	}
    }

    public void save() {
	try {
	    if (employee.getEmpId() == null) {
		RecruitmentsService.constructReRecruitmentTransaction(recruitment, employee, RecruitmentTypeEnum.RECRUITMENT_BY_EXTERNAL_MOVE.getCode());
		employee.setFirstRecruitmentDate(recruitment.getFirstRecruitmentDate());
	    }

	    RecruitmentsService.handleReRecruitmentRequests(recruitment, employee, employeeQualificationsData, employeeDataMode);
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
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

    public RecruitmentTransactionData getRecruitment() {
	return recruitment;
    }

    public void setRecruitment(RecruitmentTransactionData recruitment) {
	this.recruitment = recruitment;
    }

    public int getEmployeeDataMode() {
	return employeeDataMode;
    }

    public void setEmployeeDataMode(int employeeDataMode) {
	this.employeeDataMode = employeeDataMode;
    }

    public List<GraduationGroupPlace> getOfficersGraduationGroupPlaces() {
	return officersGraduationGroupPlaces;
    }

    public void setOfficersGraduationGroupPlaces(List<GraduationGroupPlace> officersGraduationGroupPlaces) {
	this.officersGraduationGroupPlaces = officersGraduationGroupPlaces;
    }

    public List<Rank> getOfficersRanks() {
	return officersRanks;
    }

    public List<Rank> getRecruitmentRanks() {
	return recruitmentRanks;
    }

    public void setRecruitmentRanks(List<Rank> recruitmentRanks) {
	this.recruitmentRanks = recruitmentRanks;
    }

    public void setOfficersRanks(List<Rank> officersRanks) {
	this.officersRanks = officersRanks;
    }

    public EmployeeQualificationsData getEmployeeQualificationsData() {
	return employeeQualificationsData;
    }

    public void setEmployeeQualificationsData(EmployeeQualificationsData employeeQualificationsData) {
	this.employeeQualificationsData = employeeQualificationsData;
    }

    public List<QualificationLevel> getQualificationLevels() {
	return qualificationLevels;
    }

    public void setQualificationLevels(List<QualificationLevel> qualificationLevels) {
	this.qualificationLevels = qualificationLevels;
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

}
