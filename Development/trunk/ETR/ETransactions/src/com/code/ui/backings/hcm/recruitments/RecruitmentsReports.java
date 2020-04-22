package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.GraduationGroupPlace;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.Region;
import com.code.enums.CategoriesEnum;
import com.code.enums.CategoryModesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.RanksEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.RecruitmentsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "recruitmentsReports")
@ViewScoped
public class RecruitmentsReports extends BaseBacking implements Serializable {

    private int mode;
    private int reportType = 2; // 1 for recruited employees , 2 for underRecruitment , 3 for soldiersStatistics , 4 for soldiersDetails, 5
    // 6 for navy officers formation
    private Date sysDate;

    // Under Recruitment search data
    private long graduationGroupPLace;
    private Long[] underRecruitmentStatusIds;
    private int distributionStatus;
    private long recruitmentTrainingUnitId;
    private Long graduationGroupNumber; // Number that comes from college

    private List<GraduationGroupPlace> officersGraduationGroupPlaces;

    // common between the two reports 1 and 2 and 5
    private List<Region> regionsList;
    private List<Rank> ranksList;

    // soldiersStatisics
    private Date recruitmentDateFrom;
    private Date recruitmentDateTo;
    private int recruitmentType; // 1 for recruitment In Traning Unit, 2 for recruitment or graduation letter

    // common between the reports 2 and 3 and 4

    private long recruitmentRegionId;
    private long recruitmentRankId;
    private long recruitmentMinorSpecId;

    public RecruitmentsReports() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		regionsList = CommonService.getAllRegions();
		sysDate = HijriDateService.getHijriSysDate();
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_officersRecruitmentsReports"));
		    officersGraduationGroupPlaces = CommonService.getAllGraduationGroupPlaces();

		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersRecruitmentsReports"));
		    break;

