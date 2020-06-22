package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.incentives.IncentivePort;
import com.code.dal.orm.hcm.incentives.IncentiveTransaction;
import com.code.dal.orm.hcm.missions.MissionData;
import com.code.dal.orm.setup.GovernmentalCommitteeCategory;
import com.code.enums.AdminDecisionsEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.IncentiveTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.integration.responses.payroll.AdminDecisionEmployeeData;
import com.code.services.BaseService;
import com.code.services.integration.PayrollEngineService;
import com.code.services.util.HijriDateService;

public class IncentivesService extends BaseService {

    private IncentivesService() {
    }

    public static void addIncentiveTransaction(IncentiveTransaction incentiveTransaction) throws Exception {
	validateIncentiveTransaction(incentiveTransaction);
	CustomSession session = DataAccess.getSession();

	try {
	    session.beginTransaction();

	    incentiveTransaction.setTransactionDate(HijriDateService.getHijriSysDate());
	    DataAccess.addEntity(incentiveTransaction, session);
	    if (PayrollEngineService.getIntegrationWithAllowanceAndDeductionFlag().equals(FlagsEnum.ON.getCode()))
		doPayrollIntegration(incentiveTransaction, FlagsEnum.OFF.getCode(), session);
	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();
	    throw e;
	} finally {
	    session.close();
	}
    }

    public static void payrollIntegrationFailureHandle(Long transactionId, CustomSession session) throws DatabaseException, BusinessException {
	IncentiveTransaction incentiveTransaction = getIncentiveTransactionById(transactionId);
	if (incentiveTransaction == null)
	    throw new BusinessException("error_transactionDataRetrievingError");
	doPayrollIntegration(incentiveTransaction, FlagsEnum.ON.getCode(), session);
    }

    private static void doPayrollIntegration(IncentiveTransaction incentiveTransaction, int resendFlag, CustomSession session) throws BusinessException {

	Long adminDecisionId = null;
	if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.FINANCIAL_LOSS_COMPENSATION.getCode()))
	    adminDecisionId = AdminDecisionsEnum.FINANCIAL_LOSS_COMPENSATION.getCode();
	else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.ADVISORY_COUNCILS.getCode()))
	    adminDecisionId = AdminDecisionsEnum.ADVISORY_COUNCILS.getCode();
	else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.GOVERNMENTAL_COMMITTEES.getCode()))
	    adminDecisionId = AdminDecisionsEnum.GOVERNMENTAL_COMMITTEES.getCode();
	else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.DRUGS_DESTRUCTION.getCode()))
	    adminDecisionId = AdminDecisionsEnum.DRUGS_DESTRUCTION.getCode();
	else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.TEST_COMMITTEES.getCode()))
	    adminDecisionId = AdminDecisionsEnum.TEST_COMMITTEES.getCode();
	else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.HAJJ.getCode()))
	    adminDecisionId = AdminDecisionsEnum.HAJJ.getCode();
	else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.COMPUTER.getCode()))
	    adminDecisionId = AdminDecisionsEnum.COMPUTER.getCode();
	else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.MILITARY_VACATIONS.getCode()))
	    adminDecisionId = AdminDecisionsEnum.MILITARY_VACATIONS.getCode();
	else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.MISSIONS_DIFFERENCES.getCode()))
	    adminDecisionId = AdminDecisionsEnum.MISSIONS_DIFFERENCES.getCode();

	if (adminDecisionId != null) {
	    String gregStartDateString = HijriDateService.hijriToGregDateString(HijriDateService.getHijriDateString(incentiveTransaction.getStartDate()));
	    String gregTransactionDateString = HijriDateService.hijriToGregDateString(HijriDateService.getHijriDateString(incentiveTransaction.getTransactionDate()));
	    EmployeeData employee = EmployeesService.getEmployeeData(incentiveTransaction.getEmployeeId());
	    MissionData missionData = incentiveTransaction.getMissionTransactionId() == null ? null : MissionsService.getMissionsById(MissionsService.getMissionsDetailById(incentiveTransaction.getMissionTransactionId()).getMissionId());
	    if (employee == null)
		throw new BusinessException("error_employeeDataError");
	    List<AdminDecisionEmployeeData> adminDecisionEmployeeDataList = new ArrayList<AdminDecisionEmployeeData>(Arrays.asList(new AdminDecisionEmployeeData(employee.getEmpId(), employee.getName(), incentiveTransaction.getId(), incentiveTransaction.getMissionTransactionId(), gregStartDateString, null, System.currentTimeMillis() + "", missionData == null ? null : missionData.getDecisionNumber())));
	    session.flushTransaction();
	    PayrollEngineService.doPayrollIntegration(adminDecisionId, employee.getCategoryId(), gregTransactionDateString, adminDecisionEmployeeDataList, employee.getPhysicalUnitId(), gregTransactionDateString, DataAccess.getTableName(IncentiveTransaction.class), resendFlag, FlagsEnum.OFF.getCode(), session);
	}
    }

    private static void validateIncentiveTransaction(IncentiveTransaction incentiveTransaction) throws BusinessException {
	if (incentiveTransaction == null)
	    throw new BusinessException("error_integrationMandatoryFields");
	if (incentiveTransaction.getIncentiveTypeId() == null)
	    throw new BusinessException("error_integrationMandatoryFields");
	if (incentiveTransaction.getEmployeeId() == null)
	    throw new BusinessException("error_integrationMandatoryFields");
	if (incentiveTransaction.getStartDate() == null)
	    throw new BusinessException("error_integrationMandatoryFields");
	if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.FINANCIAL_LOSS_COMPENSATION.getCode())) {
	    if (incentiveTransaction.getCompensationAmount() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	} else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.ADVISORY_COUNCILS.getCode())) {
	    if (incentiveTransaction.getPortId() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	} else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.GOVERNMENTAL_COMMITTEES.getCode())) {
	    if (incentiveTransaction.getSessionsCount() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	    if (incentiveTransaction.getCommittieeCategoryId() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	} else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.DRUGS_DESTRUCTION.getCode())) {
	    if (incentiveTransaction.getDestructionsCount() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	} else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.TEST_COMMITTEES.getCode())) {
	    if (incentiveTransaction.getPeriod() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	} else if (incentiveTransaction.getIncentiveTypeId().equals(IncentiveTypesEnum.MISSIONS_DIFFERENCES.getCode())) {
	    if (incentiveTransaction.getMissionTransactionId() == null)
		throw new BusinessException("error_integrationMandatoryFields");
	}
    }

    private static IncentiveTransaction getIncentiveTransactionById(Long id) throws DatabaseException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_ID", id);
	List<IncentiveTransaction> list = DataAccess.executeNamedQuery(IncentiveTransaction.class, QueryNamesEnum.HCM_INCENTIVE_GET_INCENTIVE_BY_ID.getCode(), qParams);
	return list == null || list.size() == 0 ? null : list.get(0);
    }

    public static List<IncentivePort> getIncentivePorts() throws DatabaseException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	return DataAccess.executeNamedQuery(IncentivePort.class, QueryNamesEnum.HCM_INCENTIVE_PORT_GET_INCENTIVE_PORTS.getCode(), qParams);
    }

    public static List<GovernmentalCommitteeCategory> getGovernmentalCommitteesCategories() throws DatabaseException {
	Map<String, Object> qParams = new HashMap<String, Object>();
	return DataAccess.executeNamedQuery(GovernmentalCommitteeCategory.class, QueryNamesEnum.HCM_GOVERNMENTAL_COMMITTEE_CATEGORY_GET_GOVERNMENTAL_COMMITTEES_CATEGORIES.getCode(), qParams);
    }
}
