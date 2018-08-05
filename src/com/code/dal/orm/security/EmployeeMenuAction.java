package com.code.dal.orm.security;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "sec_menu_getEmployeeMenuActions",
		query = " select ema from EmployeeMenuAction ema" +
			" where ema.employeeId = :P_EMP_ID " +
			" and ema.menuCode = :P_MENU_CODE " +
			" and ema.moduleId = :P_MODULE_ID "),

	@NamedQuery(name = "sec_menu_checkEmployeeMenuAction",
		query = " select ema from EmployeeMenuAction ema" +
			" where ema.employeeId = :P_EMP_ID " +
			" and ema.menuCode = :P_MENU_CODE " +
			" and ema.moduleId = :P_MODULE_ID " +
			" and ema.action = :P_ACTION ")
})
@Entity
@Table(name = "ETR_VW_EMPS_MENUS_ACTIONS")
public class EmployeeMenuAction extends BaseEntity implements Serializable {

    private Long employeeMenuActionId;
    private Long menuId;
    private Long employeeId;
    private String menuCode;
    private Long moduleId;
    private String action;
    private Long beneficiaryRegionId;
    private Long beneficiaryUnitId;
    private String beneficiaryUnitHKey;

    @Id
    @Column(name = "ID")
    public Long getEmployeeMenuActionId() {
	return employeeMenuActionId;
    }

    public void setEmployeeMenuActionId(Long employeeMenuActionId) {
	this.employeeMenuActionId = employeeMenuActionId;
    }

    @Basic
    @Column(name = "MENU_ID")
    public Long getMenuId() {
	return menuId;
    }

    public void setMenuId(Long menuId) {
	this.menuId = menuId;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "MENU_CODE")
    public String getMenuCode() {
	return menuCode;
    }

    public void setMenuCode(String menuCode) {
	this.menuCode = menuCode;
    }

    @Basic
    @Column(name = "MODULE_ID")
    public Long getModuleId() {
	return moduleId;
    }

    public void setModuleId(Long moduleId) {
	this.moduleId = moduleId;
    }

    @Basic
    @Column(name = "ACTION")
    public String getAction() {
	return action;
    }

    public void setAction(String action) {
	this.action = action;
    }

    @Basic
    @Column(name = "BENEFICAIRY_REGION_ID")
    public Long getBeneficiaryRegionId() {
	return beneficiaryRegionId;
    }

    public void setBeneficiaryRegionId(Long beneficiaryRegionId) {
	this.beneficiaryRegionId = beneficiaryRegionId;
    }

    @Basic
    @Column(name = "BENEFICAIRY_UNIT_ID")
    public Long getBeneficiaryUnitId() {
	return beneficiaryUnitId;
    }

    public void setBeneficiaryUnitId(Long beneficiaryUnitId) {
	this.beneficiaryUnitId = beneficiaryUnitId;
    }

    @Basic
    @Column(name = "BENEFICIARY_UNIT_HKEY")
    public String getBeneficiaryUnitHKey() {
	return beneficiaryUnitHKey;
    }

    public void setBeneficiaryUnitHKey(String beneficiaryUnitHKey) {
	this.beneficiaryUnitHKey = beneficiaryUnitHKey;
    }
}