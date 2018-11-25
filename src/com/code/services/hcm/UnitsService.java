package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.organization.units.UnitTransaction;
import com.code.dal.orm.hcm.organization.units.UnitTransactionData;
import com.code.dal.orm.hcm.organization.units.UnitType;
import com.code.dal.orm.workflow.WFPosition;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.ReportNamesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.RetirementsWorkFlow;

/**
 * The class <code>UnitsService</code> provides methods for handling the units operations such as create, rename, move, merge, separate, scale up, scale down, or cancel units.
 * 
 * These methods are calculating the HKey, validating the unit operation, manipulating the unit data, obtaining the unit types and unit transactions.
 * 
 */
public class UnitsService extends BaseService {

    /**
     * Constructs a newly allocated <code>UnitsService</code> object.
     */
    private UnitsService() {
    }

    /*---------------------------------------- Unit--------------------------------------------*/
    /**
     * Creates a new unit under a specific parent unit.
     * 
     * @param parent
     *            the {@link UnitData} object which represents the parent unit of the new unit
     * @param unitData
     *            the {@link UnitData} object which represents the new unit to be added
     * @param decisionDate
     *            the unit creation decision date in mm/MM/yyyy format
     * @param decisionNumber
     *            the unit creation decision number
     * @param userId
     *            the ID of the user who created the unit
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void addUnit(UnitData parent, UnitData unitData, Date decisionDate, String decisionNumber, Long userId, CustomSession... useSession) throws BusinessException {

	unitData.setParentUnitId(parent.getId());
	unitData.setUnitTypeCode(getUnitTypeById(unitData.getUnitTypeId()).getCode());
	unitData.setName(unitData.getName().trim());
	unitData.setFullName(parent.getFullName().concat("/".concat(unitData.getName())));
	unitData.setActiveFlag(FlagsEnum.ON.getCode());

	validateAddUnit(parent, unitData, decisionDate);

	List<UnitData> parentChildren = getUnitChildren(parent.getId(), false, false, FlagsEnum.ON.getCode());
	unitData.sethKey(getNextChildHkey(parent, null, parentChildren));

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(unitData.getUnit(), session);
	    unitData.setId(unitData.getUnit().getId());
	    addUnitTransaction(unitData, decisionDate, decisionNumber, TransactionTypesEnum.UNIT_CONSTRUCTION.getCode(), userId, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    unitData.setId(null);
	    throw e;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    unitData.setId(null);
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Changes the name of a specific unit.
     * 
     * @param unitData
     *            the {@link UnitData} object which represents the unit to be renamed
     * @param decisionDate
     *            the unit renaming decision {@link Date} in mm/MM/yyyy format
     * @param decisionNumber
     *            the unit renaming decision number
     * @param transactionTypeCode
     *            the transaction type code to indicate whether it was renaming unit transaction or merging units which caused renaming the unit
     * @param userId
     *            the ID of the user who renamed the unit
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void renameUnit(UnitData unitData, Date decisionDate, String decisionNumber, int transactionTypeCode, Long userId, CustomSession... useSession) throws BusinessException {
	validateOldHijriDate(decisionDate);
	String oldUnitName = unitData.getName();

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    unitData.setName(unitData.getNewName().trim());
	    List<UnitData> unitChildren = getUnitChildren(unitData.getId(), false, true, FlagsEnum.ON.getCode());

	    String oldFullName = unitData.getFullName();
	    if (oldFullName.contains("/")) {
		unitData.setFullName(oldFullName.substring(0, unitData.getFullName().lastIndexOf("/")) + "/" + unitData.getName());
	    } else {
		unitData.setFullName(unitData.getName());
	    }

	    modifyUnit(unitData, decisionDate, decisionNumber, transactionTypeCode, true, userId, session);

	    for (UnitData child : unitChildren) {
		child.setFullName(child.getFullName().replace(oldFullName, unitData.getFullName()));
		modifyUnit(child, decisionDate, decisionNumber, transactionTypeCode, false, userId, session);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    String newName = unitData.getName();
	    unitData.setName(oldUnitName);
	    unitData.setNewName(newName);
	    throw e;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    String newName = unitData.getName();
	    unitData.setName(oldUnitName);
	    unitData.setNewName(newName);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Cancels a specific unit and its children.
     * 
     * @param unitData
     *            the {@link UnitData} object which represents the unit to be cancelled
     * @param decisionNumber
     *            the unit cancellation decision number
     * @param decisionDate
     *            the unit cancellation decision date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who cancelled the unit
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void cancelUnit(UnitData unitData, String decisionNumber, Date decisionDate, Long userId, CustomSession... useSession) throws BusinessException {
	String hkey = unitData.gethKey();
	String prefixHKey = hkey.substring(0, getFirstChildIndex(hkey));

	List<UnitData> unitDataList = searchUnitsByPrefixHkey(prefixHKey, null);
	validateCancelUnit(unitDataList, decisionDate);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (UnitData unit : unitDataList) {
		unit.setAttachments(unitData.getAttachments());
		unit.setActiveFlag(FlagsEnum.OFF.getCode());
		unit.setPhysicalManagerId(null);
		unit.setOfficialManagerName(null);
		modifyUnit(unit, decisionDate, decisionNumber, TransactionTypesEnum.UNIT_CANCELLATION.getCode(), true, userId, session);
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

    /**
     * Moves unit under new parent with all its children and re-calculate HKEY and full name
     * 
     * @param unit
     *            the {@link UnitData} object which represents the unit to be moved
     * @param newParent
     *            the {@link UnitData} object which represents the new parent unit of the moved unit
     * @param updateHKey
     *            a flag indicates whether to re-calculate the HKey or not
     * @param decisionDate
     *            the unit moving decision date in mm/MM/yyyy format
     * @param decisionNumber
     *            the unit moving decision number
     * @param tranactionTypeCode
     *            the transaction type code to indicate whether the movement will happen due to merging, separation, or moving transactions
     * @param userId
     *            the ID of the user who moved the unit
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void moveUnit(UnitData unit, UnitData newParent, boolean updateHKey, Date decisionDate, String decisionNumber, int tranactionTypeCode, Long userId, CustomSession... useSession) throws BusinessException {
	validateOldHijriDate(decisionDate);
	String oldUnitParentName = unit.getParentUnitName();
	Long oldUnitParentId = unit.getParentUnitId();
	String oldUnitName = unit.getName();

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    // calculate new parent, hkey and full name
	    unit.setParentHKey(newParent.gethKey());
	    unit.setParentUnitId(newParent.getId());
	    unit.setParentUnitName(newParent.getName());
	    unit.setName(unit.getNewName().trim());
	    unit.setFullName(newParent.getFullName() + "/" + unit.getName());

	    int childKeyIndex;
	    if (updateHKey) {
		childKeyIndex = getFirstChildIndex(newParent.gethKey());
		if (childKeyIndex == newParent.gethKey().length())
		    throw new BusinessException("error_newParentWithMaxLevel");

		List<UnitData> parentChildren = getUnitChildren(newParent.getId(), false, false, FlagsEnum.ON.getCode());
		unit.sethKey(getNextChildHkey(newParent, null, parentChildren));
	    }
	    unit.setMoveToUnitId(newParent.getId());
	    modifyUnit(unit, decisionDate, decisionNumber, tranactionTypeCode, true, userId, session);

	    List<UnitData> children = getUnitChildren(unit.getId(), false, true, FlagsEnum.ON.getCode());

	    int index = 0;
	    long oldParentId = unit.getId();
	    Map<Long, UnitData> unitDataMap = new HashMap<Long, UnitData>();
	    unitDataMap.put(unit.getId(), unit);

	    for (UnitData child : children) {
		UnitData childParent = unitDataMap.get(child.getParentUnitId());
		if (childParent.getId().longValue() == oldParentId) {
		    // a new children for the same parent.
		    index++;
		} else {
		    // a new children for another parent.
		    index = 1;
		    oldParentId = childParent.getId();
		}
		child.setFullName(childParent.getFullName() + "/" + child.getName());

		childKeyIndex = getFirstChildIndex(childParent.gethKey());
		String hKey = childParent.gethKey().substring(0, childKeyIndex) + (index < 10 ? "0" + index : "" + index) + childParent.gethKey().substring(childKeyIndex + 2);
		child.sethKey(hKey);
		DataAccess.updateEntity(child.getUnit(), session);

		unitDataMap.put(child.getId(), child);
	    }

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    unit.setParentUnitName(oldUnitParentName);
	    unit.setParentUnitId(oldUnitParentId);
	    String newName = unit.getName();
	    unit.setName(oldUnitName);
	    unit.setNewName(newName);
	    throw e;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    unit.setParentUnitName(oldUnitParentName);
	    unit.setParentUnitId(oldUnitParentId);
	    String newName = unit.getName();
	    unit.setName(oldUnitName);
	    unit.setNewName(newName);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Separates two units.
     * 
     * @param separatedUnitData
     *            the {@link UnitData} object to be separated
     * @param unitDataList
     *            a list of {@link UnitData} containing the children of the unit to be separated
     * @param decisionNumber
     *            the unit separation decision number
     * @param decisionDate
     *            the unit separation decision date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who separated the unit
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void separateUnits(UnitData separatedUnitData, List<UnitData> unitDataList, String decisionNumber, Date decisionDate, Long userId, CustomSession... useSession) throws BusinessException {
	validateOldHijriDate(decisionDate);
	String unitOldName = separatedUnitData.getName();

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    // set the new name for the separated unit.
	    separatedUnitData.setName(separatedUnitData.getNewName().trim());
	    separatedUnitData.setFullName(separatedUnitData.getFullName().substring(0, separatedUnitData.getFullName().lastIndexOf("/")) + "/" + separatedUnitData.getName());
	    modifyUnit(separatedUnitData, decisionDate, decisionNumber, TransactionTypesEnum.UNIT_SEPARATION.getCode(), true, userId, session);

	    boolean atLeastOneUnitMoved = false;
	    Map<Long, String> lastGeneratedHKeyMap = new HashMap<Long, String>();
	    Map<Long, List<UnitData>> parentChildrenyMap = new HashMap<Long, List<UnitData>>();

	    for (UnitData child : unitDataList) {
		if ((child.getMoveToUnitId() == null) || (child.getMoveToUnitId().longValue() == child.getParentUnitId().longValue())) {
		    // The child of the separated unit will not move. Just
		    // rename it.
		    String oldChildFullName = child.getFullName();
		    child.setName(child.getName().trim());
		    child.setFullName(separatedUnitData.getFullName() + "/" + child.getName());
		    String newChildFullName = child.getFullName();

		    if ((oldChildFullName != null) && (!oldChildFullName.equals(newChildFullName))) {
			List<UnitData> unitChildren = getUnitChildren(child.getId(), false, true, FlagsEnum.ON.getCode());

			for (UnitData grandChild : unitChildren) {
			    grandChild.setFullName(grandChild.getFullName().replace(oldChildFullName, child.getFullName()));
			    DataAccess.updateEntity(grandChild.getUnit(), session);
			}
		    }
		    modifyUnit(child, decisionDate, decisionNumber, TransactionTypesEnum.UNIT_SEPARATION.getCode(), false, userId, session);
		} else {
		    atLeastOneUnitMoved = true;
		    UnitData parent = getUnitById(child.getMoveToUnitId());

		    List<UnitData> childrenList = null;

		    if (lastGeneratedHKeyMap.get(parent.getId()) == null) {
			// this is the first node that has been moved to this
			// parent during this separation transaction.
			childrenList = getUnitChildren(parent.getId(), false, false, FlagsEnum.ON.getCode());
			String nextHKey = getNextChildHkey(parent, null, childrenList);
			child.sethKey(nextHKey);
			lastGeneratedHKeyMap.put(parent.getId(), nextHKey);
			parentChildrenyMap.put(parent.getId(), childrenList);
		    } else {
			// at least move one child to this parent during this
			// separation transaction.
			String nextHKey = getNextChildHkey(parent, lastGeneratedHKeyMap.get(parent.getId()), parentChildrenyMap.get(parent.getId()));
			child.sethKey(nextHKey);
			lastGeneratedHKeyMap.remove(parent.getId());
			lastGeneratedHKeyMap.put(parent.getId(), nextHKey);
		    }
		    moveUnit(child, parent, false, decisionDate, decisionNumber, TransactionTypesEnum.UNIT_SEPARATION.getCode(), userId, session);
		}
	    }

	    if (!atLeastOneUnitMoved)
		throw new BusinessException("error_separationMinUnit");

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    String newName = separatedUnitData.getNewName();
	    separatedUnitData.setName(unitOldName);
	    separatedUnitData.setNewName(newName);
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    String newName = separatedUnitData.getNewName();
	    separatedUnitData.setName(unitOldName);
	    separatedUnitData.setNewName(newName);
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Merges one or more units with a specific unit.
     * 
     * @param unit
     *            the {@link UnitData} object which represents the unit to be merged
     * @param mergeWith
     *            the list of units to be merged with the specified unit
     * @param decisionNumber
     *            the unit merging decision number
     * @param decisionDate
     *            the unit merging decision date in mm/MM/yyyy format
     * @param userId
     *            the ID of the user who merged the unit
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void mergeUnit(UnitData unit, List<UnitData> mergeWith, String decisionNumber, Date decisionDate, Long userId, CustomSession... useSession) throws BusinessException {
	validateOldHijriDate(decisionDate);
	String oldUnitName = unit.getName();

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {

	    if (!isOpenedSession)
		session.beginTransaction();

	    Long[] mergedUnitsIds = new Long[mergeWith.size()];
	    for (int i = 0; i < mergeWith.size(); i++) {
		mergedUnitsIds[i] = mergeWith.get(i).getId();
	    }

	    // Integration points with jobs and employees services
	    JobsService.moveAllJobsFromUnitsToUnit(mergedUnitsIds, unit.getId(), unit.getFullName(), decisionNumber, decisionDate, userId, session);
	    EmployeesService.moveAllEmployeesFromUnitsToUnit(mergedUnitsIds, unit.getId(), HijriDateService.getHijriSysDate(), decisionNumber, decisionDate, session);

	    unit.setName(unit.getNewName().trim());
	    renameUnit(unit, decisionDate, decisionNumber, TransactionTypesEnum.UNIT_MERGE.getCode(), userId, session);

	    List<UnitData> mainParentChildren = null;
	    mainParentChildren = getUnitChildren(unit.getId(), false, false, FlagsEnum.ON.getCode());
	    String nextHKey = getNextChildHkey(unit, null, mainParentChildren);

	    for (UnitData mergedUnit : mergeWith) {

		// Can not merge two regions or two units with different regions
		if (unit.getRegionId().longValue() != mergedUnit.getRegionId().longValue()) {
		    throw new BusinessException("error_mergeTwoDifferentUnitRegion");
		}

		mergedUnit.setActiveFlag(FlagsEnum.OFF.getCode());
		mergedUnit.setOfficialManagerId(null);
		mergedUnit.setPhysicalManagerId(null);
		mergedUnit.setMergeWithUnitId(unit.getId());
		modifyUnit(mergedUnit, decisionDate, decisionNumber, TransactionTypesEnum.UNIT_MERGE.getCode(), true, userId, session);

		List<UnitData> mergedChildren = getUnitChildren(mergedUnit.getId(), false, false, FlagsEnum.ON.getCode());
		for (UnitData child : mergedChildren) {
		    child.sethKey(nextHKey);
		    nextHKey = getNextChildHkey(unit, nextHKey, mainParentChildren);
		    moveUnit(child, unit, false, decisionDate, decisionNumber, TransactionTypesEnum.UNIT_MERGE.getCode(), userId, session);
		}
	    }
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    String newUnitName = unit.getName();
	    unit.setName(oldUnitName);
	    unit.setNewName(newUnitName);
	    throw e;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    String newUnitName = unit.getName();
	    unit.setName(oldUnitName);
	    unit.setNewName(newUnitName);
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Scales a unit up or down.
     * 
     * @param transactionTypeCode
     *            the transaction type code to indicate whether the unit will be scaled up or down
     * @param modifiedUnitData
     *            the {@link UnitData} object which represents the unit to be scaled
     * @param decisionNumber
     *            the unit merging decision number
     * @param decisionDate
     *            the unit merging decision date in mm/MM/yyyy format
     * @param unitDataList
     *            a list of {@link UnitData} containing the children of the modified unit
     * @param userId
     *            the ID of the user who merged the unit
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void scaleUnit(int transactionTypeCode, UnitData modifiedUnitData, String decisionNumber, Date decisionDate, List<UnitData> unitDataList, Long userId, CustomSession... useSession) throws BusinessException {
	validateOldHijriDate(decisionDate);
	String oldUnitName = modifiedUnitData.getName();

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    // Can not scale region unit.
	    if (modifiedUnitData.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode())
		throw new BusinessException("error_regionsDonNotScale");

	    // check if scale up then higher level;
	    // if scale down lower level;
	    // otherwise error
	    UnitData parent = getUnitById(modifiedUnitData.getParentUnitId());
	    UnitType newType = getUnitTypeById(modifiedUnitData.getNewUnitTypeId());
	    if (transactionTypeCode == TransactionTypesEnum.UNIT_SCALE_UP.getCode()) {
		if (newType.getCode().intValue() < modifiedUnitData.getUnitTypeCode().intValue())
		    throw new BusinessException("error_invalidScaleUnitType");
	    } else {
		if (newType.getCode().intValue() > modifiedUnitData.getUnitTypeCode().intValue())
		    throw new BusinessException("error_invalidScaleUnitType");
	    }

	    // change only in unit type for transaction types scale up & down
	    // check new type compatibility with parent
	    if (parent.getChildrenUnitTypesCodes().contains(newType.getCode().toString())) {
		modifiedUnitData.setName(modifiedUnitData.getNewName().trim());
		modifiedUnitData.setUnitTypeId(modifiedUnitData.getNewUnitTypeId());
		modifiedUnitData.setFullName(parent.getFullName() + "/" + modifiedUnitData.getName());

		modifyUnit(modifiedUnitData, decisionDate, decisionNumber, transactionTypeCode, true, userId, session);

		String firstLevelChildrenTypeCodes = newType.getChildrenUnitTypeCodes();
		if (firstLevelChildrenTypeCodes == null && unitDataList.size() > 0) {
		    // the new unit type couldn't have a children units
		    String newName = modifiedUnitData.getNewName();
		    modifiedUnitData.setName(oldUnitName);
		    modifiedUnitData.setNewName(newName);
		    throw new BusinessException("error_invlaidLeafChildren");
		}

		// check children validations
		for (UnitData unitData : unitDataList) {
		    String oldFullName = unitData.getFullName();
		    unitData.setName(unitData.getName().trim());
		    unitData.setFullName(modifiedUnitData.getFullName() + "/" + unitData.getName());

		    if (unitData.getNewUnitTypeId().longValue() == unitData.getUnitTypeId().longValue()) {
			// same type for child
			if (firstLevelChildrenTypeCodes.contains(unitData.getUnitTypeCode().toString())) {
			    // change full name for all children
			    List<UnitData> unitChildren = getUnitChildren(unitData.getId(), false, true, FlagsEnum.ON.getCode());
			    for (UnitData child : unitChildren) {
				child.setFullName(child.getFullName().replace(oldFullName, unitData.getFullName()));
				DataAccess.updateEntity(child.getUnit(), session);
			    }
			} else {
			    String newName = modifiedUnitData.getNewName();
			    modifiedUnitData.setName(oldUnitName);
			    modifiedUnitData.setNewName(newName);
			    throw new BusinessException("error_invlaidChildtype", new Object[] { unitData.getUnitTypeDesc(), newType.getDescription() });
			}
			modifyUnit(unitData, decisionDate, decisionNumber, transactionTypeCode, false, userId, session);
		    } else { // new type for child
			if (unitData.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode()) {
			    throw new BusinessException("error_regionsDonNotScale");
			}
			unitData.setUnitTypeId(unitData.getNewUnitTypeId());

			UnitType childNewType = getUnitTypeById(unitData.getNewUnitTypeId());
			if (firstLevelChildrenTypeCodes.contains(childNewType.getCode().toString())) {

			    String secondLevelchildrenTypesCodes = childNewType.getChildrenUnitTypeCodes();
			    List<UnitData> allLevelchildren = getUnitChildren(unitData.getId(), false, true, FlagsEnum.ON.getCode());
			    for (UnitData secondLevelChild : allLevelchildren) {
				if (secondLevelChild.getParentUnitId().longValue() == unitData.getId().longValue() && !secondLevelchildrenTypesCodes.contains(secondLevelChild.getUnitTypeCode().toString())) {
				    String newName = modifiedUnitData.getNewName();
				    modifiedUnitData.setName(oldUnitName);
				    modifiedUnitData.setNewName(newName);
				    throw new BusinessException("error_invlaidChildtype", new Object[] { secondLevelChild.getUnitTypeDesc(), childNewType.getDescription() });
				}
			    }
			    // change full name for all children
			    for (UnitData child : allLevelchildren) {
				child.setFullName(child.getFullName().replace(oldFullName, unitData.getFullName()));
				modifyUnit(child, decisionDate, decisionNumber, transactionTypeCode, false, userId, session);
			    }
			    modifyUnit(unitData, decisionDate, decisionNumber, transactionTypeCode, true, userId, session);
			} else {
			    String newName = modifiedUnitData.getNewName();
			    modifiedUnitData.setName(oldUnitName);
			    modifiedUnitData.setNewName(newName);
			    throw new BusinessException("error_invlaidChildtype", new Object[] { childNewType.getDescription(), newType.getDescription() });
			}
		    }
		}
	    } else {
		String newName = modifiedUnitData.getNewName();
		modifiedUnitData.setName(oldUnitName);
		modifiedUnitData.setNewName(newName);
		throw new BusinessException("error_invlaidChildtype", new Object[] { newType.getDescription(), parent.getUnitTypeDesc() });
	    }
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    String newName = modifiedUnitData.getNewName();
	    modifiedUnitData.setName(oldUnitName);
	    modifiedUnitData.setNewName(newName);
	    throw e;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    String newName = modifiedUnitData.getNewName();
	    modifiedUnitData.setName(oldUnitName);
	    modifiedUnitData.setNewName(newName);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Changes the managers of a given list of units.
     * 
     * @param units
     *            the collection of {@link UnitData}
     * @param changeOfficialManagerFlag
     *            a flag indicates whether to change the official manager of the units or not
     * @param session
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    public static void modifyUnitsManagers(Collection<UnitData> units, boolean changeOfficialManagerFlag, CustomSession session) throws BusinessException {
	try {
	    Map<Long, Long> officialManagers = new HashMap<Long, Long>();
	    if (changeOfficialManagerFlag) {
		for (UnitData unitData : units) {
		    officialManagers.put(unitData.getId(), unitData.getOfficialManagerId());
		    unitData.setOfficialManagerId(null);
		    DataAccess.updateEntity(unitData.getUnit(), session);
		}
		session.flushTransaction();
		for (UnitData unitData : units) {
		    unitData.setOfficialManagerId(officialManagers.get(unitData.getId()));
		    DataAccess.updateEntity(unitData.getUnit(), session);
		}
	    } else {
		for (UnitData unitData : units) {
		    DataAccess.updateEntity(unitData.getUnit(), session);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Modifies a specific unit to update the order under parent, the transaction required flag, the remarks, and the attachments flags.
     * 
     * @param unitData
     *            the {@link UnitData} object which represents the unit to be modified
     * @throws BusinessException
     *             if any error occurs
     */
    public static void modifyUnitNonTransactionalData(UnitData unitData) throws BusinessException {
	modifyUnit(unitData, null, null, FlagsEnum.ALL.getCode(), false, null);
    }

