package com.code.services.integration;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.integration.security.WSSession;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.WSResponseBase;
import com.code.services.BaseService;
import com.code.services.security.SecurityService;

public class WSSessionsManagementService extends BaseService {

    private final static long SESSION_TIME_OUT_PERIOD = 60 * 60 * 1000; // 60 minutes.

    public static WSSession makeNewSession(String username, String password) throws BusinessException {

	// authenticate the user
	EmployeeData empData = SecurityService.authenticateUser(username, password);

	if (empData.getStatusId() == EmployeeStatusEnum.MOVED_EXTERNALLY.getCode()
		|| empData.getStatusId() == EmployeeStatusEnum.SERVICE_TERMINATED.getCode())
	    throw new BusinessException("error_notAuthorized");

	WSSession sessionInfo;

	CustomSession session = DataAccess.getSession();

	try {
	    session.beginTransaction();

	    // make a new session for the current user.
	    sessionInfo = new WSSession();
	    sessionInfo.setSessionId(empData.getEmpId().toString() + System.currentTimeMillis() + String.valueOf(UUID.randomUUID()).replace("-", "") + String.valueOf(UUID.randomUUID()).replace("-", ""));
	    sessionInfo.setEmpId(empData.getEmpId());
	    sessionInfo.setFirstActivityTime(new Date());
	    sessionInfo.setLastActivityTime(new Date());

	    DataAccess.addEntity(sessionInfo, session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {

	    session.close();
	}
	return sessionInfo;
    }

    public static boolean maintainSession(String sessionId, long empId, WSResponseBase wsResponse) {
	if (!maintainSession(sessionId, empId)) {
	    wsResponse.setStatus(WSResponseStatusEnum.INVALID_SESSION.getCode());
	    wsResponse.setMessage(BaseService.getMessage("error_invalidWSSession"));
	    return false;
	}

	return true;
    }

    public static boolean maintainSession(String sessionId, long empId) {
	try {
	    WSSession sessionInfo = searchWSSessionInfo(sessionId, empId);
	    if (sessionInfo == null)
		return false;

	    // check for session time out.
	    if (new Duration(new DateTime(sessionInfo.getLastActivityTime().getTime()), new DateTime()).getMillis() > SESSION_TIME_OUT_PERIOD)
		return false;

	    updateSessionActivity(sessionInfo);
	    return true;

	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
    }

    private static void updateSessionActivity(WSSession sessionInfo) throws BusinessException {

	if (sessionInfo == null)
	    return;

	CustomSession session = DataAccess.getSession();

	try {
	    session.beginTransaction();

	    sessionInfo.setLastActivityTime(new Date());
	    DataAccess.updateEntity(sessionInfo, session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    private static WSSession searchWSSessionInfo(String sessionId, long empId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_SESSION_ID", sessionId);
	    qParams.put("P_EMP_ID", empId);

	    List<WSSession> result = DataAccess.executeNamedQuery(WSSession.class, QueryNamesEnum.WS_GET_WSSESSION.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
