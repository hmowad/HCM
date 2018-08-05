package com.code.services.hcm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.additionalspecializations.EmployeeAdditionalSpecializationData;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransaction;
import com.code.dal.orm.hcm.movements.MovementWishTransaction;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MoveWishStatusesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

public class MovementsWishesService extends BaseService {

    /**
     * private constructor to prevent instantiation
     * 
     */
    private MovementsWishesService() {

    }

    /***************************** MovementWishTransaction *****************************/

    /*---------------------------Operations---------------------------*/
    public static void handleMovementWishTransaction(MovementWishTransaction movementWishTransaction, CustomSession... useSession) throws BusinessException {
	if (movementWishTransaction.getStatus().equals(MoveWishStatusesEnum.NEW.getCode()))
	    addMovementWishTransaction(movementWishTransaction);
	else
	    updateMovementWishTransaction(movementWishTransaction);
    }

    private static void addMovementWishTransaction(MovementWishTransaction movementWishTransaction, CustomSession... useSession) throws BusinessException {
	validateMovementWishTransaction(movementWishTransaction);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    movementWishTransaction.setTransDate(HijriDateService.getHijriSysDate());
	    movementWishTransaction.setTransLastUpdateDate(HijriDateService.getHijriSysDate());

	    DataAccess.addEntity(movementWishTransaction, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    movementWishTransaction.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void updateMovementWishTransaction(MovementWishTransaction movementWishTransaction, CustomSession... useSession) throws BusinessException {
	validateMovementWishTransaction(movementWishTransaction);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    movementWishTransaction.setTransLastUpdateDate(HijriDateService.getHijriSysDate());
	    DataAccess.updateEntity(movementWishTransaction, session);

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

    public static void setMovementWishesConfig(int minServicePeriod, boolean openFlag, long loginEmpId, CustomSession session) throws BusinessException {
	if (minServicePeriod < 0 || minServicePeriod > 99)
	    throw new BusinessException("error_general");

	ETRConfigurationService.setMovementOpenWishesFlag(openFlag, loginEmpId, session);
	ETRConfigurationService.setMovementWishesMinServicePeriod(minServicePeriod, loginEmpId, session);
    }

    public static void cancelInvalidMovementsWishesTransactions() {
	try {
	    List<MovementWishTransaction> transactions = getMovementWishInvalidTransactions();

	    for (MovementWishTransaction transaction : transactions) {
		CustomSession session = DataAccess.getSession();
		try {
		    session.beginTransaction();

		    transaction.setStatus(MoveWishStatusesEnum.CANCELED.getCode());
		    transaction.setTransLastUpdateDate(HijriDateService.getHijriSysDate());
		    DataAccess.updateEntity(transaction, session);

		    session.commitTransaction();
		} catch (Exception e) {
		    session.rollbackTransaction();
		    e.printStackTrace();
		} finally {
		    try {
			session.close();
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	} catch (BusinessException e) {
	    e.printStackTrace();
	}
    }

    /*---------------------------Validations--------------------------*/
    public static void validateMovementWishTransaction(MovementWishTransaction movementWishTransaction) throws BusinessException {

	validateMovementWishOpenFlagStatus();
	validateMovementWishMandatoryFields(movementWishTransaction);

	if (movementWishTransaction.getFromRegionId().equals(movementWishTransaction.getToRegionId()))
	    throw new BusinessException("error_regionFromSameAsRegionTo");

	EmployeeData employee = EmployeesService.getEmployeeData(movementWishTransaction.getEmployeeId());
	MovementWishTransaction dbMovementWishTransaction = getEmployeeActiveMovementWish(movementWishTransaction.getEmployeeId());

	if (employee.getCategoryId() != CategoriesEnum.SOLDIERS.getCode())
	    throw new BusinessException("error_employeeMustBeSoldier");

	if (!movementWishTransaction.getFromRegionId().equals(employee.getOfficialRegionId()))
	    throw new BusinessException("error_employeeOfficialRegionNotSameAsRequest");

	if (movementWishTransaction.getStatus().equals(MoveWishStatusesEnum.NEW.getCode()) && dbMovementWishTransaction != null)
	    throw new BusinessException("error_employeeHasActiveWish");

	if ((movementWishTransaction.getStatus().equals(MoveWishStatusesEnum.MODIFIED.getCode()) || movementWishTransaction.getStatus().equals(MoveWishStatusesEnum.CANCELED.getCode())) && dbMovementWishTransaction == null)
	    throw new BusinessException("error_noMovementWishesForEmployee");

	if (movementWishTransaction.getStatus().equals(MoveWishStatusesEnum.MODIFIED.getCode()) && dbMovementWishTransaction.getToRegionId().equals(movementWishTransaction.getToRegionId()))
	    throw new BusinessException("error_oldMovementWishSameAsNew");

	if (!movementWishTransaction.getStatus().equals(MoveWishStatusesEnum.CANCELED.getCode())) {
	    validateEmployeeMovementBlock(employee);
	    validateEmployeeServicePeriod(employee);
	}
    }

    private static void validateMovementWishOpenFlagStatus() throws BusinessException {
	if (!ETRConfigurationService.getMovementOpenWishesFlag())
	    throw new BusinessException("error_movementWishOpenFlagClosed");
    }

    private static void validateMovementWishMandatoryFields(MovementWishTransaction movementWishTransaction) throws BusinessException {
	if (movementWishTransaction.getStatus() == null)
	    throw new BusinessException("error_general");

	if (movementWishTransaction.getEmployeeId() == null)
	    throw new BusinessException("error_empSelectionManadatory");
	if (movementWishTransaction.getFromRegionId() == null)
	    throw new BusinessException("error_moveRegionFromMandatory");
	if (movementWishTransaction.getToRegionId() == null)
	    throw new BusinessException("error_moveRegionToMandatory");
    }

    private static void validateEmployeeMovementBlock(EmployeeData employee) throws BusinessException {
	if (employee.getMovementBlacklistFlag().equals(FlagsEnum.ON.getCode()))
	    throw new BusinessException("error_mvtBlackList");

	List<EmployeeAdditionalSpecializationData> EmployeeAdditionalSpecializationDataList = AdditionalSpecializationsService.getEmployeeAdditionalSpecializationsDataByEmpId(employee.getEmpId());

	for (int i = 0; i < EmployeeAdditionalSpecializationDataList.size(); i++) {
	    if (EmployeeAdditionalSpecializationDataList.get(i).getMovementBlacklistFlagBoolean() == true) {
		throw new BusinessException("error_employeeAdditionalSpecBlacklist");
	    }
	}
    }

    private static void validateEmployeeServicePeriod(EmployeeData employee) throws BusinessException {
	int configServicePeriod = getMovementWishesMinServicePeriod();
	Date employeeLastServiceDate;
	List<MovementTransaction> movementTransactionList = MovementsService.getLastMoveTransactionForWishes(employee.getEmpId(), CommonService.getRegionById(employee.getOfficialRegionId()).getShortDescription());
	if (movementTransactionList.size() == 0)
	    employeeLastServiceDate = employee.getRecruitmentDate();
	else
	    employeeLastServiceDate = movementTransactionList.get(0).getExecutionDate();

	if (employee.getOldEmpId() != null && !employee.getOldEmpId().isEmpty()) {
	    Date lastDeductedWorkDate = getLastDeductedWorkDate(employee.getOldEmpId());
	    if (lastDeductedWorkDate != null && lastDeductedWorkDate.after(employeeLastServiceDate))
		employeeLastServiceDate = lastDeductedWorkDate;
	}

	if (HijriDateService.hijriDateDiffByHijriYear(employeeLastServiceDate, HijriDateService.getHijriSysDate()) < configServicePeriod)
	    throw new BusinessException("error_moveServicePeriodBelowRequired");
    }

    /*---------------------------Queries------------------------------*/
    public static boolean getMovementWishesOpenFlag() throws BusinessException {
	return ETRConfigurationService.getMovementOpenWishesFlag();
    }

    public static int getMovementWishesMinServicePeriod() throws BusinessException {
	return ETRConfigurationService.getMovementWishesMinServicePeriod();
    }

    public static MovementWishTransaction getEmployeeActiveMovementWish(long employeeId) throws BusinessException {
	List<MovementWishTransaction> movementWishTransactions = searchMovementWishTransactions(employeeId, new Integer[] { MoveWishStatusesEnum.NEW.getCode(), MoveWishStatusesEnum.MODIFIED.getCode() });
	return movementWishTransactions.size() > 0 ? movementWishTransactions.get(0) : null;
    }

    private static List<MovementWishTransaction> searchMovementWishTransactions(long employeeId, Integer[] statusIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_EMPLOYEE_ID", employeeId);

	    if (statusIds == null) {
		qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUS_IDS", new Integer[] { FlagsEnum.ALL.getCode() });
	    } else {
		qParams.put("P_STATUS_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUS_IDS", statusIds);
	    }

	    return DataAccess.executeNamedQuery(MovementWishTransaction.class, QueryNamesEnum.HCM_SEARCH_MOVEMENT_WISH_TRANSACTIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<MovementWishTransaction> getMovementWishInvalidTransactions() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(MovementWishTransaction.class, QueryNamesEnum.HCM_SEARCH_MOVEMENT_WISH_TRANSACTIONS_INVALID_TRANSACTIONS.getCode(), null);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    private static Date getLastDeductedWorkDate(String empOldId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empOldId);

	    List<Date> lastDateList = DataAccess.executeNamedQuery(Date.class, QueryNamesEnum.HCM_GET_LAST_DEDUCTED_WORK_DATE.getCode(), qParams);
	    return lastDateList.size() > 0 ? lastDateList.get(0) : null;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /*---------------------------Reports------------------------------*/
    public static byte[] getMovementWishTransactionsBytes(long fromRegionId, long toRegionId, long rankId, int serviceYearsFrom, int serviceYearsTo, int employeeCount, String jobDesc) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("P_FROM_MOVE_REGION_ID", fromRegionId);
	    parameters.put("P_TO_MOVE_REGION_ID", toRegionId);
	    parameters.put("P_RANK_ID", rankId);
	    parameters.put("P_SERVICE_YEARS_FROM", serviceYearsFrom);
	    parameters.put("P_SERVICE_YEARS_TO", serviceYearsTo);
	    parameters.put("P_EMPLOYEES_COUNT", employeeCount);
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_JOB_DESC", (jobDesc == null || jobDesc.equals("")) ? FlagsEnum.ALL.getCode() + "" : jobDesc);
	    return getReportData(ReportNamesEnum.MOVEMENTS_SOLDIERS_MOVE_WISHES.getCode(), parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }
}
