package com.code.dal.orm.hcm.specializations;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>MinorSpecializationDescriptionDetailData</code> class represents the descriptions cards duties for the jobs minor specializations in detailed format.
 * 
 */
@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "hcm_minorSpecializationDescriptionDetailData_getMinorSpecializationDescriptionDetailsData",
		query = " select m from MinorSpecializationDescriptionDetailData m " +
			" where m.minorSpecializationDescriptionId = :P_MINOR_SPECIALIZATION_DESCRIPTION_ID " +
			" order by m.id "
	)
})
@Entity
@Table(name = "HCM_VW_MINOR_SPECS_DESCS_DTLS")
public class MinorSpecializationDescriptionDetailData extends BaseEntity {

    private Long id;
    private Long minorSpecializationDescriptionId;
    private String dutyDescription;

    private MinorSpecializationDescriptionDetail minorSpecializationDescriptionDetail;

    public MinorSpecializationDescriptionDetailData() {
	minorSpecializationDescriptionDetail = new MinorSpecializationDescriptionDetail();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.minorSpecializationDescriptionDetail.setId(id);
    }

    @Basic
    @Column(name = "MINOR_SPEC_DESC_ID")
    public Long getMinorSpecializationDescriptionId() {
	return minorSpecializationDescriptionId;
    }

    public void setMinorSpecializationDescriptionId(Long minorSpecializationDescriptionId) {
	this.minorSpecializationDescriptionId = minorSpecializationDescriptionId;
	this.minorSpecializationDescriptionDetail.setMinorSpecializationDescriptionId(minorSpecializationDescriptionId);
    }

    @Basic
    @Column(name = "DUTY_DESCRIPTION")
    public String getDutyDescription() {
	return dutyDescription;
    }

    public void setDutyDescription(String dutyDescription) {
	this.dutyDescription = dutyDescription;
	this.minorSpecializationDescriptionDetail.setDutyDescription(dutyDescription);
    }

    @Transient
    public MinorSpecializationDescriptionDetail getMinorSpecializationDescriptionDetail() {
	return minorSpecializationDescriptionDetail;
    }

    public void setMinorSpecializationDescriptionDetail(MinorSpecializationDescriptionDetail minorSpecializationDescriptionDetail) {
	this.minorSpecializationDescriptionDetail = minorSpecializationDescriptionDetail;
    }
}
