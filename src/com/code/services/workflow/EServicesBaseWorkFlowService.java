package com.code.services.workflow;

import java.util.ArrayList;
import java.util.List;

import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFProcess;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.WFTaskData;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.eservices.EservicesProcessesEnum;
import com.code.enums.eservices.WFTaskURLsEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.webservicesclients.eservices.EServicesWorkFlowClient;
import com.code.integration.webservicesclients.eservices.WFGeneralProcessClient;

public class EServicesBaseWorkFlowService {

    /*************************************************** getters ***********************************************************/
    public static WFTask getWFTaskById(Long taskId) throws BusinessException {
	try {
	    com.code.integration.parameters.eservices.workflow.WFTask wfTask = EServicesWorkFlowClient.getWFTaskById(taskId);
	    return toHCMWFTask(wfTask);
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage());
	}
    }

    public static WFTaskData getWFTaskDataById(Long taskId) throws BusinessException {
	try {
	    com.code.integration.parameters.eservices.workflow.WFTaskData wfTaskData = EServicesWorkFlowClient.getWFTaskDataById(taskId);
	    return toHCMWFTaskData(wfTaskData);
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage());
	}
    }

    public static WFInstance getWFInstanceById(Long instanceId) throws BusinessException {
	try {
	    com.code.integration.parameters.eservices.workflow.WFInstance wfInstance = EServicesWorkFlowClient.getWFInstanceById(instanceId);
	    return toHCMWFInstance(wfInstance);
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage());
	}
    }

    public static List<WFTaskData> getWFInstanceCompletedTasksData(Long instanceId, Long taskId)
	    throws BusinessException {
	try {
	    List<com.code.integration.parameters.eservices.workflow.WFTaskData> wfTaskDataList = EServicesWorkFlowClient.getWFInstanceCompletedTasksData(instanceId, taskId);
	    List<WFTaskData> hcmWFTaskDataList = new ArrayList<WFTaskData>();
	    for (com.code.integration.parameters.eservices.workflow.WFTaskData eserviceWFTaskData : wfTaskDataList) {
		hcmWFTaskDataList.add(toHCMWFTaskData(eserviceWFTaskData));
	    }
	    return hcmWFTaskDataList;
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage());
	}
    }

    /************************************ workflow actions ***********************************************/
    public static void doApproval(Long requesterId, WFTask hcmWFTask, Long mainEmpId, Long secondEmpId,
	    WFTaskActionsEnum action, Long transactionId, Long processCode) throws BusinessException {
	com.code.integration.parameters.eservices.workflow.WFTask eservicesWFTask = toEServicesWFTask(hcmWFTask, processCode);
	WFGeneralProcessClient.doApproval(requesterId, eservicesWFTask, mainEmpId, secondEmpId, action, transactionId);
    }

