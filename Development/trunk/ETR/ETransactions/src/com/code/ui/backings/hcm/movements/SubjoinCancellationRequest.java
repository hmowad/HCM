package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.MovementsReasonTypesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MovementsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.MovementsWorkFlow;

@ManagedBean(name = "subjoinCancellationRequest")
@ViewScoped
public class SubjoinCancellationRequest extends MovementsBase implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private MovementTransactionData lastValidTran;
    private Boolean passSecurityScan;

    public SubjoinCancellationRequest() {
	super.init();
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    this.init();
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersSubjoinCancellationRequest"));
		this.processId = WFProcessesEnum.OFFICERS_SUBJOIN_CANCELLATION_REQUEST.getCode();
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersSubjoinCancellationRequest"));
		this.processId = WFProcessesEnum.SOLDIERS_SUBJOIN_CANCELLATION_REQUEST.getCode();
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsInternalAssingnmentCancellationRequest"));
		this.processId = WFProcessesEnum.PERSONS_ASSIGNMENT_CANCELLATION_REQUEST.getCode();
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_general"));
	    }
	} else
	    setServerSideErrorMessages(getMessage("error_general"));
    }

    protected void init() {
	try {
	    wfMovement = new WFMovementData();
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfMovement = MovementsWorkFlow.getWFMovementDataByInstanceId(instance.getInstanceId()).get(0);
		lastValidTran = MovementsService.getMovementTransactions(wfMovement.getEmployeeId(), wfMovement.getCategoryId(), wfMovement.getBasedOnDecisionNumber(), wfMovement.getBasedOnDecisionDate(), wfMovement.getBasedOnDecisionDate(), MovementTypesEnum.SUBJOIN.getCode(), FlagsEnum.ON.getCode()).get(0);

		if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()) && wfMovement.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode()) && wfMovement.getMinistryApprovalDate() == null)
		    wfMovement.setMinistryApprovalDate(HijriDateService.getHijriSysDate());

		if (!(this.role.equals(WFTaskRolesEnum.DIRECT_MANAGER.getCode()) || this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()))) {
		    internalCopies = EmployeesService.getEmployeesByIdsString(wfMovement.getInternalCopies());
		    if (internalCopies == null)
			internalCopies = new ArrayList<EmployeeData>();
		    externalCopies = wfMovement.getExternalCopies();
		}

		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode()) || this.role.equals(WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode())) {
		    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
			WFTask securityEmpTask = MovementsWorkFlow.getSecurityEmpTaskByInstanceId(instance.getInstanceId());
			if (securityEmpTask.getAction().equals(WFTaskActionsEnum.PASS_SECURITY_SCAN.getCode()))
			    passSecurityScan = true;
			else
			    passSecurityScan = false;
		    }
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		    if (mode == 1)
			reviewerEmps.add(loginEmpData);
		}
	    } else {
		lastValidTran = MovementsService.getLastValidMovementTransaction(this.loginEmpData.getEmpId(), MovementTypesEnum.SUBJOIN.getCode(), FlagsEnum.ON.getCode());
		if (lastValidTran != null)
		    wfMovement = MovementsWorkFlow.constructWFMovement(processId, requester.getEmpId(), null, lastValidTran.getExecutionDateFlag(), lastValidTran.getExecutionDate(), lastValidTran.getEndDate(), lastValidTran.getUnitId(), lastValidTran.getUnitFullName(), lastValidTran.getPeriodDays(), lastValidTran.getPeriodMonths(), lastValidTran.getLocationFlag(), lastValidTran.getLocation(), lastValidTran.getDecisionNumber(), lastValidTran.getDecisionDate(),
			    MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode(), lastValidTran.getJobId(), lastValidTran.getJobCode(),
			    lastValidTran.getJobName(), MovementTypesEnum.SUBJOIN.getCode(), CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_CANCEL_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getCode());
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public MovementTransactionData getLastValidTran() {
	return lastValidTran;
    }

    public void setLastValidTran(MovementTransactionData lastValidTran) {
	this.lastValidTran = lastValidTran;
    }

    public Boolean getPassSecurityScan() {
	return passSecurityScan;
    }

    public void setPassSecurityScan(Boolean passSecurityScan) {
	this.passSecurityScan = passSecurityScan;
    }
}
