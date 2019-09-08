package com.code.ui.backings.hcm.definitionletters;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.security.EmployeeMenuAction;
import com.code.dal.orm.setup.Country;
import com.code.dal.orm.workflow.hcm.WFDefinitionLetter;
import com.code.enums.CategoriesEnum;
import com.code.enums.EmployeeStatusEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.NavigationEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.security.SecurityService;
import com.code.services.util.CountryService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.hcm.DefinitionLettersWorkFlow;
import com.code.ui.backings.base.WFBaseBacking;

@ManagedBean(name = "definitionLetter")
@ViewScoped
public class DefinitionLetter extends WFBaseBacking {

    private List<Country> embassyList;

    private Long searchEmpId;
    private EmployeeData searchEmployee;

    private WFDefinitionLetter WFDefLetter;

    private boolean retiredFlag;
    private boolean isAdmin = false;
    private Integer onOfficialPaper;

    public DefinitionLetter() {
	super.init();
	super.setScreenTitle(getMessage("title_definitionLetter"));
	onOfficialPaper = FlagsEnum.ON.getCode();
	processId = WFProcessesEnum.DEFINITION_LETTERS.getCode();
	try {
	    if (this.role.equals(WFTaskRolesEnum.REQUESTER.getCode())) {
		beneficiary = requester;
		beneficiarySearchId = beneficiary.getEmpId().toString();
		isAdmin = isRequesterAdmin();

		embassyList = CountryService.listEmbassies();
		WFDefLetter = new WFDefinitionLetter();
		WFDefLetter.setLetterType("10");
		WFDefLetter.setRelatedUserId(loginEmpData.getEmpId());
		WFDefLetter.setRequestDate(HijriDateService.getHijriSysDate());
	    } else {
		WFDefLetter = DefinitionLettersWorkFlow.getWFDefinitionLetterByInstanceId(instance.getInstanceId());

		beneficiary = EmployeesService.getEmployeeData(WFDefLetter.getRelatedUserId());
		beneficiarySearchId = beneficiary.getEmpId().toString();

		embassyList = new ArrayList<Country>();
		if (WFDefLetter.getEmbassyId() != null) {
		    Country embassy = CountryService.getEmbassyById(WFDefLetter.getEmbassyId());
		    if (embassy == null)
			throw new BusinessException("error_general");
		    embassyList.add(embassy);
		}
	    }
	    adjustRetiredFlag();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectBeneficiary() {
	try {
	    if (searchEmpId != null && (searchEmpId.toString().equals(beneficiarySearchId)))
		throw new BusinessException("error_NotSendSamePerson");

	    beneficiary = EmployeesService.getEmployeeData(Long.parseLong(beneficiarySearchId));
	    if (beneficiary == null)
		throw new BusinessException("error_noEmpFound");

	    if (isEmployeeAuthorizedViewCategory()) {
		WFDefLetter.setRelatedUserId(beneficiary.getEmpId());
		adjustRetiredFlag();
	    } else
		throw new BusinessException("error_notAuthorized");

	} catch (BusinessException e) {
	    beneficiary = requester;
	    beneficiarySearchId = beneficiary.getEmpId().toString();
	    WFDefLetter.setRelatedUserId(beneficiary.getEmpId());
	    try {
		adjustRetiredFlag();
	    } catch (BusinessException e1) {
		this.setServerSideErrorMessages(getMessage("error_general"));
	    }
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void adjustRetiredFlag() throws BusinessException {
	if (EmployeeStatusEnum.SERVICE_TERMINATED.getCode() == beneficiary.getStatusId().longValue()) {
	    retiredFlag = true;
	    WFDefLetter.setLetterType("60");
	} else {
	    retiredFlag = false;
	    if (WFDefLetter.getLetterType().equals("60"))
		WFDefLetter.setLetterType("10");
	}
    }

    public void print() {
	try {
	    byte[] bytes = DefinitionLettersWorkFlow.getDefinitionLetterBytes(WFDefLetter.getLetterType(), beneficiary.getEmpId(), WFDefLetter.getEmbassyId(), WFDefLetter.getEmployeeEnglishName(), onOfficialPaper);
	    super.print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String initProcess() {
	try {
	    DefinitionLettersWorkFlow.initDefinitionLetter(requester, searchEmployee, WFDefLetter, processId, taskUrl);
	    return NavigationEnum.OUTBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    private boolean isEmployeeAuthorizedViewCategory() {
	boolean isAuthorized = false;
	long beneficiaryCategoryId = beneficiary.getCategoryId().longValue();
	// If the physical region id of the beneficiary is null (exempted employee),
	// then the beneficiary's PhysicalRegionId will be equal to the "GENERAL DIRECTORATE OF BORDER_GUARDS"
	// which means only the admin of the head office have the authority to print the definition letters for the exempted employees.
	long beneficiaryPhysicalRegionId = (beneficiary.getPhysicalRegionId() == null) ? RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() : beneficiary.getPhysicalRegionId().longValue();
	try {
	    if (RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode() == this.loginEmpData.getPhysicalRegionId().longValue() || beneficiaryPhysicalRegionId == this.loginEmpData.getPhysicalRegionId().longValue()) {
		if (CategoriesEnum.OFFICERS.getCode() == beneficiaryCategoryId)
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.DEF_LETTER.getCode(), MenuActionsEnum.DEF_LETTERS_SHOW_ALL_OFFICERS.getCode());
		else if (CategoriesEnum.SOLDIERS.getCode() == beneficiaryCategoryId)
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.DEF_LETTER.getCode(), MenuActionsEnum.DEF_LETTERS_SHOW_ALL_SOLDIERS.getCode());
		else if (CategoriesEnum.PERSONS.getCode() == beneficiaryCategoryId || CategoriesEnum.USERS.getCode() == beneficiaryCategoryId || CategoriesEnum.WAGES.getCode() == beneficiaryCategoryId || CategoriesEnum.CONTRACTORS.getCode() == beneficiaryCategoryId || CategoriesEnum.MEDICAL_STAFF.getCode() == beneficiaryCategoryId)
		    isAuthorized = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.DEF_LETTER.getCode(), MenuActionsEnum.DEF_LETTERS_SHOW_ALL_EMPLOYEES.getCode());
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
	return isAuthorized;
    }

    private boolean isRequesterAdmin() {
	try {
	    List<EmployeeMenuAction> menuActions = SecurityService.getEmployeeMenuActions(this.loginEmpData.getEmpId(), MenuCodesEnum.DEF_LETTER.getCode());
	    if (menuActions != null && menuActions.size() > 0)
		return true;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
	return false;
    }

    public String closeProcess() {
	try {
	    DefinitionLettersWorkFlow.closeWFInstanceByNotification(instance, currentTask);
	    return NavigationEnum.INBOX.toString();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return null;
	}
    }

    public void searchEmployee() {
	try {
	    if (searchEmpId != null && searchEmpId.toString().equals(beneficiarySearchId))
		throw new BusinessException("error_NotSendSamePerson");

	    searchEmployee = EmployeesService.getEmployeeData(searchEmpId);
	    if (searchEmployee == null)
		throw new BusinessException("error_noEmpFound");

	} catch (BusinessException e) {
	    searchEmpId = null;
	    searchEmployee = null;
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<Country> getEmbassyList() {
	return embassyList;
    }

    public void setEmbassyList(List<Country> embassyList) {
	this.embassyList = embassyList;
    }

    public Long getSearchEmpId() {
	return searchEmpId;
    }

    public void setSearchEmpId(Long searchEmpId) {
	this.searchEmpId = searchEmpId;
    }

    public EmployeeData getSearchEmployee() {
	return searchEmployee;
    }

    public void setSearchEmployee(EmployeeData searchEmployee) {
	this.searchEmployee = searchEmployee;
    }

    public WFDefinitionLetter getWFDefLetter() {
	return WFDefLetter;
    }

    public void setWFDefLetter(WFDefinitionLetter wFDefLetter) {
	WFDefLetter = wFDefLetter;
    }

    public boolean isRetiredFlag() {
	return retiredFlag;
    }

    public void setRetiredFlag(boolean retiredFlag) {
	this.retiredFlag = retiredFlag;
    }

    public boolean isAdmin() {
	return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
	this.isAdmin = isAdmin;
    }

    public Integer getOnOfficialPaper() {
	return onOfficialPaper;
    }

    public void setOnOfficialPaper(Integer onOfficialPaper) {
	this.onOfficialPaper = onOfficialPaper;
    }

}
