package com.code.ui.backings.hcm.employees;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.additionalspecializations.EmployeeAdditionalSpecializationData;
import com.code.dal.orm.hcm.attachments.EmployeeFileAttachment;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.missions.MissionData;
import com.code.dal.orm.hcm.missions.MissionDetailData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.payroll.EmployeeAllowancesData;
import com.code.dal.orm.hcm.payroll.EmployeeBonusesData;
import com.code.dal.orm.hcm.payroll.EmployeePenalitiesData;
import com.code.dal.orm.hcm.promotions.PromotionTransactionData;
import com.code.dal.orm.hcm.raises.RaiseTransactionData;
import com.code.dal.orm.hcm.recruitments.RecruitmentTransactionData;
import com.code.dal.orm.hcm.retirements.DisclaimerTransactionData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.dal.orm.security.EmployeeMenuAction;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.GendersEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.PromotionsTypesEnum;
import com.code.enums.RecruitmentTypeEnum;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.AdditionalSpecializationsService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.MissionsService;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.PayrollsService;
import com.code.services.hcm.PromotionsService;
import com.code.services.hcm.RaisesService;
import com.code.services.hcm.RecruitmentsService;
import com.code.services.hcm.RetirementsService;
import com.code.services.hcm.TerminationsService;
import com.code.services.hcm.UnitsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.EmployeeFileAttachmentService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "employeeFile")
@ViewScoped
public class EmployeeFile extends BaseBacking implements Serializable {
    private int mode;
    private int pageSize = 5;
    private boolean isModifyAdmin;
    private boolean isViewAdmin;
    private long regionId;

    private boolean attachmentContentFlag;
    private EmployeeFileAttachment attachment;
    private String uploadURL;
    private String fileUploadParams;

    private List<RecruitmentTransactionData> recruitments;
    private List<RecruitmentTransactionData> graduationLetters;
    private List<MovementTransactionData> movements;
    private List<VacationData> vacations;
    private List<MissionDetailData> missions;
    private List<PromotionTransactionData> promotions;
    private List<TerminationTransactionData> terminations;
    private List<DisclaimerTransactionData> disclaimerTransactions;
    private List<EmployeeAdditionalSpecializationData> additionalSpecializations;
    private List<EmployeeBonusesData> bonuses;
    private List<EmployeePenalitiesData> penalities;
    private List<EmployeeAllowancesData> allowances;
    private List<RaiseTransactionData> raises;

    private EmployeeData employee;
    private List<EmployeeMenuAction> employeeMenuActions;

