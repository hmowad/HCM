package com.code.ui.backings.hcm.missions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.missions.MissionData;
import com.code.dal.orm.security.EmployeeDecisionPrivilege;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MissionsService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "missionsInquiry")
@ViewScoped
public class MissionsInquiry extends BaseBacking {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Civil
     **/
    private int mode;
    private int adminUser;
    private List<EmployeeDecisionPrivilege> decisionsPrivileges;

    // For Search
    private String empId;
    private String empName;
    private String decisionNumber;
    private int locationFlag;
    private String destination;
    private String purpose;
    private Date fromDate;
    private Date toDate;

    private List<MissionData> missionList = new ArrayList<MissionData>();
    private int pageSize = 10;

    public MissionsInquiry() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		if (mode == CategoryModesEnum.OFFICERS.getCode()) {
		    setScreenTitle(getMessage("title_officersMissionInquiry"));
		} else if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
		    setScreenTitle(getMessage("title_soldiersMissionInquiry"));
		} else if (mode == CategoryModesEnum.CIVILIANS.getCode()) {
		    setScreenTitle(getMessage("title_personsMissionInquiry"));
		} else {
		    setServerSideErrorMessages(getMessage("error_general"));
		}
	    } else {
		setServerSideErrorMessages(getMessage("error_general"));
	    }

	    decisionsPrivileges = SecurityService.getEmployeesDecisionsPrivileges(this.loginEmpData.getEmpId(), WFProcessesGroupsEnum.MISSIONS.getCode(), mode);
	    adminUser = decisionsPrivileges.size() == 0 ? FlagsEnum.OFF.getCode() : FlagsEnum.ON.getCode();
	    resetForm();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	decisionNumber = "";
	locationFlag = FlagsEnum.ALL.getCode();
	destination = "";
	purpose = "";
	fromDate = null;
	toDate = null;
	missionList = new ArrayList<MissionData>();

	if (this.loginEmpData.getCategoryId().intValue() == mode
		|| (mode == CategoryModesEnum.CIVILIANS.getCode() && !this.loginEmpData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !this.loginEmpData.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))) {
	    empId = this.loginEmpData.getEmpId().toString();
	    empName = this.loginEmpData.getName();
	    searchMissions();
	} else {
	    empId = null;
	    empName = null;
	}
    }

    public void searchMissions() {
	try {
	    if (!SecurityService.isDecisionPrivilegeGranted(this.loginEmpData, (empId == null || empId.equals("")) ? null : Long.parseLong(empId), decisionsPrivileges))
		throw new BusinessException("error_notAuthorized");

	    missionList = MissionsService.getMissions((empId == null || empId.equals("")) ? FlagsEnum.ALL.getCode() : Long.parseLong(empId), decisionNumber, locationFlag, destination, purpose, fromDate, toDate, mode, getEmployeesSearchRegionId());
	} catch (BusinessException e) {
	    missionList = new ArrayList<MissionData>();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    missionList = new ArrayList<MissionData>();
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void printMission(MissionData mission) {
	try {
	    byte[] bytes = MissionsService.getMissionDecisionBytes(mission.getId(), mission.getCategoryMode());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printPaymentDecision(MissionData mission) {
	try {
	    byte[] bytes = MissionsService.getMissionPaymentDecisionBytes(mission);
	    super.printRTF(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printFinancialLink(MissionData mission) {
	try {
	    byte[] bytes = MissionsService.getMissionFinancialLinkBytes(mission);
	    super.printRTF(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printMissionOfficailPerformance(MissionData mission) {
	try {

	    byte[] bytes = MissionsService.getMissionOfficialPerformanceBytes(mission, this.loginEmpData, adminUser);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<MissionData> getMissionList() {
	return missionList;
    }

    public void setMissionList(List<MissionData> missionList) {
	this.missionList = missionList;
    }

    public String getEmpId() {
	return empId;
    }

    public void setEmpId(String empId) {
	this.empId = empId;
    }

    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public int getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(int locationFlag) {
	this.locationFlag = locationFlag;
    }

    public String getDestination() {
	return destination;
    }

    public void setDestination(String destination) {
	this.destination = destination;
    }

    public String getPurpose() {
	return purpose;
    }

    public void setPurpose(String purpose) {
	this.purpose = purpose;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public int getAdminUser() {
	return adminUser;
    }

    public void setAdminUser(int adminUser) {
	this.adminUser = adminUser;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public long getEmployeesSearchRegionId() {
	return getLoginEmpPhysicalRegionFlag(adminUser == 0 ? false : true);
    }
}
