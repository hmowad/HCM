
package com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import com.code.integration.webservicesclients.yaqeenclient.com.elm.yakeen.yakeen4borderguard.Gender;
import com.code.integration.webservicesclients.yaqeenclient.com.elm.yakeen.yakeen4borderguard.LifeStatus;

/**
 * <p>
 * Java class for perosnInfoResult complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="perosnInfoResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dateOfBirthG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="englishName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="arabicName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gender" type="{http://yakeen4borderguard.yakeen.elm.com/}gender" minOccurs="0"/>
 *         &lt;element name="idExpiryDateH" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idIssueDateH" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idIssuePlace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idVersionNumber" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="lifeStatus" type="{http://yakeen4borderguard.yakeen.elm.com/}lifeStatus" minOccurs="0"/>
 *         &lt;element name="logId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nationalityDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="occupationDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passportExpiryDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sponsorMoiNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sponsorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="visaNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "perosnInfoResult",
	propOrder = {
		"dateOfBirthG",
		"englishName",
		"arabicName",
		"gender",
		"idExpiryDateH",
		"idIssueDateH",
		"idIssuePlace",
		"idVersionNumber",
		"lifeStatus",
		"logId",
		"nationalityDesc",
		"occupationDesc",
		"passportExpiryDate",
		"sponsorMoiNumber",
		"sponsorName",
		"visaNumber",
		"passportNumber"
	})
@XmlSeeAlso({
	PersonInfoResultWithDetailedSecurityStatus.class,
	PersonInfoResultWithSecurityStatus.class
})
public class PerosnInfoResult {

    protected String dateOfBirthG;
    protected String englishName;
    protected String arabicName;
    @XmlSchemaType(name = "string")
    protected Gender gender;
    protected String idExpiryDateH;
    protected String idIssueDateH;
    @XmlElementRef(name = "idIssuePlace",
	    type = JAXBElement.class,
	    required = false)
    protected JAXBElement<String> idIssuePlace;
    @XmlElement(required = true,
	    type = Short.class,
	    nillable = true)
    protected Short idVersionNumber;
    @XmlSchemaType(name = "string")
    protected LifeStatus lifeStatus;
    protected int logId;
    protected String nationalityDesc;
    protected String occupationDesc;
    @XmlElementRef(name = "passportExpiryDate",
	    type = JAXBElement.class,
	    required = false)
    protected JAXBElement<String> passportExpiryDate;
    protected String sponsorMoiNumber;
    protected String sponsorName;
    @XmlElementRef(name = "visaNumber",
	    type = JAXBElement.class,
	    required = false)
    protected JAXBElement<String> visaNumber;
    @XmlElementRef(name = "passportNumber",
	    type = JAXBElement.class,
	    required = false)
    protected JAXBElement<String> passportNumber;

    /**
     * Gets the value of the dateOfBirthG property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDateOfBirthG() {
	return dateOfBirthG;
    }

    /**
     * Sets the value of the dateOfBirthG property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setDateOfBirthG(String value) {
	this.dateOfBirthG = value;
    }

    /**
     * Gets the value of the englishName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getEnglishName() {
	return englishName;
    }

    /**
     * Sets the value of the englishName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setEnglishName(String value) {
	this.englishName = value;
    }

    /**
     * Gets the value of the arabicName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getArabicName() {
	return arabicName;
    }

    /**
     * Sets the value of the arabicName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setArabicName(String value) {
	this.arabicName = value;
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
     * Gets the value of the idExpiryDateH property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getIdExpiryDateH() {
	return idExpiryDateH;
    }

    /**
     * Sets the value of the idExpiryDateH property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setIdExpiryDateH(String value) {
	this.idExpiryDateH = value;
    }

    /**
     * Gets the value of the idIssueDateH property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getIdIssueDateH() {
	return idIssueDateH;
    }

    /**
     * Sets the value of the idIssueDateH property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setIdIssueDateH(String value) {
	this.idIssueDateH = value;
    }

    /**
     * Gets the value of the idIssuePlace property.
     * 
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     */
    public JAXBElement<String> getIdIssuePlace() {
	return idIssuePlace;
    }

    /**
     * Sets the value of the idIssuePlace property.
     * 
     * @param value
     *            allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     */
    public void setIdIssuePlace(JAXBElement<String> value) {
	this.idIssuePlace = value;
    }

    /**
     * Gets the value of the idVersionNumber property.
     * 
     * @return possible object is {@link Short }
     * 
     */
    public Short getIdVersionNumber() {
	return idVersionNumber;
    }

    /**
     * Sets the value of the idVersionNumber property.
     * 
     * @param value
     *            allowed object is {@link Short }
     * 
     */
    public void setIdVersionNumber(Short value) {
	this.idVersionNumber = value;
    }

    /**
     * Gets the value of the lifeStatus property.
     * 
     * @return possible object is {@link LifeStatus }
     * 
     */
    public LifeStatus getLifeStatus() {
	return lifeStatus;
    }

    /**
     * Sets the value of the lifeStatus property.
     * 
     * @param value
     *            allowed object is {@link LifeStatus }
     * 
     */
    public void setLifeStatus(LifeStatus value) {
	this.lifeStatus = value;
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
     * Gets the value of the nationalityDesc property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getNationalityDesc() {
	return nationalityDesc;
    }

    /**
     * Sets the value of the nationalityDesc property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setNationalityDesc(String value) {
	this.nationalityDesc = value;
    }

    /**
     * Gets the value of the occupationDesc property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getOccupationDesc() {
	return occupationDesc;
    }

    /**
     * Sets the value of the occupationDesc property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setOccupationDesc(String value) {
	this.occupationDesc = value;
    }

    /**
     * Gets the value of the passportExpiryDate property.
     * 
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     */
    public JAXBElement<String> getPassportExpiryDate() {
	return passportExpiryDate;
    }

    /**
     * Sets the value of the passportExpiryDate property.
     * 
     * @param value
     *            allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     */
    public void setPassportExpiryDate(JAXBElement<String> value) {
	this.passportExpiryDate = value;
    }

    /**
     * Gets the value of the sponsorMoiNumber property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getSponsorMoiNumber() {
	return sponsorMoiNumber;
    }

    /**
     * Sets the value of the sponsorMoiNumber property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setSponsorMoiNumber(String value) {
	this.sponsorMoiNumber = value;
    }

    /**
     * Gets the value of the sponsorName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getSponsorName() {
	return sponsorName;
    }

    /**
     * Sets the value of the sponsorName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setSponsorName(String value) {
	this.sponsorName = value;
    }

    /**
     * Gets the value of the visaNumber property.
     * 
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     */
    public JAXBElement<String> getVisaNumber() {
	return visaNumber;
    }

    /**
     * Sets the value of the visaNumber property.
     * 
     * @param value
     *            allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     */
    public void setVisaNumber(JAXBElement<String> value) {
	this.visaNumber = value;
    }

    /**
     * Gets the value of the passportNumber property.
     * 
     * @return possible object is {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     */
    public JAXBElement<String> getPassportNumber() {
	return passportNumber;
    }

    /**
     * Sets the value of the passportNumber property.
     * 
     * @param value
     *            allowed object is {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     */
    public void setPassportNumber(JAXBElement<String> value) {
	this.passportNumber = value;
    }

}
