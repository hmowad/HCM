package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.enums.MovementTypesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MovementsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.MovementsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

public abstract class MovementsBase extends WFBaseBacking implements Serializable {
    protected int mode;
    protected Long selectedReviewerEmpId;
    protected List<EmployeeData> reviewerEmps;
    protected List<EmployeeData> internalCopies;
    protected String externalCopies;
    protected int warningsCount = 0;
    protected int pageSize = 10;

    protected Boolean passSecurityScan;

    protected WFMovementData wfMovement;

    protected WFMovementData decisionData;
    protected List<WFMovementData> wfMovementsList;

    public MovementsBase() {
    }

    public String initMovement() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    for (WFMovementData wfm : wfMovementsList) {
		if (wfm.getTransactionTypeId().equals(TransactionTypesEnum.MVT_NEW_DECISION.getCode()) || wfm.getTransactionTypeId().equals(TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode())) {
		    if (wfm.getExecutionDateString() != null && ((wfm.getPeriodMonths() != null && wfm.getPeriodMonths() > 0) || (wfm.getPeriodDays() != null && wfm.getPeriodDays() > 0))) {
			String endDate = HijriDateService.addSubStringHijriMonthsDays(wfm.getExecutionDateString(), wfm.getPeriodMonths() == null ? 0 : wfm.getPeriodMonths(), wfm.getPeriodDays() == null ? -1 : wfm.getPeriodDays() - 1);
			if (!endDate.equals(wfm.getEndDateString())) {
			    wfm.setEndDateString(endDate);
			    throw new BusinessException("error_endDateIsRecalculated");
			}
		    }
		}
		wfm.getWfMovement().setSystemUser(loginEmpData.getEmpId() + "");
		updateWFMovementDecisionData(wfm, decisionData);
	    }

