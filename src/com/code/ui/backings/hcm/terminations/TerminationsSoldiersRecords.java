package com.code.ui.backings.hcm.terminations;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.terminations.TerminationReason;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.enums.FlagsEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TerminationAbsenceTypeEnum;
import com.code.enums.TerminationDeathTypeEnum;
import com.code.enums.TerminationReasonsEnum;
import com.code.enums.TerminationRequestTypeEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TerminationsService;
import com.code.services.util.AttachmentsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.TerminationsWorkflow;

@SuppressWarnings("serial")
@ManagedBean(name = "terminationsSoldiersRecords")
@ViewScoped
public class TerminationsSoldiersRecords extends TerminationsRecordsBase {

    private boolean retirementCheckFlag = false;
    private boolean regionDisabledFlag = false;

    public TerminationsSoldiersRecords() {
	this.init();
    }

    public void init() {
	super.initialize();
	if (terminationRecordData.getId() == null) {
	    ranks = new ArrayList<Rank>();

	    ranks.add(CommonService.getRankById(RanksEnum.PRIME_SERGEANTS.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.STAFF_SERGEANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.SERGEANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.UNDER_SERGEANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.CORPORAL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.FIRST_SOLDIER.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.SOLDIER.getCode()));

	    reasonLoadingRegions();
	} else {
	    if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_TERMINATION_REQUEST.getCode())
		if (terminationRecordDetailDataList.size() > 0)
		    desiredTerminationDateChange();
	}
    }

    public void reasonLoadingRegions() {
	if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

	    List<TerminationReason> tempTerminationReasons = new ArrayList<TerminationReason>();
	    for (TerminationReason reason : terminationReasons)
		if (reason.getId().longValue() == TerminationReasonsEnum.SOLDIERS_REACHING_RETIREMENT_AGE.getCode()
			|| reason.getId().longValue() == TerminationReasonsEnum.SOLDIERS_DEATH.getCode() || reason.getId().longValue() == TerminationReasonsEnum.SOLDIERS_LACK_MEDICAL_FITNESS.getCode()
			|| reason.getId().longValue() == TerminationReasonsEnum.SOLDIERS_FOREIGNER_MARRIAGE.getCode() || reason.getId().longValue() == TerminationReasonsEnum.SOLDIERS_ABSENCE.getCode())
		    tempTerminationReasons.add(reason);

	    terminationReasons = new ArrayList<TerminationReason>();
	    terminationReasons.addAll(tempTerminationReasons);

	    terminationRecordData.setRegionId(requester.getPhysicalRegionId());
	    regionDisabledFlag = true;
	}
    }

    public void reasonChanged() {
	if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
	    if (terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_REACHING_RETIREMENT_AGE.getCode()
		    || terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_DEATH.getCode() || terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_LACK_MEDICAL_FITNESS.getCode()
		    || terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_FOREIGNER_MARRIAGE.getCode() || terminationRecordData.getReasonId().longValue() == TerminationReasonsEnum.SOLDIERS_ABSENCE.getCode()) {

		terminationRecordData.setRegionId(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		regionDisabledFlag = true;
	    } else {
		regionDisabledFlag = false;
	    }
	}
    }

    public void dismissBasedOnChanged() {

	if (selectedTerminationRecordDetailData.getDismissBasedOn().equals(-1))
	    selectedTerminationRecordDetailData.setDismissBasedOnDesc(null);
	else if (selectedTerminationRecordDetailData.getDismissBasedOn().equals(1))
	    selectedTerminationRecordDetailData.setDismissBasedOnDesc(getMessage("label_serviceTerminationSoldiersBasedOnOne"));
	else if (selectedTerminationRecordDetailData.getDismissBasedOn().equals(2))
	    selectedTerminationRecordDetailData.setDismissBasedOnDesc(getMessage("label_serviceTerminationSoldiersBasedOnTwo"));
	else if (selectedTerminationRecordDetailData.getDismissBasedOn().equals(3))
	    selectedTerminationRecordDetailData.setDismissBasedOnDesc(getMessage("label_serviceTerminationSoldiersBasedOnThird"));
	else if (selectedTerminationRecordDetailData.getDismissBasedOn().equals(4))
	    selectedTerminationRecordDetailData.setDismissBasedOnDesc(getMessage("label_serviceTerminationSoldiersBasedOnFourth"));
	else if (selectedTerminationRecordDetailData.getDismissBasedOn().equals(5))
	    selectedTerminationRecordDetailData.setDismissBasedOnDesc(getMessage("label_serviceTerminationSoldiersBasedOnFifthReportAdjusted"));
	else if (selectedTerminationRecordDetailData.getDismissBasedOn().equals(6))
	    selectedTerminationRecordDetailData.setDismissBasedOnDesc(getMessage("label_serviceTerminationSoldiersBasedOnSixth"));
	else if (selectedTerminationRecordDetailData.getDismissBasedOn().equals(7))
	    selectedTerminationRecordDetailData.setDismissBasedOnDesc(getMessage("label_serviceTerminationSoldiersBasedOnSeventh"));
	else if (selectedTerminationRecordDetailData.getDismissBasedOn().equals(8))
	    selectedTerminationRecordDetailData.setDismissBasedOnDesc(getMessage("label_serviceTerminationSoldiersBasedOnEighth"));
	else if (selectedTerminationRecordDetailData.getDismissBasedOn().equals(9))
	    selectedTerminationRecordDetailData.setDismissBasedOnDesc(getMessage("label_serviceTerminationSoldiersBasedOnNinth"));
	else if (selectedTerminationRecordDetailData.getDismissBasedOn().equals(10))
	    selectedTerminationRecordDetailData.setDismissBasedOnDesc(getMessage("label_serviceTerminationSoldiersBasedOnTenth"));
    }

    public void dismissApprovalDateChanged() {
	try {
	    if (!HijriDateService.isValidHijriDate(selectedTerminationRecordDetailData.getDismissApprovalDate())) {
		selectedTerminationRecordDetailData.setDismissApprovalDate(null);
		selectedTerminationRecordDetailData.setServiceTerminationDate(null);
		this.setServerSideErrorMessages(getMessage("error_invalidDismissApprovalDate"));
		return;
	    }
	    selectedTerminationRecordDetailData.setServiceTerminationDateString(selectedTerminationRecordDetailData.getDismissApprovalDateString());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void changeDeathInDutyFlag(AjaxBehaviorEvent event) {
	if (selectedTerminationRecordDetailData.getDeathInDutyFlag().equals(FlagsEnum.OFF.getCode()) || selectedTerminationRecordDetailData.getDeathInDutyFlag().equals(FlagsEnum.ON.getCode())) {
	    selectedTerminationRecordDetailData.setDeathType(null);
	    selectedTerminationRecordDetailData.setDeathInOperations(null);
	}
    }

    public void changeDeathType(AjaxBehaviorEvent event) {
	if (!selectedTerminationRecordDetailData.getDeathType().equals(TerminationDeathTypeEnum.MARTYRDOM.getCode())) {
	    selectedTerminationRecordDetailData.setDeathInOperations(null);
	}
    }

    public void changeInjuryInDutyFlag(AjaxBehaviorEvent event) {
	if (selectedTerminationRecordDetailData.getInjuryInDutyFlag().equals(FlagsEnum.OFF.getCode()) || selectedTerminationRecordDetailData.getInjuryInDutyFlag().equals(FlagsEnum.ON.getCode())) {
	    selectedTerminationRecordDetailData.setInjuryType(null);
	}
    }

    public void changeAbsenceType(AjaxBehaviorEvent event) {
	if (!selectedTerminationRecordDetailData.getAbsenceType().equals(TerminationAbsenceTypeEnum.ATTEND_WITHOUT_EXCUSE.getCode())) {
	    selectedTerminationRecordDetailData.setAbsencePeriodDays(null);
	    selectedTerminationRecordDetailData.setAbsenceReturnDate(null);
	}

	if (!selectedTerminationRecordDetailData.getAbsenceType().equals(TerminationAbsenceTypeEnum.CONTINUOUS_DAYS.getCode())
		&& !selectedTerminationRecordDetailData.getAbsenceType().equals(TerminationAbsenceTypeEnum.DISCONTINUOUS_DAYS.getCode())) {
	    selectedTerminationRecordDetailData.setAbsencePeriodDays(null);
	}
    }

    public void changeForeignerMarriageFlag() {
	if (selectedTerminationRecordDetailData.getForeignerMarriageFlag().equals(FlagsEnum.OFF.getCode()))
	    selectedTerminationRecordDetailData.setReferring(null);
    }

    public void changeDisqualificationDrugsFlag() {
	if (selectedTerminationRecordDetailData.getDisqualificationDrugsFlag().equals(FlagsEnum.ON.getCode()))
	    selectedTerminationRecordDetailData.setDisqualificationReason(null);
	else
	    selectedTerminationRecordDetailData.setDisqualificationDrugsType(null);
    }

    public void addEmployeeRecordDetailSingular() throws BusinessException {
	if (terminationRecordData.getReasonId().equals(TerminationReasonsEnum.SOLDIERS_REACHING_RETIREMENT_AGE.getCode())) {
	    constructTerminationsDetailData();
	} else {
	    constructTerminationsDetailDataSingular();
	    if (terminationRecordDetailDataList.size() > 0) {
		if (terminationRecordData.getReasonId().equals(TerminationReasonsEnum.SOLDIERS_TERMINATION_REQUEST.getCode()))
		    desiredTerminationDateChange();
	    }
	}
    }

    public void addAttachments() {
	try {
	    terminationRecordData.setAttachments(AttachmentsService.getNextAttachmentsId());
	    TerminationsService.saveTerminationRecord(terminationRecordData, loginEmpData.getEmpId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void desiredTerminationDateChange() {
	float retiremtmentYears = HijriDateService.hijriDateDiffByHijriYear(selectedTerminationRecordDetailData.getEmpRecruitmentDate(), selectedTerminationRecordDetailData.getDesiredTerminationDate());
	if (retiremtmentYears < ETRConfigurationService.getTerminationsSoldiersServicePeriodRetirementYearsMin()) {
	    selectedTerminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RESIGNATION.getCode());
	    retirementCheckFlag = true;
	} else {
	    selectedTerminationRecordDetailData.setTerminationRequestType(TerminationRequestTypeEnum.RETIREMENT.getCode());
	    retirementCheckFlag = false;

	}
    }

    public void printTerminationTransaction(Long terminationRecordDetailId) {
	try {
	    TerminationTransactionData terminationTransactionData = TerminationsService.getTerminationTransactionByRecordDetailId(terminationRecordDetailId);
	    if (terminationTransactionData != null) {
		byte[] bytes = TerminationsService.getTerminationDecisionBytes(terminationTransactionData.getId(), mode, terminationTransactionData.getTransactionTypeCode());
		super.print(bytes);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String initProcess() {
	String internalCopiesString = EmployeesService.getEmployeesIdsString(internalCopies);
	terminationRecordData.setInternalCopies(internalCopiesString);
	try {
	    TerminationsWorkflow.initTermination(requester, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, processId, null, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveRE() {
	// TODO: move these two lines in WorkFlow for init() and RE() for all occurences of this behavior
	String internalCopiesString = EmployeesService.getEmployeesIdsString(internalCopies);
	terminationRecordData.setInternalCopies(internalCopiesString);
	try {
	    TerminationsWorkflow.doTerminationRE(requester, instance, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, currentTask, null, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    // reject by reviewer employee
    public String rejectRE() {
	try {
	    TerminationsWorkflow.doTerminationRE(requester, instance, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, currentTask, attachments, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String signSM() {
	try {
	    TerminationsWorkflow.doTerminationSM(requester, instance, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, currentTask, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectSM() {
	try {
	    TerminationsWorkflow.doTerminationSM(requester, instance, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, currentTask, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String modifySM() {
	try {
	    TerminationsWorkflow.doTerminationSM(requester, instance, wfTermination, terminationRecordData, originalTerminationRecordDetailDataList, currentTask, CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.STE_TERMINATION.getCode(), TransactionClassesEnum.TERMINATIONS.getCode()).getId(), WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String closeProcess() {
	try {
	    TerminationsWorkflow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public boolean getRegionDisabledFlag() {
	return regionDisabledFlag;
    }

    public void setRegionDisabledFlag(boolean regionDisabledFlag) {
	this.regionDisabledFlag = regionDisabledFlag;
    }

    public boolean getRetirementCheckFlag() {
	return retirementCheckFlag;
    }

    public void setRetirementCheckFlag(boolean retirementCheckFlag) {
	this.retirementCheckFlag = retirementCheckFlag;
    }

}
