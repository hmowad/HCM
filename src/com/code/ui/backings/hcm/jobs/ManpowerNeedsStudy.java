package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.jobs.ManpowerNeedData;
import com.code.dal.orm.hcm.organization.jobs.ManpowerNeedDetailData;
import com.code.dal.orm.hcm.organization.units.Unit;
import com.code.dal.orm.workflow.hcm.organization.jobs.WFManpowerNeed;
import com.code.enums.FlagsEnum;
import com.code.enums.ManpowerNeedStatusEnum;
import com.code.enums.ManpowerNeedTypesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.ManpowerNeedsService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.ManpowerNeedsWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "manpowerNeedsStudy")
@ViewScoped
public class ManpowerNeedsStudy extends WFBaseBacking {

    /** 1 for Officers and 2 for Soldiers **/
    private int mode;

    private WFManpowerNeed manpowerNeedStudyRequest;
    private ManpowerNeedData manpowerNeedStudyData;
    private List<ManpowerNeedData> manpowerNeedRequestsAndStudies;
    private Map<Long, List<ManpowerNeedDetailData>> manpowerNeedRequestsAndStudiesDetailsMap;
    private List<ManpowerNeedDetailData> manpowerNeedStudyDetails;

    private String selectedManpowerNeedRequestsAndStudiesIds;

    private ManpowerNeedData selectedManpowerNeedRequestOrStudy;

    private List<EmployeeData> reviewerEmps;
    private Long selectedReviewerEmpId;

    private Unit selectedUnit;

    private final int pageSize = 10;

    public ManpowerNeedsStudy() {
	super.init();
	if (getRequest().getParameter("mode") != null) {
	    mode = Integer.parseInt(getRequest().getParameter("mode"));
	    switch (mode) {
	    case 1:
		processId = WFProcessesEnum.OFFICERS_MANPOWER_NEED_STUDY.getCode();
		setScreenTitle(getMessage("title_manpowerNeedsStudyForOfficers"));
		break;
	    case 2:
		processId = WFProcessesEnum.SOLDIERS_MANPOWER_NEED_STUDY.getCode();
		setScreenTitle(getMessage("title_manpowerNeedsStudyForSoldiers"));
		break;
	    default:
		this.setServerSideErrorMessages(getMessage("error_URLError"));
	    }

	    intializeStudyData();

	} else {
	    this.setServerSideErrorMessages(getMessage("error_URLError"));
	}
    }

