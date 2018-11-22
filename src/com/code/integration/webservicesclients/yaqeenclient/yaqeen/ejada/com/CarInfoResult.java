
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for CarInfoResult complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CarInfoResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="logId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="majorColor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelYear" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="ownerId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="regExpiryHDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regIssueDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regPlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sequenceNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="vehicleMaker" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vehicleModel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="legalStatus" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CarInfoResult",
	propOrder = {
		"logId",
		"majorColor",
		"modelYear",
		"ownerId",
		"regExpiryHDate",
		"regIssueDate",
		"regPlace",
		"sequenceNumber",
		"vehicleMaker",
		"vehicleModel",
		"legalStatus"
	})
public class CarInfoResult {

    protected int logId;
    protected String majorColor;
    protected short modelYear;
    protected long ownerId;
    protected String regExpiryHDate;
    protected String regIssueDate;
    protected String regPlace;
    protected int sequenceNumber;
    protected String vehicleMaker;
    protected String vehicleModel;
    protected boolean legalStatus;

    /**
     * Gets the value of the logId property.
     * 
     */
    public int getLogId() {
	return logId;
    }

    /**
     * Sets the value of the logId property.
     * 
     */
    public void setLogId(int value) {
	this.logId = value;
    }

    /**
     * Gets the value of the majorColor property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getMajorColor() {
	return majorColor;
    }

    /**
     * Sets the value of the majorColor property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setMajorColor(String value) {
	this.majorColor = value;
    }

    /**
     * Gets the value of the modelYear property.
     * 
     */
    public short getModelYear() {
	return modelYear;
    }

    /**
     * Sets the value of the modelYear property.
     * 
     */
    public void setModelYear(short value) {
	this.modelYear = value;
    }

    /**
     * Gets the value of the ownerId property.
     * 
     */
    public long getOwnerId() {
	return ownerId;
    }

    /**
     * Sets the value of the ownerId property.
     * 
     */
    public void setOwnerId(long value) {
	this.ownerId = value;
    }

    /**
     * Gets the value of the regExpiryHDate property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getRegExpiryHDate() {
	return regExpiryHDate;
    }

    /**
     * Sets the value of the regExpiryHDate property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setRegExpiryHDate(String value) {
	this.regExpiryHDate = value;
    }

    /**
     * Gets the value of the regIssueDate property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getRegIssueDate() {
	return regIssueDate;
    }

    /**
     * Sets the value of the regIssueDate property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setRegIssueDate(String value) {
	this.regIssueDate = value;
    }

    /**
     * Gets the value of the regPlace property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getRegPlace() {
	return regPlace;
    }

    /**
     * Sets the value of the regPlace property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setRegPlace(String value) {
	this.regPlace = value;
    }

    /**
     * Gets the value of the sequenceNumber property.
     * 
     */
    public int getSequenceNumber() {
	return sequenceNumber;
    }

    /**
     * Sets the value of the sequenceNumber property.
     * 
     */
    public void setSequenceNumber(int value) {
	this.sequenceNumber = value;
    }

    /**
     * Gets the value of the vehicleMaker property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getVehicleMaker() {
	return vehicleMaker;
    }

    /**
     * Sets the value of the vehicleMaker property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setVehicleMaker(String value) {
	this.vehicleMaker = value;
    }

    /**
     * Gets the value of the vehicleModel property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getVehicleModel() {
	return vehicleModel;
    }

    /**
     * Sets the value of the vehicleModel property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setVehicleModel(String value) {
	this.vehicleModel = value;
    }

    /**
     * Gets the value of the legalStatus property.
     * 
     */
    public boolean isLegalStatus() {
	return legalStatus;
    }

    /**
     * Sets the value of the legalStatus property.
     * 
     */
    public void setLegalStatus(boolean value) {
	this.legalStatus = value;
    }

}
