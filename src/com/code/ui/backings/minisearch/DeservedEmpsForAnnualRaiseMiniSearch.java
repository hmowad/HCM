package com.code.ui.backings.minisearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.raises.RaiseEmployeeData;
import com.code.enums.FlagsEnum;
import com.code.enums.RaiseEmployeesTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.RaisesService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "deservedEmpsMiniSearch")
@ViewScoped
public class DeservedEmpsForAnnualRaiseMiniSearch extends BaseBacking implements Serializable {
    private String searchEmpName;
    private String searchMilitaryNumber;
    private String searchJobDesc;
    private String searchUnitFullName;
    private String searchSocialId;
    private String decisionDate;
    private String decisionNumber;
    private Long regionId;
    private Long categoryId;
    private Integer[] deservedFlagValues;
    private int rowsCount = 10;
    private List<RaiseEmployeeData> searchEmployeeList;

    public DeservedEmpsForAnnualRaiseMiniSearch() {
	deservedFlagValues = new Integer[] { RaiseEmployeesTypesEnum.DESERVED_EMPLOYEES.getCode() };
	searchEmployeeList = new ArrayList<RaiseEmployeeData>();
	if (getRequest().getParameter("decisionDateString") != null)
	    decisionDate = getRequest().getParameter("decisionDateString");
	if (getRequest().getParameter("decisionNumber") != null)
	    decisionNumber = getRequest().getParameter("decisionNumber");
	if (getRequest().getParameter("regionId") != null)
	    regionId = Long.parseLong(getRequest().getParameter("regionId"));
	if (getRequest().getParameter("categoryId") != null)
	    categoryId = Long.parseLong(getRequest().getParameter("categoryId"));
    }

    public void searchEmployee() {
	try {
	    Long militaryNumber = (long) ((searchMilitaryNumber == null || searchMilitaryNumber.isEmpty()) ? FlagsEnum.ALL.getCode() : Integer.parseInt(searchMilitaryNumber));
	    searchEmployeeList = RaisesService.getAnnualRaiseDeservedEmployees(searchSocialId, searchEmpName, searchJobDesc, searchUnitFullName, militaryNumber, decisionDate, decisionNumber, deservedFlagValues, regionId, categoryId);
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String getSearchEmpName() {
	return searchEmpName;
    }

    public void setSearchEmpName(String searchEmpName) {
	this.searchEmpName = searchEmpName;
    }

    public String getSearchMilitaryNumber() {
	return searchMilitaryNumber;
    }

    public void setSearchMilitaryNumber(String searchMilitaryNumber) {
	this.searchMilitaryNumber = searchMilitaryNumber;
    }

    public String getSearchJobDesc() {
	return searchJobDesc;
    }

    public void setSearchJobDesc(String searchJobDesc) {
	this.searchJobDesc = searchJobDesc;
    }

    public String getSearchUnitFullName() {
	return searchUnitFullName;
    }

    public void setSearchUnitFullName(String searchUnitFullName) {
	this.searchUnitFullName = searchUnitFullName;
    }

    public String getSearchSocialId() {
	return searchSocialId;
    }

    public void setSearchSocialId(String searchSocialId) {
	this.searchSocialId = searchSocialId;
    }

    public String getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(String decisionDate) {
	this.decisionDate = decisionDate;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    public List<RaiseEmployeeData> getSearchEmployeeList() {
	return searchEmployeeList;
    }

    public void setSearchEmployeeList(List<RaiseEmployeeData> searchEmployeeList) {
	this.searchEmployeeList = searchEmployeeList;
    }

    public Integer[] getDeservedFlagValues() {
	return deservedFlagValues;
    }

    public void setDeservedFlagValues(Integer[] deservedFlagValues) {
	this.deservedFlagValues = deservedFlagValues;
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

}
