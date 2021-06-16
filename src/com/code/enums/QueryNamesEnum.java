package com.code.enums;

public enum QueryNamesEnum {
    HCM_GET_EMPLOYEE_ID_BY_OLD_ID("hcm_emp_getEmployeeIdByOldId"),
    HCM_GET_EMPLOYEE_OLD_ID_BY_ID("hcm_emp_getEmployeeOldIdById"),
    HCM_GET_FUTURE_SERVICE_TERMINATED_EMPLOYEES("hcm_emp_getFutureServiceTerminatedEmployees"),
    HCM_SEARCH_EMPLOYEE_DATA_BY_REC_INFO("hcm_empData_searchEmpDataByRecInfo"),
    HCM_GET_EMPLOYEE_PHOTO_BY_EMP_ID("hcm_employeePhoto_getEmployeePhotoByEmployeeId"),

    HCM_SEARCH_EMPLOYEE_BY_ID("hcm_empData_searchEmployeeById"),
    HCM_SEARCH_EMPLOYEE_BY_SOCIAL_ID("hcm_empData_searchEmployeeBySocialId"),
    HCM_SEARCH_EMPLOYEE_BY_MILITARY_NUMBER("hcm_empData_searchEmployeeByMilitaryNumber"),
    HCM_SEARCH_EMPLOYEE_DIRECT_MANAGER("hcm_empData_searchEmployeeDirectManager"),
    HCM_SEARCH_EMPLOYEES_BY_COMMUNICATION_DATA("hcm_empData_searchEmployeeByCommunicationData"),
    HCM_SEARCH_EMPLOYEES("hcm_empData_searchEmpData"),
    HCM_SEARCH_EMPLOYEES_FOR_BENEFICIARY("hcm_empData_searchEmpDataForBeneficiary"),
    HCM_SEARCH_MANAGER_EMPLOYEES("hcm_empData_searchManagerEmployees"),
    HCM_SEARCH_EMPLOYEES_BY_PHYSICAL_OR_OFFICIAL_UNIT("hcm_empData_searchEmpDataByOfficialOrPhysicalUnit"),
    HCM_SEARCH_EMPLOYEES_EXTRA_TRANSACTION_DATA("hcm_empDataExtraTrnData_searchEmpExtraTrnData"),
    HCM_GET_AFTER_EFFECTIVE_DATE_EMPLOYEES_EXTRA_TRANSACTION_DATA("hcm_empDataExtraTrnData_getAfterEffDateEmpExtraTrnData"),
    HCM_GET_BEFORE_EFFECTIVE_DATE_EMPLOYEES_EXTRA_TRANSACTION_DATA("hcm_empDataExtraTrnData_getBeforeEffDateEmpExtraTrnData"),
    HCM_SEARCH_EMPLOYEE_MEDICAL_STAFF_DATA("hcm_empData_searchEmployeeMedicalStaffData"),
    HCM_SEARCH_TERMINATED_EMPLOYEES_BY_UNIT_ID("hcm_empData_searchTerminatedEmployeesByUnitId"),

    HCM_GET_EMPLOYEE_PREFERENCES_BY_ID("hcm_empPreferences_getEmployeePreferencesById"),

    HCM_COUNT_EMPS_BY_UNIT_ID("hcm_empData_countEmpByUnit"),
    HCM_COUNT_EMPS_BY_UNIT_HKEY_PREFIX("hcm_empData_countEmpByUnitHkeyPrefix"),
    HCM_COUNT_EMPS_BY_OFFICIAL_UNIT_HKEY_PREFIX("hcm_empData_countEmpByOfficialUnitHkeyPrefix"),
    HCM_COUNT_EMPS_BY_PHYSICAL_UNIT_HKEY_PREFIX("hcm_empData_countEmpByPhysicalUnitHkeyPrefix"),

    HCM_SEARCH_EMPLOYEES_BY_UNITS_IDS("hcm_empData_searchEmployeesByUnitsIds"),
    HCM_SEARCH_EMPLOYEES_BY_EMPS_IDS("hcm_empData_searchEmployeesByEmpsIds"),

    HCM_SEARCH_EMPLOYEE_PROMOTION_DUE_DATE("hcm_empData_searchEmployeesByPromotionDueDate"),
    HCM_SEARCH_EMPLOYEE_BY_RANK_ID_PROMOTION_DUE_DATE("hcm_empData_searchEmployeesByRankIdAndPromotionDueDate"),

    HCM_GET_EMPLOYEE_FOR_FUTURE_SERVICE_TERMINATION("hcm_empData_searchEmployeesForFutureServiceTermination"),

    HCM_COUNT_SOLDIERS_FOR_RECRUITMENT("hcm_empData_countSoldiersForRecruitment"),
    HCM_GET_ALL_DISTRIBUTED_SOLDIERS("hcm_empData_getAllDistributedSoldiers"),
    HCM_SEARCH_EMPLOYEE_COMMUNICATION_DATA_UNIQUENESS("hcm_empData_searchEmployeeCommunicationDataUniqueness"),

    /* ************************************************ Additional Specializations ********************************************** */

    HCM_SEARCH_EMPLOYEE_ADDITIONAL_SPECIALIZATION_DATA("hcm_employeeAdditionalSpecializationData_searchEmployeeAdditionalSpecializationData"),
    HCM_SEARCH_ADDITIONAL_SPECIALIZATION("hcm_additionalSpecialization_searchAdditionalSpecialization"),

    /* ************************************************ Employees Qualifications ********************************************** */

    HCM_GET_EMPLOYEE_QUALIFICATIONS_BY_EMP_ID("hcm_employeeQualificationsData_getEmployeeQualificationsByEmpId"),
    HCM_EMPLOYEE_QUALIFICATIONS_SEARCH_BY_GRADUATION_PLACE("hcm_employeeQualificationsData_searchEmpQualficicationByGraduationPlace"),
    HCM_EMPLOYEE_QUALIFICATIONS_SEARCH_BY_QUALIFICATION_SPEC("hcm_employeeQualificationsData_searchEmpQualficicationByQualificationSpec"),

    /* ************************************************ Martyrdom ********************************************** */

    HCM_SEARCH_MARTYR_TRANSACTION_DATA("hcm_martyrTransactionData_searchMartyrTransactionsData"),
    HCM_SEARCH_MARTYR_HONORS("hcm_martyrHonor_searchMartyrHonors"),
    HCM_SEARCH_MARTYRDOM_REASONS("hcm_martyrdomReason_searchMartyrdomReasons"),

    /* ************************************************ Raises ********************************************** */

    HCM_GET_NOT_EXECUTED_RAISES_TRANSACTIONS("hcm_raiseTransactionData_getNotExecutedRaisesTransactions"),
    HCM_SEARCH_RAISES("hcm_raise_searchRaises"),
    HCM_GET_DESERVED_EMPLOYEES("hcm_raise_getDeservedEmployees"),
    HCM_GET_UNDESERVED_EMPLOYEES("hcm_raise_getUnDeservedEmployees"),
    HCM_SEARCH_RAISE_EMPLOYEES("hcm_raiseEmployeeData_searchRaiseEmployees"),
    HCM_GET_DESERVED_EMPLOYEES_BY_RAISE_ID("hcm_raiseEmployee_getDeservedEmployeesByRaiseId"),
    HCM_UPDATE_EMPLOYEES_DUE_TO_ANNUAL_RAISE_EFFECT("hcm_raiseEmployee_updateEmployeesDueToAnnualRaiseEffect"),
    HCM_DELETE_RAISE_EMPLOYEES_BY_RAISE_ID("hcm_raiseEmployee_deleteRaiseEmployeesByRaiseId"),
    HCM_GET_FIRST_RAISE_TRRANSACTION_AFTER_GIVEN_DATE("hcm_raiseTransaction_getFirstRaiseTransactionAfterGivenDate"),
    HCM_GET_RAISE_TRANSACTION_DATA_BY_DECISION_DATE_AND_NUMBER("hcm_raiseTransaction_getRaiseTransactionByDecisionNumberAndDecisionDate"),
    HCM_GET_ALL_RAISES_FOR_EMPLOYEES_AFTER_GIVEN_DATE("hcm_raiseTransaction_getAllRaisesForEmployeesAfterGivenDate"),
    HCM_GET_FUTURE_RAISES_BY_EMP_ID("hcm_raiseTransactionData_getFutureRaisesByEmpId"),
    HCM_GET_ALL_RAISES_BY_EMP_ID("hcm_raiseTransactionData_getAllRaisesByEmpId"),

