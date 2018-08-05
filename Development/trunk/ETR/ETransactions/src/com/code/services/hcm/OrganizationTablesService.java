package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.organization.jobs.JobData;
import com.code.dal.orm.hcm.organization.units.OrganizationTable;
import com.code.dal.orm.hcm.organization.units.OrganizationTableDetail;
import com.code.dal.orm.hcm.organization.units.OrganizationTableDetailData;
import com.code.dal.orm.hcm.organization.units.OrganizationTableTransaction;
import com.code.dal.orm.hcm.organization.units.OrganizationTableTransactionData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.JobStatusEnum;
import com.code.enums.OperationsTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

/**
 * Provides methods for handling the organization tables operations such as construct, rename or cancel organization tables.
 * 
 */
public class OrganizationTablesService extends BaseService {

    /**
     * A constant holding special code.
     */
    private static final String SPECIAL_DECISION_NUMBER = "*2#";

    /**
     * Constructs the organization tables service.
     */
    private OrganizationTablesService() {
    }

    /*----------------------------------------------------- Organization Table -----------------------------------------------------*/

    public static void constructOrganizationTable(boolean completionFlag, String decisionNumber, Date decisionDate, Integer categoryClass, List<UnitData> units,
	    List<OrganizationTableDetailData> organizationTableDetails, Long userId, CustomSession... useSession) throws BusinessException {

	// trimming data
	decisionNumber = decisionNumber != null ? decisionNumber.trim() : decisionNumber;

	if (units.isEmpty())
	    throw new BusinessException("error_unitsListEmpty");

	Long[] unitsIds = new Long[units.size()];
	for (int i = 0; i < units.size(); i++) {
	    unitsIds[i] = units.get(i).getId();
	}
	List<OrganizationTable> organizationTables = getActiveOrganizationTables(unitsIds, categoryClass);

	validateAddOrModifyOrganizationTable(completionFlag, false, decisionNumber, decisionDate, categoryClass, units, organizationTables, organizationTableDetails);

	Map<Long, OrganizationTable> unitsOrganizationTablesMap = new HashMap<Long, OrganizationTable>();
	for (OrganizationTable organizationTable : organizationTables) {
	    unitsOrganizationTablesMap.put(organizationTable.getUnitId(), organizationTable);
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {

	    if (!isOpenedSession)
		session.beginTransaction();

	    for (UnitData unitData : units) {

		if (!completionFlag) {
		    OrganizationTable organizationTable = new OrganizationTable();
		    organizationTable.setCategoryClass(categoryClass);
		    organizationTable.setActiveFlag(FlagsEnum.ON.getCode());
		    organizationTable.setDecisionNumber(decisionNumber);
		    organizationTable.setDecisionDate(decisionDate);
		    organizationTable.setUnitId(unitData.getId());

		    organizationTable.setSystemUser(userId + ""); // For Audit.

		    DataAccess.addEntity(organizationTable, session);
		    // update organization table id
		    unitsOrganizationTablesMap.put(unitData.getId(), organizationTable);
		}

		for (OrganizationTableDetailData organizationTableDetailData : organizationTableDetails) {

		    if (organizationTableDetailData.getId() != null)
			continue;

		    // Create OrganizationTableDetail Object
		    OrganizationTableDetail organizationTableDetail = new OrganizationTableDetail();

		    organizationTableDetail.setOrganizationTableId(unitsOrganizationTablesMap.get(unitData.getId()).getId());
		    organizationTableDetail.setBasicJobName(organizationTableDetailData.getBasicJobName());
		    organizationTableDetail.setRankId(organizationTableDetailData.getRankId());
		    organizationTableDetail.setJobsCount(organizationTableDetailData.getJobsCount());
		    organizationTableDetail.setGeneralSpecialization(organizationTableDetailData.getGeneralSpecialization());
		    organizationTableDetail.setMinorSpecializationId(organizationTableDetailData.getMinorSpecializationId());
		    organizationTableDetail.setClassificationId(organizationTableDetailData.getClassificationId());
		    organizationTableDetail.setGeneralType(organizationTableDetailData.getGeneralType());
		    organizationTableDetail.setApprovedFlag(organizationTableDetailData.getApprovedFlag());
		    organizationTableDetail.setActiveFlag(FlagsEnum.ON.getCode());

		    DataAccess.addEntity(organizationTableDetail, session);
		    addOrganizationTableTransaction(organizationTableDetail, decisionDate, decisionNumber, TransactionTypesEnum.ORG_TABLE_CONSTRUCTION.getCode(), userId, session);
		}
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

    public static void modifyOrganizationTable(String decisionNumber, Date decisionDate, Integer categoryClass, List<UnitData> units,
	    List<OrganizationTableDetailData> organizationTableDetails, List<OrganizationTableDetailData> unitsOriginalOrganizationTableDetails, int organizationTableDetailsCountPerUnit,
	    Long userId, CustomSession... useSession) throws BusinessException {

	// trimming data
	decisionNumber = decisionNumber != null ? decisionNumber.trim() : decisionNumber;

	if (units.isEmpty())
	    throw new BusinessException("error_unitsListEmpty");

	Long[] unitsIds = new Long[units.size()];
	for (int i = 0; i < units.size(); i++) {
	    unitsIds[i] = units.get(i).getId();
	}

	List<OrganizationTable> organizationTables = getActiveOrganizationTables(unitsIds, categoryClass);

	validateAddOrModifyOrganizationTable(false, true, decisionNumber, decisionDate, categoryClass, units, organizationTables, organizationTableDetails);

	Map<Long, OrganizationTable> unitsOrganizationTablesMap = new HashMap<Long, OrganizationTable>();
	for (OrganizationTable organizationTable : organizationTables) {
	    unitsOrganizationTablesMap.put(organizationTable.getUnitId(), organizationTable);
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    // Update an already existing organization table details for all selected units
	    for (OrganizationTableDetailData detail : organizationTableDetails) {

		if (detail.getId() == null)
		    continue;

		OrganizationTableDetailData oldOrganizationTableDetail = unitsOriginalOrganizationTableDetails.get(detail.getIndex());

		if (oldOrganizationTableDetail.getOperationType() == null || !oldOrganizationTableDetail.getOperationType().equals(OperationsTypesEnum.UPDATE.getCode()))
		    continue;

		if (detail.equals(oldOrganizationTableDetail))
		    continue;

		for (int i = detail.getIndex(); i < unitsOriginalOrganizationTableDetails.size(); i += organizationTableDetailsCountPerUnit) {
		    if (unitsOrganizationTablesMap.containsKey(unitsOriginalOrganizationTableDetails.get(i).getUnitId())) {
			unitsOriginalOrganizationTableDetails.get(i).setBasicJobName(detail.getBasicJobName());
			unitsOriginalOrganizationTableDetails.get(i).setRankId(detail.getRankId());
			unitsOriginalOrganizationTableDetails.get(i).setJobsCount(detail.getJobsCount());
			unitsOriginalOrganizationTableDetails.get(i).setGeneralSpecialization(detail.getGeneralSpecialization());
			unitsOriginalOrganizationTableDetails.get(i).setMinorSpecializationId(detail.getMinorSpecializationId());
			unitsOriginalOrganizationTableDetails.get(i).setGeneralType(detail.getGeneralType());
			unitsOriginalOrganizationTableDetails.get(i).setApprovedFlag(detail.getApprovedFlag());
			unitsOriginalOrganizationTableDetails.get(i).setClassificationId(detail.getClassificationId());

			if (decisionNumber.equals(SPECIAL_DECISION_NUMBER))
			    unitsOriginalOrganizationTableDetails.get(i).getOrganizationTableDetail().setSystemUser(userId + ""); // For Audit.

			DataAccess.updateEntity(unitsOriginalOrganizationTableDetails.get(i).getOrganizationTableDetail(), session);

			if (!decisionNumber.equals(SPECIAL_DECISION_NUMBER))
			    addOrganizationTableTransaction(unitsOriginalOrganizationTableDetails.get(i).getOrganizationTableDetail(), decisionDate, decisionNumber, TransactionTypesEnum.ORG_TABLE_MODIFICATION.getCode(), userId, session);
		    }
		}
	    }

	    // Delete an already existing organization table detail for all selected units.
	    for (OrganizationTableDetailData detail : unitsOriginalOrganizationTableDetails) {
		if (unitsOrganizationTablesMap.containsKey(detail.getUnitId())) {

		    // Non-deleted organization table detail.
		    if (detail.getOperationType() == null || !detail.getOperationType().equals(OperationsTypesEnum.DELETE.getCode()))
			continue;

		    // Deleted organization table detail so change it to non active.
		    detail.setActiveFlag(FlagsEnum.OFF.getCode());

		    if (decisionNumber.equals(SPECIAL_DECISION_NUMBER))
			detail.getOrganizationTableDetail().setSystemUser(userId + ""); // For Audit.

		    DataAccess.updateEntity(detail.getOrganizationTableDetail(), session);

		    if (!decisionNumber.equals(SPECIAL_DECISION_NUMBER))
			addOrganizationTableTransaction(detail.getOrganizationTableDetail(), decisionDate, decisionNumber, TransactionTypesEnum.ORG_TABLE_MODIFICATION.getCode(), userId, session);
		}
	    }

	    // Add all new entities
	    for (UnitData unitData : units) {

		OrganizationTable organizationTable = unitsOrganizationTablesMap.get(unitData.getId());
		if (!decisionNumber.equals(SPECIAL_DECISION_NUMBER)) {
		    organizationTable.setDecisionNumber(decisionNumber);
		    organizationTable.setDecisionDate(decisionDate);

		    organizationTable.setSystemUser(userId + ""); // For Audit.

		    DataAccess.updateEntity(organizationTable, session);
		}
		// Add new details
		for (OrganizationTableDetailData organizationTableDetailData : organizationTableDetails) {

		    if (organizationTableDetailData.getId() != null)
			continue;

		    OrganizationTableDetail orgTableDetail = new OrganizationTableDetail();

		    orgTableDetail.setOrganizationTableId(organizationTable.getId());
		    orgTableDetail.setBasicJobName(organizationTableDetailData.getBasicJobName());
		    orgTableDetail.setRankId(organizationTableDetailData.getRankId());
		    orgTableDetail.setJobsCount(organizationTableDetailData.getJobsCount());
		    orgTableDetail.setGeneralSpecialization(organizationTableDetailData.getGeneralSpecialization());
		    orgTableDetail.setMinorSpecializationId(organizationTableDetailData.getMinorSpecializationId());
		    orgTableDetail.setClassificationId(organizationTableDetailData.getClassificationId());
		    orgTableDetail.setGeneralType(organizationTableDetailData.getGeneralType());
		    orgTableDetail.setApprovedFlag(organizationTableDetailData.getApprovedFlag());
		    orgTableDetail.setActiveFlag(FlagsEnum.ON.getCode());

		    if (decisionNumber.equals(SPECIAL_DECISION_NUMBER))
			orgTableDetail.setSystemUser(userId + ""); // For Audit.

		    DataAccess.addEntity(orgTableDetail, session);

		    if (!decisionNumber.equals(SPECIAL_DECISION_NUMBER))
			addOrganizationTableTransaction(orgTableDetail, decisionDate, decisionNumber, TransactionTypesEnum.ORG_TABLE_MODIFICATION.getCode(), userId, session);
		}
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

    public static void cancelOrganizationTable(String decisionNumber, Date decisionDate, Integer categoryClass, List<UnitData> units,
	    List<OrganizationTableDetailData> unitsOriginalOrganizationTableDetails, Long userId, CustomSession... useSession) throws BusinessException {

	// trimming data
	decisionNumber = decisionNumber != null ? decisionNumber.trim() : decisionNumber;

	if (units.isEmpty())
	    throw new BusinessException("error_unitsListEmpty");

	Long[] unitsIds = new Long[units.size()];
	for (int i = 0; i < units.size(); i++) {
	    unitsIds[i] = units.get(i).getId();
	}
	List<OrganizationTable> organizationTables = getActiveOrganizationTables(unitsIds, categoryClass);

	validateCancelOrganizationTable(decisionNumber, decisionDate, categoryClass, units, organizationTables, unitsOriginalOrganizationTableDetails);

	Map<Long, OrganizationTable> unitsOrganizationTablesMap = new HashMap<Long, OrganizationTable>();
	for (OrganizationTable organizationTable : organizationTables) {
	    unitsOrganizationTablesMap.put(organizationTable.getUnitId(), organizationTable);
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {

	    if (!isOpenedSession)
		session.beginTransaction();

	    for (OrganizationTableDetailData detail : unitsOriginalOrganizationTableDetails) {
		if (unitsOrganizationTablesMap.containsKey(detail.getUnitId())) {
		    detail.setActiveFlag(FlagsEnum.OFF.getCode());

		    DataAccess.updateEntity(detail.getOrganizationTableDetail(), session);
		    addOrganizationTableTransaction(detail.getOrganizationTableDetail(), decisionDate, decisionNumber,
			    TransactionTypesEnum.ORG_TABLE_CANCELLATION.getCode(), userId, session);
		}
	    }

	    // Set Active flag for organization tables to false
	    for (UnitData unitData : units) {
		OrganizationTable organizationTable = unitsOrganizationTablesMap.get(unitData.getId());
		organizationTable.setDecisionNumber(decisionNumber);
		organizationTable.setDecisionDate(decisionDate);

		organizationTable.setActiveFlag(FlagsEnum.OFF.getCode());

		organizationTable.setSystemUser(userId + ""); // For Audit.

		DataAccess.updateEntity(organizationTable, session);
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

    private static void validateAddOrModifyOrganizationTable(boolean completionFlag, boolean modificationFlag, String decisionNumber, Date decisionDate, Integer categoryClass,
	    List<UnitData> units, List<OrganizationTable> organizationTables, List<OrganizationTableDetailData> organizationTableDetails) throws BusinessException {

	if (decisionNumber == null || decisionNumber.isEmpty())
	    throw new BusinessException("error_decNumberMandatory");
	else if (!modificationFlag && (decisionNumber.contains("*") || decisionNumber.contains("#")))
	    throw new BusinessException("error_decNumberContainsProhibitedCharacter");

	if (decisionDate == null)
	    throw new BusinessException("error_decDateMandatory");

	// Decision Date must be today or before
	validateOldHijriDate(decisionDate);

	if (categoryClass == null)
	    throw new BusinessException("error_categoryClassMandatory");

	if (units == null || units.size() == 0)
	    throw new BusinessException("error_orgTableUnitsListEmpty");

	if (organizationTableDetails == null || organizationTableDetails.size() == 0)
	    throw new BusinessException("error_orgTableDetailsListEmpty");

	// Check that all selected units has NO organization tables for this category class
	if (!completionFlag && !modificationFlag && organizationTables.size() != 0)
	    throw new BusinessException("error_unitHasOrganizationTable");

	else if ((completionFlag || modificationFlag) && organizationTables.size() != units.size())
	    throw new BusinessException("error_unitHasNoOrganizationTable");

	// All units should have the same type id
	UnitData firstUnit = units.get(0);
	for (UnitData unit : units) {
	    if (!unit.getUnitTypeId().equals(firstUnit.getUnitTypeId()))
		throw new BusinessException("error_allUnitsShouldHaveSameType");
	}

	HashSet<String> detailsSet = new HashSet<String>();

	for (OrganizationTableDetailData organizationTableDetailData : organizationTableDetails) {
	    // Check repeated basicJobName with the same rankId
	    if (!detailsSet.add(organizationTableDetailData.getRankId() + organizationTableDetailData.getBasicJobName()))
		// repeated details with the same basicJobName and rankId
		throw new BusinessException("error_orgTableDetailRepeatedBasicJobNameWithRank",
			new String[] { organizationTableDetailData.getBasicJobName(), organizationTableDetailData.getRankDescription() });

	    if (completionFlag && organizationTableDetailData.getId() != null)
		continue;

	    // Check mandatory fields
	    if (organizationTableDetailData.getBasicJobName() == null || organizationTableDetailData.getBasicJobName().trim().isEmpty())
		throw new BusinessException("error_orgTableDetailBasicJobNameMandatory");

	    if (organizationTableDetailData.getRankId() == null || organizationTableDetailData.getRankId().longValue() == FlagsEnum.ALL.getCode())
		throw new BusinessException("error_orgTableDetailEmpRankMandatory");

	    if (organizationTableDetailData.getJobsCount() == null || organizationTableDetailData.getJobsCount() <= 0 || organizationTableDetailData.getJobsCount() > 999)
		throw new BusinessException("error_jobsCountMandatory");

	    if (organizationTableDetailData.getGeneralSpecialization() == null)
		throw new BusinessException("error_orgTableDetailGeneralSpecializationMandatory");

	    if (organizationTableDetailData.getMinorSpecializationId() == null)
		throw new BusinessException("error_orgTableDetailMinorSpecializationMandatory");

	    if (organizationTableDetailData.getCategoryId().longValue() == CategoriesEnum.PERSONS.getCode()
		    && organizationTableDetailData.getClassificationId() == null)
		throw new BusinessException("error_orgTableDetailClassificationIsMandatory");

	    if (organizationTableDetailData.getCategoryId().longValue() != CategoriesEnum.SOLDIERS.getCode()
		    && organizationTableDetailData.getApprovedFlag().equals(FlagsEnum.OFF.getCode()))
		throw new BusinessException("error_approvedFlagShouldBeActiveForNonSoldiers");
	}
    }

    private static void validateCancelOrganizationTable(String decisionNumber, Date decisionDate, Integer categoryClass, List<UnitData> units,
	    List<OrganizationTable> organizationTables, List<OrganizationTableDetailData> unitsOriginalOrganizationTableDetails) throws BusinessException {

	if (decisionNumber == null || decisionNumber.trim().isEmpty())
	    throw new BusinessException("error_decNumberMandatory");
	else if (decisionNumber.contains("*") || decisionNumber.contains("#"))
	    throw new BusinessException("error_decNumberContainsProhibitedCharacter");

	if (decisionDate == null)
	    throw new BusinessException("error_decDateMandatory");

	validateOldHijriDate(decisionDate);

	if (categoryClass == null)
	    throw new BusinessException("error_categoryClassMandatory");

	if (units == null || units.size() == 0)
	    throw new BusinessException("error_orgTableUnitsListEmpty");

	// Check that all selected units have organization tables for this category class
	if (organizationTables.size() != units.size())
	    throw new BusinessException("error_unitHasNoOrganizationTable");

	// All units should have the same type id
	UnitData firstUnit = units.get(0);
	for (UnitData unit : units) {
	    if (!unit.getUnitTypeId().equals(firstUnit.getUnitTypeId()))
		throw new BusinessException("error_allUnitsShouldHaveSameType");
	}
    }

    public static List<OrganizationTable> getActiveOrganizationTables(Long[] unitsIds, Integer categoryClass) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNITS_IDS", unitsIds);
	    qParams.put("P_CATEGORY_CLASS", categoryClass);

	    return DataAccess.executeNamedQuery(OrganizationTable.class, QueryNamesEnum.HCM_GET_ORGANIZATION_TABLE_BY_UNITS_IDS_AND_CATEGORY_CLASS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static boolean isUnitHasActiveOrganizationTableForCategoryClass(Long unitId, Integer categoryClass) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNIT_ID", unitId);
	    qParams.put("P_CATEGORY_CLASS", categoryClass);

	    long activeOrganizationTableForCategoryClassCount = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_ORGANIZATION_TABLE_BY_UNIT_ID_AND_CATEGORY_CLASS.getCode(), qParams).get(0);

	    if (activeOrganizationTableForCategoryClassCount > 0)
		return true;

	    return false;
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets Organization Table Details belonging to a unit
     * 
     * @param unitDataId
     *            Unit id
     * @param categoryClass
     *            Category class of organization Table
     * @return Organization Table Details records
     * @throws BusinessException
     */
    public static List<OrganizationTableDetailData> getActiveOrganizationTableDetails(Long[] unitsIds, Integer categoryClass) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNITS_IDS", unitsIds);
	    qParams.put("P_CATEGORY_CLASS", categoryClass);

	    return DataAccess.executeNamedQuery(OrganizationTableDetailData.class, QueryNamesEnum.HCM_GET_ORGANIZATION_TABLE_DETAILS_DATA_BY_UNIT_ID_AND_CATEGORY_CLASS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets all organization table details having the same details information as one organization table
     * 
     * @param organizationTableId
     *            Organization Table Id of the selected unit
     * @param detailsCount
     *            Number of active details of the selected unit
     * @return
     * @throws BusinessException
     */
    public static List<OrganizationTableDetailData> getIdenticalOrganizationTableDetails(Long organizationTableId, Integer detailsCount) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ORGANIZATION_TABLE_ID", organizationTableId);
	    qParams.put("P_DETAILS_COUNT", detailsCount);

	    return DataAccess.executeNamedQuery(OrganizationTableDetailData.class, QueryNamesEnum.HCM_GET_IDENTICAL_ORGANIZATION_TABLE_DETAILS_BY_ORGANIZTION_TABLE_ID_AND_DETAILS_COUNT.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*------------------------------------------------- Organization Table Transaction --------------------------------------------------*/

    public static void addOrganizationTableTransaction(OrganizationTableDetail organizationTableDetail, Date decisionDate, String decisionNumber,
	    int transactionTypeCode, Long transactionUserId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    OrganizationTableTransaction organizationTableTransaction = new OrganizationTableTransaction();

	    organizationTableTransaction.setOrganizationTableDetailId(organizationTableDetail.getId());
	    organizationTableTransaction.setBasicJobName(organizationTableDetail.getBasicJobName());
	    organizationTableTransaction.setRankId(organizationTableDetail.getRankId());
	    organizationTableTransaction.setJobsCount(organizationTableDetail.getJobsCount());

	    organizationTableTransaction.setGeneralSpecialization(organizationTableDetail.getGeneralSpecialization());
	    organizationTableTransaction.setMinorSpecializationId(organizationTableDetail.getMinorSpecializationId());
	    organizationTableTransaction.setClassificationId(organizationTableDetail.getClassificationId());
	    organizationTableTransaction.setGeneralType(organizationTableDetail.getGeneralType());
	    organizationTableTransaction.setApprovedFlag(organizationTableDetail.getApprovedFlag());

	    organizationTableTransaction.setDecisionNumber(decisionNumber);
	    organizationTableTransaction.setDecisionDate(decisionDate);

	    organizationTableTransaction.setTransactionTypeId(CommonService.getTransactionTypeByCodeAndClass(transactionTypeCode,
		    TransactionClassesEnum.UNITS.getCode()).getId());

	    organizationTableTransaction.setTransactionUserId(transactionUserId);
	    organizationTableTransaction.setTransactionDate(HijriDateService.getHijriSysDate());

	    organizationTableTransaction.setActiveFlag(organizationTableDetail.getActiveFlag());

	    DataAccess.addEntity(organizationTableTransaction, session);
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

    public static List<OrganizationTableTransactionData> getOrganizationTableTransactions(Long unitId, Integer categoryClass) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNIT_ID", unitId);
	    qParams.put("P_CATEGORY_CLASS", categoryClass);

	    return DataAccess.executeNamedQuery(OrganizationTableTransactionData.class, QueryNamesEnum.HCM_GET_ORGANIZATION_TABLE_TRANSACTION_DATA_BY_UNIT_ID_AND_CATEGORY_CLASS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*------------------------------------------------- Organization Table Jobs Generation --------------------------------------------------*/

    public static void generateOrganizationTableJobs(int categoryClass, List<UnitData> units, Long userId, CustomSession... useSession) throws BusinessException {

	Long[] unitsIds = new Long[units.size()];
	for (int i = 0; i < units.size(); i++) {
	    unitsIds[i] = units.get(i).getId();
	}

	validateGenerateOrganizationTableJobs(categoryClass, unitsIds, units);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<OrganizationTableDetailData> unitsOrganizationTableDetails = OrganizationTablesService.getActiveOrganizationTableDetails(unitsIds, categoryClass);

	    if (CategoriesEnum.OFFICERS.getCode() == categoryClass) {
		List<JobData> officersJobs = new ArrayList<JobData>();
		List<JobData> soldiersJobs = new ArrayList<JobData>();

		for (OrganizationTableDetailData organizationTableDetailData : unitsOrganizationTableDetails) {

		    Long categoryId = CommonService.getRankById(organizationTableDetailData.getRankId()).getCategoryId();
		    List<JobData> jobs = constructJobFromOrganizationTableDetail(organizationTableDetailData, categoryId);

		    if (CategoriesEnum.OFFICERS.getCode() == categoryId.longValue())
			officersJobs.addAll(jobs);
		    else if (CategoriesEnum.SOLDIERS.getCode() == categoryId.longValue())
			soldiersJobs.addAll(jobs);
		}

		if (!officersJobs.isEmpty())
		    JobsService.handleJobsTransactions(officersJobs, null, null, null, null, null, null, null, unitsOrganizationTableDetails.get(0).getDecisionNumber(), unitsOrganizationTableDetails.get(0).getDecisionDate(), HijriDateService.getHijriSysDate(), userId, false, false, false, true, session);

		if (!soldiersJobs.isEmpty())
		    JobsService.handleJobsTransactions(soldiersJobs, null, null, null, null, null, null, null, unitsOrganizationTableDetails.get(0).getDecisionNumber(), unitsOrganizationTableDetails.get(0).getDecisionDate(), HijriDateService.getHijriSysDate(), userId, false, false, false, true, session);

	    } else {
		List<JobData> civiliansJobs = new ArrayList<JobData>();

		for (OrganizationTableDetailData organizationTableDetailData : unitsOrganizationTableDetails) {
		    Long categoryId = CommonService.getRankById(organizationTableDetailData.getRankId()).getCategoryId();
		    civiliansJobs.addAll(constructJobFromOrganizationTableDetail(organizationTableDetailData, categoryId));
		}

		JobsService.handleJobsTransactions(civiliansJobs, null, null, null, null, null, null, null, unitsOrganizationTableDetails.get(0).getDecisionNumber(), unitsOrganizationTableDetails.get(0).getDecisionDate(), HijriDateService.getHijriSysDate(), userId, false, false, false, true, session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    // unitData.setId(null);
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static void validateGenerateOrganizationTableJobs(int categoryClass, Long[] unitsIds, List<UnitData> units) throws BusinessException {

	if (units.isEmpty())
	    throw new BusinessException("error_orgTableUnitsListEmpty");

	if (CategoriesEnum.OFFICERS.getCode() != categoryClass)
	    throw new BusinessException("error_cannotAutoGenerateJobsForNonMilitaries");

	List<OrganizationTable> organizationTables = getActiveOrganizationTables(unitsIds, categoryClass);
	Map<Long, Long> unitOrganizationTableMap = new HashMap<Long, Long>();

	if (units.size() != organizationTables.size()) {
	    for (OrganizationTable organizationTable : organizationTables) {
		unitOrganizationTableMap.put(organizationTable.getUnitId(), organizationTable.getId());
	    }
	    String unitsWithoutOrganizationTable = "";
	    String comma = "";
	    for (UnitData unit : units) {
		if (!unitOrganizationTableMap.containsKey(unit.getId())) {
		    unitsWithoutOrganizationTable += comma + unit.getFullName();
		    comma = ", \n ";
		}
	    }
	    throw new BusinessException("error_unitsHasNoOrganizationTables", new String[] { unitsWithoutOrganizationTable });
	} else {
	    String currentDecisionNumber = organizationTables.get(0).getDecisionNumber();
	    String currentDecisionDateString = organizationTables.get(0).getDecisionDateString();
	    for (int i = 1; i < organizationTables.size(); i++) {
		if (!currentDecisionNumber.equals(organizationTables.get(i).getDecisionNumber()) || !currentDecisionDateString.equals(organizationTables.get(i).getDecisionDateString())) {
		    unitOrganizationTableMap.put(organizationTables.get(i).getUnitId(), organizationTables.get(i).getId());
		}
	    }
	    if (!unitOrganizationTableMap.isEmpty()) {
		String unitsWithDifferentDecisionNumberAndDate = "";
		String comma = "";
		for (UnitData unit : units) {
		    if (unitOrganizationTableMap.containsKey(unit.getId())) {
			unitsWithDifferentDecisionNumberAndDate += comma + unit.getFullName();
			comma = ", \n ";
		    }
		}
		throw new BusinessException("error_unitsHaveOrganizationTableWithDifferentDecisionInfo", new String[] { unitsWithDifferentDecisionNumberAndDate }); // TODO
	    }
	}

	Long[] categoriesIds = null;
	if (CategoriesEnum.OFFICERS.getCode() == categoryClass || CategoriesEnum.SOLDIERS.getCode() == categoryClass)
	    categoriesIds = new Long[] { CategoriesEnum.OFFICERS.getCode(), CategoriesEnum.SOLDIERS.getCode() };
	else
	    categoriesIds = new Long[] { (long) categoryClass };

	long jobscount = JobsService.getJobsCountByUnitsIdsAndCategoriesIds(unitsIds, categoriesIds);
	if (jobscount > 0) {
	    throw new BusinessException("error_unitsHaveJobsWithTheSameCategoryId");
	}
    }

    private static List<JobData> constructJobFromOrganizationTableDetail(OrganizationTableDetailData unitsOrganizationTableDetail, Long categoryId) {

	List<JobData> jobs = new ArrayList<JobData>();

	for (int i = 0; i < unitsOrganizationTableDetail.getJobsCount().intValue(); i++) {

	    JobData job = new JobData();

	    job.setName(unitsOrganizationTableDetail.getBasicJobName());
	    job.setRankId(unitsOrganizationTableDetail.getRankId());
	    job.setUnitId(unitsOrganizationTableDetail.getUnitId());
	    job.setUnitFullName(unitsOrganizationTableDetail.getUnitFullName());
	    job.setRegionId(unitsOrganizationTableDetail.getRegionId());
	    job.setGeneralType(unitsOrganizationTableDetail.getGeneralType());
	    job.setGeneralSpecialization(unitsOrganizationTableDetail.getGeneralSpecialization());
	    job.setMinorSpecializationId(unitsOrganizationTableDetail.getMinorSpecializationId());

	    job.setCategoryId(categoryId);
	    if (CategoriesEnum.SOLDIERS.getCode() == categoryId) {
		job.setApprovedFlag(FlagsEnum.OFF.getCode());
		job.setStatus(JobStatusEnum.FROZEN.getCode());
	    } else {
		job.setApprovedFlag(FlagsEnum.ON.getCode());
		job.setStatus(JobStatusEnum.VACANT.getCode());
	    }

	    job.setClassificationId(unitsOrganizationTableDetail.getClassificationId());

	    jobs.add(job);
	}
	return jobs;
    }

    /*------------------------------------------------------------- Reports  ------------------------------------------------------------*/
    public static byte[] getOrganizationTableReportsBytes(int reportType, long categoryClass, String selectedUnitHkeyPrefix,
	    int approvedFlag, String reportTitle, String decisionNumber, String decisionDateString) throws BusinessException {
	try {
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String reportName = "";
	    if (reportType == 10) {
		parameters.put("P_DECISION_NUMBER", decisionNumber);
		parameters.put("P_DECISION_DATE", decisionDateString);

		if (categoryClass == CategoriesEnum.MEDICAL_STAFF.getCode())
		    reportName = ReportNamesEnum.UNITS_MEDICAL_STAFF_ORG_TABLES_LAST_DECISION.getCode();
		else
		    reportName = ReportNamesEnum.UNITS_ORG_TABLES_LAST_DECISION.getCode();
	    } else if (reportType == 20) {
		if (categoryClass == CategoriesEnum.MEDICAL_STAFF.getCode())
		    reportName = ReportNamesEnum.UNITS_MEDICAL_STAFF_ORG_TABLES_OFFICIAL.getCode();
		else
		    reportName = ReportNamesEnum.UNITS_ORG_TABLES_OFFICIAL.getCode();
	    }

	    parameters.put("P_REPORT_TITLE", reportTitle);
	    parameters.put("P_UNIT_HKEY_PREFIX", selectedUnitHkeyPrefix);

	    parameters.put("P_CATEGORY_CLASS", categoryClass);
	    parameters.put("P_JOB_APPROVED_FLAG", approvedFlag);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}