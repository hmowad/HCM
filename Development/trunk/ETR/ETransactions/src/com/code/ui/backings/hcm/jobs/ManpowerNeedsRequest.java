package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.jobs.ManpowerNeedData;
import com.code.dal.orm.hcm.organization.jobs.ManpowerNeedDetailData;
import com.code.dal.orm.hcm.organization.units.Unit;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.workflow.hcm.organization.jobs.WFManpowerNeed;
import com.code.enums.FlagsEnum;
import com.code.enums.ManpowerNeedTypesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.ManpowerNeedsService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.ManpowerNeedsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "manpowerNeedsRequest")
@ViewScoped
public class ManpowerNeedsRequest extends WFBaseBacking {

    /** 1 for Officers and 2 for Soldiers **/
    private int mode;

    private String requestingUnitHKeyPrefix;

    private WFManpowerNeed manpowerNeedRequest;
    private ManpowerNeedData manpowerNeedData;
    private List<ManpowerNeedDetailData> manpowerNeedDetailsData;

    private int allRequestsFlag;
    private Date fromDate;
    private Date toDate;

    private List<EmployeeData> reviewerEmps;
    private Long selectedReviewerEmpId;

    private Unit selectedUnit;
    private final int pageSize = 10;

    public ManpowerNeedsRequest() {
	super.init();
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		processId = WFProcessesEnum.OFFICERS_MANPOWER_NEED_REQUEST.getCode();
		setScreenTitle(getMessage("title_manpowerNeedsRequestForOfficers"));
		break;
	    case 2:
		processId = WFProcessesEnum.SOLDIERS_MANPOWER_NEED_REQUEST.getCode();
		setScreenTitle(getMessage("title_manpowerNeedsRequestForSoldiers"));
		break;
	    default:
		this.setServerSideErrorMessages(getMessage("error_URLError"));
	    }

