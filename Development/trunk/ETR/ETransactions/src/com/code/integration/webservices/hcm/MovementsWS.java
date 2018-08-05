package com.code.integration.webservices.hcm;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.MovementsReasonTypesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.responses.WSResponseBase;
import com.code.integration.responses.hcm.WSMovementsResponse;
import com.code.integration.responses.util.WSPrintResponse;
import com.code.services.BaseService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.UnitsService;
import com.code.services.integration.WSSessionsManagementService;

@WebService(targetNamespace = "http://integration.code.com/movements",
	portName = "MovementsWSHttpPort")
public class MovementsWS {
    @WebMethod(operationName = "getMovements")
    @WebResult(name = "movementsResponse")
    public WSMovementsResponse getMovements(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId) {

	WSMovementsResponse response = new WSMovementsResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;
	try {
	    List<MovementTransactionData> movementsList = MovementsService.getMovementTransactionsForInquiry(employeeId, null, null, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
	    response.setMovements(movementsList);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "printMovementDecision")
    @WebResult(name = "printMovementDecisionResponse")
    public WSPrintResponse printMovement(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId,
	    @WebParam(name = "movementId") long movementId) {

	WSPrintResponse response = new WSPrintResponse();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;
	try {
	    MovementTransactionData movement = MovementsService.getMovementTransactionById(movementId);
	    if (movement == null || movement.getEmployeeId() != employeeId)
		throw new BusinessException("error_general");

	    byte[] bytes = MovementsService.getMovementDecisionBytes(movement);
	    response.setPrintBytes(bytes);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

    @WebMethod(operationName = "saveInternalAssignmentRegistration")
    @WebResult(name = "wfMovementInternalAssignmentResponse")
    public WSResponseBase saveInternalAssignmentRegistration(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "employeeId") long employeeId, @WebParam(name = "categoryMode") long categoryMode, @WebParam(name = "beneficiaryEmpId") long beneficiaryEmpId, @WebParam(name = "unitId") long unitId, @WebParam(name = "managerFlag") int managerFlag) {

	WSResponseBase response = new WSResponseBase();
	if (!WSSessionsManagementService.maintainSession(sessionId, employeeId, response))
	    return response;

	try {
	    EmployeeData beneficiaryEmployee = EmployeesService.getEmployeeData(beneficiaryEmpId);
	    if (beneficiaryEmployee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) || beneficiaryEmployee.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
		if (!beneficiaryEmployee.getCategoryId().equals(categoryMode))
		    throw new BusinessException("error_general");
	    } else {
		if (categoryMode != CategoriesEnum.PERSONS.getCode())
		    throw new BusinessException("error_general");
	    }

	    EmployeeData requester = EmployeesService.getEmployeeData(employeeId);
	    if (requester.getEmpId().equals(beneficiaryEmployee.getEmpId()) || requester.getIsManager() != FlagsEnum.ON.getCode() || !beneficiaryEmployee.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(requester.getPhysicalUnitHKey())))
		throw new BusinessException("error_notAuthorized");

	    UnitData moveToUnit = UnitsService.getUnitById(unitId);
	    if (!moveToUnit.gethKey().startsWith(UnitsService.getHKeyPrefix(requester.getPhysicalUnitHKey())))
		throw new BusinessException("error_notAuthorized");

	    MovementTransactionData movementTrans = MovementsService.constructMovementTransaction(beneficiaryEmpId, null, null, null, null, null, null, LocationFlagsEnum.INTERNAL.getCode(), null, null, null, MovementsReasonTypesEnum.FOR_WORK_INTEREST.getCode(), MovementTypesEnum.INTERNAL_ASSIGNMENT.getCode(), TransactionTypesEnum.MVT_NEW_DECISION.getCode());
	    movementTrans.setExecutionDateFlag(FlagsEnum.ON.getCode());
	    movementTrans.setEffectFlag(FlagsEnum.ON.getCode());

	    movementTrans.setUnitId(unitId);
	    movementTrans.setUnitFullName(moveToUnit.getFullName());
	    movementTrans.setManagerFlag(managerFlag);

	    movementTrans.setApprovedId(requester.getEmpId());
	    movementTrans.setDecisionApprovedId(requester.getEmpId());
	    movementTrans.setOriginalDecisionApprovedId(requester.getEmpId());

	    List<MovementTransactionData> movementList = new ArrayList<MovementTransactionData>();
	    movementList.add(movementTrans);
	    MovementsService.handleMovementsTransactions(null, movementList, false, null, FlagsEnum.OFF.getCode());

	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (Exception e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage(e instanceof BusinessException ? e.getMessage() : "error_general"));
	    if (!(e instanceof BusinessException))
		e.printStackTrace();
	}
	return response;
    }

}
