package com.code.dal.orm.hcm.work;

import java.io.Serializable;

import java.util.Date;

public class WorkId implements Serializable{
    private String employeeNumber;
    private String workKind;
    private Date workDate;
    
    public WorkId(){}

    public WorkId(String employeeNumber, String workKind, Date workDate) {
        this.employeeNumber = employeeNumber;
        this.workKind = workKind;
        this.workDate = workDate;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setWorkKind(String workKind) {
        this.workKind = workKind;
    }

    public String getWorkKind() {
        return workKind;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof WorkId)) {
            return false;
        }
        final WorkId other = (WorkId)object;
        if (!(employeeNumber == null ? other.employeeNumber == null : employeeNumber.equals(other.employeeNumber))) {
            return false;
        }
        if (!(workKind == null ? other.workKind == null : workKind.equals(other.workKind))) {
            return false;
        }
        if (!(workDate == null ? other.workDate == null : workDate.equals(other.workDate))) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        final int PRIME = 37;
        int result = 1;
        result = PRIME * result + ((employeeNumber == null) ? 0 : employeeNumber.hashCode());
        result = PRIME * result + ((workKind == null) ? 0 : workKind.hashCode());
        result = PRIME * result + ((workDate == null) ? 0 : workDate.hashCode());
        return result;
    }
}
