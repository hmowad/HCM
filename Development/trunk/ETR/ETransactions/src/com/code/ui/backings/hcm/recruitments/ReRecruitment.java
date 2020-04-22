package com.code.ui.backings.hcm.recruitments;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.payroll.Degree;
import com.code.dal.orm.hcm.recruitments.RecruitmentTransactionData;
import com.code.dal.orm.hcm.trainings.QualificationLevel;
import com.code.enums.CategoriesEnum;
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

@ManagedBean(name = "reRecruitment")
@ViewScoped
public class ReRecruitment extends BaseBacking {

    private int mode;
    private EmployeeData employee;
    private RecruitmentTransactionData recruitment;
    private List<Rank> officersRanks;
    private List<QualificationLevel> qualificationLevels;
    private List<Degree> degrees;

    public ReRecruitment() {
	super();
	try {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersRerecruitment"));

		officersRanks = CommonService.getRanks(null, getCategoriesIdsArrayByMode((int) CategoriesEnum.OFFICERS.getCode()));
		for (int i = officersRanks.size() - 1; i >= 0; i--) {
		    if (officersRanks.get(i).getId() == RanksEnum.CADET.getCode()) {
			officersRanks.remove(i);
			break;
		    }
		}

		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersRerecruitment"));
		break;
	    case 3:
		setScreenTitle(getMessage("title_civiliansRerecruitment"));
		break;
	    default:
		throw new BusinessException("error_URLError");
	    }

	    qualificationLevels = TrainingSetupService.getAllQualificationLevels();
	    degrees = PayrollsService.getAllDegrees();

	    resetForm();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getEmployeeData() {
	try {
	    employee = EmployeesService.getEmployeeData(employee.getEmpId());

	    if (employee == null)
		throw new BusinessException("error_UIError");
	    else if (employee.getStatusId() != EmployeeStatusEnum.SERVICE_TERMINATED.getCode() && employee.getStatusId() != EmployeeStatusEnum.MOVED_EXTERNALLY.getCode()) {
		throw new BusinessException("error_empShouldBeTerminatedOrMovedExternally");
	    }
	    EmployeesJobsConflictValidator.validateEmployeesJobsConflicts(new Long[] { employee.getEmpId() }, null, TransactionClassesEnum.RETIREMENTS.getCode(), false, null);
	    RecruitmentsService.constructReRecruitmentTransaction(recruitment, employee, RecruitmentTypeEnum.RE_RECRUITMENT.getCode());
	} catch (BusinessException e) {
	    resetForm();
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void resetForm() {
	employee = new EmployeeData();
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

	    RecruitmentsService.handleReRecruitmentRequests(recruitment, employee, null, FlagsEnum.ALL.getCode());
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

    public RecruitmentTransactionData getRecruitment() {
	return recruitment;
    }

    public void setRecruitment(RecruitmentTransactionData recruitment) {
	this.recruitment = recruitment;
    }

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

    public List<Rank> getOfficersRanks() {
	return officersRanks;
    }

    public void setOfficersRanks(List<Rank> officersRanks) {
	this.officersRanks = officersRanks;
    }

    public List<QualificationLevel> getQualificationLevels() {
	return qualificationLevels;
    }

    public void setQualificationLevels(List<QualificationLevel> qualificationLevels) {
	this.qualificationLevels = qualificationLevels;
    }

    public List<Degree> getDegrees() {
	return degrees;
    }

    public void setDegrees(List<Degree> degrees) {
	this.degrees = degrees;
    }

}
