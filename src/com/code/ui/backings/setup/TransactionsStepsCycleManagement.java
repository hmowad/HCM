package com.code.ui.backings.setup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.enums.eservices.EServicesCategoryEnum;
import com.code.enums.eservices.EServicesRanksEnum;
import com.code.enums.eservices.EmployeeRegionEnum;
import com.code.enums.eservices.StoppingCriteriaEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.eservices.workflow.WFProcessCycle;
import com.code.integration.webservicesclients.eservices.EServicesWorkFlowClient;
import com.code.services.util.CommonService;
import com.code.services.util.Log4jService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ViewScoped
@ManagedBean(name = "transactionsStepsCycleManagement")
public class TransactionsStepsCycleManagement extends BaseBacking implements Serializable {

    private int pageSize = 5;

    private String processName;
    private Long jobCategory;
    private Long employeeRegion;
    private Long rankId;
    private Long approvalStopPoint;
    private List<WFProcessCycle> searchResult;
    private List<WFProcessCycle> wFProcessCycleToBeDisplayed;
    private List<Rank> searchRanks;
    private List<Rank> tableRanks;
    private List<StoppingCriteriaEnum> departmentTypeList;

    private String selectedProcessesIds;
    private String selectedProcessesNames;
    
    private String ranksNamesString;
    private String ranksIdsString;


    public TransactionsStepsCycleManagement() {
	reset();
	searchResult = new ArrayList<WFProcessCycle>();
	wFProcessCycleToBeDisplayed = new ArrayList<WFProcessCycle>();
	departmentTypeList = new ArrayList<StoppingCriteriaEnum>();
    }

    public void getProcess() {
	String[] processesIDs = selectedProcessesIds.split(",");
	String[] processesNames = selectedProcessesNames.split(",");
	if (processesIDs != null && processesIDs.length > 0) {
	    WFProcessCycle wfprocessCycle = new WFProcessCycle();
	    wfprocessCycle.setProcessId(Long.parseLong(processesIDs[0]));
	    wfprocessCycle.setProcessArabicName(processesNames[0]);
//	    wfprocessCycle.setProcessCode(wfProcess.getCode());
	    wfprocessCycle.setNewRow(true);
	    wFProcessCycleToBeDisplayed.add(0,wfprocessCycle);
	}
    }

    public void employeeRegionListener(Long employeeRegionId) {
	if (employeeRegionId == EmployeeRegionEnum.ALL.getCode()) {
	    departmentTypeList = new ArrayList<>();
	    departmentTypeList.add(StoppingCriteriaEnum.PRESIDENCY);
	} else if (employeeRegionId == EmployeeRegionEnum.DIRECTORATE_EMPLOYEE.getCode()) {
	    departmentTypeList = new ArrayList<>();
	    departmentTypeList.add(StoppingCriteriaEnum.PRESIDENCY);
	    departmentTypeList.add(StoppingCriteriaEnum.ASSISTANT_PRESIDENCY);

	} else if (employeeRegionId == EmployeeRegionEnum.REGION_EMPLOYEE.getCode()) {
	    departmentTypeList = new ArrayList<>();
	    departmentTypeList.add(StoppingCriteriaEnum.PRESIDENCY);
	    departmentTypeList.add(StoppingCriteriaEnum.REGION_COMMANDER);
	    departmentTypeList.add(StoppingCriteriaEnum.ASSISTANT_REGION_COMMANDER);
	}
    }

