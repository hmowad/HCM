package com.code.dal.orm.hcm.organization.units;

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

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

/**
 * The <code>OrganizationTableDetailData</code> class represents the organization table details in detailed format.
 * 
 */

@NamedQueries({
	@NamedQuery(name = "hcm_organizationTableDetailData_getIdenticalOrganizationTableDetails",
		query = " select d from OrganizationTableDetailData d " +
			" where d.activeFlag = 1 " +
			" and d.organizationTableId in " +
			" ( " +
			" select rd.organizationTableId " +
			" from OrganizationTableDetailData sd, OrganizationTableDetailData rd " +
			" where sd.organizationTableId = :P_ORGANIZATION_TABLE_ID " +
			" and rd.activeFlag = 1 " +
			" and rd.categoryClass = sd.categoryClass " +
			" and rd.decisionNumber = sd.decisionNumber " +
			" and rd.decisionDate = sd.decisionDate " +
			" and rd.basicJobName = sd.basicJobName " +
			" and rd.rankId = sd.rankId " +
			" and rd.jobsCount = sd.jobsCount " +
			" and rd.generalSpecialization = sd.generalSpecialization " +
			" and rd.minorSpecializationId = sd.minorSpecializationId " +
			" and ((rd.classificationId = sd.classificationId) or (rd.classificationId is null and sd.classificationId is null)) " +
			" and rd.generalType = sd.generalType " +
			" and rd.approvedFlag = sd.approvedFlag " +
			" and (select count(td.organizationTableId) from OrganizationTableDetailData td where td.organizationTableId = rd.organizationTableId and td.activeFlag = 1) = :P_DETAILS_COUNT " +
			" group by rd.organizationTableId " +
			" having count(rd.organizationTableId) = :P_DETAILS_COUNT " +
			" ) " +
			" order by d.organizationTableId, d.basicJobName, d.rankId, d.jobsCount, d.generalSpecialization, d.minorSpecializationId, d.classificationId, d.generalType, d.approvedFlag "

	),
	@NamedQuery(name = "hcm_organizationTableDetailData_getOrganizationTableDetailsData",
		query = " select td from OrganizationTableDetailData td " +
			" where td.unitId in (:P_UNITS_IDS)" +
			" and td.categoryClass = :P_CATEGORY_CLASS" +
			" and td.activeFlag = 1 " +
			" order by td.organizationTableId, td.basicJobName, td.rankId, td.jobsCount, td.generalSpecialization, td.minorSpecializationId, td.classificationId, td.generalType, td.approvedFlag "
	)
})
@Entity
@Table(name = "HCM_VW_ORG_TABLES_DETAILS")
public class OrganizationTableDetailData extends BaseEntity {

    private Long id;
    private Long organizationTableId;
    private Long unitId;
    private String unitFullName;
    private Long unitTypeId;
    private String unitHkey;
    private Long regionId;
    private String basicJobName;
    private Long rankId;
    private String rankDescription;
    private Integer jobsCount;
    private Integer generalSpecialization;
    private Long minorSpecializationId;
    private String minorSpecializationDescription;
    private Long classificationId;
    private String classificationJobCode;
    private String classificationDescription;
    private Integer generalType;
    private Integer approvedFlag;
    private Integer activeFlag;
    private Integer categoryClass;
    private String decisionNumber;
    private Date decisionDate;
    private String decisionDateString;

    private OrganizationTableDetail organizationTableDetail;

    /**
     * Transient : Used to hold the category id for this organization table detail
     */
    private Long categoryId;

    /**
     * Transient : Used to hold the operation type to distinguish between the operations that should be happened on this object.
     */
    private Integer operationType; // 1:Insert, 2:Update, 3:Delete

    /**
     * Transient : Used to hold the index of this organizationTableDetailData at intermediate lists to help in delete or modify corresponding details.
     */
    private int index;

    public OrganizationTableDetailData() {
	organizationTableDetail = new OrganizationTableDetail();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.organizationTableDetail.setId(id);
    }

    @Basic
    @Column(name = "ORG_TABLE_ID")
    public Long getOrganizationTableId() {
	return organizationTableId;
    }

    public void setOrganizationTableId(Long organizationTableId) {
	this.organizationTableId = organizationTableId;
	this.organizationTableDetail.setOrganizationTableId(organizationTableId);
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
    }

