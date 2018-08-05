package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.organization.Region;
import com.code.integration.responses.WSResponseBase;

public class WSRegionsResponse extends WSResponseBase {

    private List<Region> regions;

    @XmlElementWrapper(name = "regions")
    @XmlElement(name = "region")
    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }
}
