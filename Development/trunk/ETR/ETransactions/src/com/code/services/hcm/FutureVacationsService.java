package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.TransientVacationTransaction;
import com.code.dal.orm.hcm.vacations.TransientVacationTransactionData;
import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.PaidVacationTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.services.BaseService;
import com.code.services.buswfcoop.EmployeesTransactionsConflictValidator;
import com.code.services.integration.PayrollEngineService;
import com.code.services.util.HijriDateService;

public class FutureVacationsService extends BaseService {

    private FutureVacationsService() {

    }

    /***************************** Future Vacations *****************************/
    /*---------------------------Operations---------------------------*/

    public static void insertFutureVacation(TransientVacationTransaction futureVacationTransaction, EmployeeData vacationBeneficiary, CustomSession... useSession) throws BusinessException {
	validateDecisionNumber(futureVacationTransaction);

	if (futureVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode())
	    futureVacationTransaction.setPaidVacationType(VacationsBusinessRulesService.getPaidVacationType(futureVacationTransaction.getVacationTypeId(), futureVacationTransaction.getSubVacationType(), vacationBeneficiary, futureVacationTransaction.getStartDate(), null));
	validateFutureVacationRules(futureVacationTransaction, vacationBeneficiary);

	insertFutureVacationTransaction(futureVacationTransaction, useSession);
    }

