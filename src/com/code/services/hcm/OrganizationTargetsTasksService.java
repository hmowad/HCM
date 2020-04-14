package com.code.services.hcm;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.organization.units.OrganizationTargetTask;
import com.code.dal.orm.hcm.organization.units.OrganizationTargetTaskDetail;
import com.code.dal.orm.hcm.organization.units.OrganizationTargetTaskDetailData;
import com.code.dal.orm.hcm.organization.units.OrganizationTargetTaskTransaction;
import com.code.dal.orm.hcm.organization.units.OrganizationTargetTaskTransactionData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.enums.OperationsTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
//import com.code.exceptions.NoDataException;
import com.code.services.BaseService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

/**
 * Provides methods for handling the organization targets tasks operations such as construct, rename or cancel organization targets tasks.
 * 
 */
public class OrganizationTargetsTasksService extends BaseService {

    /**
     * A constant holding special code.
     */
    private static final String SPECIAL_DECISION_NUMBER = "*2#";

    /**
     * Constructs the organization targets tasks service.
     */
    private OrganizationTargetsTasksService() {
    }

    /*----------------------------------------------------- Targets Tasks -----------------------------------------------------*/

    public static void constructOrganizationTargetTask(Boolean completionFlag, String decisionNumber, Date decisionDate, List<UnitData> units,
	    List<OrganizationTargetTaskDetailData> targets, List<OrganizationTargetTaskDetailData> tasks, Long userId, CustomSession... useSession) throws BusinessException {

	// trimming data
	decisionNumber = decisionNumber != null ? decisionNumber.trim() : decisionNumber;
	trimTargetsTasksData(targets);
	trimTargetsTasksData(tasks);

	if (units.isEmpty())
	    throw new BusinessException("error_unitsListEmpty");

	Long[] unitIds = new Long[units.size()];
	for (int i = 0; i < units.size(); i++) {
	    unitIds[i] = units.get(i).getId();
	}
	List<OrganizationTargetTask> organizationTargetsTasks = getActiveOrganizationTargetsTasks(unitIds);

	validateConstructTargetsTasks(completionFlag, decisionNumber, decisionDate, units, organizationTargetsTasks, targets, tasks);

	Map<Long, OrganizationTargetTask> unitsTargetsTasksMap = new HashMap<Long, OrganizationTargetTask>();
	for (OrganizationTargetTask organizationTargetTask : organizationTargetsTasks) {
	    unitsTargetsTasksMap.put(organizationTargetTask.getUnitId(), organizationTargetTask);
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (UnitData unitData : units) {

		if (!completionFlag) {
		    OrganizationTargetTask targetTask = new OrganizationTargetTask();
		    targetTask.setUnitId(unitData.getId());
		    targetTask.setActiveFlag(FlagsEnum.ON.getCode());
		    targetTask.setDecisionDate(decisionDate);
		    targetTask.setDecisionNumber(decisionNumber);

		    targetTask.setSystemUser(userId + ""); // For Audit.

		    DataAccess.addEntity(targetTask, session);

		    unitsTargetsTasksMap.put(unitData.getId(), targetTask);
		}

		for (OrganizationTargetTaskDetailData targetData : targets) {

		    if (targetData.getId() != null)
			continue;

		    OrganizationTargetTaskDetail target = new OrganizationTargetTaskDetail();
		    target.setActiveFlag(FlagsEnum.ON.getCode());
		    target.setOrganizationTargetTaskFlag(FlagsEnum.ON.getCode());
		    target.setDescription(targetData.getDescription());
		    target.setOrganizationTargetTaskId(unitsTargetsTasksMap.get(unitData.getId()).getId());

		    DataAccess.addEntity(target, session);
		    addTargetTaskTransaction(target, decisionNumber, decisionDate, TransactionTypesEnum.TARGET_TASK_CONSTRUCTION.getCode(), userId, session);
		}

		for (OrganizationTargetTaskDetailData taskData : tasks) {

		    if (taskData.getId() != null)
			continue;

		    OrganizationTargetTaskDetail task = new OrganizationTargetTaskDetail();
		    task.setActiveFlag(FlagsEnum.ON.getCode());
		    task.setOrganizationTargetTaskFlag(FlagsEnum.OFF.getCode());
		    task.setDescription(taskData.getDescription());
		    task.setOrganizationTargetTaskId((unitsTargetsTasksMap.get(unitData.getId()).getId()));

		    DataAccess.addEntity(task, session);
		    addTargetTaskTransaction(task, decisionNumber, decisionDate, TransactionTypesEnum.TARGET_TASK_CONSTRUCTION.getCode(), userId, session);
		}
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void modifyOrganizationTargetTask(String decisionNumber, Date decisionDate, List<UnitData> units, int operationType,
	    OrganizationTargetTaskDetailData targetTaskDetailData, List<OrganizationTargetTaskDetailData> targets, List<OrganizationTargetTaskDetailData> tasks,
	    Long userId, CustomSession... useSession) throws BusinessException {

	// trimming data
	decisionNumber = decisionNumber != null ? decisionNumber.trim() : decisionNumber;
	if (targetTaskDetailData != null && targetTaskDetailData.getDescription() != null)
	    targetTaskDetailData.setDescription(targetTaskDetailData.getDescription().trim());

	if (units.isEmpty())
	    throw new BusinessException("error_unitsListEmpty");

	Long[] unitIds = new Long[units.size()];
	for (int i = 0; i < units.size(); i++) {
	    unitIds[i] = units.get(i).getId();
	}
	List<OrganizationTargetTask> organizationTargetsTasks = getActiveOrganizationTargetsTasks(unitIds);

	int organizationTargetTaskDetailsCountPerUnit = OrganizationTargetsTasksService.countActiveTargetTaskDetailsForUnit(unitIds[0]).intValue();
	List<OrganizationTargetTaskDetailData> originalTargetTaskDetails = OrganizationTargetsTasksService.getIdenticalTargetsTasksDetails(unitIds[0], organizationTargetTaskDetailsCountPerUnit);

	validateModifyTargetsTasks(decisionNumber, decisionDate, units, organizationTargetsTasks, originalTargetTaskDetails, operationType, targetTaskDetailData, targets, tasks);

	Map<Long, OrganizationTargetTask> unitsTargetsTasksMap = new HashMap<Long, OrganizationTargetTask>();
	for (OrganizationTargetTask organizationTargetTask : organizationTargetsTasks) {
	    unitsTargetsTasksMap.put(organizationTargetTask.getUnitId(), organizationTargetTask);
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (OperationsTypesEnum.INSERT.getCode() == operationType) {
		for (UnitData unit : units) {
		    OrganizationTargetTask targetTask = unitsTargetsTasksMap.get(unit.getId());

		    if (!SPECIAL_DECISION_NUMBER.equals(decisionNumber)) {
			targetTask.setDecisionDate(decisionDate);
			targetTask.setDecisionNumber(decisionNumber);

			targetTask.setSystemUser(userId + ""); // For Audit.

			DataAccess.updateEntity(targetTask, session);
		    }

		    OrganizationTargetTaskDetail targetTaskDetail = new OrganizationTargetTaskDetail();
		    targetTaskDetail.setActiveFlag(FlagsEnum.ON.getCode());
		    targetTaskDetail.setOrganizationTargetTaskFlag(targetTaskDetailData.getOrganizationTargetTaskFlag());
		    targetTaskDetail.setDescription(targetTaskDetailData.getDescription());
		    targetTaskDetail.setOrganizationTargetTaskId(targetTask.getId());

		    if (SPECIAL_DECISION_NUMBER.equals(decisionNumber))
			targetTaskDetail.setSystemUser(userId + ""); // For Audit.

		    DataAccess.addEntity(targetTaskDetail, session);

		    targetTaskDetailData.setId(targetTaskDetail.getId());

		    if (!SPECIAL_DECISION_NUMBER.equals(decisionNumber))
			addTargetTaskTransaction(targetTaskDetail, decisionNumber, decisionDate, TransactionTypesEnum.TARGET_TASK_MODIFICATION.getCode(), userId, session);

		}
	    } else {

		int index = 0;
		for (OrganizationTargetTaskDetailData organizationTargetTaskDetailData : originalTargetTaskDetails) {
		    if (organizationTargetTaskDetailData.getId().equals(targetTaskDetailData.getId()))
			break;
		    index++;
		}

		index = index % organizationTargetTaskDetailsCountPerUnit;

		// TODO: validate pressing save without changes.
		// if (originalTargetTaskDetails.get(index).getDescription().equals(targetTaskDetailData.getDescription()))

		for (int i = index; i < originalTargetTaskDetails.size(); i += organizationTargetTaskDetailsCountPerUnit) {
		    OrganizationTargetTaskDetailData originalTargetTaskDetailData = originalTargetTaskDetails.get(i);
		    if (unitsTargetsTasksMap.containsKey(originalTargetTaskDetailData.getUnitId())) {

			OrganizationTargetTask targetTask = unitsTargetsTasksMap.get(originalTargetTaskDetailData.getUnitId());

			if (!SPECIAL_DECISION_NUMBER.equals(decisionNumber)) {
			    targetTask.setDecisionDate(decisionDate);
			    targetTask.setDecisionNumber(decisionNumber);

			    targetTask.setSystemUser(userId + ""); // For Audit.

			    DataAccess.updateEntity(targetTask, session);
			}

			if (OperationsTypesEnum.DELETE.getCode() == operationType)
			    originalTargetTaskDetailData.setActiveFlag(FlagsEnum.OFF.getCode());
			if (OperationsTypesEnum.UPDATE.getCode() == operationType)
			    originalTargetTaskDetailData.setDescription(targetTaskDetailData.getDescription());

			if (SPECIAL_DECISION_NUMBER.equals(decisionNumber))
			    originalTargetTaskDetailData.getOrganizationTargetTaskDetail().setSystemUser(userId + ""); // For Audit.

			DataAccess.updateEntity(originalTargetTaskDetailData.getOrganizationTargetTaskDetail(), session);

			if (!SPECIAL_DECISION_NUMBER.equals(decisionNumber))
			    addTargetTaskTransaction(originalTargetTaskDetailData.getOrganizationTargetTaskDetail(), decisionNumber, decisionDate, TransactionTypesEnum.TARGET_TASK_MODIFICATION.getCode(), userId, session);

		    }
		}
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (OperationsTypesEnum.INSERT.getCode() == operationType)
		targetTaskDetailData.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void cancelOrganizationTargetsTasks(String decisionNumber, Date decisionDate, List<UnitData> units,
	    List<OrganizationTargetTaskDetailData> organizationTargetTaskDetails, Long userId, CustomSession... useSession) throws BusinessException {

	// trimming data
	decisionNumber = decisionNumber != null ? decisionNumber.trim() : decisionNumber;

	if (units.isEmpty())
	    throw new BusinessException("error_unitsListEmpty");

	Long[] unitIds = new Long[units.size()];
	for (int i = 0; i < units.size(); i++) {
	    unitIds[i] = units.get(i).getId();
	}
	List<OrganizationTargetTask> organizationTargetsTasks = getActiveOrganizationTargetsTasks(unitIds);

	validateCancelTargetsTasks(decisionNumber, decisionDate, units, organizationTargetsTasks);

	Map<Long, OrganizationTargetTask> unitsTargetsTasksMap = new HashMap<Long, OrganizationTargetTask>();
	for (OrganizationTargetTask organizationTargetTask : organizationTargetsTasks) {
	    unitsTargetsTasksMap.put(organizationTargetTask.getUnitId(), organizationTargetTask);
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (OrganizationTargetTaskDetailData detail : organizationTargetTaskDetails) {
		if (unitsTargetsTasksMap.containsKey(detail.getUnitId())) {
		    detail.setActiveFlag(FlagsEnum.OFF.getCode());

		    DataAccess.updateEntity(detail.getOrganizationTargetTaskDetail(), session);
		    addTargetTaskTransaction(detail.getOrganizationTargetTaskDetail(), decisionNumber, decisionDate,
			    TransactionTypesEnum.TARGET_TASK_CANCELLATION.getCode(), userId, session);
		}
	    }

	    // Set Active flag for organization targets tasks to false
	    for (UnitData unitData : units) {
		OrganizationTargetTask targetTask = unitsTargetsTasksMap.get(unitData.getId());
		targetTask.setDecisionNumber(decisionNumber);
		targetTask.setDecisionDate(decisionDate);

		targetTask.setActiveFlag(FlagsEnum.OFF.getCode());

		targetTask.setSystemUser(userId + ""); // For Audit.

		DataAccess.updateEntity(targetTask, session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void trimTargetsTasksData(List<OrganizationTargetTaskDetailData> organizationTargetsTasksDetails) {
	for (OrganizationTargetTaskDetailData targetTask : organizationTargetsTasksDetails) {
	    targetTask.setDescription(targetTask.getDescription() != null ? targetTask.getDescription().trim() : targetTask.getDescription());
	}
    }

    private static void validateConstructTargetsTasks(boolean completionFlag, String decisionNumber, Date decisionDate, List<UnitData> units,
	    List<OrganizationTargetTask> organizationTargetsTasks, List<OrganizationTargetTaskDetailData> targets, List<OrganizationTargetTaskDetailData> tasks) throws BusinessException {
	// must have at least one unit in the list
	if (units.isEmpty())
	    throw new BusinessException("error_unitsListEmpty");

	// validate decision number
	if (decisionNumber == null || decisionNumber.isEmpty())
	    throw new BusinessException("error_decNumberMandatory");
	else if (decisionNumber.contains("*") || decisionNumber.contains("#"))
	    throw new BusinessException("error_decNumberContainsProhibitedCharacter");

	// validate decision date
	if (decisionDate == null)
	    throw new BusinessException("error_decDateMandatory");

	// Decision Date must be today or before
	validateOldHijriDate(decisionDate);

	// must have at least target list or task list
	if (targets.isEmpty() && tasks.isEmpty())
	    throw new BusinessException("error_targetAndTaskEmpty");

	if (!completionFlag && organizationTargetsTasks.size() != 0) {
	    throw new BusinessException("error_cantAddUnitHasTargetOrTask");
	} else if (completionFlag && organizationTargetsTasks.size() != units.size()) {
	    throw new BusinessException("error_cannotAddUnitWithoutTargetOrTask");
	}

	HashSet<String> targetSet = new HashSet<String>();
	for (OrganizationTargetTaskDetailData target : targets) {
	    if (target.getDescription() == null || target.getDescription().isEmpty())
		throw new BusinessException("error_targetDescription");

	    if (target.getDescription().length() > 500)
		throw new BusinessException("error_maxNumberOfTargetLetters");

	    // we can't add the same description again
	    if (!targetSet.add(target.getDescription()))
		throw new BusinessException("error_repeatedTargetDescription");
	}

	HashSet<String> taskSet = new HashSet<String>();
	for (OrganizationTargetTaskDetailData task : tasks) {
	    if (task.getDescription() == null || task.getDescription().isEmpty())
		throw new BusinessException("error_taskDescription");

	    if (task.getDescription().length() > 500)
		throw new BusinessException("error_maxNumberOfTasksLetters");

	    // we can't add the same description again
	    if (!taskSet.add(task.getDescription()))
		throw new BusinessException("error_repeatedTaskDescription");
	}
    }

    private static void validateModifyTargetsTasks(String decisionNumber, Date decisionDate, List<UnitData> units, List<OrganizationTargetTask> organizationTargetsTasks,
	    List<OrganizationTargetTaskDetailData> organizationTargetTaskDetails, int operationType, OrganizationTargetTaskDetailData targetTaskDetailData,
	    List<OrganizationTargetTaskDetailData> targets, List<OrganizationTargetTaskDetailData> tasks) throws BusinessException {
	// must have at least one unit in the list
	if (units.isEmpty())
	    throw new BusinessException("error_unitsListEmpty");

	// validate decision number
	if (decisionNumber == null || decisionNumber.isEmpty())
	    throw new BusinessException("error_decNumberMandatory");

	// validate decision date
	if (decisionDate == null)
	    throw new BusinessException("error_decDateMandatory");

	// Decision Date must be today or before
	validateOldHijriDate(decisionDate);

	if (organizationTargetTaskDetails.isEmpty())
	    throw new BusinessException("error_cannotAddUnitWithoutTargetOrTask");

	if (organizationTargetsTasks.size() != units.size()) {
	    throw new BusinessException("error_cannotAddUnitWithoutTargetOrTask");
	}

	if (OperationsTypesEnum.DELETE.getCode() == operationType) {
	    if (targets.size() + tasks.size() == 1) {
		throw new BusinessException("error_deleteLastTargetOrTask");
	    }
	} else {
	    if (targetTaskDetailData.getDescription() == null || targetTaskDetailData.getDescription().isEmpty()) {
		if (FlagsEnum.ON.getCode() == targetTaskDetailData.getOrganizationTargetTaskFlag().intValue())
		    throw new BusinessException("error_targetDescription");
		else
		    throw new BusinessException("error_taskDescription");
	    }

	    if (targetTaskDetailData.getDescription().length() > 500) {
		if (FlagsEnum.ON.getCode() == targetTaskDetailData.getOrganizationTargetTaskFlag().intValue())
		    throw new BusinessException("error_maxNumberOfTargetLetters");
		else
		    throw new BusinessException("error_maxNumberOfTasksLetters");
	    }

	    if (FlagsEnum.ON.getCode() == targetTaskDetailData.getOrganizationTargetTaskFlag().intValue()) {
		for (OrganizationTargetTaskDetailData target : targets) {
		    if (targetTaskDetailData.getId() == null) {
			if (target.getId() != null && target.getDescription().equals(targetTaskDetailData.getDescription()))
			    throw new BusinessException("error_repeatedTargetDescription");
		    } else {
			if (target.getId() != null && !target.getId().equals(targetTaskDetailData.getId()) && target.getDescription().equals(targetTaskDetailData.getDescription()))
			    throw new BusinessException("error_repeatedTargetDescription");
		    }
		}
	    } else {
		for (OrganizationTargetTaskDetailData task : tasks) {
		    if (targetTaskDetailData.getId() == null) {
			if (task.getId() != null && task.getDescription().equals(targetTaskDetailData.getDescription()))
			    throw new BusinessException("error_repeatedTaskDescription");
		    } else {
			if (task.getId() != null && !task.getId().equals(targetTaskDetailData.getId()) && task.getDescription().equals(targetTaskDetailData.getDescription()))
			    throw new BusinessException("error_repeatedTaskDescription");
		    }
		}
	    }

	}
    }

    private static void validateCancelTargetsTasks(String decisionNumber, Date decisionDate, List<UnitData> units, List<OrganizationTargetTask> organizationTargetsTasks) throws BusinessException {

	// must have at least one unit in the list
	if (units.isEmpty())
	    throw new BusinessException("error_unitsListEmpty");

	// validate decision number
	if (decisionNumber == null || decisionNumber.isEmpty())
	    throw new BusinessException("error_decNumberMandatory");
	else if (decisionNumber.contains("*") || decisionNumber.contains("#"))
	    throw new BusinessException("error_decNumberContainsProhibitedCharacter");

	// validate decision date
	if (decisionDate == null)
	    throw new BusinessException("error_decDateMandatory");

	// Decision Date must be today or before
	validateOldHijriDate(decisionDate);

	if (organizationTargetsTasks.size() != units.size())
	    throw new BusinessException("error_cannotAddUnitWithoutTargetOrTask");
    }

    public static List<OrganizationTargetTask> getActiveOrganizationTargetsTasks(Long[] unitsIds) throws BusinessException {
	try {
	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("P_UNITS_IDS", unitsIds);
	    return DataAccess.executeNamedQuery(OrganizationTargetTask.class, QueryNamesEnum.HCM_GET_TARGETS_TASKS_BY_UNITS_IDS.getCode(), param);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Long countActiveTargetTaskDetailsForUnit(Long unitId) throws BusinessException {
	try {
	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("P_UNIT_ID", unitId);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_ACTIVE_TARGET_TASK_DETAILS_BY_UNIT_ID.getCode(), param).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<OrganizationTargetTaskDetailData> getActiveOrganizationTargetTaskDetails(Long unitId) throws BusinessException {
	try {
	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("P_UNIT_ID", unitId);
	    return DataAccess.executeNamedQuery(OrganizationTargetTaskDetailData.class, QueryNamesEnum.HCM_GET_ACTIVE_TARGET_TASK_DETAILS_BY_UNIT_ID.getCode(), param);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<OrganizationTargetTaskDetailData> getIdenticalTargetsTasksDetails(Long unitId, Integer TargetsTasksCount) throws BusinessException {
	try {
	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("P_UNIT_ID", unitId);
	    param.put("P_TARGETS_TASKS_COUNT", TargetsTasksCount);
	    return DataAccess.executeNamedQuery(OrganizationTargetTaskDetailData.class, QueryNamesEnum.HCM_GET_IDENTICAL_TARGETS_TASKS_DETAILS_BY_UNIT_ID_AND_DETAILS_COUNT.getCode(), param);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /*------------------------------------------------- Targets Tasks Transaction --------------------------------------------------*/

    private static void addTargetTaskTransaction(OrganizationTargetTaskDetail targetTaskDetail, String decisionNumber, Date decisionDate, Integer transactionTypeCode, Long transactionUserId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    OrganizationTargetTaskTransaction targetTaskTrans = new OrganizationTargetTaskTransaction();
	    targetTaskTrans.setDecisionDate(decisionDate);
	    targetTaskTrans.setDecisionNumber(decisionNumber);
	    targetTaskTrans.setDescription(targetTaskDetail.getDescription());
	    targetTaskTrans.setOrganizationTargetTaskDetailId(targetTaskDetail.getId());
	    targetTaskTrans.setTransactionDate(HijriDateService.getHijriSysDate());
	    targetTaskTrans.setTransactionTypeId(CommonService.getTransactionTypeByCodeAndClass(transactionTypeCode, TransactionClassesEnum.UNITS.getCode()).getId());
	    targetTaskTrans.setTransactionUserId(transactionUserId);
	    targetTaskTrans.setActiveFlag(targetTaskDetail.getActiveFlag());

	    DataAccess.addEntity(targetTaskTrans, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static List<OrganizationTargetTaskTransactionData> getOrganizationTargetsTasksTransactions(Long unitId) throws BusinessException {

	try {
	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("P_UNIT_ID", unitId);
	    return DataAccess.executeNamedQuery(OrganizationTargetTaskTransactionData.class, QueryNamesEnum.HCM_GET_TARGETS_TASKS_TRANSACTIONS_BY_UNIT_ID.getCode(), param);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /*------------------------------------------------------------- Reports  ------------------------------------------------------------*/

    public static byte[] getUnitsTargetsTasksReport(String unitHKeyPrefix) throws BusinessException {
	try {

	    String reportName = ReportNamesEnum.UNITS_ORG_TARGETS_TASKS.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_UNIT_HKEY_PREFIX", unitHKeyPrefix);

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }
}