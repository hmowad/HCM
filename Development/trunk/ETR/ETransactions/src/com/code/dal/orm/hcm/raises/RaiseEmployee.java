package com.code.dal.orm.hcm.raises;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_raiseEmployee_deleteRaiseEmployeesByRaiseId",
		query = "delete from RaiseEmployee r where r.raiseId = :P_RAISE_ID"),
	@NamedQuery(name = "hcm_raiseEmployee_updateEmployeesAfterAnnualRaise",
		query = "update Employee e set e.degreeId = e.degreeId + 1 , e.lastAnnualRaiseDate = to_date(:P_LAST_ANNUAL_RAISE_DATE, 'MI/MM/YYYY') where id in(select r.empId from RaiseEmployee r where raiseId = :P_RAISE_ID and deservedFlag = 1)"),
	@NamedQuery(name = "hcm_raiseEmployee_getEmployeesByRaiseId",
		query = "select e from Employee e where id in(select r.empId from RaiseEmployee r where raiseId = :P_RAISE_ID and deservedFlag = 1)")
})

@Entity
@Table(name = "HCM_RAISES_EMPLOYEES")
public class RaiseEmployee extends BaseEntity {
    private Long id;
    private Long raiseId;
    private Long empId;
    private String exclusionReason;
    private Long newDegreeId;
    private Integer deservedFlag;

    @GenericGenerator(name = "HCMRaiseSeq",
	    strategy = "enhanced-sequence",
	    parameters = {
		    @org.hibernate.annotations.Parameter(
			    name = "sequence_name",
			    value = "HCM_RAISE_SEQ"),
		    @org.hibernate.annotations.Parameter(
			    name = "optimizer",
			    value = "pooled-lo"),
		    @org.hibernate.annotations.Parameter(
			    name = "increment_size",
			    value = "30") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
	    generator = "HCMRaiseSeq")
    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "RAISE_ID")
    public Long getRaiseId() {
	return raiseId;
    }

    public void setRaiseId(Long raiseId) {
	this.raiseId = raiseId;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "EXCLUSION_REASON")
    public String getExclusionReason() {
	return exclusionReason;
    }

    public void setExclusionReason(String exclusionReason) {
	this.exclusionReason = exclusionReason;
    }

    @Basic
    @Column(name = "NEW_DEGREE_ID")
    public Long getNewDegreeId() {
	return newDegreeId;
    }

    public void setNewDegreeId(Long newDegreeId) {
	this.newDegreeId = newDegreeId;
    }

    @Basic
    @Column(name = "DESERVED_FLAG")
    public Integer getDeservedFlag() {
	return deservedFlag;
    }

    public void setDeservedFlag(Integer deservedFlag) {
	this.deservedFlag = deservedFlag;
    }
}
