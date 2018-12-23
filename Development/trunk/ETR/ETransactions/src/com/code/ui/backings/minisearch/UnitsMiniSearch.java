package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.UnitsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "unitsMiniSearch")
@ViewScoped
public class UnitsMiniSearch extends BaseBacking implements Serializable {

    private int rowsCount = 10;
    private String searchUnitFullName;
    private int unitTypeCode;
    private List<UnitData> searchUnitList;
    private String mode;
    private long unitRegionId;
    private long unitParentId;
    private String unitHKeyPrefix;
    private String unitsIdsString;

    private boolean multipleSelectFlag;
    private final int SELECTED_UNITS_MAX = 100;
    private List<UnitData> selectedUnitList;
    private String selectedUnitsIds;
    private String comma;

    public UnitsMiniSearch() {
	mode = getRequest().getParameter("mode");
	unitTypeCode = Integer.valueOf(getRequest().getParameter("unitTypeCode"));
	unitRegionId = Long.valueOf(getRequest().getParameter("unitRegionId"));
	unitParentId = Long.valueOf(getRequest().getParameter("unitParentId"));
	unitHKeyPrefix = getRequest().getParameter("unitHKeyPrefix");
	unitsIdsString = getRequest().getParameter("unitsIdsString");

	if (getRequest().getParameter("multipleSelectFlag") != null) {
	    multipleSelectFlag = Integer.parseInt(getRequest().getParameter("multipleSelectFlag")) == 1;
	    selectedUnitList = new ArrayList<UnitData>();
	    selectedUnitsIds = "";
	    comma = "";
	    if (multipleSelectFlag) {
		rowsCount = 5;
	    }
	}

    }

    public void searchUnit() {
	try {
	    searchUnitList = null;
	    if (mode.equals("1")) {
		searchUnitList = UnitsService.getUnitsByCompatibleChildType(unitTypeCode, searchUnitFullName, unitRegionId);
	    } else if (mode.equals("2")) {
		searchUnitList = UnitsService.getUnitsByRegion(unitRegionId, searchUnitFullName);
	    } else if (mode.equals("3")) {
		searchUnitList = UnitsService.getUnitsByParentByTypeByName(searchUnitFullName, (unitTypeCode == FlagsEnum.ALL.getCode()) ? FlagsEnum.ALL.getCode() : UnitsService.getUnitTypeByCode(unitTypeCode).getId(), unitParentId, unitRegionId);
	    }
	    // get all children of unit at all levels including parent unit itself
	    // used in internal assignment [o,s,p]
	    else if (mode.equals("4")) {
		searchUnitList = UnitsService.getAllUnitLevelsChildrensByName(unitParentId, searchUnitFullName);
	    } else if (mode.equals("5")) {
		searchUnitList = UnitsService.getRegionsAndSectorsByName(searchUnitFullName);
	    } else if (mode.equals("6")) {
		searchUnitList = UnitsService.getTrainingCentersAndInstitutesByName(searchUnitFullName);
	    } else if (mode.equals("7")) {
		searchUnitList = UnitsService.getUnitsByPrefixHkey(unitHKeyPrefix, searchUnitFullName);
	    } else if (mode.equals("8")) {
		searchUnitList = UnitsService.getUnitsByIdsStringAndUnitFullName(unitsIdsString, searchUnitFullName);
	    }
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addSelectedUnit(UnitData unit) {
	// Check MAX list size
	if (selectedUnitList.size() == SELECTED_UNITS_MAX) {
	    super.setServerSideErrorMessages(getMessage("error_unitsListMax"));
	    return;
	}

	// Check duplicate records
	if (("," + selectedUnitsIds + ",").contains("," + unit.getId() + ",")) {
	    super.setServerSideErrorMessages(getMessage("error_repeatedUnit"));
	    return;
	}

	// Add to list
	searchUnitList.remove(unit);
	selectedUnitList.add(unit);
	selectedUnitsIds += comma + unit.getId();
	comma = ",";
    }

    public void removeSelectedUnit(UnitData unit) {
	selectedUnitList.remove(unit);
	searchUnitList.add(unit);

	if (selectedUnitsIds.equals(unit.getId() + "")) {
	    selectedUnitsIds = "";
	    comma = "";
	} else {
	    selectedUnitsIds = ("," + selectedUnitsIds + ",").replace("," + unit.getId() + ",", ",");
	    selectedUnitsIds = selectedUnitsIds.substring(1, selectedUnitsIds.length() - 1);
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public String getSearchUnitFullName() {
	return searchUnitFullName;
    }

    public void setSearchUnitFullName(String searchUnitFullName) {
	this.searchUnitFullName = searchUnitFullName;
    }

    public List<UnitData> getSearchUnitList() {
	return searchUnitList;
    }

    public void setSearchUnitList(List<UnitData> searchUnitList) {
	this.searchUnitList = searchUnitList;
    }

    public String getSelectedUnitsIds() {
	return selectedUnitsIds;
    }

    public void setSelectedUnitsIds(String selectedUnitsIds) {
	this.selectedUnitsIds = selectedUnitsIds;
    }

    public List<UnitData> getSelectedUnitList() {
	return selectedUnitList;
    }

    public void setSelectedUnitList(List<UnitData> selectedUnitList) {
	this.selectedUnitList = selectedUnitList;
    }

    public boolean isMultipleSelectFlag() {
	return multipleSelectFlag;
    }

    public String getUnitsIdsString() {
	return unitsIdsString;
    }

    public void setUnitsIdsString(String unitsIdsString) {
	this.unitsIdsString = unitsIdsString;
    }

}
