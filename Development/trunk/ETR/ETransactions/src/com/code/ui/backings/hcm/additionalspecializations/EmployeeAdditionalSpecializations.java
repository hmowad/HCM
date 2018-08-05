package com.code.ui.backings.hcm.additionalspecializations;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.additionalspecializations.EmployeeAdditionalSpecializationData;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.AdditionalSpecializationsService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeeAdditionalSpecializations")
@ViewScoped
public class EmployeeAdditionalSpecializations extends BaseBacking implements Serializable {

    List<EmployeeAdditionalSpecializationData> employeeAdditionalSpecializationDataList;
    private Long selectedEmpId;
    private String selectedEmpName;
    private long selectedAdditionalSpecId;
    private String selectedAdditionalSpecDesc;
    private boolean mvtBlackListFlagGrant;
    private boolean mvtUnBlackListFlagGrant;

    private Long physicalRegionId;

    private int pageSize = 10;

    public EmployeeAdditionalSpecializations() {
	selectedEmpId = loginEmpData.getEmpId();
	selectedEmpName = loginEmpData.getName();
	physicalRegionId = loginEmpData.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? FlagsEnum.ALL.getCode() : loginEmpData.getPhysicalRegionId();
	searchEmployeeAdditionalSpecializations();

	try {

	    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_ADDITIONAL_SPECS.getCode(), MenuActionsEnum.PRS_ADDITIONAL_SPECS_MVT_BLACKLIST.getCode()))
		mvtBlackListFlagGrant = true;
	    if (SecurityService.isEmployeeMenuActionGranted(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_ADDITIONAL_SPECS.getCode(), MenuActionsEnum.PRS_ADDITIONAL_SPECS_MVT_UNBLACKLIST.getCode()))
		mvtUnBlackListFlagGrant = true;

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public Long getPhysicalRegionId() {
	return physicalRegionId;
    }

    public void setPhysicalRegionId(Long physicalRegionId) {
	this.physicalRegionId = physicalRegionId;
    }

    public void searchEmployeeAdditionalSpecializations() {
	try {
	    employeeAdditionalSpecializationDataList = AdditionalSpecializationsService.getEmployeeAdditionalSpecializationsDataByEmpId(selectedEmpId);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addEmployeeAdditionalSpecialization() {
	try {
	    EmployeeAdditionalSpecializationData employeeAdditionalSpecializationData = new EmployeeAdditionalSpecializationData();
	    employeeAdditionalSpecializationData.setEmpId(selectedEmpId);
	    employeeAdditionalSpecializationData.setAdditionalSpecializationId(selectedAdditionalSpecId);
	    employeeAdditionalSpecializationData.setAdditionalSpecializationDesc(selectedAdditionalSpecDesc);
	    employeeAdditionalSpecializationData.setMovementBlacklistFlagBoolean(false);
	    employeeAdditionalSpecializationData.getEmployeeAdditionalSpecialization().setSystemUser(this.loginEmpData.getEmpId() + ""); // For auditing.
	    AdditionalSpecializationsService.addEmployeeAdditionalSpecialization(employeeAdditionalSpecializationData);
	    employeeAdditionalSpecializationDataList.add(employeeAdditionalSpecializationData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateEmployeeAdditionalSpecialization(EmployeeAdditionalSpecializationData employeeAdditionalSpecializationData) {
	try {
	    employeeAdditionalSpecializationData.getEmployeeAdditionalSpecialization().setSystemUser(this.loginEmpData.getEmpId() + ""); // For auditing.
	    employeeAdditionalSpecializationData.setMovementBlacklistFlagBoolean(!employeeAdditionalSpecializationData.getMovementBlacklistFlagBoolean());
	    AdditionalSpecializationsService.changeEmpAdditionalSpecMvtBlackListFlag(employeeAdditionalSpecializationData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    employeeAdditionalSpecializationData.setMovementBlacklistFlagBoolean(!employeeAdditionalSpecializationData.getMovementBlacklistFlagBoolean());
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteEmployeeAdditionalSpecialization(EmployeeAdditionalSpecializationData employeeAdditionalSpecializationData) {
	try {
	    employeeAdditionalSpecializationData.getEmployeeAdditionalSpecialization().setSystemUser(this.loginEmpData.getEmpId() + ""); // For auditing.
	    AdditionalSpecializationsService.deleteEmployeeAdditionalSpecialization(employeeAdditionalSpecializationData);
	    employeeAdditionalSpecializationDataList.remove(employeeAdditionalSpecializationData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
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

    public Long getSelectedEmpId() {
	return selectedEmpId;
    }

    public void setSelectedEmpId(Long selectedEmpId) {
	this.selectedEmpId = selectedEmpId;
    }

    public String getSelectedEmpName() {
	return selectedEmpName;
    }

    public void setSelectedEmpName(String selectedEmpName) {
	this.selectedEmpName = selectedEmpName;
    }

    public long getSelectedAdditionalSpecId() {
	return selectedAdditionalSpecId;
    }

    public void setSelectedAdditionalSpecId(long selectedAdditionalSpecId) {
	this.selectedAdditionalSpecId = selectedAdditionalSpecId;
    }

    public String getSelectedAdditionalSpecDesc() {
	return selectedAdditionalSpecDesc;
    }

    public void setSelectedAdditionalSpecDesc(String selectedAdditionalSpecDesc) {
	this.selectedAdditionalSpecDesc = selectedAdditionalSpecDesc;
    }

    public boolean isMvtBlackListFlagGrant() {
	return mvtBlackListFlagGrant;
    }

    public void setMvtBlackListFlagGrant(boolean mvtBlackListFlagGrant) {
	this.mvtBlackListFlagGrant = mvtBlackListFlagGrant;
    }

    public boolean isMvtUnBlackListFlagGrant() {
	return mvtUnBlackListFlagGrant;
    }

    public void setMvtUnBlackListFlagGrant(boolean mvtUnBlackListFlagGrant) {
	this.mvtUnBlackListFlagGrant = mvtUnBlackListFlagGrant;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }
}
