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

@ManagedBean(name = "moveRegistration")
@ViewScoped
public class MoveRegistration extends BaseBacking implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;
    private int pageSize = 10;

    private Long selectedEmpId;
    MovementTransactionData commonMoveData;

    private List<MovementTransactionData> MoveTransactionsList;

    public MoveRegistration() {
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    this.init();
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersExternalMoveRegistration"));
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsExternalMoveRegistration"));
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} else
	    setServerSideErrorMessages(getMessage("error_URLError"));
    }

    public void init() {
	try {
	    commonMoveData = new MovementTransactionData();
	    commonMoveData.setDecisionDate(HijriDateService.getHijriSysDate());
	    MoveTransactionsList = new ArrayList<MovementTransactionData>();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewMoveTransaction() {
	try {
	    MovementTransactionData move = MovementsService.constructMovementTransaction(selectedEmpId, null, null, null, null, null, null, LocationFlagsEnum.EXTERNAL.getCode(), null, null, null, null, MovementTypesEnum.MOVE.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
	    move.setExecutionDate(HijriDateService.getHijriSysDate());
	    MoveTransactionsList.add(move);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void deleteMoveRegistration(MovementTransactionData move) {
	MoveTransactionsList.remove(move);
    }

    public void save() {
	try {
	    for (int i = 0; i < MoveTransactionsList.size(); i++) {
		MoveTransactionsList.get(i).setApprovedId(loginEmpData.getEmpId());
		MoveTransactionsList.get(i).setDecisionNumber(commonMoveData.getDecisionNumber());
		MoveTransactionsList.get(i).setDecisionDate(commonMoveData.getDecisionDate());
		MoveTransactionsList.get(i).setReasonType(commonMoveData.getReasonType());
		MoveTransactionsList.get(i).setRemarks(commonMoveData.getRemarks());
		MoveTransactionsList.get(i).setAttachments(commonMoveData.getAttachments());
	    }
	    MovementsService.handleMovementsTransactions(null, MoveTransactionsList, false, null, MovementTransactionViewsEnum.REGISTRATION.getCode());
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
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

    public Long getSelectedEmpId() {
	return selectedEmpId;
    }

    public void setSelectedEmpId(Long selectedEmpId) {
	this.selectedEmpId = selectedEmpId;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public List<MovementTransactionData> getMoveTransactionsList() {
	return MoveTransactionsList;
    }

    public MovementTransactionData getCommonMoveData() {
	return commonMoveData;
    }
}
