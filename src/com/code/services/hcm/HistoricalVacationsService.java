package com.code.services.hcm;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.vacations.HistoricalVacationTransaction;
import com.code.dal.orm.hcm.vacations.HistoricalVacationTransactionData;
import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.dal.orm.hcm.vacations.VacationConfiguration;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.GendersEnum;
import com.code.enums.LocationFlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RequestTypesEnum;
import com.code.enums.SubVacationTypesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.VacationTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.buswfcoop.EmployeesTransactionsConflictValidator;
import com.code.services.config.ETRConfigurationService;
import com.code.services.util.HijriDateService;

public class HistoricalVacationsService extends BaseService {
    private static final int BALANCE_YEAR_DAYS = HIJRI_YEAR_DAYS;

    private static final int FULL_HIJRI_MONTH_DAYS = 30;

    private HistoricalVacationsService() {
    }
    /*---------------------------------------------------------- Operations ---------------------------------------------*/

    public static void insertHistoricalVacationTransaction(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary, CustomSession... useSession) throws BusinessException {
	validateDecisionNumber(historicalVacationTransaction);
	if (historicalVacationTransaction.getRequestType() != RequestTypesEnum.CANCEL.getCode()) {
	    if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode())
		historicalVacationTransaction.setPaidVacationType(VacationsBusinessRulesService.getPaidVacationType(historicalVacationTransaction.getVacationTypeId(), historicalVacationTransaction.getSubVacationType(), vacationBeneficiary, historicalVacationTransaction.getStartDate(), null));
	    validateHistoricalVacationRules(historicalVacationTransaction, vacationBeneficiary);
	}
	insertHistoricalVacation(historicalVacationTransaction, useSession);
    }

    public static void modifyHistoricalVacationTransaction(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary, boolean signHistoricalVacationFlag, boolean skipValidationFlag, CustomSession... useSession) throws BusinessException {
	HistoricalVacationTransactionData vacationData = getHistoricalVacationTransactionDataById(historicalVacationTransaction.getId());
	if (vacationData != null) {
	    validateDecisionNumber(historicalVacationTransaction);
	    if (historicalVacationTransaction.getRequestType() != RequestTypesEnum.CANCEL.getCode() && !skipValidationFlag) {
		validateHistoricalVacationRules(historicalVacationTransaction, vacationBeneficiary);
	    }
	    modifyHistoricalVacation(historicalVacationTransaction, signHistoricalVacationFlag, useSession);
	} else {
	    throw new BusinessException("error_vacationDeleted");
	}
    }

    public static void deleteHistoricalVacationTransaction(HistoricalVacationTransaction historicalVacationTransaction, CustomSession... useSession) throws BusinessException {

	HistoricalVacationTransactionData vacationData = getHistoricalVacationTransactionDataById(historicalVacationTransaction.getId());
	if (vacationData.getApprovedFlag() == FlagsEnum.ON.getCode())
	    throw new BusinessException("error_vacationHasBeenModifiedOrCanceled");
	// if the deletion for initial cancel/modify vacation get the parent and set the active flag to on
	if (historicalVacationTransaction.getRequestType() != RequestTypesEnum.NEW.getCode()) {
	    HistoricalVacationTransaction oldHistoricalVacation = getHistoricalVacationTransactionDataById(historicalVacationTransaction.getHistoricalVacationParentId()).getHistoricalVacationTransaction();
	    oldHistoricalVacation.setActiveFlag(FlagsEnum.ON.getCode());
	    modifyHistoricalVacation(oldHistoricalVacation, false, useSession);
	}
	deleteHistoricalVacation(historicalVacationTransaction, useSession);
    }

    /*---------------------------------------------------------- Validations --------------------------------------------*/
    public static void validateOldHistoricalVacationActivationStatus(Long parentVacationId, Long vacationId) throws BusinessException {
	HistoricalVacationTransactionData oldHistoricalVacationTransactionData = getHistoricalVacationTransactionDataByParentId(parentVacationId, vacationId);
	if (oldHistoricalVacationTransactionData != null)
	    throw new BusinessException("error_vacationHasBeenModifiedOrCanceled");
    }

    public static void validateHistoricalVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {
	validateHistoricalVacationData(historicalVacationTransaction, vacationBeneficiary);
	validateVacationDates(historicalVacationTransaction);
	validateVacationLocation(historicalVacationTransaction);

	EmployeesTransactionsConflictValidator
		.validateEmployeesTransactionsConflicts(
			new Long[] { vacationBeneficiary.getEmpId() }, new String[] { vacationBeneficiary.getName() },
			TransactionClassesEnum.VACATIONS.getCode(), historicalVacationTransaction.getRequestType(),
			historicalVacationTransaction.getVacationTypeId(), FlagsEnum.ALL.getCode(), new String[] { historicalVacationTransaction.getStartDateString() }, new String[] { historicalVacationTransaction.getEndDateString() },
			historicalVacationTransaction.getRequestType().equals(RequestTypesEnum.MODIFY.getCode()) ? historicalVacationTransaction.getVacationTransactionId() : null, null);

	if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.REGULAR.getCode())
	    validateHistoricalRegularVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.COMPELLING.getCode())
	    validateHistoricalCompellingVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.SICK.getCode())
	    validateHistoricalSickVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.FIELD.getCode())
	    validateHistoricalFieldVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.EXCEPTIONAL.getCode())
	    validateHistoricalExceptionalVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.STUDY.getCode())
	    validateHistoricalStudyVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.EXAM.getCode())
	    validateHistoricalExamVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.ACCOMPANY.getCode())
	    validateHistoricalAccompanyVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.RELIEF.getCode())
	    validateHistoricalReliefVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.SPORTIVE.getCode()
		|| historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.CULTURAL.getCode()
		|| historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.SOCIAL.getCode())
	    validateHistoricalSportiveCultureSocialVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.COMPENSATION.getCode())
	    validateHistoricalCompensationVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.MATERNITY.getCode())
	    validateHistoricalMaternityVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.MOTHERHOOD.getCode())
	    validateHistoricalMotherhoodVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.ORPHAN_CARE.getCode())
	    validateHistoricalOrphanCareVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.DEATH_WAITING_PERIOD.getCode())
	    validateHistoricalDeathWaitingPeriodVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.DEATH_OF_RELATIVE.getCode())
	    validateHistoricalDeathOfRelativeVacationRules(historicalVacationTransaction, vacationBeneficiary);
	else if (historicalVacationTransaction.getVacationTypeId() == VacationTypesEnum.NEW_BABY.getCode())
	    validateHistoricalNewBabyVacationRules(historicalVacationTransaction, vacationBeneficiary);
    }

    protected static void validateVacationDates(HistoricalVacationTransaction historicalVacationTransaction) throws BusinessException {
	if (historicalVacationTransaction.getRequestType().equals(RequestTypesEnum.NEW.getCode()) && !HijriDateService.isValidHijriDate(historicalVacationTransaction.getStartDate()))
	    throw new BusinessException("error_invalidHijriDate");
	if (historicalVacationTransaction.getStartDate().after(HijriDateService.getHijriSysDate()))
	    throw new BusinessException("error_startDateBeforeToday");
    }

    protected static void validateVacationLocation(HistoricalVacationTransaction historicalVacationTransaction) throws BusinessException {
	if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode()
		&& historicalVacationTransaction.getLocationFlag().equals(LocationFlagsEnum.EXTERNAL.getCode())
		&& historicalVacationTransaction.getLocation().contains(getMessage("label_ksa")))
	    throw new BusinessException("error_invalidLocation");
    }

    private static void validateHistoricalVacationData(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	if (historicalVacationTransaction.getDecisionNumber() == null || historicalVacationTransaction.getDecisionNumber().trim().equals(""))
	    throw new BusinessException("error_decisionNumberMandatory");
	if (historicalVacationTransaction.getDecisionDate() == null)
	    throw new BusinessException("error_decisionDateIsMandatory");

	if (vacationBeneficiary.getRecruitmentDate().after(historicalVacationTransaction.getStartDate()))
	    throw new BusinessException("error_historicalVacationBeforeRecruitmentDate");

	if (historicalVacationTransaction.getPeriod() == null || historicalVacationTransaction.getPeriod() == 0)
	    throw new BusinessException("error_periodMandatory");
	if (historicalVacationTransaction.getPeriod() < 0)
	    throw new BusinessException("error_periodNotNegative");
	if (historicalVacationTransaction.getContactNumber() == null || historicalVacationTransaction.getContactNumber().trim().equals(""))
	    throw new BusinessException("error_contactNumberMandatory");
	if (historicalVacationTransaction.getContactAddress() == null || historicalVacationTransaction.getContactAddress().trim().equals(""))
	    throw new BusinessException("error_contactAddressMandatory");

    }

    public static void validateDecisionNumber(HistoricalVacationTransaction historicalVacationTransaction) throws BusinessException {
	if (countHistoricalVacationsByDecisionNumber(historicalVacationTransaction.getDecisionNumber(), historicalVacationTransaction.getId()) > 0
		|| VacationsService.countVacationByDecisionNumber(historicalVacationTransaction.getDecisionNumber(), historicalVacationTransaction.getVacationTransactionId()) > 0)
	    throw new BusinessException("error_vacationDecisionNumberRepeted");
    }

    protected static void validateHistoricalRegularVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    long categoryId = vacationBeneficiary.getCategoryId().longValue();

	    VacationConfiguration activeVacationConfiguration = null;
	    if (categoryId == CategoriesEnum.CONTRACTORS.getCode())
		activeVacationConfiguration = VacationsBusinessRulesService.getActiveRegularVacationConfigurationForContractors(vacationBeneficiary);
	    else
		activeVacationConfiguration = VacationsBusinessRulesService.getActiveVacationConfigurations(categoryId, historicalVacationTransaction.getVacationTypeId()).get(0);

	    if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode()) {
		if (categoryId == CategoriesEnum.OFFICERS.getCode() || categoryId == CategoriesEnum.SOLDIERS.getCode()) {
		    Date correctDate = HijriDateService.addSubHijriDays(vacationBeneficiary.getRecruitmentDate(), activeVacationConfiguration.getPeriodBeforeFirstVacation() - 1, true);
		    if (historicalVacationTransaction.getStartDate().before(correctDate))
			throw new BusinessException("error_vacWorkStartDate", new Object[] { activeVacationConfiguration.getPeriodBeforeFirstVacation() / FULL_HIJRI_MONTH_DAYS });
		}
	    }
	    HistoricalVacationTransaction oldVacationData = new HistoricalVacationTransaction();
	    if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode())
		oldVacationData = getHistoricalVacationTransactionDataById(historicalVacationTransaction.getHistoricalVacationParentId()).getHistoricalVacationTransaction();
	    else if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode() && historicalVacationTransaction.getId() != null)
		oldVacationData = getHistoricalVacationTransactionDataById(historicalVacationTransaction.getId()).getHistoricalVacationTransaction();

	    int previousVacationPeriod = historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode() ? 0 : oldVacationData.getPeriod();
	    if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode() && oldVacationData.getPeriod() != null && oldVacationData.getPeriod().equals(historicalVacationTransaction.getPeriod()))
		throw new BusinessException("error_sameModifyPeriodError");

	    // Check vacation periods for officers.
	    if (categoryId == CategoriesEnum.OFFICERS.getCode()) {

		if (historicalVacationTransaction.getPeriod() < activeVacationConfiguration.getMinValue() || historicalVacationTransaction.getPeriod() > activeVacationConfiguration.getMaxValue())
		    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });

		String vacationStartYear = historicalVacationTransaction.getStartDateString().split("/")[2];
		String vacationEndYear = historicalVacationTransaction.getEndDateString().split("/")[2];

		// Total vacations within first service year less than or equal activePeriod.getFirstYearMaxVacationDays().
		Date firstServiceYearEnd = HijriDateService.addSubHijriDays(vacationBeneficiary.getRecruitmentDate(), BALANCE_YEAR_DAYS - 1);
		if (historicalVacationTransaction.getStartDate().compareTo(firstServiceYearEnd) <= 0) {
		    if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode() || (historicalVacationTransaction.getPeriod() > oldVacationData.getPeriod() && oldVacationData.getEndDate().before(firstServiceYearEnd))) {
			int firstServiceYearRegularVacationsSum = VacationsBusinessRulesService.sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), vacationBeneficiary.getRecruitmentDateString(), HijriDateService.getHijriDateString(firstServiceYearEnd));
			Date vacationEndDate = HijriDateService.addSubHijriDays(historicalVacationTransaction.getStartDate(), historicalVacationTransaction.getPeriod() - 1);
			int twoYearsDifference = 0;
			if (vacationEndDate.compareTo(firstServiceYearEnd) > 0) // end vacation is after end of first year
			    twoYearsDifference = HijriDateService.hijriDateDiff(firstServiceYearEnd, vacationEndDate);

			if (firstServiceYearRegularVacationsSum + historicalVacationTransaction.getPeriod() - previousVacationPeriod - twoYearsDifference > activeVacationConfiguration.getFirstYearMaxVacationDays())
			    throw new BusinessException("error_firstYearVacationMaxPeriod", new Object[] { activeVacationConfiguration.getFirstYearMaxVacationDays() });
		    }
		}

		// Total vacations per Hijri year are less than or equal activePeriod.getMaxVacationsDaysPerYear().
		if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode() || historicalVacationTransaction.getPeriod() > oldVacationData.getPeriod()) {
		    int regularVacationsSum = VacationsBusinessRulesService.sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), "01/01/" + vacationStartYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(vacationStartYear)).getHijriMonthLength() + "/12/" + vacationStartYear);
		    int twoYearsDifference = 0;
		    if (!vacationStartYear.equals(vacationEndYear))
			twoYearsDifference = HijriDateService.hijriDateStringDiff("01/01/" + vacationEndYear, historicalVacationTransaction.getEndDateString()) + 1;

		    String exceptionalVacationStartYear = (Integer.parseInt(vacationStartYear) - 1) + "";
		    int exceptionalVacationsSum = VacationsBusinessRulesService.sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.EXCEPTIONAL.getCode(), "01/01/" + exceptionalVacationStartYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(exceptionalVacationStartYear)).getHijriMonthLength() + "/12/" + exceptionalVacationStartYear);

		    if ((regularVacationsSum + historicalVacationTransaction.getPeriod() - twoYearsDifference - previousVacationPeriod) > (activeVacationConfiguration.getMaxVacationsDaysPerYear() - exceptionalVacationsSum)) {
			if (exceptionalVacationsSum > 0)
			    throw new BusinessException("error_regularVacationsMaxDaysPerYearInCaseExceptionalExist", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYear() - exceptionalVacationsSum, exceptionalVacationsSum });
			else
			    throw new BusinessException("error_vacationsMaxDaysPerYear", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYear() });
		    }
		}
	    }

	    // Check vacation periods and counts for soldiers.
	    else if (categoryId == CategoriesEnum.SOLDIERS.getCode()) {

		if (historicalVacationTransaction.getPeriod() < activeVacationConfiguration.getMinValue() || historicalVacationTransaction.getPeriod() > activeVacationConfiguration.getMaxValue())
		    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });

		String vacationStartYear = historicalVacationTransaction.getStartDateString().split("/")[2];
		String vacationEndYear = historicalVacationTransaction.getEndDateString().split("/")[2];
		// Total vacations per Hijri year are less than or equal activePeriod.getMaxVacationsDaysPerYear().
		if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode() || historicalVacationTransaction.getPeriod() > oldVacationData.getPeriod()) {

		    int regularVacationsSum = VacationsBusinessRulesService.sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), "01/01/" + vacationStartYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(vacationStartYear)).getHijriMonthLength() + "/12/" + vacationStartYear);
		    int twoYearsDifference = 0;
		    if (!vacationStartYear.equals(vacationEndYear))
			twoYearsDifference = HijriDateService.hijriDateStringDiff("01/01/" + vacationEndYear, historicalVacationTransaction.getEndDateString()) + 1;

		    if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode()) {
			// Additional regular vacation for soldiers must be (activePeriod.getAdditionalVacationDays()) days
			if (regularVacationsSum == activeVacationConfiguration.getMaxVacationsDaysPerYear() && historicalVacationTransaction.getPeriod() != activeVacationConfiguration.getAdditionalVacationDays())
			    throw new BusinessException("error_soldiersAdditionalRegularVacPeriod", new Object[] { activeVacationConfiguration.getAdditionalVacationDays() });

			if ((regularVacationsSum != activeVacationConfiguration.getMaxVacationsDaysPerYear()) && ((regularVacationsSum + historicalVacationTransaction.getPeriod() - twoYearsDifference - previousVacationPeriod) > activeVacationConfiguration.getMaxVacationsDaysPerYear()))
			    throw new BusinessException("error_vacationsMaxDaysPerYear", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYear() });

		    } else { // Modification

			// if he takes vacations greater than (activePeriod.getMaxVacationsDaysPerYear()) days, so he wants to modify the additional vacation now, which is not applicable because the additional vacation should be (activePeriod.getAdditionalVacationDays()) days.
			if (regularVacationsSum > activeVacationConfiguration.getMaxVacationsDaysPerYear())
			    throw new BusinessException("error_soldiersAdditionalRegularVacPeriod", new Object[] { activeVacationConfiguration.getAdditionalVacationDays() });

			if (regularVacationsSum <= activeVacationConfiguration.getMaxVacationsDaysPerYear()
				&& (regularVacationsSum + historicalVacationTransaction.getPeriod() - twoYearsDifference - previousVacationPeriod) > activeVacationConfiguration.getMaxVacationsDaysPerYear())
			    throw new BusinessException("error_vacationsMaxDaysPerYear", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYear() });
		    }
		}

		if (historicalVacationTransaction.getLocationFlag() == LocationFlagsEnum.EXTERNAL.getCode()) {
		    String[] locationArr = historicalVacationTransaction.getLocation().split("/");
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

		if (historicalVacationTransaction.getPeriod() < activeVacationConfiguration.getMinValue()) {

		    String vacationStartYear = HijriDateService.hijriToGregDateString(historicalVacationTransaction.getStartDateString()).split("/")[2];
		    if (!vacationStartYear.equals(HijriDateService.hijriToGregDateString(historicalVacationTransaction.getEndDateString()).split("/")[2]))
			throw new BusinessException("error_vacationStartAndEndDatesInSameYear");

		    int vacDaysLessThanThreshold = VacationsBusinessRulesService.sumVacDays(historicalVacationTransaction.getEmpId(), VacationTypesEnum.REGULAR.getCode(), HijriDateService.gregToHijriDateString("01/01/" + vacationStartYear), HijriDateService.gregToHijriDateString("31/12/" + vacationStartYear), activeVacationConfiguration.getMinValue());
		    if (vacDaysLessThanThreshold + historicalVacationTransaction.getPeriod() - (previousVacationPeriod < activeVacationConfiguration.getMinValue() ? previousVacationPeriod : 0) > activeVacationConfiguration.getMinValue()) {
			if (vacDaysLessThanThreshold < activeVacationConfiguration.getMinValue())
			    throw new BusinessException("error_vacRequestModifiedLimits", new Object[] { activeVacationConfiguration.getMinValue() - vacDaysLessThanThreshold, activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });
			else
			    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });
		    }
		}

		if (isPeriodsExceed) {
		    if (historicalVacationTransaction.getPeriod() > activeVacationConfiguration.getMaxValueExceptional())
			throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValueExceptional() });
		} else {
		    if (historicalVacationTransaction.getPeriod() > activeVacationConfiguration.getMaxValue())
			throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });
		}

		// Total vacations per Hijri year are less than or equal activePeriod.getMaxVacationsDaysPerYear().
		if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode() || historicalVacationTransaction.getPeriod() > oldVacationData.getPeriod()) {

		    String vacationStartYear = historicalVacationTransaction.getStartDateString().split("/")[2];
		    String vacationEndYear = historicalVacationTransaction.getEndDateString().split("/")[2];
		    int regularVacationsSum = VacationsBusinessRulesService.sumVacDays(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), "01/01/" + vacationStartYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(vacationStartYear)).getHijriMonthLength() + "/12/" + vacationStartYear);
		    int twoYearsDifference = 0;
		    if (!vacationStartYear.equals(vacationEndYear))
			twoYearsDifference = HijriDateService.hijriDateStringDiff("01/01/" + vacationEndYear, historicalVacationTransaction.getEndDateString()) + 1;

		    if (isPeriodsExceed) {
			if ((regularVacationsSum + historicalVacationTransaction.getPeriod() - twoYearsDifference - previousVacationPeriod) > activeVacationConfiguration.getMaxVacationsDaysPerYearExceptional())
			    throw new BusinessException("error_vacationsMaxDaysPerYear", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYearExceptional() });
		    } else {
			if ((regularVacationsSum + historicalVacationTransaction.getPeriod() - twoYearsDifference - previousVacationPeriod) > activeVacationConfiguration.getMaxVacationsDaysPerYear())
			    throw new BusinessException("error_vacationsMaxDaysPerYear", new Object[] { activeVacationConfiguration.getMaxVacationsDaysPerYear() });
		    }
		}
	    } else if (categoryId == CategoriesEnum.CONTRACTORS.getCode()) {
		if (historicalVacationTransaction.getPeriod() < activeVacationConfiguration.getMinValue() || historicalVacationTransaction.getPeriod() > activeVacationConfiguration.getMaxValue()) {
		    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });
		}
	    }
	    if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode()) {
		if (historicalVacationTransaction.getPeriod() > (VacationsBusinessRulesService.calculateEmpRegularBalance(vacationBeneficiary, historicalVacationTransaction.getStartDate(), activeVacationConfiguration, historicalVacationTransaction.getPeriod()) + ((historicalVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode()) ? 1 : 0)))
		    throw new BusinessException("error_vacCredit");
		validateHistoricalRegularVacationEffect(historicalVacationTransaction, vacationBeneficiary);
	    } else {
		int balance = (int) VacationsBusinessRulesService.calculateEmpRegularBalance(vacationBeneficiary, historicalVacationTransaction.getStartDate(), activeVacationConfiguration, historicalVacationTransaction.getPeriod());
		balance = balance + oldVacationData.getPeriod();
		if (historicalVacationTransaction.getPeriod() > balance)
		    throw new BusinessException("error_vacCredit");
	    }

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * @param HistoricalVacationTransaction
     *            vacationBeneficiary algorithm: 1st get the last regular vacation data 2nd get the list of vacations between historical vacation and last vacation 3rd loop over the list and calculate if this vacation will cause a negative balance to any future balance if true throw error_regularBalanceConflect
     **/
    private static void validateHistoricalRegularVacationEffect(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {
	try {
	    VacationData lastVacation = VacationsService.getLastVacationData(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode());
	    if (lastVacation != null) {
		List<Vacation> vacationsList = getVacationBetweenDates(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), historicalVacationTransaction.getStartDateString(), lastVacation.getStartDateString(), FlagsEnum.ALL.getCode());
		int balance;
		for (Vacation vacation : vacationsList) {
		    balance = (int) calculateEmpHistoricalRegularBalance(EmployeesService.getEmployeeData(vacationBeneficiary.getEmpId()), vacation.getStartDate(), null, -1);
		    if ((balance - historicalVacationTransaction.getPeriod() - vacation.getPeriod()) < 0)
			throw new BusinessException("error_regularBalanceConflict");

		}
	    }
	} catch (BusinessException e) {
	    throw e;
	}
    }

    protected static void validateHistoricalCompellingVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	try {

	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // Check vacation periods.
	    if (vacationBeneficiary.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) {
		String vacationStartYear = historicalVacationTransaction.getStartDateString().split("/")[2];
		if (!vacationStartYear.equals(historicalVacationTransaction.getEndDateString().split("/")[2]))
		    throw new BusinessException("error_vacationStartAndEndDatesInSameYear");
	    } else if (vacationBeneficiary.getCategoryId() == CategoriesEnum.PERSONS.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.USERS.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.WAGES.getCode() || vacationBeneficiary.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()) {
		String vacationStartYear = HijriDateService.hijriToGregDateString(historicalVacationTransaction.getStartDateString()).split("/")[2];
		if (!vacationStartYear.equals(HijriDateService.hijriToGregDateString(historicalVacationTransaction.getEndDateString()).split("/")[2]))
		    throw new BusinessException("error_vacationStartAndEndDatesInSameYear");

		if ((int) calculateEmpHistoricalRegularBalance(vacationBeneficiary, historicalVacationTransaction.getStartDate(), null, -1) > 0)
		    throw new BusinessException("error_regularVacationBalanceIsNotZero");
	    }

	    VacationConfiguration activeVacationConfiguration = VacationsBusinessRulesService.getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), historicalVacationTransaction.getVacationTypeId()).get(0);

	    if (vacationBeneficiary.getCategoryId() == CategoriesEnum.CONTRACTORS.getCode()) {
		if (historicalVacationTransaction.getPeriod() < activeVacationConfiguration.getMinValue() || historicalVacationTransaction.getPeriod() > activeVacationConfiguration.getMaxValue()) {
		    throw new BusinessException("error_vacationRequestLimits", new Object[] { activeVacationConfiguration.getMinValue(), activeVacationConfiguration.getMaxValue() });
		}
	    }

	    if ((vacationBeneficiary.getCategoryId() == CategoriesEnum.SOLDIERS.getCode())
		    && (historicalVacationTransaction.getLocationFlag() == LocationFlagsEnum.EXTERNAL.getCode())) {
		String[] locationArr = historicalVacationTransaction.getLocation().split("/");
		if (locationArr.length > ETRConfigurationService.getVacationMaxNumberOfCountriesForSoldiersPerRequest())
		    throw new BusinessException("error_soldiersMaxCountriesPerRegVac");
	    }

	    if (historicalVacationTransaction.getPeriod() < activeVacationConfiguration.getMinValue())
		throw new BusinessException("error_vacationMinValue", new Object[] { activeVacationConfiguration.getMinValue() });

	    if (historicalVacationTransaction.getPeriod() > calculateEmpHistoricalCompellingBalance(vacationBeneficiary, historicalVacationTransaction.getStartDate(), activeVacationConfiguration))
		throw new BusinessException("error_vacCredit");

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    protected static void validateHistoricalSickVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // work injury sick vacation is allowed only for the soldiers
	    if (historicalVacationTransaction.getSubVacationType() == SubVacationTypesEnum.SUB_TYPE_TWO.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.SOLDIERS.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    HistoricalVacationTransaction oldSickVacationData = new HistoricalVacationTransaction();
	    if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode())
		oldSickVacationData = getHistoricalVacationTransactionDataById(historicalVacationTransaction.getHistoricalVacationParentId()).getHistoricalVacationTransaction();
	    else if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode() && historicalVacationTransaction.getId() != null)
		oldSickVacationData = getHistoricalVacationTransactionDataById(historicalVacationTransaction.getId()).getHistoricalVacationTransaction();

	    List<VacationConfiguration> activeVacationConfigurations = VacationsBusinessRulesService.getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), historicalVacationTransaction.getVacationTypeId(), historicalVacationTransaction.getSubVacationType(), FlagsEnum.ALL.getCode());
	    VacationConfiguration activeVacationConfiguration = activeVacationConfigurations.get(0);
	    if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode() && oldSickVacationData.getPeriod() != null && oldSickVacationData.getPeriod().equals(historicalVacationTransaction.getPeriod()))
		throw new BusinessException("error_sameModifyPeriodError");
	    // Check vacation periods.
	    String sickBalanceString = calculateEmpHistoricalSickBalance(vacationBeneficiary, historicalVacationTransaction.getSubVacationType(), historicalVacationTransaction.getStartDate(), activeVacationConfigurations);

	    int sickBalance = -1;
	    int sickBalanceType = -1;
	    if (sickBalanceString != null) {
		sickBalance = Integer.parseInt(sickBalanceString.split(",")[0]);
		sickBalanceType = Integer.parseInt(sickBalanceString.split(",")[1]);
	    }

	    int previousVacationPeriod = historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode() ? 0 : oldSickVacationData.getPeriod();

	    if (historicalVacationTransaction.getPeriod() < activeVacationConfiguration.getMinValue())
		throw new BusinessException("error_vacationMinValue", new Object[] { activeVacationConfiguration.getMinValue() });

	    if (((sickBalance == -1) && (historicalVacationTransaction.getRequestType() == RequestTypesEnum.NEW.getCode()))
		    || ((historicalVacationTransaction.getPeriod() - previousVacationPeriod) > sickBalance)
		    || ((historicalVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode())
			    && (oldSickVacationData != null)
			    && (historicalVacationTransaction.getPeriod() > oldSickVacationData.getPeriod())
			    && ((sickBalance == -1) || (oldSickVacationData.getPaidVacationType().intValue() != sickBalanceType))))
		throw new BusinessException("error_vacCredit");

	    // validate if the vacation is between 2 frames
	    Date frameStartDate = null, frameEndDate = historicalVacationTransaction.getStartDate();
	    if (CategoriesEnum.SOLDIERS.getCode() == vacationBeneficiary.getCategoryId() || CategoriesEnum.CONTRACTORS.getCode() == vacationBeneficiary.getCategoryId()) {
		frameEndDate = vacationBeneficiary.getRecruitmentDate();
	    } else {
		Vacation firstSickVacation = VacationsBusinessRulesService.getFirstVacation(vacationBeneficiary.getEmpId(), VacationTypesEnum.SICK.getCode(), historicalVacationTransaction.getSubVacationType());
		if (firstSickVacation != null && firstSickVacation.getStartDate().before(historicalVacationTransaction.getStartDate()))
		    frameEndDate = firstSickVacation.getStartDate();
	    }

	    frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    do {
		frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
		frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/" + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfigurations.get(0).getBalanceFrame()));
		frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
	    } while (!HijriDateService.isDateBetween(frameStartDate, frameEndDate, historicalVacationTransaction.getStartDate()));

	    if (historicalVacationTransaction.getEndDate().after(frameEndDate)) {
		Integer periodAtCurrentFrame = HijriDateService.hijriDateDiff(historicalVacationTransaction.getStartDate(), frameEndDate) + 1;
		Date nextFrameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		Integer periodAtNextFrame = HijriDateService.hijriDateDiff(nextFrameStartDate, historicalVacationTransaction.getEndDate()) + 1;

		throw new BusinessException("error_sickVacationBetweenTwoFrames", new Object[] { historicalVacationTransaction.getPeriod(), historicalVacationTransaction.getStartDateString(), periodAtCurrentFrame, HijriDateService.getHijriDateString(frameEndDate), HijriDateService.getHijriDateString(nextFrameStartDate), periodAtNextFrame, historicalVacationTransaction.getEndDateString() });
	    }
	    // external vacation should be greater than or equal 15 days for officers only.
	    if (vacationBeneficiary.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())
		    && historicalVacationTransaction.getLocationFlag() == LocationFlagsEnum.EXTERNAL.getCode()
		    && historicalVacationTransaction.getPeriod() < activeVacationConfiguration.getExternalMinVacationDays())
		throw new BusinessException("error_sickVacationExternalPeriodLimit", new Object[] { activeVacationConfiguration.getExternalMinVacationDays() });
	    if (historicalVacationTransaction.getRequestType().equals(RequestTypesEnum.MODIFY.getCode())) {
		if (oldSickVacationData.getPaidVacationType().equals(sickBalanceType)) {
		    int balance = sickBalance + oldSickVacationData.getPeriod();
		    if (historicalVacationTransaction.getPeriod() > balance)
			throw new BusinessException("error_vacCredit");

		} else {
		    if (historicalVacationTransaction.getPeriod() > oldSickVacationData.getPeriod()) {
			throw new BusinessException("error_vacCredit");
		    }
		}

	    } else {
		validateSickHistoricalVacationEffect(historicalVacationTransaction, vacationBeneficiary, frameEndDate);
	    }
	    /*
	     * // internal vacation has been changed to be external vacation. if (request.getLocationFlag() == LocationFlagsEnum.INTERNAL_EXTERNAL.getCode()) {
	     * 
	     * int extendedExternalPeriod = HijriDateService.hijriDateDiff(historicalVacationTransaction.getExtStartDate(), request.getExtEndDate()) + 1; // external vacation should be greater than or equal 15 days for officers only. if ((vacationBeneficiary.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()) || vacationBeneficiary.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) && extendedExternalPeriod < activeVacationConfiguration.getExternalMinVacationDays()) throw new
	     * BusinessException("error_sickVacationExternalPeriodLimit", new Object[] { activeVacationConfiguration.getExternalMinVacationDays() });
	     * 
	     * // extended external period shouldn't be equal to the vacation period if (extendedExternalPeriod == request.getPeriod()) throw new BusinessException("error_sickVacationExternalPeriodEqualTotal");
	     * 
	     * // extended external period shouldn't be out the total period if ((!HijriDateService.isDateBetween(request.getStartDate(), request.getEndDate(), request.getExtStartDate())) || (!HijriDateService.isDateBetween(request.getStartDate(), request.getEndDate(), request.getExtEndDate()))) throw new BusinessException("error_sickVacationExternalPeriodOutOriginalPeriod");
	     */
	    // }
	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    /**
     * @param HistoricalVacationTransaction
     *            employee frameEndDate
     * @algorithm get the list of vacations after this vacation to end of frame compare between this vacation and the next vacations if the paid vacation types are different throw error_sickPaidTypeConflict
     * 
     */
    private static void validateSickHistoricalVacationEffect(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData employee, Date frameEndDate) throws BusinessException {

	List<Vacation> vacationAfterHistoricalVacation = getVacationBetweenDates(employee.getEmpId(), historicalVacationTransaction.getVacationTypeId(), historicalVacationTransaction.getStartDateString(), HijriDateService.getHijriDateString(frameEndDate), historicalVacationTransaction.getSubVacationType());

	if (!vacationAfterHistoricalVacation.isEmpty()) {
	    for (Vacation vacation : vacationAfterHistoricalVacation)
		if (!historicalVacationTransaction.getPaidVacationType().equals(vacation.getPaidVacationType()))
		    throw new BusinessException("error_sickPaidTypeConflict");
	}
    }

    private static void validateHistoricalFieldVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	try {
	    if (vacationBeneficiary.getRecruitmentDate() == null)
		throw new BusinessException("error_empRecruitmentDateMandatoryForVacations");

	    // vacation start date must not exceed today plus 90 days
	    if (historicalVacationTransaction.getStartDate().after(HijriDateService.addSubHijriDays(HijriDateService.getHijriSysDate(), ETRConfigurationService.getVacationRequestAllowance())))
		throw new BusinessException("error_vacWorkStartExceedLimit");

	    if (vacationBeneficiary.getCategoryId() != CategoriesEnum.OFFICERS.getCode() && vacationBeneficiary.getCategoryId() != CategoriesEnum.SOLDIERS.getCode())
		throw new BusinessException("error_vacationTypeIsNotAllowedForThisCategory");

	    if (vacationBeneficiary.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() && historicalVacationTransaction.getLocationFlag().intValue() != LocationFlagsEnum.INTERNAL.getCode())
		throw new BusinessException("error_vacIsInternalOnly");

	    VacationConfiguration activeVacationConfiguration = VacationsBusinessRulesService.getActiveVacationConfigurations(vacationBeneficiary.getCategoryId(), historicalVacationTransaction.getVacationTypeId()).get(0);

	    // Check vacation periods.
	    if (!("," + activeVacationConfiguration.getAllowedValues() + ",").contains("," + historicalVacationTransaction.getPeriod() + ","))
		throw new BusinessException("error_vacationPeriodAllowedValues", new Object[] { activeVacationConfiguration.getAllowedValues().replaceAll(",", " , ") });

	    if (historicalVacationTransaction.getPeriod() > calculateEmpHistoricalFieldBalance(vacationBeneficiary, historicalVacationTransaction.getStartDate(), activeVacationConfiguration))
		throw new BusinessException("error_vacCredit");

	} catch (BusinessException e) {
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void validateHistoricalExceptionalVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {
	VacationsBusinessRulesService.validateExceptionalVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
	validateHistoricalExceptionalEffect(historicalVacationTransaction, vacationBeneficiary);
    }

    /**
     * 
     * @param historicalVacationTransaction
     * @param vacationBeneficiary
     * @algorithm get list of regular vacations after this vacation to the end of year if it is not empty throw error_exceptionalAffectRegular because you can't add exceptional between regular vacations in the same year
     * @throws BusinessException
     */
    private static void validateHistoricalExceptionalEffect(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {
	String vacationEndYear = historicalVacationTransaction.getEndDateString().split("/")[2];
	List<Vacation> vacationsList = getVacationBetweenDates(vacationBeneficiary.getEmpId(), VacationTypesEnum.REGULAR.getCode(), historicalVacationTransaction.getStartDateString(), HijriDateService.getHijriCalendar(12, Integer.valueOf(vacationEndYear)).getHijriMonthLength() + "/12/" + vacationEndYear, FlagsEnum.ALL.getCode());
	if (!vacationsList.isEmpty())
	    throw new BusinessException("error_exceptionalAffectRegular");
    }

    private static void validateHistoricalStudyVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateStudyVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    private static void validateHistoricalExamVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateExamVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    private static void validateHistoricalAccompanyVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateAccompanyVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    private static void validateHistoricalReliefVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateReliefVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    private static void validateHistoricalSportiveCultureSocialVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateSportiveCultureSocialVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    private static void validateHistoricalCompensationVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateCompensationVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    private static void validateHistoricalMaternityVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateMaternityVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    private static void validateHistoricalMotherhoodVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateMotherhoodVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    private static void validateHistoricalOrphanCareVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateOrphanCareVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    private static void validateHistoricalDeathWaitingPeriodVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateDeathWaitingPeriodVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    private static void validateHistoricalDeathOfRelativeVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateDeathOfRelativeVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    private static void validateHistoricalNewBabyVacationRules(HistoricalVacationTransaction historicalVacationTransaction, EmployeeData vacationBeneficiary) throws BusinessException {

	VacationsBusinessRulesService.validateNewBabyVacationRules(constructVacation(historicalVacationTransaction), vacationBeneficiary);
    }

    /*----------------------------------------------------------End of Validations --------------------------------------------*/

    /*---------------------------------------------------------- Data Handling --------------------------------------------*/

    protected static void insertHistoricalVacation(HistoricalVacationTransaction historicalVacationTransaction, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.addEntity(historicalVacationTransaction, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    historicalVacationTransaction.setId(null);
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    protected static void modifyHistoricalVacation(HistoricalVacationTransaction historicalVacationTransaction, boolean signHistoricalVacationFlag, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    Vacation vacation = new Vacation();

	    if (signHistoricalVacationFlag) {
		vacation = constructVacation(historicalVacationTransaction);
		if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.MODIFY.getCode() || historicalVacationTransaction.getRequestType() == RequestTypesEnum.CANCEL.getCode()) {
		    HistoricalVacationTransaction oldHistoricalVacationTransaction = getHistoricalVacationTransactionDataById(historicalVacationTransaction.getHistoricalVacationParentId()).getHistoricalVacationTransaction();
		    vacation.setVacationId(oldHistoricalVacationTransaction.getVacationTransactionId());
		    if (historicalVacationTransaction.getRequestType() == RequestTypesEnum.CANCEL.getCode())
			vacation.setStatus(RequestTypesEnum.CANCEL.getCode());
		    else
			vacation.setStatus(RequestTypesEnum.MODIFY.getCode());
		    DataAccess.updateEntity(vacation, session);
		} else {
		    DataAccess.addEntity(vacation, session);
		    historicalVacationTransaction.setVacationTransactionId(vacation.getVacationId());
		}

	    }
	    DataAccess.updateEntity(historicalVacationTransaction, session);
	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_modifyVacationData");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    protected static void deleteHistoricalVacation(HistoricalVacationTransaction historicalVacationTransaction, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    DataAccess.deleteEntity(historicalVacationTransaction, session);

	    if (!isOpenedSession)
		session.commitTransaction();

	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_cancelVacationData");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    /*----------------------------------------------------Calculate Balances --------------------------------------------*/
    public static String calculateHistoricalVacationBalance(long vacationTypeId, Integer subVacationType, Integer locationFlag, EmployeeData emp, Date balanceToDate) throws BusinessException {

	if (vacationTypeId == VacationTypesEnum.REGULAR.getCode())
	    return (int) VacationsBusinessRulesService.calculateEmpRegularBalance(emp, balanceToDate, null, -1) + "";

	else if (vacationTypeId == VacationTypesEnum.COMPELLING.getCode())
	    return calculateEmpHistoricalCompellingBalance(emp, balanceToDate, null) + "";

	else if (vacationTypeId == VacationTypesEnum.SICK.getCode()
		&& (subVacationType == SubVacationTypesEnum.SUB_TYPE_ONE.getCode()
			|| (subVacationType == SubVacationTypesEnum.SUB_TYPE_TWO.getCode() && emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode())))
	    return VacationsBusinessRulesService.getEmpSickBalance(emp, subVacationType, balanceToDate, null);

	else if (vacationTypeId == VacationTypesEnum.FIELD.getCode()
		&& (emp.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()))
	    return VacationsBusinessRulesService.calculateEmpFieldBalance(emp, balanceToDate, null) + "";

	else if (vacationTypeId == VacationTypesEnum.EXCEPTIONAL.getCode()
		&& (((emp.getCategoryId() == CategoriesEnum.OFFICERS.getCode() || emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()) && subVacationType == null)
			|| ((emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
				|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()) && subVacationType != null)))
	    return VacationsBusinessRulesService.calculateEmpExceptionalBalance(emp, subVacationType, balanceToDate, null) + "";

	else if (vacationTypeId == VacationTypesEnum.ACCOMPANY.getCode()
		&& (emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()))
	    return VacationsBusinessRulesService.getEmpAccompanyBalance(emp, balanceToDate, null);

	else if (vacationTypeId == VacationTypesEnum.RELIEF.getCode()
		&& (emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()))
	    return VacationsBusinessRulesService.calculateEmpReliefBalance(emp, balanceToDate, null) + "";

	else if ((vacationTypeId == VacationTypesEnum.SPORTIVE.getCode()
		|| vacationTypeId == VacationTypesEnum.CULTURAL.getCode()
		|| vacationTypeId == VacationTypesEnum.SOCIAL.getCode())
		&& (emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()))
	    return VacationsBusinessRulesService.calculateSportiveCultureSocialBalance(emp, vacationTypeId, locationFlag, balanceToDate, null) + "";

	else if (vacationTypeId == VacationTypesEnum.MOTHERHOOD.getCode()
		&& emp.getGender().equals(GendersEnum.FEMALE.getCode())
		&& (emp.getCategoryId() == CategoriesEnum.SOLDIERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()))
	    return VacationsBusinessRulesService.calculateEmpMotherhoodBalance(emp, balanceToDate, null) + "";

	else if (vacationTypeId == VacationTypesEnum.ORPHAN_CARE.getCode()
		&& emp.getGender().equals(GendersEnum.FEMALE.getCode())
		&& (emp.getCategoryId() == CategoriesEnum.PERSONS.getCode() || emp.getCategoryId() == CategoriesEnum.USERS.getCode()
			|| emp.getCategoryId() == CategoriesEnum.WAGES.getCode() || emp.getCategoryId() == CategoriesEnum.MEDICAL_STAFF.getCode()))
	    return VacationsBusinessRulesService.calculateEmpOrphanCareBalance(emp, balanceToDate, null) + "";

	else
	    return "";
    }

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
    protected static float calculateEmpHistoricalRegularBalance(EmployeeData emp, Date balanceToDate, VacationConfiguration activeVacationConfiguration, int period) throws BusinessException {
	return VacationsBusinessRulesService.calculateEmpRegularBalance(emp, balanceToDate, activeVacationConfiguration, period);
    }

    public static Integer calculateEmpHistoricalCompellingBalance(EmployeeData emp, Date balanceToDate, VacationConfiguration activeVacationConfiguration) throws BusinessException {

	VacationsBusinessRulesService.validateOldEmpId(emp.getOldEmpId());

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

	    if (emp.getRecruitmentDate() == null || (balanceToDate.before(emp.getRecruitmentDate())))
		return 0;

	    if (activeVacationConfiguration == null)
		activeVacationConfiguration = VacationsBusinessRulesService.getActiveVacationConfigurations(emp.getCategoryId(), VacationTypesEnum.COMPELLING.getCode()).get(0);

	    if (categoryId == CategoriesEnum.PERSONS.getCode() || categoryId == CategoriesEnum.USERS.getCode() || categoryId == CategoriesEnum.WAGES.getCode() || categoryId == CategoriesEnum.MEDICAL_STAFF.getCode()) {
		return activeVacationConfiguration.getBalance() - VacationsBusinessRulesService.sumVacDays(emp.getEmpId(), VacationTypesEnum.COMPELLING.getCode(), HijriDateService.gregToHijriDateString("01/01/" + vacYear), HijriDateService.gregToHijriDateString("31/12/" + vacYear));
	    } else if (categoryId == CategoriesEnum.CONTRACTORS.getCode()) {
		Date frameStartDate = null, frameEndDate = HijriDateService.addSubHijriDays(emp.getRecruitmentDate(), -1);
		do {
		    frameStartDate = HijriDateService.addSubHijriDays(frameEndDate, 1);
		    String[] frameStartDateArray = HijriDateService.getHijriDateString(frameStartDate).split("/");
		    frameEndDate = HijriDateService.getHijriDate(frameStartDateArray[0] + "/" + frameStartDateArray[1] + "/"
			    + (Integer.parseInt(frameStartDateArray[2]) + activeVacationConfiguration.getBalanceFrame()));
		    frameEndDate = HijriDateService.addSubHijriDays(frameEndDate, -1);
		} while (!HijriDateService.isDateBetween(frameStartDate, frameEndDate, balanceToDate));
		return activeVacationConfiguration.getBalance() - VacationsBusinessRulesService.sumVacDays(emp.getEmpId(), VacationTypesEnum.COMPELLING.getCode(), HijriDateService.getHijriDateString(frameStartDate), HijriDateService.getHijriDateString(frameEndDate));
	    } else {
		return activeVacationConfiguration.getBalance() - VacationsBusinessRulesService.sumVacDays(emp.getEmpId(), VacationTypesEnum.COMPELLING.getCode(), "01/01/" + vacYear, HijriDateService.getHijriCalendar(12, Integer.valueOf(vacYear)).getHijriMonthLength() + "/12/" + vacYear);
	    }
	} catch (Exception e) {
	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}

    }

    private static String calculateEmpHistoricalSickBalance(EmployeeData emp, Integer subVacationType, Date balanceToDate, List<VacationConfiguration> activeVacationConfigurations) throws BusinessException {
	return VacationsBusinessRulesService.calculateEmpSickBalance(emp, subVacationType, balanceToDate, activeVacationConfigurations);

    }

    protected static int calculateEmpHistoricalExceptionalBalance(EmployeeData emp, Integer subVacationType, Date balanceToDate, VacationConfiguration activeVacationConfiguration) throws BusinessException {
	return VacationsBusinessRulesService.calculateEmpExceptionalBalance(emp, subVacationType, balanceToDate, activeVacationConfiguration);
    }

    protected static int calculateEmpHistoricalFieldBalance(EmployeeData emp, Date balanceToDate, VacationConfiguration activeVacationConfiguration) throws BusinessException {
	return VacationsBusinessRulesService.calculateEmpFieldBalance(emp, balanceToDate, activeVacationConfiguration);
    }

    /*---------------------------------------------------------- Queries  -----------------------------------------------*/
    private static List<Vacation> getVacationBetweenDates(long empId, long vacationTypeId, String periodStartDateString, String periodEndDateString, long subVacationType) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_EMP_ID", empId);
	    qParams.put("P_PERIOD_START_DATE", periodStartDateString);
	    qParams.put("P_PERIOD_END_DATE", periodEndDateString);
	    qParams.put("P_VACATION_TYPE_ID", vacationTypeId);
	    qParams.put("P_SUB_VACATION_TYPE", subVacationType);
	    return DataAccess.executeNamedQuery(Vacation.class, QueryNamesEnum.HCM_GET_VACATIONS_BETWEEN_DATE.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");

	}

    }

    public static List<HistoricalVacationTransactionData> searchHistoricalVacations(EmployeeData employee, Integer requestTypeFlag, Integer requestType, Integer newRequestFlag, Integer modifyRequestFlag, Integer cancelRequestFlag, Integer[] approvalFlag, String decisionNumber, Integer skipDecisionDateFlag, Date decisionDate, long vacationTypeFlag, Long[] vacationTypes, Date fromDate, Date toDate, Integer period, int approvedFlag, int locationFlag, String countryName, Integer miniSearchFlag)
	    throws BusinessException {
	try {
	    Map<String, Object> qParam = new HashMap<String, Object>();
	    qParam.put("P_EMP_ID", employee.getEmpId() == null ? FlagsEnum.ALL.getCode() : employee.getEmpId());
	    qParam.put("P_VACATION_TYPE_FLAG", vacationTypeFlag);
	    qParam.put("P_VACATION_TYPES_ID", vacationTypes);
	    qParam.put("P_SKIP_START_DATE", (fromDate == null && employee.getRecruitmentDate() == null) ? FlagsEnum.ALL.getCode() : FlagsEnum.ON.getCode());
	    qParam.put("P_FROM_DATE", (fromDate == null ? HijriDateService.getHijriDateString(employee.getRecruitmentDate() == null ? HijriDateService.getHijriSysDate() : employee.getRecruitmentDate()) : HijriDateService.getHijriDateString(fromDate)));
	    qParam.put("P_TO_DATE", (toDate == null ? HijriDateService.getHijriDateString(HijriDateService.getHijriSysDate()) : HijriDateService.getHijriDateString(toDate)));
	    qParam.put("P_PERIOD", (period == null ? FlagsEnum.ALL.getCode() : period));
	    qParam.put("P_APPROVED_FLAG", approvedFlag);
	    qParam.put("P_REQUEST_TYPE", requestType);
	    qParam.put("P_DECISION_DATE_SKIP_FLAG", skipDecisionDateFlag);
	    qParam.put("P_DECISION_DATE", decisionDate == null ? HijriDateService.getHijriDateString(HijriDateService.getHijriSysDate()) : HijriDateService.getHijriDateString(decisionDate));
	    qParam.put("P_LOCATION_FLAG", locationFlag);
	    qParam.put("P_COUNTRY_NAME", countryName.equals("") ? FlagsEnum.ALL.getCode() + "" : "%" + countryName + "%");
	    qParam.put("P_DECISION_NUMBER", decisionNumber.equals("") ? FlagsEnum.ALL.getCode() + "" : decisionNumber);
	    qParam.put("P_REQUEST_TYPE_FLAG", requestTypeFlag);
	    qParam.put("P_NEW_REQUEST_FLAG", newRequestFlag);
	    qParam.put("P_MODIFY_REQUEST_FLAG", modifyRequestFlag);
	    qParam.put("P_CANCEL_REQUEST_FLAG", cancelRequestFlag);
	    qParam.put("P_MINI_SEARCH_FLAG", miniSearchFlag);
	    qParam.put("P_APPROVAL_FLAGS", approvalFlag);
	    return DataAccess.executeNamedQuery(HistoricalVacationTransactionData.class, QueryNamesEnum.HCM_SEARCH_HISTORICAL_VACATIONS.getCode(), qParam);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static HistoricalVacationTransactionData getHistoricalVacationTransactionDataById(long id) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_VACATION_ID", id);

	    List<HistoricalVacationTransactionData> result = DataAccess.executeNamedQuery(HistoricalVacationTransactionData.class, QueryNamesEnum.HCM_GET_HISTORICAL_VACATION_TRANSACTION_BY_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_getVacationData");
	}
    }

    public static HistoricalVacationTransactionData getHistoricalVacationTransactionDataByParentId(Long parentId, Long vacationId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_PARENT_ID", parentId);
	    qParams.put("P_VACATION_ID", vacationId == null ? FlagsEnum.ALL.getCode() : vacationId);
	    List<HistoricalVacationTransactionData> result = DataAccess.executeNamedQuery(HistoricalVacationTransactionData.class, QueryNamesEnum.HCM_GET_HISTORICAL_VACATION_TRANSACTION_BY_PARENT_ID.getCode(), qParams);
	    return result.isEmpty() ? null : result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_getVacationData");
	}
    }

    private static long countHistoricalVacationsByDecisionNumber(String decisionNumber, Long historicalVacationId) throws BusinessException {
	try {
	    Map<String, Object> queryParam = new HashMap<String, Object>();
	    queryParam.put("P_DECISION_NUMBER", decisionNumber);
	    queryParam.put("P_VACATION_ID", historicalVacationId == null ? FlagsEnum.ALL.getCode() : historicalVacationId);
	    List<Long> count = DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.HCM_COUNT_HISTORICAL_VACATIONS_BY_DECISION_NUMBER.getCode(), queryParam);
	    return (count.isEmpty() || count == null ? 0 : count.get(0));
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_getVacationsData");
	}
    }

    /*---------------------------------------------------------- utils -----------------------------------------------*/
    protected static Vacation constructVacation(HistoricalVacationTransaction historicalVacationTransaction) {
	Vacation vacation = new Vacation();
	vacation.setEmpId(historicalVacationTransaction.getEmpId());
	vacation.setVacationTypeId(historicalVacationTransaction.getVacationTypeId());
	vacation.setSubVacationType(historicalVacationTransaction.getSubVacationType());
	vacation.setPaidVacationType(historicalVacationTransaction.getPaidVacationType());
	vacation.setStartDate(historicalVacationTransaction.getStartDate());
	vacation.setEndDate(historicalVacationTransaction.getEndDate());
	vacation.setPeriod(historicalVacationTransaction.getPeriod());
	vacation.setLocationFlag(historicalVacationTransaction.getLocationFlag());
	vacation.setLocation(historicalVacationTransaction.getLocation());
	vacation.setContactAddress(historicalVacationTransaction.getContactAddress());
	vacation.setContactNumber(historicalVacationTransaction.getContactNumber());
	vacation.setExceededDays(historicalVacationTransaction.getExceededDays());
	vacation.setJoiningDate(historicalVacationTransaction.getJoiningDate());
	vacation.setStatus(RequestTypesEnum.NEW.getCode());
	vacation.setEtrFlag(FlagsEnum.OFF.getCode());
	vacation.setMigrationFlag(FlagsEnum.ON.getCode());
	vacation.setDecisionNumber(historicalVacationTransaction.getDecisionNumber());
	vacation.setDecisionDate(historicalVacationTransaction.getDecisionDate());
	vacation.setAttachments(historicalVacationTransaction.getAttachments());
	return vacation;
    }

    /*---------------------------------------------------------- Data Handling --------------------------------------------*/

}
