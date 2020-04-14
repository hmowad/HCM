package com.code.ui.backings.hcm.units;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;

@SuppressWarnings("serial")
@ManagedBean(name = "unitMoving")
@SessionScoped
public class UnitMoving extends UnitTreeBase implements Serializable {
    private String decNumber;
    private Date decDate;

    public void init() {
	try {
	    super.init();
	    decDate = HijriDateService.getHijriSysDate();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void moveUnit() {
	try {
	    UnitsService.moveUnit(selectedUnitData, UnitsService.getUnitById(selectedUnitData.getMoveToUnitId()), true, decDate, decNumber, TransactionTypesEnum.UNIT_MOVE.getCode(), this.loginEmpData.getEmpId());

	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    super.init();
	    decNumber = null;
	    decDate = HijriDateService.getHijriSysDate();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String getDecNumber() {
	return decNumber;
    }

    public void setDecNumber(String decNumber) {
	this.decNumber = decNumber;
    }

    public Date getDecDate() {
	return decDate;
    }

    public void setDecDate(Date decDate) {
	this.decDate = decDate;
    }
}
