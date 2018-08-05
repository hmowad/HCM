package com.code.ui.backings.hcm.units;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.organization.units.UnitType;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.UnitsCollectiveService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;
import com.code.ui.util.UnitTreeNode;

@ManagedBean(name = "unitsConstructionCollective")
@SessionScoped
public class UnitsConstructionCollective extends UnitTreeBase {

    private List<UnitData> selectedUnits = new ArrayList<UnitData>();
    private String decisionNumber;
    private Date decisionDate;
    private List<UnitType> allUnitTypes;
    private List<UnitType> availableUnitTypes;
    private UnitData newUnit;
    private final int pageSize = 10;

    public UnitsConstructionCollective() {
	try {
	    allUnitTypes = UnitsService.getAllUnitTypes();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void init() {
	try {
	    super.init();
	    selectedUnits = new ArrayList<UnitData>();
	    decisionNumber = "";
	    decisionDate = HijriDateService.getHijriSysDate();
	    newUnit = new UnitData();
	    availableUnitTypes = UnitsService.getAvailableUnitTypes(selectedUnitData, allUnitTypes);

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
	if (selectedUnits.isEmpty())
	    availableUnitTypes = UnitsService.getAvailableUnitTypes(selectedUnitData, allUnitTypes);
	selectedUnits.add(selectedUnitData);
    }

    public void deleteUnit(UnitData unit) {
	selectedUnits.remove(unit);
	if (selectedUnits.isEmpty()) {
	    init();
	}
    }

    public void save() {
	try {
	    UnitsCollectiveService.constructUnits(decisionNumber, decisionDate, selectedUnits, newUnit, this.loginEmpData.getEmpId());
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
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

    public List<UnitType> getAvailableUnitTypes() {
	return availableUnitTypes;
    }

    public UnitData getNewUnit() {
	return newUnit;
    }

    public void setNewUnit(UnitData newUnit) {
	this.newUnit = newUnit;
    }

    public int getPageSize() {
	return pageSize;
    }

}
