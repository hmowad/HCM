package com.code.dal.orm;

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

import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "decisionTemplate_searchDecisionTemplate",
		query = "select t from DecisionTemplate t where" +
			"(:P_DECISION_DESC = '-1'or t.decisionDesc like :P_DECISION_DESC)" +
			"and (:P_CATEGORY = -1 or t.categoryId = :P_CATEGORY)" +
			"and (:P_REPORT_CLASS = -1 or t.reportClass = :P_REPORT_CLASS)" +
			"and (:P_TRANSACTION_CLASS = -1 or t.transactionClass = :P_TRANSACTION_CLASS)" +
			"and (:P_TRANSACTION_BUSINESS_TYPE = -1 or t.transactionBusinessType = :P_TRANSACTION_BUSINESS_TYPE)" +
			"and (:P_VALID_FROM_DATE_FLAG = -1 or t.validFromDate >= to_date(:P_VALID_FROM_DATE, 'MI/MM/YYYY'))" +
			"and (:P_VALID_TO_DATE_FLAG = -1 or t.validToDate <= to_date(:P_VALID_TO_DATE, 'MI/MM/YYYY'))" +
			" order by t.id ")
})
@Entity
@Table(name = "ETR_DECISIONS_TEMPLATES")
public class DecisionTemplate extends BaseEntity {

    private Long id;
    private String decisionTitle;
    private String introductionTitle;
    private String introduction;
    private String alternativeIntroduction;
    private String clauses;
    private String decisionDesc;

    private Integer transactionClass;
    private Integer transactionClassType;
    private Integer transactionBusinessType;
    private Long categoryId;
    private Integer reportClass;
    private Date validFromDate;
    private String validFromDateString;
    private Date validToDate;
    private String validToDateString;

    private String riyadhRegionHeader;
    private String easternRegionHeader;
    private String northernBordersRegionHeader;
    private String jawfRegionHeader;
    private String tabukRegionHeader;
    private String makkahRegionHeader;
    private String jazanRegionHeader;
    private String asirRegionHeader;
    private String najranRegionHeader;
    private String madinahRegionHeader;
    private String academyRegionHeader;

