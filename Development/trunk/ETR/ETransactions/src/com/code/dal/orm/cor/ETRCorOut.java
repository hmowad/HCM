package com.code.dal.orm.cor;

import com.code.dal.orm.BaseEntity;

import com.code.services.util.HijriDateService;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="ETR_COR_OUT")
@IdClass(ETRCorOutId.class)
public class ETRCorOut extends BaseEntity{
    private Integer hijriYear;
    private Integer seq;
    private String subject;
    private Date eventDate;
    private String evetDateString;

    public void setHijriYear(Integer hijriYear) {
        this.hijriYear = hijriYear;
    }

    @Id
    @Column(name="HIJRI_YEAR")
    public Integer getHijriYear() {
        return hijriYear;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    @Id
    @Column(name="SEQ")
    public Integer getSeq() {
        return seq;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Basic
    @Column(name="SUBJECT")
    public String getSubject() {
        return subject;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
        this.evetDateString = HijriDateService.getHijriDateString(eventDate);
    }

    @Basic
    @Column(name="EVENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEventDate() {
        return eventDate;
    }

    public void setEvetDateString(String evetDateString) {
        this.evetDateString = evetDateString;
        this.eventDate = HijriDateService.getHijriDate(evetDateString);
    }

    @Transient
    public String getEvetDateString() {
        return evetDateString;
    }
}