package com.code.dal.orm.workflow.hcm.vacations;

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
 * The <code>WFVacationData</code> class represents the vacation request data in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "wf_vacationData_getUnitRunningWFVacationsData",
		query = " select v from WFVacationData v " +
			" where v.processId not in (:P_EXCLUDED_PROCESSES_IDS) " +
			" and v.unitId in (select id from UnitData where hKey like :P_PREFIX_HKEY) " +
			" and (:P_CATEGORIES_IDS_FLAG = -1  or v.empCategoryId in (:P_CATEGORIES_IDS) ) " +
			" and v.requestType <> 4 " +
			" and v.instanceStatus = 1 " +
			" order by v.empName "),

	@NamedQuery(name = "wf_vacationData_countUnitRunningWFVacationsData",
		query = " select count(v.instanceId) from WFVacationData v " +
			" where v.processId not in (:P_EXCLUDED_PROCESSES_IDS) " +
			" and v.unitId in (select id from UnitData where hKey like :P_PREFIX_HKEY) " +
			" and (to_date(:P_SELECTED_DATE, 'MI/MM/YYYY') between v.startDate and v.endDate) " +
			" and (:P_CATEGORIES_IDS_FLAG = -1  or v.empCategoryId in (:P_CATEGORIES_IDS) ) " +
			" and v.requestType <> 4 " +
			" and v.instanceStatus = 1 ")

})
@Entity
@Table(name = "ETR_VW_WF_VACATIONS")
public class WFVacationData extends BaseEntity {
    private Long instanceId;
    private Long processId;
    private String processName;
    private Integer requestType;
    private Integer instanceStatus;
    private Long empId;
    private String empName;
    private Long unitId;
    private Date startDate;
    private String startDateString;
    private Date endDate;
    private String endDateString;
    private Integer period;
    private Long empCategoryId;

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Id
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setProcessId(Long processId) {
	this.processId = processId;
    }

    @Id
    @Column(name = "PROCESS_ID")
    public Long getProcessId() {
	return processId;
    }

    public void setProcessName(String processName) {
	this.processName = processName;
    }

    @Basic
    @Column(name = "PROCESS_NAME")
    public String getProcessName() {
	return processName;
    }

    public void setRequestType(Integer requestType) {
	this.requestType = requestType;
    }

    @Basic
    @Column(name = "REQUEST_TYPE")
    public Integer getRequestType() {
	return requestType;
    }

    public void setInstanceStatus(Integer instanceStatus) {
	this.instanceStatus = instanceStatus;
    }

    @Basic
    @Column(name = "INSTANCE_STATUS")
    public Integer getInstanceStatus() {
	return instanceStatus;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "BENEFICIARY_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    @Basic
    @Column(name = "EMP_NAME")
    public String getEmpName() {
	return empName;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
	this.startDateString = HijriDateService.getHijriDateString(startDate);
    }

    @Basic
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
	this.startDate = HijriDateService.getHijriDate(startDateString);
    }

    @Transient
    public String getStartDateString() {
	return startDateString;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
	this.endDateString = HijriDateService.getHijriDateString(endDate);
    }

    @Basic
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
	this.endDate = HijriDateService.getHijriDate(endDateString);
    }

    @Transient
    public String getEndDateString() {
	return endDateString;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    @Basic
    @Column(name = "PERIOD")
    public Integer getPeriod() {
	return period;
    }

    @Basic
    @Column(name = "EMP_CATEGORY_ID")
    public Long getEmpCategoryId() {
	return empCategoryId;
    }

    public void setEmpCategoryId(Long empCategoryId) {
	this.empCategoryId = empCategoryId;
    }
}