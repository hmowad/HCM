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

@NamedQueries({
    @NamedQuery(name = "hcm_socialIdIssuePlace_searchSocialIdIssuePlace",
	    	query = " from SocialIdIssuePlace s where " +
	    		" (:P_ID = -1 or s.id = :P_ID) " +
                        " and (:P_DESC = '-1' or s.description like :P_DESC) " +
	    		" order by s.id "
    )
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_SOCIAL_ID_ISSUE_PLACES")
public class SocialIdIssuePlace extends BaseEntity implements Serializable {
    private Long id;
    private String description;

    @Id
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
}