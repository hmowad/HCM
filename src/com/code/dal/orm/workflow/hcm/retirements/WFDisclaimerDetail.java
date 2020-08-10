package com.code.dal.orm.workflow.hcm.retirements;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({

	@NamedQuery(name = "wf_disclaimerDetail_getWFDisclaimerDetails",
		query = "select dd from WFDisclaimerDetail dd "
			+ " where "
			+ " (:P_INSTANCE_ID = -1 or dd.instanceId = :P_INSTANCE_ID) "
			+ " and (:P_MANAGER_UNIT_ID = -1 or dd.managerUnitId = :P_MANAGER_UNIT_ID) "
			+ " and (:P_MANAGER_ID = -1 or dd.managerId = :P_MANAGER_ID) "
			+ "order by dd.id ")
})

@Entity
@Table(name = "WF_DISCLAIMERS_DETAILS")
public class WFDisclaimerDetail extends BaseEntity {

    private Long id;
    private Long instanceId;
    private Long managerId;
    private Long managerUnitId;
    private String managerUnitFullName;
    private String managerJobDesc;
    private String managerName;
    private String managerRankDesc;
    private Integer claimedFlag;

    @SequenceGenerator(name = "HCMSetupSeq",
	    sequenceName = "HCM_SETUP_SEQ")
    @Id
    @GeneratedValue(generator = "HCMSetupSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
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
    @Column(name = "MANAGER_ID")
    public Long getManagerId() {
	return managerId;
    }

    public void setManagerId(Long managerId) {
	this.managerId = managerId;
    }

    @Basic
    @Column(name = "MANAGER_UNIT_ID")
    public Long getManagerUnitId() {
	return managerUnitId;
    }

    public void setManagerUnitId(Long managerUnitId) {
	this.managerUnitId = managerUnitId;
    }

    @Basic
    @Column(name = "MANAGER_UNIT_FULL_NAME")
    public String getManagerUnitFullName() {
	return managerUnitFullName;
    }

    public void setManagerUnitFullName(String managerUnitFullName) {
	this.managerUnitFullName = managerUnitFullName;
    }

    @Basic
    @Column(name = "MANAGER_JOB_DESC")
    public String getManagerJobDesc() {
	return managerJobDesc;
    }

    public void setManagerJobDesc(String managerJobDesc) {
	this.managerJobDesc = managerJobDesc;
    }

    @Basic
    @Column(name = "MANAGER_NAME")
    public String getManagerName() {
	return managerName;
    }

    public void setManagerName(String managerName) {
	this.managerName = managerName;
    }

    @Basic
    @Column(name = "MANAGER_RANK_DESC")
    public String getManagerRankDesc() {
	return managerRankDesc;
    }

    public void setManagerRankDesc(String managerRankDesc) {
	this.managerRankDesc = managerRankDesc;
    }

    @Basic
    @Column(name = "CLAIMED_FLAG")
    public Integer getClaimedFlag() {
	return claimedFlag;
    }

    public void setClaimedFlag(Integer claimedFlag) {
	this.claimedFlag = claimedFlag;
    }

}
