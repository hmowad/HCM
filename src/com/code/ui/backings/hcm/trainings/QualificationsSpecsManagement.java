package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.QualificationMajorSpec;
import com.code.dal.orm.hcm.trainings.QualificationMinorSpecData;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.workflow.hcm.TrainingEmployeesWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "qualificationsSpecsManagement")
@ViewScoped
public class QualificationsSpecsManagement extends BaseBacking {
    private int pageSize = 10;
    private int militaryClassificationFlag;

    private String searchMajorClassification;
    private String searchMinorClassification;

    private List<QualificationMajorSpec> majorClassifications;
    private List<QualificationMinorSpecData> minorClassifications;

    private QualificationMajorSpec selectedMajorSpec;

    private int majorPageNum = 1;
    private int minorPageNum = 1;

    public QualificationsSpecsManagement() {
	if (getRequest().getParameter("mode") != null) {
	    if (Integer.parseInt(getRequest().getParameter("mode")) == FlagsEnum.ON.getCode())
		militaryClassificationFlag = FlagsEnum.ON.getCode();
	    else
		militaryClassificationFlag = FlagsEnum.OFF.getCode();

	    setScreenTitle(militaryClassificationFlag == FlagsEnum.ON.getCode() ? getMessage("title_militaryCoursesClassification") : getMessage("title_civilianCoursesClassification"));
	    reset();
	}
    }

    public void search() {
	try {
	    majorClassifications = TrainingSetupService.getQualificationMajorSpecsByMinorSpec(militaryClassificationFlag, searchMinorClassification, searchMajorClassification);

	    if (!majorClassifications.isEmpty()) {
		selectedMajorSpec = majorClassifications.get(0);
		minorClassifications = TrainingSetupService.getQualificationMinorSpecsByMajorId(majorClassifications.get(0).getId());
	    } else {
		selectedMajorSpec = null;
		minorClassifications = new ArrayList<QualificationMinorSpecData>();
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	searchMinorClassification = searchMajorClassification = null;
	search();
    }

    public void selectMajorSpec(QualificationMajorSpec majorSpec) {
	try {
	    selectedMajorSpec = majorSpec;
	    minorClassifications = TrainingSetupService.getQualificationMinorSpecsByMajorId(selectedMajorSpec.getId());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addMajorClassification() {
	selectedMajorSpec = new QualificationMajorSpec();
	selectedMajorSpec.setMilitaryClassificationFlag(militaryClassificationFlag);
	majorClassifications.add(0, selectedMajorSpec);
	majorPageNum = 1;
	minorClassifications = new ArrayList<QualificationMinorSpecData>();
    }

    public void addMinorClassification() {
	QualificationMinorSpecData minorSpec = new QualificationMinorSpecData();
	minorSpec.setMilitaryClassificationFlag(selectedMajorSpec.getMilitaryClassificationFlag());
	minorSpec.setQualificationMajorSpecId(selectedMajorSpec.getId());
	minorSpec.setQualificationMajorSpecDesc(selectedMajorSpec.getDescription());
	minorClassifications.add(0, minorSpec);
	minorPageNum = 1;
    }

    public void deleteMajorClassification(QualificationMajorSpec qualMajorSpec) {
	try {
	    if (qualMajorSpec.getId() != null) {
		TrainingEmployeesWorkFlow.validateQualificationMajorSpecWFBusinessRules(qualMajorSpec);

		qualMajorSpec.setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
		TrainingSetupService.deleteQualificationMajorSpec(qualMajorSpec);
	    }

	    majorClassifications.remove(qualMajorSpec);

	    if (selectedMajorSpec == qualMajorSpec)
		selectedMajorSpec = null;

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteMinorClassification(QualificationMinorSpecData qualMinorSpec) {
	try {
	    if (qualMinorSpec.getId() != null) {
		TrainingEmployeesWorkFlow.validateQualificationMinorSpecWFBusinessRules(qualMinorSpec);

		qualMinorSpec.getQualificationMinoSpec().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
		TrainingSetupService.deleteQualificationMinorSpec(qualMinorSpec);
	    }

	    minorClassifications.remove(qualMinorSpec);

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void saveMajorSpec(QualificationMajorSpec qualMajorSpec) {
	try {
	    qualMajorSpec.setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.

	    if (qualMajorSpec.getId() == null)
		TrainingSetupService.addQualificationMajorSpec(qualMajorSpec);
	    else {
		TrainingEmployeesWorkFlow.validateQualificationMajorSpecWFBusinessRules(qualMajorSpec);
		TrainingSetupService.updateQualificationMajorSpec(qualMajorSpec);
	    }

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void saveMinorSpec(QualificationMinorSpecData qualMinorSpec) {
	try {
	    qualMinorSpec.getQualificationMinoSpec().setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.

	    if (qualMinorSpec.getId() == null)
		TrainingSetupService.addQualificationMinorSpec(qualMinorSpec);
	    else {
		TrainingEmployeesWorkFlow.validateQualificationMinorSpecWFBusinessRules(qualMinorSpec);
		TrainingSetupService.updateQualificationMinorSpec(qualMinorSpec);
	    }

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getPageSize() {
	return pageSize;
    }

    public String getSearchMajorClassification() {
	return searchMajorClassification;
    }

    public void setSearchMajorClassification(String searchMajorClassification) {
	this.searchMajorClassification = searchMajorClassification;
    }

    public String getSearchMinorClassification() {
	return searchMinorClassification;
    }

    public void setSearchMinorClassification(String searchMinorClassification) {
	this.searchMinorClassification = searchMinorClassification;
    }

    public List<QualificationMajorSpec> getMajorClassifications() {
	return majorClassifications;
    }

    public void setMajorClassifications(List<QualificationMajorSpec> majorClassifications) {
	this.majorClassifications = majorClassifications;
    }

    public List<QualificationMinorSpecData> getMinorClassifications() {
	return minorClassifications;
    }

    public void setMinorClassifications(List<QualificationMinorSpecData> minorClassifications) {
	this.minorClassifications = minorClassifications;
    }

    public QualificationMajorSpec getSelectedMajorSpec() {
	return selectedMajorSpec;
    }

    public int getMajorPageNum() {
	return majorPageNum;
    }

    public void setMajorPageNum(int majorPageNum) {
	this.majorPageNum = majorPageNum;
    }

    public int getMinorPageNum() {
	return minorPageNum;
    }

    public void setMinorPageNum(int minorPageNum) {
	this.minorPageNum = minorPageNum;
    }

}
