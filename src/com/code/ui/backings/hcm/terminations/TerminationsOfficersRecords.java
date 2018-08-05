package com.code.ui.backings.hcm.terminations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.terminations.TerminationRecordDetailData;
import com.code.enums.FlagsEnum;
import com.code.enums.RanksEnum;
import com.code.enums.TerminationReasonsEnum;
import com.code.enums.TerminationRecordOfficersStatusEnum;
import com.code.enums.TerminationRecordStatusEnum;
import com.code.enums.TerminationRequestTypeEnum;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.TerminationsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

@SuppressWarnings("serial")
@ManagedBean(name = "terminationsOfficersRecords")
@ViewScoped
public class TerminationsOfficersRecords extends TerminationsRecordsBase {

    private Long detailSearchStatus;
    private Long selectedTerminationDetailStatus;
    private List<TerminationRecordDetailData> collectiveRecordDetailDataList;
    private String collectiveOfficersDecisionNumber;
    private Date collectiveOfficersDecisionDate;

    public TerminationsOfficersRecords() {
	super.initialize();
	terminationRecordReasonIdChange();
	collectiveRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();
    }

    public void updateTerminationRecord() {
	try {
	    if (terminationRecordData.getStatus().equals(TerminationRecordStatusEnum.CLOSED.getCode())) {
		if (checkForCloseRecord()) {
		    collectiveRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();
		    collectiveOfficersDecisionNumber = null;
		    collectiveOfficersDecisionDate = null;
		    TerminationsService.saveTerminationRecord(terminationRecordData, loginEmpData.getEmpId());
		    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
		} else {
		    terminationRecordData.setStatus(TerminationRecordStatusEnum.CURRENT.getCode());
		    this.setServerSideErrorMessages(getMessage("error_canNotCloseTerminationRecord"));
		}
	    } else { // update because of adding attachments
		TerminationsService.saveTerminationRecord(terminationRecordData, loginEmpData.getEmpId());
		this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private boolean checkForCloseRecord() {
	for (TerminationRecordDetailData terminationRecordDetailDataItr : originalTerminationRecordDetailDataList) {
	    if (terminationRecordDetailDataItr.getStatus().equals(TerminationRecordOfficersStatusEnum.MINISTRY_SENT.getCode())) {
		return false;
	    }
	}
	return true;
    }

    public void addEmployeeRecordDetailSingular() throws BusinessException {
	if (terminationRecordData.getReasonId() == TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode() || terminationRecordData.getReasonId() == TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode()) {
	    constructTerminationsDetailData();
	} else
	    constructTerminationsDetailDataSingular();

    }

    @Override
    public void resetTerminationRecordDetailList() {
	if (filteredBySearchFlag) {
	    super.resetTerminationRecordDetailList();
	    detailSearchStatus = (long) FlagsEnum.ALL.getCode();
	}
    }

    @Override
    public void searchTerminationsDetails() {

	// reset list that is used for view
	terminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();

	for (TerminationRecordDetailData terminationRecordDetailDataItr : originalTerminationRecordDetailDataList) {
	    if ((detailSearchEmpName == null || terminationRecordDetailDataItr.getEmpName().contains(detailSearchEmpName)) && (detailSearchStatus == null || detailSearchStatus == FlagsEnum.ALL.getCode() || terminationRecordDetailDataItr.getStatus().equals(detailSearchStatus)))
		terminationRecordDetailDataList.add(terminationRecordDetailDataItr);
	}

	filteredBySearchFlag = true;
	if (terminationRecordDetailDataList.size() > 0)
	    selectTerminationsDetailData(terminationRecordDetailDataList.get(0));
	else
	    selectedTerminationRecordDetailData = null;
    }

    public void checkUncheckRow(TerminationRecordDetailData terminationRecordDetailData) {
	// Add to collective List
	if (terminationRecordDetailData.getSelected()) {
	    collectiveRecordDetailDataList.add(terminationRecordDetailData);
	    selectedTerminationRecordDetailData = null;
	} else // Remove from collected list
	    collectiveRecordDetailDataList.remove(terminationRecordDetailData);
    }

    @Override
    public void selectTerminationsDetailData(TerminationRecordDetailData terminationRecordDetailData) {
	super.selectTerminationsDetailData(terminationRecordDetailData);
	if (this.selectedTerminationRecordDetailData.getStatus() != null)
	    selectedTerminationDetailStatus = this.selectedTerminationRecordDetailData.getStatus();
    }

    @Override
    public void deleteTerminationsDetailData(TerminationRecordDetailData terminationRecordDetailData) {
	super.deleteTerminationsDetailData(terminationRecordDetailData);
	collectiveRecordDetailDataList.remove(terminationRecordDetailData);
    }

    public void saveCollectiveTerminationRecordDetails() {
	try {
	    TerminationsService.saveCollectiveOfficersTerminationRecordDetails(terminationRecordData, collectiveRecordDetailDataList, collectiveOfficersDecisionNumber, collectiveOfficersDecisionDate, originalTerminationRecordDetailDataList, this.loginEmpData.getEmpId());

	    collectiveRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();
	    collectiveOfficersDecisionNumber = null;
	    collectiveOfficersDecisionDate = null;
	    if (terminationRecordData.getStatus().equals(TerminationRecordStatusEnum.CLOSED.getCode()))
		setServerSideSuccessMessages(getMessage("notify_terminationRecordAutomaticallyClosed"));
	    else
		setServerSideSuccessMessages(getMessage("notify_successOperation"));

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void saveTerminationRecordDetail() {

	Long selectedTerminationDetailOldStatus = selectedTerminationRecordDetailData.getStatus();
	try {
	    // update status, and desired termination date before saving
	    selectedTerminationRecordDetailData.setStatus(selectedTerminationDetailStatus);

	    List<TerminationRecordDetailData> terminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();
	    terminationRecordDetailDataList.add(selectedTerminationRecordDetailData);

	    TerminationsService.saveOfficersTerminationRecordDetails(terminationRecordData, terminationRecordDetailDataList, originalTerminationRecordDetailDataList, loginEmpData.getEmpId());
	    if (terminationRecordData.getStatus().equals(TerminationRecordStatusEnum.CLOSED.getCode())) {
		// Collective reasons
		if (terminationRecordData.getReasonId() == TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode() || terminationRecordData.getReasonId() == TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode())
		    setServerSideSuccessMessages(getMessage("notify_terminationRecordAutomaticallyClosed"));
		else
		    // singular record
		    setServerSideSuccessMessages(getMessage("notify_terminationRecordAutomaticallyClosedSingular"));
	    } else
		this.setServerSideSuccessMessages(getMessage("notify_successOperation"));

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    // roll back status
	    selectedTerminationRecordDetailData.setStatus(selectedTerminationDetailOldStatus);
	}
    }

    public void terminationRecordReasonIdChange() {
	ranks = new ArrayList<Rank>();
	if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode()) {
	    ranks.add(CommonService.getRankById(RanksEnum.MAJOR_GENERAL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.BRIGADIER.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT_COLONEL.getCode()));
	} else if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode()) {
	    ranks.add(CommonService.getRankById(RanksEnum.MAJOR_GENERAL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.BRIGADIER.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT_COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.MAJOR.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.CAPTAIN.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.FIRST_LIEUTENANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT.getCode()));
	}
    }

    public String getCollectiveRecordsServiceTerminationDate() {
	try {
	    TerminationsService.setCollectiveRecordsServiceTerminationDate(selectedTerminationRecordDetailData, terminationRecordData.getReasonId());
	    return selectedTerminationRecordDetailData.getServiceTerminationDateString();
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	    return null;
	}
    }

    public void recordDetailStatusChange() {

	// reset extension data
	selectedTerminationRecordDetailData.setExtensionStartDate(null);
	selectedTerminationRecordDetailData.setExtensionEndDate(null);
	selectedTerminationRecordDetailData.setExtensionPeriodDays(null);
	selectedTerminationRecordDetailData.setExtensionPeriodMonths(null);

	selectedTerminationRecordDetailData.setDecisionNumber(null);
	selectedTerminationRecordDetailData.setDecisionDate(null);

	selectedTerminationRecordDetailData.setMinistryRejectionNumber(null);
	selectedTerminationRecordDetailData.setMinistryRejectionDate(null);

	if (selectedTerminationDetailStatus.longValue() != TerminationRecordOfficersStatusEnum.TERMINATED.getCode()) {
	    selectedTerminationRecordDetailData.setServiceTerminationDate(null);
	    selectedTerminationRecordDetailData.setTerminationRequestType(null);
	    setResignation(false);
	} else {
	    serviceTerminationDateChange();
	}

	if (selectedTerminationDetailStatus.longValue() == TerminationRecordOfficersStatusEnum.UNDER_REVIEW.getCode()) {
	    selectedTerminationRecordDetailData.setMinistryRequestNumber(null);
	    selectedTerminationRecordDetailData.setMinistryRequestDate(null);
	}
    }

    public void serviceTerminationDateChange() {
	if (terminationRecordData.getReasonId().equals(TerminationReasonsEnum.OFFICERS_TERMINATION_REQUEST.getCode()) && selectedTerminationRecordDetailData.getServiceTerminationDate() != null) {
	    float servicePeriodForOfficers = HijriDateService.hijriDateDiffByHijriYear(selectedTerminationRecordDetailData.getEmpRecruitmentDate(), selectedTerminationRecordDetailData.getServiceTerminationDate());

	    if (servicePeriodForOfficers < ETRConfigurationService.getTerminationsOfficersServicePeriodRetirementYearsMin()) {
		selectedTerminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RESIGNATION.getCode());
		setResignation(true); // can NOT be modified
	    } else if (servicePeriodForOfficers >= ETRConfigurationService.getTerminationsOfficersServicePeriodRetirementYearsMin() && servicePeriodForOfficers < ETRConfigurationService.getTerminationsOfficersServicePeriodRetirementYearsMax()) {
		selectedTerminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RETIREMENT.getCode());
		setResignation(false); // can be modified
	    } else {
		selectedTerminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RETIREMENT.getCode());
		setResignation(true); // can NOT be modified
	    }
	}
    }

