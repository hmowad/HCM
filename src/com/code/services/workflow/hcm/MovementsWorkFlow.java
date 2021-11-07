package com.code.services.workflow.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.movements.WFMovement;
import com.code.dal.orm.workflow.hcm.movements.WFMovementData;
import com.code.enums.CategoriesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MovementTransactionViewsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.MovementsReasonTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.UnitsAncestorsLevelsEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.JobsService;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

/**
 * WorkFlow Service to control the flow of movement Decisions process
 */
public class MovementsWorkFlow extends BaseWorkFlow {
    private static final int SOLDIERS_MIN_PERIOD_TO_MOVE = 3 * HIJRI_YEAR_DAYS; // 3 years

    /**
     * Private constructor to prevent instantiation
     * 
     */
    private MovementsWorkFlow() {
    }

    /**
     * <p>
     * initiate movements workflow process
     * </p>
     * <ul>
     * <li>validate movements requests</li>
     * <li>create movements workflow instance</li>
     * <li>send task to appropriate employee</li>
     * </ul>
     * 
     * @param requester
     *            Employee that starts the workflow instance
     * @param MovementRequests
     *            movements requests to be handled
     * @param processId
     *            Number indicates the type of movement process
     * @param attachments
     *            Attachments sent with the request
     * @param taskUrl
     *            Url of workflow task
     * @param internalCopies
     *            Internal employees which receive notification upon termination of workflow instance
     * @param externalCopies
     *            External employees which receive hard copy of decision
     * @throws BusinessException
     *             if any error occurs
     * @see WFPositionsEnum
     * @see #addWFInstance(long, long, Date, Date, int, String, CustomSession)
     * @see #addWFTask(long, long, long, Date, Date, String, String, String, CustomSession)
     * 
     */
    public static void initMovement(EmployeeData requester, List<WFMovementData> movementRequests, long processId, String attachments, String taskUrl, List<EmployeeData> internalCopiesEmployees, String externalCopies) throws BusinessException {
	if (movementRequests != null && movementRequests.size() > 0) {

	    boolean isRequest = isRequestProcess(processId, movementRequests.get(0).getCategoryId());
	    if (isRequest) {
		if ((!movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !movementRequests.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && movementRequests.get(0).getMovementTypeId().equals(MovementTypesEnum.SUBJOIN.getCode()) && movementRequests.get(0).getTransactionTypeId().equals(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId()))
			|| movementRequests.get(0).getMovementTypeId().equals(MovementTypesEnum.MOVE.getCode()))
		    validateMovementRequests(movementRequests, requester, null, processId, null, true, isRequest ? MovementTransactionViewsEnum.REQUEST.getCode() : MovementTransactionViewsEnum.DECISION.getCode());
		else
		    validateMovementRequests(movementRequests, requester, null, processId, null, false, isRequest ? MovementTransactionViewsEnum.REQUEST.getCode() : MovementTransactionViewsEnum.DECISION.getCode());
	    } else {
		if (!movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !movementRequests.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && !checkIfEmployeeExistsInWFPositionUnit(movementRequests.get(0).getCategoryId(), requester, movementRequests.get(0).getMovementTypeId()))
		    throw new BusinessException("error_unAuthorizedRequester");
		validateMovementRequests(movementRequests, requester, null, processId, null, false, isRequest ? MovementTransactionViewsEnum.REQUEST.getCode() : MovementTransactionViewsEnum.DECISION.getCode());
	    }

	    CustomSession session = DataAccess.getSession();
	    try {
		session.beginTransaction();

		Date curDate = new Date();
		Date curHijriDate = HijriDateService.getHijriSysDate();

		WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), attachments, getMovementsInstanceBeneficiariesIds(movementRequests), session);

		if (isRequestProcess(processId, movementRequests.get(0).getCategoryId())) {

		    Long replacementEmployeeId = movementRequests.get(0).getReplacementEmployeeId();
		    // forward request to Direct Manager if movement type is not Replacement
		    if (replacementEmployeeId == null)
			addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);
		    // forward request to Exchange Employee if movement type is a Replacement one
		    else
			addWFTask(instance.getInstanceId(), getDelegate(replacementEmployeeId, processId, requester.getEmpId()), replacementEmployeeId, curDate, curHijriDate, taskUrl, WFTaskRolesEnum.EXCHANGE_EMP.getCode(), "1", session);
		} else {

		    if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) &&
			    movementRequests.get(0).getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && checkIfEmployeesInHighRanks(movementRequests))
			addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), "1", session);
		    else
			addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);

		}

		String internalCopies = EmployeesService.getEmployeesIdsString(internalCopiesEmployees);
		addWFMovements(movementRequests, instance.getInstanceId(), internalCopies, externalCopies, session);

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
    }

    private static Boolean checkIfEmployeesInHighRanks(List<WFMovementData> movementRequests) throws BusinessException {
	for (WFMovementData wfMovementData : movementRequests) {
	    if (wfMovementData.getEmployeeRankId() < RanksEnum.MAJOR.getCode())
		return true;
	}
	return false;
    }

    /**
     * handles the actions of exchange employee
     * 
     * @param requester
     *            employee started the workflow instance
     * @param instance
     *            workflow instance
     * @param eeTask
     *            task to be handled
     * @param isApproved
     *            Action Taken
     * @throws BusinessException
     *             if any error occurs
     * @see WFTaskActionsEnum
     * 
     */
    public static void doMovementEE(EmployeeData requester, long replacementEmpId, WFInstance instance, WFTask eeTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    String role = WFTaskRolesEnum.DIRECT_MANAGER.getCode();
	    long assigneeId = requester.getEmpId();
	    long originalId = requester.getEmpId();

	    if (isApproved) {
		EmployeeData replacementEmployee = EmployeesService.getEmployeeData(replacementEmpId);

		if (requester.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {

		    // If the replacement move is between general directorate and any region we have to start with the direct managers cycle of the employee from the general directorate
		    if (replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			    && !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			originalId = replacementEmployee.getManagerId();
			assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getPhysicalUnitId());
		    } else {
			originalId = requester.getManagerId();
			assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getPhysicalUnitId());
		    }

		} else if (requester.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {

		    // If the replacement move is between general directorate and any region we have to start with the direct managers cycle of the employee from the general directorate
		    if (replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			    && !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			originalId = replacementEmployee.getManagerId();
			assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getPhysicalUnitId());
		    } else {
			originalId = requester.getManagerId();
			assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getPhysicalUnitId());
		    }
		} else {
		    originalId = requester.getManagerId();
		    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getPhysicalUnitId());
		}

		completeWFTask(eeTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), assigneeId, originalId, eeTask.getTaskUrl(), role, eeTask.getLevel(), session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, eeTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    /**
     * handle the actions of the direct manager
     * 
     * If direct manager rejects then close instance and notify requester If direct manager approves then send to his manager until stop criteria
     * 
     * @param requester
     *            employee started the instance
     * @param instance
     *            movement workflow instance
     * @param movementRequests
     *            requested movements workflow
     * @param dmTask
     *            task to be handled
     * @param isApproved
     *            action taken by manager
     * @throws BusinessException
     *             if any error occurs
     * @see WFTaskActionsEnum
     */
    public static void doMovementDM(EmployeeData requester, WFInstance instance, List<WFMovementData> movementRequests, WFTask dmTask, boolean isApproved) throws BusinessException {
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
		Long replacementEmployeeId = movementRequests.get(0).getReplacementEmployeeId();
		EmployeeData replacementEmployee = replacementEmployeeId == null ? null : EmployeesService.getEmployeeData(replacementEmployeeId);

		/************************ Determine if cycle is completed ***********************/
		boolean cycleCompleted = false, generalDirectorateDecision = true;

		if (movementRequests.get(0).getCategoryId().longValue() == CategoriesEnum.OFFICERS.getCode()) {

		    if (movementRequests.get(0).getReplacementEmployeeId() != null) {

			if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				|| replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			    if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				cycleCompleted = true;
			    }
			} else {
			    if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
				cycleCompleted = true;
			    }
			}

			if (!requester.getOfficialRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				// the following condition works for Exchange Move also.
				&& UnitsService.getUnitById(movementRequests.get(0).getEmployeeUnitId()).getRegionId().equals(UnitsService.getUnitById(movementRequests.get(0).getUnitId()).getRegionId())
				&& requester.getRankId() >= RanksEnum.MAJOR.getCode()
				&& EmployeesService.getEmployeeData(movementRequests.get(0).getReplacementEmployeeId()).getRankId() >= RanksEnum.MAJOR.getCode()) {

			    generalDirectorateDecision = false;

			}

		    } else {
			if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
			    if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				cycleCompleted = true;
			    }
			} else {
			    if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
				cycleCompleted = true;
			    }
			}

			if (!requester.getOfficialRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) && movementRequests.get(0).getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode())
				&& UnitsService.getUnitById(movementRequests.get(0).getEmployeeUnitId()).getRegionId().equals(UnitsService.getUnitById(movementRequests.get(0).getUnitId()).getRegionId())
				&& requester.getRankId() >= RanksEnum.MAJOR.getCode()
				&& !MovementsService.checkIfEmployeeExistsInCertainPositions(requester)) {

			    generalDirectorateDecision = false;
			}
		    }

		} else if (movementRequests.get(0).getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode()) {

		    if (movementRequests.get(0).getReplacementEmployeeId() != null) {

			if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				|| replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			    if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				cycleCompleted = true;
			    }
			} else {
			    if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
				cycleCompleted = true;
			    }
			}

			if (!requester.getOfficialRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				&& UnitsService.getUnitById(movementRequests.get(0).getEmployeeUnitId()).getRegionId().equals(UnitsService.getUnitById(movementRequests.get(0).getUnitId()).getRegionId())) {
			    generalDirectorateDecision = false;
			}

		    } else {
			if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			    if (movementRequests.get(0).getLocationFlag().intValue() == LocationFlagsEnum.EXTERNAL.getCode()
				    && (movementRequests.get(0).getMovementTypeId().equals(MovementTypesEnum.MOVE.getCode())
					    || movementRequests.get(0).getMovementTypeId().equals(MovementTypesEnum.SUBJOIN.getCode()))) {

				if (EmployeesService.getEmployeeData(dmTask.getOriginalId()).getUnitTypeCode() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				    cycleCompleted = true;
				}
			    } else if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
				cycleCompleted = true;
			    }
			} else {
			    if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
				cycleCompleted = true;
			    }
			}

			if (!requester.getOfficialRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) && movementRequests.get(0).getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode())
				&& UnitsService.getUnitById(movementRequests.get(0).getEmployeeUnitId()).getRegionId().equals(UnitsService.getUnitById(movementRequests.get(0).getUnitId()).getRegionId())) {

			    generalDirectorateDecision = false;
			}
		    }

		} else {

		    if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			cycleCompleted = true;
		    }

		}
		/******************************************************************************/

		if (cycleCompleted) {

		    if (replacementEmployeeId != null) {

			if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {

			    if (generalDirectorateDecision) {

				// Two different regions
				if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
					&& !replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
					&& !requester.getPhysicalRegionId().equals(replacementEmployee.getPhysicalRegionId())) {

				    role = WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode();
				    originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
				} else {
				    role = WFTaskRolesEnum.REPLACEMENT_DIRECT_MANAGER.getCode();

				    // If the replacement move is between general directorate and any region we have to start with the direct managers cycle of the employee from the general directorate
				    if (replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
					    && !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
					originalId = requester.getManagerId();
					assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
				    } else {
					originalId = replacementEmployee.getManagerId();
					assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());
				    }
				}

			    } else {
				originalId = replacementEmployee.getManagerId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());
				role = WFTaskRolesEnum.REPLACEMENT_DIRECT_MANAGER.getCode();
			    }

			} else if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {

			    if (generalDirectorateDecision) {

				// Two different regions
				if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
					&& !replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
					&& !requester.getPhysicalRegionId().equals(replacementEmployee.getPhysicalRegionId())) {

				    role = WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode();
				    originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
				} else {
				    role = WFTaskRolesEnum.REPLACEMENT_DIRECT_MANAGER.getCode();

				    // If the replacement move is between general directorate and any region we have to start with the direct managers cycle of the employee from the general directorate
				    if (replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
					    && !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
					originalId = requester.getManagerId();
					assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
				    } else {
					originalId = replacementEmployee.getManagerId();
					assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());
				    }
				}

			    } else {
				originalId = replacementEmployee.getManagerId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());
				role = WFTaskRolesEnum.REPLACEMENT_DIRECT_MANAGER.getCode();
			    }

			} else {
			    originalId = replacementEmployee.getManagerId();
			    assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());
			    role = WFTaskRolesEnum.REPLACEMENT_DIRECT_MANAGER.getCode();
			}

		    } else {

			for (WFMovementData movementRequest : movementRequests) {
			    movementRequest.setApprovedId(dmTask.getOriginalId());
			    DataAccess.updateEntity(movementRequest.getWfMovement(), session);
			}

			if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
			    if (generalDirectorateDecision) {
				if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
				    role = WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode();
				    originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				} else {
				    role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
				    originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				}
			    } else {
				role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
				originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
			    }
			} else if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
			    if (generalDirectorateDecision) {
				if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {
				    role = WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode();
				    originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				} else {
				    role = WFTaskRolesEnum.MANAGER_REDIRECT_TO_SECRUITY.getCode();
				    originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				}
			    } else {
				role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
				originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
			    }
			} else {
			    role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
			    originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), generalDirectorateDecision ? RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() : requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
			}

			assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    }
		} else {

		    role = WFTaskRolesEnum.DIRECT_MANAGER.getCode();
		    originalId = nextDM.getEmpId();

		    if ((movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) || movementRequests.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))) {

			// If the replacement move is between general directorate and any region we have to start with the direct managers cycle of the employee from the general directorate
			if (replacementEmployeeId != null && replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				&& !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			    assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());
			} else {
			    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
			}
		    } else {
			assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    }
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

    /**
     * handle the actions of direct manager of the replacement employee
     * 
     * if direct manager rejects then close instance and notify requester If direct manager approves then send to his manager until stop criteria
     * 
     * @param requester
     *            employee started the instance
     * @param instance
     *            movement workflow instance
     * @param movementRequests
     *            requested movements
     * @param dmTask
     *            task to be handled
     * @param isApproved
     *            action taken
     * @throws BusinessException
     *             if any errors happens
     * @see WFTaskActionsEnum
     */
    public static void doMovementReplacementDM(EmployeeData requester, WFInstance instance, List<WFMovementData> movementRequests, WFTask dmTask, boolean isApproved) throws BusinessException {
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
		Long replacementEmployeeId = movementRequests.get(0).getReplacementEmployeeId();
		EmployeeData replacementEmployee = replacementEmployeeId == null ? null : EmployeesService.getEmployeeData(replacementEmployeeId);

		/************************ Determine if cycle is completed ***********************/
		boolean cycleCompleted = false, generalDirectorateDecision = true;

		if (movementRequests.get(0).getCategoryId().longValue() == CategoriesEnum.OFFICERS.getCode()) {

		    if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			    && replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    cycleCompleted = true;
			}
		    } else {
			if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    cycleCompleted = true;
			}
		    }

		    if (!requester.getOfficialRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			    // the following condition works for Exchange Move also.
			    && UnitsService.getUnitById(movementRequests.get(0).getEmployeeUnitId()).getRegionId().equals(UnitsService.getUnitById(movementRequests.get(0).getUnitId()).getRegionId())
			    && requester.getRankId() >= RanksEnum.MAJOR.getCode()
			    && EmployeesService.getEmployeeData(movementRequests.get(0).getReplacementEmployeeId()).getRankId() >= RanksEnum.MAJOR.getCode()) {

			generalDirectorateDecision = false;

		    }

		} else if (movementRequests.get(0).getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode()) {

		    if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			    && replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			    cycleCompleted = true;
			}
		    } else {
			if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    cycleCompleted = true;
			}
		    }

		    if (!requester.getOfficialRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			    && UnitsService.getUnitById(movementRequests.get(0).getEmployeeUnitId()).getRegionId().equals(UnitsService.getUnitById(movementRequests.get(0).getUnitId()).getRegionId())) {

			generalDirectorateDecision = false;
		    }

		} else {

		    if (nextDM.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			cycleCompleted = true;
		    }

		}
		/******************************************************************************/

		if (cycleCompleted) {

		    for (WFMovementData movementRequest : movementRequests) {
			movementRequest.setApprovedId(dmTask.getOriginalId());
			DataAccess.updateEntity(movementRequest.getWfMovement(), session);
		    }

		    if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {

			if (generalDirectorateDecision) {

			    // Two different regions
			    if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				    && !replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				    && !requester.getPhysicalRegionId().equals(replacementEmployee.getPhysicalRegionId())) {

				role = WFTaskRolesEnum.REPLACEMENT_SECONDARY_MANAGER_REDIRECT.getCode();
				originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), replacementEmployee.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());

				// Two employees from general directorate
			    } else if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				    && replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

				role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
				originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());

				// Same region but general directorate decision
			    } else if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				    && !replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

				role = WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode();
				originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());

				// One employee from a region and the other one from general directorate
			    } else {
				role = WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode();
				// If getMovementManager is changed to official region instead of physical region please change the condition to official
				originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) ? replacementEmployee.getPhysicalRegionId() : requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) ? replacementEmployee.getEmpId() : requester.getEmpId());
			    }

			    // Two employees from the same region
			} else {
			    role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
			    originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
			    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
			}
		    } else if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {

			if (generalDirectorateDecision) {

			    // Two different regions
			    if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				    && !replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				    && !requester.getPhysicalRegionId().equals(replacementEmployee.getPhysicalRegionId())) {

				role = WFTaskRolesEnum.REPLACEMENT_SECONDARY_MANAGER_REDIRECT.getCode();
				originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), replacementEmployee.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());

				// Two employees from general directorate
			    } else if (requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				    && replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

				role = WFTaskRolesEnum.MANAGER_REDIRECT_TO_SECRUITY.getCode();
				originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());

				// One employee from a region and the other one from general directorate
			    } else {
				role = WFTaskRolesEnum.SECONDARY_MANAGER_REDIRECT.getCode();
				// If getMovementManager is changed to official region instead of physical region please change the condition to official
				originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) ? replacementEmployee.getPhysicalRegionId() : requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) ? replacementEmployee.getEmpId() : requester.getEmpId());
			    }

			    // Two employees from the same region
			} else {
			    role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
			    originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), requester.getPhysicalRegionId(), movementRequests.get(0).getMovementTypeId()).getEmpId();
			    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
			}
		    } else {
			role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
			originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), movementRequests.get(0).getMovementTypeId()).getEmpId();
			assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    }

		} else {
		    role = WFTaskRolesEnum.REPLACEMENT_DIRECT_MANAGER.getCode();
		    originalId = nextDM.getEmpId();

		    if ((movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) || movementRequests.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))) {

			// If the replacement move is between general directorate and any region we have to start with the direct managers cycle of the employee from the general directorate
			if (replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				&& !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
			} else {
			    assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());
			}
		    } else {
			assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());
		    }
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

    /**
     * handle the actions of the reviewer employee redirect
     * 
     * Manager redirect to security role is to redirect request to security employee or refuse the request
     * 
     * @param requester
     *            employee started the instance
     * @param instance
     *            movement workflow instance
     * @param mrtsTask
     *            task to be handled
     * @param isApproved
     *            action taken by manager
     * @throws BusinessException
     *             if any error occurs
     * @see WFTaskActionsEnum
     */
    public static void doMovementMRTS(EmployeeData requester, WFInstance instance, WFTask mrtsTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    if (isApproved) {
		EmployeeData securityEmployee = getGeneralDirectorateSecurityEmployee();
		completeWFTask(mrtsTask, WFTaskActionsEnum.REDIRECT_SECURITY.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(securityEmployee.getEmpId(), instance.getProcessId(), requester.getEmpId()), securityEmployee.getEmpId(), mrtsTask.getTaskUrl(), WFTaskRolesEnum.SECURITY_EMP.getCode(), mrtsTask.getLevel(), session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, mrtsTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    /**
     * handle the actions of the security employee
     * 
     * Security employee role is to redirect request to manager redirect or refuse the request
     * 
     * @param requester
     *            employee started the instance
     * @param instance
     *            movement workflow instance
     * @param seTask
     *            task to be handled
     * @param isApproved
     *            action taken by manager
     * @throws BusinessException
     *             if any error occurs
     * @see WFTaskActionsEnum
     */
    public static void doMovementSE(EmployeeData requester, WFInstance instance, WFTask seTask, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFTask managerRedirectToSecurityTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.MANAGER_REDIRECT_TO_SECRUITY.getCode()).get(0);

	    if (isApproved) {
		completeWFTask(seTask, WFTaskActionsEnum.PASS_SECURITY_SCAN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(managerRedirectToSecurityTask.getOriginalId(), instance.getProcessId(), requester.getEmpId()), managerRedirectToSecurityTask.getOriginalId(), seTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), seTask.getLevel(), session);
	    } else {
		completeWFTask(seTask, WFTaskActionsEnum.NOT_PASS_SECURITY_SCAN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(managerRedirectToSecurityTask.getOriginalId(), instance.getProcessId(), requester.getEmpId()), managerRedirectToSecurityTask.getOriginalId(), seTask.getTaskUrl(), WFTaskRolesEnum.MANAGER_REDIRECT.getCode(), seTask.getLevel(), session);
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

    /**
     * handle the actions of manager redirect employee
     * 
     * @param requester
     *            employee started the instance
     * @param instance
     *            movement workflow instance
     * @param mrTask
     *            task to be handled
     * @param reviewerEmpId
     *            Id of the selected employee by redirect manager
     * @throws BusinessException
     *             if any error occurs
     */
    public static void doMovementMR(EmployeeData requester, WFInstance instance, WFTask mrTask, long reviewerEmpId, boolean isApproved) throws BusinessException {
	try {
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved)
		completeWFTask(mrTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId()), reviewerEmpId, mrTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), mrTask.getLevel());
	    else
		closeWFInstanceByAction(requester.getEmpId(), instance, mrTask, WFTaskActionsEnum.REJECT.getCode(), null);
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * handle the actions of secondary manager redirect employee
     * 
     * @param requester
     *            employee started the instance
     * @param instance
     *            movement workflow instance
     * @param mrTask
     *            task to be handled
     * @param reviewerEmpId
     *            Id of the selected employee by redirect manager
     * @throws BusinessException
     *             if any error occurs
     */
    public static void doMovementSMR(EmployeeData requester, WFInstance instance, Long replacementEmpId, WFTask smrTask, long reviewerEmpId) throws BusinessException {
	try {
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    long assigneeId;

	    // TODO SIMPLIFY
	    if (replacementEmpId != null) {

		EmployeeData replacementEmployee = EmployeesService.getEmployeeData(replacementEmpId);
		if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			&& !replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

		    assigneeId = getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId());

		} else if (replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			&& !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

		    assigneeId = getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId());
		} else {
		    assigneeId = getDelegate(reviewerEmpId, instance.getProcessId(), replacementEmployee.getEmpId());
		}
	    } else {
		assigneeId = getDelegate(reviewerEmpId, instance.getProcessId(), requester.getEmpId());
	    }

	    completeWFTask(smrTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), assigneeId, reviewerEmpId, smrTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode(), smrTask.getLevel());
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * handle the actions of replacement secondary manager redirect employee
     * 
     * @param requester
     *            employee started the instance
     * @param instance
     *            movement workflow instance
     * @param mrTask
     *            task to be handled
     * @param reviewerEmpId
     *            Id of the selected employee by redirect manager
     * @throws BusinessException
     *             if any error occurs
     */
    public static void doMovementRSMR(EmployeeData requester, WFInstance instance, List<WFMovementData> wfMovementsList, WFTask rsmrTask, long reviewerEmpId) throws BusinessException {
	try {
	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    completeWFTask(rsmrTask, WFTaskActionsEnum.REDIRECT.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(reviewerEmpId, instance.getProcessId(), wfMovementsList.get(0).getReplacementEmployeeId()), reviewerEmpId, rsmrTask.getTaskUrl(), WFTaskRolesEnum.REPLACEMENT_SECONDARY_REVIEWER_EMP.getCode(), rsmrTask.getLevel());
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * handle the action of reviewer employee
     * 
     * @param requester
     *            employee started the workflow instance
     * @param instance
     *            work flow instance
     * @param movementRequests
     *            requested movements
     * @param reTask
     *            task to be handled
     * @param internalCopies
     *            Internal employees which receive notification upon termination of workflow instance
     * @param externalCopies
     *            External employees which receive hard copy of decision
     * @param isApproved
     *            action taken
     * @throws BusinessException
     *             if any error occurs
     * @see WFTaskActionsEnum
     */
    public static void doMovementRE(EmployeeData requester, WFInstance instance, List<WFMovementData> movementRequests, WFTask reTask, String attachments, List<EmployeeData> internalCopiesEmployees, String externalCopies, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {
		if (movementRequests != null && movementRequests.size() > 0) {
		    updateWFInstanceBeneficiaries(instance.getInstanceId(), getMovementsInstanceBeneficiariesIds(movementRequests), session);

		    boolean isRequest = isRequestProcess(instance.getProcessId(), movementRequests.get(0).getCategoryId());
		    if (!isRequest && !movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !movementRequests.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && !checkIfEmployeeExistsInWFPositionUnit(movementRequests.get(0).getCategoryId(), requester, movementRequests.get(0).getMovementTypeId()))
			throw new BusinessException("error_unAuthorizedRequester");
		    List<WFTask> includedSRETasks = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode());
		    if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) &&
			    movementRequests.get(0).getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && checkIfEmployeesInHighRanks(movementRequests) && (includedSRETasks == null || includedSRETasks.size() == 0))
			throw new BusinessException("error_cantAddHigherRanks");

		    validateMovementRequests(movementRequests, requester, instance.getInstanceId(), instance.getProcessId(), reTask, false, isRequest ? MovementTransactionViewsEnum.REQUEST.getCode() : MovementTransactionViewsEnum.DECISION.getCode());

		    List<WFMovementData> movementRequestsToAdd = new ArrayList<WFMovementData>();
		    List<WFMovementData> movementRequestsToUpdate = new ArrayList<WFMovementData>();

		    for (WFMovementData movementRequest : movementRequests) {
			if (movementRequest.getInstanceId() == null) {
			    movementRequestsToAdd.add(movementRequest);
			} else
			    movementRequestsToUpdate.add(movementRequest);
		    }

		    String internalCopies = EmployeesService.getEmployeesIdsString(internalCopiesEmployees);
		    if (movementRequestsToAdd.size() > 0)
			addWFMovements(movementRequestsToAdd, instance.getInstanceId(), internalCopies, externalCopies, session);
		    if (movementRequestsToUpdate.size() > 0)
			updateWFMovements(movementRequestsToUpdate, internalCopies, externalCopies, session);

		    EmployeeData movementManager = EmployeesService.getEmployeeDirectManager(reTask.getOriginalId());
		    completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(movementManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), movementManager.getEmpId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), reTask.getLevel(), session);
		}
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, reTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
	    }

	    updateWFInstanceAttachments(instance, attachments, session);

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

    /**
     * handle the action of the secondary reviewer employee
     * 
     * @param requester
     *            employee started the workflow instance
     * @param instance
     *            work flow instance
     * @param movementRequests
     *            requested movements
     * @param reTask
     *            task to be handled
     * @param internalCopies
     *            Internal employees which receive notification upon termination of workflow instance
     * @param externalCopies
     *            External employees which receive hard copy of decision
     * @param isApproved
     *            action taken
     * @throws BusinessException
     *             if any error occurs
     * @see WFTaskActionsEnum
     */
    public static void doMovementSRE(EmployeeData requester, WFInstance instance, List<WFMovementData> movementRequests, WFTask sreTask, List<EmployeeData> internalCopiesEmployees, String externalCopies, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {
		String internalCopies = EmployeesService.getEmployeesIdsString(internalCopiesEmployees);

		updateWFMovements(movementRequests, internalCopies, externalCopies, session);

		EmployeeData movementManager = EmployeesService.getEmployeeDirectManager(sreTask.getOriginalId());

		long assigneeId;

		if (movementRequests.get(0).getReplacementEmployeeId() != null) {

		    EmployeeData replacementEmployee = EmployeesService.getEmployeeData(movementRequests.get(0).getReplacementEmployeeId());
		    if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			    && !replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			assigneeId = getDelegate(movementManager.getEmpId(), instance.getProcessId(), requester.getEmpId());

		    } else if (replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			    && !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			assigneeId = getDelegate(movementManager.getEmpId(), instance.getProcessId(), requester.getEmpId());
		    } else {
			assigneeId = getDelegate(movementManager.getEmpId(), instance.getProcessId(), replacementEmployee.getEmpId());
		    }
		} else {
		    assigneeId = getDelegate(movementManager.getEmpId(), instance.getProcessId(), requester.getEmpId());
		}

		completeWFTask(sreTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), assigneeId, movementManager.getEmpId(), sreTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), sreTask.getLevel(), session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, sreTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    /**
     * handle the action of the replacement secondary reviewer employee
     * 
     * @param requester
     *            employee started the workflow instance
     * @param instance
     *            work flow instance
     * @param movementRequests
     *            requested movements
     * @param reTask
     *            task to be handled
     * @param internalCopies
     *            Internal employees which receive notification upon termination of workflow instance
     * @param externalCopies
     *            External employees which receive hard copy of decision
     * @param isApproved
     *            action taken
     * @throws BusinessException
     *             if any error occurs
     * @see WFTaskActionsEnum
     */
    public static void doMovementRSRE(EmployeeData requester, WFInstance instance, List<WFMovementData> movementRequests, WFTask rsreTask, List<EmployeeData> internalCopiesEmployees, String externalCopies, boolean isApproved) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {
		String internalCopies = EmployeesService.getEmployeesIdsString(internalCopiesEmployees);

		updateWFMovements(movementRequests, internalCopies, externalCopies, session);

		EmployeeData movementManager = EmployeesService.getEmployeeDirectManager(rsreTask.getOriginalId());

		EmployeeData replacementEmployee = EmployeesService.getEmployeeData(movementRequests.get(0).getReplacementEmployeeId());

		completeWFTask(rsreTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(movementManager.getEmpId(), instance.getProcessId(), replacementEmployee.getEmpId()), movementManager.getEmpId(), rsreTask.getTaskUrl(), WFTaskRolesEnum.REPLACEMENT_SECONDARY_SIGN_MANAGER.getCode(), rsreTask.getLevel(), session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, rsreTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    /**
     * handle the actions of planning and organization departments employees used in move by freeze process
     * 
     * @param requester
     *            employee started the workflow instance
     * @param instance
     *            workflow instance
     * @param movementRequests
     *            requested movements
     * @param pomTask
     *            task to be handled
     * @param approvalFlag
     *            action taken
     * @throws BusinessException
     *             if any error occurs
     * @see WFTaskActionsEnum
     */
    public static void doMovementPOM(EmployeeData requester, WFInstance instance, List<WFMovementData> movementRequests, WFTask pomTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		EmployeeData nextManager = EmployeesService.getEmployeeDirectManager(pomTask.getOriginalId());

		if (nextManager.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {

		    // done, go to requester direct direct manager, as direct manager already signed after the requester
		    EmployeeData requesterDirectManager = EmployeesService.getEmployeeData(requester.getManagerId());
		    completeWFTask(pomTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requesterDirectManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), requesterDirectManager.getManagerId(), pomTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), pomTask.getLevel(), session);

		} else {
		    // go to next sign manager
		    completeWFTask(pomTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), pomTask.getTaskUrl(), WFTaskRolesEnum.PLANNING_ORGANIZATION_MANAGER.getCode(), pomTask.getLevel(), session);
		}
	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		completeWFTask(pomTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requester.getEmpId(), instance.getProcessId(), requester.getEmpId()), requester.getEmpId(), pomTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), pomTask.getLevel(), session);
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, pomTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    /**
     * Handle the actions of sign manager
     * 
     * @param requester
     *            employee started the workflow instance
     * @param instance
     *            workflow instance
     * @param movementRequests
     *            requested movements
     * @param smTask
     *            task to be handled
     * @param approvalFlag
     *            action taken
     * @throws BusinessException
     *             if any error occurs
     * @see WFTaskActionsEnum
     */
    public static void doMovementSM(EmployeeData requester, WFInstance instance, List<WFMovementData> movementRequests, WFTask smTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		EmployeeData manager = EmployeesService.getEmployeeData(smTask.getOriginalId());

		if (manager.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode() || manager.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
		    closeMovementWorkFlow(requester, instance, movementRequests, smTask, manager.getPhysicalRegionId(), session);
		}
		// Send to next manager
		else {

		    String role = WFTaskRolesEnum.SIGN_MANAGER.getCode();
		    EmployeeData nextManager = EmployeesService.getEmployeeDirectManager(smTask.getOriginalId());
		    if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())
			    && (movementRequests.get(0).getMovementTypeId().equals(MovementTypesEnum.MOVE.getCode())
				    || movementRequests.get(0).getMovementTypeId().equals(MovementTypesEnum.SUBJOIN.getCode()))
			    && movementRequests.get(0).getLocationFlag().intValue() == LocationFlagsEnum.INTERNAL.getCode()) {

			if (nextManager.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {

			    WFPosition position = getWFPosition(WFPositionsEnum.VICE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			    nextManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());

			} else if (instance.getProcessId().equals(WFProcessesEnum.SOLDIERS_MOVE.getCode()) && movementRequests.get(0).getFreezeJobId() != null && requester.getManagerId().equals(smTask.getOriginalId())) {

			    WFPosition manpowerOrganizationPosition = getWFPosition(WFPositionsEnum.ORGANIZATION_MANPOWER_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
			    nextManager = EmployeesService.getEmployeeByPosition(manpowerOrganizationPosition.getUnitId(), manpowerOrganizationPosition.getEmpId());
			    role = WFTaskRolesEnum.PLANNING_ORGANIZATION_MANAGER.getCode();
			}
		    }
		    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), nextManager.getEmpId(), smTask.getTaskUrl(), role, smTask.getLevel(), session);
		}

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		long originalId = requester.getEmpId();
		if (isRequestProcess(instance.getProcessId(), movementRequests.get(0).getCategoryId().longValue()) ||
			(movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) &&
				movementRequests.get(0).getLocationFlag().equals(LocationFlagsEnum.INTERNAL.getCode()) && checkIfEmployeesInHighRanks(movementRequests))) {
		    WFTask reviewerTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REVIEWER_EMP.getCode()).get(0);
		    originalId = reviewerTask.getOriginalId();
		}
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(originalId, instance.getProcessId(), requester.getEmpId()), originalId, smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), smTask.getLevel(), session);

		updateWFMovements(movementRequests, movementRequests.get(0).getInternalCopies(), movementRequests.get(0).getExternalCopies(), session);
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

    /**
     * Handle the actions of secondary sign manager, only for officers and soldiers requests
     * 
     * @param requester
     *            employee started the workflow instance
     * @param instance
     *            workflow instance
     * @param movementRequests
     *            requested movements
     * @param smTask
     *            task to be handled
     * @param approvalFlag
     *            action taken
     * @throws BusinessException
     *             if any error occurs
     * @see WFTaskActionsEnum
     */
    public static void doMovementSSM(EmployeeData requester, WFInstance instance, List<WFMovementData> movementRequests, WFTask ssmTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		EmployeeData manager = EmployeesService.getEmployeeData(ssmTask.getOriginalId());

		if ((movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())
			&& manager.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode())
			||
			((movementRequests.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))
				&&
				// In external movement, general manager must sign before the reviewer employee inserts ministry number and date
				((movementRequests.get(0).getLocationFlag().intValue() == LocationFlagsEnum.EXTERNAL.getCode()
					&& manager.getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) || (movementRequests.get(0).getLocationFlag().intValue() == LocationFlagsEnum.INTERNAL.getCode()
						&& manager.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode())))) {

		    String role;
		    long assigneeId = requester.getEmpId();
		    long originalId = requester.getEmpId();

		    if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {

			if (movementRequests.get(0).getReplacementEmployeeId() != null) {

			    EmployeeData replacementEmployee = EmployeesService.getEmployeeData(movementRequests.get(0).getReplacementEmployeeId());

			    if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				    && !replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				    && !requester.getPhysicalRegionId().equals(replacementEmployee.getPhysicalRegionId())) {

				role = WFTaskRolesEnum.REPLACEMENT_DIRECT_MANAGER.getCode();
				originalId = replacementEmployee.getManagerId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());
			    } else {
				role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
				originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
			    }
			} else {
			    role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
			    originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), movementRequests.get(0).getMovementTypeId()).getEmpId();
			    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
			}
		    } else {
			// Only soldiers
			if (movementRequests.get(0).getReplacementEmployeeId() != null) {

			    EmployeeData replacementEmployee = EmployeesService.getEmployeeData(movementRequests.get(0).getReplacementEmployeeId());

			    if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				    && !replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				    && !requester.getPhysicalRegionId().equals(replacementEmployee.getPhysicalRegionId())) {

				role = WFTaskRolesEnum.REPLACEMENT_DIRECT_MANAGER.getCode();
				originalId = replacementEmployee.getManagerId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());
			    } else {
				role = WFTaskRolesEnum.MANAGER_REDIRECT_TO_SECRUITY.getCode();
				originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), movementRequests.get(0).getMovementTypeId()).getEmpId();
				assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
			    }
			} else {
			    role = WFTaskRolesEnum.MANAGER_REDIRECT_TO_SECRUITY.getCode();
			    originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), movementRequests.get(0).getMovementTypeId()).getEmpId();
			    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
			}
		    }

		    for (WFMovementData movementRequest : movementRequests) {
			movementRequest.setApprovedId(ssmTask.getOriginalId());
			DataAccess.updateEntity(movementRequest.getWfMovement(), session);
		    }

		    completeWFTask(ssmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), assigneeId, originalId, ssmTask.getTaskUrl(), role, ssmTask.getLevel(), session);
		}
		// Send to next manager
		else {
		    EmployeeData nextManager = EmployeesService.getEmployeeDirectManager(ssmTask.getOriginalId());

		    long assigneeId;

		    if (movementRequests.get(0).getReplacementEmployeeId() != null) {

			EmployeeData replacementEmployee = EmployeesService.getEmployeeData(movementRequests.get(0).getReplacementEmployeeId());
			if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				&& !replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			    assigneeId = getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId());

			} else if (replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
				&& !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			    assigneeId = getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId());
			} else {
			    assigneeId = getDelegate(nextManager.getEmpId(), instance.getProcessId(), replacementEmployee.getEmpId());
			}
		    } else {
			assigneeId = getDelegate(nextManager.getEmpId(), instance.getProcessId(), requester.getEmpId());
		    }

		    completeWFTask(ssmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), assigneeId, nextManager.getEmpId(), ssmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(), ssmTask.getLevel(), session);
		}

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		long originalId = requester.getEmpId();
		if (isRequestProcess(instance.getProcessId(), movementRequests.get(0).getCategoryId().longValue())) {
		    WFTask reviewerTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode()).get(0);
		    originalId = reviewerTask.getOriginalId();
		}

		long assigneeId;

		if (movementRequests.get(0).getReplacementEmployeeId() != null) {

		    EmployeeData replacementEmployee = EmployeesService.getEmployeeData(movementRequests.get(0).getReplacementEmployeeId());
		    if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			    && !replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());

		    } else if (replacementEmployee.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			    && !requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

			assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    } else {
			assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmployee.getEmpId());
		    }
		} else {
		    assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		}

		completeWFTask(ssmTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), assigneeId, originalId, ssmTask.getTaskUrl(), WFTaskRolesEnum.SECONDARY_REVIEWER_EMP.getCode(), ssmTask.getLevel(), session);

	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, ssmTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    /**
     * Handle the actions of replacement secondary sign manager, only for officers and soldiers requests
     * 
     * @param requester
     *            employee started the workflow instance
     * @param instance
     *            workflow instance
     * @param movementRequests
     *            requested movements
     * @param smTask
     *            task to be handled
     * @param approvalFlag
     *            action taken
     * @throws BusinessException
     *             if any error occurs
     * @see WFTaskActionsEnum
     */
    public static void doMovementRSSM(EmployeeData requester, WFInstance instance, List<WFMovementData> movementRequests, WFTask rssmTask, int approvalFlag) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    EmployeeData replacementEmp = EmployeesService.getEmployeeData(movementRequests.get(0).getReplacementEmployeeId());

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {

		EmployeeData manager = EmployeesService.getEmployeeData(rssmTask.getOriginalId());

		if (manager.getUnitTypeCode().intValue() >= UnitTypesEnum.REGION_COMMANDER.getCode()) {

		    String role;
		    long assigneeId = requester.getEmpId();
		    long originalId = requester.getEmpId();
		    if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
			role = WFTaskRolesEnum.MANAGER_REDIRECT.getCode();
			originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), movementRequests.get(0).getMovementTypeId()).getEmpId();
		    } else {
			// Only soldiers
			role = WFTaskRolesEnum.MANAGER_REDIRECT_TO_SECRUITY.getCode();
			originalId = getMovementManager(movementRequests.get(0).getCategoryId().longValue(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode(), movementRequests.get(0).getMovementTypeId()).getEmpId();
		    }

		    assigneeId = getDelegate(originalId, instance.getProcessId(), replacementEmp.getEmpId());

		    for (WFMovementData movementRequest : movementRequests) {
			movementRequest.setApprovedId(rssmTask.getOriginalId());
			DataAccess.updateEntity(movementRequest.getWfMovement(), session);
		    }

		    completeWFTask(rssmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), assigneeId, originalId, rssmTask.getTaskUrl(), role, rssmTask.getLevel(), session);
		}
		// Send to next manager
		else {
		    EmployeeData nextManager = EmployeesService.getEmployeeDirectManager(rssmTask.getOriginalId());
		    completeWFTask(rssmTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(nextManager.getEmpId(), instance.getProcessId(), replacementEmp.getEmpId()), nextManager.getEmpId(), rssmTask.getTaskUrl(), WFTaskRolesEnum.REPLACEMENT_SECONDARY_SIGN_MANAGER.getCode(), rssmTask.getLevel(), session);
		}

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		long originalId = requester.getEmpId();
		if (isRequestProcess(instance.getProcessId(), movementRequests.get(0).getCategoryId().longValue())) {
		    WFTask reviewerTask = getWFInstanceTasksByRole(instance.getInstanceId(), WFTaskRolesEnum.REPLACEMENT_SECONDARY_REVIEWER_EMP.getCode()).get(0);
		    originalId = reviewerTask.getOriginalId();
		}
		completeWFTask(rssmTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(originalId, instance.getProcessId(), replacementEmp.getEmpId()), originalId, rssmTask.getTaskUrl(), WFTaskRolesEnum.REPLACEMENT_SECONDARY_REVIEWER_EMP.getCode(), rssmTask.getLevel(), session);

	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, rssmTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
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

    /*---------------------------Work Flow Joining Date----------------------*/
    public static void initMovementJoiningDate(EmployeeData requester, WFMovementData movementRequest, long processId, String taskUrl) throws BusinessException {

	if (movementRequest == null || movementRequest.getTransactionTypeId() == null || movementRequest.getMovementTypeId() == null || movementRequest.getTransactionId() == null)
	    throw new BusinessException("error_transactionDataError");
	if (movementRequest.getEmployeeId() == null)
	    throw new BusinessException("error_employeeMandatory");

	List<WFMovement> wfMovementsRunningRequests = getRunningRequests(new Long[] { movementRequest.getEmployeeId() }, null, null);
	if (!wfMovementsRunningRequests.isEmpty())
	    throw new BusinessException("error_conflictMovementsEmployee");

	if (movementRequest.getReturnJoiningDate() != null)
	    MovementsService.validateMovementTrasactionReturnJoiningDate(MovementsService.getMovementTransactionById(movementRequest.getTransactionId()), movementRequest.getReturnJoiningDate());
	else
	    MovementsService.validateMovementTrasactionJoiningDate(MovementsService.getMovementTransactionById(movementRequest.getTransactionId()), movementRequest.getJoiningDate(), FlagsEnum.ON.getCode());

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), null, Arrays.asList(movementRequest.getEmployeeId()), session);

	    addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.DIRECT_MANAGER.getCode(), "1", session);

	    addWFMovements(Arrays.asList(movementRequest), instance.getInstanceId(), null, null, session);

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

    public static void doMovementJoiningDateDM(EmployeeData requester, WFMovementData movementRequest, WFInstance instance, WFTask dmTask, boolean isApproved) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (isApproved) {

		EmployeeData nextDM = EmployeesService.getEmployeeDirectManager(dmTask.getOriginalId());

		if (nextDM.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode()) {
		    if (movementRequest.getReturnJoiningDate() != null)
			MovementsService.handleMovementTrasactionReturnJoiningDate(movementRequest.getTransactionId(), movementRequest.getReturnJoiningDate(), dmTask.getAssigneeId(), session);
		    else
			MovementsService.handleMovementTrasactionJoiningDate(movementRequest.getTransactionId(), movementRequest.getJoiningDate(), dmTask.getOriginalId(), dmTask.getAssigneeId(), FlagsEnum.ON.getCode(), session);
		    closeWFInstanceByAction(requester.getEmpId(), instance, dmTask, WFTaskActionsEnum.APPROVE.getCode(), new Long[] { movementRequest.getEmployeeId() }, session);
		} else {
		    long originalId = nextDM.getEmpId();
		    long assigneeId = getDelegate(originalId, instance.getProcessId(), requester.getEmpId());
		    completeWFTask(dmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), assigneeId, originalId, dmTask.getTaskUrl(), WFTaskRolesEnum.DIRECT_MANAGER.getCode(), dmTask.getLevel(), session);
		}

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

    /**
     * Gets the security employee of general directorate region
     * 
     * @throws BusinessException
     *             if any error occurs
     * @see RegionsEnum
     */
    private static EmployeeData getGeneralDirectorateSecurityEmployee() throws BusinessException {
	try {
	    WFPosition position = getWFPosition(WFPositionsEnum.SECURITY_UNIT_MOVEMENTS_PERSONNEL.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	    return EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
	} catch (BusinessException e) {
	    throw e;
	}
    }

    /**
     * constructs movements requests as successors of previous movement decision wrapper for {@link #constructWFMovement(long, long, Long, Integer, Date, Date, Long, String, Integer, Integer, Integer, String, String, Date, Integer, Long, String, String, long, int)}
     * 
     * @param decisionNumber
     *            previous decision number
     * @param decisionDate
     *            previous decision date
     * @param categoriesIds
     *            categories of employees in the ancestor decision
     * @param movementTypeId
     *            type of movement
     * @param transactionTypeCode
     *            successor transaction type
     * @param regionId
     * 
     * @return constructed movement requests
     * @throws BusinessException
     *             if any error occurs
     * @see TransactionTypesEnum , MovementTypesEnum
     * 
     */
    public static List<WFMovementData> constructWFMovementByDecisionInfo(long processId, String decisionNumber, Date decisionDate, Long[] categoriesIds, long movementTypeId, int transactionTypeCode, long regionId, String... extraParams) throws BusinessException {
	List<MovementTransactionData> movementTransactions = MovementsService.getValidDecisionMembers(decisionNumber, decisionDate, categoriesIds, movementTypeId, regionId, FlagsEnum.ON.getCode());
	List<WFMovementData> movementRequests = new ArrayList<WFMovementData>();
	if (movementTransactions != null && movementTransactions.size() > 0) {
	    for (MovementTransactionData movementTransaction : movementTransactions) {
		movementRequests.add(constructWFMovement(processId, movementTransaction.getEmployeeId(), null, movementTransaction.getExecutionDateFlag(), movementTransaction.getExecutionDate(), transactionTypeCode == TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode() ? HijriDateService.getHijriSysDate() : movementTransaction.getEndDate(), movementTransaction.getUnitId(), movementTransaction.getUnitFullName(), movementTransaction.getPeriodDays(), movementTransaction.getPeriodMonths(),
			movementTransaction.getLocationFlag(), movementTransaction.getLocation(), movementTransaction.getDecisionNumber(),
			movementTransaction.getDecisionDate(), movementTransaction.getReasonType(), movementTransaction.getJobId(), movementTransaction.getJobCode(), movementTransaction.getJobName(), movementTransaction.getMovementTypeId(), transactionTypeCode, extraParams));
	    }
	}
	return movementRequests;
    }

    /**
     * construct movement request from it's details
     * 
     * @param employeeId
     *            beneficiary employee
     * @param replacementEmployeeId
     *            replacement beneficiary employee
     * @param executionDateFlag
     *            indicator for the time of execution of the decision {future execution or not }
     * @param executionDate
     *            execution date of the decision
     * @param endDate
     *            end of movement period
     * @param unitId
     *            destination unit in the movement transaction
     * @param unitFullName
     *            fullname of destination unit in the movement transaction
     * @param periodDays
     *            Days part of the movement period
     * @param periodMonths
     *            months part of the movement period
     * @param locationFlag
     *            indicator for internal or external movement
     * @param location
     *            location in case of external movement
     * @param decisionNumber
     *            ancestor decision number
     * @param decisionDate
     *            ancestor decision date
     * @param reasonType
     *            reason of movement
     * @param jobId
     *            id of the destination job
     * @param jobCode
     *            code of the destination job
     * @param jobName
     *            name of the destination job
     * @param movementTypeId
     *            type of movement
     * @param transactionTypeCode
     *            type of the transaction
     * @return constructed movement request
     * @throws BusinessException
     *             if any error occurs
     * @see LocationFlagsEnum , MovementTypesEnum , TransactionTypesEnum , MovementsReasonTypesEnum
     */
    public static WFMovementData constructWFMovement(long processId, long employeeId, Long replacementEmployeeId, Integer executionDateFlag, Date executionDate, Date endDate, Long unitId, String unitFullName, Integer periodDays, Integer periodMonths, Integer locationFlag, String location, String decisionNumber, Date decisionDate, Integer reasonType, Long jobId, String jobCode, String jobName, long movementTypeId, int transactionTypeCode, String... extraParams) throws BusinessException {
	WFMovementData movementRequest = new WFMovementData();

	EmployeeData employee = EmployeesService.getEmployeeData(employeeId);
	if (employee == null)
	    throw new BusinessException("error_employeeDataError");

	movementRequest.setEmployeeId(employeeId);
	movementRequest.setCategoryId(employee.getCategoryId());
	movementRequest.setCategoryDesc(employee.getCategoryDesc());
	movementRequest.setEmployeeName(employee.getName());
	movementRequest.setEmployeeJobCode(employee.getJobCode());
	movementRequest.setEmployeeJobId(employee.getJobId());
	movementRequest.setEmployeeJobName(employee.getJobDesc());
	movementRequest.setEmployeeRankId(employee.getRankId());
	movementRequest.setEmployeeRankDesc(employee.getRankDesc());
	movementRequest.setEmployeeUnitId(employee.getOfficialUnitId());
	movementRequest.setEmployeeUnitName(employee.getOfficialUnitFullName());
	movementRequest.setEmployeeMilitaryNumber(employee.getMilitaryNumber());
	movementRequest.setEmployeeSocialID(employee.getSocialID());

	movementRequest.setReplacementEmployeeId(replacementEmployeeId);

	movementRequest.setMovementTypeId(movementTypeId);
	movementRequest.setMovementTypeDesc(MovementsService.getMovementTypeById(movementTypeId).getDescription());
	movementRequest.setTransactionTypeId(CommonService.getTransactionTypeByCodeAndClass(transactionTypeCode, TransactionClassesEnum.MOVEMENTS.getCode()).getId());

	movementRequest.setVacationGrantFlag(FlagsEnum.OFF.getCode());
	movementRequest.setManagerFlag(FlagsEnum.OFF.getCode());
	movementRequest.setVerbalOrderFlag(FlagsEnum.OFF.getCode());

	movementRequest.setJobId(jobId);
	movementRequest.setJobCode(jobCode);
	movementRequest.setJobName(jobName);

	movementRequest.setExtraParams((extraParams != null && extraParams.length > 0) ? extraParams[0] : null);
	if (reasonType != null)
	    movementRequest.setReasonType(reasonType);
	else
	    movementRequest.setReasonType(MovementsReasonTypesEnum.BASED_ON_HIS_REQUEST.getCode());

	movementRequest.setExecutionDateFlag(executionDateFlag);
	movementRequest.setSequentialMvtFlagBoolean(false);
	movementRequest.setTransferAllowanceFlagBoolean(false);

	if (transactionTypeCode == TransactionTypesEnum.MVT_NEW_DECISION.getCode()) {

	    // This executionDate should be an initial value as Hijri today date
	    movementRequest.setExecutionDate(executionDate);

	} else {
	    movementRequest.setBasedOnDecisionNumber(decisionNumber);
	    movementRequest.setBasedOnDecisionDate(decisionDate);
	    movementRequest.setUnitId(unitId);
	    movementRequest.setUnitFullName(unitFullName);
	    movementRequest.setLocation(location);

	    if (transactionTypeCode == TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode()) {

		// This endDate is the end date of the transaction which we extend it
		movementRequest.setExecutionDate(HijriDateService.addSubHijriDays(endDate, 1));

	    } else if (transactionTypeCode == TransactionTypesEnum.MVT_TERMINATION_DECISION.getCode()) {

		// Execution date of the transaction to be terminated
		movementRequest.setExecutionDate(executionDate);
		// This endDate should be an initial value as Hijri today date
		movementRequest.setEndDate(endDate);

	    } else if (transactionTypeCode == TransactionTypesEnum.MVT_CANCEL_DECISION.getCode()) {

		// All these data are the same of the transaction which we cancel it
		movementRequest.setExecutionDate(executionDate);
		movementRequest.setEndDate(endDate);
		movementRequest.setPeriodDays(periodDays);
		movementRequest.setPeriodMonths(periodMonths);
	    }
	}

	movementRequest.setLocationFlag(locationFlag);

	calculateWarnings(movementRequest, processId);

	return movementRequest;
    }

    public static WFMovementData constructWFMovementForJoiningDate(long employeeId, long movementTypeId, long transactionId) throws BusinessException {
	WFMovementData movementRequest = new WFMovementData();

	EmployeeData employee = EmployeesService.getEmployeeData(employeeId);
	if (employee == null)
	    throw new BusinessException("error_employeeDataError");

	movementRequest.setEmployeeId(employeeId);
	movementRequest.setCategoryId(employee.getCategoryId());
	movementRequest.setCategoryDesc(employee.getCategoryDesc());
	movementRequest.setEmployeeName(employee.getName());
	movementRequest.setEmployeeJobCode(employee.getJobCode());
	movementRequest.setEmployeeJobId(employee.getJobId());
	movementRequest.setEmployeeJobName(employee.getJobDesc());
	movementRequest.setEmployeeRankId(employee.getRankId());
	movementRequest.setEmployeeRankDesc(employee.getRankDesc());
	movementRequest.setEmployeeUnitId(employee.getOfficialUnitId());
	movementRequest.setEmployeeUnitName(employee.getOfficialUnitFullName());
	movementRequest.setEmployeeMilitaryNumber(employee.getMilitaryNumber());
	movementRequest.setEmployeeSocialID(employee.getSocialID());

	movementRequest.setMovementTypeId(movementTypeId);
	movementRequest.setMovementTypeDesc(MovementsService.getMovementTypeById(movementTypeId).getDescription());
	movementRequest.setTransactionTypeId(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId());

	movementRequest.setVacationGrantFlag(FlagsEnum.OFF.getCode());
	movementRequest.setManagerFlag(FlagsEnum.OFF.getCode());
	movementRequest.setVerbalOrderFlag(FlagsEnum.OFF.getCode());

	movementRequest.setSequentialMvtFlagBoolean(false);
	movementRequest.setTransferAllowanceFlagBoolean(false);

	movementRequest.setLocationFlag(LocationFlagsEnum.INTERNAL.getCode());
	movementRequest.setTransactionId(transactionId);

	return movementRequest;
    }

    /**
     * delete movement request
     * 
     * @param movementRequest
     *            request needed to be deleted
     * @param useSession
     *            optional database session
     * @throws BusinessException
     *             if any error occurs
     */
    public static void deleteWFMovement(WFMovementData movementRequest, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(movementRequest.getWfMovement(), session);
	    deleteWFInstanceBeneficiaries(movementRequest.getInstanceId(), Arrays.asList(movementRequest.getEmployeeId()), session);

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

    /**
     * search wrapper for {@link #searchWFMovementsByInstanceId(Long[])}
     * 
     * @param instanceId
     *            id of requested instance
     * @return requested instance if Exists
     * @throws BusinessException
     *             if any error occurs
     */

    public static List<WFMovementData> getWFMovementDataByInstanceId(Long instanceId) throws BusinessException {
	return searchWFMovementsByInstanceId(new Long[] { instanceId });
    }

    /**
     * search for instances by their Ids
     * 
     * @param instanceIds
     *            list of Ids of requested instances
     * 
     * @return requested instances if Exists
     * @throws BusinessException
     *             if any error occurs
     */
    public static Map<Long, List<WFMovementData>> getWFMovementsByInstanceIds(Long[] instanceIds) throws BusinessException {
	List<WFMovementData> wfMovementsData = searchWFMovementsByInstanceId(instanceIds);
	Map<Long, List<WFMovementData>> wfMovements = new HashMap<Long, List<WFMovementData>>();

	for (WFMovementData wfMovementData : wfMovementsData) {
	    List<WFMovementData> wfMovementDataList = wfMovements.get(wfMovementData.getInstanceId());
	    if (wfMovementDataList == null) {
		wfMovementDataList = new ArrayList<WFMovementData>();
		wfMovementDataList.add(wfMovementData);
		wfMovements.put(wfMovementData.getInstanceId(), wfMovementDataList);
	    } else {
		wfMovementDataList.add(wfMovementData);
	    }
	}
	return wfMovements;
    }

    /**
     * search for movements request by their instances Ids
     * 
     * @param instanceIds
     *            list of instances Ids of requested movements requests
     * @return movements requests of given instances if Exists
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<WFMovementData> searchWFMovementsByInstanceId(Long[] instanceIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceIds);
	    return DataAccess.executeNamedQuery(WFMovementData.class, QueryNamesEnum.WF_GET_WFMOVEMENT_BY_INSTANCE_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * search for movements workflow tasks by assignee employee
     * 
     * @param assigneeId
     *            id of the employee assigned to handle the task
     * @param assigneeWfRoles
     *            the role of the employee assigned to handle the task
     * @return requested workflow tasks
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<Object> getWFMovementsTasks(Long assigneeId, String[] assigneeWfRoles, Long[] processIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_ASSIGNEE_WF_ROLES", assigneeWfRoles);
	    qParams.put("P_PROCESS_GROUP_ID", WFProcessesGroupsEnum.MOVEMENTS.getCode());
	    if (processIds == null || processIds.length == 0) {
		qParams.put("P_PROCESS_IDS_FLAG", FlagsEnum.ALL.getCode() + "");
		qParams.put("P_PROCESS_IDS", FlagsEnum.ALL.getCode() + "");
	    } else {
		qParams.put("P_PROCESS_IDS_FLAG", FlagsEnum.ON.getCode() + "");
		qParams.put("P_PROCESS_IDS", processIds);
	    }
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.WF_GET_WFMOVMENT_TASKS.getCode(), qParams);
	} catch (Exception e) {
	    throw new BusinessException("error_general");
	}
    }

    /**
     * determine if the workflow process is request or not
     * 
     * @param processId
     *            id of the process
     * @param categoryId
     *            category of the process
     * @return true if the process is request , flase otherwise
     */
    private static boolean isRequestProcess(long processId, long categoryId) {
	if (categoryId == CategoriesEnum.OFFICERS.getCode()) {
	    if (processId == WFProcessesEnum.OFFICERS_MOVE_REQUEST.getCode() || processId == WFProcessesEnum.OFFICERS_SUBJOIN_REQUEST.getCode() || processId == WFProcessesEnum.OFFICERS_SUBJOIN_EXTENSION_REQUEST.getCode() || processId == WFProcessesEnum.OFFICERS_SUBJOIN_TERMINATION_REQUEST.getCode() || processId == WFProcessesEnum.OFFICERS_SUBJOIN_CANCELLATION_REQUEST.getCode())
		return true;
	    return false;
	} else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {
	    if (processId == WFProcessesEnum.SOLDIERS_MOVE_REQUEST.getCode() || processId == WFProcessesEnum.SOLDIERS_SUBJOIN_REQUEST.getCode() || processId == WFProcessesEnum.SOLDIERS_SUBJOIN_EXTENSION_REQUEST.getCode() || processId == WFProcessesEnum.SOLDIERS_SUBJOIN_TERMINATION_REQUEST.getCode() || processId == WFProcessesEnum.SOLDIERS_SUBJOIN_CANCELLATION_REQUEST.getCode())
		return true;
	    return false;
	} else {
	    if (processId == WFProcessesEnum.PERSONS_MOVE_REQUEST.getCode() || processId == WFProcessesEnum.PERSONS_ASSIGNMENT_REQUEST.getCode() || processId == WFProcessesEnum.PERSONS_ASSIGNMENT_EXTENSION_REQUEST.getCode() || processId == WFProcessesEnum.PERSONS_ASSIGNMENT_TERMINATION_REQUEST.getCode() || processId == WFProcessesEnum.PERSONS_ASSIGNMENT_CANCELLATION_REQUEST.getCode())
		return true;
	    return false;
	}
    }

    /**
     * calculate the warning for the movement request
     * 
     * @param movementRequest
     *            movement request for which warnings to be calculated
     * 
     * @throws BusinessException
     *             if any error occurs
     */
    public static void calculateWarnings(WFMovementData movementRequest, long processId) throws BusinessException {
	EmployeeData emp = EmployeesService.getEmployeeData(movementRequest.getEmployeeId());
	if (emp == null)
	    throw new BusinessException("error_employeeDataError");

	EmployeeData replacementEmp = null;
	if (movementRequest.getReplacementEmployeeId() != null) {
	    replacementEmp = EmployeesService.getEmployeeData(movementRequest.getReplacementEmployeeId());
	    if (replacementEmp == null)
		throw new BusinessException("error_employeeDataError");
	}

	movementRequest.setWarningMessages("");

	long extensionDecisionTransactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_EXTENSION_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();
	long newDecisionTransactionTypeId = CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.MVT_NEW_DECISION.getCode(), TransactionClassesEnum.MOVEMENTS.getCode()).getId();
	if (!isRequestProcess(processId, movementRequest.getCategoryId()) && (movementRequest.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) || movementRequest.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))
		&& (movementRequest.getTransactionTypeId() == newDecisionTransactionTypeId || movementRequest.getTransactionTypeId() == extensionDecisionTransactionTypeId)
		&& (!MovementsService.checkMonthsRule(movementRequest.getExecutionDate() != null ? movementRequest.getExecutionDate() : HijriDateService.getHijriSysDate(), emp.getServiceTerminationDueDate(), emp.getLastExtensionEndDate(), emp.getCategoryId()))) {
	    movementRequest.setWarningMessages(movementRequest.getWarningMessages() + "error_serviceTerminationDueDateIsInLessThan" + ",");

	}
	if (isRequestProcess(processId, movementRequest.getCategoryId()) || movementRequest.getTransactionTypeId() != extensionDecisionTransactionTypeId) {

	    // All
	    if (emp.getStatusId().longValue() == EmployeeStatusEnum.ON_DUTY.getCode() && (emp.getPhysicalUnitId() != null && emp.getOfficialUnitId() != null && emp.getPhysicalUnitId().longValue() != emp.getOfficialUnitId().longValue())) {
		movementRequest.setWarningMessages(movementRequest.getWarningMessages() + "error_internallyAssignedEmployeeWarning" + ",");
	    }

	    if (replacementEmp != null && replacementEmp.getStatusId().longValue() == EmployeeStatusEnum.ON_DUTY.getCode() && (replacementEmp.getPhysicalUnitId() != null && replacementEmp.getOfficialUnitId() != null && replacementEmp.getPhysicalUnitId().longValue() != replacementEmp.getOfficialUnitId().longValue())) {
		movementRequest.setWarningMessages(movementRequest.getWarningMessages() + "error_internallyAssignedReplacementEmployeeWarning" + ",");
	    }

	    // Officers
	    if (emp.getCategoryId().longValue() == CategoriesEnum.OFFICERS.getCode()) {
		if (emp.getStatusId().longValue() == EmployeeStatusEnum.ASSIGNED.getCode())
		    movementRequest.setWarningMessages(movementRequest.getWarningMessages() + "error_assignedOfficerWarning" + ",");

		if (replacementEmp != null && replacementEmp.getStatusId().longValue() == EmployeeStatusEnum.ASSIGNED.getCode())
		    movementRequest.setWarningMessages(movementRequest.getWarningMessages() + "error_assignedReplacementOfficerWarning" + ",");
	    }

	    // Soldiers : check also for replacement soldier
	    if (movementRequest.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
		if (!checkSoldiersMovementFiveYearsRule(movementRequest.getEmployeeId(), movementRequest.getExecutionDate() == null ? HijriDateService.getHijriSysDate() : movementRequest.getExecutionDate()))
		    movementRequest.setWarningMessages(movementRequest.getWarningMessages() + "error_soldierMinYearsPeriodWarning" + ",");

		if (movementRequest.getReplacementEmployeeId() != null && !checkSoldiersMovementFiveYearsRule(movementRequest.getReplacementEmployeeId(), movementRequest.getExecutionDate() == null ? HijriDateService.getHijriSysDate() : movementRequest.getExecutionDate()))
		    movementRequest.setWarningMessages(movementRequest.getWarningMessages() + "error_replacementSoldierMinYearsPeriodWarning" + ",");
	    }

	    if (emp.getCategoryId().longValue() == CategoriesEnum.OFFICERS.getCode() || movementRequest.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
		if (movementRequest.getReplacementEmployeeId() == null && movementRequest.getJobId() != null && emp.getMajorSpecId().longValue() != JobsService.getJobById(movementRequest.getJobId()).getMajorSpecializationId().longValue())
		    movementRequest.setWarningMessages(movementRequest.getWarningMessages() + "error_empSpecNotMatchingJobSpec" + ",");

		if (movementRequest.getReplacementEmployeeId() != null && emp.getMajorSpecId().longValue() != replacementEmp.getMajorSpecId().longValue())
		    movementRequest.setWarningMessages(movementRequest.getWarningMessages() + "error_empsSpecsNotMatching");
	    }
	}
    }

    /**
     * checks if duration between execution date of the new movement transaction and the last movement transaction or recruitment date of a soldier is less than five years
     * 
     * @param employeeId
     *            employee to be test the rule on him
     * @param executionDate
     *            execution date of new movement transaction
     * @return true if the duration is more than five years , false otherwise
     * 
     * @throws BusinessException
     *             if any error occurs
     */
    private static boolean checkSoldiersMovementFiveYearsRule(long employeeId, Date executionDate) throws BusinessException {
	MovementTransactionData movementTransaction = MovementsService.getLastValidMovementTransaction(employeeId, MovementTypesEnum.MOVE.getCode(), FlagsEnum.ALL.getCode());
	Date startDate = movementTransaction == null ? EmployeesService.getEmployeeData(employeeId).getRecruitmentDate() : movementTransaction.getExecutionDate();
	return HijriDateService.hijriDateDiff(startDate, executionDate) < SOLDIERS_MIN_PERIOD_TO_MOVE ? false : true;
    }

    private static List<Long> getMovementsInstanceBeneficiariesIds(List<WFMovementData> movementRequests) throws BusinessException {

	List<Long> instanceBeneficiariesIds = new ArrayList<Long>();
	for (WFMovementData movementRequest : movementRequests) {
	    instanceBeneficiariesIds.add(movementRequest.getEmployeeId());
	    if (movementRequest.getReplacementEmployeeId() != null)
		instanceBeneficiariesIds.add(movementRequest.getReplacementEmployeeId());
	}

	return instanceBeneficiariesIds;
    }

    /**
     * The main Validate method for movements requests
     * 
     * @param movementRequests
     *            new movements requests
     * @param requester
     *            employee started the workflow process
     * @param instanceId
     *            workflow instance
     * @param processId
     *            workflow process
     * @param wfTask
     *            workflow task
     * @param bypassJobValidation
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateMovementRequests(List<WFMovementData> movementRequests, EmployeeData requester, Long instanceId, long processId, WFTask wfTask, boolean bypassJobValidation, int transactionSourceView) throws BusinessException {
	HashSet<Long> empsIds = new HashSet<Long>();
	HashSet<Long> jobsIds = new HashSet<Long>();
	HashSet<Long> freezeJobsIds = new HashSet<Long>();
	HashSet<Long> unitIds = new HashSet<Long>(); // Units which employees will transfered/assigned to it as manager

	long categoryId = 0;
	UnitData ancestorUnderPresidency = null;

	if (movementRequests != null && movementRequests.size() > 0) {
	    categoryId = movementRequests.get(0).getCategoryId();
	    if (movementRequests.get(0).getMovementTypeId().longValue() == MovementTypesEnum.ASSIGNMENT.getCode() && movementRequests.get(0).getUnitId() != null)
		ancestorUnderPresidency = UnitsService.getAncestorUnderPresidencyByLevel(movementRequests.get(0).getUnitId(), UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode());
	}

	for (WFMovementData movementRequest : movementRequests) {
	    if (movementRequest.getCategoryId().longValue() != categoryId)
		throw new BusinessException("error_differentEmployeesCategoriesInSameDecision");
	    if (!empsIds.add(movementRequest.getEmployeeId()))
		throw new BusinessException("error_repeatedEmployee", new Object[] { movementRequest.getEmployeeName() });
	    if (movementRequest.getReplacementEmployeeId() != null && !empsIds.add(movementRequest.getReplacementEmployeeId())) {
		EmployeeData replacementEmployee = EmployeesService.getEmployeeData(movementRequest.getReplacementEmployeeId());
		if (replacementEmployee == null)
		    throw new BusinessException("error_employeeDataError");
		throw new BusinessException("error_repeatedEmployee", new Object[] { replacementEmployee.getName() });
	    }
	    // prevent adding more than one emp as manager to the same unit.
	    if (movementRequest.getManagerFlagBoolean() && !unitIds.add(movementRequest.getUnitId()))
		throw new BusinessException("error_multipleManagerIsInvalid", new String[] { movementRequest.getUnitFullName() });

	    // Bypass adding job for assignment job(El keyam be3ml wazefa)
	    if (movementRequest.getMovementTypeId().longValue() == MovementTypesEnum.MOVE.getCode()) {
		if (movementRequest.getJobId() != null)
		    if (!jobsIds.add(movementRequest.getJobId()))
			throw new BusinessException("error_repeatedJobParameterized", new Object[] { movementRequest.getJobName() });

		if (movementRequest.getFreezeJobId() != null)
		    if (!freezeJobsIds.add(movementRequest.getFreezeJobId()))
			throw new BusinessException("error_repeatedFreezeJobParameterized", new Object[] { movementRequest.getFreezeJobName() });
	    }

	    EmployeeData employee = EmployeesService.getEmployeeData(movementRequest.getEmployeeId());
	    if (employee == null)
		throw new BusinessException("error_employeeDataError");
	    if (employee.getStatusId() >= EmployeeStatusEnum.MOVED_EXTERNALLY.getCode())
		throw new BusinessException("error_employeeSpecifiedStatusIsNotSuitable", new Object[] { employee.getName() });
	    // Check that the units is under the same parent under the general manager for Officers Assignments
	    if (movementRequest.getMovementTypeId().longValue() == MovementTypesEnum.ASSIGNMENT.getCode() && movementRequest.getUnitId() != null && movementRequest.getEmployeeId() != null) {

		if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			&& (employee.getUnitTypeCode().equals(UnitTypesEnum.REGION_COMMANDER.getCode())
				|| UnitsService.getUnitById(movementRequest.getUnitId()).getUnitTypeCode().equals(UnitTypesEnum.REGION_COMMANDER.getCode()))) {

		    throw new BusinessException("error_cannotDoAssignmentOnThisEmployee");
		}

		UnitData ancestorUnit = UnitsService.getAncestorUnderPresidencyByLevel(movementRequest.getUnitId(), UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode());
		if (ancestorUnit.getId().longValue() != ancestorUnderPresidency.getId().longValue()) {

		    throw new BusinessException("error_assignmentUnitsMustbeUnderTheSameAncestor");
		}
	    }

	    if (movementRequest.getMovementTypeId().equals(MovementTypesEnum.SUBJOIN.getCode()) &&
		    movementRequest.getLocationFlag().equals(FlagsEnum.OFF.getCode()) && movementRequest.getUnitId() != null &&
		    (!isRequestProcess(processId, categoryId) || (wfTask != null && wfTask.getAssigneeWfRole().equals(WFTaskRolesEnum.REVIEWER_EMP.getCode())))) {

		UnitData unit = UnitsService.getUnitById(movementRequest.getUnitId());

		if (unit.getUnitTypeCode().equals(UnitTypesEnum.PRESIDENCY.getCode()) ||
			unit.getUnitTypeCode().equals(UnitTypesEnum.REGION_COMMANDER.getCode()) ||
			unit.getUnitTypeCode().equals(UnitTypesEnum.SECTOR_COMMANDER.getCode())) {

		    if (employee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) || employee.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))
			throw new BusinessException("error_cannotDoSubjoinOnThisUnitMustBeSubUnit");
		    else
			throw new BusinessException("error_cannotDoEmployeesSubjoinOnThisUnit");
		}
	    }

	    if (movementRequest.getMovementTypeId().longValue() == MovementTypesEnum.MOVE.getCode() ||
		    movementRequest.getMovementTypeId().longValue() == MovementTypesEnum.SUBJOIN.getCode()) {

		if (!requester.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())
			&& (!isRequestProcess(processId, categoryId) || (wfTask != null && wfTask.getAssigneeWfRole().equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()) && !EmployeesService.getEmployeeData(wfTask.getOriginalId()).getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())))
			&& movementRequest.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {

		    Long unitId = null;
		    if (movementRequest.getMovementTypeId().longValue() == MovementTypesEnum.MOVE.getCode()) {
			if (movementRequest.getJobId() != null) {
			    unitId = JobsService.getJobById(movementRequest.getJobId()).getUnitId();
			}
		    } else
			unitId = movementRequest.getUnitId();

		    if (unitId != null && MovementsService.checkIfMovementOperationOnCertainPositions(unitId, movementRequest.getManagerFlag())) {

			if (movementRequest.getMovementTypeId().longValue() == MovementTypesEnum.MOVE.getCode())
			    throw new BusinessException("error_cannotDoMoveOnThisJob");
			else {
			    if (movementRequest.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) || movementRequest.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))
				throw new BusinessException("error_cannotDoSubjoinOnThisUnit");
			    else // TODO Dead code
				throw new BusinessException("error_cannotDoEmployeesSubjoinOnThisUnit");
			}
		    }
		}
	    }

	    if (wfTask != null && wfTask.getAssigneeWfRole().equals(WFTaskRolesEnum.REVIEWER_EMP.getCode()) && categoryId == CategoriesEnum.SOLDIERS.getCode() &&
		    (movementRequest.getMovementTypeId().equals(MovementTypesEnum.MOVE.getCode()) || movementRequest.getMovementTypeId().equals(MovementTypesEnum.SUBJOIN.getCode()))
		    && movementRequest.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode())) {

		if (movementRequest.getMinistryApprovalDate() == null)
		    throw new BusinessException("error_ministryApprovalDateMandatory");
		if (!HijriDateService.isValidHijriDate(movementRequest.getMinistryApprovalDate()))
		    throw new BusinessException("error_invalidHijriDate");
		if (movementRequest.getMinistryApprovalNumber() == null)
		    throw new BusinessException("error_ministryApprovalNumberMandatory");
	    }

	    validateBusinessRules(movementRequest, instanceId, processId, bypassJobValidation, transactionSourceView);
	}

	if (freezeJobsIds.size() != 0) {
	    List<JobData> jobsToUnfreeze = JobsService.getJobsByJobsIds(jobsIds.toArray(new Long[jobsIds.size()]));
	    List<JobData> jobsToFreeze = JobsService.getJobsByJobsIds(freezeJobsIds.toArray(new Long[freezeJobsIds.size()]));
	    JobsService.validateVacantOrOccupiedJobsToFreezeJobsToUnfreeze(jobsToFreeze, jobsToUnfreeze);
	}
    }

    /**
     * list either the running movement instance that contains certain employees or certain jobs
     * 
     * @param employeesIds
     *            list of employees to count requests for them
     * @param jobsIds
     *            list of jobs to count requests for them
     * @param excludedInstanceId
     *            workflow instance to be excluded from the counting
     * @return count
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<WFMovement> getRunningRequests(Long[] employeesIds, Long[] jobsIds, Long excludedInstanceId) throws BusinessException {
	try {
	    if ((employeesIds == null || employeesIds.length == 0) && (jobsIds == null || jobsIds.length == 0))
		return new ArrayList<WFMovement>();

	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EMPS_IDS", (employeesIds != null && employeesIds.length > 0) ? employeesIds : new Long[] { (long) FlagsEnum.ALL.getCode() });
	    qParams.put("P_JOBS_IDS", (jobsIds != null && jobsIds.length > 0) ? jobsIds : new Long[] { (long) FlagsEnum.ALL.getCode() });
	    qParams.put("P_EXCLUDED_INSTANCE_ID", excludedInstanceId == null ? (long) FlagsEnum.ALL.getCode() : excludedInstanceId);

	    return DataAccess.executeNamedQuery(WFMovement.class, QueryNamesEnum.WF_CHECK_EXISTING_MOVEMENT_PROCESSES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static WFTask getSecurityEmpTaskByInstanceId(Long instanceId) throws BusinessException {
	List<WFTask> result = getWFInstanceTasksByRole(instanceId, WFTaskRolesEnum.SECURITY_EMP.getCode());
	return result == null || result.size() == 0 ? null : result.get(0);
    }

    /**
     * validate the business rule of given movemnet request
     * 
     * @param movementRequest
     *            movement request to be validated
     * @param instanceId
     *            workflow instance
     * @param processId
     *            workflow process
     * @param bypassJobValidation
     * @throws BusinessException
     *             if any error occurs
     */
    private static void validateBusinessRules(WFMovementData movementRequest, Long instanceId, Long processId, boolean bypassJobValidation, int transactionSourceView) throws BusinessException {
	List<WFMovementData> list = new ArrayList<WFMovementData>();
	list.add(movementRequest);
	List<MovementTransactionData> movementsTransactionsData = constructMovementTransactions(list, processId, bypassJobValidation, null, null);

	MovementsService.validateBusinessRules(movementsTransactionsData.get(0), movementRequest.getReplacementEmployeeId(), instanceId, transactionSourceView);

	// For the case of replacement movements which one requests transfers to two transactions
	if (movementsTransactionsData.size() > 1)
	    MovementsService.validateBusinessRules(movementsTransactionsData.get(1), movementRequest.getEmployeeId(), instanceId, transactionSourceView);
    }

    /**
     * addd new movements requests to the database
     * 
     * @param movementRequests
     *            movements requests to be added
     * @param instanceId
     *            workflow instance of requests
     * @param processId
     *            workflow process
     * @param internalCopies
     *            Internal employees which receive notification upon termination of workflow instance
     * @param externalCopies
     *            External employees which receive hard copy of decision
     * @param useSession
     *            optional database session
     * @throws BusinessException
     *             if any error occurs
     */
    private static void addWFMovements(List<WFMovementData> movementRequests, long instanceId, String internalCopies, String externalCopies, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (WFMovementData movementRequest : movementRequests) {
		movementRequest.setInstanceId(instanceId);
		movementRequest.setInternalCopies(internalCopies);
		movementRequest.setExternalCopies(externalCopies);
		DataAccess.addEntity(movementRequest.getWfMovement(), session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    for (WFMovementData movementRequest : movementRequests)
		movementRequest.setInstanceId(null);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * updates exist movements requests
     * 
     * @param movementRequests
     *            movements requests to be updated
     * @param processId
     *            workflow process
     * @param internalCopies
     *            Internal employees which receive notification upon termination of workflow instance
     * @param externalCopies
     *            External employees which receive hard copy of decision
     * @param useSession
     *            optional session id
     * @throws BusinessException
     *             if any error occurs
     */
    private static void updateWFMovements(List<WFMovementData> movementRequests, String internalCopies, String externalCopies, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (WFMovementData movementRequest : movementRequests) {
		movementRequest.setInternalCopies(internalCopies);
		movementRequest.setExternalCopies(externalCopies);
		DataAccess.updateEntity(movementRequest.getWfMovement(), session);
	    }

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

    /**
     * <p>
     * closes the movements workflow instance
     * </p>
     * 
     * @param requester
     *            employee started the workflow instance
     * @param instance
     *            workflow instance
     * @param movementRequests
     *            movements requests
     * @param task
     *            task received by the employee that will close the workflow
     * @param ministryApprovalNumber
     *            approval number of ministry of interior
     * @param ministryApprovalDate
     *            approval date of ministry of interior
     * @param decisionRegionId
     * @param session
     *            optional session
     * @throws BusinessException
     *             if any error occurs
     */
    private static void closeMovementWorkFlow(EmployeeData requester, WFInstance instance, List<WFMovementData> movementRequests, WFTask task, Long decisionRegionId, CustomSession session) throws BusinessException {
	try {
	    List<Long> beneficairyEmployeesIds = new ArrayList<Long>();
	    List<Long> dynamicCopiesList = new ArrayList<Long>();
	    List<Long> internalCopiesList = new ArrayList<Long>();

	    Date sysDate = HijriDateService.getHijriSysDate();

	    for (WFMovementData movementRequest : movementRequests) {
		// Sign Manager employee
		movementRequest.setDecisionApprovedId(task.getOriginalId());
		movementRequest.setOriginalDecisionApprovedId(task.getOriginalId());

		movementRequest.setDecisionRegionId(decisionRegionId);

		if (movementRequest.getExecutionDateFlag().intValue() == FlagsEnum.ON.getCode())
		    movementRequest.setExecutionDate(sysDate);

		DataAccess.updateEntity(movementRequest.getWfMovement(), session);

		beneficairyEmployeesIds.add(movementRequest.getEmployeeId());

		if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) && movementRequests.get(0).getDecisionRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode())) {

		    Long empUnitPhysicalManager = UnitsService.getAncestorUnderPresidencyByLevel(movementRequest.getEmployeeUnitId(), UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode()).getPhysicalManagerId();
		    if (empUnitPhysicalManager != null)
			dynamicCopiesList.add(empUnitPhysicalManager);

		    Long mvtUnitPhysicalManager;
		    if (movementRequest.getJobId() != null)
			mvtUnitPhysicalManager = UnitsService.getAncestorUnderPresidencyByLevel(JobsService.getJobById(movementRequest.getJobId()).getUnitId(), UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode()).getPhysicalManagerId();
		    else
			mvtUnitPhysicalManager = UnitsService.getAncestorUnderPresidencyByLevel(movementRequest.getUnitId(), UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode()).getPhysicalManagerId();

		    if (mvtUnitPhysicalManager != null)
			dynamicCopiesList.add(mvtUnitPhysicalManager);
		}
	    }

	    doMovementIntegration(movementRequests, instance, task, getWFProcess(instance.getProcessId()).getProcessName(), requester, isRequestProcess(instance.getProcessId(), movementRequests.get(0).getCategoryId()) ? MovementTransactionViewsEnum.REQUEST.getCode() : MovementTransactionViewsEnum.DECISION.getCode(), session);

	    // Get all movements needed copies (Internal copies + Dynamic copies + Computed copies)
	    // Get internal copies
	    if (movementRequests.get(0).getInternalCopies() != null) {
		String[] internalCopies = movementRequests.get(0).getInternalCopies().split(",");
		for (int i = 0; i < internalCopies.length; i++) {
		    internalCopiesList.add(Long.valueOf(internalCopies[i]));
		}
	    }

	    // Get Dynamic copies
	    if (movementRequests.get(0).getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()) && movementRequests.get(0).getFreezeJobId() != null) {
		Long archivesInformationDepartmentManager = UnitsService.getUnitPhysicalManagerId(getWFPosition(WFPositionsEnum.SOLDIERS_ARCHIVES_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()).getUnitId());
		if (archivesInformationDepartmentManager != null)
		    dynamicCopiesList.add(archivesInformationDepartmentManager);
		Long informationSectionManager = UnitsService.getUnitPhysicalManagerId(getWFPosition(WFPositionsEnum.SOLDIERS_INFORMATION_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()).getUnitId());
		if (informationSectionManager != null)
		    dynamicCopiesList.add(informationSectionManager);
	    }

	    if (instance.getProcessId() == WFProcessesEnum.OFFICERS_SUBJOIN_EXTENSION_REQUEST.getCode() || instance.getProcessId() == WFProcessesEnum.OFFICERS_SUBJOIN_TERMINATION_REQUEST.getCode() || instance.getProcessId() == WFProcessesEnum.OFFICERS_SUBJOIN_CANCELLATION_REQUEST.getCode()
		    || instance.getProcessId() == WFProcessesEnum.SOLDIERS_SUBJOIN_EXTENSION_REQUEST.getCode() || instance.getProcessId() == WFProcessesEnum.SOLDIERS_SUBJOIN_TERMINATION_REQUEST.getCode() || instance.getProcessId() == WFProcessesEnum.SOLDIERS_SUBJOIN_CANCELLATION_REQUEST.getCode()
		    || instance.getProcessId() == WFProcessesEnum.PERSONS_ASSIGNMENT_EXTENSION_REQUEST.getCode() || instance.getProcessId() == WFProcessesEnum.PERSONS_ASSIGNMENT_TERMINATION_REQUEST.getCode() || instance.getProcessId() == WFProcessesEnum.PERSONS_ASSIGNMENT_CANCELLATION_REQUEST.getCode()) {
		List<EmployeeData> employeesData = EmployeesService.getEmployeesByEmpsIds(beneficairyEmployeesIds.toArray(new Long[beneficairyEmployeesIds.size()]));
		for (EmployeeData empData : employeesData) {
		    List<UnitData> unitsData = UnitsService.getAncestorsUnitsByHKey(empData.getOfficialUnitHKey(), UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode());
		    for (UnitData unitData : unitsData) {
			if (unitData.getPhysicalManagerId() != null)
			    dynamicCopiesList.add(unitData.getPhysicalManagerId());
		    }
		}
	    }

	    List<Long> additionalIds = new ArrayList<Long>();
	    additionalIds.addAll(internalCopiesList);
	    additionalIds.addAll(dynamicCopiesList);

	    Long[] allCopies = computeInstanceNotifications(new ArrayList<Long>(Arrays.asList(movementRequests.get(0).getCategoryId())), movementRequests.get(0).getDecisionRegionId(), instance.getProcessId(), beneficairyEmployeesIds, additionalIds);
	    // Get all movements needed copies (Internal copies + Dynamic copies + Computed copies)

	    closeWFInstanceByAction(requester.getEmpId(), instance, task, WFTaskActionsEnum.SUPER_SIGN.getCode(), allCopies, session);
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * integrate movements workflow process with movements transaction service
     * 
     * @param movementRequests
     *            movement requests to be handled
     * @param instance
     *            workflow instance
     * @param smTask
     *            sign manager task where the integration will be done
     * @param processName
     *            workflow process name
     * @param requester
     *            employee started the workflow instance
     * @param session
     *            optional database session
     * @throws Exception
     *             if any error occurs
     */
    private static void doMovementIntegration(List<WFMovementData> movementRequests, WFInstance instance, WFTask smTask, String processName, EmployeeData requester, int transactionSourceView, CustomSession session) throws Exception {
	List<MovementTransactionData> movementTransactions = constructMovementTransactions(movementRequests, instance.getProcessId(), false, requester, instance.getAttachments());
	MovementsService.handleMovementsTransactions(processName, movementTransactions, movementRequests.get(0).getReplacementEmployeeId() != null, instance.getInstanceId(), transactionSourceView, session);
    }

    /**
     * constructs movements transactions from movements requests , wrapper for {@link #constructMovementTransaction(WFMovementData, long, Long, Long, boolean, EmployeeData, String)}
     * 
     * @param movementRequests
     *            movements requests to be converted
     * @param processId
     *            workflow process
     * @param bypassJobValidation
     * @param requester
     *            employee started the workflow process
     * @param attachments
     *            attachments to workflow instance
     * @return list of construct movements transactions
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<MovementTransactionData> constructMovementTransactions(List<WFMovementData> movementRequests, Long processId, boolean bypassJobValidation, EmployeeData requester, String attachments) throws BusinessException {
	List<MovementTransactionData> movementTransactions = new ArrayList<MovementTransactionData>();
	for (WFMovementData movementRequest : movementRequests) {
	    MovementTransactionData firstTransaction = constructMovementTransaction(movementRequest, movementRequest.getEmployeeId(), movementRequest.getReplacementEmployeeId(), processId, bypassJobValidation, requester, attachments);
	    movementTransactions.add(firstTransaction);
	    if (movementRequest.getReplacementEmployeeId() != null) {
		MovementTransactionData secondTransaction = constructMovementTransaction(movementRequest, movementRequest.getReplacementEmployeeId(), movementRequest.getEmployeeId(), processId, bypassJobValidation, requester, attachments);
		movementTransactions.add(secondTransaction);
	    }
	}
	return movementTransactions;
    }

    /**
     * construct movement transaction from movement request
     * 
     * @param movementRequest
     *            movement request to be converted
     * @param employeeId
     *            beneficiary employee
     * @param replacementEmployeeId
     *            replacement beneficiary employee
     * @param processId
     *            workflow process
     * @param bypassJobValidation
     * @param requester
     *            employee started the workflow instance
     * @param attachments
     *            attachements to the requests
     * @return constructed movement transactions
     * @throws BusinessException
     *             if any error occurs
     */
    private static MovementTransactionData constructMovementTransaction(WFMovementData movementRequest, long employeeId, Long replacementEmployeeId, Long processId, boolean bypassJobValidation, EmployeeData requester, String attachments) throws BusinessException {
	try {
	    MovementTransactionData movementTransaction = new MovementTransactionData();
	    movementTransaction.setMovementTypeId(movementRequest.getMovementTypeId());
	    movementTransaction.setTransactionTypeId(movementRequest.getTransactionTypeId());
	    movementTransaction.setBasedOnOrderNumber(movementRequest.getBasedOnOrderNumber());
	    movementTransaction.setBasedOnOrderDate(movementRequest.getBasedOnOrderDate());
	    movementTransaction.setBasedOn(movementRequest.getBasedOn());
	    movementTransaction.setCategoryId(movementRequest.getCategoryId());
	    movementTransaction.setEmployeeId(employeeId);
	    movementTransaction.setSpecialRemarks(movementRequest.getSpecialRemarks());
	    movementTransaction.setSpecialAttachments(movementRequest.getSpecialAttachments());
	    movementTransaction.setExtraParams(movementRequest.getExtraParams());
	    EmployeeData replacementEmp = replacementEmployeeId == null ? null : EmployeesService.getEmployeeData(replacementEmployeeId);

	    JobData job = null;
	    UnitData unit = null;
	    if (replacementEmp != null)
		job = JobsService.getJobById(replacementEmp.getJobId());
	    else if (bypassJobValidation)
		movementTransaction.setJobId(-1L);
	    else if (movementRequest.getJobId() != null)
		job = JobsService.getJobById(movementRequest.getJobId());

	    if (job != null) {
		movementTransaction.setJobId(job.getId());
		movementTransaction.setJobCode(job.getCode());
		movementTransaction.setJobName(job.getName());
		movementTransaction.setJobRankId(job.getRankId());
		movementTransaction.setJobMinorSpecId(job.getMinorSpecializationId());
		movementTransaction.setJobMinorSpecDesc(job.getMinorSpecializationDescription());
		movementTransaction.setJobClassificationId(job.getClassificationId());

		unit = UnitsService.getUnitById(job.getUnitId());
	    } else if (movementRequest.getUnitId() != null) {
		unit = UnitsService.getUnitById(movementRequest.getUnitId());
	    }

	    if (unit != null) {
		movementTransaction.setUnitId(unit.getId());
		movementTransaction.setUnitFullName(unit.getFullName());
	    }
	    movementTransaction.setLocationFlag(movementRequest.getLocationFlag());
	    movementTransaction.setLocation(movementRequest.getLocation());

	    EmployeeData emp = EmployeesService.getEmployeeData(employeeId);

	    if (movementRequest.getMovementTypeId().longValue() == MovementTypesEnum.ASSIGNMENT.getCode() && unit != null && requester != null) {
		UnitData directedToUnit = UnitsService.getAncestorUnderPresidencyByLevel(unit.getId(), requester.getPhysicalRegionId() == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() ? UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode() : UnitsAncestorsLevelsEnum.LEVEL_THREE.getCode());
		String directedToJobName = null;

		if (directedToUnit == null || directedToUnit.getOfficialManagerId() == null)
		    directedToJobName = " ";
		else
		    directedToJobName = EmployeesService.getEmployeeData(directedToUnit.getOfficialManagerId()).getJobDesc();
		movementTransaction.setDirectedToJobName(directedToJobName);
	    }

	    movementTransaction.setSecurityUnitFlag(FlagsEnum.OFF.getCode());

	    if (movementRequest.getFreezeJobId() != null) {
		job = JobsService.getJobById(movementRequest.getFreezeJobId());
		movementTransaction.setFreezeJobId(movementRequest.getFreezeJobId());
		movementTransaction.setFreezeJobCode(job.getCode());
		movementTransaction.setFreezeJobName(job.getName());
		movementTransaction.setFreezeJobRankId(job.getRankId());
		movementTransaction.setFreezeJobUnitFullName(job.getUnitFullName());
	    }
	    movementTransaction.setManagerFlag(movementRequest.getManagerFlag());
	    movementTransaction.setExecutionDateFlag(movementRequest.getExecutionDateFlag());
	    movementTransaction.setEndDate(movementRequest.getEndDate());
	    movementTransaction.setPeriodDays(movementRequest.getPeriodDays());
	    movementTransaction.setPeriodMonths(movementRequest.getPeriodMonths());
	    movementTransaction.setReasons(movementRequest.getReasons());
	    movementTransaction.setReasonType(movementRequest.getReasonType());
	    movementTransaction.setVacationGrantFlag(movementRequest.getVacationGrantFlag());
	    movementTransaction.setMinistryApprovalNumber(movementRequest.getMinistryApprovalNumber());
	    movementTransaction.setMinistryApprovalDate(movementRequest.getMinistryApprovalDate());
	    movementTransaction.setRemarks(movementRequest.getRemarks());
	    movementTransaction.setInternalCopies(movementRequest.getInternalCopies());
	    movementTransaction.setExternalCopies(movementRequest.getExternalCopies());
	    movementTransaction.setAttachments(attachments);
	    movementTransaction.setEtrFlag(FlagsEnum.ON.getCode());
	    movementTransaction.setMigrationFlag(FlagsEnum.OFF.getCode());
	    movementTransaction.setApprovedId(movementRequest.getApprovedId());
	    movementTransaction.setOriginalDecisionApprovedId(movementRequest.getOriginalDecisionApprovedId());
	    movementTransaction.setDecisionApprovedId(movementRequest.getDecisionApprovedId());
	    movementTransaction.setDecisionRegionId(movementRequest.getDecisionRegionId());
	    movementTransaction.setTransEmpRankId(emp.getRankId());
	    movementTransaction.setTransEmpRankDesc(emp.getRankDesc() + (emp.getRankTitleDesc() != null ? " " + emp.getRankTitleDesc() : ""));
	    movementTransaction.setTransEmpJobMinorSpecDesc(emp.getMinorSpecDesc());
	    movementTransaction.setTransEmpJobCode(emp.getJobCode());
	    movementTransaction.setTransEmpJobClassJobCode(emp.getJobClassificationCode());
	    movementTransaction.setTransEmpJobClassJobDesc(emp.getJobClassificationDesc());
	    movementTransaction.setTransEmpJobName(emp.getJobDesc());

	    if (emp.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		JobData empJob = JobsService.getJobById(emp.getJobId());
		movementTransaction.setTransEmpJobRankDesc(empJob.getRankDescription());
	    } else {
		movementTransaction.setTransEmpJobRankDesc(emp.getRankDesc());
	    }

	    movementTransaction.setTransEmpUnitFullName(emp.getOfficialUnitFullName());
	    if (movementRequest.getApprovedId() != null) {
		EmployeeData approvedEmp = EmployeesService.getEmployeeData(movementRequest.getApprovedId());
		movementTransaction.setTransApprovedRankDesc(approvedEmp.getRankDesc());
		movementTransaction.setTransApprovedJobName(approvedEmp.getJobDesc());
	    }

	    movementTransaction.setVerbalOrderFlag(movementRequest.getVerbalOrderFlag());
	    if (isRequestProcess(processId, movementRequest.getCategoryId()))
		movementTransaction.setRequestTransactionFlag(FlagsEnum.ON.getCode());
	    else
		movementTransaction.setRequestTransactionFlag(FlagsEnum.OFF.getCode());

	    movementTransaction.setBasedOnDecisionNumber(movementRequest.getBasedOnDecisionNumber());
	    movementTransaction.setBasedOnDecisionDate(movementRequest.getBasedOnDecisionDate());

	    movementTransaction.setExecutionDateString(movementRequest.getExecutionDateString());
	    movementTransaction.setSequentialMvtFlagBoolean(movementRequest.getSequentialMvtFlagBoolean());
	    movementTransaction.setTransferAllowanceFlagBoolean(movementRequest.getTransferAllowanceFlagBoolean());

	    return movementTransaction;
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets movements workflow position by category and region
     * 
     * @param categoryId
     *            category of the position
     * @param regionId
     *            region of the position
     * @return requested workflow position
     * @throws BusinessException
     *             if any error occurs
     * @see WFPositionsEnum
     */
    private static WFPosition getWFPositionByCategoryIdAndRegionId(long categoryId, long regionId, long movementTypeId) throws BusinessException {
	int positionCode = -1;

	if (regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
	    if (CategoriesEnum.OFFICERS.getCode() == categoryId)
		positionCode = WFPositionsEnum.OFFICERS_MOVEMENTS_UNIT_MANAGER.getCode();

	    else if (CategoriesEnum.SOLDIERS.getCode() == categoryId)
		if (movementTypeId == MovementTypesEnum.SUBJOIN.getCode() || movementTypeId == MovementTypesEnum.MANDATE.getCode() || movementTypeId == MovementTypesEnum.SECONDMENT.getCode())
		    positionCode = WFPositionsEnum.SOLDIERS_SUBJOIN_MOVEMENTS_UNIT_MANAGER.getCode();
		else
		    positionCode = WFPositionsEnum.SOLDIERS_MOVEMENTS_UNIT_MANAGER.getCode();

	    else if (CategoriesEnum.CONTRACTORS.getCode() == categoryId)
		positionCode = WFPositionsEnum.CONTRACTORS_UNIT_MANAGER.getCode();

	    else if (CategoriesEnum.USERS.getCode() == categoryId || CategoriesEnum.WAGES.getCode() == categoryId)
		positionCode = WFPositionsEnum.USERS_WAGES_UNIT_MANAGER.getCode();
	    else
		positionCode = WFPositionsEnum.PERSONS_UNIT_MANAGER.getCode();

	} else {
	    if (CategoriesEnum.OFFICERS.getCode() == categoryId)
		positionCode = WFPositionsEnum.REGION_OFFICERS_AFFAIRS_UNIT_MANAGER.getCode();

	    else if (CategoriesEnum.SOLDIERS.getCode() == categoryId)
		positionCode = WFPositionsEnum.REGION_SOLDIERS_AFFAIRS_UNIT_MANAGER.getCode();

	    else
		positionCode = WFPositionsEnum.REGION_CIVILIANS_AFFAIRS_UNIT_MANAGER.getCode();
	}

	return getWFPosition(positionCode, regionId);
    }

    /**
     * Gets the employee holds movement manager
     * 
     * @param categoryId
     *            category of position
     * @param regionId
     *            region of the position
     * @return manager employee
     * 
     * @throws BusinessException
     *             if any error occurs
     */

    // Currently we are forwarding to the specialized unit according to the physical region of the employee
    private static EmployeeData getMovementManager(long categoryId, long regionId, long movementTypeId) throws BusinessException {
	WFPosition position = getWFPositionByCategoryIdAndRegionId(categoryId, regionId, movementTypeId);
	if (position == null) {
	    throw new BusinessException("error_positionNotFound");
	}

	EmployeeData emp = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
	if (emp == null) {
	    throw new BusinessException("error_employeeDataError");
	}

	return emp;
    }

    /**
     * Checks if employee holds the movement manager position
     * 
     * @param categoryId
     *            category of the position
     * @param employee
     * @return true if employee holds the position
     * @throws BusinessException
     *             if any error occurs
     */
    private static boolean checkIfEmployeeExistsInWFPositionUnit(long categoryId, EmployeeData employee, long movementTypeId) throws BusinessException {
	WFPosition position = getWFPositionByCategoryIdAndRegionId(categoryId, employee.getPhysicalRegionId(), movementTypeId);
	if (position == null) {
	    throw new BusinessException("error_positionNotFound");
	}

	if (position.getUnitId() != null && position.getUnitId().equals(employee.getPhysicalUnitId())) {
	    return true;
	}

	return false;
    }

    /**
     * 
     * @return line separated string of external employees that will receive hard copy upon issuing of soldiers movements by freeze decision
     */
    public static String getMoveByFreezeDescisionExternalCopies() {
	String externalCopies = "";

	externalCopies = getMessage("label_copyTo") + "\n" + getMessage("label_soldiersArchivesInformationUnitManager") + "\n" + getMessage("label_payrollUnitManager");

	return externalCopies;
    }

    /**
     * return the first ancestor with type (Sector commander, Region commander or Presidency) of a given unit id
     * 
     * @param unitId
     *            the unit which we get its ancestor
     * @return id of the acestor unit
     * @throws BusinessException
     *             if any error occurs
     */
    public static Long getUnitAncestorId(long unitId) throws BusinessException {
	UnitData ancestorUnit;
	ancestorUnit = UnitsService.getAncestorUnderPresidencyByLevel(unitId, UnitsAncestorsLevelsEnum.LEVEL_THREE.getCode());

	if (ancestorUnit.getUnitTypeCode().equals(UnitTypesEnum.SECTOR_COMMANDER.getCode()))
	    return ancestorUnit.getId();
	else {
	    ancestorUnit = UnitsService.getAncestorUnderPresidencyByLevel(unitId, UnitsAncestorsLevelsEnum.LEVEL_TWO.getCode());
	    if (ancestorUnit.getUnitTypeCode().equals(UnitTypesEnum.REGION_COMMANDER.getCode())) {
		return ancestorUnit.getId();
	    }
	    return UnitsService.getAncestorUnderPresidencyByLevel(unitId, UnitsAncestorsLevelsEnum.LEVEL_ONE.getCode()).getId();
	}
    }

}