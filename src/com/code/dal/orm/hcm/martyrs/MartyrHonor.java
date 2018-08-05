package com.code.dal.orm.hcm.martyrs;

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
import javax.persistence.Transient;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

/**
 * Provides all information regarding martyr honors including details of both honors of type 1 and type 2 like honor number and honor date<br/>
 */

@NamedQueries({
	@NamedQuery(name = "hcm_martyrHonor_searchMartyrHonors",
		query = " select mh" +
			" from MartyrHonor mh "
			+ " where ( mh.martyrTransactionId = :P_MARTYR_TRANSACTION_ID ) "
			+ " and ( :P_HONOR_TYPE = -1 or mh.honorType = :P_HONOR_TYPE ) "
			+ " order by honorDate")
})
@Entity
@Table(name = "HCM_PRS_MARTYRS_HONORS")
public class MartyrHonor extends AuditEntity implements InsertableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long martyrTransactionId;
    private Integer honorType;
    private String honorNumber;
    private Date honorDate;
    private String honorDateString;
    private String honorDescription;
    

    @SequenceGenerator(name = "HCMMartyrsSeq",
	    sequenceName = "HCM_PRS_MARTYRS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMMartyrsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "MARTYR_TRANSACTION_ID")
    public Long getMartyrTransactionId() {
	return martyrTransactionId;
    }

    public void setMartyrTransactionId(Long martyrTransactionId) {
	this.martyrTransactionId = martyrTransactionId;
    }

    @Basic
    @Column(name = "HONOR_TYPE")
    public Integer getHonorType() {
	return honorType;
    }

    public void setHonorType(Integer honorType) {
	this.honorType = honorType;
    }

    @Basic
    @Column(name = "HONOR_NUMBER")
    public String getHonorNumber() {
	return honorNumber;
    }

    public void setHonorNumber(String honorNumber) {
	this.honorNumber = honorNumber;
    }

    @Basic
    @Column(name = "HONOR_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getHonorDate() {
	return honorDate;
    }

    public void setHonorDate(Date honorDate) {
	this.honorDate = honorDate;
	honorDateString = HijriDateService.getHijriDateString(honorDate);
    }

    @Transient
    public String getHonorDateString() {
	return honorDateString;
    }

    public void setHonorDateString(String honorDateString) {
	this.honorDateString = honorDateString;
	honorDate = HijriDateService.getHijriDate(honorDateString);
    }

    @Basic
    @Column(name = "HONOR_DESCRIPTION")
    public String getHonorDescription() {
	return honorDescription;
    }

    public void setHonorDescription(String honorDescription) {
	this.honorDescription = honorDescription;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "id:" + id + AUDIT_SEPARATOR +
		"martyrId:" + martyrTransactionId + AUDIT_SEPARATOR +
		"honorType:" + honorType + AUDIT_SEPARATOR +
		"honorNumber:" + honorNumber + AUDIT_SEPARATOR +
		"honorDate:" + honorDate + AUDIT_SEPARATOR +
		"honorDescription:" + honorDescription + AUDIT_SEPARATOR;
    }
}
