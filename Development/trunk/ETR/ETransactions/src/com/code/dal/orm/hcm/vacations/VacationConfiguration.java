package com.code.dal.orm.hcm.vacations;

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
 * The <code>VacationConfiguration</code> class represents the period of each vacation type for all categories (Officers, Soldiers, and Employees). The period is represented by balance, minimum and maximum vacation days within specific duration.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_vacationConfig_getVacationConfigurations",
		query = " select v from VacationConfiguration v " +
			" where v.categoryId = :P_CATEGORY_ID " +
			" and v.vacationTypeId = :P_VACATION_TYPE_ID " +
			" order by fromDate asc "),

	@NamedQuery(name = "hcm_vacationConfig_getActiveVacationConfigurations",
		query = " select v from VacationConfiguration v " +
			" where v.categoryId = :P_CATEGORY_ID " +
			" and v.vacationTypeId = :P_VACATION_TYPE_ID " +
			" and (:P_SUB_VACATION_TYPE = -1 or v.subVacationType = :P_SUB_VACATION_TYPE) " +
			" and (:P_LOCATION_FLAG = -1 or v.locationFlag = :P_LOCATION_FLAG) " +
			" and v.active = 1" +
			"order by paidVacationType asc"),

	@NamedQuery(name = "hcm_vacationConfig_searchVacationsConfigurations",
		query = " select vc, vt, c.description " +
			" from VacationConfiguration vc ,VacationType vt, Category c" +
			" where vc.vacationTypeId = vt.vacationTypeId " +
			" and vc.categoryId = c.id " +
			" and (:P_CATEGORY_ID = -1 or vc.categoryId = :P_CATEGORY_ID) " +
			" and (:P_VACATION_TYPE_ID = -1 or vc.vacationTypeId = :P_VACATION_TYPE_ID) " +
			" order by vc.categoryId asc, vc.vacationTypeId asc, vc.subVacationType asc, vc.paidVacationType asc, vc.locationFlag asc, vc.fromDate desc")
})
@Entity
@Table(name = "HCM_VAC_CONFIG")
public class VacationConfiguration extends BaseEntity {

