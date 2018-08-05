package com.code.dal.orm.hcm.recruitments;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_recruitmentWishData_searchRecruitmentWishData",
		query = " from RecruitmentWishData recWish where " +
			" (:P_EMP_ID = -1 or recWish.empId = :P_EMP_ID) " +
			" and (:P_EMP_NAME = '-1' or recWish.empName like :P_EMP_NAME) " +
			" and (:P_MINOR_SPEC_ID = -1 or recWish.empRecMinorSpecId = :P_MINOR_SPEC_ID) " +
			" and (:P_RANK_ID = -1 or recWish.empRecRankId = :P_RANK_ID) " +
			" and (:P_EVALUATION_DEGREE = -1 or recWish.evaluationDegree = :P_EVALUATION_DEGREE) " +
			" and (:P_STATUSES_IDS_FLAG = -1 or recWish.empStatusId in ( :P_STATUSES_IDS )) " +
			" order by recWish.empRecRankId, recWish.empRecMinorSpecId, recWish.evaluationDegree desc "),

	@NamedQuery(name = "hcm_recruitmentWishData_getAllNonDistributedSoldiersWishes",
		query = "select w, e from RecruitmentWishData w, EmployeeData e " +
			" where (w.empId = e.empId) " +
			" and (e.statusId = 10 or e.statusId = 12) " +
			" and (e.exceptionalRecruitmentFlag = 0) " +
			" and (e.gender = 'M') " +
			" order by w.evaluationDegree desc ")
})
@Entity
@Table(name = "HCM_VW_REC_WISHES")
public class RecruitmentWishData extends BaseEntity implements Serializable {
    private Long id;
    private Long empId;
    private String empName;
    private Long empStatusId;
    private String empStatusDesc;
    private Long empRecRankId;
    private String empRecRankDesc;
    private Long empRecMinorSpecId;
    private String empRecMinorSpecDesc;
    private Integer evaluationDegree;
    private Long regionsFirstWishId;
    private String regionsFirstWishDesc;
    private Long regionsSecondWishId;
    private String regionsSecondWishDesc;
    private Long regionsThirdWishId;
    private String regionsThirdWishDesc;
    private Long regionsFourthWishId;
    private String regionsFourthWishDesc;
    private Long regionsFifthWishId;
    private String regionsFifthWishDesc;
    private Long regionsSixthWishId;
    private String regionsSixthWishDesc;
    private Long regionsSeventhWishId;
    private String regionsSeventhWishDesc;
    private Long regionsEighthWishId;
    private String regionsEighthWishDesc;
    private Long regionsNinthWishId;
    private String regionsNinthWishDesc;
    private Long regionsTenthWishId;
    private String regionsTenthWishDesc;
    private Long regionsEleventhWishId;
    private String regionsEleventhWishDesc;

    private RecruitmentWish recruitmentWish;

    public RecruitmentWishData() {
	recruitmentWish = new RecruitmentWish();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	recruitmentWish.setId(id);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	recruitmentWish.setEmpId(empId);
    }

    @Basic
    @Column(name = "EMP_NAME")
    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    @Basic
    @Column(name = "EMP_STATUS_ID")
    public Long getEmpStatusId() {
	return empStatusId;
    }

    public void setEmpStatusId(Long empStatusId) {
	this.empStatusId = empStatusId;
    }

    @Basic
    @Column(name = "EMP_STATUS_DESC")
    public String getEmpStatusDesc() {
	return empStatusDesc;
    }

    public void setEmpStatusDesc(String empStatusDesc) {
	this.empStatusDesc = empStatusDesc;
    }

    @Basic
    @Column(name = "EMP_REC_RANK_ID")
    public Long getEmpRecRankId() {
	return empRecRankId;
    }

    public void setEmpRecRankId(Long empRecRankId) {
	this.empRecRankId = empRecRankId;
    }

    @Basic
    @Column(name = "EMP_REC_RANK_DESC")
    public String getEmpRecRankDesc() {
	return empRecRankDesc;
    }

    public void setEmpRecRankDesc(String empRecRankDesc) {
	this.empRecRankDesc = empRecRankDesc;
    }

    @Basic
    @Column(name = "EMP_REC_MINOR_SPEC_ID")
    public Long getEmpRecMinorSpecId() {
	return empRecMinorSpecId;
    }

    public void setEmpRecMinorSpecId(Long empRecMinorSpecId) {
	this.empRecMinorSpecId = empRecMinorSpecId;
    }

    @Basic
    @Column(name = "EMP_REC_MINOR_SPEC_DESC")
    public String getEmpRecMinorSpecDesc() {
	return empRecMinorSpecDesc;
    }

