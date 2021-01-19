package com.code.services.util;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.attachments.SecurityKeyData;
import com.code.enums.SequenceNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;

public class AttachmentsService extends BaseService {

    private final static String SECURITY_KEY_PREFIX = "HCM_ETR_";

    private AttachmentsService() {
    }

    // Generate the next attachment identifier.
    public static String getNextAttachmentsId() throws BusinessException {

	if (ETRConfigurationService.getAttachmentUploadURL() == null || ETRConfigurationService.getAttachmentUploadURL().isEmpty()
		|| ETRConfigurationService.getAttachmentsAddURL() == null || ETRConfigurationService.getAttachmentsAddURL().isEmpty())
	    throw new BusinessException("error_attachmentsIntegrationDisabled");

	try {
	    return DataAccess.getNextValFromSequence(SequenceNamesEnum.ETR_ATTACHMENTS_SEQUENCE.getCode()) + "";
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    // Generate the security key which is required to view the attachments via the integration.
    public static String getSecurityKey() throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    SecurityKeyData securityKeyData = new SecurityKeyData();
	    securityKeyData.setSecurityKey(SECURITY_KEY_PREFIX + System.currentTimeMillis());

	    DataAccess.addEntity(securityKeyData, session);
	    session.commitTransaction();

	    return securityKeyData.getSecurityKey();
	} catch (Exception e) {
	    e.printStackTrace();
	    session.rollbackTransaction();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static String getAttachmentsAddURL(String attachmentId, long employeeId) throws BusinessException {

	if (attachmentId == null || attachmentId.isEmpty())
	    throw new BusinessException("error_attachmentsMissing");

	try {
	    String securityKey = getSecurityKey();
	    if (securityKey == null)
		throw new BusinessException("error_transactionDataError");

	    return ETRConfigurationService.getAttachmentsAddURL().replace("P_ATTACHMENTS_ID", attachmentId).replace("P_LOGGED_USER", employeeId + "").replace("P_SEC_FLG", securityKey);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static String getAttachmentsViewURL(String attachmentId, long employeeId) throws BusinessException {

	if (attachmentId == null || attachmentId.isEmpty())
	    throw new BusinessException("error_attachmentsMissing");

	try {
	    String securityKey = getSecurityKey();
	    if (securityKey == null)
		throw new BusinessException("error_transactionDataError");

	    int accessRight = 1; // view only
	    return ETRConfigurationService.getAttachmentsViewURL().replace("P_ATTACHMENTS_ID", attachmentId).replace("P_USER_NAME", employeeId + "").replace("P_ACL_RIGHT", accessRight + "").replace("P_SEC_FLG", securityKey);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
