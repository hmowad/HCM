package com.code.integration.responses.workflow.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.integration.responses.WSResponseBase;

public class WSWFMovementResponse extends WSResponseBase {

    private Integer verbalOrderFlag;
    private String verbalOrderMessage;
    private String basedOn;
    private String basedOnOrderNumber;
    private String basedOnOrderDateString;
    private String remarks;

    private List<WFMovementData> wfMovementDataList;

    public Integer getVerbalOrderFlag() {
	return verbalOrderFlag;
    }

    public void setVerbalOrderFlag(Integer verbalOrderFlag) {
	this.verbalOrderFlag = verbalOrderFlag;
    }

    public String getVerbalOrderMessage() {
	return verbalOrderMessage;
    }

    public void setVerbalOrderMessage(String verbalOrderMessage) {
	this.verbalOrderMessage = verbalOrderMessage;
    }

    @XmlElement(nillable = true)
    public String getBasedOn() {
	return basedOn;
    }

    public void setBasedOn(String basedOn) {
	this.basedOn = basedOn;
    }

    @XmlElement(nillable = true)
    public String getBasedOnOrderNumber() {
	return basedOnOrderNumber;
    }

    public void setBasedOnOrderNumber(String basedOnOrderNumber) {
	this.basedOnOrderNumber = basedOnOrderNumber;
    }

    @XmlElement(nillable = true)
    public String getBasedOnOrderDateString() {
	return basedOnOrderDateString;
    }

    public void setBasedOnOrderDateString(String basedOnOrderDateString) {
	this.basedOnOrderDateString = basedOnOrderDateString;
    }

    @XmlElement(nillable = true)
    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    @XmlElementWrapper(name = "wfMovements")
    @XmlElement(name = "wfMovement")
    public List<WFMovementData> getWfMovementDataList() {
	return wfMovementDataList;
    }

    public void setWfMovementDataList(List<WFMovementData> wfMovementDataList) {
	this.wfMovementDataList = wfMovementDataList;
    }
}
