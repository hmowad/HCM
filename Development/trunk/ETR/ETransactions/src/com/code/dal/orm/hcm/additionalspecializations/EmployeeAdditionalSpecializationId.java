package com.code.dal.orm.hcm.additionalspecializations;

import java.io.Serializable;

public class EmployeeAdditionalSpecializationId implements Serializable {
    private Long empId;
    private Long additionalSpecializationId;
    
    public EmployeeAdditionalSpecializationId() {}

    public EmployeeAdditionalSpecializationId(Long empId, Long additionalSpecializationId) {
        this.empId = empId;
        this.additionalSpecializationId = additionalSpecializationId;
    }

    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public Long getAdditionalSpecializationId() {
        return additionalSpecializationId;
    }

    public void setAdditionalSpecializationId(Long additionalSpecializationId) {
        this.additionalSpecializationId = additionalSpecializationId;
    }

    public boolean equals(Object o) {
        return ( (o instanceof EmployeeAdditionalSpecializationId) && (empId.equals(((EmployeeAdditionalSpecializationId)o).getEmpId())) && (additionalSpecializationId.equals(((EmployeeAdditionalSpecializationId)o).getAdditionalSpecializationId())) );
    }
    
    public int hashCode() {
        return empId.hashCode() + additionalSpecializationId.hashCode();
    }
}
