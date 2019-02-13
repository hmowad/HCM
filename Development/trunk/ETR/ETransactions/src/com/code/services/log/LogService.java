package com.code.services.log;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.log.EmployeeLog;
import com.code.dal.orm.log.EmployeeLogData;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

public class LogService extends BaseService {
    /**
     * private constructor to prevent instantiation
     * 
     */
    private LogService() {

    }

    /***************************** Object *****************************/
    /*---------------------------Operations---------------------------*/
    /*---------------------------Validations--------------------------*/
    /*---------------------------Queries------------------------------*/
    /*---------------------------Reports------------------------------*/

    /************************************* EmployeeLog ***************************************************/
    /*--------------------------------------Operations---------------------------------------------------*/
    /**
     * Add a EmployeeLogData object to database
     * 
     * @param empDate
     *            employee object
     * @param effectiveHijriDate
     * @param decisionNumber
     * @param decisionDate
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void logEmployeeData(EmployeeData empData, Date effectiveHijriDate, String decisionNumber, Date decisionDate, CustomSession... useSession) throws BusinessException {
	EmployeeLogData employeeLogData = constructEmployeeLogData(empData, effectiveHijriDate, decisionNumber, decisionDate);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    DataAccess.addEntity(employeeLogData.getEmployeelog(), session);
	    employeeLogData.setId(employeeLogData.getEmployeelog().getId());

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    employeeLogData.setId(null);
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
     * Add a EmployeeLog object to database
     * 
     * @param EmployeeLog
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void logEmployeeData(EmployeeLog employeeLog, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    DataAccess.addEntity(employeeLog, session);

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
     * construct and add a new employeeLogData object
     * 
     * @param empDate
     *            employee object
     * @param effectiveHijriDate
     * @param decisionNumber
     * @param decisionDate
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static EmployeeLogData constructEmployeeLogData(EmployeeData empData, Date effectiveHijriDate, String decisionNumber, Date decisionDate) throws BusinessException {
	try {
	    EmployeeLogData employeeLogData = new EmployeeLogData();
	    employeeLogData.setEmpId(empData.getEmpId());
	    employeeLogData.setJobId(empData.getJobId());

	    if (empData.getJobId() == null)
		employeeLogData.setBasicJobNameId(null);
	    else {
		// TODO wait until we move basic job name to test branch
		// employeeLogData.setBasicJobNameId(JobsService.getJobById(empData.getJobId()).getBasicJobNameId());
	    }

	    employeeLogData.setPhysicalUnitId(empData.getPhysicalUnitId());
	    employeeLogData.setOfficialUnitId(empData.getOfficialUnitId());
	    employeeLogData.setRankId(empData.getRankId());
	    employeeLogData.setRankTitleId(empData.getRankTitleId());
	    employeeLogData.setSalaryRankId(empData.getSalaryRankId());
	    employeeLogData.setDegreeId(empData.getDegreeId());
	    employeeLogData.setSocialStatus(empData.getSocialStatus());
	    employeeLogData.setGeneralSpecialization(empData.getGeneralSpecialization());

	    employeeLogData.setEffectiveGregDate(HijriDateService.hijriToGregDate(effectiveHijriDate));
	    employeeLogData.setEffectiveHijriDate(effectiveHijriDate);
	    employeeLogData.setDecisionNumber(decisionNumber);
	    employeeLogData.setDecisionDate(decisionDate);

	    employeeLogData.getEmployeelog().setInsertionTime(System.currentTimeMillis());

	    return employeeLogData;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*-------------------------------------------------Queries-----------------------------------------------------------*/

    /**
     * Wrapper for {@link #searchEmployeesLog()} to get employeesLog
     * 
     * @param id
     * @param empId
     * @param rankId
     * @param jobId
     * @param basicJobNameId
     * @param physicalUnitId
     * @param degreeId
     * @param salaryRankId
     * @param socialStatus
     * @param rankTitleId
     * @param generalSpecialization
     * @param effectiveHijriDate
     * @param effectiveGregDate
     * @param decisionNumber
     * @param decisionDate
     * @return array list of employeesLogData objects
     * @throws BusinessException
     */
    public static List<EmployeeLogData> getEmployeesLog(long id, long empId, long rankId, long jobId, long basicJobNameId, long physicalUnitId, long degreeId, long salaryRankId, int socialStatus, long rankTitleId, int generalSpecialization, Date effectiveHijriDate, Date effectiveGregDate, String decisionNumber, Date decisionDate) throws BusinessException {
	return searchEmployeesLog(id, empId, rankId, jobId, basicJobNameId, physicalUnitId, degreeId, salaryRankId, socialStatus, rankTitleId, generalSpecialization, effectiveHijriDate, effectiveGregDate, decisionNumber, decisionDate);
    }

