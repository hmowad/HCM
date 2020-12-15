package com.code.dal.orm.hcm.employees;

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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;
import com.code.enums.FlagsEnum;
import com.code.services.util.HijriDateService;

@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_empData_searchEmpData",
		query = " select e from EmployeeData e" +
			" where "
			+ "   (:P_EMP_NAME = '-1' or e.name like :P_EMP_NAME ) " +
			" and (:P_CATEGORIES_IDS_FLAG = -1 or e.categoryId in ( :P_CATEGORIES_IDS )) " +
			" and (:P_JOB_ID = -1 or e.jobId = :P_JOB_ID) " +
			" and (:P_MILITARY_NUMBER = -1 or e.militaryNumber = :P_MILITARY_NUMBER) " +
			" and (:P_SOCIAL_ID = '-1' or e.socialID = :P_SOCIAL_ID) " +
			" and (:P_PHYSICAL_UNIT_ID = -1 or e.physicalUnitId = :P_PHYSICAL_UNIT_ID ) " +
			" and (:P_OFFICIAL_UNIT_ID = -1 or e.officialUnitId = :P_OFFICIAL_UNIT_ID ) " +
			" and (:P_MAJOR_SPEC_ID = -1 or e.majorSpecId = :P_MAJOR_SPEC_ID) " +
			" and (:P_MINOR_SPEC_ID = -1 or e.minorSpecId = :P_MINOR_SPEC_ID) " +
			" and (:P_STATUSES_IDS_FLAG = -1 or e.statusId in ( :P_STATUSES_IDS )) " +
			" and (:P_SEQUENCE_NUMBER = -1 or e.sequenceNumber = :P_SEQUENCE_NUMBER) " +
			" and (:P_PHYSICAL_UNIT_HKEY = '-1' or physicalUnitHKey like :P_PHYSICAL_UNIT_HKEY ) " +
			" and (:P_PHYSICAL_REGION_ID = -1 or physicalRegionId = :P_PHYSICAL_REGION_ID ) " +
			" and (:P_OFFICIAL_REGION_ID = -1 or officialRegionId = :P_OFFICIAL_REGION_ID ) " +
			" and (:P_JOB_DESC = '-1' or e.jobDesc like :P_JOB_DESC ) " +
			" and (:P_PHYSICAL_UNIT_FULL_NAME = '-1' or e.physicalUnitFullName like :P_PHYSICAL_UNIT_FULL_NAME ) " +
			" and (:P_GENDER = '-1' or e.gender = :P_GENDER) " +
			" and (:P_EXCEPTIONAL_RECRUITMENT_FLAG = -1 or e.exceptionalRecruitmentFlag = :P_EXCEPTIONAL_RECRUITMENT_FLAG ) " +
			" and (:P_RANK_ID = -1 OR e.rankId = :P_RANK_ID)" +
			" order by e.militaryNumber, e.rankId, e.recruitmentDate, e.name "),

	@NamedQuery(name = "hcm_empData_searchEmpDataForBeneficiary",
		query = " select e from EmployeeData e" +
			" where "
			+ "   (:P_EMP_NAME = '-1' or e.name like :P_EMP_NAME ) " +
			" and (:P_CATEGORIES_IDS_FLAG = -1 or e.categoryId in ( :P_CATEGORIES_IDS )) " +
			" and (:P_MILITARY_NUMBER = -1 or e.militaryNumber = :P_MILITARY_NUMBER) " +
			" and (:P_SEQUENCE_NUMBER = -1 or e.sequenceNumber = :P_SEQUENCE_NUMBER) " +
			" and (:P_SOCIAL_ID = '-1' or e.socialID = :P_SOCIAL_ID) " +
			" and (:P_STATUSES_IDS_FLAG = -1 or e.statusId in ( :P_STATUSES_IDS ))" +
			" and  (:P_EMP_IDS_FLAG = -1 or e.empId in ( :P_EMP_IDS )) " +
			" and (:P_JOB_DESC = '-1' or e.jobDesc like :P_JOB_DESC ) " +
			" and (:P_PHYSICAL_UNIT_FULL_NAME = '-1' or e.physicalUnitFullName like :P_PHYSICAL_UNIT_FULL_NAME ) " +
			" order by e.rankId, e.militaryNumber, e.recruitmentDate, e.name "),

	@NamedQuery(name = "hcm_empData_searchEmpDataByOfficialOrPhysicalUnit",
		query = " select e from EmployeeData e" +
			" where "
			+ " (:P_EMP_NAME = '-1' or e.name like :P_EMP_NAME ) " +
			" and (:P_CATEGORIES_IDS_FLAG = -1 or e.categoryId in ( :P_CATEGORIES_IDS)) " +
			" and (:P_JOB_ID = -1 or e.jobId = :P_JOB_ID) " +
			" and (:P_JOB_DESC = '-1' or e.jobDesc like :P_JOB_DESC ) " +
			" and (:P_UNIT_FULL_NAME = '-1' or (e.physicalUnitFullName like :P_UNIT_FULL_NAME or e.officialUnitFullName like :P_UNIT_FULL_NAME)) " +
			" and (:P_MILITARY_NUMBER = -1 or e.militaryNumber = :P_MILITARY_NUMBER) " +
			" and (:P_SEQUENCE_NUMBER = -1 or e.sequenceNumber = :P_SEQUENCE_NUMBER) " +
			" and (:P_REGION_ID = -1 or (e.physicalRegionId = :P_REGION_ID or e.officialRegionId = :P_REGION_ID )) " +
			" and (:P_SOCIAL_ID = '-1' or e.socialID = :P_SOCIAL_ID) " +
			" and (:P_UNIT_ID = -1 or ( e.physicalUnitId = :P_UNIT_ID or e.officialUnitId = :P_UNIT_ID )) " +
			" and (:P_MINOR_SPEC_ID = -1 or e.minorSpecId = :P_MINOR_SPEC_ID) " +
			" and (:P_GENDER = '-1' or e.gender = :P_GENDER) " +
			" order by e.militaryNumber, e.rankId, e.recruitmentDate, e.name "),

	@NamedQuery(name = "hcm_empData_searchEmployeeById",
		query = " select e from EmployeeData e" +
			" where e.empId = :P_EMP_ID "),

	@NamedQuery(name = "hcm_empData_searchEmployeeBySocialId",
		query = " select e from EmployeeData e" +
			" where e.socialID = :P_SOCIAL_ID "),

	@NamedQuery(name = "hcm_empData_searchEmployeeByMilitaryNumber",
		query = " select e from EmployeeData e" +
			" where e.militaryNumber = :P_MILITARY_NUMBER "),

	@NamedQuery(name = "hcm_empData_searchEmployeeDirectManager",
		query = " select m from EmployeeData e, EmployeeData m" +
			" where e.empId = :P_EMP_ID " +
			" and e.managerId = m.empId "),

	@NamedQuery(name = "hcm_empData_searchManagerEmployees",
		query = " select e from EmployeeData e" +
			" where e.managerId = :P_MANAGER_ID " +
			" order by e.militaryNumber, e.rankId, e.recruitmentDate, e.name "),

	@NamedQuery(name = "hcm_empData_countEmpByUnit",
		query = " select count(e.empId) from EmployeeData e " +
			" where (e.officialUnitId in (:P_UNIT_ID) or e.physicalUnitId in (:P_UNIT_ID) ) "),

	@NamedQuery(name = "hcm_empData_countEmpByUnitHkeyPrefix",
		query = " select count(e.empId) from EmployeeData e " +
			" where (e.physicalUnitHKey like :P_PHYSICAL_UNIT_HKEY ) " +
			" and (:P_CATEGORIES_IDS_FLAG = -1  or e.categoryId in (:P_CATEGORIES_IDS) ) "),

	@NamedQuery(name = "hcm_empData_searchEmpDataByRecInfo",
		query = " select e from EmployeeData e" +
			" where (:P_RECRUITMENT_RANK_ID = -1 or e.recruitmentRankId = :P_RECRUITMENT_RANK_ID) " +
			" and (:P_RECRUITMENT_REGION_ID = -1 or e.recruitmentRegionId = :P_RECRUITMENT_REGION_ID) " +
			" and (:P_GRADUATION_GROUP_NUMBER = -1 or e.graduationGroupNumber = :P_GRADUATION_GROUP_NUMBER ) " +
			" and (:P_GRADUATION_GROUP_PLACE = -1 or e.graduationGroupPlace = :P_GRADUATION_GROUP_PLACE ) " +
			" and (:P_RECRUITMENT_MINOR_SPEC_ID = -1 or e.recruitmentMinorSpecId = :P_RECRUITMENT_MINOR_SPEC_ID ) " +
			" and (:P_REC_TRAINING_UNIT_ID = -1 or e.recTrainingUnitId = :P_REC_TRAINING_UNIT_ID ) " +
			" and (:P_CATEGORY_ID = -1 or e.categoryId = :P_CATEGORY_ID ) " +
			" and (:P_STATUSES_IDS_FLAG = -1 or e.statusId in ( :P_STATUSES_IDS )) " +
			" and ((e.militaryNumber is not null and e.recruitmentRegionId is not null) or :P_CATEGORY_ID <> 1) " +
			" and (:P_EMP_NAME = '-1' or e.name like :P_EMP_NAME ) " +
			" and (:P_SEQUENCE_NUMBER = -1 or e.sequenceNumber = :P_SEQUENCE_NUMBER) " +
			" and (:P_SOCIAL_ID = '-1' or e.socialID = :P_SOCIAL_ID) " +
			" and (:P_JOB_DESC = '-1' or e.jobDesc like :P_JOB_DESC ) " +
			" and (:P_PHYSICAL_UNIT_FULL_NAME = '-1' or e.physicalUnitFullName like :P_PHYSICAL_UNIT_FULL_NAME ) " +
			" and (:P_GENDER = '-1' or e.gender = :P_GENDER) " +
			" and (:P_EXCEPTIONAL_RECRUITMENT_FLAG = -1 or e.exceptionalRecruitmentFlag = :P_EXCEPTIONAL_RECRUITMENT_FLAG ) " +
			" and (:P_MILITARY_NUMBER= -1 or e.militaryNumber = :P_MILITARY_NUMBER) " +
			" order by e.militaryNumber, e.rankId, e.recruitmentDate, e.name "),

	@NamedQuery(name = "hcm_empData_searchEmployeesByUnitsIds",
		query = " select e from EmployeeData e " +
			" where e.physicalUnitId in (:P_UNITS_IDS)  " +
			" order by e.militaryNumber, e.rankId, e.recruitmentDate, e.name "),

	@NamedQuery(name = "hcm_empData_searchEmployeesByEmpsIds",
		query = " select e from EmployeeData e " +
			" where e.empId in (:P_EMPS_IDS) " +
			" order by e.militaryNumber, e.rankId, e.recruitmentDate, e.name "),

	@NamedQuery(name = "hcm_empData_searchEmployeesByPromotionDueDate",
		query = " select e from EmployeeData e " +
			" where (:P_DUE_DATE_FLAG = -1 or to_date(:P_DUE_DATE, 'MI/MM/YYYY') >= e.promotionDueDate)" +
			" and (:P_CATEGORY_ID = -1  or e.categoryId = :P_CATEGORY_ID ) " +
			" and (:P_EMP_RANK_FLAG = -1 or e.rankId in (:P_EMP_RANK))" +
			" and (:P_REGION_ID = -1 or e.officialRegionId = :P_REGION_ID ) " +
			" and (SELECT count(rd.empId) from PromotionReportDetail rd where rd.status <> 35 and rd.empId = e.empId and (select r.status from PromotionReport r where r.id = rd.reportId ) != 20 ) = 0 " +
			" and ((e.rankId >= 202 and e.gender = 'F') or e.gender = 'M')" +
			" and e.statusId >= 15 and e.statusId < 50 " +
			" order by e.militaryNumber, e.rankId ,e.recruitmentDate ,e.degreeId, e.jobClassificationCode, e.name "),

	@NamedQuery(name = "hcm_empData_countSoldiersForRecruitment",
		query = " select count(e.empId) from EmployeeData e, RecruitmentWishData r " +
			" where ( (:P_RECRUITMENT_REGION_ID_FLAG = 1 and e.recruitmentRegionId is not null) " +
			" or (:P_RECRUITMENT_REGION_ID_FLAG = 0 and e.recruitmentRegionId is null) " +
			" or (:P_RECRUITMENT_REGION_ID_FLAG = -1) ) " +
			" and (e.id = r.empId) " +
			" and (e.exceptionalRecruitmentFlag = 0) " +
			" and (e.gender = 'M') " +
			" and (e.statusId in (10,12)) "),

	@NamedQuery(name = "hcm_empData_getAllDistributedSoldiers",
		query = "select e from EmployeeData e " +
			" where (e.statusId = 10 or e.statusId = 12) " +
			" and (e.exceptionalRecruitmentFlag = 0) " +
			" and (e.gender = 'M') " +
			" and (e.recruitmentRegionId is not null) "),

	@NamedQuery(name = "hcm_empData_searchEmployeesByRankIdAndPromotionDueDate",
		query = " select e from EmployeeData e " +
			" where (:P_PROMOTION_DUE_DATE = '-1' or to_date(:P_PROMOTION_DUE_DATE, 'MI/MM/YYYY') >= e.promotionDueDate)" +
			" and (:P_CATEGORIES_IDS_FLAG = -1  or e.categoryId in (:P_CATEGORIES_IDS) ) " +
			" and (:P_RANK_IDS_FLAG = -1 or e.rankId in (:P_RANK_IDS))" +
			" and (:P_EMP_NAME = '-1' or e.name like :P_EMP_NAME ) " +
			" and (:P_SOCIAL_ID = '-1' or e.socialID = :P_SOCIAL_ID) " +
			" and (:P_JOB_DESC = '-1' or e.jobDesc like :P_JOB_DESC ) " +
			" and (:P_SEQUENCE_NUMBER = -1 or e.sequenceNumber = :P_SEQUENCE_NUMBER) " +
			" and (:P_MILITARY_NUMBER = -1 or e.militaryNumber = :P_MILITARY_NUMBER) " +
			" and (:P_OFFICIAL_REGION_ID = -1 or e.officialRegionId = :P_OFFICIAL_REGION_ID ) " +
			" and ((e.rankId >= 202 and e.gender = 'F') or e.gender = 'M')" +
			" and e.statusId >= 15 and e.statusId < 50 " +
			" and (:P_PHYSICAL_UNIT_FULL_NAME = '-1' or e.physicalUnitFullName like :P_PHYSICAL_UNIT_FULL_NAME ) " +
			" order by e.militaryNumber, e.rankId, e.recruitmentDate, e.name "),

	@NamedQuery(name = "hcm_empData_countEmpByOfficialUnitHkeyPrefix",
		query = " select count(e.empId) from EmployeeData e" +
			" where e.officialUnitHKey like :P_OFFICIAL_UNIT_HKEY " +
			" and (:P_CATEGORIES_IDS_FLAG = -1  or e.categoryId in (:P_CATEGORIES_IDS) ) " +
			" and e.minorSpecId = :P_MINOR_SPEC_ID "),

	@NamedQuery(name = "hcm_empData_countEmpByPhysicalUnitHkeyPrefix",
		query = " select count(e.empId) from EmployeeData e" +
			" where e.physicalUnitHKey like :P_PHYSICAL_UNIT_HKEY " +
			" and (:P_CATEGORIES_IDS_FLAG = -1  or e.categoryId in (:P_CATEGORIES_IDS) ) " +
			" and e.minorSpecId = :P_MINOR_SPEC_ID "),

	@NamedQuery(name = "hcm_empData_searchEmployeesForFutureServiceTermination",
		query = " select e from EmployeeData e where " +
			" e.serviceTerminationDate <= to_date(:P_EXECUTION_DATE, 'MI/MM/YYYY') " +
			" and e.statusId in (12, 15, 20, 22, 25, 30, 35, 40, 42, 45)"),

	@NamedQuery(name = "hcm_empData_searchEmployeesTerminationEligibility",
		query = " select e From EmployeeData e where " +
			" (:P_TERMINATION_DUE_DATE_FROM_FLAG =-1 or " +
			" 	(e.lastExtensionEndDate is null and to_date(:P_TERMINATION_DUE_DATE_FROM, 'MI/MM/YYYY') <= e.serviceTerminationDueDate) " +
			" 	or " +
			" 	(e.lastExtensionEndDate is not null and to_date(:P_TERMINATION_DUE_DATE_FROM, 'MI/MM/YYYY') <= e.lastExtensionEndDate) " +
			" ) " +
			" and ( " +
			" 	(e.lastExtensionEndDate is null and to_date(:P_TERMINATION_DUE_DATE_TO, 'MI/MM/YYYY') >= e.serviceTerminationDueDate)" +
			" or" +
			" 	(e.lastExtensionEndDate is not null and to_date(:P_TERMINATION_DUE_DATE_TO, 'MI/MM/YYYY') >= e.lastExtensionEndDate)" +
			" ) " +
			" and (:P_RANK_ID = -1 or e.rankId = :P_RANK_ID)" +
			" and (:P_REGION_ID = -1 or e.officialRegionId = :P_REGION_ID ) " +
			" and (e.categoryId = (:P_CATEGORY_ID) ) " +
			" and e.statusId >= 15" +
			" and e.statusId < 50" +
			" and e.serviceTerminationDate is null" +
			" and (select count(td.empId) from TerminationRecordDetailData td where (td.status <> 15 or td.status is null or td.extensionFlag = 1) and td.empId = e.empId and (select tr.status from TerminationRecordData tr where tr.id = td.recordId ) != 15 ) = 0 " +
			" order by e.militaryNumber, e.rankId ,e.recruitmentDate ,e.name"),

	@NamedQuery(name = "hcm_empData_searchEmployeesTerminationEligibilityByRankPeriod",
		query = " select e From EmployeeData e where " +
			" (:P_RANK_ID = -1 or e.rankId = :P_RANK_ID)" +
			" and (:P_REGION_ID = -1 or e.officialRegionId = :P_REGION_ID )" +
			" and e.categoryId = 1" +
			" and e.statusId >= 15" +
			" and e.statusId < 50" +
			" and e.rankId >= 103" +
			" and e.rankId <= 106" +
			" and e.serviceTerminationDate is null" +
			" and (:P_LAST_PRM_DATE_FROM_FLAG = -1 or " +
			"	( e.lastPromotionDate is not null and e.lastPromotionDate >= " +
			"      	  ( case when e.rankId = 103 then to_date(:P_LAST_PRM_DATE_FROM_MJR_GNRL, 'MI/MM/YYYY') else to_date(:P_LAST_PRM_DATE_FROM, 'MI/MM/YYYY') end ) ) " +
			"      or " +
			"	( e.lastPromotionDate is null and e.recruitmentDate >= " +
			"	  ( case when e.rankId = 103 then to_date(:P_LAST_PRM_DATE_FROM_MJR_GNRL, 'MI/MM/YYYY') else to_date(:P_LAST_PRM_DATE_FROM, 'MI/MM/YYYY') end ) ) ) " +
			" and ( " +
			"	( e.lastPromotionDate is not null and e.lastPromotionDate <= " +
			"	  ( case when e.rankId = 103 then to_date(:P_LAST_PRM_DATE_TO_MJR_GNRL, 'MI/MM/YYYY') else to_date(:P_LAST_PRM_DATE_TO, 'MI/MM/YYYY') end ) ) " +
			"      or " +
			"	( e.lastPromotionDate is null and e.recruitmentDate <= " +
			"	  ( case when e.rankId = 103 then to_date(:P_LAST_PRM_DATE_TO_MJR_GNRL, 'MI/MM/YYYY') else to_date(:P_LAST_PRM_DATE_TO, 'MI/MM/YYYY') end ) ) ) " +
			" and (select count(td.empId) from TerminationRecordDetailData td where (td.status <> 15 or td.extensionFlag = 1) and td.empId = e.empId and (select tr.status from TerminationRecordData tr where tr.id = td.recordId ) != 15 ) = 0 " +
			" order by e.militaryNumber, e.rankId ,e.recruitmentDate ,e.name"),

	@NamedQuery(name = "hcm_empData_searchEmployeeCommunicationDataUniqueness",
		query = " select e from EmployeeData e" +
			" where "
			+ " (e.empId <> :P_EXCLUDED_EMP_ID) "
			+ " and ((:P_OFFICIAL_MOBILE_NUMBER in (e.officialMobileNumber, e.privateMobileNumber, e.shieldMobileNumber)) "
			+ " or  (:P_PRIVATE_MOBILE_NUMBER in (e.officialMobileNumber, e.privateMobileNumber, e.shieldMobileNumber)) "
			+ " or  (:P_SHIELD_MOBILE_NUMBER in (e.officialMobileNumber, e.privateMobileNumber, e.shieldMobileNumber)) "
			+ " or  (:P_EMAIL = e.email))"),

	@NamedQuery(name = "hcm_empData_searchEmployeeByCommunicationData",
		query = " select e from EmployeeData e" +
			" where " +
			"   (:P_EMP_NAME = '-1' or e.name like :P_EMP_NAME ) " +
			" and (:P_SOCIAL_ID = '-1' or e.socialID = :P_SOCIAL_ID) " +
			" and (:P_PHYSICAL_UNIT_FULL_NAME = '-1' or e.physicalUnitFullName like :P_PHYSICAL_UNIT_FULL_NAME ) " +
			" and (:P_JOB_DESC = '-1' or e.jobDesc like :P_JOB_DESC ) " +
			" and (:P_OFFICIAL_MOBILE_NUMBER = '-1' or  e.officialMobileNumber like :P_OFFICIAL_MOBILE_NUMBER) " +
			" and (:P_DIRECT_PHONE_NUMBER = '-1' or  e.directPhoneNumber like :P_DIRECT_PHONE_NUMBER) " +
			" and (:P_PRIVATE_MOBILE_NUMBER = '-1' or  e.privateMobileNumber like :P_PRIVATE_MOBILE_NUMBER) " +
			" and (:P_PHONE_EXT = '-1' or  e.phoneExt like :P_PHONE_EXT) " +
			" and (:P_SHIELD_MOBILE_NUMBER = '-1' or  e.shieldMobileNumber like :P_SHIELD_MOBILE_NUMBER) " +
			" and (:P_IP_PHONE_EXT = '-1' or  e.ipPhoneExt like :P_IP_PHONE_EXT) " +
			" and (:P_EMAIL = '-1' or  e.email like :P_EMAIL) " +
			" and (:P_USER_ACCOUNT = '-1' or  e.userAccount like :P_USER_ACCOUNT) " +
			" order by  e.militaryNumber, e.rankId, e.recruitmentDate, e.name "),

	@NamedQuery(name = "hcm_empData_searchTerminatedEmployeesByUnitId",
		query = " select e from EmployeeData e, TerminationTransaction ste " +
			" where e.id = ste.empId " +
			" and (:P_UNIT_ID = '-1' or ste.transEmpUnitId = :P_UNIT_ID ) " +
			" and (ste.transactionTypeId = 33 ) " +
			" and (e.serviceTerminationDate is not null ) " +
			" and (e.statusId = 70 ) " +
			" and (:P_EMP_NAME = '-1' or e.name like :P_EMP_NAME ) " +
			" and (:P_SOCIAL_ID = '-1' or e.socialID = :P_SOCIAL_ID) " +
			" and (:P_PHYSICAL_UNIT_FULL_NAME = '-1' or e.physicalUnitFullName like :P_PHYSICAL_UNIT_FULL_NAME ) " +
			" and (:P_JOB_DESC = '-1' or e.jobDesc like :P_JOB_DESC ) " +
			" and (:P_SEQUENCE_NUMBER = -1 or e.sequenceNumber = :P_SEQUENCE_NUMBER) " +
			" and (:P_MILITARY_NUMBER = -1 or e.militaryNumber = :P_MILITARY_NUMBER) " +
			" order by e.id ")
})
@Entity
@Table(name = "HCM_VW_PRS_EMPLOYEES")
public class EmployeeData extends BaseEntity implements Serializable {
    private Long empId;
    private String name;
    private String firstName;
    private String secondName;
    private String thirdName;
    private String lastName;
    private String firstNameEn;
    private String secondNameEn;
    private String thirdNameEn;
    private String lastNameEn;
    private Long rankId;
    private String rankDesc;
    private Long salaryRankId;
    private String salaryRankDesc;
    private Long salaryDegreeId;
    private String salaryDegreeDesc;
    private Long categoryId;
    private String categoryDesc;
    private Long jobId;
    private String jobCode;
    private String jobDesc;
    private Long jobClassificationId;
    private String jobClassificationCode;
    private String jobClassificationDesc;
    private Long rankTitleId;
    private String rankTitleDesc;
    private Long minorSpecId;
    private String minorSpecDesc;
    private Long majorSpecId;
    private String majorSpecDesc;
    private Long physicalUnitId;
    private String physicalUnitFullName;
    private String physicalUnitHKey;
    private Long physicalRegionId;
    private Long officialUnitId;
    private String officialUnitFullName;
    private String officialUnitHKey;
    private Long officialRegionId;
    private Date recruitmentDate;
    private String recruitmentDateString;
    private Integer militaryNumber;
    private Integer isManager;
    private Integer unitTypeCode;
    private Long managerId;
    private String managerName;
    private String oldEmpId;
    private String phoneExt;
    private String ipPhoneExt;
    private String officialMobileNumber;
    private String privateMobileNumber;
    private String shieldMobileNumber;
    private String email;
    private Long countryId;
    private String nationality;
    private Date birthDate;
    private String birthDateString;
    private String birthPlace;
    private String socialID;
    private Date socialIDIssueDate;
    private String socialIDIssueDateString;
    private Date socialIDExpiryDate;
    private String socialIDExpiryDateString;
    private Long socialIDIssuePlaceID;
    private String socialIDIssuePlaceDesc;
    private String bloodGroup;
    private String gender;
    private Integer socialStatus;
    private Integer generalSpecialization;
    private Long degreeId;
    private String degreeDesc;
    private Long statusId;
    private String statusDesc;
    private Integer graduationGroupNumber;
    private Integer graduationGroupPlace;
    private String recruitmentDecisionNumber;
    private Date recruitmentDecisionDate;
    private String recruitmentDecisionDateString;
    private Date recruitmentJoiningDate;
    private String recruitmentJoiningDateString;
    private Long recruitmentRankId;
    private String recruitmentRankDesc;
    private Date firstRecruitmentDate;
    private String firstRecruitmentDateString;
    private Date recruitmentAsOfficerDate;
    private String recruitmentAsOfficerDateString;
    private Date lastPromotionDate;
    private String lastPromotionDateString;
    private Date promotionDueDate;
    private String promotionDueDateString;
    private String directPhoneNumber;
    private Integer jobModifiedFlag;
    private Integer movementBlacklistFlag;
    private Boolean movementBlacklistFlagBoolean;
    private Long recruitmentRegionId;
    private String recruitmentRegionDesc;
    private Long recruitmentMinorSpecId;
    private String recruitmentMinorSpecDesc;
    private Long recTrainingUnitId;
    private String recTrainingUnitFullName;
    private Date recTrainingJoiningDate;
    private String recTrainingJoiningDateString;
    private Date serviceTerminationDate;
    private String serviceTerminationDateString;
    private Date serviceTerminationDueDate;
    private String serviceTerminationDueDateString;
    private Date lastExtensionEndDate;
    private String lastExtensionEndDateString;
    private Integer exceptionalRecruitmentFlag;
    private Boolean exceptionalRecruitmentFlagBoolean;
    private Long sequenceNumber;
    private String userAccount;
    private Long categoryClassificationId;
    private String navyFormation;
    private String socialIdCopy;
    private Date lastAnnualRaiseDate;
    private String lastAnnualRaiseDateString;
    private String occupationDescription;

