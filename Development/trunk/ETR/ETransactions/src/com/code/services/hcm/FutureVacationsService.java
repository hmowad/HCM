package com.code.services.hcm;

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
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.buswfcoop.EmployeesTransactionsConflictValidator;
import com.code.services.util.HijriDateService;

public class FutureVacationsService extends BaseService {

    private FutureVacationsService() {

    }

    /***************************** Future Vacatons *****************************/
    /*---------------------------Operations---------------------------*/

    public static void insertFutureVacation(TransientVacationTransaction futureVacationTransaction, EmployeeData vacationBeneficiary, CustomSession... useSession) throws BusinessException {
	validateDecisionNumber(futureVacationTransaction);
	if (futureVacationTransaction.getRequestType() != RequestTypesEnum.CANCEL.getCode()) {
	    if (futureVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode())
		futureVacationTransaction.setPaidVacationType(VacationsBusinessRulesService.getPaidVacationType(futureVacationTransaction.getVacationTypeId(), futureVacationTransaction.getSubVacationType(), vacationBeneficiary, futureVacationTransaction.getStartDate(), null));
	    validateFutureVacationRules(futureVacationTransaction, vacationBeneficiary);
	}
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
	    validateDecisionNumber(futureVacationTransaction);
	    if (futureVacationTransaction.getRequestType() != RequestTypesEnum.CANCEL.getCode() && !skipValidationFlag) {
		validateFutureVacationRules(futureVacationTransaction, vacationBeneficiary);
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
		    TransientVacationTransactionData oldFutureVacationTransaction = getFutureActiveVacationTransactionDataById(futureVacationTransaction.getTransientVacationParentId());
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

	    }
	    DataAccess.updateEntity(futureVacationTransaction, session);
	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_modifyVacationData");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void deleteFutureVacation(TransientVacationTransaction futureVacationTransaction, CustomSession... useSession) throws BusinessException {

	if (futureVacationTransaction.getApprovedFlag() == FlagsEnum.ON.getCode())
	    throw new BusinessException("error_vacationHasBeenModifiedOrCanceled");
	// if the deletion for initial cancel/modify vacation get the parent and set the active flag to on
	if (futureVacationTransaction.getRequestType() != RequestTypesEnum.NEW.getCode()) {
	    TransientVacationTransactionData oldFutureVacation = getFutureVacationTransactionDataById(futureVacationTransaction.getId());
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
    /*---------------------------Validations--------------------------*/

    private static void validateDecisionNumber(TransientVacationTransaction FutureVacationTransaction) throws BusinessException {
	if (countFutureVacationsByDecisionNumber(FutureVacationTransaction.getDecisionNumber(), FutureVacationTransaction.getId()) > 0
		|| VacationsService.countVacationByDecisionNumber(FutureVacationTransaction.getDecisionNumber(), FutureVacationTransaction.getVacationTransactionId()) > 0)
	    throw new BusinessException("error_vacationDecisionNumberRepeted");
    }

    private static void validateFutureVacationRules(TransientVacationTransaction futureVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {
	validateFutureVacationData(futureVacationTransaction, vacationBeneficiary);
	validateFutureVacationDates(futureVacationTransaction);
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

    private static void validateFutureVacationDates(TransientVacationTransaction futureVacationTransaction) throws BusinessException {
	if (futureVacationTransaction.getStartDate().before(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_vacationBeforeSysDate");
    }

    public static void validateOldFutureVacationActivationStatus(Long parentVacationId, Long vacationId) throws BusinessException {
	TransientVacationTransactionData oldFutureVacationTransactionData = getFutureVacationTransactionDataByParentId(parentVacationId, vacationId);
	if (oldFutureVacationTransactionData != null)
	    throw new BusinessException("error_vacationHasBeenModifiedOrCanceled");
    }
    /*---------------------------Utilities------------------------------*/

    private static Vacation constructVacation(TransientVacationTransaction futureVacationTransaction) {
	Vacation vacation = new Vacation();
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
    public static List<TransientVacationTransactionData> getFutureVacations(EmployeeData employee, String decisionNumber, Integer requestType, Long vacationTypeId, Integer approvedFlag, Date fromDate, Date toDate, Integer period, Integer locationFlag)
	    throws BusinessException {
	try {
	    Map<String, Object> qParam = new HashMap<String, Object>();
	    qParam.put("P_EMP_ID", employee.getEmpId() == null ? FlagsEnum.ALL.getCode() : employee.getEmpId());
	    qParam.put("P_DECISION_NUMBER", decisionNumber.equals("") ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    // qParam.put("P_REQUEST_TYPE_FLAG", requestType == null ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode());
	    qParam.put("P_REQUEST_TYPE", requestType);
	    // qParam.put("P_VACATION_TYPE_FLAG", vacationTypeId == null ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode());
	    qParam.put("P_VACATION_TYPE_ID", vacationTypeId);
	    qParam.put("P_APPROVED_FLAG", approvedFlag);
	    // qParam.put("P_SKIP_DATES", fromDate == null ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode());
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

    public static TransientVacationTransactionData getFutureActiveVacationTransactionDataById(long id) throws BusinessException {
	return getFutureVacationTransactionData(id, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ON.getCode());
    }

    public static TransientVacationTransactionData getFutureVacationTransactionDataById(long id) throws BusinessException {
	return getFutureVacationTransactionData(id, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.OFF.getCode());
    }

    public static TransientVacationTransactionData getFutureVacationTransactionDataByVacType(long empId, long vactionTypeId, long approvedFlag) throws BusinessException {
	return getFutureVacationTransactionData(FlagsEnum.ALL.getCode(), empId, vactionTypeId, approvedFlag);
    }

    private static TransientVacationTransactionData getFutureVacationTransactionData(long id, long empId, long vactionTypeId, long approvedFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_VACATION_ID", id);
	    qParams.put("P_EMPLOYEE_ID", empId);
	    qParams.put("P_VACATION_TYPE_ID", vactionTypeId);
	    qParams.put("P_APPROVED_FLAG", approvedFlag);

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