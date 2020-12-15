package com.code.dal.orm.hcm.employees;

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

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_emp_getEmployeeIdByOldId",
		query = " select e.id from Employee e where e.oldEmpId = :P_OLD_EMP_ID"),
	@NamedQuery(name = "hcm_emp_getEmployeeOldIdById",
		query = " select e.oldEmpId from Employee e where e.id = :P_EMP_ID"),

	@NamedQuery(name = "hcm_emp_getFutureServiceTerminatedEmployees",
		query = " select e from Employee e " +
			" where e.id in (:P_EMPS_IDS) " +
			" and e.serviceTerminationDate >= to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY')" +
			" and e.statusId in (12, 15, 20, 22, 25, 30, 35, 40, 42, 45)")

})
@Entity
@Table(name = "HCM_PRS_EMPLOYEES")
public class Employee extends AuditEntity implements Serializable, InsertableAuditEntity, UpdatableAuditEntity {
    private Long id;
    private String firstName;
    private String secondName;
    private String thirdName;
    private String lastName;
    private String firstNameEn;
    private String secondNameEn;
    private String thirdNameEn;
    private String lastNameEn;
    private Long categoryId;
    private Long rankId;
    private Long salaryRankId;
    private Long salaryDegreeId;
    private Long countryId;
    private Date birthDate;
    private String birthPlace;
    private String socialID;
    private Date socialIDIssueDate;
    private Date socialIDExpiryDate;
    private Long socialIDIssuePlaceID;
    private String bloodGroup;
    private String gender;
    private Integer socialStatus;
    private Integer generalSpecialization;
    private Long degreeId;
    private Long statusId;
    private Integer graduationGroupNumber;
    private Integer graduationGroupPlace;
    private Integer militaryNumber;
    private String recruitmentDecisionNumber;
    private Date recruitmentDecisionDate;
    private Date recruitmentDate;
    private Date recruitmentJoiningDate;
    private Long recruitmentRankId;
    private Date firstRecruitmentDate;
    private Date recruitmentAsOfficerDate;
    private Long jobId;
    private Long rankTitleId;
    private Date lastPromotionDate;
    private Date promotionDueDate;
    private Long physicalUnitId;
    private String phoneExt;
    private String ipPhoneExt;
    private String officialMobileNumber;
    private String privateMobileNumber;
    private String shieldMobileNumber;
    private String email;
    private String directPhoneNumber;
    private Integer jobModifiedFlag;
    private Integer movementBlacklistFlag;
    private String oldEmpId;
    private Long recruitmentRegionId;
    private Long recruitmentMinorSpecId;
    private Long recTrainingUnitId;
    private Date recTrainingJoiningDate;
    private Date serviceTerminationDate;
    private Date serviceTerminationDueDate;
    private Date lastExtensionEndDate;
    private Integer exceptionalRecruitmentFlag;
    private Long sequenceNumber;
    private String userAccount;
    private Long categoryClassificationId;
    private String navyFormation;
    private String socialIdCopy;
    private Date lastAnnualRaiseDate;
    private String occupationDescription;

    public void setId(Long id) {
	this.id = id;
    }