    private static void insertFutureVacationTransaction(TransientVacationTransaction futureVacationTransaction, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(futureVacationTransaction, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    futureVacationTransaction.setId(null);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void modifyFutureVacation(TransientVacationTransaction futureVacationTransaction, EmployeeData vacationBeneficiary, boolean signFutureVacationFlag, boolean skipValidationFlag, CustomSession... useSession) throws BusinessException {
	if (futureVacationTransaction != null) {
	    if (!skipValidationFlag) {
		validateDecisionNumber(futureVacationTransaction);
		validateFutureVacationRules(futureVacationTransaction, vacationBeneficiary);
	    } else {
		if (futureVacationTransaction.getJoiningDate() != null && futureVacationTransaction.getApprovedFlag() == FlagsEnum.ON.getCode() && futureVacationTransaction.getActiveFlag() == FlagsEnum.ON.getCode())
		    validateFutureVacationJoining(futureVacationTransaction);
	    }
	    modifyFutureVacationTransaction(futureVacationTransaction, signFutureVacationFlag, useSession);

	} else {
	    throw new BusinessException("error_vacationDeleted");
	}
    }

    private static void modifyFutureVacationTransaction(TransientVacationTransaction futureVacationTransaction, boolean signFutureVacationFlag, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    Vacation vacation = new Vacation();

	    if (signFutureVacationFlag) {
		vacation = constructVacation(futureVacationTransaction);
		if (futureVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode() || futureVacationTransaction.getRequestType() == RequestTypesEnum.CANCEL.getCode()) {
		    TransientVacationTransactionData oldFutureVacationTransaction = getFutureVacationTransactionDataById(futureVacationTransaction.getTransientVacationParentId());
		    vacation.setVacationId(oldFutureVacationTransaction.getVacationTransactionId());
		    if (futureVacationTransaction.getRequestType() == RequestTypesEnum.CANCEL.getCode())
			vacation.setStatus(RequestTypesEnum.CANCEL.getCode());
		    else
			vacation.setStatus(RequestTypesEnum.MODIFY.getCode());
		    DataAccess.updateEntity(vacation, session);
		} else {
		    DataAccess.addEntity(vacation, session);
		    futureVacationTransaction.setVacationTransactionId(vacation.getVacationId());
		}
		if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		    doPayrollIntegration(vacation, FlagsEnum.OFF.getCode(), session);
	    }
	    DataAccess.updateEntity(futureVacationTransaction, session);
	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_modifyVacationData");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void deleteFutureVacation(TransientVacationTransaction futureVacationTransaction, CustomSession... useSession) throws BusinessException {

	TransientVacationTransactionData futureVacation = getFutureVacationTransactionDataById(futureVacationTransaction.getId());
	if (futureVacation.getApprovedFlag() == FlagsEnum.ON.getCode())
	    throw new BusinessException("error_vacationHasBeenModifiedOrCanceled");
	// if the deletion for initial cancel/modify vacation get the parent and set the active flag to on
	if (futureVacationTransaction.getRequestType() != RequestTypesEnum.NEW.getCode()) {
	    TransientVacationTransactionData oldFutureVacation = getFutureVacationTransactionDataById(futureVacationTransaction.getTransientVacationParentId());
	    oldFutureVacation.setActiveFlag(FlagsEnum.ON.getCode());
	    modifyFutureVacationTransaction(oldFutureVacation.getTransientVacationTransaction(), false, useSession);
	}
	deleteFutureVacationTransaction(futureVacationTransaction, useSession);
    }

    private static void deleteFutureVacationTransaction(TransientVacationTransaction futureVacationTransaction, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(futureVacationTransaction, session);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_cancelVacationData");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    private static void doPayrollIntegration(Vacation request, Integer resendFlag, CustomSession... useSession) throws BusinessException {
	Long adminDecisionId = null;
	Vacation originalVacation = null;
	EmployeeData employee = EmployeesService.getEmployeeData(request.getEmpId());
	if (employee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
	    if (employee.getPhysicalUnitId().equals(UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getId())) {
		if (request.getJoiningDate() != null) { // in case of vacation joining
		    originalVacation = request;
		    if (request.getVacationTypeId().equals(VacationTypesEnum.SICK.getCode())) {
			if (request.getPaidVacationType().equals(PaidVacationTypesEnum.HALF_PAID.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.PRESIDENCY_HALF_PAID_SICK_VACATION_JOINING.getCode();
			}
		    }
		} else {
		    if (request.getStatus() == RequestTypesEnum.NEW.getCode()) {
			if (request.getVacationTypeId().equals(VacationTypesEnum.REGULAR.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.PRESIDENCY_REGULAR_VACATION_REQUEST.getCode();
			} else if (request.getVacationTypeId().equals(VacationTypesEnum.COMPELLING.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.PRESIDENCY_COMPELLING_VACATION_REQUEST.getCode();
			} else if (request.getVacationTypeId().equals(VacationTypesEnum.SICK.getCode())) {
			    if (request.getPaidVacationType().equals(PaidVacationTypesEnum.FULL_PAID.getCode())) {
				adminDecisionId = AdminDecisionsEnum.PRESIDENCY_FULL_PAID_SICK_VACATION_REQUEST.getCode();
			    } else if (request.getPaidVacationType().equals(PaidVacationTypesEnum.HALF_PAID.getCode())) {
				adminDecisionId = AdminDecisionsEnum.PRESIDENCY_HALF_PAID_SICK_VACATION_REQUEST.getCode();
			    }
			}
		    } else if (request.getStatus() == RequestTypesEnum.MODIFY.getCode()) {
			originalVacation = VacationsService.getVacationById(request.getVacationId());
			if (request.getVacationTypeId().equals(VacationTypesEnum.REGULAR.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.PRESIDENCY_MODIFY_REGULAR_VACATION_REQUEST.getCode();
			} else if (request.getVacationTypeId().equals(VacationTypesEnum.SICK.getCode())) {
			    if (request.getPaidVacationType().equals(PaidVacationTypesEnum.FULL_PAID.getCode())) {
				adminDecisionId = AdminDecisionsEnum.PRESIDENCY_MODIFY_FULL_PAID_SICK_VACATION_REQUEST.getCode();
			    } else if (request.getPaidVacationType().equals(PaidVacationTypesEnum.HALF_PAID.getCode())) {
				adminDecisionId = AdminDecisionsEnum.PRESIDENCY_MODIFY_HALF_PAID_SICK_VACATION_REQUEST.getCode();
			    }
			}
		    } else if (request.getStatus() == RequestTypesEnum.CANCEL.getCode()) {
			originalVacation = VacationsService.getVacationById(request.getVacationId());
			if (request.getVacationTypeId().equals(VacationTypesEnum.REGULAR.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.PRESIDENCY_CANCEL_REGULAR_VACATION_REQUEST.getCode();
			} else if (request.getVacationTypeId().equals(VacationTypesEnum.COMPELLING.getCode())) {
			    adminDecisionId = AdminDecisionsEnum.PRESIDENCY_CANCEL_COMPELLING_VACATION_REQUEST.getCode();
			} else if (request.getVacationTypeId().equals(VacationTypesEnum.SICK.getCode())) {
			    if (request.getPaidVacationType().equals(PaidVacationTypesEnum.FULL_PAID.getCode())) {
				adminDecisionId = AdminDecisionsEnum.PRESIDENCY_CANCEL_FULL_PAID_SICK_VACATION_REQUEST.getCode();
			    } else if (request.getPaidVacationType().equals(PaidVacationTypesEnum.HALF_PAID.getCode())) {
				adminDecisionId = AdminDecisionsEnum.PRESIDENCY_CANCEL_HALF_PAID_SICK_VACATION_REQUEST.getCode();
			    }
			}
		    }
		}
	    }
	}
	if (adminDecisionId != null) {
	    boolean isOpenedSession = isSessionOpened(useSession);
	    CustomSession session = isOpenedSession ? useSession[0] : null;
	    if (session == null)
		throw new BusinessException("error_general");
	    String gregDecisionDateString = HijriDateService.hijriToGregDateString(request.getDecisionDateString());
	    String gregVacationStartDateString = request.getJoiningDate() != null ? HijriDateService.hijriToGregDateString(HijriDateService.getHijriDateString(request.getJoiningDate())) : HijriDateService.hijriToGregDateString(request.getStartDateString());
	    String gregVacationEndDateString = (request.getJoiningDate() != null || adminDecisionId.equals(AdminDecisionsEnum.PRESIDENCY_CANCEL_HALF_PAID_SICK_VACATION_REQUEST.getCode())) ? null : HijriDateService.hijriToGregDateString(request.getEndDateString());
	    String requestDecisionNumber = request.getJoiningDate() != null ? System.currentTimeMillis() + "" : request.getDecisionNumber();
	    String originalDecisionNumber = originalVacation != null && originalVacation.getDecisionNumber() != null ? originalVacation.getDecisionNumber() : null;
	    List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>(
		    Arrays.asList(new AdminDecisionEmployeeData(employee.getEmpId(), employee.getName(), request.getVacationId(), null, gregVacationStartDateString, gregVacationEndDateString, requestDecisionNumber, originalDecisionNumber)));
	    session.flushTransaction();
	    PayrollEngineService.doPayrollIntegration(adminDecisionId, employee.getCategoryId(), gregVacationStartDateString, adminDecisionEmployeeDataList, employee.getPhysicalUnitId(), gregDecisionDateString, DataAccess.getTableName(TransientVacationTransaction.class), resendFlag, request.getJoiningDate() != null ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode(), session);
	}
    }

    public static void payrollIntegrationFailureHandle(Date decisionDate, String decisionNumber, CustomSession session) throws BusinessException {
	Vacation vacation = VacationsService.getVacationByDecisionDateAndDecisionNumber(decisionDate, decisionNumber);
	if (vacation != null)
	    doPayrollIntegration(vacation, FlagsEnum.ON.getCode(), session);
	else
	    throw new BusinessException("error_transactionDataRetrievingError");
    }
    /*---------------------------Validations--------------------------*/

    private static void validateDecisionNumber(TransientVacationTransaction FutureVacationTransaction) throws BusinessException {
	if (countFutureVacationsByDecisionNumber(FutureVacationTransaction.getDecisionNumber(), FutureVacationTransaction.getId()) > 0
		|| VacationsService.countVacationByDecisionNumber(FutureVacationTransaction.getDecisionNumber(), FutureVacationTransaction.getVacationTransactionId()) > 0)
	    throw new BusinessException("error_vacationDecisionNumberRepeted");
    }

    private static void validateFutureVacationRules(TransientVacationTransaction futureVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {
	validateFutureVacationData(futureVacationTransaction, vacationBeneficiary);

	Vacation vacation = constructVacation(futureVacationTransaction);

	EmployeesTransactionsConflictValidator
		.validateEmployeesTransactionsConflicts(
			new Long[] { vacationBeneficiary.getEmpId() }, new String[] { vacationBeneficiary.getName() },
			TransactionClassesEnum.VACATIONS.getCode(), futureVacationTransaction.getRequestType(),
			futureVacationTransaction.getVacationTypeId(), FlagsEnum.ALL.getCode(), new String[] { futureVacationTransaction.getStartDateString() }, new String[] { futureVacationTransaction.getEndDateString() },
			futureVacationTransaction.getRequestType().equals(RequestTypesEnum.MODIFY.getCode()) ? futureVacationTransaction.getVacationTransactionId() : null, null);

	VacationsService.validateVacationRules(vacation, vacationBeneficiary, FlagsEnum.ON.getCode(), FlagsEnum.OFF.getCode());
    }

    private static void validateFutureVacationData(TransientVacationTransaction futureVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {
	if (futureVacationTransaction.getVacationTypeId() == null)
	    throw new BusinessException("error_vacationTypeMandatory");

	if (futureVacationTransaction.getDecisionNumber() == null || futureVacationTransaction.getDecisionNumber().trim().equals(""))
	    throw new BusinessException("error_decisionNumberMandatory");

	if (futureVacationTransaction.getDecisionDate() == null)
	    throw new BusinessException("error_decisionDateIsMandatory");

	if (futureVacationTransaction.getPeriod() == null || futureVacationTransaction.getPeriod() == 0)
	    throw new BusinessException("error_periodMandatory");

	if (futureVacationTransaction.getPeriod() < 0)
	    throw new BusinessException("error_periodNotNegative");
	if (futureVacationTransaction.getContactNumber() == null || futureVacationTransaction.getContactNumber().trim().equals(""))
	    throw new BusinessException("error_contactNumberMandatory");

	if (futureVacationTransaction.getContactAddress() == null || futureVacationTransaction.getContactAddress().trim().equals(""))
	    throw new BusinessException("error_contactAddressMandatory");

    }

    public static void validateOldFutureVacationActivationStatus(Long parentVacationId, Long vacationId) throws BusinessException {
	TransientVacationTransactionData oldFutureVacationTransactionData = getFutureVacationTransactionDataByParentId(parentVacationId, vacationId);
	if (oldFutureVacationTransactionData != null)
	    throw new BusinessException("error_vacationHasBeenModifiedOrCanceled");
    }

    private static void validateFutureVacationJoining(TransientVacationTransaction futureVacationTransaction) throws BusinessException {
	if (futureVacationTransaction.getExceededDays() == null || futureVacationTransaction.getExceededDays() < 0)
	    throw new BusinessException("error_exceededDaysPositive");
    }
    /*---------------------------Utilities------------------------------*/

    private static Vacation constructVacation(TransientVacationTransaction futureVacationTransaction) {
	Vacation vacation = new Vacation();
	if (futureVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode() || futureVacationTransaction.getRequestType() == RequestTypesEnum.CANCEL.getCode())
	    vacation.setVacationId(futureVacationTransaction.getVacationTransactionId());

	vacation.setEmpId(futureVacationTransaction.getEmpId());
	vacation.setVacationTypeId(futureVacationTransaction.getVacationTypeId());
	vacation.setSubVacationType(futureVacationTransaction.getSubVacationType());
	vacation.setPaidVacationType(futureVacationTransaction.getPaidVacationType());
	vacation.setStartDate(futureVacationTransaction.getStartDate());
	vacation.setEndDate(futureVacationTransaction.getEndDate());
	vacation.setPeriod(futureVacationTransaction.getPeriod());
	vacation.setLocationFlag(futureVacationTransaction.getLocationFlag());
	vacation.setLocation(futureVacationTransaction.getLocation());
	vacation.setContactAddress(futureVacationTransaction.getContactAddress());
	vacation.setContactNumber(futureVacationTransaction.getContactNumber());
	vacation.setExceededDays(futureVacationTransaction.getExceededDays());
	vacation.setJoiningDate(futureVacationTransaction.getJoiningDate());
	vacation.setStatus(futureVacationTransaction.getRequestType());
	vacation.setEtrFlag(FlagsEnum.OFF.getCode());
	vacation.setMigrationFlag(FlagsEnum.OFF.getCode());
	vacation.setDecisionNumber(futureVacationTransaction.getDecisionNumber());
	vacation.setDecisionDate(futureVacationTransaction.getDecisionDate());
	vacation.setAttachments(futureVacationTransaction.getAttachments());
	return vacation;
    }

    /*---------------------------Queries------------------------------*/
    public static List<TransientVacationTransactionData> searchFutureVacations(EmployeeData employee, String decisionNumber, Integer requestType, Long vacationTypeId, Integer approvedFlag, Date fromDate, Date toDate, Integer period, Integer locationFlag)
	    throws BusinessException {
	return getFutureVacations(employee, decisionNumber, requestType, vacationTypeId, approvedFlag, fromDate, toDate, period, locationFlag);
    }

    private static List<TransientVacationTransactionData> getFutureVacations(EmployeeData employee, String decisionNumber, Integer requestType, Long vacationTypeId, Integer approvedFlag, Date fromDate, Date toDate, Integer period, Integer locationFlag)
	    throws BusinessException {
	try {
	    Map<String, Object> qParam = new HashMap<String, Object>();
	    qParam.put("P_EMP_ID", employee.getEmpId() == null ? FlagsEnum.ALL.getCode() : employee.getEmpId());
	    qParam.put("P_DECISION_NUMBER", decisionNumber.equals("") ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    qParam.put("P_REQUEST_TYPE", requestType);
	    qParam.put("P_VACATION_TYPE_ID", vacationTypeId);
	    qParam.put("P_APPROVED_FLAG", approvedFlag);
	    qParam.put("P_FROM_DATE_FLAG", fromDate == null ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode());
	    qParam.put("P_FROM_DATE", fromDate == null ? HijriDateService.getHijriDateString(HijriDateService.getHijriSysDate()) : HijriDateService.getHijriDateString(fromDate));
	    qParam.put("P_TO_DATE_FLAG", toDate == null ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode());
	    qParam.put("P_TO_DATE", toDate == null ? HijriDateService.getHijriDateString(HijriDateService.getHijriSysDate()) : HijriDateService.getHijriDateString(toDate));
	    qParam.put("P_PERIOD", (period == null ? FlagsEnum.ALL.getCode() : period));
	    qParam.put("P_LOCATION_FLAG", locationFlag);
	    return DataAccess.executeNamedQuery(TransientVacationTransactionData.class, QueryNamesEnum.HCM_SEARCH_FUTURE_VACATIONS.getCode(), qParam);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static long countFutureVacationsByDecisionNumber(String decisionNumber, Long futureVacationId) throws BusinessException {
	try {
	    Map<String, Object> queryParam = new HashMap<String, Object>();
	    queryParam.put("P_DECISION_NUMBER", decisionNumber);
	    queryParam.put("P_VACATION_ID", futureVacationId == null ? FlagsEnum.ALL.getCode() : futureVacationId);
	    List<Long> count = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_FUTURE_VACATIONS_BY_DECISION_NUMBER.getCode(), queryParam);
	    return (count.isEmpty() || count == null ? 0 : count.get(0));
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_getVacationsData");
	}
    }

    public static TransientVacationTransactionData getFutureVacationTransactionDataById(long id) throws BusinessException {
	return getFutureVacationTransactionData(id, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static TransientVacationTransactionData getFutureVacationTransactionDataByVacType(long empId, long vactionTypeId, long approvedFlag, long activeFlag, long requestType) throws BusinessException {
	return getFutureVacationTransactionData(FlagsEnum.ALL.getCode(), empId, vactionTypeId, approvedFlag, activeFlag, requestType);
    }

    private static TransientVacationTransactionData getFutureVacationTransactionData(long futureVacationId, long empId, long vactionTypeId, long approvedFlag, long activeFlag, long requestTypeFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_VACATION_ID", futureVacationId);
	    qParams.put("P_EMPLOYEE_ID", empId);
	    qParams.put("P_VACATION_TYPE_ID", vactionTypeId);
	    qParams.put("P_APPROVED_FLAG", approvedFlag);
	    qParams.put("P_ACTIVE_FLAG", activeFlag);
	    qParams.put("P_REQUEST_TYPE_FLAG", requestTypeFlag);

	    List<TransientVacationTransactionData> result = DataAccess.executeNamedQuery(TransientVacationTransactionData.class, QueryNamesEnum.HCM_GET_FUTURE_VACATION_TRANSACTION_BY_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_getVacationData");
	}
    }

    public static TransientVacationTransactionData getFutureVacationTransactionDataByParentId(Long parentId, Long vacationId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PARENT_ID", parentId);
	    qParams.put("P_VACATION_ID", vacationId == null ? FlagsEnum.ALL.getCode() : vacationId);
	    List<TransientVacationTransactionData> result = DataAccess.executeNamedQuery(TransientVacationTransactionData.class, QueryNamesEnum.HCM_GET_FUTURE_VACATION_TRANSACTION_BY_PARENT_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_getVacationData");
	}
    }

}