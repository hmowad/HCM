package com.code.services.hcm;

import java.util.Date;
import java.util.List;

import com.code.dal.CustomSession;
import com.code.dal.orm.hcm.raises.Raise;
import com.code.dal.orm.hcm.raises.RaiseEmployee;
import com.code.dal.orm.hcm.raises.RaiseEmployeeData;
import com.code.dal.orm.hcm.raises.RaiseTransactionData;
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
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void addRaise(Raise raise, CustomSession... useSession) throws BusinessException {
	/* To Do */
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
    private static void validateRaise(Raise raise) throws BusinessException {
	/* To Do */
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
     * Add a raise employee object to database
     * 
     * @param raiseEmployeeData
     *            The raiseEmployeeData will be added in DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void addRaiseEmployee(RaiseEmployeeData raiseEmployeeData, CustomSession... useSession) throws BusinessException {
	/* To Do */
    }

    /**
     * Add a raise employees list of objects to database
     * 
     * @param raiseEmployeeDataList
     *            The raiseEmployeeDataList will be added in DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void addRaiseEmployees(List<RaiseEmployeeData> raiseEmployeeDataList, CustomSession... useSession) throws BusinessException {
	/* To Do */
    }

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
     * Updates raiseEmployeeDataList of objects in database
     * 
     * @param raiseEmployeeDataList
     *            The raiseEmployeeDataList will be updated in DB
     * 
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static void updateRaiseEmployees(List<RaiseEmployeeData> raiseEmployeeDataList, CustomSession... useSession) throws BusinessException {
	/* To Do */
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
	/* To Do */
    }

    /*------------------------------------------Queries------------------------------------------------*/
    /**
     * get raise employees by raiseId
     * 
     * @return array list of raiseEmployee objects
     * @throws BusinessException
     */
    public static List<RaiseEmployee> getRaiseEmployeeByRaiseId(Long raiseId) throws BusinessException {
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