    private Long sampleTransactionId;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "DECISION_TITLE")
    public String getDecisionTitle() {
	return decisionTitle;
    }

    public void setDecisionTitle(String decisionTitle) {
	this.decisionTitle = decisionTitle;
    }

    @Basic
    @Column(name = "INTRODUCTION_TITLE")
    public String getIntroductionTitle() {
	return introductionTitle;
    }

    public void setIntroductionTitle(String introductionTitle) {
	this.introductionTitle = introductionTitle;
    }

    @Basic
    @Column(name = "INTRODUCTION")
    public String getIntroduction() {
	return introduction;
    }

    public void setIntroduction(String introduction) {
	this.introduction = introduction;
    }

    @Basic
    @Column(name = "ALTERNATIVE_INTRODUCTION")
    public String getAlternativeIntroduction() {
	return alternativeIntroduction;
    }

    public void setAlternativeIntroduction(String alternativeIntroduction) {
	this.alternativeIntroduction = alternativeIntroduction;
    }

    @Basic
    @Column(name = "CLAUSES")
    public String getClauses() {
	return clauses;
    }

    public void setClauses(String clauses) {
	this.clauses = clauses;
    }

    @Basic
    @Column(name = "DECISION_DESC")
    public String getDecisionDesc() {
	return decisionDesc;
    }

    public void setDecisionDesc(String decisionDesc) {
	this.decisionDesc = decisionDesc;
    }

    @Basic
    @Column(name = "TRANSACTION_CLASS")
    public Integer getTransactionClass() {
	return transactionClass;
    }

    public void setTransactionClass(Integer transactionClass) {
	this.transactionClass = transactionClass;
    }

    @Basic
    @Column(name = "TRANSACTION_CLASS_TYPE")
    public Integer getTransactionClassType() {
	return transactionClassType;
    }

    public void setTransactionClassType(Integer transactionClassType) {
	this.transactionClassType = transactionClassType;
    }

    @Basic
    @Column(name = "TRANSACTION_BUSINESS_TYPE")
    public Integer getTransactionBusinessType() {
	return transactionBusinessType;
    }

    public void setTransactionBusinessType(Integer transactionBusinessType) {
	this.transactionBusinessType = transactionBusinessType;
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "REPORT_CLASS")
    public Integer getReportClass() {
	return reportClass;
    }

    public void setReportClass(Integer reportClass) {
	this.reportClass = reportClass;
    }

    @Basic
    @Column(name = "VALID_FROM_DATE")
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
    @Column(name = "VALID_TO_DATE")
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
    @Column(name = "RIYADH_REGION_HEADER")
    public String getRiyadhRegionHeader() {
	return riyadhRegionHeader;
    }

    public void setRiyadhRegionHeader(String riyadhRegionHeader) {
	this.riyadhRegionHeader = riyadhRegionHeader;
    }

    @Basic
    @Column(name = "EASTERN_REGION_HEADER")
    public String getEasternRegionHeader() {
	return easternRegionHeader;
    }

    public void setEasternRegionHeader(String easternRegionHeader) {
	this.easternRegionHeader = easternRegionHeader;
    }

    @Basic
    @Column(name = "NORTHERN_BORDERS_REGION_HEADER")
    public String getNorthernBordersRegionHeader() {
	return northernBordersRegionHeader;
    }

    public void setNorthernBordersRegionHeader(String northernBordersRegionHeader) {
	this.northernBordersRegionHeader = northernBordersRegionHeader;
    }

    @Basic
    @Column(name = "JAWF_REGION_HEADER")
    public String getJawfRegionHeader() {
	return jawfRegionHeader;
    }

    public void setJawfRegionHeader(String jawfRegionHeader) {
	this.jawfRegionHeader = jawfRegionHeader;
    }

    @Basic
    @Column(name = "TABUK_REGION_HEADER")
    public String getTabukRegionHeader() {
	return tabukRegionHeader;
    }

    public void setTabukRegionHeader(String tabukRegionHeader) {
	this.tabukRegionHeader = tabukRegionHeader;
    }

    @Basic
    @Column(name = "MAKKAH_REGION_HEADER")
    public String getMakkahRegionHeader() {
	return makkahRegionHeader;
    }

    public void setMakkahRegionHeader(String makkahRegionHeader) {
	this.makkahRegionHeader = makkahRegionHeader;
    }

    @Basic
    @Column(name = "JIZAN_REGION_HEADER")
    public String getJazanRegionHeader() {
	return jazanRegionHeader;
    }

    public void setJazanRegionHeader(String jazanRegionHeader) {
	this.jazanRegionHeader = jazanRegionHeader;
    }

    @Basic
    @Column(name = "ASIR_REGION_HEADER")
    public String getAsirRegionHeader() {
	return asirRegionHeader;
    }

    public void setAsirRegionHeader(String asirRegionHeader) {
	this.asirRegionHeader = asirRegionHeader;
    }

    @Basic
    @Column(name = "NAJRAN_REGION_HEADER")
    public String getNajranRegionHeader() {
	return najranRegionHeader;
    }

    public void setNajranRegionHeader(String najranRegionHeader) {
	this.najranRegionHeader = najranRegionHeader;
    }

    @Basic
    @Column(name = "MADINAH_REGION_HEADER")
    public String getMadinahRegionHeader() {
	return madinahRegionHeader;
    }

    public void setMadinahRegionHeader(String madinahRegionHeader) {
	this.madinahRegionHeader = madinahRegionHeader;
    }

    @Basic
    @Column(name = "ACADEMY_REGION_HEADER")
    public String getAcademyRegionHeader() {
	return academyRegionHeader;
    }

    public void setAcademyRegionHeader(String academyRegionHeader) {
	this.academyRegionHeader = academyRegionHeader;
    }

    @Basic
    @Column(name = "SAMPLE_TRANSACTION_ID")
    public Long getSampleTransactionId() {
	return sampleTransactionId;
    }

    public void setSampleTransactionId(Long sampleTransactionId) {
	this.sampleTransactionId = sampleTransactionId;
    }
}