    @Basic
    @Column(name = "UNIT_FULL_NAME")
    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
    }

    @Basic
    @Column(name = "UNIT_TYPE_ID")
    public Long getUnitTypeId() {
	return unitTypeId;
    }

    public void setUnitTypeId(Long unitTypeId) {
	this.unitTypeId = unitTypeId;
    }

    @Basic
    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    @Basic
    @Column(name = "UNIT_HKEY")
    public String getUnitHkey() {
	return unitHkey;
    }

    public void setUnitHkey(String unitHkey) {
	this.unitHkey = unitHkey;
    }

    @Basic
    @Column(name = "BASIC_JOB_NAME")
    public String getBasicJobName() {
	return basicJobName;
    }

    public void setBasicJobName(String basicJobName) {
	this.basicJobName = basicJobName;
	this.organizationTableDetail.setBasicJobName(basicJobName);
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
	this.organizationTableDetail.setRankId(rankId);
    }

    @Basic
    @Column(name = "RANK_DESCRIPTION")
    public String getRankDescription() {
	return rankDescription;
    }

    public void setRankDescription(String rankDescription) {
	this.rankDescription = rankDescription;
    }

    @Basic
    @Column(name = "JOBS_COUNT")
    public Integer getJobsCount() {
	return jobsCount;
    }

    public void setJobsCount(Integer jobsCount) {
	this.jobsCount = jobsCount;
	this.organizationTableDetail.setJobsCount(jobsCount);
    }

    @Basic
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
	this.organizationTableDetail.setGeneralSpecialization(generalSpecialization);
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
	this.organizationTableDetail.setMinorSpecializationId(minorSpecializationId);
    }

    @Basic
    @Column(name = "MINOR_SPEC_DESCRIPTION")
    public String getMinorSpecializationDescription() {
	return minorSpecializationDescription;
    }

    public void setMinorSpecializationDescription(String minorSpecializationDescription) {
	this.minorSpecializationDescription = minorSpecializationDescription;
    }

    @Basic
    @Column(name = "CLASSIFICATION_ID")
    public Long getClassificationId() {
	return classificationId;
    }

    public void setClassificationId(Long classificationId) {
	this.classificationId = classificationId;
	this.organizationTableDetail.setClassificationId(classificationId);
    }

    @Basic
    @Column(name = "CLASSIFICATION_JOB_CODE")
    public String getClassificationJobCode() {
	return classificationJobCode;
    }

    public void setClassificationJobCode(String classificationJobCode) {
	this.classificationJobCode = classificationJobCode;
    }

    @Basic
    @Column(name = "CLASSIFICATION_JOB_DESCRIPTION")
    public String getClassificationDescription() {
	return classificationDescription;
    }

    public void setClassificationDescription(String classificationDescription) {
	this.classificationDescription = classificationDescription;
    }

    @Basic
    @Column(name = "GENERAL_TYPE")
    public Integer getGeneralType() {
	return generalType;
    }

    public void setGeneralType(Integer generalType) {
	this.generalType = generalType;
	this.organizationTableDetail.setGeneralType(generalType);
    }

    @Basic
    @Column(name = "APPROVED_FLAG")
    public Integer getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(Integer approvedFlag) {
	this.approvedFlag = approvedFlag;
	this.organizationTableDetail.setApprovedFlag(approvedFlag);
    }

    @Basic
    @Column(name = "ACTIVE_FLAG")
    public Integer getActiveFlag() {
	return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
	this.activeFlag = activeFlag;
	this.organizationTableDetail.setActiveFlag(activeFlag);
    }

    @Basic
    @Column(name = "CATEGORY_CLASS")
    public Integer getCategoryClass() {
	return categoryClass;
    }

    public void setCategoryClass(Integer categoryClass) {
	this.categoryClass = categoryClass;
    }

    @Basic
    @Column(name = "DECISION_NUMBER")
    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    @Basic
    @Column(name = "DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
	this.decisionDateString = HijriDateService.getHijriDateString(decisionDate);
    }

    @Transient
    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
	this.decisionDate = HijriDateService.getHijriDate(decisionDateString);
    }

    @Transient
    public OrganizationTableDetail getOrganizationTableDetail() {
	return organizationTableDetail;
    }

    public void setOrganizationTableDetail(OrganizationTableDetail organizationTableDetail) {
	this.organizationTableDetail = organizationTableDetail;
    }

    @Transient
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    @Transient
    public Integer getOperationType() {
	return operationType;
    }

    public void setOperationType(Integer operationType) {
	this.operationType = operationType;
    }

    @Transient
    public int getIndex() {
	return index;
    }

    public void setIndex(int index) {
	this.index = index;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;

	OrganizationTableDetailData organizationTableDetailData = (OrganizationTableDetailData) obj;
	if (!organizationTableDetailData.getBasicJobName().equals(this.getBasicJobName()))
	    return false;

	if (organizationTableDetailData.getRankId().longValue() != this.getRankId().longValue())
	    return false;

	if (organizationTableDetailData.getJobsCount().intValue() != this.getJobsCount().intValue())
	    return false;

	if (organizationTableDetailData.getGeneralSpecialization().intValue() != this.getGeneralSpecialization().intValue())
	    return false;
	if (organizationTableDetailData.getMinorSpecializationId().longValue() != this.getMinorSpecializationId().longValue())
	    return false;

	if ((this.getClassificationId() != null) && (organizationTableDetailData.getClassificationId().longValue() != this.getClassificationId().longValue()))
	    return false;

	if (organizationTableDetailData.getGeneralType().intValue() != this.getGeneralType().intValue())
	    return false;

	if (organizationTableDetailData.getApprovedFlag().intValue() != this.getApprovedFlag().intValue())
	    return false;

	return true;
    }
}