    /* ************************************************ Navy Formations ********************************************** */

    HCM_SEARCH_NAVY_FORMATIONS("hcm_navyFormation_searchNavyFormations"),

    /* ************************************************ Correspondence ********************************************************** */

    COR_GET_ETR_COR("cor_etr_getETRCor"),

    /* ************************************************ Country ********************************************************** */

    HCM_GET_COUNTRY_BY_COUNTRY_DESC("hcm_country_getCountryByCountryDesc"),
    HCM_GET_COUNTRY_BY_YAQEEN_NAME("hcm_country_getCountryByYaqeenName"),
    HCM_GET_COUNTRY_BLACKLIST_BY_COUNTRY_ID("hcm_countryBlacklist_getCountryBlacklistByCountryId"),

    /* ************************************************ Vacations ********************************************************** */

    HCM_GET_VACATION_TYPES("hcm_vacType_getVacationTypes"),

    HCM_GET_VACATION_BY_ID("hcm_vacation_getVacationById"),
    HCM_SEARCH_VACATIONS("hcm_vacation_searchVacations"),
    HCM_VACATION_DATES_CONFLICT("hcm_vacation_checkDatesConflict"),
    HCM_SUM_VAC_DAYS("hcm_vacation_sumVacDays"),
    HCM_SUM_VAC_DAYS_WITHIN_CONTRACTUAL_YEAR("hcm_vacation_sumVacDaysWithinContractualYear"),
    HCM_VACATION_INCLUDE_DATE("hcm_vacation_includeDate"),
    HCM_GET_VACATION_BY_BENEFICIARY_AND_START_DATE("hcm_vacation_getVacationByBeneficiaryAndStartDate"),
    HCM_GET_VACATIONS_BETWEEN_DATE("hcm_vacation_getVacationsBetweenDates"),
    HCM_GET_FIRST_VACATION("hcm_vacation_getFirstVacation"),
    HCM_SUM_VAC_RELATED_DEDUCTED_BALANCE("hcm_vacation_sumRelatedDeductedBalance"),
    HCM_COUNT_LATER_VACATIONS("hcm_vacation_countLaterVacations"),
    HCM_COUNT_LATER_VACATIONS_HAS_JOINING_DATE("hcm_vacation_countLaterVacationsHasJoiningDate"),
    HCM_COUNT_VACATION_BY_DECISION_NUMBER("hcm_vacation_countVacationByDecisionNumber"),

    HCM_GET_VACATION_DATA_BY_ID("hcm_vacationData_getVacationDataById"),
    HCM_GET_LAST_VACATION_DATA("hcm_vacationData_getLastVacation"),
    HCM_GET_EXTERNAL_VACATION_DATA("hcm_vacationData_getExternalVacation"),
    HCM_GET_LAST_VACATION_DATA_BEFORE_SPECIFIC_DATE("hcm_vacationData_getLastVacationBeforeSpecificDate"),
    HCM_GET_UNIT_CURRENT_VACACTIONS_DATA("hcm_vacationData_getUnitCurrentVacationsData"),
    HCM_COUNT_UNIT_CURRENT_VACACTIONS_DATA("hcm_vacationData_countUnitCurrentVacationsData"),
    HCM_GET_VACATION_DATA_TRANSACTIONS_HISTORY("hcm_vacationData_transactions_history"),
    HCM_GET_VACATION_BY_DECISION_DATE_AND_DECISION_NUMBER("hcm_vacationData_getVacationByDecisionDateAndDecisionNumber"),

    HCM_GET_VACATION_CONFIGURATIONS("hcm_vacationConfig_getVacationConfigurations"),
    HCM_GET_ACTIVE_VACATION_CONFIGURATIONS("hcm_vacationConfig_getActiveVacationConfigurations"),
    HCM_SEARCH_VACATIONS_CONFIGURATIONS("hcm_vacationConfig_searchVacationsConfigurations"),

    HCM_SEARCH_HISTORICAL_VACATIONS("hcm_historicalVacationTransactionData_searchHistoricalVacations"),
    HCM_COUNT_HISTORICAL_VACATIONS_BY_DECISION_NUMBER("hcm_historicalVacationTransaction_countHistoricalVacationsByDecisionNumber"),
    HCM_GET_HISTORICAL_VACATION_TRANSACTION_BY_ID("hcm_historicalVacationTransactionData_getHistoricalVacationById"),
    HCM_GET_HISTORICAL_VACATION_TRANSACTION_BY_PARENT_ID("hcm_historicalVacationTransactionData_getHistoricalVacationByParentId"),

    HCM_SEARCH_FUTURE_VACATIONS("hcm_transientVacationTransactionData_searchFutureVacations"),
    HCM_COUNT_FUTURE_VACATIONS_BY_DECISION_NUMBER("hcm_transientVacationTransaction_countFutureVacationsByDecisionNumber"),
    HCM_GET_FUTURE_VACATION_TRANSACTION_BY_ID("hcm_transientVacationTransactionData_getFutureVacationById"),
    HCM_GET_FUTURE_VACATION_TRANSACTION_BY_PARENT_ID("hcm_transientVacationTransactionData_getFutureVacationByParentId"),

    HCM_GET_VACATION_LOG_BY_VACATION_ID_AND_STATUS("hcm_vacationLog_getVacationLogByVacationIdAndStatus"),
    /* ************************************************ Payroll ********************************************************** */
    HCM_GET_PAYROLL_DIFF_BY_TRANS_STATUS("hcm_EmployeePayrollDifferenceData_getPayrollDiffByTrnsStatus"),

    HCM_GET_ALL_DEGREES("hcm_degree_getAllDegrees"),
    HCM_SEARCH_PAYROLL_SALARIES("hcm_payrollSalary_searchPayrollSalary"),
    HCM_SEARCH_PAYROLL_NEW_SALARIES("hcm_payrollSalary_searchPayrollNewSalary"),
    HCM_SEARCH_PAYROLL_NEW_SALARIES_DEMOTING("hcm_payrollSalary_searchPayrollNewSalary_demoting"),
    HCM_GET_END_OF_LADDER_OF_RANK("hcm_payrollSalary_getEndOfLadderOfRank"),
    HCM_GET_EMPLOYEE_PENALITIES_BY_ID("hcm_employeePenalitiesData_getEmployeePenalitiesData"),
    HCM_GET_EMPLOYEE_BONUSES_BY_ID("hcm_employeeBonusesData_getEmployeeBonusesData"),
    HCM_GET_EMPLOYEE_ALLOWANCES_BY_ID("hcm_employeeAllowancesData_getEmployeeAllowancesData"),
    HCM_GET_END_OF_LADDER_FOR_ALL_RANKS_OFF_CATEGORY("hcm_payrollSalary_getEndOfLadderForAllRanksOfCategory"),
    /* ************************************************ Finance ****************************************************** */
    FIN_GET_PAID_ISSUE_ORDERS("fin_PaidIssueOrderData_getPaidIssueOrder"),

    /* ************************************************ Units ********************************************************** */
    HCM_GET_UNIT_TYPE("hcm_unitType_getUnitType"),