    @SequenceGenerator(name = "HCMPrsEmpsSeq",
	    sequenceName = "HCM_PRS_EMPS_SEQ")
    @Id
    @GeneratedValue(generator = "HCMPrsEmpsSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    @Basic
    @Column(name = "FIRST_NAME")
    public String getFirstName() {
	return firstName;
    }

    public void setSecondName(String secondName) {
	this.secondName = secondName;
    }

    @Basic
    @Column(name = "SECOND_NAME")
    public String getSecondName() {
	return secondName;
    }

    public void setThirdName(String thirdName) {
	this.thirdName = thirdName;
    }

    @Basic
    @Column(name = "THIRD_NAME")
    public String getThirdName() {
	return thirdName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    @Basic
    @Column(name = "LAST_NAME")
    public String getLastName() {
	return lastName;
    }

    @Basic
    @Column(name = "FIRST_NAME_EN")
    public String getFirstNameEn() {
	return firstNameEn;
    }

    public void setFirstNameEn(String firstNameEn) {
	this.firstNameEn = firstNameEn;
    }

    @Basic
    @Column(name = "SECOND_NAME_EN")
    public String getSecondNameEn() {
	return secondNameEn;
    }

    public void setSecondNameEn(String secondNameEn) {
	this.secondNameEn = secondNameEn;
    }

    @Basic
    @Column(name = "THIRD_NAME_EN")
    public String getThirdNameEn() {
	return thirdNameEn;
    }

    public void setThirdNameEn(String thirdNameEn) {
	this.thirdNameEn = thirdNameEn;
    }

    @Basic
    @Column(name = "LAST_NAME_EN")
    public String getLastNameEn() {
	return lastNameEn;
    }

    public void setLastNameEn(String lastNameEn) {
	this.lastNameEn = lastNameEn;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    @Basic
    @Column(name = "SALARY_RANK_ID")
    public Long getSalaryRankId() {
	return salaryRankId;
    }

    public void setSalaryRankId(Long salaryRankId) {
	this.salaryRankId = salaryRankId;
    }

    @Basic
    @Column(name = "SALARY_DEGREE_ID")
    public Long getSalaryDegreeId() {
	return salaryDegreeId;
    }

    public void setSalaryDegreeId(Long salaryDegreeId) {
	this.salaryDegreeId = salaryDegreeId;
    }

    public void setCountryId(Long countryId) {
	this.countryId = countryId;
    }

    @Basic
    @Column(name = "COUNTRY_ID")
    public Long getCountryId() {
	return countryId;
    }

    public void setBirthDate(Date birthDate) {
	this.birthDate = birthDate;
    }

    @Basic
    @Column(name = "BIRTH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getBirthDate() {
	return birthDate;
    }

    public void setBirthPlace(String birthPlace) {
	this.birthPlace = birthPlace;
    }

    @Basic
    @Column(name = "BIRTH_PLACE")
    public String getBirthPlace() {
	return birthPlace;
    }

    public void setSocialID(String socialID) {
	this.socialID = socialID;
    }

    @Basic
    @Column(name = "SOCIAL_ID")
    public String getSocialID() {
	return socialID;
    }

    public void setSocialIDIssueDate(Date socialIDIssueDate) {
	this.socialIDIssueDate = socialIDIssueDate;
    }

    @Basic
    @Column(name = "SOCIAL_ID_ISSUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSocialIDIssueDate() {
	return socialIDIssueDate;
    }

    public void setSocialIDExpiryDate(Date socialIDExpiryDate) {
	this.socialIDExpiryDate = socialIDExpiryDate;
    }

    @Basic
    @Column(name = "SOCIAL_ID_EXPIRY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSocialIDExpiryDate() {
	return socialIDExpiryDate;
    }

    public void setSocialIDIssuePlaceID(Long socialIDIssuePlaceID) {
	this.socialIDIssuePlaceID = socialIDIssuePlaceID;
    }

    @Basic
    @Column(name = "SOCIAL_ID_ISSUE_PLACE_ID")
    public Long getSocialIDIssuePlaceID() {
	return socialIDIssuePlaceID;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
    }

    @Basic
    @Column(name = "GENDER")
    public String getGender() {
	return gender;
    }

    public void setGender(String gender) {
	this.gender = gender;
    }

    @Basic
    @Column(name = "SOCIAL_STATUS")
    public Integer getSocialStatus() {
	return socialStatus;
    }

    public void setSocialStatus(Integer socialStatus) {
	this.socialStatus = socialStatus;
    }

    @Basic
    @Column(name = "BLOOD_GROUP")
    public String getBloodGroup() {
	return bloodGroup;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
    }

    @Basic
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setDegreeId(Long degreeId) {
	this.degreeId = degreeId;
    }

    @Basic
    @Column(name = "DEGREE_ID")
    public Long getDegreeId() {
	return degreeId;
    }

    public void setStatusId(Long statusId) {
	this.statusId = statusId;
    }

    @Basic
    @Column(name = "STATUS_ID")
    public Long getStatusId() {
	return statusId;
    }

    public void setGraduationGroupNumber(Integer graduationGroupNumber) {
	this.graduationGroupNumber = graduationGroupNumber;
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_NUMBER")
    public Integer getGraduationGroupNumber() {
	return graduationGroupNumber;
    }

    public void setGraduationGroupPlace(Integer graduationGroupPlace) {
	this.graduationGroupPlace = graduationGroupPlace;
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_PLACE")
    public Integer getGraduationGroupPlace() {
	return graduationGroupPlace;
    }

    public void setMilitaryNumber(Integer militaryNumber) {
	this.militaryNumber = militaryNumber;
    }

    @Basic
    @Column(name = "MILITARY_NUMBER")
    public Integer getMilitaryNumber() {
	return militaryNumber;
    }

    public void setRecruitmentDecisionNumber(String recruitmentDecisionNumber) {
	this.recruitmentDecisionNumber = recruitmentDecisionNumber;
    }

    @Basic
    @Column(name = "RECRUITMENT_DECISION_NUMBER")
    public String getRecruitmentDecisionNumber() {
	return recruitmentDecisionNumber;
    }

    public void setRecruitmentDecisionDate(Date recruitmentDecisionDate) {
	this.recruitmentDecisionDate = recruitmentDecisionDate;
    }

    @Basic
    @Column(name = "RECRUITMENT_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRecruitmentDecisionDate() {
	return recruitmentDecisionDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
	this.recruitmentDate = recruitmentDate;
    }

    @Basic
    @Column(name = "RECRUITMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRecruitmentDate() {
	return recruitmentDate;
    }

    public void setRecruitmentJoiningDate(Date recruitmentJoiningDate) {
	this.recruitmentJoiningDate = recruitmentJoiningDate;
    }

    @Basic
    @Column(name = "RECRUITMENT_JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRecruitmentJoiningDate() {
	return recruitmentJoiningDate;
    }

    public void setRecruitmentRankId(Long recruitmentRankId) {
	this.recruitmentRankId = recruitmentRankId;
    }

    @Basic
    @Column(name = "RECRUITMENT_RANK_ID")
    public Long getRecruitmentRankId() {
	return recruitmentRankId;
    }

    public void setFirstRecruitmentDate(Date firstRecruitmentDate) {
	this.firstRecruitmentDate = firstRecruitmentDate;
    }

    @Basic
    @Column(name = "FIRST_RECRUITMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getFirstRecruitmentDate() {
	return firstRecruitmentDate;
    }

    public void setRecruitmentAsOfficerDate(Date recruitmentAsOfficerDate) {
	this.recruitmentAsOfficerDate = recruitmentAsOfficerDate;
    }

    @Basic
    @Column(name = "RECRUITMENT_AS_OFFICER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRecruitmentAsOfficerDate() {
	return recruitmentAsOfficerDate;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setRankTitleId(Long rankTitleId) {
	this.rankTitleId = rankTitleId;
    }

    @Basic
    @Column(name = "RANK_TITLE_ID")
    public Long getRankTitleId() {
	return rankTitleId;
    }

    public void setLastPromotionDate(Date lastPromotionDate) {
	this.lastPromotionDate = lastPromotionDate;
    }

    @Basic
    @Column(name = "LAST_PROMOTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastPromotionDate() {
	return lastPromotionDate;
    }

    public void setPromotionDueDate(Date promotionDueDate) {
	this.promotionDueDate = promotionDueDate;
    }

    @Basic
    @Column(name = "PROMOTION_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPromotionDueDate() {
	return promotionDueDate;
    }

    public void setPhysicalUnitId(Long physicalUnitId) {
	this.physicalUnitId = physicalUnitId;
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_ID")
    public Long getPhysicalUnitId() {
	return physicalUnitId;
    }

    public void setPhoneExt(String phoneExt) {
	this.phoneExt = phoneExt;
    }

    @Basic
    @Column(name = "PHONE_EXT")
    public String getPhoneExt() {
	return phoneExt;
    }

    public void setIpPhoneExt(String ipPhoneExt) {
	this.ipPhoneExt = ipPhoneExt;
    }

    @Basic
    @Column(name = "IP_PHONE_EXT")
    public String getIpPhoneExt() {
	return ipPhoneExt;
    }

    public void setOfficialMobileNumber(String officialMobileNumber) {
	this.officialMobileNumber = officialMobileNumber;
    }

    @Basic
    @Column(name = "OFFICIAL_MOBILE_NUMBER")
    public String getOfficialMobileNumber() {
	return officialMobileNumber;
    }

    public void setPrivateMobileNumber(String privateMobileNumber) {
	this.privateMobileNumber = privateMobileNumber;
    }

    @Basic
    @Column(name = "PRIVATE_MOBILE_NUMBER")
    public String getPrivateMobileNumber() {
	return privateMobileNumber;
    }

    public void setShieldMobileNumber(String shieldMobileNumber) {
	this.shieldMobileNumber = shieldMobileNumber;
    }

    @Basic
    @Column(name = "SHIELD_MOBILE_NUMBER")
    public String getShieldMobileNumber() {
	return shieldMobileNumber;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    @Basic
    @Column(name = "EMAIL")
    public String getEmail() {
	return email;
    }

    public void setDirectPhoneNumber(String directPhoneNumber) {
	this.directPhoneNumber = directPhoneNumber;
    }

    @Basic
    @Column(name = "DIRECT_PHONE_NUMBER")
    public String getDirectPhoneNumber() {
	return directPhoneNumber;
    }

    public void setJobModifiedFlag(Integer jobModifiedFlag) {
	this.jobModifiedFlag = jobModifiedFlag;
    }

    @Basic
    @Column(name = "JOB_MODIFIED_FLAG")
    public Integer getJobModifiedFlag() {
	return jobModifiedFlag;
    }

    public void setMovementBlacklistFlag(Integer movementBlacklistFlag) {
	this.movementBlacklistFlag = movementBlacklistFlag;
    }

    @Basic
    @Column(name = "MOVEMENT_BLACK_LIST_FLAG")
    public Integer getMovementBlacklistFlag() {
	return movementBlacklistFlag;
    }

    public void setOldEmpId(String oldEmpId) {
	this.oldEmpId = oldEmpId;
    }

    @Basic
    @Column(name = "EMP_OLD_ID")
    public String getOldEmpId() {
	return oldEmpId;
    }

    public void setRecruitmentRegionId(Long recruitmentRegionId) {
	this.recruitmentRegionId = recruitmentRegionId;
    }

    @Basic
    @Column(name = "RECRUITMENT_REGION_ID")
    public Long getRecruitmentRegionId() {
	return recruitmentRegionId;
    }

    public void setRecruitmentMinorSpecId(Long recruitmentMinorSpecId) {
	this.recruitmentMinorSpecId = recruitmentMinorSpecId;
    }

    @Basic
    @Column(name = "RECRUITMENT_MINOR_SPEC_ID")
    public Long getRecruitmentMinorSpecId() {
	return recruitmentMinorSpecId;
    }

    @Basic
    @Column(name = "REC_TRAINING_UNIT_ID")
    public Long getRecTrainingUnitId() {
	return recTrainingUnitId;
    }

    public void setRecTrainingUnitId(Long recTrainingUnitId) {
	this.recTrainingUnitId = recTrainingUnitId;
    }

    @Basic
    @Column(name = "REC_TRAINING_JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRecTrainingJoiningDate() {
	return recTrainingJoiningDate;
    }

    public void setRecTrainingJoiningDate(Date recTrainingJoiningDate) {
	this.recTrainingJoiningDate = recTrainingJoiningDate;
    }

    @Basic
    @Column(name = "SERVICE_TERMINATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getServiceTerminationDate() {
	return serviceTerminationDate;
    }

    public void setServiceTerminationDate(Date serviceTerminationDate) {
	this.serviceTerminationDate = serviceTerminationDate;
    }

    @Basic
    @Column(name = "SERVICE_TERMINATION_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getServiceTerminationDueDate() {
	return serviceTerminationDueDate;
    }

    public void setServiceTerminationDueDate(Date serviceTerminationDueDate) {
	this.serviceTerminationDueDate = serviceTerminationDueDate;
    }

    @Basic
    @Column(name = "LAST_EXTENSION_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastExtensionEndDate() {
	return lastExtensionEndDate;
    }

    public void setLastExtensionEndDate(Date lastExtensionEndDate) {
	this.lastExtensionEndDate = lastExtensionEndDate;

    }

    @Basic
    @Column(name = "EXCEPTIONAL_RECRUITMENT_FLAG")
    public Integer getExceptionalRecruitmentFlag() {
	return exceptionalRecruitmentFlag;
    }

    public void setExceptionalRecruitmentFlag(Integer exceptionalRecruitmentFlag) {
	this.exceptionalRecruitmentFlag = exceptionalRecruitmentFlag;
    }

    @Basic
    @Column(name = "SEQUENCE_NUMBER")
    public Long getSequenceNumber() {
	return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
	this.sequenceNumber = sequenceNumber;
    }

    @Basic
    @Column(name = "USER_ACCOUNT")
    public String getUserAccount() {
	return userAccount;
    }

    public void setUserAccount(String userAccount) {
	this.userAccount = userAccount;
    }

    @Basic
    @Column(name = "CATEGORY_CLASSIFICATION_ID")
    public Long getCategoryClassificationId() {
	return categoryClassificationId;
    }

    public void setCategoryClassificationId(Long categoryClassificationId) {
	this.categoryClassificationId = categoryClassificationId;
    }

    @Basic
    @Column(name = "NAVY_FORMATION")
    public String getNavyFormation() {
	return navyFormation;
    }

    public void setNavyFormation(String navyFormation) {
	this.navyFormation = navyFormation;
    }

    @Basic
    @Column(name = "SOCIAL_ID_COPY")
    public String getSocialIdCopy() {
	return socialIdCopy;
    }

    public void setSocialIdCopy(String socialIdCopy) {
	this.socialIdCopy = socialIdCopy;
    }

    @Basic
    @Column(name = "LAST_ANNUAL_RAISE_DATE")
    public Date getLastAnnualRaiseDate() {
	return lastAnnualRaiseDate;
    }

    public void setLastAnnualRaiseDate(Date lastAnnualRaiseDate) {
	this.lastAnnualRaiseDate = lastAnnualRaiseDate;
    }

    @Basic
    @Column(name = "OCCUPATION_DESC")
    public String getOccupationDescription() {
	return occupationDescription;
    }

    public void setOccupationDescription(String occupationDescription) {
	this.occupationDescription = occupationDescription;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "firstName:" + firstName + AUDIT_SEPARATOR +
		"secondName:" + secondName + AUDIT_SEPARATOR +
		"thirdName:" + thirdName + AUDIT_SEPARATOR +
		"lastName:" + lastName + AUDIT_SEPARATOR +
		"categoryId:" + categoryId + AUDIT_SEPARATOR +
		"rankId:" + rankId + AUDIT_SEPARATOR +
		"salaryDegreeId:" + salaryDegreeId + AUDIT_SEPARATOR +
		"countryId:" + countryId + AUDIT_SEPARATOR +
		"birthDate:" + birthDate + AUDIT_SEPARATOR +
		"birthPlace:" + birthPlace + AUDIT_SEPARATOR +
		"socialID:" + socialID + AUDIT_SEPARATOR +
		"socialIDIssueDate:" + socialIDIssueDate + AUDIT_SEPARATOR +
		"socialIDIssuePlaceID:" + socialIDIssuePlaceID + AUDIT_SEPARATOR +
		"bloodGroup:" + bloodGroup + AUDIT_SEPARATOR +
		"gender:" + gender + AUDIT_SEPARATOR +
		"socialStatus:" + socialStatus + AUDIT_SEPARATOR +
		"generalSpecialization:" + generalSpecialization + AUDIT_SEPARATOR +
		"degreeId:" + degreeId + AUDIT_SEPARATOR +
		"statusId:" + statusId + AUDIT_SEPARATOR +
		"graduationGroupNumber:" + graduationGroupNumber + AUDIT_SEPARATOR +
		"graduationGroupPlace:" + graduationGroupPlace + AUDIT_SEPARATOR +
		"militaryNumber:" + militaryNumber + AUDIT_SEPARATOR +
		"recruitmentDecisionNumber:" + recruitmentDecisionNumber + AUDIT_SEPARATOR +
		"recruitmentDecisionDate:" + recruitmentDecisionDate + AUDIT_SEPARATOR +
		"recruitmentDate:" + recruitmentDate + AUDIT_SEPARATOR +
		"recruitmentJoiningDate:" + recruitmentJoiningDate + AUDIT_SEPARATOR +
		"recruitmentRankId:" + recruitmentRankId + AUDIT_SEPARATOR +
		"firstRecruitmentDate:" + firstRecruitmentDate + AUDIT_SEPARATOR +
		"recruitmentAsOfficerDate:" + recruitmentAsOfficerDate + AUDIT_SEPARATOR +
		"jobId:" + jobId + AUDIT_SEPARATOR +
		"rankTitleId:" + rankTitleId + AUDIT_SEPARATOR +
		"lastPromotionDate:" + lastPromotionDate + AUDIT_SEPARATOR +
		"promotionDueDate:" + promotionDueDate + AUDIT_SEPARATOR +
		"physicalUnitId:" + physicalUnitId + AUDIT_SEPARATOR +
		"phoneExt:" + phoneExt + AUDIT_SEPARATOR +
		"ipPhoneExt:" + ipPhoneExt + AUDIT_SEPARATOR +
		"officialMobileNumber:" + officialMobileNumber + AUDIT_SEPARATOR +
		"privateMobileNumber:" + privateMobileNumber + AUDIT_SEPARATOR +
		"shieldMobileNumber:" + shieldMobileNumber + AUDIT_SEPARATOR +
		"email:" + email + AUDIT_SEPARATOR +
		"directPhoneNumber:" + directPhoneNumber + AUDIT_SEPARATOR +
		"jobModifiedFlag:" + jobModifiedFlag + AUDIT_SEPARATOR +
		"movementBlacklistFlag:" + movementBlacklistFlag + AUDIT_SEPARATOR +
		"recruitmentRegionId:" + recruitmentRegionId + AUDIT_SEPARATOR +
		"recruitmentMinorSpecId:" + recruitmentMinorSpecId + AUDIT_SEPARATOR +
		"recTrainingUnitId:" + recTrainingUnitId + AUDIT_SEPARATOR +
		"recTrainingJoiningDate:" + recTrainingJoiningDate + AUDIT_SEPARATOR +
		"exceptionalRecruitmentFlag:" + exceptionalRecruitmentFlag + AUDIT_SEPARATOR +
		"serviceTerminationDate:" + serviceTerminationDate + AUDIT_SEPARATOR +
		"sequenceNumber:" + sequenceNumber + AUDIT_SEPARATOR +
		"userAccount:" + userAccount + AUDIT_SEPARATOR +
		"categoryClassificationId:" + categoryClassificationId + AUDIT_SEPARATOR +
		"navyFormation:" + navyFormation + AUDIT_SEPARATOR +
		"lastAnnualRaiseDate" + lastAnnualRaiseDate + AUDIT_SEPARATOR +
		"occupationDescription" + occupationDescription + AUDIT_SEPARATOR;

    }

}