    /**
     * Modifies a specific unit with a given transaction type such as rename, move, merge, separate, scale, cancel or set attachment for this unit.
     * 
     * @param unitData
     *            the {@link UnitData} object which represents the unit to be modified
     * @param decisionDate
     *            the unit merging decision date in mm/MM/yyyy format
     * @param decisionNumber
     *            the unit merging decision number
     * @param transactionTypeCode
     *            create, rename, move, merge, separate, scale up, scale down, or cancel
     * @param insertTransaction
     *            a flag indicates whether to add unit transaction or not
     * @param userId
     *            the ID of the user who modified the unit
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    private static void modifyUnit(UnitData unitData, Date decisionDate, String decisionNumber, int transactionTypeCode, boolean insertTransaction, Long userId, CustomSession... useSession) throws BusinessException {
	if (transactionTypeCode != FlagsEnum.ALL.getCode()) {
	    UnitData oldUnitData = getUnitById(unitData.getId());
	    // if (!oldUnitData.getCode().equals(unitData.getCode()) && getUnitByExactCode(unitData.getCode()) != null)
	    // throw new BusinessException("error_unitUniqueName");

	    if (!oldUnitData.getFullName().equals(unitData.getFullName()) && getUnitByExactFullName(unitData.getFullName()) != null)
		throw new BusinessException("error_unitUniqueName");
	}

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(unitData.getUnit(), session);
	    if (insertTransaction) {
		addUnitTransaction(unitData, decisionDate, decisionNumber, transactionTypeCode, userId, session);
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

    /**
     * Gets the HKey prefix of a given HKey which starts from index zero of the HKey to the index before the zeros which represents the first child index.
     * 
     * @param key
     *            the HKey string to get its prefix
     * @return the HKey prefix string
     */
    public static String getHKeyPrefix(String key) {
	if (key == null)
	    return null;
	return key.substring(0, getFirstChildIndex(key));
    }

