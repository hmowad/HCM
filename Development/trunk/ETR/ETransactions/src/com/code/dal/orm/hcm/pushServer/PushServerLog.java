package com.code.dal.orm.hcm.pushServer;

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

import com.code.dal.orm.BaseEntity;

@Entity
@Table(name = "BG_PUSH_SERVER_LOG")
public class PushServerLog extends BaseEntity {

    private Long id;
    private Long employeeId;
    private Integer operation;
    private Date operationDate;

    private String deviceToken;
    private String deviceType;
    private String osName;
    private String osVersion;

    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "BGPushServerLogSeq",
	    sequenceName = "BG_PUSH_SERVER_LOG_SEQ")
    @GeneratedValue(generator = "BGPushServerLogSeq")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "OPERATION")
    public Integer getOperation() {
	return operation;
    }

    public void setOperation(Integer operation) {
	this.operation = operation;
    }

    @Basic
    @Column(name = "OPERATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getOperationDate() {
	return operationDate;
    }

    public void setOperationDate(Date operationDate) {
	this.operationDate = operationDate;
    }

    @Basic
    @Column(name = "DEVICE_TOKEN")
    public String getDeviceToken() {
	return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
	this.deviceToken = deviceToken;
    }

    @Basic
    @Column(name = "DEVICE_TYPE")
    public String getDeviceType() {
	return deviceType;
    }

    public void setDeviceType(String deviceType) {
	this.deviceType = deviceType;
    }

    @Basic
    @Column(name = "OS_NAME")
    public String getOsName() {
	return osName;
    }

    public void setOsName(String osName) {
	this.osName = osName;
    }

    @Basic
    @Column(name = "OS_VERSION")
    public String getOsVersion() {
	return osVersion;
    }

    public void setOsVersion(String osVersion) {
	this.osVersion = osVersion;
    }

}