    /**
     * search Employees Log
     * 
     * @param id
     * @param empId
     * @param rankId
     * @param jobId
     * @param basicJobNameId
     * @param physicalUnitId
     * @param degreeId
     * @param salaryRankId
     * @param socialStatus
     * @param rankTitleId
     * @param generalSpecialization
     * @param effectiveHijriDate
     * @param effectiveGregDate
     * @param decisionNumber
     * @param decisionDate
     * @return array list of employeesLogData objects
     * @throws BusinessException
     */
    private static List<EmployeeLogData> searchEmployeesLog(long id, long empId, long rankId, long jobId, long basicJobNameId, long physicalUnitId, long degreeId, long salaryRankId, int socialStatus, long rankTitleId, int generalSpecialization, Date effectiveHijriDate, Date effectiveGregDate, String decisionNumber, Date decisionDate) throws BusinessException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	try {
	    qParams.put("P_ID", id);
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_RANK_ID", rankId);
	    qParams.put("P_JOB_ID", jobId);
	    qParams.put("P_BASIC_JOB_NAME_ID", basicJobNameId);
	    qParams.put("P_PHYSICAL_UNIT_ID", physicalUnitId);
	    qParams.put("P_DEGREE_ID", degreeId);
	    qParams.put("P_SALARY_RANK_ID", salaryRankId);
	    qParams.put("P_SOCIAL_STATUS", socialStatus);
	    qParams.put("P_RANK_TITLE_ID", rankTitleId);
	    qParams.put("P_GENERAL_SPECIALIZATION", generalSpecialization);

	    if (effectiveHijriDate != null) {
		qParams.put("P_EFFECTIVE_HIJRI_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_EFFECTIVE_HIJRI_DATE", HijriDateService.getHijriDateString(effectiveHijriDate));
	    } else {
		qParams.put("P_EFFECTIVE_HIJRI_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EFFECTIVE_HIJRI_DATE", HijriDateService.getHijriSysDateString());
	    }

	    if (effectiveGregDate != null) {
		qParams.put("P_EFFECTIVE_GREG_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_EFFECTIVE_GREG_DATE", HijriDateService.getHijriDateString(effectiveGregDate));
	    } else {
		qParams.put("P_EFFECTIVE_GREG_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_EFFECTIVE_GREG_DATE", HijriDateService.getHijriSysDateString());
	    }

	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.equals("")) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);

	    if (decisionDate != null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    }

	    return DataAccess.executeNamedQuery(EmployeeLogData.class, QueryNamesEnum.HCM_EMPLOYEE_LOG_DATA_SEARCH_EMPLOYEES_LOG.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Wrapper for {@link #getLastEmployeeLog()} to get last employee log
     * 
     * @param empId
     * @param effectiveHijriDate
     * @return the last {@link employeeLog} object for this employee
     * @throws BusinessException
     */
    public static EmployeeLog getLastEmployeeLogBeforeGivenDate(long empId, Date effectiveHijriDate) throws BusinessException {
	EmployeeLog employeeLog = getLastEmployeeLog(empId, effectiveHijriDate);
	if (employeeLog == null)
	    throw new BusinessException("error_employeeHasProblemInTransactionsLog");
	return employeeLog;

    }

    /**
     * Gets the last log for an employee.
     * 
     * @param empId
     * @param effectiveHijriDate
     * @return the last {@link employeeLog} object for this employee
     * @throws NoDataException
     *             if there is no logs for this employee
     * @throws BusinessException
     *             if a database error occurs
     */
    private static EmployeeLog getLastEmployeeLog(long empId, Date effectiveHijriDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_EFFECTIVE_HIJRI_DATE", HijriDateService.getHijriDateString(effectiveHijriDate));

	    List<EmployeeLog> result = DataAccess.executeNamedQuery(EmployeeLog.class, QueryNamesEnum.HCM_EMPLOYEE_LOG_GET_LAST_EMPLOYEE_LOG.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