    /**
     * Gets Next Child HKey based on parameter children list after lastChildHKey if sent; otherwise start from 1 based on parameter children list.
     * 
     * @param parent
     *            the {@link UnitData} object which represents the unit to get its children
     * @param lastChildHKey
     *            last generated child to get next child after it; otherwise null to start from 1
     * @param children
     *            list of children of type {@link UnitData}
     * @return the HKey of the next child
     * @throws BusinessException
     *             if there are more than 99 units at the same level
     */
    private static String getNextChildHkey(UnitData parent, String lastChildHKey, List<UnitData> children) throws BusinessException {
	int startIndex = 1;
	if (lastChildHKey != null) {
	    int lastZeroIndex = getFirstChildIndex(lastChildHKey);
	    startIndex = Integer.valueOf(lastChildHKey.substring(lastZeroIndex - 2, lastZeroIndex)) + 1;
	}

	Map<String, String> map = new HashMap<String, String>();
	for (UnitData i : children) {
	    map.put(i.gethKey(), i.gethKey());
	}

	int childKeyIndex = getFirstChildIndex(parent.gethKey());
	for (int i = startIndex; i < 100; i++) {
	    String hKey = parent.gethKey().substring(0, childKeyIndex) + (i < 10 ? "0" + i : "" + i) + (childKeyIndex == parent.gethKey().length() - 2 ? "" : parent.gethKey().substring(childKeyIndex + 2));

	    if (map.get(hKey) == null) {
		return hKey;
	    }
	}
	throw new BusinessException("error_hKeyValidation");
    }

