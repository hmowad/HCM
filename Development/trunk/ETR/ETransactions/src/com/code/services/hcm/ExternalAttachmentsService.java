package com.code.services.hcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.attachments.ExternalAttachment;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.webservicesclients.filenetclient.DocumentVersion;
import com.code.integration.webservicesclients.filenetclient.Exception_Exception;
import com.code.integration.webservicesclients.filenetclient.FNAttachmentServicesImplService;
import com.code.integration.webservicesclients.filenetclient.FNAttachmentsServices;
import com.code.integration.webservicesclients.filenetclient.FnAttachment;
import com.code.integration.webservicesclients.filenetclient.FnAttachment.DocProps;
import com.code.integration.webservicesclients.filenetclient.FnAttachment.DocProps.Entry;
import com.code.services.config.ETRConfigurationService;

public class ExternalAttachmentsService {

    public static List<ExternalAttachment> getAttachmentPropsFromFileNet(String attachmentsId) throws BusinessException {
	try {
	    if (attachmentsId == null || attachmentsId == "")
		return new ArrayList<ExternalAttachment>();
	    FNAttachmentServicesImplService fNAttachmentServicesImplService = new FNAttachmentServicesImplService();
	    FNAttachmentsServices services = fNAttachmentServicesImplService.getFNAttachmentServicesImplPort();
	    FnAttachment requestFnAttachment = new FnAttachment();
	    DocProps docProps = new DocProps();
	    Entry attachmentIdEntry = new Entry();
	    List<ExternalAttachment> result = new ArrayList<ExternalAttachment>();

	    attachmentIdEntry.setKey("attachmentsId");
	    attachmentIdEntry.setValue(attachmentsId);
	    docProps.getEntry().add(attachmentIdEntry);
	    requestFnAttachment.setDocProps(docProps);
	    requestFnAttachment.setDocumentClass(ETRConfigurationService.getFileNetDocumentClass());

	    FnAttachment responseFnAttachment = services.searchDocumentByMetadata(ETRConfigurationService.getFileNetUsername(), ETRConfigurationService.getFileNetPassword(), requestFnAttachment);

	    for (DocumentVersion documentVersion : responseFnAttachment.getDocVersions()) {
		ExternalAttachment externalAttachment = new ExternalAttachment();
		externalAttachment.setDocumentClass(requestFnAttachment.getDocumentClass());
		externalAttachment.setFilename((documentVersion.getDocumentTitle() == null || documentVersion.getDocumentTitle().isEmpty()) ? ("HCM" + System.currentTimeMillis()) : documentVersion.getDocumentTitle());
		externalAttachment.setAttachmentId(documentVersion.getVerSId());
		result.add(externalAttachment);
	    }

	    return result;

	} catch (Exception_Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<ExternalAttachment> getExternalAttachmentByTransactionIdAndTableName(Long transactionId, String tableName) throws DatabaseException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_TRANSACTION_ID", transactionId == null ? FlagsEnum.ALL.getCode() : transactionId);
	qParams.put("P_TABLE_NAME", (tableName == null || tableName == "") ? FlagsEnum.ALL.getCode() : tableName);

	return DataAccess.executeNamedQuery(ExternalAttachment.class, QueryNamesEnum.HCM_EXTERNAL_ATTACHMENT_GET_EXTERNAL_ATTACHMENT_BY_TRANSACTION_ID_AND_TABLE_NAME.getCode(), qParams);
    }

}
