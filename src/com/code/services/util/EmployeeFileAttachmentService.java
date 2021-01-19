package com.code.services.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.attachments.EmployeeFileAttachment;
import com.code.dal.orm.hcm.attachments.EmployeeFileAttachmentDetail;
import com.code.dal.orm.hcm.attachments.EmployeeFileAttachmentDetailData;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;

public class EmployeeFileAttachmentService extends BaseService {
    public static final String SERVICE_CODE = "ETR_HCM_SYS";

    public EmployeeFileAttachmentService() {

    }

    /*---------------------------------------------------------- Operations --------------------------------------------*/
    public static void deleteAttachment(EmployeeFileAttachmentDetailData empFileAttachmentDetails, long userId) throws BusinessException {
	try {
	    String deleteURL = ETRConfigurationService.getAttachmentDeleteURL();
	    URL url = new URL(deleteURL + EncryptionUtil.encryptSymmetrically("contentId=" + empFileAttachmentDetails.getContentId()));
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("GET");
	    int responseCode = con.getResponseCode();
	    if (responseCode == HttpServletResponse.SC_OK) {
		deleteEmployeeFileAttachmentDetailData(empFileAttachmentDetails, userId);
	    } else {
		throw new BusinessException("error_general");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------------------------------- Data Handling --------------------------------------------*/
    public static void addEmployeeFileAttachment(EmployeeFileAttachment empFileAttachment, long userId, CustomSession... useSession) throws BusinessException {

	validateEmployeeFileAttachment(empFileAttachment);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	    Date date = new Date();
	    empFileAttachment.setCreationTime(sdf.format(date).toString());

	    empFileAttachment.setCreationDate(HijriDateService.getHijriSysDate());

	    empFileAttachment.setSystemUser(userId + ""); // For Auditing.

	    DataAccess.addEntity(empFileAttachment, session);

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

    public static void addEmployeeFileAttachmentDetail(EmployeeFileAttachmentDetail empFileAttachmentDetail, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(empFileAttachmentDetail, session);

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

    public static void deleteEmployeeFileAttachment(EmployeeFileAttachment empFileAttachment, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(empFileAttachment, session);

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

    public static void deleteEmployeeFileAttachmentDetailData(EmployeeFileAttachmentDetailData empFileAttachmentDetailsData, long userId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    empFileAttachmentDetailsData.getEmployeeFileAttachmentDetail().setSystemUser(userId + ""); // For Auditing.

	    DataAccess.deleteEntity(empFileAttachmentDetailsData.getEmployeeFileAttachmentDetail(), session);

	    if (!isOpenedSession)
		session.commitTransaction();

	    if (countEmpFileAttachmentsDetailsByAttachmentId(empFileAttachmentDetailsData.getAttachmentId()) == 0) {
		EmployeeFileAttachment empFileAttachment = getEmpFileAttachmentById(empFileAttachmentDetailsData.getAttachmentId());
		deleteEmployeeFileAttachment(empFileAttachment, useSession);
	    }

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

    public static List<EmployeeFileAttachmentDetailData> searchEmpFileAttachmentsDetails(Long empId, Integer transactionClass, String decisionNumber, String description, Date fromDate, Date toDate) throws BusinessException {
	return getEmpFileAttachmentsDetails(empId, transactionClass, decisionNumber, description, fromDate, toDate);
    }

    /*---------------------------------------------------------- Validations --------------------------------------------*/
    private static void validateEmployeeFileAttachment(EmployeeFileAttachment attachment) throws BusinessException {
	if (attachment.getEmpId() == null)
	    throw new BusinessException("error_employeeMandatory");
	if (attachment.getDecisionNumber() == null || attachment.getDecisionNumber().trim().isEmpty())
	    throw new BusinessException("error_decisionNumberIsMandatory");
	if (attachment.getDecisionDate() == null)
	    throw new BusinessException("error_decisionDateIsMandatory");

	if (countEmpFileAttachmentsByDecisionNumber(attachment.getDecisionNumber(), attachment.getTransactionClass()) > 0) {
	    throw new BusinessException("error_attachmentDecisionNumberRepeted");
	}
    }

    /*---------------------------------------------------------- Queries  -----------------------------------------------*/
    private static long countEmpFileAttachmentsByDecisionNumber(String decisionNumber, Integer transactionClass) throws BusinessException {
	try {
	    Map<String, Object> queryParam = new HashMap<String, Object>();
	    queryParam.put("P_DECISION_NUMBER", decisionNumber);
	    queryParam.put("P_TRANSACTION_CLASS", transactionClass);
	    List<Long> count = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.ETR_COUNT_EMPLOYEE_FILE_ATTACHMENT_BY_DECISION_NUMBER.getCode(), queryParam);
	    return (count.isEmpty() || count == null ? 0 : count.get(0));
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static long countEmpFileAttachmentsDetailsByAttachmentId(Long attachmentId) throws BusinessException {
	try {
	    Map<String, Object> queryParam = new HashMap<String, Object>();
	    queryParam.put("P_ATTACHMENT_ID", attachmentId);
	    List<Long> count = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.ETR_COUNT_EMPLOYEE_FILE_ATTACHMENT_DETAILS_BY_ATTACHMENT_ID.getCode(), queryParam);
	    return (count.isEmpty() || count == null ? 0 : count.get(0));
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static EmployeeFileAttachment getEmpFileAttachmentById(Long attachmentId) throws BusinessException {
	try {
	    Map<String, Object> qParam = new HashMap<String, Object>();
	    qParam.put("P_ATTACHMENT_ID", attachmentId);
	    List<EmployeeFileAttachment> result = DataAccess.executeNamedQuery(EmployeeFileAttachment.class, QueryNamesEnum.ETR_EMPLOYEE_FILE_ATTACHMENT_GET_EMPLOYEE_FILE_ATTACHMENT_BY_ATTACHMENT_ID.getCode(), qParam);
	    return result != null && result.size() > 0 ? result.get(0) : null;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<EmployeeFileAttachmentDetailData> getEmpFileAttachmentsDetails(Long empId, Integer transactionClass, String decisionNumber, String description, Date fromDate, Date toDate) throws BusinessException {
	try {
	    Map<String, Object> qParam = new HashMap<String, Object>();
	    qParam.put("P_EMP_ID", empId);
	    qParam.put("P_TRANSACTION_CLASS", transactionClass);
	    qParam.put("P_DECISION_NUMBER", decisionNumber.equals("") ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    qParam.put("P_DESCRIPTION", description.equals("") ? FlagsEnum.ALL.getCode() + "" : "%" + description + "%");
	    qParam.put("P_SKIP_FROM_DATE", fromDate == null ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode());
	    qParam.put("P_FROM_DATE", fromDate == null ? HijriDateService.getHijriDateString(HijriDateService.getHijriSysDate()) : HijriDateService.getHijriDateString(fromDate));
	    qParam.put("P_SKIP_TO_DATE", toDate == null ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode());
	    qParam.put("P_TO_DATE", (toDate == null ? HijriDateService.getHijriDateString(HijriDateService.getHijriSysDate()) : HijriDateService.getHijriDateString(toDate)));
	    return DataAccess.executeNamedQuery(EmployeeFileAttachmentDetailData.class, QueryNamesEnum.ETR_EMPLOYEE_FILE_ATTACHMENT_DETAILS_GET_EMPLOYEE_FILE_ATTACHMENTS.getCode(), qParam);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