    /**
     * Returns first index of "00" string at even position; otherwise returns the string length.
     * 
     * @param key
     *            the HKey of the unit to get its first child
     * @return the index of the first child of a certain unit
     */
    private static int getFirstChildIndex(String key) {
	int childKeyIndex = 0;
	do {
	    childKeyIndex = key.indexOf("00", childKeyIndex);
	} while (childKeyIndex != -1 && (childKeyIndex++ % 2) != 0);

	if (childKeyIndex == -1) {
	    // full hkey to 10 levels
	    childKeyIndex = key.length();
	} else {
	    childKeyIndex--;
	}

	return childKeyIndex;
    }

    /**
     * Checks for the following rules to be applied before adding a new unit:
     * <ul>
     * <li>The new unit has unique code and name</li>
     * <li>The new unit level is not greater than 10</li>
     * <li>The units of type region are not duplicated</li>
     * <li>Unit code must be 9 numbers (1 + 2 for regionCode + 6 for serial)</li>
     * <li>The region of the unit is the same with the parent unit</li>
     * </ul>
     * 
     * @param parent
     *            the {@link UnitData} object which represents the parent unit of the new unit
     * @param newUnit
     *            the {@link UnitData} object which represents the new unit to be added
     * @param decisionDate
     *            the unit creation decision date which should be today or before, in mm/MM/yyyy format
     * @throws BusinessException
     *             in any validation error occurs
     */
    private static void validateAddUnit(UnitData parent, UnitData newUnit, Date decisionDate) throws BusinessException {
	try {
	    // Decision Date must be today or before
	    validateOldHijriDate(decisionDate);

	    // Unique Code.
	    // if (getUnitByExactCode(newUnit.getCode()) != null)
	    // throw new BusinessException("error_unitUnique");

	    // Unique Full Name.
	    if (getUnitByExactFullName(newUnit.getFullName()) != null)
		throw new BusinessException("error_unitUnique");

	    // max levels is 10
	    if (Integer.valueOf(parent.gethKey().substring(parent.gethKey().length() - 2)).intValue() != 0) {
		throw new BusinessException("error_maxLevelViolation");
	    }

	    // units of type region must not be duplicated.
	    if (newUnit.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode()) {
		List<UnitData> regionUnits = getUnitsByType(newUnit.getUnitTypeId());
		long newUnitRegionId = newUnit.getRegionId().longValue();
		for (UnitData region : regionUnits) {
		    if (region.getRegionId().longValue() == newUnitRegionId) {
			throw new BusinessException("error_existedRegion");
		    }
		}
	    }

	    // unit code must be 9 numbers (1 + 2 for regionCode + 6 for serial)
	    // int start = Integer.valueOf(newUnit.getCode().substring(0, 1));
	    // if (start != 1 || newUnit.getCode().length() != 9) {
	    // throw new BusinessException("error_unitInvalidCode");
	    // }
	    //
	    // if (!newUnit.getCode().substring(1, 3).equals(newUnit.getRegionCode())) {
	    // throw new BusinessException("error_unitCodeRegionConflict");
	    // }

	    StringTokenizer childToken = new StringTokenizer(parent.getChildrenUnitTypesCodes(), ",");
	    String newUnitTypeCode = newUnit.getUnitTypeCode().toString();
	    boolean found = false;
	    while (childToken.hasMoreTokens()) {
		if (childToken.nextToken().equals(newUnitTypeCode)) {
		    found = true;
		    break;
		}
	    }
	    if (!found) {
		throw new BusinessException("error_unitTypeValidation");
	    }

	    // check different regions
	    if ((newUnit.getUnitTypeCode().intValue() != UnitTypesEnum.REGION_COMMANDER.getCode()) && (newUnit.getRegionId().longValue() != parent.getRegionId().longValue())) {
		throw new BusinessException("error_unitCreationDifferentRegion");
	    }
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Makes sure there no jobs nor employees under units to be canceled.
     * 
     * @param unitDataList
     *            the list of {@link UnitData} to be cancelled
     * @param decisionDate
     *            the unit cancellation decision date which should be today or before, in mm/MM/yyyy format
     * @throws BusinessException
     *             if there is any job or employee under the unit to be cancelled
     */
    private static void validateCancelUnit(List<UnitData> unitDataList, Date decisionDate) throws BusinessException {
	validateOldHijriDate(decisionDate);

	Long[] unitsIdsArray = new Long[unitDataList.size()];
	for (int i = 0; i < unitDataList.size(); i++) {
	    unitsIdsArray[i] = unitDataList.get(i).getId();
	}

	long jobscount = JobsService.getJobsCountByUnitsIdsAndCategoriesIds(unitsIdsArray, null);
	if (jobscount > 0) {
	    throw new BusinessException("error_cancelUnitJobsError", new String[] { unitDataList.get(0).getFullName() });
	}

	long empCount = EmployeesService.countEmployeesByUnitsIds(unitsIdsArray);
	if (empCount > 0) {
	    throw new BusinessException("error_cancelUnitEmpsError", new String[] { unitDataList.get(0).getFullName() });
	}
    }

    /**
     * Gets the physical manager id of a specific unit.
     * 
     * @param unitDataId
     *            the ID of the unit to get its physical manager
     * @return the ID of the physical manager
     * @throws NoDataException
     *             if there is no unit with this ID
     * @throws BusinessException
     *             if any error occurs
     */
    public static Long getUnitPhysicalManagerId(long unitDataId) throws BusinessException {
	return getUnitById(unitDataId).getPhysicalManagerId();
    }

    /**
     * Gets a unit with a specific ID. </br>
     * wrapper for {@link #searchUnits(long, String, long, long, long, String, int)}
     * 
     * @param unitDataId
     *            the ID of the unit to get from the database
     * @return the {@link UnitData} object with this ID
     * @throws BusinessException
     *             if any error occurs
     */
    public static UnitData getUnitById(long unitDataId) throws BusinessException {
	List<UnitData> unitDataList = searchUnits(unitDataId, null, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, FlagsEnum.ON.getCode());
	if (unitDataList.size() > 0)
	    return unitDataList.get(0);
	else
	    return null;
    }

    /**
     * Gets all units with a specific type. </br>
     * wrapper for {@link #searchUnits(long, String, long, long, long, String, int)}
     * 
     * @param unitTypeId
     *            the type of the unit to get from the database
     * @return the list of {@link UnitData} with this type
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<UnitData> getUnitsByType(long unitTypeId) throws BusinessException {
	return searchUnits(FlagsEnum.ALL.getCode(), null, null, unitTypeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, FlagsEnum.ON.getCode());
    }

    /**
     * Gets all units with a specific type and a specific region. </br>
     * wrapper for {@link #searchUnits(long, String, long, long, long, String, int)}
     * 
     * @param unitTypeId
     *            the type of the unit to get from the database
     * @param regionId
     *            the region to get from the database
     * @return the list of {@link UnitData} with this type
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<UnitData> getUnitsByTypeAndRegion(long unitTypeId, long regionId) throws BusinessException {
	return searchUnits(FlagsEnum.ALL.getCode(), null, null, unitTypeId, FlagsEnum.ALL.getCode(), regionId, null, FlagsEnum.ON.getCode());
    }

    /**
     * Gets all units with a specific region and name. </br>
     * wrapper for {@link #searchUnits(long, String, long, long, long, String, int)}
     * 
     * @param regionId
     *            the region of the unit
     * @param unitFullName
     *            the full name of the unit
     * @return the list of {@link UnitData} with this region and name
     * @throws BusinessException
     *             if any error occurs
     */
    // TODO to be rename to getUnitsByRegionAndName
    public static List<UnitData> getUnitsByRegion(long regionId, String unitFullName) throws BusinessException {
	return searchUnits(FlagsEnum.ALL.getCode(), null, unitFullName, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), regionId, null, FlagsEnum.ON.getCode());
    }

    /**
     * Gets all units which are under parentId, with type equals unitTypeId, and in region equals regionId. </br>
     * wrapper for {@link #searchUnits(long, String, long, long, long, String, int)}
     * 
     * @param unitFullName
     *            the full name of the unit
     * @param unitTypeId
     *            the type of the unit
     * @param parentId
     *            the parent of the unit
     * @param regionId
     *            the region of the unit
     * @return the list of {@link UnitData} with this name, type, parent, and region
     * @throws BusinessException
     *             if any error occurs
     */
    // TODO to be rename to getUnitsByParentAndTypeAndName
    public static List<UnitData> getUnitsByParentByTypeByName(String unitFullName, long unitTypeId, long parentId, long regionId) throws BusinessException {
	return searchUnits(FlagsEnum.ALL.getCode(), null, unitFullName, unitTypeId, parentId, regionId, null, FlagsEnum.ON.getCode());
    }

    // /**
    // * Gets a unit with a specific code. </br> wrapper for {@link #searchUnits(long, String, long, long, long, String, int)}
    // *
    // * @param exactCode
    // * the code of the unit to get from the database
    // * @return the {@link UnitData} object with this code
    // * @throws BusinessException
    // * if any error occurs
    // */
    // private static UnitData getUnitByExactCode(String exactCode) throws BusinessException {
    // List<UnitData> unitDataList = searchUnits(FlagsEnum.ALL.getCode(), null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), exactCode, FlagsEnum.ALL.getCode(), FlagsEnum.OFF.getCode());
    // if (unitDataList.size() > 0)
    // return unitDataList.get(0);
    // else
    // return null;
    // }

    /**
     * Gets all units which matches a specific search criteria.
     * 
     * @param unitDataId
     *            the ID of the unit
     * @param unitName
     *            the name of the unit
     * @param unitFullName
     *            the full name of the unit
     * @param unitTypeId
     *            the type of the unit
     * @param parentId
     *            the parent ID of the unit
     * @param regionId
     *            the region ID of the unit
     * @param exactCode
     *            the code of the unit
     * @param activeFlag
     *            (1) means to include only active; (0) means to include not active; (-1) means to include all
     * @return the list of {@link UnitData} which matches the search criteria
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<UnitData> searchUnits(long unitDataId, String unitName, String unitFullName, long unitTypeId, long parentId, long regionId, String exactCode, int activeFlag) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNIT_ID", unitDataId);
	    qParams.put("P_UNIT_NAME", (unitName == null || unitName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + unitName + "%");
	    qParams.put("P_UNIT_FULL_NAME", (unitFullName == null || unitFullName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + unitFullName + "%");
	    qParams.put("P_UNIT_TYPE_ID", unitTypeId);
	    qParams.put("P_PARENT_ID", parentId);
	    qParams.put("P_REGION_ID", regionId);
	    // qParams.put("P_CODE", (exactCode == null || exactCode.equals("")) ? FlagsEnum.ALL.getCode() + "" : exactCode);
	    qParams.put("P_ACTIVE_FLAG", activeFlag);
	    return DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_GET_UNIT_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets all children of specified unit include the unit itself or not, including all levels of children or not and (active only or not active or all)
     * 
     * @param unitId
     *            the ID of the unit to get its children
     * @param includeParent
     *            a flag indicates whether the unit itself will be included within the list or not
     * @param getAllLevels
     *            a flag indicates whether all levels will be included of children or not
     * @param activeFlag
     *            (1) means to include only active; (0) means to include not active; (-1) means to include all
     * @return list of the unit children of type {@link UnitData}
     * @throws BusinessException
     */
    public static List<UnitData> getUnitChildren(long unitId, boolean includeParent, boolean getAllLevels, int activeFlag) throws BusinessException {
	List<UnitData> unitDataList = new ArrayList<UnitData>();
	try {
	    UnitData parent = null;
	    if (includeParent) {
		parent = getUnitById(unitId);
		unitDataList.add(parent);
	    }

	    if (getAllLevels) {
		if (parent == null)
		    parent = getUnitById(unitId);

		String hkey = parent.gethKey();
		String prefixHKey = hkey.substring(0, getFirstChildIndex(hkey));
		unitDataList.addAll(searchUnitsByPrefixHkey(prefixHKey, null));
		unitDataList.remove(0);
	    } else {
		Map<String, Object> qParams = new HashMap<String, Object>();
		qParams.put("P_PARENT_UNIT_ID", unitId);
		qParams.put("P_ACTIVE_FLAG", activeFlag);
		unitDataList.addAll(DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_GET_UNIT_DATA_CHILDREN.getCode(), qParams));
	    }
	} catch (DatabaseException e) {
	    throw new BusinessException("error_general");
	}
	return unitDataList;
    }

