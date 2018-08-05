package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.integration.responses.WSResponseBase;

public class WSUnitsResponse extends WSResponseBase {

    private List<UnitData> units;

    @XmlElementWrapper(name = "units")
    @XmlElement(name = "unit")
    public List<UnitData> getUnits() {
	return units;
    }

    public void setUnits(List<UnitData> units) {
	this.units = units;
    }
}