    public void jobCategoryListener(Long jobCategoryId, Boolean search) {
	try {
	    if (search) {
		searchRanks = CommonService.getRanks(null, new Long[] { jobCategoryId });
	    } else {
		tableRanks = CommonService.getRanks(null, new Long[] { jobCategoryId });
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	    Log4jService.traceErrorException(TransactionsStepsCycleManagement.class, e, "TransactionsStepsCycleManagement");
	}

    }

    /********************************* ActionButtons ******************************************/

    public void search() {
	searchResult.clear();
	wFProcessCycleToBeDisplayed.clear();
	try {
	    searchResult = EServicesWorkFlowClient.getAllWFProcessesCycle(null, processName, jobCategory, employeeRegion, rankId, approvalStopPoint, null);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	    Log4jService.traceErrorException(TransactionsStepsCycleManagement.class, e, "TransactionsStepsCycleManagement");
	}
	if (searchResult != null) {
	    wFProcessCycleToBeDisplayed = new ArrayList<WFProcessCycle>(searchResult);
	    for (WFProcessCycle processCycle : wFProcessCycleToBeDisplayed) {
		if (processCycle.getJobId() == null) {
		    processCycle.setJobId(EServicesCategoryEnum.ALL.getCode());
		}
		if (processCycle.getRegionId() == null) {
		    processCycle.setRegionId((long) EmployeeRegionEnum.ALL.getCode());
		}
	    }
	}
    }

    public void reset() {
	processName = "";
	jobCategory = -1L;
	employeeRegion = -1L;
	rankId = -1L;
	approvalStopPoint = -1L;
	searchRanks = new ArrayList<Rank>();

    }

    public void save(WFProcessCycle cycle) {
	try {
	    Boolean Enabled;
	    if (cycle.getId() == null) {
		if (cycle.getJobId() == 0) {
		    setServerSideErrorMessages(getMessage("error_jobIdMandatory"));
		    return;
		}
		if (cycle.getRegionId() == 0) {
		    setServerSideErrorMessages(getMessage("error_regionIdMandatory"));
		    return;
		}
		if (ranksIdsString == null || ranksIdsString.isEmpty()) {
		    setServerSideErrorMessages(getMessage("error_rankMandatory"));
		    return;
		}

		List<Long> selectedRanksIds = new ArrayList<Long>();
		String[] ranksIDsArray = ranksIdsString.split(",");
		for(String rankString: ranksIDsArray){
		    selectedRanksIds.add(Long.parseLong(rankString));
		}
		
		List<String> selectedRanksDescriptions = new ArrayList<String>();
		String[] ranksDescriptionsArray = ranksNamesString.split(",");
		for(String rankDescription: ranksDescriptionsArray){
		    selectedRanksDescriptions.add(rankDescription);
		}
		
		cycle.setSelectedRanksIds(selectedRanksIds);
		cycle.setSelectedRanksDescriptions(selectedRanksDescriptions);
		cycle.setRankId("," + ranksIdsString + ",");
		cycle.setRankDescription("," + ranksNamesString + ",");
		cycle.setId(EServicesWorkFlowClient.saveOrUpdateWFProcessCycles(cycle));
		cycle.setNewRow(false);
		setServerSideSuccessMessages(getParameterizedMessage("label_processCycleSaved"));

	    } else {
		Enabled = cycle.getEnabled();
		EServicesWorkFlowClient.saveOrUpdateWFProcessCycles(cycle);
		if (Enabled) {
		    setServerSideSuccessMessages(getParameterizedMessage("notify_processCycleEnabled"));
		} else {
		    setServerSideSuccessMessages(getParameterizedMessage("notify_processCycleDisabled"));
		}
	    }

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	    Log4jService.traceErrorException(TransactionsStepsCycleManagement.class, e, "TransactionsStepsCycleManagement");
	    return;
	}

    }

    /********************************* EnumsGetters ******************************************/

    public List<EServicesCategoryEnum> getCategoryEnum() {
	return Arrays.asList(EServicesCategoryEnum.values());
    }

    public List<EmployeeRegionEnum> getEmployeeRegionEnum() {
	return Arrays.asList(EmployeeRegionEnum.values());
    }

    public List<StoppingCriteriaEnum> getDepartmentTypeEnum() {
	return Arrays.asList(StoppingCriteriaEnum.values());
    }

    public List<EServicesRanksEnum> getRanksEnum() {
	return Arrays.asList(EServicesRanksEnum.values());
    }

    public String getRankDescription(Long rankId) {
	return EServicesRanksEnum.valueOfNumber(rankId);
    }

    /********************************* Setters&Getters ***************************************/

    public String getProcessName() {
	return processName;
    }

    public void setProcessName(String processName) {
	this.processName = processName;
    }

    public Long getJobCategory() {
	return jobCategory;
    }

    public void setJobCategory(Long jobCategory) {
	this.jobCategory = jobCategory;
    }

    public Long getEmployeeRegion() {
	return employeeRegion;
    }

    public void setEmployeeRegion(Long employeeRegion) {
	this.employeeRegion = employeeRegion;
    }

    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
    }

    public Long getApprovalStopPoint() {
	return approvalStopPoint;
    }

    public void setApprovalStopPoint(Long approvalStopPoint) {
	this.approvalStopPoint = approvalStopPoint;
    }

    public List<WFProcessCycle> getSearchResult() {
	return searchResult;
    }

    public void setSearchResult(List<WFProcessCycle> searchResult) {
	this.searchResult = searchResult;
    }

    public List<WFProcessCycle> getwFProcessCycleToBeDisplayed() {
	return wFProcessCycleToBeDisplayed;
    }

    public void setwFProcessCycleToBeDisplayed(List<WFProcessCycle> wFProcessCycleToBeDisplayed) {
	this.wFProcessCycleToBeDisplayed = wFProcessCycleToBeDisplayed;
    }

    public List<Rank> getSearchRanks() {
	return searchRanks;
    }

    public void setSearchRanks(List<Rank> searchRanks) {
	this.searchRanks = searchRanks;
    }

    public List<Rank> getTableRanks() {
	return tableRanks;
    }

    public void setTableRanks(List<Rank> tableRanks) {
	this.tableRanks = tableRanks;
    }

    public List<StoppingCriteriaEnum> getDepartmentTypeList() {
	return departmentTypeList;
    }

    public void setDepartmentTypeList(List<StoppingCriteriaEnum> departmentTypeList) {
	this.departmentTypeList = departmentTypeList;
    }

    public String getSelectedProcessesIds() {
	return selectedProcessesIds;
    }

    public void setSelectedProcessesIds(String selectedProcessesIds) {
	this.selectedProcessesIds = selectedProcessesIds;
    }

    public String getSelectedProcessesNames() {
	return selectedProcessesNames;
    }

    public void setSelectedProcessesNames(String selectedProcessesNames) {
	this.selectedProcessesNames = selectedProcessesNames;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public String getRanksNamesString() {
	return ranksNamesString;
    }

    public void setRanksNamesString(String ranksNamesString) {
	this.ranksNamesString = ranksNamesString;
    }

    public String getRanksIdsString() {
	return ranksIdsString;
    }

    public void setRanksIdsString(String ranksIdsString) {
	this.ranksIdsString = ranksIdsString;
    }

}