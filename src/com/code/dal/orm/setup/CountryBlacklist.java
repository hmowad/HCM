package com.code.dal.orm.setup;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.orm.AuditEntity;

/**
 * The <code>Country</code> class represents the data of the countries.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_countryBlacklist_getCountryBlacklistByCountryId",
		query = " select cbl from CountryBlacklist cbl" +
			" where (cbl.countryId = :P_COUNTRY_ID)")
})

@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_COUNTRIES_BLACK_LIST")
public class CountryBlacklist extends AuditEntity implements Serializable, InsertableAuditEntity, DeletableAuditEntity {
    private Long countryId;
    
    @Id
    @Column(name = "COUNTRY_ID")
    public Long getCountryId() {
	return countryId;
    }

    public void setCountryId(Long countryId) {
	this.countryId = countryId;
    }

    @Override
    public Long calculateContentId() {
	return countryId;
    }

    @Override
    public String calculateContent() {
	return "countryId:"+ countryId + AUDIT_SEPARATOR;
    }
    
    
}