    /**
     * Gets all children of unit at all levels including unit itself, used in minisearch mode 4. </br>
     * wrapper for {@link #searchUnitsByPrefixHkey(String, String)}
     * 
     * @param parentId
     *            the ID of the unit to get its children
     * @param unitFullName
     *            the full name of the unit
     * @return the list of {@link UnitData} contains all children of this unit
     * @throws BusinessException
     *             if any error occurs
     */
    // TODO children should be without "s" at the end
    public static List<UnitData> getAllUnitLevelsChildrensByName(long parentId, String unitFullName) throws BusinessException {
	UnitData parent = getUnitById(parentId);
	return searchUnitsByPrefixHkey(getHKeyPrefix(parent.gethKey()), unitFullName);
    }

    /**
     * Gets all children of unit at all levels including unit itself. </br>
     * wrapper for {@link #searchUnitsByPrefixHkey(String, String)}
     * 
     * @param prefixUnitHKey
     *            the HKey prefix of the unit to get its children
     * @param unitFullName
     *            the full name of the unit
     * @return the list of {@link UnitData} contains all children of this unit
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<UnitData> getUnitsByPrefixHkey(String prefixUnitHKey, String unitFullName) throws BusinessException {
	return searchUnitsByPrefixHkey(prefixUnitHKey, unitFullName);
    }

    /**
     * Gets all children of unit at all levels including unit itself.
     * 
     * @param prefixUnitHKey
     *            the HKey prefix of the unit to get its children
     * @param unitFullName
     *            the full name of the unit
     * @return the list of {@link UnitData} contains all children of this unit
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<UnitData> searchUnitsByPrefixHkey(String prefixUnitHKey, String unitFullName) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PREFIX_HKEY", prefixUnitHKey + "%");
	    qParams.put("P_UNIT_FULL_NAME", (unitFullName == null || unitFullName.isEmpty()) ? (FlagsEnum.ALL.getCode() + "") : ("%" + unitFullName + "%"));
	    return DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_GET_ALL_LEVELS_CHILD_BY_HKEY.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets all children identifiers for specified unit in case of those children are parents. </br>
     * wrapper for {@link #searchUnitParentChildren(long)}
     * 
     * @param unitDataId
     *            the ID of the unit to get its children IDs
     * @return the list of unit children identifiers
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<Long> getUnitParentChildrenIds(long unitDataId) throws BusinessException {
	return searchUnitParentChildren(unitDataId);
    }

    /**
     * Gets all children identifiers for specified unit in case of those children are parents.
     * 
     * @param unitDataId
     *            the ID of the unit to get its children IDs
     * @return the list of unit children identifiers
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<Long> searchUnitParentChildren(long unitDataId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNIT_ID", unitDataId);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_GET_UNIT_DATA_PARENT_CHILDREN.getCode(), qParams);
	} catch (DatabaseException e) {
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets all the units at all levels with a specific name.
     * 
     * @param unitName
     *            the name of the unit to search with
     * @return the list of {@link UnitData} which their name matches the input name
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<UnitData> getAllLevelUnitsByName(String unitName) throws BusinessException {
	List<UnitData> matchUnitList = searchUnits(FlagsEnum.ALL.getCode(), unitName, null, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), null, FlagsEnum.ON.getCode());
	if (matchUnitList.size() == 0)
	    return matchUnitList;

	Map<String, String> hKeyMap = new HashMap<String, String>();
	for (UnitData unitData : matchUnitList) {
	    if (hKeyMap.get(unitData.gethKey()) == null) {
		// add unit hKey to the hKeysMap.
		hKeyMap.put(unitData.gethKey(), unitData.gethKey());
	    }

	    int index = getFirstChildIndex(unitData.gethKey());

	    String parentHKey = unitData.gethKey();
	    while (index != 2) {
		// add the hKeys for the parent hierarchy of the matched unit.
		parentHKey = parentHKey.substring(0, index - 2) + "00" + (index == parentHKey.length() ? "" : parentHKey.substring(index));
		if (hKeyMap.get(parentHKey) == null) {
		    hKeyMap.put(parentHKey, parentHKey);
		}
		index -= 2;
	    }
	}

	List<String> hKeyList = new ArrayList<String>();
	for (Map.Entry<String, String> entry : hKeyMap.entrySet()) {
	    hKeyList.add(entry.getValue());
	}

	return searchUnitsByHKey(hKeyList);
    }

    /**
     * Gets a unit with a given HKey.
     * 
     * @param exactHKey
     *            the hKey to get the unit with this key
     * @return the unit which its HKey is the given key
     * @throws BusinessException
     *             if any error occurs
     */
    public static UnitData getUnitByExactHKey(String exactHKey) throws BusinessException {
	List<String> hKeyList = new ArrayList<String>();
	hKeyList.add(exactHKey);
	List<UnitData> unitDataList = searchUnitsByHKey(hKeyList);
	if (unitDataList.size() > 0)
	    return unitDataList.get(0);
	else
	    return null;
    }

