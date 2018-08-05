package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.RankTitle;
import com.code.dal.orm.hcm.promotions.PromotionReportDetailData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.PromotionCandidateStatusEnum;
import com.code.enums.PromotionReportStatusEnum;
import com.code.enums.RanksEnum;
import com.code.enums.ReportOutputFormatsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PromotionsService;
import com.code.services.security.SecurityService;
import com.code.services.util.AttachmentsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

@ManagedBean(name = "promotionOfficersReport")
@ViewScoped
public class PromotionOfficersReport extends PromotionsBase {

    private List<RankTitle> rankTitles;
    private boolean brigadierPromoteAuthorize;

    private List<PromotionReportDetailData> collectivePromotionReportDetailDataList;

    private String collectiveOfficersDecisionNumber;
    private Date collectiveOfficersDecisionDate;

    public PromotionOfficersReport() {
	this.init();
    }

    public void init() {
	try {
	    empId = FlagsEnum.ALL.getCode();
	    collectivePromotionReportDetailDataList = new ArrayList<PromotionReportDetailData>();

	    Long reportId = getRequest().getParameter("reportId") == null ? null : Long.parseLong(getRequest().getParameter("reportId"));

	    if (reportId != null) {
		promotionReportData = PromotionsService.getPromotionReportDataById(reportId);
		promotionReportDetailDataList = PromotionsService.getPromotionReportDetailsDataByReportId(reportId);
	    } else {
		super.initialize(CategoriesEnum.OFFICERS.getCode());
	    }

	    ranks = PromotionsService.getPromotionReportRanksByCategoryId(CategoriesEnum.OFFICERS.getCode());
	    rankTitles = CommonService.getAllRanksTitles();

	    setBrigadierPromoteAuthorize(SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.PRM_OFFICERS_REPORT.getCode(), MenuActionsEnum.PRM_OFFICER_REPORT_BRIGADIER_PROMOTE.getCode()));

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    @Override
    public void addModifyReportData() {
	super.addModifyReportData();

	if (promotionReportData.getStatus().equals(PromotionReportStatusEnum.CLOSED.getCode())) {
	    collectivePromotionReportDetailDataList = new ArrayList<PromotionReportDetailData>();
	    collectiveOfficersDecisionNumber = null;
	    collectiveOfficersDecisionDate = null;
	}
    }

    public void addAttachments() {
	try {
	    promotionReportData.setAttachments(AttachmentsService.getNextAttachmentsId());
	    PromotionsService.addModifyPromotionReport(promotionReportData, loginEmpData.getEmpId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchPromotionDetails() {
	try {

	    promotionReportDetailDataList = PromotionsService.getPromotionReportDetailsData(promotionReportData.getId(), FlagsEnum.ALL.getCode(), detailSearchEmpName, detailSearchStatus == FlagsEnum.ALL.getCode() ? null : new Long[] { detailSearchStatus });
	    if (promotionReportDetailDataList.size() > 0)
		selectReportDetail(promotionReportDetailDataList.get(0));
	    else
		this.selectedPromotionReportDetailData = null;

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    @Override
    public void deletePromotionReportDetailData(PromotionReportDetailData promotionReportDetailData) {
	try {
	    PromotionsService.deletePromotionOfficersReportDetail(promotionReportData, promotionReportDetailData, promotionReportDetailDataList, this.loginEmpData.getEmpId());
	    if (selectedPromotionReportDetailData != null && promotionReportDetailData.equals(selectedPromotionReportDetailData)) {
		if (promotionReportDetailDataList.size() > 0)
		    selectReportDetail(promotionReportDetailDataList.get(0));
		else
		    selectedPromotionReportDetailData = null;
	    }

	    if (promotionReportDetailData.getSelected())
		collectivePromotionReportDetailDataList.remove(promotionReportDetailData);

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    // TODO: When searching in the ui, we will query all the details except (all of collective details), and then we will addAll() collective to the returned result from query, so that we solve the problem of difference of references between ui and query result
    public void checkUncheckRow(PromotionReportDetailData promotionReportDetailData) {
	// Add to collective List
	if (promotionReportDetailData.getSelected()) {
	    collectivePromotionReportDetailDataList.add(promotionReportDetailData);
	    selectedPromotionReportDetailData = null;
	} else // Remove from collected list
	    collectivePromotionReportDetailDataList.remove(promotionReportDetailData);
    }

    public void saveCollectivePromotionReportDetails() {

	try {
	    handleBrigadierPromoteAuthorize(true);
	    PromotionsService.addModifyOfficersCollectiveReportDetails(promotionReportData, collectivePromotionReportDetailDataList, collectiveOfficersDecisionNumber, collectiveOfficersDecisionDate, this.loginEmpData.getEmpId());

	    collectivePromotionReportDetailDataList = new ArrayList<PromotionReportDetailData>();
	    collectiveOfficersDecisionNumber = null;
	    collectiveOfficersDecisionDate = null;
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void savePromotionReportDetail() {
	try {
	    handleBrigadierPromoteAuthorize(false);

	    List<PromotionReportDetailData> tempPromotionReportDetailDataList = new ArrayList<PromotionReportDetailData>();
	    tempPromotionReportDetailDataList.add(selectedPromotionReportDetailData);
	    PromotionsService.addModifyOfficersReportDetails(promotionReportData, tempPromotionReportDetailDataList, this.loginEmpData.getEmpId());

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));

	} catch (BusinessException e) {
	    selectedPromotionReportDetailData.setStatus(PromotionCandidateStatusEnum.CANDIDATE.getCode());
	    resetSelectedPromotionReportDetail();
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void handleBrigadierPromoteAuthorize(boolean collectiveFlag) throws BusinessException {
	if (collectiveFlag || this.selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.ROYAL_OREDER_ISSUED.getCode()))
	    if (this.promotionReportData.getRankId().equals(RanksEnum.BRIGADIER.getCode()) && !this.brigadierPromoteAuthorize)
		throw new BusinessException("error_notAuthorized");
    }

    public void resetSelectedPromotionReportDetail() {
	if (selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE.getCode()) || selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode())) {
	    selectedPromotionReportDetailData.setExternalDecisionNumber(null);
	    selectedPromotionReportDetailData.setExternalDecisionDate(null);
	    selectedPromotionReportDetailData.setExternalDecisionDateString(null);
	    if (promotionReportData.getRankId() == RanksEnum.PRIME_SERGEANTS.getCode()) {
		selectedPromotionReportDetailData.setMilitaryNumber(null);
		if (selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode())) {
		    selectedPromotionReportDetailData.setNewJobCode(null);
		    selectedPromotionReportDetailData.setNewJobId(null);
		    selectedPromotionReportDetailData.setNewJobDesc(null);
		}
	    }
	}
    }

    public String getPrimeSergantFourtyAgeDate() {
	if (selectedPromotionReportDetailData.getBirthDate() == null)
	    return getMessage("error_ageNotAllowed");

	String date = HijriDateService.getHijriDateString(selectedPromotionReportDetailData.getBirthDate());
	int year = Integer.parseInt(date.substring(6, 10)) + 40;
	String month = date.substring(3, 5);
	String day = date.substring(0, 2);
	String fourtyAgeDate = day + "/" + month + "/" + year;
	return fourtyAgeDate;
    }

    public void printPromotionReport() {

	try {
	    if (promotionReportData != null && promotionReportData.getId() != null) {
		byte[] bytes = PromotionsService.getDraftPromotionBytes(promotionReportData, ReportOutputFormatsEnum.PDF.getCode());
		super.print(bytes);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printPrimeSergentsShow() {
	try {

	    byte[] bytes = PromotionsService.getPrimeSergentsShowBytes(promotionReportData.getId(), this.selectedPromotionReportDetailData.getEmpId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<RankTitle> getRankTitles() {
	return rankTitles;
    }

    public boolean isBrigadierPromoteAuthorize() {
	return brigadierPromoteAuthorize;
    }

    public void setBrigadierPromoteAuthorize(boolean brigadierPromoteAuthorize) {
	this.brigadierPromoteAuthorize = brigadierPromoteAuthorize;
    }

    public List<PromotionReportDetailData> getCollectivePromotionReportDetailDataList() {
	return collectivePromotionReportDetailDataList;
    }

    public void setCollectivePromotionReportDetailDataList(List<PromotionReportDetailData> collectivePromotionReportDetailDataList) {
	this.collectivePromotionReportDetailDataList = collectivePromotionReportDetailDataList;
    }

    public String getCollectiveOfficersDecisionNumber() {
	return collectiveOfficersDecisionNumber;
    }

    public void setCollectiveOfficersDecisionNumber(String collectiveOfficersDecisionNumber) {
	this.collectiveOfficersDecisionNumber = collectiveOfficersDecisionNumber;
    }

    public Date getCollectiveOfficersDecisionDate() {
	return collectiveOfficersDecisionDate;
    }

    public void setCollectiveOfficersDecisionDate(Date collectiveOfficersDecisionDate) {
	this.collectiveOfficersDecisionDate = collectiveOfficersDecisionDate;
    }

}
