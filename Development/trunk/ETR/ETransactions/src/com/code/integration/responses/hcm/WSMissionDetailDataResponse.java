package com.code.integration.responses.hcm;

public class WSMissionDetailDataResponse {
    private Long id;
    private String decisionNumber;
    private String decisionDate;
    private Boolean locationFlag;
    private String destination;
    private String purpose;
    private Integer period;
    private String startDate;
    private String endDate;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public String getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(String decisionDate) {
	this.decisionDate = decisionDate;
    }

    public Boolean getLocationFlag() {
	return locationFlag;
    }

    public void setLocationFlag(Boolean locationFlag) {
	this.locationFlag = locationFlag;
    }

    public String getDestination() {
	return destination;
    }

    public void setDestination(String destination) {
	this.destination = destination;
    }

    public String getPurpose() {
	return purpose;
    }

    public void setPurpose(String purpose) {
	this.purpose = purpose;
    }

    public Integer getPeriod() {
	return period;
    }

    public void setPeriod(Integer period) {
	this.period = period;
    }

    public String getStartDate() {
	return startDate;
    }

    public void setStartDate(String startDate) {
	this.startDate = startDate;
    }

    public String getEndDate() {
	return endDate;
    }

    public void setEndDate(String endDate) {
	this.endDate = endDate;
    }

}