    public static List<UnitData> getAncestorsUnitsByHKey(String unitHKey) throws BusinessException {
	return getUnitsByHKeys(getAncestorsHkeys(unitHKey, 1));
    }

    public static List<UnitData> getAncestorsUnitsByHKey(String unitHKey, int level) throws BusinessException {
	return getUnitsByHKeys(getAncestorsHkeys(unitHKey, level));
    }

    /**
     * Gets all the units have any of the sent Hkey. </br>
     * wrapper for {@link #searchUnitsByHKey(List)}
     * 
     * @param hKeyList
     *            the list of the HKeys to get the unit with these keys
     * @return the list of {@link UnitData} which their HKey matches the input HKey list
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<UnitData> getUnitsByHKeys(List<String> hKeyList) throws BusinessException {
	return searchUnitsByHKey(hKeyList);
    }

    /**
     * Gets all the units by their HKey.
     * 
     * @param hKeyList
     *            the list of the HKeys to get the unit with these keys
     * @return the list of {@link UnitData} which their HKey matches the input HKey list
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<UnitData> searchUnitsByHKey(List<String> hKeyList) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_HKEY_LIST", hKeyList.toArray());
	    return DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_GET_UNIT_DATA_BY_HKEY.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets all units which are in a specified region and contain sent unitTypeCode in its children type codes. </br>
     * wrapper for {@link #searchUnitsByCompatibleChildType(int, String, long)}
     * 
     * @param childTypeCode
     *            specified code type of child that must exist under any parent of returned list
     * @param unitFullName
     *            the full name of the unit
     * @param regionId
     *            filter all returned list on specified region only
     * @return the list of {@link UnitData} in this region and the children with this type
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<UnitData> getUnitsByCompatibleChildType(int childTypeCode, String unitFullName, long regionId) throws BusinessException {
	return searchUnitsByCompatibleChildType(childTypeCode, unitFullName, regionId);
    }

    /**
     * Gets all units which are in a specified region and contain sent unitTypeCode in its children type codes.
     * 
     * @param childTypeCode
     *            specified code type of child that must exist under any parent of returned list
     * @param unitFullName
     *            the full name of the unit
     * @param regionId
     *            filter all returned list on specified region only
     * @return the list of {@link UnitData} which are in a specified region and contain sent unitTypeCode in its children type codes.
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<UnitData> searchUnitsByCompatibleChildType(int childTypeCode, String unitFullName, long regionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_CHILD_TYPE_CODE", "%" + childTypeCode + "%");
	    qParams.put("P_REGION_ID", regionId);
	    qParams.put("P_UNIT_FULL_NAME", (unitFullName == null || unitFullName.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + unitFullName + "%");
	    return DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_GET_UNIT_DATA_BY_CHILD_TYPE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets a unit with a specific full name. </br>
     * wrapper for {@link #searchUnitsByExactFullName(String)}
     * 
     * @param exactFullName
     *            the full name of the unit to get from the database
     * @return a {@link UnitData} which its name is the input full name
     * @throws BusinessException
     *             if any error occurs
     */
    public static UnitData getUnitByExactFullName(String exactFullName) throws BusinessException {
	List<UnitData> unitDataList = searchUnitsByExactFullName(exactFullName);
	if (unitDataList.size() > 0)
	    return unitDataList.get(0);
	else
	    return null;
    }