    public void extensionEndDateChange() {
	try {
	    if (selectedTerminationRecordDetailData.getExtensionEndDate() == null) {
		selectedTerminationRecordDetailData.setExtensionPeriodDays(null);
		selectedTerminationRecordDetailData.setExtensionPeriodMonths(null);
		return;
	    }

	    if (!HijriDateService.isValidHijriDate(selectedTerminationRecordDetailData.getExtensionEndDate())) {
		selectedTerminationRecordDetailData.setExtensionEndDate(null);
		selectedTerminationRecordDetailData.setExtensionPeriodDays(null);
		selectedTerminationRecordDetailData.setExtensionPeriodMonths(null);

		this.setServerSideErrorMessages(getMessage("error_invalidExtensionEndDate"));
		return;
	    }

	    // compute extension start date if not computed before
	    if (selectedTerminationRecordDetailData.getExtensionStartDate() == null) {
		// in case there are no extensions, set extensionStartDate to empServiceTerminationDueDate
		if (selectedTerminationRecordDetailData.getEmpLastExtensionEndDate() == null) {
		    // 2- OFFICERS_COMPLETION_PERIOD_CURRENT_RANK and lastPromotionEndDate is not null
		    if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode()) {
			if (selectedTerminationRecordDetailData.getEmpLastPromotionDate() == null && selectedTerminationRecordDetailData.getEmpRecruitmentDate() == null) {
			    this.setServerSideErrorMessages(getMessage("error_recruitmentDateInvalidForExtension"));
			    return;
			}
			Date empCurrentRankEndDate;
			// check rank to compute terminationDueDate
			if (selectedTerminationRecordDetailData.getEmpRankId().equals(RanksEnum.MAJOR_GENERAL.getCode()))
			    empCurrentRankEndDate = HijriDateService.addSubHijriMonthsDays(selectedTerminationRecordDetailData.getEmpLastPromotionDate() != null ? selectedTerminationRecordDetailData.getEmpLastPromotionDate() : selectedTerminationRecordDetailData.getEmpRecruitmentDate(), ETRConfigurationService.getTerminationsOfficersRankPeriodMajorGeneralYearsMax() * 12, 0);
			else
			    empCurrentRankEndDate = HijriDateService.addSubHijriMonthsDays(selectedTerminationRecordDetailData.getEmpLastPromotionDate() != null ? selectedTerminationRecordDetailData.getEmpLastPromotionDate() : selectedTerminationRecordDetailData.getEmpRecruitmentDate(), ETRConfigurationService.getTerminationsOfficersRankPeriodYearsMax() * 12, 0);
			selectedTerminationRecordDetailData.setExtensionStartDate(empCurrentRankEndDate);

		    } else if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode()) {
			selectedTerminationRecordDetailData.setExtensionStartDate(selectedTerminationRecordDetailData.getEmpTerminationDueDate());
		    }
		} else {
		    // in case there are extensions, set extensionStartDate to the day (after) last extensionEndDate for officer
		    Date newExtensionStartDate = HijriDateService.addSubHijriDays(selectedTerminationRecordDetailData.getEmpLastExtensionEndDate(), 1);
		    selectedTerminationRecordDetailData.setExtensionStartDate(newExtensionStartDate);
		}
	    }

