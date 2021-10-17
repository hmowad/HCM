package com.code.dal.orm.hcm.generalNews;

import java.io.Serializable;
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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.code.services.util.HijriDateService;

@NamedQueries({
	@NamedQuery(name = "hcm_generalNews_getGeneralNews",
		query = " select gn from GeneralNews gn " +
			" where gn.publishFlag = 1 " +
			" order by gn.newsDate desc "),

	@NamedQuery(name = "hcm_generalNews_countGeneralNews",
		query = " select count(gn) from GeneralNews gn " +
			" where gn.publishFlag = 1 ")
})

@Entity
@Table(name = "BG_VW_GENERAL_NEWS")
public class GeneralNews implements Serializable {

    private Long id;
    private Date newsDate;
    private String newsDateString;
    private Integer publishFlag;
    private String subject;
    private String shortDesc;
    private String details;
    private byte[] image1;
    private byte[] image2;
    private byte[] image3;
    private byte[] image4;
    private byte[] image5;

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "NEWS_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlTransient
    public Date getNewsDate() {
	return newsDate;
    }

    public void setNewsDate(Date newsDate) {
	this.newsDate = newsDate;
	this.newsDateString = HijriDateService.getHijriDateString(newsDate);
    }

    @Transient
    public String getNewsDateString() {
	return newsDateString;
    }

    public void setNewsDateString(String newsDateString) {
	this.newsDateString = newsDateString;
	this.newsDate = HijriDateService.getHijriDate(newsDateString);
    }

    @Basic
    @Column(name = "PUBLISH")
    @XmlTransient
    public Integer getPublishFlag() {
	return publishFlag;
    }

    public void setPublishFlag(Integer publishFlag) {
	this.publishFlag = publishFlag;
    }

    @Basic
    @Column(name = "SUBJECT")
    public String getSubject() {
	return subject;
    }

    public void setSubject(String subject) {
	this.subject = subject;
    }

    @Transient
    public String getShortDesc() {
	return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
	this.shortDesc = shortDesc;
    }

    @Basic
    @Column(name = "DETAILS")
    public String getDetails() {
	return details;
    }

    public void setDetails(String details) {
	this.details = details;

	if (details.length() < 250)
	    setShortDesc(details);
	else {
	    String shortDesc = details.substring(0, 250);
	    setShortDesc(shortDesc.lastIndexOf(" ") == -1 ? shortDesc : shortDesc.substring(0, shortDesc.lastIndexOf(" ")));
	}
    }

    @Basic
    @Column(name = "IMAGE1")
    @Lob
    public byte[] getImage1() {
	return image1;
    }

    public void setImage1(byte[] image1) {
	this.image1 = image1;
    }

    @Basic
    @Column(name = "IMAGE2")
    @Lob
    @XmlElement(nillable = true)
    public byte[] getImage2() {
	return image2;
    }

    public void setImage2(byte[] image2) {
	this.image2 = image2;
    }

    @Basic
    @Column(name = "IMAGE3")
    @Lob
    @XmlElement(nillable = true)
    public byte[] getImage3() {
	return image3;
    }

    public void setImage3(byte[] image3) {
	this.image3 = image3;
    }

    @Basic
    @Column(name = "IMAGE4")
    @Lob
    @XmlElement(nillable = true)
    public byte[] getImage4() {
	return image4;
    }

    public void setImage4(byte[] image4) {
	this.image4 = image4;
    }

    @Basic
    @Column(name = "IMAGE5")
    @Lob
    @XmlElement(nillable = true)
    public byte[] getImage5() {
	return image5;
    }

    public void setImage5(byte[] image5) {
	this.image5 = image5;
    }

}