		default:
		    setServerSideErrorMessages(getMessage("error_URLError"));
		}

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
	    if (reportType == 2) {
		if (mode == CategoryModesEnum.OFFICERS.getCode()) {
		    bytes = RecruitmentsService.getOfficersUnderRecDataBytes(graduationGroupNumber == null ? FlagsEnum.ALL.getCode() : graduationGroupNumber.longValue(), graduationGroupPLace, recruitmentRegionId);
		} else {
		    bytes = RecruitmentsService.getSoldiersUnderRecAndDistribDataBytes(Arrays.asList(underRecruitmentStatusIds), recruitmentRankId, recruitmentMinorSpecId, recruitmentRegionId, recruitmentTrainingUnitId, distributionStatus);
		}
	    } else if (reportType == 3) {
		bytes = RecruitmentsService.getRecruitedSoldiersStatisticalDataBytes(recruitmentRankId, recruitmentMinorSpecId, recruitmentRegionId, HijriDateService.getHijriDateString(recruitmentDateFrom), HijriDateService.getHijriDateString(recruitmentDateTo), recruitmentType);
	    } else if (reportType == 4) {
		bytes = RecruitmentsService.getRecruitedSoldiersDetailedDataBytes(recruitmentRankId, recruitmentMinorSpecId, recruitmentRegionId, HijriDateService.getHijriDateString(recruitmentDateFrom), HijriDateService.getHijriDateString(recruitmentDateTo), recruitmentType);
	    }
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() throws BusinessException {
	if (reportType == 2) {
	    graduationGroupPLace = FlagsEnum.ALL.getCode();
	    recruitmentMinorSpecId = FlagsEnum.ALL.getCode();
	    recruitmentRankId = FlagsEnum.ALL.getCode();
	    recruitmentRegionId = FlagsEnum.ALL.getCode();
	    recruitmentTrainingUnitId = FlagsEnum.ALL.getCode();
	    graduationGroupNumber = null;
	    distributionStatus = FlagsEnum.ALL.getCode();
	    underRecruitmentStatusIds = new Long[] { EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode(), EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode(), EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode() };

	} else if (reportType == 3 || reportType == 4) {
	    recruitmentMinorSpecId = FlagsEnum.ALL.getCode();
	    recruitmentRankId = FlagsEnum.ALL.getCode();
	    recruitmentRegionId = FlagsEnum.ALL.getCode();
	    recruitmentDateFrom = recruitmentDateTo = sysDate;
	    recruitmentType = 1;

	}

	if (mode == CategoriesEnum.SOLDIERS.getCode()) {
	    ranksList = CommonService.getRanks(null, (Long[]) getCategoriesIdsArrayByMode(mode));
	    prepareRanksList();
	}
    }

    private void prepareRanksList() {
	if (reportType == 3 || reportType == 4) {
	    if (recruitmentType == 1) {
		ranksList = new ArrayList<Rank>();
		ranksList.add(CommonService.getRankById(RanksEnum.SOLDIER.getCode()));
		ranksList.add(CommonService.getRankById(RanksEnum.FIRST_SOLDIER.getCode()));
	    } else {
		for (int i = 0; i < ranksList.size(); i++) {
		    if (ranksList.get(i).getId().equals(RanksEnum.STUDENT_SOLDIER.getCode())) {
			ranksList.remove(i);
			break;
		    }
		}
	    }
	}
    }

    public void recruitmentTypeChangeListner() {

	try {
	    if (recruitmentType == 2)
		ranksList = CommonService.getRanks(null, (Long[]) getCategoriesIdsArrayByMode(mode));
	    prepareRanksList();

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

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public long getGraduationGroupPLace() {
	return graduationGroupPLace;
    }

    public void setGraduationGroupPLace(long graduationGroupPLace) {
	this.graduationGroupPLace = graduationGroupPLace;
    }

    public long getRecruitmentRegionId() {
	return recruitmentRegionId;
    }

    public void setRecruitmentRegionId(long recruitmentRegionId) {
	this.recruitmentRegionId = recruitmentRegionId;
    }

    public Long[] getUnderRecruitmentStatusIds() {
	return underRecruitmentStatusIds;
    }

    public void setUnderRecruitmentStatusIds(Long[] underRecruitmentStatusIds) {
	this.underRecruitmentStatusIds = underRecruitmentStatusIds;
    }

    public long getRecruitmentRankId() {
	return recruitmentRankId;
    }

    public void setRecruitmentRankId(long recruitmentRankId) {
	this.recruitmentRankId = recruitmentRankId;
    }

    public long getRecruitmentMinorSpecId() {
	return recruitmentMinorSpecId;
    }

    public void setRecruitmentMinorSpecId(long recruitmentMinorSpecId) {
	this.recruitmentMinorSpecId = recruitmentMinorSpecId;
    }

    public long getRecruitmentTrainingUnitId() {
	return recruitmentTrainingUnitId;
    }

    public void setRecruitmentTrainingUnitId(long recruitmentTrainingUnitId) {
	this.recruitmentTrainingUnitId = recruitmentTrainingUnitId;
    }

    public Long getGraduationGroupNumber() {
	return graduationGroupNumber;
    }

    public void setGraduationGroupNumber(Long graduationGroupNumber) {
	this.graduationGroupNumber = graduationGroupNumber;
    }

    public List<GraduationGroupPlace> getOfficersGraduationGroupPlaces() {
	return officersGraduationGroupPlaces;
    }

    public void setOfficersGraduationGroupPlaces(List<GraduationGroupPlace> officersGraduationGroupPlaces) {
	this.officersGraduationGroupPlaces = officersGraduationGroupPlaces;
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

    public int getRecruitmentType() {
	return recruitmentType;
    }

    public void setRecruitmentType(int recruitmentType) {
	this.recruitmentType = recruitmentType;
    }

    public int getDistributionStatus() {
	return distributionStatus;
    }

    public void setDistributionStatus(int distributionStatus) {
	this.distributionStatus = distributionStatus;
    }

}