    private void intializeStudyData() {
	try {
	    manpowerNeedRequestsAndStudiesDetailsMap = new HashMap<Long, List<ManpowerNeedDetailData>>();
	    selectedUnit = new Unit();

	    if (getRequest().getParameter("studyId") != null && this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		manpowerNeedStudyRequest = new WFManpowerNeed();
		manpowerNeedStudyData = ManpowerNeedsService.getManpowerNeedDataById(Long.parseLong(getRequest().getParameter("studyId")));
		manpowerNeedRequestsAndStudies = ManpowerNeedsService.getManpowerNeedsDataByStudyId(manpowerNeedStudyData.getId());
		manpowerNeedStudyDetails = ManpowerNeedsService.getManpowerNeedDetailsDataByManpowerNeedId(manpowerNeedStudyData.getId());

		// if the study's status is not submitted and the physical unit of the logged in user equals the study's requesting unit
		// then the role is still requester, else a viewer.
		if (ManpowerNeedStatusEnum.NOT_SUBMITTED.getCode() != manpowerNeedStudyData.getStatus().intValue()
			|| !this.loginEmpData.getPhysicalUnitId().equals(manpowerNeedStudyData.getRequestingUnitId())) {
		    this.role = WFTaskRolesEnum.VIEWER.getCode();
		} else {
		    // adjust task url to remove the study id parameter
		    int startIndex = taskUrl.indexOf("&studyId=");
		    int endIndex = -1;

		    if (startIndex != -1) {
			endIndex = taskUrl.indexOf("&", startIndex + 1);

			if (endIndex == -1)
			    endIndex = taskUrl.length();
		    } else {
			startIndex = taskUrl.indexOf("studyId=");
			endIndex = taskUrl.indexOf("&", startIndex + 1);

			if (endIndex == -1)
			    endIndex = taskUrl.length() - 1;

			endIndex++;
		    }

		    String toBeReplacedString = taskUrl.substring(startIndex, endIndex);
		    taskUrl = taskUrl.replace(toBeReplacedString, "");
		}
	    } else {
		if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		    manpowerNeedStudyRequest = new WFManpowerNeed();
		    manpowerNeedStudyData = new ManpowerNeedData();
		    manpowerNeedStudyData.setCategoryId((long) mode);
		    manpowerNeedStudyData.setTransactionDate(HijriDateService.getHijriSysDate());
		    manpowerNeedStudyData.setRequestingUnitId(this.loginEmpData.getPhysicalUnitId());
		    manpowerNeedStudyData.setRequestingRegionId(this.loginEmpData.getPhysicalRegionId());
		    manpowerNeedStudyData.setStatus(ManpowerNeedStatusEnum.NOT_SUBMITTED.getCode());
		    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == this.loginEmpData.getPhysicalRegionId().longValue())
			manpowerNeedStudyData.setNeedType(ManpowerNeedTypesEnum.MAIN_STUDY.getCode());
		    else
			manpowerNeedStudyData.setNeedType(ManpowerNeedTypesEnum.STUDY.getCode());
		    manpowerNeedRequestsAndStudies = new ArrayList<ManpowerNeedData>();
		    manpowerNeedStudyDetails = new ArrayList<ManpowerNeedDetailData>();
		} else {
		    manpowerNeedStudyRequest = ManpowerNeedsWorkFlow.getWFManpowerNeedByInstanceId(instance.getInstanceId());
		    manpowerNeedStudyData = ManpowerNeedsService.getManpowerNeedDataById(manpowerNeedStudyRequest.getManpowerNeedId());
		    manpowerNeedRequestsAndStudies = ManpowerNeedsService.getManpowerNeedsDataByStudyId(manpowerNeedStudyData.getId());
		    manpowerNeedStudyDetails = ManpowerNeedsService.getManpowerNeedDetailsDataByManpowerNeedId(manpowerNeedStudyData.getId());

		    if (this.role.equals(WFTaskRolesEnum.MANAGER_REDIRECT.getCode())) {
			selectedReviewerEmpId = 0L;
			reviewerEmps = EmployeesService.getManagerEmployees(currentEmployee.getEmpId());
		    } else if (role.equals(WFTaskRolesEnum.NOTIFICATION.getCode())) {
			String prevNotifyTaskAction = prevTasks.get(prevTasks.size() - 1).getAction();
			if (prevNotifyTaskAction.equals(WFTaskActionsEnum.SUBMIT_TO_STUDY.getCode())) {
			    notificationMessage = this.getMessage("notify_studySubmittedToMainStudy");
			    instanceApproved = new Integer(FlagsEnum.ON.getCode());
			}
		    }
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addManpowerNeedRequestsAndStudies() {
	if (selectedManpowerNeedRequestsAndStudiesIds != null && !selectedManpowerNeedRequestsAndStudiesIds.isEmpty()) {
	    String duplicatedUnitsIfAny = "";
	    int duplicatedUnitsCount = 0;
	    String comma = "";

	    List<ManpowerNeedData> manpowerNeedDataList = new ArrayList<ManpowerNeedData>();
	    try {
		manpowerNeedDataList = ManpowerNeedsService.getManpowerNeedsDataByIdsString(selectedManpowerNeedRequestsAndStudiesIds);
	    } catch (BusinessException e) {
		this.setServerSideErrorMessages(getMessage(e.getMessage()));
		return;
	    }

	    for (int i = 0; i < manpowerNeedDataList.size(); i++) {
		try {
		    validateDuplicateManpowerNeed(manpowerNeedDataList.get(i).getId());
		    manpowerNeedRequestsAndStudies.add(manpowerNeedDataList.get(i));
		} catch (BusinessException e) {
		    duplicatedUnitsIfAny += comma + " - " + manpowerNeedDataList.get(i).getRequestingUnitFullName();
		    duplicatedUnitsCount++;
		    comma = ", \n ";
		}
	    }
	    selectedManpowerNeedRequestOrStudy = new ManpowerNeedData();
	    if (duplicatedUnitsCount > 0)
		this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfRequests", new Object[] { duplicatedUnitsCount, duplicatedUnitsIfAny }));
	}
    }

    private void validateDuplicateManpowerNeed(Long manpowerNeedId) throws BusinessException {
	for (ManpowerNeedData manpowerNeedData : manpowerNeedRequestsAndStudies) {
	    if (manpowerNeedId.equals(manpowerNeedData.getId()))
		throw new BusinessException("error_repeatedRequest");
	}
    }

    public void deleteManpowerNeed(ManpowerNeedData manpowerNeedData) {
	try {
	    if (manpowerNeedData.getStudyId() != null)
		ManpowerNeedsService.disjoinManpowerNeedRequestOrStudy(manpowerNeedRequestsAndStudies, manpowerNeedData, this.loginEmpData.getEmpId());

	    manpowerNeedRequestsAndStudies.remove(manpowerNeedData);
	    manpowerNeedRequestsAndStudiesDetailsMap.remove(manpowerNeedData.getId());
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectManpowerNeedRequestOrStudy(ManpowerNeedData manpowerNeedRequestOrStudy) {
	selectedManpowerNeedRequestOrStudy = manpowerNeedRequestOrStudy;
	try {
	    if (manpowerNeedRequestsAndStudiesDetailsMap.get(selectedManpowerNeedRequestOrStudy.getId()) == null
		    || manpowerNeedRequestsAndStudiesDetailsMap.get(selectedManpowerNeedRequestOrStudy.getId()).isEmpty()) {

		List<Long> manpowerNeedRequestsAndStudiesIds = new ArrayList<Long>();
		manpowerNeedRequestsAndStudiesIds.add(selectedManpowerNeedRequestOrStudy.getId());

		if (ManpowerNeedTypesEnum.STUDY.getCode() == selectedManpowerNeedRequestOrStudy.getNeedType().intValue()) {
		    List<ManpowerNeedData> manpowerNeedRequestsData = ManpowerNeedsService.getManpowerNeedsDataByStudyId(selectedManpowerNeedRequestOrStudy.getId());
		    for (ManpowerNeedData manpowerNeedRequest : manpowerNeedRequestsData) {
			manpowerNeedRequestsAndStudiesIds.add(manpowerNeedRequest.getId());
		    }
		}

		List<ManpowerNeedDetailData> manpowerNeedRequestsAndStudiesDetailsData = ManpowerNeedsService.getManpowerNeedDetailsDataByManpowerNeedsIds(manpowerNeedRequestsAndStudiesIds.toArray(new Long[0]));
		for (ManpowerNeedDetailData manpowerNeedRequestsAndStudiesDetailData : manpowerNeedRequestsAndStudiesDetailsData) {
		    // if main study, initialize the main study suggested count, else if study, initialize the study suggested count
		    if (ManpowerNeedTypesEnum.MAIN_STUDY.getCode() == manpowerNeedStudyData.getNeedType().intValue()) {
			if (manpowerNeedRequestsAndStudiesDetailData.getMainStudySuggestedCount() == null) {
			    if (ManpowerNeedTypesEnum.REQUEST.getCode() == selectedManpowerNeedRequestOrStudy.getNeedType().longValue())
				manpowerNeedRequestsAndStudiesDetailData.setMainStudySuggestedCount(manpowerNeedRequestsAndStudiesDetailData.getRequestSuggestedCount());
			    else
				manpowerNeedRequestsAndStudiesDetailData.setMainStudySuggestedCount(manpowerNeedRequestsAndStudiesDetailData.getStudySuggestedCount());
			}
		    } else {
			if (manpowerNeedRequestsAndStudiesDetailData.getStudySuggestedCount() == null)
			    manpowerNeedRequestsAndStudiesDetailData.setStudySuggestedCount(manpowerNeedRequestsAndStudiesDetailData.getRequestSuggestedCount());
		    }
		}

		manpowerNeedRequestsAndStudiesDetailsMap.put(selectedManpowerNeedRequestOrStudy.getId(), manpowerNeedRequestsAndStudiesDetailsData);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addNewManpowerNeedStudyDetail() {
	if (selectedUnit != null) {
	    ManpowerNeedDetailData manpowerNeedStudyDetailData = new ManpowerNeedDetailData();
	    manpowerNeedStudyDetailData.setAddedOnStudyFlag(FlagsEnum.ON.getCode());
	    manpowerNeedStudyDetailData.setUnitId(selectedUnit.getId());
	    manpowerNeedStudyDetailData.setUnitFullName(selectedUnit.getFullName());
	    manpowerNeedStudyDetailData.setUnitHKey(selectedUnit.gethKey());

	    if (ManpowerNeedTypesEnum.MAIN_STUDY.getCode() == manpowerNeedStudyData.getNeedType().intValue())
		manpowerNeedStudyDetailData.setMainStudySuggestedCount(0);
	    else
		manpowerNeedStudyDetailData.setStudySuggestedCount(0);

	    manpowerNeedStudyDetails.add(0, manpowerNeedStudyDetailData);
	}
    }

    public void calculateEmployeesCounts(ManpowerNeedDetailData manpowerNeedStudyDetailData) {
	try {
	    manpowerNeedStudyDetailData.setOfficialEmployeesCount(EmployeesService.countEmployeesByOfficialUnitHkeyAndMinorSpecAndCategory(manpowerNeedStudyDetailData.getUnitHKey(),
		    manpowerNeedStudyDetailData.getMinorSpecializationId(), getCategoriesIdsArrayByMode(mode)).intValue());
	    manpowerNeedStudyDetailData.setPhysicalEmployeesCount(EmployeesService.countEmployeesByPhysicalUnitHkeyAndMinorSpecAndCategory(manpowerNeedStudyDetailData.getUnitHKey(),
		    manpowerNeedStudyDetailData.getMinorSpecializationId(), getCategoriesIdsArrayByMode(mode)).intValue());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void deleteManpowerNeedStudyDetail(ManpowerNeedDetailData manpowerNeedStudyDetailData) {
	try {
	    if (manpowerNeedStudyDetailData.getId() != null)
		ManpowerNeedsService.deleteManpowerNeedStudyDetail(manpowerNeedStudyDetailData, this.loginEmpData.getEmpId());
	    manpowerNeedStudyDetails.remove(manpowerNeedStudyDetailData);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    /*****************************************************************************************************************/

    // save the study data by requester
    public void save() throws BusinessException {
	try {
	    ManpowerNeedsService.saveManpowerNeedStudy(manpowerNeedStudyData, manpowerNeedRequestsAndStudies, manpowerNeedRequestsAndStudiesDetailsMap, manpowerNeedStudyDetails, requester.getEmpId());
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // initialize the process by requester after saving the study data
    public String initProcess() {
	try {
	    ManpowerNeedsWorkFlow.initManpowerNeedStudy(requester, manpowerNeedStudyData, manpowerNeedRequestsAndStudies, manpowerNeedRequestsAndStudiesDetailsMap, manpowerNeedStudyDetails, processId, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // approve by secondary reviewer employee (the requester)
    public String approveSRE() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudySRE(requester, instance, manpowerNeedStudyData, manpowerNeedRequestsAndStudies, manpowerNeedRequestsAndStudiesDetailsMap, manpowerNeedStudyDetails, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // reject by secondary reviewer employee (the requester)
    public String rejectSRE() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudySRE(requester, instance, manpowerNeedStudyData, manpowerNeedRequestsAndStudies, manpowerNeedRequestsAndStudiesDetailsMap, manpowerNeedStudyDetails, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // approve by secondary sign manager
    public String approveSSM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudySSM(requester, instance, manpowerNeedStudyData, currentTask, 1);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // return to secondary reviewer employee by secondary sign manager
    public String modifySSM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudySSM(requester, instance, manpowerNeedStudyData, currentTask, 2);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // reject by secondary sign manager
    public String rejectSSM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudySSM(requester, instance, manpowerNeedStudyData, currentTask, 0);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // redirect to reviewer employee (the requester) by manager redirect
    public String redirectMR() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudyMR(requester, instance, currentTask, selectedReviewerEmpId);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // approve by reviewer employee (the requester) after saving the study data
    public String approveRE() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudyRE(requester, instance, manpowerNeedStudyData, manpowerNeedRequestsAndStudies, manpowerNeedRequestsAndStudiesDetailsMap, manpowerNeedStudyDetails, currentTask, true);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // reject by reviewer employee (the requester)
    public String rejectRE() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudyRE(requester, instance, manpowerNeedStudyData, manpowerNeedRequestsAndStudies, manpowerNeedRequestsAndStudiesDetailsMap, manpowerNeedStudyDetails, currentTask, false);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // TODO merge the submit and approve?
    // submit to study by sign manager
    public String submitSM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudySM(requester, instance, manpowerNeedStudyData, currentTask, 1);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // approve by sign manager
    public String approveSM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudySM(requester, instance, manpowerNeedStudyData, currentTask, 1);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // return to the reviewer employee by sign manager
    public String modifySM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudySM(requester, instance, manpowerNeedStudyData, currentTask, 2);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    // reject by sign manager
    public String rejectSM() {
	try {
	    ManpowerNeedsWorkFlow.doManpowerNeedStudySM(requester, instance, manpowerNeedStudyData, currentTask, 0);
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

    public int getMode() {
	return mode;
    }

    public String getSelectedManpowerNeedRequestsAndStudiesIds() {
	return selectedManpowerNeedRequestsAndStudiesIds;
    }

    public void setSelectedManpowerNeedRequestsAndStudiesIds(String selectedManpowerNeedRequestsAndStudiesIds) {
	this.selectedManpowerNeedRequestsAndStudiesIds = selectedManpowerNeedRequestsAndStudiesIds;
    }

    public ManpowerNeedData getManpowerNeedStudyData() {
	return manpowerNeedStudyData;
    }

    public void setManpowerNeedStudyData(ManpowerNeedData manpowerNeedStudyData) {
	this.manpowerNeedStudyData = manpowerNeedStudyData;
    }

    public List<ManpowerNeedData> getManpowerNeedRequestsAndStudies() {
	return manpowerNeedRequestsAndStudies;
    }

    public void setManpowerNeedRequestsAndStudies(List<ManpowerNeedData> manpowerNeedRequestsAndStudies) {
	this.manpowerNeedRequestsAndStudies = manpowerNeedRequestsAndStudies;
    }

    public Map<Long, List<ManpowerNeedDetailData>> getManpowerNeedRequestsAndStudiesDetailsMap() {
	return manpowerNeedRequestsAndStudiesDetailsMap;
    }

    public void setManpowerNeedRequestsAndStudiesDetailsMap(Map<Long, List<ManpowerNeedDetailData>> manpowerNeedRequestsAndStudiesDetailsMap) {
	this.manpowerNeedRequestsAndStudiesDetailsMap = manpowerNeedRequestsAndStudiesDetailsMap;
    }

    public List<ManpowerNeedDetailData> getManpowerNeedStudyDetails() {
	return manpowerNeedStudyDetails;
    }

    public void setManpowerNeedStudyDetails(List<ManpowerNeedDetailData> manpowerNeedStudyDetails) {
	this.manpowerNeedStudyDetails = manpowerNeedStudyDetails;
    }

    public ManpowerNeedData getSelectedManpowerNeedRequestOrStudy() {
	return selectedManpowerNeedRequestOrStudy;
    }

    public void setSelectedManpowerNeedRequestOrStudy(ManpowerNeedData selectedManpowerNeedRequestOrStudy) {
	this.selectedManpowerNeedRequestOrStudy = selectedManpowerNeedRequestOrStudy;
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
