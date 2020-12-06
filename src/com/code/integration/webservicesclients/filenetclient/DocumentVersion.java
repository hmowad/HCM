
package com.code.integration.webservicesclients.filenetclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for documentVersion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documentVersion">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="checkedOut" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="contentSize" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documentTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documentURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="majorVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mimeType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="minorVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modifiedBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modifiedOn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="objectId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reserved" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="verSId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="versionNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documentVersion", propOrder = {
    "checkedOut",
    "contentSize",
    "documentTitle",
    "documentURL",
    "majorVersion",
    "mimeType",
    "minorVersion",
    "modifiedBy",
    "modifiedOn",
    "objectId",
    "reserved",
    "verSId",
    "versionNo"
})
public class DocumentVersion {

    protected boolean checkedOut;
    protected String contentSize;
    protected String documentTitle;
    protected String documentURL;
    protected String majorVersion;
    protected String mimeType;
    protected String minorVersion;
    protected String modifiedBy;
    protected String modifiedOn;
    protected String objectId;
    protected boolean reserved;
    protected String verSId;
    protected String versionNo;

    /**
     * Gets the value of the checkedOut property.
     * 
     */
    public boolean isCheckedOut() {
        return checkedOut;
    }

    /**
     * Sets the value of the checkedOut property.
     * 
     */
    public void setCheckedOut(boolean value) {
        this.checkedOut = value;
    }

    /**
     * Gets the value of the contentSize property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentSize() {
        return contentSize;
    }

    /**
     * Sets the value of the contentSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentSize(String value) {
        this.contentSize = value;
    }

    /**
     * Gets the value of the documentTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentTitle() {
        return documentTitle;
    }

    /**
     * Sets the value of the documentTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentTitle(String value) {
        this.documentTitle = value;
    }

    /**
     * Gets the value of the documentURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentURL() {
        return documentURL;
    }

    /**
     * Sets the value of the documentURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentURL(String value) {
        this.documentURL = value;
    }

    /**
     * Gets the value of the majorVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMajorVersion() {
        return majorVersion;
    }

    /**
     * Sets the value of the majorVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMajorVersion(String value) {
        this.majorVersion = value;
    }

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the minorVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinorVersion() {
        return minorVersion;
    }

    /**
     * Sets the value of the minorVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinorVersion(String value) {
        this.minorVersion = value;
    }

    /**
     * Gets the value of the modifiedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the value of the modifiedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModifiedBy(String value) {
        this.modifiedBy = value;
    }

    /**
     * Gets the value of the modifiedOn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModifiedOn() {
        return modifiedOn;
    }

    /**
     * Sets the value of the modifiedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModifiedOn(String value) {
        this.modifiedOn = value;
    }

    /**
     * Gets the value of the objectId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * Sets the value of the objectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectId(String value) {
        this.objectId = value;
    }

    /**
     * Gets the value of the reserved property.
     * 
     */
    public boolean isReserved() {
        return reserved;
    }

    /**
     * Sets the value of the reserved property.
     * 
     */
    public void setReserved(boolean value) {
        this.reserved = value;
    }

    /**
     * Gets the value of the verSId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerSId() {
        return verSId;
    }

    /**
     * Sets the value of the verSId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerSId(String value) {
        this.verSId = value;
    }

    /**
     * Gets the value of the versionNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersionNo() {
        return versionNo;
    }

    /**
     * Sets the value of the versionNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersionNo(String value) {
        this.versionNo = value;
    }

}