    /**
     * Gets all units with a specific full name.
     * 
     * @param exactFullName
     *            the full name of the unit to get from the database
     * @return a list of {@link UnitData} which their name is the input full name
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<UnitData> searchUnitsByExactFullName(String exactFullName) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_FULL_NAME", (exactFullName == null || exactFullName.equals("")) ? FlagsEnum.ALL.getCode() + "" : exactFullName);
	    return DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_GET_UNIT_DATA_BY_FULL_NAME.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the maximum order under specific unit.
     * 
     * @param unitId
     *            the unit ID to get the maximum order under it
     * @return the maximum order under the input unit
     * @throws BusinessException
     *             if any error occurs
     */
    public static Integer getMaximumOrderUnderParent(long unitId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PARENT_UNIT_ID", unitId);
	    List<Integer> result = DataAccess.executeNamedQuery(Integer.class, QueryNamesEnum.HCM_GET_MAX_ORDER_UNDER_PARENT.getCode(), qParams);
	    if (result.isEmpty() || result.get(0) == null)
		return 0;
	    else
		return result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets all units with type region or sector with a specific name. </br>
     * wrapper for {@link #getUnitsByTypeAndName(Long[], String)}
     * 
     * @param unitFullName
     *            the full name of the unit
     * @return a list of {@link UnitData} which their name is the input name and with region and sector types
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<UnitData> getRegionsAndSectorsByName(String unitFullName) throws BusinessException {
	Long[] sectorsAndRegionsTypes = new Long[] { (long) UnitTypesEnum.PRESIDENCY.getCode(), (long) UnitTypesEnum.REGION_COMMANDER.getCode(), (long) UnitTypesEnum.SECTOR_COMMANDER.getCode() };
	return getUnitsByTypeAndName(sectorsAndRegionsTypes, unitFullName);
    }

    /**
     * Gets all units with type training center or institute with a specific name. </br>
     * wrapper for {@link #getUnitsByTypeAndName(Long[], String)}
     * 
     * @param unitFullName
     *            the full name of the unit
     * @return a list of {@link UnitData} which their name is the input name and with training center and institute types
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<UnitData> getTrainingCentersAndInstitutesByName(String unitFullName) throws BusinessException {

	Long[] trainingCentersAndInstitutesTypesIds = new Long[] { (long) UnitTypesEnum.TRAINING_CENTER.getCode(), (long) UnitTypesEnum.INSTITUTE.getCode() };
	Long academyTypeId = (long) UnitTypesEnum.REGION_COMMANDER.getCode();
	Long academyRegionId = RegionsEnum.ACADEMY.getCode();

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TYPE_IDS", trainingCentersAndInstitutesTypesIds);
	    qParams.put("P_ACADEMY_TYPE_ID", academyTypeId);
	    qParams.put("P_ACADEMY_REGION_ID", academyRegionId);
	    qParams.put("P_UNIT_FULL_NAME", (unitFullName == null || unitFullName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : "%" + unitFullName + "%");
	    return DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_GET_TRAINING_CENTERS_AND_INSTITUTES_AND_NAME.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets all units with a specific name and types.
     * 
     * @param typesIds
     *            the list of units types
     * @param unitFullName
     *            the full name of the unit
     * @return a list of {@link UnitData} which their name is the input name and their type is one of the input list of types
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<UnitData> getUnitsByTypeAndName(Long[] typesIds, String unitFullName) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_TYPE_IDS", typesIds);
	    qParams.put("P_UNIT_FULL_NAME", (unitFullName == null || unitFullName.length() == 0) ? FlagsEnum.ALL.getCode() + "" : "%" + unitFullName + "%");
	    return DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_GET_UNITS_BY_TYPE_AND_NAME.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets a unit under the presidency.
     * 
     * @param unitId
     *            the unit ID
     * @param level
     *            level from the presidency
     * @return the {@link UnitData} under the presidency
     * @throws BusinessException
     *             if any error occurs
     */
    // TODO more details about the unit ID usage?
    public static UnitData getAncestorUnderPresidencyByLevel(long unitId, int level) throws BusinessException {
	try {
	    int endIndex = level * 2;
	    String zerosSuffix = "00000000000000000000";

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNIT_ID", unitId);
	    qParams.put("P_END_INDEX", endIndex);
	    qParams.put("P_ZEROS_SUFFIX", zerosSuffix.substring(endIndex));
	    List<UnitData> result = DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_GET_UNIT_DATA_ANCESTOR_UNDER_PRESIDENCY_BY_LEVEL.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    else
		return result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<UnitData> getUnitsByIdsString(String unitsIdsString) throws BusinessException {
	List<UnitData> unitsList = new ArrayList<UnitData>();
	if (unitsIdsString != null && unitsIdsString.length() > 0) {
	    String[] unitsIdsStrings = unitsIdsString.split(",");
	    Long[] unitsIds = new Long[unitsIdsStrings.length];
	    for (int i = 0; i < unitsIdsStrings.length; i++)
		unitsIds[i] = Long.parseLong(unitsIdsStrings[i]);

	    unitsList = searchUnitsByUnitsIds(unitsIds);
	}
	return unitsList;
    }

    private static List<UnitData> searchUnitsByUnitsIds(Long[] unitsIds) throws BusinessException {
	try {
	    if (unitsIds == null || unitsIds.length == 0)
		return new ArrayList<UnitData>();

	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNITS_IDS", unitsIds);

	    return DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_SEARCH_UNITS_BY_UNITS_IDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<UnitData> getUnitsByPhysicalManager(long physicalManagerId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PHYSICAL_MANAGER_ID", physicalManagerId);
	    return DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_GET_UNITS_BY_PHYSICAL_MANAGER.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Checks if a given unit with HKey "unitHKey" is a child of another unit with HKey "parentHKey" or not.
     * 
     * @param parentHKey
     *            the HKey of the parent unit
     * @param unitHKey
     *            the HKey of the unit to check if it is a child for the given parent or not
     * @return true if the unit with HKey "unitHKey" is a child for the unit with HKey "parentHKey", otherwise returns false.
     */
    public static boolean isChildUnit(String parentHKey, String unitHKey) {
	String parentHKeyPrefix = getHKeyPrefix(parentHKey);
	String unitHKeyPrefix = getHKeyPrefix(unitHKey);
	return (unitHKeyPrefix.length() > parentHKeyPrefix.length() && unitHKeyPrefix.startsWith(parentHKeyPrefix));
    }

    /**
     * Checks if a given unit with HKey "unitHKey" is a parent for another unit with HKey "childHKey" or not.
     * 
     * @param childHKey
     *            the HKey of the child unit
     * @param unitHKey
     *            the HKey of the unit to check if it is a parent for the given child or not
     * @return true if the unit with HKey "unitHKey" is a parent of the unit with HKey "childHKey", otherwise returns false.
     */
    public static boolean isParentUnit(String childHKey, String unitHKey) {
	String childHKeyPrefix = getHKeyPrefix(childHKey);
	String unitHKeyPrefix = getHKeyPrefix(unitHKey);
	return (childHKeyPrefix.length() > unitHKeyPrefix.length() && childHKeyPrefix.startsWith(unitHKeyPrefix));
    }

    public static List<String> getAncestorsHkeys(String unitHKey, int level) {
	ArrayList<String> unitsHKeyList = new ArrayList<>();
	unitsHKeyList.add(unitHKey);
	String hKeyPrefix = UnitsService.getHKeyPrefix(unitHKey);
	hKeyPrefix = hKeyPrefix.substring(0, hKeyPrefix.length() - 2);

	// level 1 is the "Presidency"
	while (hKeyPrefix.length() > (2 * (level - 1))) {
	    unitsHKeyList.add(String.format("%s%0" + (20 - hKeyPrefix.length()) + "d", hKeyPrefix, 0));
	    hKeyPrefix = hKeyPrefix.substring(0, hKeyPrefix.length() - 2);
	}

	return unitsHKeyList;
    }

    /*---------------------------------------- Field Units ------------------------------------*/

    public static void addFieldUnit(long unitId, String unitHKey, long transactionEmployeeId) throws BusinessException {
	String fieldUnitsIds = ETRConfigurationService.getFieldUnitsIds();
	validateAddFieldUnit(unitId, unitHKey, fieldUnitsIds);
	ETRConfigurationService.setFieldUnitsIds(fieldUnitsIds + "," + unitId, transactionEmployeeId);
    }

    public static void deleteFieldUnit(long unitId, long transactionEmployeeId) throws BusinessException {
	String fieldUnitsIds = "," + ETRConfigurationService.getFieldUnitsIds() + ",";

	validateDeleteFieldUnit(fieldUnitsIds, unitId);

	fieldUnitsIds = fieldUnitsIds.replace(("," + unitId + ","), ",");
	ETRConfigurationService.setFieldUnitsIds(fieldUnitsIds.substring(1, fieldUnitsIds.length() - 1), transactionEmployeeId);
    }

    private static void validateAddFieldUnit(long unitId, String unitHKey, String fieldUnitsIds) throws BusinessException {

	// Check if the Unit itself is a field unit
	if (("," + fieldUnitsIds + ",").contains("," + unitId + ","))
	    throw new BusinessException("error_fieldUnitExist");

	// Check if one the ancestors units is already a field unit
	if (isFieldUnit(unitHKey, fieldUnitsIds))
	    throw new BusinessException("error_ancestorFieldUnitExist");

	// Check if one the children units is already a field unit
	if (isFieldUnitExistAsChildren(unitHKey, fieldUnitsIds))
	    throw new BusinessException("error_childFieldUnitExist");
    }

    private static void validateDeleteFieldUnit(String fieldUnitsIds, long unitId) throws BusinessException {
	if (!fieldUnitsIds.contains("," + unitId + ","))
	    throw new BusinessException("error_fieldUnitNotExist");
    }

    private static boolean isFieldUnitExistAsChildren(String unitHKey, String fieldUnitsIds) throws BusinessException {
	if (countUnitChildrenExistInUnits(UnitsService.getHKeyPrefix(unitHKey), BaseService.splitStringToLongArray(fieldUnitsIds)) > 0)
	    return true;
	return false;
    }

    private static long countUnitChildrenExistInUnits(String prefixUnitHKey, Long[] unitsIds) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PREFIX_HKEY", prefixUnitHKey + "%");
	    qParams.put("P_UNITS_IDS", unitsIds);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_UNIT_CHILDREN_EXIST_IN_UNITS.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static boolean isFieldUnit(String unitHKey, String... fieldUnitsIds) throws BusinessException {

	ArrayList<String> unitsHKeyList = (ArrayList<String>) getAncestorsHkeys(unitHKey, 2);

	List<UnitData> unitList = getUnitsByHKeys(unitsHKeyList);
	String fieldUnitsIdsString = "";
	if (fieldUnitsIds != null && fieldUnitsIds.length > 0)
	    fieldUnitsIdsString = "," + fieldUnitsIds[0] + ",";
	else
	    fieldUnitsIdsString = "," + ETRConfigurationService.getFieldUnitsIds() + ",";

	for (UnitData unit : unitList) {
	    if (fieldUnitsIdsString.contains("," + unit.getId() + ","))
		return true;
	}
	return false;
    }

    private static List<UnitData> searchUnitsByDisclaimersInstanceId(Long instanceId, Long empUnitRegionId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    return DataAccess.executeNamedQuery(UnitData.class, QueryNamesEnum.HCM_GET_UNIT_BY_DISCLAIMER_DETAILS_INSTANCE_ID.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<UnitData> getUnitsByDisclaimersInstanceId(Long instanceId, Long empUnitRegionId) throws BusinessException {
	List<UnitData> sentBackUnitsData = searchUnitsByDisclaimersInstanceId(instanceId, empUnitRegionId);

	WFPosition regionPosition = RetirementsWorkFlow.getRegionPayrollUnitManager(empUnitRegionId);
	WFPosition generalDirectoratePosition = RetirementsWorkFlow.getRegionPayrollUnitManager(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	if (regionPosition == null || generalDirectoratePosition == null) {
	    throw new BusinessException("error_general");
	}

	UnitData payrollRegionUnitData = getUnitById(regionPosition.getUnitId());
	UnitData payrollGeneralDirectorateUnitData = getUnitById(generalDirectoratePosition.getUnitId());
	for (int i = 0; i < sentBackUnitsData.size(); i++) {
	    if ((sentBackUnitsData.get(i).getId()).equals(payrollRegionUnitData.getId()) || (sentBackUnitsData.get(i).getId()).equals(payrollGeneralDirectorateUnitData.getId())) {
		sentBackUnitsData.remove(sentBackUnitsData.get(i));
		i--;
	    }
	}
	return sentBackUnitsData;
    }

    /*---------------------------------------- Unit Transaction--------------------------------*/

    /**
     * Inserts a unit transaction for all transactions types (create, rename, move, merge, separate, scale up, scale down, or cancel).
     * 
     * @param unit
     *            the {@link UnitData} object which the transaction performed for it
     * @param decisionDate
     *            the unit transaction insertion decision date in mm/MM/yyyy format
     * @param decisionNumber
     *            the unit transaction insertion decision number
     * @param transactionTypeCode
     *            create, rename, move, merge, separate, scale up, scale down, or cancel
     * @param transactionUserId
     *            the user ID who performed this transaction
     * @param useSession
     *            the {@link CustomSession} to be used to access the database
     * @throws BusinessException
     *             if any error occurs
     */
    private static void addUnitTransaction(UnitData unit, Date decisionDate, String decisionNumber, int transactionTypeCode, Long transactionUserId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    UnitTransaction unitTransaction = new UnitTransaction();
	    unitTransaction.setUnitId(unit.getId());
	    unitTransaction.setDecisionNumber(decisionNumber);
	    unitTransaction.setDecisionDate(decisionDate);
	    unitTransaction.setTransactionTypeId(CommonService.getTransactionTypeByCodeAndClass(transactionTypeCode, TransactionClassesEnum.UNITS.getCode()).getId());
	    // unitTransaction.setCode(unit.getCode());
	    unitTransaction.setUnitTypeId(unit.getUnitTypeId());
	    unitTransaction.setName(unit.getName());
	    unitTransaction.setFullName(unit.getFullName());
	    unitTransaction.setParentUnitId(unit.getParentUnitId());
	    unitTransaction.sethKey(unit.gethKey());
	    unitTransaction.setRegionId(unit.getRegionId());
	    unitTransaction.setRemarks(unit.getRemarks());
	    unitTransaction.setOrderUnitParent(unit.getOrderUnderParent());
	    unitTransaction.setOfficialManagerId(unit.getOfficialManagerId());
	    unitTransaction.setPhysicalManagerId(unit.getPhysicalManagerId());
	    unitTransaction.setTransactionUserId(transactionUserId);
	    unitTransaction.setTransactionDate(HijriDateService.getHijriSysDate());

	    if (unit.getMoveToUnitId() != null) {
		unitTransaction.setMoveToUnitId(unit.getMoveToUnitId());
		unitTransaction.setMoveToUnitFullName(getUnitById(unit.getMoveToUnitId()).getFullName());
	    }

	    if (unit.getMergeWithUnitId() != null) {
		unitTransaction.setMergedWithUnitId(unit.getMergeWithUnitId());
		unitTransaction.setMergedWithUnitFullName(getUnitById(unit.getMergeWithUnitId()).getFullName());
	    }

	    DataAccess.addEntity(unitTransaction, session);

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

    /**
     * Gets all unit transactions for a certain unit. </br>
     * wrapper for {@link #searchUnitTransactions(long)}
     * 
     * @param unitId
     *            the Id of the unit to get its transactions history
     * @return a list of {@link UnitTransactionData} for this unit
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<UnitTransactionData> getUnitTransactionsByUnitId(long unitId) throws BusinessException {
	return searchUnitTransactions(unitId);
    }

    /**
     * Gets all unit transactions for a certain unit.
     * 
     * @param unitId
     *            the Id of the unit to get its transactions
     * @return a list of {@link UnitTransactionData} for this unit
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<UnitTransactionData> searchUnitTransactions(long unitId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_UNIT_ID", unitId);
	    return DataAccess.executeNamedQuery(UnitTransactionData.class, QueryNamesEnum.HCM_GET_UNIT_TRANSACTION_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------------- Unit Type---------------------------------------*/

    /**
     * Gets the unit's types available in a given unit's children.
     * 
     * @param unit
     *            the {@link UnitData} to get its children units types
     * @param allUnitTypes
     *            a {@link UnitType} list contains all the types
     * @return a {@link UnitType} list contains the types available in the children of the given unit
     */
    public static List<UnitType> getAvailableUnitTypes(UnitData unit, List<UnitType> allUnitTypes) {
	List<UnitType> availableUnits = new ArrayList<UnitType>();
	if (unit.getChildrenUnitTypesCodes() != null) {
	    StringTokenizer childToken = new StringTokenizer(unit.getChildrenUnitTypesCodes(), ",");
	    String token;
	    while (childToken.hasMoreTokens()) {
		token = childToken.nextToken();
		for (UnitType i : allUnitTypes) {
		    if (token.equals(i.getCode().toString())) {
			availableUnits.add(i);
			break;
		    }
		}
	    }
	}
	return availableUnits;
    }

    /**
     * Gets all units types. </br>
     * wrapper for {@link #searchUnitTypes(long, int)}
     * 
     * @return a list of all {@link UnitType}
     * @throws BusinessException
     *             if any error occurs
     */
    public static List<UnitType> getAllUnitTypes() throws BusinessException {
	return searchUnitTypes(FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    /**
     * Gets a unit type by its ID. </br>
     * wrapper for {@link #searchUnitTypes(long, int)}
     * 
     * @param id
     *            the ID of the unit type
     * @return the {@link UnitType} with this ID
     * @throws BusinessException
     *             if any error occurs
     */
    public static UnitType getUnitTypeById(long id) throws BusinessException {
	List<UnitType> unitTypes = searchUnitTypes(id, FlagsEnum.ALL.getCode());
	if (unitTypes.size() > 0)
	    return unitTypes.get(0);
	else
	    return null;
    }

    /**
     * Gets a unit type by its Code. </br>
     * wrapper for {@link #searchUnitTypes(long, int)}
     * 
     * @param code
     *            the code of the unit type
     * @return the {@link UnitType} with this code
     * @throws BusinessException
     *             if any error occurs
     */
    public static UnitType getUnitTypeByCode(int code) throws BusinessException {
	List<UnitType> unitTypes = searchUnitTypes(FlagsEnum.ALL.getCode(), code);
	if (unitTypes.size() > 0)
	    return unitTypes.get(0);
	else
	    return null;
    }

    /**
     * Gets all units types with a specific ID and code.
     * 
     * @param id
     *            the ID of the unit type
     * @param code
     *            the code of the unit type
     * @return the {@link UnitType} with this ID and code
     * @throws BusinessException
     *             if any error occurs
     */
    private static List<UnitType> searchUnitTypes(long id, int code) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ID", id);
	    qParams.put("P_CODE", code);
	    return DataAccess.executeNamedQuery(UnitType.class, QueryNamesEnum.HCM_GET_UNIT_TYPE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------------- Reports---------------------------------------*/

    /**
     * Gets the bytes to be sent to the report to print the units reports.
     * 
     * @param reportType
     *            an <code>int</code> containing the type of the report (10) organization hierarchy report. (20) units transactions details report. (30) units transactions statistics report. (40) units transactions report. (50) units transactions required report.
     * @param selectedUnitHkey
     *            a <code>String</code> containing the HKey of the unit to search for it
     * @param selectedUnitId
     *            a <code>long</code> containing the ID of the unit to search for it
     * @param fromDate
     *            a <code>Date</code> containing the from hijri date, in mm/MM/yyyy format, to start searching from it
     * @param toDate
     *            a <code>Date</code> containing the to hijri date, in mm/MM/yyyy format, to end searching at it
     * @param unitTransactionType
     *            a <code>long</code> containing the type of the transaction to search for it
     * @param reportTitle
     *            a <code>String</code> containing the title of the report
     * @return an array of bytes to be sent to the report
     * @throws BusinessException
     *             if any error occurs
     */
    public static byte[] getUnitsReportsBytes(int reportType, String selectedUnitHkey, long selectedUnitId, Date fromDate, Date toDate, long unitTransactionType, String reportTitle) throws BusinessException {
	try {
	    String reportName = "";
	    Map<String, Object> parameters = new HashMap<String, Object>();
	    String unitHKeyPrefix = UnitsService.getHKeyPrefix(selectedUnitHkey);

	    if (reportType == 10) {
		reportName = ReportNamesEnum.UNITS_ORGANIZATION_HIERARCHY.getCode();
		parameters.put("P_SELECTED_LVL", unitHKeyPrefix.length() / 2);
	    } else if (reportType == 20) {
		reportName = ReportNamesEnum.UNITS_HIERARCHY_TRANSACTIONS.getCode();
		parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
		parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
		parameters.put("P_TRNS_TYPE", unitTransactionType);
	    } else if (reportType == 30) {
		reportName = ReportNamesEnum.UNITS_HIERARCHY_TRANSACTIONS_STATISTICAL.getCode();
		parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
		parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
		parameters.put("P_TRNS_TYPE", unitTransactionType);
	    } else if (reportType == 40) {
		reportName = ReportNamesEnum.UNITS_UNIT_TRANSACTIONS.getCode();
		parameters.put("P_UNIT_ID", selectedUnitId);
	    } else {
		reportName = ReportNamesEnum.UNITS_REQUIRED_TRANSACTIONS.getCode();
	    }

	    parameters.put("P_SELECTED_UNIT_HKEY_PREFIX", unitHKeyPrefix);
	    parameters.put("P_SYS_DATE", HijriDateService.getHijriSysDateString());
	    parameters.put("P_REPORT_TITLE", reportTitle);
	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}