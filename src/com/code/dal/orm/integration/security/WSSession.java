package com.code.dal.orm.integration.security;

import java.io.Serializable;
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

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "ws_session_getWSSession",
		query = " select s from WSSession s" +
			" where s.sessionId = :P_SESSION_ID " +
			" and s.empId = :P_EMP_ID "
	)
})
@Entity
@Table(name = "ETR_WS_SESSIONS")
public class WSSession extends BaseEntity implements Serializable {

    private String sessionId;
    private Long empId;
    private Date firstActivityTime;
    private Date lastActivityTime;

    @Id
    @Column(name = "SESSION_ID")
    public String getSessionId() {
	return sessionId;
    }

    public void setSessionId(String sessionId) {
	this.sessionId = sessionId;
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
    @Column(name = "FIRST_ACTIVITY_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getFirstActivityTime() {
	return firstActivityTime;
    }

    public void setFirstActivityTime(Date firstActivityTime) {
	this.firstActivityTime = firstActivityTime;
    }

    @Basic
    @Column(name = "LAST_ACTIVITY_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastActivityTime() {
	return lastActivityTime;
    }

    public void setLastActivityTime(Date lastActivityTime) {
	this.lastActivityTime = lastActivityTime;
    }
}