//    public static void doNotifyTasks(List<WFTask> hcmNotificationTasks, Long processCode) throws BusinessException {
//	List<com.code.integration.parameters.eservices.workflow.WFTask> eservicesNotificationTasks = new ArrayList<com.code.integration.parameters.eservices.workflow.WFTask>();
//	for (WFTask notificationTask : hcmNotificationTasks) {
//	    notificationTask.setSelected(true);
//	    eservicesNotificationTasks.add(toEServicesWFTask(notificationTask, processCode));
//	}
//	EServicesWorkFlowClient.doNotifyTasks(eservicesNotificationTasks);
//    }

    /************************************ inbox actions ***********************************************/
    private static Boolean isEservicesProcess(Long processCode) {
	if (processCode != null) {
	    EservicesProcessesEnum[] eservicesProcessesEnum = EservicesProcessesEnum.values();
	    for (int i = 0; i < eservicesProcessesEnum.length; i++) {
		if (eservicesProcessesEnum[i].getCode() == processCode) {
		    return true;
		}
	    }
	}
	return false;
    }

    private static List<WFTaskData> searchEservicesWFTasksData(Long assigneeId, Long beneficiaryId, String taskOwnerName, Long processGroupId, Long processId, Boolean isRunning, Integer taskRole, Boolean isDESC) throws BusinessException {
	try {
	    List<WFTaskData> hcmWFTaskDataList = new ArrayList<WFTaskData>();
	    List<com.code.integration.parameters.eservices.workflow.WFTaskData> wfTaskDataList = EServicesWorkFlowClient.searchWFTasksData(assigneeId, taskRole, taskOwnerName, null, Integer.parseInt(processGroupId.toString()), processId, isRunning, isDESC);
	    for (com.code.integration.parameters.eservices.workflow.WFTaskData eserviceWFTaskData : wfTaskDataList) {
		hcmWFTaskDataList.add(toHCMWFTaskData(eserviceWFTaskData));
	    }
	    return hcmWFTaskDataList;
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage());
	}
    }

    public static List<WFTaskData> searchWFTasksData(Long assigneeId, Long beneficiaryId, String taskOwnerName, Long processGroupId, Long processId, Boolean isRunning, int taskRole, Boolean isDESC) throws BusinessException {
	try {
	    List<WFTaskData> hcmWFTaskDataList = new ArrayList<WFTaskData>();
	    if (processGroupId == 0 && (processId == 0 || (processId != 0 && isEservicesProcess(processId)))) {
		hcmWFTaskDataList.addAll(searchEservicesWFTasksData(assigneeId, beneficiaryId, taskOwnerName, processGroupId, processId, isRunning, taskRole -1, isDESC));
	    } else if (processGroupId != 0) {
		List<WFProcess> processes = BaseWorkFlow.getWFGroupProcesses(processGroupId);
		for (WFProcess process : processes) {
		    if (isEservicesProcess(process.getProcessId()) && (processId == 0 || isEservicesProcess(processId))) {
			hcmWFTaskDataList.addAll(searchEservicesWFTasksData(assigneeId, beneficiaryId, taskOwnerName, processGroupId, processId, isRunning, taskRole -1, isDESC));
			break;
		    }
		}
	    }
	    hcmWFTaskDataList.addAll(BaseWorkFlow.searchWFTasksData(assigneeId, beneficiaryId, taskOwnerName, processGroupId, processId, isRunning, taskRole, isDESC));
	    return hcmWFTaskDataList;
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage());
	}
    }

    public static void closeWFInstancesByNotification(List<WFTask> tasks) throws BusinessException {
    }

    /************************************ header actions ***********************************************/
    public static String countWFTasks(Long loginEmpId, Integer notificationFlag) throws BusinessException {
	try {
	    Long count = BaseWorkFlow.countWFTasks(loginEmpId, notificationFlag) + EServicesWorkFlowClient.countWFTasks(loginEmpId, notificationFlag);
	    return count.toString();
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage());
	}
    }

    public static List<Object> countWFDelegations(long empId) throws BusinessException {
	try {
	    return BaseWorkFlow.countWFDelegations(empId); // TODO: add eservices delegations when needed
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage());
	}
    }

    /************************************ map from eservices to hcm ***********************************************/
    private static WFTask toHCMWFTask(com.code.integration.parameters.eservices.workflow.WFTask eservicesWFTask) {
	WFTask hcmWFTask = new WFTask();
	hcmWFTask.setTaskId(eservicesWFTask.getId());
	hcmWFTask.setInstanceId(eservicesWFTask.getInstanceId());
	hcmWFTask.setAssigneeId(eservicesWFTask.getAssigneeId());
	hcmWFTask.setOriginalId(eservicesWFTask.getOriginalId());
	hcmWFTask.setHijriAssignDate(eservicesWFTask.getAssignDate());
	hcmWFTask.setAssignDate(eservicesWFTask.getAssignGregDate());
	hcmWFTask.setHijriAssignDateString(eservicesWFTask.getAssignDateString());
	hcmWFTask.setTaskUrl(eservicesWFTask.getTaskURL());
	hcmWFTask.setAssigneeWfRole(eservicesWFTask.getAssigneeWfRole());
	hcmWFTask.setAction(eservicesWFTask.getAction());
	hcmWFTask.setHijriActionDate(eservicesWFTask.getActionDate());
	hcmWFTask.setActionDate(eservicesWFTask.getActionGregDate());
	hcmWFTask.setHijriActionDateString(eservicesWFTask.getActionDateString());
	hcmWFTask.setNotes(eservicesWFTask.getNotes());
	hcmWFTask.setRefuseReasons(eservicesWFTask.getRefuseReasons());
	hcmWFTask.setExternalLocationId(eservicesWFTask.getExternalLocationId());
	hcmWFTask.setEmailNotified(eservicesWFTask.getEmailNotified());
	hcmWFTask.setFlexField1(eservicesWFTask.getFlexField1());
	hcmWFTask.setFlexField2(eservicesWFTask.getFlexField2());
	hcmWFTask.setFlexField3(eservicesWFTask.getFlexField3());
	hcmWFTask.setPriority(eservicesWFTask.getPriority());
	hcmWFTask.setProcessGroupId(eservicesWFTask.getProcessGroupId());
	hcmWFTask.setLevel(eservicesWFTask.getStepOrder().toString());
	hcmWFTask.setArabicDetailsSummary(eservicesWFTask.getArabicDetailsSummary());
	hcmWFTask.setEnglishDetailsSummary(eservicesWFTask.getEnglishDetailsSummary());
	return hcmWFTask;
    }

    private static WFTaskData toHCMWFTaskData(com.code.integration.parameters.eservices.workflow.WFTaskData eservicesWFTaskData) {
	WFTaskData hcmWFTAskData = new WFTaskData();
	hcmWFTAskData.setTaskId(eservicesWFTaskData.getId());
	hcmWFTAskData.setInstanceId(eservicesWFTaskData.getInstanceId());
	hcmWFTAskData.setProcessId(eservicesWFTaskData.getProcessId());
	hcmWFTAskData.setProcessName(eservicesWFTaskData.getProcessName());
	hcmWFTAskData.setProcessGroupId(eservicesWFTaskData.getProcessGroupId());
	hcmWFTAskData.setAssigneeId(eservicesWFTaskData.getAssigneeId());
	hcmWFTAskData.setOriginalId(eservicesWFTaskData.getOriginalId());
	hcmWFTAskData.setOriginalCategoryId(eservicesWFTaskData.getOriginalCategoryId());
	hcmWFTAskData.setOriginalRankDesc(eservicesWFTaskData.getOriginalRankDesc());
	hcmWFTAskData.setOriginalJobDesc(eservicesWFTaskData.getOriginalJobDesc());
	hcmWFTAskData.setOriginalUnitFullName(eservicesWFTaskData.getOriginalDeptDesc());
	hcmWFTAskData.setDelegatingId(eservicesWFTaskData.getDelegatingId());
	hcmWFTAskData.setDelegatingName(eservicesWFTaskData.getDelegatingName());
	hcmWFTAskData.setSummary(eservicesWFTaskData.getSummary());
	hcmWFTAskData.setOriginalNumber(eservicesWFTaskData.getEmpNo());
	hcmWFTAskData.setOriginalName(eservicesWFTaskData.getOriginalName());
	hcmWFTAskData.setHijriAssignDate(eservicesWFTaskData.getAssignDate());
	hcmWFTAskData.setAssignDate(eservicesWFTaskData.getAssignGregDate());
	hcmWFTAskData.setHijriAssignDateString(eservicesWFTaskData.getAssignDateString());
	hcmWFTAskData.setTaskUrl(getProcessHcmUrl(eservicesWFTaskData.getProcessId()) + "?taskId=" + eservicesWFTaskData.getId());
	hcmWFTAskData.setAssigneeWfRole(eservicesWFTaskData.getAssigneeWfRole());
	hcmWFTAskData.setAction(eservicesWFTaskData.getAction());
	hcmWFTAskData.setHijriActionDate(eservicesWFTaskData.getActionDate());
	hcmWFTAskData.setActionDate(eservicesWFTaskData.getActionGregDate());
	hcmWFTAskData.setHijriActionDateString(eservicesWFTaskData.getActionDateString());
	hcmWFTAskData.setNotes(eservicesWFTaskData.getNotes());
	hcmWFTAskData.setRefuseReasons(eservicesWFTaskData.getRefuseReasons());
	hcmWFTAskData.setExternalLocationId(eservicesWFTaskData.getExternalLocationId());
	hcmWFTAskData.setEmailNotified(eservicesWFTaskData.getEmailNotified());
	hcmWFTAskData.setFlexField1(eservicesWFTaskData.getFlexField1());
	hcmWFTAskData.setFlexField2(eservicesWFTaskData.getFlexField2());
	hcmWFTAskData.setFlexField3(eservicesWFTaskData.getFlexField3());
	hcmWFTAskData.setPriority(eservicesWFTaskData.getPriority());
	hcmWFTAskData.setLevel(eservicesWFTaskData.getStepOrder().toString());
	hcmWFTAskData.setTaskOwnerName(eservicesWFTaskData.getTaskOwnerName());
	hcmWFTAskData.setTaskOwnerEmpNo(eservicesWFTaskData.getTaskOwnerEmpNo());
	// hcmWFTAskData.setRequesterReferenceName(eservicesWFTaskData.getTaskOwnerReferenceDesc());
	hcmWFTAskData.setArabicDetailsSummary(eservicesWFTaskData.getArabicDetailsSummary());
	hcmWFTAskData.setEnglishDetailsSummary(eservicesWFTaskData.getEnglishDetailsSummary());
	return hcmWFTAskData;
    }

    private static WFInstance toHCMWFInstance(com.code.integration.parameters.eservices.workflow.WFInstance eservicesWFInstance) {
	WFInstance hcmWFInstance = new WFInstance();
	hcmWFInstance.setInstanceId(eservicesWFInstance.getId());
	hcmWFInstance.setRequesterId(eservicesWFInstance.getRequesterId());
	hcmWFInstance.setHijriRequestDate(eservicesWFInstance.getRequestDate());
	hcmWFInstance.setHijriRequestDateString(eservicesWFInstance.getRequestDateString());
	hcmWFInstance.setCreatorId(eservicesWFInstance.getCreatorId());
	hcmWFInstance.setSummary(eservicesWFInstance.getSummary());
	hcmWFInstance.setStatus(eservicesWFInstance.getStatus());
	hcmWFInstance.setParentInstanceId(eservicesWFInstance.getParentInstanceId());
	hcmWFInstance.setProcessId(eservicesWFInstance.getProcessId());
	return hcmWFInstance;
    }

    /*********************************** map from hcm to eservices ***********************************************/
    private static com.code.integration.parameters.eservices.workflow.WFTask toEServicesWFTask(WFTask hcmWFTask, Long processCode) {
	com.code.integration.parameters.eservices.workflow.WFTask eservicesWFTAsk = new com.code.integration.parameters.eservices.workflow.WFTask();
	eservicesWFTAsk.setId(hcmWFTask.getTaskId());
	eservicesWFTAsk.setInstanceId(hcmWFTask.getInstanceId());
	eservicesWFTAsk.setAssigneeId(hcmWFTask.getAssigneeId());
	eservicesWFTAsk.setOriginalId(hcmWFTask.getOriginalId());
	eservicesWFTAsk.setAssignDate(hcmWFTask.getHijriAssignDate());
	eservicesWFTAsk.setAssignGregDate(hcmWFTask.getAssignDate());

	eservicesWFTAsk.setTaskURL(getProcessEservicesUrl(processCode));

	eservicesWFTAsk.setAssigneeWfRole(hcmWFTask.getAssigneeWfRole());
	eservicesWFTAsk.setAction(hcmWFTask.getAction());
	eservicesWFTAsk.setActionDate(hcmWFTask.getHijriActionDate());
	eservicesWFTAsk.setActionGregDate(hcmWFTask.getActionDate());
	eservicesWFTAsk.setActionDateString(hcmWFTask.getHijriActionDateString());
	eservicesWFTAsk.setNotes(hcmWFTask.getNotes());
	eservicesWFTAsk.setRefuseReasons(hcmWFTask.getRefuseReasons());
	eservicesWFTAsk.setExternalLocationId(hcmWFTask.getExternalLocationId());
	eservicesWFTAsk.setEmailNotified(hcmWFTask.getEmailNotified());
	eservicesWFTAsk.setFlexField1(hcmWFTask.getFlexField1());
	eservicesWFTAsk.setFlexField2(hcmWFTask.getFlexField2());
	eservicesWFTAsk.setFlexField3(hcmWFTask.getFlexField3());
	eservicesWFTAsk.setPriority(hcmWFTask.getPriority());
	eservicesWFTAsk.setProcessGroupId(hcmWFTask.getProcessGroupId());
	eservicesWFTAsk.setStepOrder(Integer.parseInt(hcmWFTask.getLevel()));
	eservicesWFTAsk.setArabicDetailsSummary(hcmWFTask.getArabicDetailsSummary());
	eservicesWFTAsk.setEnglishDetailsSummary(hcmWFTask.getEnglishDetailsSummary());
	return eservicesWFTAsk;
    }

    /************************************ others ***********************************************/
    public static String getProcessEservicesUrl(Long processCode) {
	WFTaskURLsEnum[] wfTaskURLsEnum = WFTaskURLsEnum.values();
	for (int i = 0; i < wfTaskURLsEnum.length; i++) {
	    if (wfTaskURLsEnum[i].getCode().equals(processCode)) {
		return wfTaskURLsEnum[i].getEservicesUrl();
	    }
	}
	return "";
    }

    public static String getProcessHcmUrl(Long processCode) {
	WFTaskURLsEnum[] wfTaskURLsEnum = WFTaskURLsEnum.values();
	for (int i = 0; i < wfTaskURLsEnum.length; i++) {
	    if (wfTaskURLsEnum[i].getCode().equals(processCode)) {
		return wfTaskURLsEnum[i].getHcmUrl();
	    }
	}
	return "";
    }
}
