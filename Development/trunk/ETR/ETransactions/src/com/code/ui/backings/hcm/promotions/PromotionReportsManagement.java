package com.code.ui.backings.hcm.promotions;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.promotions.PromotionReportData;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.PromotionReportStatusEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.ReportOutputFormatsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PromotionsService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "promotionReportsManagement")
@ViewScoped
public class PromotionReportsManagement extends BaseBacking {

    private int mode;
    private Date reportDate;
    private String reportNumber;
    private Date dueDate;
    private Long rankId;
    private Long regionId;
    private Long status;
    private Long empId;
    private String empName;
    private String decisionNumber;
    private Date decisionDate;
    private boolean allSubmittedOfficersFlag;

    private List<Rank> ranks;
    private List<Region> regions;

    private List<PromotionReportData> promotionsReports;

    private final int pageSize = 10;

    public PromotionReportsManagement() {
	this.init();
    }

    public void init() {
	resetForm();
	if (getRequest().getParameter("mode") != null || getRequest().getAttribute("mode") != null) {
	    mode = getRequest().getParameter("mode") != null ? Integer.parseInt(getRequest().getParameter("mode")) : Integer.parseInt(getRequest().getAttribute("mode").toString());

	    ranks = PromotionsService.getPromotionReportRanksByCategoryId(mode);
	    if (mode == CategoryModesEnum.OFFICERS.getCode())
		setScreenTitle(getMessage("label_promotionOfficersReports"));
	    else if (mode == CategoryModesEnum.SOLDIERS.getCode())
		setScreenTitle(getMessage("label_promotionSoldiersReports"));
	    else if (mode == CategoryModesEnum.CIVILIANS.getCode())
		setScreenTitle(getMessage("label_promotionEmployeeReports"));
	    else
		setServerSideErrorMessages(getMessage("error_URLError"));
	} else {
	    setServerSideErrorMessages(getMessage("error_URLError"));
	}

	if (mode == CategoryModesEnum.SOLDIERS.getCode())
	    regions = CommonService.getAllRegions();
    }

    public void resetForm() {
	reportDate = null;
	reportNumber = null;
	dueDate = null;
	rankId = (long) FlagsEnum.ALL.getCode();

	if (loginEmpData.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()))
	    regionId = new Long(FlagsEnum.ALL.getCode());
	else
	    regionId = new Long(loginEmpData.getPhysicalRegionId());

	status = PromotionReportStatusEnum.CURRENT.getCode();
	empId = (long) FlagsEnum.ALL.getCode();
	empName = "";
	decisionDate = null;
	decisionNumber = null;

	allSubmittedOfficersFlag = false;
	promotionsReports = null;
    }

    public void search() {
	// long officialRegionId = this.loginEmpData.getOfficialRegionId();
	promotionsReports = null;

	try {
	    // TODO: exclude exceptional promotions from this query
	    this.promotionsReports = PromotionsService.getPromotionsReportsData(reportNumber, reportDate, dueDate, rankId, status, decisionNumber, decisionDate, empId, allSubmittedOfficersFlag, mode, regionId);

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deletePromotionReport(PromotionReportData promotionReportData) {
	try {

	    PromotionsService.deletePromotionReport(promotionReportData, this.loginEmpData.getEmpId().intValue());
	    // if we reached this line, this means promotionReport has been successfully deleted !
	    promotionsReports.remove(promotionReportData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printPromotionReport(PromotionReportData promotionReportData) {
	try {
	    byte[] bytes = PromotionsService.getDraftPromotionBytes(promotionReportData, ReportOutputFormatsEnum.PDF.getCode());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public Date getReportDate() {
	return reportDate;
    }

    public void setReportDate(Date reportDate) {
	this.reportDate = reportDate;
    }

    public String getReportNumber() {
	return reportNumber;
    }

    public void setReportNumber(String reportNumber) {
	this.reportNumber = reportNumber;
    }

    public Date getDueDate() {
	return dueDate;
    }

    public void setDueDate(Date dueDate) {
	this.dueDate = dueDate;
    }

    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    public Long getStatus() {
	return status;
    }

    public void setStatus(Long status) {
	this.status = status;
    }

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
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

    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public void setRanks(List<Rank> ranks) {
	this.ranks = ranks;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }

    public List<PromotionReportData> getPromotionsReports() {
	return promotionsReports;
    }

    public void setPromotionsReports(List<PromotionReportData> promotionsReports) {
	this.promotionsReports = promotionsReports;
    }

    public boolean isAllSubmittedOfficersFlag() {
	return allSubmittedOfficersFlag;
    }

    public void setAllSubmittedOfficersFlag(boolean allSubmittedOfficersFlag) {
	this.allSubmittedOfficersFlag = allSubmittedOfficersFlag;
    }

    public int getPageSize() {
	return pageSize;
    }

}
