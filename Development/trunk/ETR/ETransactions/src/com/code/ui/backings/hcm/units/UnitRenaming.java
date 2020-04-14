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
@ManagedBean(name = "unitRenaming")
@SessionScoped
public class UnitRenaming extends UnitTreeBase implements Serializable {
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

    public void update() {
	try {
	    UnitsService.renameUnit(selectedUnitData, decDate, decNumber, TransactionTypesEnum.UNIT_NAME_MODIFICATION.getCode(), this.loginEmpData.getEmpId());
	    selectedUnitTreeNode.setUnitName(selectedUnitData.getNewName());
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
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