    HCM_GET_UNIT_DATA_BY_CHILD_TYPE("hcm_unitData_getUnitDataByChildType"),
    HCM_GET_ALL_LEVELS_CHILD_BY_HKEY("hcm_unitData_getAllLevelChildrenByHKey"),
    HCM_GET_UNIT_DATA("hcm_unitData_getUnitData"),
    HCM_GET_UNIT_DATA_CHILDREN("hcm_unitData_getUnitDataChildren"),
    HCM_GET_UNIT_DATA_PARENT_CHILDREN("hcm_unitData_getUnitDataParentChildren"),
    HCM_GET_UNIT_DATA_BY_HKEY("hcm_unitData_getUnitDataByHKey"),
    HCM_GET_UNIT_DATA_BY_FULL_NAME("hcm_unitData_getUnitDataByFullName"),
    HCM_GET_MAX_ORDER_UNDER_PARENT("hcm_unitData_getMaxOrderUnderParent"),
    HCM_GET_UNITS_BY_TYPE_AND_NAME("hcm_unitData_getUnitsByTypeAndName"),
    HCM_GET_UNITS_BY_PHYSICAL_MANAGER("hcm_unitData_getUnitsByPhysicalManager"),
    HCM_GET_TRAINING_CENTERS_AND_INSTITUTES_AND_NAME("hcm_unitData_getTrainingCentersAndInstitutesByName"),
    HCM_GET_UNIT_DATA_ANCESTOR_UNDER_PRESIDENCY_BY_LEVEL("hcm_unitData_getAncestorUnderPresidencyByLevel"),
    HCM_SEARCH_UNITS_BY_UNITS_IDS("hcm_unitData_searchUnitsByUnitsIdsAndUnitFullName"),
    HCM_COUNT_UNIT_CHILDREN_EXIST_IN_UNITS("hcm_unit_countUnitChildrenExistInUnits"),
    HCM_GET_REGION("hcm_Region_getRegion"),
    HCM_GET_UNIT_TRANSACTION_DATA("hcm_unitTransactionData_getUnitTransactions"),

    /* ************************************************ Organization Tables ********************************************************** */
    HCM_GET_ORGANIZATION_TABLE_BY_UNITS_IDS_AND_CATEGORY_CLASS("hcm_organizationTable_getOrganizationTablesByCategoryClassAndUnitsIds"),
    HCM_COUNT_ORGANIZATION_TABLE_BY_UNIT_ID_AND_CATEGORY_CLASS("hcm_organizationTable_countOrganizationTablesByCategoryClassAndUnitId"),

    HCM_GET_IDENTICAL_ORGANIZATION_TABLE_DETAILS_BY_ORGANIZTION_TABLE_ID_AND_DETAILS_COUNT("hcm_organizationTableDetailData_getIdenticalOrganizationTableDetails"),
    HCM_GET_ORGANIZATION_TABLE_DETAILS_DATA_BY_UNIT_ID_AND_CATEGORY_CLASS("hcm_organizationTableDetailData_getOrganizationTableDetailsData"),

    HCM_GET_ORGANIZATION_TABLE_TRANSACTION_DATA_BY_UNIT_ID_AND_CATEGORY_CLASS("hcm_organizationTableTransactionData_getOrganizationTableTransactionsByUnitIdAndCatgeoryClass"),

    /* **************************** Targets Tasks ************************** */
    HCM_GET_TARGETS_TASKS_BY_UNITS_IDS("hcm_organizationTargetTask_getOrganizationTargetsTasksByUnitsIds"),

    HCM_COUNT_ACTIVE_TARGET_TASK_DETAILS_BY_UNIT_ID("hcm_organiztionTargetsTasksDetails_countActiveTargetTaskDetailsByUnitId"),
    HCM_GET_ACTIVE_TARGET_TASK_DETAILS_BY_UNIT_ID("hcm_organiztionTargetsTasksDetails_getActiveTargetTaskDetailsByUnitId"),
    HCM_GET_IDENTICAL_TARGETS_TASKS_DETAILS_BY_UNIT_ID_AND_DETAILS_COUNT("hcm_organiztionTargetsTasksDetails_getIdenticalTargetsTasksDetails"),

    HCM_GET_TARGETS_TASKS_TRANSACTIONS_BY_UNIT_ID("hcm_organizationTargetTaskTransactionData_getOrganizationTargetTaskTransactionsByUnitId"),

    /* ************************************************ Jobs ********************************************************** */
    HCM_SEARCH_JOBS("hcm_jobData_searchJobData"),
    HCM_GET_JOBS_FOR_RECRUITMENT("hcm_jobData_getJobsForRecruitment"),
    HCM_SEARCH_JOBS_BY_UNITS_IDS("hcm_jobData_searchJobByUnitsIds"),
    HCM_SEARCH_JOBS_BY_JOBS_IDS("hcm_jobData_searchJobsByJobsIds"),
    HCM_COUNT_JOBS_BY_UNIT_HKEY_AND_MINOR_SPECS_IDS("hcm_jobData_countJobsByUnitHKeyAndMinorSpecsIds"),
    HCM_GET_JOBS_BY_DECISION_INFO("hcm_jobData_getJobsByDecisionInfo"),

    HCM_COUNT_JOBS_BY_BASIC_JOB_NAME_ID("hcm_job_countJobsByBasicJobNameId"),
    HCM_COUNT_JOBS_BY_UNITS_IDS_AND_CATEGORIES_IDS("hcm_job_countJobsByUnitsIdsAndCategoriesIds"),
    HCM_COUNT_JOBS_BY_SERIALS("hcm_job_countJobsBySerials"),
    HCM_GET_JOBS_SERIALS_WHERE_GAPS_AFTER_THEM("hcm_job_getJobsSerialsWhereGapsAfterThem"),
    HCM_GET_JOBS_SERIALS_WHERE_GAPS_BEFORE_THEM("hcm_job_getJobsSerialsWhereGapsBeforeThem"),

    HCM_COUNT_JOBS_TRANSACTIONS_BY_BASIC_JOB_NAME_ID("hcm_jobTransaction_countJobsTransactionsByBasicJobNameId"),
    HCM_GET_JOBS_DECISIONS("hcm_jobTransaction_getJobsDecisions"),
    /* ****************************************** Job Transactions **************************************************** */
    HCM_GET_JOB_TRANSACTIONS_DATA_BY_JOB_ID("hcm_jobTransactionData_getJobTransactionsByJobId"),
    /* ****************************************** Job Classifications ************************************************* */
    HCM_SEARCH_JOB_CLASSIFICATION("hcm_jobClassification_searchJobClassification"),
    HCM_GET_JOB_CLASSIFICATION_MAJOR_GROUP_DESC("hcm_jobClassification_getJobClassificationMajorGroupDesc"),
    HCM_GET_JOB_CLASSIFICATION_MINOR_GROUP_DESC("hcm_jobClassification_getJobClassificationMinorGroupDesc"),
    HCM_GET_JOB_CLASSIFICATION_SERIES_DESC("hcm_jobClassification_getJobClassificationSeriesDesc"),
    /* ****************************************** Basic Jobs Names **************************************************** */
    HCM_SEARCH_BASIC_JOBS_NAMES("hcm_basicJobNameData_searchBasicJobNameData"),
    HCM_COUNT_BASIC_JOBS_NAMES("hcm_basicJobName_countBasicJobsNames"),
    /* ****************************************** Jobs Balances **************************************************** */
    HCM_GET_JOBS_BALANCES_DATA("hcm_jobsBalanceData_getJobsBalanceData"),
    HCM_GET_TOTAL_JOBS_BALANCES_DATA("hcm_jobsBalanceData_getTotalJobsBalanceData"),
    HCM_GET_JOBS_BALANCE_BY_REGIONS_AND_RANKS("hcm_jobsBalanceData_getJobsBalanceDataByRegionsAndRanks"),
    /* ****************************************** WF Jobs ******************************************* */
    WF_GET_RUNNING_JOBS_REQUESTS("wf_job_getRunningJobsRequests"),
    WF_COUNT_JOBS_REQUESTS_BY_BASIC_JOB_NAME_ID("wf_job_countJobsRequestsByBasicJobNameId"),
    WF_GET_WFJOBS_BY_INSTANCE_ID("wf_jobData_getWFJobsByInstanceId"),
    /* ****************************************** Manpower Needs **************************************************** */
    HCM_GET_MANPOWER_NEED_REQUESTS_AND_STUDIES("hcm_manpowerNeedData_getManpowerNeedsRequestsAndStudies"),
    HCM_GET_MANPOWER_NEED_STUDIES("hcm_manpowerNeedData_getManpowerNeedsStudies"),
    HCM_GET_MANPOWER_NEEDS_BY_STUDIES_IDS("hcm_manpowerNeedData_getManpowerNeedsByStudiesIds"),
    HCM_GET_MANPOWER_NEEDS_BY_IDS("hcm_manpowerNeedData_getManpowerNeedsByIds"),
    HCM_COUNT_UNDER_PROCESSING_MANPOWER_NEED_DATA_STUDIES("hcm_manpowerNeedData_countUnderProcessingStudies"),