    public EmployeeFile() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		employee = new EmployeeData();
		attachment = new EmployeeFileAttachment();
		employee.setCategoryId(new Long(mode));
		if (mode == loginEmpData.getCategoryId().intValue() || (mode == 3 && (loginEmpData.getCategoryId().longValue() == CategoriesEnum.USERS.getCode() || loginEmpData.getCategoryId().longValue() == CategoriesEnum.WAGES.getCode() || loginEmpData.getCategoryId().longValue() == CategoriesEnum.CONTRACTORS.getCode() || loginEmpData.getCategoryId().longValue() == CategoriesEnum.MEDICAL_STAFF.getCode()))) {
		    employee.setEmpId(this.loginEmpData.getEmpId());
		    getEmployeeFileData();
		}
		isModifyAdmin = false;
		isViewAdmin = false;
		attachmentContentFlag = false;
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_officerFile"));
		    employeeMenuActions = SecurityService.getEmployeeMenuActions(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_OFFICER_FILE.getCode());
		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldierFile"));
		    employeeMenuActions = SecurityService.getEmployeeMenuActions(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_SOLDIER_FILE.getCode());
		    break;
		case 3:
		    setScreenTitle(getMessage("title_personFile"));
		    employeeMenuActions = SecurityService.getEmployeeMenuActions(loginEmpData.getEmpId(), MenuCodesEnum.EMPS_CIVILIAN_FILE.getCode());
		    break;
		default:
		    setServerSideErrorMessages(getMessage("error_URLError"));
		    break;
		}

		for (EmployeeMenuAction menuAction : employeeMenuActions) {
		    if (menuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_MODIFY_OFFICERS.getCode()) || menuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_MODIFY_SOLDIERS.getCode()) || menuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_MODIFY_CIVILIANS.getCode())) {
			isModifyAdmin = true;
			break;
		    }
		}

		if (employeeMenuActions != null && employeeMenuActions.size() > 0 && isModifyAdmin == false)
		    isViewAdmin = true;

		regionId = getLoginEmpPhysicalRegionFlag(isModifyAdmin || isViewAdmin);
		uploadURL = ETRConfigurationService.getAttachmentUploadURL();
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addAttachment() {
	attachment = new EmployeeFileAttachment();
	attachmentContentFlag = true;
    }

    public void saveEmployeeFileAttachment(Integer transactionClass) {
	try {
	    attachment.setTransactionClass(transactionClass);
	    attachment.setEmpId(employee.getEmpId());

	    EmployeeFileAttachmentService.addEmployeeFileAttachment(attachment, loginEmpData.getEmpId());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void cancelAttachment() {
	try {
	    if (!attachment.equals(null) && attachment.getId() != null)
		EmployeeFileAttachmentService.deleteEmployeeFileAttachment(attachment);
	    resetAttachment();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetAttachment() {
	attachmentContentFlag = false;
	attachment = new EmployeeFileAttachment();
    }

    public void getEmployeeFileData() {
	try {
	    EmployeeData selectedEmployee = EmployeesService.getEmployeeData(employee.getEmpId());
	    Boolean tempIsModifyAdmin = isModifyAdmin;
	    Boolean tempIsViewAdmin = isViewAdmin;
	    if (selectedEmployee == null)
		throw new BusinessException("error_UIError");
	    else if (loginEmpData.getEmpId().equals(selectedEmployee.getEmpId())
		    || (this.loginEmpData.getIsManager().equals(FlagsEnum.ON.getCode()) && selectedEmployee.getPhysicalUnitHKey() != null && selectedEmployee.getPhysicalUnitHKey().startsWith(UnitsService.getHKeyPrefix(this.loginEmpData.getPhysicalUnitHKey())))
		    || isRequesterAuthorizedForModifyOnly(selectedEmployee) || isRequesterAuthorizedForViewOnly(selectedEmployee)) {
		employee = selectedEmployee;
		movements = MovementsService.getMovementTransactionsHistory(employee.getEmpId());
		recruitments = RecruitmentsService.getRecruitmentTransactionsHistory(employee.getEmpId(), new Integer[] { RecruitmentTypeEnum.RECRUITMENT.getCode(), RecruitmentTypeEnum.RE_RECRUITMENT.getCode(), RecruitmentTypeEnum.RECRUITMENT_BY_EXTERNAL_MOVE.getCode() });
		graduationLetters = RecruitmentsService.getRecruitmentTransactionsHistory(employee.getEmpId(), new Integer[] { RecruitmentTypeEnum.GRADUATION_LETTER.getCode() });
		vacations = VacationsService.getVacationTransactionsHistory(employee.getEmpId());
		missions = MissionsService.getMissionsHistory(employee.getEmpId());
		promotions = PromotionsService.getPromotionsHistory(employee.getEmpId(), FlagsEnum.ALL.getCode());
		terminations = TerminationsService.getTerminationsHistory(employee.getEmpId());
		disclaimerTransactions = RetirementsService.getDisclaimersHistory(employee.getEmpId());
		additionalSpecializations = AdditionalSpecializationsService.getEmployeeAdditionalSpecializationsDataByEmpId(employee.getEmpId());
		penalities = PayrollsService.getEmployeePenalities(employee.getOldEmpId());
		allowances = PayrollsService.getEmployeeAllowances(employee.getOldEmpId());
		bonuses = PayrollsService.getEmployeeBonuses(employee.getOldEmpId());
		raises = RaisesService.getAllRaisesByEmployeeId(employee.getEmpId());
	    } else {
		isModifyAdmin = tempIsModifyAdmin;
		isViewAdmin = tempIsViewAdmin;
		throw new BusinessException("error_notAuthorized");
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private boolean isRequesterAuthorizedForViewOnly(EmployeeData searchEmployee) throws BusinessException {
	if (employeeMenuActions.size() == 0)
	    return false;

	long searchEmployeePhysicalRegionId = searchEmployee.getPhysicalRegionId() != null ? searchEmployee.getPhysicalRegionId() : FlagsEnum.ALL.getCode();
	long searchEmployeeOfficialRegionId = searchEmployee.getOfficialRegionId() != null ? searchEmployee.getOfficialRegionId() : FlagsEnum.ALL.getCode();

	if (loginEmpData.getPhysicalRegionId() != RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() && searchEmployeePhysicalRegionId != FlagsEnum.ALL.getCode()
		&& searchEmployeePhysicalRegionId != loginEmpData.getPhysicalRegionId() && searchEmployeeOfficialRegionId != loginEmpData.getPhysicalRegionId())
	    return false;

	// check needed for service terminated employees
	if (searchEmployee.getPhysicalRegionId() == null) {
	    for (EmployeeMenuAction employeeMenuAction : employeeMenuActions) {
		if (employeeMenuAction.getBeneficiaryRegionId() == null && employeeMenuAction.getBeneficiaryUnitId() == null) {
		    if (searchEmployee.getCategoryId() == CategoriesEnum.OFFICERS.getCode() && employeeMenuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_VIEW_OFFICERS.getCode()))
			return true;
		    else if (searchEmployee.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() && ((searchEmployee.getGender().equals(GendersEnum.MALE.getCode()) && employeeMenuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_VIEW_SOLDIERS.getCode())) ||
			    (searchEmployee.getGender().equals(GendersEnum.FEMALE.getCode()) && employeeMenuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_VIEW_FEMALE_SOLDIERS.getCode()))))
			return true;
		    else if ((CategoriesEnum.PERSONS.getCode() == searchEmployee.getCategoryId() || CategoriesEnum.USERS.getCode() == searchEmployee.getCategoryId()
			    || CategoriesEnum.WAGES.getCode() == searchEmployee.getCategoryId() || CategoriesEnum.CONTRACTORS.getCode() == searchEmployee.getCategoryId()
			    || CategoriesEnum.MEDICAL_STAFF.getCode() == searchEmployee.getCategoryId()) && ((searchEmployee.getGender().equals(GendersEnum.MALE.getCode()) && employeeMenuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_VIEW_CIVILIANS.getCode())) ||
				    (searchEmployee.getGender().equals(GendersEnum.FEMALE.getCode()) && employeeMenuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_VIEW_FEMALE_CIVILIANS.getCode()))))
			return true;
		}
	    }
	    return false;
	}

	if (searchEmployee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode())) {
	    return SecurityService.isEmployeeMenuActionGranted(searchEmployeePhysicalRegionId, searchEmployee.getPhysicalUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_VIEW_OFFICERS.getCode(), employeeMenuActions)
		    ||
		    SecurityService.isEmployeeMenuActionGranted(searchEmployeeOfficialRegionId, searchEmployee.getOfficialUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_VIEW_OFFICERS.getCode(), employeeMenuActions);
	} else if (searchEmployee.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
	    if (searchEmployee.getGender().equals(GendersEnum.MALE.getCode())) {
		return SecurityService.isEmployeeMenuActionGranted(searchEmployeePhysicalRegionId, searchEmployee.getPhysicalUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_VIEW_SOLDIERS.getCode(), employeeMenuActions)
			||
			SecurityService.isEmployeeMenuActionGranted(searchEmployeeOfficialRegionId, searchEmployee.getOfficialUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_VIEW_SOLDIERS.getCode(), employeeMenuActions);
	    } else {
		return SecurityService.isEmployeeMenuActionGranted(searchEmployeePhysicalRegionId, searchEmployee.getPhysicalUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_VIEW_FEMALE_SOLDIERS.getCode(), employeeMenuActions)
			||
			SecurityService.isEmployeeMenuActionGranted(searchEmployeeOfficialRegionId, searchEmployee.getOfficialUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_VIEW_FEMALE_SOLDIERS.getCode(), employeeMenuActions);
	    }
	} else {
	    if (searchEmployee.getGender().equals(GendersEnum.MALE.getCode())) {
		return SecurityService.isEmployeeMenuActionGranted(searchEmployeePhysicalRegionId, searchEmployee.getPhysicalUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_VIEW_CIVILIANS.getCode(), employeeMenuActions)
			||
			SecurityService.isEmployeeMenuActionGranted(searchEmployeeOfficialRegionId, searchEmployee.getOfficialUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_VIEW_CIVILIANS.getCode(), employeeMenuActions);
	    } else {
		return SecurityService.isEmployeeMenuActionGranted(searchEmployeePhysicalRegionId, searchEmployee.getPhysicalUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_VIEW_FEMALE_CIVILIANS.getCode(), employeeMenuActions)
			||
			SecurityService.isEmployeeMenuActionGranted(searchEmployeeOfficialRegionId, searchEmployee.getOfficialUnitHKey(), MenuActionsEnum.PRS_EMPS_FILE_VIEW_FEMALE_CIVILIANS.getCode(), employeeMenuActions);

	    }
	}
    }

    private boolean isRequesterAuthorizedForModifyOnly(EmployeeData searchEmployee) throws BusinessException {
	isModifyAdmin = false;
	for (EmployeeMenuAction menuAction : employeeMenuActions) {
	    if (menuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_MODIFY_OFFICERS.getCode()) || menuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_MODIFY_SOLDIERS.getCode()) || menuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_MODIFY_CIVILIANS.getCode())) {
		isModifyAdmin = true;
		return isModifyAdmin;
	    }
	}
	if (searchEmployee.getCategoryId().equals(CategoriesEnum.OFFICERS.getCode()))
	    return isModifyAdmin;
	else if (searchEmployee.getCategoryId().equals(CategoriesEnum.SOLDIERS.getCode())) {
	    if (searchEmployee.getGender().equals(GendersEnum.MALE.getCode())) {
		for (EmployeeMenuAction menuAction : employeeMenuActions) {
		    if (menuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_MODIFY_MALE_SOLDIERS.getCode())) {
			isModifyAdmin = true;
			isViewAdmin = false;
			return isModifyAdmin;
		    }
		}
	    } else {
		for (EmployeeMenuAction menuAction : employeeMenuActions) {
		    if (menuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_MODIFY_FEMALE_SOLDIERS.getCode())) {
			isModifyAdmin = true;
			isViewAdmin = false;
			return isModifyAdmin;
		    }
		}
	    }
	} else {
	    if (searchEmployee.getGender().equals(GendersEnum.MALE.getCode())) {
		for (EmployeeMenuAction menuAction : employeeMenuActions) {
		    if (menuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_MODIFY_MALE_CIVILIANS.getCode())) {
			isModifyAdmin = true;
			isViewAdmin = false;
			return isModifyAdmin;
		    }
		}
	    } else {
		for (EmployeeMenuAction menuAction : employeeMenuActions) {
		    if (menuAction.getAction().equals(MenuActionsEnum.PRS_EMPS_FILE_MODIFY_FEMALE_CIVILIANS.getCode())) {
			isModifyAdmin = true;
			isViewAdmin = false;
			return isModifyAdmin;
		    }
		}
	    }

	}
	return isModifyAdmin;
    }

    public void updateRecruitmentJoiningDate(RecruitmentTransactionData recruitment) {
	try {
	    RecruitmentsService.updateRecruitmentTransactionJoiningDate(recruitment.getId(), recruitment.getJoiningDate(), loginEmpData.getEmpId());
	    if (recruitment.getRecruitmentType().equals(FlagsEnum.ON.getCode())) {
		recruitments = RecruitmentsService.getRecruitmentTransactionsHistory(employee.getEmpId(), new Integer[] { RecruitmentTypeEnum.RECRUITMENT.getCode() });
	    } else {
		graduationLetters = RecruitmentsService.getRecruitmentTransactionsHistory(employee.getEmpId(), new Integer[] { RecruitmentTypeEnum.GRADUATION_LETTER.getCode() });
	    }
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    recruitment.setJoiningDate(null);
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateMovementJoiningDate(MovementTransactionData movement) {
	try {
	    MovementsService.handleMovementTrasactionJoiningDate(movement.getId(), movement.getJoiningDate(), null, loginEmpData.getEmpId(), FlagsEnum.OFF.getCode());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateVacationJoiningDate(VacationData vacation) {
	try {
	    VacationsService.updateVacationJoiningDate(vacation.getId(), vacation.getJoiningDate(), loginEmpData.getEmpId());
	    vacations = VacationsService.getVacationTransactionsHistory(employee.getEmpId());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateMissionJoiningDate(MissionDetailData mission) {
	try {
	    MissionsService.modifyMissionJoiningDate(mission.getId(), mission.getJoiningDate(), loginEmpData.getEmpId());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updatePromotionJoiningDate(PromotionTransactionData promotion) {
	try {
	    PromotionsService.modifyPromotionJoiningDate(promotion.getId(), promotion.getJoiningDate(), loginEmpData.getEmpId());
	    employee = EmployeesService.getEmployeeData(employee.getEmpId());
	    if (employee == null)
		throw new BusinessException("error_employeeDataError");

	    promotions = PromotionsService.getPromotionsHistory(employee.getEmpId(), FlagsEnum.ALL.getCode());
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    promotion.setJoiningDate(null);
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printMovement(MovementTransactionData movement) {
	try {
	    byte[] bytes = MovementsService.getMovementDecisionBytes(movement);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printMovementJoiningDocument(MovementTransactionData movement) {
	try {
	    byte[] bytes = MovementsService.getJoiningDocumentBytes(movement.getId(), movement.getMovementTypeId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printRecruitment(RecruitmentTransactionData recuruitment) {
	try {
	    byte[] reportBytes = RecruitmentsService.getRecruitmentDecisionBytes(recuruitment);
	    this.print(reportBytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printDisclaimer(Long disclaimerTransactionId) {
	try {
	    byte[] reportBytes = RetirementsService.getDisclaimerDecisionBytes(disclaimerTransactionId);
	    this.print(reportBytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printMission(MissionDetailData missionDetail) {
	try {
	    MissionData mission = MissionsService.getMissionsById(missionDetail.getMissionId());
	    byte[] bytes = MissionsService.getMissionDecisionBytes(mission.getId(), mission.getCategoryMode());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printVaction(VacationData vacation) {
	try {
	    byte[] bytes = VacationsService.getVacationDecisionBytes(vacation.getId(), vacation.getVacationTypeId(), vacation.getTransactionEmpCategoryId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printJoiningDocument(VacationData vacation) {
	try {
	    byte[] bytes = VacationsService.getJoiningDocumentBytes(vacation.getId());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printPromotion(PromotionTransactionData promotion) {
	try {
	    Date promotionTransactionDecisionDate = null;
	    String promotionTransactionDecisionNumber = null;
	    if (promotion.getPromotionTypeId() == PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode()) {
		PromotionTransactionData originalPromotionTransaction = PromotionsService.getPromotionTransactionById(promotion.getBasedOnTransactionId());
		promotionTransactionDecisionDate = originalPromotionTransaction.getDecisionDate();
		promotionTransactionDecisionNumber = originalPromotionTransaction.getDecisionNumber();
	    }
	    byte[] bytes = PromotionsService.getPromotionBytes(promotion, promotionTransactionDecisionDate, promotionTransactionDecisionNumber);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printTerminationReport(TerminationTransactionData terminationTransactionData) {
	try {
	    byte[] bytes = TerminationsService.getTerminationDecisionBytes(terminationTransactionData.getId(), mode, terminationTransactionData.getTransactionTypeCode());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void printDisclaimerStepsReport(DisclaimerTransactionData disclaimerTransactionData) {
	try {
	    EmployeeData employee = EmployeesService.getEmployeeData(disclaimerTransactionData.getEmpId());
	    TerminationTransactionData employeeTerminationTransaction = TerminationsService.getTerminationTransactionById(disclaimerTransactionData.getTerminationTransactionId());
	    byte[] bytes = RetirementsService.getDisclaimerStepsBytes(employeeTerminationTransaction, employee.getMilitaryNumber());
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updateMovementBlackListFlag() {
	try {
	    employee.getEmployee().setSystemUser(this.loginEmpData.getEmpId() + ""); // For
										     // auditing.
	    EmployeesService.updateEmployee(employee);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getMode() {
	return mode;
    }

    public boolean getIsModifyAdmin() {
	return isModifyAdmin;
    }

    public long getRegionId() {
	return regionId;
    }

    public void setRegionId(long regionId) {
	this.regionId = regionId;
    }

    public List<RecruitmentTransactionData> getRecruitments() {
	return recruitments;
    }

    public List<RecruitmentTransactionData> getGraduationLetters() {
	return graduationLetters;
    }

    public List<MovementTransactionData> getMovements() {
	return movements;
    }

    public void setMovements(List<MovementTransactionData> movements) {
	this.movements = movements;
    }

    public List<VacationData> getVacations() {
	return vacations;
    }

    public void setVacations(List<VacationData> vacations) {
	this.vacations = vacations;
    }

    public List<MissionDetailData> getMissions() {
	return missions;
    }

    public void setMissions(List<MissionDetailData> missions) {
	this.missions = missions;
    }

    public int getPageSize() {
	return pageSize;
    }

    public EmployeeData getEmployee() {
	return employee;
    }

    public void setEmployee(EmployeeData employee) {
	this.employee = employee;
    }

    public List<PromotionTransactionData> getPromotions() {
	return promotions;
    }

    public void setPromotions(List<PromotionTransactionData> promotions) {
	this.promotions = promotions;
    }

    public List<EmployeeAdditionalSpecializationData> getAdditionalSpecializations() {
	return additionalSpecializations;
    }

    public void setAdditionalSpecializations(List<EmployeeAdditionalSpecializationData> additionalSpecializations) {
	this.additionalSpecializations = additionalSpecializations;
    }

    public List<TerminationTransactionData> getTerminations() {
	return terminations;
    }

    public void setTerminations(List<TerminationTransactionData> terminations) {
	this.terminations = terminations;
    }

    public boolean getIsViewAdmin() {
	return isViewAdmin;
    }

    public List<EmployeeBonusesData> getBonuses() {
	return bonuses;
    }

    public void setBonuses(List<EmployeeBonusesData> bonuses) {
	this.bonuses = bonuses;
    }

    public List<EmployeePenalitiesData> getPenalities() {
	return penalities;
    }

    public void setPenalities(List<EmployeePenalitiesData> penalities) {
	this.penalities = penalities;
    }

    public List<EmployeeAllowancesData> getAllowances() {
	return allowances;
    }

    public void setAllowances(List<EmployeeAllowancesData> allowances) {
	this.allowances = allowances;
    }

    public List<EmployeeMenuAction> getEmployeeMenuActions() {
	return employeeMenuActions;
    }

    public void setEmployeeMenuActions(List<EmployeeMenuAction> employeeMenuActions) {
	this.employeeMenuActions = employeeMenuActions;
    }

    public List<DisclaimerTransactionData> getDisclaimerTransactions() {
	return disclaimerTransactions;
    }

    public void setDisclaimerTransactions(List<DisclaimerTransactionData> disclaimerTransactions) {
	this.disclaimerTransactions = disclaimerTransactions;
    }

    public List<RaiseTransactionData> getRaises() {
	return raises;
    }

    public void setRaises(List<RaiseTransactionData> raises) {
	this.raises = raises;
    }

    public EmployeeFileAttachment getAttachment() {
	return attachment;
    }

    public void setAttachment(EmployeeFileAttachment attachment) {
	this.attachment = attachment;
    }

    public boolean isAttachmentContentFlag() {
	return attachmentContentFlag;
    }

    public void setAttachmentContentFlag(boolean attachmentContentFlag) {
	this.attachmentContentFlag = attachmentContentFlag;
    }

    public String getFileUploadParams() {
	return fileUploadParams;
    }

    public void setFileUploadParams(String fileUploadParams) {
	this.fileUploadParams = fileUploadParams;
    }

    public String getUploadURL() {
	return uploadURL;
    }

}