    public void setEmpRecMinorSpecDesc(String empRecMinorSpecDesc) {
	this.empRecMinorSpecDesc = empRecMinorSpecDesc;
    }

    @Basic
    @Column(name = "EVALUATION_DEGREE")
    public Integer getEvaluationDegree() {
	return evaluationDegree;
    }

    public void setEvaluationDegree(Integer evaluationDegree) {
	this.evaluationDegree = evaluationDegree;
	recruitmentWish.setEvaluationDegree(evaluationDegree);
    }

    @Basic
    @Column(name = "REGIONS_FIRST_WISH_ID")
    public Long getRegionsFirstWishId() {
	return regionsFirstWishId;
    }

    public void setRegionsFirstWishId(Long regionsFirstWishId) {
	this.regionsFirstWishId = regionsFirstWishId;
	recruitmentWish.setRegionsFirstWishId(regionsFirstWishId);
    }

    @Basic
    @Column(name = "REGIONS_FIRST_WISH_DESC")
    public String getRegionsFirstWishDesc() {
	return regionsFirstWishDesc;
    }

    public void setRegionsFirstWishDesc(String regionsFirstWishDesc) {
	this.regionsFirstWishDesc = regionsFirstWishDesc;
    }

    @Basic
    @Column(name = "REGIONS_SECOND_WISH_ID")
    public Long getRegionsSecondWishId() {
	return regionsSecondWishId;
    }

    public void setRegionsSecondWishId(Long regionsSecondWishId) {
	this.regionsSecondWishId = regionsSecondWishId;
	recruitmentWish.setRegionsSecondWishId(regionsSecondWishId);
    }

    @Basic
    @Column(name = "REGIONS_SECOND_WISH_DESC")
    public String getRegionsSecondWishDesc() {
	return regionsSecondWishDesc;
    }

    public void setRegionsSecondWishDesc(String regionsSecondWishDesc) {
	this.regionsSecondWishDesc = regionsSecondWishDesc;
    }

    @Basic
    @Column(name = "REGIONS_THIRD_WISH_ID")
    public Long getRegionsThirdWishId() {
	return regionsThirdWishId;
    }

    public void setRegionsThirdWishId(Long regionsThirdWishId) {
	this.regionsThirdWishId = regionsThirdWishId;
	recruitmentWish.setRegionsThirdWishId(regionsThirdWishId);
    }

    @Basic
    @Column(name = "REGIONS_THIRD_WISH_DESC")
    public String getRegionsThirdWishDesc() {
	return regionsThirdWishDesc;
    }

    public void setRegionsThirdWishDesc(String regionsThirdWishDesc) {
	this.regionsThirdWishDesc = regionsThirdWishDesc;
    }

    @Basic
    @Column(name = "REGIONS_FOURTH_WISH_ID")
    public Long getRegionsFourthWishId() {
	return regionsFourthWishId;
    }

    public void setRegionsFourthWishId(Long regionsFourthWishId) {
	this.regionsFourthWishId = regionsFourthWishId;
	recruitmentWish.setRegionsFourthWishId(regionsFourthWishId);
    }

    @Basic
    @Column(name = "REGIONS_FOURTH_WISH_DESC")
    public String getRegionsFourthWishDesc() {
	return regionsFourthWishDesc;
    }

    public void setRegionsFourthWishDesc(String regionsFourthWishDesc) {
	this.regionsFourthWishDesc = regionsFourthWishDesc;
    }

    @Basic
    @Column(name = "REGIONS_FIFTH_WISH_ID")
    public Long getRegionsFifthWishId() {
	return regionsFifthWishId;
    }

    public void setRegionsFifthWishId(Long regionsFifthWishId) {
	this.regionsFifthWishId = regionsFifthWishId;
	recruitmentWish.setRegionsFifthWishId(regionsFifthWishId);
    }

    @Basic
    @Column(name = "REGIONS_FIFTH_WISH_DESC")
    public String getRegionsFifthWishDesc() {
	return regionsFifthWishDesc;
    }

    public void setRegionsFifthWishDesc(String regionsFifthWishDesc) {
	this.regionsFifthWishDesc = regionsFifthWishDesc;
    }

    @Basic
    @Column(name = "REGIONS_SIXTH_WISH_ID")
    public Long getRegionsSixthWishId() {
	return regionsSixthWishId;
    }

    public void setRegionsSixthWishId(Long regionsSixthWishId) {
	this.regionsSixthWishId = regionsSixthWishId;
	recruitmentWish.setRegionsSixthWishId(regionsSixthWishId);
    }

