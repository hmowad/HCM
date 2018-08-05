package com.code.ui.backings.hcm.terminations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.terminations.TerminationReason;
import com.code.dal.orm.hcm.terminations.TerminationRecordData;
import com.code.dal.orm.hcm.terminations.TerminationRecordDetailData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.workflow.hcm.terminations.WFTerminationData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.TerminationReasonsEnum;
import com.code.enums.TerminationRecordStatusEnum;
import com.code.enums.TerminationTypeFlagEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.TerminationsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.TerminationsWorkflow;
import com.code.ui.backings.base.WFBaseBacking;

public class TerminationsRecordsBase extends WFBaseBacking {

    protected List<TerminationReason> terminationReasons;
    protected List<Region> regions;
    protected List<Rank> ranks;

    // Insertion details
    protected int mode;
    protected Long rankId;
    protected Date terminationDueDateFrom;
    protected Date terminationDueDateTo;
    protected WFTerminationData wfTermination;

    protected String detailSearchEmpName;

    protected Long selectedEmployeeDataId;

    protected TerminationRecordData terminationRecordData;

    protected List<TerminationRecordDetailData> terminationRecordDetailDataList;

    // used to hold the original complete list of details (used in case of collective reasons)
    protected List<TerminationRecordDetailData> originalTerminationRecordDetailDataList;

    protected TerminationRecordDetailData selectedTerminationRecordDetailData;
    protected List<TerminationTransactionData> terminationExtensionTransactionDataList;

    protected List<Category> civilianCategories;
    protected List<EmployeeData> internalCopies;

    private boolean resignation;
    protected boolean filteredBySearchFlag = false;

    protected Date today;

    protected final int pageSize = 10;

