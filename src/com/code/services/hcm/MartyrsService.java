package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.martyrs.MartyrHonor;
import com.code.dal.orm.hcm.martyrs.MartyrTransactionData;
import com.code.dal.orm.hcm.martyrs.MartyrdomReason;
import com.code.enums.FlagsEnum;
import com.code.enums.MartyrdomTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

/**
 * The class <code>MartyrsService</code> provides methods for handling Martyrs operations such as adding, updating martyrs transactions, and Martyrdom reasons.</br>
 */

public class MartyrsService extends BaseService {

    private MartyrsService() {
    }

    /*-----------------------------------------------------Martyrs Transactions operations---------------------------------------------*/

    public static void addMartyrTransactionAndHonors(MartyrTransactionData martyrTransactionData, List<MartyrTransactionData> martyrTransactionDataList, List<MartyrHonor> martyrHonorsData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    addMartyrTransaction(martyrTransactionData, martyrTransactionDataList, session);

	    for (MartyrHonor martyrHonor : martyrHonorsData) {
		martyrHonor.setMartyrTransactionId(martyrTransactionData.getId());
		addMartyrHonor(martyrHonor, martyrTransactionData.getMartyrdomDate(), session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    martyrTransactionData.setId(null);

	    for (MartyrHonor martyrHonor : martyrHonorsData)
		martyrHonor.setId(null);

	    throw e;
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateMartyrTransactionAndHonors(MartyrTransactionData martyrTransactionData, List<MartyrTransactionData> martyrTransactionDataList, List<MartyrHonor> martyrHonorsData, CustomSession... useSession) throws BusinessException {
	List<MartyrHonor> newHonorsList = new ArrayList<MartyrHonor>();

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    updateMartyrTransaction(martyrTransactionData, martyrTransactionDataList, session);

	    for (MartyrHonor martyrHonor : martyrHonorsData)
		if (martyrHonor.getId() == null) {
		    newHonorsList.add(martyrHonor);
		    martyrHonor.setMartyrTransactionId(martyrTransactionData.getId());
		    addMartyrHonor(martyrHonor, martyrTransactionData.getMartyrdomDate(), session);
		}

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    for (MartyrHonor newHonor : newHonorsList)
		newHonor.setId(null);

	    throw e;
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void addMartyrTransaction(MartyrTransactionData martyrTransactionData, List<MartyrTransactionData> martyrTransactionDataList, CustomSession... useSession) throws BusinessException {
	validateMartyrTransaction(martyrTransactionData, martyrTransactionDataList);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(martyrTransactionData.getMartyrTransaction(), session);
	    martyrTransactionData.setId(martyrTransactionData.getMartyrTransaction().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    martyrTransactionData.setId(null);
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void updateMartyrTransaction(MartyrTransactionData martyrTransactionData, List<MartyrTransactionData> martyrTransactionDataList, CustomSession... useSession) throws BusinessException {
	validateMartyrTransaction(martyrTransactionData, martyrTransactionDataList);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(martyrTransactionData.getMartyrTransaction(), session);

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

    public static void deleteMartyrTransactionAndHonors(MartyrTransactionData martyrTransactionData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<MartyrHonor> honors = getMartyrHonors(martyrTransactionData.getId(), FlagsEnum.ALL.getCode());

	    for (MartyrHonor martyrHonor : honors) {
		if (martyrHonor.getId() != null)
		    deleteMartyrHonor(martyrHonor, session);
	    }

	    DataAccess.deleteEntity(martyrTransactionData.getMartyrTransaction(), session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    if (e instanceof BusinessException) {
		throw (BusinessException) e;
	    } else {
		throw new BusinessException("error_general");
	    }
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void validateMartyrTransaction(MartyrTransactionData martyrTransactionData, List<MartyrTransactionData> martyrTransactionDataList) throws BusinessException {
	if (martyrTransactionData == null || martyrTransactionData.getMedicalDecisionFlag() == null)
	    throw new BusinessException("error_transactionDataError");
	if (martyrTransactionData.getEmployeeId() == null)
	    throw new BusinessException("error_empSelectionManadatory");

	Date recruitmentDate = EmployeesService.getEmployeeData(martyrTransactionData.getEmployeeId()).getRecruitmentDate();

	if (martyrTransactionData.getMartyrdomType() == null || (martyrTransactionData.getMartyrdomType() != MartyrdomTypesEnum.INJURED.getCode() && martyrTransactionData.getMartyrdomType() != MartyrdomTypesEnum.DEAD.getCode() && martyrTransactionData.getMartyrdomType() != MartyrdomTypesEnum.MARTYR.getCode() && martyrTransactionData.getMartyrdomType() != MartyrdomTypesEnum.MISSING.getCode()))
	    throw new BusinessException("error_martyrdomTypeMandatory");
	if (martyrTransactionData.getMartyrdomReasonId() == null || martyrTransactionData.getMartyrdomReasonId().longValue() == FlagsEnum.ALL.getCode())
	    throw new BusinessException("error_martyrdomReasonMandatory");
	if (martyrTransactionData.getMartyrdomDate() == null)
	    throw new BusinessException("error_martyrdomDateMandatory");
	else if (!HijriDateService.isValidHijriDate(martyrTransactionData.getMartyrdomDate()))
	    throw new BusinessException("error_invalidHijriDate");
	else if (martyrTransactionData.getMartyrdomDate().after(HijriDateService.getHijriSysDate()) || !martyrTransactionData.getMartyrdomDate().after(recruitmentDate))
	    throw new BusinessException("error_invalidMartyrdomDate");
	if (martyrTransactionData.getMartyrdomRegionId() == null || martyrTransactionData.getMartyrdomRegionId().longValue() == FlagsEnum.ALL.getCode())
	    throw new BusinessException("error_martyrdomRegionMandatory");
	if ((martyrTransactionData.getDeathCompensationDate() != null && !HijriDateService.isValidHijriDate(martyrTransactionData.getDeathCompensationDate()))
		|| (martyrTransactionData.getHousingCompensationDate() != null && !HijriDateService.isValidHijriDate(martyrTransactionData.getHousingCompensationDate()))
		|| (martyrTransactionData.getInjuryCompensationDate() != null && !HijriDateService.isValidHijriDate(martyrTransactionData.getInjuryCompensationDate()))
		|| (martyrTransactionData.getRetirementCompensationDate() != null && !HijriDateService.isValidHijriDate(martyrTransactionData.getRetirementCompensationDate()))
		|| (martyrTransactionData.getTerminationCompensationDate() != null && !HijriDateService.isValidHijriDate(martyrTransactionData.getTerminationCompensationDate()))
		|| (martyrTransactionData.getVacationsCompensationDate() != null && !HijriDateService.isValidHijriDate(martyrTransactionData.getVacationsCompensationDate())))
	    throw new BusinessException("error_invalidHijriDate");

	if ((martyrTransactionData.getDeathCompensationDate() != null && (martyrTransactionData.getDeathCompensationDate().after(HijriDateService.getHijriSysDate()) || !martyrTransactionData.getDeathCompensationDate().after(martyrTransactionData.getMartyrdomDate())))
		|| (martyrTransactionData.getHousingCompensationDate() != null && (martyrTransactionData.getHousingCompensationDate().after(HijriDateService.getHijriSysDate()) || !martyrTransactionData.getHousingCompensationDate().after(martyrTransactionData.getMartyrdomDate())))
		|| (martyrTransactionData.getInjuryCompensationDate() != null && (martyrTransactionData.getInjuryCompensationDate().after(HijriDateService.getHijriSysDate()) || !martyrTransactionData.getInjuryCompensationDate().after(martyrTransactionData.getMartyrdomDate())))
		|| (martyrTransactionData.getRetirementCompensationDate() != null && (martyrTransactionData.getRetirementCompensationDate().after(HijriDateService.getHijriSysDate()) || !martyrTransactionData.getRetirementCompensationDate().after(martyrTransactionData.getMartyrdomDate())))
		|| (martyrTransactionData.getTerminationCompensationDate() != null && (martyrTransactionData.getTerminationCompensationDate().after(HijriDateService.getHijriSysDate()) || !martyrTransactionData.getTerminationCompensationDate().after(martyrTransactionData.getMartyrdomDate())))
		|| (martyrTransactionData.getVacationsCompensationDate() != null && (martyrTransactionData.getVacationsCompensationDate().after(HijriDateService.getHijriSysDate()) || !martyrTransactionData.getVacationsCompensationDate().after(martyrTransactionData.getMartyrdomDate()))))
	    throw new BusinessException("error_invalidCompensantionOrHonorDate");

	if (martyrTransactionData.getRetirementCompensationNumber() != null && martyrTransactionData.getRetirementCompensationNumber().isEmpty())
	    martyrTransactionData.setRetirementCompensationNumber(null);
	if (martyrTransactionData.getVacationsCompensationNumber() != null && martyrTransactionData.getVacationsCompensationNumber().isEmpty())
	    martyrTransactionData.setVacationsCompensationNumber(null);
	if (martyrTransactionData.getHousingCompensationNumber() != null && martyrTransactionData.getHousingCompensationNumber().isEmpty())
	    martyrTransactionData.setHousingCompensationNumber(null);
	if (martyrTransactionData.getTerminationCompensationNumber() != null && martyrTransactionData.getTerminationCompensationNumber().isEmpty())
	    martyrTransactionData.setTerminationCompensationNumber(null);
	if (martyrTransactionData.getInjuryCompensationNumber() != null && martyrTransactionData.getInjuryCompensationNumber().isEmpty())
	    martyrTransactionData.setInjuryCompensationNumber(null);
	if (martyrTransactionData.getDeathCompensationNumber() != null && martyrTransactionData.getDeathCompensationNumber().isEmpty())
	    martyrTransactionData.setDeathCompensationNumber(null);

	if ((martyrTransactionData.getRetirementCompensationNumber() == null && martyrTransactionData.getRetirementCompensationDate() != null) || (martyrTransactionData.getRetirementCompensationNumber() != null && martyrTransactionData.getRetirementCompensationDate() == null)
		|| (martyrTransactionData.getVacationsCompensationNumber() == null && martyrTransactionData.getVacationsCompensationDate() != null) || (martyrTransactionData.getVacationsCompensationNumber() != null && martyrTransactionData.getVacationsCompensationDate() == null)
		|| (martyrTransactionData.getHousingCompensationNumber() == null && martyrTransactionData.getHousingCompensationDate() != null) || (martyrTransactionData.getHousingCompensationNumber() != null && martyrTransactionData.getHousingCompensationDate() == null)
		|| (martyrTransactionData.getTerminationCompensationNumber() == null && martyrTransactionData.getTerminationCompensationDate() != null) || (martyrTransactionData.getTerminationCompensationNumber() != null && martyrTransactionData.getTerminationCompensationDate() == null)
		|| (martyrTransactionData.getInjuryCompensationNumber() == null && martyrTransactionData.getInjuryCompensationDate() != null) || (martyrTransactionData.getInjuryCompensationNumber() != null && martyrTransactionData.getInjuryCompensationDate() == null)
		|| (martyrTransactionData.getDeathCompensationNumber() == null && martyrTransactionData.getDeathCompensationDate() != null) || (martyrTransactionData.getDeathCompensationNumber() != null && martyrTransactionData.getDeathCompensationDate() == null))
	    throw new BusinessException("error_martyrCompansationMustBeComplete");

	if (martyrTransactionData.getMartyrdomType() == MartyrdomTypesEnum.INJURED.getCode() && martyrTransactionData.getInjuryCompensation() != null && (martyrTransactionData.getInjuryCompensation() < 1 || martyrTransactionData.getInjuryCompensation() > 100))
	    throw new BusinessException("error_martyrInjuryCompansationMustBePercentage");

	// TODO : CHANGE MESSAGES
	if (martyrTransactionData.getMartyrdomType() == MartyrdomTypesEnum.INJURED.getCode() && (martyrTransactionData.getDeathCompensationNumber() != null || martyrTransactionData.getDeathCompensationDate() != null))
	    throw new BusinessException("error_martyrInjuryCompansationDataMandatory");
	if (martyrTransactionData.getMartyrdomType() != MartyrdomTypesEnum.INJURED.getCode() && (martyrTransactionData.getInjuryCompensation() != null || martyrTransactionData.getInjuryCompensationNumber() != null || martyrTransactionData.getInjuryCompensationDate() != null))
	    throw new BusinessException("error_martyrDeathCompansationDataMandatory");

	for (MartyrTransactionData martyrTransaction : martyrTransactionDataList) {
	    if (!martyrTransaction.getId().equals(martyrTransactionData.getId()) &&
		    HijriDateService.hijriDateDiff(martyrTransaction.getMartyrdomDate(), martyrTransactionData.getMartyrdomDate()) == 0) {
		throw new BusinessException("error_dublictInjuryDate");
	    }
	    if (martyrTransaction.getMartyrdomType() != MartyrdomTypesEnum.INJURED.getCode() && !martyrTransaction.getId().equals(martyrTransactionData.getId())
		    && HijriDateService.hijriDateDiff(martyrTransaction.getMartyrdomDate(), martyrTransactionData.getMartyrdomDate()) >= 0) {
		throw new BusinessException("error_injuryDateGreaterThanMartyrDate");
	    }
	    if (martyrTransactionData.getMartyrdomType() != MartyrdomTypesEnum.INJURED.getCode()) {
		if (martyrTransaction.getMartyrdomType() == MartyrdomTypesEnum.INJURED.getCode() && !martyrTransaction.getId().equals(martyrTransactionData.getId())
			&& HijriDateService.hijriDateDiff(martyrTransaction.getMartyrdomDate(), martyrTransactionData.getMartyrdomDate()) <= 0) {
		    throw new BusinessException("error_martyrDateLessThanInjuredDate");
		}
	    }
	}
    }

    public static List<MartyrTransactionData> getMartyrTransactionByEmployeeId(long empId) throws BusinessException {
	return searchMartyrTransactions(empId, FlagsEnum.ALL.getCode());
    }

    private static List<MartyrTransactionData> searchMartyrTransactions(long empId, long reasonId) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_REASON_ID", reasonId);
	    return DataAccess.executeNamedQuery(MartyrTransactionData.class, QueryNamesEnum.HCM_SEARCH_MARTYR_TRANSACTION_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*-----------------------------------------------------Martyrs Honors operations---------------------------------------------*/
    private static void addMartyrHonor(MartyrHonor martyrHonor, Date martyrdomDate, CustomSession... useSession) throws BusinessException {
	validateMartyrHonor(martyrHonor, martyrdomDate);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(martyrHonor, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    martyrHonor.setId(null);
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void deleteMartyrHonor(MartyrHonor martyrHonor, CustomSession... useSession) throws BusinessException {
	if (martyrHonor == null || martyrHonor.getId() == null)
	    throw new BusinessException("error_transactionDataError");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(martyrHonor, session);

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

    private static void validateMartyrHonor(MartyrHonor martyrHonor, Date martyrTransactionDate) throws BusinessException {
	if (martyrHonor == null || martyrHonor.getMartyrTransactionId() == null)
	    throw new BusinessException("error_transactionDataError");
	if (martyrHonor.getHonorType() == null)
	    throw new BusinessException("error_martyrHonorTypeMandatory");
	if (martyrHonor.getHonorNumber() == null || martyrHonor.getHonorNumber().length() == 0)
	    throw new BusinessException("error_martyrHonorNumberMandatory");
	if (martyrHonor.getHonorDate() == null)
	    throw new BusinessException("error_martyrHonorDateMandatory");
	else if (!HijriDateService.isValidHijriDate(martyrHonor.getHonorDate()))
	    throw new BusinessException("error_invalidHijriDate");
	else if (martyrHonor.getHonorDate().after(HijriDateService.getHijriSysDate()) || !martyrHonor.getHonorDate().after(martyrTransactionDate)) {
	    throw new BusinessException("error_invalidCompensantionOrHonorDate");
	}
	if (martyrHonor.getHonorDescription() == null || martyrHonor.getHonorDescription().length() == 0)
	    throw new BusinessException("error_martyrHonorDescriptionMandatory");
    }

    public static List<MartyrHonor> getMartyrHonors(long martyrTransactionId, long martyrHonorType) throws BusinessException {
	return searchMartyrHonors(martyrTransactionId, martyrHonorType);
    }

    private static List<MartyrHonor> searchMartyrHonors(long martyrTransactionId, long martyrHonorType) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MARTYR_TRANSACTION_ID", martyrTransactionId);
	    qParams.put("P_HONOR_TYPE", martyrHonorType);
	    return DataAccess.executeNamedQuery(MartyrHonor.class, QueryNamesEnum.HCM_SEARCH_MARTYR_HONORS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*-----------------------------------------------------Martyrs Reasons operations---------------------------------------------*/
    public static void addMartyrdomReason(MartyrdomReason martyrdomReason, CustomSession... useSession) throws BusinessException {
	validateMartyrdomReason(martyrdomReason);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(martyrdomReason, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    martyrdomReason.setId(null);
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateMartyrdomReason(MartyrdomReason martyrdomReason, CustomSession... useSession) throws BusinessException {
	validateMartyrdomReason(martyrdomReason);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(martyrdomReason, session);

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

    public static void deleteMartyrdomReason(MartyrdomReason martyrdomReason, CustomSession... useSession) throws BusinessException {
	if (martyrdomReason == null || martyrdomReason.getId() == null)
	    throw new BusinessException("error_transactionDataError");
	if (checkUsageOfMartyrdomReason(martyrdomReason))
	    throw new BusinessException("error_martyrdomReasonIsUsedBymartyrTransaction");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(martyrdomReason, session);

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

    private static void validateMartyrdomReason(MartyrdomReason martyrdomReason) throws BusinessException {
	if (martyrdomReason == null)
	    throw new BusinessException("error_transactionDataError");
	if (martyrdomReason.getId() != null && checkUsageOfMartyrdomReason(martyrdomReason))
	    throw new BusinessException("error_martyrdomReasonIsUsedBymartyrTransaction");
	if (martyrdomReason.getDescription() == null || martyrdomReason.getDescription().length() == 0)
	    throw new BusinessException("error_martyrdomReasonIdMandatory");
	if (checkMartyrdomReasonsDescriptionRepeated(martyrdomReason.getDescription(), martyrdomReason.getId()))
	    throw new BusinessException("error_martyrdomReasonDescriptionRepeated");
    }

    private static boolean checkUsageOfMartyrdomReason(MartyrdomReason martyrdomReason) throws BusinessException {
	return searchMartyrTransactions(FlagsEnum.ALL.getCode(), martyrdomReason.getId()).size() > 0;
    }

    public static List<MartyrdomReason> getMartyrdomReasons() throws BusinessException {
	return searchMartyrdomReasons(null, FlagsEnum.ALL.getCode());
    }

    private static boolean checkMartyrdomReasonsDescriptionRepeated(String description, Long execludedReasonId) throws BusinessException {
	return searchMartyrdomReasons(description, execludedReasonId == null ? FlagsEnum.ALL.getCode() : execludedReasonId).size() > 0;
    }

    private static List<MartyrdomReason> searchMartyrdomReasons(String description, long execludedReasonId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_DESCRIPTION", (description == null || description.equals("") || description.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : description);
	    qParams.put("P_EXECLUDED_REASON_ID", execludedReasonId);
	    return DataAccess.executeNamedQuery(MartyrdomReason.class, QueryNamesEnum.HCM_SEARCH_MARTYRDOM_REASONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*-----------------------------------------------------Martyrs Reports---------------------------------------------*/
    public static byte[] getMartyrsDataBytes(Long employeeId, int martyrdomTypeFlag, long martyrReasonId, long martyrRegionId, long categoryId, long martyrRankId, Date martyrDateFrom, Date martyrDateTo, int medicalDecisionFlag, int terminationDecisionFlag, int retirementCompensationFlag, int vacationsCompensationFlag, int terminationCompensationFlag, int housingCompensationFlag, int deathCompensationFlag, int injuryCompensationFlag, int heirsCompensationFlag, Long injuryCompensationFrom,
	    Long injuryCompensationTo) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.MARTYRS_DATA.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("P_EMPLOYEE_ID", employeeId == null ? FlagsEnum.ALL.getCode() : employeeId);
	    parameters.put("P_MARTYRDOM_TYPE", martyrdomTypeFlag);
	    parameters.put("P_MARTYRDOM_REGION_ID", martyrRegionId);
	    parameters.put("P_MARTYRDOM_REASON_ID", martyrReasonId);
	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_RANK_ID", martyrRankId);

	    if (martyrDateFrom != null) {
		parameters.put("P_MARTYRDOM_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		parameters.put("P_MARTYRDOM_DATE_FROM", HijriDateService.getHijriDateString(martyrDateFrom));
	    } else {
		parameters.put("P_MARTYRDOM_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		parameters.put("P_MARTYRDOM_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }

	    if (martyrDateTo != null) {
		parameters.put("P_MARTYRDOM_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		parameters.put("P_MARTYRDOM_DATE_TO", HijriDateService.getHijriDateString(martyrDateTo));
	    } else {
		parameters.put("P_MARTYRDOM_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		parameters.put("P_MARTYRDOM_DATE_TO", HijriDateService.getHijriSysDateString());
	    }

	    parameters.put("P_MEDICAL_DECISION_FLAG", medicalDecisionFlag);
	    parameters.put("P_EMP_STATUS_FLAG", terminationDecisionFlag);
	    parameters.put("P_RETIREMENT_COMPENSATION_FLAG", retirementCompensationFlag);
	    parameters.put("P_VACATIONS_COMPENSATION_FLAG", vacationsCompensationFlag);
	    parameters.put("P_TERMINATION_COMPENSATION_FLAG", terminationCompensationFlag);
	    parameters.put("P_HOUSING_COMPENSATION_FLAG", housingCompensationFlag);
	    parameters.put("P_DEATH_COMPENSATION_FLAG", deathCompensationFlag);
	    parameters.put("P_INJURY_COMPENSATION_FLAG", injuryCompensationFlag);
	    parameters.put("P_HEIRS_COMPENSATION_FLAG", heirsCompensationFlag);
	    parameters.put("P_INJURY_COMPENSATION_FROM", injuryCompensationFrom);
	    parameters.put("P_INJURY_COMPENSATION_TO", injuryCompensationTo);
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getMartyrFileBytes(long employeeId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("P_EMP_ID", employeeId);
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());

	    return getReportData(ReportNamesEnum.MARTYR_FILE.getCode(), parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }
}