    HCM_GET_MANPOWER_NEED_DETAILS_DATA_BY_MANPOWER_NEEDS_IDS("hcm_manpowerNeedDetailData_getManpowerNeedDetailsDataByManpowerNeedsIds"),
    HCM_COUNT_UNDER_PROCESSING_MANPOWER_NEED_DATA_REQUESTS_FROM_UNIT("hcm_manpowerNeedData_countUnderProcessingRequestsFromUnit"),
    HCM_COUNT_UNDER_PROCESSING_MANPOWER_NEED_DATA_REQUESTS_TO_UNIT("hcm_manpowerNeedDetailData_countUnderProcessingRequestsToUnit"),

    WF_GET_WFMANPOWER_NEED_BY_INSTANCE_ID("wf_manpowerNeed_getWFManpowerNeedByInstanceId"),

    /* ****************************************** Transactions Types ************************************************** */
    HCM_GET_TRANSACTION_TYPES("hcm_transactionType_getAllTransactionTypes"),
    /* ****************************************** Minor Specializations *********************************************** */
    HCM_SEARCH_MINOR_SPECIALIZATIONS("hcm_minorSpecializationData_searchMinorSpecialization"),
    HCM_SEARCH_MINOR_SPECIALIZATIONS_BY_IDS("hcm_minorSpecializationData_searchMinorSpecializationByIds"),
    /* ****************************************** Minor Specializations Description*********************************************** */
    HCM_GET_MINOR_SPECIALIZATION_DESCRIPTION("hcm_minorSpecializationDescriptionData_getMinorSpecializationDescriptionData"),
    /* ****************************************** Minor Specialisations Description Detail *********************************************** */
    HCM_GET_MINOR_SPECIALIZATION_DESCRIPTION_DETAILS("hcm_minorSpecializationDescriptionDetailData_getMinorSpecializationDescriptionDetailsData"),
    /* ****************************************** Qualification Minor Specs ******************************************* */
    HCM_SEARCH_QUALIFICATION_MINOR_SPECS("hcm_qualificationMinorSpecData_searchQualificationMinorSpec"),
    HCM_SEARCH_QUALIFICATION_MAJOR_AND_MINOR_SPEC("hcm_qualificationMajorSpec_searchQualificationMajorAndMinorSpec"),
    /* ****************************************** Qualification Major Specs ******************************************* */
    HCM_SEARCH_QUALIFICATION_MAJOR_SPEC("hcm_qualificationMajorSpec_searchQualificationMajorSpec"),
    /* ****************************************** Qualification Levels ************************************************ */
    HCM_SEARCH_QUALIFICATION_LEVELS("hcm_qualificationLevel_searchQualificationLevel"),
    /* ****************************************** Social ID Issue Places ********************************************** */
    HCM_SEARCH_SOCIAL_ID_ISSUE_PLACES("hcm_socialIdIssuePlace_searchSocialIdIssuePlace"),
    /* ****************************************** Graduation Places *************************************************** */
    HCM_SEARCH_GRADUATION_PLACES_DETAILS_DATA("hcm_graduationPlaceDetailData_searchGraduationPlaceDetailData"),
    HCM_SEARCH_GRADUATION_PLACES_DATA("hcm_graduationPlaceData_searchGraduationPlaceData"),
    /* ****************************************** Graduation Group Places ********************************************* */
    HCM_GET_ALL_GRADUATION_GROUP_PLACES("hcm_graduationGroupPlaces_getAllGraduationGroupPlaces"),
    /* ************************************************* Ranks ******************************************************** */
    HCM_SEARCH_RANKS("hcm_rank_searchRank"),
    /* ************************************************* Ranks Titles ************************************************* */
    HCM_GET_All_RanksTitles("hcm_RanksTitles_getAllRanksTitles"),
    /* *********************************************** Categories ***************************************************** */
    HCM_GET_ALL_CATEGORIES("hcm_category_getAllCategories"),
    /* *********************************************** Medical stuff ranks ***************************************************** */
    HCM_GET_ALL_MEDICAL_STAFF_RANKS("hcm_category_getAllMedicalStaffRanks"),
    /* *********************************************** Medical stuff levels ***************************************************** */
    HCM_GET_ALL_MEDICAL_STAFF_LEVELS("hcm_category_getAllMedicalStaffLevels"),
    /******************************************************************************************************************/
    SEC_GET_EMP_MENUS("sec_menu_getEmployeeMenus"),
    SEC_GET_EXTERNAL_MENUS("sec_menu_getExternalMenus"),
    SEC_GET_EMPLOYEE_DECISION_PRIVILEGE("sec_employeeDecisionPrivilege_getEmployeeDecisionPrivilege"),
    SEC_SEARCH_DECISION_PRIVILEGE_DATA("sec_decisionPrivilegeData_searchDecisionPrivilegeData"),
    SEC_COUNT_DECISION_PRIVILEGE_DATA("sec_decisionPrivilegeData_countDecisionPrivilegeData"),

    SEC_GET_EMLOYEE_MENU_ACTIONS("sec_menu_getEmployeeMenuActions"),
    SEC_CHECK_EMPLOYEE_MENU_ACTION("sec_menu_checkEmployeeMenuAction"),
    /******************************************************************************************************************/

    UTIL_GET_ALL_HIJRI_CALENDARS("hijri_calendar_getAllHijriCalendars"),
    /******************************************************************************************************************/

    WF_GET_WFINSTANCE_BY_ID("wf_instance_getWFInstanceById"),
    WF_GET_WFINSTANCES_BY_IDS("wf_instance_getWFInstancesByIds"),
    WF_COUNT_INSTANCES_BY_PROCESSES_IDS("wf_instance_countInstancesByProcessesIds"),

    WF_GET_WFINSTANCE_DATA_BY_ID("wf_instanceData_getWFInstanceDataById"),
    WF_SEARCH_WFINSTANCE_DATA("wf_instanceData_searchWFInstancesData"),
    WF_GET_WFINSTANCE_UNDER_PROCESSING_COUNT("wf_instanceData_getWFInstancesUnderProcessingCount"),

    WF_GET_WF_INSTANCE_BENEFICIARIES_BY_INSTANCE_ID("wf_instanceBeneficiary_getWFInstanceBeneficiariesByInstanceId"),

    WF_GET_WFTASK_BY_ID("wf_task_getWFTaskById"),
    WF_GET_WFTASKS_BY_IDS("wf_task_getWFTasksByIds"),
    WF_GET_WFINSTANCE_TASKS("wf_task_getWFInstanceTasks"),
    WF_GET_WFINSTANCE_TASKS_BY_ROLE("wf_task_getWFInstanceTasksByRole"),
    WF_CAN_CHANGE_WFINSTANCE_DONE("wf_task_canChangeWFInstanceStatusToDone"),
    WF_CAN_CHANGE_WFINSTANCE_COMPLETE("wf_task_canChangeWFInstanceStatusToComplete"),
    WF_COUNT_TASKS("wf_task_countTasks"),
    WF_TASK_SECURITY("wf_task_taskSecurity"),
    WF_GET_RUNNING_WFTASK_FOR_INVALIDATION_BY_ASSIGNEES_OR_ORIGINALS("wf_task_getRunningWFTasksForInvalidationByAssigneesOrOriginals"),
    WF_GET_RUNNING_WFTASK_FOR_INVALIDATION_BY_PROCESSES("wf_task_getRunningWFTasksForInvalidationByProcesses"),
    WF_GET_WFINSTANCE_COMPLETED_TASKS_BY_LEVEL_AND_ORIGINAL_ID("wf_task_getWFInstanceCompletedTasksByLevelAndOriginalId"),
    WF_GET_UNASSIGNED_WF_TASKS("wf_task_getUnassignedWFTasks"),

