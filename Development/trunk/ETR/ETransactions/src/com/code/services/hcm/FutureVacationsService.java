package com.code.services.hcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.TransientVacationTransaction;
import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.buswfcoop.EmployeesTransactionsConflictValidator;
import com.code.services.config.ETRConfigurationService;
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
	TransientVacationTransaction vacationData = getFutureVacationTransactionDataById(futureVacationTransaction.getId());
	if (vacationData != null) {
	    validateDecisionNumber(futureVacationTransaction);
	    if (futureVacationTransaction.getRequestType() != RequestTypesEnum.CANCEL.getCode() && !skipValidationFlag) {
		validateFutureVacationRules(futureVacationTransaction, vacationBeneficiary);
	    }
	    modifyHistoricalVacationTransaction(futureVacationTransaction, signFutureVacationFlag, useSession);
	} else {
	    throw new BusinessException("error_historicalVacationDeleted");
	}
    }

    private static void modifyHistoricalVacationTransaction(TransientVacationTransaction futureVacationTransaction, boolean signFutureVacationFlag, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    Vacation vacation = new Vacation();

	    if (signFutureVacationFlag) {
		vacation = constructVacation(futureVacationTransaction);
		if (futureVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode() || futureVacationTransaction.getRequestType() == RequestTypesEnum.CANCEL.getCode()) {
		    TransientVacationTransaction oldFutureVacationTransaction = getFutureVacationTransactionDataById(futureVacationTransaction.getTransientVacationParentId());
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

	TransientVacationTransaction vacationData = getFutureVacationTransactionDataById(futureVacationTransaction.getId());
	if (vacationData.getApprovedFlag() == FlagsEnum.ON.getCode())
	    throw new BusinessException("error_vacationHasBeenModifiedOrCanceled");
	// if the deletion for initial cancel/modify vacation get the parent and set the active flag to on
	if (futureVacationTransaction.getRequestType() != RequestTypesEnum.NEW.getCode()) {
	    TransientVacationTransaction oldFutureVacation = getFutureVacationTransactionDataById(futureVacationTransaction.getId());
	    oldFutureVacation.setActiveFlag(FlagsEnum.ON.getCode());
	    modifyHistoricalVacationTransaction(oldFutureVacation, false, useSession);
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
	validateVacationDates(futureVacationTransaction);
	validateVacationLocation(futureVacationTransaction);

	EmployeesTransactionsConflictValidator
		.validateEmployeesTransactionsConflicts(
			new Long[] { vacationBeneficiary.getEmpId() }, new String[] { vacationBeneficiary.getName() },
			TransactionClassesEnum.VACATIONS.getCode(), futureVacationTransaction.getRequestType(),
			futureVacationTransaction.getVacationTypeId(), FlagsEnum.ALL.getCode(), new String[] { futureVacationTransaction.getStartDateString() }, new String[] { futureVacationTransaction.getEndDateString() },
			futureVacationTransaction.getRequestType().equals(RequestTypesEnum.MODIFY.getCode()) ? futureVacationTransaction.getVacationTransactionId() : null, null);

	if (ETRConfigurationService.getVacationRequestRequireJoining() == FlagsEnum.ON.getCode() && futureVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode())
	    VacationsBusinessRulesService.validatePreviousVacationJoining(vacationBeneficiary.getEmpId(), vacationBeneficiary.getCategoryId(), futureVacationTransaction.getStartDate());

	if (futureVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode() || futureVacationTransaction.getRequestType() == RequestTypesEnum.CANCEL.getCode())
	    VacationsBusinessRulesService.validateModifyAndCancelEVacation(futureVacationTransaction.getId(), futureVacationTransaction.getRequestType(), FlagsEnum.OFF.getCode());

	if (futureVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode() || futureVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode()) {

	    VacationsBusinessRulesService.validateAbsenceOfLaterVacations(vacationBeneficiary.getEmpId(), futureVacationTransaction.getVacationTypeId(), futureVacationTransaction.getStartDateString());
	    Vacation vacation = constructVacation(futureVacationTransaction);
	    if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.REGULAR.getCode())
		VacationsBusinessRulesService.validateRegularVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.COMPELLING.getCode())
		VacationsBusinessRulesService.validateCompellingVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.SICK.getCode())
		VacationsBusinessRulesService.validateSickVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.FIELD.getCode())
		VacationsBusinessRulesService.validateFieldVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.EXCEPTIONAL.getCode())
		VacationsBusinessRulesService.validateExceptionalVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.STUDY.getCode())
		VacationsBusinessRulesService.validateStudyVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.EXAM.getCode())
		VacationsBusinessRulesService.validateExamVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.ACCOMPANY.getCode())
		VacationsBusinessRulesService.validateAccompanyVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.RELIEF.getCode())
		VacationsBusinessRulesService.validateReliefVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.SPORTIVE.getCode()
		    || futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.CULTURAL.getCode()
		    || futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.SOCIAL.getCode())
		VacationsBusinessRulesService.validateSportiveCultureSocialVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.COMPENSATION.getCode())
		VacationsBusinessRulesService.validateCompensationVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.MATERNITY.getCode())
		VacationsBusinessRulesService.validateMaternityVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.MOTHERHOOD.getCode())
		VacationsBusinessRulesService.validateMotherhoodVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.ORPHAN_CARE.getCode())
		VacationsBusinessRulesService.validateOrphanCareVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.DEATH_WAITING_PERIOD.getCode())
		VacationsBusinessRulesService.validateDeathWaitingPeriodVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.DEATH_OF_RELATIVE.getCode())
		VacationsBusinessRulesService.validateDeathOfRelativeVacationRules(vacation, vacationBeneficiary);
	    else if (futureVacationTransaction.getVacationTypeId() == VacationTypesEnum.NEW_BABY.getCode())
		VacationsBusinessRulesService.validateNewBabyVacationRules(vacation, vacationBeneficiary);
	}
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

    private static void validateVacationDates(TransientVacationTransaction futureVacationTransaction) throws BusinessException {
	if (futureVacationTransaction.getRequestType().equals(RequestTypesEnum.NEW.getCode()) && !HijriDateService.isValidHijriDate(futureVacationTransaction.getStartDate()))
	    throw new BusinessException("error_invalidHijriDate");

	if (futureVacationTransaction.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), 1)))
	    throw new BusinessException("error_vacationBeforeSysDate");
    }

    private static void validateVacationLocation(TransientVacationTransaction futureVacationTransaction) throws BusinessException {
	if (futureVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode()
		&& futureVacationTransaction.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode())
		&& futureVacationTransaction.getLocation().contains(getMessage("label_ksa")))
	    throw new BusinessException("error_invalidLocation");
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
	vacation.setMigrationFlag(FlagsEnum.ON.getCode());
	vacation.setDecisionNumber(futureVacationTransaction.getDecisionNumber());
	vacation.setDecisionDate(futureVacationTransaction.getDecisionDate());
	vacation.setAttachments(futureVacationTransaction.getAttachments());
	return vacation;
    }

    /*---------------------------Queries------------------------------*/
    private static long countFutureVacationsByDecisionNumber(String decisionNumber, Long historicalVacationId) throws BusinessException {
	try {
	    Map<String, Object> queryParam = new HashMap<String, Object>();
	    queryParam.put("P_DECISION_NUMBER", decisionNumber);
	    queryParam.put("P_VACATION_ID", historicalVacationId == null ? FlagsEnum.ALL.getCode() : historicalVacationId);
	    List<Long> count = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_FUTURE_VACATIONS_BY_DECISION_NUMBER.getCode(), queryParam);
	    return (count.isEmpty() || count == null ? 0 : count.get(0));
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_getVacationsData");
	}
    }

    public static TransientVacationTransaction getFutureVacationTransactionDataById(long id) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_VACATION_ID", id);

	    List<TransientVacationTransaction> result = DataAccess.executeNamedQuery(TransientVacationTransaction.class, QueryNamesEnum.HCM_GET_FUTURE_VACATION_TRANSACTION_BY_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_getVacationData");
	}
    }

}