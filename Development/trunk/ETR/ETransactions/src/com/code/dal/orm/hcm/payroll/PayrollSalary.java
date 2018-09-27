package com.code.dal.orm.hcm.payroll;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_payrollSalary_searchPayrollSalary",
		query = " select s " +
			" from PayrollSalary s where " +
			" (:P_RANK_ID = -1 or s.rankId = :P_RANK_ID) " +
			" and (:P_DEGREE_ID = -1 or s.degreeId = :P_DEGREE_ID) "),
	@NamedQuery(name = "hcm_payrollSalary_searchPayrollNewSalary",
		query = " select s " +
			" from PayrollSalary s " +
			" where s.rankId = :P_RANK_ID " +
			"   and s.basicSalary >= :P_BASIC_SALARY " +
			" order by s.basicSalary "),

	@NamedQuery(name = "hcm_payrollSalary_searchPayrollNewSalary_demoting",
		query = " select s " +
			" from PayrollSalary s " +
			" where s.rankId = :P_RANK_ID " +
			"   and s.basicSalary <= :P_BASIC_SALARY " +
			" order by s.basicSalary DESC "),
	@NamedQuery(name = "hcm_payrollSalary_getEndOfLadderOfRank",
		query = " select s.degreeId from PayrollSalary s " +
			" where s.rankId = :P_RANK_ID " +
			" and s.degreeId = (select max(m.degreeIdd) from PayrollSalary m where m.rankId = s.rankId)")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_VW_PRL_SALARIES")
@IdClass(PayrollSalaryId.class)
public class PayrollSalary extends BaseEntity implements Serializable {
    private Long rankId;
    private Long degreeId;
    private Double basicSalary;
    private Double transportationAllowance;

    @Id
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    @Id
    @Column(name = "DEGREE_ID")
    public Long getDegreeId() {
	return degreeId;
    }

    public void setDegreeId(Long degreeId) {
	this.degreeId = degreeId;
    }

    @Basic
    @Column(name = "BASIC_SALARY")
    public Double getBasicSalary() {
	return basicSalary;
    }

    public void setBasicSalary(Double basicSalary) {
	this.basicSalary = basicSalary;
    }

    @Basic
    @Column(name = "TRANSPORTATION_ALLOWANCE")
    public Double getTransportationAllowance() {
	return transportationAllowance;
    }

    public void setTransportationAllowance(Double transportationAllowance) {
	this.transportationAllowance = transportationAllowance;
    }
}