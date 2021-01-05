package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.promotions.PromotionReportDetailData;
import com.code.dal.orm.hcm.promotions.PromotionTransactionData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.PromotionCandidateStatusEnum;
import com.code.enums.PromotionMedicalTestStatusEnum;
import com.code.enums.PromotionReportStatusEnum;
import com.code.enums.ReportOutputFormatsEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PromotionsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.PromotionsWorkFlow;

@SuppressWarnings("serial")
@ManagedBean(name = "promotionSoldiersReport")
@ViewScoped
public class PromotionSoldiersReport extends PromotionsBase {

    private List<Region> regions;
    private Integer enrollNumber;
    private String medicalTestExemptionReason;
    private List<EmployeeData> internalCopies;

    private boolean statusChangedToNonCandidate = false;
    private boolean statusChangedToCandidate = false;

    private List<String> detailSearchStatusesList;
    private Integer medicalTest;
    private int reportType;
    private final static int DRAFT_PROMOTION = 1;
    private final static int DRAFT_PROMOTION_CANDIDATES_BY_DIFFERENTIATION = 2;

    public PromotionSoldiersReport() {
	this.init();
    }

    public void init() {
	super.init();
	try {
	    reportType = DRAFT_PROMOTION;
	    empId = FlagsEnum.ALL.getCode();
	    medicalTest = FlagsEnum.ALL.getCode();
	    this.processId = WFProcessesEnum.SOLDIERS_PROMOTION.getCode();

	    Long reportId = null;
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode()))
		reportId = PromotionsWorkFlow.getWFPromotionByInstanceId(instance.getInstanceId()).getReportId();
	    else
		reportId = getRequest().getParameter("reportId") == null ? null : Long.parseLong(getRequest().getParameter("reportId"));

	    if (reportId != null) {
		promotionReportData = PromotionsService.getPromotionReportDataById(reportId);
		internalCopies = EmployeesService.getEmployeesByIdsString(promotionReportData.getInternalCopies());
	    } else {
		super.initialize(CategoriesEnum.SOLDIERS.getCode());
		promotionReportData.setRegionId(this.loginEmpData.getOfficialRegionId());
		promotionReportData.setScaleUpFlagBoolean(false);
		promotionReportData.setReportDate(HijriDateService.getHijriSysDate());
		internalCopies = new ArrayList<EmployeeData>();
	    }
	    adjustDetailSearchStatusesList();
	    searchPromotionDetails();