    WF_GET_WFTASK_DATA_BY_ID("wf_taskData_getWFTaskDataById"),
    WF_SEARCH_WFTASK_DATA("wf_taskData_searchWFTasksData"),
    WF_GET_WFINSTANCE_TASKS_DATA("wf_taskData_getWFInstanceTasksData"),
    WF_GET_WFINSTANCE_COMPLETED_TASKS_DATA("wf_taskData_getWFInstanceCompletedTasksData"),
    WF_GET_WFINSTANCE_COMPLETED_TASKS_DATA_ORDERED_BY_LEVEL_LENGTH("wf_taskData_getWFInstanceCompletedTasksDataOrderedByLevelLength"),

    WF_GET_PROCESSES_GROUPS("wf_group_getProcessesGroups"),
    WF_GET_PROCESSES_GROUP_BY_ID("wf_group_getProcessesGroupById"),
    WF_GET_PROCESSES_GROUPS_APRRROVAL_COUNTS("wf_group_getProcessesGroupsApprovalCounts"),

    WF_GET_GROUP_PROCESSES("wf_process_getGroupProcesses"),
    WF_GET_PROCESS("wf_process_getWFProcess"),
    WF_GET_PROCESSES_BY_GROUP_AND_NAME("wf_process_getProcessesByGroupAndName"),

    WF_GET_DELEGATE_ID_ALL_HIERARCHY("wf_delegation_getDelegateIdAllHierarchy"),
    WF_GET_DELEGATIONS_COUNT("wf_delegation_getWFDelegationsCount"),
    WF_GET_DELEGATIONS_EITHER_FROM_OR_TO_EMPLOYEES("wf_delegation_getWFDelegationsEitherFromOrToEmployees"),
    WF_GET_DELEGATE_DATA("wf_delegationData_getWFDelegationData"),
    WF_GET_DELEGATE_ID_SPECIFIC_HIERARCHY("wf_delegationUnitData_getDelegateIdSpecificHierarchy"),

    WF_GET_POSITION("wf_position_getWFPosition"),

    WF_COUNT_NOTIFICATIONS_CONFIG("wf_notificationsConfig_countWFNotificationsConfig"),
    WF_SEARCH_NOTIFICATIONS_CONFIG_DATA("wf_notificationsConfigData_searchWFNotificationsConfigData"),
    WF_COUNT_NOTIFICATIONS_CONFIG_DETAILS("wf_notificationsConfigDetail_countWFNotificationsConfigDetail"),
    WF_SEARCH_NOTIFICATIONS_CONFIG_DETAIL_DATA("wf_notificationsConfigDetailData_searchWFNotificationsConfigDetailData"),
    WF_SEARCH_NOTIFICATIONS_CONFIG_DETAIL_EMPLOYEE_DATA_FOR_WF_INSTANCE("wf_notificationsConfigDetailEmployeeData_searchWFNotificationsConfigDetailEmployeeDataforWFInstance"),

    WF_GET_WFVACATION_BY_INSTANCE_ID("wf_vacation_getWFVacationByInstanceId"),
    WF_VALIDATE_VACATION_RUNNING_PROCESSES("wf_vacation_validateRunningProcesses"),
    WF_VALIDATE_VACATION_CHECK_DATES_CONFLICT("wf_vacation_checkDatesConflict"),
    WF_GET_WFVACATIONS_TASKS("wf_vacation_getVacationsTasks"),

    WF_GET_UNIT_RUNNING_WF_VACACTIONS_DATA("wf_vacationData_getUnitRunningWFVacationsData"),
    WF_COUNT_UNIT_RUNNING_WF_VACACTIONS_DATA("wf_vacationData_countUnitRunningWFVacationsData"),
    WF_GET_WF_DEF_LETTER_BY_INSTANCE_ID("hcm_WFDefLetter_getWFDefLetterByInstanceId"),

    CONFG_GET_ETR_CONFIGURATIONS("config_ETRConfig_getConfigurations"),

    /* ****************************************** Mission Data ************************************************** */
    HCM_SEARCH_MISSION_DATA("hcm_missionData_searchMissionData"),
    HCM_SEARCH_MISSION_REQUEST("hcm_missionData_searchMissionRequest"),

    /* ****************************************** Mission Data Detail ******************************************* */
    HCM_SEARCH_MISSION_DETAIL_DATA_BY_MISSION_ID("hcm_missionDetailData_searchMissionDetailDataByMissionId"),
    HCM_SEARCH_MISSION_DETAIL_DATA("hcm_missionDetailData_searchMissionDetailData"),
    HCM_SEARCH_MISSION_DETAIL_DATA_BY_EMP_ID("hcm_missionDetailData_searchMissionDetailDataByEmpId"),
    HCM_MISSION_DETAIL_DATA_SUM_ACTUAL_AND_ROAD_PARIOD("hcm_missionDetailData_sumActualPariodAndRoadPariod"),
    HCM_MISSION_DETAIL_OVERLAP("hcm_missionDetail_overLap"),
    HCM_GET_MISSION_DETAIL_DATA_AFTER_DECISION_DATE("hcm_missionDetailData_getMissionTransactionAfterDecisionDate"),
    HCM_MISSION_DETAIL_DATA_GET_MISSIONS_DETAILS_WITH_NO_INCENTIVES_BY_EMP_ID("hcm_missionDetailData_getMissionsDetailsWithNoIncentivesByEmpId"),

    /* ****************************************** WF Mission Detail ******************************************* */
    WF_MISSIONS_DETAILS_VALIDATE_RUNNING_PROCESSES("wf_missionDetail_validateRunningProcesses"),

    /* ****************************************** WF Mission Data ******************************************* */
    WF_GET_MISSION_BY_INSTANCE_ID("wf_mission_getWFMissionByInstanceId"),
    WF_GET_WFMISSIONS_TASKS("wf_mission_getMessionsTasks"),
    /* ****************************************** WF Mission Detail Data ******************************************* */
    WF_GET_MISSION_DETAILS_BY_INSTANCE_ID("wf_missionDetails_getWFMissionDetailsByInstanceId"),

    /* ****************************************** Recruitment Wish ************************************** */
    HCM_SEARCH_RECRUITMENT_WISHES("hcm_recruitmentWishData_searchRecruitmentWishData"),
    HCM_GET_ALL_NON_DISTRIBUTED_SOLDIERS_WISHES("hcm_recruitmentWishData_getAllNonDistributedSoldiersWishes"),
    /* ****************************************** Recruitment Distribution ************************************** */
    HCM_SEARCH_RECRUITMENT_DISTRIBUTIONS("hcm_recruitmentDistributionData_searchRecruitmentDistributionData"),
    HCM_COUNT_REGIONS_NEEDS("hcm_recruitmentDistributionData_countRegionsNeeds"),
    HCM_CHECK_SOLDIERS_AND_REGIONS_NEEDS_COUNT("hcm_recruitmentDistributionData_checkSoldiersAndRegionsNeedsCount"),

    /* ****************************************** Recruitment Transaction *************************************** */
    HCM_SEARCH_RECRUITMENT_TRANSACTIONS("hcm_recruitmentTransactionData_searchRecruitmentTransactionData"),
    HCM_GET_RECRUITMENT_TRANSACTIONS_BY_DECISION_NUMBER_AND_DECISION_DATE("hcm_recruitmentTransactionData_getRecruitmentTransactionDataByDecisionNumberAndDecisionDate"),
    HCM_GET_RECRUITMENT_TRANSACTIONS_BY_ID("hcm_recruitmentTransactionData_getRecruitmentTransactionDataById"),
    HCM_GET_RECRUITMENT_TRANSACTIONS_BY_EMPLOYEE_ID_AND_REC_DATE_AND_REC_TYPES("hcm_recruitmentTransaction_getRecruitmentTransactionByEmployeeIdAndRecDateAndRecTypes"),
    /* ****************************************** Recruitment Workflow ****************************************** */
    WF_CHECK_EXISTING_RECRUITMENT_PROCESSES("wf_recruitment_checkExistingRecruitmentProcesses"),
    WF_GET_EMPLOYEES_IDS_IN_RUNNING_RECRUITMENT_PRCOESSES("wf_recruitmentData_getEmployeesIdsInRunningRecruitmentProcesses"),
    WF_GET_JOBS_IDS_IN_RUNNING_RECRUITMENT_PRCOESSES("wf_recruitmentData_getJobsIdsInRunningRecruitmentProcesses"),
    WF_GET_WFRECRUITMENT_BY_INSTANCE_ID("wf_recruitmentData_getWFRecruitmentDataByInstanceId"),
    WF_GET_WFRECRUITMENT_TASKS("wf_recruitmentData_getRecruitmentsTasks"),
    /* ****************************************** Movement Type ************************************************* */
    HCM_SEARCH_MOVEMENT_TYPES("hcm_movementType_searchMovementType"),

