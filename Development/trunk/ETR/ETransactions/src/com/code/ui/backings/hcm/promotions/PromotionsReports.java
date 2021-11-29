package com.code.ui.backings.hcm.promotions;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PromotionsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "promotionsReports")
@ViewScoped
public class PromotionsReports extends BaseBacking {

    private int mode;
    private int reportType;
    private List<Rank> ranks;
    private List<Region> regions;
    private long rankId;
    private long regionId;
    private Date promotionDueDateFrom;
    private Date promotionDueDateTo;
    private Date decisionDateFrom;
    private Date decisionDateTo;
    private Date promotionDate;
    private Boolean scaleUpFlag;
    private Long unitId;
    private Long minorSpecsId;
    private String decisionNumber;

    public PromotionsReports() {
	init();
    }

    // init all mandatory variables to run PromotionsReports inquiry(ranks, regions according to mode)
    public void init() {
	try {

	    if (getRequest().getParameter("mode") != null || getRequest().getAttribute("mode") != null) {
		mode = getRequest().getParameter("mode") != null ? Integer.parseInt(getRequest().getParameter("mode")) : Integer.parseInt(getRequest().getAttribute("mode").toString());

		if (mode == CategoryModesEnum.OFFICERS.getCode())
		    setScreenTitle(getMessage("title_officersPromotionsReports"));
		else if (mode == CategoryModesEnum.SOLDIERS.getCode())
		    setScreenTitle(getMessage("title_soldiersPromotionsReports"));
		else if (mode == CategoryModesEnum.CIVILIANS.getCode())
		    setScreenTitle(getMessage("title_personsPromotionsReports"));
		else
		    setServerSideErrorMessages(getMessage("error_URLError"));

		reportType = 10;
		ranks = PromotionsService.getPromotionsRanksForReports(mode, reportType);
		regions = CommonService.getAllRegions();
		resetForm();

	    } else {
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // reset form to make a new search
    public void resetForm() {
	try {
	    rankId = (long) FlagsEnum.ALL.getCode();
	    regionId = getLoginEmpPhysicalRegionFlag(true);
	    unitId = null;
	    minorSpecsId = null;
	    promotionDueDateFrom = null;
	    promotionDueDateTo = HijriDateService.getHijriSysDate();
	    decisionDateFrom = null;
	    decisionDateTo = HijriDateService.getHijriSysDate();
	    scaleUpFlag = false;
	    decisionNumber = null;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void changeReportType() {
	try {
	    if (mode == CategoryModesEnum.OFFICERS.getCode())
		ranks = PromotionsService.getPromotionsRanksForReports(mode, reportType);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void scaleUpJobs() {
	scaleUpFlag = !scaleUpFlag;
    }

    // the main print button used to print PROMOTION_ELIGIBLE_INQUIRY report
    public void printPromotionsReport() {
	try {
	    if (reportType == 10 && promotionDueDateFrom != null && promotionDueDateFrom.after(promotionDueDateTo)) {
		setServerSideErrorMessages(getMessage("error_fromPromotionDueDateViolation"));
		return;
	    }
	    if ((reportType == 20 || reportType == 30) && decisionDateFrom != null && decisionDateFrom.after(decisionDateTo)) {
		setServerSideErrorMessages(getMessage("error_fromDecisionDateViolation"));
		return;
	    }
	    byte[] bytes = PromotionsService.getPromotionsReport(reportType, (long) mode, rankId, regionId, promotionDueDateFrom, promotionDueDateTo, decisionDateFrom, decisionDateTo, scaleUpFlag, promotionDate, unitId == null ? FlagsEnum.ALL.getCode() : unitId, minorSpecsId == null ? FlagsEnum.ALL.getCode() : minorSpecsId, decisionNumber);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    /*
     * *********** Setters and Getters *************************
     */
    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
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

    public long getRankId() {
	return rankId;
    }

    public void setRankId(long rankId) {
	this.rankId = rankId;
    }

    public long getRegionId() {
	return regionId;
    }

    public void setRegionId(long regionId) {
	this.regionId = regionId;
    }

    public Date getPromotionDueDateFrom() {
	return promotionDueDateFrom;
    }

    public void setPromotionDueDateFrom(Date promotionDueDateFrom) {
	this.promotionDueDateFrom = promotionDueDateFrom;
    }

    public Date getPromotionDueDateTo() {
	return promotionDueDateTo;
    }

    public void setPromotionDueDateTo(Date promotionDueDateTo) {
	this.promotionDueDateTo = promotionDueDateTo;
    }

    public Date getDecisionDateFrom() {
	return decisionDateFrom;
    }

    public void setDecisionDateFrom(Date decisionDateFrom) {
	this.decisionDateFrom = decisionDateFrom;
    }

    public Date getDecisionDateTo() {
	return decisionDateTo;
    }

    public void setDecisionDateTo(Date decisionDateTo) {
	this.decisionDateTo = decisionDateTo;
    }

    public Date getPromotionDate() {
	return promotionDate;
    }

    public void setPromotionDate(Date promotionDate) {
	this.promotionDate = promotionDate;
    }

    public Boolean getScaleUpFlag() {
	return scaleUpFlag;
    }

    public void setScaleUpFlag(Boolean scaleUpFlag) {
	this.scaleUpFlag = scaleUpFlag;
    }

    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    public Long getMinorSpecsId() {
	return minorSpecsId;
    }

    public void setMinorSpecsId(Long minorSpecsId) {
	this.minorSpecsId = minorSpecsId;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

}
