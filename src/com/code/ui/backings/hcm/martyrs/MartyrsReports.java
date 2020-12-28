package com.code.ui.backings.hcm.martyrs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.martyrs.MartyrdomReason;
import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MartyrsService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "martyrsReports")
@ViewScoped
public class MartyrsReports extends BaseBacking {

    private int reportType; // 1 for Martyrs data
    private String categoriesIds;
    private int martyrdomTypeFlag;
    private long martyrReasonId;
    private List<MartyrdomReason> reasonsList;
    private EmployeeData employee;
    private long martyrRegionId;
    private List<Region> regionsList;
    private long categoryId;
    private long martyrRankId;
    private List<Rank> ranksOfficersList;
    private List<Rank> ranksSolidersList;
    private List<Rank> ranksList;
    private Date martyrDateFrom;
    private Date martyrDateTo;
    private int medicalDecisionFlag;
    private int terminationDecisionFlag;
    private int retirementCompensationFlag;
    private int vacationsCompensationFlag;
    private int terminationCompensationFlag;
    private int deathCompensationFlag;
    private int injuryCompensationFlag;
    private int housingCompensationFlag;
    private int heirsCompensationFlag;
    private Long injuryCompensationFrom;
    private Long injuryCompensationTo;

    // constructors
    public MartyrsReports() {
	try {
	    reportType = FlagsEnum.ON.getCode();
	    reasonsList = MartyrsService.getMartyrdomReasons();
	    regionsList = CommonService.getAllRegions();
	    ranksOfficersList = CommonService.getRanks(null, new Long[] { (long) 1 });
	    ranksSolidersList = CommonService.getRanks(null, new Long[] { (long) 2 });
	    categoriesIds = CategoriesEnum.OFFICERS.getCode() + "," + CategoriesEnum.SOLDIERS.getCode();
	    resetForm();

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectEmployee() throws BusinessException {
	employee = EmployeesService.getEmployeeData(employee.getEmpId());
    }

    // category changed method
    public void categoryChangedListener() {

	if (categoryId == 1) {
	    ranksList = ranksOfficersList;
	} else if (categoryId == 2) {
	    ranksList = ranksSolidersList;
	} else {
	    ranksList = new ArrayList<Rank>();
	    ranksList.addAll(ranksOfficersList);
	    ranksList.addAll(ranksSolidersList);
	}
    }

    // print form
    public void print() {
	try {
	    byte[] bytes = null;
	    bytes = MartyrsService.getMartyrsDataBytes(employee.getEmpId(), martyrdomTypeFlag, martyrReasonId, martyrRegionId, categoryId, martyrRankId, martyrDateFrom, martyrDateTo, medicalDecisionFlag, terminationDecisionFlag, retirementCompensationFlag, vacationsCompensationFlag, terminationCompensationFlag, housingCompensationFlag, deathCompensationFlag, injuryCompensationFlag, heirsCompensationFlag,
		    injuryCompensationFrom == null ? FlagsEnum.ALL.getCode() : injuryCompensationFrom, injuryCompensationTo == null ? FlagsEnum.ALL.getCode() : injuryCompensationTo);
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    // reset form
    public void resetForm() throws BusinessException {
	martyrReasonId = FlagsEnum.ALL.getCode();
	employee = new EmployeeData();
	martyrRegionId = getLoginEmpPhysicalRegionFlag(true);
	martyrRankId = FlagsEnum.ALL.getCode();
	martyrDateFrom = martyrDateTo = null;
	categoryId = FlagsEnum.ALL.getCode();
	martyrdomTypeFlag = FlagsEnum.ON.getCode();
	medicalDecisionFlag = FlagsEnum.ALL.getCode();
	terminationDecisionFlag = FlagsEnum.ALL.getCode();
	retirementCompensationFlag = FlagsEnum.ALL.getCode();
	vacationsCompensationFlag = FlagsEnum.ALL.getCode();
	terminationCompensationFlag = FlagsEnum.ALL.getCode();
	deathCompensationFlag = FlagsEnum.ALL.getCode();
	injuryCompensationFlag = FlagsEnum.ALL.getCode();
	housingCompensationFlag = FlagsEnum.ALL.getCode();
	heirsCompensationFlag = FlagsEnum.ALL.getCode();
	injuryCompensationFrom = injuryCompensationTo = null;
	ranksList = new ArrayList<Rank>();
	ranksList.addAll(ranksOfficersList);
	ranksList.addAll(ranksSolidersList);
    }

    // setters and getters for martyrs data attribute
    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public String getCategoriesIds() {
	return categoriesIds;
    }

    public void setCategoriesIds(String categoriesIds) {
	this.categoriesIds = categoriesIds;
    }

    // setters and getters for martyrs type attribute
    public int getMartyrdomTypeFlag() {
	return martyrdomTypeFlag;
    }

    public void setMartyrdomTypeFlag(int martyrdomTypeFlag) {
	this.martyrdomTypeFlag = martyrdomTypeFlag;
    }

    // setters and getters for martyrs reason attribute
    public long getMartyrReasonId() {
	return martyrReasonId;
    }

    public void setMartyrReasonId(long martyrReasonId) {
	this.martyrReasonId = martyrReasonId;
    }

    // setters and getters for martyrs reason list
    public List<MartyrdomReason> getReasonsList() {
	return reasonsList;
    }

    public void setReasonsList(List<MartyrdomReason> reasonsList) {
	this.reasonsList = reasonsList;
    }

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

    // setters and getters for martyrs region attribute
    public long getMartyrRegionId() {
	return martyrRegionId;
    }

    public void setMartyrRegionId(long martyrRegionId) {
	this.martyrRegionId = martyrRegionId;
    }

    // setters and getters for martyrs region list
    public List<Region> getRegionsList() {
	return regionsList;
    }

    public void setRegionsList(List<Region> regionsList) {
	this.regionsList = regionsList;
    }

    // setters and getters for martyrs rank attribute
    public long getMartyrRankId() {
	return martyrRankId;
    }

    public void setMartyrRankId(long martyrRankId) {
	this.martyrRankId = martyrRankId;
    }

    // setters and getters for category attribute
    public long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(long categoryId) {
	this.categoryId = categoryId;
    }

    // setters and getters for matyr date from attribute
    public Date getMartyrDateFrom() {
	return martyrDateFrom;
    }

    public void setMartyrDateFrom(Date martyrDateFrom) {
	this.martyrDateFrom = martyrDateFrom;
    }

    // setters and getters for matyr date to attribute
    public Date getMartyrDateTo() {
	return martyrDateTo;
    }

    public void setMartyrDateTo(Date martyrDateTo) {
	this.martyrDateTo = martyrDateTo;
    }

    // setters and getters for medical decision flag attribute
    public int getMedicalDecisionFlag() {
	return medicalDecisionFlag;
    }

    public void setMedicalDecisionFlag(int medicalDecisionFlag) {
	this.medicalDecisionFlag = medicalDecisionFlag;
    }

    // setters and getters for termination decision flag attribute
    public int getTerminationDecisionFlag() {
	return terminationDecisionFlag;
    }

    public void setTerminationDecisionFlag(int terminationDecisionFlag) {
	this.terminationDecisionFlag = terminationDecisionFlag;
    }

    // setters and getters for retirement compensation flag attribute
    public int getRetirementCompensationFlag() {
	return retirementCompensationFlag;
    }

    public void setRetirementCompensationFlag(int retirementCompensationFlag) {
	this.retirementCompensationFlag = retirementCompensationFlag;
    }

    // setters and getters for vacation compensation flag attribute
    public int getVacationsCompensationFlag() {
	return vacationsCompensationFlag;
    }

    public void setVacationsCompensationFlag(int vacationsCompensationFlag) {
	this.vacationsCompensationFlag = vacationsCompensationFlag;
    }

    // setters and getters for termination compensation flag attribute
    public int getTerminationCompensationFlag() {
	return terminationCompensationFlag;
    }

    public void setTerminationCompensationFlag(int terminationCompensationFlag) {
	this.terminationCompensationFlag = terminationCompensationFlag;
    }

    // setters and getters for death compensation flag attribute
    public int getDeathCompensationFlag() {
	return deathCompensationFlag;
    }

    public void setDeathCompensationFlag(int deathCompensationFlag) {
	this.deathCompensationFlag = deathCompensationFlag;
    }

    // setters and getters for injury compensation flag attribute
    public int getInjuryCompensationFlag() {
	return injuryCompensationFlag;
    }

    public void setInjuryCompensationFlag(int injuryCompensationFlag) {
	this.injuryCompensationFlag = injuryCompensationFlag;
    }

    // setters and getters for housing compensation flag attribute
    public int getHousingCompensationFlag() {
	return housingCompensationFlag;
    }

    public void setHousingCompensationFlag(int housingCompensationFlag) {
	this.housingCompensationFlag = housingCompensationFlag;
    }

    // setters and getters for heirs compensation flag attribute
    public int getHeirsCompensationFlag() {
	return heirsCompensationFlag;
    }

    public void setHeirsCompensationFlag(int heirsCompensationFlag) {
	this.heirsCompensationFlag = heirsCompensationFlag;
    }

    // setters and getters for compensation rate from attribute
    public Long getInjuryCompensationFrom() {
	return injuryCompensationFrom;
    }

    public void setInjuryCompensationFrom(Long injuryCompensationFrom) {
	this.injuryCompensationFrom = injuryCompensationFrom;
    }

    // setters and getters for compensation rate to attribute
    public Long getInjuryCompensationTo() {
	return injuryCompensationTo;
    }

    public void setInjuryCompensationTo(Long injuryCompensationTo) {
	this.injuryCompensationTo = injuryCompensationTo;
    }

    public List<Rank> getRanksList() {
	return ranksList;
    }

    public void setRanksList(List<Rank> ranksList) {
	this.ranksList = ranksList;
    }

}
