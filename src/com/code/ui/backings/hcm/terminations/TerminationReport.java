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
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TerminationReasonsEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TerminationsService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "terminationReport")
@ViewScoped
public class TerminationReport extends BaseBacking {
    private int mode;
    private int reportType;
    private Long reasonId;
    private List<TerminationReason> terminationReasons;
    private long regionId;
    private List<Region> regions;
    private String unitFullName;
    private Long categoryId;
    private List<Category> categories;
    private Long rankId;
    private List<Rank> ranks;
    private Date serviceTerminationDateFrom;
    private Date serviceTerminationDateTo;
    private Date extensionDateFrom;
    private Date extensionDateTo;
    private Date terminationDueDateFrom;
    private Date terminationDueDateTo;
    private Date decisionDateFrom;
    private Date decisionDateTo;

    public TerminationReport() {
	init();
    }

    public void init() {
	try {
	    if (getRequest().getParameter("mode") != null || getRequest().getAttribute("mode") != null) {
		mode = getRequest().getParameter("mode") != null ? Integer.parseInt(getRequest().getParameter("mode")) : Integer.parseInt(getRequest().getAttribute("mode").toString());
		reportType = 1;
		categories = new ArrayList<Category>();

		if (mode != CategoryModesEnum.OFFICERS.getCode() && mode != CategoryModesEnum.SOLDIERS.getCode() && mode != CategoryModesEnum.CIVILIANS.getCode()) {
		    setServerSideErrorMessages(getMessage("error_general"));
		} else {
		    ranks = CommonService.getRanks(null, new Long[] { (long) mode });
		    if (mode == CategoryModesEnum.OFFICERS.getCode()) {
			setScreenTitle(getMessage("title_officersTerminationReport"));
			ranks.remove(ranks.size() - 1); // to remove CADET from the list
		    } else if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
			setScreenTitle(getMessage("title_soldiersTerminationReport"));
			ranks.remove(ranks.size() - 1); // to remove STUDENT_SOLDIER from the list
		    } else if (mode == CategoryModesEnum.CIVILIANS.getCode()) {
			setScreenTitle(getMessage("title_civiliansTerminationReport"));
			categories = CommonService.getPersonsCategories();
		    }
		}
		regions = CommonService.getAllRegions();
		resetForm();
	    } else {
		setServerSideErrorMessages(getMessage("error_general"));
	    }
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void resetEmployeeUnit() {
	try {

	    if (regionId == FlagsEnum.ALL.getCode())
		unitFullName = null;
	    else if (regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
		unitFullName = UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getFullName();
	    else
		unitFullName = UnitsService.getUnitsByTypeAndRegion(UnitTypesEnum.REGION_COMMANDER.getCode(), regionId).get(0).getFullName();

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void changeOfficerRanks() {
	ranks = new ArrayList<Rank>();
	if (reasonId.equals(TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode())) {
	    ranks.add(CommonService.getRankById(RanksEnum.MAJOR_GENERAL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.BRIGADIER.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT_COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.MAJOR.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.CAPTAIN.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.FIRST_LIEUTENANT.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT.getCode()));
	} else {
	    ranks.add(CommonService.getRankById(RanksEnum.MAJOR_GENERAL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.BRIGADIER.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.COLONEL.getCode()));
	    ranks.add(CommonService.getRankById(RanksEnum.LIEUTENANT_COLONEL.getCode()));
	}
    }

    public void civilianCategoryChanged() {
	try {
	    ranks = CommonService.getRanks(null, new Long[] { categoryId });
	    if (!categoryId.equals(CategoriesEnum.CONTRACTORS.getCode()))
		terminationReasons = TerminationsService.getTerminationReasons((long) mode);
	    else
		terminationReasons = TerminationsService.getTerminationReasons(categoryId);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    byte[] bytes = TerminationsService.getTerminationReportsBytes(categoryId, reportType, reasonId, regionId, unitFullName, rankId, serviceTerminationDateFrom, serviceTerminationDateTo, terminationDueDateFrom, terminationDueDateTo, extensionDateFrom, extensionDateTo, decisionDateFrom, decisionDateTo);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	try {
	    reasonId = null;
	    regionId = getLoginEmpPhysicalRegionFlag(true);
	    categoryId = new Long(mode);
	    rankId = (long) FlagsEnum.ALL.getCode();

	    if (reportType == 1 || reportType == 2 || reportType == 3 || reportType == 4 || reportType == 8) {
		terminationReasons = TerminationsService.getTerminationReasons((long) mode);

		if (mode == CategoryModesEnum.CIVILIANS.getCode()) {
		    categories = CommonService.getPersonsCategories();
		    ranks = CommonService.getRanks(null, new Long[] { categoryId });
		}

		// civilianCategoryChanged();
		resetEmployeeUnit();

		serviceTerminationDateFrom = null;
		serviceTerminationDateTo = HijriDateService.getHijriSysDate();
		extensionDateFrom = null;
		extensionDateTo = HijriDateService.getHijriSysDate();
		decisionDateFrom = null;
		decisionDateTo = HijriDateService.getHijriSysDate();

	    } else if (reportType == 5 || reportType == 6 || reportType == 7) {
		ranks = CommonService.getRanks(null, new Long[] { categoryId });
		terminationDueDateFrom = null;
		terminationDueDateTo = HijriDateService.getHijriSysDate();

		if (mode == CategoryModesEnum.OFFICERS.getCode()) {
		    reasonId = TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode();
		    List<TerminationReason> terminationReasonsTmp = TerminationsService.getTerminationReasons((long) mode);
		    terminationReasons = new ArrayList<TerminationReason>();
		    terminationReasons.add(terminationReasonsTmp.get(0));
		    terminationReasons.add(terminationReasonsTmp.get(1));
		    ranks.remove(0);
		    ranks.remove(0);
		    ranks.remove(ranks.size() - 1);
		} else if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
		    ranks.remove(ranks.size() - 1);
		} else if (mode == CategoryModesEnum.CIVILIANS.getCode()) {
		    List<Category> civilianCategoriesTemp = CommonService.getPersonsCategories();
		    categories = new ArrayList<Category>();
		    for (Category category : civilianCategoriesTemp) {
			if (!category.getId().equals(CategoriesEnum.CONTRACTORS.getCode())) {
			    categories.add(category);
			}
		    }
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public Long getReasonId() {
	return reasonId;
    }

    public void setReasonId(Long reasonId) {
	this.reasonId = reasonId;
    }

    public List<TerminationReason> getTerminationReasons() {
	return terminationReasons;
    }

    public void setTerminationReasons(List<TerminationReason> terminationReasons) {
	this.terminationReasons = terminationReasons;
    }

    public long getRegionId() {
	return regionId;
    }

    public void setRegionId(long regionId) {
	this.regionId = regionId;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }

    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
    }

    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    public List<Category> getCategories() {
	return categories;
    }

    public void setCategories(List<Category> categories) {
	this.categories = categories;
    }

    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public void setRanks(List<Rank> ranks) {
	this.ranks = ranks;
    }

    public Date getServiceTerminationDateFrom() {
	return serviceTerminationDateFrom;
    }

    public void setServiceTerminationDateFrom(Date serviceTerminationDateFrom) {
	this.serviceTerminationDateFrom = serviceTerminationDateFrom;
    }

    public Date getServiceTerminationDateTo() {
	return serviceTerminationDateTo;
    }

    public void setServiceTerminationDateTo(Date serviceTerminationDateTo) {
	this.serviceTerminationDateTo = serviceTerminationDateTo;
    }

    public Date getExtensionDateFrom() {
	return extensionDateFrom;
    }

    public void setExtensionDateFrom(Date extensionDateFrom) {
	this.extensionDateFrom = extensionDateFrom;
    }

    public Date getExtensionDateTo() {
	return extensionDateTo;
    }

    public void setExtensionDateTo(Date extensionDateTo) {
	this.extensionDateTo = extensionDateTo;
    }

    public Date getTerminationDueDateFrom() {
	return terminationDueDateFrom;
    }

    public void setTerminationDueDateFrom(Date terminationDueDateFrom) {
	this.terminationDueDateFrom = terminationDueDateFrom;
    }

    public Date getTerminationDueDateTo() {
	return terminationDueDateTo;
    }

    public void setTerminationDueDateTo(Date terminationDueDateTo) {
	this.terminationDueDateTo = terminationDueDateTo;
    }

    public Date getDecisionDateFrom() {
	return decisionDateFrom;
    }

    public void setDecisionDateFrom(Date decisionDateFrom) {
	this.decisionDateFrom = decisionDateFrom;
    }

    public Date getDecisionDateTo() {
	return decisionDateTo;
    }

    public void setDecisionDateTo(Date decisionDateTo) {
	this.decisionDateTo = decisionDateTo;
    }

}
