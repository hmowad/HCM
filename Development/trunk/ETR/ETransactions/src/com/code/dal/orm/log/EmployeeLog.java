package com.code.dal.orm.log;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;
import com.code.services.util.HijriDateService;

@Entity
@Table(name = "HCM_EMPLOYEES_LOG")
public class EmployeeLog extends BaseEntity {
    private Long id;
    private Long empId;
    private Integer onDutyFlag;
    private Long jobId;
    private Long basicJobNameId;
    private Long physicalUnitId;
    private Long rankId;
    private Long rankTitleId;
    private Long salaryRankId;
    private Long salaryDegreeId;
    private Long degreeId;
    private Integer socialStatus;
    private Integer generalSpecialization;
    private Date effectiveGregDate;
    private Date effectiveHijriDate;
    private String decisionNumber;
    private Date decisionDate;
    private Long insertionTime;
    private Long officialUnitId;
    private Long medStaffRankId;
    private Long medStaffLevelId;
    private Long medStaffDegreeId;
    private String transactionTableName;

    public EmployeeLog() {
    }

    public EmployeeLog(Long empId, Integer onDutyFlag, Long jobId, Long basicJobNameId, Long physicalUnitId, Long officialUnitId, Long rankId, Long rankTitleId, Long salaryRankId, Long salaryDegreeId, Long degreeId, Integer socialStatus, Integer generalSpecialization, Date effectiveGregDate, Date effectiveHijriDate, String decisionNumber, Date decisionDate, Long insertionTime, Long medStaffRankId, Long medStaffLevelId, Long medStaffDegreeId, String transactionTableName) {
	this.empId = empId;
	this.onDutyFlag = onDutyFlag;
	this.jobId = jobId;
	this.basicJobNameId = basicJobNameId;
	this.physicalUnitId = physicalUnitId;
	this.officialUnitId = officialUnitId;
	this.rankId = rankId;
	this.rankTitleId = rankTitleId;
	this.salaryRankId = salaryRankId;
	this.salaryDegreeId = salaryDegreeId;
	this.degreeId = degreeId;
	this.socialStatus = socialStatus;
	this.generalSpecialization = generalSpecialization;
	this.effectiveGregDate = effectiveGregDate;
	this.effectiveHijriDate = effectiveHijriDate;
	this.decisionNumber = decisionNumber;
	this.decisionDate = decisionDate;
	this.insertionTime = insertionTime;
	this.medStaffRankId = medStaffRankId;
	this.medStaffLevelId = medStaffLevelId;
	this.medStaffDegreeId = medStaffDegreeId;
	this.transactionTableName = transactionTableName;
    }

