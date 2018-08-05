package com.code.services.workflow.hcm;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.hcm.WFDefinitionLetter;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.util.CountryService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

public class DefinitionLettersWorkFlow extends BaseWorkFlow {

    private final static String EMBASSY_NAME_LABEL = "To Embassy of ";
    private final static String TO_WHOM_IT_MAY_CONCERN = "To Whom It May Concern";

    private DefinitionLettersWorkFlow() {
    }

    public static void initDefinitionLetter(EmployeeData requester, EmployeeData printerEmp, WFDefinitionLetter definitionLetterRequest, long processId, String taskUrl) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), null, Arrays.asList(definitionLetterRequest.getRelatedUserId()), session);
	    addWFTask(instance.getInstanceId(), getDelegate(printerEmp.getEmpId(), processId, requester.getEmpId()), printerEmp.getEmpId(),
		    curDate, curHijriDate, taskUrl, WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1", session);

	    definitionLetterRequest.setInstanceId(instance.getInstanceId());
	    DataAccess.addEntity(definitionLetterRequest, session);

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static WFDefinitionLetter getWFDefinitionLetterByInstanceId(long instanceId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    List<WFDefinitionLetter> result = DataAccess.executeNamedQuery(WFDefinitionLetter.class, QueryNamesEnum.WF_GET_WF_DEF_LETTER_BY_INSTANCE_ID.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);
	} catch (Exception e) {
	    throw new BusinessException("error_general");
	}
    }

    public static byte[] getDefinitionLetterBytes(String letterType, Long empId, Long embassyId, String empEnglishName) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = "";
	    String hijriDateString = HijriDateService.getHijriSysDateString();

	    if (letterType.equals("10"))
		reportName = ReportNamesEnum.DEFINITION_LETTERS_NET_SALARY.getCode();
	    else if (letterType.equals("20"))
		reportName = ReportNamesEnum.DEFINITION_LETTERS_BASIC_SALARY.getCode();
	    else if (letterType.equals("30"))
		reportName = ReportNamesEnum.DEFINITION_LETTERS_TOTAL_SALARY.getCode();
	    else if (letterType.equals("40")) {
		reportName = ReportNamesEnum.DEFINITION_LETTERS_NET_SALARY_ENGLISH.getCode();

		String embassyNameParameter = TO_WHOM_IT_MAY_CONCERN;
		if (embassyId != null)
		    embassyNameParameter = EMBASSY_NAME_LABEL + CountryService.getEmbassyById(embassyId).getEmbassyLatinName();

		parameters.put("P_EMBASSY_NAME", embassyNameParameter);
		parameters.put("P_EMP_EN_NAME", empEnglishName);
		parameters.put("P_GREG_SYS_DATE", HijriDateService.hijriToGregDateString(hijriDateString));
	    } else if (letterType.equals("50"))
		// Definition Letter For Opening/Renewing Medical File
		reportName = ReportNamesEnum.DEFINITION_LETTERS_MEDICAL_LETTER.getCode();
	    else if (letterType.equals("60"))
		// Definition Letter For Opening/Renewing Medical File For Retired
		reportName = ReportNamesEnum.DEFINITION_LETTERS_MEDICAL_LETTER_RETIRED.getCode();

	    // Common Parameters
	    parameters.put("P_EMP_ID", empId);
	    parameters.put("P_SYS_DATE", hijriDateString);

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}
