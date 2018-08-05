package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.promotions.PromotionReportDetailData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.PromotionCandidateStatusEnum;
import com.code.enums.ReportOutputFormatsEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PromotionsService;
import com.code.services.workflow.hcm.PromotionsWorkFlow;

@ManagedBean(name = "promotionCiviliansReport")
@ViewScoped
public class PromotionCiviliansReport extends PromotionsBase {

    private List<EmployeeData> internalCopies;
    private boolean unCandidateSearchFlag;
    private int reportType;
    private Long newPromotionStatus = null;
    private final static int DRAFT_PROMOTION_PDF = 1;
    private final static int DRAFT_PROMOTION_RTF = 2;
    private final static int UNCANDIDATES_PERSONS_PROMOTION_NO_VACANT = 3;
    private final static int UNCANDIDATES_PERSONS_PROMOTION_OTHER_REASONS = 4;

    public PromotionCiviliansReport() {
	this.init();
    }

    public void init() {
	super.init();

	empId = FlagsEnum.ALL.getCode();
	this.processId = WFProcessesEnum.CIVILIANS_PROMOTION.getCode();
	unCandidateSearchFlag = false;
	reportType = DRAFT_PROMOTION_PDF;

	try {
	    Long reportId = null;
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode()))
		reportId = PromotionsWorkFlow.getWFPromotionByInstanceId(instance.getInstanceId()).getReportId();
	    else
		reportId = getRequest().getParameter("reportId") == null ? null : Long.parseLong(getRequest().getParameter("reportId"));

	    if (reportId != null) {
		promotionReportData = PromotionsService.getPromotionReportDataById(reportId);
		searchUnCandidate();

		internalCopies = EmployeesService.getEmployeesByIdsString(promotionReportData.getInternalCopies());
	    } else {
		super.initialize(CategoriesEnum.PERSONS.getCode());
		internalCopies = new ArrayList<EmployeeData>();
	    }

