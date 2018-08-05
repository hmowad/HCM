package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.hcm.recruitments.WFRecruitmentData;
import com.code.enums.RecruitmentClassesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PayrollsService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.RecruitmentsWorkFlow;

/**
 * @author Sherif Osama
 * 
 */
@ManagedBean(name = "soldiersExceptionalRecruitment")
@ViewScoped
public class SoldiersExceptionalRecruitment extends RecruitmentsBase implements Serializable {
    /**
     * mode: 0 for exceptional recruitments decisions(soldiers/first soldiers), 1 for exceptional recruitments decisions(corporal or higher), 2 for exceptional graduation letters, 3 for inspectors exceptional recruitments
     **/
    private int recruitmentMode;
    private long recruitmentClass;

    private WFRecruitmentData recruitmentData;
    private EmployeeData employee;

    private String instituteOrTrainingCenterUnitHKey;

    public SoldiersExceptionalRecruitment() {
	super.init();
	if (getRequest().getParameter("mode") != null) {
	    recruitmentMode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (recruitmentMode) {
	    case 0:
		setScreenTitle(getMessage("title_soldierAndFirstSoldierExceptionalRecruitment"));
		this.processId = WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode();
		recruitmentClass = RecruitmentClassesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode();
		break;
	    case 1:
		setScreenTitle(getMessage("title_corporalSoldierOrHigherExceptionalRecruitment"));
		this.processId = WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_EXCEPTIONAL_RECRUITMENT.getCode();
		recruitmentClass = RecruitmentClassesEnum.SOLDIERS_CORPORAL_OR_HIGHER_EXCEPTIONAL_RECRUITMENT.getCode();
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldierExceptionalGraduationLetterDecision"));
		this.processId = WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode();
		recruitmentClass = RecruitmentClassesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT.getCode();
		break;
	    case 3:
		setScreenTitle(getMessage("title_inspectorExceptionalRecruitment"));
		this.processId = WFProcessesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode();
		recruitmentClass = RecruitmentClassesEnum.INSPECTORS_EXCEPTIONAL_RECRUITMENT.getCode();
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_general"));
	    }

	    init();
	} else
	    setServerSideErrorMessages(getMessage("error_general"));
    }

    public void init() {
	try {
	    degrees = PayrollsService.getAllDegrees();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {

		recruitmentData = new WFRecruitmentData();
		recruitmentsList = new ArrayList<WFRecruitmentData>();

		recruitmentData.setBasedOnOrderDate(HijriDateService.getHijriSysDate());

		internalCopies = new ArrayList<EmployeeData>();
		externalCopies = RecruitmentsWorkFlow.getSoldiersRecruitmentExternalCopies();

		if (recruitmentMode == 1)
		    recruitmentData.setRecruitmentDate(HijriDateService.getHijriSysDate());
	    } else {
		recruitmentsList = RecruitmentsWorkFlow.getWFRecruitmentsByInstanceId(instance.getInstanceId());
		recruitmentData = recruitmentsList.get(0);
		employee = EmployeesService.getEmployeeData(recruitmentData.getEmployeeId());
		selectedEmpId = employee.getEmpId();
		internalCopies = EmployeesService.getEmployeesByIdsString(recruitmentsList.get(0).getInternalCopies());
		externalCopies = recruitmentsList.get(0).getExternalCopies();
	    }

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addRecruitment() {
	try {
	    recruitmentsList = new ArrayList<WFRecruitmentData>();

	    employee = EmployeesService.getEmployeeData(selectedEmpId);
	    if (employee == null)
		throw new BusinessException("error_general");

	    if (processId == WFProcessesEnum.SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT.getCode())
		instituteOrTrainingCenterUnitHKey = UnitsService.getUnitById(employee.getRecTrainingUnitId()).gethKey();

	    recruitmentData = RecruitmentsWorkFlow.constructExceptionalSoldiersWFRecruitment(processId, employee, recruitmentData.getBasedOnOrderNumber(), recruitmentData.getBasedOnOrderDate(), recruitmentData.getRecruitmentDate());
	    recruitmentsList.add(recruitmentData);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getRecruitmentMode() {
	return recruitmentMode;
    }

    public void setRecruitmentMode(int recruitmentMode) {
	this.recruitmentMode = recruitmentMode;
    }

    public long getRecruitmentClass() {
	return recruitmentClass;
    }

    public void setRecruitmentClass(long recruitmentClass) {
	this.recruitmentClass = recruitmentClass;
    }

    public WFRecruitmentData getRecruitmentData() {
	return recruitmentData;
    }

    public void setRecruitmentData(WFRecruitmentData recruitmentData) {
	this.recruitmentData = recruitmentData;
    }

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

    public String getInstituteOrTrainingCenterUnitHKey() {
	return instituteOrTrainingCenterUnitHKey;
    }

    public void setInstituteOrTrainingCenterUnitHKey(String instituteOrTrainingCenterUnitHKey) {
	this.instituteOrTrainingCenterUnitHKey = instituteOrTrainingCenterUnitHKey;
    }
}
