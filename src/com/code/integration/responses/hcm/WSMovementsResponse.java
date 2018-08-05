package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.integration.responses.WSResponseBase;

public class WSMovementsResponse extends WSResponseBase {

    private List<MovementTransactionData> movements;

    @XmlElementWrapper(name = "movements")
    @XmlElement(name = "movement")
    public List<MovementTransactionData> getMovements() {
	return movements;
    }

    public void setMovements(List<MovementTransactionData> movements) {
	this.movements = movements;
    }
}
