package com.code.dal.orm.hcm.organization.jobs;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>ManpowerNeedDetailData</code> class represents the manpower needs details data in detailed format.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_manpowerNeedDetailData_getManpowerNeedDetailsDataByManpowerNeedsIds",
		query = " select m from ManpowerNeedDetailData m " +
			" where m.manpowerNeedId in ( :P_MANPOWER_NEEDS_IDS ) " +
			" order by m.manpowerNeedId, m.id "),

	@NamedQuery(name = "hcm_manpowerNeedDetailData_countUnderProcessingRequestsToUnit",
		query = " select count(m.id) from ManpowerNeedDetailData m " +
			" where m.unitHKey like :P_UNIT_HKEY_PREFIX " +
			" and (select n.status from ManpowerNeed n where n.id = m.manpowerNeedId and n.categoryId = :P_CATEGORY_ID) = 2 ")
})
@Entity
@Table(name = "HCM_VW_ORG_MANPOWER_NEEDS_DTLS")
public class ManpowerNeedDetailData extends BaseEntity {

    private Long id;
    private Long manpowerNeedId;
    private Long unitId;
    private String unitFullName;
    private String unitHKey;
    private Long regionId;
    private String regionDescription;
    private Long minorSpecializationId;
    private String minorSpecializationDescription;
    private Integer requiredCount;
    private String requestReasons;
    private Integer requestSuggestedCount;
    private Integer studySuggestedCount;
    private String studyReasons;
    private Integer mainStudySuggestedCount;
    private String mainStudyReasons;
    private Integer addedOnStudyFlag;
    private Integer officialEmployeesCount;
    private Integer physicalEmployeesCount;

    private ManpowerNeedDetail manpowerNeedDetail;

    public ManpowerNeedDetailData() {
	setManpowerNeedDetail(new ManpowerNeedDetail());
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.manpowerNeedDetail.setId(id);
    }

    @Basic
    @Column(name = "MANPOWER_NEED_ID")
    public Long getManpowerNeedId() {
	return manpowerNeedId;
    }

    public void setManpowerNeedId(Long manpowerNeedId) {
	this.manpowerNeedId = manpowerNeedId;
	this.manpowerNeedDetail.setManpowerNeedId(manpowerNeedId);
    }

    @Basic
    @Column(name = "UNIT_ID")
    public Long getUnitId() {
	return unitId;
    }

    public void setUnitId(Long unitId) {
	this.unitId = unitId;
	this.manpowerNeedDetail.setUnitId(unitId);
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
    @Column(name = "UNIT_HKEY")
    public String getUnitHKey() {
	return unitHKey;
    }

    public void setUnitHKey(String unitHKey) {
	this.unitHKey = unitHKey;
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
    @Column(name = "REGION_DESCRIPTION")
    public String getRegionDescription() {
	return regionDescription;
    }

    public void setRegionDescription(String regionDescription) {
	this.regionDescription = regionDescription;
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_ID")
    public Long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(Long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
	this.manpowerNeedDetail.setMinorSpecializationId(minorSpecializationId);
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
    @Column(name = "REQUIRED_COUNT")
    public Integer getRequiredCount() {
	return requiredCount;
    }

    public void setRequiredCount(Integer requiredCount) {
	this.requiredCount = requiredCount;
	this.manpowerNeedDetail.setRequiredCount(requiredCount);
    }

    @Basic
    @Column(name = "REQUEST_REASONS")
    public String getRequestReasons() {
	return requestReasons;
    }

    public void setRequestReasons(String requestReasons) {
	this.requestReasons = requestReasons;
	this.manpowerNeedDetail.setRequestReasons(requestReasons);
    }

    @Basic
    @Column(name = "REQUEST_SUGGESTED_COUNT")
    public Integer getRequestSuggestedCount() {
	return requestSuggestedCount;
    }

    public void setRequestSuggestedCount(Integer requestSuggestedCount) {
	this.requestSuggestedCount = requestSuggestedCount;
	this.manpowerNeedDetail.setRequestSuggestedCount(requestSuggestedCount);
    }

    @Basic
    @Column(name = "STUDY_SUGGESTED_COUNT")
    public Integer getStudySuggestedCount() {
	return studySuggestedCount;
    }

    public void setStudySuggestedCount(Integer studySuggestedCount) {
	this.studySuggestedCount = studySuggestedCount;
	this.manpowerNeedDetail.setStudySuggestedCount(studySuggestedCount);
    }

    @Basic
    @Column(name = "STUDY_REASONS")
    public String getStudyReasons() {
	return studyReasons;
    }

    public void setStudyReasons(String studyReasons) {
	this.studyReasons = studyReasons;
	this.manpowerNeedDetail.setStudyReasons(studyReasons);
    }

    @Basic
    @Column(name = "MAIN_STUDY_SUGGESTED_COUNT")
    public Integer getMainStudySuggestedCount() {
	return mainStudySuggestedCount;
    }

    public void setMainStudySuggestedCount(Integer mainStudySuggestedCount) {
	this.mainStudySuggestedCount = mainStudySuggestedCount;
	this.manpowerNeedDetail.setMainStudySuggestedCount(mainStudySuggestedCount);
    }

    @Basic
    @Column(name = "MAIN_STUDY_REASONS")
    public String getMainStudyReasons() {
	return mainStudyReasons;
    }

    public void setMainStudyReasons(String mainStudyReasons) {
	this.mainStudyReasons = mainStudyReasons;
	this.manpowerNeedDetail.setMainStudyReasons(mainStudyReasons);
    }

    @Basic
    @Column(name = "ADDED_ON_STUDY_FLAG")
    public Integer getAddedOnStudyFlag() {
	return addedOnStudyFlag;
    }

    public void setAddedOnStudyFlag(Integer addedOnStudyFlag) {
	this.addedOnStudyFlag = addedOnStudyFlag;
	this.manpowerNeedDetail.setAddedOnStudyFlag(addedOnStudyFlag);
    }

    @Basic
    @Column(name = "OFFICIAL_EMPLOYEES_COUNT")
    public Integer getOfficialEmployeesCount() {
	return officialEmployeesCount;
    }

    public void setOfficialEmployeesCount(Integer officialEmployeesCount) {
	this.officialEmployeesCount = officialEmployeesCount;
	this.manpowerNeedDetail.setOfficialEmployeesCount(officialEmployeesCount);
    }

    @Basic
    @Column(name = "PHYSICAL_EMPLOYEES_COUNT")
    public Integer getPhysicalEmployeesCount() {
	return physicalEmployeesCount;
    }

    public void setPhysicalEmployeesCount(Integer physicalEmployeesCount) {
	this.physicalEmployeesCount = physicalEmployeesCount;
	this.manpowerNeedDetail.setPhysicalEmployeesCount(physicalEmployeesCount);
    }

    @Transient
    public ManpowerNeedDetail getManpowerNeedDetail() {
	return manpowerNeedDetail;
    }

    public void setManpowerNeedDetail(ManpowerNeedDetail manpowerNeedDetail) {
	this.manpowerNeedDetail = manpowerNeedDetail;
    }

}
