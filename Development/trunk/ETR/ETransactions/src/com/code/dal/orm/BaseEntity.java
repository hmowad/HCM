package com.code.dal.orm;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

public abstract class BaseEntity implements Serializable {
    private boolean selected;

    @Transient
    @XmlTransient
    public boolean getSelected() {
	return selected;
    }

    public void setSelected(boolean selected) {
	this.selected = selected;
    }
}