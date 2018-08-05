package com.code.dal.orm.hcm.exercises;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>EmployeeExerciseData</code> class represents the employee exercise data in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_employeeExerciseData_getEmployeeExerciseData",
		query = " select e from EmployeeExerciseData e " +
			" where (:P_EXERCISE_ID = -1 or e.exerciseId = :P_EXERCISE_ID) " +
			" and (:P_EMPLOYEE_ID = -1 or e.employeeId = :P_EMPLOYEE_ID) " +
			" and (:P_START_DATE_FLAG = -1 or (to_date (:P_START_DATE ,'MI/MM/YYYY') >= e.startDate and to_date (:P_START_DATE ,'MI/MM/YYYY') < e.endDate) " +
			" or (:P_END_DATE_FLAG = -1 or (to_date (:P_END_DATE ,'MI/MM/YYYY') > e.startDate and to_date (:P_END_DATE ,'MI/MM/YYYY') <= e.endDate))) ")
})

@Entity
@Table(name = "HCM_VW_PRS_EMPS_EXERCISES")
public class EmployeeExerciseData extends BaseEntity {

    private Long id;
    private Long exerciseId;
    private Long employeeId;
    private Integer period;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;

    private EmployeeExercise employeeExercise;

    public EmployeeExerciseData() {
	employeeExercise = new EmployeeExercise();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	employeeExercise.setId(id);
    }

    @Basic
    @Column(name = "EXERCISE_ID")
    public Long getExerciseId() {
	return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
	this.exerciseId = exerciseId;
	employeeExercise.setExerciseId(exerciseId);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
	employeeExercise.setEmployeeId(employeeId);
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
	try {
	    if (startDate != null && period != null && period > 0)
		this.setEndDateString(HijriDateService.addSubStringHijriDays(startDateString, period - 1));
	    else
		this.setEndDateString(null);
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setEndDateString(null);
	}
	employeeExercise.setPeriod(period);
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
	this.startDateString = HijriDateService.getHijriDateString(startDate);
	try {
	    if (startDate != null && period != null && period > 0)
		this.setEndDateString(HijriDateService.addSubStringHijriDays(startDateString, period - 1));
	    else
		this.setEndDateString(null);
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setEndDateString(null);
	}
	employeeExercise.setStartDate(startDate);
    }

    @Transient
    public String getStartDateString() {
	return startDateString;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
	this.startDate = HijriDateService.getHijriDate(startDateString);
	try {
	    if (startDate != null && period != null && period > 0)
		this.setEndDateString(HijriDateService.addSubStringHijriDays(startDateString, period - 1));
	    else
		this.setEndDateString(null);
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setEndDateString(null);
	}
	employeeExercise.setStartDate(this.startDate);
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
	this.endDateString = HijriDateService.getHijriDateString(endDate);
	employeeExercise.setEndDate(endDate);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
	employeeExercise.setEndDate(this.endDate);
    }

    @Transient
    public EmployeeExercise getEmployeeExercise() {
	return employeeExercise;
    }

    public void setEmployeeExercise(EmployeeExercise employeeExercise) {
	this.employeeExercise = employeeExercise;
    }
}