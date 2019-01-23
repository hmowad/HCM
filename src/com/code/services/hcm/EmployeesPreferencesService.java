package com.code.services.hcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeePreferences;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;

public class EmployeesPreferencesService extends BaseService {
    private EmployeesPreferencesService() {
    }

    private static void addEmployeePreferences(EmployeePreferences empPreferences, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(empPreferences, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    empPreferences.setId(null);

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateEmployeePreferences(EmployeePreferences empPreferences, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(empPreferences, session);

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

    private static EmployeePreferences getEmployeePreferencesByEmpId(long employeeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", employeeId);

	    List<EmployeePreferences> result = DataAccess.executeNamedQuery(EmployeePreferences.class, QueryNamesEnum.HCM_GET_EMPLOYEE_PREFERENCES_BY_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static EmployeePreferences getEmployeePreferences(long employeeId) throws BusinessException {
	EmployeePreferences employeePreferences = getEmployeePreferencesByEmpId(employeeId);
	if (employeePreferences == null) {
	    employeePreferences = new EmployeePreferences();
	    employeePreferences.setId(employeeId);
	    employeePreferences.setTimeLineAutoHideFlag(FlagsEnum.OFF.getCode());
	    addEmployeePreferences(employeePreferences);
	}

	return employeePreferences;
    }
}
