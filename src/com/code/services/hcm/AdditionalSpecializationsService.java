package com.code.services.hcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.additionalspecializations.AdditionalSpecialization;
import com.code.dal.orm.hcm.additionalspecializations.EmployeeAdditionalSpecializationData;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

public class AdditionalSpecializationsService extends BaseService {
    private AdditionalSpecializationsService() {
    }

    /* ********************************** Employee Additional Specialization Data ********************************* */

    /*---------------------------Operations---------------------------*/

    public static void addEmployeeAdditionalSpecialization(EmployeeAdditionalSpecializationData employeeAdditionalSpecializationData, CustomSession... useSession) throws BusinessException {

	if (employeeAdditionalSpecializationData != null) {
	    validateEmployeeAdditionalSpecializationData(employeeAdditionalSpecializationData);

	    boolean isOpenedSession = isSessionOpened(useSession);
	    CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	    try {
		if (!isOpenedSession)
		    session.beginTransaction();

		DataAccess.addEntity(employeeAdditionalSpecializationData.getEmployeeAdditionalSpecialization(), session);

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
    }

    public static void changeEmpAdditionalSpecMvtBlackListFlag(EmployeeAdditionalSpecializationData employeeAdditionalSpecializationData, CustomSession... useSession) throws BusinessException {

	if (employeeAdditionalSpecializationData != null) {

	    boolean isOpenedSession = isSessionOpened(useSession);
	    CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	    try {
		if (!isOpenedSession)
		    session.beginTransaction();

		DataAccess.updateEntity(employeeAdditionalSpecializationData.getEmployeeAdditionalSpecialization(), session);

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
    }

    public static void deleteEmployeeAdditionalSpecialization(EmployeeAdditionalSpecializationData employeeAdditionalSpecializationData, CustomSession... useSession) throws BusinessException {

	if (employeeAdditionalSpecializationData != null) {
	    boolean isOpenedSession = isSessionOpened(useSession);
	    CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	    try {
		if (!isOpenedSession)
		    session.beginTransaction();

		DataAccess.deleteEntity(employeeAdditionalSpecializationData.getEmployeeAdditionalSpecialization(), session);

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
    }

    /*---------------------------Validations-------- -----------------*/
    private static void validateEmployeeAdditionalSpecializationData(EmployeeAdditionalSpecializationData employeeAdditionalSpecializationData) throws BusinessException {
	if (employeeAdditionalSpecializationData.getEmpId() == null)
	    throw new BusinessException("error_empSelectionManadatory");
	if (employeeAdditionalSpecializationData.getAdditionalSpecializationId() == null)
	    throw new BusinessException("error_additionalSpecIsMandatory");
	if (employeeAdditionalSpecializationData.getMovementBlacklistFlag() == null)
	    throw new BusinessException("error_additionalSpecMvtBlackListMandatory");

	if (searchEmployeeAdditionalSpecializationData(employeeAdditionalSpecializationData.getEmpId(), employeeAdditionalSpecializationData.getAdditionalSpecializationId(), FlagsEnum.ALL.getCode()).size() > 0)
	    throw new BusinessException("error_repeatedEmployeeAdditionalSpecialization");
    }

    /*---------------------------Queries------------------------------*/

    public static List<EmployeeAdditionalSpecializationData> getEmployeeAdditionalSpecializationsDataByEmpId(long empId) throws BusinessException {
	return searchEmployeeAdditionalSpecializationData(empId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    public static List<EmployeeAdditionalSpecializationData> getEmployeesAdditionalSpecializationDataByAdditionalSpecializationIdAndPhysicalRegionId(long additionalSpecializationId, long physicalRegionId) throws BusinessException {
	return searchEmployeeAdditionalSpecializationData(FlagsEnum.ALL.getCode(), additionalSpecializationId, physicalRegionId);
    }

    private static List<EmployeeAdditionalSpecializationData> searchEmployeeAdditionalSpecializationData(long empId, long additionalSpecializationId, long physicalRegionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_ADDITIONAL_SPECIALIZATION_ID", additionalSpecializationId);
	    qParams.put("P_PHYSICAL_REGION_ID", physicalRegionId);

	    return DataAccess.executeNamedQuery(EmployeeAdditionalSpecializationData.class, QueryNamesEnum.HCM_SEARCH_EMPLOYEE_ADDITIONAL_SPECIALIZATION_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /* ********************************** Additional Specialization ********************************* */
    /*---------------------------operations------------------------------*/

    public static void addAdditionalSpecialization(AdditionalSpecialization additionalSpecializtion, CustomSession... useSession) throws BusinessException {

	if (additionalSpecializtion != null) {
	    validateAdditionalSpecialization(additionalSpecializtion);

	    boolean isOpenedSession = isSessionOpened(useSession);
	    CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	    try {
		if (!isOpenedSession)
		    session.beginTransaction();

		DataAccess.addEntity(additionalSpecializtion, session);

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
    }

    public static void updateAdditionalSpecialization(AdditionalSpecialization additionalSpecializtion, CustomSession... useSession) throws BusinessException {

	if (additionalSpecializtion != null) {
	    validateAdditionalSpecialization(additionalSpecializtion);

	    boolean isOpenedSession = isSessionOpened(useSession);
	    CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	    try {
		if (!isOpenedSession)
		    session.beginTransaction();

		DataAccess.updateEntity(additionalSpecializtion, session);

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
    }

    public static void deleteAdditionalSpecialization(AdditionalSpecialization additionalSpecializtion, CustomSession... useSession) throws BusinessException {

	if (additionalSpecializtion != null) {
	    checkUsageOfAdditionalSpecialization(additionalSpecializtion);

	    boolean isOpenedSession = isSessionOpened(useSession);
	    CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	    try {
		if (!isOpenedSession)
		    session.beginTransaction();

		DataAccess.deleteEntity(additionalSpecializtion, session);

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
    }

    /*---------------------------validations------------------------------*/
    public static void validateAdditionalSpecialization(AdditionalSpecialization additionalSpecialization) throws BusinessException {
	if (additionalSpecialization.getDescription().trim().isEmpty())
	    throw new BusinessException("error_additionalSpecNameIsMandatory");
	checkRepeatedAdditionalSpecializationDescription(additionalSpecialization);
	if (additionalSpecialization.getId() != null)
	    checkUsageOfAdditionalSpecialization(additionalSpecialization);
    }

    public static void checkRepeatedAdditionalSpecializationDescription(AdditionalSpecialization additionalSpecialization) throws BusinessException {
	if (searchAdditionalSpecialization(additionalSpecialization.getDescription(), additionalSpecialization.getId() != null ? additionalSpecialization.getId() : FlagsEnum.ALL.getCode(), true).size() > 0)
	    throw new BusinessException("error_repeatedAdditionalSpecName");
    }

    public static void checkUsageOfAdditionalSpecialization(AdditionalSpecialization additionalSpecialization) throws BusinessException {
	if (searchEmployeeAdditionalSpecializationData(FlagsEnum.ALL.getCode(), additionalSpecialization.getId(), FlagsEnum.ALL.getCode()).size() > 0)
	    throw new BusinessException("error_additionalSpecIsUsedInTheSystem");
    }

    /*---------------------------Queries------------------------------*/

    public static List<AdditionalSpecialization> getAdditionalSpecializations(String description) throws BusinessException {
	return searchAdditionalSpecialization(description, FlagsEnum.ALL.getCode(), false);
    }

    private static List<AdditionalSpecialization> searchAdditionalSpecialization(String description, long exculudedId, boolean isExactDesc) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    if (isExactDesc)
		qParams.put("P_DESCRIPTION", (description == null || description.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : description);
	    else
		qParams.put("P_DESCRIPTION", (description == null || description.length() == 0) ? (FlagsEnum.ALL.getCode() + "") : "%" + description + "%");
	    qParams.put("P_EXCLUDED_ID", exculudedId);
	    return DataAccess.executeNamedQuery(AdditionalSpecialization.class, QueryNamesEnum.HCM_SEARCH_ADDITIONAL_SPECIALIZATION.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------Report------------------------------*/

    public static byte[] getAdditionalSpecializationDataBytes(long regionId, long empId, long additionalSpecId, String unitHKey) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.EMPLOYEES_ADDITIONAL_SPECIALIZATION.getCode();

	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_PHYSICAL_REGION_ID", regionId);
	    parameters.put("P_EMPLOYEE_ID", empId);
	    parameters.put("P_ADDITIONAL_SPEC_ID", additionalSpecId);
	    parameters.put("P_PHYSICAL_UNIT_HKEY", unitHKey);

	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(getMessage("error_reportPrintingError"));
	}
    }
}
