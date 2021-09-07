package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.dal.orm.hcm.vacations.VacationConfiguration;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.GendersEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.PaidVacationTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.SubVacationTypesEnum;
import com.code.enums.TerminationReasonsEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.HijriDateService;

/**
 * The class <code>VacationsBusinessRulesService</code> provides methods for handling the Internal and External vacations requests (Regular, Compelling and Sick vacations) for Officers, Soldiers and Employees.
 * 
 * These methods are calculating the balance, validating the request, manipulating the vacation request data, obtaining the vacation types and the vacations' transactions history.
 */
public class VacationsBusinessRulesService extends BaseService {

    /**
     * A constant holding the number of days of a hijri year which the balance is calculated based on it.
     */
    private static final int BALANCE_YEAR_DAYS = HIJRI_YEAR_DAYS;

    private static final int FULL_HIJRI_MONTH_DAYS = 30;

    /**
     * Constructs a newly allocated <code>VacationsBusinessRulesService</code> object.
     */
    private VacationsBusinessRulesService() {
    }

    /*---------------------------------------------------------- Vacation ------------------------------------------------------*/

    /*---------------------------------------------------------- Validations ---------------------------------------------------*/

    /**
     * Applies the regular vacation business rules before handling the request. </br>
     * In case of modifications the full new periods are sent. </br>
     * 
     * The Business Rules are:
     * <ul>
     * <li>The vacations balance should be enough to request a vacation.</li>
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check if there is a conflict with the beneficiary previous vacations dates.</li>
     * 
     * <li>For Officers:
     * <ul>
     * <li>Officers cannot take the first vacation before 6 working months.</li>
     * <li>The vacation period should be between 1 and 90 days.</li>
     * <li>Total vacation days within first service year cannot be more than 15 days.</li>
     * <li>Total vacations per Hijri year should be less than or equal 90 days.</li>
     * </ul>
     * </li>
     * 
     * <li>For Soldiers:
     * <ul>
     * <li>Soldiers cannot take the first vacation before 11 working months.</li>
     * <li>The vacation period should be between 15 and 30 days.</li>
     * <li>Soldiers can request only 2 vacations per year.</li>
     * <li>Additional vacation balance should be 15 days.</li>
     * <li>Total vacations per Hijri year should be less than or equal 60 days.</li>
     * <li>For the external vacation, It's not allowed to choose more than two countries.</li>
     * </ul>
     * </li>
     * 
     * <li>For Old Employees:
     * <ul>
     * <li>The vacation period should be between 5 and 120 days.</li>
     * <li>Total vacations per Hijri year should be less than or equal 120 days.</li>
     * </ul>
     * </li>
     * 
     * <li>For New Employees:
     * <ul>
     * <li>The vacation period should be between 5 and 90 days.</li>
     * <li>Total vacations per Hijri year should be less than or equal 90 days.</li>
     * </ul>
     * </li>
     * 
     * <li>For Contractors:
     * <ul>
     * <li>The vacation period should be between 5 and 30 days.</li>
     * </ul>
     * </li>
     * 
     * </ul>
     * 
     * @param empId
     *            the employee ID of the requester who makes a vacation request
     * @param startDateString
     *            a <code>String</code> containing the vacation start hijri date in mm/MM/yyyy format
     * @param endDateString
     *            a <code>String</code> containing the vacation end hijri date in mm/MM/yyyy format
     * @param period
     *            an <code>int</code> value containing the vacation period in days
     * @param location
     *            a <code>String</code> containing the vacation location which represents the country (or countries) which the employee will spend the vacation in
     * @param locationFlag
     *            the flag indicates whether the vacation is (0) internal or (1) external
     * @param requestType
     *            the vacation request type which is (1) New, (2) Modify or (4) Cancel vacation
     * @param relatedType
     *            This will be used for civil employees later
     * @throws BusinessException
     *             if any business rule is broken or any general error occurs
     * 
     * @see RequestTypesEnum
     * @see CategoriesEnum
     * @see VacationTypesEnum
     * @see LocationFlagsEnum
     */
    protected static void validateRegularVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	try {

	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    long categoryId = vacationBeneficiary.getCategoryId().longValue();

	    VacationConfiguration activeVacationConfiguration = null;
	    if (categoryId == CategoriesEnum.CONTRACTORS.getCode())
		activeVacationConfiguration = getActiveRegularVacationConfigurationForContractors(vacationBeneficiary);
	    else
		activeVacationConfiguration = getActiveVacationConfigurations(categoryId, request.getVacationTypeId()).get(0);

	    if (request.getStatus() == RequestTypesEnum.NEW.getCode()) {
		if (categoryId == CategoriesEnum.OFFICERS.getCode() || categoryId == CategoriesEnum.SOLDIERS.getCode()) {
		    Date correctDate = HijriDateService.addSubHijriDays(vacationBeneficiary.getRecruitmentDate(), activeVacationConfiguration.getPeriodBeforeFirstVacation() - 1, true);
		    if (request.getStartDate().before(correctDate))
			throw new BusinessException("error_vacWorkStartDate", new Object[] { activeVacationConfiguration.getPeriodBeforeFirstVacation() / FULL_HIJRI_MONTH_DAYS });
		}
	    }

	    // Get last regular vacation.
	    VacationData lastVacation = VacationsService.getLastVacationData(vacationBeneficiary.getEmpId(), request.getVacationTypeId());
	    if (lastVacation == null)
		lastVacation = new VacationData();

	    if (request.getStatus() == RequestTypesEnum.MODIFY.getCode() && lastVacation.getPeriod() != null && lastVacation.getPeriod().equals(request.getPeriod()))
		throw new BusinessException("error_sameModifyPeriodError");

	    int previousVacationPeriod = request.getStatus() == RequestTypesEnum.NEW.getCode() ? 0 : lastVacation.getPeriod();

	    // Check vacation periods for officers.
	    if (categoryId == CategoriesEnum.OFFICERS.getCode()) {

		if (request.getPeriod() < activeVacationConfiguration.getMinValue() || request.getPeriod() > activeVacationConfiguration.getMaxValue())
		    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });

		String vacationStartYear = request.getStartDateString().split("/")[2];
		String vacationEndYear = request.getEndDateString().split("/")[2];

		// Total vacations within first service year less than or equal activePeriod.getFirstYearMaxVacationDays().
		Date firstServiceYearEnd = HijriDateService.addSubHijriDays(vacationBeneficiary.getRecruitmentDate(), BALANCE_YEAR_DAYS - 1);
		if (request.getStartDate().compareTo(firstServiceYearEnd) <= 0) {
		    if (request.getStatus() == RequestTypesEnum.NEW.getCode() || (request.getPeriod() > lastVacation.getPeriod() && lastVacation.getEndDate().before(firstServiceYearEnd))) {
			int firstServiceYearRegularVacationsSum = sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), vacationBeneficiary.getRecruitmentDateString(), HijriDateService.getHijriDateString(firstServiceYearEnd));
			Date vacationEndDate = HijriDateService.addSubHijriDays(request.getStartDate(), request.getPeriod() - 1);
			int twoYearsDifference = 0;
			if (vacationEndDate.compareTo(firstServiceYearEnd) > 0) // end vacation is after end of first year
			    twoYearsDifference = HijriDateService.hijriDateDiff(firstServiceYearEnd, vacationEndDate);

