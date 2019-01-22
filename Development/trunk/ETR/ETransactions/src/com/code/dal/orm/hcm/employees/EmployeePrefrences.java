package com.code.dal.orm.hcm.employees;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;
import com.code.enums.FlagsEnum;

@NamedQueries({
	@NamedQuery(name = "hcm_empPrefrences_getEmployeePrefrencesById",
		query = " select e from EmployeePrefrences e" +
			" where e.id = :P_EMP_ID ")
})
@Entity
@Table(name = "HCM_PRS_EMPLOYEES_PREFRENCES")
public class EmployeePrefrences extends BaseEntity {
    private Long id;
    private Integer timeLineAutoHideFlag;
    private Boolean timeLineAutoHideFlagBoolean;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "TIMELINE_AUTO_HIDE_FLAG")
    public Integer getTimeLineAutoHideFlag() {
	return timeLineAutoHideFlag;
    }

    public void setTimeLineAutoHideFlag(Integer timeLineAutoHideFlag) {
	this.timeLineAutoHideFlag = timeLineAutoHideFlag;
	if (this.timeLineAutoHideFlag == null || this.timeLineAutoHideFlag == FlagsEnum.OFF.getCode()) {
	    this.timeLineAutoHideFlagBoolean = false;
	} else {
	    this.timeLineAutoHideFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getTimeLineAutoHideFlagBoolean() {
	return timeLineAutoHideFlagBoolean;
    }

    public void setTimeLineAutoHideFlagBoolean(Boolean timeLineAutoHideFlagBoolean) {
	this.timeLineAutoHideFlagBoolean = timeLineAutoHideFlagBoolean;
	if (this.timeLineAutoHideFlagBoolean == false) {
	    setTimeLineAutoHideFlag(FlagsEnum.OFF.getCode());
	} else {
	    setTimeLineAutoHideFlag(FlagsEnum.ON.getCode());
	}
    }

}