	    MovementsWorkFlow.initMovement(requester, wfMovementsList, processId, attachments, taskUrl, internalCopies, externalCopies);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String approveMovementEE() {
	try {
	    MovementsWorkFlow.doMovementEE(requester, wfMovement.getReplacementEmployeeId(), instance, currentTask, true);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String rejectMovementEE() {
	try {
	    MovementsWorkFlow.doMovementEE(requester, wfMovement.getReplacementEmployeeId(), instance, currentTask, false);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String approveMovementDM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementDM(requester, instance, wfMovementsList, currentTask, true);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String rejectMovementDM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementDM(requester, instance, wfMovementsList, currentTask, false);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String redirectMovementMRTS() {
	try {
	    MovementsWorkFlow.doMovementMRTS(requester, instance, currentTask, true);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String rejectMovementMRTS() {
	try {
	    MovementsWorkFlow.doMovementMRTS(requester, instance, currentTask, false);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String approveMovementSE() {
	try {
	    MovementsWorkFlow.doMovementSE(requester, instance, currentTask, true);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String rejectMovementSE() {
	try {
	    MovementsWorkFlow.doMovementSE(requester, instance, currentTask, false);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String approveMovementReplacementDM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementReplacementDM(requester, instance, wfMovementsList, currentTask, true);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String rejectMovementReplacementDM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementReplacementDM(requester, instance, wfMovementsList, currentTask, false);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String redirectMovementMR() {
	try {
	    MovementsWorkFlow.doMovementMR(requester, instance, currentTask, selectedReviewerEmpId, true);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String rejectMovementMR() {
	try {
	    MovementsWorkFlow.doMovementMR(requester, instance, currentTask, selectedReviewerEmpId, false);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String redirectMovementSMR() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementSMR(requester, instance, wfMovementsList.get(0).getReplacementEmployeeId(), currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String redirectMovementRSMR() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementRSMR(requester, instance, wfMovementsList, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();

	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String approveMovementRE() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }
	    for (WFMovementData wfm : wfMovementsList) {
		if (wfm.getTransactionTypeId().equals(TransactionTypesEnum.MVT_NEW_DECISION.getCode()) || wfm.getTransactionTypeId().equals(TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode())) {
		    if (wfm.getExecutionDateString() != null && ((wfm.getPeriodMonths() != null && wfm.getPeriodMonths() > 0) || (wfm.getPeriodDays() != null && wfm.getPeriodDays() > 0))) {
			String endDate = HijriDateService.addSubStringHijriMonthsDays(wfm.getExecutionDateString(), wfm.getPeriodMonths() == null ? 0 : wfm.getPeriodMonths(), wfm.getPeriodDays() == null ? -1 : wfm.getPeriodDays() - 1);
			if (!endDate.equals(wfm.getEndDateString())) {
			    wfm.setEndDateString(endDate);
			    throw new BusinessException("error_endDateIsRecalculated");
			}
		    }
		}
		wfm.getWfMovement().setSystemUser(loginEmpData.getEmpId() + "");
		updateWFMovementDecisionData(wfm, decisionData);
	    }

	    MovementsWorkFlow.doMovementRE(requester, instance, wfMovementsList, currentTask, attachments, internalCopies, externalCopies, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String rejectMovementRE() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementRE(requester, instance, wfMovementsList, currentTask, attachments, internalCopies, externalCopies, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String approveMovementSRE() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }
	    for (WFMovementData wfm : wfMovementsList) {
		wfm.getWfMovement().setSystemUser(loginEmpData.getEmpId() + "");
	    }

	    MovementsWorkFlow.doMovementSRE(requester, instance, wfMovementsList, currentTask, internalCopies, externalCopies, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String rejectMovementSRE() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementSRE(requester, instance, wfMovementsList, currentTask, internalCopies, externalCopies, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String approveMovementRSRE() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }
	    for (WFMovementData wfm : wfMovementsList) {
		wfm.getWfMovement().setSystemUser(loginEmpData.getEmpId() + "");
	    }

	    MovementsWorkFlow.doMovementRSRE(requester, instance, wfMovementsList, currentTask, internalCopies, externalCopies, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}
    }

    public String rejectMovementRSRE() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementRSRE(requester, instance, wfMovementsList, currentTask, internalCopies, externalCopies, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String approveMovementSM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    for (WFMovementData wfm : wfMovementsList) {
		updateWFMovementDecisionData(wfm, decisionData);
	    }

	    MovementsWorkFlow.doMovementSM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String rejectMovementSM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementSM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String returnMovementSM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementSM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String approveMovementSSM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementSSM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String rejectMovementSSM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementSSM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String returnMovementSSM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementSSM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String approveMovementRSSM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementRSSM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String rejectMovementRSSM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementRSSM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String returnMovementRSSM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementRSSM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String approveMovementPOM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementPOM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.APPROVE.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String rejectMovementPOM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementPOM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.REJECT.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String returnMovementPOM() {
	try {
	    if (wfMovementsList == null) {
		wfMovementsList = new ArrayList<WFMovementData>();
		wfMovementsList.add(wfMovement);
	    }

	    MovementsWorkFlow.doMovementPOM(requester, instance, wfMovementsList, currentTask, WFActionFlagsEnum.RETURN_REVIEWER.getCode());
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    public String closeProcess() {
	try {
	    MovementsWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e1) {
	    setServerSideErrorMessages(getParameterizedMessage(e1.getMessage(), e1.getParams()));
	    return null;
	}

    }

    /**
     * updates movements request based on common details in decision data
     * 
     * @param movementRequest
     *            movement request to be updated
     * @param decisionData
     *            movement request contains common details
     * @param processId
     *            workflow process
     */
    private static void updateWFMovementDecisionData(WFMovementData movementRequest, WFMovementData decisionData) {
	// IF decsiionData equals null, This means this is a singular WF and no need for a common object
	if (decisionData != null) {

	    // We check for "FLAGS" only if they are null as they might have a default value on construction of WF movement or already have been set from
	    // last transaction like in extension/termination/cancellation subjoin
	    if (decisionData.getLocationFlag() != null)
		movementRequest.setLocationFlag(decisionData.getLocationFlag());

	    if (decisionData.getReasonType() != null)
		movementRequest.setReasonType(decisionData.getReasonType());

	    if (decisionData.getVacationGrantFlag() != null)
		movementRequest.setVacationGrantFlag(decisionData.getVacationGrantFlag());

	    if (decisionData.getVerbalOrderFlag() != null)
		movementRequest.setVerbalOrderFlag(decisionData.getVerbalOrderFlag());

	    if (decisionData.getTransferAllowanceFlag() != null)
		movementRequest.setTransferAllowanceFlag(decisionData.getTransferAllowanceFlag());

	    if (decisionData.getExecutionDateFlag() != null)
		movementRequest.setExecutionDateFlag(decisionData.getExecutionDateFlag());

	    // No need to check for nulls in these parameters as their default values are always null

	    // ExecutionDate is common only in move operations
	    if (movementRequest.getMovementTypeId().longValue() == MovementTypesEnum.MOVE.getCode())
		movementRequest.setExecutionDate(decisionData.getExecutionDate());
	    movementRequest.setBasedOn(decisionData.getBasedOn());
	    movementRequest.setBasedOnOrderNumber(decisionData.getBasedOnOrderNumber());
	    movementRequest.setBasedOnOrderDate(decisionData.getBasedOnOrderDate());
	    movementRequest.setRemarks(decisionData.getRemarks());
	    movementRequest.setSpecialRemarks(decisionData.getSpecialRemarks());
	    movementRequest.setSpecialAttachments(decisionData.getSpecialAttachments());
	    movementRequest.setDecisionApprovalRemarks(decisionData.getDecisionApprovalRemarks());
	    movementRequest.setMinistryApprovalNumber(decisionData.getMinistryApprovalNumber());
	    movementRequest.setMinistryApprovalDate(decisionData.getMinistryApprovalDate());
	}
    }

    public String getActualWarningMessage(String warnings) {
	if (warnings == null || warnings.isEmpty())
	    return "";
	String[] messagesKeys = warnings.split(",");
	String message = "1- " + getMessage(messagesKeys[0]) + ".";
	for (int i = 1; i < messagesKeys.length; i++) {
	    message += "\n";
	    message += i + 1 + "- " + getMessage(messagesKeys[i]) + ".";
	}
	return message;
    }

    public String getConcatenatedPeriod(WFMovementData movement) {
	StringBuilder period = new StringBuilder();
	if (movement.getPeriodMonths() != null && movement.getPeriodMonths() != 0) {
	    period.append(movement.getPeriodMonths() + " " + getMessage("label_month") + " ");
	}
	if (movement.getPeriodDays() != null && movement.getPeriodDays() != 0)
	    period.append(movement.getPeriodDays() + " " + getMessage("label_day"));
	return period.toString();
    }

    public void getSecurityPassScanValue() throws BusinessException {
	if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
	    WFTask securityEmpTask = MovementsWorkFlow.getSecurityEmpTaskByInstanceId(instance.getInstanceId());
	    if (securityEmpTask == null || securityEmpTask.getAction().equals(WFTaskActionsEnum.PASS_SECURITY_SCAN.getCode()))
		passSecurityScan = true;
	    else
		passSecurityScan = false;
	}
    }

    public void print() {
	try {
	    WFMovementData m = wfMovement == null ? wfMovementsList.get(0) : wfMovement;

	    List<MovementTransactionData> transactions = MovementsService.getMovementTransactionsByMovementInfo(m.getEmployeeId(), m.getExecutionDate(), m.getMovementTypeId(), m.getTransactionTypeId());
	    if (transactions != null) {
		byte[] decisionBytes = MovementsService.getMovementDecisionBytes(transactions.get(0));
		super.print(decisionBytes);
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public Long getSelectedReviewerEmpId() {
	return selectedReviewerEmpId;
    }

    public void setSelectedReviewerEmpId(Long selectedReviewerEmpId) {
	this.selectedReviewerEmpId = selectedReviewerEmpId;
    }

    public List<EmployeeData> getReviewerEmps() {
	return reviewerEmps;
    }

    public void setReviewerEmps(List<EmployeeData> reviewerEmps) {
	this.reviewerEmps = reviewerEmps;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }

    public String getExternalCopies() {
	return externalCopies;
    }

    public void setExternalCopies(String externalCopies) {
	this.externalCopies = externalCopies;
    }

    public int getWarningsCount() {
	return warningsCount;
    }

    public String getWarningSummary() {
	return getParameterizedMessage("label_thereIsWarningsForNOfEmployees", warningsCount);
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public Boolean getPassSecurityScan() {
	return passSecurityScan;
    }

    public void setPassSecurityScan(Boolean passSecurityScan) {
	this.passSecurityScan = passSecurityScan;
    }

    public WFMovementData getWfMovement() {
	return wfMovement;
    }

    public void setWfMovement(WFMovementData wfMovement) {
	this.wfMovement = wfMovement;
    }

    public List<WFMovementData> getWfMovementsList() {
	return wfMovementsList;
    }

    public void setWfMovementsList(List<WFMovementData> wfMovementsList) {
	this.wfMovementsList = wfMovementsList;
    }

    public WFMovementData getDecisionData() {
	return decisionData;
    }

    public void setDecisionData(WFMovementData decisionData) {
	this.decisionData = decisionData;
    }

}
