package com.code.dal.orm.hcm.movements;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_movementWishTransaction_searchMovementWishTransactions",
		query = " select m from MovementWishTransaction m " +
			" where " +
			" 	(:P_EMPLOYEE_ID = -1 or m.employeeId = :P_EMPLOYEE_ID) " +
			"   and (:P_STATUS_IDS_FLAG = -1 or m.status in (:P_STATUS_IDS))"),

	@NamedQuery(name = "hcm_movementWishTransaction_getMovementWishInvalidTransactions",
		query = " select m from MovementWishTransaction m, EmployeeData e " +
			" where " +
			" 	m.employeeId = e.empId " +
			"   and m.status in (1,2) " +
			"   and (m.fromRegionId <> e.officialRegionId or e.officialRegionId is null) ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_MVT_WISHES_TRANSACTIONS")
public class MovementWishTransaction extends BaseEntity {
    private Long id;
    private Long employeeId;
    private Long fromRegionId;
    private Long toRegionId;
    private String reasons;
    private Integer status;
    private String attachments;
    private Date transDate;
    private Date transLastUpdateDate;

    @SequenceGenerator(name = "HCMMovementsWishesSeq",
	    sequenceName = "HCM_MVT_WISHES_SEQ")
    @Id
    @GeneratedValue(generator = "HCMMovementsWishesSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Basic
    @Column(name = "FROM_REGION_ID")
    public Long getFromRegionId() {
	return fromRegionId;
    }

    public void setFromRegionId(Long fromRegionId) {
	this.fromRegionId = fromRegionId;
    }

    @Basic
    @Column(name = "TO_REGION_ID")
    public Long getToRegionId() {
	return toRegionId;
    }

    public void setToRegionId(Long toRegionId) {
	this.toRegionId = toRegionId;
    }

    @Basic
    @Column(name = "REASONS")
    public String getReasons() {
	return reasons;
    }

    public void setReasons(String reasons) {
	this.reasons = reasons;
    }

    @Basic
    @Column(name = "STATUS")
    public Integer getStatus() {
	return status;
    }

    public void setStatus(Integer status) {
	this.status = status;
    }

    @Basic
    @Column(name = "ATTACHMENTS")
    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    @Basic
    @Column(name = "TRANS_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransDate() {
	return transDate;
    }

    public void setTransDate(Date transDate) {
	this.transDate = transDate;
    }

    @Basic
    @Column(name = "TRANS_LAST_UPDATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getTransLastUpdateDate() {
	return transLastUpdateDate;
    }

    public void setTransLastUpdateDate(Date transLastUpdateDate) {
	this.transLastUpdateDate = transLastUpdateDate;
    }

}
