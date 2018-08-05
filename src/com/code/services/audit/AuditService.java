package com.code.services.audit;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.DataAccess;
import com.code.dal.orm.audit.AuditLog;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;

public class AuditService extends BaseService {

    public AuditService() {
    }

    public static List<AuditLog> getAuditLogs(String contentEntity, Long contentId, String operation, Long systemUser, Date operationDateFrom, Date operationDateTo, String content) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CONTENT_ENTITY", (contentEntity == null || contentEntity.equals("")) ? (FlagsEnum.ALL.getCode() + "") : contentEntity);
	    qParams.put("P_CONTENT_ID", contentId == null ? FlagsEnum.ALL.getCode() : contentId);
	    qParams.put("P_OPERATION", (operation == null || operation.equals("")) ? (FlagsEnum.ALL.getCode() + "") : operation);
	    qParams.put("P_SYSTEM_USER", systemUser == null ? FlagsEnum.ALL.getCode() : systemUser);

	    content = content.trim();
	    qParams.put("P_CONTENT", (content == null || content.equals("")) ? (FlagsEnum.ALL.getCode() + "") : ("%" + content + "%"));

	    if (operationDateFrom != null) {
		qParams.put("P_FROM_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_FROM_DATE", operationDateFrom);
	    } else {
		qParams.put("P_FROM_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_FROM_DATE", new Date());
	    }
	    if (operationDateTo != null) {
		qParams.put("P_TO_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_TO_DATE", operationDateTo);
	    } else {
		qParams.put("P_TO_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_TO_DATE", new Date());
	    }
	    return DataAccess.executeNamedQuery(AuditLog.class, QueryNamesEnum.ETR_GET_AUDIT_LOGS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<Object> getContentEntites() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.ETR_GET_CONTENT_ENTITIES.getCode(), null);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}
