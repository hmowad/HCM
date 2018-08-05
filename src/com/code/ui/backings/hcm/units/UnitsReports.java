package com.code.ui.backings.hcm.units;

import java.io.Serializable;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "unitsReports")
@ViewScoped
public class UnitsReports extends BaseBacking implements Serializable {
    private int reportType;

    private String selectedUnitFullName;
    private String selectedUnitHKey;
    private long selectedUnitId;
    private Date fromDate;
    private Date toDate;

    private long unitTransactionType;

    public UnitsReports() {
	reportType = 10;
	resetForm();
    }

    public void resetForm() {
	unitTransactionType = -1;
	try {
	    UnitData unit = UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0);
	    selectedUnitFullName = unit.getFullName();
	    selectedUnitHKey = unit.gethKey();
	    selectedUnitId = unit.getId();
	    fromDate = toDate = HijriDateService.getHijriSysDate();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    String reportTitle = "";
	    if (reportType == 10) {
		reportTitle = getMessage("label_reportOrganizationHierarchy") + " (" + selectedUnitFullName + ")";
	    } else if (reportType == 20) {
		reportTitle = getMessage("label_reportUnitsTransactionsDetails") + " (" + selectedUnitFullName + ")";
	    } else if (reportType == 30) {
		reportTitle = getMessage("label_reportUnitsTransactionsStatistics") + " (" + selectedUnitFullName + ")";
	    } else if (reportType == 40) {
		reportTitle = getMessage("label_reportUnitTransactions") + " (" + selectedUnitFullName + ")";
	    } else {
		reportTitle = getMessage("label_reportUnitsTransactionsRequired") + " (" + selectedUnitFullName + ")";
	    }

	    byte[] bytes = UnitsService.getUnitsReportsBytes(reportType, selectedUnitHKey, selectedUnitId, fromDate, toDate, unitTransactionType, reportTitle);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public String getSelectedUnitFullName() {
	return selectedUnitFullName;
    }

    public void setSelectedUnitFullName(String selectedUnitFullName) {
	this.selectedUnitFullName = selectedUnitFullName;
    }

    public String getSelectedUnitHKey() {
	return selectedUnitHKey;
    }

    public void setSelectedUnitHKey(String selectedUnitHKey) {
	this.selectedUnitHKey = selectedUnitHKey;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    public long getUnitTransactionType() {
	return unitTransactionType;
    }

    public void setUnitTransactionType(long unitTransactionType) {
	this.unitTransactionType = unitTransactionType;
    }

    public long getSelectedUnitId() {
	return selectedUnitId;
    }

    public void setSelectedUnitId(long selectedUnitId) {
	this.selectedUnitId = selectedUnitId;
    }

}
