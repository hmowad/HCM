package com.code.dal.orm.hcm.navyformations;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@NamedQuery(name = "hcm_navyFormation_searchNavyFormations",
	query = "select n from NavyFormation n "
		+ " where (:P_DESCRIPTION = '-1' or n.description like :P_DESCRIPTION) "
		+ " and (:P_EXECLUDED_NAVY_FORMATION_ID = -1 or n.id != :P_EXECLUDED_NAVY_FORMATION_ID) "
		+ " and (:P_TYPE = -1 or (:P_TYPE = 0 and ((:P_REGION_ID = -1 and n.regionId is not null) or (:P_REGION_ID != -1 and n.regionId = :P_REGION_ID))) or (:P_TYPE = 1 and n.regionId is null)) "
		+ " order by n.description ")

@Entity
@Table(name = "HCM_NAVY_FORMATIONS")
public class NavyFormation extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private String description;
    private Long regionId;

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

    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Column(name = "REGION_ID")
    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "id:" + id + AUDIT_SEPARATOR +
		"description:" + description + AUDIT_SEPARATOR +
		"regionId:" + regionId + AUDIT_SEPARATOR;
    }
}
