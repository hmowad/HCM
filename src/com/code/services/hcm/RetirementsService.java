package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.retirements.DisclaimerTransaction;
import com.code.dal.orm.hcm.retirements.DisclaimerTransactionData;
import com.code.dal.orm.hcm.retirements.DisclaimerTransactionDetail;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.payroll.PayrollCategoriesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.services.BaseService;
import com.code.services.cor.ETRCorrespondence;
import com.code.services.integration.PayrollEngineService;
import com.code.services.util.HijriDateService;

public class RetirementsService extends BaseService {

    /**
     * Private constructor to prevent instantiation
     */
    private RetirementsService() {
    }

    /*********************************************************** DisclaimerTransaction & DisclaimerTransactionDetail ***********************************************************/

    /*------------------------------------------------ Operations --------------------------------------------------*/
    public static void saveDisclaimerTransactionAndDetails(DisclaimerTransactionData disclaimerTransactionData, List<DisclaimerTransactionDetail> disclaimerTransactionDetails,
	    String subject, CustomSession... useSession) throws BusinessException {

	validateDisclaimerTransactionAndDisclaimerTransactionDetails(disclaimerTransactionData, disclaimerTransactionDetails);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    addDisclaimerTransaction(disclaimerTransactionData, subject, session);
	    addDisclaimerTransactionDetails(disclaimerTransactionDetails, disclaimerTransactionData.getId(), session);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw new BusinessException(e.getMessage(), e.getParams());
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

    private static void addDisclaimerTransaction(DisclaimerTransactionData disclaimerTransactionData, String subject,
	    CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    if (subject != null) {
		String[] etrCorInfo = ETRCorrespondence.doETRCorOut(subject, session);
		disclaimerTransactionData.setDecisionNumber(etrCorInfo[0]);
		disclaimerTransactionData.setDecisionDateString(etrCorInfo[1]);
	    }
	    DataAccess.addEntity(disclaimerTransactionData.getDisclaimerTransaction(), session);
	    disclaimerTransactionData.setId(disclaimerTransactionData.getDisclaimerTransaction().getId());
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doPayrollIntegration(disclaimerTransactionData, FlagsEnum.OFF.getCode(), session);
	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    disclaimerTransactionData.setId(null);
	    e.printStackTrace();
	    throw new BusinessException(e.getMessage());
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void addDisclaimerTransactionDetails(List<DisclaimerTransactionDetail> disclaimerTransactionDetails, Long disclaimerTranasctionId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    for (DisclaimerTransactionDetail disclaimerTransactionDetail : disclaimerTransactionDetails) {
		disclaimerTransactionDetail.setDisclaimerTransactionId(disclaimerTranasctionId);
		DataAccess.addEntity(disclaimerTransactionDetail, session);
	    }
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

    private static void doPayrollIntegration(DisclaimerTransactionData disclaimerTransactionData, Integer resendFlag, CustomSession session) throws BusinessException {

	try {
	    Long disclaimerAdminDecisionId = null;
	    if (disclaimerTransactionData.getTransEmpCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
		disclaimerAdminDecisionId = AdminDecisionsEnum.OFFICERS_DISCLAIMER.getCode();
	    } else if (disclaimerTransactionData.getTransEmpCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
		disclaimerAdminDecisionId = AdminDecisionsEnum.SOLDIERS_DISCLAIMER.getCode();
	    }
	    String gregDecisionDateString = HijriDateService.hijriToGregDateString(disclaimerTransactionData.getDecisionDateString());
	    String gregTerminationDateString = HijriDateService.hijriToGregDateString(disclaimerTransactionData.getTransServiceTerminationDateString());
	    TerminationTransactionData terminationTransactionData = TerminationsService.getTerminationTransactionById(disclaimerTransactionData.getTerminationTransactionId());
	    if (terminationTransactionData.getTransEmpUnitId() == null) {
		throw new BusinessException("error_noUnitIdInTerminationTransaction");
	    }
	    Long employeeCategory = EmployeesService.getEmployeeMedicalStaffDataByEmpId(disclaimerTransactionData.getEmpId()) != null ? PayrollCategoriesEnum.MILITARY_MEDICAL_STAFF.getCode() : disclaimerTransactionData.getTransEmpCategoryId();
	    List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>(Arrays.asList(new AdminDecisionEmployeeData(disclaimerTransactionData.getEmpId(), terminationTransactionData.getEmpName(), disclaimerTransactionData.getId(), null, gregTerminationDateString, gregTerminationDateString, disclaimerTransactionData.getDecisionNumber(), null)));
	    session.flushTransaction();
	    PayrollEngineService.doPayrollIntegration(disclaimerAdminDecisionId, employeeCategory, gregTerminationDateString, adminDecisionEmployeeDataList, null, terminationTransactionData.getTransEmpUnitId(), gregDecisionDateString, DataAccess.getTableName(DisclaimerTransaction.class), resendFlag, FlagsEnum.OFF.getCode(), session);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(e.getMessage());
	}
    }

    public static void payrollIntegrationFailureHandle(String decisionNumber, Date decisionDate, CustomSession session) throws BusinessException {
	DisclaimerTransactionData disclaimerTransactionData = getDisclaimerTransByDecisionNumberAndDate(decisionNumber, decisionDate).get(0);
	if (disclaimerTransactionData != null) {
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doPayrollIntegration(disclaimerTransactionData, FlagsEnum.ON.getCode(), session);
	} else {
	    throw new BusinessException("error_transactionDataRetrievingError");
	}
    }

    /*---------------------------Validations--------------------------*/
    private static void validateDisclaimerTransactionAndDisclaimerTransactionDetails(DisclaimerTransactionData disclaimerTransactionData, List<DisclaimerTransactionDetail> disclaimerTransactionDetails) throws BusinessException {
	if (disclaimerTransactionData == null)
	    throw new BusinessException("error_transactionDataError");
	if (disclaimerTransactionData.getEmpId() == null)
	    throw new BusinessException("error_empSelectionManadatory");
	if (disclaimerTransactionData.getTransEmpCategoryId() == null)
	    throw new BusinessException("error_categoryMandatory");
	if (disclaimerTransactionData.getTerminationTransactionId() == null)
	    throw new BusinessException("error_steEmpShouldBeTerminated");
	if (disclaimerTransactionData.getDecisionApprovedId() == null || disclaimerTransactionData.getOriginalDecisionApprovedId() == null)
	    throw new BusinessException("error_decisionApprovedMandatory");
    }

    /*------------------------------------------------ Queries --------------------------------------------------*/
    public static List<DisclaimerTransactionData> getDisclaimerTransactionsAfterDecisionDate(Long empId, Date decisiondate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	    if (decisiondate == null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisiondate));
	    }
	    return DataAccess.executeNamedQuery(DisclaimerTransactionData.class, QueryNamesEnum.HCM_GET_DISCLAIMER_TRANSACTION_DATA_AFTER_DESICION_DATE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<DisclaimerTransactionData> getDisclaimersHistory(Long empId) throws BusinessException {
	return searchDisclaimerTransactions(empId, null);
    }

    public static DisclaimerTransactionData getDisclaimerTransByTerminationTransId(Long terminationTransactionId) throws BusinessException {
	List<DisclaimerTransactionData> disclaimerTransactionsData = searchDisclaimerTransactions(null, terminationTransactionId);
	return disclaimerTransactionsData.size() > 0 ? disclaimerTransactionsData.get(0) : null;
    }

    private static List<DisclaimerTransactionData> searchDisclaimerTransactions(Long empId, Long terminationTransactionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId == null ? FlagsEnum.ALL.getCode() : empId);
	    qParams.put("P_TERM_TRANS_ID", terminationTransactionId == null ? FlagsEnum.ALL.getCode() : terminationTransactionId);
	    return DataAccess.executeNamedQuery(DisclaimerTransactionData.class, QueryNamesEnum.HCM_GET_DISCLAIMER_TRANSACTION_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<DisclaimerTransactionData> getDisclaimerTransByDecisionNumberAndDate(String decisionNumber, Date decisionDate) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_DECISION_NUMBER", (decisionNumber == null || decisionNumber.length() == 0) ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    if (decisionDate == null) {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ALL.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriSysDateString());
	    } else {
		qParams.put("P_DECISION_DATE_FLAG", FlagsEnum.ON.getCode());
		qParams.put("P_DECISION_DATE", HijriDateService.getHijriDateString(decisionDate));
	    }
	    return DataAccess.executeNamedQuery(DisclaimerTransactionData.class, QueryNamesEnum.HCM_GET_DISCLAIMER_TRANSACTION_DATA_BY_DECISION_DATE_AND_NUMBER.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*------------------------------------------------ Reports --------------------------------------------------*/
    public static byte[] getDisclaimerDecisionBytes(Long disclaimerTransactionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_DISCLAIMER_TRANSACTION_ID", disclaimerTransactionId);
	    return getReportData(ReportNamesEnum.RETIREMENTS_DISCLAIMER_DECISION.getCode(), qParams);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getDisclaimerStepsBytes(TerminationTransactionData terminationTransactionData, Integer empMilitaryNumber) throws BusinessException {
	String reportName = "";
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_TERMINATION_TRANS_ID", terminationTransactionData.getId());
	    parameters.put("P_EMP_NUMBER", terminationTransactionData.getCategoryId() == CategoriesEnum.OFFICERS.getCode() ? empMilitaryNumber : terminationTransactionData.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() ? terminationTransactionData.getJobCode() : "");
	    parameters.put("P_EMP_NAME", terminationTransactionData.getEmpName());
	    parameters.put("P_EMP_RANK_DESC", terminationTransactionData.getTransEmpRankDesc());
	    parameters.put("P_EMP_JOB_DESC", terminationTransactionData.getTransEmpJobClassJobDesc());
	    parameters.put("P_EMP_UNIT_FULL_NAME", terminationTransactionData.getTransEmpUnitFullName());
	    parameters.put("P_TERMINATION_TRANSACTION_REASON", terminationTransactionData.getReasonDesc());
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());

	    reportName = ReportNamesEnum.RETIREMENTS_DISCLAIMER_STEPS.getCode();

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }

    public static byte[] getRetirementReportsBytes(int reportType, long empId) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = "";

	    if (reportType == 10)
		reportName = ReportNamesEnum.RETIREMENTS_SICK_VACATIONS.getCode();
	    else if (reportType == 20)
		reportName = ReportNamesEnum.RETIREMENTS_OFFICERS_SERVICES_SUMMARY.getCode();
	    else if (reportType == 30 || reportType == 40)
		reportName = ReportNamesEnum.RETIREMENTS_SERVICES_STATEMENT.getCode();
	    else if (reportType == 50) {
		reportName = ReportNamesEnum.RETIREMENTS_REGULAR_VACATIONS.getCode();

		List<String> regularBalancesReportParameters = VacationsBusinessRulesService.getEmployeeRegularVacationsBalances(empId);
		for (int i = 0; i < regularBalancesReportParameters.size() - 1; i++) {
		    String[] record = regularBalancesReportParameters.get(i).split(",");
		    parameters.put("START_DATE_" + (i + 1), record[0]);
		    parameters.put("END_DATE_" + (i + 1), record[1]);
		    parameters.put("DUE_BALANCE_" + (i + 1), record[2]);
		    parameters.put("CONSUMED_VAC_DAYS_" + (i + 1), record[3]);
		    parameters.put("REMAINING_BALANCE_" + (i + 1), record[4]);
		    parameters.put("COMPENSATION_DUE_BALANCE_" + (i + 1), record[5]);
		    parameters.put("NO_COMPENSATION_DUE_BALANCE_" + (i + 1), record[6]);
		}

		parameters.put("TOTAL_COMPENSATION_DUE_BALANCE", regularBalancesReportParameters.get(regularBalancesReportParameters.size() - 1));
	    }

	    parameters.put("P_EMPLOYEE_ID", empId);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());

	    if (reportType == 10 || reportType == 50)
		return getReportData(reportName, parameters);
	    else
		return getRTFReportData(reportName, parameters);

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }
}
