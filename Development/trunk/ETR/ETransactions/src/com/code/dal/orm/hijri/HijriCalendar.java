package com.code.dal.orm.hijri;

import com.code.dal.orm.BaseEntity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@NamedQueries({
          @NamedQuery(name="hijri_calendar_getAllHijriCalendars", 
	              query=" select h from HijriCalendar h " +
                            " order by hijriYear, hijriMonth "
          )
})

@Entity
@Table(name="HCM_HEJRI_CALENDAR")
public class HijriCalendar extends BaseEntity{
    private Date hijriMonthEndGregDate;
    private Integer hijriMonth;
    private Integer hijriYear;
    private Integer hijriMonthLength;

    public void setHijriMonthEndGregDate(Date hijriMonthEndGregDate) {
        this.hijriMonthEndGregDate = hijriMonthEndGregDate;
    }

    @Id
    @Column(name="H_MONTH_END_GREG_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getHijriMonthEndGregDate() {
        return hijriMonthEndGregDate;
    }

    public void setHijriMonth(Integer hijriMonth) {
        this.hijriMonth = hijriMonth;
    }

    @Basic
    @Column(name="HEJRI_MONTH")
    public Integer getHijriMonth() {
        return hijriMonth;
    }

    public void setHijriYear(Integer hijriYear) {
        this.hijriYear = hijriYear;
    }

    @Basic
    @Column(name="HEJRI_YEAR")
    public Integer getHijriYear() {
        return hijriYear;
    }

    public void setHijriMonthLength(Integer hijriMonthLength) {
        this.hijriMonthLength = hijriMonthLength;
    }

    @Basic
    @Column(name="HEJRI_MONTH_LENGTH")
    public Integer getHijriMonthLength() {
        return hijriMonthLength;
    }
}