    @Basic
    @Column(name = "REGIONS_SIXTH_WISH_DESC")
    public String getRegionsSixthWishDesc() {
	return regionsSixthWishDesc;
    }

    public void setRegionsSixthWishDesc(String regionsSixthWishDesc) {
	this.regionsSixthWishDesc = regionsSixthWishDesc;
    }

    @Basic
    @Column(name = "REGIONS_SEVENTH_WISH_ID")
    public Long getRegionsSeventhWishId() {
	return regionsSeventhWishId;
    }

    public void setRegionsSeventhWishId(Long regionsSeventhWishId) {
	this.regionsSeventhWishId = regionsSeventhWishId;
	recruitmentWish.setRegionsSeventhWishId(regionsSeventhWishId);
    }

    @Basic
    @Column(name = "REGIONS_SEVENTH_WISH_DESC")
    public String getRegionsSeventhWishDesc() {
	return regionsSeventhWishDesc;
    }

    public void setRegionsSeventhWishDesc(String regionsSeventhWishDesc) {
	this.regionsSeventhWishDesc = regionsSeventhWishDesc;
    }

    @Basic
    @Column(name = "REGIONS_EIGHTH_WISH_ID")
    public Long getRegionsEighthWishId() {
	return regionsEighthWishId;
    }

    public void setRegionsEighthWishId(Long regionsEighthWishId) {
	this.regionsEighthWishId = regionsEighthWishId;
	recruitmentWish.setRegionsEighthWishId(regionsEighthWishId);
    }

    @Basic
    @Column(name = "REGIONS_EIGHTH_WISH_DESC")
    public String getRegionsEighthWishDesc() {
	return regionsEighthWishDesc;
    }

    public void setRegionsEighthWishDesc(String regionsEighthWishDesc) {
	this.regionsEighthWishDesc = regionsEighthWishDesc;
    }

    @Basic
    @Column(name = "REGIONS_NINTH_WISH_ID")
    public Long getRegionsNinthWishId() {
	return regionsNinthWishId;
    }

    public void setRegionsNinthWishId(Long regionsNinthWishId) {
	this.regionsNinthWishId = regionsNinthWishId;
	recruitmentWish.setRegionsNinthWishId(regionsNinthWishId);
    }

    @Basic
    @Column(name = "REGIONS_NINTH_WISH_DESC")
    public String getRegionsNinthWishDesc() {
	return regionsNinthWishDesc;
    }

    public void setRegionsNinthWishDesc(String regionsNinthWishDesc) {
	this.regionsNinthWishDesc = regionsNinthWishDesc;
    }

    @Basic
    @Column(name = "REGIONS_TENTH_WISH_ID")
    public Long getRegionsTenthWishId() {
	return regionsTenthWishId;
    }

    public void setRegionsTenthWishId(Long regionsTenthWishId) {
	this.regionsTenthWishId = regionsTenthWishId;
	recruitmentWish.setRegionsTenthWishId(regionsTenthWishId);
    }

    @Basic
    @Column(name = "REGIONS_TENTH_WISH_DESC")
    public String getRegionsTenthWishDesc() {
	return regionsTenthWishDesc;
    }

    public void setRegionsTenthWishDesc(String regionsTenthWishDesc) {
	this.regionsTenthWishDesc = regionsTenthWishDesc;
    }

    @Basic
    @Column(name = "REGIONS_ELEVENTH_WISH_ID")
    public Long getRegionsEleventhWishId() {
	return regionsEleventhWishId;
    }

    public void setRegionsEleventhWishId(Long regionsEleventhWishId) {
	this.regionsEleventhWishId = regionsEleventhWishId;
	recruitmentWish.setRegionsEleventhWishId(regionsEleventhWishId);
    }

    @Basic
    @Column(name = "REGIONS_ELEVENTH_WISH_DESC")
    public String getRegionsEleventhWishDesc() {
	return regionsEleventhWishDesc;
    }

    public void setRegionsEleventhWishDesc(String regionsEleventhWishDesc) {
	this.regionsEleventhWishDesc = regionsEleventhWishDesc;
    }

    @Transient
    public Long[] getRegionsIds() {
	Long[] wishes = { regionsFirstWishId, regionsSecondWishId, regionsThirdWishId, regionsFourthWishId, regionsFifthWishId, regionsSixthWishId, regionsSeventhWishId, regionsEighthWishId, regionsNinthWishId, regionsTenthWishId, regionsEleventhWishId };
	return wishes;
    }

    @Transient
    public RecruitmentWish getRecruitmentWish() {
	return recruitmentWish;
    }

    public void setRecruitmentWish(RecruitmentWish recruitmentWish) {
	this.recruitmentWish = recruitmentWish;
    }
}
