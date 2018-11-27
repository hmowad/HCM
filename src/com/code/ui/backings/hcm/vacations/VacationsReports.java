package com.code.ui.backings.hcm.vacations;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "vacationsReports")
@ViewScoped
public class VacationsReports extends BaseBacking implements Serializable {

    private List<Region> regionList;
    private long selectedRegionId;

    private long selectedCategoryId;
    private String selectedUnitFullName;
    private String selectedUnitHKey;

    private int reportType;
    private boolean viewAllLevelsVacationsFlag = false;

    private Date fromDate;
    private Date toDate;

    private boolean admin = false;

    public VacationsReports() {

	try {
	    admin = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.VAC_VACATIONS_REPORTS_FOR_ALL.getCode(), MenuActionsEnum.VAC_VACATIONS_REPORTS_FOR_ALL.getCode());

	    regionList = CommonService.getAllRegions();
	    reportType = 10;
	    resetForm();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void resetForm() {
	try {
	    selectedCategoryId = FlagsEnum.ALL.getCode();
	    selectedRegionId = getLoginEmpPhysicalRegionFlag(true);
	    fromDate = toDate = HijriDateService.getHijriSysDate();

	    selectedUnitFullName = this.loginEmpData.getPhysicalUnitFullName();
	    selectedUnitHKey = this.loginEmpData.getPhysicalUnitHKey();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    String reportTitle = "";
	    String regionDesc = getMessage("label_all");
	    if (reportType == 10) {
		reportTitle = getMessage("title_vacationsReportsTitle");

		if (selectedRegionId != FlagsEnum.ALL.getCode()) {
		    for (Region region : regionList) {
			if (region.getId().longValue() == selectedRegionId) {
			    regionDesc = region.getDescription();
			    break;
			}
		    }
		}
	    } else if (reportType == 80) {
		reportTitle = getMessage("title_vacationsUnitsPercentageReportTitle");
	    }

	    byte[] bytes = VacationsService.getVacationsReportsBytes(reportType, selectedRegionId, regionDesc, selectedUnitHKey, null, selectedCategoryId, fromDate, toDate,
		    FlagsEnum.OFF.getCode(), FlagsEnum.OFF.getCode(), (long) FlagsEnum.OFF.getCode(), null, null, (long) FlagsEnum.OFF.getCode(), "",
		    (long) FlagsEnum.OFF.getCode(), "", null, null, null, viewAllLevelsVacationsFlag, reportTitle);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public long getSelectedRegionId() {
	return selectedRegionId;
    }

    public void setSelectedRegionId(long selectedRegionId) {
	this.selectedRegionId = selectedRegionId;
    }

    public long getSelectedCategoryId() {
	return selectedCategoryId;
    }

    public void setSelectedCategoryId(long selectedCategoryId) {
	this.selectedCategoryId = selectedCategoryId;
    }

    public String getSelectedUnitFullName() {
	return selectedUnitFullName;
    }

    public void setSelectedUnitFullName(String selectedUnitFullName) {
	this.selectedUnitFullName = selectedUnitFullName;
    }

    public String getSelectedUnitHKey() {
	return selectedUnitHKey;
    }

    public void setSelectedUnitHKey(String selectedUnitHKey) {
	this.selectedUnitHKey = selectedUnitHKey;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public boolean isViewAllLevelsVacationsFlag() {
	return viewAllLevelsVacationsFlag;
    }

    public void setViewAllLevelsVacationsFlag(boolean viewAllLevelsVacationsFlag) {
	this.viewAllLevelsVacationsFlag = viewAllLevelsVacationsFlag;
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

    public List<Region> getRegionList() {
	return regionList;
    }

    public void setRegionList(List<Region> regionList) {
	this.regionList = regionList;
    }

    public boolean isAdmin() {
	return admin;
    }

    public void setAdmin(boolean admin) {
	this.admin = admin;
    }
}
