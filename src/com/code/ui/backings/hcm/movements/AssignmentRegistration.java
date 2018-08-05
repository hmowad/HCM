package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MovementTransactionViewsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MovementsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "assignmentRegisteration")
@ViewScoped
public class AssignmentRegistration extends BaseBacking implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;
    private int pageSize = 10;

    private MovementTransactionData commonAssignmentData;
    private List<MovementTransactionData> assignmentList;

    public AssignmentRegistration() {
	setScreenTitle(getMessage("title_officersExternalAssignmentRegistration"));
	this.init();
    }

    public void init() {
	try {
	    commonAssignmentData = new MovementTransactionData();
	    commonAssignmentData.setDecisionDate(HijriDateService.getHijriSysDate());
	    assignmentList = new ArrayList<MovementTransactionData>();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    for (int i = 0; i < assignmentList.size(); i++) {
		assignmentList.get(i).setDecisionNumber(commonAssignmentData.getDecisionNumber());
		assignmentList.get(i).setDecisionDate(commonAssignmentData.getDecisionDate());
		assignmentList.get(i).setRemarks(commonAssignmentData.getRemarks());
		assignmentList.get(i).setAttachments(commonAssignmentData.getAttachments());
		assignmentList.get(i).setReasonType(commonAssignmentData.getReasonType());
		assignmentList.get(i).setApprovedId(loginEmpData.getEmpId());
	    }
	    MovementsService.handleMovementsTransactions(null, assignmentList, false, null, MovementTransactionViewsEnum.REGISTRATION.getCode());
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void addNewAssignmentTransaction() {
	try {
	    MovementTransactionData movementTransactionData = MovementsService.constructMovementTransaction(commonAssignmentData.getEmployeeId(), HijriDateService.getHijriSysDate(), null, null, null, null, null, LocationFlagsEnum.EXTERNAL.getCode(), null, null, null, null, MovementTypesEnum.ASSIGNMENT.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
	    movementTransactionData.setExecutionDate(HijriDateService.getHijriSysDate());
	    assignmentList.add(movementTransactionData);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void deleteAssignment(MovementTransactionData Assignment) {
	assignmentList.remove(Assignment);
    }

    public void manipulateEndDate(MovementTransactionData Assignment) {
	try {
	    if (Assignment.getExecutionDateString() != null && ((Assignment.getPeriodMonths() != null && Assignment.getPeriodMonths() > 0) || (Assignment.getPeriodDays() != null && Assignment.getPeriodDays() > 0))) {
		Assignment.setEndDateString(HijriDateService.addSubStringHijriMonthsDays(Assignment.getExecutionDateString(), Assignment.getPeriodMonths() == null ? 0 : Assignment.getPeriodMonths(), Assignment.getPeriodDays() == null ? -1 : Assignment.getPeriodDays() - 1));
	    } else
		Assignment.setEndDateString(null);
	} catch (Exception e) {
	    Assignment.setEndDateString(null);
	}
    }

    public MovementTransactionData getCommonAssignmentData() {
	return commonAssignmentData;
    }

    public void setCommonAssignmentData(MovementTransactionData commonAssignmentData) {
	this.commonAssignmentData = commonAssignmentData;
    }

    public List<MovementTransactionData> getAssignmentList() {
	return assignmentList;
    }

    public void setAssignmentList(List<MovementTransactionData> assignmentList) {
	this.assignmentList = assignmentList;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }
}
