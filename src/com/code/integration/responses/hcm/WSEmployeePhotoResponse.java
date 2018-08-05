package com.code.integration.responses.hcm;

import javax.xml.bind.annotation.XmlElement;

import com.code.integration.responses.WSResponseBase;

public class WSEmployeePhotoResponse extends WSResponseBase {

    private byte[] photo;

    public byte[] getPhoto() {
	return photo;
    }

    @XmlElement(nillable = true)
    public void setPhoto(byte[] photo) {
	this.photo = photo;
    }

}
