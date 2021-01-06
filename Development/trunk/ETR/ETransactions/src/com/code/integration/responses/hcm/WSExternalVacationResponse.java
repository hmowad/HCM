package com.code.integration.responses.hcm;

import com.code.integration.responses.WSResponseBase;

public class WSExternalVacationResponse extends WSResponseBase {
    private String employeeName;
    private String socialId;
    private String rankDescription;
    private Integer militaryNumber;
    private String decisionNumber;
    private String decisionDateString;
    private Integer period;
    private String startDateString;
    private String endDateString;
    private String location;

    public String getEmployeeName() {
	return employeeName;
    }

    public void setEmployeeName(String employeeName) {
	this.employeeName = employeeName;
    }

    public String getSocialId() {
	return socialId;
    }

    public void setSocialId(String socialId) {
	this.socialId = socialId;
    }

    public String getRankDescription() {
	return rankDescription;
    }

    public void setRankDescription(String rankDescription) {
	this.rankDescription = rankDescription;
    }

    public Integer getMilitaryNumber() {
	return militaryNumber;
    }

    public void setMilitaryNumber(Integer militaryNumber) {
	this.militaryNumber = militaryNumber;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public String getDecisionDateString() {
	return decisionDateString;
    }

    public void setDecisionDateString(String decisionDateString) {
	this.decisionDateString = decisionDateString;
    }

    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    public String getStartDateString() {
	return startDateString;
    }

    public void setStartDateString(String startDateString) {
	this.startDateString = startDateString;
    }

    public String getEndDateString() {
	return endDateString;
    }

    public void setEndDateString(String endDateString) {
	this.endDateString = endDateString;
    }

    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

}
