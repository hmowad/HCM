package com.code.services;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.ReportService;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.HijriDateService;

public abstract class BaseService {
    protected static final int HIJRI_YEAR_DAYS = 354;

    private static ResourceBundle config = ResourceBundle.getBundle("com.code.resources.config");
    private static ResourceBundle messages = ResourceBundle.getBundle("com.code.resources.messages", new Locale("ar"));

    protected BaseService() {
    }

    protected static boolean isSessionOpened(CustomSession[] sessions) {
	if (sessions != null && sessions.length > 0)
	    return true;

	return false;
    }

    protected static void validateOldHijriDate(Date hijriDate) throws BusinessException {
	if (hijriDate == null)
	    return;
	if (!HijriDateService.isValidHijriDate(hijriDate))
	    throw new BusinessException("error_invalidHijriDate");
	if (hijriDate.after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_hijriDateViolation");
    }

    public static String getConfig(String key) {
	return config.getString(key);
    }

    protected static void log(int userId, String message) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	System.out.println(" < " + sdf.format(new Date()) + " > USER : " + userId + " : " + message);
    }

    public static String getMessage(String key) {
	return messages.getString(key);
    }

    public static String getParameterizedMessage(String key, Object... params) {
	String value = messages.getString(key);
	return params == null ? value : MessageFormat.format(value, params);
    }

    @SuppressWarnings("unchecked")
    protected static <T> List<T> getManyEntities(Class<T> dataClass, List<Object> queryInfo, Object[] manyEntitiesCriteria) throws BusinessException {
	try {
	    String queryName = (String) queryInfo.get(0);
	    Map<String, Object> parameters = (Map<String, Object>) queryInfo.get(1);

	    List<T> returnedEntities = new ArrayList<T>();
	    String manyEntitiesCriteriaKey = null;

	    for (String key : parameters.keySet()) {
		if (parameters.get(key).equals(manyEntitiesCriteria)) {
		    manyEntitiesCriteriaKey = key;
		    break;
		}
	    }

	    int i = 0;
	    while (i < manyEntitiesCriteria.length) {
		Object[] tempEntitiesCriteria = (manyEntitiesCriteria.length > 1000) ? Arrays.copyOfRange(manyEntitiesCriteria, i, Math.min(i + 1000, manyEntitiesCriteria.length)) : manyEntitiesCriteria;
		parameters.put(manyEntitiesCriteriaKey, tempEntitiesCriteria);
		returnedEntities.addAll(DataAccess.executeNamedQuery(dataClass, queryName, parameters));
		i += 1000;
	    }

	    // Reset parameters to original value.
	    parameters.put(manyEntitiesCriteriaKey, manyEntitiesCriteria);
	    return returnedEntities;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");

	}
    }

    /**
     * Split comma separated string to List<Long>
     * 
     * Wrapper for {@link #splitStringToLongArray(String)}.
     * 
     * @param value
     *            input String
     * @return List<Long> of separated values
     */
    public static List<Long> splitStringToLongList(String value) {

	return Arrays.asList(splitStringToLongArray(value));
    }

    /**
     * Split comma separated String to Long array
     * 
     * @param value
     *            input String
     * @return Long array of separated values
     */
    public static Long[] splitStringToLongArray(String value) {

	if (value == null || value.equals(""))
	    return new Long[0];

	String[] list = value.split(",");
	Long[] result = new Long[list.length];
	if (list.length > 0) {
	    for (int i = 0; i < list.length; i++)
		result[i] = Long.parseLong(list[i].trim());
	}

	return result;
    }

    public static byte[] getReportData(String reportFilePath, Map<String, Object> parameters) throws Exception {
	return ReportService.getReportData(reportFilePath, parameters, ETRConfigurationService.getReportsRoot());
    }

    public static byte[] getReportData(List<ArrayList<Object>> reportParts) throws Exception {
	return ReportService.getReportData(reportParts, ETRConfigurationService.getReportsRoot());
    }

    public static byte[] getRTFReportData(final String reportFilePath, final Map<String, Object> parameters) throws Exception {
	return ReportService.getRTFReportData(reportFilePath, parameters, ETRConfigurationService.getReportsRoot());
    }

}
