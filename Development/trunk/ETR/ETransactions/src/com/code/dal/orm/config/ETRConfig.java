package com.code.dal.orm.config;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@NamedQueries({
	@NamedQuery(name = "config_ETRConfig_getConfigurations",
		query = " select c from ETRConfig c" +
			" where " +
			" 	(:P_CODE = '-1' or c.code = :P_CODE)" +
			" and (:P_TRANSACTION_CLASS = -1 or c.transactionClass = :P_TRANSACTION_CLASS)" +
			" order by c.code")

})
@Entity
@Table(name = "ETR_CONFIG")
public class ETRConfig extends AuditEntity implements UpdatableAuditEntity {
    private Long id;
    private String code;
    private String value;
    private String remarks;
    private Integer transactionClass;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long Id) {
	this.id = Id;
    }

    @Basic
    @Column(name = "CODE")
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    @Basic
    @Column(name = "VALUE")
    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    @Basic
    @Column(name = "REMARKS")
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    @Basic
    @Column(name = "TRANSACTION_CLASS")
    public Integer getTransactionClass() {
	return transactionClass;
    }

    public void setTransactionClass(Integer transactionClass) {
	this.transactionClass = transactionClass;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "code:" + code + AUDIT_SEPARATOR +
		"value:" + value + AUDIT_SEPARATOR;
    }

}