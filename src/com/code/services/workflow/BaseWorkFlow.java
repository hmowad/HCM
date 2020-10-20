package com.code.services.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.workflow.WFDelegation;
import com.code.dal.orm.workflow.WFDelegationData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFInstanceBeneficiary;
import com.code.dal.orm.workflow.WFInstanceData;
import com.code.dal.orm.workflow.WFNotificationsConfigDetailEmployeeData;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFProcess;
import com.code.dal.orm.workflow.WFProcessGroup;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.WFTaskData;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.webservicesclients.pushclient.PushNotificationRestClient;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;

public abstract class BaseWorkFlow extends BaseService {
    protected BaseWorkFlow() {
    }

    /****************************************** Instance Methods ****************************************************/
    protected static WFInstance addWFInstance(long processId, long requesterId, Date requestDate, Date hijriRequestDate, int status, String attachments, List<Long> instanceBeneficiariesIds, CustomSession session) throws BusinessException {
	try {
	    WFInstance instance = new WFInstance();
	    instance.setProcessId(processId);
	    instance.setRequesterId(requesterId);
	    instance.setRequestDate(requestDate);
	    instance.setHijriRequestDate(hijriRequestDate);
	    instance.setStatus(status);
	    instance.setAttachments(attachments);

	    DataAccess.addEntity(instance, session);
	    saveWFInstanceBeneficiaries(instance.getInstanceId(), instanceBeneficiariesIds, true, session);

	    return instance;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static WFInstance changeWFInstanceStatus(WFInstance instance, int newStatus, CustomSession session) throws DatabaseException {
	instance.setStatus(newStatus);

	DataAccess.updateEntity(instance, session);

	return instance;
    }

    protected static void updateWFInstanceAttachments(WFInstance instance, String attachments, CustomSession session) throws DatabaseException {
	instance.setAttachments(attachments);
	DataAccess.updateEntity(instance, session);
    }

    protected static void closeWFInstanceByAction(Long requesterId, WFInstance instance, WFTask task, String action, Long[] empIds, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    Set<Long> notifyEmpsIdsSet = new HashSet<Long>();

	    if (empIds != null && empIds.length > 0) {
		for (Long empId : empIds)
		    notifyEmpsIdsSet.add(empId);
	    }

	    List<WFTask> wfTasks = getWFInstanceTasks(instance.getInstanceId());
	    if (wfTasks != null && wfTasks.size() > 0) {
		for (WFTask wfTask : wfTasks) {
		    notifyEmpsIdsSet.add(wfTask.getOriginalId());
		}
	    }

	    changeWFInstanceStatus(instance, WFInstanceStatusEnum.DONE.getCode(), session);

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    notifyEmpsIdsSet.remove(instance.getRequesterId());
	    int subLevel = notifyEmpsIdsSet.size() > 0 ? 1 : 0;

	    completeWFTask(task, action, curDate, curHijriDate, instance.getInstanceId(), instance.getRequesterId(), instance.getRequesterId(), task.getTaskUrl(), WFTaskRolesEnum.NOTIFICATION.getCode(), task.getLevel() + (subLevel == 0 ? "" : ("." + (subLevel++))), session);

	    if (notifyEmpsIdsSet.size() > 0) {
		for (Long empId : notifyEmpsIdsSet) {
		    addWFTask(instance.getInstanceId(), getDelegate(empId, instance.getProcessId(), requesterId), empId, curDate, curHijriDate, task.getTaskUrl(), WFTaskRolesEnum.NOTIFICATION.getCode(), task.getLevel() + "." + (subLevel++), session);
		}
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void closeWFInstanceByNotification(WFInstance instance, WFTask task) throws BusinessException {
	try {
	    closeWFInstance(instance, task, WFTaskActionsEnum.NOTIFIED.getCode(), new Date(), HijriDateService.getHijriSysDate());
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void closeWFInstancesByNotification(List<WFInstance> instances, List<WFTask> tasks) throws BusinessException {

	validateCloseWFInstancesByNotification(tasks);

	CustomSession session = DataAccess.getSession();

	try {
	    Map<Long, WFInstance> instancesMap = new HashMap<Long, WFInstance>();
	    for (WFInstance wfInstance : instances) {
		instancesMap.put(wfInstance.getInstanceId(), wfInstance);
	    }

	    session.beginTransaction();
	    for (int i = 0; i < tasks.size(); i++) {
		closeWFInstance(instancesMap.get(tasks.get(i).getInstanceId()), tasks.get(i), WFTaskActionsEnum.NOTIFIED.getCode(), new Date(), HijriDateService.getHijriSysDate(), session);
	    }
	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    private static void closeWFInstance(WFInstance instance, WFTask task, String action, Date actionDate, Date hijriActionDate, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = isSessionOpened(useSession);

	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    setWFTaskAction(task, action, actionDate, hijriActionDate, session);

	    if (canChangeWFInstanceStatusToComplete(instance.getInstanceId()))
		changeWFInstanceStatus(instance, WFInstanceStatusEnum.COMPLETED.getCode(), session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void validateCloseWFInstancesByNotification(List<WFTask> tasks) throws BusinessException {
	for (WFTask wfTask : tasks) {
	    if (!wfTask.getAssigneeWfRole().equals(WFTaskRolesEnum.NOTIFICATION.getCode()) || wfTask.getAction() != null)
		throw new BusinessException("error_cannotCloseInstancesByNotification");
	}
    }

    protected static boolean canChangeWFInstanceStatusToDone(long instanceId) throws DatabaseException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_INSTANCE_ID", instanceId);
	Long count = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_CAN_CHANGE_WFINSTANCE_DONE.getCode(), qParams).get(0);

	if (count == 1)
	    return true;

	return false;
    }

    private static boolean canChangeWFInstanceStatusToComplete(long instanceId) throws BusinessException {
	if (countUncompletedWFInstanceTasks(instanceId) == 1)
	    return true;

	return false;
    }

    public static long countUncompletedWFInstanceTasks(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_CAN_CHANGE_WFINSTANCE_COMPLETE.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static WFInstance getWFInstanceById(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    List<WFInstance> result = DataAccess.executeNamedQuery(WFInstance.class, QueryNamesEnum.WF_GET_WFINSTANCE_BY_ID.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFInstance> getWFInstancesByIds(List<Long> instancesIds) throws BusinessException {

	Long[] instancesIdsArray = instancesIds.toArray(new Long[0]);

	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_INSTANCES_IDS", instancesIdsArray);

	List<Object> queryInfo = new ArrayList<Object>();
	queryInfo.add(QueryNamesEnum.WF_GET_WFINSTANCES_BY_IDS.getCode());
	queryInfo.add(qParams);

	return getManyEntities(WFInstance.class, queryInfo, instancesIdsArray);
    }

    public static WFInstanceData getWFInstanceDataById(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);

	    List<WFInstanceData> result = DataAccess.executeNamedQuery(WFInstanceData.class, QueryNamesEnum.WF_GET_WFINSTANCE_DATA_BY_ID.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFInstanceData> searchWFInstancesData(Long requesterId, Long beneficiaryId, long processGroupId, long processId, boolean isRunning, boolean isASC) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_REQUESTER_ID", requesterId == null ? FlagsEnum.ALL.getCode() : requesterId);
	    qParams.put("P_BENEFICIARY_ID", beneficiaryId == null ? FlagsEnum.ALL.getCode() : beneficiaryId);
	    qParams.put("P_PROCESS_GROUP_ID", processGroupId);
	    qParams.put("P_PROCESS_ID", processId);
	    qParams.put("P_STATUS_VALUES", isRunning ? new Integer[] { WFInstanceStatusEnum.RUNNING.getCode() } : new Integer[] { WFInstanceStatusEnum.DONE.getCode(), WFInstanceStatusEnum.COMPLETED.getCode() });

	    List<WFInstanceData> instances = DataAccess.executeNamedQuery(WFInstanceData.class, QueryNamesEnum.WF_SEARCH_WFINSTANCE_DATA.getCode(), qParams);

	    if (isASC)
		Collections.reverse(instances);

	    return instances;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Long getWFInstancesUnderProcessingCount(long requesterId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_REQUESTER_ID", requesterId);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_GET_WFINSTANCE_UNDER_PROCESSING_COUNT.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static long countWFInstancesByProcessesIds(Long[] processesIds, Integer[] statusesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    if (processesIds != null && processesIds.length > 0) {
		qParams.put("P_PROCESSES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_PROCESSES_IDS", processesIds);
	    } else {
		qParams.put("P_PROCESSES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_PROCESSES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    if (statusesIds != null && statusesIds.length > 0) {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUSES_IDS", statusesIds);
	    } else {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUSES_IDS", new Integer[] { FlagsEnum.ALL.getCode() });
	    }

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_COUNT_INSTANCES_BY_PROCESSES_IDS.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /****************************************************************************************************************/

    /************************************** Instance Beneficiaries Methods ******************************************/
    // Should be used only if there is a chance to add a beneficiary without taking a WorkFlow action.

    public static void addWFInstanceBeneficiaries(long instanceId, List<Long> instanceBeneficiariesIds, CustomSession session) throws BusinessException {
	if (instanceBeneficiariesIds == null || instanceBeneficiariesIds.isEmpty())
	    throw new BusinessException("error_transactionDataError");

	try {
	    for (Long newBeneficiaryId : instanceBeneficiariesIds) {
		WFInstanceBeneficiary wfInstanceBeneificary = new WFInstanceBeneficiary();
		wfInstanceBeneificary.setInstanceId(instanceId);
		wfInstanceBeneificary.setBeneficiaryId(newBeneficiaryId);
		DataAccess.addEntity(wfInstanceBeneificary, session);
	    }
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // Should be used only if there is a chance to remove a beneficiary without taking a WorkFlow action.
    public static void deleteWFInstanceBeneficiaries(long instanceId, List<Long> instanceBeneficiariesIds, CustomSession session) throws BusinessException {
	if (instanceBeneficiariesIds == null || instanceBeneficiariesIds.isEmpty())
	    throw new BusinessException("error_transactionDataError");

	try {
	    for (Long removedInstanceBeneficiaryId : instanceBeneficiariesIds) {
		WFInstanceBeneficiary wfInstanceBeneificary = new WFInstanceBeneficiary();
		wfInstanceBeneificary.setInstanceId(instanceId);
		wfInstanceBeneificary.setBeneficiaryId(removedInstanceBeneficiaryId);
		DataAccess.deleteEntity(wfInstanceBeneificary, session);
	    }
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // Should be used - if and only if - while taking a WorkFlow action.
    public static void updateWFInstanceBeneficiaries(long instanceId, List<Long> instanceBeneficiariesIds, CustomSession session) throws BusinessException {
	saveWFInstanceBeneficiaries(instanceId, instanceBeneficiariesIds, false, session);
    }

    private static void saveWFInstanceBeneficiaries(long instanceId, List<Long> instanceBeneficiariesIds, boolean isNewInstance, CustomSession session) throws BusinessException {

	if (instanceBeneficiariesIds == null || instanceBeneficiariesIds.isEmpty())
	    throw new BusinessException("error_transactionDataError");

	try {
	    Map<Long, WFInstanceBeneficiary> oldInstanceBeneficiariesMap = new HashMap<Long, WFInstanceBeneficiary>();

	    if (!isNewInstance) {
		List<WFInstanceBeneficiary> oldInstanceBeneficiaries = getWFInstanceBeneficiariesByInstanceId(instanceId);

		for (WFInstanceBeneficiary oldBeneficiary : oldInstanceBeneficiaries)
		    oldInstanceBeneficiariesMap.put(oldBeneficiary.getBeneficiaryId(), oldBeneficiary);
	    }

	    for (Long newBeneficiaryId : instanceBeneficiariesIds) {
		if (!oldInstanceBeneficiariesMap.containsKey(newBeneficiaryId)) {
		    WFInstanceBeneficiary wfInstanceBeneificary = new WFInstanceBeneficiary();
		    wfInstanceBeneificary.setInstanceId(instanceId);
		    wfInstanceBeneificary.setBeneficiaryId(newBeneficiaryId);
		    DataAccess.addEntity(wfInstanceBeneificary, session);
		} else {
		    oldInstanceBeneficiariesMap.remove(newBeneficiaryId);
		}
	    }

	    for (Long removedInstanceBeneficiaryId : oldInstanceBeneficiariesMap.keySet())
		DataAccess.deleteEntity(oldInstanceBeneficiariesMap.get(removedInstanceBeneficiaryId), session);

	} catch (BusinessException e) {
	    throw e;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<WFInstanceBeneficiary> getWFInstanceBeneficiariesByInstanceId(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    return DataAccess.executeNamedQuery(WFInstanceBeneficiary.class, QueryNamesEnum.WF_GET_WF_INSTANCE_BENEFICIARIES_BY_INSTANCE_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /********************************************** Task Methods ****************************************************/
    protected static WFTask addWFTask(long instanceId, long assigneeId, long originalId, Date assignDate, Date hijriAssignDate, String taskUrl, String assigneeWfRole, String level, CustomSession session) throws DatabaseException {
	try {
	    WFTask task = new WFTask();
	    task.setInstanceId(instanceId);
	    task.setAssigneeId(assigneeId);
	    task.setOriginalId(originalId);
	    task.setAssignDate(assignDate);
	    task.setHijriAssignDate(hijriAssignDate);
	    task.setTaskUrl(taskUrl);
	    task.setAssigneeWfRole(assigneeWfRole);
	    task.setLevel(level);
	    task.setOriginalUnitId(EmployeesService.getEmployeeData(originalId).getPhysicalUnitId());

	    DataAccess.addEntity(task, session);

	    PushNotificationRestClient.pushNotification(assigneeId, assigneeWfRole);

	    return task;
	} catch (Exception e) {
	    throw new DatabaseException(e.getMessage());
	}
    }

    protected static WFTask setWFTaskAction(WFTask task, String action, Date actionDate, Date hijriActionDate, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = isSessionOpened(useSession);

	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    task.setAction(action);
	    task.setActionDate(actionDate);
	    task.setHijriActionDate(hijriActionDate);

	    DataAccess.updateEntity(task, session);

	    if (!isOpenedSession)
		session.commitTransaction();

	    return task;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    protected static WFTask completeWFTask(WFTask curTask, String action, Date actionDate, Date hijriActionDate, long instanceId, long assigneeId, long originalId, String taskUrl, String assigneeWfRole, String level, CustomSession... useSession) throws DatabaseException {
	boolean isOpenedSession = isSessionOpened(useSession);

	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    setWFTaskAction(curTask, action, actionDate, hijriActionDate, session);
	    WFTask newTask = addWFTask(instanceId, assigneeId, originalId, actionDate, hijriActionDate, taskUrl, assigneeWfRole, level, session);

	    if (!isOpenedSession)
		session.commitTransaction();

	    return newTask;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw new DatabaseException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static Long[] invalidateWFTasksByAssigneesIdsOrOriginalsIds(Long[] assigneesIdsOrOriginalsIds, String refuseReasons, CustomSession session) throws BusinessException {
	if (session == null)
	    throw new BusinessException("error_general");
	if (assigneesIdsOrOriginalsIds == null || assigneesIdsOrOriginalsIds.length == 0 || refuseReasons == null || refuseReasons.isEmpty())
	    throw new BusinessException("error_transactionDataError");

	try {
	    long lastInstanceId = -1;

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    List<Object> tasksAndInstances = getManyEntities(Object.class, getRunningWFTasksForInvalidationByAssigneesIdsOrOriginalsIdsQueryInfo(assigneesIdsOrOriginalsIds), assigneesIdsOrOriginalsIds);
	    Set<Long> instancesIds = new HashSet<Long>();
	    for (Object taskAndInstance : tasksAndInstances) {
		WFTask task = (WFTask) (((Object[]) taskAndInstance)[0]);
		WFInstance instance = (WFInstance) (((Object[]) taskAndInstance)[1]);

		List<String> processesIdsExcludedFromInvalidation = new ArrayList<String>(Arrays.asList(ETRConfigurationService.getProcessesIdsExcludedFromInvalidation().split(",")));
		if (processesIdsExcludedFromInvalidation.contains(instance.getProcessId() + "")) {
		    Long originalUnitManagerId = task.getOriginalUnitId() == null ? null : UnitsService.getUnitById(task.getOriginalUnitId()).getPhysicalManagerId();
		    task.setAssigneeId((originalUnitManagerId == null || originalUnitManagerId.equals(task.getOriginalId())) ? null : originalUnitManagerId); // TODO: to be revised later
		    task.setOriginalId((originalUnitManagerId == null || originalUnitManagerId.equals(task.getOriginalId())) ? null : originalUnitManagerId);
		    DataAccess.updateEntity(task, session);
		} else {
		    instancesIds.add(instance.getInstanceId());
		    task.setRefuseReasons(refuseReasons);

		    if (instance.getInstanceId() == lastInstanceId) {
			// just reject this task as that means this task's instance has been closed (This happens at the parallel tasks).
			setWFTaskAction(task, WFTaskActionsEnum.REJECT.getCode(), curDate, curHijriDate, session);
		    } else {
			closeWFInstanceByAction(null, instance, task, WFTaskActionsEnum.REJECT.getCode(), null, session);
			lastInstanceId = instance.getInstanceId();
		    }
		}
	    }

	    return instancesIds.toArray(new Long[instancesIds.size()]);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Long[] invalidateWFTasksByProcessesIds(Long[] processesIds, String refuseReasons, CustomSession session) throws BusinessException {
	if (session == null)
	    throw new BusinessException("error_general");
	if (processesIds == null || processesIds.length == 0 || refuseReasons == null || refuseReasons.isEmpty())
	    throw new BusinessException("error_transactionDataError");

	try {
	    long lastInstanceId = -1;

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    List<Object> tasksAndInstances = getRunningWFTasksForInvalidationByProcessIds(processesIds);
	    Set<Long> instancesIds = new HashSet<Long>();
	    for (Object taskAndInstance : tasksAndInstances) {
		WFTask task = (WFTask) (((Object[]) taskAndInstance)[0]);
		WFInstance instance = (WFInstance) (((Object[]) taskAndInstance)[1]);

		instancesIds.add(instance.getInstanceId());
		task.setRefuseReasons(refuseReasons);

		if (instance.getInstanceId() == lastInstanceId) {
		    // just reject this task as that means this task's instance has been closed (This happens at the parallel tasks).
		    setWFTaskAction(task, WFTaskActionsEnum.REJECT.getCode(), curDate, curHijriDate, session);
		} else {
		    closeWFInstanceByAction(null, instance, task, WFTaskActionsEnum.REJECT.getCode(), null, session);
		    lastInstanceId = instance.getInstanceId();
		}
	    }

	    return instancesIds.toArray(new Long[instancesIds.size()]);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Delegate tasks to selected employee
     * 
     * @param tasksIds
     * @param delegatorId
     * @param delegateeId
     * @param userId
     * @throws BusinessException
     */
    public static void delegateWFTasks(List<Long> tasksIds, long delegatorId, long delegateeId, long userId) throws BusinessException {

	validateWFTasksDelegation(tasksIds, delegatorId, delegateeId);

	try {
	    List<WFTask> tasks = getWFTasksByIds(tasksIds);
	    for (WFTask task : tasks) {
		task.setAssigneeId(delegateeId);
		task.setSystemUser(userId + ""); // For Auditing.
		DataAccess.updateEntity(task);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void validateWFTasksDelegation(List<Long> tasksIds, long delegatorId, long delegateeId) throws BusinessException {
	if (tasksIds == null || tasksIds.isEmpty())
	    throw new BusinessException("error_selectTasksForDelegation");

	if (FlagsEnum.ALL.getCode() == delegatorId)
	    throw new BusinessException("error_employeeMandatory");

	if (FlagsEnum.ALL.getCode() == delegateeId)
	    throw new BusinessException("error_delegateeMandatory");

	if (delegatorId == delegateeId)
	    throw new BusinessException("error_CannotDelegateToTheSameEmployee");
    }

    public static void validateWFTaskRefuseReasonsAndNotes(int approvalFlag, String refuseReasons, String notes) throws BusinessException {
	if (approvalFlag == WFActionFlagsEnum.REJECT.getCode()) {
	    if (refuseReasons == null || refuseReasons.trim().isEmpty())
		throw new BusinessException("error_refuseReasonsManadatory");
	} else {
	    if (refuseReasons != null && !refuseReasons.trim().isEmpty())
		throw new BusinessException("error_refuseReasonsEmpty");

	    if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode() && (notes == null || notes.trim().isEmpty()))
		throw new BusinessException("error_notesManadatory");
	}
    }

    public static WFTask getWFTaskById(long taskId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TASK_ID", taskId);

	    List<WFTask> result = DataAccess.executeNamedQuery(WFTask.class, QueryNamesEnum.WF_GET_WFTASK_BY_ID.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFTask> getWFTasksByIds(List<Long> tasksIds) throws BusinessException {

	Long[] tasksIdsArray = tasksIds.toArray(new Long[0]);

	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_TASKS_IDS", tasksIdsArray);

	List<Object> queryInfo = new ArrayList<Object>();
	queryInfo.add(QueryNamesEnum.WF_GET_WFTASKS_BY_IDS.getCode());
	queryInfo.add(qParams);

	return getManyEntities(WFTask.class, queryInfo, tasksIdsArray);
    }

    private static List<WFTask> getWFInstanceTasks(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    return DataAccess.executeNamedQuery(WFTask.class, QueryNamesEnum.WF_GET_WFINSTANCE_TASKS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static List<WFTask> getWFInstanceTasksByRole(long instanceId, String role) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    qParams.put("P_ROLE", role);
	    return DataAccess.executeNamedQuery(WFTask.class, QueryNamesEnum.WF_GET_WFINSTANCE_TASKS_BY_ROLE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<Object> getRunningWFTasksForInvalidationByAssigneesIdsOrOriginalsIdsQueryInfo(Long[] assigneesIdsOrOriginalsIds) {
	List<Object> queryInfo = new ArrayList<Object>();
	queryInfo.add(QueryNamesEnum.WF_GET_RUNNING_WFTASK_FOR_INVALIDATION_BY_ASSIGNEES_OR_ORIGINALS.getCode());

	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_ASSIGNEES_IDS_ORIGINALS_IDS", assigneesIdsOrOriginalsIds);
	queryInfo.add(qParams);

	return queryInfo;
    }

    private static List<Object> getRunningWFTasksForInvalidationByProcessIds(Long[] processesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PROCESSES_IDS", processesIds);

	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.WF_GET_RUNNING_WFTASK_FOR_INVALIDATION_BY_PROCESSES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFTask> getWFInstanceCompletedTasksByLevelAndOriginalId(long instanceId, String level, long originalId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    qParams.put("P_LEVEL", level);
	    qParams.put("P_ORIGINAL_ID", originalId);
	    return DataAccess.executeNamedQuery(WFTask.class, QueryNamesEnum.WF_GET_WFINSTANCE_COMPLETED_TASKS_BY_LEVEL_AND_ORIGINAL_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static WFTaskData getWFTaskDataById(long taskId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TASK_ID", taskId);
	    List<WFTaskData> result = DataAccess.executeNamedQuery(WFTaskData.class, QueryNamesEnum.WF_GET_WFTASK_DATA_BY_ID.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFTaskData> searchWFTasksData(long assigneeId, Long beneficiaryId, String taskOwnerName, long processGroupId, long processId, boolean isRunning, int taskRole, boolean isDESC) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_BENEFICIRY_ID", beneficiaryId == null ? FlagsEnum.ALL.getCode() : beneficiaryId);
	    qParams.put("P_TASK_OWNER_NAME", "%" + taskOwnerName + "%");
	    qParams.put("P_PROCESS_GROUP_ID", processGroupId);
	    qParams.put("P_PROCESS_ID", processId);
	    qParams.put("P_RUNNING", isRunning ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    qParams.put("P_TASK_ROLE", taskRole);

	    List<WFTaskData> tasks = DataAccess.executeNamedQuery(WFTaskData.class, QueryNamesEnum.WF_SEARCH_WFTASK_DATA.getCode(), qParams);

	    if (isDESC)
		Collections.reverse(tasks);

	    return tasks;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Long countWFTasks(long assigneeId, int notificationFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_NOTIFICATION_ROLE", WFTaskRolesEnum.NOTIFICATION.getCode());
	    qParams.put("P_NOTIFICATION_FLAG", notificationFlag);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_COUNT_TASKS.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFTaskData> getWFInstanceTasksData(long instanceId) throws BusinessException {
	return getWFInstanceTasksData(instanceId, FlagsEnum.ALL.getCode());
    }

    public static List<WFTaskData> getNonNotificationWFInstanceTasksData(long instanceId) throws BusinessException {
	return getWFInstanceTasksData(instanceId, FlagsEnum.ON.getCode());
    }

    private static List<WFTaskData> getWFInstanceTasksData(long instanceId, int taskRole) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    qParams.put("P_TASK_ROLE", taskRole);
	    return DataAccess.executeNamedQuery(WFTaskData.class, QueryNamesEnum.WF_GET_WFINSTANCE_TASKS_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFTaskData> getWFInstanceCompletedTasksData(long instanceId, long taskId, String taskLevel) throws BusinessException {
	try {
	    String levels = taskLevel, tempLevel = taskLevel;

	    if (!taskLevel.equals("" + FlagsEnum.ALL.getCode())) {
		while (tempLevel.indexOf(".") != -1) {
		    tempLevel = tempLevel.substring(0, tempLevel.lastIndexOf('.'));
		    levels += "," + tempLevel;
		}
	    }

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    qParams.put("P_TASK_ID", taskId);

	    if (!taskLevel.equals("" + FlagsEnum.ALL.getCode())) {
		qParams.put("P_LEVELS_FLAG", FlagsEnum.ON.getCode());
	    } else {
		qParams.put("P_LEVELS_FLAG", FlagsEnum.ALL.getCode());
	    }

	    qParams.put("P_LEVELS", levels.split(","));

	    return DataAccess.executeNamedQuery(WFTaskData.class, QueryNamesEnum.WF_GET_WFINSTANCE_COMPLETED_TASKS_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFTaskData> getWFInstanceCompletedTasksDataOrderedByLevelLength(long instanceId, long taskId) throws BusinessException {
	try {

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    qParams.put("P_TASK_ID", taskId);

	    return DataAccess.executeNamedQuery(WFTaskData.class, QueryNamesEnum.WF_GET_WFINSTANCE_COMPLETED_TASKS_DATA_ORDERED_BY_LEVEL_LENGTH.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<String> getWFTaskSecurityUrls(long assigneeId, String taskUrl, long taskId, boolean isRunning) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_TASK_URL", "%" + taskUrl + "%");
	    qParams.put("P_TASK_ID", taskId);
	    qParams.put("P_RUNNING", isRunning ? FlagsEnum.ON.getCode() : FlagsEnum.ALL.getCode());
	    return DataAccess.executeNamedQuery(String.class, QueryNamesEnum.WF_TASK_SECURITY.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFTask> getUnassignedWFTasksByInstanceId(Long instanceId) throws BusinessException {
	return searchUnassignedWFTasks(instanceId);
    }

    public static List<WFTask> getAllUnassignedWFTasks() throws BusinessException {
	return searchUnassignedWFTasks(null);
    }

    private static List<WFTask> searchUnassignedWFTasks(Long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId == null ? FlagsEnum.ALL.getCode() : instanceId);
	    return DataAccess.executeNamedQuery(WFTask.class, QueryNamesEnum.WF_GET_UNASSIGNED_WF_TASKS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /****************************************************************************************************************/

    /********************************************** Groups - Processes Methods **************************************/

    public static List<Object> getWFProcessesGroupsApprovalCounts(long assigneeId, String[] assigneeWfRoles, Long[] processGroupIds, Long[] excludedProcessIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_ASSIGNEE_WF_ROLES", assigneeWfRoles);
	    qParams.put("P_PROCESS_GROUPS_IDS", processGroupIds);
	    qParams.put("P_EXCLUDED_PROCESS_IDS", excludedProcessIds);

	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.WF_GET_PROCESSES_GROUPS_APRRROVAL_COUNTS.getCode(), qParams);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFProcessGroup> getWFProcessesGroups() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(WFProcessGroup.class, QueryNamesEnum.WF_GET_PROCESSES_GROUPS.getCode(), null);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static WFProcessGroup getWFProcessesGroupById(Long id) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", id);
	    return DataAccess.executeNamedQuery(WFProcessGroup.class, QueryNamesEnum.WF_GET_PROCESSES_GROUP_BY_ID.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFProcess> getWFGroupProcesses(long processGroupId) throws BusinessException {
	return searchWFProcesses(processGroupId, null, null);
    }

    public static List<WFProcess> getWFGroupProcesses(long processGroupId, String processName, Long[] processesIds) throws BusinessException {
	return searchWFProcesses(processGroupId, processName, processesIds);
    }

    private static List<WFProcess> searchWFProcesses(long processGroupId, String processName, Long[] processesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_GROUP_ID", processGroupId);
	    qParams.put("P_PROCESS_NAME", (processName == null || processName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + processName + "%");
	    if (processesIds != null && processesIds.length > 0) {
		qParams.put("P_PROCESSES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_PROCESSES_IDS", processesIds);
	    } else {
		qParams.put("P_PROCESSES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_PROCESSES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    return DataAccess.executeNamedQuery(WFProcess.class, QueryNamesEnum.WF_GET_GROUP_PROCESSES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static WFProcess getWFProcess(long processId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PROCESS_ID", processId);
	    return DataAccess.executeNamedQuery(WFProcess.class, QueryNamesEnum.WF_GET_PROCESS.getCode(), qParams).get(0);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFProcess> getWFProcessesByGroupIdAndName(String processName, Long[] processGroupsIds, Long[] processesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PROCESS_NAME", (processName == null || processName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + processName + "%");
	    qParams.put("P_PROCESSES_IDS", processesIds);
	    if (processGroupsIds != null && processGroupsIds.length > 0) {
		qParams.put("P_GROUPS_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_GROUPS_IDS", processGroupsIds);
	    } else {
		qParams.put("P_GROUPS_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_GROUPS_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    return DataAccess.executeNamedQuery(WFProcess.class, QueryNamesEnum.WF_GET_PROCESSES_BY_GROUP_AND_NAME.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /****************************************************************************************************************/

    /********************************************** Delegation Methods **********************************************/

    public static void saveWFDelegation(long empId, long delegateId, Long processId, Long unitId, String hKey, List<WFDelegationData> totalDelegationList, List<WFDelegationData> partialDelegationList) throws BusinessException {
	if (empId == delegateId)
	    throw new BusinessException("error_CannotDelegateToYourself");

	// Total Delegation, All Units.
	if (processId == null && unitId == null) {
	    if (totalDelegationList != null) {
		for (WFDelegationData delegation : totalDelegationList) {
		    if (delegation.getDelegateId().equals(delegateId)) {
			if (delegation.getUnitId() == null) {
			    throw new BusinessException("error_TotalAndTotalAllUnitsSameEmployeeDelegationConflict");
			} else {
			    throw new BusinessException("error_TotalAndTotalAllUnitsSpecificUnitSameEmployeeDelegationConflict");
			}
		    } else {
			if (delegation.getUnitId() == null) {
			    throw new BusinessException("error_TotalAndTotalAllUnitsTwoEmployeesDelegationConflict");
			}
		    }
		}
	    }

	    if (partialDelegationList != null) {
		for (WFDelegationData delegation : partialDelegationList) {
		    if (delegation.getDelegateId().equals(delegateId)) {
			if (delegation.getUnitId() == null) {
			    throw new BusinessException("error_TotalAndPartialAllUnitsSameEmployeeDelegationConflict");
			} else {
			    throw new BusinessException("error_TotalAndPartialAllUnitsSpecificUnitSameEmployeeDelegationConflict");
			}
		    }
		}
	    }
	}

	// Total Delegation, Specific Unit.
	else if (processId == null && unitId != null) {
	    if (totalDelegationList != null) {
		for (WFDelegationData delegation : totalDelegationList) {
		    if (delegation.getDelegateId().equals(delegateId)) {
			if (delegation.getUnitId() == null) {
			    throw new BusinessException("error_TotalAndTotalAllUnitsSpecificUnitSameEmployeeDelegationConflict");
			} else if (delegation.getUnitId().equals(unitId)) {
			    throw new BusinessException("error_TotalAndTotalSameUnitSameEmployeeDelegationConflict");
			} else if (isHierarchical(hKey, delegation.getUnitHKey())) {
			    throw new BusinessException("error_TotalAndTotalParentUnitSameEmployeeDelegationConflict");
			}
		    } else {
			if (delegation.getUnitId() != null) {
			    if (delegation.getUnitId().equals(unitId)) {
				throw new BusinessException("error_TotalAndTotalSameUnitTwoEmployeesDelegationConflict");
			    } else if (isHierarchical(hKey, delegation.getUnitHKey())) {
				throw new BusinessException("error_TotalAndTotalParentUnitTwoEmployeesDelegationConflict");
			    }
			}
		    }
		}
	    }

	    if (partialDelegationList != null) {
		for (WFDelegationData delegation : partialDelegationList) {
		    if (delegation.getDelegateId().equals(delegateId)) {
			if (delegation.getUnitId() != null) {
			    if (delegation.getUnitId().equals(unitId)) {
				throw new BusinessException("error_TotalAndPartialSameUnitSameEmployeeDelegationConflict");
			    } else if (isHierarchical(hKey, delegation.getUnitHKey())) {
				throw new BusinessException("error_TotalAndPartialParentUnitSameEmployeeDelegationConflict");
			    }
			}
		    }
		}
	    }
	}

	// Partial Delegation, All Units.
	else if (processId != null && unitId == null) {
	    if (totalDelegationList != null) {
		for (WFDelegationData delegation : totalDelegationList) {
		    if (delegation.getDelegateId().equals(delegateId)) {
			if (delegation.getUnitId() == null) {
			    throw new BusinessException("error_TotalAndPartialAllUnitsSameEmployeeDelegationConflict");
			}
		    }
		}
	    }

	    if (partialDelegationList != null) {
		for (WFDelegationData delegation : partialDelegationList) {
		    if (delegation.getProcessId().longValue() == processId) {
			if (delegation.getDelegateId().equals(delegateId)) {
			    if (delegation.getUnitId() == null) {
				throw new BusinessException("error_PartialAndPartialAllUnitsSameEmployeeSameProcessDelegationConflict");
			    } else {
				throw new BusinessException("error_PartialAndPartialSpecificUnitSameEmployeeSameProcessDelegationConflict");
			    }
			} else {
			    if (delegation.getUnitId() == null) {
				throw new BusinessException("error_PartialAndPartialAllUnitsTwoEmployeesSameProcessDelegationConflict");
			    }
			}
		    }
		}
	    }

	}

	// Partial Delegation, Specific Unit.
	else if (processId != null && unitId != null) {
	    if (totalDelegationList != null) {
		for (WFDelegationData delegation : totalDelegationList) {
		    if (delegation.getDelegateId().equals(delegateId)) {
			if (delegation.getUnitId() == null) {
			    throw new BusinessException("error_TotalAndPartialAllUnitsSpecificUnitSameEmployeeDelegationConflict");
			} else if (delegation.getUnitId().equals(unitId)) {
			    throw new BusinessException("error_TotalAndPartialSameUnitSameEmployeeDelegationConflict");
			} else if (isHierarchical(hKey, delegation.getUnitHKey())) {
			    throw new BusinessException("error_TotalAndPartialParentUnitSameEmployeeDelegationConflict");
			}
		    }
		}
	    }

	    if (partialDelegationList != null) {
		for (WFDelegationData delegation : partialDelegationList) {
		    if (delegation.getProcessId().longValue() == processId) {
			if (delegation.getDelegateId().equals(delegateId)) {
			    if (delegation.getUnitId() == null) {
				throw new BusinessException("error_PartialAndPartialAllUnitsSpecificUnitSameEmployeeSameProcessDelegationConflict");
			    } else if (delegation.getUnitId().equals(unitId)) {
				throw new BusinessException("error_PartialAndPartialSameUnitSameEmployeeSameProcessDelegationConflict");
			    } else if (isHierarchical(hKey, delegation.getUnitHKey())) {
				throw new BusinessException("error_PartialAndPartialParentUnitSameEmployeeSameProcessDelegationConflict");
			    }
			} else {
			    if (delegation.getUnitId() != null) {
				if (delegation.getUnitId().equals(unitId)) {
				    throw new BusinessException("error_PartialAndPartialSameUnitTwoEmployeesSameProcessDelegationConflict");
				} else if (isHierarchical(hKey, delegation.getUnitHKey())) {
				    throw new BusinessException("error_PartialAndPartialParentUnitTwoEmployeesSameProcessDelegationConflict");
				}
			    }
			}
		    }
		}
	    }
	}

	WFDelegation delegation = new WFDelegation();
	delegation.setEmpId(empId);
	delegation.setDelegateId(delegateId);
	delegation.setProcessId(processId);
	delegation.setUnitId(unitId);
	delegation.setSystemUser(empId + ""); // For Auditing

	try {
	    DataAccess.addEntity(delegation);
	} catch (DatabaseException e) {
	    throw new BusinessException("error_duplicatedDelegation");
	}
    }

    public static void invalidateWFDelegations(Long[] empsIds, CustomSession session) throws BusinessException {
	if (session == null)
	    throw new BusinessException("error_general");
	if (empsIds == null || empsIds.length == 0)
	    throw new BusinessException("error_transactionDataError");

	List<WFDelegation> delegations = getManyEntities(WFDelegation.class, getWFDelegationsEitherFromOrToEmployeesQueryInfo(empsIds), empsIds);
	for (WFDelegation delegation : delegations)
	    deleteWFDelegation(delegation.getId(), delegation.getEmpId(), session);
    }

    public static void deleteWFDelegation(long delegationId, long empId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    WFDelegation delegation = new WFDelegation();
	    delegation.setId(delegationId);
	    delegation.setSystemUser(empId + ""); // For Auditing
	    DataAccess.deleteEntity(delegation, session);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static boolean isHierarchical(String targetHKey, String referenceHKey) {
	String targetHKeyPrefix = targetHKey.replaceAll("0*$", "");
	targetHKeyPrefix = targetHKeyPrefix.length() % 2 == 0 ? targetHKeyPrefix : (targetHKeyPrefix + "0");

	String referenceHKeyPrefix = referenceHKey.replaceAll("0*$", "");
	referenceHKeyPrefix = referenceHKeyPrefix.length() % 2 == 0 ? referenceHKeyPrefix : (referenceHKeyPrefix + "0");

	return (targetHKeyPrefix.length() > referenceHKeyPrefix.length() && targetHKeyPrefix.startsWith(referenceHKeyPrefix))
		|| (referenceHKeyPrefix.length() > targetHKeyPrefix.length() && referenceHKeyPrefix.startsWith(targetHKeyPrefix));
    }

    protected static long getDelegate(long empId, long processId, Long requesterId) throws BusinessException {
	Set<Long> loopDetectionSet = new HashSet<Long>();
	loopDetectionSet.add(empId);
	long delegateId = empId;
	Long fetchedDelegateId = null;

	while (true) {
	    // Search for partial delegation with a specific unit.
	    if (requesterId != null) {
		fetchedDelegateId = getDelegateIdSpecificHierarchy(delegateId, processId, requesterId);

		if (fetchedDelegateId != null) {
		    delegateId = fetchedDelegateId.longValue();
		    if (!loopDetectionSet.add(delegateId))
			throw new BusinessException("error_delegationLoop");

		    if (requesterId.longValue() == delegateId) {
			delegateId = empId;
			break;
		    }
		    continue;
		}
	    }

	    // Search for partial delegation without a specific unit.
	    fetchedDelegateId = getDelegateIdAllHierarchy(delegateId, processId);
	    if (fetchedDelegateId != null) {
		delegateId = fetchedDelegateId.longValue();
		if (!loopDetectionSet.add(delegateId))
		    throw new BusinessException("error_delegationLoop");

		if (requesterId != null && requesterId.longValue() == delegateId) {
		    delegateId = empId;
		    break;
		}
		continue;
	    }

	    // Search for total delegation with a specific unit.
	    if (requesterId != null) {
		fetchedDelegateId = getDelegateIdSpecificHierarchy(delegateId, FlagsEnum.ALL.getCode(), requesterId);

		if (fetchedDelegateId != null) {
		    delegateId = fetchedDelegateId.longValue();
		    if (!loopDetectionSet.add(delegateId))
			throw new BusinessException("error_delegationLoop");

		    if (requesterId.longValue() == delegateId) {
			delegateId = empId;
			break;
		    }
		    continue;
		}
	    }

	    // Search for total delegation without a specific unit.
	    fetchedDelegateId = getDelegateIdAllHierarchy(delegateId, FlagsEnum.ALL.getCode());
	    if (fetchedDelegateId != null) {
		delegateId = fetchedDelegateId.longValue();
		if (!loopDetectionSet.add(delegateId))
		    throw new BusinessException("error_delegationLoop");

		if (requesterId != null && requesterId.longValue() == delegateId) {
		    delegateId = empId;
		    break;
		}
		continue;
	    }

	    // No more delegations exist.
	    break;
	}

	return delegateId;
    }

    private static Long getDelegateIdAllHierarchy(long empId, long processId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_PROCESS_ID", processId);

	    List<Long> result = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_GET_DELEGATE_ID_ALL_HIERARCHY.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static Long getDelegateIdSpecificHierarchy(long empId, long processId, long unitEmployeeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_PROCESS_ID", processId);
	    qParams.put("P_UNIT_EMPLOYEE_ID", unitEmployeeId);

	    List<Long> result = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_GET_DELEGATE_ID_SPECIFIC_HIERARCHY.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<Object> countWFDelegations(long empId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.WF_GET_DELEGATIONS_COUNT.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<WFDelegationData> getWFTotalDelegationsFrom(long empId) throws BusinessException {
	return searchWFDelegation(empId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static List<WFDelegationData> getWFTotalDelegationTo(long delegateId) throws BusinessException {
	return searchWFDelegation(FlagsEnum.ALL.getCode(), delegateId, FlagsEnum.ALL.getCode());
    }

    public static List<WFDelegationData> getWFPartialDelegationsFrom(long empId) throws BusinessException {
	return searchWFDelegation(empId, FlagsEnum.ALL.getCode(), FlagsEnum.ON.getCode());
    }

    public static List<WFDelegationData> getWFPartialDelegationTo(long delegateId) throws BusinessException {
	return searchWFDelegation(FlagsEnum.ALL.getCode(), delegateId, FlagsEnum.ON.getCode());
    }

    private static List<WFDelegationData> searchWFDelegation(long empId, long delegateId, long processIdFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_DELEGATE_ID", delegateId);
	    qParams.put("P_PROCESS_ID_FLAG", processIdFlag);
	    return DataAccess.executeNamedQuery(WFDelegationData.class, QueryNamesEnum.WF_GET_DELEGATE_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<Object> getWFDelegationsEitherFromOrToEmployeesQueryInfo(Long[] empsIds) {
	List<Object> queryInfo = new ArrayList<Object>();
	queryInfo.add(QueryNamesEnum.WF_GET_DELEGATIONS_EITHER_FROM_OR_TO_EMPLOYEES.getCode());

	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_EMPS_IDS", empsIds);
	queryInfo.add(qParams);

	return queryInfo;
    }

    /****************************************************************************************************************/

    /********************************************** Positions Methods ***********************************************/
    public static WFPosition getWFPosition(int positionCode, long regionId) throws BusinessException {

	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_CODE", positionCode);
	qParams.put("P_REGION_ID", regionId);

	try {
	    List<WFPosition> result = DataAccess.executeNamedQuery(WFPosition.class, QueryNamesEnum.WF_GET_POSITION.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /****************************************************************************************************************/

    /************************************** Notifications Configuration data Methods ********************************/

    /**
     * Computes all the notifications needed for instance including Configured notification for the process, and any additional notifications
     * 
     * @param categoriesIds
     *            Categories of employees to search for in the notification configuration table
     * @param decisionRegionId
     *            The region in which the decision will be approved
     * @param processId
     *            Work flow process Id
     * @param beneficairyEmployeesIds
     *            Beneficiaries of the transaction
     * @param additionalIds
     *            Any other dynamic or manually selected notifications (internal copies and any other dynamic notifications)
     * @return Long array of all needed employees IDs to be notified
     * @throws BusinessException
     */
    public static Long[] computeInstanceNotifications(List<Long> categoriesIds, long decisionRegionId, long processId, List<Long> beneficairyEmployeesIds, List<Long> additionalIds) throws BusinessException {

	Set<Long> notificationsEmployeesIdsSet = new HashSet<Long>();

	if (categoriesIds == null || categoriesIds.size() == 0)
	    throw new IllegalArgumentException();

	List<Long> finalBeneficairyEmployeesIds = (beneficairyEmployeesIds == null || beneficairyEmployeesIds.size() == 0) ? new ArrayList<Long>(Arrays.asList(new Long(-1))) : beneficairyEmployeesIds;

	List<WFNotificationsConfigDetailEmployeeData> wfNotificationsConfigDetailEmployeeList = NotificationsConfigService.searchNotificationsConfigDetailEmployeeForWfInstance(categoriesIds, decisionRegionId, processId, finalBeneficairyEmployeesIds);

	for (WFNotificationsConfigDetailEmployeeData wfNotificationsConfigDetailEmployee : wfNotificationsConfigDetailEmployeeList) {
	    notificationsEmployeesIdsSet.add(wfNotificationsConfigDetailEmployee.getEmployeeId());
	}

	if (additionalIds != null && additionalIds.size() > 0)
	    notificationsEmployeesIdsSet.addAll(additionalIds);

	return notificationsEmployeesIdsSet.toArray(new Long[notificationsEmployeesIdsSet.size()]);
    }

    /******************************************* Scheduler methods ***************************************************/
    public static void handleUnassignedTasks() throws BusinessException {
	try {
	    List<WFTask> unassignedWFTasks = getAllUnassignedWFTasks();
	    for (WFTask wfTask : unassignedWFTasks) {
		Long originalUnitManagerId = wfTask.getOriginalUnitId() == null ? null : UnitsService.getUnitById(wfTask.getOriginalUnitId()).getPhysicalManagerId();
		if (originalUnitManagerId != null) {
		    wfTask.setAssigneeId(originalUnitManagerId);
		    wfTask.setOriginalId(originalUnitManagerId);
		    DataAccess.updateEntity(wfTask);
		}
	    }
	} catch (Exception e) {
	    throw new BusinessException("error_general");
	}
    }

    /****************************************************************************************************************/
}