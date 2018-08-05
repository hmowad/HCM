package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.trainings.QualificationMinorSpecData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "qualificationMinorSpecsMiniSearch")
@ViewScoped
public class QualificationMinorSpecsMiniSearch extends BaseBacking implements Serializable {
    private int rowsCount = 10;
    private String searchMinorDesc;
    private String searchMajorDesc;
    private int militaryClassificationFlag; // 1 for military courses, 0 for others
    private List<QualificationMinorSpecData> qualificationMinorSpecsList;

    public QualificationMinorSpecsMiniSearch() {
	if (getRequest().getParameter("militaryClassificationFlag") != null)
	    militaryClassificationFlag = Integer.parseInt(getRequest().getParameter("militaryClassificationFlag"));
	reset();
    }

    public void searchQualificationMinorSpecs() {
	try {
	    super.init();
	    qualificationMinorSpecsList = TrainingSetupService.getQualificationMinorSpecs(militaryClassificationFlag, searchMinorDesc, searchMajorDesc);
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reset() {
	searchMinorDesc = "";
	searchMajorDesc = "";
	searchQualificationMinorSpecs();
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public String getSearchMinorDesc() {
	return searchMinorDesc;
    }

    public void setSearchMinorDesc(String searchMinorDesc) {
	this.searchMinorDesc = searchMinorDesc;
    }

    public String getSearchMajorDesc() {
	return searchMajorDesc;
    }

    public void setSearchMajorDesc(String searchMajorDesc) {
	this.searchMajorDesc = searchMajorDesc;
    }

    public List<QualificationMinorSpecData> getQualificationMinorSpecsList() {
	return qualificationMinorSpecsList;
    }

    public void setQualificationMinorSpecsList(List<QualificationMinorSpecData> qualificationMinorSpecsList) {
	this.qualificationMinorSpecsList = qualificationMinorSpecsList;
    }

    public int getMilitaryClassificationFlag() {
	return militaryClassificationFlag;
    }

    public void setMilitaryClassificationFlag(int militaryClassificationFlag) {
	this.militaryClassificationFlag = militaryClassificationFlag;
    }
}