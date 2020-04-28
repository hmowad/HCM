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

@ManagedBean(name = "subjoinRegistration")
@ViewScoped
public class SubjoinRegistration extends BaseBacking implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;
    private int pageSize = 10;

    private MovementTransactionData commonSubjoinData;
    private List<MovementTransactionData> subjoinList;

    public SubjoinRegistration() {
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    this.init();
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersExternalSubjoinRegistration"));
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsExternalAssignmentRegistration"));
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} else
	    setServerSideErrorMessages(getMessage("error_URLError"));
    }

    public void init() {
	try {
	    commonSubjoinData = new MovementTransactionData();
	    commonSubjoinData.setDecisionDate(HijriDateService.getHijriSysDate());
	    subjoinList = new ArrayList<MovementTransactionData>();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    for (int i = 0; i < subjoinList.size(); i++) {
		subjoinList.get(i).setDecisionNumber(commonSubjoinData.getDecisionNumber());
		subjoinList.get(i).setDecisionDate(commonSubjoinData.getDecisionDate());
		subjoinList.get(i).setRemarks(commonSubjoinData.getRemarks());
		subjoinList.get(i).setAttachments(commonSubjoinData.getAttachments());
		subjoinList.get(i).setReasonType(commonSubjoinData.getReasonType());
		subjoinList.get(i).setApprovedId(loginEmpData.getEmpId());
	    }
	    MovementsService.handleMovementsTransactions(null, subjoinList, false, null, MovementTransactionViewsEnum.REGISTRATION.getCode());
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void addNewSubjoinTransaction() {
	try {
	    MovementTransactionData movementTransactionData = MovementsService.constructMovementTransaction(commonSubjoinData.getEmployeeId(), HijriDateService.getHijriSysDate(), null, null, null, null, null, LocationFlagsEnum.EXTERNAL.getCode(), null, null, null, null, MovementTypesEnum.SUBJOIN.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
	    movementTransactionData.setExecutionDate(HijriDateService.getHijriSysDate());
	    subjoinList.add(movementTransactionData);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void deleteSubjoin(MovementTransactionData subjoin) {
	subjoinList.remove(subjoin);
    }

    public void manipulateEndDate(MovementTransactionData subjoin) {
	try {
	    if (subjoin.getExecutionDateString() != null && ((subjoin.getPeriodMonths() != null && subjoin.getPeriodMonths() > 0) || (subjoin.getPeriodDays() != null && subjoin.getPeriodDays() > 0))) {
		subjoin.setEndDateString(HijriDateService.addSubStringHijriMonthsDays(subjoin.getExecutionDateString(), subjoin.getPeriodMonths() == null ? 0 : subjoin.getPeriodMonths(), subjoin.getPeriodDays() == null ? -1 : subjoin.getPeriodDays() - 1));
	    } else
		subjoin.setEndDateString(null);
	} catch (Exception e) {
	    subjoin.setEndDateString(null);
	}
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

    public MovementTransactionData getCommonSubjoinData() {
	return commonSubjoinData;
    }

    public void setCommonSubjoinData(MovementTransactionData commonSubjoinData) {
	this.commonSubjoinData = commonSubjoinData;
    }

    public List<MovementTransactionData> getSubjoinList() {
	return subjoinList;
    }

    public void setSubjoinList(List<MovementTransactionData> subjoinList) {
	this.subjoinList = subjoinList;
    }
}
