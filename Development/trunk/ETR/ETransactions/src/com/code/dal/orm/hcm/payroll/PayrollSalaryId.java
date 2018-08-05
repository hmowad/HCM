package com.code.dal.orm.hcm.payroll;

import java.io.Serializable;

public class PayrollSalaryId implements Serializable {
    private Long rankId;
    private Long degreeId;

    public PayrollSalaryId() {
    }

    public PayrollSalaryId(Long rankId, Long degreeId) {
	this.rankId = rankId;
	this.degreeId = degreeId;
    }
    
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    public Long getDegreeId() {
	return degreeId;
    }

    public void setDegreeId(Long degreeId) {
	this.degreeId = degreeId;
    }

    public boolean equals(Object o) {
	return ((o instanceof PayrollSalaryId) && (rankId.equals(((PayrollSalaryId) o).getRankId())) && (degreeId.equals(((PayrollSalaryId) o).getDegreeId())));
    }

    public int hashCode() {
	return rankId.hashCode() + degreeId.hashCode();
    }
}
