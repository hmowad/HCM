package com.code.dal.orm.setup;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({
	@NamedQuery(name = "hcm_adminDecision_getAdminDecisionById",
		query = " select a from AdminDecision a" +
			" where (a.id = :P_ADMIN_DECISION_ID)")
})
@Entity
@Table(name = "HCM_VW_ADMIN_DECISIONS")
public class AdminDecision {

    private Long id;
    private String name;
    private Integer integrationTypeFlag;

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

    @Basic
    @Column(name = "INTEG_TYPE_FLAG")
    public Integer getIntegrationTypeFlag() {
	return integrationTypeFlag;
    }

    public void setIntegrationTypeFlag(Integer integrationTypeFlag) {
	this.integrationTypeFlag = integrationTypeFlag;
    }

}
