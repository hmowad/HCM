package com.code.services.hcm;

import java.util.Date;
import java.util.List;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.BaseService;

/**
 * Provides methods for handling the units collective operations such as construct, rename or cancel units.
 * 
 */
public class UnitsCollectiveService extends BaseService {

    /**
     * Constructs the units collective service.
     */
    private UnitsCollectiveService() {
    }

    /***
     * to construct collective of units
     * 
     * @param decisionNumber
     * @param decisionDate
     * @param parentsUnitsData
     * @param unitData
     * @param userId
     * @param useSession
     * @throws BusinessException
     */
    public static void constructUnits(String decisionNumber, Date decisionDate, List<UnitData> parentsUnitsData, UnitData unitData, Long userId, CustomSession... useSession) throws BusinessException {

	validateConstructionUnits(decisionNumber, decisionDate, parentsUnitsData, unitData);

	decisionNumber = decisionNumber.trim();
	unitData.setName(unitData.getName().trim());

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (UnitData parentUnitData : parentsUnitsData) {
		UnitData newUnitData = new UnitData();

		newUnitData.setName(unitData.getName());
		newUnitData.setUnitTypeId(unitData.getUnitTypeId());
		newUnitData.setOrderUnderParent(unitData.getOrderUnderParent());
		newUnitData.setRemarks(unitData.getRemarks());

		newUnitData.setRegionId(parentUnitData.getRegionId());

		UnitsService.addUnit(parentUnitData, newUnitData, decisionDate, decisionNumber, userId, session);
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
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /***
     * to rename collective of units
     * 
     * @param decisionNumber
     * @param decisionDate
     * @param unitsData
     * @param newUnitName
     * @param userId
     * @param useSession
     * @throws BusinessException
     */
    public static void renameUnits(String decisionNumber, Date decisionDate, List<UnitData> unitsData, String newUnitName, Long userId, CustomSession... useSession) throws BusinessException {

	validateRenamingAndCancellationUnits(decisionNumber, decisionDate, unitsData, newUnitName, TransactionTypesEnum.UNIT_NAME_MODIFICATION.getCode());

	decisionNumber = decisionNumber.trim();
	newUnitName = newUnitName.trim();

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (UnitData unitData : unitsData) {
		unitData.setNewName(newUnitName);
		UnitsService.renameUnit(unitData, decisionDate, decisionNumber, TransactionTypesEnum.UNIT_NAME_MODIFICATION.getCode(), userId, session);
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
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /***
     * to cancel collective units
     * 
     * @param decisionNumber
     * @param decisionDate
     * @param unitsData
     * @param userId
     * @param useSession
     * @throws BusinessException
     */

    public static void cancelUnits(String decisionNumber, Date decisionDate, List<UnitData> unitsData, Long userId, CustomSession... useSession) throws BusinessException {

	validateRenamingAndCancellationUnits(decisionNumber, decisionDate, unitsData, null, TransactionTypesEnum.UNIT_CANCELLATION.getCode());

	decisionNumber = decisionNumber.trim();

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    for (UnitData unitData : unitsData)
		UnitsService.cancelUnit(unitData, decisionNumber, decisionDate, userId, session);

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
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /***
     * validate units collective data for construct, rename and cancel methods
     * 
     * @param decisionNumber
     * @param decisionDate
     * @param unitsData
     * @param unitName
     * @throws BusinessException
     */
    private static void validateConstructionUnits(String decisionNumber, Date decisionDate, List<UnitData> unitsData, UnitData unitData) throws BusinessException {

	// validate if units list is empty or not
	if (unitsData == null || unitsData.isEmpty())
	    throw new BusinessException("error_unitsListEmpty");

	// validate decision number
	if (decisionNumber == null || decisionNumber.trim().isEmpty())
	    throw new BusinessException("error_decNumberMandatory");

	// validate decision date
	if (decisionDate == null)
	    throw new BusinessException("error_decDateMandatory");

	// Decision Date must be today or before
	validateOldHijriDate(decisionDate);

	// validate unit type
	if (unitData == null || unitData.getUnitTypeId() == null)
	    throw new BusinessException("error_unitTypeMandatory");

	// validate unit order
	if (unitData == null || unitData.getOrderUnderParent() == null)
	    throw new BusinessException("error_unitOrderMandatory");

	// validate unit name
	if (unitData == null || unitData.getName() == null || unitData.getName().trim().isEmpty())
	    throw new BusinessException("error_unitNameMandatory");

    }

    private static void validateRenamingAndCancellationUnits(String decisionNumber, Date decisionDate, List<UnitData> unitsData, String newUnitName, int transactionType) throws BusinessException {

	// validate if units list is empty or not
	if (unitsData == null || unitsData.isEmpty())
	    throw new BusinessException("error_unitsListEmpty");

	// validate decision number
	if (decisionNumber == null || decisionNumber.trim().isEmpty())
	    throw new BusinessException("error_decNumberMandatory");

	// validate decision date
	if (decisionDate == null)
	    throw new BusinessException("error_decDateMandatory");

	// Decision Date must be today or before
	validateOldHijriDate(decisionDate);

	UnitData unitData = unitsData.get(0);

	if (TransactionTypesEnum.UNIT_NAME_MODIFICATION.getCode() == transactionType) {
	    if (newUnitName == null || newUnitName.trim().isEmpty())
		throw new BusinessException("error_unitNameMandatory");

	    if (unitData.getName().equals(newUnitName))
		throw new BusinessException("error_renameUnitWithTheSameNameNotAllowed");
	}

	for (int i = 1; i < unitsData.size(); ++i) {
	    if (unitData.getUnitTypeId().longValue() != unitsData.get(i).getUnitTypeId().longValue()) {
		throw new BusinessException("error_unitTypeValidation");
	    }

	    if (TransactionTypesEnum.UNIT_NAME_MODIFICATION.getCode() == transactionType && !unitData.getName().equals(unitsData.get(i).getName())) {
		throw new BusinessException("error_allSelectedUnitsShouldBeWithTheSameName");
	    }
	}
    }
}