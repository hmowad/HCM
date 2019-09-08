package com.code.services.hcm;

import java.util.Date;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.dal.orm.hcm.vacations.VacationConfiguration;
import com.code.dal.orm.hcm.vacations.VacationLog;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.PaidVacationTypesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.BaseService;
import com.code.services.cor.ETRCorrespondence;
import com.code.services.util.HijriDateService;

/**
 * The class <code>VacationsDataHandlingService</code> provides methods for manipulating vacations related data.
 */
public class VacationsDataHandlingService extends BaseService {

    /**
     * Constructs a newly allocated <code>VacationsDataHandlingService</code> object.
     */
    private VacationsDataHandlingService() {
    }

    /*---------------------------------------------------------- Vacation ------------------------------------------------------*/

    /**
     * Inserts a vacation transaction after the request has been approved.
     * 
     * @param vacation
     *            the {@link Vacation} transaction that will be inserted at the DB
     * @param paidVacationType
     *            the paid vacation type {@link PaidVacationTypesEnum}
     * @param vacationBeneficiary
     *            the {@link EmployeeData} represents the vacation beneficiary
     * @param externalCopies
     *            a <code>String</code> contains the external copies
     * @param subject
     *            a <code>String</code> contains the workflow process name
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs like a database error
     * 
     * @see VacationTypesEnum
     * @see CategoriesEnum
     */
    protected static void insertVacData(Vacation vacation, EmployeeData vacationBeneficiary, Integer skipWFFlag, String subject, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    vacation.setStatus(RequestTypesEnum.NEW.getCode());
	    vacation.setTransactionEmpRankDesc(vacationBeneficiary.getRankDesc() + (vacationBeneficiary.getRankTitleDesc() != null ? " " + vacationBeneficiary.getRankTitleDesc() : ""));
	    vacation.setTransactionEmpJobCode(vacationBeneficiary.getJobCode());
	    vacation.setTransactionEmpJobDesc(vacationBeneficiary.getJobDesc());
	    vacation.setTransactionEmpUnitDesc(vacationBeneficiary.getPhysicalUnitFullName());
	    vacation.setTransactionEmpCategoryId(vacationBeneficiary.getCategoryId());

	    if (skipWFFlag == FlagsEnum.ON.getCode()) {
		vacation.setEtrFlag(FlagsEnum.OFF.getCode());
		vacation.setMigrationFlag(FlagsEnum.ON.getCode());
		vacation.setExceededDays(FlagsEnum.OFF.getCode());
		vacation.setJoiningDate(HijriDateService.addSubHijriDays(vacation.getEndDate(), 1));

	    } else {
		String[] etrCorInfo = ETRCorrespondence.doETRCorOut(subject, session);
		vacation.setDecisionNumber(etrCorInfo[0]);
		vacation.setDecisionDateString(etrCorInfo[1]);
		EmployeeData approvedEmp = EmployeesService.getEmployeeData(vacation.getApprovedId());
		vacation.setTransactionApprovedRankDesc(approvedEmp.getRankDesc());
		vacation.setTransactionApprovedJobDesc(approvedEmp.getJobDesc());
		vacation.setEtrFlag(FlagsEnum.ON.getCode());
		vacation.setMigrationFlag(FlagsEnum.OFF.getCode());
		vacation.setExternalCopies(handleExternalCopies(vacation.getDecisionRegionId(), vacation.getTransactionEmpCategoryId(), vacation.getVacationTypeId(), vacation.getTransactionApprovedJobDesc(), vacation.getApprovedId(), vacation.getOriginalDecisionApprovedId()));

	    }

	    DataAccess.addEntity(vacation, session);

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

    /**
     * Modifies a vacation transaction after the request has been approved.
     * 
     * @param modifiedOldVacation
     *            the {@link Vacation} transaction that will be modified at the DB
     * @param externalCopies
     *            a <code>String</code> contains the external copies
     * @param subject
     *            a <code>String</code> contains the workflow process name
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs like a database error
     */
    protected static void modifyVacData(Vacation request, EmployeeData vacationBeneficiary, Integer skipWFFlag, String subject, CustomSession... useSession) throws BusinessException {

	Vacation originalVacation = VacationsService.getVacationById(request.getVacationId());

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (originalVacation.getStatus().equals(RequestTypesEnum.NEW.getCode())) {
		logVacationData(originalVacation, session);
	    }

	    originalVacation.setStatus(RequestTypesEnum.MODIFY.getCode());

	    originalVacation.setPeriod(request.getPeriod());
	    originalVacation.setEndDateString(request.getEndDateString());
	    originalVacation.setApprovedId(request.getApprovedId());

	    originalVacation.setTransactionEmpRankDesc(vacationBeneficiary.getRankDesc() + (vacationBeneficiary.getRankTitleDesc() != null ? " " + vacationBeneficiary.getRankTitleDesc() : ""));
	    originalVacation.setTransactionEmpJobCode(vacationBeneficiary.getJobCode());
	    originalVacation.setTransactionEmpJobDesc(vacationBeneficiary.getJobDesc());
	    originalVacation.setTransactionEmpUnitDesc(vacationBeneficiary.getPhysicalUnitFullName());
	    originalVacation.setTransactionEmpCategoryId(vacationBeneficiary.getCategoryId());

	    originalVacation.setDecisionApprovedId(request.getDecisionApprovedId());
	    originalVacation.setOriginalDecisionApprovedId(request.getOriginalDecisionApprovedId());
	    originalVacation.setDecisionRegionId(request.getDecisionRegionId());

	    if (request.getLocationFlag() == LocationFlagsEnum.INTERNAL_EXTERNAL.getCode()) {
		originalVacation.setExtLocation(request.getExtLocation());
		originalVacation.setExtStartDateString(request.getExtStartDateString());
		originalVacation.setExtEndDateString(request.getExtEndDateString());
		originalVacation.setExtPeriod(request.getExtPeriod());
		originalVacation.setLocationFlag(request.getLocationFlag());
	    }

	    if (vacationBeneficiary.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode()
		    && request.getVacationTypeId() == VacationTypesEnum.REGULAR.getCode()) {

		originalVacation.setContractualYearStartDateString(request.getContractualYearStartDateString());
		originalVacation.setContractualYearEndDateString(request.getContractualYearEndDateString());
	    }

	    originalVacation.setAttachments(request.getAttachments());
	    originalVacation.setReferring(request.getReferring());

	    if (skipWFFlag == FlagsEnum.ON.getCode()) {
		originalVacation.setDecisionNumber(request.getDecisionNumber());
		originalVacation.setDecisionDate(request.getDecisionDate());
		originalVacation.setExceededDays(FlagsEnum.OFF.getCode());
		originalVacation.setJoiningDate(HijriDateService.addSubHijriDays(originalVacation.getEndDate(), 1));

	    } else {
		String[] etrCorInfo = ETRCorrespondence.doETRCorOut(subject, session);
		originalVacation.setDecisionNumber(etrCorInfo[0]);
		originalVacation.setDecisionDateString(etrCorInfo[1]);
		EmployeeData approvedEmp = EmployeesService.getEmployeeData(request.getApprovedId());
		originalVacation.setTransactionApprovedRankDesc(approvedEmp.getRankDesc());
		originalVacation.setTransactionApprovedJobDesc(approvedEmp.getJobDesc());
		originalVacation.setExternalCopies(handleExternalCopies(originalVacation.getDecisionRegionId(), originalVacation.getTransactionEmpCategoryId(), originalVacation.getVacationTypeId(), originalVacation.getTransactionApprovedJobDesc(), originalVacation.getApprovedId(), originalVacation.getOriginalDecisionApprovedId()));
	    }

	    DataAccess.updateEntity(originalVacation, session);
	    logVacationData(originalVacation, session);

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

    /**
     * Cancels a vacation transaction after the request has been approved.
     * 
     * @param modifiedOldVacation
     *            the {@link Vacation} transaction that will be canceled at the DB
     * @param externalCopies
     *            a <code>String</code> contains the external copies
     * @param subject
     *            a <code>String</code> contains the workflow process name
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs like a database error
     */
    protected static void cancelVacData(Vacation request, EmployeeData vacationBeneficiary, Integer skipWFFlag, String subject, CustomSession... useSession) throws BusinessException {

	Vacation originalVacation = VacationsService.getVacationById(request.getVacationId());

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (originalVacation.getStatus().equals(RequestTypesEnum.NEW.getCode())) {
		logVacationData(originalVacation, session);
	    }

	    originalVacation.setStatus(RequestTypesEnum.CANCEL.getCode());

	    originalVacation.setApprovedId(request.getApprovedId());

	    originalVacation.setTransactionEmpRankDesc(vacationBeneficiary.getRankDesc() + (vacationBeneficiary.getRankTitleDesc() != null ? " " + vacationBeneficiary.getRankTitleDesc() : ""));
	    originalVacation.setTransactionEmpJobCode(vacationBeneficiary.getJobCode());
	    originalVacation.setTransactionEmpJobDesc(vacationBeneficiary.getJobDesc());
	    originalVacation.setTransactionEmpUnitDesc(vacationBeneficiary.getPhysicalUnitFullName());
	    originalVacation.setTransactionEmpCategoryId(vacationBeneficiary.getCategoryId());

	    originalVacation.setDecisionApprovedId(request.getDecisionApprovedId());
	    originalVacation.setOriginalDecisionApprovedId(request.getOriginalDecisionApprovedId());
	    originalVacation.setDecisionRegionId(request.getDecisionRegionId());

	    originalVacation.setAttachments(request.getAttachments());
	    originalVacation.setReferring(request.getReferring());
	    if (skipWFFlag == FlagsEnum.ON.getCode()) {
		originalVacation.setDecisionNumber(request.getDecisionNumber());
		originalVacation.setDecisionDate(request.getDecisionDate());
		originalVacation.setExceededDays(null);
		originalVacation.setJoiningDate(null);

	    } else {
		String[] etrCorInfo = ETRCorrespondence.doETRCorOut(subject, session);
		originalVacation.setDecisionNumber(etrCorInfo[0]);
		originalVacation.setDecisionDateString(etrCorInfo[1]);
		EmployeeData approvedEmp = EmployeesService.getEmployeeData(request.getApprovedId());
		originalVacation.setTransactionApprovedRankDesc(approvedEmp.getRankDesc());
		originalVacation.setTransactionApprovedJobDesc(approvedEmp.getJobDesc());
		originalVacation.setExternalCopies(handleExternalCopies(originalVacation.getDecisionRegionId(), originalVacation.getTransactionEmpCategoryId(), originalVacation.getVacationTypeId(), originalVacation.getTransactionApprovedJobDesc(), originalVacation.getApprovedId(), originalVacation.getOriginalDecisionApprovedId()));

	    }

	    DataAccess.updateEntity(originalVacation, session);
	    logVacationData(originalVacation, session);

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

    /**
     * Gets the correct external copies depending on the category and vacation type to be printed to the vacation decision.
     * 
     * @param regionId
     *            the physical region of the vacation beneficiary
     * @param categoryId
     *            the category of the vacation beneficiary
     * @param vacationTypeId
     *            the vacation type of the approved vacation
     * @param transactionApprovedJobDesc
     *            a <code>String</code> containing the job description of the employee who approved the vacation request
     * @return a <code>String</code> containing the external copies to be printed to the vacation decision
     * 
     * @see RegionsEnum
     * @see CategoriesEnum
     * @see VacationTypesEnum
     */
    private static String handleExternalCopies(long regionId, long categoryId, long vacationTypeId, String transactionApprovedJobDesc, long approvedId, long originalDecisionApprovedId) {
	String externalCopies = "";
	if (regionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
	    if (CategoriesEnum.OFFICERS.getCode() == categoryId) {
		if (VacationTypesEnum.COMPELLING.getCode() == vacationTypeId)
		    externalCopies = getMessage("label_officersCompellingVacationCopies").replace("P_JOB_APP", transactionApprovedJobDesc);
		else
		    externalCopies = getMessage("label_officersVacationCopies").replace("P_JOB_APP", transactionApprovedJobDesc);

		if (approvedId == originalDecisionApprovedId)
		    externalCopies = externalCopies.substring(0, externalCopies.lastIndexOf('\n'));

	    } else if (CategoriesEnum.SOLDIERS.getCode() == categoryId) {
		if (VacationTypesEnum.SICK.getCode() == vacationTypeId)
		    externalCopies = getMessage("label_soldiersSickVacationCopies");
		else
		    externalCopies = getMessage("label_soldiersVacationCopies");

	    } else if (CategoriesEnum.CONTRACTORS.getCode() == categoryId) {
		externalCopies = getMessage("label_contractorsVacationCopies");
	    } else {
		externalCopies = getMessage("label_employeesVacationCopies");
	    }
	}
	return externalCopies;
    }

    /**
     * Keeps the history of vacations transactions.
     * 
     * @param oldVac
     *            the {@link Vacation} data to be logged to the database.
     * @param session
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    private static void logVacationData(Vacation oldVac, CustomSession session) throws BusinessException {
	try {
	    VacationLog vacLog = new VacationLog();

	    vacLog.setVacationId(oldVac.getVacationId());
	    vacLog.setEndDateString(oldVac.getEndDateString());
	    vacLog.setPeriod(oldVac.getPeriod());
	    vacLog.setDecisionNumber(oldVac.getDecisionNumber());
	    vacLog.setDecisionDateString(oldVac.getDecisionDateString());
	    vacLog.setStatus(oldVac.getStatus());

	    vacLog.setLocationFlag(oldVac.getLocationFlag());
	    vacLog.setExtLocation(oldVac.getExtLocation());
	    vacLog.setExtStartDateString(oldVac.getExtStartDateString());
	    vacLog.setExtEndDateString(oldVac.getExtEndDateString());
	    vacLog.setExtPeriod(oldVac.getExtPeriod());

	    vacLog.setApprovedId(oldVac.getApprovedId());
	    vacLog.setTransactionApprovedRankDesc(oldVac.getTransactionApprovedRankDesc());
	    vacLog.setTransactionApprovedJobDesc(oldVac.getTransactionApprovedJobDesc());

	    vacLog.setDecisionApprovedId(oldVac.getDecisionApprovedId());
	    vacLog.setOriginalDecisionApprovedId(oldVac.getOriginalDecisionApprovedId());
	    vacLog.setDecisionRegionId(oldVac.getDecisionRegionId());

	    vacLog.setTransactionEmpRankDesc(oldVac.getTransactionEmpRankDesc());
	    vacLog.setTransactionEmpJobCode(oldVac.getTransactionEmpJobCode());
	    vacLog.setTransactionEmpJobDesc(oldVac.getTransactionEmpJobDesc());
	    vacLog.setTransactionEmpUnitDesc(oldVac.getTransactionEmpUnitDesc());
	    vacLog.setTransactionEmpCategoryId(oldVac.getTransactionEmpCategoryId());

	    vacLog.setDecisionData(oldVac.getDecisionData());
	    vacLog.setContractualYearStartDateString(oldVac.getContractualYearStartDateString());
	    vacLog.setContractualYearEndDateString(oldVac.getContractualYearEndDateString());

	    vacLog.setExternalCopies(oldVac.getExternalCopies());
	    vacLog.setReferring(oldVac.getReferring());

	    DataAccess.addEntity(vacLog, session);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Updates the vacation joining date.
     * 
     * @param vacationTransactionId
     *            the vacation transaction ID to be updated
     * @param joiningDate
     *            the new date to be set to the vacation transaction in mm/MM/yyyy format
     * @param userId
     *            a <code>long</code> containing the user ID who makes the transaction. This ID is used for auditing.
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    protected static void updateVacationJoiningDate(long vacationTransactionId, Date joiningDate, long userId, CustomSession... useSession) throws BusinessException {

	Vacation vacation = VacationsService.getVacationById(vacationTransactionId);

	if (joiningDate == null || !HijriDateService.isValidHijriDate(joiningDate))
	    throw new BusinessException("error_invalidHijriDate");

	if (!joiningDate.after(vacation.getEndDate()) || joiningDate.after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_invalidJoiningDate");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    vacation.setJoiningDate(joiningDate);
	    vacation.setExceededDays(HijriDateService.hijriDateDiff(vacation.getEndDate(), joiningDate) - 1);
	    vacation.setSystemUser(userId + ""); // For Auditing.

	    DataAccess.updateEntity(vacation, session);

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

    /*----------------------------------------------------- Vacation Configuration --------------------------------------*/

    protected static void modifyVacationConfiguration(VacationConfiguration vacationConfiguration, VacationConfiguration originalVacationConfiguration, CustomSession... useSession) throws BusinessException {

	VacationsBusinessRulesService.validateVacationConfiguration(vacationConfiguration, originalVacationConfiguration);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(vacationConfiguration, session);

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
}
