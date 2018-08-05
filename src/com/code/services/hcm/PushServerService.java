package com.code.services.hcm;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.pushServer.PushServerLog;
import com.code.exceptions.BusinessException;

public class PushServerService {

    private PushServerService() {
    }

    public static void logPushServerToken(PushServerLog pushServerLog) throws BusinessException {
	CustomSession session = DataAccess.getSession();

	try {
	    session.beginTransaction();

	    DataAccess.addEntity(pushServerLog, session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }
}
