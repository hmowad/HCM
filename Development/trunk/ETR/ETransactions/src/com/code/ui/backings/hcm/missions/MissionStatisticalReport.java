package com.code.ui.backings.hcm.missions;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MissionsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "missionStatisticalReport")
@ViewScoped
public class MissionStatisticalReport extends BaseBacking {

    private int reportType = 1;
    private Long empId;
    private String empFullName;
    private Long orgUnitId;
    private String orgUnitFullName;
    private String orgUnitHKey;

    private List<Region> regionsList;
    private int category;
    private long regionId;
    private Date toDate;
    private Date fromDate;

    private Boolean isManager;
    private Boolean isAdmin;

    public MissionStatisticalReport() {
	regionsList = CommonService.getAllRegions();
	resetForm();
    }

    public void resetForm() {
	try {
	    category = FlagsEnum.ALL.getCode();
	    regionId = getLoginEmpPhysicalRegionFlag(true);
	    fromDate = toDate = HijriDateService.getHijriSysDate();
	    if (reportType == 2) {
		getInquiryReportPrivileges();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getInquiryReportPrivileges() {
	try {
	    isAdmin = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.MSN_INQUIRY_REPORT.getCode(), MenuActionsEnum.MSN_INQUIRY_REPORT.getCode());
	    isManager = loginEmpData.getIsManager().equals(FlagsEnum.ON.getCode());

	    if (isAdmin) {
		regionId = getLoginEmpPhysicalRegionFlag(isAdmin);
	    } else if (isManager) {
		regionId = loginEmpData.getPhysicalRegionId();
		orgUnitId = loginEmpData.getPhysicalUnitId();
		orgUnitFullName = loginEmpData.getPhysicalUnitFullName();
		orgUnitHKey = loginEmpData.getPhysicalUnitHKey();
	    }

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    if (reportType == FlagsEnum.ON.getCode()) {
		byte[] bytes = MissionsService.getMissionStatisicalReport(category, regionId, toDate, fromDate);
		super.print(bytes);
	    } else if (reportType == 2) {
		byte[] bytes = MissionsService.getMissionEmployeesStatisicalReport(category, regionId, toDate, fromDate, empId, empFullName, orgUnitId, orgUnitFullName);
		super.print(bytes);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getCategory() {
	return category;
    }

    public void setCategory(int category) {
	this.category = category;
    }

    public long getRegionId() {
	return regionId;
    }

    public void setRegionId(long regionId) {
	this.regionId = regionId;
    }

    public List<Region> getRegionsList() {
	return regionsList;
    }

    public void setRegionsList(List<Region> regionsList) {
	this.regionsList = regionsList;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public Long getOrgUnitId() {
	return orgUnitId;
    }

    public void setOrgUnitId(Long orgUnitId) {
	this.orgUnitId = orgUnitId;
    }

    public String getOrgUnitFullName() {
	return orgUnitFullName;
    }

    public void setOrgUnitFullName(String unitFullName) {
	this.orgUnitFullName = unitFullName;
    }

    public String getEmpFullName() {
	return empFullName;
    }

    public void setEmpFullName(String empFullName) {
	this.empFullName = empFullName;
    }

    public String getOrgUnitHKey() {
	return orgUnitHKey;
    }

    public void setOrgUnitHKey(String orgUnitHKey) {
	this.orgUnitHKey = orgUnitHKey;
    }

    public Boolean getIsManager() {
	return isManager;
    }

    public void setIsManager(Boolean isManager) {
	this.isManager = isManager;
    }

    public Boolean getIsAdmin() {
	return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
	this.isAdmin = isAdmin;
    }

}