	    int extensionPeriod = HijriDateService.hijriDateDiff(selectedTerminationRecordDetailData.getExtensionStartDate(), selectedTerminationRecordDetailData.getExtensionEndDate());
	    int extensionPeriodMonths = extensionPeriod / 30;
	    int extensionPeriodDays = extensionPeriod % 30;

	    selectedTerminationRecordDetailData.setExtensionPeriodMonths(extensionPeriodMonths);
	    selectedTerminationRecordDetailData.setExtensionPeriodDays(extensionPeriodDays);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    @Override
    public void printTerminationsReport() {
	try {
	    byte[] bytes = TerminationsService.getTerminationRecordBytes(terminationRecordData.getId(), (long) mode, terminationRecordData.getReasonId(), detailSearchEmpName, detailSearchStatus);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    e.printStackTrace();
	}
    }

    // Setters and getters
    public Long getDetailSearchStatus() {
	return detailSearchStatus;
    }

    public void setDetailSearchStatus(Long detailSearchStatus) {
	this.detailSearchStatus = detailSearchStatus;
    }

    public Long getSelectedTerminationDetailStatus() {
	return selectedTerminationDetailStatus;
    }

    public void setSelectedTerminationDetailStatus(Long selectedTerminationDetailStatus) {
	this.selectedTerminationDetailStatus = selectedTerminationDetailStatus;
    }

    public List<TerminationRecordDetailData> getCollectiveRecordDetailDataList() {
	return collectiveRecordDetailDataList;
    }

    public void setCollectiveRecordDetailDataList(List<TerminationRecordDetailData> collectiveRecordDetailDataList) {
	this.collectiveRecordDetailDataList = collectiveRecordDetailDataList;
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
