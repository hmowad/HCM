package com.code.dal.orm.signatures;

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

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;
import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "etr_signature_searchSignatures",
		query = " select s from Signature s " +
			" where (:P_SIGN_TYPE = -1 or s.signType = :P_SIGN_TYPE) " +
			" and (:P_SIGN_DESC = '-1' or s.signDesc = :P_SIGN_DESC) " +
			" and (:P_SIGN_STATUS = -1 or s.validToDate = (select max(s2.validToDate) from Signature s2 where s2.signType = s.signType)) " +
			" and (:P_NAME ='-1' or s.name like :P_NAME) " +
			" and (:P_TITLE_DESC = '-1' or s.titleDesc like :P_TITLE_DESC) " +
			" and (:P_SIGN_STAMP_TYPE = -1 or (:P_SIGN_STAMP_TYPE = 1 and empId is not null) or (:P_SIGN_STAMP_TYPE = 2 and empId is null )) " +
			" and (:P_EXCLUDED_ID = -1 or s.id <> :P_EXCLUDED_ID) " +
			" order by s.signDesc, s.validFromDate desc "),

	@NamedQuery(name = "etr_signature_getSignatureById",
		query = " select s from Signature s where s.id = :P_SIGN_ID "),

	@NamedQuery(name = "etr_signature_getDistinctSignDescs",
		query = " select distinct s.signDesc from Signature s " +
			" where (:P_SIGN_STAMP_TYPE = 1 and empId is not null) or (:P_SIGN_STAMP_TYPE = 2 and empId is null) " +
			" order by s.signDesc ")

})
@Entity
@Table(name = "ETR_SIGNATURES")
public class Signature extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity {
    private Long id;
    private Integer signType;
    private String signDesc;
    private String name;
    private String rankDesc;
    private String titleDesc;
    private byte[] signaturePhoto;
    private Date validFromDate;
    private String validFromDateString;
    private Date validToDate;
    private String validToDateString;
    private String enName;
    private String enRankDesc;
    private String enTitleDesc;
    private Long empId;

    @SequenceGenerator(name = "ETRGeneralSeq",
	    sequenceName = "ETR_GENERAL_SEQ")
    @Id
    @GeneratedValue(generator = "ETRGeneralSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "SIGN_TYPE")
    public Integer getSignType() {
	return signType;
    }

    public void setSignType(Integer signType) {
	this.signType = signType;
    }

    @Basic
    @Column(name = "SIGN_DESC")
    public String getSignDesc() {
	return signDesc;
    }

    public void setSignDesc(String signDesc) {
	this.signDesc = signDesc;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Basic
    @Column(name = "RANK_DESC")
    public String getRankDesc() {
	return rankDesc;
    }

    public void setRankDesc(String rankDesc) {
	this.rankDesc = rankDesc;
    }

    @Basic
    @Column(name = "TITLE_DESC")
    public String getTitleDesc() {
	return titleDesc;
    }

    public void setTitleDesc(String titleDesc) {
	this.titleDesc = titleDesc;
    }

    @Basic
    @Column(name = "SIGNATURE")
    public byte[] getSignaturePhoto() {
	return signaturePhoto;
    }

    public void setSignaturePhoto(byte[] signaturePhoto) {
	this.signaturePhoto = signaturePhoto;
    }

    @Basic
    @Column(name = "VALID_FROM")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getValidFromDate() {
	return validFromDate;
    }

    public void setValidFromDate(Date validFromDate) {
	this.validFromDate = validFromDate;
	this.validFromDateString = HijriDateService.getHijriDateString(validFromDate);
    }

    @Transient
    public String getValidFromDateString() {
	return validFromDateString;
    }

    public void setValidFromDateString(String validFromDateString) {
	this.validFromDateString = validFromDateString;
	this.validFromDate = HijriDateService.getHijriDate(validFromDateString);
    }

    @Basic
    @Column(name = "VALID_TO")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getValidToDate() {
	return validToDate;
    }

    public void setValidToDate(Date validToDate) {
	this.validToDate = validToDate;
	this.validToDateString = HijriDateService.getHijriDateString(validToDate);
    }

    @Transient
    public String getValidToDateString() {
	return validToDateString;
    }

    public void setValidToDateString(String validToDateString) {
	this.validToDateString = validToDateString;
	this.validToDate = HijriDateService.getHijriDate(validToDateString);
    }

    @Basic
    @Column(name = "EN_NAME")
    public String getEnName() {
	return enName;
    }

    public void setEnName(String enName) {
	this.enName = enName;
    }

    @Basic
    @Column(name = "EN_RANK_DESC")
    public String getEnRankDesc() {
	return enRankDesc;
    }

    public void setEnRankDesc(String enRankDesc) {
	this.enRankDesc = enRankDesc;
    }

    @Basic
    @Column(name = "EN_TITLE_DESC")
    public String getEnTitleDesc() {
	return enTitleDesc;
    }

    public void setEnTitleDesc(String enTitleDesc) {
	this.enTitleDesc = enTitleDesc;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "empId:" + empId + AUDIT_SEPARATOR +
		"signType:" + signType + AUDIT_SEPARATOR +
		"signDesc:" + signDesc + AUDIT_SEPARATOR +
		"name:" + name + AUDIT_SEPARATOR +
		"rankDesc:" + rankDesc + AUDIT_SEPARATOR +
		"titleDesc:" + titleDesc + AUDIT_SEPARATOR +
		"validFromDate:" + validFromDate + AUDIT_SEPARATOR +
		"validToDate:" + validToDate + AUDIT_SEPARATOR +
		"enName:" + enName + AUDIT_SEPARATOR +
		"enRankDesc:" + enRankDesc + AUDIT_SEPARATOR +
		"enTitleDesc:" + enTitleDesc + AUDIT_SEPARATOR;
    }

}
