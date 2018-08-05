package com.code.dal.orm.hcm.exercises;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * The <code>EmployeeExercise</code> class represents the employee exercise data.
 * 
 */
@Entity
@Table(name = "HCM_PRS_EMPS_EXERCISES")
public class EmployeeExercise extends AuditEntity implements InsertableAuditEntity {

    private Long id;
    private Long exerciseId;
    private Long employeeId;
    private Integer period;
    private Date startDate;
    private Date endDate;

    @SequenceGenerator(name = "HCMExercisesSeq",
	    sequenceName = "HCM_EXERCISES_SEQ")
    @Id
    @GeneratedValue(generator = "HCMExercisesSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "EXERCISE_ID")
    public Long getExerciseId() {
	return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
	this.exerciseId = exerciseId;
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
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "exerciseId:" + exerciseId + AUDIT_SEPARATOR +
		"employeeId:" + employeeId + AUDIT_SEPARATOR +
		"period:" + period + AUDIT_SEPARATOR +
		"startDate:" + startDate + AUDIT_SEPARATOR +
		"endDate:" + endDate;
    }
}
