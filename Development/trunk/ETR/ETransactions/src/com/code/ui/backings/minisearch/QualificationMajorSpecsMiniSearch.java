package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.QualificationMajorSpec;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "qualificationMajorSpecsMiniSearch")
@ViewScoped
public class QualificationMajorSpecsMiniSearch extends BaseBacking implements Serializable {
    private int rowsCount = 10;
    private String searchMajorDesc;
    private int militaryClassificationFlag; // 1 for military courses, 0 for others
    private List<QualificationMajorSpec> qualificationMajorSpecsList;

    public QualificationMajorSpecsMiniSearch() {
	if (getRequest().getParameter("militaryClassificationFlag") != null)
	    militaryClassificationFlag = Integer.parseInt(getRequest().getParameter("militaryClassificationFlag"));
	reset();
    }

    public void searchQualificationMajorSpecs() {
	try {
	    super.init();
	    setQualificationMajorSpecsList(TrainingSetupService.getQualificationMajorSpecs(militaryClassificationFlag, searchMajorDesc, FlagsEnum.ALL.getCode()));
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	searchMajorDesc = "";
	searchQualificationMajorSpecs();
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public String getSearchMajorDesc() {
	return searchMajorDesc;
    }

    public void setSearchMajorDesc(String searchMajorDesc) {
	this.searchMajorDesc = searchMajorDesc;
    }

    public int getMilitaryClassificationFlag() {
	return militaryClassificationFlag;
    }

    public void setMilitaryClassificationFlag(int militaryClassificationFlag) {
	this.militaryClassificationFlag = militaryClassificationFlag;
    }

    public List<QualificationMajorSpec> getQualificationMajorSpecsList() {
	return qualificationMajorSpecsList;
    }

    public void setQualificationMajorSpecsList(List<QualificationMajorSpec> qualificationMajorSpecsList) {
	this.qualificationMajorSpecsList = qualificationMajorSpecsList;
    }
}