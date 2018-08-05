package com.code.services.decisiontemplates;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.DecisionTemplate;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

public class DecisionTemplatesService extends BaseService {

    public static void updateDecisionTemplate(DecisionTemplate template, CustomSession... useSession) throws BusinessException {
	validateDecisionTemplate(template);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(template, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_mandatoryFieldsRequired");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void validateDecisionTemplate(DecisionTemplate template) throws BusinessException {
	validateDecisionTemplateMandatoryFields(template);
    }

    public static void validateDecisionTemplateMandatoryFields(DecisionTemplate template) throws BusinessException {
	if (template.getDecisionDesc() == null || template.getDecisionDesc().trim().isEmpty()) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}
	if (template.getIntroductionTitle() == null || template.getIntroductionTitle().trim().isEmpty()) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}

	if (template.getIntroduction() == null || template.getIntroduction().trim().isEmpty()) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}
	if (template.getClauses() == null || template.getClauses().trim().isEmpty()) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}
	if (template.getDecisionDesc() == null || template.getDecisionDesc().trim().isEmpty()) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}
	if (template.getRiyadhRegionHeader() == null || template.getRiyadhRegionHeader().trim().isEmpty()) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}

	if (template.getValidFromDate() == null) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}
	if (template.getValidToDate() == null) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}
	if (template.getCategoryId() == null) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}
	if (template.getTransactionClass() == null) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}
	if (template.getTransactionClassType() == null) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}
	if (template.getTransactionBusinessType() == null) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}
	if (template.getReportClass() == null) {
	    throw new BusinessException("error_mandatoryFieldsRequired");
	}
	if (!template.getValidToDate().after(template.getValidFromDate())) {
	    throw new BusinessException("error_templateValidToDateBeforeStartDate");
	}
    }

    public static List<DecisionTemplate> getDecisionTemplates(String decisionDesc, int category, int reportClass, int transactionClass, int transactionBusinessType, Date validFromDate, Date validToDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_DECISION_DESC", decisionDesc == null || decisionDesc.isEmpty() ? FlagsEnum.ALL.getCode() + "" : "%" + decisionDesc + "%");
	    qParams.put("P_CATEGORY", category);
	    qParams.put("P_REPORT_CLASS", reportClass);
	    qParams.put("P_TRANSACTION_CLASS", transactionClass);
	    qParams.put("P_TRANSACTION_BUSINESS_TYPE", transactionBusinessType);
	    String sysDate = HijriDateService.getHijriSysDateString();
	    if (validFromDate != null) {
		qParams.put("P_VALID_FROM_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_VALID_FROM_DATE", HijriDateService.getHijriDateString(validFromDate));

	    } else {
		qParams.put("P_VALID_FROM_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_VALID_FROM_DATE", sysDate);
	    }
	    if (validToDate != null) {
		qParams.put("P_VALID_TO_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_VALID_TO_DATE", HijriDateService.getHijriDateString(validToDate));

	    } else {
		qParams.put("P_VALID_TO_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_VALID_TO_DATE", sysDate);
	    }
	    return DataAccess.executeNamedQuery(DecisionTemplate.class, QueryNamesEnum.UTIL_DECISION_TEMPLATE_SEARCH_DECISION_TEMPLATE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}