    public void initialize() {
	try {
	    super.init();

	    Long recordId = null;
	    String modeStr = getRequest().getParameter("mode") == null ? null : getRequest().getParameter("mode").toString();
	    if (modeStr == null)
		throw new Exception("error_general");

	    mode = Integer.parseInt(modeStr);
	    regions = CommonService.getAllRegions();
	    terminationReasons = TerminationsService.getTerminationReasons((long) mode);
	    setResignation(false);
	    filteredBySearchFlag = false;
	    originalTerminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();

	    today = HijriDateService.getHijriSysDate();

	    if (mode == CategoriesEnum.SOLDIERS.getCode()) {
		this.processId = WFProcessesEnum.SOLDIERS_TERMINATION_RECORD.getCode();
	    } else if (mode == CategoriesEnum.PERSONS.getCode()) {
		this.processId = WFProcessesEnum.CIVILIANS_TERMINATION_RECORD.getCode();
		ranks = CommonService.getRanks(null, new Long[] { (long) mode });
	    }
	    if (!this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		wfTermination = TerminationsWorkflow.getWFTerminationByInstanceId(instance.getInstanceId());
		recordId = (wfTermination.getRecordId());
	    } else {
		recordId = getRequest().getParameter("recordId") == null ? null : Long.parseLong(getRequest().getParameter("recordId").toString());
	    }

	    if (recordId != null) {
		terminationRecordData = TerminationsService.getTerminationRecordDataById(recordId);
		internalCopies = EmployeesService.getEmployeesByIdsString(terminationRecordData.getInternalCopies());
		terminationRecordDetailDataList = TerminationsService.getTerminationRecordDetailsByRecordId(terminationRecordData.getId());
		if (terminationRecordDetailDataList != null && terminationRecordDetailDataList.size() > 0)
		    selectTerminationsDetailData(terminationRecordDetailDataList.get(0));
		if (terminationRecordData.getCategoryId() != CategoriesEnum.OFFICERS.getCode() && terminationRecordData.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()) {
		    if (terminationRecordData.getCategoryId() != CategoriesEnum.CONTRACTORS.getCode())
			terminationReasons = TerminationsService.getTerminationReasons(CategoriesEnum.PERSONS.getCode());
		    else
			terminationReasons = TerminationsService.getTerminationReasons(CategoriesEnum.CONTRACTORS.getCode());
		}

		// copy all details from details list here
		originalTerminationRecordDetailDataList.addAll(terminationRecordDetailDataList);
	    } else {
		// initialize
		terminationRecordData = new TerminationRecordData();
		terminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();
		terminationExtensionTransactionDataList = new ArrayList<TerminationTransactionData>();
		terminationDueDateTo = today;

		terminationRecordData.setTypeFlag(TerminationTypeFlagEnum.RECORD.getCode());
		terminationRecordData.setStatus(TerminationRecordStatusEnum.CURRENT.getCode());
		terminationRecordData.setCategoryId((long) mode);
		setRankId(new Long(FlagsEnum.ALL.getCode()));

		internalCopies = new ArrayList<EmployeeData>();

		if (mode == CategoriesEnum.OFFICERS.getCode())
		    terminationRecordData.setReasonId(TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode());
		else if (mode == CategoriesEnum.SOLDIERS.getCode())
		    terminationRecordData.setReasonId(TerminationReasonsEnum.SOLDIERS_REACHING_RETIREMENT_AGE.getCode());
		else if (mode == CategoriesEnum.PERSONS.getCode())
		    terminationRecordData.setReasonId(TerminationReasonsEnum.PERSONS_REACHING_RETIREMENT_AGE.getCode());
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}

    }

    // add detail to an already existing record (Eligibility for Retirement)
    public void constructTerminationsDetailData() {
	try {
	    if (selectedEmployeeDataId != null && selectedEmployeeDataId.intValue() != FlagsEnum.OFF.getCode()) {

		resetTerminationRecordDetailList();
		selectedTerminationRecordDetailData = TerminationsWorkflow.addCollectiveReasonsTerminationRecordDetail(instance, terminationRecordData, terminationRecordDetailDataList, selectedEmployeeDataId, loginEmpData.getEmpId());

		// add newly added detail to original list to make data consistent
		originalTerminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();
		originalTerminationRecordDetailDataList.addAll(terminationRecordDetailDataList);

		// if (terminationRecordDetailDataList.size() > 0)
		selectTerminationsDetailData(selectedTerminationRecordDetailData);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    /*
     * used when adding new detail to list in case current list is (filtered) by search used only with collective reasons (OFFICERS_REACHING_RETIREMENT_AGE, and OFFICERS_COMPLETION_PERIOD_CURRENT_RANK )
     */
    public void resetTerminationRecordDetailList() {
	// to enhance time, check if current state is filteredBySearch or not
	if (filteredBySearchFlag) {
	    filteredBySearchFlag = false; // set filteredBySearch to false
	    terminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>(); // reset terminationRecordDetailsList
	    terminationRecordDetailDataList.addAll(originalTerminationRecordDetailDataList);

	    detailSearchEmpName = "";
	}
    }

    // add single detail to an already existing record
    public void constructTerminationsDetailDataSingular() {
	try {
	    if (selectedEmployeeDataId != null && selectedEmployeeDataId.intValue() != FlagsEnum.OFF.getCode()) {
		TerminationsService.addSingularReasonsTerminationRecordDetail(terminationRecordData, terminationRecordDetailDataList, selectedEmployeeDataId, loginEmpData.getEmpId());
		if (terminationRecordDetailDataList.size() == 1) {
		    selectTerminationsDetailData(terminationRecordDetailDataList.get(0));
		    originalTerminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();
		    originalTerminationRecordDetailDataList.add(selectedTerminationRecordDetailData);
		}
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    // save report and get eligible details if applied
    public void addModifyTerminationRecordAndTerminationDetails() {

	if (terminationRecordData.getReasonId().equals(TerminationReasonsEnum.OFFICERS_REACHING_RETIREMENT_AGE.getCode())
		|| terminationRecordData.getReasonId().equals(TerminationReasonsEnum.SOLDIERS_REACHING_RETIREMENT_AGE.getCode())
		|| terminationRecordData.getReasonId().equals(TerminationReasonsEnum.PERSONS_REACHING_RETIREMENT_AGE.getCode())
		|| terminationRecordData.getReasonId().equals(TerminationReasonsEnum.OFFICERS_COMPLETION_PERIOD_CURRENT_RANK.getCode())) {
	    if (terminationDueDateFrom != null && terminationDueDateFrom.after(terminationDueDateTo)) {
		setServerSideErrorMessages(getMessage("error_terminationDateFromMustBefore"));
		return;
	    }
	}

	try {

	    if (terminationRecordData.getRegionId().intValue() == FlagsEnum.ALL.getCode())
		terminationRecordData.setRegionId(null);

	    TerminationsService.addTerminationRecordAndTerminationDetails(terminationRecordData, terminationRecordDetailDataList, rankId, terminationDueDateFrom, terminationDueDateTo, loginEmpData.getEmpId());

	    // add newly added detail to original list to make data consistent
	    originalTerminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();
	    originalTerminationRecordDetailDataList.addAll(terminationRecordDetailDataList);

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectTerminationsDetailData(TerminationRecordDetailData terminationRecordDetailData) {
	this.selectedTerminationRecordDetailData = terminationRecordDetailData;
	try {
	    terminationExtensionTransactionDataList = TerminationsService.getTerminationExtensionTransactionsByEmpId(selectedTerminationRecordDetailData.getEmpId());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // save one already existing detail
    public void addModifyTerminationDetail() {
	try {
	    List<TerminationRecordDetailData> terminationRecordDetailDataListTemp = new ArrayList<TerminationRecordDetailData>();
	    terminationRecordDetailDataListTemp.add(selectedTerminationRecordDetailData);
	    TerminationsService.validateTerminationRecordDetails(terminationRecordData, terminationRecordDetailDataListTemp);
	    TerminationsService.addModifyTerminationRecordDetail(terminationRecordData, terminationRecordDetailDataListTemp, loginEmpData.getEmpId());

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    // delete one detail
    public void deleteTerminationsDetailData(TerminationRecordDetailData terminationRecordDetailData) {
	try {
	    TerminationsWorkflow.deleteTerminationRecordDetail(instance, terminationRecordDetailDataList, terminationRecordDetailData, this.loginEmpData.getEmpId());
	    // delete from original too to make data consistent
	    originalTerminationRecordDetailDataList.remove(terminationRecordDetailData);
	    selectedTerminationRecordDetailData = null;
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void searchTerminationsDetails() {

	// reset list that is used for view
	terminationRecordDetailDataList = new ArrayList<TerminationRecordDetailData>();

	for (TerminationRecordDetailData terminationRecordDetailDataItr : originalTerminationRecordDetailDataList) {
	    if (detailSearchEmpName == null || terminationRecordDetailDataItr.getEmpName().contains(detailSearchEmpName))
		terminationRecordDetailDataList.add(terminationRecordDetailDataItr);
	}

	filteredBySearchFlag = true;
	if (terminationRecordDetailDataList.size() > 0)
	    selectTerminationsDetailData(terminationRecordDetailDataList.get(0));
	else
	    selectedTerminationRecordDetailData = null;

    }

    // print details filtered by empName
    public void printTerminationsReport() {

	try {
	    byte[] bytes = TerminationsService.getTerminationRecordBytes(terminationRecordData.getId(), (long) mode, terminationRecordData.getReasonId(), detailSearchEmpName, null);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // Getters and setters
    public List<TerminationReason> getTerminationReasons() {
	return terminationReasons;
    }

    public void setTerminationReasons(List<TerminationReason> terminationReasons) {
	this.terminationReasons = terminationReasons;
    }

    public List<Region> getRegions() {
	return regions;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public int getMode() {
	return mode;
    }

    public String getAttachments() {
	return attachments;
    }

    public void setAttachments(String attachments) {
	this.attachments = attachments;
    }

    public Long getRankId() {
	return rankId;
    }

    public void setRankId(Long rankId) {
	this.rankId = rankId;
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

    public String getDetailSearchEmpName() {
	return detailSearchEmpName;
    }

    public void setDetailSearchEmpName(String detailSearchEmpName) {
	this.detailSearchEmpName = detailSearchEmpName;
    }

    public Long getSelectedEmployeeDataId() {
	return selectedEmployeeDataId;
    }

    public void setSelectedEmployeeDataId(Long selectedEmployeeDataId) {
	this.selectedEmployeeDataId = selectedEmployeeDataId;
    }

    public TerminationRecordData getTerminationRecordData() {
	return terminationRecordData;
    }

    public void setTerminationRecordData(TerminationRecordData terminationRecordData) {
	this.terminationRecordData = terminationRecordData;
    }

    public List<TerminationRecordDetailData> getTerminationRecordDetailDataList() {
	return terminationRecordDetailDataList;
    }

    public void setTerminationRecordDetailDataList(List<TerminationRecordDetailData> terminationRecordDetailDataList) {
	this.terminationRecordDetailDataList = terminationRecordDetailDataList;
    }

    public TerminationRecordDetailData getSelectedTerminationRecordDetailData() {
	return selectedTerminationRecordDetailData;
    }

    public void setSelectedTerminationRecordDetailData(TerminationRecordDetailData selectedTerminationRecordDetailData) {
	this.selectedTerminationRecordDetailData = selectedTerminationRecordDetailData;
    }

    public List<TerminationTransactionData> getTerminationExtensionTransactionDataList() {
	return terminationExtensionTransactionDataList;
    }

    public List<TerminationRecordDetailData> getOriginalTerminationRecordDetailDataList() {
	return originalTerminationRecordDetailDataList;
    }

    public void setOriginalTerminationRecordDetailDataList(List<TerminationRecordDetailData> originalTerminationRecordDetailDataList) {
	this.originalTerminationRecordDetailDataList = originalTerminationRecordDetailDataList;
    }

    public void setTerminationExtensionTransactionDataList(List<TerminationTransactionData> terminationExtensionTransactionDataList) {
	this.terminationExtensionTransactionDataList = terminationExtensionTransactionDataList;
    }

    public boolean isResignation() {
	return resignation;
    }

    public void setResignation(boolean isResignation) {
	this.resignation = isResignation;
    }

    public int getPageSize() {
	return pageSize;
    }

    public List<Category> getCivilianCategories() {
	return civilianCategories;
    }

    public void setCivilianCategories(List<Category> civilianCategories) {
	this.civilianCategories = civilianCategories;
    }

    public List<EmployeeData> getInternalCopies() {
	return internalCopies;
    }

    public void setInternalCopies(List<EmployeeData> internalCopies) {
	this.internalCopies = internalCopies;
    }

}
