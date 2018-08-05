package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_graduationPlaceDetailData_searchGraduationPlaceDetailData",
		query = " from GraduationPlaceDetailData g where " +
			" (:P_ID = -1 or g.id = :P_ID) " +
			" and (:P_DESC = '-1' or g.description like :P_DESC) " +
			" and (:P_GRADUATION_PLACE_ID = -1 or g.graduationPlaceId = :P_GRADUATION_PLACE_ID) " +
			" and (:P_GRADUATION_PLACE_DESC = '-1' or g.graduationPlaceDesc like :P_GRADUATION_PLACE_DESC) " +
			" and (:P_GRADUATION_PLACE_TYPE = -1 or g.graduationPlaceType = :P_GRADUATION_PLACE_TYPE) " +
			" and (:P_GRADUATION_PLACE_COUNTRY_NAME = '-1' or g.graduationPlaceCountryName like :P_GRADUATION_PLACE_COUNTRY_NAME) " +
			" and (:P_EXCLUDED_ID = -1 or g.id <> :P_EXCLUDED_ID)" +
			" order by g.id ")
})

@Entity
@Table(name = "HCM_VW_GRADUATION_PLACES_DTLS")
public class GraduationPlaceDetailData extends BaseEntity {

    private Long id;
    private String description;
    private String address;
    private Long graduationPlaceId;
    private String graduationPlaceDesc;
    private Integer graduationPlaceType;
    private Long graduationPlaceCountryId;
    private String graduationPlaceCountryName;

    private GraduationPlaceDetail graduationPlaceDetail;

    public GraduationPlaceDetailData() {
	graduationPlaceDetail = new GraduationPlaceDetail();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	graduationPlaceDetail.setId(id);
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
	graduationPlaceDetail.setDescription(description);
    }

    @Basic
    @Column(name = "ADDRESS")
    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
	graduationPlaceDetail.setAddress(address);
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_ID")
    public Long getGraduationPlaceId() {
	return graduationPlaceId;
    }

    public void setGraduationPlaceId(Long graduationPlaceId) {
	this.graduationPlaceId = graduationPlaceId;
	graduationPlaceDetail.setGraduationPlaceId(graduationPlaceId);
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_DESC")
    public String getGraduationPlaceDesc() {
	return graduationPlaceDesc;
    }

    public void setGraduationPlaceDesc(String graduationPlaceDesc) {
	this.graduationPlaceDesc = graduationPlaceDesc;
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_TYPE")
    public Integer getGraduationPlaceType() {
	return graduationPlaceType;
    }

    public void setGraduationPlaceType(Integer graduationPlaceType) {
	this.graduationPlaceType = graduationPlaceType;
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_COUNTRY_ID")
    public Long getGraduationPlaceCountryId() {
	return graduationPlaceCountryId;
    }

    public void setGraduationPlaceCountryId(Long graduationPlaceCountryId) {
	this.graduationPlaceCountryId = graduationPlaceCountryId;
    }

    @Basic
    @Column(name = "GRADUATION_PLACE_COUNTRY_NAME")
    public String getGraduationPlaceCountryName() {
	return graduationPlaceCountryName;
    }

    public void setGraduationPlaceCountryName(String graduationPlaceCountryName) {
	this.graduationPlaceCountryName = graduationPlaceCountryName;
    }

    @Transient
    public GraduationPlaceDetail getGraduationPlaceDetail() {
	return graduationPlaceDetail;
    }

    public void setGraduationPlaceDetail(GraduationPlaceDetail graduationPlaceDetail) {
	this.graduationPlaceDetail = graduationPlaceDetail;
    }
}
