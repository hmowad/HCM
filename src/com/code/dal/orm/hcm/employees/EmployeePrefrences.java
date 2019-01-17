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
	@NamedQuery(name = "hcm_empPrefrences_searchEmployeePrefrencesById",
		query = " select e from EmployeePrefrences e" +
			" where e.id = :P_EMP_ID ")
})
@Entity
@Table(name = "HCM_PRS_EMPLOYEES_PREFRENCES")
public class EmployeePrefrences extends BaseEntity {
    private Long id;
    private Integer timeLineAutoShowFlag;
    private Boolean timeLineAutoShowFlagBoolean;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "TIMELINE_AUTO_SHOW_FLAG")
    public Integer getTimeLineAutoShowFlag() {
	return timeLineAutoShowFlag;
    }

    public void setTimeLineAutoShowFlag(Integer timeLineAutoShowFlag) {
	this.timeLineAutoShowFlag = timeLineAutoShowFlag;
	if (this.timeLineAutoShowFlag == null || this.timeLineAutoShowFlag == FlagsEnum.OFF.getCode()) {
	    this.timeLineAutoShowFlagBoolean = false;
	} else {
	    this.timeLineAutoShowFlagBoolean = true;
	}
    }

    @Transient
    public Boolean getTimeLineAutoShowFlagBoolean() {
	return timeLineAutoShowFlagBoolean;
    }

    public void setTimeLineAutoShowFlagBoolean(Boolean timeLineAutoShowFlagBoolean) {
	this.timeLineAutoShowFlagBoolean = timeLineAutoShowFlagBoolean;
	if (this.timeLineAutoShowFlagBoolean == false) {
	    setTimeLineAutoShowFlag(FlagsEnum.OFF.getCode());
	} else {
	    setTimeLineAutoShowFlag(FlagsEnum.ON.getCode());
	}
    }

}
