package com.code.ui.backings.hcm.missions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.missions.MissionData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.workflow.hcm.missions.WFMissionData;
import com.code.dal.orm.workflow.hcm.missions.WFMissionDetailData;
import com.code.enums.CategoryModesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MissionsService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.MissionsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "missionRequest")
@ViewScoped
public class MissionRequest extends WFBaseBacking {
    /**
     * 1 for Officers, 2 for Soldiers and 3 for Civil
     **/
    private int mode;

    private WFMissionData wfMission;
    private List<WFMissionDetailData> wfMissionDetailDataList;
    private WFMissionDetailData selectedMissionDetail;
    private List<Region> messionRegions;
    private long empId;
    private boolean outOfRequesterUnitFlag;
    private List<EmployeeData> internalCopies;
    private List<EmployeeData> reviewerEmps;
    private Long selectedReviewerEmpId;

    private Integer oldPeriod;
    private Integer oldRoadPeriod;
    private Date oldStartDate;

    private int pageSize = 10;

    public MissionRequest() {

	super.init();

	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    if (mode == CategoryModesEnum.OFFICERS.getCode()) {
		this.processId = WFProcessesEnum.OFFICERS_MISSION.getCode();
		setScreenTitle(getMessage("title_officersMissionView"));
	    } else if (mode == CategoryModesEnum.SOLDIERS.getCode()) {
		this.processId = WFProcessesEnum.SOLDIERS_MISSION.getCode();
		setScreenTitle(getMessage("title_soldiersMissionView"));
	    } else if (mode == CategoryModesEnum.CIVILIANS.getCode()) {
		this.processId = WFProcessesEnum.CIVILIANS_MISSION.getCode();
		setScreenTitle(getMessage("title_personsMissionView"));
	    } else {
		setServerSideErrorMessages(getMessage("error_general"));
	    }
	} else {
	    setServerSideErrorMessages(getMessage("error_general"));
	}

