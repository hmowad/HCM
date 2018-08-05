package com.code.ui.backings.hcm.additionalspecializations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.additionalspecializations.EmployeeAdditionalSpecializationData;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.AdditionalSpecializationsService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeesAdditionalSpecializationsInquiry")
@ViewScoped
public class EmployeesAdditionalSpecializationsInquiry extends BaseBacking implements Serializable {

    List<EmployeeAdditionalSpecializationData> employeeAdditionalSpecializationDataList;
    private Long searchAdditionalSpecId;
    private String searchAdditionalSpecDesc;
    private Long physicalRegionId;

    private int pageSize = 10;

    public EmployeesAdditionalSpecializationsInquiry() {
	searchAdditionalSpecId = null;
	searchAdditionalSpecDesc = null;
	employeeAdditionalSpecializationDataList = new ArrayList<EmployeeAdditionalSpecializationData>();
	physicalRegionId = loginEmpData.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? FlagsEnum.ALL.getCode() : loginEmpData.getPhysicalRegionId();
    }

    public void searchEmployeesAdditionalSpecialization() {
	try {
	    employeeAdditionalSpecializationDataList = AdditionalSpecializationsService.getEmployeesAdditionalSpecializationDataByAdditionalSpecializationIdAndPhysicalRegionId(searchAdditionalSpecId, physicalRegionId);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<EmployeeAdditionalSpecializationData> getEmployeeAdditionalSpecializationDataList() {
	return employeeAdditionalSpecializationDataList;
    }

    public void setEmployeeAdditionalSpecializationDataList(List<EmployeeAdditionalSpecializationData> employeeAdditionalSpecializationDataList) {
	this.employeeAdditionalSpecializationDataList = employeeAdditionalSpecializationDataList;
    }

    public Long getSearchAdditionalSpecId() {
	return searchAdditionalSpecId;
    }

    public void setSearchAdditionalSpecId(Long searchAdditionalSpecId) {
	this.searchAdditionalSpecId = searchAdditionalSpecId;
    }

    public String getSearchAdditionalSpecDesc() {
	return searchAdditionalSpecDesc;
    }

    public void setSearchAdditionalSpecDesc(String searchAdditionalSpecDesc) {
	this.searchAdditionalSpecDesc = searchAdditionalSpecDesc;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }
}
