package com.code.services.hcm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.exercises.EmployeeExercise;
import com.code.dal.orm.hcm.exercises.EmployeeExerciseData;
import com.code.dal.orm.hcm.exercises.Exercise;
import com.code.dal.orm.hcm.exercises.ExerciseData;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.ReportNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

/**
 * Provides methods for handling the exercises.
 * 
 */
public class ExercisesService extends BaseService {

    /**
     * Constructs the exercises service.
     */
    private ExercisesService() {
    }

    /************************************ Exercises *****************************************************/

    public static void addExercise(ExerciseData exerciseData, long userId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    Exercise exercise = exerciseData.getExercise();
	    exercise.setSystemUser(userId + ""); // For Auditing.

	    DataAccess.addEntity(exercise, session);

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
     * Gets all exercises from the database. </br>
     * wrapper for {@link #searchExercises(long)}.
     * 
     * @return List contains all the {@link ExerciseData} objects
     * @throws BusinessException
     *             of any error occurs
     */
    public static List<ExerciseData> getExercises() throws BusinessException {
	return searchExercises(FlagsEnum.ALL.getCode());
    }

    /**
     * Gets a specific exercise by its ID. </br>
     * wrapper for {@link #searchExercises(long)}.
     * 
     * @return the {@link ExerciseData} object
     * @throws BusinessException
     *             of any error occurs
     */
    public static ExerciseData getExerciseById(long id) throws BusinessException {
	List<ExerciseData> exercisesData = searchExercises(id);
	if (exercisesData.size() > 0)
	    return exercisesData.get(0);
	else
	    return null;
    }

    /**
     * Gets the exercises from the database.
     * 
     * @return List contains all the {@link ExerciseData} objects
     * @throws BusinessException
     *             of any error occurs
     */
    private static List<ExerciseData> searchExercises(long exerciseId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EXERCISE_ID", exerciseId);
	    return DataAccess.executeNamedQuery(ExerciseData.class, QueryNamesEnum.HCM_GET_EXERCISES_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /************************************ Employees Exercises *****************************************************/

    public static void addEmployeeExercise(EmployeeExerciseData employeeExerciseData, long userId, CustomSession... useSession) throws BusinessException {

	validateEmployeeExercise(employeeExerciseData);

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    EmployeeExercise employeeExercise = employeeExerciseData.getEmployeeExercise();
	    employeeExercise.setSystemUser(userId + ""); // For Auditing.

	    DataAccess.addEntity(employeeExercise, session);

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

    private static void validateEmployeeExercise(EmployeeExerciseData employeeExerciseData) throws BusinessException {
	ExerciseData exerciseData = getExerciseById(employeeExerciseData.getExerciseId());
	if (employeeExerciseData.getStartDate().before(exerciseData.getStartDate())
		|| employeeExerciseData.getEndDate().after(exerciseData.getEndDate()))
	    throw new BusinessException("error_employeeExerciseDatesConflict");
	else {
	    List<EmployeeExerciseData> empExerciseData = getEmployeeExerciseData(employeeExerciseData.getExerciseId(), employeeExerciseData.getEmployeeId(), employeeExerciseData.getStartDate(), employeeExerciseData.getEndDate());
	    if (empExerciseData != null && empExerciseData.size() != 0)
		throw new BusinessException("error_empDuplicateSameExercise");

	}
    }

    /************************************ Queries *****************************************************/

    public static List<EmployeeExerciseData> getEmployeeExerciseData(Long exerciseId, Long employeeId, Date startDate, Date endDate) throws BusinessException {

	Map<String, Object> qParams = new HashMap<String, Object>();
	qParams.put("P_EXERCISE_ID", exerciseId);
	qParams.put("P_EMPLOYEE_ID", employeeId);
	if (startDate != null) {
	    qParams.put("P_START_DATE_FLAG", FlagsEnum.ON.getCode());
	    qParams.put("P_START_DATE", HijriDateService.getHijriDateString(startDate));
	}
	if (endDate != null) {
	    qParams.put("P_END_DATE_FLAG", FlagsEnum.ON.getCode());
	    qParams.put("P_END_DATE", HijriDateService.getHijriDateString(endDate));
	}

	try {
	    return DataAccess.executeNamedQuery(EmployeeExerciseData.class, QueryNamesEnum.HCM_GET_EMPLOYEE_EXERCISES_DATA.getCode(), qParams);
	} catch (DatabaseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /************************************ Reports ********************************************************************/

    public static byte[] getExercisesReportsBytes(int reportType, long regionId, String regionDesc, String unitHKey, String unitFullName,
	    Long employeeId, long categoryId, Long exerciseId, Date fromDate, Date toDate) throws BusinessException {
	try {
	    String reportName = "";
	    String hijriSysDateString = HijriDateService.getHijriSysDateString();
	    Map<String, Object> parameters = new HashMap<String, Object>();

	    if (reportType == 10) {
		reportName = ReportNamesEnum.EXERCISES_DATA.getCode();
	    } else if (reportType == 20) {
		reportName = ReportNamesEnum.EXERCISES_EMPLOYEES.getCode();

		parameters.put("P_REGION_ID", regionId);
		parameters.put("P_REGION_DESC", regionDesc);
		parameters.put("P_UNIT_HKEY_PREFIX", UnitsService.getHKeyPrefix(unitHKey));
		parameters.put("P_UNIT_FULL_NAME", unitFullName);
		parameters.put("P_EMPLOYEE_ID", employeeId == null ? FlagsEnum.ALL.getCode() : employeeId);
		parameters.put("P_CATEGORY_ID", categoryId);
		parameters.put("P_EXERCISE_ID", exerciseId == null ? FlagsEnum.ALL.getCode() : exerciseId);
	    }

	    if (fromDate != null) {
		parameters.put("P_FROM_DATE_FLAG", FlagsEnum.ON.getCode());
		parameters.put("P_FROM_DATE", HijriDateService.getHijriDateString(fromDate));
	    } else {
		parameters.put("P_FROM_DATE_FLAG", FlagsEnum.ALL.getCode());
		parameters.put("P_FROM_DATE", hijriSysDateString);
	    }
	    if (toDate != null) {
		parameters.put("P_TO_DATE_FLAG", FlagsEnum.ON.getCode());
		parameters.put("P_TO_DATE", HijriDateService.getHijriDateString(toDate));
	    } else {
		parameters.put("P_TO_DATE_FLAG", FlagsEnum.ALL.getCode());
		parameters.put("P_TO_DATE", hijriSysDateString);
	    }

	    parameters.put("P_SYS_DATE", hijriSysDateString);

	    return getReportData(reportName, parameters);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_reportPrintingError");
	}
    }
}