    /* ****************************************** Movement Transaction ****************************************** */
    HCM_SEARCH_MOVEMENT_TRANSACTIONS("hcm_movementTransactionData_searchMovementTransactionData"),
    HCM_GET_MOVEMENT_TRANSACTIONS_BY_DECISION_NUMBER_AND_DECISION_DATE("hcm_movementTransactionData_getMovementTransactionDataByDecisionNumberAndDecisionDate"),
    HCM_GET_CANCELLED_AND_TERMINATED_MOVEMENT_TRANSACTIONS_BY_DECISION_NUMBER_AND_DECISION_DATE("hcm_movementTransactionData_getCancelledAndTerminatedMovementTransactionDataByDecisionNumberAndDecisionDate"),
    HCM_GET_MOVEMENT_TRANSACTIONS_VALID_DECISION_MEMBERS("hcm_movementTransactionData_getValidDecisionMembers"),
    HCM_COUNT_CONFLICT_DATES_TRANSACTIONS("hcm_movementTransactionData_countConflictDatesTransactions"),
    HCM_GET_LAST_VALID_MOVEMENT_TRANSACTION("hcm_movementTransactionData_getLastValidMovementTransaction"),
    HCM_GET_NOT_EXECUTED_MOVEMENTS_TRANSACTIONS("hcm_movementTransactionData_getNotExecutedMovementsTransactions"),
    HCM_COUNT_EMPLOYEE_TRANSACTIONS_AFTER_GIVEN_EXECUTION_DATE("hcm_movementTransactionData_countEmployeeTransactionsAfterGivenExecutionDate"),
    HCM_GET_EMPLOYESS_AND_MOVEMENTS_TRANSACTIONS_FOR_ROLLBACK("hcm_movementTransactionData_getEmployeesAndMovementsTransactionsForRollback"),
    HCM_GET_FUTUTRE_EFFECT_MOVE_TRANSACTIONS("hcm_movementTransaction_getFutureEffectMoveTransactions"),
    HCM_GET_MOVEMENT_TRANSACTIONS_BY_ID("hcm_movementTransactionData_getMovementTransactionDataByById"),
    HCM_GET_LAST_MOVE_TRANSACTION_FOR_WISHES("hcm_movementTransaction_getLastMoveTransactionForWishes"),
    HCM_GET_EXTERNAL_MOVEMENT_TRANSACTIONS("hcm_movementTransaction_getExternalMovementTransactions"),

    HCM_GET_LAST_DEDUCTED_WORK_DATE("hcm_work_getLastDeductedWorkDate"),
    HCM_GET_LAST_MOVE_TRANSACTION_FOR_JOINING_DATE("hcm_movementTransactionData_getLastMovementTransactionForJoiningDate"),
    HCM_GET_LAST_MOVE_TRANSACTION_FOR_RETURN_JOINING_DATE("hcm_movementTransactionData_getLastMovementTransactionForReturnJoiningDate"),
    HCM_GET_MOVEMENT_TRANSACTION_FOR_JOINING_REPORT("hcm_movementTransactionData_getMovementTransactionForJoiningReport"),
    /* ****************************************** Movement Workflow ********************************************* */
    WF_GET_WFMOVEMENT_BY_INSTANCE_ID("wf_movementData_getWFMovementDataByInstanceId"),
    WF_CHECK_EXISTING_MOVEMENT_PROCESSES("wf_movement_checkExistingMovementProcesses"),
    WF_GET_WFMOVMENT_TASKS("wf_movementData_getMovementsTasks"),
    /* ****************************************** Movement Wishes ********************************************* */
    HCM_SEARCH_MOVEMENT_WISH_TRANSACTIONS("hcm_movementWishTransaction_searchMovementWishTransactions"),
    HCM_SEARCH_MOVEMENT_WISH_TRANSACTIONS_INVALID_TRANSACTIONS("hcm_movementWishTransaction_getMovementWishInvalidTransactions"),
    WF_SEARCH_WFMOVEMENT_WISHES("wf_movementWish_searchWFMovementWishes"),
    WF_GET_WFMOVEMENT_WISHES_BY_INSTANCES_IDS("wf_movementWish_getWFMovementWishesByInstancesIds"),
    /* ***************************************** Promotions ****************************************************** */
    HCM_GET_RANKS_POWER_DATA("hcm_rankPowerData_getAllRanksPowerData"),
    HCM_GET_PROMOTION_SENIORTY_POINTS("hcm_promotionSeniortyPoints_getPromotionSeniortyPoints"),

    HCM_GET_PROMOTION_EMPLOYEE_DEGREE_DATA_BY_EMPS_IDS("hcm_PromotionEmployeeDegreeData_getPromotionEmployeeDegreeDataByEmpsIds"),
    HCM_GET_RANKED_PROMOTION_DETAILS_DATA_BY_REPORT_ID("hcm_promotionRankingData_getRankedPromotionReportDetailsDataByReportId"),

    HCM_GET_PROMOTION_REPORTS("hcm_promotionReport_searchPromotionReports"),
    HCM_COUNT_RUNNING_PROMOTION_REPORTS("hcm_promotionReport_countRunningPromotionReports"),
    HCM_GET_PROMOTION_REPORTS_DATA("hcm_promotionReportData_searchPromotionReportsData"),
    HCM_GET_PROMOTION_REPORT_DETAIL_DATA("hcm_promotionReportDetailData_searchPromotionReportDetailData"),
    HCM_GET_PROMOTION_REPORT_DETAIL_DATA_BY_SOCIAL_IDS("hcm_promotionReportDetailData_searchPromotionReportDetailDataBySocialIds"),
    HCM_GET_PROMOTION_OPENED_REPORT_DETAILS("hcm_promotionReportDetail_getOpenedPromotionReportDetails"),
    HCM_GET_FREEZED_JOBS_IDS_IN_PROMOTION_REPORT_DETAILS("hcm_promotionReportDetail_getFreezedJobsIdsInPromotionReportDetails"),
    HCM_GET_PROMOTION_OFFICERS_REPORT_DETAIL_DATA_BY_ROYAL_NUMBER_AND_ROYAL_DATE("hcm_PromotionReportDetailData_getPromotionReportDetailDataOfficersByRoyalDateAndRoyalNumber"),
    HCM_COUNT_PROMOTION_REPORT_DETAIL_STATUSES("hcm_promotionReportDetail_countPromotionReportDetailsStatuses"),

    HCM_GET_PROMOTION_TRANSACTIONS("hcm_promotionTransactionData_searchPromotionTransactions"),
    HCM_GET_PROMOTIONS_DECISIONS("hcm_promotionTransactionData_searchPromotionTransactionsDecisions"),
    HCM_COUNT_PROMOTION_TRANSACTIONS("hcm_promotionTransaction_countPromotionTransactions"),
    HCM_GET_PROMOTION_TRANSACTION_DATA_BY_DECISION_NUMBER_AND_DECISION_DATE("hcm_promotionTransactionData_getPromotionTransactionDataByDecisionDateAndDecisionNumber"),

    HCM_SEARCH_WF_PROMOTION_NOTIFICATION_DATA("hcm_wfPromotionNotificationData_searchWFPromotionNotificationData"),

    WF_GET_WFPROMOTION_BY_INSTANCE_ID("wf_wfPromotion_getWFPromotionByInstanceId"),
    WF_GET_WFPROMOTIONS_TASKS("wf_wfPromotion_getWFPromotionsTasks"),
    WF_GET_PROMOTIONS_REPORTS_IDS_BY_INSTANCES_IDS("wf_wfPromotion_getPromotionsReportsIdsByInstancesIds"),

    /* ***************************************** Terminations *************************************************** */
    HCM_GET_TERMINATION_REASONS("hcm_terminationReason_getReasons"),

