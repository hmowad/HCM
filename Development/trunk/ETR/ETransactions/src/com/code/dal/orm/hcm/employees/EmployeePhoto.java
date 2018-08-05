package com.code.dal.orm.hcm.employees;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
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

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "id:" + id + AUDIT_SEPARATOR;
    }
}
