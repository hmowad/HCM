package com.code.ui.backings.hcm.trainings;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TrainingCourseEventStatusEnum;
import com.code.enums.TrainingTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingCoursesEventsService;
import com.code.services.hcm.TrainingEmployeesService;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "trainingReports")
@ViewScoped
public class TrainingReports extends BaseBacking {

    private int reportType = 1; // 1- Cancelled Trainees Courses, 2- Training transactions reports, 3- Training plan candidates, 4- Training plan candidates statistics

    // For report type = 1
    private long empId;

    // For report type = 2
    private String regionDesc;
    private long qualificationMinorSpecId;
    private String qualificationMinorSpecDesc;
    private long qualificationMajorSpecId;
    private String qualificationMajorSpecDesc;
    private String empName;
    private int[] checkedTrainingTypes = new int[] { 1, 2, 3, 4, 5, 6 };
    private String unitName;
    private int traineeSuccessFlag = FlagsEnum.ALL.getCode();
    private int traineeStatus = FlagsEnum.ALL.getCode();
    private long categoryId = FlagsEnum.ALL.getCode();

    // For report type = 3
    private long courseEventId;
    private String courseEventName;

    private Date fromDate; // Report type 1,4
    private Date toDate; // Report type 1,4
    private List<TrainingYear> trainingYears; // Report type 3,4
    private List<TrainingUnitData> trainingUnits; // Report type 3,4
    private long trainingYearId; // // Report type 3,4
    private List<Region> regions; // Report type 2,3
    private long regionId; // Report type 2,3
    private long unitId; // Report type 2,3,4
    private long trainingCourseId; // Report type 2,4
    private String trainingCourseName; // Report type 2,4
    private int[] checkedcourseEventStatuses;// Report Type 4
    private boolean noTrainingUnitsflag = false;
    private int requestStatus;

    public TrainingReports() {
	reset();
    }

    public void reset() {
	try {
	    noTrainingUnitsflag = false;
	    if (reportType == 1) {
		Date sysDate;
		sysDate = HijriDateService.getHijriSysDate();
		fromDate = HijriDateService.addSubHijriMonthsDays(sysDate, -6, 0);
		toDate = sysDate;
		empId = FlagsEnum.ALL.getCode();
	    } else if (reportType == 2) {
		regions = CommonService.getAllRegions();
		unitId = qualificationMajorSpecId = qualificationMinorSpecId = empId = trainingCourseId = FlagsEnum.ALL.getCode();
		fromDate = toDate = null;
		regionId = (loginEmpData.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? FlagsEnum.ALL.getCode() : loginEmpData.getPhysicalRegionId());
		if (regionId != FlagsEnum.ALL.getCode())
		    regionDesc = CommonService.getRegionById(regionId).getDescription();
		traineeSuccessFlag = FlagsEnum.ALL.getCode();
		traineeStatus = FlagsEnum.ALL.getCode();
		categoryId = FlagsEnum.ALL.getCode();
		empName = unitName = trainingCourseName = qualificationMajorSpecDesc = qualificationMinorSpecDesc = "";

	    } else if (reportType == 3) {
		regions = CommonService.getAllRegions();
		trainingYears = TrainingSetupService.getAllTrainingYears();
		trainingUnits = TrainingSetupService.getAllTrainingUnitsData();

		trainingYearId = trainingYears.get(0).getId();
		unitId = courseEventId = regionId = FlagsEnum.ALL.getCode();
		courseEventName = null;
	    } else if (reportType == 4) {
		regionId = (loginEmpData.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? FlagsEnum.ALL.getCode() : loginEmpData.getPhysicalRegionId());
		trainingUnits = TrainingSetupService.getTrainingUnitsByRegionId(regionId);
		if (trainingUnits.size() == 0) {
		    noTrainingUnitsflag = true;
		} else {
		    trainingYears = TrainingSetupService.getAllTrainingYears();
		    trainingYearId = trainingYears.get(0).getId();
		    unitId = trainingUnits.get(0).getUnitId();
		    trainingCourseId = FlagsEnum.ALL.getCode();
		    checkedcourseEventStatuses = new int[] { TrainingCourseEventStatusEnum.COURSE_EVENT_HOLDING_DECISION_ISSUED.getCode(), TrainingCourseEventStatusEnum.COURSE_EVENT_HELD.getCode() };
		    trainingCourseName = "";
		    fromDate = toDate = null;
		}

	    } else if (reportType == 5) {
		regions = CommonService.getAllRegions();
		unitId = empId = trainingCourseId = FlagsEnum.ALL.getCode();
		fromDate = toDate = null;
		regionId = (loginEmpData.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? FlagsEnum.ALL.getCode() : loginEmpData.getPhysicalRegionId());
		if (regionId != FlagsEnum.ALL.getCode())
		    regionDesc = CommonService.getRegionById(regionId).getDescription();
		requestStatus = FlagsEnum.ALL.getCode();
		categoryId = FlagsEnum.ALL.getCode();
		empName = unitName = trainingCourseName = "";
		requestStatus = FlagsEnum.OFF.getCode();
	    }
	} catch (BusinessException e) {
	}
    }