	    intializeRequestData();

	} else {
	    this.setServerSideErrorMessages(getMessage("error_URLError"));
	}
    }

    private void intializeRequestData() {
	try {
	    selectedUnit = new Unit();
	    allRequestsFlag = FlagsEnum.OFF.getCode();
	    fromDate = toDate = HijriDateService.getHijriSysDate();

	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		manpowerNeedRequest = new WFManpowerNeed();
		manpowerNeedData = new ManpowerNeedData();
		manpowerNeedData.setCategoryId((long) mode);
		manpowerNeedData.setTransactionDate(HijriDateService.getHijriSysDate());
		manpowerNeedData.setNeedType(ManpowerNeedTypesEnum.REQUEST.getCode());

		UnitData requestingUnit = calculateRequestingUnitData();
		manpowerNeedData.setRequestingUnitId(requestingUnit.getId());
		manpowerNeedData.setRequestingUnitHKey(requestingUnit.gethKey());
		manpowerNeedData.setRequestingRegionId(requestingUnit.getRegionId());
		manpowerNeedDetailsData = new ArrayList<ManpowerNeedDetailData>();
	    } else {
		manpowerNeedRequest = ManpowerNeedsWorkFlow.getWFManpowerNeedByInstanceId(instance.getInstanceId());
		manpowerNeedData = ManpowerNeedsService.getManpowerNeedDataById(manpowerNeedRequest.getManpowerNeedId());
		manpowerNeedDetailsData = ManpowerNeedsService.getManpowerNeedDetailsDataByManpowerNeedId(manpowerNeedRequest.getManpowerNeedId());

		requestingUnitHKeyPrefix = UnitsService.getHKeyPrefix(manpowerNeedData.getRequestingUnitHKey());

		if (this.role.equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())) {
		    for (ManpowerNeedDetailData manpowerNeedDetailData : manpowerNeedDetailsData) {
			if (manpowerNeedDetailData.getRequestSuggestedCount() == null)
			    manpowerNeedDetailData.setRequestSuggestedCount(manpowerNeedDetailData.getRequiredCount());
		    }
		} else if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
		    selectedReviewerEmpId = 0L;
		    reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		} else if (role.equals(WFTaskRolesEnum.NOTIFICATION.getCode())) {
		    String prevNotifyTaskAction = prevTasks.get(prevTasks.size() - 1).getAction();
		    if (prevNotifyTaskAction.equals(WFTaskActionsEnum.SUBMIT_TO_STUDY.getCode())) {
			notificationMessage = this.getMessage("notify_requestSubmittedToStudy");
			instanceApproved = new Integer(FlagsEnum.ON.getCode());
		    }
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private UnitData calculateRequestingUnitData() {
	try {
	    if (this.loginEmpData.getPhysicalRegionId().longValue() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
		requestingUnitHKeyPrefix = this.loginEmpData.getPhysicalUnitHKey().substring(0, 4);
	    else
		requestingUnitHKeyPrefix = this.loginEmpData.getPhysicalUnitHKey().substring(0, 6);

	    return UnitsService.getUnitByExactHKey(String.format("%s%0" + (20 - requestingUnitHKeyPrefix.length()) + "d", requestingUnitHKeyPrefix, 0));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public void addNewManpowerNeedsDetail() {
	if (selectedUnit != null) {
	    ManpowerNeedDetailData manpowerDetailData = new ManpowerNeedDetailData();
	    manpowerDetailData.setAddedOnStudyFlag(FlagsEnum.OFF.getCode());
	    manpowerDetailData.setUnitId(selectedUnit.getId());
	    manpowerDetailData.setUnitFullName(selectedUnit.getFullName());
	    manpowerDetailData.setUnitHKey(selectedUnit.gethKey());
	    manpowerDetailData.setRequiredCount(0);

	    manpowerNeedDetailsData.add(0, manpowerDetailData);
	}
    }

    public void calculateEmployeesCounts(ManpowerNeedDetailData manpowerDetailData) {
	try {
	    ManpowerNeedsService.validateMinorSpecAvailabilityInRequestingUnit(manpowerNeedData.getRequestingUnitHKey(), new Long[] { manpowerDetailData.getMinorSpecializationId() });

	    manpowerDetailData.setOfficialEmployeesCount(EmployeesService.countEmployeesByOfficialUnitHkeyAndMinorSpecAndCategory(manpowerDetailData.getUnitHKey(),
		    manpowerDetailData.getMinorSpecializationId(), getCategoriesIdsArrayByMode(manpowerNeedData.getCategoryId().intValue())).intValue());
	    manpowerDetailData.setPhysicalEmployeesCount(EmployeesService.countEmployeesByPhysicalUnitHkeyAndMinorSpecAndCategory(manpowerDetailData.getUnitHKey(),
		    manpowerDetailData.getMinorSpecializationId(), getCategoriesIdsArrayByMode(manpowerNeedData.getCategoryId().intValue())).intValue());
	} catch (BusinessException e) {
	    manpowerDetailData.setMinorSpecializationId(null);
	    manpowerDetailData.setMinorSpecializationDescription("");
	    manpowerDetailData.setOfficialEmployeesCount(null);
	    manpowerDetailData.setPhysicalEmployeesCount(null);
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteManpowerNeedsDetail(ManpowerNeedDetailData manpowerDetailData) {
	manpowerNeedDetailsData.remove(manpowerDetailData);
    }

    /*****************************************************************************************************************/

    // initialize the process by requester
    public String initProcess() {
	try {
	    ManpowerNeedsWorkFlow.initManpowerNeedRequest(requester, manpowerNeedData, manpowerNeedDetailsData, processId, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // approve by direct manager
    public String approveDM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedRequestDM(requester, instance, manpowerNeedData, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // reject by direct manager
    public String rejectDM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedRequestDM(requester, instance, manpowerNeedData, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // redirect to reviewer employee by manager redirect
    public String redirectMR() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedRequestMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // approve by reviewer employee
    public String approveRE() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedRequestRE(requester, instance, manpowerNeedData, manpowerNeedDetailsData, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // reject by reviewer employee
    public String rejectRE() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedRequestRE(requester, instance, manpowerNeedData, manpowerNeedDetailsData, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // submit to study by sign manager
    public String submitSM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedRequestSM(requester, instance, manpowerNeedData, currentTask, 1);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // return to the reviewer employee by sign manager
    public String modifySM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedRequestSM(requester, instance, manpowerNeedData, currentTask, 2);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // reject by sign manager
    public String rejectSM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedRequestSM(requester, instance, manpowerNeedData, currentTask, 0);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // close the process
    public String closeProcess() {
	try {
	    ManpowerNeedsWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    /*****************************************************************************************************************/

    public void print() {
	try {
	    String allRequestsFlagDesc = "";
	    if (allRequestsFlag == FlagsEnum.OFF.getCode())
		allRequestsFlagDesc = getMessage("label_notInApprovedMainStudyRequests");
	    else if (allRequestsFlag == FlagsEnum.ALL.getCode())
		allRequestsFlagDesc = getMessage("label_allHistoricalRequests");

	    byte[] bytes = ManpowerNeedsService.getManpowerNeedsRequestsReportsBytes(10, manpowerNeedData.getCategoryId(), requestingUnitHKeyPrefix, allRequestsFlag, allRequestsFlagDesc, fromDate, toDate);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    /*****************************************************************************************************************/

    public int getMode() {
	return mode;
    }

    public String getRequestingUnitHKeyPrefix() {
	return requestingUnitHKeyPrefix;
    }

    public WFManpowerNeed getManpowerNeedRequest() {
	return manpowerNeedRequest;
    }

    public void setManpowerNeedRequest(WFManpowerNeed manpowerNeedRequest) {
	this.manpowerNeedRequest = manpowerNeedRequest;
    }

    public ManpowerNeedData getManpowerNeedData() {
	return manpowerNeedData;
    }

    public List<ManpowerNeedDetailData> getManpowerNeedDetailsData() {
	return manpowerNeedDetailsData;
    }

    public void setManpowerNeedDetailsData(List<ManpowerNeedDetailData> manpowerNeedDetailsData) {
	this.manpowerNeedDetailsData = manpowerNeedDetailsData;
    }

    public int getAllRequestsFlag() {
	return allRequestsFlag;
    }

    public void setAllRequestsFlag(int allRequestsFlag) {
	this.allRequestsFlag = allRequestsFlag;
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

    public Unit getSelectedUnit() {
	return selectedUnit;
    }

    public void setSelectedUnit(Unit selectedUnit) {
	this.selectedUnit = selectedUnit;
    }

    public int getPageSize() {
	return pageSize;
    }
}
