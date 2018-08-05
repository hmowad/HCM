package com.code.services.hcm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.navyformations.NavyFormation;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

public class NavyFormationsService extends BaseService {
    private NavyFormationsService() {
    }

    /*---------------------------Operations---------------------------*/
    public static void addNavyFormation(NavyFormation navyFormation, int type, CustomSession... useSession) throws BusinessException {
	validateNavyFormation(navyFormation, type);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(navyFormation, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    navyFormation.setId(null);
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void updateNavyFormation(NavyFormation navyFormation, int type, CustomSession... useSession) throws BusinessException {
	validateNavyFormation(navyFormation, type);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(navyFormation, session);

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

    public static void deleteNavyFormation(NavyFormation navyFormation, CustomSession... useSession) throws BusinessException {
	if (navyFormation == null || navyFormation.getId() == null)
	    throw new BusinessException("error_general");

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(navyFormation, session);

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

    /*---------------------------Validations--------------------------*/
    private static void validateNavyFormation(NavyFormation navyFormation, int type) throws BusinessException {

	if (type != FlagsEnum.OFF.getCode() && type != FlagsEnum.ON.getCode())
	    throw new BusinessException("error_general");

	if (navyFormation.getDescription() == null || navyFormation.getDescription().isEmpty())
	    throw new BusinessException(type == FlagsEnum.OFF.getCode() ? "error_navyCommitteeDescription" : "error_navyCommitteeDescription");

	if (type == FlagsEnum.OFF.getCode() && navyFormation.getRegionId() == null) {
	    throw new BusinessException("error_regionMandatory");
	}

	if (checkNavyFormationDescriptionRepeated(navyFormation.getDescription(), navyFormation.getId()))
	    throw new BusinessException(type == FlagsEnum.OFF.getCode() ? "error_repeatedNavyVesselDescription" : "error_repeatedNavyCommitteeDescription");
    }

    /*---------------------------Queries------------------------------*/
    public static List<NavyFormation> getNavyVessels(String description, Long regionId) throws BusinessException {
	return searchNavyFormations(description, new Long(FlagsEnum.ALL.getCode()), FlagsEnum.OFF.getCode(), regionId);
    }

    public static List<NavyFormation> getNavyCommittees(String description) throws BusinessException {
	return searchNavyFormations(description, new Long(FlagsEnum.ALL.getCode()), FlagsEnum.ON.getCode(), new Long(FlagsEnum.ALL.getCode()));
    }

    private static boolean checkNavyFormationDescriptionRepeated(String description, Long execludedNavyFormationId) throws BusinessException {
	return searchNavyFormations(description, execludedNavyFormationId == null ? FlagsEnum.ALL.getCode() : execludedNavyFormationId, FlagsEnum.ALL.getCode(), new Long(FlagsEnum.ALL.getCode())).size() > 0;
    }

    private static List<NavyFormation> searchNavyFormations(String description, Long execludedNavyFormationId, long type, Long regionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_DESCRIPTION", (description == null || description.isEmpty() || description.equals(FlagsEnum.ALL.getCode() + "")) ? FlagsEnum.ALL.getCode() + "" : (type == FlagsEnum.ALL.getCode() ? description : "%" + description + "%"));
	    qParams.put("P_EXECLUDED_NAVY_FORMATION_ID", execludedNavyFormationId == null ? FlagsEnum.ALL.getCode() : execludedNavyFormationId);
	    qParams.put("P_TYPE", type);
	    qParams.put("P_REGION_ID", regionId == null ? FlagsEnum.ALL.getCode() : regionId);
	    return DataAccess.executeNamedQuery(NavyFormation.class, QueryNamesEnum.HCM_SEARCH_NAVY_FORMATIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------reports------------------------------*/
    public static byte[] getNavyFormationDataBytes(long categoryId, long employeeId, long physicalRegionId, long physicalUnitId, String navyFormationDescription) throws BusinessException {
	try {
	    String reportName = ReportNamesEnum.NAVY_FORMATION_REPORT.getCode();
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    parameters.put("P_CATEGORY_ID", categoryId);

	    parameters.put("P_EMPLOYEE_ID", employeeId);
	    parameters.put("P_PHYSICAL_REGION_ID", physicalRegionId);
	    parameters.put("P_PHYSICAL_UNIT_ID", physicalUnitId);
	    parameters.put("P_NAVY_FORMATION_DESCRIPTION", (navyFormationDescription == null || navyFormationDescription.isEmpty()) ? FlagsEnum.ALL.getCode() + "" : navyFormationDescription);

	    parameters.put("P_PRINT_DATE", HijriDateService.getHijriSysDateString());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}
