package com.code.ui.backings.hcm.units;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;
import com.code.ui.util.UnitTreeNode;

@SuppressWarnings("serial")
@ManagedBean(name = "unitSeparation")
@SessionScoped
public class UnitSeparation extends UnitTreeBase implements Serializable {
    private List<UnitData> unitDataList;

    private UnitData selectedUnitTableData;

    private String decNumber;
    private Date decDate;
    private String decDateString;

    private int pageSize = 10;

    public UnitSeparation() {
    }

    public void init() {
	try {
	    super.init();
	    decDate = HijriDateService.getHijriSysDate();
	    unitDataList = new ArrayList<UnitData>();
	    enroll();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void saveUnitItem() {
	this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
    }

    public void click(UnitTreeNode unitItem) {
	super.click(unitItem);
	unitDataList = new ArrayList<UnitData>();
	decNumber = "";
	enroll();
    }

    public void search() {
	super.search();
	unitDataList = new ArrayList<UnitData>();
	decNumber = "";
	selectedUnitTableData = new UnitData();
    }

    public void enroll() {
	try {
	    unitDataList = UnitsService.getUnitChildren(selectedUnitData.getId(), false, false, FlagsEnum.ON.getCode());
	    selectedUnitTableData = new UnitData();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectFromTable(UnitData unit) {
	selectedUnitTableData = unit;
    }

    public void separate() {
	try {
	    UnitsService.separateUnits(selectedUnitData, unitDataList, decNumber, decDate, this.loginEmpData.getEmpId());
	    init();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	    decNumber = "";
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
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

    public String getDecDateString() {
	return decDateString;
    }

    public void setDecDateString(String decDateString) {
	this.decDateString = decDateString;
    }

    public List<UnitData> getUnitDataList() {
	return unitDataList;
    }

    public void setUnitDataList(List<UnitData> unitDataList) {
	this.unitDataList = unitDataList;
    }

    public UnitData getSelectedUnitTableData() {
	return selectedUnitTableData;
    }

    public void setSelectedUnitTableData(UnitData selectedUnitTableData) {
	this.selectedUnitTableData = selectedUnitTableData;
    }
}
