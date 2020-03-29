package com.code.dal.orm.hcm.incentives;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_incentivePort_getIncentivePorts",
		query = " select ip" +
			" from IncentivePort ip" +
			" order by ip.id ")
})
@Entity
@Table(name = "HCM_VW_INC_PORTS")
public class IncentivePort extends BaseEntity {
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
