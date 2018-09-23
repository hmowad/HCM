package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.raises.Raise;
import com.code.dal.orm.hcm.raises.RaiseEmployeeData;
import com.code.dal.orm.hcm.raises.RaiseTransactionData;
import com.code.enums.DegreesEnum;
import com.code.enums.RaiseStatusEnum;
import com.code.enums.RaiseTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.BaseService;

public class RaisesService extends BaseService {

    /**
     * private constructor to prevent instantiation
     * 
     */
    private RaisesService() {

    }

    /***************************** Object *****************************/
    /*---------------------------Operations---------------------------*/
    /*---------------------------Validations--------------------------*/
    /*---------------------------Queries------------------------------*/
    /*---------------------------Reports------------------------------*/

    /******************************************* Raise ************************************************/
    /*----------------------------------------Operations----------------------------------------------*/

    /**
     * Add a raise object to database
     * 
     * @param raise
     *            The raise will be added in DB
     * @param raiseEmployeeDataList
     *            The raiseEmployeeDataList will be added in DB
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void addRaiseAndRaiseEmployees(Raise raise, List<RaiseEmployeeData> raiseEmployeeDataList, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (raise.getType() == RaiseTypesEnum.ADDITIONAL.getCode()) {
		if (raiseEmployeeDataList.size() != 0) {
		    raise.setStatus(RaiseStatusEnum.INITIAL.getCode());
		    validateRaise(raise);
		    DataAccess.addEntity(raise, session);
		    for (RaiseEmployeeData raiseEmployeeData : raiseEmployeeDataList) {
			validateRaiseEmployee(raiseEmployeeData);
			raiseEmployeeData.setRaiseId(raise.getId());
			DataAccess.addEntity(raiseEmployeeData.getRaiseEmployee(), session);
			raiseEmployeeData.setId(raiseEmployeeData.getRaiseEmployee().getId());
		    }
		} else {
		    throw new BusinessException("error_mustAddOneEmployeeAtLeastToSaveAdditionalRaise");
		}
	    } else {
		raise.setStatus(RaiseStatusEnum.INITIAL.getCode());
		validateRaise(raise);
		DataAccess.addEntity(raise, session);
		if (raiseEmployeeDataList.size() != 0) {
		    for (RaiseEmployeeData raiseEmployeeData : raiseEmployeeDataList) {
			validateRaiseEmployee(raiseEmployeeData);
			raiseEmployeeData.setRaiseId(raise.getId());
			DataAccess.addEntity(raiseEmployeeData.getRaiseEmployee(), session);
			raiseEmployeeData.setId(raiseEmployeeData.getRaiseEmployee().getId());
		    }
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

    /**
     * Updates a raise object in database
     * 
     * @param raise
     *            The raise will be updated in DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void updateRaise(Raise raise, CustomSession... useSession) throws BusinessException {
	/* To Do */
    }

    /**
     * Deletes a raise object from database
     * 
     * @param raise
     *            The raise will be deleted from DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void deleteRaise(Raise raise, CustomSession... useSession) throws BusinessException {
	/* To Do */
    }

    /*----------------------------------------Validations----------------------------------------------*/

    /**
     * Validates raise object
     * 
     * @param raise
     * 
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validateRaise(Raise newRaise) throws BusinessException {
	List<Raise> raisesList = getRaises(null, null, newRaise.getDecisionNumber(), newRaise.getExecutionDate(), null);
	if (raisesList.size() != 0)
	    throw new BusinessException("error_decisionNumberAndExecutionDateCannotBeRepeated");
	if (newRaise.getDecisionNumber() == null)
	    throw new BusinessException("error_decisionNumberIsMandatory");
	if (newRaise.getDecisionDate() == null)
	    throw new BusinessException("error_decisionDateIsMandatory");
	if (newRaise.getCategoryId() == null)
	    throw new BusinessException("error_categoryIdIsMandatory");
	if (newRaise.getExecutionDate() == null)
	    throw new BusinessException("error_raiseDateIsMandatory");
    }

    /*------------------------------------------Queries------------------------------------------------*/
    /**
     * Wrapper for {@link #searchRaises()} to get all raises
     * 
     * @return array list of raises objects
     * @throws BusinessException
     */
    public static List<Raise> getAllRaises() throws BusinessException {
	return searchRaises(null, null, null, null, null);

    }

    public static Raise getRaiseById(Long id) throws BusinessException {
	List<Raise> result = searchRaises(id, null, null, null, null);
	if (result == null || result.size() == 0)
	    return null;
	return result.get(0);
    }

    public static List<Raise> getRaises(Long id, Date decisionDate, String decisionNumber, Date executionDate, Long categoryId) throws BusinessException {
	return searchRaises(id, decisionDate, decisionNumber, executionDate, categoryId);
    }

    private static List<Raise> searchRaises(Long id, Date decisionDate, String decisionNumber, Date executionDate, Long categoryId) throws BusinessException {
	return null;
    }

    /*************************************** Raise Employee ********************************************/
    /*----------------------------------------Operations----------------------------------------------*/
    /**
     * Updates a raiseEmployeeData object in database
     * 
     * @param raiseEmployeeData
     *            The raiseEmployeeData will be updated in DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void updateRaiseEmployee(RaiseEmployeeData raiseEmployeeData, CustomSession... useSession) throws BusinessException {
	/* To Do */
    }

    /**
     * Updates a raiseEmployeeData object in database
     * 
     * @param raise
     *            The raise will be updated in DB
     * @param raiseEmployeeDataList
     *            The raiseEmployeeDataList will be updated in DB
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */

    public static void updateRaiseAndEmployees(Raise raise, List<RaiseEmployeeData> raiseEmployeeDataList, CustomSession... useSession) throws BusinessException {
	validateRaise(raise);

	for (RaiseEmployeeData empData : raiseEmployeeDataList)
	    validateRaiseEmployee(empData);
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.updateEntity(raise, session);
	    updateRaiseEmployees(raise.getId(), raiseEmployeeDataList, useSession);
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    /**
     * Updates raiseEmployeeDataList of objects in database
     * 
     * @param raiseEmployeeDataList
     *            The raiseEmployeeDataList will be updated in DB
     * @param raiseId
     *            to get raise employees list from DB
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void updateRaiseEmployees(Long raiseId, List<RaiseEmployeeData> raiseEmployeeDataList, CustomSession... useSession) throws BusinessException {
	/* To Do */
	List<RaiseEmployeeData> empList = new ArrayList();
	// get the deserved for special Raise and the excluded for another reason for Annual Raise
	empList = getRaiseEmployeeByRaiseId(raiseId);
	List<RaiseEmployeeData> addEmpList = new ArrayList();
	List<RaiseEmployeeData> deleteEmpList = new ArrayList();
	raiseEmployeeDataList.sort((o1, o2) -> o1.getEmpId().compareTo(o2.getEmpId()));
	empList.sort((o1, o2) -> o1.getEmpId().compareTo(o2.getEmpId()));
	int i = 0, j = 0;
	while (i < empList.size() && j < raiseEmployeeDataList.size()) {
	    if (empList.get(i).getEmpId() == raiseEmployeeDataList.get(j).getEmpId()) {
		i++;
		j++;
	    } else if (empList.get(i).getEmpId() > raiseEmployeeDataList.get(j).getEmpId()) {
		addEmpList.add(raiseEmployeeDataList.get(j));
		j++;
	    } else {
		deleteEmpList.add(empList.get(i));
		i++;
	    }

	}
	if (i < empList.size()) {
	    deleteEmpList.add(empList.get(i));
	    i++;
	}
	if (j < raiseEmployeeDataList.size()) {
	    addEmpList.add(raiseEmployeeDataList.get(j));
	    j++;
	}

	addRaiseAndRaiseEmployees(null, addEmpList, useSession);
	deleteRaiseEmployees(deleteEmpList, useSession);

    }

    /**
     * Deletes a raiseEmployeeData object from database
     * 
     * @param raiseEmployeeData
     *            The raiseEmployeeData will be deleted from DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void deleteRaiseEmployee(RaiseEmployeeData raiseEmployeeData, CustomSession... useSession) throws BusinessException {
	/* To Do */
    }

    /**
     * Deletes raiseEmployeeDataList of objects from database
     * 
     * @param raiseEmployeeDataList
     *            The raiseEmployeeDataList will be deleted from DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void deleteRaiseEmployees(List<RaiseEmployeeData> raiseEmployeeDataList, CustomSession... useSession) throws BusinessException {
	/* To Do */
    }
    /*----------------------------------------Validations----------------------------------------------*/

    /**
     * Validates raiseEmployeeData object
     * 
     * @param raiseEmployeeData
     * 
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validateRaiseEmployee(RaiseEmployeeData raiseEmployeeData) throws BusinessException {
	Raise raise = getRaiseById(raiseEmployeeData.getRaiseId());
	if (raiseEmployeeData.getEmpDegreeId() == DegreesEnum.FIFTEENTH.getCode())
	    throw new BusinessException("error_thisEmployeeMustBeExcludedForEndOfLadder");
	if (raise.getType() == RaiseTypesEnum.ADDITIONAL.getCode()) {
	    if (raiseEmployeeData.getEmpNewDegreeId() <= raiseEmployeeData.getEmpDegreeId())
		throw new BusinessException("error_newDegreeOfEmployeeMustBeBiggerThanCurrentDegreeOfEmployee");
	}
    }

    /*------------------------------------------Queries------------------------------------------------*/
    /**
     * get raise employees by raiseId
     * 
     * @return array list of raiseEmployee objects
     * @throws BusinessException
     */
    public static List<RaiseEmployeeData> getRaiseEmployeeByRaiseId(Long raiseId) throws BusinessException {
	return null;
    }

    /*------------------------------------------Reports------------------------------------------------*/
    public static byte[] getRaiseEmployeesReportBytes(String decisionNumber, Date decisionDate, Integer deservedFlag) {
	return null;
    }

    /*************************************** Raise Transaction *****************************************/
    /*----------------------------------------Operations----------------------------------------------*/

    /**
     * Add a raiseTransactionData object to database
     * 
     * @param raiseTransactionData
     *            The raiseTransactionData will be added in DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void addRaiseTransaction(RaiseTransactionData raiseTransactionData, CustomSession... useSession) throws BusinessException {
	/* To Do */
    }

    /*----------------------------------------Validations----------------------------------------------*/

    /**
     * Validates raiseTransactionData object
     * 
     * @param raiseTransactionData
     * 
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    private static void validateRaiseTransactionData(RaiseTransactionData raiseTransactionData) throws BusinessException {
	/* To Do */
    }

}
