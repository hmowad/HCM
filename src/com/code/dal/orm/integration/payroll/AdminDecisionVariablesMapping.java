package com.code.dal.orm.integration.payroll;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({
	@NamedQuery(name = "hcm_adminDecisionVariablesMapping_getAdminDecisionVariablesMappingByVariableNameAndHCMValue",
		query = " select a from AdminDecisionVariablesMapping a" +
			" where (:P_VARIABLE_NAME = '-1' OR a.variableName = :P_VARIABLE_NAME) " +
			"AND (:P_HCM_VALUE = '-1' OR a.hcmValue = :P_HCM_VALUE)")
})
@Entity
@Table(name = "HCM_ADMIN_DEC_VARS_MAPPINGS")
public class AdminDecisionVariablesMapping {

    private Long id;
    private String variableName;
    private String hcmValue;
    private String prlValue;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "VARIABLE_NAME")
    public String getVariableName() {
	return variableName;
    }

    public void setVariableName(String variableName) {
	this.variableName = variableName;
    }

    @Basic
    @Column(name = "HCM_VALUE")
    public String getHcmValue() {
	return hcmValue;
    }

    public void setHcmValue(String hcmValue) {
	this.hcmValue = hcmValue;
    }

    @Basic
    @Column(name = "PRL_VALUE")
    public String getPrlValue() {
	return prlValue;
    }

    public void setPrlValue(String prlValue) {
	this.prlValue = prlValue;
    }

}
