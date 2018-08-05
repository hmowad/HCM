package com.code.services.workflow.hcm;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementWishTransaction;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.movements.WFMovementWish;
import com.code.enums.FlagsEnum;
import com.code.enums.MoveWishStatusesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MovementsWishesService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

public class MovementsWishesWorkFlow extends BaseWorkFlow {

    /**
     * Private constructor to prevent instantiation
     */
    private MovementsWishesWorkFlow() {
    }

    /*********************************************************** WFMovementWish ***********************************************************/
    /*---------------------------Work Flow Steps----------------------*/
    public static void initWFMovementWish(EmployeeData requester, WFMovementWish wfMovementWish, String attachments, long processId, String taskUrl) throws BusinessException {
	validateWFMovementWish(wfMovementWish, processId, attachments);
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, Arrays.asList(wfMovementWish.getEmployeeId()), session);
	    if (isRequestProcess(processId)) {
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
	    } else {
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
	    }

	    wfMovementWish.setInstanceId(instance.getInstanceId());
	    DataAccess.addEntity(wfMovementWish, session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    wfMovementWish.setInstanceId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doWFMovementWishDM(EmployeeData requester, WFInstance instance, WFTask dmTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    String role;
	    long assigneeId = requester.getEmpId();
	    long originalId = requester.getEmpId();

	    if (isApproved) {

		EmployeeData nextDM = EmployeesService.getEmployeeDirectManager(dmTask.getOriginalId());

		if (nextDM.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode() || nextDM.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
		    WFPosition position = getWFPosition(requester.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? WFPositionsEnum.SOLDIERS_MOVEMENTS_UNIT_MANAGER.getCode() : WFPositionsEnum.REGION_SOLDIERS_AFFAIRS_UNIT_MANAGER.getCode(), requester.getPhysicalRegionId());
		    originalId = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId()).getEmpId();
		    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
		} else {
		    originalId = nextDM.getEmpId();
		    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    role = WFTaskRolesEnum.DIRECT_MANAGER.getCode();
		}

		completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), assigneeId, originalId, dmTask.getTaskUrl(), role, dmTask.getLevel(), session);

	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, dmTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    public static void doWFMovementWishMR(EmployeeData requester, WFInstance instance, WFTask mrTask, long reviewerEmpId) throws BusinessException {
	try {
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    completeWFTask(mrTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, mrTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), mrTask.getLevel());
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static void doWFMovementWishRE(EmployeeData requester, WFMovementWish wfMovementWish, WFInstance instance, String attachments, WFTask reTask, boolean isApproved) throws BusinessException {
	if (isApproved)
	    validateWFMovementWish(wfMovementWish, instance.getProcessId(), attachments);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {
		EmployeeData manager = EmployeesService.getEmployeeDirectManager(reTask.getOriginalId());
		completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(manager.getEmpId(), instance.getProcessId(), requester.getEmpId()), manager.getEmpId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), reTask.getLevel(), session);

		if (requester.getEmpId().equals(reTask.getOriginalId())) {
		    DataAccess.updateEntity(wfMovementWish, session);
		    updateWFInstanceBeneficiaries(instance.getInstanceId(), Arrays.asList(wfMovementWish.getEmployeeId()), session);

		    if (attachments != null && instance.getAttachments() == null)
			updateWFInstanceAttachments(instance, attachments, session);
		}
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, reTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    session.rollbackTransaction();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void doWFMovementWishSM(EmployeeData requester, WFInstance instance, WFMovementWish wfMovementWish, WFTask smTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		EmployeeData nextSM = EmployeesService.getEmployeeDirectManager(smTask.getOriginalId());
		if (nextSM.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
		    closeWFMovementWishWorkFlow(requester, instance, wfMovementWish, smTask, session);
		} else { // Send to next manager
		    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextSM.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextSM.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), smTask.getLevel(), session);
		}

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		List<WFTask> reviewerTasks = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP.getCode());
		long originalId = reviewerTasks.isEmpty() ? requester.getEmpId() : reviewerTasks.get(0).getOriginalId();
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(originalId, instance.getProcessId(), requester.getEmpId()), originalId, smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), smTask.getLevel(), session);

	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    session.rollbackTransaction();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    /*---------------------------Operations---------------------------*/
    private static boolean isRequestProcess(long processId) {
	if (processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_REQUEST.getCode()
		|| processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_MODIFY_REQUEST.getCode() ||
		processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_CANCEL_REQUEST.getCode())
	    return true;
	return false;
    }

    public static Object[] constructWfMovementWish(long employeeId, long processId) throws BusinessException {

	WFMovementWish wfMovementWish = new WFMovementWish();
	MovementWishTransaction movementWishTransaction = null;

	if (processId == WFProcessesEnum.SOLDIERS_MOVE_WISH.getCode() || processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_REQUEST.getCode()) {

	    EmployeeData employee = EmployeesService.getEmployeeData(employeeId);
	    if (employee == null)
		throw new BusinessException("error_general");

	    wfMovementWish.setEmployeeId(employeeId);
	    wfMovementWish.setFromRegionId(employee.getOfficialRegionId());

	} else if (processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_MODIFY.getCode() || processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_MODIFY_REQUEST.getCode()
		|| processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_CANCEL.getCode() || processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_CANCEL_REQUEST.getCode()) {

	    movementWishTransaction = MovementsWishesService.getEmployeeActiveMovementWish(employeeId);
	    if (movementWishTransaction == null)
		throw new BusinessException("error_noMovementWishesForEmployee");

	    wfMovementWish.setEmployeeId(movementWishTransaction.getEmployeeId());
	    wfMovementWish.setFromRegionId(movementWishTransaction.getFromRegionId());
	    wfMovementWish.setToRegionId(movementWishTransaction.getToRegionId());
	}

	return new Object[] { wfMovementWish, movementWishTransaction == null ? null : movementWishTransaction.getAttachments() };
    }

    private static void closeWFMovementWishWorkFlow(EmployeeData requester, WFInstance instance, WFMovementWish wfMovementWish, WFTask smTask, CustomSession session) throws BusinessException {
	try {
	    closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), new Long[] { wfMovementWish.getEmployeeId() }, session);
	    MovementsWishesService.handleMovementWishTransaction(constructMovementWishTransaction(wfMovementWish, instance.getProcessId(), instance.getAttachments()), session);
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static MovementWishTransaction constructMovementWishTransaction(WFMovementWish wfMovementWish, long processId, String attachments) throws BusinessException {
	MovementWishTransaction movementWishTransaction;
	if (processId == WFProcessesEnum.SOLDIERS_MOVE_WISH.getCode() || processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_REQUEST.getCode()) {
	    movementWishTransaction = new MovementWishTransaction();
	    movementWishTransaction.setStatus(MoveWishStatusesEnum.NEW.getCode());
	    movementWishTransaction.setEmployeeId(wfMovementWish.getEmployeeId());
	    movementWishTransaction.setFromRegionId(wfMovementWish.getFromRegionId());
	    movementWishTransaction.setToRegionId(wfMovementWish.getToRegionId());
	} else {
	    movementWishTransaction = MovementsWishesService.getEmployeeActiveMovementWish(wfMovementWish.getEmployeeId());
	    if (processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_MODIFY.getCode() || processId == WFProcessesEnum.SOLDIERS_MOVE_WISH_MODIFY_REQUEST.getCode()) {
		movementWishTransaction.setStatus(MoveWishStatusesEnum.MODIFIED.getCode());
		movementWishTransaction.setToRegionId(wfMovementWish.getToRegionId());
	    } else {
		movementWishTransaction.setStatus(MoveWishStatusesEnum.CANCELED.getCode());
	    }
	}

	movementWishTransaction.setReasons(wfMovementWish.getReasons());
	movementWishTransaction.setAttachments(attachments);

	return movementWishTransaction;
    }

    private static void rejectAllMovementsWishesWorkflows(CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    invalidateWFTasksByProcessesIds(new Long[] { WFProcessesEnum.SOLDIERS_MOVE_WISH.getCode(), WFProcessesEnum.SOLDIERS_MOVE_WISH_REQUEST.getCode(), WFProcessesEnum.SOLDIERS_MOVE_WISH_MODIFY.getCode(), WFProcessesEnum.SOLDIERS_MOVE_WISH_MODIFY_REQUEST.getCode(), WFProcessesEnum.SOLDIERS_MOVE_WISH_CANCEL.getCode(), WFProcessesEnum.SOLDIERS_MOVE_WISH_CANCEL_REQUEST.getCode() },
		    getMessage("label_movementsWishesWorkflowDiscardedAutomatically"), session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
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

    public static void setMovementWishesConfig(int minServicePeriod, boolean openFlag, long loginEmpId) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();
	    MovementsWishesService.setMovementWishesConfig(minServicePeriod, openFlag, loginEmpId, session);
	    if (!openFlag)
		rejectAllMovementsWishesWorkflows(session);
	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static long countMovementWishesRunningWorkflows() throws BusinessException {
	try {
	    return BaseWorkFlow.countWFInstancesByProcessesIds(new Long[] { WFProcessesEnum.SOLDIERS_MOVE_WISH.getCode(), WFProcessesEnum.SOLDIERS_MOVE_WISH_REQUEST.getCode(), WFProcessesEnum.SOLDIERS_MOVE_WISH_MODIFY.getCode(), WFProcessesEnum.SOLDIERS_MOVE_WISH_MODIFY_REQUEST.getCode(), WFProcessesEnum.SOLDIERS_MOVE_WISH_CANCEL.getCode(), WFProcessesEnum.SOLDIERS_MOVE_WISH_CANCEL_REQUEST.getCode() }, new Integer[] { WFInstanceStatusEnum.RUNNING.getCode() });
	} catch (BusinessException e) {
	    throw e;
	}

    }

    /*---------------------------Validations---------------------------*/
    private static void validateWFMovementWish(WFMovementWish wfMovementWish, long processId, String attachments) throws BusinessException {
	if (wfMovementWish == null)
	    throw new BusinessException("error_general");
	if (wfMovementWish.getEmployeeId() == null)
	    throw new BusinessException("error_empSelectionManadatory");

	EmployeeData employee = EmployeesService.getEmployeeData(wfMovementWish.getEmployeeId());
	if (isRequestProcess(processId) && !employee.getOfficialRegionId().equals(employee.getPhysicalRegionId())) {
	    throw new BusinessException("label_physicalOfficalUnitShouldBeSimilarOrByOfficialSpecialist");
	}

	if (getRunningWFMovementWishes(wfMovementWish.getInstanceId(), wfMovementWish.getEmployeeId()).size() > 0)
	    throw new BusinessException("error_employeeHasPendingMovementWishWF");

	MovementsWishesService.validateMovementWishTransaction(constructMovementWishTransaction(wfMovementWish, processId, attachments));
    }

    /*---------------------------Queries---------------------------*/
    public static WFMovementWish getWFMovementWishByInstanceId(long instanceId) throws BusinessException {
	return searchWFMovementWishes(instanceId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode()).get(0);
    }

    private static List<WFMovementWish> getRunningWFMovementWishes(Long excludedInstanceId, long employeeId) throws BusinessException {
	return searchWFMovementWishes(FlagsEnum.ALL.getCode(), excludedInstanceId == null ? FlagsEnum.ALL.getCode() : excludedInstanceId, employeeId, WFInstanceStatusEnum.RUNNING.getCode());
    }

    private static List<WFMovementWish> searchWFMovementWishes(long instanceId, long excludedInstanceId, long employeeId, int instanceStatus) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_INSTANCE_ID", instanceId);
	    qParams.put("P_EMPLOYEE_ID", employeeId);
	    qParams.put("P_STATUS", instanceStatus);
	    qParams.put("P_EXCLUDED_INSTANCE_ID", excludedInstanceId);

	    return DataAccess.executeNamedQuery(WFMovementWish.class, QueryNamesEnum.WF_SEARCH_WFMOVEMENT_WISHES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Map<Long, WFMovementWish> getWFMovementsWishesByInstancesIds(Long[] instancesIds) throws BusinessException {
	List<WFMovementWish> wfMovementWishes = getWFMovementWishesByInstancesIds(instancesIds);
	Map<Long, WFMovementWish> wfMovementWishesMap = new HashMap<Long, WFMovementWish>();

	for (WFMovementWish wfMovementWish : wfMovementWishes) {
	    wfMovementWishesMap.put(wfMovementWish.getInstanceId(), wfMovementWish);
	}
	return wfMovementWishesMap;
    }

    private static List<WFMovementWish> getWFMovementWishesByInstancesIds(Long[] instancesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCES_IDS", instancesIds);
	    return DataAccess.executeNamedQuery(WFMovementWish.class, QueryNamesEnum.WF_GET_WFMOVEMENT_WISHES_BY_INSTANCES_IDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}
