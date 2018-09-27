package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.promotions.PromotionReportData;
import com.code.dal.orm.hcm.promotions.PromotionReportDetailData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.PromotionReportStatusEnum;
import com.code.enums.PromotionsTypesEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PromotionsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

@ManagedBean(name = "promotionSoldiersCollectiveReport")
@ViewScoped
public class PromotionSoldiersCollectiveReport extends PromotionsBase {

    private int pageSize = 10;

    private Date reportDate;
    private String reportDateString;
    private Date promotionDate;
    private String promotionDateString;
    private Date promotionDueDate;
    private String promotionDueDateString;

    private String ranksNamesString;
    private String ranksIdsString;
    private List<Long> ranksIds;

    private String regionsNamesString;
    private String regionsIdsString;
    private List<Long> regionsIds;

    private Boolean scaleUpFlagBoolean;

    private List<PromotionReportData> promotionSoldiersCollectiveReports;

    public PromotionSoldiersCollectiveReport() {
	super.init();
	try {
	    ranksIds = new ArrayList<Long>();
	    regionsIds = new ArrayList<Long>();
	    scaleUpFlagBoolean = false;

	    reportDate = HijriDateService.getHijriSysDate();
	    promotionDate = HijriDateService.getHijriSysDate();
	    promotionDueDate = HijriDateService.getHijriSysDate();

	    promotionSoldiersCollectiveReports = new ArrayList<PromotionReportData>();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void toggleAllPromotionSoldiersReportsScaleUpFlag() {
	scaleUpFlagBoolean = !scaleUpFlagBoolean;
    }

    public void togglePomotionSoldiersReportScaleUpFlag(PromotionReportData promotionSoldierReport) {
	promotionSoldierReport.setScaleUpFlagBoolean(!promotionSoldierReport.getScaleUpFlagBoolean());
    }

    public void deleteAllPromotionSoldiersReports() {
	regionsIds.clear();
	ranksIds.clear();
	promotionSoldiersCollectiveReports.clear();
    }

    private void setupRegionsIds() {
	String[] regionIdsStrings = regionsIdsString.split(",");
	Boolean allRegionsIdSelected = false;
	Boolean generalDirectorateOfBorderGuardsIdSelected = false;

	for (int i = 0; i < regionIdsStrings.length; i++) {
	    regionsIds.add(new Long(regionIdsStrings[i]));
	    if (new Long(regionIdsStrings[i]) == FlagsEnum.ALL.getCode())
		allRegionsIdSelected = true;
	    if (new Long(regionIdsStrings[i]) == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
		generalDirectorateOfBorderGuardsIdSelected = true;
	}

	if (allRegionsIdSelected) {
	    List<Region> allRegions = CommonService.getAllRegions();
	    if (!generalDirectorateOfBorderGuardsIdSelected)
		allRegions.remove(CommonService.getRegionById(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()));

	    regionsIds.clear();
	    for (Region region : allRegions)
		regionsIds.add(region.getId());
	}
    }

    private void setupRanksIds() {
	String[] ranksIdsStrings = ranksIdsString.split(",");
	for (int i = 0; i < ranksIdsStrings.length; i++) {
	    if (new Long(ranksIdsStrings[i]) == RanksEnum.STUDENT_SOLDIER.getCode())
		continue;
	    ranksIds.add(new Long(ranksIdsStrings[i]));
	}
    }

    public void constructPromotionSoldiersReportData(PromotionReportData promotionReportData, Long regionId, Long rankId) {
	promotionReportData.setReportDate(reportDate);
	promotionReportData.setPromotionDate(promotionDate);
	promotionReportData.setDueDate(promotionDueDate);
	promotionReportData.setCategoryId(CategoriesEnum.SOLDIERS.getCode());
	promotionReportData.setScaleUpFlagBoolean(scaleUpFlagBoolean);
	promotionReportData.setScaleUpFlag(scaleUpFlagBoolean ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	promotionReportData.setRegionId(regionId);
	promotionReportData.setRegionDesc(CommonService.getRegionById(regionId).getDescription());
	promotionReportData.setRankId(rankId);
	promotionReportData.setRankDesc(CommonService.getRankById(rankId).getDescription());
	promotionReportData.setStatus(PromotionReportStatusEnum.CURRENT.getCode());
	promotionReportData.setPromotionTypeId(PromotionsTypesEnum.NORMAL_PROMOTION.getCode());
    }

    public void addAllPromotionSoldiersReports() {
	reportDateString = HijriDateService.getHijriDateString(reportDate);
	promotionDateString = HijriDateService.getHijriDateString(promotionDate);
	promotionDueDateString = HijriDateService.getHijriDateString(promotionDueDate);

	setupRanksIds();
	setupRegionsIds();

	for (Long regionId : regionsIds) {
	    for (Long rankId : ranksIds) {
		PromotionReportData promotionReportData = new PromotionReportData();
		constructPromotionSoldiersReportData(promotionReportData, regionId, rankId);
		promotionSoldiersCollectiveReports.add(promotionReportData);
	    }
	}
	regionsIds.clear();
	ranksIds.clear();
    }

    public void deletePromotionSoldiersReport(PromotionReportData promotionSoldierReport) {
	promotionSoldiersCollectiveReports.remove(promotionSoldierReport);
    }

    public void saveAllPromotionSoldiersReports() {
	try {
	    Set<String> reportNumbers = new HashSet<String>();
	    int reportNumbersSetSize = 0;
	    for (PromotionReportData promotionSoldierReport : promotionSoldiersCollectiveReports) {
		reportNumbers.add(promotionSoldierReport.getReportNumber());
		reportNumbersSetSize = reportNumbersSetSize + 1;
		if (reportNumbersSetSize > reportNumbers.size()) {
		    reportNumbersSetSize = 0;
		    throw new BusinessException("error_promotionReportSaveError", new String[] { promotionSoldierReport.getReportNumber() });
		}
	    }

	    for (PromotionReportData promotionReportData : promotionSoldiersCollectiveReports) {
		List<PromotionReportDetailData> promotionReportDetailData = new ArrayList<PromotionReportDetailData>();
		PromotionsService.addPromotionReportAndReportDetails(promotionReportData, promotionReportDetailData, loginEmpData.getEmpId());
	    }
	    setServerSideSuccessMessages(getParameterizedMessage("notify_promotionSoldiersCollectiveReportsSuccess", promotionSoldiersCollectiveReports.size()));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public Date getReportDate() {
	return reportDate;
    }

    public void setReportDate(Date reportDate) {
	this.reportDate = reportDate;
    }

    public Date getPromotionDate() {
	return promotionDate;
    }

    public void setPromotionDate(Date promotionDate) {
	this.promotionDate = promotionDate;
    }

    public Date getPromotionDueDate() {
	return promotionDueDate;
    }

    public void setPromotionDueDate(Date promotionDueDate) {
	this.promotionDueDate = promotionDueDate;
    }

    public Boolean getScaleUpFlagBoolean() {
	return scaleUpFlagBoolean;
    }

    public void setScaleUpFlagBoolean(Boolean scalableFlag) {
	this.scaleUpFlagBoolean = scalableFlag;
    }

    public String getRegionsNamesString() {
	return regionsNamesString;
    }

    public void setRegionsNamesString(String regionsNames) {
	this.regionsNamesString = regionsNames;
    }

    public String getRegionsIdsString() {
	return regionsIdsString;
    }

    public void setRegionsIdsString(String regionsCodesString) {
	this.regionsIdsString = regionsCodesString;
    }

    public String getRanksNamesString() {
	return ranksNamesString;
    }

    public void setRanksNamesString(String ranksNames) {
	this.ranksNamesString = ranksNames;
    }

    public String getRanksIdsString() {
	return ranksIdsString;
    }

    public void setRanksIdsString(String ranksCodesString) {
	this.ranksIdsString = ranksCodesString;
    }

    public List<PromotionReportData> getPromotionSoldiersCollectiveReports() {
	return promotionSoldiersCollectiveReports;
    }

    public void setPromotionSoldiersCollectiveReports(List<PromotionReportData> promotionSoldiersCollectiveReports) {
	this.promotionSoldiersCollectiveReports = promotionSoldiersCollectiveReports;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public List<Long> getRanksIds() {
	return ranksIds;
    }

    public void setRanksIds(List<Long> ranksIds) {
	this.ranksIds = ranksIds;
    }

    public List<Long> getRegionsIds() {
	return regionsIds;
    }

    public void setRegionsIds(List<Long> regionsIds) {
	this.regionsIds = regionsIds;
    }

    public String getReportDateString() {
	return reportDateString;
    }

    public void setReportDateString(String reportDateString) {
	this.reportDateString = reportDateString;
    }

    public String getPromotionDateString() {
	return promotionDateString;
    }

    public void setPromotionDateString(String promotionDateString) {
	this.promotionDateString = promotionDateString;
    }

    public String getPromotionDueDateString() {
	return promotionDueDateString;
    }

    public void setPromotionDueDateString(String promotionDueDateString) {
	this.promotionDueDateString = promotionDueDateString;
    }

}