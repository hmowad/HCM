package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.organization.jobs.ManpowerNeedData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.ManpowerNeedsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "manpowerNeedsStudiesInquiry")
@ViewScoped
public class ManpowerNeedsStudiesInquiry extends BaseBacking {

    private Long categoryId;
    private Date studyDateFrom;
    private Date studyDateTo;

    private List<ManpowerNeedData> manpowerNeedStudiesData;
    private ManpowerNeedData selectedStudy;

    private int reportType;
    private List<Region> regionsList;
    private long regionId;
    private String selectedMinorSpecsIds;
    private String selectedMinorSpecsDescriptions;

    private final int pageSize = 10;

    public ManpowerNeedsStudiesInquiry() {
	regionsList = CommonService.getAllRegions();
	resetForm();
    }

    public void resetForm() {
	try {
	    categoryId = CategoriesEnum.OFFICERS.getCode();
	    studyDateFrom = studyDateTo = HijriDateService.getHijriSysDate();
	    manpowerNeedStudiesData = new ArrayList<ManpowerNeedData>();
	    selectedStudy = new ManpowerNeedData();

	    resetReportsFields();

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void resetReportsFields() {
	reportType = 10;
	regionId = getLoginEmpPhysicalRegionFlag(true);
	selectedMinorSpecsIds = "";
	selectedMinorSpecsDescriptions = getMessage("label_all");
    }

    public void searchStudies() {
	selectedStudy = new ManpowerNeedData();
	try {
	    manpowerNeedStudiesData = ManpowerNeedsService.getManpowerNeedsStudiesData(this.loginEmpData.getPhysicalRegionId(), categoryId, studyDateFrom, studyDateTo);
	} catch (BusinessException e) {
	    manpowerNeedStudiesData = new ArrayList<ManpowerNeedData>();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectStudy(ManpowerNeedData manpowerNeedData) {
	selectedStudy = manpowerNeedData;
	resetReportsFields();
    }

    public void deleteManpowerNeedStudy(ManpowerNeedData manpowerNeedData) {
	try {
	    ManpowerNeedsService.deleteOrRejectManpowerNeedStudy(manpowerNeedData, true);
	    manpowerNeedStudiesData.remove(manpowerNeedData);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    String regionDesc = getMessage("label_all");
	    if (regionId != FlagsEnum.ALL.getCode())
		regionDesc = CommonService.getRegionById(regionId).getDescription();

	    byte[] bytes = ManpowerNeedsService.getManpowerNeedsStudiesReportsBytes(reportType, selectedStudy.getId(), selectedStudy.getNeedType(), selectedStudy.getTransactionDate(), categoryId, regionId, regionDesc, selectedMinorSpecsIds, selectedMinorSpecsDescriptions);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    public Date getStudyDateFrom() {
	return studyDateFrom;
    }

    public void setStudyDateFrom(Date studyDateFrom) {
	this.studyDateFrom = studyDateFrom;
    }

    public Date getStudyDateTo() {
	return studyDateTo;
    }

    public void setStudyDateTo(Date studyDateTo) {
	this.studyDateTo = studyDateTo;
    }

    public List<ManpowerNeedData> getManpowerNeedStudiesData() {
	return manpowerNeedStudiesData;
    }

    public void setManpowerNeedStudiesData(List<ManpowerNeedData> manpowerNeedStudiesData) {
	this.manpowerNeedStudiesData = manpowerNeedStudiesData;
    }

    public ManpowerNeedData getSelectedStudy() {
	return selectedStudy;
    }

    public void setSelectedStudy(ManpowerNeedData selectedStudy) {
	this.selectedStudy = selectedStudy;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public List<Region> getRegionsList() {
	return regionsList;
    }

    public void setRegionsList(List<Region> regionsList) {
	this.regionsList = regionsList;
    }

    public long getRegionId() {
	return regionId;
    }

    public void setRegionId(long regionId) {
	this.regionId = regionId;
    }

    public String getSelectedMinorSpecsIds() {
	return selectedMinorSpecsIds;
    }

    public void setSelectedMinorSpecsIds(String selectedMinorSpecsIds) {
	this.selectedMinorSpecsIds = selectedMinorSpecsIds;
    }

    public String getSelectedMinorSpecsDescriptions() {
	return selectedMinorSpecsDescriptions;
    }

    public void setSelectedMinorSpecsDescriptions(String selectedMinorSpecsDescriptions) {
	this.selectedMinorSpecsDescriptions = selectedMinorSpecsDescriptions;
    }

    public int getPageSize() {
	return pageSize;
    }
}
