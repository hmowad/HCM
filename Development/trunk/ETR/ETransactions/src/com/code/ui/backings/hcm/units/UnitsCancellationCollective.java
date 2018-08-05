package com.code.ui.backings.hcm.units;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.UnitsCollectiveService;
import com.code.services.util.HijriDateService;
import com.code.ui.util.UnitTreeNode;

@ManagedBean(name = "unitsCancellationCollective")
@SessionScoped
public class UnitsCancellationCollective extends UnitTreeBase {

    private List<UnitData> selectedUnits = new ArrayList<UnitData>();

    private String decisionNumber;
    private Date decisionDate;
    private String unitTypeDescription;

    private final int pageSize = 10;

    public void init() {
	try {
	    super.init();
	    selectedUnits = new ArrayList<UnitData>();
	    decisionNumber = "";
	    decisionDate = HijriDateService.getHijriSysDate();
	    unitTypeDescription = "";

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void click(UnitTreeNode unitItem) {
	try {
	    super.click(unitItem);
	    addUnitToSelectedUnits();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void addUnitToSelectedUnits() throws BusinessException {
	if (selectedUnits.isEmpty()) {
	    selectedUnits.add(selectedUnitData);
	    unitTypeDescription = selectedUnitData.getUnitTypeDesc();
	}
	else {
	    for (UnitData unit : selectedUnits) {
		if (unit.getId().longValue() == selectedUnitData.getId().longValue()) {
		    super.setServerSideErrorMessages(getMessage("error_cannotAddTheSameUnitAgain"));
		    return;
		}
		if (unit.getUnitTypeId().longValue() != selectedUnitData.getUnitTypeId().longValue()) {
		    super.setServerSideErrorMessages(getMessage("error_allUnitsShouldHaveSameType"));
		    return;
		}
	    }
	    selectedUnits.add(selectedUnitData);
	}
    }

    public void deleteUnit(UnitData unit) {
	selectedUnits.remove(unit);
	if (selectedUnits.isEmpty()) {
	    init();
	}
    }

    public void save() {
	try {
	    UnitsCollectiveService.cancelUnits(decisionNumber, decisionDate, selectedUnits, this.loginEmpData.getEmpId());
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    init();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public List<UnitData> getSelectedUnits() {
	return selectedUnits;
    }

    public void setSelectedUnits(List<UnitData> selectedUnits) {
	this.selectedUnits = selectedUnits;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    public String getUnitTypeDescription() {
	return unitTypeDescription;
    }

    public int getPageSize() {
	return pageSize;
    }

}
