package com.code.dal.orm.hcm;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@SuppressWarnings("serial")
@NamedQueries({
    @NamedQuery(name = "hcm_transactionType_getAllTransactionTypes",
	    	query = " from TransactionType t" +
	    		" order by t.id")
})
@Entity
@Table(name = "HCM_TRANSACTIONS_TYPES")
public class TransactionType extends BaseEntity implements Serializable {
    private Long id;
    private Integer code;
    private String description;
    private Integer transactionClass;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "CODE")
    public Integer getCode() {
	return code;
    }

    public void setCode(Integer code) {
	this.code = code;
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Basic
    @Column(name = "TRANSACTION_CLASS")
    public Integer getTransactionClass() {
	return transactionClass;
    }

    public void setTransactionClass(Integer transactionClass) {
	this.transactionClass = transactionClass;
    }
}
