package com.code.services.buswfcoop;

import com.code.dal.CustomSession;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.services.BaseService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.services.workflow.hcm.PromotionsWorkFlow;
import com.code.services.workflow.hcm.TerminationsWorkflow;

public class BusinessWorkflowCooperation extends BaseService {

    private BusinessWorkflowCooperation() {
    }

    /**
     * Invalidate Employees' tasks (reject tasks) and Delegations (delete delegations from and to the employee).
     * 
     * @param empsIds
     *            the identifiers of the employees.
     * @param transactionClass
     *            the transaction class to determine the refuse reasons; only support the Terminations, the Movements and the Promotions Transaction classes.
     * @param session
     * @throws BusinessException
     */
    public static void invalidateEmployeesInboxAndDelegations(Long[] empsIds, int transactionClass, CustomSession session) throws BusinessException {
	if (empsIds == null || empsIds.length == 0)
	    return;

	String refuseReasons = "";
	if (transactionClass == TransactionClassesEnum.TERMINATIONS.getCode())
	    refuseReasons = BaseService.getMessage("notify_refuseReasonsForTerminations");
	else if (transactionClass == TransactionClassesEnum.MOVEMENTS.getCode())
	    refuseReasons = BaseService.getMessage("notify_refuseReasonsForMovements");
	else if (transactionClass == TransactionClassesEnum.PROMOTIONS.getCode())
	    refuseReasons = BaseService.getMessage("notify_refuseReasonsForPromotions");
	else
	    throw new BusinessException("error_general");

	Long[] instancesIds = BaseWorkFlow.invalidateWFTasksByAssigneesIdsOrOriginalsIds(empsIds, refuseReasons, session);
	BaseWorkFlow.invalidateWFDelegations(empsIds, session);

	// This section designed to handle invalidation business in all modules.
	if (instancesIds != null && instancesIds.length > 0) {
	    PromotionsWorkFlow.handlePromotionsInvalidation(instancesIds, session);
	    TerminationsWorkflow.handleTerminationsInvalidation(instancesIds, session);
	}
    }

    public static long countRunningProcesses(Long[] processesIds) throws BusinessException {
	Integer[] statusesIds = { WFInstanceStatusEnum.RUNNING.getCode() };
	return BaseWorkFlow.countWFInstancesByProcessesIds(processesIds, statusesIds);
    }
}
