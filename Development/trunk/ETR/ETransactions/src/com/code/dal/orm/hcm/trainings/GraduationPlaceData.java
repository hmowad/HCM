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
	@NamedQuery(name = "hcm_graduationPlaceData_searchGraduationPlaceData",
		query = " from GraduationPlaceData g where " +
			" (:P_ID = -1 or g.id = :P_ID)" +
			" and (:P_TYPE = -1 or g.type = :P_TYPE) " +
			" and (:P_DESC = '-1' or g.description like :P_DESC) " +
			" and (:P_COUNTRY_ID = -1 or g.countryId = :P_COUNTRY_ID) " +
			" and (:P_COUNTRY_NAME = '-1' or g.countryName like :P_COUNTRY_NAME) " +
			" and (:P_EXCLUDED_ID = -1 or g.id <> :P_EXCLUDED_ID)" +
			" order by g.id ")
})
@Entity
@Table(name = "HCM_VW_GRADUATION_PLACES")
public class GraduationPlaceData extends BaseEntity {
    private Long id;
    private String description;
    private Integer type;
    private Long countryId;
    private String countryName;

    private GraduationPlace graduationPlace;

    public GraduationPlaceData() {
	graduationPlace = new GraduationPlace();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	graduationPlace.setId(id);
    }

    @Basic
    @Column(name = "DESCRIPTION")
    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
	graduationPlace.setDescription(description);
    }

    @Basic
    @Column(name = "TYPE")
    public Integer getType() {
	return type;
    }

    public void setType(Integer type) {
	this.type = type;
	graduationPlace.setType(type);
    }

    @Basic
    @Column(name = "COUNTRY_ID")
    public Long getCountryId() {
	return countryId;
    }

    public void setCountryId(Long countryId) {
	this.countryId = countryId;
	graduationPlace.setCountryId(countryId);
    }

    @Basic
    @Column(name = "COUNTRY_NAME")
    public String getCountryName() {
	return countryName;
    }

    public void setCountryName(String countryName) {
	this.countryName = countryName;
    }

    @Transient
    public GraduationPlace getGraduationPlace() {
	return graduationPlace;
    }

    public void setGraduationPlace(GraduationPlace graduationPlace) {
	this.graduationPlace = graduationPlace;
    }
}