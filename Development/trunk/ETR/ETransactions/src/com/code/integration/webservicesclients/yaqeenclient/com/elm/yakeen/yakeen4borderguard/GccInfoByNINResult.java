
package com.code.integration.webservicesclients.yaqeenclient.com.elm.yakeen.yakeen4borderguard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for gccInfoByNINResult complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gccInfoByNINResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dateOfBirthH" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="familyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fatherName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gender" type="{http://yakeen4borderguard.yakeen.elm.com/}gender" minOccurs="0"/>
 *         &lt;element name="grandFatherName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lifeStatusCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="logId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="passportExpiryDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passportNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gccInfoByNINResult",
	propOrder = {
		"dateOfBirthH",
		"familyName",
		"fatherName",
		"firstName",
		"gender",
		"grandFatherName",
		"lifeStatusCode",
		"logId",
		"passportExpiryDate",
		"passportNumber"
	})
public class GccInfoByNINResult {

    protected String dateOfBirthH;
    protected String familyName;
    protected String fatherName;
    protected String firstName;
    @XmlSchemaType(name = "string")
    protected Gender gender;
    protected String grandFatherName;
    protected int lifeStatusCode;
    protected int logId;
    protected String passportExpiryDate;
    protected String passportNumber;

    /**
     * Gets the value of the dateOfBirthH property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDateOfBirthH() {
	return dateOfBirthH;
    }

    /**
     * Sets the value of the dateOfBirthH property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setDateOfBirthH(String value) {
	this.dateOfBirthH = value;
    }

    /**
     * Gets the value of the familyName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getFamilyName() {
	return familyName;
    }

    /**
     * Sets the value of the familyName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setFamilyName(String value) {
	this.familyName = value;
    }

    /**
     * Gets the value of the fatherName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getFatherName() {
	return fatherName;
    }

    /**
     * Sets the value of the fatherName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setFatherName(String value) {
	this.fatherName = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getFirstName() {
	return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setFirstName(String value) {
	this.firstName = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return possible object is {@link Gender }
     * 
     */
    public Gender getGender() {
	return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *            allowed object is {@link Gender }
     * 
     */
    public void setGender(Gender value) {
	this.gender = value;
    }

    /**
     * Gets the value of the grandFatherName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getGrandFatherName() {
	return grandFatherName;
    }

    /**
     * Sets the value of the grandFatherName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setGrandFatherName(String value) {
	this.grandFatherName = value;
    }

    /**
     * Gets the value of the lifeStatusCode property.
     * 
     */
    public int getLifeStatusCode() {
	return lifeStatusCode;
    }

    /**
     * Sets the value of the lifeStatusCode property.
     * 
     */
    public void setLifeStatusCode(int value) {
	this.lifeStatusCode = value;
    }

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
     * Gets the value of the passportExpiryDate property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getPassportExpiryDate() {
	return passportExpiryDate;
    }

    /**
     * Sets the value of the passportExpiryDate property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setPassportExpiryDate(String value) {
	this.passportExpiryDate = value;
    }

    /**
     * Gets the value of the passportNumber property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getPassportNumber() {
	return passportNumber;
    }

    /**
     * Sets the value of the passportNumber property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setPassportNumber(String value) {
	this.passportNumber = value;
    }

}
