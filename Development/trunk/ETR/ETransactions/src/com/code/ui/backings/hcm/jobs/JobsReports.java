package com.code.ui.backings.hcm.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.enums.JobStatusEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.JobsService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "jobsReports")
@ViewScoped
public class JobsReports extends BaseBacking implements Serializable {

    private int mode;
    private int reportType;

    private List<Region> regionsList;
    private long selectedRegionId;

    private long selectedUnitId;
    private String selectedUnitFullName;
    private String selectedUnitHKey;

    private List<Category> categories;
    private long civiliansCategoryId;

    private List<Rank> ranks;
    private long rankId;

    private boolean jobStatusVacant;
    private boolean jobStatusOccupied;
    private boolean jobStatusFrozen;
    private boolean jobStatusCanceled;
    private int jobReservationStatus;

    private Date fromDate;
    private Date toDate;
    private long jobTransactionType;

    private int generalSpec;
    private int generalType;
    private int approvedFlag;
    private int minorSpec;
    private String minorSpecDesc;
    private String jobName;

    private boolean recruitmentsFlag;
    private boolean movementsFlag;
    private boolean promotionsFlag;

    private boolean showJobsInTransactionsReports = false;

    public JobsReports() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_jobsOfficersReports"));
		    showJobsInTransactionsReports = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.JOBS_REPORTS_FOR_OFFICERS.getCode(), MenuActionsEnum.JOBS_REPORTS_SHOW_JOBS_IN_TRANSACTIONS_REPORTS.getCode());
		    break;
		case 2:
		    setScreenTitle(getMessage("title_jobsSoldiersReports"));
		    showJobsInTransactionsReports = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.JOBS_REPORTS_FOR_SOLDIERS.getCode(), MenuActionsEnum.JOBS_REPORTS_SHOW_JOBS_IN_TRANSACTIONS_REPORTS.getCode());
		    break;
		case 3:
		    setScreenTitle(getMessage("title_jobsPersonsReports"));
		    showJobsInTransactionsReports = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.JOBS_REPORTS_FOR_CIVILIANS.getCode(), MenuActionsEnum.JOBS_REPORTS_SHOW_JOBS_IN_TRANSACTIONS_REPORTS.getCode());
		    break;
		default:
		    setServerSideErrorMessages(getMessage("error_URLError"));
		}
	    } else {
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }

	    reportType = 10;
	    regionsList = CommonService.getAllRegions();

	    if (mode != 3) {
		ranks = CommonService.getRanks(null, getCategoriesIdsArrayByMode(mode));
		ranks.remove(ranks.size() - 1);
	    } else {
		categories = CommonService.getPersonsCategories();
	    }

	    resetForm();
	} catch (BusinessException e) {
	    ranks = new ArrayList<Rank>();
	    categories = new ArrayList<Category>();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	jobReservationStatus = FlagsEnum.ALL.getCode();
	jobTransactionType = FlagsEnum.ALL.getCode();
	selectedRegionId = getLoginEmpPhysicalRegionFlag(true);
	civiliansCategoryId = FlagsEnum.ALL.getCode();
	rankId = FlagsEnum.ALL.getCode();

	jobStatusVacant = true;
	jobStatusOccupied = true;
	jobStatusFrozen = false;
	jobStatusCanceled = false;

	generalSpec = FlagsEnum.ALL.getCode();
	generalType = FlagsEnum.ALL.getCode();
	approvedFlag = FlagsEnum.ALL.getCode();
	minorSpec = FlagsEnum.ALL.getCode();
	minorSpecDesc = getMessage("label_all");
	jobName = "";

	recruitmentsFlag = true;
	movementsFlag = true;
	promotionsFlag = true;

	try {
	    resetUnits(null);
	    fromDate = toDate = HijriDateService.getHijriSysDate();
	    if (mode == 3)
		ranks = CommonService.getRanks(null, getCategoriesIdsArrayByMode(mode));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetUnits(AjaxBehaviorEvent event) {
	try {
	    UnitData unit = null;
	    if (selectedRegionId == FlagsEnum.ALL.getCode() || selectedRegionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
		unit = UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0);
	    } else {
		unit = UnitsService.getUnitsByTypeAndRegion(UnitTypesEnum.REGION_COMMANDER.getCode(), selectedRegionId).get(0);
	    }

	    selectedUnitFullName = unit.getFullName();
	    selectedUnitHKey = unit.gethKey();
	    selectedUnitId = unit.getId();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    String reportTitle = "";
	    String selectedRegionDesc = getMessage("label_all");
	    String jobsStatusString = "";
	    String jobStatusDesc = "";
	    String generalSpecDesc = getMessage("label_all");
	    String generalTypeDesc = getMessage("label_all");
	    String approvedFlagDesc = getMessage("label_all");

	    if (reportType == 10) {
		reportTitle = getMessage("label_jobsDetails") + " " + (mode == 1 ? getMessage("label_forOfficers") : (mode == 2 ? getMessage("label_forSoldiers") : getMessage("label_forPersons")));
	    } else if (reportType == 20) {
		reportTitle = getMessage("label_jobTransactionDetails") + " " + (mode == 1 ? getMessage("label_forOfficers") : (mode == 2 ? getMessage("label_forSoldiers") : getMessage("label_forPersons")));
	    } else if (reportType == 30) {
		reportTitle = getMessage("label_jobsStatisticsReport") + " " + (mode == 1 ? getMessage("label_forOfficers") : (mode == 2 ? getMessage("label_forSoldiers") : getMessage("label_forCivilians")));

		if (selectedRegionId != FlagsEnum.ALL.getCode())
		    selectedRegionDesc = CommonService.getRegionById(selectedRegionId).getDescription();

		switch (generalSpec) {
		case 1:
		    generalSpecDesc = getMessage("label_overland");
		    break;
		case 2:
		    generalSpecDesc = getMessage("label_naval");
		    break;
		case 3:
		    generalSpecDesc = getMessage("label_aerial");
		    break;
		default:
		    generalSpecDesc = getMessage("label_all");
		}

		switch (generalType) {
		case 1:
		    generalTypeDesc = getMessage("label_jobTypeNormal");
		    break;
		case 2:
		    generalTypeDesc = getMessage("label_jobTypeTechnical");
		    break;
		case 3:
		    generalTypeDesc = getMessage("label_jobTypeField");
		    break;
		default:
		    generalTypeDesc = getMessage("label_all");
		}

		switch (approvedFlag) {
		case 0:
		    approvedFlagDesc = getMessage("label_notApproved");
		    break;
		case 1:
		    approvedFlagDesc = getMessage("label_approved");
		    break;
		default:
		    approvedFlagDesc = getMessage("label_all");
		}
	    } else if (reportType == 40) {
		reportTitle = getMessage("label_jobsInTransactions") + " " + (mode == 1 ? getMessage("label_forOfficers") : (mode == 2 ? getMessage("label_forSoldiers") : getMessage("label_forPersons")));
		if (selectedRegionId != FlagsEnum.ALL.getCode())
		    selectedRegionDesc = CommonService.getRegionById(selectedRegionId).getDescription();

	    } else if (reportType == 50) {
		reportTitle = getMessage("label_jobsInTransactionsStatistics");
		if (selectedRegionId != FlagsEnum.ALL.getCode())
		    selectedRegionDesc = CommonService.getRegionById(selectedRegionId).getDescription();
	    } else if (reportType == 60) {
		if (selectedRegionId != FlagsEnum.ALL.getCode())
		    selectedRegionDesc = CommonService.getRegionById(selectedRegionId).getDescription();
	    }

	    if (reportType == 10 || reportType == 30) {

		if (!jobStatusVacant && !jobStatusOccupied && !jobStatusFrozen && !jobStatusCanceled) {
		    this.setServerSideErrorMessages(getMessage("error_jobStatusMandatory"));
		    return;
		}

		jobsStatusString = (jobStatusVacant ? (JobStatusEnum.VACANT.getCode() + ",") : "") +
			(jobStatusOccupied ? (JobStatusEnum.OCCUPIED.getCode() + ",") : "") +
			(jobStatusFrozen ? (JobStatusEnum.FROZEN.getCode() + ",") : "") +
			(jobStatusCanceled ? JobStatusEnum.CANCELED.getCode() : "");
		if (jobsStatusString.charAt(jobsStatusString.length() - 1) == ',')
		    jobsStatusString = jobsStatusString.substring(0, jobsStatusString.length() - 1);

		jobStatusDesc = (jobStatusVacant ? (getMessage("label_vacant") + ",") : "") +
			(jobStatusOccupied ? (getMessage("label_occupied") + ",") : "") +
			(jobStatusFrozen ? (getMessage("label_frozen") + ",") : "") +
			(jobStatusCanceled ? getMessage("label_canceled") : "");
		if (jobStatusDesc.charAt(jobStatusDesc.length() - 1) == ',')
		    jobStatusDesc = jobStatusDesc.substring(0, jobStatusDesc.length() - 1);
	    }

	    byte[] bytes = JobsService.getJobsReportsBytes(reportType, mode, selectedRegionId, selectedRegionDesc, selectedUnitId, selectedUnitHKey, selectedUnitFullName, jobName, jobsStatusString, jobStatusDesc, jobReservationStatus, fromDate, toDate, jobTransactionType, generalSpec, generalSpecDesc, generalType,
		    generalTypeDesc, approvedFlag, approvedFlagDesc, minorSpec, minorSpecDesc, civiliansCategoryId, rankId, recruitmentsFlag, movementsFlag, promotionsFlag, reportTitle);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void categoryChangedListener() {
	try {
	    ranks = CommonService.getRanks(null, (Long[]) (civiliansCategoryId == FlagsEnum.ALL.getCode() ? getCategoriesIdsArrayByMode(mode) : new Long[] { civiliansCategoryId }));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String getSelectedUnitFullName() {
	return selectedUnitFullName;
    }

    public void setSelectedUnitFullName(String selectedUnitFullName) {
	this.selectedUnitFullName = selectedUnitFullName;
    }

    public String getSelectedUnitHKey() {
	return selectedUnitHKey;
    }

    public void setSelectedUnitHKey(String selectedUnitHKey) {
	this.selectedUnitHKey = selectedUnitHKey;
    }

    public long getSelectedUnitId() {
	return selectedUnitId;
    }

    public void setSelectedUnitId(long selectedUnitId) {
	this.selectedUnitId = selectedUnitId;
    }

    public List<Category> getCategories() {
	return categories;
    }

    public void setCategories(List<Category> categories) {
	this.categories = categories;
    }

    public long getCiviliansCategoryId() {
	return civiliansCategoryId;
    }

    public void setCiviliansCategoryId(long civiliansCategoryId) {
	this.civiliansCategoryId = civiliansCategoryId;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public void setRanks(List<Rank> ranks) {
	this.ranks = ranks;
    }

    public long getRankId() {
	return rankId;
    }

    public void setRankId(long rankId) {
	this.rankId = rankId;
    }

    public int getJobReservationStatus() {
	return jobReservationStatus;
    }

    public void setJobReservationStatus(int jobReservationStatus) {
	this.jobReservationStatus = jobReservationStatus;
    }

    public long getSelectedRegionId() {
	return selectedRegionId;
    }

    public void setSelectedRegionId(long selectedRegionId) {
	this.selectedRegionId = selectedRegionId;
    }

    public List<Region> getRegionsList() {
	return regionsList;
    }

    public void setRegionsList(List<Region> regionsList) {
	this.regionsList = regionsList;
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

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    public long getJobTransactionType() {
	return jobTransactionType;
    }

    public void setJobTransactionType(long jobTransactionType) {
	this.jobTransactionType = jobTransactionType;
    }

    public int getGeneralSpec() {
	return generalSpec;
    }

    public void setGeneralSpec(int generalSpec) {
	this.generalSpec = generalSpec;
    }

    public int getGeneralType() {
	return generalType;
    }

    public void setGeneralType(int generalType) {
	this.generalType = generalType;
    }

    public int getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(int approvedFlag) {
	this.approvedFlag = approvedFlag;
    }

    public int getMinorSpec() {
	return minorSpec;
    }

    public void setMinorSpec(int minorSpec) {
	this.minorSpec = minorSpec;
    }

    public String getMinorSpecDesc() {
	return minorSpecDesc;
    }

    public void setMinorSpecDesc(String minorSpecDesc) {
	this.minorSpecDesc = minorSpecDesc;
    }

    public boolean isJobStatusVacant() {
	return jobStatusVacant;
    }

    public void setJobStatusVacant(boolean jobStatusVacant) {
	this.jobStatusVacant = jobStatusVacant;
    }

    public boolean isJobStatusOccupied() {
	return jobStatusOccupied;
    }

    public void setJobStatusOccupied(boolean jobStatusOccupied) {
	this.jobStatusOccupied = jobStatusOccupied;
    }

    public boolean isJobStatusFrozen() {
	return jobStatusFrozen;
    }

    public void setJobStatusFrozen(boolean jobStatusFrozen) {
	this.jobStatusFrozen = jobStatusFrozen;
    }

    public boolean isJobStatusCanceled() {
	return jobStatusCanceled;
    }

    public void setJobStatusCanceled(boolean jobStatusCanceled) {
	this.jobStatusCanceled = jobStatusCanceled;
    }

    public String getJobName() {
	return jobName;
    }

    public void setJobName(String jobName) {
	this.jobName = jobName;
    }

    public boolean isRecruitmentsFlag() {
	return recruitmentsFlag;
    }

    public void setRecruitmentsFlag(boolean recruitmentsFlag) {
	this.recruitmentsFlag = recruitmentsFlag;
    }

    public boolean isMovementsFlag() {
	return movementsFlag;
    }

    public void setMovementsFlag(boolean movementsFlag) {
	this.movementsFlag = movementsFlag;
    }

    public boolean isPromotionsFlag() {
	return promotionsFlag;
    }

    public void setPromotionsFlag(boolean promotionsFlag) {
	this.promotionsFlag = promotionsFlag;
    }

    public boolean getShowJobsInTransactionsReports() {
	return showJobsInTransactionsReports;
    }

    public void setShowJobsInTransactionsReports(boolean showJobsInTransactionsReports) {
	this.showJobsInTransactionsReports = showJobsInTransactionsReports;
    }

}
