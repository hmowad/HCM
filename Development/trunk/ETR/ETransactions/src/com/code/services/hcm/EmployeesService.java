package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.ws.WebServiceException;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.SocialIdIssuePlace;
import com.code.dal.orm.hcm.employees.Employee;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeExtraTransaction;
import com.code.dal.orm.hcm.employees.EmployeeExtraTransactionData;
import com.code.dal.orm.hcm.employees.EmployeePhoto;
import com.code.dal.orm.hcm.employees.EmployeeQualificationsData;
import com.code.dal.orm.hcm.employees.medicalstuff.EmployeeMedicalStaffData;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.organization.units.UnitTransaction;
import com.code.dal.orm.log.EmployeeLog;
import com.code.dal.orm.setup.Country;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoriesEnum;
import com.code.enums.CountriesEnum;
import com.code.enums.DegreesEnum;
import com.code.enums.EmployeePhotoStatusEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.WFProcessesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com.BusinessException_Exception;
import com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com.DataValidationException;
import com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com.IdType;
import com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com.PersonInfoDetailedResult;
import com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com.PersonInfoRequest;
import com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com.Yakeen4BorderGuardFaultException;
import com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com.Yakeen4BorderGuardService;
import com.code.integration.webservicesclients.yaqeenclient.yaqeen.ejada.com.YaqeenServices;
import com.code.services.BaseService;
import com.code.services.buswfcoop.BusinessWorkflowCooperation;
import com.code.services.config.ETRConfigurationService;
import com.code.services.integration.PayrollEngineService;
import com.code.services.log.LogService;
import com.code.services.util.CommonService;
import com.code.services.util.CountryService;
import com.code.services.util.HijriDateService;

public class EmployeesService extends BaseService {
    private EmployeesService() {
    }

