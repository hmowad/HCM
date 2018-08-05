package com.code.dal.orm.security;

import java.io.Serializable;

public class EmployeeMenuId implements Serializable {
    private Long empId;
    private Long menuId;

    public EmployeeMenuId() {
    }

    public EmployeeMenuId(Long empId, Long menuId) {
	this.empId = empId;
	this.menuId = menuId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public Long getEmpId() {
	return empId;
    }

    public void setMenuId(Long menuId) {
	this.menuId = menuId;
    }

    public Long getMenuId() {
	return menuId;
    }

    public boolean equals(Object o) {
	return ((o instanceof EmployeeMenuId) && (empId.equals(((EmployeeMenuId) o).getEmpId())) && (menuId.equals(((EmployeeMenuId) o).getMenuId())));
    }

    public int hashCode() {
	return empId.hashCode() + menuId.hashCode();
    }
}