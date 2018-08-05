package com.code.dal.orm.workflow;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "wf_notificationsConfigDetailEmployeeData_searchWFNotificationsConfigDetailEmployeeDataforWFInstance",
		query = "select de from WFNotificationsConfigDetailEmployeeData de, WFNotificationsConfigData n, WFProcess p " +
			" where de.wfNotificationsConfigId = n.id " +
			" and n.beneficiaryCategoryId in (:P_CATEGORIES_IDS) " +
			" and p.id = :P_PROCESS_ID " +
			" and n.wfProcessGroupId = p.processGroupId " +
			" and (n.wfProcessId is null or n.wfProcessId = :P_PROCESS_ID) " +
			" and (n.decisionRegionId is null or n.decisionRegionId = :P_DECISION_REGION_ID) " +
			" and (n.beneficiaryUnitHKeyPrefix is null " +
			"    or (select count(e.id) from EmployeeData e " +
			"        where e.id in (:P_EMP_IDS) " +
			"          and e.physicalUnitHKey like n.beneficiaryUnitHKeyPrefix || '%' " +
			"          and (n.beneficiaryRegionId is null or e.physicalRegionId = n.beneficiaryRegionId)) > 0) ")
})
@Entity
@Table(name = "ETR_VW_WF_NOTFC_CONFG_DTL_EMPS")
@IdClass(WFNotificationsConfigDetailEmployeeDataId.class)
public class WFNotificationsConfigDetailEmployeeData extends BaseEntity {

    private Long employeeId;
    private Long wfNotificationsConfigDetailId;
    private Long wfNotificationsConfigId;

    @Id
    @Column(name = "EMPLOYEE_ID")
    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    @Id
    @Column(name = "WF_NOTIFICATIONS_CONFIG_DTL_ID")
    public Long getWfNotificationsConfigDetailId() {
	return wfNotificationsConfigDetailId;
    }

    public void setWfNotificationsConfigDetailId(Long wfNotificationsConfigDetailId) {
	this.wfNotificationsConfigDetailId = wfNotificationsConfigDetailId;
    }

    @Basic
    @Column(name = "WF_NOTIFICATIONS_CONFIG_ID")
    public Long getWfNotificationsConfigId() {
	return wfNotificationsConfigId;
    }

    public void setWfNotificationsConfigId(Long wfNotificationsConfigId) {
	this.wfNotificationsConfigId = wfNotificationsConfigId;
    }
}
