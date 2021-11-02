package com.code.integration.responses.hcm;

public class WSEmployeeBasicInfoResponse {
    private String name;
    private String managerName;
    private String jobDesc;
    private String socialID;
    private String physicalUnitFullName;
    private String officialMobileNumber;
    private String directPhoneNumber;
    private String privateMobileNumber;
    private String phoneExt;
    private String shieldMobileNumber;
    private String ipPhoneExt;
    private String email;
    private byte[] photo;

    public WSEmployeeBasicInfoResponse(String name, String managerName, String jobDesc, String socialID, String physicalUnitFullName, String officialMobileNumber, String directPhoneNumber, String privateMobileNumber, String phoneExt, String shieldMobileNumber, String ipPhoneExt, String email, byte[] photo) {
	this.name = name;
	this.managerName = managerName;
	this.jobDesc = jobDesc;
	this.socialID = socialID;
	this.physicalUnitFullName = physicalUnitFullName;
	this.officialMobileNumber = officialMobileNumber;
	this.directPhoneNumber = directPhoneNumber;
	this.privateMobileNumber = privateMobileNumber;
	this.phoneExt = phoneExt;
	this.shieldMobileNumber = shieldMobileNumber;
	this.ipPhoneExt = ipPhoneExt;
	this.email = email;
	this.photo = photo;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getManagerName() {
	return managerName;
    }

    public void setManagerName(String managerName) {
	this.managerName = managerName;
    }

    public String getJobDesc() {
	return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
	this.jobDesc = jobDesc;
    }

    public String getSocialID() {
	return socialID;
    }

    public void setSocialID(String socialID) {
	this.socialID = socialID;
    }

    public String getPhysicalUnitFullName() {
	return physicalUnitFullName;
    }

    public void setPhysicalUnitFullName(String physicalUnitFullName) {
	this.physicalUnitFullName = physicalUnitFullName;
    }

    public String getOfficialMobileNumber() {
	return officialMobileNumber;
    }

    public void setOfficialMobileNumber(String officialMobileNumber) {
	this.officialMobileNumber = officialMobileNumber;
    }

    public String getDirectPhoneNumber() {
	return directPhoneNumber;
    }

    public void setDirectPhoneNumber(String directPhoneNumber) {
	this.directPhoneNumber = directPhoneNumber;
    }

    public String getPrivateMobileNumber() {
	return privateMobileNumber;
    }

    public void setPrivateMobileNumber(String privateMobileNumber) {
	this.privateMobileNumber = privateMobileNumber;
    }

    public String getPhoneExt() {
	return phoneExt;
    }

    public void setPhoneExt(String phoneExt) {
	this.phoneExt = phoneExt;
    }

    public String getShieldMobileNumber() {
	return shieldMobileNumber;
    }

    public void setShieldMobileNumber(String shieldMobileNumber) {
	this.shieldMobileNumber = shieldMobileNumber;
    }

    public String getIpPhoneExt() {
	return ipPhoneExt;
    }

    public void setIpPhoneExt(String ipPhoneExt) {
	this.ipPhoneExt = ipPhoneExt;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public byte[] getPhoto() {
	return photo;
    }

    public void setPhoto(byte[] photo) {
	this.photo = photo;
    }

}
