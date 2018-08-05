package com.code.dal.orm.cor;

import java.io.Serializable;

public class ETRCorOutId implements Serializable{
    private Integer hijriYear;
    private Integer seq;
    
    public ETRCorOutId() {}

    public ETRCorOutId(Integer hijriYear, Integer seq) {        
        this.hijriYear = hijriYear;
        this.seq = seq;
    }

    public void setHijriYear(Integer hijriYear) {
        this.hijriYear = hijriYear;
    }

    public Integer getHijriYear() {
        return hijriYear;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getSeq() {
        return seq;
    }
    
    public boolean equals(Object o) {
        return ( (o instanceof ETRCorOutId) && (hijriYear.equals(((ETRCorOutId)o).getHijriYear())) && (seq.equals(((ETRCorOutId)o).getSeq())) );
    }

    public int hashCode() {
        return hijriYear.hashCode() + seq.hashCode();
    }
}