	this.init();
    }

    public void init() {

	try {
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfMission = MissionsWorkFlow.getWFMissionByInstanceId(instance.getInstanceId());
		wfMissionDetailDataList = MissionsWorkFlow.getWFMissionDetailsByInstanceId(instance.getInstanceId());
		if (wfMissionDetailDataList.size() > 0)
		    selectedMissionDetail = wfMissionDetailDataList.get(0);
		else
		    selectedMissionDetail = new WFMissionDetailData();
		if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		}

		if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()) || this.role.equals(WFTaskRolesEnum.SIGN_MANAGER.getCode()) || this.role.equals(WFTaskRolesEnum.NOTIFICATION.getCode()) || this.role.equals(WFTaskRolesEnum.HISTORY.getCode())) {
		    internalCopies = EmployeesService.getEmployeesByIdsString(wfMission.getInternalCopies());
		}

	    } else {
		wfMission = new WFMissionData();
		wfMission.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
		wfMission.setLocation(getMessage("label_ksa"));
		wfMission.setRegionsCodes(null);
		wfMission.setRoadPeriod(FlagsEnum.OFF.getCode());
		wfMission.setHajjFlag(FlagsEnum.OFF.getCode());
		wfMission.setStartDate(HijriDateService.getHijriSysDate());
		wfMission.setPreviousFlag(FlagsEnum.OFF.getCode());
		wfMission.setBasedOnFlag(FlagsEnum.OFF.getCode());
		selectedMissionDetail = new WFMissionDetailData();
		selectedMissionDetail.setRoadPeriod(FlagsEnum.OFF.getCode());
		internalCopies = new ArrayList<EmployeeData>();
		wfMissionDetailDataList = new ArrayList<WFMissionDetailData>();
	    }

	    oldPeriod = wfMission.getPeriod();
	    oldRoadPeriod = wfMission.getRoadPeriod();
	    oldStartDate = wfMission.getStartDate();
	    messionRegions = CommonService.getAllRegions();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}

    }

    public void addEmp() {
	try {
	    if (empId != FlagsEnum.OFF.getCode() && wfMission != null) {
		selectedMissionDetail = MissionsWorkFlow.addWFMissionDetail(empId, wfMission);
		wfMissionDetailDataList.add(selectedMissionDetail);

		checkOutOfRequesterUnit();
	    }
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void showMissionDetail(WFMissionDetailData missionDetail) {
	this.selectedMissionDetail = missionDetail;
    }

    public void deleteEmp(WFMissionDetailData wfMissionDetailData) {
	try {
	    if (wfMissionDetailDataList.size() == FlagsEnum.ON.getCode())
		throw new BusinessException("error_mustHaveAtLeastOnlyOne");

	    wfMissionDetailData.getWfMissionDetail().setSystemUser(this.loginEmpData.getEmpId() + "");
	    MissionsWorkFlow.deleteWFMissionDetail(wfMissionDetailData, wfMissionDetailDataList);
	    checkOutOfRequesterUnit();

	    if (wfMissionDetailData.equals(selectedMissionDetail)) {
		if (wfMissionDetailDataList.size() > 0)
		    selectedMissionDetail = wfMissionDetailDataList.get(0);
		else
		    selectedMissionDetail = new WFMissionDetailData();
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void changeLocationFlag(AjaxBehaviorEvent event) {
	if (wfMission.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode())) {
	    wfMission.setLocation(null);
	    wfMission.setHajjFlag(FlagsEnum.OFF.getCode());
	    wfMission.setDoubleBalanceFlag(FlagsEnum.OFF.getCode());
	    wfMission.setRegionsCodes(null);
	} else {
	    wfMission.setLocation(getMessage("label_ksa"));
	    wfMission.setMinisteryApprovalNumber(null);
	    wfMission.setMinisteryApprovalDate(null);
	    wfMission.setRegionsCodes(null);
	}

	wfMission.setEmpExternalFlag(FlagsEnum.OFF.getCode());
	wfMission.setEmpExternalStatus(null);

	checkOutOfRequesterUnit();
    }

    public void changePerviouseFlag(AjaxBehaviorEvent event) {
	if (wfMission.getPreviousFlag().equals(FlagsEnum.OFF.getCode()))
	    wfMission.setPreviousDescription(null);
    }

    public void changeRelatedFlag(AjaxBehaviorEvent event) {
	if (wfMission.getRelatedFlag().equals(FlagsEnum.OFF.getCode()))
	    wfMission.setRelatedDescription(null);
    }

    public void changeBasedOnFlag(AjaxBehaviorEvent event) {
	if (wfMission.getBasedOnFlag().equals(FlagsEnum.OFF.getCode()) || wfMission.getBasedOnFlag().equals(FlagsEnum.ON.getCode())) {
	    wfMission.setBasedOnNumber(null);
	    wfMission.setBasedOnDate(null);
	}
    }

    public void changeResultLetterFlag(AjaxBehaviorEvent event) {
	if (wfMission.getResultLetterFlag().equals(FlagsEnum.OFF.getCode()))
	    wfMission.setResultLetterDescription(null);
    }

    public void changeResultOtherFlag(AjaxBehaviorEvent event) {
	if (wfMission.getResultOtherFlag().equals(FlagsEnum.OFF.getCode()))
	    wfMission.setResultOtherDescription(null);
    }

    private void checkOutOfRequesterUnit() {
	// If 1- requester, 2- location is internal,
	// 3- (not in requester unit hierarchy) OR (exist in requester unit
	// hierarchy and not Manager)

	if (wfMission.getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
	    outOfRequesterUnitFlag = false;
	    for (WFMissionDetailData detail : wfMissionDetailDataList) {
		if (!detail.getEmpPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(requester.getPhysicalUnitHKey())) || (detail.getEmpPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(requester.getPhysicalUnitHKey())) && !requester.getIsManager().equals(FlagsEnum.ON.getCode()))) {
		    outOfRequesterUnitFlag = true;
		    break;
		}
	    }
	} else {
	    outOfRequesterUnitFlag = false;
	}
    }

    public void adjustMissionEndDate() {
	try {
	    if (wfMission.getPeriod() == null || wfMission.getRoadPeriod() == null || wfMission.getStartDate() == null)
		return;

	    if (wfMission.getPeriod().equals(oldPeriod) && wfMission.getRoadPeriod().equals(oldRoadPeriod) && wfMission.getStartDate().equals(oldStartDate))
		return;

	    wfMission.setEndDateString(MissionsService.calculateMissionEndDate(wfMission.getStartDateString(), wfMission.getPeriod(), wfMission.getRoadPeriod()));
	    MissionsWorkFlow.adjustMissionDetailsData(wfMission, wfMissionDetailDataList, !wfMission.getStartDate().equals(oldStartDate), !wfMission.getRoadPeriod().equals(oldRoadPeriod));

	    oldPeriod = wfMission.getPeriod();
	    oldRoadPeriod = wfMission.getRoadPeriod();
	    oldStartDate = wfMission.getStartDate();
	    if (wfMissionDetailDataList.size() > 0) {
		if (wfMission.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode()) && this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {
		    super.setServerSideSuccessMessages(getMessage("notify_allMissionDetailChangeSuccessfully"));
		} else {
		    super.setServerSideSuccessMessages(getMessage("notify_missionDetailChangeSuccessfully"));
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void adjustMissionDetailEndDate() {
	if (selectedMissionDetail.getPeriod() == null || selectedMissionDetail.getRoadPeriod() == null || selectedMissionDetail.getStartDate() == null)
	    return;

	selectedMissionDetail.setEndDateString(MissionsService.calculateMissionEndDate(selectedMissionDetail.getStartDateString(), selectedMissionDetail.getPeriod(), selectedMissionDetail.getRoadPeriod()));
    }

    public void refreshEmpBalance() {
	try {
	    adjustMissionDetailEndDate();

	    selectedMissionDetail.setBalance(MissionsService.calculateEmpMissionsBalance(selectedMissionDetail.getEmpId(), selectedMissionDetail.getStartDate()));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void auditMissionRequest(WFMissionData wfMission, List<WFMissionDetailData> wfMissionDetailDataList) {
	wfMission.getWfMission().setSystemUser(loginEmpData.getEmpId() + "");
	for (WFMissionDetailData missionDetailData : wfMissionDetailDataList) {
	    missionDetailData.getWfMissionDetail().setSystemUser(loginEmpData.getEmpId() + "");
	}
    }

    public String initProcess() {
	try {
	    auditMissionRequest(wfMission, wfMissionDetailDataList);
	    MissionsWorkFlow.initMission(requester, wfMission, wfMissionDetailDataList, processId, attachments, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String approveDM() {
	try {
	    auditMissionRequest(wfMission, wfMissionDetailDataList);
	    MissionsWorkFlow.doMissionDM(requester, instance, wfMission, wfMissionDetailDataList, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String rejectDM() {
	try {
	    auditMissionRequest(wfMission, wfMissionDetailDataList);
	    MissionsWorkFlow.doMissionDM(requester, instance, wfMission, wfMissionDetailDataList, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public String redirectVM() {
	try {
	    MissionsWorkFlow.doMissionMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public String approveRE() {
	String internalCopiesString = EmployeesService.getEmployeesIdsString(internalCopies);
	wfMission.setInternalCopies(internalCopiesString);
	try {
	    auditMissionRequest(wfMission, wfMissionDetailDataList);
	    MissionsWorkFlow.doMissionRE(requester, wfMission, wfMissionDetailDataList, instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	    return null;
	}
    }

    public String signVM() {
	try {
	    MissionsWorkFlow.doMissionSM(requester, instance, wfMission, wfMissionDetailDataList, currentTask, 1);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public String modifyVM() {
	try {
	    MissionsWorkFlow.doMissionSM(requester, instance, wfMission, wfMissionDetailDataList, currentTask, 2);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public String rejectVM() {
	try {
	    MissionsWorkFlow.doMissionSM(requester, instance, wfMission, wfMissionDetailDataList, currentTask, 0);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public String closeProcess() {

	try {
	    MissionsWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public void print() {
	try {
	    MissionData missionData = MissionsService.getMissionRequest(wfMissionDetailDataList.get(0).getEmpId(), wfMission.getStartDate(), wfMission.getEndDate());
	    if (missionData != null) {
		byte[] bytes = MissionsService.getMissionDecisionBytes(missionData.getId(), missionData.getCategoryMode());
		super.print(bytes);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printMissionData() {

	try {
	    byte[] bytes = MissionsWorkFlow.getWfMissionDataBytes(wfMission.getInstanceId(), mode);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String getStartDateOfYear() {
	String startDateOfYear = "";
	try {
	    if (wfMission.getStartDateString() != null)
		startDateOfYear = MissionsService.getFinYearStartAndEndHijriDates(wfMission.getStartDateString()).get(0);

	    return startDateOfYear;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    e.printStackTrace();
	    return startDateOfYear;
	}
    }

    public String getRegionsNames() {
	String regionsNames = "";
	String comma = "";
	if (wfMission.getRegionsCodes() != null && !wfMission.getRegionsCodes().isEmpty()) {
	    String[] codes = wfMission.getRegionsCodes().split(",");
	    for (String code : codes) {
		if (!code.equals("-1")) {
		    regionsNames += comma + CommonService.getRegionByCode(code).getDescription();
		    comma = ",";
		}
	    }

	    if (wfMission.getRegionsCodes().contains("-1"))
		regionsNames += comma + getMessage("lable_otherRegions");
	}

	return regionsNames;
    }

    public boolean isExceptionalBalanceValid() {
	if (selectedMissionDetail.getBalance() - (selectedMissionDetail.getPeriod() == null ? 0 : selectedMissionDetail.getPeriod()) - (selectedMissionDetail.getRoadPeriod() == null ? 0 : selectedMissionDetail.getRoadPeriod()) < 0)
	    return true;

	selectedMissionDetail.setExceptionalApprovalDate(null);
	selectedMissionDetail.setExceptionalApprovalNumber(null);
	return false;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

    public WFMissionData getWfMission() {
	return wfMission;
    }

    public void setWfMission(WFMissionData wfMission) {
	this.wfMission = wfMission;
    }

    public List<WFMissionDetailData> getWfMissionDetailDataList() {
	return wfMissionDetailDataList;
    }

    public void setWfMissionDetailDataList(List<WFMissionDetailData> wfMissionDetailDataList) {
	this.wfMissionDetailDataList = wfMissionDetailDataList;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public List<Region> getMessionRegions() {
	return messionRegions;
    }

    public void setMessionRegions(List<Region> messionRegions) {
	this.messionRegions = messionRegions;
    }

    public long getEmpId() {
	return empId;
    }

    public void setEmpId(long empId) {
	this.empId = empId;
    }

    public WFMissionDetailData getSelectedMissionDetail() {
	return selectedMissionDetail;
    }

    public void setSelectedMissionDetail(WFMissionDetailData selectedMissionDetail) {
	this.selectedMissionDetail = selectedMissionDetail;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }

    public List<EmployeeData> getReviewerEmps() {
	return reviewerEmps;
    }

    public void setReviewerEmps(List<EmployeeData> reviewerEmps) {
	this.reviewerEmps = reviewerEmps;
    }

    public Long getSelectedReviewerEmpId() {
	return selectedReviewerEmpId;
    }

    public void setSelectedReviewerEmpId(Long selectedReviewerEmpId) {
	this.selectedReviewerEmpId = selectedReviewerEmpId;
    }

    public boolean isOutOfRequesterUnitFlag() {
	return outOfRequesterUnitFlag;
    }

    public void setOutOfRequesterUnitFlag(boolean outOfRequesterUnitFlag) {
	this.outOfRequesterUnitFlag = outOfRequesterUnitFlag;
    }

    public long getRegionId() {
	return requester.getPhysicalRegionId() == null ? FlagsEnum.OFF.getCode() : requester.getPhysicalRegionId().longValue();
    }

}