    private Long id;
    private Date fromDate;
    private String fromDateString;
    private Date toDate;
    private String toDateString;
    private Long categoryId;
    private Long categoryClassificationId;
    private Integer active;
    private Long vacationTypeId;
    private Integer subVacationType;
    private Integer paidVacationType;
    private Integer locationFlag;
    private Integer balance;
    private Integer minValue;
    private Integer maxValue;
    private String allowedValues;
    private Integer periodBeforeFirstVacation;
    private Integer maxVacationsDaysPerYear;
    private Integer maxVacationsDaysPerYearExceptional;
    private Integer firstYearMaxVacationDays;
    private Integer balanceFrame;
    private Integer externalMinVacationDays;
    private Integer additionalVacationDays;
    private Integer maxValueExceptional;
    private String periods;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "FROM_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
	this.fromDateString = HijriDateService.getHijriDateString(fromDate);
    }

    @Transient
    public String getFromDateString() {
	return fromDateString;
    }

    public void setFromDateString(String fromDateString) {
	this.fromDateString = fromDateString;
	this.fromDate = HijriDateService.getHijriDate(fromDateString);
    }

    @Basic
    @Column(name = "TO_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
	this.toDateString = HijriDateService.getHijriDateString(toDate);
    }

    @Transient
    public String getToDateString() {
	return toDateString;
    }

    public void setToDateString(String toDateString) {
	this.toDateString = toDateString;
	this.toDate = HijriDateService.getHijriDate(toDateString);
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "CATEGORY_CLASSIFICATION_ID")
    public Long getCategoryClassificationId() {
	return categoryClassificationId;
    }

    public void setCategoryClassificationId(Long categoryClassificationId) {
	this.categoryClassificationId = categoryClassificationId;
    }

    @Basic
    @Column(name = "ACTIVE")
    public Integer getActive() {
	return active;
    }

    public void setActive(Integer active) {
	this.active = active;
    }

    @Basic
    @Column(name = "VACATION_TYPE_ID")
    public Long getVacationTypeId() {
	return vacationTypeId;
    }

    public void setVacationTypeId(Long vacationTypeId) {
	this.vacationTypeId = vacationTypeId;
    }

    @Basic
    @Column(name = "SUB_VACATION_TYPE")
    public Integer getSubVacationType() {
	return subVacationType;
    }

    public void setSubVacationType(Integer subVacationType) {
	this.subVacationType = subVacationType;
    }

    @Basic
    @Column(name = "PAID_VACATION_TYPE")
    public Integer getPaidVacationType() {
	return paidVacationType;
    }

    public void setPaidVacationType(Integer paidVacationType) {
	this.paidVacationType = paidVacationType;
    }

    @Basic
    @Column(name = "LOCATION_FLAG")
    public Integer getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Integer locationFlag) {
	this.locationFlag = locationFlag;
    }

    @Basic
    @Column(name = "BALANCE")
    public Integer getBalance() {
	return balance;
    }

    public void setBalance(Integer balance) {
	this.balance = balance;
    }

    @Basic
    @Column(name = "MIN_VALUE")
    public Integer getMinValue() {
	return minValue;
    }

    public void setMinValue(Integer minValue) {
	this.minValue = minValue;
    }

    @Basic
    @Column(name = "MAX_VALUE")
    public Integer getMaxValue() {
	return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
	this.maxValue = maxValue;
    }

    @Basic
    @Column(name = "ALLOWED_VALUES")
    public String getAllowedValues() {
	return allowedValues;
    }

    public void setAllowedValues(String allowedValues) {
	this.allowedValues = allowedValues;
    }

    @Basic
    @Column(name = "PERIOD_BEFORE_FIRST_VACATION")
    public Integer getPeriodBeforeFirstVacation() {
	return periodBeforeFirstVacation;
    }

    public void setPeriodBeforeFirstVacation(Integer periodBeforeFirstVacation) {
	this.periodBeforeFirstVacation = periodBeforeFirstVacation;
    }

    @Basic
    @Column(name = "MAX_VACATIONS_DAYS_PER_YEAR")
    public Integer getMaxVacationsDaysPerYear() {
	return maxVacationsDaysPerYear;
    }

    public void setMaxVacationsDaysPerYear(Integer maxVacationsDaysPerYear) {
	this.maxVacationsDaysPerYear = maxVacationsDaysPerYear;
    }

    @Basic
    @Column(name = "MAX_VACS_DAYS_PER_YEAR_EXCP")
    public Integer getMaxVacationsDaysPerYearExceptional() {
	return maxVacationsDaysPerYearExceptional;
    }

    public void setMaxVacationsDaysPerYearExceptional(Integer maxVacationsDaysPerYearExceptional) {
	this.maxVacationsDaysPerYearExceptional = maxVacationsDaysPerYearExceptional;
    }

    @Basic
    @Column(name = "FIRST_YEAR_MAX_VACATIONS_DAYS")
    public Integer getFirstYearMaxVacationDays() {
	return firstYearMaxVacationDays;
    }

    public void setFirstYearMaxVacationDays(Integer firstYearMaxVacationDays) {
	this.firstYearMaxVacationDays = firstYearMaxVacationDays;
    }

    @Basic
    @Column(name = "BALANCE_FRAME")
    public Integer getBalanceFrame() {
	return balanceFrame;
    }

    public void setBalanceFrame(Integer balanceFrame) {
	this.balanceFrame = balanceFrame;
    }

    @Basic
    @Column(name = "EXTERNAL_MIN_VAC_DAYS")
    public Integer getExternalMinVacationDays() {
	return externalMinVacationDays;
    }

    public void setExternalMinVacationDays(Integer externalMinVacationDays) {
	this.externalMinVacationDays = externalMinVacationDays;
    }

    @Basic
    @Column(name = "ADDITIONAL_VACATION_DAYS")
    public Integer getAdditionalVacationDays() {
	return additionalVacationDays;
    }

    public void setAdditionalVacationDays(Integer additionalVacationDays) {
	this.additionalVacationDays = additionalVacationDays;
    }

    @Basic
    @Column(name = "MAX_VALUE_EXCP")
    public Integer getMaxValueExceptional() {
	return maxValueExceptional;
    }

    public void setMaxValueExceptional(Integer maxValueExceptional) {
	this.maxValueExceptional = maxValueExceptional;
    }

    @Basic
    @Column(name = "PERIODS")
    public String getPeriods() {
	return periods;
    }

    public void setPeriods(String periods) {
	this.periods = periods;
    }

}