	    ranks = PromotionsService.getPromotionReportRanksByCategoryId(CategoriesEnum.SOLDIERS.getCode());
	    regions = CommonService.getAllRegions();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    @Override
    public void addPromotionReportAndReportDetails() {

	try {
	    PromotionsService.addPromotionReportAndReportDetails(promotionReportData, promotionReportDetailDataList, loginEmpData.getEmpId());
	    searchPromotionDetails();

	    setServerSideSuccessMessages(getMessage("notify_promotionReportAddModifySuccess"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    @Override
    public void addPromotionReportDetail() {
	try {
	    if (empId != FlagsEnum.ALL.getCode()) {
		selectedPromotionReportDetailData = PromotionsWorkFlow.addPromotionReportDetail(instance, empId, promotionReportData, loginEmpData.getEmpId());
		PromotionsService.modifyReportDetailsDrugTestResult(Arrays.asList(selectedPromotionReportDetailData));
		promotionReportDetailDataList.add(selectedPromotionReportDetailData);

		this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    }
	} catch (BusinessException e) {
	    empId = FlagsEnum.ALL.getCode();
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void scaleUpJobs() {
	try {
	    if (promotionReportData.getId() != null) {
		clearSearch();
		searchPromotionDetails();
		String detailsModifiedMessage = PromotionsService.scaleUpPromotionDetailsJobs(promotionReportData, promotionReportDetailDataList, loginEmpData.getEmpId());

		if (detailsModifiedMessage != null)
		    this.setServerSideSuccessMessages(getMessage(detailsModifiedMessage));
	    } else {
		promotionReportData.setScaleUpFlagBoolean(!promotionReportData.getScaleUpFlagBoolean());
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    private void adjustDetailSearchStatusesList() {
	detailSearchStatusesList = new ArrayList<String>();
	if (promotionReportData.getStatus().equals(PromotionReportStatusEnum.CURRENT.getCode())) {
	    detailSearchStatusesList.add(PromotionCandidateStatusEnum.CANDIDATE.getCode() + "");
	    detailSearchStatusesList.add(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode() + "");
	} else if (promotionReportData.getStatus().equals(PromotionReportStatusEnum.UNDER_APPROVAL.getCode())) {
	    detailSearchStatusesList.add(PromotionCandidateStatusEnum.CANDIDATE.getCode() + "");
	} else if (promotionReportData.getStatus().equals(PromotionReportStatusEnum.CLOSED.getCode())) {
	    if ((role.equals(WFTaskRolesEnum.NOTIFICATION.getCode()) || role.equals(WFTaskRolesEnum.HISTORY.getCode())) && instanceApproved != null && instanceApproved == FlagsEnum.ON.getCode()) {
		detailSearchStatusesList.add(PromotionCandidateStatusEnum.PROMOTED.getCode() + "");
	    } else {
		detailSearchStatusesList.add(PromotionCandidateStatusEnum.CANDIDATE.getCode() + "");
		detailSearchStatusesList.add(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode() + "");
		detailSearchStatusesList.add(PromotionCandidateStatusEnum.PROMOTED.getCode() + "");
	    }
	}
    }

    public void searchPromotionDetails() {
	try {
	    if (detailSearchStatusesList != null && !detailSearchStatusesList.isEmpty() && promotionReportData.getId() != null) {
		Long[] detailSearchStatuses = new Long[detailSearchStatusesList.size()];
		for (int i = 0; i < detailSearchStatusesList.size(); i++)
		    detailSearchStatuses[i] = Long.parseLong(detailSearchStatusesList.get(i) + "");

		promotionReportDetailDataList = PromotionsService.getRankedPromotionReportDetailsDataByReportId(promotionReportData.getId(), detailSearchStatuses, medicalTest, detailSearchEmpName);

		if (promotionReportDetailDataList.size() > 0)
		    selectReportDetail(promotionReportDetailDataList.get(0));
		else
		    this.selectedPromotionReportDetailData = null;
	    } else {
		promotionReportDetailDataList = new ArrayList<PromotionReportDetailData>();
		this.selectedPromotionReportDetailData = null;
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void updatePromotionList() {
	detailSearchStatusesList = new ArrayList<String>();
	detailSearchStatusesList.add(PromotionCandidateStatusEnum.CANDIDATE.getCode() + "");
	detailSearchStatusesList.add(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode() + "");
	detailSearchStatusesList.add(PromotionCandidateStatusEnum.PROMOTED.getCode() + "");
	medicalTest = FlagsEnum.ALL.getCode();
	detailSearchEmpName = "";
	enrollNumber = null;
	medicalTestExemptionReason = "";

	searchPromotionDetails();
	if (promotionReportDetailDataList != null && promotionReportDetailDataList.size() != FlagsEnum.OFF.getCode())
	    this.setServerSideSuccessMessages(getMessage("notify_drugTestDataUpdatedSuccessfully"));
	else
	    this.setServerSideErrorMessages(getMessage("error_promotionNoSoldiersToUpdateDrugTest"));
    }

    public void handleReportDetailsFreezedJobs(boolean addFreezedJobsFlag) {
	try {
	    clearSearch();
	    searchPromotionDetails();

	    if (promotionReportDetailDataList.size() != 0)
		PromotionsService.handleReportDetailsFreezedJobs(promotionReportData, promotionReportDetailDataList, loginEmpData.getEmpId(), addFreezedJobsFlag);

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void enrollReportDetail() {
	try {
	    PromotionsWorkFlow.enrollSoldierPromotionReportDetails(instance, enrollNumber, promotionReportData, promotionReportDetailDataList, loginEmpData.getEmpId());
	    clearSearch();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void sendDrugsTestRequest() throws BusinessException {
	try {
	    PromotionsService.sendPromotionsSoldiersDrugsTestRequest(promotionReportData, this.loginEmpData.getEmpId());
	    searchPromotionDetails();
	    this.setServerSideSuccessMessages(getMessage("notify_drugTestRequestSentSuccessfully"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void savePromotionReportDetail() {

	try {
	    PromotionsWorkFlow.savePromotionReportDetail(instance, promotionReportData.getId(), selectedPromotionReportDetailData, promotionReportData.getCategoryId(), oldPromotionReportDetailStatus, selectedPromotionReportDetailData.getStatus(), this.loginEmpData.getEmpId());

	    // updatePromotionDetailsLists();
	    this.oldPromotionReportDetailStatus = selectedPromotionReportDetailData.getStatus();
	    if (statusChangedToCandidate) {
		statusChangedToCandidate = false;
		this.setServerSideSuccessMessages(getMessage("notify_savingNewSoldierInReport"));
	    } else {
		if (statusChangedToNonCandidate) {
		    statusChangedToNonCandidate = false;
		}
		this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }

    public void changeStatus() {

	if (selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode()))
	    statusChangedToNonCandidate = true;
	else
	    statusChangedToNonCandidate = false;

	if (selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE.getCode()))
	    statusChangedToCandidate = true;
	else
	    statusChangedToCandidate = false;

	try {
	    PromotionsService.handleScaleUpJob(selectedPromotionReportDetailData, statusChangedToNonCandidate ? false : promotionReportData.getScaleUpFlagBoolean());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void changeMedicalResults() {
	selectedPromotionReportDetailData.setMedicalTestExemptionReason(null);
    }

    public void updateCollectiveMedicalExemption() {

	try {
	    promotionReportDetailDataList = PromotionsService.updatePromotionSoldiersReportDetailsMedicalExemption(promotionReportData.getId(), medicalTestExemptionReason, this.loginEmpData.getEmpId());
	    clearSearch();
	    medicalTest = PromotionMedicalTestStatusEnum.EXEMPT.getCode();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    private void clearSearch() {
	detailSearchEmpName = "";
	detailSearchStatusesList.clear();
	detailSearchStatusesList.add(PromotionCandidateStatusEnum.CANDIDATE.getCode() + "");
	medicalTest = FlagsEnum.ALL.getCode();
	selectedPromotionReportDetailData = null;
    }

    public void deletePromotionReportDetailsDataBySearch() {
	try {
	    PromotionsWorkFlow.deletePromotionReportDetail(instance, null, promotionReportDetailDataList, this.loginEmpData.getEmpId(), true);
	    selectedPromotionReportDetailData = null;

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    /*
     * 
     * @Deprecated public void checkfileApproved() { this.selectedPromotionReportDetailData.setFileApprovedId(null); this.selectedPromotionReportDetailData.setFileApprovedName(null);
     * 
     * if (selectedPromotionReportDetailData.getFileDegree() == null) selectedPromotionReportDetailData.setFileDegree(0);
     * 
     * calculateTotalDegrees(); }
     * 
     * 
     * @Deprecated public void checkQualificationApproved() { this.selectedPromotionReportDetailData.setQualificationApprovedId(null); this.selectedPromotionReportDetailData.setQualificationApprovedName(null);
     * 
     * if (selectedPromotionReportDetailData.getQualificationDegree() == null) selectedPromotionReportDetailData.setQualificationDegree(0.0);
     * 
     * calculateTotalDegrees(); }
     * 
     * 
     * 
     * @Deprecated public void checkTrainingApproved() { this.selectedPromotionReportDetailData.setTrainingApprovedId(null); this.selectedPromotionReportDetailData.setTrainingApprovedName(null);
     * 
     * if (selectedPromotionReportDetailData.getTrainingDegree() == null) selectedPromotionReportDetailData.setTrainingDegree(0.0);
     * 
     * calculateTotalDegrees(); }
     * 
     * @Deprecated public void approveEducationDegree() { selectedPromotionReportDetailData.setQualificationApprovedId(loginEmpData.getEmpId()); selectedPromotionReportDetailData.setQualificationApprovedName(loginEmpData.getName()); savePromotionReportDetail(); }
     * 
     * @Deprecated public void approveFileDegree() { selectedPromotionReportDetailData.setFileApprovedId(loginEmpData.getEmpId()); selectedPromotionReportDetailData.setFileApprovedName(loginEmpData.getName()); savePromotionReportDetail();
     * 
     * }
     * 
     * @Deprecated public void approveTrainingDegree() { selectedPromotionReportDetailData.setTrainingApprovedId(loginEmpData.getEmpId()); selectedPromotionReportDetailData.setTrainingApprovedName(loginEmpData.getName()); savePromotionReportDetail(); }
     * 
     */

    // @Deprecated
    // public String sendToReview() {
    // try {
    //
    // if (promotionReportDetailDataList == null || promotionReportDetailDataList.size() == 0)
    // throw new BusinessException("error_NoOneToPromote");
    // promotionReportData.setStatus(PromotionReportStatusEnum.UNDER_REVIEW.getCode());
    // PromotionsService.addModifyPromotionReport(promotionReportData, this.loginEmpData.getEmpId());
    //
    // getRequest().setAttribute("mode", CategoriesEnum.SOLDIERS.getCode());
    // this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
    //
    // return NavigationEnum.REPORT_STATUS_REVIEW.toString();
    // } catch (BusinessException e) {
    // this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
    // return null;
    // }
    // }

    public String initProcess() {
	try {
	    List<PromotionReportDetailData> reportDetails = PromotionsService.getPromotionReportDetailsData(promotionReportData.getId(), FlagsEnum.ALL.getCode(), null, new Long[] { PromotionCandidateStatusEnum.CANDIDATE.getCode() });
	    PromotionsWorkFlow.initPromotion(requester, promotionReportData, reportDetails, processId, taskUrl, internalCopies);

	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approvePDM() {
	try {
	    PromotionsWorkFlow.doPromotionPDM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String modifyPDM() {
	try {
	    PromotionsWorkFlow.doPromotionPDM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectPDM() {
	try {
	    PromotionsWorkFlow.doPromotionPDM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveAOM() {
	try {
	    PromotionsWorkFlow.doPromotionAOM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String modifyAOM() {
	try {
	    PromotionsWorkFlow.doPromotionAOM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveRE() {
	try {
	    List<PromotionReportDetailData> reportDetails = PromotionsService.getPromotionReportDetailsData(promotionReportData.getId(), FlagsEnum.ALL.getCode(), null, new Long[] { PromotionCandidateStatusEnum.CANDIDATE.getCode() });
	    PromotionsWorkFlow.doPromotionRE(requester, promotionReportData, reportDetails, instance, currentTask, internalCopies, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectRE() {
	try {
	    PromotionsWorkFlow.doPromotionRE(requester, promotionReportData, null, instance, currentTask, internalCopies, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String signSM() {
	try {
	    PromotionsWorkFlow.doPromotionSM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String modifySM() {
	try {
	    PromotionsWorkFlow.doPromotionSM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectSM() {
	try {
	    PromotionsWorkFlow.doPromotionSM(requester, promotionReportData, instance, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public void print() {

	try {
	    PromotionTransactionData promotionTransaction = PromotionsService.getPromotionTransactionByDecisionNumberAndDecisionDate(promotionReportData.getDecisionNumber(), promotionReportData.getDecisionDate(), promotionReportData.getPromotionTypeId());
	    if (promotionTransaction != null) {
		byte[] bytes = PromotionsService.getPromotionBytes(promotionTransaction, null, null);
		super.print(bytes);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printPromotionReport() {
	try {
	    byte[] bytes = null;
	    if (promotionReportData != null && promotionReportData.getId() != null) {
		if (reportType == DRAFT_PROMOTION)
		    bytes = PromotionsService.getDraftPromotionBytes(promotionReportData, ReportOutputFormatsEnum.PDF.getCode());
		else if (reportType == DRAFT_PROMOTION_CANDIDATES_BY_DIFFERENTIATION)
		    bytes = PromotionsService.getCandidatesSoldiersPromotionByDifferentiationBytes(promotionReportData);
		super.print(bytes);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String closeProcess() {
	try {
	    PromotionsWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public List<Region> getRegions() {
	return regions;
    }

    public Integer getEnrollNumber() {
	return enrollNumber;
    }

    public void setEnrollNumber(Integer enrollNumber) {
	this.enrollNumber = enrollNumber;
    }

    public String getMedicalTestExemptionReason() {
	return medicalTestExemptionReason;
    }

    public void setMedicalTestExemptionReason(String medicalTestExemptionReason) {
	this.medicalTestExemptionReason = medicalTestExemptionReason;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }

    public List<String> getDetailSearchStatusesList() {
	return detailSearchStatusesList;
    }

    public void setDetailSearchStatusesList(List<String> detailSearchStatusesList) {
	this.detailSearchStatusesList = detailSearchStatusesList;
    }

    public Integer getMedicalTest() {
	return medicalTest;
    }

    public void setMedicalTest(Integer medicalTest) {
	this.medicalTest = medicalTest;
    }

    public Long getPromotionNextRankId() {
	try {
	    return promotionReportData.getRankId() == null ? null : PromotionsService.getNextRank(promotionReportData.getRankId());
	} catch (BusinessException e) {
	    return null;
	}
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }
}
