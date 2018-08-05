package com.code.ui.backings.hcm.units;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.TransactionType;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.organization.units.UnitType;
import com.code.enums.FlagsEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.UnitsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.util.UnitTreeNode;

@SuppressWarnings("serial")
@ManagedBean(name = "unitScale")
@SessionScoped
public class UnitScale extends UnitTreeBase implements Serializable {
    private List<UnitData> unitDataList;
    private List<UnitType> allUnitTypeList;

    private TransactionType transTypeScaleUp;
    private TransactionType transTypeScaleDown;

    private long selectedTransTypeId;

    private UnitData selectedUnitDataTransaction;
    private UnitData selectedUnitTableData;
    private String decNumber;
    private Date decDate;
    private String decDateString;
    private long oldTypeId;

    private int pageSize = 10;

    public UnitScale() {
	try {
	    selectedUnitDataTransaction = new UnitData();
	    transTypeScaleUp = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.UNIT_SCALE_UP.getCode(), TransactionClassesEnum.UNITS.getCode());
	    transTypeScaleDown = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.UNIT_SCALE_DOWN.getCode(), TransactionClassesEnum.UNITS.getCode());
	    selectedTransTypeId = transTypeScaleUp.getId();

	    decDate = HijriDateService.getHijriSysDate();

	    allUnitTypeList = UnitsService.getAllUnitTypes();
	    for (int i = 0; i < allUnitTypeList.size(); i++) {
		if (allUnitTypeList.get(i).getCode().equals(UnitTypesEnum.PRESIDENCY.getCode()) || allUnitTypeList.get(i).getCode().equals(UnitTypesEnum.REGION_COMMANDER.getCode())) {
		    allUnitTypeList.remove(i);
		    i--;
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void init() {
	super.init();
	enroll();
	decNumber = "";
    }

    public void saveUnitItem() {
	this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
    }

    public void click(UnitTreeNode unitItem) {
	super.click(unitItem);
	unitDataList = new ArrayList<UnitData>();
	oldTypeId = selectedUnitData.getUnitTypeId();
	decNumber = "";
	enroll();
    }

    public void search() {
	super.search();
	unitDataList = new ArrayList<UnitData>();
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

    public void save() {
	try {
	    int transactionCode = transTypeScaleUp.getCode();
	    if (transTypeScaleUp.getId().longValue() != selectedTransTypeId) {
		transactionCode = transTypeScaleDown.getCode();
	    }
	    UnitsService.scaleUnit(transactionCode, super.selectedUnitData, decNumber, decDate, unitDataList, this.loginEmpData.getEmpId());
	    decNumber = "";
	    click(selectedUnitTreeNode);
	    super.init();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public UnitData getSelectedUnitDataTransaction() {
	return selectedUnitDataTransaction;
    }

    public void setSelectedUnitDataTransaction(UnitData selectedUnitDataTransaction) {
	this.selectedUnitDataTransaction = selectedUnitDataTransaction;
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

    public List<UnitType> getAllUnitTypeList() {
	return allUnitTypeList;
    }

    public void setAllUnitTypeList(List<UnitType> allUnitTypeList) {
	this.allUnitTypeList = allUnitTypeList;
    }

    public UnitData getSelectedUnitTableData() {
	return selectedUnitTableData;
    }

    public void setSelectedUnitTableData(UnitData selectedUnitTableData) {
	this.selectedUnitTableData = selectedUnitTableData;
    }

    public long getSelectedTransTypeId() {
	return selectedTransTypeId;
    }

    public void setSelectedTransTypeId(long selectedTransTypeId) {
	this.selectedTransTypeId = selectedTransTypeId;
    }

    public long getOldTypeId() {
	return oldTypeId;
    }

    public void setOldTypeId(long oldTypeId) {
	this.oldTypeId = oldTypeId;
    }

    public TransactionType getTransTypeScaleUp() {
	return transTypeScaleUp;
    }

    public void setTransTypeScaleUp(TransactionType transTypeScaleUp) {
	this.transTypeScaleUp = transTypeScaleUp;
    }

    public TransactionType getTransTypeScaleDown() {
	return transTypeScaleDown;
    }

    public void setTransTypeScaleDown(TransactionType transTypeScaleDown) {
	this.transTypeScaleDown = transTypeScaleDown;
    }

}
