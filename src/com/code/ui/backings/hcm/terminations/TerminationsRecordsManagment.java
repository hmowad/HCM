package com.code.ui.backings.hcm.terminations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.terminations.TerminationReason;
import com.code.dal.orm.hcm.terminations.TerminationRecordData;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TerminationReasonsEnum;
import com.code.enums.TerminationRecordStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TerminationsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "terminationsRecordsMangement")
@ViewScoped
public class TerminationsRecordsManagment extends BaseBacking {

    private Long categoryId;
    private Long reasonId;
    private Long regionId;
    private String recordNumber;
    private Long status;
    private Date recordDateFrom;
    private Date recordDateTo;
    private Date retirementDateFrom;
    private Date retirementDateTo;
    private Long rankId;
    private int mode;
    private Long empId;
    private String empName;
    private List<Rank> ranks;
    private List<TerminationReason> terminationReasons;
    private List<TerminationReason> terminationCiviliansReasons;
    private List<TerminationReason> terminationContractorsReasons;
    private List<Region> regions;
    private List<Category> categories;
    private List<TerminationRecordData> terminationRecords;

    private final int pageSize = 10;
    private static final int ONE_YEAR = -360;

    public TerminationsRecordsManagment() {
	this.init();
    }

    public void init() {
	if (getRequest().getParameter("mode") != null || getRequest().getAttribute("mode") != null) {
	    mode = getRequest().getParameter("mode") != null ? Integer.parseInt(getRequest().getParameter("mode")) : Integer.parseInt(getRequest().getAttribute("mode").toString());

	    setStatus(TerminationRecordStatusEnum.CURRENT.getCode());

	    try {
		resetForm();
		regions = CommonService.getAllRegions();
		categories = new ArrayList<Category>();
		if (mode == CategoryModesEnum.OFFICERS.getCode()) {
		    setScreenTitle(getMessage("label_terminationOfficersRecords"));
		    categories.add(CommonService.getCategoryById(CategoriesEnum.OFFICERS.getCode()));
		    categoryId = CategoriesEnum.OFFICERS.getCode();
		    terminationReasons = TerminationsService.getTerminationReasons(CategoriesEnum.OFFICERS.getCode());
		    ranks = getOfficerRankList(new Long(FlagsEnum.ALL.getCode()));
		} else if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
		    setScreenTitle(getMessage("label_terminationSoldiersRecords"));
		    categories.add(CommonService.getCategoryById(CategoriesEnum.SOLDIERS.getCode()));
		    categoryId = CategoriesEnum.SOLDIERS.getCode();
		    terminationReasons = TerminationsService.getTerminationReasons(CategoriesEnum.SOLDIERS.getCode());
		    ranks = getSoldierRankList();
		} else if (mode == CategoryModesEnum.CIVILIANS.getCode()) {
		    setScreenTitle(getMessage("label_terminationCiviliansRecords"));
		    categories = CommonService.getPersonsCategories();
		    categoryId = CategoriesEnum.PERSONS.getCode();
		    terminationCiviliansReasons = TerminationsService.getTerminationReasons(CategoriesEnum.PERSONS.getCode());
		    terminationContractorsReasons = TerminationsService.getTerminationReasons(CategoriesEnum.CONTRACTORS.getCode());
		    terminationReasons = terminationCiviliansReasons;
		} else {
		    setServerSideErrorMessages(getMessage("error_URLError"));
		}
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	}

    }

