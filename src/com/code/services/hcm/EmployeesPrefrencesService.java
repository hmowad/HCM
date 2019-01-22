package com.code.services.hcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeePrefrences;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;

public class EmployeesPrefrencesService extends BaseService {
    private EmployeesPrefrencesService() {
    }

    private static void addEmployeePrefrences(EmployeePrefrences empPrefrences, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(empPrefrences, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    empPrefrences.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateEmployeePrefrences(EmployeePrefrences empPrefrences, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(empPrefrences, session);

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

    private static EmployeePrefrences getEmployeePrefrencesByEmpId(long employeeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", employeeId);

	    List<EmployeePrefrences> result = DataAccess.executeNamedQuery(EmployeePrefrences.class, QueryNamesEnum.HCM_GET_EMPLOYEE_PREFRENCES_BY_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static EmployeePrefrences getEmployeePrefrences(long employeeId) throws BusinessException {
	EmployeePrefrences employeePrefrences = getEmployeePrefrencesByEmpId(employeeId);
	if (employeePrefrences == null) {
	    employeePrefrences = new EmployeePrefrences();
	    employeePrefrences.setId(employeeId);
	    employeePrefrences.setTimeLineAutoHideFlag(FlagsEnum.OFF.getCode());
	    addEmployeePrefrences(employeePrefrences);
	}

	return employeePrefrences;
    }
}
