package com.code.dal.orm.setup;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({
	@NamedQuery(name = "hcm_adminDecision_getAdminDecisionByName‎",
		query = " select a from AdminDecision a" +
			" where (:P_ADMIN_DECISION_NAME = '-1' OR a.name = :P_ADMIN_DECISION_NAME)")
})
@Entity
@Table(name = "HCM_VW_ADMIN_DECISIONS")
public class AdminDecision {

    private Long id;
    private String name;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

}
