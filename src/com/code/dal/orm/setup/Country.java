package com.code.dal.orm.setup;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.code.dal.orm.BaseEntity;

/**
 * The <code>Country</code> class represents the data of the countries.
 * 
 */
@NamedQueries({
	@NamedQuery(name = "hcm_country_getCountryByCountryDesc",
		query = " select c from Country c" +
			" where (:P_COUNTRY_DESC = '-1' or c.name like :P_COUNTRY_DESC )" +
			" and (:P_COUNTRY_CODE = '-1' or c.code = :P_COUNTRY_CODE)" +
			" and (:P_BLACK_LIST = -1 or c.blackListFlag = :P_BLACK_LIST)" +
			" and (:P_ID = -1 or c.id = :P_ID)" +
			" and (:P_COUNTRY_FLAG = -1 or c.countryFlag = :P_COUNTRY_FLAG)" +
			" and (:P_EMBASSY_FLAG = -1 or c.embassyFlag = :P_EMBASSY_FLAG)" +
			" order by c.name "),

	@NamedQuery(name = "hcm_country_getCountryByYaqeenName",
		query = " select c from Country c" +
			" where (c.yaqeenName IN (:P_YAQEEN_NAME))" +
			" order by c.id ")
})
@SuppressWarnings("serial")
@Entity
@Table(name = "HCM_COUNTRIES")
public class Country extends BaseEntity implements Serializable {
    private Long id;
    private String code;
    private String name;
    private String latinName;
    private String embassyName;
    private String embassyLatinName;
    private String nationality;
    private String latinNationality;
    private Integer countryFlag;
    private Integer embassyFlag;
    private Integer blackListFlag;
    private String yaqeenName;

    @SequenceGenerator(name = "HCMSetupSeq",
	    sequenceName = "HCM_SETUP_SEQ")
    @Id
    @GeneratedValue(generator = "HCMSetupSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "CODE")
    @XmlTransient
    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Basic
    @Column(name = "L_NAME")
    @XmlTransient
    public String getLatinName() {
	return latinName;
    }

    public void setLatinName(String latinName) {
	this.latinName = latinName;
    }

    @Basic
    @Column(name = "EMBASSY_NAME")
    public String getEmbassyName() {
	return embassyName;
    }

    public void setEmbassyName(String embassyName) {
	this.embassyName = embassyName;
    }

    @Basic
    @Column(name = "EMBASSY_L_NAME")
    @XmlTransient
    public String getEmbassyLatinName() {
	return embassyLatinName;
    }

    public void setEmbassyLatinName(String embassyLatinName) {
	this.embassyLatinName = embassyLatinName;
    }

    @Basic
    @Column(name = "NATIONALITY")
    @XmlTransient
    public String getNationality() {
	return nationality;
    }

    public void setNationality(String nationality) {
	this.nationality = nationality;
    }

    @Basic
    @Column(name = "L_NATIONALITY")
    @XmlTransient
    public String getLatinNationality() {
	return latinNationality;
    }

    public void setLatinNationality(String latinNationality) {
	this.latinNationality = latinNationality;
    }

    @Basic
    @Column(name = "COUNTRY_FLAG")
    @XmlTransient
    public Integer getCountryFlag() {
	return countryFlag;
    }

    public void setCountryFlag(Integer countryFlag) {
	this.countryFlag = countryFlag;
    }

    @Basic
    @Column(name = "EMBASSY_FLAG")
    @XmlTransient
    public Integer getEmbassyFlag() {
	return embassyFlag;
    }

    public void setEmbassyFlag(Integer embassyFlag) {
	this.embassyFlag = embassyFlag;
    }

    @Basic
    @Column(name = "BLACK_LIST_FLAG")
    @XmlTransient
    public Integer getBlackListFlag() {
	return blackListFlag;
    }

    public void setBlackListFlag(Integer blackListFlag) {
	this.blackListFlag = blackListFlag;
    }

    @Basic
    @Column(name = "YAQEEN_NAME")
    @XmlTransient
    public String getYaqeenName() {
	return yaqeenName;
    }

    public void setYaqeenName(String yaqeenName) {
	this.yaqeenName = yaqeenName;
    }

}
