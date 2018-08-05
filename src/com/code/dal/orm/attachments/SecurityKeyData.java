package com.code.dal.orm.attachments;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.dal.orm.BaseEntity;

@Entity
@Table(name = "BG_VW_SEC_WEB_SHOW")
public class SecurityKeyData extends BaseEntity implements Serializable {
    
    private String securityKey;
    
    @Id
    @Column(name="SEC_KEY")
    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

}