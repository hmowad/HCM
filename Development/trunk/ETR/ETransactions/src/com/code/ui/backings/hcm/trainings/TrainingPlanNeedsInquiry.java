package com.code.ui.backings.hcm.trainings;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.trainings.TrainingUnitData;
import com.code.dal.orm.hcm.trainings.TrainingYear;
import com.code.dal.orm.workflow.hcm.trainings.WFTrainingPlanNeedData;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.TrainingSetupService;
import com.code.services.util.CommonService;
import com.code.services.workflow.hcm.TrainingCoursesEventsWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "trainingPlanNeedsInquiry")
@ViewScoped
public class TrainingPlanNeedsInquiry extends BaseBacking {

    private final int pageSize = 10;
    private Long trainingUnitId;
    private Long trainingYearId;
    private Long requestingRegionId;
    private Long requestingUnitId;
    // private Long parentUnitId;
    private String requestingUnitFullName;
    private String courseName;
    private Long courseEventId;
    private List<WFTrainingPlanNeedData> trainingPlanUnitsRequests;
    private List<WFTrainingPlanNeedData> trainingPlanUnitNeeds;
    private List<TrainingUnitData> trainingUnits;
    private List<TrainingYear> trainingYears;
    private List<Region> regions;
    private WFTrainingPlanNeedData selectedRequest;
    private String requestingUnitDetailsMessage;
    private int reportType;