    HCM_GET_TERMINATION_RECORDS("hcm_terminationRecord_searchTerminationRecords"),
    HCM_GET_TERMINATION_RECORDS_DATA("hcm_terminationRecordData_searchTerminationRecordData"),
    HCM_GET_TERMINATION_RECORDS_DATA_BY_ID("hcm_terminationRecordData_searchTerminationRecordDataById"),
    HCM_COUNT_TERMINATION_RECORDS_DETAIL_STATUS("hcm_terminationRecordDetailData_countTerminationRecordDetailStatus"),
    HCM_GET_TERMINATION_RECORD_DETAILS("hcm_terminationRecordDetailData_getTerminationRecordDetails"),
    HCM_GET_MAX_TERMINATION_RECORD_NUMBER("hcm_terminationRecordData_getMaxRecordNumber"),

    HCM_GET_TERMINATION_TRANSACTIONS_BY_CANCEL_TRANSACTION_ID("hcm_terminationTransaction_searchTerminationTransactionsByCancelTransactionId"),
    HCM_SEARCH_LAST_TERMINATION_MOVEMENT_TRANSACTION("hcm_terminationTransaction_searchLastTerminationMovementTransaction"),

    HCM_GET_TERMINATION_TRANSACTIONS("hcm_terminationTransactionData_searchTerminationTransactions"),
    HCM_GET_TERMINATION_TRANSACTIONS_BY_UNIT_HKEY("hcm_terminationTransactionData_getTerminationTransactionsByunitHkey"),
    HCM_GET_TERMINATION_TRANSACTIONS_BY_ID("hcm_terminationTransactionData_searchTerminationTransactionsById"),
    HCM_GET_TERMINATION_TRANSACTIONS_AFTER_DECISION_DATE("hcm_terminationTransactionData_getTerminationTransactionAfterDecisionDate"),
    HCM_GET_TERMINATION_MOVEMENT_TRANSACTIONS("hcm_terminationTransactionData_searchTerminationCivilianMovementTransaction"),
    HCM_GET_TERMINATION_ELIGIBILITY_EMPLOYEES("hcm_empData_searchEmployeesTerminationEligibility"),
    HCM_GET_TERMINATION_ELIGIBILITY_EMPLOYEES_BY_COMPLETION_PERIOD_CURRENT_RANK("hcm_empData_searchEmployeesTerminationEligibilityByRankPeriod"),

    HCM_GET_RUNNING_TERMINATION_RECORDS("hcm_terminationRecordDetail_getRunningTerminationRecords"),

    HCM_GET_LAST_TERMINATION_TRANSACTION("hcm_terminationTransactionData_getLastTerminationTransaction"),

    HCM_SEARCH_EMPLOYEE_TERMINATION_TRANSACTIONS("hcm_terminationTransaction_searchEmployeeTerminationTransactions"),

    /* ****************************************** WF Termination Data ******************************************* */

    WF_GET_RUNNING_TERMINATION_REQUESTS("wf_termination_getRunningTerminationRequests"),
    WF_GET_TERMINATION_BY_INSTANCE_ID("wf_terminationData_getWFTerminationByInstanceId"),
    WF_GET_TERMINATIONS_RECORDS_IDS_BY_INSTANCES_IDS("wf_wfTermination_getTerminationsRecordsIdsByInstancesIds"),

    WF_GET_TERMINATION_CANCELATION_MOVEMENT_BY_INSTANCE_ID("wf_serviceCancelMvt_getWFTerminationCancellationMovementByInstanceId"),
    WF_GET_RUNNING_TERMINATION_CANCELLATION_MOVEMENTS("wf_serviceCancelMvt_getRunningTerminationCancellationMovements"),
    WF_GET_WFTERMINATIONS_TASKS("wf_terminationData_getTerminationsTasks"),

    /* ****************************************** Duty Extension ******************************************* */
    HCM_SEARCH_DUTY_EXTENSION_TRANSACTION_DATA("hcm_dutyExtensionTransactionData_searchDutyExtensionDataTransactions"),

    /* ****************************************** Training And Scholarship ******************************************* */
    WF_GET_WFTRAINING("wf_trainingData_searchWFTrainingData"),
    WF_GET_WFTRAINING_TASKS("wf_trainingData_searchWFTrainingTasks"),
    WF_GET_WF_TRAINING_COURSE_EVENT_TASKS("wf_trainingCourseEventData_searchWFTrainingCourseEventTasks"),
    WF_GET_WFTRAINING_PLAN("wf_trainingPlanData_searchWFTrainingPlanData"),
    WF_SEARCH_WF_TRAINING_COURSE_DATA("wf_wfTrainingCourseData_searchWFTrainingCoursesData"),
    WF_GET_RUNNING_WF_TRAINING_COURSES("wf_wfTrainingCourse_getRunningWFTrainingCourses"),
    HCM_SEARCH_TRAINING_UNITS_DATA("hcm_trainingunitdata_searchTrainingUnitData"),
    HCM_SEARCH_TRAINING_YEARS("hcm_trainingYear_searchTrainingYears"),
    HCM_SEARCH_TRAINING_COURSES("hcm_trainingCourseData_searchTrainingCourseData"),
    HCM_GET_TRAINING_COURSE_BY_TRAINING_TRANSACTION_DETAIL_ID("hcm_trainingCourseData_getTrainingCourseDataByTransactionDetailId"),
    HCM_GET_TRAINING_UNIT_DATA_BY_TRAINING_TRANSACTION_DETAIL_ID("hcm_trainingUnitData_getTrainingUnitDataByTransactionDetailId"),
    HCM_SEARCH_ANNUAL_COURSES("hcm_trainingAnnualCourseData_searchTrainingAnnualCourseData"),
    HCM_SEARCH_TRAINING_COURSES_EVENTS("hcm_trainingCourseEventData_searchTrainingCourseEventData"),
    HCM_SEARCH_MILITARY_TRAINING_COURSES_EVENTS_FOR_BROWSING("hcm_trainingCourseEventData_searchMilitaryTrainingCourseEventDataForBrowsing"),
    HCM_SEARCH_TRAINING_COURSES_EVENTS_FOR_SERIAL_GENERATION("hcm_trainingCourseEventData_searchTrainingCourseEventDataForSerialGeneration"),
    HCM_SEARCH_TRAINING_COURSES_EVENTS_ALLOCATIONS("hcm_trainingCourseEventAllocationData_searchTrainingCourseEventAllocationData"),
    HCM_SEARCH_TRAINING_COURSES_EVENTS_ALLOCATIONS_EMPS("hcm_trainingCourseEventAllocationEmpsData_searchTrainingCourseEventAllocationEmpData"),
    HCM_GET_TRAINING_COURSES_EVENTS_NOT_IN_TRAINING_YEAR_INTERVAL("hcm_trainingCourseEvent_countTrainingCoursesEventsNotInTrainingYearInterval"),
    HCM_SEARCH_TRAINING_EXTERNAL_PARTIES("hcm_trainingExternalPartyData_searchTrainingExternalPartyData"),
    HCM_SEARCH_TRAINING_ANNUAL_PARTIES("hcm_trainingAnnualPartyData_searchTrainingAnnualPartyData"),
    HCM_COUNT_CONFLICT_TRAINING_TRANSACTIONS_DATES("hcm_trainingTransaction_countConflictTrainingTransactionsDates"),
    HCM_SEARCH_TRAINING_TRANSACTIONS("hcm_trainingTransactionData_searchTrainingTransactionsData"),
    HCM_COUNT_CONFLICT_COURSES_EVENTS_DATES("hcm_trainingTransaction_countConflictCoursesEventsDates"),
    HCM_SEARCH_TRAINING_TRANSACTIONS_DETAIL_DATA("hcm_trainingTransactionDetailData_searchTrainingTransactionsDetailData"),
    WF_COUNT_APPROVED_WFTRAINING_PLAN("wf_trainingPlan_countApprovedWFTrainingPlan"),
    HCM_SEARCH_MISSING_TRAINING_UNITS_PLAN_APPROVAL("hcm_trainingunitdata_searchMissingTrainingUnitsPlanApproval"),
    HCM_SEARCH_WF_TRAINING_PLAN_NEED_DATA("wf_trainingPlanNeedData_searchWFTrainingPlanNeedData"),
    HCM_SEARCH_WF_TRAINING_PLAN_NEED_REQUEST("wf_trainingPlanNeedData_searchWFTrainingPlanNeedRequest"),
    HCM_SEARCH_WF_TRAINING_COURSE_EVENT_DATA("wf_trainingCourseEventData_searchWFTrainingCourseEventData"),
    HCM_SEARCH_TRAINING_COURSES_EVENTS_FOR_NOMINATION("hcm_trainingCourseEventData_searchTrainingCourseEventDataForNomination"),
    HCM_SEARCH_TRAINING_COURSE_EVENT_DECISION_DATA("hcm_trainingCourseEventDecisionData_searchTrainingCourseEventDecisionData"),
    HCM_SEARCH_TRAINING_COURSE_EVENT_DECISION_DATA_FOR_INQUIRY("hcm_trainingCourseEventDecisionData_searchTrainingCourseEventDecisionDataForInquiry"),
    HCM_SEARCH_WF_TRAINING_COURSE_DATA("hcm_WFTrainingCourseData_searchWFTrainingCoursesData"),
    HCM_GET_EXTERNAL_PARTIES_NOT_HAVING_ALLOCATION("hcm_trainingCourseEventAllocationData_getExternalPartiesNotHavingAllocation"),
    HCM_GET_COURSE_EVENTS_DATA_NOT_HAVING_ALLOCATION("hcm_trainingCourseEventData_getCourseEventsDataNotHavingAllocation"),
    HCM_COUNT_CONFLICT_COURSES_EVENTS_DATES_FOR_TRAINGING_PLAN("hcm_trainingAllocationEmps_countConflictCoursesEventsDates"),
    HCM_GET_LAST_TRN_COURSE_EVENT_DECISION_EMPLOYEES("hcm_trainingCourseEventDecisionEmployee_getLastTrnCourseEventDecisionEmployees"),
    HCM_COUNT_NUMBER_OF_SEMESTERS_IN_TRAINING_YEAR("hcm_trainingYear_countNumberOfSemestersInTrainingYear"),
    HCM_GET_TRN_COURSE_EVENT_DECISION_EXTERNAL_EMPLOYEES("hcm_trainingCourseEventDecisionEmployee_getTrnCourseEventDecisionExternalEmployees"),
    HCM_GET_TRAINING_COURSES_DECISIONS("hcm_trainingCourseDecisionData_searchTrainingCourseDecision"),