    public void resetForm() {
	try {
	    status = TerminationRecordStatusEnum.CURRENT.getCode();
	    categoryId = new Long(mode);
	    recordDateFrom = HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ONE_YEAR, true);
	    recordDateTo = HijriDateService.getHijriSysDate();
	    recordNumber = null;
	    retirementDateFrom = null;
	    retirementDateTo = null;
	    rankId = null;
	    if (loginEmpData.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()))
		regionId = new Long(FlagsEnum.ALL.getCode());
	    else
		regionId = new Long(loginEmpData.getPhysicalRegionId());
	    reasonId = new Long(-1);
	    empId = null;
	    empName = "";
	    terminationRecords = null;
	    if (mode == CategoryModesEnum.CIVILIANS.getCode()) {
		categories = CommonService.getPersonsCategories();
		ranks = CommonService.getRanks(null, new Long[] { new Long(mode) });
		terminationReasons = terminationCiviliansReasons;
	    }
	    reasonChanged();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void civilianCategoryChanged() {
	try {
	    ranks = CommonService.getRanks(null, new Long[] { categoryId });
	    if (categoryId == CategoriesEnum.CONTRACTORS.getCode()) {
		terminationReasons = terminationContractorsReasons;
	    } else {
		terminationReasons = terminationCiviliansReasons;
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void reasonChanged() {
	if (mode == CategoryModesEnum.OFFICERS.getCode())
	    getOfficerRankList(reasonId);

	recordNumber = null;
	retirementDateFrom = null;
	retirementDateTo = null;

	empId = null;
	empName = "";
    }

    public void search() {
	terminationRecords = null;
	try {
	    this.terminationRecords = TerminationsService.getTerminationRecordsData(recordNumber, recordDateFrom, recordDateTo, retirementDateFrom, retirementDateTo, rankId, status, reasonId, empId, categoryId, regionId);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private List<Rank> getOfficerRankList(long reasonId) {
	ranks = new ArrayList<Rank>();
	if (reasonId == TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode()) {
	    ranks.add(CommonService.getRankById(RanksEnum.MAJOR_GENERAL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.BRIGADIER.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT_COLONEL.getCode()));
	} else {
	    ranks.add(CommonService.getRankById(RanksEnum.MAJOR_GENERAL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.BRIGADIER.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT_COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.MAJOR.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.CAPTAIN.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.FIRST_LIEUTENANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT.getCode()));
	}
	return ranks;
    }

    private List<Rank> getSoldierRankList() {
	ranks = new ArrayList<Rank>();

	ranks.add(CommonService.getRankById(RanksEnum.PRIME_SERGEANTS.getCode()));
	ranks.add(CommonService.getRankById(RanksEnum.STAFF_SERGEANT.getCode()));
	ranks.add(CommonService.getRankById(RanksEnum.SERGEANT.getCode()));
	ranks.add(CommonService.getRankById(RanksEnum.UNDER_SERGEANT.getCode()));
	ranks.add(CommonService.getRankById(RanksEnum.CORPORAL.getCode()));
	ranks.add(CommonService.getRankById(RanksEnum.FIRST_SOLDIER.getCode()));
	ranks.add(CommonService.getRankById(RanksEnum.SOLDIER.getCode()));
	return ranks;
    }

    public void deleteTerminationRecord(TerminationRecordData terminationRecordData) {

	try {
	    TerminationsService.deleteTerminationRecordAndDetails(terminationRecordData, new Long(mode), this.loginEmpData.getEmpId());
	    terminationRecords.remove(terminationRecordData);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printTerminationRecord(TerminationRecordData terminationRecordData) {
	try {
	    byte[] bytes = TerminationsService.getTerminationRecordBytes(terminationRecordData.getId(), (long) mode, terminationRecordData.getReasonId(), null, null);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<TerminationRecordData> getTerminationRecords() {
	return terminationRecords;
    }

    public void setTerminationRecords(List<TerminationRecordData> terminationRecords) {
	this.terminationRecords = terminationRecords;
    }

    public List<Category> getCategories() {
	return categories;
    }

    public void setCategories(List<Category> categories) {
	this.categories = categories;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }

    public List<TerminationReason> getTerminationReasons() {
	return terminationReasons;
    }

    public void setTerminationReasons(List<TerminationReason> terminationReasons) {
	this.terminationReasons = terminationReasons;
    }

    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    public Long getReasonId() {
	return reasonId;
    }

    public void setReasonId(Long reasonId) {
	this.reasonId = reasonId;
    }

    public Long getRegionId() {
	return regionId;
    }

    public void setRegionId(Long regionId) {
	this.regionId = regionId;
    }

    public String getRecordNumber() {
	return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
	this.recordNumber = recordNumber;
    }

    public Long getStatus() {
	return status;
    }

    public void setStatus(Long status) {
	this.status = status;
    }

    public Date getRecordDateFrom() {
	return recordDateFrom;
    }

    public void setRecordDateFrom(Date recordDateFrom) {
	this.recordDateFrom = recordDateFrom;
    }

    public Date getRecordDateTo() {
	return recordDateTo;
    }

    public void setRecordDateTo(Date recordDateTo) {
	this.recordDateTo = recordDateTo;
    }

    public Date getRetirementDateFrom() {
	return retirementDateFrom;
    }

    public void setRetirementDateFrom(Date retirementDateFrom) {
	this.retirementDateFrom = retirementDateFrom;
    }

    public Date getRetirementDateTo() {
	return retirementDateTo;
    }

    public void setRetirementDateTo(Date retirementDateTo) {
	this.retirementDateTo = retirementDateTo;
    }

    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    public int getPageSize() {
	return pageSize;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public void setRanks(List<Rank> ranks) {
	this.ranks = ranks;
    }

}
