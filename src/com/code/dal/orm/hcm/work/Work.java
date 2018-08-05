package com.code.dal.orm.hcm.work;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_work_getLastDeductedWorkDate",
		query = " select max(w.workDate) from Work w" +
			" where w.employeeNumber = :P_EMP_ID " +
			" and w.workKind = '13' ")
})

@Entity
@Table(name = "BG_VW_WORK")
@IdClass(WorkId.class)
public class Work implements Serializable {

    private String employeeNumber;
    private String workKind;
    private Date workDate;
    private String workDateString;

    public void setEmployeeNumber(String employeeNumber) {
	this.employeeNumber = employeeNumber;
    }

    @Id
    @Column(name = "EMP_NO")
    public String getEmployeeNumber() {
	return employeeNumber;
    }

    public void setWorkKind(String workKind) {
	this.workKind = workKind;
    }

    @Id
    @Column(name = "W_KIND")
    public String getWorkKind() {
	return workKind;
    }

    public void setWorkDate(Date workDate) {
	this.workDate = workDate;
	this.workDateString = HijriDateService.getHijriDateString(workDate);
    }

    @Id
    @Column(name = "WE_DATE_H")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getWorkDate() {
	return workDate;
    }

    public void setWorkDateString(String workDateString) {
	this.workDateString = workDateString;
	this.workDate = HijriDateService.getHijriDate(workDateString);
    }

    @Transient
    public String getWorkDateString() {
	return workDateString;
    }
}