			if (firstServiceYearRegularVacationsSum + request.getPeriod() - previousVacationPeriod - twoYearsDifference > activeVacationConfiguration.getFirstYearMaxVacationDays())
			    throw new BusinessException("error_firstYearVacationMaxPeriod", new Object[] { activeVacationConfiguration.getFirstYearMaxVacationDays() });
		    }
		}

		// Total vacations per Hijri year are less than or equal activePeriod.getMaxVacationsDaysPerYear().
		if (request.getStatus() == RequestTypesEnum.NEW.getCode() || request.getPeriod() > lastVacation.getPeriod()) {
		    int regularVacationsSum = sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), "01/01/" + vacationStartYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(vacationStartYear)).getHijriMonthLength() + "/12/" + vacationStartYear);
		    int twoYearsDifference = 0;
		    if (!vacationStartYear.equals(vacationEndYear))
			twoYearsDifference = HijriDateService.hijriDateStringDiff("01/01/" + vacationEndYear, request.getEndDateString()) + 1;

		    String exceptionalVacationStartYear = (Integer.parseInt(vacationStartYear) - 1) + "";
		    int exceptionalVacationsSum = sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.EXCEPTIONAL.getCode(), "01/01/" + exceptionalVacationStartYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(exceptionalVacationStartYear)).getHijriMonthLength() + "/12/" + exceptionalVacationStartYear);

		    if ((regularVacationsSum + request.getPeriod() - twoYearsDifference - previousVacationPeriod) > (activeVacationConfiguration.getMaxVacationsDaysPerYear() - exceptionalVacationsSum)) {
			if (exceptionalVacationsSum > 0)
			    throw new BusinessException("error_regularVacationsMaxDaysPerYearInCaseExceptionalExist", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYear() - exceptionalVacationsSum, exceptionalVacationsSum });
			else
			    throw new BusinessException("error_vacationsMaxDaysPerYear", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYear() });
		    }
		}
	    }

	    // Check vacation periods and counts for soldiers.
	    else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {

		if (request.getPeriod() < activeVacationConfiguration.getMinValue() || request.getPeriod() > activeVacationConfiguration.getMaxValue())
		    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });

		String vacationStartYear = request.getStartDateString().split("/")[2];
		String vacationEndYear = request.getEndDateString().split("/")[2];

		// Total vacations per Hijri year are less than or equal activePeriod.getMaxVacationsDaysPerYear().
		if (request.getStatus() == RequestTypesEnum.NEW.getCode() || request.getPeriod() > lastVacation.getPeriod()) {

		    int regularVacationsSum = sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), "01/01/" + vacationStartYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(vacationStartYear)).getHijriMonthLength() + "/12/" + vacationStartYear);
		    int twoYearsDifference = 0;
		    if (!vacationStartYear.equals(vacationEndYear))
			twoYearsDifference = HijriDateService.hijriDateStringDiff("01/01/" + vacationEndYear, request.getEndDateString()) + 1;

		    if (request.getStatus() == RequestTypesEnum.NEW.getCode()) {
			// Additional regular vacation for soldiers must be (activePeriod.getAdditionalVacationDays()) days
			if (regularVacationsSum == activeVacationConfiguration.getMaxVacationsDaysPerYear() && request.getPeriod() != activeVacationConfiguration.getAdditionalVacationDays())
			    throw new BusinessException("error_soldiersAdditionalRegularVacPeriod", new Object[] { activeVacationConfiguration.getAdditionalVacationDays() });

			if ((regularVacationsSum != activeVacationConfiguration.getMaxVacationsDaysPerYear()) && ((regularVacationsSum + request.getPeriod() - twoYearsDifference - previousVacationPeriod) > activeVacationConfiguration.getMaxVacationsDaysPerYear()))
			    throw new BusinessException("error_vacationsMaxDaysPerYear", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYear() });

		    } else { // Modification

			// if he takes vacations greater than (activePeriod.getMaxVacationsDaysPerYear()) days, so he wants to modify the additional vacation now, which is not applicable because the additional vacation should be (activePeriod.getAdditionalVacationDays()) days.
			if (regularVacationsSum > activeVacationConfiguration.getMaxVacationsDaysPerYear())
			    throw new BusinessException("error_soldiersAdditionalRegularVacPeriod", new Object[] { activeVacationConfiguration.getAdditionalVacationDays() });

			if (regularVacationsSum <= activeVacationConfiguration.getMaxVacationsDaysPerYear()
				&& (regularVacationsSum + request.getPeriod() - twoYearsDifference - previousVacationPeriod) > activeVacationConfiguration.getMaxVacationsDaysPerYear())
			    throw new BusinessException("error_vacationsMaxDaysPerYear", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYear() });
		    }
		}

		if (request.getLocationFlag() == LocationFlagsEnum.EXTERNAL.getCode()) {
		    String[] locationArr = request.getLocation().split("/");
		    if (locationArr.length > ETRConfigurationService.getVacationMaxNumberOfCountriesForSoldiersPerRequest())
			throw new BusinessException("error_soldiersMaxCountriesPerRegVac");
		}

	    }
	    // Check vacation periods for employees.
	    else if (categoryId == CategoriesEnum.PERSONS.getCode() || categoryId == CategoriesEnum.USERS.getCode() || categoryId == CategoriesEnum.WAGES.getCode() || categoryId == CategoriesEnum.MEDICAL_STAFF.getCode()) {

		String[] recruitmentDateArray = vacationBeneficiary.getRecruitmentDateString().split("/");
		String[] birthDateArray = vacationBeneficiary.getBirthDateString().split("/");
		Date curDate = HijriDateService.getHijriSysDate();

		Date serviceLimitDate = HijriDateService.getHijriDate(recruitmentDateArray[0] + "/" + recruitmentDateArray[1] + "/" + (Integer.parseInt(recruitmentDateArray[2]) + ETRConfigurationService.getVacationEmployeesServiceYears()));
		Date ageLimitDate = HijriDateService.getHijriDate(birthDateArray[0] + "/" + birthDateArray[1] + "/" + (Integer.parseInt(birthDateArray[2]) + ETRConfigurationService.getVacationEmployeesAgeYears()));

		boolean isPeriodsExceed = false;
		if (curDate.after(serviceLimitDate) || curDate.after(ageLimitDate))
		    isPeriodsExceed = true;

		if (request.getPeriod() < activeVacationConfiguration.getMinValue()) {

		    String vacationStartYear = HijriDateService.hijriToGregDateString(request.getStartDateString()).split("/")[2];
		    if (!vacationStartYear.equals(HijriDateService.hijriToGregDateString(request.getEndDateString()).split("/")[2]))
			throw new BusinessException("error_vacationStartAndEndDatesInSameYear");

		    int vacDaysLessThanThreshold = sumVacDays(request.getEmpId(), VacationTypesEnum.REGULAR.getCode(), HijriDateService.gregToHijriDateString("01/01/" + vacationStartYear), HijriDateService.gregToHijriDateString("31/12/" + vacationStartYear), activeVacationConfiguration.getMinValue());
		    if (vacDaysLessThanThreshold + request.getPeriod() - (previousVacationPeriod < activeVacationConfiguration.getMinValue() ? previousVacationPeriod : 0) > activeVacationConfiguration.getMinValue()) {
			if (vacDaysLessThanThreshold < activeVacationConfiguration.getMinValue())
			    throw new BusinessException("error_vacRequestModifiedLimits", new Object[] { activeVacationConfiguration.getMinValue() - vacDaysLessThanThreshold, activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });
			else
			    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });
		    }
		}

		if (isPeriodsExceed) {
		    if (request.getPeriod() > activeVacationConfiguration.getMaxValueExceptional())
			throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValueExceptional() });
		} else {
		    if (request.getPeriod() > activeVacationConfiguration.getMaxValue())
			throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });
		}

		// Total vacations per Hijri year are less than or equal activePeriod.getMaxVacationsDaysPerYear().
		if (request.getStatus() == RequestTypesEnum.NEW.getCode() || request.getPeriod() > lastVacation.getPeriod()) {

		    String vacationStartYear = request.getStartDateString().split("/")[2];
		    String vacationEndYear = request.getEndDateString().split("/")[2];
		    int regularVacationsSum = sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), "01/01/" + vacationStartYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(vacationStartYear)).getHijriMonthLength() + "/12/" + vacationStartYear);
		    int twoYearsDifference = 0;
		    if (!vacationStartYear.equals(vacationEndYear))
			twoYearsDifference = HijriDateService.hijriDateStringDiff("01/01/" + vacationEndYear, request.getEndDateString()) + 1;

		    if (isPeriodsExceed) {
			if ((regularVacationsSum + request.getPeriod() - twoYearsDifference - previousVacationPeriod) > activeVacationConfiguration.getMaxVacationsDaysPerYearExceptional())
			    throw new BusinessException("error_vacationsMaxDaysPerYear", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYearExceptional() });
		    } else {
			if ((regularVacationsSum + request.getPeriod() - twoYearsDifference - previousVacationPeriod) > activeVacationConfiguration.getMaxVacationsDaysPerYear())
			    throw new BusinessException("error_vacationsMaxDaysPerYear", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYear() });
		    }
		}
	    } else if (categoryId == CategoriesEnum.CONTRACTORS.getCode()) {
		if (request.getPeriod() < activeVacationConfiguration.getMinValue() || request.getPeriod() > activeVacationConfiguration.getMaxValue()) {
		    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });
		}
	    }

	    if (request.getPeriod() > (calculateEmpRegularBalance(vacationBeneficiary, request.getStartDate(), activeVacationConfiguration, request.getPeriod()) + ((request.getStatus() == RequestTypesEnum.MODIFY.getCode()) ? 1 : 0)))
		throw new BusinessException("error_vacCredit");

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Validates the contractual year for a new or modified vacation for a contractor.
     * 
     * @param empId
     *            a <code>long</code> value containing the beneficiary id of this vacation
     * @param requestType
     *            an <code>int</code> value containing the vacation request type (New / Modify / Cancel)
     * @param vacationTypeId
     *            an <code>int</code> value containing the vacation type (Regular / Compelling / Sick)
     * @param period
     *            an <code>int</code> value containing the requested vacation period
     * @param startDate
     *            a {@link Date} containing the vacation start date
     * @param contractualYearStartDate
     *            a {@link Date} containing the suggested contractual year start date by the reviewer employee
     * @param contractualYearEndDate
     *            a {@link Date} containing the suggested contractual year end date by the reviewer employee
     * @throws BusinessException
     *             <ol>
     *             <li>if the suggested contractual year by the reviewer employee is not a valid contractual year.</li>
     *             <li>if the suggested contractual year by the reviewer employee doesn't enclose the vacation start date or it is not the previous contractual year for the one which enclose the vacation start date.</li>
     *             <li>if the requested vacation period plus the summation of the periods of previous not cancelled vacations at the same suggested contractual year is greater than 30.</li>
     *             </ol>
     * 
     * @see RequestTypesEnum
     * @see CategoriesEnum
     * @see VacationTypesEnum
     */
    protected static void validateContractualYearAndBalance(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	try {
	    if (!(CategoriesEnum.CONTRACTORS.getCode() == vacationBeneficiary.getCategoryId() && VacationTypesEnum.REGULAR.getCode() == request.getVacationTypeId()
		    && (RequestTypesEnum.NEW.getCode() == request.getStatus() || RequestTypesEnum.MODIFY.getCode() == request.getStatus())))
		throw new BusinessException("error_transactionDataError");

	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    if (request.getContractualYearStartDate() == null || request.getContractualYearEndDate() == null || !HijriDateService.isValidHijriDate(request.getContractualYearStartDate()) || !HijriDateService.isValidHijriDate(request.getContractualYearEndDate()))
		throw new BusinessException("error_invalidContractualYearHijriDates");

	    VacationConfiguration activeVacationConfiguration = getActiveRegularVacationConfigurationForContractors(vacationBeneficiary);

	    String[] recruitmentDateArray = vacationBeneficiary.getRecruitmentDateString().split("/");
	    String[] inputContractualYearStartDateArray = request.getContractualYearStartDateString().split("/");

	    Date expectedContractualYearEndDate = HijriDateService.getHijriDate(inputContractualYearStartDateArray[0] + "/" + inputContractualYearStartDateArray[1] + "/" + (Integer.parseInt(inputContractualYearStartDateArray[2]) + activeVacationConfiguration.getBalanceFrame()));
	    expectedContractualYearEndDate = HijriDateService.addSubHijriDays(expectedContractualYearEndDate, -1);

	    if ((!inputContractualYearStartDateArray[0].equals(recruitmentDateArray[0]) || !inputContractualYearStartDateArray[1].equals(recruitmentDateArray[1])))
		throw new BusinessException("error_invalidStartContractualYear", new Object[] { vacationBeneficiary.getRecruitmentDateString() });

	    if (!request.getContractualYearEndDate().equals(expectedContractualYearEndDate))
		throw new BusinessException("error_invalidEndContractualYear", new Object[] { HijriDateService.getHijriDateString(expectedContractualYearEndDate) });

	    String[] vacationContratualYear = calculateContractualYearStartAndEndDates(vacationBeneficiary, request.getStartDateString(), activeVacationConfiguration);

	    if (!vacationContratualYear[0].equals(request.getContractualYearStartDateString()) || !vacationContratualYear[1].equals(request.getContractualYearEndDateString())) {
		String[] vacationContratualYearStartDateArray = vacationContratualYear[0].split("/");
		String vacationPreviousContractualStartDateString = vacationContratualYearStartDateArray[0] + "/" + vacationContratualYearStartDateArray[1] + "/" + (Integer.parseInt(vacationContratualYearStartDateArray[2]) - activeVacationConfiguration.getBalanceFrame());
		Date vacationPreviousContractualEndDate = HijriDateService.addSubHijriDays(HijriDateService.getHijriDate(vacationContratualYear[0]), -1);

		if (!vacationPreviousContractualStartDateString.equals(request.getContractualYearStartDateString()) || !vacationPreviousContractualEndDate.equals(request.getContractualYearEndDate()))
		    throw new BusinessException("error_vacationNotInValidContractualYear");
	    }

	    int vacDays = sumVacDaysWithinContractualYear(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), request.getContractualYearStartDateString(), request.getContractualYearEndDateString());

	    VacationData lastVacation = (request.getStatus() == RequestTypesEnum.MODIFY.getCode()) ? VacationsService.getLastVacationData(vacationBeneficiary.getEmpId(), request.getVacationTypeId()) : new VacationData();

	    int previousVacationPeriod = (request.getStatus() == RequestTypesEnum.NEW.getCode()) ? 0 : lastVacation.getPeriod();

	    if (vacDays + request.getPeriod() - previousVacationPeriod > activeVacationConfiguration.getBalance())
		throw new BusinessException("error_notEnoughVacationBalanceForThisContractualYear", new Object[] { activeVacationConfiguration.getBalance() });

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Applies the compelling vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * <li>The vacations balance should be enough to request a vacation.</li>
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check dates conflict.</li>
     * 
     * <li>Start date and end date should be in the same year.</li>
     * <li>For Contractors, Vacations should not be lower than 5 days and not exceed 90 days.</li>
     * <li>For Soldiers, It's not allowed to choose more than two countries for the external vacations.</li>
     * </ul>
     * 
     * @param empId
     *            the employee ID of the requester who makes a vacation request
     * @param startDateString
     *            a <code>String</code> containing the vacation start hijri date in mm/MM/yyyy format
     * @param endDateString
     *            a <code>String</code> containing the vacation end hijri date in mm/MM/yyyy format
     * @param period
     *            an <code>int</code> value containing the vacation period in days
     * @param location
     *            a <code>String</code> containing the vacation location which represents the country (or countries) which the employee will spend the vacation in
     * @param locationFlag
     *            the flag indicates whether the vacation is (0) internal or (1) external
     * @throws BusinessException
     *             if any business rule is broken or any general error occurs
     */
    protected static void validateCompellingVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	try {

	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    // Check vacation periods.
	    if (vacationBeneficiary.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		String vacationStartYear = request.getStartDateString().split("/")[2];
		if (!vacationStartYear.equals(request.getEndDateString().split("/")[2]))
		    throw new BusinessException("error_vacationStartAndEndDatesInSameYear");
	    } else if (vacationBeneficiary.getCategoryId() == CategoriesEnum.PERSONS.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.USERS.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.WAGES.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()) {
		String vacationStartYear = HijriDateService.hijriToGregDateString(request.getStartDateString()).split("/")[2];
		if (!vacationStartYear.equals(HijriDateService.hijriToGregDateString(request.getEndDateString()).split("/")[2]))
		    throw new BusinessException("error_vacationStartAndEndDatesInSameYear");

		if ((int) calculateEmpRegularBalance(vacationBeneficiary, request.getStartDate(), null, -1) > 0)
		    throw new BusinessException("error_regularVacationBalanceIsNotZero");
	    }

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    if (vacationBeneficiary.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode()) {
		if (request.getPeriod() < activeVacationConfiguration.getMinValue() || request.getPeriod() > activeVacationConfiguration.getMaxValue()) {
		    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });
		}
	    }

	    if ((vacationBeneficiary.getCategoryId() == CategoriesEnum.SOLDIERS.getCode())
		    && (request.getLocationFlag() == LocationFlagsEnum.EXTERNAL.getCode())) {
		String[] locationArr = request.getLocation().split("/");
		if (locationArr.length > ETRConfigurationService.getVacationMaxNumberOfCountriesForSoldiersPerRequest())
		    throw new BusinessException("error_soldiersMaxCountriesPerRegVac");
	    }

	    if (request.getPeriod() < activeVacationConfiguration.getMinValue())
		throw new BusinessException("error_vacationMinValue", new Object[] { activeVacationConfiguration.getMinValue() });

	    if (request.getPeriod() > calculateEmpCompellingBalance(vacationBeneficiary, request.getStartDate(), activeVacationConfiguration))
		throw new BusinessException("error_vacCredit");

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Applies the sick vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * <li>The vacations balance should be enough to request a vacation.</li>
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check dates conflict.</li>
     * 
     * <li>For Officers, External vacation should be greater than or equal 15 days.</li>
     * 
     * <li>If an internal vacation has been changed to be external vacation, check the following rules:
     * <ul>
     * <li>For Officers, External vacation should be greater than or equal 15 days.</li>
     * <li>Extended external period shouldn't be equal to the vacation period.</li>
     * <li>Extended external period shouldn't be out the total period.</li>
     * </ul>
     * </li>
     * </ul>
     * 
     * @param empId
     *            the employee ID of the requester who makes a vacation request
     * @param startDateString
     *            a <code>String</code> containing the vacation start hijri date in mm/MM/yyyy format
     * @param endDateString
     *            a <code>String</code> containing the vacation end hijri date in mm/MM/yyyy format
     * @param requestType
     *            the vacation request type which is (1) New, (2) Modify or (4) Cancel vacation
     * @param period
     *            an <code>int</code> value containing the vacation period in days
     * @param locationFlag
     *            the flag indicates whether the vacation is (0) internal, (1) external or (2) internal external
     * @param extStartDateStringIfAny
     *            a <code>String</code> containing the external vacation start hijri date in mm/MM/yyyy format in case if the vacation is Internal_External
     * @param extEndDateStringIfAny
     *            a <code>String</code> containing the external vacation end hijri date in mm/MM/yyyy format in case if the vacation is Internal_External
     * @throws BusinessException
     *             if any business rule is broken or any general error occurs
     */
    protected static void validateSickVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    // work injury sick vacation is allowed only for the soldiers
	    if (request.getSubVacationType() == SubVacationTypesEnum.SUB_TYPE_TWO.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.SOLDIERS.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    // Get last sick vacation.
	    VacationData lastVacation = VacationsService.getLastVacationData(vacationBeneficiary.getEmpId(), request.getVacationTypeId(), request.getSubVacationType());
	    if (lastVacation == null)
		lastVacation = new VacationData();

	    List<VacationConfiguration> activeVacationConfigurations = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId(), request.getSubVacationType(), FlagsEnum.ALL.getCode());
	    VacationConfiguration activeVacationConfiguration = activeVacationConfigurations.get(0);

	    // Check vacation periods.
	    String sickBalanceString = calculateEmpSickBalance(vacationBeneficiary, request.getSubVacationType(), request.getStartDate(), activeVacationConfigurations);

	    int sickBalance = -1;
	    int sickBalanceType = -1;
	    if (sickBalanceString != null) {
		sickBalance = Integer.parseInt(sickBalanceString.split(",")[0]);
		sickBalanceType = Integer.parseInt(sickBalanceString.split(",")[1]);
	    }

	    int previousVacationPeriod = request.getStatus() == RequestTypesEnum.NEW.getCode() ? 0 : lastVacation.getPeriod();

	    if (request.getPeriod() < activeVacationConfiguration.getMinValue())
		throw new BusinessException("error_vacationMinValue", new Object[] { activeVacationConfiguration.getMinValue() });

	    if (((sickBalance == -1) && (request.getStatus() == RequestTypesEnum.NEW.getCode()))
		    || ((request.getPeriod() - previousVacationPeriod) > sickBalance)
		    || ((request.getStatus() == RequestTypesEnum.MODIFY.getCode())
			    && (lastVacation != null)
			    && (request.getPeriod() > lastVacation.getPeriod())
			    && ((sickBalance == -1) || (lastVacation.getPaidVacationType().intValue() != sickBalanceType))))
		throw new BusinessException("error_vacCredit");

	    // validate if the vacation is between 2 frames
	    Date frameStartDate = null, frameEndDate = request.getStartDate();
	    if (CategoriesEnum.SOLDIERS.getCode() == vacationBeneficiary.getCategoryId() || CategoriesEnum.CONTRACTORS.getCode() == vacationBeneficiary.getCategoryId()) {
		frameEndDate = vacationBeneficiary.getRecruitmentDate();
	    } else {
		Vacation firstSickVacation = getFirstVacation(vacationBeneficiary.getEmpId(), VacationTypesEnum.SICK.getCode(), request.getSubVacationType());
		if (firstSickVacation != null && firstSickVacation.getStartDate().before(request.getStartDate()))
		    frameEndDate = firstSickVacation.getStartDate();
	    }

	    frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    do {
		frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
		frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/" + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfigurations.get(0).getBalanceFrame()));
		frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    } while (!HijriDateService.isDateBetween(frameStartDate, frameEndDate, request.getStartDate()));

	    if (request.getEndDate().after(frameEndDate)) {
		Integer periodAtCurrentFrame = HijriDateService.hijriDateDiff(request.getStartDate(), frameEndDate) + 1;
		Date nextFrameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		Integer periodAtNextFrame = HijriDateService.hijriDateDiff(nextFrameStartDate, request.getEndDate()) + 1;

		throw new BusinessException("error_sickVacationBetweenTwoFrames", new Object[] { request.getPeriod(), request.getStartDateString(), periodAtCurrentFrame, HijriDateService.getHijriDateString(frameEndDate), HijriDateService.getHijriDateString(nextFrameStartDate), periodAtNextFrame, request.getEndDateString() });
	    }
	    // external vacation should be greater than or equal 15 days for officers only.
	    if (vacationBeneficiary.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())
		    && request.getLocationFlag() == LocationFlagsEnum.EXTERNAL.getCode()
		    && request.getPeriod() < activeVacationConfiguration.getExternalMinVacationDays())
		throw new BusinessException("error_sickVacationExternalPeriodLimit", new Object[] { activeVacationConfiguration.getExternalMinVacationDays() });

	    // internal vacation has been changed to be external vacation.
	    if (request.getLocationFlag() == LocationFlagsEnum.INTERNAL_EXTERNAL.getCode()) {

		int extendedExternalPeriod = HijriDateService.hijriDateDiff(request.getExtStartDate(), request.getExtEndDate()) + 1;
		// external vacation should be greater than or equal 15 days for officers only.
		if ((vacationBeneficiary.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) || vacationBeneficiary.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode()))
			&& extendedExternalPeriod < activeVacationConfiguration.getExternalMinVacationDays())
		    throw new BusinessException("error_sickVacationExternalPeriodLimit", new Object[] { activeVacationConfiguration.getExternalMinVacationDays() });

		// extended external period shouldn't be equal to the vacation period
		if (extendedExternalPeriod == request.getPeriod())
		    throw new BusinessException("error_sickVacationExternalPeriodEqualTotal");

		// extended external period shouldn't be out the total period
		if ((!HijriDateService.isDateBetween(request.getStartDate(), request.getEndDate(), request.getExtStartDate()))
			|| (!HijriDateService.isDateBetween(request.getStartDate(), request.getEndDate(), request.getExtEndDate())))
		    throw new BusinessException("error_sickVacationExternalPeriodOutOriginalPeriod");
	    }
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Applies the Field vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * <li>The vacations balance should be enough to request a vacation.</li>
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check dates conflict.</li>
     * 
     * <li>Physical unit for any field vacation Beneficiary must be a field unit and be in GENERAL_DIRECTORATE_OF_BORDER_GUARDS
     * 
     * <li>For Officers, Vacation should be 15 or 30 days with 60 days maximum in each hijiri year</li>
     * <li>For Soldiers, Vacation should be 15,30,45 days with 45 days maximum in each hijiri year</li>
     * 
     * 
     * @param request
     *            vacation request data
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateFieldVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.OFFICERS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.SOLDIERS.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    if (vacationBeneficiary.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() && request.getLocationFlag().intValue() != LocationFlagsEnum.INTERNAL.getCode())
		throw new BusinessException("error_vacIsInternalOnly");

	    if (!UnitsService.isFieldUnit(vacationBeneficiary.getPhysicalUnitHKey()))
		throw new BusinessException("error_fieldVacationIsAllowedForEmployeesAtFieldUnitOnly");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    // Check vacation periods.
	    if (!("," + activeVacationConfiguration.getAllowedValues() + ",").contains("," + request.getPeriod() + ","))
		throw new BusinessException("error_vacationPeriodAllowedValues", new Object[] { activeVacationConfiguration.getAllowedValues().replaceAll(",", " , ") });

	    if (request.getPeriod() > calculateEmpFieldBalance(vacationBeneficiary, request.getStartDate(), activeVacationConfiguration))
		throw new BusinessException("error_vacCredit");

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static void validateExceptionalVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // Vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    // exceptional vacation is not allowed for the contractors
	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.OFFICERS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId(), request.getSubVacationType(), FlagsEnum.ALL.getCode()).get(0);

	    if (vacationBeneficiary.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		// Check the period before first vacation
		if (request.getStatus() == RequestTypesEnum.NEW.getCode()) {
		    Date correctDate = HijriDateService.addSubHijriDays(vacationBeneficiary.getRecruitmentDate(), activeVacationConfiguration.getPeriodBeforeFirstVacation() - 1, true);
		    if (request.getStartDate().before(correctDate))
			throw new BusinessException("error_vacWorkStartDate", new Object[] { activeVacationConfiguration.getPeriodBeforeFirstVacation() / FULL_HIJRI_MONTH_DAYS });
		}

		// Check regular vacation sum before requesting an exceptional vacation
		String vacationStartYear = request.getStartDateString().split("/")[2];
		int regularVacationsSum = sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), "01/01/" + vacationStartYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(vacationStartYear)).getHijriMonthLength() + "/12/" + vacationStartYear);
		int regularBalance = (int) calculateEmpRegularBalance(vacationBeneficiary, request.getStartDate(), null, -1);
		VacationConfiguration regularActiveVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), VacationTypesEnum.REGULAR.getCode()).get(0);

		if (regularBalance >= regularActiveVacationConfiguration.getMinValue()
			&& regularVacationsSum < regularActiveVacationConfiguration.getMaxVacationsDaysPerYear()) {
		    if ((regularBalance + regularVacationsSum) < regularActiveVacationConfiguration.getMaxVacationsDaysPerYear())
			throw new BusinessException("error_regularVacationBalanceBeforeExceptionalVacationForMilitaries", new Object[] { regularActiveVacationConfiguration.getMinValue() });
		    else
			throw new BusinessException("error_regularVacationSumBeforeExceptionalVacation", new Object[] { regularActiveVacationConfiguration.getMaxVacationsDaysPerYear() });
		}
	    } else {
		// Check regular vacation balance before requesting an exceptional for circumstances vacation
		if (request.getSubVacationType() == SubVacationTypesEnum.SUB_TYPE_ONE.getCode()) {
		    int regularBalance = (int) calculateEmpRegularBalance(vacationBeneficiary, request.getStartDate(), null, -1);
		    VacationConfiguration regularActiveVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), VacationTypesEnum.REGULAR.getCode()).get(0);

		    if (regularBalance >= regularActiveVacationConfiguration.getMinValue())
			throw new BusinessException("error_regularVacationBalanceBeforeExceptionalVacation", new Object[] { regularActiveVacationConfiguration.getMinValue() });
		}
	    }

	    // Check vacation periods
	    if (request.getPeriod() < activeVacationConfiguration.getMinValue() || request.getPeriod() > activeVacationConfiguration.getMaxValue())
		throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });

	    // Check vacation balance
	    if (request.getPeriod() > calculateEmpExceptionalBalance(vacationBeneficiary, request.getSubVacationType(), request.getStartDate(), activeVacationConfiguration))
		throw new BusinessException("error_vacCredit");

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * 
     * Validate old employee id cause there is no calculate balance method
     * 
     * Applies the Study vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * 
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check dates conflict.</li>
     * <li>Check the beneficiary recruitment must from 3 years ago</li>
     * 
     * <li>
     * 
     * @param request
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateStudyVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	// Validate Old Emp Id cause there is no calculate balance method
	validateOldEmpId(vacationBeneficiary.getOldEmpId());

	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // check the recruitment date cause there is no calculate balance method
	    if (request.getStartDate().before(vacationBeneficiary.getRecruitmentDate()))
		throw new BusinessException("error_vacationStartDateBeforeEmpRecruitmentDate");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    // Check the period before first vacation
	    if (request.getStatus() == RequestTypesEnum.NEW.getCode()) {
		Date correctDate = HijriDateService.addSubHijriDays(vacationBeneficiary.getRecruitmentDate(), activeVacationConfiguration.getPeriodBeforeFirstVacation() - 1, true);
		if (request.getStartDate().before(correctDate))
		    throw new BusinessException("error_vacWorkStartDate", new Object[] { activeVacationConfiguration.getPeriodBeforeFirstVacation() / FULL_HIJRI_MONTH_DAYS });
	    }

	    // Check vacation periods
	    if (request.getPeriod() < activeVacationConfiguration.getMinValue() || request.getPeriod() > activeVacationConfiguration.getMaxValue())
		throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * 
     * Validate old employee id cause there is no calculate balance method
     * 
     * Applies the Exam vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * 
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check dates conflict.</li>
     * 
     * <li>
     * 
     * @param request
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateExamVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	// Validate Old Emp Id cause there is no calculate balance method
	validateOldEmpId(vacationBeneficiary.getOldEmpId());

	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // check the recruitment date cause there is no calculate balance method
	    if (request.getStartDate().before(vacationBeneficiary.getRecruitmentDate()))
		throw new BusinessException("error_vacationStartDateBeforeEmpRecruitmentDate");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    if (vacationBeneficiary.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() && request.getLocationFlag().intValue() != LocationFlagsEnum.INTERNAL.getCode())
		throw new BusinessException("error_vacIsInternalOnly");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    // Check vacation periods
	    if (request.getPeriod() < activeVacationConfiguration.getMinValue() || request.getPeriod() > activeVacationConfiguration.getMaxValue())
		throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static void validateAccompanyVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    List<VacationConfiguration> activeVacationConfigurations = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId());

	    // Check vacation periods
	    if (request.getPeriod() < activeVacationConfigurations.get(0).getMinValue())
		throw new BusinessException("error_vacationMinValue", new Object[] { activeVacationConfigurations.get(0).getMinValue() });

	    int regularBalance = (int) calculateEmpRegularBalance(vacationBeneficiary, request.getStartDate(), null, -1);

	    if (request.getPeriod() > regularBalance) {
		String accompanyBalanceString = calculateEmpAccompanyBalance(vacationBeneficiary, request.getStartDate(), activeVacationConfigurations);
		int accompanyBalance = -1;
		int accompanyBalanceType = -1;
		if (accompanyBalanceString != null) {
		    accompanyBalance = Integer.parseInt(accompanyBalanceString.split(",")[0]);
		    accompanyBalanceType = Integer.parseInt(accompanyBalanceString.split(",")[1]);
		}

		if (accompanyBalanceType == -1)
		    throw new BusinessException("error_vacCredit");

		if (accompanyBalanceType == PaidVacationTypesEnum.FULL_PAID.getCode()) {
		    if (request.getPeriod() > (accompanyBalance + regularBalance))
			throw new BusinessException("error_vacCredit");
		} else {
		    VacationConfiguration regularActiveVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), VacationTypesEnum.REGULAR.getCode()).get(0);

		    if (regularBalance < regularActiveVacationConfiguration.getMinValue()) {
			if (regularBalance == 0 && request.getPeriod() > activeVacationConfigurations.get(0).getMaxValue())
			    throw new BusinessException("error_vacationMaxValue", new Object[] { activeVacationConfigurations.get(0).getMaxValue() });
			if (request.getPeriod() > Math.max(regularBalance, accompanyBalance))
			    throw new BusinessException("error_vacCredit");
		    } else {
			if (request.getPeriod() > regularBalance)
			    throw new BusinessException("error_accompanyVacationDifferentPaidTypes");
		    }
		}
	    }
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * 
     * 
     * Applies the Relief vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * 
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check dates conflict.</li>
     * <li>Employee has 45 days per year and not shifted to the next year</li>
     * <li>
     * 
     * @param request
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateReliefVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    if (request.getPeriod() < activeVacationConfiguration.getMinValue())
		throw new BusinessException("error_vacationMinValue", new Object[] { activeVacationConfiguration.getMinValue() });

	    if (request.getPeriod() > calculateEmpReliefBalance(vacationBeneficiary, request.getStartDate(), activeVacationConfiguration))
		throw new BusinessException("error_vacCredit");

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * 
     * 
     * Applies the Sportive , Culture and Social vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * 
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check dates conflict.</li>
     * <li>
     * 
     * @param request
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateSportiveCultureSocialVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId(), FlagsEnum.ALL.getCode(), request.getLocationFlag()).get(0);

	    if (request.getPeriod() < activeVacationConfiguration.getMinValue())
		throw new BusinessException("error_vacationMinValue", new Object[] { activeVacationConfiguration.getMinValue() });

	    if (request.getPeriod() > calculateSportiveCultureSocialBalance(vacationBeneficiary, request.getVacationTypeId(), request.getLocationFlag(), request.getStartDate(), activeVacationConfiguration))
		throw new BusinessException("error_vacCredit");

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * 
     * Validate old employee id cause there is no calculate balance method
     * 
     * Applies the COMPENSATION vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * 
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check dates conflict.</li>
     * 
     * <li>
     * 
     * @param request
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateCompensationVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	// Validate Old Emp Id cause there is no calculate balance method
	validateOldEmpId(vacationBeneficiary.getOldEmpId());

	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // check the recruitment date cause there is no calculate balance method
	    if (request.getStartDate().before(vacationBeneficiary.getRecruitmentDate()))
		throw new BusinessException("error_vacationStartDateBeforeEmpRecruitmentDate");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    // Check vacation periods
	    if (request.getPeriod() < activeVacationConfiguration.getMinValue() || request.getPeriod() > activeVacationConfiguration.getMaxValue())
		throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Applies the Maternity vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * 
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Vacation Allowed only for Females</li>
     * <li>Check dates conflict.</li>
     * 
     * <li>
     * 
     * <li>For Soldiers, Vacation should be 40,60 days with 60 days maximum in each new baby</li>
     * <li>For Employees, Vacation should be 60 days with 60 days maximum in each new baby</li>
     * 
     * 
     * @param request
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateMaternityVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	// Validate Old Emp Id cause there is no calculate balance method
	validateOldEmpId(vacationBeneficiary.getOldEmpId());

	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // check the recruitment date cause there is no calculate balance method
	    if (request.getStartDate().before(vacationBeneficiary.getRecruitmentDate()))
		throw new BusinessException("error_vacationStartDateBeforeEmpRecruitmentDate");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    if (!vacationBeneficiary.getGender().equals(GendersEnum.FEMALE.getCode()))
		throw new BusinessException("error_vacAllowedOnlyForFemales");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    // Check vacation periods
	    if (vacationBeneficiary.getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode()) {
		if (request.getPeriod() < activeVacationConfiguration.getMinValue() || request.getPeriod() > activeVacationConfiguration.getMaxValue())
		    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });

	    } else if (vacationBeneficiary.getCategoryId() == CategoriesEnum.PERSONS.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.USERS.getCode()
		    || vacationBeneficiary.getCategoryId() == CategoriesEnum.WAGES.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()) {

		if (!("," + activeVacationConfiguration.getAllowedValues() + ",").contains("," + request.getPeriod() + ","))
		    throw new BusinessException("error_vacationPeriodAllowedValues", new Object[] { activeVacationConfiguration.getAllowedValues().replaceAll(",", " , ") });
	    }

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * 
     * Applies the Motherhood vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * 
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check dates conflict.</li>
     * <li>this vacation only for females</li>
     * <li>Employee has 3 years per service period</li>
     * <li>
     * 
     * @param request
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateMotherhoodVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    if (!vacationBeneficiary.getGender().equals(GendersEnum.FEMALE.getCode()))
		throw new BusinessException("error_vacAllowedOnlyForFemales");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    if (request.getPeriod() < activeVacationConfiguration.getMinValue())
		throw new BusinessException("error_vacationMinValue", new Object[] { activeVacationConfiguration.getMinValue() });

	    if (request.getPeriod() > calculateEmpMotherhoodBalance(vacationBeneficiary, request.getStartDate(), activeVacationConfiguration))
		throw new BusinessException("error_vacCredit");

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * 
     * 
     * Applies the Orphan care vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * 
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check dates conflict.</li>
     * <li>this vacation only for females</li>
     * <li>Employee has 3 years per service period</li>
     * <li>
     * 
     * @param request
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateOrphanCareVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    if (!vacationBeneficiary.getGender().equals(GendersEnum.FEMALE.getCode()))
		throw new BusinessException("error_vacAllowedOnlyForFemales");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    if (request.getPeriod() < activeVacationConfiguration.getMinValue())
		throw new BusinessException("error_vacationMinValue", new Object[] { activeVacationConfiguration.getMinValue() });

	    if (request.getPeriod() > calculateEmpOrphanCareBalance(vacationBeneficiary, request.getStartDate(), activeVacationConfiguration))
		throw new BusinessException("error_vacCredit");

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Applies the DEATH_WAITING_PERIOD vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * 
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Vacation Allowed only for Females</li>
     * <li>Check dates conflict.</li>
     * 
     * <li>
     * 
     * <li>For Soldiers, Vacation should be 130 days</li>
     * <li>For Employees, Vacation should be 1 day to year</li>
     * 
     * 
     * @param request
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateDeathWaitingPeriodVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	// Validate Old Emp Id cause there is no calculate balance method
	validateOldEmpId(vacationBeneficiary.getOldEmpId());

	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // check the recruitment date cause there is no calculate balance method
	    if (request.getStartDate().before(vacationBeneficiary.getRecruitmentDate()))
		throw new BusinessException("error_vacationStartDateBeforeEmpRecruitmentDate");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.SOLDIERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    if (!vacationBeneficiary.getGender().equals(GendersEnum.FEMALE.getCode()))
		throw new BusinessException("error_vacAllowedOnlyForFemales");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    // Check vacation periods for Soldiers
	    if (vacationBeneficiary.getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode()) {
		if (!("," + activeVacationConfiguration.getAllowedValues() + ",").contains("," + request.getPeriod() + ","))
		    throw new BusinessException("error_vacationPeriodAllowedValues", new Object[] { activeVacationConfiguration.getAllowedValues().replaceAll(",", " , ") });

	    } else if (vacationBeneficiary.getCategoryId() == CategoriesEnum.PERSONS.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.USERS.getCode()
		    || vacationBeneficiary.getCategoryId() == CategoriesEnum.WAGES.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()) {

		if (request.getPeriod() < activeVacationConfiguration.getMinValue() || request.getPeriod() > activeVacationConfiguration.getMaxValue())
		    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });
	    }

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Applies the death for Relative vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * 
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Check dates conflict.</li>
     * 
     * <li>
     * 
     * <li>For Employees, Vacation should be 1 to 3 days with 3 days maximum in Death</li>
     * 
     * 
     * @param request
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateDeathOfRelativeVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	// Validate Old Emp Id cause there is no calculate balance method
	validateOldEmpId(vacationBeneficiary.getOldEmpId());

	try {

	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // check the recruitment date cause there is no calculate balance method
	    if (request.getStartDate().before(vacationBeneficiary.getRecruitmentDate()))
		throw new BusinessException("error_vacationStartDateBeforeEmpRecruitmentDate");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    if (request.getPeriod() < activeVacationConfiguration.getMinValue() || request.getPeriod() > activeVacationConfiguration.getMaxValue())
		throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Applies the New Baby vacation business rules before handling the request.
     * 
     * The Business Rules are:
     * <ul>
     * 
     * <li>The beneficiary should has recruitment date.</li>
     * <li>Vacation start date must not exceed today plus 90 days.</li>
     * <li>Vacation Allowed only for Males</li>
     * <li>Check dates conflict.</li>
     * 
     * <li>
     * 
     * <li>For Employees, Vacation should be 1 day with 1 day maximum in each new baby</li>
     * 
     * 
     * @param request
     * @param vacationBeneficiary
     * @throws BusinessException
     */
    protected static void validateNewBabyVacationRules(Vacation request, EmployeeData vacationBeneficiary) throws BusinessException {
	// Validate Old Emp Id cause there is no calculate balance method
	validateOldEmpId(vacationBeneficiary.getOldEmpId());

	try {

	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // check the recruitment date cause there is no calculate balance method
	    if (request.getStartDate().before(vacationBeneficiary.getRecruitmentDate()))
		throw new BusinessException("error_vacationStartDateBeforeEmpRecruitmentDate");

	    // vacation start date must not exceed today plus 90 days
	    if (request.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.PERSONS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.USERS.getCode()
		    && vacationBeneficiary.getCategoryId() != CategoriesEnum.WAGES.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.MEDICAL_STAFF.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    if (!vacationBeneficiary.getGender().equals(GendersEnum.MALE.getCode()))
		throw new BusinessException("error_vacAllowedOnlyForMales");

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), request.getVacationTypeId()).get(0);

	    // Check vacation periods.
	    if (!("," + activeVacationConfiguration.getAllowedValues() + ",").contains("," + request.getPeriod() + ","))
		throw new BusinessException("error_vacationPeriodAllowedValues", new Object[] { activeVacationConfiguration.getAllowedValues().replaceAll(",", " , ") });

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static void validateVacationDates(Vacation request) throws BusinessException {
	if (request.getStatus().equals(RequestTypesEnum.NEW.getCode()) && !HijriDateService.isValidHijriDate(request.getStartDate()))
	    throw new BusinessException("error_invalidHijriDate");

	if (request.getExtStartDate() != null && !HijriDateService.isValidHijriDate(request.getExtStartDate()))
	    throw new BusinessException("error_invalidHijriDate");
    }

    protected static void validateVacationLocation(Vacation request) throws BusinessException {
	if (request.getStatus() == RequestTypesEnum.NEW.getCode()
		&& request.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode())
		&& request.getLocation().contains(getMessage("label_ksa")))
	    throw new BusinessException("error_invalidLocation");

	if (request.getVacationTypeId() == VacationTypesEnum.SICK.getCode()
		&& request.getStatus() == RequestTypesEnum.MODIFY.getCode()
		&& request.getLocationFlag().equals(LocationFlagsEnum.INTERNAL_EXTERNAL.getCode())
		&& request.getExtLocation().contains(getMessage("label_ksa")))
	    throw new BusinessException("error_invalidLocation");
    }

    /**
     * Validates the vacation to check if it's electronic or not where it's not allowed to Modify or Cancel a vacation if it is not electronic.
     * 
     * @param requestType
     *            an <code>Integer</code> containing the vacation request type which is New, Modify, or Cancel vacation
     * @param oldVacationId
     *            a <code>Long</code> containing the vacation ID which will be modified or cancelled but it needs to check it it's an electronic vacation or not
     * @throws BusinessException
     *             if the request type is modify or cancel and the vacation is not electronic and
     * 
     * @see RequestTypesEnum
     */
    protected static void validateModifyAndCancelEVacation(Long oldVacationId, Integer status, Integer skipEVacationValidation) throws BusinessException {
	Vacation oldVac = VacationsService.getVacationById(oldVacationId);
	if (oldVac == null)
	    throw new BusinessException("error_transactionDataError");
	if (skipEVacationValidation == FlagsEnum.OFF.getCode()) {
	    if (oldVac.getEtrFlag() != FlagsEnum.ON.getCode())
		throw new BusinessException("error_cannotModifyOrCancelNotElectronicVacation");
	}
	if (oldVac.getJoiningDate() != null && status == RequestTypesEnum.MODIFY.getCode())
	    throw new BusinessException("error_modifyVacAfterJoining");
	if (oldVac.getJoiningDate() != null && status == RequestTypesEnum.CANCEL.getCode())
	    throw new BusinessException("error_cancelVacAfterJoining");
    }

    /**
     * Validates the employee old ID to check if it exists or not where it's not allowed to calculate the employee's balance if this employee is not found at the old database.
     * 
     * @param oldEmpId
     *            a <code>String</code> containing the old emp ID to check if this employee is found in the old database or not.
     * @throws BusinessException
     *             if this employee is not found in the old database
     */
    public static void validateOldEmpId(String oldEmpId) throws BusinessException {
	if (oldEmpId == null || oldEmpId.isEmpty()) {
	    throw new BusinessException("error_userNotFoundInOldDB");
	}
    }

    protected static void validatePreviousVacationJoining(long vacationBeneficiaryId, long beneficiaryCategoryId, Date vacationStartDate) throws BusinessException {
	if (!isValidPreviousVacationJoiningDate(vacationBeneficiaryId, beneficiaryCategoryId, vacationStartDate))
	    throw new BusinessException("error_previousVacationWithoutJoiningDate");
    }

    protected static void validateCurrentAndFutureVacation(long vacationBeneficiaryId) throws BusinessException {
	VacationData lastFutureVacationData = getLastVacationAfterCurrentDate(vacationBeneficiaryId);
	if (lastFutureVacationData != null)
	    throw new BusinessException("error_futureVacationExists");
    }

    protected static void validateVacationJoiningData(boolean validateDataOnlyFlag, Long empId, Long vacationTypeId, Integer subVacationType, String endDateString, Integer exceededDays) throws BusinessException {
	if (exceededDays == null || exceededDays < 0)
	    throw new BusinessException("error_exceededDaysPositive");

	// if (!validateDataOnlyFlag && countLaterVacationsHasJoiningDate(empId, endDateString, vacationTypeId, subVacationType) > 0)
	// throw new BusinessException("error_laterVacationHasJoiningDate");
    }

    /*---------------------------------------------------------- Calculate Balances --------------------------------------------*/

    /**
     * Calculates the total regular vacation balance based on Employee start date is before balanceToDate. Calculation is based on (serviced days - deducted days) without taken vacations
     * 
     * @param emp
     *            the {@link EmployeeData} object which contains the beneficiary data to calculate his vacation balance
     * @param balanceToDate
     *            the <code>Date</code> to calculate the balance till it in mm/MM/yyyy format
     * @param period
     *            the new vacation period to check if the balance will allow the employee to take this new vacation or not
     * @param calculateDueBalance
     *            true when the caller needs the total balance without deducting any vacations taken and false when the caller needs the net balance. Cannot be applied for contractors
     * @return the employee balance
     * @throws BusinessException
     *             if any error occurs
     */
    protected static float calculateEmpRegularBalance(EmployeeData emp, Date balanceToDate, VacationConfiguration activeVacationConfiguration, int period) throws BusinessException {
	validateOldEmpId(emp.getOldEmpId());

	float balance = 0.0F;
	try {
	    balanceToDate = HijriDateService.floorInvalidHijriDateToValidHijriDate(balanceToDate);

	    // balance to date before recruitment date so the balance will be 0.
	    if (emp.getRecruitmentDate() == null || balanceToDate.before(emp.getRecruitmentDate()))
		return balance;

	    if (emp.getCategoryId().equals(CategoriesEnum.CONTRACTORS.getCode())) {

		if (activeVacationConfiguration == null)
		    activeVacationConfiguration = getActiveRegularVacationConfigurationForContractors(emp);

		int contractorBalancePerContractPeriod = activeVacationConfiguration.getBalance();

		// frameInfo String structure :
		// frame start date,
		// frame end date,
		// frame balance,
		// carry from previous frame,
		// sum of vacation days this frame,
		// consumed from the balance at the next frame
		List<String> framesInfo = new ArrayList<String>();
		// the index of the contract period that contains the balance to date.
		int targetIndex = 0;
		boolean useTwoContractPeriodsBalance = false;
		// Get last regular vacation.
		VacationData lastVacation = VacationsService.getLastVacationData(emp.getEmpId(), VacationTypesEnum.REGULAR.getCode());

		Date stoppingDate = null;
		if (period == -1) {
		    // stopping date = max(balance to date, last vacation end date)
		    if (lastVacation == null)
			stoppingDate = balanceToDate;
		    else
			stoppingDate = balanceToDate.before(lastVacation.getEndDate()) ? lastVacation.getEndDate() : balanceToDate;
		} else {
		    stoppingDate = HijriDateService.addSubHijriDays(balanceToDate, period);
		    if (lastVacation != null)
			stoppingDate = stoppingDate.before(lastVacation.getEndDate()) ? lastVacation.getEndDate() : stoppingDate;
		}

		Date frameStartDate = null, frameEndDate = HijriDateService.addSubHijriDays(emp.getRecruitmentDate(), -1);
		int frameBalance = 0;
		int carryFromPreviousFrame = 0;
		int sumVacDays = 0;
		int consumedAtNextFrame = 0;
		String frameInfo;
		String[] frameInfoArray;

		int previousFrameBalance = 0;
		int previousCarryFromPreviousFrame = 0;
		int previousSumVacDays = 0;
		int previousConsumedAtNextFrame = 0;
		String previousFrameStartDateString;
		String previousFrameEndDateString;
		String previousFrameInfo;
		String[] previousFrameInfoArray;

		int index = 0;

		do {

		    frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		    String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
		    frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/" + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfiguration.getBalanceFrame()));
		    frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);

		    frameBalance = contractorBalancePerContractPeriod - (deductedDays(emp, frameStartDate, frameEndDate) * contractorBalancePerContractPeriod / BALANCE_YEAR_DAYS);
		    sumVacDays = sumVacDays(emp.getEmpId(), VacationTypesEnum.REGULAR.getCode(), HijriDateService.getHijriDateString(frameStartDate), HijriDateService.getHijriDateString(frameEndDate));

		    if (index > 0) {
			previousFrameInfo = framesInfo.get(index - 1);
			previousFrameInfoArray = previousFrameInfo.split(",");
			previousFrameStartDateString = previousFrameInfoArray[0];
			previousFrameEndDateString = previousFrameInfoArray[1];
			previousFrameBalance = Integer.parseInt(previousFrameInfoArray[2]);
			previousCarryFromPreviousFrame = Integer.parseInt(previousFrameInfoArray[3]);
			previousSumVacDays = Integer.parseInt(previousFrameInfoArray[4]);

			if (previousSumVacDays <= previousCarryFromPreviousFrame)
			    carryFromPreviousFrame = previousFrameBalance;
			else
			    carryFromPreviousFrame = Math.min(contractorBalancePerContractPeriod, Math.max(0, (previousFrameBalance - (previousSumVacDays - previousCarryFromPreviousFrame))));

			if (sumVacDays < carryFromPreviousFrame)
			    previousConsumedAtNextFrame = sumVacDays;
			else
			    previousConsumedAtNextFrame = carryFromPreviousFrame;

			framesInfo.set(index - 1, previousFrameStartDateString + "," + previousFrameEndDateString + "," + String.valueOf(previousFrameBalance) + "," + String.valueOf(previousCarryFromPreviousFrame) + "," + String.valueOf(previousSumVacDays) + "," + String.valueOf(previousConsumedAtNextFrame));
		    }

		    frameInfo = HijriDateService.getHijriDateString(frameStartDate) + "," + HijriDateService.getHijriDateString(frameEndDate) + "," + String.valueOf(frameBalance) + "," + String.valueOf(carryFromPreviousFrame) + "," + String.valueOf(sumVacDays) + "," + String.valueOf(consumedAtNextFrame);
		    framesInfo.add(frameInfo);

		    if (HijriDateService.isDateBetween(frameStartDate, frameEndDate, balanceToDate)) {
			targetIndex = index;
			if ((period != -1) && HijriDateService.addSubHijriDays(balanceToDate, period).after(frameEndDate))
			    useTwoContractPeriodsBalance = true;
		    }

		    index++;

		} while (!frameStartDate.after(stoppingDate));

		if (!useTwoContractPeriodsBalance) {
		    frameInfo = framesInfo.get(targetIndex);
		    frameInfoArray = frameInfo.split(",");
		    frameBalance = Integer.parseInt(frameInfoArray[2]);
		    carryFromPreviousFrame = Integer.parseInt(frameInfoArray[3]);
		    sumVacDays = Integer.parseInt(frameInfoArray[4]);
		    consumedAtNextFrame = Integer.parseInt(frameInfoArray[5]);

		    balance = frameBalance + carryFromPreviousFrame - (sumVacDays + consumedAtNextFrame);
		} else {

		    previousFrameInfo = framesInfo.get(targetIndex);
		    previousFrameInfoArray = previousFrameInfo.split(",");
		    previousFrameBalance = Integer.parseInt(previousFrameInfoArray[2]);
		    previousCarryFromPreviousFrame = Integer.parseInt(previousFrameInfoArray[3]);
		    previousSumVacDays = Integer.parseInt(previousFrameInfoArray[4]);
		    previousConsumedAtNextFrame = Integer.parseInt(previousFrameInfoArray[5]);

		    balance = previousFrameBalance + previousCarryFromPreviousFrame - (previousSumVacDays + previousConsumedAtNextFrame);

		    if (balance < HijriDateService.hijriDateDiff(balanceToDate, HijriDateService.getHijriDate(previousFrameInfoArray[1])) + 1)
			balance = -1;
		    else {
			if ((targetIndex + 1) <= framesInfo.size()) {
			    frameInfo = framesInfo.get(targetIndex + 1);
			    frameInfoArray = frameInfo.split(",");
			    frameBalance = Integer.parseInt(frameInfoArray[2]);
			    carryFromPreviousFrame = Integer.parseInt(frameInfoArray[3]);
			    sumVacDays = Integer.parseInt(frameInfoArray[4]);
			    consumedAtNextFrame = Integer.parseInt(frameInfoArray[5]);

			    balance += frameBalance - (sumVacDays + consumedAtNextFrame);
			}
		    }

		}
	    } else {
		List<VacationConfiguration> vacationConfigurations = getVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.REGULAR.getCode());
		for (VacationConfiguration vacationConfiguration : vacationConfigurations) {
		    if (HijriDateService.isDateBetween(vacationConfiguration.getFromDate(), vacationConfiguration.getToDate(), emp.getRecruitmentDate())
			    && HijriDateService.isDateBetween(vacationConfiguration.getFromDate(), vacationConfiguration.getToDate(), balanceToDate)) {

			balance += HijriDateService.hijriDateDiff(emp.getRecruitmentDate(), balanceToDate) * vacationConfiguration.getBalance() / (BALANCE_YEAR_DAYS * 1.0);
			balance -= deductedDays(emp, emp.getRecruitmentDate(), balanceToDate) * vacationConfiguration.getBalance() / (BALANCE_YEAR_DAYS * 1.0);

		    } else if (HijriDateService.isDateBetween(vacationConfiguration.getFromDate(), vacationConfiguration.getToDate(), emp.getRecruitmentDate())
			    && !HijriDateService.isDateBetween(vacationConfiguration.getFromDate(), vacationConfiguration.getToDate(), balanceToDate)) {

			balance += HijriDateService.hijriDateDiff(emp.getRecruitmentDate(), vacationConfiguration.getToDate()) * vacationConfiguration.getBalance() / (BALANCE_YEAR_DAYS * 1.0);
			balance -= deductedDays(emp, emp.getRecruitmentDate(), vacationConfiguration.getToDate()) * vacationConfiguration.getBalance() / (BALANCE_YEAR_DAYS * 1.0);

		    } else if (!HijriDateService.isDateBetween(vacationConfiguration.getFromDate(), vacationConfiguration.getToDate(), emp.getRecruitmentDate())
			    && HijriDateService.isDateBetween(vacationConfiguration.getFromDate(), vacationConfiguration.getToDate(), balanceToDate)) {

			balance += HijriDateService.hijriDateDiff(vacationConfiguration.getFromDate(), balanceToDate) * vacationConfiguration.getBalance() / (BALANCE_YEAR_DAYS * 1.0);
			balance -= deductedDays(emp, vacationConfiguration.getFromDate(), balanceToDate) * vacationConfiguration.getBalance() / (BALANCE_YEAR_DAYS * 1.0);

		    } else if (HijriDateService.isDateBetween(emp.getRecruitmentDate(), balanceToDate, vacationConfiguration.getFromDate())
			    && HijriDateService.isDateBetween(emp.getRecruitmentDate(), balanceToDate, vacationConfiguration.getToDate())) {

			balance += HijriDateService.hijriDateDiff(vacationConfiguration.getFromDate(), vacationConfiguration.getToDate()) * vacationConfiguration.getBalance() / (BALANCE_YEAR_DAYS * 1.0);
			balance -= deductedDays(emp, vacationConfiguration.getFromDate(), vacationConfiguration.getToDate()) * vacationConfiguration.getBalance() / (BALANCE_YEAR_DAYS * 1.0);
		    }
		}

		balance -= sumVacDays(emp.getEmpId(), VacationTypesEnum.REGULAR.getCode(), emp.getRecruitmentDateString(), HijriDateService.getHijriDateString(balanceToDate));

		if (emp.getCategoryId() == CategoriesEnum.USERS.getCode() || emp.getCategoryId() == CategoriesEnum.WAGES.getCode()
			|| emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()) {
		    balance -= sumRelatedDeductedBalance(emp.getEmpId(), VacationTypesEnum.ACCOMPANY.getCode(), emp.getRecruitmentDateString(), HijriDateService.getHijriDateString(balanceToDate));
		}
	    }
	    return balance;
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Calculates the start date and end date of the contractual year for a specific vacation start date given the recruitment date of the contractor who requests the vacation.
     * 
     * @param recruitmentDateString
     *            a <code>String</code> containing the recruitment hijri date in mm/MM/yyyy format
     * @param vacationStartDateString
     *            a <code>String</code> containing the vacation start hijri date in mm/MM/yyyy format
     * @return an array of 2 Strings the first <code>String</code> contains the contractual year start date and the second <code>String</code> contains the contractual year end date
     * @throws BusinessException
     *             if any error occurs
     */
    protected static String[] calculateContractualYearStartAndEndDates(EmployeeData vacationBeneficiary, String vacationStartDateString, VacationConfiguration activeVacationConfiguration) throws BusinessException {
	try {

	    if (activeVacationConfiguration == null)
		activeVacationConfiguration = getActiveRegularVacationConfigurationForContractors(vacationBeneficiary);

	    String[] contractualYear = new String[2];

	    String[] recruitmentDateArray = vacationBeneficiary.getRecruitmentDateString().split("/");
	    String[] vacationStartDateArray = vacationStartDateString.split("/");

	    String contractualYearStartDateString = recruitmentDateArray[0] + "/" + recruitmentDateArray[1] + "/" + vacationStartDateArray[2];
	    if (HijriDateService.getHijriDate(contractualYearStartDateString).after(HijriDateService.getHijriDate(vacationStartDateString)))
		contractualYearStartDateString = recruitmentDateArray[0] + "/" + recruitmentDateArray[1] + "/" + (Integer.parseInt(vacationStartDateArray[2]) - activeVacationConfiguration.getBalanceFrame());

	    String[] contractualYearStartDateArray = contractualYearStartDateString.split("/");
	    Date contractualYearEndDate = HijriDateService.getHijriDate(contractualYearStartDateArray[0] + "/" + contractualYearStartDateArray[1] + "/" + (Integer.parseInt(contractualYearStartDateArray[2]) + activeVacationConfiguration.getBalanceFrame()));
	    contractualYearEndDate = HijriDateService.addSubHijriDays(contractualYearEndDate, -1);

	    contractualYear[0] = contractualYearStartDateString;
	    contractualYear[1] = HijriDateService.getHijriDateString(contractualYearEndDate);
	    return contractualYear;

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Calculates the total compelling vacation balance based on Employee start date is before balanceToDate. Calculation is based on (serviced days - deducted days) without taken vacations
     * 
     * @param emp
     *            the {@link EmployeeData} object which contains the beneficiary data to calculate his vacation balance
     * @param balanceToDate
     *            the <code>Date</code> to calculate the balance till it in mm/MM/yyyy format
     * @return the employee balance
     * @throws BusinessException
     *             if any error occurs
     */
    protected static Integer calculateEmpCompellingBalance(EmployeeData emp, Date balanceToDate, VacationConfiguration activeVacationConfiguration) throws BusinessException {
	validateOldEmpId(emp.getOldEmpId());

	try {
	    balanceToDate = HijriDateService.floorInvalidHijriDateToValidHijriDate(balanceToDate);
	    long categoryId = emp.getCategoryId().longValue();
	    String balanceToDateString = HijriDateService.getHijriDateString(balanceToDate);
	    String vacYear = "", curYear = "";
	    if (categoryId == CategoriesEnum.PERSONS.getCode() || categoryId == CategoriesEnum.USERS.getCode() || categoryId == CategoriesEnum.WAGES.getCode() || categoryId == CategoriesEnum.MEDICAL_STAFF.getCode()) {
		vacYear = HijriDateService.hijriToGregDateString(balanceToDateString).split("/")[2];
		curYear = HijriDateService.hijriToGregDateString(HijriDateService.getHijriSysDateString()).split("/")[2];
	    } else {
		vacYear = balanceToDateString.split("/")[2];
		curYear = HijriDateService.getHijriSysDateString().split("/")[2];
	    }

	    if ((!vacYear.equals(curYear)) || emp.getRecruitmentDate() == null || (balanceToDate.before(emp.getRecruitmentDate())))
		return 0;

	    if (activeVacationConfiguration == null)
		activeVacationConfiguration = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.COMPELLING.getCode()).get(0);

	    if (categoryId == CategoriesEnum.PERSONS.getCode() || categoryId == CategoriesEnum.USERS.getCode() || categoryId == CategoriesEnum.WAGES.getCode() || categoryId == CategoriesEnum.MEDICAL_STAFF.getCode()) {
		return activeVacationConfiguration.getBalance() - sumVacDays(emp.getEmpId(), VacationTypesEnum.COMPELLING.getCode(), HijriDateService.gregToHijriDateString("01/01/" + vacYear), HijriDateService.gregToHijriDateString("31/12/" + vacYear));
	    } else if (categoryId == CategoriesEnum.CONTRACTORS.getCode()) {
		Date frameStartDate = null, frameEndDate = HijriDateService.addSubHijriDays(emp.getRecruitmentDate(), -1);
		do {
		    frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		    String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
		    frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/"
			    + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfiguration.getBalanceFrame()));
		    frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
		} while (!HijriDateService.isDateBetween(frameStartDate, frameEndDate, balanceToDate));
		return activeVacationConfiguration.getBalance() - sumVacDays(emp.getEmpId(), VacationTypesEnum.COMPELLING.getCode(), HijriDateService.getHijriDateString(frameStartDate), HijriDateService.getHijriDateString(frameEndDate));
	    } else {
		return activeVacationConfiguration.getBalance() - sumVacDays(emp.getEmpId(), VacationTypesEnum.COMPELLING.getCode(), "01/01/" + vacYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(vacYear)).getHijriMonthLength() + "/12/" + vacYear);
	    }
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the available sick vacation balance for an employee with balance type substituted with the suitable label.
     * 
     * @param emp
     *            the {@link EmployeeData} object which contains the beneficiary data to calculate his vacation balance
     * @param balanceToDate
     *            the <code>Date</code> to calculate the balance till it in mm/MM/yyyy format
     * @return the employee's sick vacation balance from a specific type (i.e 150 days full paid or 120 days half paid and etc..)
     * @throws BusinessException
     *             if any error occurs
     */
    protected static String getEmpSickBalance(EmployeeData emp, Integer subVacationType, Date balanceToDate, List<VacationConfiguration> activeVacationConfigurations) throws BusinessException {
	return getBalanceAndPaidVacationTypeLabel(calculateEmpSickBalance(emp, subVacationType, balanceToDate, activeVacationConfigurations));
    }

    /**
     * Calculates the available sick vacation balance for an employee.
     * 
     * @param emp
     *            the {@link EmployeeData} object which contains the beneficiary data to calculate his vacation balance
     * @param balanceToDate
     *            the <code>Date</code> to calculate the balance till it in mm/MM/yyyy format
     * @return the employee's sick vacation balance from a specific type (i.e 150 days full paid or 120 days half paid and etc..)
     * @throws BusinessException
     *             if any error occurs
     */
    public static String calculateEmpSickBalance(EmployeeData emp, Integer subVacationType, Date balanceToDate, List<VacationConfiguration> activeVacationConfigurations) throws BusinessException {
	validateOldEmpId(emp.getOldEmpId());

	try {
	    balanceToDate = HijriDateService.floorInvalidHijriDateToValidHijriDate(balanceToDate);

	    if (emp.getRecruitmentDate() == null || balanceToDate.before(emp.getRecruitmentDate()))
		return null;

	    Date frameStartDate = null, frameEndDate = null;
	    long categoryId = emp.getCategoryId();

	    // get first sick vacation for the employee.
	    Vacation firstSickVacation = getFirstVacation(emp.getEmpId(), VacationTypesEnum.SICK.getCode(), subVacationType);

	    frameEndDate = balanceToDate;
	    if (CategoriesEnum.SOLDIERS.getCode() == categoryId || CategoriesEnum.CONTRACTORS.getCode() == categoryId) {
		frameEndDate = emp.getRecruitmentDate();
	    } else {
		// officers, persons, users and wages.
		if (firstSickVacation != null && firstSickVacation.getStartDate().before(balanceToDate))
		    frameEndDate = firstSickVacation.getStartDate();
	    }

	    if (activeVacationConfigurations == null)
		activeVacationConfigurations = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.SICK.getCode(), subVacationType, FlagsEnum.ALL.getCode());

	    frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    do {
		frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
		frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/" + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfigurations.get(0).getBalanceFrame()));
		frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    } while (!HijriDateService.isDateBetween(frameStartDate, frameEndDate, balanceToDate));

	    int balance = sumVacDays(emp.getEmpId(), VacationTypesEnum.SICK.getCode(), subVacationType, FlagsEnum.ALL.getCode(), HijriDateService.getHijriDateString(frameStartDate), HijriDateService.getHijriDateString(frameEndDate));

	    // Get sick vacation periods which has been ordered ASC by the paid policy.
	    int periodBalance = 0;
	    for (VacationConfiguration activeVacationConfiguration : activeVacationConfigurations) {
		periodBalance = activeVacationConfiguration.getBalance();
		if (periodBalance > balance) {
		    balance = periodBalance - balance;
		    return balance + "," + activeVacationConfiguration.getPaidVacationType();
		}
		balance -= periodBalance;
	    }
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
	return null;
    }

    /**
     * get sick vacation frame
     * 
     * @param emp
     * @param subVacationType
     * @param Vacation
     *            Start Date
     * @return string array contain frame Start Date and frame end Date
     * @throws BusinessException
     */
    public static Date[] getSickVacationsFrameStartAndEndDate(EmployeeData emp, Integer subVacationType, Date StartDate) throws BusinessException {

	try {

	    VacationConfiguration activeVacationConfiguration;

	    if (emp.getRecruitmentDate() == null || StartDate.before(emp.getRecruitmentDate()))
		return null;

	    Date frameStartDate = null, frameEndDate = null;
	    long categoryId = emp.getCategoryId();

	    // get first sick vacation for the employee.
	    Vacation firstSickVacation = getFirstVacation(emp.getEmpId(), VacationTypesEnum.SICK.getCode(), subVacationType);

	    frameEndDate = StartDate;
	    if (CategoriesEnum.SOLDIERS.getCode() == categoryId || CategoriesEnum.CONTRACTORS.getCode() == categoryId) {
		frameEndDate = emp.getRecruitmentDate();
	    } else {
		// officers, persons, users and wages.
		if (firstSickVacation != null && firstSickVacation.getStartDate().before(StartDate))
		    frameEndDate = firstSickVacation.getStartDate();
	    }

	    activeVacationConfiguration = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.SICK.getCode(), subVacationType, FlagsEnum.ALL.getCode()).get(0);

	    frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    do {
		frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
		frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/" + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfiguration.getBalanceFrame()));
		frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    } while (!HijriDateService.isDateBetween(frameStartDate, frameEndDate, StartDate));
	    Date[] result = { frameStartDate, frameEndDate };
	    return result;
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /**
     * Calculates the available Field vacation balance for an employee.
     * 
     * @param emp
     *            the {@link EmployeeData} object which contains the beneficiary data to calculate his vacation balance
     * @param balanceToDate
     *            the <code>Date</code> to calculate the balance till it in mm/MM/yyyy format
     * @return the employee's Field vacation balance from a specific type (i.e 60 for officers and 45 for soldiers..)
     * @throws BusinessException
     *             if any error occurs
     */
    protected static int calculateEmpFieldBalance(EmployeeData emp, Date balanceToDate, VacationConfiguration activeVacationConfiguration) throws BusinessException {
	validateOldEmpId(emp.getOldEmpId());

	try {
	    balanceToDate = HijriDateService.floorInvalidHijriDateToValidHijriDate(balanceToDate);

	    if (emp.getRecruitmentDate() == null || balanceToDate.before(emp.getRecruitmentDate()))
		return 0;

	    if (activeVacationConfiguration == null)
		activeVacationConfiguration = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.FIELD.getCode()).get(0);

	    Date frameStartDate = null, frameEndDate = HijriDateService.addSubHijriDays(emp.getRecruitmentDate(), -1);

	    do {
		frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
		frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/" + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfiguration.getBalanceFrame()));
		frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    } while (!HijriDateService.isDateBetween(frameStartDate, frameEndDate, balanceToDate));

	    return activeVacationConfiguration.getBalance() - sumVacDays(emp.getEmpId(), VacationTypesEnum.FIELD.getCode(), HijriDateService.getHijriDateString(frameStartDate), HijriDateService.getHijriDateString(frameEndDate));
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static int calculateEmpExceptionalBalance(EmployeeData emp, Integer subVacationType, Date balanceToDate, VacationConfiguration activeVacationConfiguration) throws BusinessException {
	validateOldEmpId(emp.getOldEmpId());

	int balance = 0;
	try {
	    balanceToDate = HijriDateService.floorInvalidHijriDateToValidHijriDate(balanceToDate);

	    if (emp.getRecruitmentDate() == null || balanceToDate.before(emp.getRecruitmentDate()))
		return balance;

	    if (activeVacationConfiguration == null)
		activeVacationConfiguration = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.EXCEPTIONAL.getCode(), subVacationType, FlagsEnum.ALL.getCode()).get(0);

	    if (emp.getCategoryId() == CategoriesEnum.OFFICERS.getCode()) {

		Date frameStartDate = null, frameEndDate = HijriDateService.addSubHijriDays(emp.getRecruitmentDate(), -1);
		do {
		    frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		    String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
		    frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/" + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfiguration.getBalanceFrame()));
		    frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
		} while (!HijriDateService.isDateBetween(frameStartDate, frameEndDate, balanceToDate));

		balance = activeVacationConfiguration.getBalance() - sumVacDays(emp.getEmpId(), VacationTypesEnum.EXCEPTIONAL.getCode(), HijriDateService.getHijriDateString(frameStartDate), HijriDateService.getHijriDateString(frameEndDate));

	    } else if (emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		balance = activeVacationConfiguration.getBalance() - sumVacDays(emp.getEmpId(), VacationTypesEnum.EXCEPTIONAL.getCode(), emp.getRecruitmentDateString(), HijriDateService.getHijriDateString(balanceToDate));
	    } else {
		int exceptionalVacationSum = sumVacDays(emp.getEmpId(), VacationTypesEnum.EXCEPTIONAL.getCode(), emp.getRecruitmentDateString(), HijriDateService.getHijriDateString(balanceToDate));

		if (subVacationType == SubVacationTypesEnum.SUB_TYPE_ONE.getCode()) {
		    // get first exceptional for circumstances vacation for the employee.
		    Vacation firstExceptionalForCircumstancesVacation = getFirstVacation(emp.getEmpId(), VacationTypesEnum.EXCEPTIONAL.getCode(), SubVacationTypesEnum.SUB_TYPE_ONE.getCode());

		    Date frameStartDate = null, frameEndDate = balanceToDate;
		    if (firstExceptionalForCircumstancesVacation != null && firstExceptionalForCircumstancesVacation.getStartDate().before(balanceToDate))
			frameEndDate = firstExceptionalForCircumstancesVacation.getStartDate();

		    frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
		    do {
			frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
			String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
			frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/" + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfiguration.getBalanceFrame()));
			frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
		    } while (!HijriDateService.isDateBetween(frameStartDate, frameEndDate, balanceToDate));

		    int exceptionalForCircumstancesVacationSumWithinFrame = sumVacDays(emp.getEmpId(), VacationTypesEnum.EXCEPTIONAL.getCode(), SubVacationTypesEnum.SUB_TYPE_ONE.getCode(), FlagsEnum.ALL.getCode(), HijriDateService.getHijriDateString(frameStartDate), HijriDateService.getHijriDateString(frameEndDate));

		    VacationConfiguration exceptionalForAccompanyActiveVacationConfiguration = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.EXCEPTIONAL.getCode(), SubVacationTypesEnum.SUB_TYPE_TWO.getCode(), FlagsEnum.ALL.getCode()).get(0);

		    balance = Math.min((activeVacationConfiguration.getBalance() - exceptionalForCircumstancesVacationSumWithinFrame), (exceptionalForAccompanyActiveVacationConfiguration.getBalance() - exceptionalVacationSum));
		} else if (subVacationType == SubVacationTypesEnum.SUB_TYPE_TWO.getCode()) {
		    balance = activeVacationConfiguration.getBalance() - exceptionalVacationSum;
		}
	    }
	    return balance;
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static String getEmpAccompanyBalance(EmployeeData emp, Date balanceToDate, List<VacationConfiguration> activeVacationConfigurations) throws BusinessException {
	int regularVacBalance = (int) calculateEmpRegularBalance(emp, balanceToDate, null, -1);
	String balanceString = "";
	if (regularVacBalance > 0)
	    balanceString = regularVacBalance + " " + getMessage("label_regularBalance");

	String accompanyBalanceString = getBalanceAndPaidVacationTypeLabel(calculateEmpAccompanyBalance(emp, balanceToDate, activeVacationConfigurations));

	if (!accompanyBalanceString.equals("0"))
	    balanceString += (regularVacBalance > 0 ? " , " : "") + accompanyBalanceString;

	return balanceString.equals("") ? "0" : balanceString;
    }

    private static String calculateEmpAccompanyBalance(EmployeeData emp, Date balanceToDate, List<VacationConfiguration> activeVacationConfigurations) throws BusinessException {
	validateOldEmpId(emp.getOldEmpId());

	try {
	    balanceToDate = HijriDateService.floorInvalidHijriDateToValidHijriDate(balanceToDate);

	    if (emp.getRecruitmentDate() == null || balanceToDate.before(emp.getRecruitmentDate()))
		return null;

	    // get first accompany vacation for the employee.
	    Vacation firstAccompanyVacation = getFirstVacation(emp.getEmpId(), VacationTypesEnum.ACCOMPANY.getCode(), FlagsEnum.ALL.getCode());

	    Date frameStartDate = null, frameEndDate = balanceToDate;
	    if (firstAccompanyVacation != null && firstAccompanyVacation.getStartDate().before(balanceToDate))
		frameEndDate = firstAccompanyVacation.getStartDate();

	    if (activeVacationConfigurations == null)
		activeVacationConfigurations = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.ACCOMPANY.getCode());

	    frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    do {
		frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
		frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/" + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfigurations.get(0).getBalanceFrame()));
		frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    } while (!HijriDateService.isDateBetween(frameStartDate, frameEndDate, balanceToDate));

	    int balance = sumVacDays(emp.getEmpId(), VacationTypesEnum.ACCOMPANY.getCode(), HijriDateService.getHijriDateString(frameStartDate), HijriDateService.getHijriDateString(frameEndDate))
		    - sumRelatedDeductedBalance(emp.getEmpId(), VacationTypesEnum.ACCOMPANY.getCode(), HijriDateService.getHijriDateString(frameStartDate), HijriDateService.getHijriDateString(frameEndDate));

	    // Get accompany vacation periods which has been ordered ASC by the paid policy.
	    int periodBalance = 0;
	    for (VacationConfiguration activeVacationConfiguration : activeVacationConfigurations) {
		periodBalance = activeVacationConfiguration.getBalance();
		if (periodBalance > balance) {
		    balance = periodBalance - balance;
		    return balance + "," + activeVacationConfiguration.getPaidVacationType();
		}
		balance -= periodBalance;
	    }
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
	return null;
    }

    /**
     * Calculates the available Relief vacation balance for an employee.
     * 
     * @param emp
     *            the {@link EmployeeData} object which contains the beneficiary data to calculate his vacation balance
     * @param balanceToDate
     *            the <code>Date</code> to calculate the balance till it in mm/MM/yyyy format
     * @return the employee's relief vacation balance.
     * @throws BusinessException
     *             if any error occurs
     */
    protected static int calculateEmpReliefBalance(EmployeeData emp, Date balanceToDate, VacationConfiguration activeVacationConfiguration) throws BusinessException {
	validateOldEmpId(emp.getOldEmpId());

	try {
	    balanceToDate = HijriDateService.floorInvalidHijriDateToValidHijriDate(balanceToDate);

	    if (emp.getRecruitmentDate() == null || balanceToDate.before(emp.getRecruitmentDate()))
		return 0;

	    if (activeVacationConfiguration == null)
		activeVacationConfiguration = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.RELIEF.getCode()).get(0);

	    Date frameStartDate = null, frameEndDate = HijriDateService.addSubHijriDays(emp.getRecruitmentDate(), -1);
	    do {
		frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
		frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/" + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfiguration.getBalanceFrame()));
		frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    } while (!HijriDateService.isDateBetween(frameStartDate, frameEndDate, balanceToDate));

	    return activeVacationConfiguration.getBalance() - sumVacDays(emp.getEmpId(), VacationTypesEnum.RELIEF.getCode(), HijriDateService.getHijriDateString(frameStartDate), HijriDateService.getHijriDateString(frameEndDate));
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static int calculateSportiveCultureSocialBalance(EmployeeData emp, long vacationTypeId, int locationFlag, Date balanceToDate, VacationConfiguration activeVacationConfiguration) throws BusinessException {
	validateOldEmpId(emp.getOldEmpId());

	try {
	    balanceToDate = HijriDateService.floorInvalidHijriDateToValidHijriDate(balanceToDate);

	    if (emp.getRecruitmentDate() == null || balanceToDate.before(emp.getRecruitmentDate()))
		return 0;

	    if (activeVacationConfiguration == null)
		activeVacationConfiguration = getActiveVacationConfigurations(emp.getCategoryId(), vacationTypeId, FlagsEnum.ALL.getCode(), locationFlag).get(0);

	    // TODO new query to sum vac days ?
	    int vacDays = sumVacDays(emp.getEmpId(), VacationTypesEnum.SPORTIVE.getCode(), FlagsEnum.ALL.getCode(), locationFlag, emp.getRecruitmentDateString(), HijriDateService.getHijriDateString(balanceToDate)) +
		    sumVacDays(emp.getEmpId(), VacationTypesEnum.SOCIAL.getCode(), FlagsEnum.ALL.getCode(), locationFlag, emp.getRecruitmentDateString(), HijriDateService.getHijriDateString(balanceToDate)) +
		    sumVacDays(emp.getEmpId(), VacationTypesEnum.CULTURAL.getCode(), FlagsEnum.ALL.getCode(), locationFlag, emp.getRecruitmentDateString(), HijriDateService.getHijriDateString(balanceToDate));

	    return activeVacationConfiguration.getBalance() - vacDays;
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Calculates the available Motherhood vacation balance for an employee.
     * 
     * @param emp
     *            the {@link EmployeeData} object which contains the beneficiary data to calculate his vacation balance
     * @param balanceToDate
     *            the <code>Date</code> to calculate the balance till it in mm/MM/yyyy format
     * @return the employee's motherhood vacation balance.
     * @throws BusinessException
     *             if any error occurs
     */
    protected static int calculateEmpMotherhoodBalance(EmployeeData emp, Date balanceToDate, VacationConfiguration activeVacationConfiguration) throws BusinessException {
	validateOldEmpId(emp.getOldEmpId());

	try {
	    balanceToDate = HijriDateService.floorInvalidHijriDateToValidHijriDate(balanceToDate);

	    if (emp.getRecruitmentDate() == null || balanceToDate.before(emp.getRecruitmentDate()))
		return 0;

	    if (activeVacationConfiguration == null)
		activeVacationConfiguration = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.MOTHERHOOD.getCode()).get(0);

	    return activeVacationConfiguration.getBalance() - sumVacDays(emp.getEmpId(), VacationTypesEnum.MOTHERHOOD.getCode(), emp.getRecruitmentDateString(), HijriDateService.getHijriDateString(balanceToDate));
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Calculates the available Orphan care vacation balance for an employee.
     * 
     * @param emp
     *            the {@link EmployeeData} object which contains the beneficiary data to calculate his vacation balance
     * @param balanceToDate
     *            the <code>Date</code> to calculate the balance till it in mm/MM/yyyy format
     * @return the employee's orphan care vacation balance.
     * @throws BusinessException
     *             if any error occurs
     */
    protected static int calculateEmpOrphanCareBalance(EmployeeData emp, Date balanceToDate, VacationConfiguration activeVacationConfiguration) throws BusinessException {
	validateOldEmpId(emp.getOldEmpId());

	try {
	    balanceToDate = HijriDateService.floorInvalidHijriDateToValidHijriDate(balanceToDate);

	    if (emp.getRecruitmentDate() == null || balanceToDate.before(emp.getRecruitmentDate()))
		return 0;

	    if (activeVacationConfiguration == null)
		activeVacationConfiguration = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.ORPHAN_CARE.getCode()).get(0);

	    return activeVacationConfiguration.getBalance() - sumVacDays(emp.getEmpId(), VacationTypesEnum.ORPHAN_CARE.getCode(), emp.getRecruitmentDateString(), HijriDateService.getHijriDateString(balanceToDate));
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------------------------------- Utilities -----------------------------------------------------*/

    /**
     * Gets the employee sick vacation balance type (i.e 150 days full paid or 120 days half paid and etc..).
     * 
     * @param emp
     *            the {@link EmployeeData} object which contains the beneficiary data to calculate his vacation balance type
     * @param balanceToDate
     *            the <code>Date</code> to calculate the balance till it in mm/MM/yyyy format
     * @return the employee's sick vacation balance type
     * @throws BusinessException
     *             if any error occurs
     */
    protected static int getPaidVacationType(long vacationTypeId, Integer subVacationType, EmployeeData emp, Date balanceToDate, Integer relatedDeductedBalance) throws BusinessException {
	int paidVacationType = PaidVacationTypesEnum.FULL_PAID.getCode();
	try {
	    String balanceAndPaidVacationType = "";

	    if (emp.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode() && vacationTypeId == VacationTypesEnum.COMPELLING.getCode())
		paidVacationType = PaidVacationTypesEnum.WITHOUT_PAID.getCode();

	    if (vacationTypeId == VacationTypesEnum.SICK.getCode()) {
		List<VacationConfiguration> activeVacationConfigurations = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.SICK.getCode(), subVacationType, FlagsEnum.ALL.getCode());
		balanceAndPaidVacationType = calculateEmpSickBalance(emp, subVacationType, balanceToDate, activeVacationConfigurations);
		paidVacationType = Integer.parseInt(balanceAndPaidVacationType.split(",")[1]);
	    } else if (vacationTypeId == VacationTypesEnum.ACCOMPANY.getCode() && relatedDeductedBalance != null && relatedDeductedBalance == 0) {
		List<VacationConfiguration> activeVacationConfigurations = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.ACCOMPANY.getCode());
		balanceAndPaidVacationType = calculateEmpAccompanyBalance(emp, balanceToDate, activeVacationConfigurations);
		paidVacationType = Integer.parseInt(balanceAndPaidVacationType.split(",")[1]);
	    }
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
	return paidVacationType;
    }

    protected static String getDecisionData(Long vacationTypeId, EmployeeData vacationBeneficiary, String startDateString) throws BusinessException {
	String decisionData = null;
	try {
	    if (vacationTypeId == VacationTypesEnum.REGULAR.getCode() && vacationBeneficiary.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		String vacStartYear = startDateString.split("/")[2];
		int regularVacationsSum = sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), "01/01/" + vacStartYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(vacStartYear)).getHijriMonthLength() + "/12/" + vacStartYear);

		VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), VacationTypesEnum.REGULAR.getCode()).get(0);
		if (regularVacationsSum == activeVacationConfiguration.getMaxVacationsDaysPerYear())
		    decisionData = "1";
	    }
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
	return decisionData;
    }

    private static String getBalanceAndPaidVacationTypeLabel(String balanceAndPaidVacationType) throws BusinessException {
	String balance = "0";
	try {
	    if (balanceAndPaidVacationType != null) {
		String[] balanceAndPaidVacationTypeArray = balanceAndPaidVacationType.split(",");
		String paidVacationTypeLabel = "";
		int paidVacationType = Integer.parseInt(balanceAndPaidVacationTypeArray[1]);
		if (PaidVacationTypesEnum.FULL_PAID.getCode() == paidVacationType)
		    paidVacationTypeLabel = getMessage("label_fullPaid");
		else if (PaidVacationTypesEnum.THREE_QUARTER_PAID.getCode() == paidVacationType)
		    paidVacationTypeLabel = getMessage("label_threeQuarterPaid");
		else if (PaidVacationTypesEnum.HALF_PAID.getCode() == paidVacationType)
		    paidVacationTypeLabel = getMessage("label_halfPaid");
		else if (PaidVacationTypesEnum.QUARTER_PAID.getCode() == paidVacationType)
		    paidVacationTypeLabel = getMessage("label_quarterPaid");
		else if (PaidVacationTypesEnum.WITHOUT_PAID.getCode() == paidVacationType)
		    paidVacationTypeLabel = getMessage("label_withoutPaid");

		balance = balanceAndPaidVacationTypeArray[0] + " " + paidVacationTypeLabel;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
	return balance;
    }

    protected static Integer getRelatedDeductedBalance(EmployeeData emp, long vacationTypeId, Date balanceToDate, int period) throws BusinessException {
	Integer relatedDeductedBalance = null;
	try {
	    if (vacationTypeId == VacationTypesEnum.ACCOMPANY.getCode()
		    && (emp.getCategoryId() == CategoriesEnum.USERS.getCode()
			    || emp.getCategoryId() == CategoriesEnum.WAGES.getCode()
			    || emp.getCategoryId() == CategoriesEnum.PERSONS.getCode()
			    || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode())) {

		relatedDeductedBalance = 0;
		int regularBalance = (int) calculateEmpRegularBalance(emp, balanceToDate, null, -1);

		String accompanyBalanceString = calculateEmpAccompanyBalance(emp, balanceToDate, null);
		int accompanyBalanceType = -1;
		if (accompanyBalanceString != null)
		    accompanyBalanceType = Integer.parseInt(accompanyBalanceString.split(",")[1]);

		if (period <= regularBalance)
		    relatedDeductedBalance = period;
		else if (period > regularBalance && accompanyBalanceType == PaidVacationTypesEnum.FULL_PAID.getCode())
		    relatedDeductedBalance = regularBalance;
	    }
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
	return relatedDeductedBalance;
    }

    private static boolean isValidPreviousVacationJoiningDate(long beneficiaryId, long beneficiaryCategoryId, Date vacationStartDate) throws BusinessException {
	VacationData lastVacationBeforeSpecificDate = getLastVacationBeforeCurrentDate(beneficiaryId, false);

	if (lastVacationBeforeSpecificDate == null
		|| lastVacationBeforeSpecificDate.getJoiningDate() != null
		|| ((beneficiaryCategoryId == CategoriesEnum.OFFICERS.getCode() || beneficiaryCategoryId == CategoriesEnum.SOLDIERS.getCode())
			&& (HijriDateService.hijriDateDiff(lastVacationBeforeSpecificDate.getEndDate(), vacationStartDate) == 1))) {
	    return true;
	}

	return false;
    }

    protected static boolean checkPreviousVacationContinuity(long beneficiaryId, long beneficiaryCategoryId, Date vacationStartDate) throws BusinessException {
	VacationData lastVacationBeforeSpecificDate = getLastVacationBeforeSpecificDate(beneficiaryId, false, HijriDateService.getHijriDateString(vacationStartDate));

	if (lastVacationBeforeSpecificDate != null
		&& lastVacationBeforeSpecificDate.getJoiningDate() == null
		&& (beneficiaryCategoryId == CategoriesEnum.OFFICERS.getCode() || beneficiaryCategoryId == CategoriesEnum.SOLDIERS.getCode())
		&& HijriDateService.hijriDateDiff(lastVacationBeforeSpecificDate.getEndDate(), vacationStartDate) == 1) {
	    return true;
	}

	return false;
    }

    protected static VacationData getLastVacationBeforeCurrentDate(long empId, boolean withoutJoiningFlag) throws BusinessException {
	return getLastVacationBeforeSpecificDate(empId, withoutJoiningFlag, HijriDateService.getHijriSysDateString());
    }

    protected static VacationData getLastVacationAfterCurrentDate(long empId) throws BusinessException {
	return getLastVacationAfterSpecificDate(empId, HijriDateService.getHijriSysDateString());
    }

    /**
     * Gets the last vacation either with or without joining date before a specific date for an employee.
     * 
     * @param empId
     *            the employee ID to get his last vacation
     * 
     * @return the last {@link Vacation} object for this employee
     * @throws BusinessException
     */
    private static VacationData getLastVacationBeforeSpecificDate(long empId, boolean withoutJoiningFlag, String date) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_DATE", date);
	    qParams.put("P_WITHOUT_JOINING_FLAG", withoutJoiningFlag ? FlagsEnum.ON.getCode() : FlagsEnum.ALL.getCode());

	    List<VacationData> result = DataAccess.executeNamedQuery(VacationData.class, QueryNamesEnum.HCM_GET_LAST_VACATION_DATA_BEFORE_SPECIFIC_DATE.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static VacationData getLastVacationAfterSpecificDate(long empId, String date) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_DATE", date);

	    List<VacationData> result = DataAccess.executeNamedQuery(VacationData.class, QueryNamesEnum.HCM_GET_LAST_VACATION_DATA_AFTER_SPECIFIC_DATE.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static List<String> getEmployeeRegularVacationsBalances(long employeeId) throws BusinessException {
	try {
	    EmployeeData employeeData = EmployeesService.getEmployeeData(employeeId);

	    Date balanceFromDate = null, balanceToDate = null, periodFromDate = null, periodToDate = null, endDate = null;
	    float dueBalance = 0.0F;
	    int sumVacDays = 0;
	    int daysNumberPerYear = 360;

	    List<Float> dueBalances = new ArrayList<Float>();
	    List<Integer> consumedVacDays = new ArrayList<Integer>();
	    List<Float> remainingBalances = new ArrayList<Float>();
	    List<Float> compensationDueBalances = new ArrayList<Float>();

	    /********************* Calculate termination date for this employee *******************************************/

	    if (employeeData.getServiceTerminationDate() != null)
		endDate = employeeData.getServiceTerminationDate();
	    else {
		Date todayDate = HijriDateService.getHijriSysDate();
		VacationData lastVacation = VacationsService.getLastVacationData(employeeId, VacationTypesEnum.REGULAR.getCode());

		if (lastVacation == null || todayDate.after(lastVacation.getEndDate()))
		    endDate = todayDate;
		else
		    endDate = lastVacation.getEndDate();
	    }

	    /********************* Calculate due balance, consumed vacations days and remaining balance for each period ***/

	    VacationConfiguration activeVacationConfiguration = getActiveVacationConfigurations(employeeData.getCategoryId(), VacationTypesEnum.REGULAR.getCode()).get(0);
	    String periodsString = employeeData.getRecruitmentDateString() + activeVacationConfiguration.getPeriods() + HijriDateService.getHijriDateString(endDate);
	    String[] periods = periodsString.split(",");

	    for (int i = 0; i < periods.length; i++) {
		String[] period = periods[i].split("-");

		periodFromDate = HijriDateService.getHijriDate(period[0]);
		periodToDate = HijriDateService.getHijriDate(period[1]);

		balanceFromDate = null;
		balanceToDate = null;

		if (employeeData.getRecruitmentDate().after(periodToDate) || endDate.before(periodFromDate)) {
		    dueBalances.add(0.0F);
		    consumedVacDays.add(0);
		    remainingBalances.add(0.0F);
		    continue;
		}

		if (HijriDateService.isDateBetween(periodFromDate, periodToDate, employeeData.getRecruitmentDate())
			&& HijriDateService.isDateBetween(periodFromDate, periodToDate, endDate)) {

		    balanceFromDate = employeeData.getRecruitmentDate();
		    balanceToDate = endDate;

		} else if (HijriDateService.isDateBetween(periodFromDate, periodToDate, employeeData.getRecruitmentDate())
			&& !HijriDateService.isDateBetween(periodFromDate, periodToDate, endDate)) {

		    balanceFromDate = employeeData.getRecruitmentDate();
		    balanceToDate = periodToDate;

		} else if (!HijriDateService.isDateBetween(periodFromDate, periodToDate, employeeData.getRecruitmentDate())
			&& HijriDateService.isDateBetween(periodFromDate, periodToDate, endDate)) {

		    balanceFromDate = periodFromDate;
		    balanceToDate = endDate;

		} else if (HijriDateService.isDateBetween(employeeData.getRecruitmentDate(), endDate, periodFromDate)
			&& HijriDateService.isDateBetween(employeeData.getRecruitmentDate(), endDate, periodToDate)) {

		    balanceFromDate = periodFromDate;
		    balanceToDate = periodToDate;
		}

		if (balanceFromDate != null && balanceToDate != null) {
		    dueBalance = (HijriDateService.hijriDateDiff(balanceFromDate, balanceToDate) + 1) * activeVacationConfiguration.getBalance() / (daysNumberPerYear * 1.0F);
		    dueBalance -= deductedDays(employeeData, balanceFromDate, balanceToDate) * activeVacationConfiguration.getBalance() / (daysNumberPerYear * 1.0F);

		    sumVacDays = sumVacDays(employeeData.getEmpId(), VacationTypesEnum.REGULAR.getCode(), HijriDateService.getHijriDateString(balanceFromDate), HijriDateService.getHijriDateString(balanceToDate));

		    dueBalances.add(dueBalance);
		    consumedVacDays.add(sumVacDays);
		    remainingBalances.add(dueBalance - sumVacDays);
		} else {
		    dueBalances.add(0.0F);
		    consumedVacDays.add(0);
		    remainingBalances.add(0.0F);
		}
	    }

	    /********************* Adjust remaining balances for each period **********************************************/

	    for (int i = remainingBalances.size() - 1; i > 0; i--) {
		if (remainingBalances.get(i) < 0) {
		    remainingBalances.set(i - 1, remainingBalances.get(i - 1) - Math.abs(remainingBalances.get(i)));
		    remainingBalances.set(i, 0.0F);
		}
	    }

	    /********************* Calculate compensation due balance for each period *************************************/

	    TerminationTransactionData terminationTransactionData = TerminationsService.getEffectiveTerminationTransaction(employeeId);

	    if (terminationTransactionData != null && terminationTransactionData.getReasonId() != null &&
		    (terminationTransactionData.getReasonId().equals(TerminationReasonsEnum.OFFICERS_LACK_MEDICAL_FITNESS.getCode())
			    || terminationTransactionData.getReasonId().equals(TerminationReasonsEnum.OFFICERS_DEATH_LOSS.getCode())
			    || terminationTransactionData.getReasonId().equals(TerminationReasonsEnum.SOLDIERS_DEATH.getCode())
			    || terminationTransactionData.getReasonId().equals(TerminationReasonsEnum.SOLDIERS_LACK_MEDICAL_FITNESS.getCode()))) {

		compensationDueBalances.addAll(remainingBalances);

	    } else {
		if (remainingBalances.get(0) > 0) {
		    if (remainingBalances.get(0) < ETRConfigurationService.getFirstPeriodRemainingBalanceForRulesOneAndTwo() * FULL_HIJRI_MONTH_DAYS) {
			// Rule # 2
			compensationDueBalances.add(Math.min(remainingBalances.get(0), ETRConfigurationService.getFirstAndSecondPeriodsMaxBalanceToBeCompensatedForRuleTwo() * FULL_HIJRI_MONTH_DAYS));
			compensationDueBalances.add(Math.min(remainingBalances.get(1), ((ETRConfigurationService.getFirstAndSecondPeriodsMaxBalanceToBeCompensatedForRuleTwo() * FULL_HIJRI_MONTH_DAYS) - compensationDueBalances.get(0))));
			compensationDueBalances.add(Math.min(remainingBalances.get(2), ETRConfigurationService.getThirdPeriodMaxBalanceToBeCompensatedForRuleTwo() * FULL_HIJRI_MONTH_DAYS));
		    } else {
			// Rule # 1
			compensationDueBalances.add(remainingBalances.get(0));
			compensationDueBalances.add(0.0F);
			compensationDueBalances.add(Math.min(remainingBalances.get(2), ETRConfigurationService.getThirdPeriodMaxBalanceToBeCompensatedForRuleOne() * FULL_HIJRI_MONTH_DAYS));
		    }
		} else {
		    if (remainingBalances.get(2) < 0.04F) {
			// Rule # 5
			compensationDueBalances.add(0.0F);
			compensationDueBalances.add(Math.min(remainingBalances.get(1), ETRConfigurationService.getSecondPeriodMaxBalanceToBeCompensatedForRuleFive() * FULL_HIJRI_MONTH_DAYS));
			compensationDueBalances.add(0.0F);
		    } else {
			if (remainingBalances.get(1) < 0.04F) {
			    // Rule # 4
			    compensationDueBalances.add(0.0F);
			    compensationDueBalances.add(0.0F);
			    compensationDueBalances.add(Math.min(remainingBalances.get(2), ETRConfigurationService.getSecondAndThirdPeriodsMaxBalanceToBeCompensatedForRulesThreeAndFour() * FULL_HIJRI_MONTH_DAYS));
			} else {
			    // Rule # 3
			    compensationDueBalances.add(0.0F);
			    compensationDueBalances.add(Math.min(remainingBalances.get(1), ETRConfigurationService.getSecondAndThirdPeriodsMaxBalanceToBeCompensatedForRulesThreeAndFour() * FULL_HIJRI_MONTH_DAYS));
			    compensationDueBalances.add(Math.min(remainingBalances.get(2), ((ETRConfigurationService.getSecondAndThirdPeriodsMaxBalanceToBeCompensatedForRulesThreeAndFour() * FULL_HIJRI_MONTH_DAYS) - compensationDueBalances.get(1))));
			}
		    }
		}
	    }

	    /********************* Construct a string representing each period balance info *******************************/

	    List<String> parameters = new ArrayList<String>();
	    float totalCompensationDueBalance = 0.0F;

	    for (int i = 0; i < periods.length; i++) {
		String[] period = periods[i].split("-");

		String periodFromDateString = (i == 0) ? (HijriDateService.getHijriDate(period[1]).after(employeeData.getRecruitmentDate()) ? employeeData.getRecruitmentDateString() : "") : period[0];
		String periodToDateString = (i == periods.length - 1) ? (HijriDateService.getHijriDate(period[0]).before(endDate) ? HijriDateService.getHijriDateString(endDate) : "") : period[1];

		parameters.add(periodFromDateString
			+ "," + periodToDateString
			+ "," + convertDaysToMonthsDaysHours(dueBalances.get(i))
			+ "," + convertDaysToMonthsDaysHours(consumedVacDays.get(i))
			+ "," + convertDaysToMonthsDaysHours(remainingBalances.get(i))
			+ "," + convertDaysToMonthsDaysHours(compensationDueBalances.get(i))
			+ "," + convertDaysToMonthsDaysHours(remainingBalances.get(i) - compensationDueBalances.get(i)));

		totalCompensationDueBalance += compensationDueBalances.get(i);
	    }
	    parameters.add(convertDaysToMonthsDaysHours(totalCompensationDueBalance));

	    return parameters;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static String convertDaysToMonthsDaysHours(float daysNumberInput) {
	int monthsNumber = (int) daysNumberInput / FULL_HIJRI_MONTH_DAYS;
	int daysNumber = (int) daysNumberInput - (monthsNumber * FULL_HIJRI_MONTH_DAYS);
	long hoursNumber = Math.round((daysNumberInput - (int) daysNumberInput) * 24);

	return monthsNumber + ":" + daysNumber + ":" + hoursNumber;
    }

    /*---------------------------------------------------------- Queries -------------------------------------------------------*/

    /**
     * Checks if there is any dates conflict for this employee vacations.
     * 
     * @param empId
     *            the employee ID of the requester who makes a vacation request
     * @param startDateString
     *            a <code>String</code> containing the vacation start hijri date in mm/MM/yyyy format
     * @param endDateString
     *            a <code>String</code> containing the vacation end hijri date in mm/MM/yyyy format
     * @param excludedVacationId
     *            a <code>Long</code> containing a vacation transaction Id to be excluded.
     * @throws BusinessException
     *             if there is any dates conflict or any general error occurs
     */
    protected static long checkDatesConflict(long empId, String startDateString, String endDateString, Long excludedVacationId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_START_DATE", startDateString);
	    qParams.put("P_END_DATE", endDateString);
	    qParams.put("P_EXCLUDED_VACATION_ID", excludedVacationId == null ? FlagsEnum.ALL.getCode() : excludedVacationId);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_VACATION_DATES_CONFLICT.getCode(), qParams).get(0);

	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Calculates the employee deducted days.
     * 
     * @param empId
     *            the employee ID to calculate his deducted days
     * @param fromDate
     *            the <code>Date</code> to start the calculation from it, in mm/MM/yyyy format
     * @param toDate
     *            the <code>Date</code> to end the calculation at it, in mm/MM/yyyy format
     * @return an integer containing the deducted days number
     * @throws DatabaseException
     *             if a database exception occurs
     */
    public static int deductedDays(EmployeeData employeeData, Date fromDate, Date toDate) throws BusinessException {
	int deductedDays = EmployeesDeductionsService.deductedDays(employeeData, fromDate, toDate);

	if (employeeData.getCategoryId().equals(CategoriesEnum.PERSONS.getCode()) || employeeData.getCategoryId().equals(CategoriesEnum.WAGES.getCode())
		|| employeeData.getCategoryId().equals(CategoriesEnum.USERS.getCode()) || employeeData.getCategoryId().equals(CategoriesEnum.MEDICAL_STAFF.getCode()))
	    deductedDays += sumVacDays(employeeData.getEmpId(), VacationTypesEnum.EXCEPTIONAL.getCode(), HijriDateService.getHijriDateString(fromDate), HijriDateService.getHijriDateString(toDate));

	return deductedDays;
    }

    /**
     * Gets the first vacation for an employee.
     * 
     * @param empId
     *            the employee ID to get his first vacation
     * @param vacationTypeId
     *            the vacation type is (1) Regular, (5) Compelling or (2) Sick
     * @return the first {@link Vacation} object for this employee
     * @throws NoDataException
     *             if there is no vacations for this employee
     * @throws BusinessException
     *             if a database error occurs
     */
    public static Vacation getFirstVacation(long empId, long vacationTypeId, Integer subVacationType) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_VACATION_TYPE_ID", vacationTypeId);
	    qParams.put("P_SUB_VACATION_TYPE", subVacationType == null ? FlagsEnum.ALL.getCode() : subVacationType);

	    List<Vacation> result = DataAccess.executeNamedQuery(Vacation.class, QueryNamesEnum.HCM_GET_FIRST_VACATION.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the total number of not cancelled vacations days of the vacations with specific employee, type, and date.
     * 
     * @param empId
     *            the employee ID to get his vacations which matches the search criteria
     * @param vacationTypeId
     *            the vacation type is (1) Regular, (5) Compelling or (2) Sick
     * @param fromDateString
     *            a <code>String</code> containing the date which the vacation start date or end date should be after it, in mm/MM/yyyy format
     * @param toDateString
     *            a <code>String</code> containing the date which the vacation start date or end date should be before it, in mm/MM/yyyy format
     * @return the total number of vacations days which matches the search criteria
     * @throws BusinessException
     *             if database error occurs
     */
    public static int sumVacDays(long empId, long vacationTypeId, String fromDateString, String toDateString) throws BusinessException {
	return sumVacDays(empId, vacationTypeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), fromDateString, toDateString, FlagsEnum.ALL.getCode());
    }

    private static int sumVacDays(long empId, long vacationTypeId, Integer subVacationType, Integer locationFlag, String fromDateString, String toDateString) throws BusinessException {
	return sumVacDays(empId, vacationTypeId, subVacationType, locationFlag, fromDateString, toDateString, FlagsEnum.ALL.getCode());
    }

    public static int sumVacDays(long empId, long vacationTypeId, String fromDateString, String toDateString, long allowedThrshold) throws BusinessException {
	return sumVacDays(empId, vacationTypeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), fromDateString, toDateString, allowedThrshold);
    }

    /**
     * Gets the total number of not cancelled vacations days of the vacations with specific employee, type, and date.
     * 
     * @param empId
     *            the employee ID to get his vacations which matches the search criteria
     * @param vacationTypeId
     *            the vacation type is (1) Regular, (5) Compelling or (2) Sick
     * @param fromDateString
     *            a <code>String</code> containing the date which the vacation start date or end date should be after it, in mm/MM/yyyy format
     * @param toDateString
     *            a <code>String</code> containing the date which the vacation start date or end date should be before it, in mm/MM/yyyy format
     * @return the total number of vacations days which matches the search criteria
     * @throws BusinessException
     *             if database error occurs
     */
    private static int sumVacDays(long empId, long vacationTypeId, Integer subVacationType, Integer locationFlag, String fromDateString, String toDateString, long allowedThreshold) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_FROM_DATE", fromDateString);
	    qParams.put("P_TO_DATE", toDateString);
	    qParams.put("P_VACATION_TYPE_ID", vacationTypeId);
	    qParams.put("P_SUB_VACATION_TYPE", subVacationType == null ? FlagsEnum.ALL.getCode() : subVacationType);
	    qParams.put("P_LOCATION_FLAG", locationFlag == null ? FlagsEnum.ALL.getCode() : locationFlag);
	    qParams.put("P_ALLOWED_THRESHOLD", allowedThreshold);

	    Long sum = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_SUM_VAC_DAYS.getCode(), qParams).get(0);

	    Vacation vacStart = getVacationIncludeDate(fromDateString, vacationTypeId, subVacationType, locationFlag, empId, allowedThreshold);
	    int firstDifference = vacStart == null ? 0 : HijriDateService.hijriDateStringDiff(vacStart.getStartDateString(), fromDateString);
	    Vacation vacEnd = getVacationIncludeDate(toDateString, vacationTypeId, subVacationType, locationFlag, empId, allowedThreshold);
	    int endDifference = vacEnd == null ? 0 : HijriDateService.hijriDateStringDiff(toDateString, vacEnd.getEndDateString());

	    sum = sum - firstDifference - endDifference;

	    return (int) sum.longValue();
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static Vacation getVacationIncludeDate(String dateString, long vacationTypeId, Integer subVacationType, Integer locationFlag, long empId) throws BusinessException {
	return getVacationIncludeDate(dateString, vacationTypeId, subVacationType, locationFlag, empId, FlagsEnum.ALL.getCode());
    }

    /**
     * Searches for not cancelled vacations with specific employee, type, and their periods include a specific date.
     * 
     * @param dateString
     *            a <code>String</code> containing the date which should be included in the vacation period, in mm/MM/yyyy format
     * @param vacationTypeId
     *            the vacation type is (1) Regular, (5) Compelling or (2) Sick
     * @param empId
     *            the employee ID to get his vacations which matches the search criteria
     * @return the {@link Vacation} object which matches the search criteria
     * @throws BusinessException
     *             if database error occurs
     */
    private static Vacation getVacationIncludeDate(String dateString, long vacationTypeId, Integer subVacationType, Integer locationFlag, long empId, long allowedThreshold) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_DATE_BETWEEN", dateString);
	    qParams.put("P_VACATION_TYPE_ID", vacationTypeId);
	    qParams.put("P_SUB_VACATION_TYPE", subVacationType == null ? FlagsEnum.ALL.getCode() : subVacationType);
	    qParams.put("P_LOCATION_FLAG", locationFlag == null ? FlagsEnum.ALL.getCode() : locationFlag);
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_ALLOWED_THRESHOLD", allowedThreshold);

	    List<Vacation> result = DataAccess.executeNamedQuery(Vacation.class, QueryNamesEnum.HCM_VACATION_INCLUDE_DATE.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the total number of not cancelled vacations days of the vacations with specific employee and type in a specific contractual year.
     * 
     * @param empId
     *            the employee ID to get his vacations which matches the search criteria
     * @param vacationTypeId
     *            the vacation type is (1) Regular, (5) Compelling or (2) Sick
     * @param contractualYearStartDateString
     *            a <code>String</code> containing the contractual year start date, in mm/MM/yyyy format
     * @param contractualYearEndDateString
     *            a <code>String</code> containing the contractual year end date, in mm/MM/yyyy format
     * @return the total number of vacations days which matches the search criteria
     * @throws BusinessException
     *             if database error occurs
     */
    private static int sumVacDaysWithinContractualYear(long empId, long vacationTypeId, String contractualYearStartDateString, String contractualYearEndDateString) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("CONTRACTUAL_YEAR_START_DATE", contractualYearStartDateString);
	    qParams.put("CONTRACTUAL_YEAR_END_DATE", contractualYearEndDateString);
	    qParams.put("P_VACATION_TYPE_ID", vacationTypeId);

	    List<Long> result = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_SUM_VAC_DAYS_WITHIN_CONTRACTUAL_YEAR.getCode(), qParams);
	    return result.isEmpty() ? 0 : (int) result.get(0).longValue();
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static int sumRelatedDeductedBalance(long empId, long vacationTypeId, String fromDateString, String toDateString) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_FROM_DATE", fromDateString);
	    qParams.put("P_TO_DATE", toDateString);
	    qParams.put("P_VACATION_TYPE_ID", vacationTypeId);

	    Long sum = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_SUM_VAC_RELATED_DEDUCTED_BALANCE.getCode(), qParams).get(0);

	    Vacation vacStart = getVacationIncludeDate(fromDateString, vacationTypeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), empId);
	    if (vacStart != null && vacStart.getRelatedDeductedBalance() != null && vacStart.getRelatedDeductedBalance() > 0) {
		Date fromDate = HijriDateService.getHijriDate(fromDateString);

		Date relatedDeductedBalanceEndDate = HijriDateService.addSubHijriDays(vacStart.getStartDate(), vacStart.getRelatedDeductedBalance());
		if (relatedDeductedBalanceEndDate.after(fromDate))
		    sum -= (HijriDateService.hijriDateDiff(vacStart.getStartDate(), fromDate));
		else
		    sum -= vacStart.getRelatedDeductedBalance();
	    }

	    Vacation vacEnd = getVacationIncludeDate(toDateString, vacationTypeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode(), empId);
	    if (vacEnd != null && vacEnd.getRelatedDeductedBalance() != null && vacEnd.getRelatedDeductedBalance() > 0) {
		Date toDate = HijriDateService.getHijriDate(toDateString);

		int diff = HijriDateService.hijriDateDiff(vacEnd.getStartDate(), toDate);
		if (diff < vacEnd.getRelatedDeductedBalance())
		    sum -= (vacEnd.getRelatedDeductedBalance() - diff);
	    }

	    return (int) sum.longValue();
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    protected static void validateAbsenceOfLaterVacations(long empId, long vacationTypeId, String startDateString) throws BusinessException {
	try {
	    if (vacationTypeId == VacationTypesEnum.SICK.getCode())
		return;

	    List<Long> vacationTypesIds = new ArrayList<Long>();
	    if (vacationTypeId == VacationTypesEnum.REGULAR.getCode() || vacationTypeId == VacationTypesEnum.ACCOMPANY.getCode()) {
		vacationTypesIds.add(VacationTypesEnum.REGULAR.getCode());
		vacationTypesIds.add(VacationTypesEnum.ACCOMPANY.getCode());
	    } else if (vacationTypeId == VacationTypesEnum.SPORTIVE.getCode() || vacationTypeId == VacationTypesEnum.CULTURAL.getCode() || vacationTypeId == VacationTypesEnum.SOCIAL.getCode()) {
		vacationTypesIds.add(VacationTypesEnum.SPORTIVE.getCode());
		vacationTypesIds.add(VacationTypesEnum.CULTURAL.getCode());
		vacationTypesIds.add(VacationTypesEnum.SOCIAL.getCode());
	    } else {
		vacationTypesIds.add(vacationTypeId);
	    }

	    // The sub vacation type should be settled if the sick vacation will have the same limitation.
	    if (countLaterVacations(empId, vacationTypesIds.toArray(new Long[vacationTypesIds.size()]), null, startDateString) > 0)
		throw new BusinessException("error_vacationExistenceOfLaterVacations");

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static long countLaterVacations(long empId, Long[] vacationTypesIds, Integer subVacationType, String startDateString) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_VACATION_TYPES_IDS", vacationTypesIds);
	    qParams.put("P_SUB_VACATION_TYPE", subVacationType == null ? FlagsEnum.ALL.getCode() : subVacationType);
	    qParams.put("P_START_DATE", startDateString);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_LATER_VACATIONS.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static long countLaterVacationsHasJoiningDate(Long empId, String endDateString, Long vacationTypeId, Integer subVacationType) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_END_DATE", endDateString);
	    qParams.put("P_VACATION_TYPE_ID", vacationTypeId);
	    qParams.put("P_SUB_VACATION_TYPE", subVacationType == null ? FlagsEnum.ALL.getCode() : subVacationType);

	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_LATER_VACATIONS_HAS_JOINING_DATE.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /*---------------------------------------------------------- Vacation Configuration ----------------------------------------*/

    protected static void validateVacationConfiguration(VacationConfiguration vacationConfiguration, VacationConfiguration originalVacationConfiguration) throws BusinessException {

	if (vacationConfiguration.getBalance() == null)
	    throw new BusinessException("error_vacationBalanceIsMandatory");

	if (originalVacationConfiguration.getMinValue() != null && (vacationConfiguration.getMinValue() == null || vacationConfiguration.getMinValue() <= 0))
	    throw new BusinessException("error_vacationMinValueRequired");

	if (originalVacationConfiguration.getMaxValue() != null && vacationConfiguration.getMaxValue() == null)
	    throw new BusinessException("error_vacationMaxValueRequired");

	if (originalVacationConfiguration.getAllowedValues() != null && (vacationConfiguration.getAllowedValues() == null || vacationConfiguration.getAllowedValues().trim().isEmpty()))
	    throw new BusinessException("error_vacationAllowedValuesRequired");

	if (originalVacationConfiguration.getPeriodBeforeFirstVacation() != null && vacationConfiguration.getPeriodBeforeFirstVacation() == null)
	    throw new BusinessException("error_periodBeforeFirstVacRequired");

	if (originalVacationConfiguration.getFirstYearMaxVacationDays() != null && vacationConfiguration.getFirstYearMaxVacationDays() == null)
	    throw new BusinessException("error_firstYearMaxVacDaysRequired");

	if (originalVacationConfiguration.getMaxVacationsDaysPerYear() != null && vacationConfiguration.getMaxVacationsDaysPerYear() == null)
	    throw new BusinessException("error_maxVacDaysPerYearRequired");

	if (originalVacationConfiguration.getMaxVacationsDaysPerYearExceptional() != null && vacationConfiguration.getMaxVacationsDaysPerYearExceptional() == null)
	    throw new BusinessException("error_maxVacDaysPerYearExceptionalRequired");

	if (originalVacationConfiguration.getMaxValueExceptional() != null && vacationConfiguration.getMaxValueExceptional() == null)
	    throw new BusinessException("error_maxVacDaysPerRequestExceptionalRequired");

	if (originalVacationConfiguration.getBalanceFrame() != null && vacationConfiguration.getBalanceFrame() == null)
	    throw new BusinessException("error_balanceFrameValidityRequired");

	if (originalVacationConfiguration.getExternalMinVacationDays() != null && vacationConfiguration.getExternalMinVacationDays() == null)
	    throw new BusinessException("error_minExternalVacDaysRequired");

	if (originalVacationConfiguration.getAdditionalVacationDays() != null && vacationConfiguration.getAdditionalVacationDays() == null)
	    throw new BusinessException("error_additionalRegularVacDaysForSoldiersRequired");

	if (originalVacationConfiguration.getPeriods() != null && (vacationConfiguration.getPeriods() == null || vacationConfiguration.getPeriods().trim().isEmpty()))
	    throw new BusinessException("error_periodsRequired");
    }

    public static VacationConfiguration getActiveRegularVacationConfigurationForContractors(EmployeeData emp) throws BusinessException {
	VacationConfiguration activeVacationConfiguration = null;
	if (emp.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode()) {
	    List<VacationConfiguration> activeVacationConfigurations = getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.REGULAR.getCode());
	    for (VacationConfiguration vacationConfiguration : activeVacationConfigurations) {
		if (vacationConfiguration.getCategoryClassificationId() == null && emp.getCategoryClassificationId() == null) {
		    activeVacationConfiguration = vacationConfiguration;
		    break;
		} else if (vacationConfiguration.getCategoryClassificationId() != null && emp.getCategoryClassificationId() != null) {
		    activeVacationConfiguration = vacationConfiguration;
		    break;
		}
	    }
	}
	if (activeVacationConfiguration == null)
	    throw new BusinessException("error_transactionDataError");

	return activeVacationConfiguration;
    }

    /**
     * Gets the vacation periods from the database for a specific category and vacation type.
     * 
     * @param categoryId
     *            a <code>Long</code> containing the employee category which is (1) Officer, (2) Soldier or (3, 4, 5, 6, 9) Employee
     * @param vacationTypeId
     *            an <code>Integer</code> containing the vacation type which is (1) Regular, (5) Compelling or (2) Sick
     * @return List contains the {@link VacationConfiguration} objects
     * @throws NoDataException
     *             if no data found
     * @throws DatabaseException
     *             if database error occurs
     */
    public static List<VacationConfiguration> getVacationConfigurations(Long categoryId, Long vacationTypeId) throws BusinessException {
	try {
	    Map<String, Object> queryParam = new HashMap<String, Object>();
	    queryParam.put("P_CATEGORY_ID", categoryId);
	    queryParam.put("P_VACATION_TYPE_ID", vacationTypeId);
	    return DataAccess.executeNamedQuery(VacationConfiguration.class, QueryNamesEnum.HCM_GET_VACATION_CONFIGURATIONS.getCode(), queryParam);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Gets the active vacation periods from the database for a specific category and vacation type.
     * 
     * @param categoryId
     *            a <code>Long</code> containing the employee category which is (1) Officer, (2) Soldier or (3, 4, 5, 6, 9) Employee
     * @param vacationTypeId
     *            an <code>Integer</code> containing the vacation type which is (1) Regular, (5) Compelling or (2) Sick
     * @return List contains the active {@link VacationConfiguration} objects
     * @throws NoDataException
     *             if no data found
     * @throws DatabaseException
     *             if database error occurs
     */
    public static List<VacationConfiguration> getActiveVacationConfigurations(Long categoryId, Long vacationTypeId) throws BusinessException {
	return getActiveVacationConfigurations(categoryId, vacationTypeId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());
    }

    /**
     * Gets the active vacation periods from the database for a specific category and vacation type.
     * 
     * @param categoryId
     *            a <code>Long</code> containing the employee category which is (1) Officer, (2) Soldier or (3, 4, 5, 6, 9) Employee
     * @param vacationTypeId
     *            an <code>Integer</code> containing the vacation type which is (1) Regular, (5) Compelling or (2) Sick
     * @return List contains the active {@link VacationConfiguration} objects
     * @throws NoDataException
     *             if no data found
     * @throws DatabaseException
     *             if database error occurs
     */
    public static List<VacationConfiguration> getActiveVacationConfigurations(Long categoryId, Long vacationTypeId, Integer subVacationType, Integer locationFlag) throws BusinessException {
	try {
	    Map<String, Object> queryParam = new HashMap<String, Object>();
	    queryParam.put("P_CATEGORY_ID", categoryId);
	    queryParam.put("P_VACATION_TYPE_ID", vacationTypeId);
	    queryParam.put("P_SUB_VACATION_TYPE", subVacationType == null ? FlagsEnum.ALL.getCode() : subVacationType);
	    queryParam.put("P_LOCATION_FLAG", locationFlag == null ? FlagsEnum.ALL.getCode() : locationFlag);

	    return DataAccess.executeNamedQuery(VacationConfiguration.class, QueryNamesEnum.HCM_GET_ACTIVE_VACATION_CONFIGURATIONS.getCode(), queryParam);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}