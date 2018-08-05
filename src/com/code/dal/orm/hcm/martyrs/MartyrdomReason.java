package com.code.dal.orm.hcm.martyrs;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * Provides martyrdom reasons descriptions <br/>
 * 
 */

@NamedQueries({
	@NamedQuery(name = "hcm_martyrdomReason_searchMartyrdomReasons",
		query = " select mr" +
			" from MartyrdomReason mr "
			+ " where (:P_DESCRIPTION = '-1' or mr.description = :P_DESCRIPTION) "
			+ " and (:P_EXECLUDED_REASON_ID = -1 or mr.id != :P_EXECLUDED_REASON_ID) "
			+ " order by mr.description ")
})

@Entity
@Table(name = "HCM_PRS_MARTYRDOM_REASONS")
public class MartyrdomReason extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {

    private Long id;
    private String description;

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
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "id:" + id + AUDIT_SEPARATOR +
		"description:" + description + AUDIT_SEPARATOR;
    }

}
