package com.code.ui.backings.hcm.navyformations;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.NavyFormationsService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "employeesNavyFormationReport")
@ViewScoped
public class EmployeesNavyFormationReport extends BaseBacking {
    private int mode;
    private long regionId;
    private long unitId;
    private String unitFullName;
    private long empId;
    private String empName;
    private List<Region> regionsList;
    private String navyFormationDescription;

    public EmployeesNavyFormationReport() {
	try {
	    resetForm();
	    regionsList = CommonService.getAllRegions();
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));

		if (mode == CategoryModesEnum.OFFICERS.getCode())
		    setScreenTitle(getMessage("title_officersNavyFormationReport"));
		else if (mode == CategoryModesEnum.SOLDIERS.getCode())
		    setScreenTitle(getMessage("title_soldiersNavyFormationReport"));
		else
		    setServerSideErrorMessages(getMessage("error_URLError"));

	    } else {
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void regionChangeListener() throws BusinessException {

	unitId = FlagsEnum.ALL.getCode();
	unitFullName = "";
	empId = FlagsEnum.ALL.getCode();
	empName = "";
    }

    public void resetForm() {

	regionId = getLoginEmpPhysicalRegionFlag(true);
	unitId = FlagsEnum.ALL.getCode();
	unitFullName = null;
	empId = FlagsEnum.ALL.getCode();
	empName = null;
	navyFormationDescription = null;
    }

    public void print() {
	try {

	    byte[] bytes = NavyFormationsService.getNavyFormationDataBytes((long) mode, empId, regionId, unitId, navyFormationDescription);

	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public long getRegionId() {
	return regionId;
    }

    public void setRegionId(long regionId) {
	this.regionId = regionId;
    }

    public long getUnitId() {
	return unitId;
    }

    public void setUnitId(long unitId) {
	this.unitId = unitId;
    }

    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
    }

    public long getEmpId() {
	return empId;
    }

    public void setEmpId(long empId) {
	this.empId = empId;
    }

    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    public List<Region> getRegionsList() {
	return regionsList;
    }

    public void setRegionsList(List<Region> regionsList) {
	this.regionsList = regionsList;
    }

    public String getnavyFormationDescription() {
	return navyFormationDescription;
    }

    public void setnavyFormationDescription(String navyFormationDescription) {
	this.navyFormationDescription = navyFormationDescription;
    }

}