	    ranks = PromotionsService.getPromotionReportRanksByCategoryId(CategoriesEnum.PERSONS.getCode());

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void searchPromotionDetails() {
	try {
	    promotionReportDetailDataList = PromotionsService.getPromotionReportDetailsData(promotionReportData.getId(), FlagsEnum.ALL.getCode(), detailSearchEmpName, new Long[] { detailSearchStatus });
	    unCandidateSearchFlag = false;
	    if (promotionReportDetailDataList.size() > 0)
		selectReportDetail(promotionReportDetailDataList.get(0));
	    else
		this.selectedPromotionReportDetailData = null;

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void savePromotionReportDetail() {
	try {
	    if (selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode()) && selectedPromotionReportDetailData.getNoVacantFlagBoolean() == false && (selectedPromotionReportDetailData.getNonCandidateReasons() == null || selectedPromotionReportDetailData.getNonCandidateReasons().equals("")))
		throw new BusinessException("error_civilianNonCandidateReasonsMandatory");

	    PromotionsWorkFlow.savePromotionReportDetail(instance, promotionReportData.getId(), selectedPromotionReportDetailData, promotionReportData.getCategoryId(), oldPromotionReportDetailStatus, selectedPromotionReportDetailData.getStatus(), this.loginEmpData.getEmpId());
	    this.oldPromotionReportDetailStatus = selectedPromotionReportDetailData.getStatus();

	    if (newPromotionStatus != null) {
		// '^' XOR: it means if only one of conditions is true then result expression is true (0,0)-> 0, (0,1)-> 1, (1,0)-> 1, (1,1)->0
		if (newPromotionStatus.equals(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode()) ^ unCandidateSearchFlag)
		    searchUnCandidate();

		newPromotionStatus = null;
	    }

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void changeStatus() {

	newPromotionStatus = selectedPromotionReportDetailData.getStatus();
	selectedPromotionReportDetailData.setNewJobId(null);
	selectedPromotionReportDetailData.setNewJobClassCode(null);
	selectedPromotionReportDetailData.setNewJobClassDesc(null);
	selectedPromotionReportDetailData.setNewJobCode(null);
	selectedPromotionReportDetailData.setNewJobDesc(null);
	if (!selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode())) {
	    selectedPromotionReportDetailData.setNoVacantFlagBoolean(false);
	    selectedPromotionReportDetailData.setNonCandidateReasons(null);
	}
	setPromotionCommitteeOpinion();
    }

    public void searchUnCandidate() {

	try {
	    String searchEmpName = unCandidateSearchFlag ? null : detailSearchEmpName;
	    Long[] statuses = unCandidateSearchFlag ? new Long[] { PromotionCandidateStatusEnum.NON_CANDIDATE.getCode() } : new Long[] { PromotionCandidateStatusEnum.CANDIDATE.getCode(), PromotionCandidateStatusEnum.CANDIDATE_SEQUENTIALLY.getCode(), PromotionCandidateStatusEnum.PRECEDENTED_POINTS.getCode(), PromotionCandidateStatusEnum.PROMOTED.getCode() };

	    promotionReportDetailDataList = PromotionsService.getPromotionReportDetailsData(promotionReportData.getId(), FlagsEnum.ALL.getCode(), searchEmpName, statuses);

	    if (promotionReportDetailDataList.size() > 0)
		selectReportDetail(promotionReportDetailDataList.get(0));
	    else
		selectedPromotionReportDetailData = null;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void printPromotionReport() {

	try {
	    byte[] bytes = null;
	    if (promotionReportData != null && promotionReportData.getId() != null) {
		if (reportType == DRAFT_PROMOTION_RTF) {// Word
		    bytes = PromotionsService.getDraftPromotionBytes(promotionReportData, ReportOutputFormatsEnum.RTF.getCode());
		    super.printRTF(bytes);
		} else {
		    if (reportType == DRAFT_PROMOTION_PDF) // PDF
			bytes = PromotionsService.getDraftPromotionBytes(promotionReportData, ReportOutputFormatsEnum.PDF.getCode());
		    else if (reportType == UNCANDIDATES_PERSONS_PROMOTION_NO_VACANT)
			bytes = PromotionsService.getUnCandidatesPersonsPromotionBytes(promotionReportData.getId(), true);
		    else if (reportType == UNCANDIDATES_PERSONS_PROMOTION_OTHER_REASONS)
			bytes = PromotionsService.getUnCandidatesPersonsPromotionBytes(promotionReportData.getId(), false);

		    super.print(bytes);
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void checkNoVacant() {
	if (selectedPromotionReportDetailData.getNoVacantFlagBoolean())
	    selectedPromotionReportDetailData.setNonCandidateReasons(null);
    }

    public void setPromotionCommitteeOpinion() {
	if (selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE.getCode()))
	    selectedPromotionReportDetailData.setPromotionCommitteeOpinion(getMessage("label_candidateStatus"));
	else if (selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode()))
	    selectedPromotionReportDetailData.setPromotionCommitteeOpinion(getMessage("label_unCandidateStatus"));
	else if (selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE_SEQUENTIALLY.getCode()))
	    selectedPromotionReportDetailData.setPromotionCommitteeOpinion(getMessage("label_candidateSequentially"));
	else if (selectedPromotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.PRECEDENTED_POINTS.getCode()))
	    selectedPromotionReportDetailData.setPromotionCommitteeOpinion(getMessage("label_unprecedentedPoints"));
    }

    public void handleBonus() {
	if (this.selectedPromotionReportDetailData != null) {
	    try {
		PromotionsService.handlePromotionCiviliansBonus(selectedPromotionReportDetailData);
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    } catch (Exception e) {
		this.setServerSideErrorMessages(getMessage("error_general"));
	    }
	}
    }

    public String initProcess() {
	try {
	    // taskUrl = "/Promotions/PromotionCiviliansReport.jsf?mode=3&reportId=" + promotionReportData.getId();
	    List<PromotionReportDetailData> reportDetails = PromotionsService.getPromotionReportDetailsDataByReportId(promotionReportData.getId());
	    PromotionsWorkFlow.initPromotion(requester, promotionReportData, reportDetails, processId, taskUrl, internalCopies);
	    return NavigationEnum.OUTBOX.toString();

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveRE() {
	try {
	    List<PromotionReportDetailData> reportDetails = PromotionsService.getPromotionReportDetailsDataByReportId(promotionReportData.getId());
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

    public String closeProcess() {
	try {
	    PromotionsWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }

    public boolean isUnCandidateSearchFlag() {
	return unCandidateSearchFlag;
    }

    public void setUnCandidateSearchFlag(boolean unCandidateSearchFlag) {
	this.unCandidateSearchFlag = unCandidateSearchFlag;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

}
