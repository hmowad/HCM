package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.List;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.promotions.PromotionReportData;
import com.code.dal.orm.hcm.promotions.PromotionReportDetailData;
import com.code.enums.FlagsEnum;
import com.code.enums.PromotionReportStatusEnum;
import com.code.enums.PromotionsTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PromotionsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.PromotionsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

public abstract class PromotionsBase extends WFBaseBacking {

    protected PromotionReportData promotionReportData;
    protected List<PromotionReportDetailData> promotionReportDetailDataList;
    protected PromotionReportDetailData selectedPromotionReportDetailData;

    protected List<Rank> ranks;

    protected String detailSearchEmpName;
    protected Long detailSearchStatus;
    protected long empId;

    protected final int pageSize = 10;
    protected Long oldPromotionReportDetailStatus;

    protected void initialize(Long categoryId) throws BusinessException {

	promotionReportData = new PromotionReportData();
	promotionReportData.setStatus(PromotionReportStatusEnum.CURRENT.getCode());
	promotionReportData.setPromotionTypeId(PromotionsTypesEnum.NORMAL_PROMOTION.getCode());
	promotionReportData.setCategoryId(categoryId);
	promotionReportData.setReportDate(HijriDateService.getHijriSysDate());
	promotionReportData.setDueDate(HijriDateService.getHijriSysDate());

	promotionReportDetailDataList = new ArrayList<PromotionReportDetailData>();
	selectedPromotionReportDetailData = null;
    }

    public void addPromotionReportAndReportDetails() {
	try {
	    PromotionsService.addPromotionReportAndReportDetails(promotionReportData, promotionReportDetailDataList, loginEmpData.getEmpId());

	    if (promotionReportDetailDataList != null && promotionReportDetailDataList.size() > 0)
		selectReportDetail(promotionReportDetailDataList.get(0));
	    setServerSideSuccessMessages(getMessage("notify_promotionReportAddModifySuccess"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    // TODO: Used when changing report status or adding attachments
    public void addModifyReportData() {
	try {
	    PromotionsService.addModifyPromotionReport(promotionReportData, loginEmpData.getEmpId());
	    setServerSideSuccessMessages(getMessage("notify_promotionReportAddModifySuccess"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void addPromotionReportDetail() {
	try {
	    if (empId != FlagsEnum.ALL.getCode()) {
		selectedPromotionReportDetailData = PromotionsWorkFlow.addPromotionReportDetail(instance, empId, promotionReportData, loginEmpData.getEmpId());

		promotionReportDetailDataList.add(selectedPromotionReportDetailData);

		this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    }
	} catch (BusinessException e) {
	    empId = FlagsEnum.ALL.getCode();
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	} catch (Exception e) {
	    empId = FlagsEnum.ALL.getCode();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void selectReportDetail(PromotionReportDetailData reportDetailData) {
	this.selectedPromotionReportDetailData = reportDetailData;
	this.oldPromotionReportDetailStatus = selectedPromotionReportDetailData.getStatus();
    }

    public void deletePromotionReportDetailData(PromotionReportDetailData promotionReportDetailData) {
	try {

	    PromotionsWorkFlow.deletePromotionReportDetail(instance, promotionReportDetailData, promotionReportDetailDataList, this.loginEmpData.getEmpId(), false);

	    if (selectedPromotionReportDetailData != null && promotionReportDetailData.equals(selectedPromotionReportDetailData))
		selectedPromotionReportDetailData = null;

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public PromotionReportData getPromotionReportData() {
	return promotionReportData;
    }

    public void setPromotionReportData(PromotionReportData promotionReportData) {
	this.promotionReportData = promotionReportData;
    }

    public List<PromotionReportDetailData> getPromotionReportDetailDataList() {
	return promotionReportDetailDataList;
    }

    public void setPromotionReportDetailDataList(List<PromotionReportDetailData> promotionReportDetailDataList) {
	this.promotionReportDetailDataList = promotionReportDetailDataList;
    }

    public String getDetailSearchEmpName() {
	return detailSearchEmpName;
    }

    public void setDetailSearchEmpName(String detailSearchEmpName) {
	this.detailSearchEmpName = detailSearchEmpName;
    }

    public Long getDetailSearchStatus() {
	return detailSearchStatus;
    }

    public void setDetailSearchStatus(Long detailSearchStatus) {
	this.detailSearchStatus = detailSearchStatus;
    }

    public PromotionReportDetailData getSelectedPromotionReportDetailData() {
	return selectedPromotionReportDetailData;
    }

    public void setSelectedPromotionReportDetailData(PromotionReportDetailData selectedPromotionReportDetailData) {
	this.selectedPromotionReportDetailData = selectedPromotionReportDetailData;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public long getEmpId() {
	return empId;
    }

    public void setEmpId(long empId) {
	this.empId = empId;
    }

    public int getPageSize() {
	return pageSize;
    }

}