    // ----------------------- EmployeeData------------------------------------
    public static void addEmployee(EmployeeData empData, EmployeeQualificationsData employeeQualificationsData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    addEmployee(empData, session);

	    employeeQualificationsData.setEmployeeId(empData.getEmpId());

	    addEmployeeQualifications(employeeQualificationsData, empData, session);

	    EmployeeLog log = new EmployeeLog.Builder().setRankId(empData.getRankId()).setSocialStatus(empData.getSocialStatus()).setGeneralSpecialization(empData.getGeneralSpecialization()).setDegreeId(DegreesEnum.FIRST.getCode())
		    .constructCommonFields(empData.getEmpId(), FlagsEnum.ON.getCode(), null, null, HijriDateService.getHijriSysDate(), DataAccess.getTableName(Employee.class)).build();
	    LogService.logEmployeeData(log, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    empData.setEmpId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession) {
		session.close();
		if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()) && empData.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		    doPayrollIntegration(empData.getEmpId(), FlagsEnum.OFF.getCode());
		}
	    }
	}
    }

    private static void addEmployee(EmployeeData empData, CustomSession... useSession) throws BusinessException {
	validateEmployee(empData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (EmployeesService.checkIfSoldiersDistributionNeedsToBeDeleted(empData))
		EmployeesService.deleteSoldiersDistribution(null, session);

	    if (empData.getExceptionalRecruitmentFlag() == null)
		empData.setExceptionalRecruitmentFlag(FlagsEnum.OFF.getCode());

	    if (empData.getRankId() == null && empData.getStatusId() == null) {
		if (empData.getCategoryId().intValue() == CategoriesEnum.OFFICERS.getCode()) {
		    Rank rank = CommonService.getRankById(RanksEnum.CADET.getCode());
		    empData.setRankId(rank.getId());
		    empData.setRankDesc(rank.getDescription());
		    empData.setStatusId(EmployeeStatusEnum.UNDER_RECRUITMENT.getCode());

		} else if (empData.getCategoryId().intValue() == CategoriesEnum.SOLDIERS.getCode()) {
		    Rank rank = CommonService.getRankById(RanksEnum.STUDENT_SOLDIER.getCode());
		    empData.setRankId(rank.getId());
		    empData.setRankDesc(rank.getDescription());
		    if (empData.getRecruitmentRankId().longValue() == RanksEnum.SOLDIER.getCode() || empData.getRecruitmentRankId().longValue() == RanksEnum.FIRST_SOLDIER.getCode())
			empData.setStatusId(EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode());
		    else
			empData.setStatusId(EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode());
		} else {
		    empData.setStatusId(EmployeeStatusEnum.UNDER_RECRUITMENT.getCode());
		}
	    }
	    if (empData.getCategoryId().longValue() != CategoriesEnum.CONTRACTORS.getCode())
		empData.setCountryId(CountryService.getCountryByCode(CountriesEnum.SAUDI_ARABIA.getCode()).getId());

	    empData.setMovementBlacklistFlag(FlagsEnum.OFF.getCode());

	    DataAccess.addEntity(empData.getEmployee(), session);
	    empData.setEmpId(empData.getEmployee().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    empData.setEmpId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateEmployee(EmployeeData empData, CustomSession... useSession) throws BusinessException {
	validateEmployee(empData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (EmployeesService.checkIfSoldiersDistributionNeedsToBeDeleted(empData))
		EmployeesService.deleteSoldiersDistribution(empData, session);

	    DataAccess.updateEntity(empData.getEmployee(), session);

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

    public static void updateEmployeeAndHisQualifications(EmployeeData empData, EmployeeQualificationsData employeeQualificationsData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    updateEmployee(empData, session);
	    updateEmployeeQualifications(employeeQualificationsData, empData.getBirthDate(), empData.getStatusId(), session);

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

    public static void updateEmployeePromotionData(EmployeeData empData, Long jobId, Long rankId, Long rankTitleId, Long degreeId, Integer militaryNumber, Date promotionDueDate, Date lastPromotionDate, CustomSession... useSession) throws BusinessException {

	if (rankId != null)
	    empData.setRankId(rankId);

	if (rankTitleId != null)
	    empData.setRankTitleId(rankTitleId);

	if (degreeId != null)
	    empData.setDegreeId(degreeId);

	if (militaryNumber != null)
	    empData.setMilitaryNumber(militaryNumber);

	if (promotionDueDate != null)
	    empData.setPromotionDueDate(promotionDueDate);

	if (lastPromotionDate != null)
	    empData.setLastPromotionDate(lastPromotionDate);

	if (jobId != null)
	    empData.setJobId(jobId);

	updateEmployee(empData, useSession);
    }

    public static void moveAllEmployeesFromUnitsToUnit(Long[] unitsIds, Long toUnitId, List<JobData> jobs, Date effectiveHijriDate, String decisionNumber, Date decisionDate, CustomSession... useSession) throws BusinessException {
	List<EmployeeData> unitsEmployees = getEmployeesByUnitsIds(unitsIds);
	if (unitsEmployees.size() == 0)
	    return;

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    logMergedUnitsEmployees(unitsIds, toUnitId, jobs, unitsEmployees, HijriDateService.getHijriSysDate(), decisionNumber, decisionDate, session);
	    for (EmployeeData emp : unitsEmployees) {
		emp.setPhysicalUnitId(toUnitId);
		DataAccess.updateEntity(emp.getEmployee(), session);
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

    public static void logMergedUnitsEmployees(Long[] mergedUnitsIds, Long toUnitId, List<JobData> jobs, List<EmployeeData> mergedUnitsEmployees, Date effectiveHijriDate, String decisionNumber, Date decisionDate, CustomSession... useSession) throws BusinessException {
	Map<Long, EmployeeData> empMap = new HashMap<>();
	Set<Long> unitsSet = new HashSet<>();
	for (EmployeeData empData : mergedUnitsEmployees) {
	    empMap.put(empData.getEmpId(), empData);
	}
	for (JobData job : jobs) {
	    if (job.getEmployeeId() != null && !empMap.containsKey(job.getEmployeeId())) {
		empMap.put(job.getEmployeeId(), null);
	    }
	}
	for (Long unitId : mergedUnitsIds) {
	    unitsSet.add(unitId);
	}
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    Iterator<Entry<Long, EmployeeData>> it = empMap.entrySet().iterator();
	    while (it.hasNext()) {
		Map.Entry<Long, EmployeeData> empEntry = it.next();
		EmployeeLog employeeLog = new EmployeeLog.Builder()
			.setPhysicalUnitId(empEntry.getValue() != null && unitsSet.contains(empEntry.getValue().getPhysicalUnitId()) ? toUnitId : null)
			.setOfficialUnitId(empEntry.getValue() == null || unitsSet.contains(empEntry.getValue().getOfficialUnitId()) ? toUnitId : null)
			.constructCommonFields(empEntry.getKey(), FlagsEnum.ON.getCode(), decisionNumber, decisionDate, effectiveHijriDate, DataAccess.getTableName(UnitTransaction.class))
			.build();
		LogService.logEmployeeData(employeeLog, session);
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

    public static boolean checkIfSoldiersDistributionNeedsToBeDeleted(EmployeeData employeeData) throws BusinessException {
	if (employeeData.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {

	    if (employeeData.getEmpId() != null) {

		if (RecruitmentsService.getRecruitmentWishByEmpId(employeeData.getEmpId()) != null) {

		    EmployeeData originalEmpData = EmployeesService.getEmployeeData(employeeData.getEmpId());
		    if ((originalEmpData.getStatusId().equals(EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode()) && (!employeeData.getRecruitmentMinorSpecId().equals(originalEmpData.getRecruitmentMinorSpecId())))

			    || (originalEmpData.getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode()) && (!employeeData.getRecruitmentMinorSpecId().equals(originalEmpData.getRecruitmentMinorSpecId()) || !employeeData.getRecruitmentRankId().equals(originalEmpData.getRecruitmentRankId())))

			    || (originalEmpData.getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode()) && employeeData.getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode())))
			return true;
		}
	    }
	}

	return false;
    }

    public static void deleteSoldiersDistribution(EmployeeData modifiedEmp, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<EmployeeData> employeesList = getAllDistributedSoldiers();

	    if (employeesList.size() > 0) {
		if (BusinessWorkflowCooperation.countRunningProcesses(new Long[] { WFProcessesEnum.SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT.getCode(), WFProcessesEnum.SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT.getCode() }) > 0) {
		    throw new BusinessException("error_cannotDeleteSoldiersDistribution");
		}

		for (EmployeeData emp : employeesList) {
		    if (modifiedEmp != null && emp.getEmpId().equals(modifiedEmp.getEmpId())) {
			modifiedEmp.setRecruitmentRegionId(null);
		    } else {
			emp.setRecruitmentRegionId(null);
			DataAccess.updateEntity(emp.getEmployee(), session);
		    }
		}
	    }

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

    public static void payrollIntegrationFailureHandle(Long empId) throws BusinessException {
	if (empId != null)
	    doPayrollIntegration(empId, FlagsEnum.ON.getCode());
	else
	    throw new BusinessException("error_transactionDataRetrievingError");
    }

    private static void validateEmployee(EmployeeData empData) throws BusinessException {
	employeeCommonValidate(empData);
	if (empData.getCategoryId().intValue() == CategoriesEnum.OFFICERS.getCode())
	    officersValidate(empData);
	else if (empData.getCategoryId().intValue() == CategoriesEnum.SOLDIERS.getCode())
	    soldiersValidate(empData);
	else
	    personsValidate(empData);
    }

    private static void employeeCommonValidate(EmployeeData empData) throws BusinessException {
	if (empData == null)
	    throw new BusinessException("error_empSelectionManadatory");
	if (empData.getFirstName() == null || empData.getFirstName().length() == 0)
	    throw new BusinessException("error_firstNameMandatory");
	if (empData.getSecondName() == null || empData.getSecondName().length() == 0)
	    throw new BusinessException("error_secondNameMandatory");
	if (empData.getLastName() == null || empData.getLastName().length() == 0)
	    throw new BusinessException("error_lastNameMandatory");
	if (empData.getBirthPlace() == null || empData.getBirthPlace().length() == 0)
	    throw new BusinessException("error_birthPlaceMandatory");
	if (empData.getSocialID() == null || empData.getSocialID().length() == 0)
	    throw new BusinessException("error_socialIDMandatory");
	if (empData.getSocialID().length() != 10)
	    throw new BusinessException("error_invalidSocialID");
	if (empData.getCategoryId() != CategoriesEnum.CONTRACTORS.getCode() && empData.getSocialID().charAt(0) != '1') {
	    throw new BusinessException("error_employeeHasToBeSaudi");
	}

	EmployeeData conflictEmp = getEmployeeBySocialID(empData.getSocialID());
	if (conflictEmp != null && (empData.getEmpId() == null || !conflictEmp.getEmpId().equals(empData.getEmpId())))
	    throw new BusinessException("error_socialIdAlreadyExists");

	if ((empData.getPrivateMobileNumber() != null && empData.getShieldMobileNumber() != null && !empData.getPrivateMobileNumber().isEmpty() && !empData.getShieldMobileNumber().isEmpty() && empData.getPrivateMobileNumber().equals(empData.getShieldMobileNumber()))
		|| (empData.getPrivateMobileNumber() != null && empData.getOfficialMobileNumber() != null && !empData.getPrivateMobileNumber().isEmpty() && !empData.getOfficialMobileNumber().isEmpty() && empData.getPrivateMobileNumber().equals(empData.getOfficialMobileNumber()))
		|| (empData.getShieldMobileNumber() != null && empData.getOfficialMobileNumber() != null && !empData.getShieldMobileNumber().isEmpty() && !empData.getOfficialMobileNumber().isEmpty() && empData.getShieldMobileNumber().equals(empData.getOfficialMobileNumber())))
	    throw new BusinessException("error_mobileNumbersShouldBeUnique");

	List<EmployeeData> conflictEmployees = getEmployeeCommunicationsDataUniqueness(empData.getOfficialMobileNumber(), empData.getPrivateMobileNumber(), empData.getShieldMobileNumber(), empData.getEmail(), empData.getEmpId());
	for (EmployeeData employee : conflictEmployees) {
	    if (empData.getEmail() != null && !empData.getEmail().isEmpty() && empData.getEmail().equalsIgnoreCase(employee.getEmail()))
		throw new BusinessException("error_emailAlreadyExists");
	    else if (empData.getPrivateMobileNumber() != null && !empData.getPrivateMobileNumber().isEmpty() && (empData.getPrivateMobileNumber().equalsIgnoreCase(employee.getPrivateMobileNumber()) ||
		    empData.getPrivateMobileNumber().equalsIgnoreCase(employee.getOfficialMobileNumber()) || empData.getPrivateMobileNumber().equalsIgnoreCase(employee.getShieldMobileNumber())))
		throw new BusinessException("error_privateMobileNumbersExists");
	    else if (empData.getOfficialMobileNumber() != null && !empData.getOfficialMobileNumber().isEmpty() && (empData.getOfficialMobileNumber().equalsIgnoreCase(employee.getPrivateMobileNumber()) ||
		    empData.getOfficialMobileNumber().equalsIgnoreCase(employee.getOfficialMobileNumber()) || empData.getOfficialMobileNumber().equalsIgnoreCase(employee.getShieldMobileNumber())))
		throw new BusinessException("error_officialMobileNumbersExists");
	    else if (empData.getShieldMobileNumber() != null && !empData.getShieldMobileNumber().isEmpty() && (empData.getShieldMobileNumber().equalsIgnoreCase(employee.getPrivateMobileNumber()) ||
		    empData.getShieldMobileNumber().equalsIgnoreCase(employee.getOfficialMobileNumber()) || empData.getShieldMobileNumber().equalsIgnoreCase(employee.getShieldMobileNumber())))
		throw new BusinessException("error_shieldMobileNumbersExists");
	}
	validateDates(empData);
    }

    private static void validateDates(EmployeeData emp) throws BusinessException {
	if (emp.getBirthDate() == null)
	    throw new BusinessException("error_birthDateMandatory");

	if (!HijriDateService.isValidHijriDate(emp.getBirthDate()))
	    throw new BusinessException("error_invalidHijriBirthDate");
	if (emp.getSocialIDIssueDate() != null && !HijriDateService.isValidHijriDate(emp.getSocialIDIssueDate()))
	    throw new BusinessException("error_invalidHijriSocialIdIssueDate");
	if (emp.getSocialIDExpiryDate() != null && !HijriDateService.isValidHijriDate(emp.getSocialIDExpiryDate()))
	    throw new BusinessException("error_invalidHijriSocialIdIssueDate");

	Date sysDate = HijriDateService.getHijriSysDate();

	if (HijriDateService.hijriDateDiff(emp.getBirthDate(), sysDate) <= 0)
	    throw new BusinessException("error_invalidBirthDate");
	if (emp.getSocialIDIssueDate() != null && HijriDateService.hijriDateDiff(emp.getBirthDate(), emp.getSocialIDIssueDate()) <= 0)
	    throw new BusinessException("error_invalidSocialIdIssueDate");
    }

    private static void officersValidate(EmployeeData empData) throws BusinessException {
	if (empData.getMilitaryNumber() != null && (empData.getMilitaryNumber() + "").length() != 6)
	    throw new BusinessException("error_invalidMilitaryNumber");
	if (empData.getGraduationGroupNumber() == null && empData.getStatusId().equals(EmployeeStatusEnum.UNDER_RECRUITMENT.getCode()))
	    throw new BusinessException("error_graduationGroupNumberMandatory");
	if (empData.getGraduationGroupPlace() == null && empData.getStatusId().equals(EmployeeStatusEnum.UNDER_RECRUITMENT.getCode()))
	    throw new BusinessException("error_graduationGroupPlaceMandatory");
	if (empData.getGeneralSpecialization() == null)
	    throw new BusinessException("error_generalSpecializationMandatory");

	EmployeeData conflictEmp = null;
	if (empData.getMilitaryNumber() != null)
	    conflictEmp = getOfficerByMilitaryNumber(empData.getMilitaryNumber());
	if (conflictEmp != null && (empData.getEmpId() == null || !conflictEmp.getEmpId().equals(empData.getEmpId())))
	    throw new BusinessException("error_militaryNumberAlreadyExists");
    }

    private static void soldiersValidate(EmployeeData empData) throws BusinessException {
	if ((empData.getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode()) || empData.getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode())) && empData.getRecruitmentRankId() == null)
	    throw new BusinessException("error_recruitmentRankMandatory");
	if ((empData.getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode()) || empData.getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode())) && empData.getRecruitmentMinorSpecId() == null)
	    throw new BusinessException("error_recruitmentMinorSpecMandatory");
	if (empData.getGeneralSpecialization() == null)
	    throw new BusinessException("error_generalSpecializationMandatory");
	if (empData.getRecTrainingUnitId() != null) {
	    UnitData recTrainingUnit = UnitsService.getUnitById(empData.getRecTrainingUnitId());
	    if (recTrainingUnit == null || (!recTrainingUnit.getUnitTypeCode().equals(UnitTypesEnum.TRAINING_CENTER.getCode()) && !recTrainingUnit.getUnitTypeCode().equals(UnitTypesEnum.INSTITUTE.getCode()) && !(recTrainingUnit.getUnitTypeCode().equals(UnitTypesEnum.REGION_COMMANDER.getCode()) && recTrainingUnit.getRegionId().equals(RegionsEnum.ACADEMY.getCode())))) {
		throw new BusinessException("error_recTrainingUnitTypeInvalid");
	    }
	}

	// Check if null as in registration rank_id is not set yet, Check for student soldiers as we check for employees before recruitment only
	if (empData.getGender().equals("M") && (empData.getRankId() == null || empData.getRankId().equals(RanksEnum.STUDENT_SOLDIER.getCode()))) {

	    if (empData.getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode()) && empData.getRecTrainingUnitId() == null)
		throw new BusinessException("error_recTrainingUnitMandatory");

	    if (empData.getRecTrainingJoiningDate() == null)
		throw new BusinessException("error_recTranJoiningDateMandatory");

	    if (!HijriDateService.isValidHijriDate(empData.getRecTrainingJoiningDate()))
		throw new BusinessException("error_incorrectRecTranJoiningDate");

	    if (empData.getRecTrainingJoiningDate().after(HijriDateService.getHijriSysDate()))
		throw new BusinessException("error_invalidRecTranJoiningDate");
	}

	// This rule is meant to check for corporal or higher rank soldiers with inserted recruitment wish that has been updated to be a soldier or first soldier
	if (empData.getEmpId() != null && empData.getStatusId().equals(EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode()) && RecruitmentsService.getRecruitmentWishByEmpId(empData.getEmpId()) != null) {
	    throw new BusinessException("error_cannotChangeRankForSoldierWithRecruitmentRank");
	}

    }

    private static void personsValidate(EmployeeData empData) throws BusinessException {
	if (empData.getCategoryId().longValue() == CategoriesEnum.CONTRACTORS.getCode() && empData.getCountryId() == null)
	    throw new BusinessException("error_countryMandatory");
    }

    private static void doPayrollIntegration(Long employeeId, Integer resendFlag) throws BusinessException {
	String gregSysDateString = HijriDateService.hijriToGregDateString(HijriDateService.getHijriSysDateString());
	EmployeeData employee = EmployeesService.getEmployeeData(employeeId);
	List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>(Arrays.asList(new AdminDecisionEmployeeData(employee.getEmpId(), employee.getName(), null, null, gregSysDateString, gregSysDateString, System.currentTimeMillis() + "", null)));
	if (employee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()))
	    PayrollEngineService.doPayrollIntegration(AdminDecisionsEnum.OFFICERS_REGISTRATION.getCode(), CategoriesEnum.OFFICERS.getCode(), gregSysDateString, adminDecisionEmployeeDataList, employee.getPhysicalUnitId() == null ? UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0).getId() : employee.getPhysicalUnitId(), gregSysDateString, DataAccess.getTableName(Employee.class), resendFlag, FlagsEnum.OFF.getCode());
    }

    public static EmployeeData getEmployeeDirectManager(long employeeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", employeeId);

	    List<EmployeeData> result = DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEE_DIRECT_MANAGER.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static EmployeeData getEmployeeData(long employeeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", employeeId);

	    List<EmployeeData> result = DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEE_BY_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static EmployeeData getEmployeeBySocialID(String socialID) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_SOCIAL_ID", socialID);

	    List<EmployeeData> result = DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEE_BY_SOCIAL_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeData> getEmployeeCommunicationsDataUniqueness(String officialMobileNumber, String privateMobileNumber, String shieldMobileNumber, String email, Long employeeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_OFFICIAL_MOBILE_NUMBER", (officialMobileNumber == null || officialMobileNumber.equals("")) ? (FlagsEnum.ALL.getCode() + "") : officialMobileNumber);
	    qParams.put("P_PRIVATE_MOBILE_NUMBER", (privateMobileNumber == null || privateMobileNumber.equals("")) ? (FlagsEnum.ALL.getCode() + "") : privateMobileNumber);
	    qParams.put("P_SHIELD_MOBILE_NUMBER", (shieldMobileNumber == null || shieldMobileNumber.equals("")) ? (FlagsEnum.ALL.getCode() + "") : shieldMobileNumber);
	    qParams.put("P_EMAIL", (email == null || email.equals("")) ? (FlagsEnum.ALL.getCode() + "") : email);
	    qParams.put("P_EXCLUDED_EMP_ID", (employeeId == null) ? (FlagsEnum.ALL.getCode()) : employeeId);

	    List<EmployeeData> result = DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEE_COMMUNICATION_DATA_UNIQUENESS.getCode(), qParams);
	    return result;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static EmployeeData getOfficerByMilitaryNumber(int militaryNumber) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MILITARY_NUMBER", militaryNumber);

	    List<EmployeeData> result = DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEE_BY_MILITARY_NUMBER.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeData> getManagerEmployees(long managerId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_MANAGER_ID", managerId);
	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_MANAGER_EMPLOYEES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    public static List<EmployeeData> getEmployeesByPhysicalUnitHkeyNameAndStatusesID(String socialId, String empName, String jobDesc, String physicalUnitFullName, String unitHkey, Long[] statusesId, Long[] categoriesIds, long regionId, int militaryNumber, long officialRegionId, Long sequenceNumber, long rankId) throws BusinessException {
	return searchEmployees(empName, categoriesIds, FlagsEnum.ALL.getCode(), militaryNumber, socialId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), statusesId, unitHkey, regionId, officialRegionId, jobDesc, physicalUnitFullName, FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), sequenceNumber, rankId);
    }

    public static List<EmployeeData> getEmpDataByPhysicalUnitId(long physicalUnitId) throws BusinessException {
	return searchEmployees(FlagsEnum.ALL.getCode() + "", null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode() + "", physicalUnitId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode());
    }

    public static List<EmployeeData> getEmpDataByOfficialUnitId(long officialUnitId) throws BusinessException {
	return searchEmployees(FlagsEnum.ALL.getCode() + "", null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), officialUnitId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode());
    }

    public static List<EmployeeData> getEmpByEmpName(String socialId, String empName, String jobDesc, String physicalUnitFullName, Long[] categoriesIds, long physicalRegionId, long officialRegionId, int militaryNumber, Long sequenceNumber) throws BusinessException {
	return searchEmployees(empName, categoriesIds, FlagsEnum.ALL.getCode(), militaryNumber, socialId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, null, physicalRegionId, officialRegionId, jobDesc, physicalUnitFullName, FlagsEnum.ALL.getCode() + "", FlagsEnum.ALL.getCode(), sequenceNumber, FlagsEnum.ALL.getCode());
    }

    public static List<EmployeeData> getEmployeesByEmpStatusesId(String socialId, String empName, String jobDesc, String physicalUnitFullName, Long[] statusesIds, Long[] categoriesIds, long physicalRegionId, long officialRegionId, String gender, long exceptionalRecruitmentFlag, int militaryNumber, Long sequenceNumber) throws BusinessException {
	return searchEmployees(empName, categoriesIds, FlagsEnum.ALL.getCode(), militaryNumber, socialId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), statusesIds, null, physicalRegionId, officialRegionId, jobDesc, physicalUnitFullName, gender, exceptionalRecruitmentFlag, sequenceNumber, FlagsEnum.ALL.getCode());
    }

    private static List<EmployeeData> searchEmployees(String empName, Long[] categoriesIds, long jobId, int militaryNumber, String socialID, long physicalUnitId, long officialUnitId, long jobMajorSpecId, long jobMinorSpecId, Long[] statusesIds, String physicalUnitHKey, long physicalRegionId, long officialRegionId, String jobDesc, String physicalUnitFullName, String gender, long exceptionalRecruitmentFlag, Long sequenceNumber, long rankId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_NAME", (empName == null || empName.equals("") || empName.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    String usedHKey = UnitsService.getHKeyPrefix(physicalUnitHKey);
	    qParams.put("P_PHYSICAL_UNIT_HKEY", (usedHKey == null || usedHKey.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : (usedHKey + "%"));

	    qParams.put("P_JOB_ID", jobId);
	    qParams.put("P_MILITARY_NUMBER", militaryNumber);
	    qParams.put("P_SOCIAL_ID", (socialID == null || socialID.equals("")) ? FlagsEnum.ALL.getCode() + "" : socialID);
	    qParams.put("P_PHYSICAL_UNIT_ID", physicalUnitId);
	    qParams.put("P_OFFICIAL_UNIT_ID", officialUnitId);
	    qParams.put("P_MAJOR_SPEC_ID", jobMajorSpecId);
	    qParams.put("P_MINOR_SPEC_ID", jobMinorSpecId);
	    if (statusesIds != null && statusesIds.length > 0) {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUSES_IDS", statusesIds);
	    } else {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUSES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    qParams.put("P_PHYSICAL_REGION_ID", physicalRegionId);
	    qParams.put("P_OFFICIAL_REGION_ID", officialRegionId);
	    qParams.put("P_JOB_DESC", (jobDesc == null || jobDesc.equals("") || jobDesc.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + jobDesc + "%");
	    qParams.put("P_PHYSICAL_UNIT_FULL_NAME", (physicalUnitFullName == null || physicalUnitFullName.equals("") || physicalUnitFullName.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + physicalUnitFullName + "%");
	    qParams.put("P_GENDER", gender);
	    qParams.put("P_EXCEPTIONAL_RECRUITMENT_FLAG", exceptionalRecruitmentFlag);
	    qParams.put("P_SEQUENCE_NUMBER", (sequenceNumber == null) ? FlagsEnum.ALL.getCode() : sequenceNumber);
	    qParams.put("P_RANK_ID", rankId);
	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeData> searchEmployeesForFutureVacation(String empName, Long[] categoriesIds, Long[] employeesId, int militaryNumber, String socialID, Long[] statusesIds, String jobDesc, String physicalUnitFullName, Long sequenceNumber) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_NAME", (empName == null || empName.equals("") || empName.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    qParams.put("P_MILITARY_NUMBER", militaryNumber);
	    qParams.put("P_SOCIAL_ID", (socialID == null || socialID.equals("")) ? FlagsEnum.ALL.getCode() + "" : socialID);
	    if (statusesIds != null && statusesIds.length > 0) {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUSES_IDS", statusesIds);
	    } else {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUSES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    if (employeesId != null && employeesId.length > 0) {
		qParams.put("P_EMP_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_EMP_IDS", employeesId);
	    } else {
		qParams.put("P_EMP_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EMP_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    qParams.put("P_JOB_DESC", (jobDesc == null || jobDesc.equals("") || jobDesc.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + jobDesc + "%");
	    qParams.put("P_PHYSICAL_UNIT_FULL_NAME", (physicalUnitFullName == null || physicalUnitFullName.equals("") || physicalUnitFullName.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + physicalUnitFullName + "%");
	    qParams.put("P_SEQUENCE_NUMBER", (sequenceNumber == null) ? FlagsEnum.ALL.getCode() : sequenceNumber);

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEES_FOR_BENEFICIARY.getCode(), qParams);
	} catch (

	DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeData> getEmpByPhysicalOrOfficialUnit(String empName, Long[] categoriesIds, int militaryNumber, String socialId, String jobDesc, String searchUnitFullName, long jobId, long unitId, long jobMinorSpecId, long regionId, Long sequenceNmuber, String gender) throws BusinessException {
	return searchEmployeesByPhysicalOrOfficialUnit(empName, categoriesIds, jobId, militaryNumber, socialId, unitId, jobMinorSpecId, regionId, jobDesc, searchUnitFullName, sequenceNmuber, gender);
    }

    private static List<EmployeeData> searchEmployeesByPhysicalOrOfficialUnit(String empName, Long[] categoriesIds, long jobId, int militaryNumber, String socialID, long unitId, long jobMinorSpecId, long regionId, String jobDesc, String unitFullName, Long sequenceNumber, String gender) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_NAME", (empName == null || empName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    qParams.put("P_JOB_ID", jobId);
	    qParams.put("P_MILITARY_NUMBER", militaryNumber);
	    qParams.put("P_SOCIAL_ID", (socialID == null || socialID.equals("")) ? FlagsEnum.ALL.getCode() + "" : socialID);
	    qParams.put("P_UNIT_ID", unitId);
	    qParams.put("P_MINOR_SPEC_ID", jobMinorSpecId);
	    qParams.put("P_REGION_ID", regionId);
	    qParams.put("P_JOB_DESC", (jobDesc == null || jobDesc.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + jobDesc + "%");
	    qParams.put("P_UNIT_FULL_NAME", (unitFullName == null || unitFullName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + unitFullName + "%");
	    qParams.put("P_SEQUENCE_NUMBER", (sequenceNumber == null) ? FlagsEnum.ALL.getCode() : sequenceNumber);
	    qParams.put("P_GENDER", (gender == null || gender.equals("")) ? FlagsEnum.ALL.getCode() + "" : gender);
	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEES_BY_PHYSICAL_OR_OFFICIAL_UNIT.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeData> getEmployeesByEmpCommunicationData(String socialId, String empName, String physicalUnitName, String jobDesc, String searchOfficialMobileNumber, String searchDirectPhoneNumber, String searchPrivateMobileNumber, String searchPhoneExt, String searchShieldMobileNumber, String searchIpPhoneExt, String searchEmail, String searchUserAccount) throws BusinessException {
	return searchEmployeesByCommunicationData(empName, socialId, jobDesc, physicalUnitName, searchOfficialMobileNumber, searchDirectPhoneNumber, searchPrivateMobileNumber, searchPhoneExt, searchShieldMobileNumber, searchIpPhoneExt, searchEmail, searchUserAccount);
    }

    public static EmployeeData getEmployeeByUserAccount(String userAccount) throws BusinessException {
	List<EmployeeData> result = searchEmployeesByCommunicationData(null, null, null, null, null, null, null, null, null, null, null, userAccount);
	return result.isEmpty() ? null : result.get(0);
    }

    private static List<EmployeeData> searchEmployeesByCommunicationData(String empName, String socialID, String jobDesc, String physicalUnitFullName, String officialMobileNumber, String directPhoneNumber, String privateMobileNumber,
	    String phoneExt, String shieldMobileNumber, String ipPhoneExt, String email, String userAccount) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_NAME", (empName == null || empName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");
	    qParams.put("P_SOCIAL_ID", (socialID == null || socialID.equals("")) ? FlagsEnum.ALL.getCode() + "" : socialID);
	    qParams.put("P_JOB_DESC", (jobDesc == null || jobDesc.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + jobDesc + "%");
	    qParams.put("P_PHYSICAL_UNIT_FULL_NAME", (physicalUnitFullName == null || physicalUnitFullName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + physicalUnitFullName + "%");
	    qParams.put("P_OFFICIAL_MOBILE_NUMBER", (officialMobileNumber == null || officialMobileNumber.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + officialMobileNumber + "%");
	    qParams.put("P_DIRECT_PHONE_NUMBER", (directPhoneNumber == null || directPhoneNumber.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + directPhoneNumber + "%");
	    qParams.put("P_PRIVATE_MOBILE_NUMBER", (privateMobileNumber == null || privateMobileNumber.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + privateMobileNumber + "%");
	    qParams.put("P_PHONE_EXT", (phoneExt == null || phoneExt.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + phoneExt + "%");
	    qParams.put("P_SHIELD_MOBILE_NUMBER", (shieldMobileNumber == null || shieldMobileNumber.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + shieldMobileNumber + "%");
	    qParams.put("P_IP_PHONE_EXT", (ipPhoneExt == null || ipPhoneExt.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + ipPhoneExt + "%");
	    qParams.put("P_EMAIL", (email == null || email.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + email + "%");
	    qParams.put("P_USER_ACCOUNT", (userAccount == null || userAccount.equals("")) ? FlagsEnum.ALL.getCode() + "" : userAccount + "@" + ETRConfigurationService.getLDAPDomain());

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEES_BY_COMMUNICATION_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeData> getEmployeesByCategoryIdAndRankIdAndPromotionDueDate(String socialId, String empName, String jobDesc, String physicalUnitFullName, Long[] categoriesIds, long rankId, String promotionDueDate, long officialRegionId, int militaryNumber, Long sequenceNumber) throws BusinessException {
	Long[] rankIds;

	if (rankId == RanksEnum.FIRST.getCode())
	    rankIds = new Long[] { RanksEnum.FIRST.getCode(), RanksEnum.SECOND.getCode(), RanksEnum.THIRD.getCode(), RanksEnum.FOURTH.getCode() };
	else if (rankId == RanksEnum.FIFTH.getCode())
	    rankIds = new Long[] { RanksEnum.FIFTH.getCode(), RanksEnum.SIXTH.getCode(), RanksEnum.SEVENTH.getCode(), RanksEnum.EIGHTH.getCode(), RanksEnum.NINTH.getCode() };
	else if (rankId == RanksEnum.TENTH.getCode())
	    rankIds = new Long[] { RanksEnum.TENTH.getCode(), RanksEnum.ELEVENTH.getCode(), RanksEnum.TWELFTH.getCode(), RanksEnum.THIRTEENTH.getCode() };
	else
	    rankIds = new Long[] { rankId };

	return searchEmployeesByRankIdAndPromotionDueDate(socialId, empName, jobDesc, physicalUnitFullName, categoriesIds, rankIds, promotionDueDate, officialRegionId, militaryNumber, sequenceNumber);
    }

    private static List<EmployeeData> searchEmployeesByRankIdAndPromotionDueDate(String socialId, String empName, String jobDesc, String physicalUnitFullName, Long[] categoriesIds, Long[] rankIds, String promotionDueDate, long officialRegionId, int militaryNumber, Long sequenceNumber) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_NAME", (empName == null || empName.equals("") || empName.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    if (rankIds != null && rankIds.length > 0) {
		qParams.put("P_RANK_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_RANK_IDS", rankIds);
	    } else {
		qParams.put("P_RANK_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_RANK_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    qParams.put("P_PROMOTION_DUE_DATE", (promotionDueDate == null || promotionDueDate.equals("") || promotionDueDate.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : promotionDueDate);

	    qParams.put("P_SOCIAL_ID", (socialId == null || socialId.equals("")) ? FlagsEnum.ALL.getCode() + "" : socialId);
	    qParams.put("P_OFFICIAL_REGION_ID", officialRegionId);
	    qParams.put("P_MILITARY_NUMBER", militaryNumber);
	    qParams.put("P_JOB_DESC", (jobDesc == null || jobDesc.equals("") || jobDesc.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + jobDesc + "%");
	    qParams.put("P_PHYSICAL_UNIT_FULL_NAME", (physicalUnitFullName == null || physicalUnitFullName.equals("") || physicalUnitFullName.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + physicalUnitFullName + "%");
	    qParams.put("P_SEQUENCE_NUMBER", (sequenceNumber == null) ? FlagsEnum.ALL.getCode() : sequenceNumber);

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEE_BY_RANK_ID_PROMOTION_DUE_DATE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeData> getOfficersByRecruitmentInfo(long recruitmentRegionId, int graduationGroupNumber, int graduationGroupPlace) throws BusinessException {
	return searchEmployeesByRecruitmentInfo(null, null, null, null, CategoriesEnum.OFFICERS.getCode(), new Long[] { EmployeeStatusEnum.UNDER_RECRUITMENT.getCode() }, FlagsEnum.ALL.getCode(), recruitmentRegionId, FlagsEnum.ALL.getCode(), graduationGroupNumber, graduationGroupPlace, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode() + "", FlagsEnum.OFF.getCode(), FlagsEnum.ALL.getCode(), null);
    }

    public static List<EmployeeData> getSoldiersByRecruitmentInfo(Long[] statusesIds, long recruitmentRankId, long recruitmentRegionId, Long recTrainingUnitId, String gender, long exceptionalRecruitmentFlag) throws BusinessException {
	return searchEmployeesByRecruitmentInfo(null, null, null, null, CategoriesEnum.SOLDIERS.getCode(), statusesIds, recruitmentRankId, recruitmentRegionId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), recTrainingUnitId, "M", FlagsEnum.OFF.getCode(), FlagsEnum.ALL.getCode(), null);
    }

    public static List<EmployeeData> getEmployeesForRecruitment(String socialId, String empName, String jobDesc, String physicalUnitFullName, long CategoryId, long recruitmentRegionId, int graduationGroupNumber, int graduationGroupPlace, long recruitmentRankId, long recruitmentTrainingUnitId, String gender, long exceptionalRecruitmentFlag, Long[] statusIds, int militaryNumber, Long sequenceNumber) throws BusinessException {
	if (statusIds == null)
	    statusIds = new Long[] { EmployeeStatusEnum.UNDER_RECRUITMENT.getCode(), EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode(), EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode(), EmployeeStatusEnum.ON_DUTY_UNDER_RECRUITMENT.getCode() };

	return searchEmployeesByRecruitmentInfo(socialId, empName, jobDesc, physicalUnitFullName, CategoryId, statusIds, recruitmentRankId, recruitmentRegionId, FlagsEnum.ALL.getCode(), graduationGroupNumber, graduationGroupPlace, recruitmentTrainingUnitId, gender,
		exceptionalRecruitmentFlag, militaryNumber, sequenceNumber);
    }

    public static List<EmployeeData> getExceptionalEmployeesForRecruitment(String socialId, String empName, String jobDesc, String physicalUnitFullName, String gender, Long[] statusIds, int militaryNumber) throws BusinessException {
	return searchEmployeesByRecruitmentInfo(socialId, empName, jobDesc, physicalUnitFullName, CategoriesEnum.SOLDIERS.getCode(), statusIds, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(),
		FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), gender, FlagsEnum.ON.getCode(), militaryNumber, null);
    }

    private static List<EmployeeData> searchEmployeesByRecruitmentInfo(String socialId, String empName, String jobDesc, String physicalUnitFullName, long categoryId, Long[] statusesIds, long recruitmentRankId, long recruitmentRegionId, long recruitmentMinorSpecId, int graduationGroupNumber, int graduationGroupPlace, long recTrainingUnitId, String gender, long exceptionalRecruitmentFlag, int militaryNumber, Long sequenceNumber) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_NAME", (empName == null || empName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : "%" + empName + "%");
	    qParams.put("P_RECRUITMENT_RANK_ID", recruitmentRankId);
	    qParams.put("P_RECRUITMENT_REGION_ID", recruitmentRegionId);
	    qParams.put("P_GRADUATION_GROUP_NUMBER", graduationGroupNumber);
	    qParams.put("P_GRADUATION_GROUP_PLACE", graduationGroupPlace);
	    qParams.put("P_RECRUITMENT_MINOR_SPEC_ID", recruitmentMinorSpecId);
	    qParams.put("P_REC_TRAINING_UNIT_ID", recTrainingUnitId);
	    qParams.put("P_CATEGORY_ID", categoryId);
	    qParams.put("P_MILITARY_NUMBER", militaryNumber);

	    if (statusesIds != null && statusesIds.length > 0) {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_STATUSES_IDS", statusesIds);
	    } else {
		qParams.put("P_STATUSES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_STATUSES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }

	    qParams.put("P_SOCIAL_ID", (socialId == null || socialId.equals("")) ? FlagsEnum.ALL.getCode() + "" : socialId);
	    qParams.put("P_JOB_DESC", (jobDesc == null || jobDesc.equals("") || jobDesc.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + jobDesc + "%");
	    qParams.put("P_PHYSICAL_UNIT_FULL_NAME", (physicalUnitFullName == null || physicalUnitFullName.equals("") || physicalUnitFullName.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : "%" + physicalUnitFullName + "%");

	    qParams.put("P_GENDER", (gender == null || gender.equals("")) ? FlagsEnum.ALL.getCode() + "" : gender);
	    qParams.put("P_EXCEPTIONAL_RECRUITMENT_FLAG", exceptionalRecruitmentFlag);
	    qParams.put("P_SEQUENCE_NUMBER", (sequenceNumber == null) ? FlagsEnum.ALL.getCode() : sequenceNumber);

	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEE_DATA_BY_REC_INFO.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static String getEmployeesIdsString(List<EmployeeData> employeesList) {
	String resultString = "";
	if (employeesList != null)
	    for (EmployeeData emp : employeesList)
		resultString += emp.getEmpId() + ",";
	return resultString.length() == 0 ? resultString : resultString.substring(0, resultString.length() - 1);
    }

    public static List<EmployeeData> getEmployeesByIdsString(String empIdsString) throws BusinessException {
	List<EmployeeData> employeesList = new ArrayList<EmployeeData>();
	if (empIdsString != null && empIdsString.length() > 0) {
	    String[] empsIdsStrings = empIdsString.split(",");
	    Long[] empsIds = new Long[empsIdsStrings.length];
	    for (int i = 0; i < empsIdsStrings.length; i++)
		empsIds[i] = Long.parseLong(empsIdsStrings[i]);

	    employeesList = EmployeesService.getEmployeesByEmpsIds(empsIds);
	}
	return employeesList;
    }

    public static List<EmployeeData> getEmployeesByEmpsIds(Long[] empsIds) throws BusinessException {
	return searchEmployeesByEmpsIds(empsIds);
    }

    private static List<EmployeeData> searchEmployeesByEmpsIds(Long[] empsIds) throws BusinessException {
	List<Object> queryInfo = new ArrayList<Object>();
	queryInfo.add(QueryNamesEnum.HCM_SEARCH_EMPLOYEES_BY_EMPS_IDS.getCode());

	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_EMPS_IDS", empsIds);
	queryInfo.add(qParams);

	return getManyEntities(EmployeeData.class, queryInfo, empsIds);
    }

    public static List<EmployeeData> getPromotionEligibleEmployees(List<Long> ranksID, Date promotionDueDate, Integer categoryId, Long regionId) throws BusinessException {
	try {
	    if (ranksID == null || ranksID.size() == 0)
		return new ArrayList<EmployeeData>();
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    if (ranksID != null && ranksID.size() > 0) {
		qParams.put("P_EMP_RANK_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_EMP_RANK", ranksID.toArray(new Long[ranksID.size()]));
	    } else {
		qParams.put("P_EMP_RANK_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EMP_RANK", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    qParams.put("P_CATEGORY_ID", (categoryId == null) ? (FlagsEnum.ALL.getCode()) : categoryId);
	    qParams.put("P_REGION_ID", (regionId == null) ? (FlagsEnum.ALL.getCode()) : regionId);
	    // qParams.put("P_REPORT_ID", (reportId == null) ? (FlagsEnum.ALL.getCode()) : reportId);
	    if (promotionDueDate != null) {
		qParams.put("P_DUE_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DUE_DATE", HijriDateService.getHijriDateString(promotionDueDate));
	    } else {
		qParams.put("P_DUE_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DUE_DATE", HijriDateService.getHijriSysDateString());
	    }
	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEE_PROMOTION_DUE_DATE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Returns only soldiers(That have evaluation degrees and wishes) with status new student ranked soldier or on duty under recruitment If recruitmentRegionIdFlag 0, returns soldiers with recruitmentRegionId equals null If recruitmentRegionIdFlag 1, returns soldiers with recruitmentRegionId not equals null If recruitmentRegionIdFlag -1, returns all soldiers
     * 
     * @param recruitmentRegionIdFlag
     *            0,1 or -1
     * @return soldiers count
     * @throws BusinessException
     */
    public static long countSoldiersForRecruitment(int recruitmentRegionIdFlag) throws BusinessException {
	try {
	    if (recruitmentRegionIdFlag != FlagsEnum.OFF.getCode() && recruitmentRegionIdFlag != FlagsEnum.ON.getCode() && recruitmentRegionIdFlag != FlagsEnum.ALL.getCode())
		return 0;

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RECRUITMENT_REGION_ID_FLAG", recruitmentRegionIdFlag);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_SOLDIERS_FOR_RECRUITMENT.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeData> getAllDistributedSoldiers() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_GET_ALL_DISTRIBUTED_SOLDIERS.getCode(), new HashMap<String, Object>());
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static EmployeeData getEmployeeByPosition(long unitId, Long empId) throws BusinessException {
	EmployeeData emp = null;
	try {
	    if (empId == null)
		emp = getEmployeeData(UnitsService.getUnitPhysicalManagerId(unitId));
	    else {
		emp = getEmployeeData(empId);
		if (!emp.getPhysicalUnitId().equals(unitId))
		    throw new Exception();
	    }
	} catch (Exception e) {
	    throw new BusinessException("error_notValidPosition");
	}

	return emp;
    }

    public static Long getEmployeeIdByOldId(String employeeNumber) throws BusinessException {
	Map<String, Object> queryParam = new HashMap<String, Object>();
	queryParam.put("P_OLD_EMP_ID", employeeNumber);
	try {
	    List<Long> result = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_GET_EMPLOYEE_ID_BY_OLD_ID.getCode(), queryParam);

	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static String getEmployeeOldIdById(long empId) throws BusinessException {
	Map<String, Object> queryParam = new HashMap<String, Object>();
	queryParam.put("P_EMP_ID", empId);

	try {
	    List<String> result = DataAccess.executeNamedQuery(String.class, QueryNamesEnum.HCM_GET_EMPLOYEE_OLD_ID_BY_ID.getCode(), queryParam);

	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static long countFutureServiceTerminatedEmployees(Long[] empsIds) throws BusinessException {
	return getFutureServiceTerminatedEmployees(empsIds).size();
    }

    public static List<Employee> getFutureServiceTerminatedEmployees(Long[] employeesIds) throws BusinessException {
	try {
	    if (employeesIds == null || employeesIds.length == 0)
		return new ArrayList<Employee>();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMPS_IDS", employeesIds);
	    qParams.put("P_EXECUTION_DATE", HijriDateService.getHijriSysDateString());

	    return DataAccess.executeNamedQuery(Employee.class, QueryNamesEnum.HCM_GET_FUTURE_SERVICE_TERMINATED_EMPLOYEES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeData> getEmployeesByUnitsIds(Long[] unitsIds) throws BusinessException {
	return searchEmployeesByUnitsIds(unitsIds);
    }

    private static List<EmployeeData> searchEmployeesByUnitsIds(Long[] unitsIds) throws BusinessException {
	try {
	    if (unitsIds == null || unitsIds.length == 0)
		return new ArrayList<EmployeeData>();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNITS_IDS", unitsIds);
	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEES_BY_UNITS_IDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<EmployeeData> getTerminatedEmployeesByUnitId(Long unitId, String searchEmpName, String searchSocialId,
	    String searchJobDesc, String searchUnitFullName, Integer searchMilitaryNumber, Long sequenceNumber) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNIT_ID", unitId == null ? FlagsEnum.ALL.getCode() : unitId);
	    qParams.put("P_EMP_NAME", searchEmpName == null || searchEmpName.isEmpty() ? FlagsEnum.ALL.getCode() : "%" + searchEmpName + "%");
	    qParams.put("P_SOCIAL_ID", searchSocialId == null || searchSocialId.isEmpty() ? FlagsEnum.ALL.getCode() : searchSocialId);
	    qParams.put("P_PHYSICAL_UNIT_FULL_NAME", searchUnitFullName == null || searchUnitFullName.isEmpty() ? FlagsEnum.ALL.getCode() : "%" + searchUnitFullName + "%");
	    qParams.put("P_JOB_DESC", searchJobDesc == null || searchJobDesc.isEmpty() ? FlagsEnum.ALL.getCode() : "%" + searchJobDesc + "%");
	    qParams.put("P_SEQUENCE_NUMBER", sequenceNumber == null ? FlagsEnum.ALL.getCode() : sequenceNumber);
	    qParams.put("P_MILITARY_NUMBER", searchMilitaryNumber == null ? FlagsEnum.ALL.getCode() : searchMilitaryNumber);
	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_SEARCH_TERMINATED_EMPLOYEES_BY_UNIT_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static long countEmployeesByUnitsIds(Long[] unitsIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNIT_ID", unitsIds);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_EMPS_BY_UNIT_ID.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * @param hkeyPrefix
     * @return count of the employees at all the units starts with hkeyPrefix, if hkeyPrefix is null or empty string it will return 0.
     * @throws BusinessException
     */
    public static long countEmployeesByCategoryAndUnitHkey(String hkey, Long[] categoriesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    String hkeyPrefix = UnitsService.getHKeyPrefix(hkey);
	    qParams.put("P_PHYSICAL_UNIT_HKEY", (hkeyPrefix == null || hkeyPrefix.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : (hkeyPrefix + "%"));
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_EMPS_BY_UNIT_HKEY_PREFIX.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Long countEmployeesByOfficialUnitHkeyAndMinorSpecAndCategory(String hkey, Long minorSpecId, Long[] categoriesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    String hkeyPrefix = UnitsService.getHKeyPrefix(hkey);
	    qParams.put("P_OFFICIAL_UNIT_HKEY", (hkeyPrefix == null || hkeyPrefix.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : (hkeyPrefix + "%"));
	    qParams.put("P_MINOR_SPEC_ID", minorSpecId);
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_EMPS_BY_OFFICIAL_UNIT_HKEY_PREFIX.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Long countEmployeesByPhysicalUnitHkeyAndMinorSpecAndCategory(String hkey, Long minorSpecId, Long[] categoriesIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    String hkeyPrefix = UnitsService.getHKeyPrefix(hkey);
	    qParams.put("P_PHYSICAL_UNIT_HKEY", (hkeyPrefix == null || hkeyPrefix.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : (hkeyPrefix + "%"));
	    qParams.put("P_MINOR_SPEC_ID", minorSpecId);
	    if (categoriesIds != null && categoriesIds.length > 0) {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_CATEGORIES_IDS", categoriesIds);
	    } else {
		qParams.put("P_CATEGORIES_IDS_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_CATEGORIES_IDS", new Long[] { (long) FlagsEnum.ALL.getCode() });
	    }
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_EMPS_BY_PHYSICAL_UNIT_HKEY_PREFIX.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // ----------------------------------- Employees Reports -------------------
    public static byte[] getEmployeesDataBytes(int officialPhysicalFlag, int officialPhysicalSimilarityFlag, long categoryId, long regionId, String unitHKey, long rankId, long minorSpecializationId, String groupNumber, List<Long> statusIds, long rankTitleId, int generalSpecialization, Date recruitmentDateFrom, Date recruitmentDateTo) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.EMPLOYEES_DATA.getCode();

	    Map<String, Object> parameters = new HashMap<String, Object>();
	    StringBuilder statusDescs = new StringBuilder();
	    if (statusIds.contains(EmployeeStatusEnum.ON_DUTY.getCode()))
		statusDescs.append(getMessage("label_onDuty")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.ASSIGNED.getCode()))
		statusDescs.append(getMessage("label_assigned")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.SUBJOINED.getCode()))
		statusDescs.append(getMessage("label_subjoined")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode()))
		statusDescs.append(getMessage("label_assignedExternally")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()))
		statusDescs.append(getMessage("label_subjoinedExternally")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.MANDATED.getCode()))
		statusDescs.append(getMessage("label_mandated")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.SECONDMENTED.getCode()))
		statusDescs.append(getMessage("label_secondmented")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.PERSONS_SUBJOINED.getCode()))
		statusDescs.append(getMessage("label_assigned")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode()))
		statusDescs.append(getMessage("label_assignedExternally")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.MOVED_EXTERNALLY.getCode()))
		statusDescs.append(getMessage("label_movedExternally")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.SERVICE_TERMINATED.getCode()))
		statusDescs.append(getMessage("label_serviceTerminated"));

	    if (statusDescs.length() != 0 && statusDescs.substring(statusDescs.length() - 2).equals("/ ")) {
		statusDescs.delete(statusDescs.length() - 2, statusDescs.length());
	    }

	    parameters.put("P_STATUS_DESCS", statusDescs.toString());
	    parameters.put("P_OFFICIAL_PHYSICAL_FLAG", officialPhysicalFlag);
	    parameters.put("P_OFFICIAL_PHYSICAL_SIMILARITY_FLAG", officialPhysicalSimilarityFlag);
	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_OFFICIAL_REGION_ID", officialPhysicalFlag == FlagsEnum.OFF.getCode() ? regionId : FlagsEnum.ALL.getCode());
	    parameters.put("P_PHYSICAL_REGION_ID", officialPhysicalFlag == FlagsEnum.ON.getCode() ? regionId : FlagsEnum.ALL.getCode());
	    parameters.put("P_OFFICIAL_UNIT_HKEY", officialPhysicalFlag == FlagsEnum.OFF.getCode() && unitHKey != null ? UnitsService.getHKeyPrefix(unitHKey) : "" + FlagsEnum.ALL.getCode());
	    parameters.put("P_PHYSICAL_UNIT_HKEY", officialPhysicalFlag == FlagsEnum.ON.getCode() && unitHKey != null ? UnitsService.getHKeyPrefix(unitHKey) : "" + FlagsEnum.ALL.getCode());
	    parameters.put("P_RANK_ID", rankId);
	    parameters.put("P_MINOR_SPECIALIZATION_ID", minorSpecializationId);
	    parameters.put("P_GROUP_NUMBER", groupNumber);
	    parameters.put("P_STATUS_IDS", statusIds.size() == 0 ? FlagsEnum.ALL.getCode() + "" : statusIds.toString().replaceAll("\\[|\\]", ""));
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_RANK_TITLE_ID", rankTitleId);
	    parameters.put("P_GENERAL_SPECIALIZATION", generalSpecialization);
	    if (recruitmentDateFrom != null) {
		parameters.put("P_RECRUITMENT_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		parameters.put("P_RECRUITMENT_DATE_FROM", HijriDateService.getHijriDateString(recruitmentDateFrom));
	    } else {
		parameters.put("P_RECRUITMENT_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		parameters.put("P_RECRUITMENT_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }
	    if (recruitmentDateTo != null) {
		parameters.put("P_RECRUITMENT_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		parameters.put("P_RECRUITMENT_DATE_TO", HijriDateService.getHijriDateString(recruitmentDateTo));
	    } else {
		parameters.put("P_RECRUITMENT_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		parameters.put("P_RECRUITMENT_DATE_TO", HijriDateService.getHijriSysDateString());
	    }

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(getMessage("error_reportPrintingError"));
	}
    }

    public static byte[] getEmployeesFileReportDataBytes(long empId, long categoryId, boolean printRecruitments, boolean printSeniortiy, boolean printPromotions, boolean printVacations, boolean printMovements, boolean printPenalities, boolean printBonuses, boolean printServiceTermination, boolean printTraining, boolean printEducations, boolean printAllowances, boolean printServiceExtension, boolean printExercises, boolean printRaises, boolean printStatus) throws BusinessException {
	try {
	    String reportName;
	    if (categoryId == CategoriesEnum.OFFICERS.getCode())
		reportName = ReportNamesEnum.EMPLOYEES_OFFICERS_FILE_REPORT.getCode();
	    else if (categoryId == CategoriesEnum.SOLDIERS.getCode())
		reportName = ReportNamesEnum.EMPLOYEES_SOLDIERS_FILE_REPORT.getCode();
	    else
		reportName = ReportNamesEnum.EMPLOYEES_CIVILIANS_FILE_REPORT.getCode();

	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_EMP_ID", empId);
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());

	    parameters.put("P_PRINT_RECRUITMENTS", printRecruitments == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_PROMOTIONS", printPromotions == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_VACACTIONS", printVacations == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_MOVEMENTS", printMovements == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_PENALITIES", printPenalities == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_BOUNSES", printBonuses == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_TRAINING", printTraining == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_EDUCATIONS", printEducations == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_ALLOWANCES", printAllowances == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_SERVICE_TERMINATION", printServiceTermination == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_SERVICE_EXTENSION", printServiceExtension == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_RAISES", printRaises == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    parameters.put("P_PRINT_STATUS", printStatus == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());

	    if (categoryId != CategoriesEnum.SOLDIERS.getCode())
		parameters.put("P_PRINT_SENIORTY", printSeniortiy == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    if (categoryId == CategoriesEnum.OFFICERS.getCode() || categoryId == CategoriesEnum.SOLDIERS.getCode())
		parameters.put("P_PRINT_EXERCISES", printExercises == true ? FlagsEnum.ON.getCode() : FlagsEnum.OFF.getCode());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getEmployeesStatisticsDataBytes(int officialPhysicalFlag, int officialPhysicalSimilarityFlag, long categoryId, long regionId, long unitId, String unitFullName, long rankId, long minorSpecializationId, String groupNumber, List<Long> statusIds, long rankTitleId, int generalSpecialization, String recruitmentDateFrom, String recruitmentDateTo) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.EMPLOYEES_STATISTICS.getCode();
	    if (categoryId == CategoriesEnum.MEDICAL_STAFF.getCode())
		reportName = ReportNamesEnum.EMPLOYEES_MEDICAL_STAFF_STATISTICS.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    StringBuilder statusDescs = new StringBuilder();
	    if (statusIds.contains(EmployeeStatusEnum.ON_DUTY.getCode()))
		statusDescs.append(getMessage("label_onDuty")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.ASSIGNED.getCode()))
		statusDescs.append(getMessage("label_assigned")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.SUBJOINED.getCode()))
		statusDescs.append(getMessage("label_subjoined")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.ASSIGNED_EXTERNALLY.getCode()))
		statusDescs.append(getMessage("label_assignedExternally")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.SUBJOINED_EXTERNALLY.getCode()))
		statusDescs.append(getMessage("label_subjoinedExternally")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.MANDATED.getCode()))
		statusDescs.append(getMessage("label_mandated")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.SECONDMENTED.getCode()))
		statusDescs.append(getMessage("label_secondmented")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.PERSONS_SUBJOINED.getCode()))
		statusDescs.append(getMessage("label_assigned")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.PERSONS_SUBJOINED_EXTERNALLY.getCode()))
		statusDescs.append(getMessage("label_assignedExternally")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.MOVED_EXTERNALLY.getCode()))
		statusDescs.append(getMessage("label_movedExternally")).append("/ ");
	    if (statusIds.contains(EmployeeStatusEnum.SERVICE_TERMINATED.getCode()))
		statusDescs.append(getMessage("label_serviceTerminated"));

	    if (statusDescs.length() != 0 && statusDescs.substring(statusDescs.length() - 2).equals("/ ")) {
		statusDescs.delete(statusDescs.length() - 2, statusDescs.length());
	    }

	    parameters.put("P_STATUS_DESCS", statusDescs.toString());
	    parameters.put("P_OFFICIAL_PHYSICAL_FLAG", officialPhysicalFlag);
	    parameters.put("P_OFFICIAL_PHYSICAL_SIMILARITY_FLAG", officialPhysicalSimilarityFlag);
	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_CATEGORY_DESC", categoryId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : CommonService.getCategoryById(categoryId).getDescription());
	    parameters.put("P_REGION_ID", regionId);
	    parameters.put("P_REGION_DESC", regionId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : CommonService.getRegionById(regionId).getDescription());
	    parameters.put("P_UNIT_ID", unitId);
	    parameters.put("P_UNIT_FULL_NAME", unitFullName);
	    parameters.put("P_RANK_ID", rankId);
	    parameters.put("P_RANK_DESC", rankId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : CommonService.getRankById(rankId).getDescription());
	    parameters.put("P_MINOR_SPECIALIZATION_ID", minorSpecializationId);
	    parameters.put("P_MINOR_SPECIALIZATION_DESC", minorSpecializationId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : SpecializationsService.getMinorSpecializationsDescStringByIdsString(minorSpecializationId + ""));
	    parameters.put("P_GROUP_NUMBER", groupNumber);
	    parameters.put("P_STATUS_IDS", statusIds.size() == 0 ? FlagsEnum.ALL.getCode() + "" : statusIds.toString().replaceAll("\\[|\\]", ""));
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_RANK_TITLE_ID", rankTitleId);
	    parameters.put("P_RANK_TITLE_DESC", rankTitleId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : CommonService.getRankTitleById(rankTitleId).getDescription());
	    parameters.put("P_GENERAL_SPECIALIZATION", generalSpecialization);
	    if (recruitmentDateFrom != null) {
		parameters.put("P_RECRUITMENT_DATE_FROM_FLAG", FlagsEnum.ON.getCode());
		parameters.put("P_RECRUITMENT_DATE_FROM", recruitmentDateFrom);
	    } else {
		parameters.put("P_RECRUITMENT_DATE_FROM_FLAG", FlagsEnum.ALL.getCode());
		parameters.put("P_RECRUITMENT_DATE_FROM", HijriDateService.getHijriSysDateString());
	    }
	    if (recruitmentDateTo != null) {
		parameters.put("P_RECRUITMENT_DATE_TO_FLAG", FlagsEnum.ON.getCode());
		parameters.put("P_RECRUITMENT_DATE_TO", recruitmentDateTo);
	    } else {
		parameters.put("P_RECRUITMENT_DATE_TO_FLAG", FlagsEnum.ALL.getCode());
		parameters.put("P_RECRUITMENT_DATE_TO", HijriDateService.getHijriSysDateString());
	    }

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getEmployeesQualificationsBytes(int countryFlag, int currentQualFlag, int currentRecruitmentsFlag, int onDutyFlag, long categoryId, long regionId, long unitId, String unitFullName, long qualMajorId, String qualMajorDesc, long qualMinorId, String qualMinorDesc, long qualLevelId, String graduationPlaceDetailsIds, String graduationPlaceDetailsDescs) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.EMPLOYEES_QUALIFICATIONS.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    StringBuilder statusDescs = new StringBuilder();
	    if (onDutyFlag == FlagsEnum.ON.getCode())
		statusDescs.append(getMessage("label_onDuty"));
	    else if (onDutyFlag == FlagsEnum.OFF.getCode())
		statusDescs.append(getMessage("label_serviceTerminated"));
	    else
		statusDescs.append(getMessage("label_all"));
	    parameters.put("P_STATUS_DESCS", statusDescs.toString());

	    parameters.put("P_COUNTRY_FLAG", countryFlag);
	    parameters.put("P_CURRENT_FLAG", currentQualFlag);
	    parameters.put("P_CUR_REC_SIMILARITY_FLAG", currentRecruitmentsFlag);
	    parameters.put("P_ON_DUTY_FLAG", onDutyFlag);
	    parameters.put("P_CATEGORY_ID", categoryId);

	    parameters.put("P_PHYSICAL_REGION_ID", regionId);
	    parameters.put("P_PHYSICAL_REGION_DESC", regionId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : CommonService.getRegionById(regionId).getDescription());
	    parameters.put("P_PHYSICAL_UNIT_ID", unitId);
	    parameters.put("P_PHYSICAL_UNIT_DESC", unitId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : unitFullName);

	    parameters.put("P_QUAL_MAJOR_ID", qualMajorId);
	    parameters.put("P_QUAL_MAJOR_DESC", qualMajorId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : qualMajorDesc);
	    parameters.put("P_QUAL_MINOR_ID", qualMinorId);
	    parameters.put("P_QUAL_MINOR_DESC", qualMinorId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : qualMinorDesc);

	    parameters.put("P_QUAL_LEVEL_ID", qualLevelId);
	    parameters.put("P_QUAL_LEVEL_DESC", qualLevelId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : TrainingSetupService.getQualificationLevelsDescStringByIdsString(qualLevelId + ""));

	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_GRAD_PLACE_DTLS_IDS", (graduationPlaceDetailsIds == null || graduationPlaceDetailsIds.isEmpty()) ? FlagsEnum.ALL.getCode() + "" : graduationPlaceDetailsIds);
	    parameters.put("P_GRAD_PLACE_DTLS_DESCS", (graduationPlaceDetailsIds == null || graduationPlaceDetailsIds.equals("")) ? getMessage("label_all") : graduationPlaceDetailsDescs);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getEmployeesServiceYearsDataBytes(long categoryId, long physicalRegionId, String physicalUnitHKey, int onDutyFlag, Long serviceYearsBegin, Long serviceYearsEnd) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.EMPLOYEES_SERVICE_YEARS.getCode();

	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_PHYSICAL_REGION_ID", physicalRegionId);
	    parameters.put("P_PHYSICAL_REGION_DESC", physicalRegionId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : CommonService.getRegionById(physicalRegionId).getDescription());
	    parameters.put("P_PHYSICAL_UNIT_HKEY", physicalUnitHKey == null || physicalUnitHKey.isEmpty() ? FlagsEnum.ALL.getCode() + "" : UnitsService.getHKeyPrefix(physicalUnitHKey));
	    parameters.put("P_ON_DUTY_FLAG", onDutyFlag);
	    parameters.put("P_SERVICE_YEARS_BEGIN", serviceYearsBegin != null ? serviceYearsBegin : FlagsEnum.ALL.getCode());
	    parameters.put("P_SERVICE_YEARS_END", serviceYearsEnd != null ? serviceYearsEnd : FlagsEnum.ALL.getCode());
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(getMessage("error_reportPrintingError"));
	}
    }

    public static byte[] getJobModifiedFlagDataBytes(long categoryId, long physicalRegionId, String physicalUnitHKey, int jobModfiedFlag) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.EMPLOYEES_JOB_MODIFIED_FLAG.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_PHYSICAL_REGION_ID", physicalRegionId);
	    parameters.put("P_PHYSICAL_REGION_DESC", physicalRegionId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : CommonService.getRegionById(physicalRegionId).getDescription());
	    parameters.put("P_PHYSICAL_UNIT_HKEY", physicalUnitHKey == null || physicalUnitHKey.isEmpty() ? FlagsEnum.ALL.getCode() + "" : UnitsService.getHKeyPrefix(physicalUnitHKey));
	    parameters.put("P_JOB_MODIFIED_FLAG", jobModfiedFlag);
	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(getMessage("error_reportPrintingError"));
	}

    }

    public static byte[] getEmployeesNotFoundOrNotUpdatedPhotosBytes(long physicalRegionId, String physicalUnitHKey, long categoryId, List<Long> statusIds) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.EMPLOYEES_NOT_FOUND_OR_UPDATED_PHOTO.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    StringBuilder statusDescs = new StringBuilder();
	    if (statusIds.contains(EmployeePhotoStatusEnum.NOT_FOUND.getCode()))
		statusDescs.append(getMessage("label_photoNotFoundInSystem")).append("/ ");
	    if (statusIds.contains(EmployeePhotoStatusEnum.NOT_UPDATED.getCode()))
		statusDescs.append(getMessage("label_photoNotUpdateAfterLastPromotion")).append("/ ");
	    if (statusDescs.length() != 0 && statusDescs.substring(statusDescs.length() - 2).equals("/ ")) {
		statusDescs.delete(statusDescs.length() - 2, statusDescs.length());
	    }
	    parameters.put("P_CATEGORY_ID", categoryId);
	    parameters.put("P_STATUS_IDS", statusIds.size() > 1 ? FlagsEnum.ALL.getCode() : statusIds.get(0));
	    parameters.put("P_STATUS_DESCS", statusDescs.toString());
	    parameters.put("P_PHYSICAL_REGION_ID", physicalRegionId);
	    parameters.put("P_PHYSICAL_REGION_DESC", physicalRegionId == FlagsEnum.ALL.getCode() ? getMessage("label_all") : CommonService.getRegionById(physicalRegionId).getDescription());
	    parameters.put("P_PHYSICAL_UNIT_HKEY", physicalUnitHKey == null || physicalUnitHKey.isEmpty() ? FlagsEnum.ALL.getCode() + "" : UnitsService.getHKeyPrefix(physicalUnitHKey));
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(getMessage("error_reportPrintingError"));
	}

    }
    // ----------------------------------------Employee Photo------------------

    public static void addEmployeePhoto(EmployeePhoto empPhoto, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(empPhoto, session);

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

    public static void updateEmployeePhoto(EmployeePhoto empPhoto, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(empPhoto, session);

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

    public static EmployeePhoto getEmployeePhotoByEmpId(long empId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);

	    List<EmployeePhoto> result = DataAccess.executeNamedQuery(EmployeePhoto.class, QueryNamesEnum.HCM_GET_EMPLOYEE_PHOTO_BY_EMP_ID.getCode(), qParams);

	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // ----------------------------------------------------------service termination---------------------------------------

    public static List<EmployeeData> getEmployeesForFutureServiceTermination() throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EXECUTION_DATE", HijriDateService.getHijriSysDateString());
	    return DataAccess.executeNamedQuery(EmployeeData.class, QueryNamesEnum.HCM_GET_EMPLOYEE_FOR_FUTURE_SERVICE_TERMINATION.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // --------------------------------------- Employees Qualifications -------------------------------------------//

    /*---------------------------Operations---------------------------*/
    private static void addEmployeeQualifications(EmployeeQualificationsData employeeQualificationsData, EmployeeData empData, CustomSession... useSession) throws BusinessException {
	validateEmployeeQualifications(employeeQualificationsData, empData.getBirthDate(), empData.getStatusId());

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(employeeQualificationsData.getEmployeeQualifications(), session);

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

    private static void updateEmployeeQualifications(EmployeeQualificationsData employeeQualificationsData, Date empBirthDate, long employeeStatusId, CustomSession... useSession) throws BusinessException {
	validateEmployeeQualifications(employeeQualificationsData, empBirthDate, employeeStatusId);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(employeeQualificationsData.getEmployeeQualifications(), session);

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

    /*---------------------------Validations--------------------------*/
    public static void validateEmployeeQualifications(EmployeeQualificationsData employeeQualificationsData, Date empBirthDate, long employeeStatusId) throws BusinessException {
	// Validate mandatory fields
	if (employeeQualificationsData.getRecQualLevelId() == null)
	    throw new BusinessException("error_recruitmentQualificationLevelMandatory");

	if (employeeQualificationsData.getCurQualLevelId() == null && employeeStatusId >= EmployeeStatusEnum.ON_DUTY.getCode())
	    throw new BusinessException("error_currentQualificationLevelMandatory");

	// Validate dates
	int currentYear = HijriDateService.getHijriDateYear(HijriDateService.getHijriSysDate());

	if (employeeQualificationsData.getRecGraduationYear() != null && employeeQualificationsData.getRecGraduationYear() > currentYear)
	    throw new BusinessException("error_invalidRecruitmentGraduationYear");

	if (employeeQualificationsData.getCurGraduationYear() != null && employeeQualificationsData.getCurGraduationYear() > currentYear)
	    throw new BusinessException("error_invalidCurrentGraduationYear");

	int birthYear = HijriDateService.getHijriDateYear(empBirthDate);

	if (employeeQualificationsData.getRecGraduationYear() != null && employeeQualificationsData.getRecGraduationYear() <= birthYear)
	    throw new BusinessException("error_graduationYearMustBeAfterBirthYear");

	if (employeeQualificationsData.getCurGraduationYear() != null && employeeQualificationsData.getCurGraduationYear() <= birthYear)
	    throw new BusinessException("error_graduationYearMustBeAfterBirthYear");
    }

    /*---------------------------Queries------------------------------*/
    public static EmployeeQualificationsData getEmployeeQualificationsByEmpId(long employeeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", employeeId);
	    List<EmployeeQualificationsData> result = DataAccess.executeNamedQuery(EmployeeQualificationsData.class, QueryNamesEnum.HCM_GET_EMPLOYEE_QUALIFICATIONS_BY_EMP_ID.getCode(), qParams);

	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static boolean checkUsageOfGraduationPlaceInEmployeeQualifications(long graduationPlaceId, long graduationPlaceDetailId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_GRADUATION_PLACE_ID", graduationPlaceId);
	    qParams.put("P_GRADUATION_PLACE_DETAIL_ID", graduationPlaceDetailId);

	    return DataAccess.executeNamedQuery(EmployeeQualificationsData.class, QueryNamesEnum.HCM_EMPLOYEE_QUALIFICATIONS_SEARCH_BY_GRADUATION_PLACE.getCode(), qParams).size() > 0;

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static boolean checkUsageOfQualificationInEmployeeQualifications(long qualMajorSpecId, long qualMinorSpecId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_QUAL_MAJOR_SPEC_ID", qualMajorSpecId);
	    qParams.put("P_QUAL_MINOR_SPEC_ID", qualMinorSpecId);

	    return DataAccess.executeNamedQuery(EmployeeQualificationsData.class, QueryNamesEnum.HCM_EMPLOYEE_QUALIFICATIONS_SEARCH_BY_QUALIFICATION_SPEC.getCode(), qParams).size() > 0;

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /************************************************************** Migration Methods ************************/
    public static void migrateSoldiersData(List<EmployeeData> soldiersData, List<EmployeeQualificationsData> qualificationsData) throws BusinessException {
	if (soldiersData != null && soldiersData.size() > 0) {

	    CustomSession session = DataAccess.getSession();

	    EmployeeData empData = null;

	    try {
		session.beginTransaction();

		for (int i = 0; i < soldiersData.size(); i++) {
		    empData = soldiersData.get(i);

		    if (empData.getGender().equals("F"))
			throw new BusinessException("error_soldiersmustbemale");

		    empData.setCategoryId(CategoriesEnum.SOLDIERS.getCode());
		    empData.setExceptionalRecruitmentFlag(FlagsEnum.OFF.getCode());

		    if (empData.getRecruitmentRankId() != null) {
			if (empData.getRecruitmentRankId().intValue() == RanksEnum.SOLDIER.getCode() || empData.getRecruitmentRankId().intValue() == RanksEnum.FIRST_SOLDIER.getCode()) {
			    empData.setStatusId(EmployeeStatusEnum.NEW_STUDENT_SOLDIER.getCode());
			} else {
			    empData.setStatusId(EmployeeStatusEnum.NEW_STUDENT_RANKED_SOLDIER.getCode());
			}
		    }

		    addEmployee(empData, qualificationsData.get(i), session);
		}

		session.commitTransaction();
	    } catch (BusinessException e) {
		session.rollbackTransaction();
		throw new BusinessException("error_recWSErrorMessage", new Object[] { empData.getSocialID(), getEmployeeFullName(empData), getMessage(e.getMessage()) });
	    } finally {
		session.close();
	    }
	}
    }

    public static String getEmployeeFullName(EmployeeData empData) {
	return empData.getFirstName() + " " + empData.getSecondName() + (empData.getThirdName() == null || empData.getThirdName().isEmpty() ? "" : " " + empData.getThirdName()) + " " + empData.getLastName();
    }

    /************************************************************** Yaqeen integration **************************************************************/
    public static PersonInfoDetailedResult retrieveEmployeeInfoFromYaqeen(String socialId, String birthDate, String loginSocialId, String clientIpAddress) throws BusinessException {
	validateYaqeenMandatoryFields(socialId, birthDate);
	try {
	    PersonInfoRequest personInfoRequest = new PersonInfoRequest();
	    personInfoRequest.setIdNumber(socialId);
	    personInfoRequest.setOperatorId(loginSocialId);
	    personInfoRequest.setIdType(socialId.charAt(0) == '1' ? IdType.C : (socialId.charAt(0) == '2' ? IdType.R : IdType.V));
	    personInfoRequest.setDateOfBirth(personInfoRequest.getIdType() == IdType.C ? birthDate.replace('/', '-') : HijriDateService.hijriToGregDateString(birthDate).replace('/', '-'));
	    personInfoRequest.setClientIpAddress(clientIpAddress);
	    String systemCode = ETRConfigurationService.getYaqeenSystemCode();
	    personInfoRequest.setSystemCode(systemCode);
	    Yakeen4BorderGuardService yakeen4BorderGuardService = new Yakeen4BorderGuardService();
	    YaqeenServices yaqeenService = yakeen4BorderGuardService.getYakeen4BorderGuardPort();
	    return yaqeenService.getPersonInfoDetailed(personInfoRequest);
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    if (e instanceof Yakeen4BorderGuardFaultException)
		throw new BusinessException("error_cannotRetrieveInformationsFromYakeenAsDataEnteredIsWrong");
	    if (e instanceof BusinessException_Exception || e instanceof DataValidationException || e instanceof WebServiceException)
		throw new BusinessException("error_cannotRetrieveInformationsFromYakeenAsItIsNotAvailableNow");
	    throw new BusinessException("error_integrationError");
	}
    }

    public static void updateEmployeeDataFromYaqeen(EmployeeData emp, String loginEmployeeSocialId, String clientIpAddress) throws BusinessException {
	PersonInfoDetailedResult personInfoDetailedResult = retrieveEmployeeInfoFromYaqeen(emp.getSocialID(), emp.getBirthDateString(), loginEmployeeSocialId, clientIpAddress);

	emp.setFirstName(personInfoDetailedResult.getFirstName());
	emp.setSecondName(personInfoDetailedResult.getFatherName());
	emp.setThirdName(personInfoDetailedResult.getGrandFatherName());
	emp.setLastName(personInfoDetailedResult.getFamilyName());
	emp.setFirstNameEn(personInfoDetailedResult.getEnglishFirstName());
	emp.setSecondNameEn(personInfoDetailedResult.getEnglishSecondName());
	emp.setThirdNameEn(personInfoDetailedResult.getEnglishThirdName());
	emp.setLastNameEn(personInfoDetailedResult.getEnglishLastName());

	emp.setSocialIDIssueDate(HijriDateService.getHijriDate(personInfoDetailedResult.getIdIssueDateH().replace('-', '/')));
	emp.setSocialIDExpiryDate(HijriDateService.getHijriDate(personInfoDetailedResult.getIdExpiryDateH().replace('-', '/')));
	emp.setGender(personInfoDetailedResult.getGender().toString());

	List<SocialIdIssuePlace> socialIdIssuePlace = CommonService.getSocialIdIssuePlacesByExactDescription(personInfoDetailedResult.getIdIssuePlace().getValue());
	emp.setSocialIDIssuePlaceDesc(socialIdIssuePlace == null || socialIdIssuePlace.isEmpty() ? null : socialIdIssuePlace.get(0).getDescription());
	emp.setSocialIDIssuePlaceID(socialIdIssuePlace == null || socialIdIssuePlace.isEmpty() ? null : socialIdIssuePlace.get(0).getId());

	Country country = CountryService.getCountryByYaqeenName(personInfoDetailedResult.getNationalityDesc());
	emp.setCountryId(country.getId());
	emp.setNationality(country.getNationality());
    }

    private static void validateYaqeenMandatoryFields(String socialId, String birthDate) throws BusinessException {
	if (socialId == null || socialId.trim().equals(""))
	    throw new BusinessException("error_socialIDMandatory");
	if (socialId.length() != 10)
	    throw new BusinessException("error_invalidSocialID");
	if (birthDate == null || birthDate.equals(""))
	    throw new BusinessException("error_birthDateMandatory");
    }

    public static boolean isSocialIdExpired(EmployeeData employeeData) throws BusinessException {
	if (employeeData != null && employeeData.getSocialIDExpiryDate() != null && HijriDateService.getHijriSysDate().after(employeeData.getSocialIDExpiryDate()))
	    return true;

	return false;
    }

    public static boolean isSocialIdExpiryDateInRenewalPeriodWarning(EmployeeData employeeData) throws BusinessException {
	if (employeeData != null && employeeData.getSocialIDExpiryDate() != null) {
	    int diffDays = Math.abs(HijriDateService.hijriDateDiff(HijriDateService.getHijriSysDate(), employeeData.getSocialIDExpiryDate()));
	    if (diffDays <= ETRConfigurationService.getSocialIdRenewalPeriodWarning()) {
		return true;
	    }
	}

	return false;
    }

    // --------------------------------------- Employees Data Extra Transaction -------------------------------------------//
    private static void validateEmployeeDataExtraTransaction(EmployeeExtraTransactionData employeeExtraTransactionData) throws BusinessException {
	Object[] params = new Object[] {};
	if (employeeExtraTransactionData.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_SOCIAL_STATUS.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId()) {
	    params = new Object[] { getMessage("label_socialStatus") };
	    if (employeeExtraTransactionData.getSocialStatus() == null)
		throw new BusinessException("error_socialStatusMandatory");
	}
	if (employeeExtraTransactionData.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_SALARY_RANK.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId()) {
	    params = new Object[] { getMessage("label_salaryRank") };
	    if (employeeExtraTransactionData.getSalaryRankId() == null || employeeExtraTransactionData.getSalaryDegreeId() == null)
		throw new BusinessException("error_salaryRankMandatory");
	}
	if (employeeExtraTransactionData.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_RANK_TITLE.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId()) {
	    params = new Object[] { getMessage("label_rankTitle") };
	    if (employeeExtraTransactionData.getRankTitleId() == null)
		throw new BusinessException("error_rankTitleMandatory");
	}
	if (employeeExtraTransactionData.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_GENERAL_SPECIALIZATION.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId()) {
	    params = new Object[] { getMessage("label_generalSpec") };
	    if (employeeExtraTransactionData.getGeneralSpecialization() == null)
		throw new BusinessException("error_generalSpecializationMandatory");
	}
	if (employeeExtraTransactionData.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEE_MEDICAL_STAFF_DATA.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId()) {
	    params = new Object[] { getMessage("label_medicalStaffRankData") };
	    if (employeeExtraTransactionData.getMedStaffDegreeId() == null || employeeExtraTransactionData.getMedStaffRankId() == null || employeeExtraTransactionData.getMedStaffLevelId() == null)
		throw new BusinessException("error_medStaffDataMandatory");
	}
	if (employeeExtraTransactionData.getTransactionTypeId() != CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_SOCIAL_STATUS.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId() && (employeeExtraTransactionData.getDecisionNumber() == null || employeeExtraTransactionData.getDecisionNumber().equals("")))
	    throw new BusinessException("error_decisionNumberMandatory");
	Date sysDate = HijriDateService.getHijriSysDate();
	if (employeeExtraTransactionData.getDecisionDate() != null && employeeExtraTransactionData.getDecisionDate().after(sysDate))
	    throw new BusinessException("error_decDateAfterMandatory");
	if (employeeExtraTransactionData.getEffectiveDate().after(sysDate))
	    throw new BusinessException("error_incorrectEffectiveDate");
	List<EmployeeExtraTransactionData> transactionsWithTheSameEffectiveDate = getEmployeeDataExtraTransactionByEmpIdAndTransactionTypeAndEffectiveDate(employeeExtraTransactionData.getEmpId(), employeeExtraTransactionData.getTransactionTypeId(), employeeExtraTransactionData.getEffectiveDateString());
	if (transactionsWithTheSameEffectiveDate != null && transactionsWithTheSameEffectiveDate.size() != 0)
	    throw new BusinessException("error_effectiveDateMustBeUniqueForSameTransaction", params);
	List<EmployeeExtraTransactionData> duplicatedEmployeeExtraTransactionDataWithSameDecDateAndNumber = getEmployeeExtraTransactionByEmpIdAndDecisionDateAndDecisionNumberAndTransactionTypeId(employeeExtraTransactionData.getEmpId(), employeeExtraTransactionData.getDecisionNumber(), employeeExtraTransactionData.getDecisionDateString(), employeeExtraTransactionData.getTransactionTypeId());
	if (duplicatedEmployeeExtraTransactionDataWithSameDecDateAndNumber != null && duplicatedEmployeeExtraTransactionDataWithSameDecDateAndNumber.size() != 0)
	    throw new BusinessException("error_decDateAndDecNumberMustBeUnique", params);
	EmployeeExtraTransactionData employeeExtraTransactionDataBeforeCurrentDecDate = getBeforeEffDateEmployeeExtraTransactionData(employeeExtraTransactionData.getEmpId(), employeeExtraTransactionData.getEffectiveDateString(), employeeExtraTransactionData.getTransactionTypeId());
	EmployeeExtraTransactionData employeeExtraTransactionDataAfterCurrentDecDate = getAfterEffDateEmployeeExtraTransactionData(employeeExtraTransactionData.getEmpId(), employeeExtraTransactionData.getEffectiveDateString(), employeeExtraTransactionData.getTransactionTypeId());
	if (checkEmployeeExtraTransactionDataAtBeforeAndAfterDecDates(employeeExtraTransactionData, employeeExtraTransactionDataBeforeCurrentDecDate, employeeExtraTransactionDataAfterCurrentDecDate))
	    throw new BusinessException("error_dataCantBeChangedAtSameDate", params);
    }

    private static void validateStopTransaction(EmployeeExtraTransactionData employeeExtraTransactionData) throws BusinessException {
	if (employeeExtraTransactionData.getEndDate() == null)
	    throw new BusinessException("error_endDateIsMandatory");
	if (employeeExtraTransactionData.getEndDate().after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_endDateAfterSysDate");
	if (!employeeExtraTransactionData.getEndDate().after(employeeExtraTransactionData.getEffectiveDate()))
	    throw new BusinessException("error_endDateBeforeEffectiveDate");
    }

    public static void validateSelectedEmployeeForExtraTransaction(EmployeeData selectedEmployee) throws BusinessException {
	if (!(selectedEmployee.getStatusId() >= EmployeeStatusEnum.ON_DUTY.getCode() && selectedEmployee.getStatusId() < EmployeeStatusEnum.MOVED_EXTERNALLY.getCode())) {
	    throw new BusinessException("error_empMustBeOnDuty");
	} else if (selectedEmployee.getCategoryId() != CategoriesEnum.OFFICERS.getCode() && selectedEmployee.getCategoryId() != CategoriesEnum.SOLDIERS.getCode())
	    throw new BusinessException("error_empMustBeMilitary");
    }

    public static void constructEmployeeMedicalStaffData(EmployeeExtraTransactionData employeeExtraTransactionData, EmployeeMedicalStaffData employeeMedicalStaffData) {
	employeeMedicalStaffData.setMedStaffDegreeId(employeeExtraTransactionData.getMedStaffDegreeId());
	employeeMedicalStaffData.setMedStaffLevelId(employeeExtraTransactionData.getMedStaffLevelId());
	employeeMedicalStaffData.setMedStaffRankId(employeeExtraTransactionData.getMedStaffRankId());
    }

    public static void addEmployeeDataExtraTransaction(EmployeeData employee, EmployeeExtraTransactionData employeeExtraTransactionData, EmployeeMedicalStaffData employeeMedicalStaffData, CustomSession... useSession) throws BusinessException {
	validateEmployeeDataExtraTransaction(employeeExtraTransactionData);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<EmployeeExtraTransactionData> employeeExtraTransactions = getEmployeeDataExtraTransactionByEmpIdAndTransactionType(employee.getEmpId(), employeeExtraTransactionData.getTransactionTypeId());
	    if (employeeExtraTransactions == null || employeeExtraTransactions.size() == 0 ||
		    employeeExtraTransactions.get(0).getEffectiveDate().before(employeeExtraTransactionData.getEffectiveDate())) {
		if (employeeExtraTransactionData.getSocialStatus() != null)
		    employee.setSocialStatus(employeeExtraTransactionData.getSocialStatus());
		if (employeeExtraTransactionData.getGeneralSpecialization() != null)
		    employee.setGeneralSpecialization(employeeExtraTransactionData.getGeneralSpecialization());
		if (employeeExtraTransactionData.getRankTitleId() != null)
		    employee.setRankTitleId(employeeExtraTransactionData.getRankTitleId());
		if (employeeExtraTransactionData.getSalaryRankId() != null)
		    employee.setSalaryRankId(employeeExtraTransactionData.getSalaryRankId());
		if (employeeExtraTransactionData.getSalaryDegreeId() != null)
		    employee.setSalaryDegreeId(employeeExtraTransactionData.getSalaryDegreeId());
		updateEmployee(employee, session);
		if (employeeMedicalStaffData != null) {
		    employeeMedicalStaffData.setEmpId(employeeExtraTransactionData.getEmpId());
		    addModifyEmployeeMedicalStaffData(employeeExtraTransactionData, employeeMedicalStaffData, session);
		}
		EmployeeLog log = new EmployeeLog.Builder().setSocialStatus(employeeExtraTransactionData.getSocialStatus()).setGeneralSpecialization(employeeExtraTransactionData.getGeneralSpecialization()).setRankTitleId(employeeExtraTransactionData.getRankTitleId()).setSalaryRankId(employeeExtraTransactionData.getSalaryRankId()).setSalaryDegreeId(employeeExtraTransactionData.getSalaryDegreeId())
			.setMedStaffDegreeId(employeeExtraTransactionData.getMedStaffDegreeId()).setMedStaffLevelId(employeeExtraTransactionData.getMedStaffLevelId()).setMedStaffRankId(employeeExtraTransactionData.getMedStaffRankId())
			.constructCommonFields(employee.getEmpId(), FlagsEnum.ON.getCode(), employeeExtraTransactionData.getDecisionNumber(), employeeExtraTransactionData.getDecisionDate(), employeeExtraTransactionData.getEffectiveDate(), DataAccess.getTableName(EmployeeExtraTransaction.class)).build();
		LogService.logEmployeeData(log, session);
	    }

	    DataAccess.addEntity(employeeExtraTransactionData.getEmployeeExtraTransaction(), session);
	    employeeExtraTransactionData.setId(employeeExtraTransactionData.getEmployeeExtraTransaction().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    employeeExtraTransactionData.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void stopEmployeeDataExtraTransaction(EmployeeExtraTransactionData employeeExtraTransactionData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    validateStopTransaction(employeeExtraTransactionData);
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<EmployeeExtraTransactionData> employeeExtraTransactions = getEmployeeDataExtraTransactionByEmpIdAndTransactionType(employeeExtraTransactionData.getEmpId(), employeeExtraTransactionData.getTransactionTypeId());
	    if (employeeExtraTransactions.get(0).getId().longValue() == employeeExtraTransactionData.getId().longValue()) {
		EmployeeData employee = getEmployeeData(employeeExtraTransactionData.getEmpId());
		if (employeeExtraTransactionData.getSalaryRankId() != null) {
		    employee.setSalaryRankId(null);
		    employee.setSalaryDegreeId(null);
		    updateEmployee(employee, session);
		}
	    }
	    DataAccess.updateEntity(employeeExtraTransactionData.getEmployeeExtraTransaction(), session);
	    EmployeeLog log = new EmployeeLog.Builder().setSalaryRankId(Long.parseLong(FlagsEnum.ALL.getCode() + "")).setSalaryDegreeId(Long.parseLong(FlagsEnum.ALL.getCode() + ""))
		    .constructCommonFields(employeeExtraTransactionData.getEmpId(), FlagsEnum.ON.getCode(), employeeExtraTransactionData.getDecisionNumber(), employeeExtraTransactionData.getDecisionDate(), employeeExtraTransactionData.getEndDate(), DataAccess.getTableName(EmployeeExtraTransaction.class)).build();
	    LogService.logEmployeeData(log, session);
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    employeeExtraTransactionData.setEndDate(null);
	    if (e instanceof BusinessException)
		throw (BusinessException) e;
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static EmployeeMedicalStaffData getEmployeeMedicalStaffDataByEmpId(Long empId) throws BusinessException {
	List<EmployeeMedicalStaffData> resultList = searchEmployeeMedicalStaffDataByEmpId(empId);
	return (resultList == null || resultList.size() == 0) ? null : resultList.get(0);
    }

    public static List<EmployeeExtraTransactionData> getEmployeeDataExtraTransactionByEmpIdAndTransactionType(Long empId, Long transactionType) throws BusinessException {
	return searchEmployeeExtraTransactionData(null, empId, transactionType, null, null, null, null, null, null, null, null, null, null, null);
    }

    public static List<EmployeeExtraTransactionData> getEmployeeExtraTransactionByDecisionNumber(String decisionNumber) throws BusinessException {
	return searchEmployeeExtraTransactionData(null, null, null, decisionNumber, null, null, null, null, null, null, null, null, null, null);
    }

    private static List<EmployeeExtraTransactionData> getEmployeeExtraTransactionByEmpIdAndDecisionDateAndDecisionNumberAndTransactionTypeId(Long empId, String decisionNumber, String decisionDate, Long transactionType) throws BusinessException {
	return searchEmployeeExtraTransactionData(null, empId, transactionType, decisionNumber, decisionDate, null, null, null, null, null, null, null, null, null);
    }

    public static List<EmployeeExtraTransactionData> getEmployeeDataExtraTransactionByEmpIdAndTransactionTypeAndEffectiveDate(Long empId, Long transactionType, String effectiveDate) throws BusinessException {
	return searchEmployeeExtraTransactionData(null, empId, transactionType, null, null, null, null, null, null, null, null, null, null, effectiveDate);
    }

    private static Boolean checkEmployeeExtraTransactionDataAtBeforeAndAfterDecDates(EmployeeExtraTransactionData employeeExtraTransactionData, EmployeeExtraTransactionData dateBeforeExtraTransactionData, EmployeeExtraTransactionData dateAfterExtraTransactionData) throws BusinessException {
	if (employeeExtraTransactionData.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_SOCIAL_STATUS.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId()) {
	    if ((dateBeforeExtraTransactionData != null && dateBeforeExtraTransactionData.getSocialStatus() == employeeExtraTransactionData.getSocialStatus()) || (dateAfterExtraTransactionData != null && dateAfterExtraTransactionData.getSocialStatus() == employeeExtraTransactionData.getSocialStatus()))
		return true;
	} else if (employeeExtraTransactionData.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_SALARY_RANK.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId()) {
	    if ((dateBeforeExtraTransactionData != null && dateBeforeExtraTransactionData.getSalaryRankId() == employeeExtraTransactionData.getSalaryRankId() && dateBeforeExtraTransactionData.getSalaryDegreeId() == employeeExtraTransactionData.getSalaryDegreeId())
		    || (dateAfterExtraTransactionData != null && dateAfterExtraTransactionData.getSalaryRankId() == employeeExtraTransactionData.getSalaryRankId() && dateAfterExtraTransactionData.getSalaryDegreeId() == employeeExtraTransactionData.getSalaryDegreeId()))
		return true;
	} else if (employeeExtraTransactionData.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_GENERAL_SPECIALIZATION.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId()) {
	    if ((dateBeforeExtraTransactionData != null && dateBeforeExtraTransactionData.getGeneralSpecialization() == employeeExtraTransactionData.getGeneralSpecialization()) || (dateAfterExtraTransactionData != null && dateAfterExtraTransactionData.getGeneralSpecialization() == employeeExtraTransactionData.getGeneralSpecialization()))
		return true;
	} else if (employeeExtraTransactionData.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEES_EXTRA_DATA_RANK_TITLE.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId()) {
	    if ((dateBeforeExtraTransactionData != null && dateBeforeExtraTransactionData.getRankTitleId() == employeeExtraTransactionData.getRankTitleId()) || (dateAfterExtraTransactionData != null && dateAfterExtraTransactionData.getRankTitleId() == employeeExtraTransactionData.getRankTitleId()))
		return true;
	} else if (employeeExtraTransactionData.getTransactionTypeId() == CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEE_MEDICAL_STAFF_DATA.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId()) {
	    if ((dateBeforeExtraTransactionData != null && dateBeforeExtraTransactionData.getMedStaffDegreeId().equals(employeeExtraTransactionData.getMedStaffDegreeId()) && dateBeforeExtraTransactionData.getMedStaffRankId().equals(employeeExtraTransactionData.getMedStaffRankId()) && dateBeforeExtraTransactionData.getMedStaffLevelId().equals(employeeExtraTransactionData.getMedStaffLevelId()))
		    || (dateAfterExtraTransactionData != null && dateAfterExtraTransactionData.getMedStaffDegreeId().equals(employeeExtraTransactionData.getMedStaffDegreeId()) && dateAfterExtraTransactionData.getMedStaffRankId().equals(employeeExtraTransactionData.getMedStaffRankId()) && dateAfterExtraTransactionData.getMedStaffLevelId().equals(employeeExtraTransactionData.getMedStaffLevelId())))
		return true;
	}
	return false;
    }

    private static void addModifyEmployeeMedicalStaffData(EmployeeExtraTransactionData employeeExtraTransactionData, EmployeeMedicalStaffData employeeMedicalStaffData, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    EmployeeMedicalStaffData exitingEmployeeMedicalStaffData = getEmployeeMedicalStaffDataByEmpId(employeeExtraTransactionData.getEmpId());
	    if (exitingEmployeeMedicalStaffData == null)
		DataAccess.addEntity(employeeMedicalStaffData.getEmployeeMedicalStuff(), session);
	    else {
		if (employeeExtraTransactionData.getMedStaffDegreeId() != null)
		    exitingEmployeeMedicalStaffData.setMedStaffDegreeId(employeeExtraTransactionData.getMedStaffDegreeId());
		if (employeeExtraTransactionData.getMedStaffLevelId() != null)
		    exitingEmployeeMedicalStaffData.setMedStaffLevelId(employeeExtraTransactionData.getMedStaffLevelId());
		if (employeeExtraTransactionData.getMedStaffRankId() != null)
		    exitingEmployeeMedicalStaffData.setMedStaffRankId(employeeExtraTransactionData.getMedStaffRankId());
		DataAccess.updateEntity(exitingEmployeeMedicalStaffData.getEmployeeMedicalStuff(), session);
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

    private static List<EmployeeExtraTransactionData> searchEmployeeExtraTransactionData(Long id, Long empId, Long transactionType, String decisionNumber, String decisionDate, Long rankTitleId, Long salaryRankId, Long salaryDegreeId, Integer socialStatus, Integer generalSpecialization, Long medStaffRankId, Long medStaffLevelId, Long medStaffDegreeId, String effectiveDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", id == null ? FlagsEnum.ALL.getCode() : id);
	    qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	    qParams.put("P_DECISION_NUMBER", decisionNumber == null ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    if (decisionDate == null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.OFF.getCode());
		qParams.put("P_DECISION_DATE", decisionDate);
	    }
	    if (effectiveDate == null) {
		qParams.put("P_EFFECTIVE_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EFFECTIVE_DATE", HijriDateService.getHijriSysDateString());
	    } else {
		qParams.put("P_EFFECTIVE_DATE_FLAG", FlagsEnum.OFF.getCode());
		qParams.put("P_EFFECTIVE_DATE", effectiveDate);
	    }
	    qParams.put("P_TRANSACTION_TYPE", transactionType == null ? FlagsEnum.ALL.getCode() + "" : transactionType);
	    qParams.put("P_RANK_TITLE_ID", rankTitleId == null ? FlagsEnum.ALL.getCode() + "" : rankTitleId);
	    qParams.put("P_SALARY_RANK_ID", salaryRankId == null ? FlagsEnum.ALL.getCode() + "" : salaryRankId);
	    qParams.put("P_SALARY_DEGREE_ID", salaryDegreeId == null ? FlagsEnum.ALL.getCode() + "" : salaryDegreeId);
	    qParams.put("P_SOCIAL_STATUS", socialStatus == null ? FlagsEnum.ALL.getCode() + "" : socialStatus);
	    qParams.put("P_GENERAL_SPECIALIZATION", generalSpecialization == null ? FlagsEnum.ALL.getCode() + "" : generalSpecialization);
	    qParams.put("P_MED_STAFF_RANK_ID", medStaffRankId == null ? FlagsEnum.ALL.getCode() + "" : medStaffRankId);
	    qParams.put("P_MED_STAFF_LEVEL_ID", medStaffLevelId == null ? FlagsEnum.ALL.getCode() + "" : medStaffLevelId);
	    qParams.put("P_MED_STAFF_DEGREE_ID", medStaffDegreeId == null ? FlagsEnum.ALL.getCode() + "" : medStaffDegreeId);
	    return DataAccess.executeNamedQuery(EmployeeExtraTransactionData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEES_EXTRA_TRANSACTION_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<EmployeeMedicalStaffData> searchEmployeeMedicalStaffDataByEmpId(Long empId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	    return DataAccess.executeNamedQuery(EmployeeMedicalStaffData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEE_MEDICAL_STAFF_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static EmployeeExtraTransactionData getBeforeEffDateEmployeeExtraTransactionData(Long empId, String effectiveDate, Long transactionType) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_EFFECTIVE_DATE", effectiveDate);
	    qParams.put("P_TRANSACTION_TYPE", transactionType == null ? FlagsEnum.ALL.getCode() + "" : transactionType);
	    List<EmployeeExtraTransactionData> result;
	    result = DataAccess.executeNamedQuery(EmployeeExtraTransactionData.class, QueryNamesEnum.HCM_GET_BEFORE_EFFECTIVE_DATE_EMPLOYEES_EXTRA_TRANSACTION_DATA.getCode(), qParams);
	    return (result == null || result.size() == 0) ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    private static EmployeeExtraTransactionData getAfterEffDateEmployeeExtraTransactionData(Long empId, String effectiveDate, Long transactionType) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_EFFECTIVE_DATE", effectiveDate);
	    qParams.put("P_TRANSACTION_TYPE", transactionType == null ? FlagsEnum.ALL.getCode() + "" : transactionType);
	    List<EmployeeExtraTransactionData> result = DataAccess.executeNamedQuery(EmployeeExtraTransactionData.class, QueryNamesEnum.HCM_GET_AFTER_EFFECTIVE_DATE_EMPLOYEES_EXTRA_TRANSACTION_DATA.getCode(), qParams);
	    return (result == null || result.size() == 0) ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

}