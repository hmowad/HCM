package com.code.ui.backings.hcm.employees;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.RankTitle;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.trainings.QualificationLevel;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeesReports")
@ViewScoped
public class EmployeesReports extends BaseBacking implements Serializable {

    private int mode;
    private int reportType = 1; // 1 for recruited employees , 2 for underRecruitment , 3 for soldiersStatistics , 4 for soldiersDetails, 5
    // 6 for navy officers formation
    // 7 FOR QUALIFICATION
    // 8 for service years
    // recruitedSearchData
    private int officialPhysicalFlag = 0; // 0 means official , 1 means physical
    private int officialPhysicalSimilarityFlag = -1; // 1 means both physical and official are the same unit, 0 means that they are different and -1 mean don't care
    private long categoryId = -1;
    private long regionId = -1;
    private long unitId = -1;
    private String unitHKey;
    private String unitFullName;
    private long rankId = -1;
    private long minorSpecializationId = -1;
    private Long[] statusIds;
    private String groupNumber; // First two digits from military number
    private Integer[] photosStatuses;

    private List<Category> personsCategorieslist;

    // common between the two reports 1 and 2 and 5
    private List<Region> regionsList;
    private List<Rank> ranksList;
    private Date recruitmentDateFrom;
    private Date recruitmentDateTo;

    // common between the reports 1 and 5
    private List<RankTitle> rankTitlesList;
    private long rankTitleId;
    private int generalSpecialization;

    // for emps qual
    private long qualMajorSpecId;
    private String qualMajorSpecDesc;
    private long qualMinorSpecId;
    private String qualMinorSpecDesc;
    private long qualLevelId;
    private String graduationPlaceDetailsIds;
    private String graduationPlaceDetailsDescs;
    private int onDutyFlag;
    private int countryFlag;
    private int currentQualFlag;
    private int curRecSimilarityFlag;
    private List<QualificationLevel> qualificationLevels;

    private boolean isAdmin;
    private boolean isManager;
    private String managerUnitHKeyPrefix;

    // report 8
    private Long serviceYearsBegin;
    private Long serviceYearsEnd;

    // report 9
    private int jobModifiedFlag;

