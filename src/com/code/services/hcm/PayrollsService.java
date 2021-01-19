package com.code.services.hcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.payroll.Degree;
import com.code.dal.orm.hcm.payroll.EmployeeAllowancesData;
import com.code.dal.orm.hcm.payroll.EmployeeBonusesData;
import com.code.dal.orm.hcm.payroll.EmployeePayrollDifferenceData;
import com.code.dal.orm.hcm.payroll.EmployeePenalitiesData;
import com.code.dal.orm.hcm.payroll.PayrollSalary;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;

public class PayrollsService extends BaseService {
    private PayrollsService() {
    }

    public static List<EmployeePayrollDifferenceData> getEmployeePayrollDifferences(Long empId, int transactionStatus, String elementDescription) throws BusinessException {
	return searchEmployeePayrollDifferences(empId, transactionStatus, elementDescription);
    }

    private static List<EmployeePayrollDifferenceData> searchEmployeePayrollDifferences(Long empId, int transactionStatus, String elementDescription) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_TRANSACTION_STATUS", transactionStatus);
	    qParams.put("P_ELEMENT_DESC", (elementDescription == null || elementDescription.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + elementDescription + "%");
	    return DataAccess.executeNamedQuery(EmployeePayrollDifferenceData.class, QueryNamesEnum.HCM_GET_PAYROLL_DIFF_BY_TRANS_STATUS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------- Degrees -----------------------------------*/
    public static List<Degree> getAllDegrees() throws BusinessException {
	return searchDegrees();
    }

    public static Long getEndOfLadderOfRank(long rankId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RANK_ID", rankId);

	    List<Long> degree = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_GET_END_OF_LADDER_OF_RANK.getCode(), qParams);
	    return degree.isEmpty() ? null : degree.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<Degree> searchDegrees() throws BusinessException {
	try {
	    return DataAccess.executeNamedQuery(Degree.class, QueryNamesEnum.HCM_GET_ALL_DEGREES.getCode(), null);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<PayrollSalary> getEndOfLadderForAllRanksOfCategory(long categoryId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CATEGORY_ID", categoryId);

	    return DataAccess.executeNamedQuery(PayrollSalary.class, QueryNamesEnum.HCM_GET_END_OF_LADDER_FOR_ALL_RANKS_OFF_CATEGORY.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /*---------------------------------- Salaries -----------------------------------*/
    public static PayrollSalary getPayrollSalary(long rankId, long degreeId) throws BusinessException {
	List<PayrollSalary> payrollSalaries = searchPayrollSalaries(rankId, degreeId);
	if (payrollSalaries == null || payrollSalaries.size() == 0)
	    return null;
	return payrollSalaries.get(0);
    }

    private static List<PayrollSalary> searchPayrollSalaries(Long rankId, Long degreeId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RANK_ID", rankId == null ? (long) FlagsEnum.ALL.getCode() : rankId);
	    qParams.put("P_DEGREE_ID", degreeId == null ? (long) FlagsEnum.ALL.getCode() : degreeId);

	    return DataAccess.executeNamedQuery(PayrollSalary.class, QueryNamesEnum.HCM_SEARCH_PAYROLL_SALARIES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static PayrollSalary getPayrollNewSalary(long rankId, double salary) throws BusinessException {
	List<PayrollSalary> payrollSalaries = searchPayrollNewSalaries(rankId, salary);
	if (payrollSalaries == null || payrollSalaries.size() == 0)
	    return null;
	return payrollSalaries.get(0);
    }

    private static List<PayrollSalary> searchPayrollNewSalaries(long rankId, double salary) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RANK_ID", rankId);
	    qParams.put("P_BASIC_SALARY", salary);

	    return DataAccess.executeNamedQuery(PayrollSalary.class, QueryNamesEnum.HCM_SEARCH_PAYROLL_NEW_SALARIES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static PayrollSalary getPayrollNewSalaryDemoting(long rankId, double salary) throws BusinessException {
	List<PayrollSalary> payrollSalaries = searchPayrollNewSalariesDemoting(rankId, salary);
	if (payrollSalaries == null || payrollSalaries.size() == 0)
	    return null;
	return payrollSalaries.get(0);
    }

    private static List<PayrollSalary> searchPayrollNewSalariesDemoting(long rankId, double salary) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_RANK_ID", rankId);
	    qParams.put("P_BASIC_SALARY", salary);

	    return DataAccess.executeNamedQuery(PayrollSalary.class, QueryNamesEnum.HCM_SEARCH_PAYROLL_NEW_SALARIES_DEMOTING.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------- Penalities -----------------------------------*/
    public static List<EmployeePenalitiesData> getEmployeePenalities(String employeeOldId) throws BusinessException {
	return searchEmployeePenalities(employeeOldId);
    }

    private static List<EmployeePenalitiesData> searchEmployeePenalities(String employeeOldId) throws BusinessException {
	try {
	    if (employeeOldId == null)
		return new ArrayList<EmployeePenalitiesData>();
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_OLD_ID", employeeOldId);
	    return DataAccess.executeNamedQuery(EmployeePenalitiesData.class, QueryNamesEnum.HCM_GET_EMPLOYEE_PENALITIES_BY_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------- Bonuses -----------------------------------*/
    public static List<EmployeeBonusesData> getEmployeeBonuses(String employeeOldId) throws BusinessException {
	return searchEmployeeBonuses(employeeOldId);
    }

    private static List<EmployeeBonusesData> searchEmployeeBonuses(String employeeOldId) throws BusinessException {
	try {
	    if (employeeOldId == null)
		return new ArrayList<EmployeeBonusesData>();
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_OLD_ID", employeeOldId);
	    return DataAccess.executeNamedQuery(EmployeeBonusesData.class, QueryNamesEnum.HCM_GET_EMPLOYEE_BONUSES_BY_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------- Allowances -----------------------------------*/
    public static List<EmployeeAllowancesData> getEmployeeAllowances(String employeeOldId) throws BusinessException {
	return searchEmployeeAllowances(employeeOldId);
    }

    private static List<EmployeeAllowancesData> searchEmployeeAllowances(String employeeOldId) throws BusinessException {
	try {
	    if (employeeOldId == null)
		return new ArrayList<EmployeeAllowancesData>();
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_OLD_ID", employeeOldId);
	    return DataAccess.executeNamedQuery(EmployeeAllowancesData.class, QueryNamesEnum.HCM_GET_EMPLOYEE_ALLOWANCES_BY_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}