    public TrainingPlanNeedsInquiry() {
	try {
	    setScreenTitle(getMessage("title_trainingPlanNeedsInquiry"));
	    trainingYears = TrainingSetupService.getAllTrainingYears();
	    trainingUnits = TrainingSetupService.getAllTrainingUnitsData();
	    if (loginEmpData.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
		regions = CommonService.getAllRegions();
	    } else {
		regions = new ArrayList<Region>(1);
		regions.add(CommonService.getRegionById(loginEmpData.getPhysicalRegionId()));

	    }
	    reset();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void reset() {
	trainingUnitId = trainingUnits.get(0).getUnitId();
	if (trainingYears.size() != 0)
	    trainingYearId = trainingYears.get(trainingYears.size() - 1).getId();
	requestingRegionId = regions.get(0).getId();
	// manipulateParentUnitId();
	requestingUnitId = (long) FlagsEnum.ALL.getCode();
	requestingUnitFullName = "";
	courseEventId = (long) FlagsEnum.ALL.getCode();
	trainingPlanUnitsRequests = null;
	trainingPlanUnitNeeds = null;
	selectedRequest = null;
	reportType = 1;
    }

    public void searchNeedsRequests() {

	try {
	    trainingPlanUnitsRequests = TrainingCoursesEventsWorkFlow.getDistinctApprovedWFTrainingPlanNeedsData(requestingUnitId == null ? FlagsEnum.ALL.getCode() : requestingUnitId, trainingUnitId, trainingYearId, requestingRegionId, courseEventId);
	    selectedRequest = null;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    String trainingYearName = "";
	    String trainingUnitName = "";
	    String regionName = "";
	    for (TrainingYear year : trainingYears)
		if (year.getId().equals(trainingYearId)) {
		    trainingYearName = year.getName();
		    break;
		}
	    for (TrainingUnitData unit : trainingUnits)
		if (unit.getUnitId().equals(trainingUnitId)) {
		    trainingUnitName = unit.getName();
		    break;
		}
	    for (Region region : regions)
		if (region.getId().equals(requestingRegionId)) {
		    regionName = region.getDescription();
		    break;
		}

	    byte[] bytes;
	    // regionName TrainingYearName TrainingUnitName
	    if (reportType == 1)
		bytes = TrainingCoursesEventsWorkFlow.getTrainingPlanNeedsInquiryByCourseBytes(trainingYearName, trainingUnitName, requestingRegionId, regionName, courseName, courseEventId, requestingUnitId);
	    else // regionName TrainingYearName TrainingUnitName requestingUnitName
		bytes = TrainingCoursesEventsWorkFlow.getTrainingPlanNeedsInquiryByRequestingUnitBytes(trainingYearId, trainingYearName, trainingUnitId, trainingUnitName, regionName, requestingRegionId, courseEventId, requestingUnitId);
	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectNeedsRequest(WFTrainingPlanNeedData need) {
	try {
	    selectedRequest = need;
	    requestingUnitDetailsMessage = getParameterizedMessage("label_needsRequestingDataDetailsWithDate", new Object[] { need.getRequestingUnitFullName(), need.getHijriRequestDateString() });
	    trainingPlanUnitNeeds = TrainingCoursesEventsWorkFlow.getWFTrainingPlanNeedData(need.getInstanceId(), trainingUnitId, courseEventId);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public Long getTrainingUnitId() {
	return trainingUnitId;
    }

    public void setTrainingUnitId(Long trainingUnitId) {
	this.trainingUnitId = trainingUnitId;
    }

    public Long getTrainingYearId() {
	return trainingYearId;
    }

    public void setTrainingYearId(Long trainingYearId) {
	this.trainingYearId = trainingYearId;
    }

    public Long getRequestingRegionId() {
	return requestingRegionId;
    }

    public void setRequestingRegionId(Long requestingRegionId) {
	this.requestingRegionId = requestingRegionId;
    }

    public Long getRequestingUnitId() {
	return requestingUnitId;
    }

    public void setRequestingUnitId(Long requestingUnitId) {
	this.requestingUnitId = requestingUnitId;
    }

    public String getRequestingUnitFullName() {
	return requestingUnitFullName;
    }

    public void setRequestingUnitFullName(String requestingUnitFullName) {
	this.requestingUnitFullName = requestingUnitFullName;
    }

    public List<WFTrainingPlanNeedData> getTrainingPlanUnitsRequests() {
	return trainingPlanUnitsRequests;
    }

    public void setTrainingPlanUnitsRequests(List<WFTrainingPlanNeedData> trainingPlanUnitsRequests) {
	this.trainingPlanUnitsRequests = trainingPlanUnitsRequests;
    }

    public List<WFTrainingPlanNeedData> getTrainingPlanUnitNeeds() {
	return trainingPlanUnitNeeds;
    }

    public void setTrainingPlanUnitNeeds(List<WFTrainingPlanNeedData> trainingPlanUnitNeeds) {
	this.trainingPlanUnitNeeds = trainingPlanUnitNeeds;
    }

    public int getPageSize() {
	return pageSize;
    }

    public List<TrainingUnitData> getTrainingUnits() {
	return trainingUnits;
    }

    public void setTrainingUnits(List<TrainingUnitData> trainingUnits) {
	this.trainingUnits = trainingUnits;
    }

    public List<TrainingYear> getTrainingYears() {
	return trainingYears;
    }

    public void setTrainingYears(List<TrainingYear> trainingYears) {
	this.trainingYears = trainingYears;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public void setRegions(List<Region> regions) {
	this.regions = regions;
    }

    public WFTrainingPlanNeedData getSelectedRequest() {
	return selectedRequest;
    }

    public void setSelectedRequest(WFTrainingPlanNeedData selectedRequest) {
	this.selectedRequest = selectedRequest;
    }

    public String getRequestingUnitDetailsMessage() {
	return requestingUnitDetailsMessage;
    }

    public void setRequestingUnitDetailsMessage(String requestingUnitDetailsMessage) {
	this.requestingUnitDetailsMessage = requestingUnitDetailsMessage;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public Long getCourseEventId() {
	return courseEventId;
    }

    public String getCourseName() {
	return courseName;
    }

    public void setCourseName(String courseName) {
	this.courseName = courseName;
    }

    public void setCourseEventId(Long courseEventId) {
	this.courseEventId = courseEventId;
    }

}