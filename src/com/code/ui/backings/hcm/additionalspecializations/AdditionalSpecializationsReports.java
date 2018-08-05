package com.code.ui.backings.hcm.additionalspecializations;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.AdditionalSpecializationsService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "additionalSpecializationsReports")
@ViewScoped
public class AdditionalSpecializationsReports extends BaseBacking {

    private Long regionId;
    private String unitHKey;
    private Long employeeId;
    private Long additionalSpecId;

    private String unitFullName;
    private String employeeName;
    private String additionalSpecDesc;

    private List<Region> regions;
    private int reportType = 1;

    public AdditionalSpecializationsReports() {
	regions = CommonService.getAllRegions();
	reset();
    }

    public void reset() {
	regionId = getLoginEmpPhysicalRegionFlag(true);
	employeeId = additionalSpecId = (long) FlagsEnum.ALL.getCode();
	unitHKey = unitFullName = employeeName = additionalSpecDesc = "";
    }

    public void print() {
	try {
	    byte[] bytes = AdditionalSpecializationsService.getAdditionalSpecializationDataBytes(regionId, employeeId == null ? FlagsEnum.ALL.getCode() : employeeId, additionalSpecId == null ? FlagsEnum.ALL.getCode() : additionalSpecId, unitHKey.isEmpty() ? FlagsEnum.ALL.getCode() + "" : unitHKey);
	    super.print(bytes);
	} catch (BusinessException e) {
	}
    }

    public void regionChangeListener() {
	unitHKey = unitFullName = null;
    }

    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    public Long getAdditionalSpecId() {
	return additionalSpecId;
    }

    public void setAdditionalSpecId(Long additionalSpecId) {
	this.additionalSpecId = additionalSpecId;
    }

    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
    }

    public String getUnitHKey() {
	return unitHKey;
    }

    public void setUnitHKey(String unitHkey) {
	this.unitHKey = unitHkey;
    }

    public String getEmployeeName() {
	return employeeName;
    }

    public void setEmployeeName(String employeeName) {
	this.employeeName = employeeName;
    }

    public String getAdditionalSpecDesc() {
	return additionalSpecDesc;
    }

    public void setAdditionalSpecDesc(String additionalSpecDesc) {
	this.additionalSpecDesc = additionalSpecDesc;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }
}