    public void searchDataChangeListener() {
	courseEventId = FlagsEnum.ALL.getCode();
	courseEventName = null;
    }

    public void print() {
	try {
	    byte[] bytes = null;
	    if (reportType == 1)
		bytes = TrainingEmployeesService.getCancelledCourseTraineesBytes(getLoginEmpPhysicalRegionFlag(true), empId, fromDate, toDate);
	    else if (reportType == 2) {
		String regionDesc = regionId == FlagsEnum.ALL.getCode() ? "" : CommonService.getRegionById(regionId).getDescription();
		StringBuilder trainingTypesIds = new StringBuilder();
		StringBuilder trainingTypesDesc = new StringBuilder();
		for (int i = 0; i < checkedTrainingTypes.length; i++) {
		    int checked = checkedTrainingTypes[i];
		    trainingTypesIds.append(checked);
		    if (checked == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode())
			trainingTypesDesc.append(getMessage("label_internalMilitaryCourse"));
		    else if (checked == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode())
			trainingTypesDesc.append(getMessage("label_externalMilitaryCourse"));
		    else if (checked == TrainingTypesEnum.SCHOLARSHIP.getCode())
			trainingTypesDesc.append(getMessage("label_scholarship"));
		    else if (checked == TrainingTypesEnum.STUDY_ENROLLMENT.getCode())
			trainingTypesDesc.append(getMessage("label_studyEnrolment"));
		    else if (checked == TrainingTypesEnum.MORNING_COURSE.getCode())
			trainingTypesDesc.append(getMessage("label_morningCourse"));
		    else if (checked == TrainingTypesEnum.EVENING_COURSE.getCode())
			trainingTypesDesc.append(getMessage("label_eveningCourse"));

		    if (i != checkedTrainingTypes.length - 1) {
			trainingTypesIds.append(",");
			trainingTypesDesc.append("/");
		    }
		}
		bytes = TrainingEmployeesService.getTrainingTransactionsBytes(regionId, regionDesc, empId, empName, fromDate, toDate, trainingCourseId, trainingCourseName, qualificationMajorSpecId, qualificationMajorSpecDesc, qualificationMinorSpecId, qualificationMinorSpecDesc, unitId, unitName, trainingTypesIds.toString(), trainingTypesDesc.toString(), traineeSuccessFlag, traineeStatus, categoryId);
	    } else if (reportType == 3) {
		bytes = TrainingEmployeesService.getTrainingPlanCandidatesEmployeesBytes(trainingYearId, unitId, courseEventId, regionId);
	    } else if (reportType == 4) {
		StringBuilder courseEventStatusesIds = new StringBuilder();
		StringBuilder courseEventStatusesDesc = new StringBuilder();

		for (int i = 0; i < checkedcourseEventStatuses.length; i++) {
		    int checked = checkedcourseEventStatuses[i];
		    courseEventStatusesIds.append(checked);

		    if (checked == TrainingCourseEventStatusEnum.PLANNED_TO_BE_HELD.getCode())
			courseEventStatusesDesc.append(getMessage("label_coursePlannedToBeHeld"));
		    else if (checked == TrainingCourseEventStatusEnum.COURSE_EVENT_HOLDING_DECISION_ISSUED.getCode())
			courseEventStatusesDesc.append(getMessage("label_courseHoldingDecisionIssued"));
		    else if (checked == TrainingCourseEventStatusEnum.COURSE_EVENT_POSTPONEMENT_DECISION_ISSUED.getCode())
			courseEventStatusesDesc.append(getMessage("label_coursePostponementDecisionIssued"));
		    else if (checked == TrainingCourseEventStatusEnum.COURSE_EVENT_CANCEL_DECISION_ISSUED.getCode())
			courseEventStatusesDesc.append(getMessage("label_courseCancelDecisionIssued"));
		    else if (checked == TrainingCourseEventStatusEnum.COURSE_EVENT_HELD.getCode())
			courseEventStatusesDesc.append(getMessage("label_courseHeld"));

		    if (i != checkedcourseEventStatuses.length - 1) {
			courseEventStatusesIds.append(",");
			courseEventStatusesDesc.append("/");
		    }
		}
		bytes = TrainingCoursesEventsService.getTrainingCourseEventCandidateEmployeesStatistics(trainingYearId, unitId, trainingCourseId, trainingCourseName, fromDate, toDate, checkedcourseEventStatuses.length == 0 ? null : courseEventStatusesIds.toString(), checkedcourseEventStatuses.length == 0 ? null : courseEventStatusesDesc.toString());

	    } else if (reportType == 5) {
		String regionDesc = regionId == FlagsEnum.ALL.getCode() ? "" : CommonService.getRegionById(regionId).getDescription();
		StringBuilder trainingTypesIds = new StringBuilder();
		StringBuilder trainingTypesDesc = new StringBuilder();
		for (int i = 0; i < checkedTrainingTypes.length; i++) {
		    int checked = checkedTrainingTypes[i];
		    trainingTypesIds.append(checked);
		    if (checked == TrainingTypesEnum.INTERNAL_MILITARY_COURSE.getCode())
			trainingTypesDesc.append(getMessage("label_internalMilitaryCourse"));
		    else if (checked == TrainingTypesEnum.EXTERNAL_MILITARY_COURSE.getCode())
			trainingTypesDesc.append(getMessage("label_externalMilitaryCourse"));
		    else if (checked == TrainingTypesEnum.SCHOLARSHIP.getCode())
			trainingTypesDesc.append(getMessage("label_scholarship"));
		    else if (checked == TrainingTypesEnum.STUDY_ENROLLMENT.getCode())
			trainingTypesDesc.append(getMessage("label_studyEnrolment"));
		    else if (checked == TrainingTypesEnum.MORNING_COURSE.getCode())
			trainingTypesDesc.append(getMessage("label_morningCourse"));
		    else if (checked == TrainingTypesEnum.EVENING_COURSE.getCode())
			trainingTypesDesc.append(getMessage("label_eveningCourse"));

		    if (i != checkedTrainingTypes.length - 1) {
			trainingTypesIds.append(",");
			trainingTypesDesc.append("/");
		    }
		}
		bytes = TrainingEmployeesService.getTrainingRequestsBytes(regionId, regionDesc, empId, empName, fromDate, toDate, trainingCourseId, trainingCourseName, unitId, unitName, trainingTypesIds.toString(), trainingTypesDesc.toString(), requestStatus, categoryId);
	    }
	    super.print(bytes);
	} catch (BusinessException e) {

	}
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

    public long getEmpId() {
	return empId;
    }

    public void setEmpId(long empId) {
	this.empId = empId;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
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

    public String getQualificationMinorSpecDesc() {
	return qualificationMinorSpecDesc;
    }

    public void setQualificationMinorSpecDesc(String qualificationMinorSpecDesc) {
	this.qualificationMinorSpecDesc = qualificationMinorSpecDesc;
    }

    public long getQualificationMinorSpecId() {
	return qualificationMinorSpecId;
    }

    public void setQualificationMinorSpecId(long qualificationMinorSpecId) {
	this.qualificationMinorSpecId = qualificationMinorSpecId;
    }

    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

    public String getQualificationMajorSpecDesc() {
	return qualificationMajorSpecDesc;
    }

    public void setQualificationMajorSpecDesc(String qualificationMajorSpecDesc) {
	this.qualificationMajorSpecDesc = qualificationMajorSpecDesc;
    }

    public String getTrainingCourseName() {
	return trainingCourseName;
    }

    public void setTrainingCourseName(String trainingCourseName) {
	this.trainingCourseName = trainingCourseName;
    }

    public int[] getCheckedTrainingTypes() {
	return checkedTrainingTypes;
    }

    public void setCheckedTrainingTypes(int[] checkedTrainingTypes) {
	this.checkedTrainingTypes = checkedTrainingTypes;
    }

    public long getUnitId() {
	return unitId;
    }

    public void setUnitId(long unitId) {
	this.unitId = unitId;
    }

    public String getUnitName() {
	return unitName;
    }

    public void setUnitName(String unitName) {
	this.unitName = unitName;
    }

    public long getTrainingCourseId() {
	return trainingCourseId;
    }

    public void setTrainingCourseId(long trainingCourseId) {
	this.trainingCourseId = trainingCourseId;
    }

    public long getQualificationMajorSpecId() {
	return qualificationMajorSpecId;
    }

    public void setQualificationMajorSpecId(long qualificationMajorSpecId) {
	this.qualificationMajorSpecId = qualificationMajorSpecId;
    }

    public String getRegionDesc() {
	return regionDesc;
    }

    public void setRegionDesc(String regionDesc) {
	this.regionDesc = regionDesc;
    }

    public int getTraineeSuccessFlag() {
	return traineeSuccessFlag;
    }

    public void setTraineeSuccessFlag(int traineeSuccessFlag) {
	this.traineeSuccessFlag = traineeSuccessFlag;
    }

    public int getTraineeStatus() {
	return traineeStatus;
    }

    public void setTraineeStatus(int traineeStatus) {
	this.traineeStatus = traineeStatus;
    }

    public long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(long categoryId) {
	this.categoryId = categoryId;
    }

    public List<TrainingYear> getTrainingYears() {
	return trainingYears;
    }

    public void setTrainingYears(List<TrainingYear> trainingYears) {
	this.trainingYears = trainingYears;
    }

    public List<TrainingUnitData> getTrainingUnits() {
	return trainingUnits;
    }

    public void setTrainingUnits(List<TrainingUnitData> trainingUnits) {
	this.trainingUnits = trainingUnits;
    }

    public long getTrainingYearId() {
	return trainingYearId;
    }

    public void setTrainingYearId(long trainingYearId) {
	this.trainingYearId = trainingYearId;
    }

    public long getCourseEventId() {
	return courseEventId;
    }

    public void setCourseEventId(long courseEventId) {
	this.courseEventId = courseEventId;
    }

    public String getCourseEventName() {
	return courseEventName;
    }

    public void setCourseEventName(String courseEventName) {
	this.courseEventName = courseEventName;
    }

    public int[] getCheckedcourseEventStatuses() {
	return checkedcourseEventStatuses;
    }

    public void setCheckedcourseEventStatuses(int[] checkedcourseEventStatuses) {
	this.checkedcourseEventStatuses = checkedcourseEventStatuses;
    }

    public int getRequestStatus() {
	return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
	this.requestStatus = requestStatus;
    }

    public boolean isNoTrainingUnitsflag() {
	return noTrainingUnitsflag;
    }

    public void setNoTrainingUnitsflag(boolean noTrainingUnitsflag) {
	this.noTrainingUnitsflag = noTrainingUnitsflag;
    }

}
