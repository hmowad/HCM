package com.code.dal.orm.workflow.hcm;

import java.io.Serializable;
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

/**
 * The <code>WFDefinitionLetter</code> class represents the definition letter request data.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_WFDefLetter_getWFDefLetterByInstanceId",
		query = " select d from WFDefinitionLetter d " +
			" where d.instanceId = :P_INSTANCE_ID ")
})
@Entity
@Table(name = "WF_DEFINITION_LETTERS")
public class WFDefinitionLetter extends BaseEntity implements Serializable {
    private Long id;
    private String addressedTo;
    private String letterType;
    private Long relatedUserId;
    private Long instanceId;
    private String employeeEnglishName;
    private Long embassyId;
    private Date requestDate;

    @SequenceGenerator(name = "WFGeneralSeq",
	    sequenceName = "WF_GENERAL_SEQ")
    @Id
    @GeneratedValue(generator = "WFGeneralSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "ADDRESSED_TO")
    public String getAddressedTo() {
	return addressedTo;
    }

    public void setAddressedTo(String addressedTo) {
	this.addressedTo = addressedTo;
    }

    @Basic
    @Column(name = "RELATED_USER_ID")
    public Long getRelatedUserId() {
	return relatedUserId;
    }

    public void setRelatedUserId(Long relatedUserId) {
	this.relatedUserId = relatedUserId;
    }

    @Basic
    @Column(name = "INSTANCE_ID")
    public Long getInstanceId() {
	return instanceId;
    }

    public void setInstanceId(Long instanceId) {
	this.instanceId = instanceId;
    }

    @Basic
    @Column(name = "EMP_EN_NAME")
    public String getEmployeeEnglishName() {
	return employeeEnglishName;
    }

    public void setEmployeeEnglishName(String employeeEnglishName) {
	this.employeeEnglishName = employeeEnglishName;
    }

    @Basic
    @Column(name = "EMBASSY_ID")
    public Long getEmbassyId() {
	return embassyId;
    }

    public void setEmbassyId(Long embassyId) {
	this.embassyId = embassyId;
    }

    @Basic
    @Column(name = "REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRequestDate() {
	return requestDate;
    }

    public void setRequestDate(Date requestDate) {
	this.requestDate = requestDate;
    }

    @Basic
    @Column(name = "LETTER_TYPE")
    public String getLetterType() {
	return letterType;
    }

    public void setLetterType(String letterType) {
	this.letterType = letterType;
    }
}