    /* ****************************************** Disclaimer ******************************************* */
    HCM_GET_DISCLAIMER_TRANSACTION_DATA("hcm_disclaimerTransactionData_getDisclaimerTransaction"),
    HCM_GET_DISCLAIMER_TRANSACTION_DATA_AFTER_DESICION_DATE("hcm_disclaimerTransactionData_getDisclaimerTransactionBasedOnDecisionDate"),
    HCM_GET_DISCLAIMER_TRANSACTION_DATA_BY_DECISION_DATE_AND_NUMBER("hcm_disclaimerTransactionData_getDisclaimerTransactionBasedOnDecisionDateAndDecisionNumber"),
    WF_GET_DISCLAIMER_DATA("wf_disclaimerData_getWFDisclaimerData"),
    WF_GET_RETIREMENT_TASKS("wf_disclaimerData_getRetirementsTasks"),
    WF_GET_RUNNING_DISCLAIMER_REQUESTS("wf_disclaimerData_getRunningDisclaimerRequests"),
    WF_GET_DISCLAIMER_DETAILS("wf_disclaimerDetail_getWFDisclaimerDetails"),

    /* ****************************************** Exercise ******************************************* */
    HCM_GET_EXERCISES_DATA("hcm_exerciseData_getExercisesData"),
    HCM_GET_EMPLOYEE_EXERCISES_DATA("hcm_employeeExerciseData_getEmployeeExerciseData"),

    /* ****************************************** SESSION INFO ************************************************* */
    WS_GET_WSSESSION("ws_session_getWSSession"),
    /* ****************************************** UTIL************************************************* */
    UTIL_DECISION_TEMPLATE_SEARCH_DECISION_TEMPLATE("decisionTemplate_searchDecisionTemplate"),
    /* ****************************************** SIGNATURE ******************************************************* */
    ETR_SEARCH_SIGNATURES("etr_signature_searchSignatures"),
    ETR_GET_SIGNATURE_BY_ID("etr_signature_getSignatureById"),
    ETR_GET_DISTINCT_SIGNATURES_DESCRIPTIONS("etr_signature_getDistinctSignDescs"),
    /* ****************************************** Audit ******************************************************* */
    ETR_GET_CONTENT_ENTITIES("etr_auditLog_getContentEntities"),
    ETR_GET_AUDIT_LOGS("etr_auditLog_getAuditLogs"),

    /* ****************************************** EmployeesLog ******************************************************* */
    HCM_EMPLOYEE_LOG_DATA_SEARCH_EMPLOYEES_LOG("hcm_employeeLogData_searchEmployeesLog"),
    HCM_EMPLOYEE_LOG_GET_LAST_EMPLOYEE_LOG("hcm_employeeLog_getLastEmployeeLog"),
    /* ****************************************** General News ************************************************ */
    HCM_GET_GENERAL_NEWS("hcm_generalNews_getGeneralNews"),

    /* ******************************************* Rankings ****************************************************** */
    HCM_RANKINGS_SEARCH_RANKINGS("hcm_rankings_searchRankings"),

    /* ******************************************* Timeline ****************************************************** */
    HCM_GET_ALL_FUTURE_TRANSACTIONS("hcm_transactionTimeline_getAllFutureTransactions"),

    /* ******************************************* Payroll integration ****************************************************** */
    HCM_SEARCH_ADMIN_DECISION("hcm_adminDecision_searchAdminDecision"),
    HCM_GET_ADMIN_DECISION_VARIABLES_MAPPING_BY_VARIABLE_NAME_AND_HCM_VALUE("hcm_adminDecisionVariablesMapping_getAdminDecisionVariablesMappingByVariableNameAndHCMValue"),
    HCM_GET_NON_EXECUTED_PAYROLL_INTEGRATION_FAILURE_LOG("hcm_payrollIntegrationFailureLog_getNonExecutedPayrollIntegrationFailureLog"),

    /* ******************************************* Incentives ****************************************************** */
    HCM_INCENTIVE_GET_INCENTIVE_BY_ID("hcm_incentive_getIncentiveById"),
    HCM_INCENTIVE_PORT_GET_INCENTIVE_PORTS("hcm_incentivePort_getIncentivePorts"),
    HCM_GOVERNMENTAL_COMMITTEE_CATEGORY_GET_GOVERNMENTAL_COMMITTEES_CATEGORIES("hcm_governmentalCommitteeCategory_getGovernmentalCommitteesCategories"),

    /* ******************************************* Employee File Attachment ****************************************************** */
    ETR_COUNT_EMPLOYEE_FILE_ATTACHMENT_BY_DECISION_NUMBER("etr_employeeFileAttachment_countEmployeeFileAttachmentsByDecisionNumber"),
    ETR_EMPLOYEE_FILE_ATTACHMENT_GET_EMPLOYEE_FILE_ATTACHMENT_BY_ATTACHMENT_ID("etr_employeeFileAttachment_getEmployeeFileAttachmentById"),
    ETR_EMPLOYEE_FILE_ATTACHMENT_DETAILS_GET_EMPLOYEE_FILE_ATTACHMENTS("etr_employeeFileAttachmentDetailData_searchEmployeeFileAttachmentDetails"),
    ETR_COUNT_EMPLOYEE_FILE_ATTACHMENT_DETAILS_BY_ATTACHMENT_ID("etr_employeeFileAttachmentDetailData_countEmployeeFileAttachmentDetailsByAttachmentId"),

    /* ******************************************* External Attachment ****************************************************** */
    HCM_EXTERNAL_ATTACHMENT_GET_EXTERNAL_ATTACHMENT_BY_TRANSACTION_ID_AND_TABLE_NAME("hcm_externalAttachment_getExternalAttachmentByTransactionIdAndTableName");

    private String code;

    private QueryNamesEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}