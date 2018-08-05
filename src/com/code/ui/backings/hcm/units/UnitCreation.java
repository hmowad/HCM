package com.code.ui.backings.hcm.units;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.organization.units.UnitType;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.UnitsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.util.UnitTreeNode;

@SuppressWarnings("serial")
@ManagedBean(name = "unitCreation")
@SessionScoped
public class UnitCreation extends UnitTreeBase implements Serializable {
    private final static Integer GAP_SIZE = 5;
    private final static Integer MAX_ORDER_UNDER_PARENT = 99;
    private List<UnitType> allUnitTypeList;
    private List<Region> allUnitRegionList;
    private UnitData newUnit;
    private String decNumber;
    private Date decDate;
    private String decDateString;

    public UnitCreation() {
	try {
	    allUnitTypeList = UnitsService.getAllUnitTypes();
	    allUnitRegionList = CommonService.getAllRegions();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void init() {
	try {
	    super.init();
	    initNewUnit();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void initNewUnit() throws BusinessException {
	newUnit = new UnitData();
	newUnit.setOrderUnderParent(Math.min((UnitsService.getMaximumOrderUnderParent(super.selectedUnitData.getId()) + GAP_SIZE), MAX_ORDER_UNDER_PARENT));
	newUnit.setRegionId(selectedUnitData.getRegionId());

	newUnit.setTargetsArchivedFlag(FlagsEnum.OFF.getCode());
	newUnit.setDutiesArchivedFlag(FlagsEnum.OFF.getCode());
	newUnit.setJobsArchivedFlag(FlagsEnum.OFF.getCode());
	newUnit.setOrganizationalArchivedFlag(FlagsEnum.OFF.getCode());
	newUnit.setEquipmentsArchivedFlag(FlagsEnum.OFF.getCode());

	decDate = HijriDateService.getHijriSysDate();
	decNumber = "";
    }

    public void click(UnitTreeNode unitItem) {
	try {
	    super.click(unitItem);
	    initNewUnit();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    for (Region region : allUnitRegionList) {
		if (region.getId().longValue() == newUnit.getRegionId().longValue()) {
		    newUnit.setRegionCode(region.getCode());
		    break;
		}
	    }

	    UnitsService.addUnit(super.selectedUnitData, newUnit, decDate, decNumber, this.loginEmpData.getEmpId());
	    if (selectedUnitTreeNode.isVisited() || selectedUnitTreeNode.isLeaf()) {
		super.addChildUnderSelected(newUnit.getId(), newUnit.getName(), newUnit.getUnitTypeCode() + "", newUnit.getOrderUnderParent() + "");
		selectedUnitTreeNode.setVisited(true);
	    }
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    initNewUnit();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<UnitType> getAllUnitTypeList() {
	return UnitsService.getAvailableUnitTypes(super.selectedUnitData, allUnitTypeList);
    }

    public UnitData getNewUnit() {
	return newUnit;
    }

    public void setNewUnit(UnitData newUnit) {
	this.newUnit = newUnit;
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
	this.decDateString = HijriDateService.getHijriDateString(decDate);
    }

    public String getDecDateString() {
	return decDateString;
    }

    public void setDecDateString(String decDateString) {
	this.decDateString = decDateString;
	this.decDate = HijriDateService.getHijriDate(decDateString);
    }

    public List<Region> getAllUnitRegionList() {
	return allUnitRegionList;
    }

    public void setAllUnitRegionList(List<Region> allUnitRegionList) {
	this.allUnitRegionList = allUnitRegionList;
    }
}
