package com.code.dal.orm.cor;

import com.code.dal.orm.BaseEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@NamedQueries({
	  @NamedQuery(name="cor_etr_getETRCor", 
	              query= " select e from ETRCor e " +
                             " where e.hijriYear = :P_HIJRI_YEAR "
          )
})

@Entity
@Table(name="ETR_COR")
public class ETRCor extends BaseEntity{
    private Long id;
    private Integer hijriYear;
    private Integer seq;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @Column(name="ID")
    public Long getId() {
        return id;
    }

    public void setHijriYear(Integer hijriYear) {
        this.hijriYear = hijriYear;
    }

    @Basic
    @Column(name="HIJRI_YEAR")
    public Integer getHijriYear() {
        return hijriYear;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    @Basic
    @Column(name="SEQ")
    public Integer getSeq() {
        return seq;
    }
}