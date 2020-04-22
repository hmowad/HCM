package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.hcm.recruitments.WFRecruitmentData;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PayrollsService;
import com.code.services.workflow.hcm.RecruitmentsWorkFlow;

@ManagedBean(name = "civiliansRecruitment")
@ViewScoped
public class CiviliansRecruitment extends RecruitmentsBase implements Serializable {

    private String basedOn;

    private int civiliansMode;

    public CiviliansRecruitment() {
	super.init();
	if (getRequest().getParameter("mode") != null) {
	    civiliansMode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (civiliansMode) {
	    case 3:
		setScreenTitle(getMessage("title_personsRecruitment"));
		this.processId = WFProcessesEnum.PERSONS_RECRUITMENT.getCode();
		break;
	    case 4:
		setScreenTitle(getMessage("title_usersRecruitment"));
		this.processId = WFProcessesEnum.USERS_RECRUITMENT.getCode();
		break;
	    case 5:
		setScreenTitle(getMessage("title_wagesRecruitment"));
		this.processId = WFProcessesEnum.WAGES_RECRUITMENT.getCode();
		break;
	    case 6:
		setScreenTitle(getMessage("title_contractorsRecruitment"));
		this.processId = WFProcessesEnum.CONTRACTORS_RECRUITMENT.getCode();
		break;
	    case 9:
		setScreenTitle(getMessage("title_medicalStaffRecruitment"));
		this.processId = WFProcessesEnum.MEDICAL_STAFF_RECRUITMENT.getCode();
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	    this.init();
	} else
	    setServerSideErrorMessages(getMessage("error_URLError"));
    }

    public void init() {
	try {
	    degrees = PayrollsService.getAllDegrees();
	    selectedRec = new WFRecruitmentData();
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		recruitmentsList = new ArrayList<WFRecruitmentData>();
		internalCopies = new ArrayList<EmployeeData>();
	    } else {
		recruitmentsList = RecruitmentsWorkFlow.getWFRecruitmentsByInstanceId(instance.getInstanceId());
		basedOnOrderNumber = recruitmentsList.get(0).getBasedOnOrderNumber();
		basedOnOrderDate = recruitmentsList.get(0).getBasedOnOrderDate();
		basedOn = recruitmentsList.get(0).getBasedOn();
		internalCopies = EmployeesService.getEmployeesByIdsString(recruitmentsList.get(0).getInternalCopies());
		externalCopies = recruitmentsList.get(0).getExternalCopies();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewRecruitment() {
	try {
	    if (!validateEmployeesDuplicate()) {
		setServerSideErrorMessages(getMessage("error_duplicateEmp"));
		return;
	    }
	    selectedRec = RecruitmentsWorkFlow.constructCiviliansWFRecruitment(processId, selectedEmpId, basedOnOrderNumber, basedOnOrderDate, basedOn);
	    recruitmentsList.add(selectedRec);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void saveWFRecruitment() {
	if (!validateJobsConflict(selectedRec)) {
	    setServerSideErrorMessages(getMessage("error_recruitmentJobRepeated"));
	    return;
	}
	setDegree();
    }

    public void deleteRecruitment(WFRecruitmentData rec) {
	try {

	    if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {

		if (recruitmentsList.size() == 1)
		    throw new BusinessException("error_recruitmentListEmpty");

		boolean isAllNewRecords = true;
		for (WFRecruitmentData wfRecruitmentData : recruitmentsList) {
		    if (wfRecruitmentData.getInstanceId() != null && !wfRecruitmentData.getEmployeeId().equals(rec.getEmployeeId())) {
			isAllNewRecords = false;
			break;
		    }
		}

		if (isAllNewRecords)
		    throw new BusinessException("error_saveRecordsBeforeDeleting");
	    }

	    if (rec.getInstanceId() != null)
		RecruitmentsWorkFlow.deleteWFRecruitment(rec);

	    recruitmentsList.remove(rec);
	    if (selectedRec.equals(rec)) {
		selectedRec = new WFRecruitmentData();
		selectedEmpId = null;
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getCiviliansMode() {
	return civiliansMode;
    }

    public void setCiviliansMode(int civiliansMode) {
	this.civiliansMode = civiliansMode;
    }

    public String getBasedOn() {
	return basedOn;
    }

    public void setBasedOn(String basedOn) {
	this.basedOn = basedOn;
    }
}
