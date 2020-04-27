package com.code.services.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.code.dal.DataAccess;
import com.code.dal.orm.hijri.HijriCalendar;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.BaseService;

public class HijriDateService extends BaseService {

    private static Map<String, HijriCalendar> hijriCalendarMap;
    private static Map<String, HijriCalendar> gregDatesMap;

    private HijriDateService() {
    }

    public static void init() {
	try {
	    hijriCalendarMap = new HashMap<String, HijriCalendar>();
	    gregDatesMap = new HashMap<String, HijriCalendar>();
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    List<HijriCalendar> calendarList = DataAccess.executeNamedQuery(HijriCalendar.class, QueryNamesEnum.UTIL_GET_ALL_HIJRI_CALENDARS.getCode(), null);

	    for (HijriCalendar cal : calendarList) {
		hijriCalendarMap.put(cal.getHijriMonth() + "" + cal.getHijriYear(), cal);
		gregDatesMap.put(sdf.format(cal.getHijriMonthEndGregDate()), cal);
	    }
	} catch (Exception e) {
	    System.out.println("HijriDateService initialization failed !!");
	    e.printStackTrace();
	}
    }

    public static Date getHijriSysDate() throws BusinessException {
	try {
	    return gregToHijriDate(new Date());
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static String getHijriSysDateString() throws BusinessException {
	try {
	    return getHijriDateString(gregToHijriDate(new Date()));
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static String getHijriDateString(Date hijriDate) {
	SimpleDateFormat sdf = new SimpleDateFormat("mm/MM/yyyy");
	return ((hijriDate == null) ? null : sdf.format(hijriDate));
    }

    public static Date getHijriDate(String hijriDateString) {
	if (hijriDateString == null || hijriDateString.equals(""))
	    return null;

	try {
	    SimpleDateFormat sdf = new SimpleDateFormat("mm/MM/yyyy");
	    return sdf.parse(hijriDateString);
	} catch (ParseException e) {
	    return null;
	}
    }

    public static Date getExternalHijriDate(String date) {
	try {
	    date = date.substring(0, 2) + "/" + date.substring(2, 4) + "/" + date.substring(4, 8);
	    return getHijriDate(date);

	} catch (Exception e) {
	    return null;
	}
    }

    public static boolean isValidHijriDate(Date hijriDate) throws BusinessException {
	try {
	    String[] splittedDate = getHijriDateString(hijriDate).split("/");

	    if (Integer.parseInt(splittedDate[0]) <= 29)
		return true;

	    HijriCalendar tempCal = getHijriCalendar(Integer.parseInt(splittedDate[1]), Integer.parseInt(splittedDate[2]));

	    if (tempCal.getHijriMonthLength() >= Integer.parseInt(splittedDate[0]))
		return true;

	    return false;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Date addSubHijriDays(Date inputHijriDate, Integer noOfDays, boolean... flags) throws BusinessException {
	return getHijriDate(addSubStringHijriDays(getHijriDateString(inputHijriDate), noOfDays, flags));
    }

    public static String addSubStringHijriDays(String inputHijriDate, Integer noOfDays, boolean... flags) throws BusinessException {
	boolean thirtyFlag = false;
	if (flags != null && flags.length > 0)
	    thirtyFlag = flags[0];

	try {
	    String[] splittedDate;
	    int[] date = new int[3];

	    splittedDate = inputHijriDate.split("/");
	    for (int j = 0; j <= 2; j++) {
		date[j] = Integer.parseInt(splittedDate[j]);
	    }

	    if (noOfDays >= 0) {
		while (noOfDays > 0) {
		    HijriCalendar tempCal;
		    if (thirtyFlag) {
			tempCal = new HijriCalendar();
			tempCal.setHijriMonthLength(30);
		    } else {
			tempCal = getHijriCalendar(date[1], date[2]);
		    }

		    if (tempCal.getHijriMonthLength() >= date[0] + noOfDays) {
			date[0] += noOfDays;
			noOfDays = 0;
		    } else {
			if (date[1] == 12) {
			    date[2]++;
			    date[1] = 0;
			}
			date[1]++;
			noOfDays = noOfDays - (tempCal.getHijriMonthLength() - date[0]) - 1;
			date[0] = 1;
		    }
		}
	    } else {
		while (noOfDays < 0) {
		    if (date[0] > (-1 * noOfDays)) {
			date[0] += noOfDays;
			noOfDays = 0;
		    } else {
			if (date[1] == 1) {
			    date[2]--;
			    date[1] = 13;
			}
			date[1]--;

			HijriCalendar tempCal;
			if (thirtyFlag) {
			    tempCal = new HijriCalendar();
			    tempCal.setHijriMonthLength(30);
			} else {
			    tempCal = getHijriCalendar(date[1], date[2]);
			}

			date[0] += tempCal.getHijriMonthLength();
		    }
		}
	    }

	    String newHijriDate = (date[0] < 10 ? "0" + date[0] : "" + date[0]) + "/" + (date[1] < 10 ? "0" + date[1] : "" + date[1]) + "/" + date[2];
	    if (!isValidHijriDate(getHijriDate(newHijriDate))) {
		newHijriDate = "29" + "/" + (date[1] < 10 ? "0" + date[1] : "" + date[1]) + "/" + date[2];
	    }
	    return newHijriDate;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Date addSubHijriMonthsDays(Date inputHijriDate, int noOfMonths, int noOfDays) throws BusinessException {
	return getHijriDate(addSubStringHijriMonthsDays(getHijriDateString(inputHijriDate), noOfMonths, noOfDays));
    }

    public static String addSubStringHijriMonthsDays(String inputHijriDate, int noOfMonths, int noOfDays) throws BusinessException {
	if (inputHijriDate != null && !inputHijriDate.isEmpty()) {
	    String monthsDateString = addSubStringHijriDays(inputHijriDate, noOfMonths * 30, true);

	    return addSubStringHijriDays(monthsDateString, noOfDays);
	}

	return null;
    }

    /**
     * Returns the difference between two hijri dates. Results is represented in both months and days
     * 
     * @param startDate
     * @param endDate
     *
     * @return array of Integers, First index contains the days counts and the second index contains the months count
     * @throws BusinessException
     *             if any of the dates are invalid or the end date is before the start date, Or if any errors happen
     */
    public static Integer[] hijriDateDiffInMonthsAndDays(String startDate, String endDate) throws BusinessException {

	try {
	    // Throws an exception if any of the dates are invalid or the end date is before the start date
	    if (!isValidHijriDate(getHijriDate(startDate)) || !isValidHijriDate(getHijriDate(endDate)) || getHijriDate(endDate).before(getHijriDate(startDate)))
		throw new Exception();

	    // Result array, Index 0 contains days count and index 1 contains months count
	    Integer[] diff = new Integer[2];
	    diff[0] = 0;
	    diff[1] = 0;

	    // Splitting both start date and end date into arrays

	    String[] splittedStartDate;
	    int[] startDateArray = new int[3];

	    splittedStartDate = startDate.split("/");
	    for (int j = 0; j <= 2; j++) {
		startDateArray[j] = Integer.parseInt(splittedStartDate[j]);
	    }

	    String[] splittedEndDate;
	    int[] endDateArray = new int[3];

	    splittedEndDate = endDate.split("/");
	    for (int j = 0; j <= 2; j++) {
		endDateArray[j] = Integer.parseInt(splittedEndDate[j]);
	    }

	    // First we get the months between the two dates but with excluding both start and end month
	    if (endDateArray[2] - startDateArray[2] >= 1) {
		diff[1] = (endDateArray[2] - startDateArray[2] - 1) * 12;
		diff[1] += 12 - startDateArray[1];
		diff[1] += endDateArray[1] - 1;
	    } else {
		int diffMonths = endDateArray[1] - startDateArray[1] - 1;
		if (diffMonths > 0)
		    diff[1] = diffMonths;
	    }

	    // Now we are calculating the days count in the starting month and the ending month

	    // If the starting months is the ending month
	    if (endDateArray[2] == startDateArray[2] && endDateArray[1] == startDateArray[1]) {
		diff[0] = endDateArray[0] - startDateArray[0] + 1;

		// If the difference in days is the whole month length then its considered as one month
		int startMonthActualLength = getHijriCalendar(startDateArray[1], startDateArray[2]).getHijriMonthLength();
		if (startMonthActualLength == diff[0]) {
		    diff[0] = 0;
		    diff[1]++;
		}
	    } else {
		diff[0] = 30 - startDateArray[0] + 1;
		diff[0] += endDateArray[0];

		if (diff[0] >= 30) {
		    diff[0] -= 30;
		    diff[1]++;

		    // If the difference in days is the whole month length then its considered as one month
		    if (diff[0] == getHijriCalendar(endDateArray[1], endDateArray[2]).getHijriMonthLength()) {
			diff[0] = 0;
			diff[1]++;
		    }
		} else { // Difference in days are less than 30 so it's counted in days based on month (The month before the last month) length
		    int monthActualLength;
		    if (endDateArray[1] == 1)
			monthActualLength = getHijriCalendar(12, endDateArray[2] - 1).getHijriMonthLength();
		    else
			monthActualLength = getHijriCalendar(endDateArray[1] - 1, endDateArray[2]).getHijriMonthLength();

		    diff[0] = monthActualLength - startDateArray[0] + 1;
		    diff[0] += endDateArray[0];
		}
	    }

	    return diff;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Date hijriToGregDate(Date date) {
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    String gregDate = hijriToGregDateString(getHijriDateString(date));
	    return sdf.parse(gregDate);
	} catch (ParseException e) {
	    return null;
	}
    }

    public static String hijriToGregDateString(String date) {
	try {
	    String[] splittedDate = date.split("/");
	    HijriCalendar tempCal = getHijriCalendar(Integer.parseInt(splittedDate[1]), Integer.parseInt(splittedDate[2]));

	    DateTime hijriMonthEndGregDate = new DateTime(tempCal.getHijriMonthEndGregDate().getTime());
	    DateTime gregDate = hijriMonthEndGregDate.minusDays(tempCal.getHijriMonthLength() - Integer.parseInt(splittedDate[0]));

	    Date result = new Date(gregDate.getMillis());
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    return sdf.format(result);
	} catch (Exception e) {
	    return null;
	}
    }

    public static Date gregToHijriDate(Date date) {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	return getHijriDate(gregToHijriDateString(sdf.format(date)));
    }

    public static String gregToHijriDateString(String date) {
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    Date gregDate = sdf.parse(date);
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(gregDate);

	    HijriCalendar endCal = gregDatesMap.get(sdf.format(gregDate));
	    if (endCal == null) {
		for (int i = 0; i < 31; i++) {
		    cal.add(Calendar.DAY_OF_MONTH, 1);
		    endCal = gregDatesMap.get(sdf.format(cal.getTime()));
		    if (endCal != null)
			break;
		}
	    }

	    DateTime endGregDate = new DateTime(endCal.getHijriMonthEndGregDate().getTime());
	    DateTime startGregDate = new DateTime(gregDate.getTime());
	    Days d = Days.daysBetween(startGregDate, endGregDate);
	    int daysDiff = d.getDays();

	    String monthYear = "/" + (endCal.getHijriMonth() < 10 ? "0" + endCal.getHijriMonth() : "" + endCal.getHijriMonth()) + "/" + endCal.getHijriYear();

	    int variance = endCal.getHijriMonthLength() - daysDiff;
	    if (variance <= 0) {
		variance--;
		return addSubStringHijriDays("01" + monthYear, variance);
	    }

	    return (variance < 10 ? "0" + variance : "" + variance) + monthYear;
	} catch (Exception e) {
	    return null;
	}
    }

    /**
     * Get the difference between two hijri dates formatted as java Date
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    public static int hijriDateDiff(Date startDate, Date endDate) {
	return hijriDateStringDiff(getHijriDateString(startDate), getHijriDateString(endDate));
    }

    public static float hijriDateDiffByHijriYear(Date startDate, Date endDate) {
	return (float) ((float) hijriDateDiff(startDate, endDate) / 354.0);
    }

    /**
     * Get the difference between two hijri dates formatted as String
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    public static int hijriDateStringDiff(String startDate, String endDate) {
	try {
	    String gregStartDate = hijriToGregDateString(startDate);
	    String gregEndDate = hijriToGregDateString(endDate);

	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    return gregDateDiff(sdf.parse(gregStartDate), sdf.parse(gregEndDate));
	} catch (Exception e) {
	    return -1;
	}
    }

    /**
     * Get the difference between two Greg dates formatted as java Date
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    public static int gregDateDiff(Date startDate, Date endDate) {
	DateTime startGregDate = new DateTime(startDate.getTime());
	DateTime endGregDate = new DateTime(endDate.getTime());
	Days d = Days.daysBetween(startGregDate, endGregDate);
	return d.getDays();
    }

    public static HijriCalendar getHijriCalendar(int hijriMonth, int hijriYear) throws BusinessException {
	try {
	    return hijriCalendarMap.get(hijriMonth + "" + hijriYear);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Check if date check is between Date start and end or not
     * 
     * @param start
     * @param end
     * @param check
     * @return ture in case of between; false otherwise.
     */
    public static boolean isDateBetween(Date start, Date end, Date check) {
	return check.compareTo(start) * end.compareTo(check) >= 0;
    }

    public static int getHijriMonthLength(int hijriMonth, int hijriYear) throws BusinessException {
	try {
	    HijriCalendar hijriCalendar = getHijriCalendar(hijriMonth, hijriYear);

	    return hijriCalendar.getHijriMonthLength();
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static int getHijriDateDay(Date date) throws BusinessException {
	try {
	    String dateString = HijriDateService.getHijriDateString(date);
	    String[] dateArray = dateString.split("/");
	    return Integer.parseInt(dateArray[0]);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static int getHijriDateMonth(Date date) throws BusinessException {
	try {
	    String dateString = HijriDateService.getHijriDateString(date);
	    String[] dateArray = dateString.split("/");
	    return Integer.parseInt(dateArray[1]);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static int getHijriDateYear(Date date) throws BusinessException {
	try {
	    String dateString = HijriDateService.getHijriDateString(date);
	    String[] dateArray = dateString.split("/");
	    return Integer.parseInt(dateArray[2]);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static int getHijriStringDay(String date) throws BusinessException {
	try {
	    String[] dateArray = date.split("/");
	    return Integer.parseInt(dateArray[0]);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static int getHijriStringMonth(String date) throws BusinessException {
	try {
	    String[] dateArray = date.split("/");
	    return Integer.parseInt(dateArray[1]);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static int getHijriStringYear(String date) throws BusinessException {
	try {
	    String[] dateArray = date.split("/");
	    return Integer.parseInt(dateArray[2]);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Date floorInvalidHijriDateToValidHijriDate(Date date)
	    throws BusinessException {

	if (!HijriDateService.isValidHijriDate(date)) {
	    return HijriDateService.addSubHijriDays(date, -1);
	} else
	    return date;
    }
}