    private Employee employee;

    public EmployeeData() {
	employee = new Employee();
    }

    @Id
    @Column(name = "ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	employee.setId(empId);
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
    @Column(name = "CATEGORY_ID")
    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
	employee.setCategoryId(categoryId);
    }

    @Basic
    @Column(name = "CATEGORY_DESC")
    @XmlTransient
    public String getCategoryDesc() {
	return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
	this.categoryDesc = categoryDesc;
    }

    @Basic
    @Column(name = "JOB_ID")
    @XmlElement(nillable = true)
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
	employee.setJobId(jobId);
    }

    @Basic
    @Column(name = "JOB_CODE")
    @XmlTransient
    public String getJobCode() {
	return jobCode;
    }

    public void setJobCode(String jobCode) {
	this.jobCode = jobCode;
    }

    @Basic
    @Column(name = "JOB_DESC")
    @XmlElement(nillable = true)
    public String getJobDesc() {
	return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
	this.jobDesc = jobDesc;
    }

    @Basic
    @Column(name = "JOB_CLASSIFICATION_ID")
    @XmlTransient
    public Long getJobClassificationId() {
	return jobClassificationId;
    }

    @Basic
    @Column(name = "JOB_CLASSIFICATION_CODE")
    @XmlTransient
    public String getJobClassificationCode() {
	return jobClassificationCode;
    }

    public void setJobClassificationCode(String jobClassificationCode) {
	this.jobClassificationCode = jobClassificationCode;
    }

    public void setJobClassificationId(Long jobClassificationId) {
	this.jobClassificationId = jobClassificationId;
    }

    @Basic
    @Column(name = "JOB_CLASSIFICATION_DESCRIPTION")
    @XmlTransient
    public String getJobClassificationDesc() {
	return jobClassificationDesc;
    }

    public void setJobClassificationDesc(String jobClassificationDesc) {
	this.jobClassificationDesc = jobClassificationDesc;
    }

    @Basic
    @Column(name = "MINOR_SPECIALIZATION_ID")
    @XmlTransient
    public Long getMinorSpecId() {
	return minorSpecId;
    }

    public void setMinorSpecId(Long minorSpecId) {
	this.minorSpecId = minorSpecId;
    }

    @Basic
    @Column(name = "MINOR_SPEC_DESCRIPTION")
    @XmlTransient
    public String getMinorSpecDesc() {
	return minorSpecDesc;
    }

    public void setMinorSpecDesc(String minorSpecDesc) {
	this.minorSpecDesc = minorSpecDesc;
    }

    @Basic
    @Column(name = "MAJOR_SPECIALIZATION_ID")
    @XmlTransient
    public Long getMajorSpecId() {
	return majorSpecId;
    }

    public void setMajorSpecId(Long majorSpecId) {
	this.majorSpecId = majorSpecId;
    }

    @Basic
    @Column(name = "MAJOR_SPEC_DESCRIPTION")
    @XmlTransient
    public String getMajorSpecDesc() {
	return majorSpecDesc;
    }

    public void setMajorSpecDesc(String majorSpecDesc) {
	this.majorSpecDesc = majorSpecDesc;
    }

    @Basic
    @Column(name = "RANK_ID")
    @XmlElement(nillable = true)
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
	employee.setRankId(rankId);
    }

    @Basic
    @Column(name = "RANK_DESC")
    @XmlElement(nillable = true)
    public String getRankDesc() {
	return rankDesc;
    }

    public void setRankDesc(String rankDesc) {
	this.rankDesc = rankDesc;
    }

    @Basic
    @Column(name = "SALARY_RANK_ID")
    @XmlTransient
    public Long getSalaryRankId() {
	return salaryRankId;
    }

    public void setSalaryRankId(Long salaryRankId) {
	this.salaryRankId = salaryRankId;
	this.employee.setSalaryRankId(salaryRankId);
    }

    @Basic
    @Column(name = "SALARY_RANK_DESC")
    @XmlTransient
    public String getSalaryRankDesc() {
	return salaryRankDesc;
    }

    public void setSalaryRankDesc(String salaryRankDesc) {
	this.salaryRankDesc = salaryRankDesc;
    }

    @Basic
    @Column(name = "SALARY_DEGREE_ID")
    @XmlTransient
    public Long getSalaryDegreeId() {
	return salaryDegreeId;
    }

    public void setSalaryDegreeId(Long salaryDegreeId) {
	this.salaryDegreeId = salaryDegreeId;
	this.employee.setSalaryDegreeId(salaryDegreeId);
    }

    @Basic
    @Column(name = "SALARY_DEGREE_DESC")
    @XmlTransient
    public String getSalaryDegreeDesc() {
	return salaryDegreeDesc;
    }

    public void setSalaryDegreeDesc(String salaryDegreeDesc) {
	this.salaryDegreeDesc = salaryDegreeDesc;
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_ID")
    @XmlElement(nillable = true)
    public Long getPhysicalUnitId() {
	return physicalUnitId;
    }

    public void setPhysicalUnitId(Long physicalUnitId) {
	this.physicalUnitId = physicalUnitId;
	employee.setPhysicalUnitId(physicalUnitId);
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_FULL_NAME")
    @XmlElement(nillable = true)
    public String getPhysicalUnitFullName() {
	return physicalUnitFullName;
    }

    public void setPhysicalUnitFullName(String physicalUnitFullName) {
	this.physicalUnitFullName = physicalUnitFullName;
	employee.setPhysicalUnitId(physicalUnitId);
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_HKEY")
    @XmlTransient
    public String getPhysicalUnitHKey() {
	return physicalUnitHKey;
    }

    public void setPhysicalUnitHKey(String physicalUnitHKey) {
	this.physicalUnitHKey = physicalUnitHKey;
    }

    @Basic
    @Column(name = "PHYSICAL_REGION_ID")
    @XmlElement(nillable = true)
    public Long getPhysicalRegionId() {
	return physicalRegionId;
    }

    public void setPhysicalRegionId(Long physicalRegionId) {
	this.physicalRegionId = physicalRegionId;
    }

    @Basic
    @Column(name = "OFFICIAL_UNIT_ID")
    @XmlElement(nillable = true)
    public Long getOfficialUnitId() {
	return officialUnitId;
    }

    public void setOfficialUnitId(Long officialUnitId) {
	this.officialUnitId = officialUnitId;
    }

    @Basic
    @Column(name = "OFFICIAL_UNIT_FULL_NAME")
    @XmlElement(nillable = true)
    public String getOfficialUnitFullName() {
	return officialUnitFullName;
    }

    public void setOfficialUnitFullName(String officialUnitFullName) {
	this.officialUnitFullName = officialUnitFullName;
    }

    @Basic
    @Column(name = "OFFICIAL_UNIT_HKEY")
    @XmlTransient
    public String getOfficialUnitHKey() {
	return officialUnitHKey;
    }

    public void setOfficialUnitHKey(String officialUnitHKey) {
	this.officialUnitHKey = officialUnitHKey;
    }

    @Basic
    @Column(name = "OFFICIAL_REGION_ID")
    @XmlElement(nillable = true)
    public Long getOfficialRegionId() {
	return officialRegionId;
    }

    public void setOfficialRegionId(Long officialRegionId) {
	this.officialRegionId = officialRegionId;
    }

    @Basic
    @Column(name = "RECRUITMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getRecruitmentDate() {
	return recruitmentDate;
    }

    public void setRecruitmentDate(Date recruitmentDate) {
	this.recruitmentDate = recruitmentDate;
	this.recruitmentDateString = HijriDateService.getHijriDateString(recruitmentDate);
	employee.setRecruitmentDate(recruitmentDate);
    }

    @Transient
    @XmlTransient
    public String getRecruitmentDateString() {
	return recruitmentDateString;
    }

    public void setRecruitmentDateString(String recruitmentDateString) {
	this.recruitmentDateString = recruitmentDateString;
	this.recruitmentDate = HijriDateService.getHijriDate(recruitmentDateString);
	employee.setRecruitmentDate(recruitmentDate);
    }

    @Basic
    @Column(name = "MILITARY_NUMBER")
    @XmlTransient
    public Integer getMilitaryNumber() {
	return militaryNumber;
    }

    public void setMilitaryNumber(Integer militaryNumber) {
	this.militaryNumber = militaryNumber;
	employee.setMilitaryNumber(militaryNumber);
    }

    @Basic
    @Column(name = "IS_MANAGER")
    @XmlTransient
    public Integer getIsManager() {
	return isManager;
    }

    public void setIsManager(Integer isManager) {
	this.isManager = isManager;
    }

    @Basic
    @Column(name = "UNIT_TYPE_CODE")
    @XmlTransient
    public Integer getUnitTypeCode() {
	return unitTypeCode;
    }

    public void setUnitTypeCode(Integer unitTypeCode) {
	this.unitTypeCode = unitTypeCode;
    }

    @Basic
    @Column(name = "MANAGER_ID")
    @XmlTransient
    public Long getManagerId() {
	return managerId;
    }

    public void setManagerId(Long managerId) {
	this.managerId = managerId;
    }

    @Basic
    @Column(name = "MANAGER_NAME")
    @XmlTransient
    public String getManagerName() {
	return managerName;
    }

    public void setManagerName(String managerName) {
	this.managerName = managerName;
    }

    @Basic
    @Column(name = "EMP_OLD_ID")
    @XmlTransient
    public String getOldEmpId() {
	return oldEmpId;
    }

    public void setOldEmpId(String oldEmpId) {
	this.oldEmpId = oldEmpId;
	employee.setOldEmpId(oldEmpId);
    }

    @Basic
    @Column(name = "OFFICIAL_MOBILE_NUMBER")
    @XmlElement(nillable = true)
    public String getOfficialMobileNumber() {
	return officialMobileNumber;
    }

    public void setOfficialMobileNumber(String officialMobileNumber) {
	this.officialMobileNumber = officialMobileNumber;
	employee.setOfficialMobileNumber(officialMobileNumber);
    }

    @Basic
    @Column(name = "PRIVATE_MOBILE_NUMBER")
    @XmlElement(nillable = true)
    public String getPrivateMobileNumber() {
	return privateMobileNumber;
    }

    public void setPrivateMobileNumber(String privateMobileNumber) {
	this.privateMobileNumber = privateMobileNumber;
	employee.setPrivateMobileNumber(privateMobileNumber);
    }

    @Basic
    @Column(name = "SHIELD_MOBILE_NUMBER")
    @XmlElement(nillable = true)
    public String getShieldMobileNumber() {
	return shieldMobileNumber;
    }

    public void setShieldMobileNumber(String shieldMobileNumber) {
	this.shieldMobileNumber = shieldMobileNumber;
	employee.setShieldMobileNumber(shieldMobileNumber);
    }

    @Basic
    @Column(name = "FIRST_NAME")
    @XmlTransient
    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
	employee.setFirstName(firstName);
    }

    @Basic
    @Column(name = "SECOND_NAME")
    @XmlTransient
    public String getSecondName() {
	return secondName;
    }

    public void setSecondName(String secondName) {
	this.secondName = secondName;
	employee.setSecondName(secondName);
    }

    @Basic
    @Column(name = "THIRD_NAME")
    @XmlTransient
    public String getThirdName() {
	return thirdName;
    }

    public void setThirdName(String thirdName) {
	this.thirdName = thirdName;
	employee.setThirdName(thirdName);
    }

    @Basic
    @Column(name = "LAST_NAME")
    @XmlTransient
    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
	employee.setLastName(lastName);
    }

    @Basic
    @Column(name = "FIRST_NAME_EN")
    @XmlTransient
    public String getFirstNameEn() {
	return firstNameEn;
    }

    public void setFirstNameEn(String firstNameEn) {
	this.firstNameEn = firstNameEn;
	employee.setFirstNameEn(firstNameEn);
    }

    @Basic
    @Column(name = "SECOND_NAME_EN")
    @XmlTransient
    public String getSecondNameEn() {
	return secondNameEn;
    }

    public void setSecondNameEn(String secondNameEn) {
	this.secondNameEn = secondNameEn;
	employee.setSecondNameEn(secondNameEn);
    }

    @Basic
    @Column(name = "THIRD_NAME_EN")
    @XmlTransient
    public String getThirdNameEn() {
	return thirdNameEn;
    }

    public void setThirdNameEn(String thirdNameEn) {
	this.thirdNameEn = thirdNameEn;
	employee.setThirdNameEn(thirdNameEn);
    }

    @Basic
    @Column(name = "LAST_NAME_EN")
    @XmlTransient
    public String getLastNameEn() {
	return lastNameEn;
    }

    public void setLastNameEn(String lastNameEn) {
	this.lastNameEn = lastNameEn;
	employee.setLastNameEn(lastNameEn);
    }

    @Basic
    @Column(name = "COUNTRY_ID")
    @XmlTransient
    public Long getCountryId() {
	return countryId;
    }

    public void setCountryId(Long countryId) {
	this.countryId = countryId;
	employee.setCountryId(countryId);
    }

    @Basic
    @Column(name = "NATIONALITY")
    @XmlTransient
    public String getNationality() {
	return nationality;
    }

    public void setNationality(String nationality) {
	this.nationality = nationality;
    }

    @Basic
    @Column(name = "BIRTH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getBirthDate() {
	return birthDate;
    }

    public void setBirthDate(Date birthDate) {
	this.birthDate = birthDate;
	this.birthDateString = HijriDateService.getHijriDateString(birthDate);
	employee.setBirthDate(birthDate);
    }

    @Transient
    @XmlElement(nillable = true)
    public String getBirthDateString() {
	return birthDateString;
    }

    public void setBirthDateString(String birthDateString) {
	this.birthDateString = birthDateString;
	this.birthDate = HijriDateService.getHijriDate(birthDateString);
	employee.setBirthDate(birthDate);
    }

    @Basic
    @Column(name = "BIRTH_PLACE")
    @XmlTransient
    public String getBirthPlace() {
	return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
	this.birthPlace = birthPlace;
	employee.setBirthPlace(birthPlace);
    }

    @Basic
    @Column(name = "SOCIAL_ID")
    @XmlElement(nillable = true)
    public String getSocialID() {
	return socialID;
    }

    public void setSocialID(String socialID) {
	this.socialID = socialID;
	employee.setSocialID(socialID);
    }

    @Basic
    @Column(name = "SOCIAL_ID_ISSUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getSocialIDIssueDate() {
	return socialIDIssueDate;
    }

    public void setSocialIDIssueDate(Date socialIDIssueDate) {
	this.socialIDIssueDate = socialIDIssueDate;
	this.socialIDIssueDateString = HijriDateService.getHijriDateString(socialIDIssueDate);
	employee.setSocialIDIssueDate(socialIDIssueDate);
    }

    @Basic
    @Column(name = "SOCIAL_ID_EXPIRY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getSocialIDExpiryDate() {
	return socialIDExpiryDate;
    }

    public void setSocialIDExpiryDate(Date socialIDExpiryDate) {
	this.socialIDExpiryDate = socialIDExpiryDate;
	this.socialIDExpiryDateString = HijriDateService.getHijriDateString(socialIDExpiryDate);
	employee.setSocialIDExpiryDate(socialIDExpiryDate);
    }

    @Transient
    @XmlTransient
    public String getSocialIDExpiryDateString() {
	return socialIDExpiryDateString;
    }

    public void setSocialIDExpiryDateString(String socialIDExpiryDateString) {
	this.socialIDExpiryDateString = socialIDExpiryDateString;
	this.socialIDExpiryDate = HijriDateService.getHijriDate(socialIDExpiryDateString);
	employee.setSocialIDExpiryDate(socialIDExpiryDate);
    }

    @Transient
    @XmlTransient
    public String getSocialIDIssueDateString() {
	return socialIDIssueDateString;
    }

    public void setSocialIDIssueDateString(String socialIDIssueDateString) {
	this.socialIDIssueDateString = socialIDIssueDateString;
	this.socialIDIssueDate = HijriDateService.getHijriDate(socialIDIssueDateString);
	employee.setSocialIDIssueDate(socialIDIssueDate);
    }

    @Basic
    @Column(name = "SOCIAL_ID_ISSUE_PLACE_ID")
    @XmlTransient
    public Long getSocialIDIssuePlaceID() {
	return socialIDIssuePlaceID;
    }

    public void setSocialIDIssuePlaceID(Long socialIDIssuePlaceID) {
	this.socialIDIssuePlaceID = socialIDIssuePlaceID;
	employee.setSocialIDIssuePlaceID(socialIDIssuePlaceID);
    }

    @Basic
    @Column(name = "SOCIAL_ID_ISSUE_PLACE_DESC")
    @XmlTransient
    public String getSocialIDIssuePlaceDesc() {
	return socialIDIssuePlaceDesc;
    }

    public void setSocialIDIssuePlaceDesc(String socialIDIssuePlaceDesc) {
	this.socialIDIssuePlaceDesc = socialIDIssuePlaceDesc;
    }

    @Basic
    @Column(name = "BLOOD_GROUP")
    @XmlTransient
    public String getBloodGroup() {
	return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
	employee.setBloodGroup(bloodGroup);
    }

    @Basic
    @Column(name = "GENDER")
    @XmlTransient
    public String getGender() {
	return gender;
    }

    public void setGender(String gender) {
	this.gender = gender;
	employee.setGender(gender);
    }

    @Basic
    @Column(name = "SOCIAL_STATUS")
    @XmlTransient
    public Integer getSocialStatus() {
	return socialStatus;
    }

    public void setSocialStatus(Integer socialStatus) {
	this.socialStatus = socialStatus;
	employee.setSocialStatus(socialStatus);
    }

    @Basic
    @Column(name = "GENERAL_SPECIALIZATION")
    @XmlTransient
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
	employee.setGeneralSpecialization(generalSpecialization);
    }

    @Basic
    @Column(name = "DEGREE_ID")
    @XmlTransient
    public Long getDegreeId() {
	return degreeId;
    }

    public void setDegreeId(Long degreeId) {
	this.degreeId = degreeId;
	employee.setDegreeId(degreeId);
    }

    @Basic
    @Column(name = "DEGREE_DESC")
    @XmlTransient
    public String getDegreeDesc() {
	return degreeDesc;
    }

    public void setDegreeDesc(String degreeDesc) {
	this.degreeDesc = degreeDesc;
    }

    @Basic
    @Column(name = "STATUS_ID")
    public Long getStatusId() {
	return statusId;
    }

    public void setStatusId(Long statusId) {
	this.statusId = statusId;
	employee.setStatusId(statusId);
    }

    @Basic
    @Column(name = "STATUS_DESC")
    public String getStatusDesc() {
	return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
	this.statusDesc = statusDesc;
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_NUMBER")
    @XmlTransient
    public Integer getGraduationGroupNumber() {
	return graduationGroupNumber;
    }

    public void setGraduationGroupNumber(Integer graduationGroupNumber) {
	this.graduationGroupNumber = graduationGroupNumber;
	employee.setGraduationGroupNumber(graduationGroupNumber);
    }

    @Basic
    @Column(name = "GRADUATION_GROUP_PLACE")
    @XmlTransient
    public Integer getGraduationGroupPlace() {
	return graduationGroupPlace;
    }

    public void setGraduationGroupPlace(Integer graduationGroupPlace) {
	this.graduationGroupPlace = graduationGroupPlace;
	employee.setGraduationGroupPlace(graduationGroupPlace);
    }

    @Basic
    @Column(name = "RECRUITMENT_DECISION_NUMBER")
    @XmlTransient
    public String getRecruitmentDecisionNumber() {
	return recruitmentDecisionNumber;
    }

    public void setRecruitmentDecisionNumber(String recruitmentDecisionNumber) {
	this.recruitmentDecisionNumber = recruitmentDecisionNumber;
	employee.setRecruitmentDecisionNumber(recruitmentDecisionNumber);
    }

    @Basic
    @Column(name = "RECRUITMENT_DECISION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getRecruitmentDecisionDate() {
	return recruitmentDecisionDate;
    }

    public void setRecruitmentDecisionDate(Date recruitmentDecisionDate) {
	this.recruitmentDecisionDate = recruitmentDecisionDate;
	this.recruitmentDecisionDateString = HijriDateService.getHijriDateString(recruitmentDecisionDate);
	employee.setRecruitmentDecisionDate(recruitmentDecisionDate);
    }

    @Transient
    @XmlTransient
    public String getRecruitmentDecisionDateString() {
	return recruitmentDecisionDateString;
    }

    public void setRecruitmentDecisionDateString(String recruitmentDecisionDateString) {
	this.recruitmentDecisionDateString = recruitmentDecisionDateString;
	this.recruitmentDecisionDate = HijriDateService.getHijriDate(recruitmentDecisionDateString);
	employee.setRecruitmentDecisionDate(recruitmentDecisionDate);
    }

    @Basic
    @Column(name = "RECRUITMENT_JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getRecruitmentJoiningDate() {
	return recruitmentJoiningDate;
    }

    public void setRecruitmentJoiningDate(Date recruitmentJoiningDate) {
	this.recruitmentJoiningDate = recruitmentJoiningDate;
	this.recruitmentJoiningDateString = HijriDateService.getHijriDateString(recruitmentJoiningDate);
	employee.setRecruitmentJoiningDate(recruitmentJoiningDate);
    }

    @Transient
    @XmlTransient
    public String getRecruitmentJoiningDateString() {
	return recruitmentJoiningDateString;
    }

    public void setRecruitmentJoiningDateString(String recruitmentJoiningDateString) {
	this.recruitmentJoiningDateString = recruitmentJoiningDateString;
	this.recruitmentJoiningDate = HijriDateService.getHijriDate(recruitmentJoiningDateString);
	employee.setRecruitmentJoiningDate(recruitmentJoiningDate);
    }

    @Basic
    @Column(name = "RECRUITMENT_RANK_ID")
    @XmlTransient
    public Long getRecruitmentRankId() {
	return recruitmentRankId;
    }

    public void setRecruitmentRankId(Long recruitmentRankId) {
	this.recruitmentRankId = recruitmentRankId;
	employee.setRecruitmentRankId(recruitmentRankId);
    }

    @Basic
    @Column(name = "RECRUITMENT_RANK_DESC")
    @XmlTransient
    public String getRecruitmentRankDesc() {
	return recruitmentRankDesc;
    }

    public void setRecruitmentRankDesc(String recruitmentRankDesc) {
	this.recruitmentRankDesc = recruitmentRankDesc;
    }

    @Basic
    @Column(name = "FIRST_RECRUITMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getFirstRecruitmentDate() {
	return firstRecruitmentDate;
    }

    public void setFirstRecruitmentDate(Date firstRecruitmentDate) {
	this.firstRecruitmentDate = firstRecruitmentDate;
	this.firstRecruitmentDateString = HijriDateService.getHijriDateString(firstRecruitmentDate);
	employee.setFirstRecruitmentDate(firstRecruitmentDate);
    }

    @Transient
    @XmlTransient
    public String getFirstRecruitmentDateString() {
	return firstRecruitmentDateString;
    }

    public void setFirstRecruitmentDateString(String firstRecruitmentDateString) {
	this.firstRecruitmentDateString = firstRecruitmentDateString;
	this.firstRecruitmentDate = HijriDateService.getHijriDate(firstRecruitmentDateString);
	employee.setFirstRecruitmentDate(firstRecruitmentDate);
    }

    @Basic
    @Column(name = "RECRUITMENT_AS_OFFICER_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getRecruitmentAsOfficerDate() {
	return recruitmentAsOfficerDate;
    }

    public void setRecruitmentAsOfficerDate(Date recruitmentAsOfficerDate) {
	this.recruitmentAsOfficerDate = recruitmentAsOfficerDate;
	this.recruitmentAsOfficerDateString = HijriDateService.getHijriDateString(recruitmentAsOfficerDate);
	employee.setRecruitmentAsOfficerDate(recruitmentAsOfficerDate);
    }

    @Transient
    @XmlTransient
    public String getRecruitmentAsOfficerDateString() {
	return recruitmentAsOfficerDateString;
    }

    public void setRecruitmentAsOfficerDateString(String recruitmentAsOfficerDateString) {
	this.recruitmentAsOfficerDateString = recruitmentAsOfficerDateString;
	this.recruitmentAsOfficerDate = HijriDateService.getHijriDate(recruitmentAsOfficerDateString);
	employee.setRecruitmentAsOfficerDate(recruitmentAsOfficerDate);
    }

    @Basic
    @Column(name = "RANK_TITLE_ID")
    @XmlElement(nillable = true)
    public Long getRankTitleId() {
	return rankTitleId;
    }

    public void setRankTitleId(Long rankTitleId) {
	this.rankTitleId = rankTitleId;
	employee.setRankTitleId(rankTitleId);
    }

    @Basic
    @Column(name = "RANK_TITLE_DESC")
    @XmlElement(nillable = true)
    public String getRankTitleDesc() {
	return rankTitleDesc;
    }

    public void setRankTitleDesc(String rankTitleDesc) {
	this.rankTitleDesc = rankTitleDesc;
    }

    @Basic
    @Column(name = "LAST_PROMOTION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getLastPromotionDate() {
	return lastPromotionDate;
    }

    public void setLastPromotionDate(Date lastPromotionDate) {
	this.lastPromotionDate = lastPromotionDate;
	this.lastPromotionDateString = HijriDateService.getHijriDateString(lastPromotionDate);
	employee.setLastPromotionDate(lastPromotionDate);
    }

    @Transient
    @XmlTransient
    public String getLastPromotionDateString() {
	return lastPromotionDateString;
    }

    public void setLastPromotionDateString(String lastPromotionDateString) {
	this.lastPromotionDateString = lastPromotionDateString;
	this.lastPromotionDate = HijriDateService.getHijriDate(lastPromotionDateString);
	employee.setLastPromotionDate(lastPromotionDate);
    }

    @Basic
    @Column(name = "PROMOTION_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getPromotionDueDate() {
	return promotionDueDate;
    }

    public void setPromotionDueDate(Date promotionDueDate) {
	this.promotionDueDate = promotionDueDate;
	this.promotionDueDateString = HijriDateService.getHijriDateString(promotionDueDate);
	employee.setPromotionDueDate(promotionDueDate);
    }

    @Transient
    @XmlTransient
    public String getPromotionDueDateString() {
	return promotionDueDateString;
    }

    public void setPromotionDueDateString(String promotionDueDateString) {
	this.promotionDueDateString = promotionDueDateString;
	this.promotionDueDate = HijriDateService.getHijriDate(promotionDueDateString);
	employee.setPromotionDueDate(promotionDueDate);
    }

    @Basic
    @Column(name = "PHONE_EXT")
    @XmlElement(nillable = true)
    public String getPhoneExt() {
	return phoneExt;
    }

    public void setPhoneExt(String phoneExt) {
	this.phoneExt = phoneExt;
	employee.setPhoneExt(phoneExt);
    }

    @Basic
    @Column(name = "IP_PHONE_EXT")
    @XmlElement(nillable = true)
    public String getIpPhoneExt() {
	return ipPhoneExt;
    }

    public void setIpPhoneExt(String ipPhoneExt) {
	this.ipPhoneExt = ipPhoneExt;
	employee.setIpPhoneExt(ipPhoneExt);
    }

    @Basic
    @Column(name = "EMAIL")
    @XmlElement(nillable = true)
    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
	employee.setEmail(email);
    }

    @Basic
    @Column(name = "DIRECT_PHONE_NUMBER")
    @XmlTransient
    public String getDirectPhoneNumber() {
	return directPhoneNumber;
    }

    public void setDirectPhoneNumber(String directPhoneNumber) {
	this.directPhoneNumber = directPhoneNumber;
	employee.setDirectPhoneNumber(directPhoneNumber);
    }

    @Basic
    @Column(name = "JOB_MODIFIED_FLAG")
    @XmlTransient
    public Integer getJobModifiedFlag() {
	return jobModifiedFlag;
    }

    public void setJobModifiedFlag(Integer jobModifiedFlag) {
	this.jobModifiedFlag = jobModifiedFlag;
	employee.setJobModifiedFlag(jobModifiedFlag);
    }

    @Basic
    @Column(name = "MOVEMENT_BLACK_LIST_FLAG")
    @XmlTransient
    public Integer getMovementBlacklistFlag() {
	return movementBlacklistFlag;
    }

    public void setMovementBlacklistFlag(Integer movementBlacklistFlag) {
	this.movementBlacklistFlag = movementBlacklistFlag;
	if (this.movementBlacklistFlag == null || this.movementBlacklistFlag == FlagsEnum.OFF.getCode())
	    this.movementBlacklistFlagBoolean = false;
	else
	    this.movementBlacklistFlagBoolean = true;
	employee.setMovementBlacklistFlag(movementBlacklistFlag);
    }

    @Transient
    @XmlTransient
    public Boolean getMovementBlacklistFlagBoolean() {
	return movementBlacklistFlagBoolean;
    }

    public void setMovementBlacklistFlagBoolean(Boolean movementBlacklistFlagBoolean) {
	this.movementBlacklistFlagBoolean = movementBlacklistFlagBoolean;

	if (this.movementBlacklistFlagBoolean == null || !this.movementBlacklistFlagBoolean) {
	    this.movementBlacklistFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.movementBlacklistFlag = FlagsEnum.ON.getCode();
	}

	employee.setMovementBlacklistFlag(movementBlacklistFlag);
    }

    @Basic
    @Column(name = "RECRUITMENT_REGION_ID")
    @XmlTransient
    public Long getRecruitmentRegionId() {
	return recruitmentRegionId;
    }

    public void setRecruitmentRegionId(Long recruitmentRegionId) {
	this.recruitmentRegionId = recruitmentRegionId;
	employee.setRecruitmentRegionId(recruitmentRegionId);
    }

    @Basic
    @Column(name = "RECRUITMENT_REGION_DESC")
    @XmlTransient
    public String getRecruitmentRegionDesc() {
	return recruitmentRegionDesc;
    }

    public void setRecruitmentRegionDesc(String recruitmentRegionDesc) {
	this.recruitmentRegionDesc = recruitmentRegionDesc;
    }

    @Basic
    @Column(name = "RECRUITMENT_MINOR_SPEC_ID")
    @XmlTransient
    public Long getRecruitmentMinorSpecId() {
	return recruitmentMinorSpecId;
    }

    public void setRecruitmentMinorSpecId(Long recruitmentMinorSpecId) {
	this.recruitmentMinorSpecId = recruitmentMinorSpecId;
	employee.setRecruitmentMinorSpecId(recruitmentMinorSpecId);
    }

    @Basic
    @Column(name = "RECRUITMENT_MINOR_SPEC_DESC")
    @XmlTransient
    public String getRecruitmentMinorSpecDesc() {
	return recruitmentMinorSpecDesc;
    }

    public void setRecruitmentMinorSpecDesc(String recruitmentMinorSpecDesc) {
	this.recruitmentMinorSpecDesc = recruitmentMinorSpecDesc;
    }

    @Basic
    @Column(name = "REC_TRAINING_UNIT_ID")
    @XmlTransient
    public Long getRecTrainingUnitId() {
	return recTrainingUnitId;
    }

    public void setRecTrainingUnitId(Long recTrainingUnitId) {
	this.recTrainingUnitId = recTrainingUnitId;
	employee.setRecTrainingUnitId(recTrainingUnitId);
    }

    @Basic
    @Column(name = "REC_TRAINING_UNIT_FULL_NAME")
    @XmlTransient
    public String getRecTrainingUnitFullName() {
	return recTrainingUnitFullName;
    }

    public void setRecTrainingUnitFullName(String recTrainingUnitFullName) {
	this.recTrainingUnitFullName = recTrainingUnitFullName;
    }

    @Basic
    @Column(name = "REC_TRAINING_JOINING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getRecTrainingJoiningDate() {
	return recTrainingJoiningDate;
    }

    public void setRecTrainingJoiningDate(Date recTrainingJoiningDate) {
	this.recTrainingJoiningDate = recTrainingJoiningDate;
	this.recTrainingJoiningDateString = HijriDateService.getHijriDateString(recTrainingJoiningDate);
	employee.setRecTrainingJoiningDate(recTrainingJoiningDate);
    }

    @Transient
    @XmlTransient
    public String getRecTrainingJoiningDateString() {
	return recTrainingJoiningDateString;
    }

    public void setRecTrainingJoiningDateString(String recTrainingJoiningDateString) {
	this.recTrainingJoiningDateString = recTrainingJoiningDateString;
	this.recTrainingJoiningDate = HijriDateService.getHijriDate(recTrainingJoiningDateString);
	employee.setRecTrainingJoiningDate(recTrainingJoiningDate);
    }

    @Basic
    @Column(name = "SERVICE_TERMINATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getServiceTerminationDate() {
	return serviceTerminationDate;
    }

    public void setServiceTerminationDate(Date serviceTerminationDate) {
	this.serviceTerminationDate = serviceTerminationDate;
	this.serviceTerminationDateString = HijriDateService.getHijriDateString(serviceTerminationDate);
	employee.setServiceTerminationDate(serviceTerminationDate);
    }

    @Transient
    @XmlTransient
    public String getServiceTerminationDateString() {
	return serviceTerminationDateString;
    }

    public void setServiceTerminationDateString(String serviceTerminationDateString) {
	this.serviceTerminationDateString = serviceTerminationDateString;
	this.serviceTerminationDate = HijriDateService.getHijriDate(serviceTerminationDateString);
	employee.setServiceTerminationDate(serviceTerminationDate);
    }

    @Basic
    @Column(name = "SERVICE_TERMINATION_DUE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getServiceTerminationDueDate() {
	return serviceTerminationDueDate;
    }

    public void setServiceTerminationDueDate(Date serviceTerminationDueDate) {
	this.serviceTerminationDueDate = serviceTerminationDueDate;
	this.serviceTerminationDueDateString = HijriDateService.getHijriDateString(serviceTerminationDueDate);
	employee.setServiceTerminationDueDate(serviceTerminationDueDate);
    }

    @Transient
    @XmlTransient
    public String getServiceTerminationDueDateString() {
	return serviceTerminationDueDateString;
    }

    public void setServiceTerminationDueDateString(String serviceTerminationDueDateString) {
	this.serviceTerminationDueDateString = serviceTerminationDueDateString;
	this.serviceTerminationDueDate = HijriDateService.getHijriDate(serviceTerminationDueDateString);
	employee.setServiceTerminationDueDate(serviceTerminationDueDate);
    }

    @Basic
    @Column(name = "LAST_EXTENSION_END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getLastExtensionEndDate() {
	return lastExtensionEndDate;
    }

    public void setLastExtensionEndDate(Date lastExtensionEndDate) {
	this.lastExtensionEndDate = lastExtensionEndDate;
	this.lastExtensionEndDateString = HijriDateService.getHijriDateString(lastExtensionEndDate);
	employee.setLastExtensionEndDate(lastExtensionEndDate);
    }

    @Transient
    @XmlTransient
    public String getLastExtensionEndDateString() {
	return lastExtensionEndDateString;
    }

    public void setLastExtensionEndDateString(String lastExtensionEndDateString) {
	this.lastExtensionEndDateString = lastExtensionEndDateString;
	this.lastExtensionEndDate = HijriDateService.getHijriDate(lastExtensionEndDateString);
	employee.setLastExtensionEndDate(lastExtensionEndDate);
    }

    @Basic
    @Column(name = "EXCEPTIONAL_RECRUITMENT_FLAG")
    @XmlTransient
    public Integer getExceptionalRecruitmentFlag() {
	return exceptionalRecruitmentFlag;
    }

    public void setExceptionalRecruitmentFlag(Integer exceptionalRecruitmentFlag) {
	this.exceptionalRecruitmentFlag = exceptionalRecruitmentFlag;
	if (this.exceptionalRecruitmentFlag == null || this.exceptionalRecruitmentFlag == FlagsEnum.OFF.getCode())
	    this.exceptionalRecruitmentFlagBoolean = false;
	else
	    this.exceptionalRecruitmentFlagBoolean = true;

	employee.setExceptionalRecruitmentFlag(exceptionalRecruitmentFlag);
    }

    @Transient
    @XmlTransient
    public Boolean getExceptionalRecruitmentFlagBoolean() {
	return exceptionalRecruitmentFlagBoolean;
    }

    public void setExceptionalRecruitmentFlagBoolean(Boolean exceptionalRecruitmentFlagBoolean) {
	this.exceptionalRecruitmentFlagBoolean = exceptionalRecruitmentFlagBoolean;

	if (this.exceptionalRecruitmentFlagBoolean == null || !this.exceptionalRecruitmentFlagBoolean) {
	    this.exceptionalRecruitmentFlag = FlagsEnum.OFF.getCode();
	} else {
	    this.exceptionalRecruitmentFlag = FlagsEnum.ON.getCode();
	}

	employee.setExceptionalRecruitmentFlag(exceptionalRecruitmentFlag);
    }

    @Basic
    @Column(name = "SEQUENCE_NUMBER")
    @XmlTransient
    public Long getSequenceNumber() {
	return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
	this.sequenceNumber = sequenceNumber;
	employee.setSequenceNumber(sequenceNumber);
    }

    @Basic
    @Column(name = "USER_ACCOUNT")
    @XmlTransient
    public String getUserAccount() {
	return userAccount;
    }

    public void setUserAccount(String userAccount) {
	this.userAccount = userAccount;
	employee.setUserAccount(userAccount);
    }

    @Basic
    @Column(name = "CATEGORY_CLASSIFICATION_ID")
    @XmlTransient
    public Long getCategoryClassificationId() {
	return categoryClassificationId;
    }

    public void setCategoryClassificationId(Long categoryClassificationId) {
	this.categoryClassificationId = categoryClassificationId;
	employee.setCategoryClassificationId(categoryClassificationId);
    }

    @Basic
    @Column(name = "NAVY_FORMATION")
    @XmlTransient
    public String getNavyFormation() {
	return navyFormation;
    }

    public void setNavyFormation(String navyFormation) {
	this.navyFormation = navyFormation;
	employee.setNavyFormation(navyFormation);
    }

    @Basic
    @Column(name = "SOCIAL_ID_COPY")
    @XmlTransient
    public String getSocialIdCopy() {
	return socialIdCopy;
    }

    public void setSocialIdCopy(String socialIdCopy) {
	this.socialIdCopy = socialIdCopy;
	employee.setSocialIdCopy(socialIdCopy);
    }

    @Basic
    @Column(name = "LAST_ANNUAL_RAISE_DATE")
    @XmlTransient
    public Date getLastAnnualRaiseDate() {
	return lastAnnualRaiseDate;
    }

    public void setLastAnnualRaiseDate(Date lastAnnualRaiseDate) {
	this.lastAnnualRaiseDate = lastAnnualRaiseDate;
    }

    @Transient
    @XmlTransient
    public String getLastAnnualRaiseDateString() {
	return lastAnnualRaiseDateString;
    }

    public void setLastAnnualRaiseDateString(String lastAnnualRaiseDateString) {
	this.lastAnnualRaiseDateString = lastAnnualRaiseDateString;
	this.lastAnnualRaiseDate = HijriDateService.getHijriDate(lastAnnualRaiseDateString);
	employee.setLastAnnualRaiseDate(lastAnnualRaiseDate);
    }

    @Basic
    @Column(name = "OCCUPATION_DESC")
    @XmlTransient
    public String getOccupationDescription() {
	return occupationDescription;
    }

    public void setOccupationDescription(String occupationDescription) {
	this.occupationDescription = occupationDescription;
    }

    @Transient
    @XmlTransient
    public Employee getEmployee() {
	return employee;
    }

    public void setEmployee(Employee employee) {
	this.employee = employee;
    }

}