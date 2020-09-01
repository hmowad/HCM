package com.code.dal.orm.hcm.employees;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;

import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_employeePhoto_getEmployeePhotoByEmployeeId",
		query = " from EmployeePhoto p where " +
			" p.id = :P_EMP_ID ")
})

@Entity
@Table(name = "HCM_PRS_EMPLOYEES_PHOTOS")
public class EmployeePhoto extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity {

    private Long id;
    private byte[] photo;
    private Date createdDate;
    private Date updatedDate;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "PHOTO")
    @Lob
    @XmlElement(nillable = true)
    public byte[] getPhoto() {
	return photo;
    }

    public void setPhoto(byte[] photo) {
	this.photo = photo;
    }

    @Basic
    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
	this.createdDate = createdDate;
    }

    @Basic
    @Column(name = "UPDATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedDate() {
	return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
	this.updatedDate = updatedDate;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "id:" + id + AUDIT_SEPARATOR;
    }
}
