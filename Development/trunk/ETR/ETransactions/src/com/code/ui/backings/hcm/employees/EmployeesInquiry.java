package com.code.ui.backings.hcm.employees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "employeesInquiry")
@ViewScoped
public class EmployeesInquiry extends BaseBacking implements Serializable {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Persons
     **/
    private int mode;

    List<EmployeeData> employeesList;

    // For Search
    private String name;
    private String socialId;
    private Long unitId;
    private Long regionId;
    private Long jobId;
    private Long minorSpecId;
    private Integer militaryNumber;
    private Long sequenceNumber;

    private List<Region> regionsList;

    private int pageSize = 10;

    public EmployeesInquiry() {
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		setScreenTitle(getMessage("title_officersDataInquiry"));
		break;
	    case 2:
		setScreenTitle(getMessage("title_soldiersDataInquiry"));
		break;
	    case 3:
		setScreenTitle(getMessage("title_personsDataInquiry"));
		break;
	    default:
		setServerSideErrorMessages(getMessage("error_general"));
	    }
	    resetForm();
	} else
	    setServerSideErrorMessages(getMessage("error_general"));

	regionsList = CommonService.getAllRegions();
    }

    public void searchEmployees() {
	try {
	    if (name != null && name.isEmpty())
		name = null;
	    if (socialId != null && socialId.isEmpty())
		socialId = null;
	    employeesList = EmployeesService.getEmpByPhysicalOrOfficialUnit(name, getCategoriesIdsArrayByMode(mode), militaryNumber == null ? FlagsEnum.ALL.getCode() : militaryNumber, socialId, null, null, jobId == null ? FlagsEnum.ALL.getCode() : jobId, unitId == null ? FlagsEnum.ALL.getCode() : unitId, minorSpecId == null ? FlagsEnum.ALL.getCode() : minorSpecId, regionId == null ? FlagsEnum.ALL.getCode() : regionId, sequenceNumber);
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	name = null;
	socialId = null;
	regionId = getLoginEmpPhysicalRegionFlag(true);
	unitId = null;
	jobId = null;
	minorSpecId = null;
	militaryNumber = null;
	employeesList = new ArrayList<EmployeeData>();
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    public List<EmployeeData> getEmployeesList() {
	return employeesList;
    }

    public void setEmployeesList(List<EmployeeData> employeesList) {
	this.employeesList = employeesList;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getSocialId() {
	return socialId;
    }

    public void setSocialId(String socialId) {
	this.socialId = socialId;
    }

    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
    }

    public Long getMinorSpecId() {
	return minorSpecId;
    }

    public void setMinorSpecId(Long minorSpecId) {
	this.minorSpecId = minorSpecId;
    }

    public Integer getMilitaryNumber() {
	return militaryNumber;
    }

    public void setMilitaryNumber(Integer militaryNumber) {
	this.militaryNumber = militaryNumber;
    }

    public Long getSequenceNumber() {
	return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
	this.sequenceNumber = sequenceNumber;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    public List<Region> getRegionsList() {
	return regionsList;
    }

    public void setRegionsList(List<Region> regionsList) {
	this.regionsList = regionsList;
    }
}
