package com.code.integration.responses.workflow.hcm;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class WSWFTrainingTaskInfo implements Serializable {
    private Long taskId;
    private Long processId;
    private String processName;
    private String requesterName;
    private String delegatingName;
    private long trainingTypeId;
    private String courseName;
    private String trainingUnitName;
    private String externalPartyDesc;
    private String graduationPlaceDetailDesc;
    private String studySubject;

    public Long getTaskId() {
	return taskId;
    }

    public void setTaskId(Long taskId) {
	this.taskId = taskId;
    }

    public Long getProcessId() {
	return processId;
    }

    public void setProcessId(Long processId) {
	this.processId = processId;
    }

    public String getProcessName() {
	return processName;
    }

    public void setProcessName(String processName) {
	this.processName = processName;
    }

    public String getRequesterName() {
	return requesterName;
    }

    public void setRequesterName(String requesterName) {
	this.requesterName = requesterName;
    }

    @XmlElement(nillable = true)
    public String getDelegatingName() {
	return delegatingName;
    }

    public void setDelegatingName(String delegatingName) {
	this.delegatingName = delegatingName;
    }

    public long getTrainingTypeId() {
	return trainingTypeId;
    }

    public void setTrainingTypeId(long trainingTypeId) {
	this.trainingTypeId = trainingTypeId;
    }

    @XmlElement(nillable = true)
    public String getCourseName() {
	return courseName;
    }

    public void setCourseName(String courseName) {
	this.courseName = courseName;
    }

    @XmlElement(nillable = true)
    public String getTrainingUnitName() {
	return trainingUnitName;
    }

    public void setTrainingUnitName(String trainingUnitName) {
	this.trainingUnitName = trainingUnitName;
    }

    @XmlElement(nillable = true)
    public String getExternalPartyDesc() {
	return externalPartyDesc;
    }

    public void setExternalPartyDesc(String externalPartyDesc) {
	this.externalPartyDesc = externalPartyDesc;
    }

    @XmlElement(nillable = true)
    public String getGraduationPlaceDetailDesc() {
	return graduationPlaceDetailDesc;
    }

    public void setGraduationPlaceDetailDesc(String graduationPlaceDetailDesc) {
	this.graduationPlaceDetailDesc = graduationPlaceDetailDesc;
    }

    @XmlElement(nillable = true)
    public String getStudySubject() {
	return studySubject;
    }

    public void setStudySubject(String studySubject) {
	this.studySubject = studySubject;
    }
}