    public EmployeesReports() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		regionsList = CommonService.getAllRegions();
		setQualificationLevels(TrainingSetupService.getAllQualificationLevels());
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_officersDataReports"));
		    rankTitlesList = CommonService.getAllRanksTitles();
		    if (SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.EMPS_OFFICER_DATA_REPORTS_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_REPORTS_VIEW_ALL_OFFICERS.getCode()))
			isAdmin = true;
		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersDataReports"));
		    if (SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.EMPS_SOLDIER_DATA_REPORTS_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_REPORTS_VIEW_ALL_SOLDIERS.getCode()))
			isAdmin = true;
		    break;
		case 3:
		    setScreenTitle(getMessage("title_personsDataReports"));
		    personsCategorieslist = CommonService.getPersonsCategories();
		    if (SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.EMPS_CIVILIAN_DATA_REPORTS_VIEW.getCode(), MenuActionsEnum.PRS_EMPS_DATA_REPORTS_VIEW_ALL_CIVILIANS.getCode()))
			isAdmin = true;
		    break;
		default:
		    throw new BusinessException("error_URLError");
		}
		if (loginEmpData.getIsManager() == FlagsEnum.ON.getCode() && !isAdmin)
		    isManager = true;
		resetForm();
	    } else {
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    byte[] bytes = null;
	    if (reportType == 1) {
		bytes = EmployeesService.getEmployeesDataBytes(officialPhysicalFlag, officialPhysicalSimilarityFlag, categoryId, regionId, unitHKey.equals("") ? null : unitHKey, rankId, minorSpecializationId, groupNumber == null || groupNumber.equals("") ? FlagsEnum.ALL.getCode() + "" : groupNumber, Arrays.asList(statusIds), rankTitleId, generalSpecialization, recruitmentDateFrom, recruitmentDateTo);
	    } else if (reportType == 5) {
		bytes = EmployeesService.getEmployeesStatisticsDataBytes(officialPhysicalFlag, officialPhysicalSimilarityFlag, categoryId, regionId, unitId, unitFullName, rankId, minorSpecializationId, groupNumber == null || groupNumber.equals("") ? FlagsEnum.ALL.getCode() + "" : groupNumber, Arrays.asList(statusIds), rankTitleId, generalSpecialization, HijriDateService.getHijriDateString(recruitmentDateFrom), HijriDateService.getHijriDateString(recruitmentDateTo));
	    } else if (reportType == 8) {
		bytes = EmployeesService.getEmployeesServiceYearsDataBytes(categoryId, regionId, unitHKey, onDutyFlag, serviceYearsBegin, serviceYearsEnd);
	    } else if (reportType == 9) {
		bytes = EmployeesService.getJobModifiedFlagDataBytes(categoryId, regionId, unitHKey, jobModifiedFlag);
	    } else if (reportType == 10) {

		if (photosStatuses == null || photosStatuses.length == 0) {
		    this.setServerSideErrorMessages(getMessage("error_photoStatusIsMandatory"));
		    return;
		}

		bytes = EmployeesService.getEmployeesNotFoundOrNotUpdatedPhotosBytes(categoryId, regionId, unitHKey, Arrays.asList(photosStatuses));
	    } else {
		bytes = EmployeesService.getEmployeesQualificationsBytes(countryFlag, currentQualFlag, curRecSimilarityFlag, onDutyFlag, categoryId, regionId, unitId, unitFullName, qualMajorSpecId, qualMajorSpecDesc, qualMinorSpecId, qualMinorSpecDesc, qualLevelId, graduationPlaceDetailsIds, graduationPlaceDetailsDescs);
	    }
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() throws BusinessException {
	if (reportType == 1 || reportType == 5) {
	    officialPhysicalFlag = FlagsEnum.OFF.getCode();
	    officialPhysicalSimilarityFlag = FlagsEnum.ALL.getCode();
	    categoryId = (mode == CategoryModesEnum.CIVILIANS.getCode() ? FlagsEnum.ALL.getCode() : mode);

	    rankId = FlagsEnum.ALL.getCode();
	    minorSpecializationId = FlagsEnum.ALL.getCode();
	    // statusId = FlagsEnum.ALL.getCode();
	    groupNumber = "";
	    rankTitleId = FlagsEnum.ALL.getCode();
	    recruitmentDateFrom = null;
	    recruitmentDateTo = null;
	    generalSpecialization = FlagsEnum.ALL.getCode();
	    if (mode == CategoryModesEnum.OFFICERS.getCode()) {
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY.getCode(),
			EmployeeStatusEnum.ASSIGNED.getCode(),
			EmployeeStatusEnum.SUBJOINED.getCode(),
			EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode(),
			EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode() };
	    } else if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY.getCode(),
			EmployeeStatusEnum.SUBJOINED.getCode(),
			EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode(),
			EmployeeStatusEnum.MANDATED.getCode(),
			EmployeeStatusEnum.SECONDMENTED.getCode() };
	    } else {
		statusIds = new Long[] { EmployeeStatusEnum.ON_DUTY.getCode(),
			EmployeeStatusEnum.PERSONS_SUBJOINED.getCode(),
			EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode(),
		};
	    }

	}
	if (reportType == 7) {
	    categoryId = (mode == CategoryModesEnum.CIVILIANS.getCode() ? FlagsEnum.ALL.getCode() : mode);

	    qualMajorSpecId = FlagsEnum.ALL.getCode();
	    qualMinorSpecId = FlagsEnum.ALL.getCode();
	    qualMinorSpecDesc = "";
	    qualMajorSpecDesc = "";
	    qualLevelId = FlagsEnum.ALL.getCode();
	    graduationPlaceDetailsIds = "";
	    graduationPlaceDetailsDescs = "";
	    onDutyFlag = FlagsEnum.ON.getCode();
	    countryFlag = FlagsEnum.ALL.getCode();
	    currentQualFlag = FlagsEnum.ALL.getCode();
	    curRecSimilarityFlag = FlagsEnum.ALL.getCode();
	}
	if (reportType == 8) {
	    categoryId = (mode == CategoryModesEnum.CIVILIANS.getCode() ? FlagsEnum.ALL.getCode() : mode);
	    regionId = FlagsEnum.ALL.getCode();
	    unitHKey = "";
	    onDutyFlag = FlagsEnum.ALL.getCode();
	    serviceYearsBegin = null;
	    serviceYearsEnd = null;
	}
	if (reportType == 9) {
	    regionId = FlagsEnum.ALL.getCode();
	    unitHKey = "";
	    jobModifiedFlag = FlagsEnum.ALL.getCode();
	}
	if (reportType == 10) {
	    regionId = FlagsEnum.ALL.getCode();
	    unitHKey = "";
	    photosStatuses = null;
	}
	regionId = getLoginEmpPhysicalRegionFlag(isAdmin);
	regionChangeListener();
	if (ranksList == null || mode == CategoryModesEnum.CIVILIANS.getCode() || mode == CategoriesEnum.SOLDIERS.getCode()) {
	    ranksList = CommonService.getRanks(null, (Long[]) getCategoriesIdsArrayByMode(mode));
	    prepareRanksList();
	}
    }

    public void regionChangeListener() throws BusinessException {
	if (isAdmin) {

	    if (reportType == 5) {
		UnitData unit = null;
		if (regionId == FlagsEnum.ALL.getCode() || regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
		    unit = UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0);
		} else {
		    unit = UnitsService.getUnitsByTypeAndRegion(UnitTypesEnum.REGION_COMMANDER.getCode(), regionId).get(0);
		}
		unitId = unit.getId();
		unitFullName = unit.getFullName();
		unitHKey = unit.gethKey();
		managerUnitHKeyPrefix = ""; // not used
	    } else {
		unitId = FlagsEnum.ALL.getCode();
		unitFullName = "";
		unitHKey = "";
		managerUnitHKeyPrefix = "";

	    }
	} else if (isManager) {
	    unitId = loginEmpData.getPhysicalUnitId();
	    unitFullName = loginEmpData.getPhysicalUnitFullName();
	    unitHKey = loginEmpData.getPhysicalUnitHKey();
	    managerUnitHKeyPrefix = UnitsService.getHKeyPrefix(unitHKey);

	}
    }

    private void prepareRanksList() {
	if (reportType == 1) {
	    if (mode == CategoryModesEnum.OFFICERS.getCode()) {
		for (int i = 0; i < ranksList.size(); i++) {
		    if (ranksList.get(i).getId().equals(RanksEnum.CADET.getCode())) {
			ranksList.remove(i);
			break;
		    }
		}
	    } else if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
		for (int i = 0; i < ranksList.size(); i++) {
		    if (ranksList.get(i).getId().equals(RanksEnum.STUDENT_SOLDIER.getCode())) {
			ranksList.remove(i);
			break;
		    }
		}
	    }
	}
    }

    public void categoryChangedListener() {
	try {
	    ranksList = CommonService.getRanks(null, (Long[]) (categoryId == FlagsEnum.ALL.getCode() ? getCategoriesIdsArrayByMode(mode) : new Long[] { categoryId }));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public int getOfficialPhysicalFlag() {
	return officialPhysicalFlag;
    }

    public void setOfficialPhysicalFlag(int officialPhysicalFlag) {
	this.officialPhysicalFlag = officialPhysicalFlag;
    }

    public int getOfficialPhysicalSimilarityFlag() {
	return officialPhysicalSimilarityFlag;
    }

    public void setOfficialPhysicalSimilarityFlag(int officialPhysicalSimilarityFlag) {
	this.officialPhysicalSimilarityFlag = officialPhysicalSimilarityFlag;
    }

    public long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(long categoryId) {
	this.categoryId = categoryId;
    }

    public long getRegionId() {
	return regionId;
    }

    public void setRegionId(long regionId) {
	this.regionId = regionId;
    }

    public long getUnitId() {
	return unitId;
    }

    public void setUnitId(long unitId) {
	this.unitId = unitId;
    }

    public long getRankId() {
	return rankId;
    }

    public void setRankId(long rankId) {
	this.rankId = rankId;
    }

    public long getMinorSpecializationId() {
	return minorSpecializationId;
    }

    public void setMinorSpecializationId(long minorSpecializationId) {
	this.minorSpecializationId = minorSpecializationId;
    }

    public List<Region> getRegionsList() {
	return regionsList;
    }

    public void setRegionsList(List<Region> regionsList) {
	this.regionsList = regionsList;
    }

    public List<Rank> getRanksList() {
	return ranksList;
    }

    public void setRanksList(List<Rank> ranksList) {
	this.ranksList = ranksList;
    }

    public Date getRecruitmentDateFrom() {
	return recruitmentDateFrom;
    }

    public void setRecruitmentDateFrom(Date recruitmentDateFrom) {
	this.recruitmentDateFrom = recruitmentDateFrom;
    }

    public Date getRecruitmentDateTo() {
	return recruitmentDateTo;
    }

    public void setRecruitmentDateTo(Date recruitmentDateTo) {
	this.recruitmentDateTo = recruitmentDateTo;
    }

    public List<Category> getPersonsCategorieslist() {
	return personsCategorieslist;
    }

    public void setPersonsCategorieslist(List<Category> personsCategorieslist) {
	this.personsCategorieslist = personsCategorieslist;
    }

    public String getUnitHKey() {
	return unitHKey;
    }

    public void setUnitHKey(String unitHKey) {
	this.unitHKey = unitHKey;
    }

    public String getUnitFullName() {
	return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
	this.unitFullName = unitFullName;
    }

    public String getGroupNumber() {
	return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
	this.groupNumber = groupNumber;
    }

    public Integer[] getPhotosStatuses() {
	return photosStatuses;
    }

    public void setPhotosStatuses(Integer[] photosStatuses) {
	this.photosStatuses = photosStatuses;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public long getRankTitleId() {
	return rankTitleId;
    }

    public void setRankTitleId(long rankTitleId) {
	this.rankTitleId = rankTitleId;
    }

    public List<RankTitle> getRankTitlesList() {
	return rankTitlesList;
    }

    public Long[] getStatusIds() {
	return statusIds;
    }

    public void setStatusIds(Long[] statusIds) {
	this.statusIds = statusIds;
    }

    public long getQualMajorSpecId() {
	return qualMajorSpecId;
    }

    public void setQualMajorSpecId(long qualMajorSpecId) {
	this.qualMajorSpecId = qualMajorSpecId;
    }

    public long getQualMinorSpecId() {
	return qualMinorSpecId;
    }

    public void setQualMinorSpecId(long qualMinorSpecId) {
	this.qualMinorSpecId = qualMinorSpecId;
    }

    public long getQualLevelId() {
	return qualLevelId;
    }

    public void setQualLevelId(long qualLevelId) {
	this.qualLevelId = qualLevelId;
    }

    public int getOnDutyFlag() {
	return onDutyFlag;
    }

    public void setOnDutyFlag(int onDutyFlag) {
	this.onDutyFlag = onDutyFlag;
    }

    public int getCurrentQualFlag() {
	return currentQualFlag;
    }

    public void setCurrentQualFlag(int currentQualFlag) {
	this.currentQualFlag = currentQualFlag;
    }

    public int getCurRecSimilarityFlag() {
	return curRecSimilarityFlag;
    }

    public void setCurRecSimilarityFlag(int curRecSimilarityFlag) {
	this.curRecSimilarityFlag = curRecSimilarityFlag;
    }

    public void setRankTitlesList(List<RankTitle> rankTitlesList) {
	this.rankTitlesList = rankTitlesList;
    }

    public String getGraduationPlaceDetailsDescs() {
	return graduationPlaceDetailsDescs;
    }

    public void setGraduationPlaceDetailsDescs(String graduationPlaceDetailsDescs) {
	this.graduationPlaceDetailsDescs = graduationPlaceDetailsDescs;
    }

    public List<QualificationLevel> getQualificationLevels() {
	return qualificationLevels;
    }

    public void setQualificationLevels(List<QualificationLevel> qualificationLevels) {
	this.qualificationLevels = qualificationLevels;
    }

    public String getQualMinorSpecDesc() {
	return qualMinorSpecDesc;
    }

    public void setQualMinorSpecDesc(String qualMinorSpecDesc) {
	this.qualMinorSpecDesc = qualMinorSpecDesc;
    }

    public String getQualMajorSpecDesc() {
	return qualMajorSpecDesc;
    }

    public void setQualMajorSpecDesc(String qualMajorSpecDesc) {
	this.qualMajorSpecDesc = qualMajorSpecDesc;
    }

    public boolean isAdmin() {
	return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
	this.isAdmin = isAdmin;
    }

    public boolean isManager() {
	return isManager;
    }

    public void setManager(boolean isManager) {
	this.isManager = isManager;
    }

    public String getManagerUnitHKeyPrefix() {
	return managerUnitHKeyPrefix;
    }

    public void setManagerUnitHKeyPrefix(String managerUnitHKeyPrefix) {
	this.managerUnitHKeyPrefix = managerUnitHKeyPrefix;
    }

    public int getGeneralSpecialization() {
	return generalSpecialization;
    }

    public void setGeneralSpecialization(int generalSpecialization) {
	this.generalSpecialization = generalSpecialization;
    }

    public int getCountryFlag() {
	return countryFlag;
    }

    public void setCountryFlag(int countryFlag) {
	this.countryFlag = countryFlag;
    }

    public String getGraduationPlaceDetailsIds() {
	return graduationPlaceDetailsIds;
    }

    public void setGraduationPlaceDetailsIds(String graduationPlaceDetailsIds) {
	this.graduationPlaceDetailsIds = graduationPlaceDetailsIds;
    }

    public Long getServiceYearsBegin() {
	return serviceYearsBegin;
    }

    public void setServiceYearsBegin(Long serviceYearsBegin) {
	this.serviceYearsBegin = serviceYearsBegin;
    }

    public Long getServiceYearsEnd() {
	return serviceYearsEnd;
    }

    public void setServiceYearsEnd(Long serviceYearsEnd) {
	this.serviceYearsEnd = serviceYearsEnd;
    }

    public int getJobModifiedFlag() {
	return jobModifiedFlag;
    }

    public void setJobModifiedFlag(int jobModifiedFlag) {
	this.jobModifiedFlag = jobModifiedFlag;
    }

}