    @SequenceGenerator(name = "HCMLogSeq",
	    sequenceName = "HCM_LOG_SEQ",
	    allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
	    generator = "HCMLogSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Basic
    @Column(name = "ON_DUTY_FLAG")
    public Integer getOnDutyFlag() {
	return onDutyFlag;
    }

    public void setOnDutyFlag(Integer onDutyFlag) {
	this.onDutyFlag = onDutyFlag;
    }

    @Basic
    @Column(name = "JOB_ID")
    public Long getJobId() {
	return jobId;
    }

    public void setJobId(Long jobId) {
	this.jobId = jobId;
    }

    @Basic
    @Column(name = "BASIC_JOB_NAME_ID")
    public Long getBasicJobNameId() {
	return basicJobNameId;
    }

    public void setBasicJobNameId(Long basicJobNameId) {
	this.basicJobNameId = basicJobNameId;
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_ID")
    public Long getPhysicalUnitId() {
	return physicalUnitId;
    }

    public void setPhysicalUnitId(Long physicalUnitId) {
	this.physicalUnitId = physicalUnitId;
    }

    @Basic
    @Column(name = "RANK_ID")
    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    @Basic
    @Column(name = "RANK_TITLE_ID")
    public Long getRankTitleId() {
	return rankTitleId;
    }

    public void setRankTitleId(Long rankTitleId) {
	this.rankTitleId = rankTitleId;
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

    @Basic
    @Column(name = "DEGREE_ID")
    public Long getDegreeId() {
	return degreeId;
    }

    public void setDegreeId(Long degreeId) {
	this.degreeId = degreeId;
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
    @Column(name = "GENERAL_SPECIALIZATION")
    public Integer getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(Integer generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
    }

    @Basic
    @Column(name = "EFFECTIVE_GREG_DATE")
    public Date getEffectiveGregDate() {
	return effectiveGregDate;
    }

    public void setEffectiveGregDate(Date effectiveGregDate) {
	this.effectiveGregDate = effectiveGregDate;
    }

    @Basic
    @Column(name = "EFFECTIVE_HIJRI_DATE")
    public Date getEffectiveHijriDate() {
	return effectiveHijriDate;
    }

    public void setEffectiveHijriDate(Date effectiveHijriDate) {
	this.effectiveHijriDate = effectiveHijriDate;
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
    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    @Basic
    @Column(name = "INSERTION_TIME")
    public Long getInsertionTime() {
	return insertionTime;
    }

    public void setInsertionTime(Long insertionTime) {
	this.insertionTime = insertionTime;
    }

    @Basic
    @Column(name = "OFFICIAL_UNIT_ID")
    public Long getOfficialUnitId() {
	return officialUnitId;
    }

    public void setOfficialUnitId(Long officialUnitId) {
	this.officialUnitId = officialUnitId;
    }

    @Basic
    @Column(name = "MED_STAFF_RANK_ID")
    public Long getMedStaffRankId() {
	return medStaffRankId;
    }

    public void setMedStaffRankId(Long medStaffRankId) {
	this.medStaffRankId = medStaffRankId;
    }

    @Basic
    @Column(name = "MED_STAFF_LEVEL_ID")
    public Long getMedStaffLevelId() {
	return medStaffLevelId;
    }

    public void setMedStaffLevelId(Long medStaffLevelId) {
	this.medStaffLevelId = medStaffLevelId;
    }

    @Basic
    @Column(name = "MED_STAFF_DEGREE_ID")
    public Long getMedStaffDegreeId() {
	return medStaffDegreeId;
    }

    public void setMedStaffDegreeId(Long medStaffDegreeId) {
	this.medStaffDegreeId = medStaffDegreeId;
    }

    @Basic
    @Column(name = "TRANSACTION_TABLE_NAME")
    public String getTransactionTableName() {
	return transactionTableName;
    }

    public void setTransactionTableName(String transactionTableName) {
	this.transactionTableName = transactionTableName;
    }

    public static class Builder {
	private Long empId;
	private Integer onDutyFlag;
	private Long jobId;
	private Long basicJobNameId;
	private Long physicalUnitId;
	private Long rankId;
	private Long rankTitleId;
	private Long salaryRankId;
	private Long salaryDegreeId;
	private Long degreeId;
	private Integer socialStatus;
	private Integer generalSpecialization;
	private Date effectiveGregDate;
	private Date effectiveHijriDate;
	private String decisionNumber;
	private Date decisionDate;
	private Long insertionTime;
	private Long officialUnitId;
	private Long medStaffRankId;
	private Long medStaffLevelId;
	private Long medStaffDegreeId;
	private String transactionTableName;

	public Builder() {
	}

	public void setEmpId(Long empId) {
	    this.empId = empId;
	}

	public void setOnDutyFlag(Integer onDutyFlag) {
	    this.onDutyFlag = onDutyFlag;
	}

	public void setEffectiveGregDate(Date effectiveGregDate) {
	    this.effectiveGregDate = effectiveGregDate;
	}

	public void setEffectiveHijriDate(Date effectiveHijriDate) {
	    this.effectiveHijriDate = effectiveHijriDate;
	}

	public void setDecisionNumber(String decisionNumber) {
	    this.decisionNumber = decisionNumber;
	}

	public void setDecisionDate(Date decisionDate) {
	    this.decisionDate = decisionDate;
	}

	public void setInsertionTime(Long insertionTime) {
	    this.insertionTime = insertionTime;
	}

	public Builder setJobId(Long jobId) {
	    this.jobId = jobId;
	    return this;
	}

	public Builder setBasicJobNameId(Long basicJobNameId) {
	    this.basicJobNameId = basicJobNameId;
	    return this;
	}

	public Builder setPhysicalUnitId(Long physicalUnitId) {
	    this.physicalUnitId = physicalUnitId;
	    return this;
	}

	public Builder setRankId(Long rankId) {
	    this.rankId = rankId;
	    return this;
	}

	public Builder setRankTitleId(Long rankTitleId) {
	    this.rankTitleId = rankTitleId;
	    return this;
	}

	public Builder setSalaryRankId(Long salaryRankId) {
	    this.salaryRankId = salaryRankId;
	    return this;
	}

	public Builder setSalaryDegreeId(Long salaryDegreeId) {
	    this.salaryDegreeId = salaryDegreeId;
	    return this;
	}

	public Builder setDegreeId(Long degreeId) {
	    this.degreeId = degreeId;
	    return this;
	}

	public Builder setSocialStatus(Integer socialStatus) {
	    this.socialStatus = socialStatus;
	    return this;
	}

	public Builder setGeneralSpecialization(Integer generalSpecialization) {
	    this.generalSpecialization = generalSpecialization;
	    return this;
	}

	public Builder setOfficialUnitId(Long officialUnitId) {
	    this.officialUnitId = officialUnitId;
	    return this;
	}

	public Builder setMedStaffRankId(Long medStaffRankId) {
	    this.medStaffRankId = medStaffRankId;
	    return this;
	}

	public Builder setMedStaffLevelId(Long medStaffLevelId) {
	    this.medStaffLevelId = medStaffLevelId;
	    return this;
	}

	public Builder setMedStaffDegreeId(Long medStaffDegreeId) {
	    this.medStaffDegreeId = medStaffDegreeId;
	    return this;
	}

	public void setTransactionTableName(String transactionTableName) {
	    this.transactionTableName = transactionTableName;
	}

	public Builder constructCommonFields(Long empId, Integer onDutyFlag, String decisionNumber, Date decisionDate, Date effectiveHijriDate, String transactionTableName) {
	    this.empId = empId;
	    this.onDutyFlag = onDutyFlag;
	    this.decisionNumber = decisionNumber;
	    this.decisionDate = decisionDate;
	    this.insertionTime = System.currentTimeMillis();
	    this.effectiveHijriDate = effectiveHijriDate;
	    this.effectiveGregDate = HijriDateService.hijriToGregDate(effectiveHijriDate);
	    this.transactionTableName = transactionTableName;
	    return this;
	}

	public EmployeeLog build() {
	    return new EmployeeLog(empId, onDutyFlag, jobId, basicJobNameId, physicalUnitId, officialUnitId, rankId, rankTitleId, salaryRankId, salaryDegreeId, degreeId, socialStatus, generalSpecialization, effectiveGregDate, effectiveHijriDate, decisionNumber, decisionDate, insertionTime, medStaffRankId, medStaffLevelId, medStaffDegreeId, transactionTableName);
	}
    }

}
