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
 * The <code>ExerciseData</code> class represents the exercise data in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_exerciseData_getExercisesData",
		query = " select e from ExerciseData e " +
			" where (:P_EXERCISE_ID = -1 or e.id = :P_EXERCISE_ID) " +
			" order by e.startDate desc ")
})
@Entity
@Table(name = "HCM_VW_EXERCISES")
public class ExerciseData extends BaseEntity {

    private Long id;
    private String name;
    private Integer locationFlag;
    private Integer period;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;

    private Exercise exercise;

    public ExerciseData() {
	exercise = new Exercise();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	exercise.setId(id);
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
	exercise.setName(name);
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
	exercise.setLocationFlag(locationFlag);
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
	exercise.setPeriod(period);
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
	exercise.setStartDate(startDate);
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
	exercise.setStartDate(this.startDate);
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
	exercise.setEndDate(endDate);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
	exercise.setEndDate(this.endDate);
    }

    @Transient
    public Exercise getExercise() {
	return exercise;
    }

    public void setExercise(Exercise exercise) {
	this.exercise = exercise;
    }
}