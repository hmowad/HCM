package com.code.ui.backings.hcm.units;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.exceptions.BusinessException;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;
import com.code.ui.util.UnitTreeNode;

@SuppressWarnings("serial")
@ManagedBean(name = "unitCancel")
@SessionScoped
public class UnitCancel extends UnitTreeBase implements Serializable {
    private String decNumber;
    private Date decDate;

    public void init() {
	try {
	    super.init();
	    decDate = HijriDateService.getHijriSysDate();
	    decNumber = "";
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void click(UnitTreeNode unitItem) {
	super.click(unitItem);
	decNumber = "";
    }

    public void cancel() {
	try {
	    UnitsService.cancelUnit(selectedUnitData, decNumber, decDate, this.loginEmpData.getEmpId());
	    super.removeSelected();
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    decNumber = null;
	    decDate = HijriDateService.getHijriSysDate();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
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
