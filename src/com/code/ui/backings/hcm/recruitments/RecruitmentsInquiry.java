package com.code.ui.backings.hcm.recruitments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.GraduationGroupPlace;
import com.code.dal.orm.hcm.recruitments.RecruitmentTransactionData;
import com.code.dal.orm.security.EmployeeDecisionPrivilege;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.RecruitmentsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "recruitmentsInquiry")
@ViewScoped
public class RecruitmentsInquiry extends BaseBacking implements Serializable {
    private int mode;
    private boolean adminUser;
    private Long categoryId;
    private Integer recruitmentType;
    private Long employeeId;
    private String decisionNumber;
    private String basedOnOrderNumber;
    private Date decisionDateFrom;
    private Date decisionDateTo;
    private Integer graduationGroupNumber;
    private Integer graduationGroupPlace;
    private long regionId;

    private List<EmployeeDecisionPrivilege> decisionsPrivileges;

    private List<Category> categories;
    private List<RecruitmentTransactionData> recruitmentsTransactions;
    private List<GraduationGroupPlace> officersGraduationGroupPlaces;
    private Long[] categoriesIds;
    private int pageSize = 10;

    public RecruitmentsInquiry() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_officersRecruitmentInquiry"));
		    categoriesIds = new Long[] { CommonService.getCategoryById(CategoriesEnum.OFFICERS.getCode()).getId() };
		    officersGraduationGroupPlaces = CommonService.getAllGraduationGroupPlaces();

		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersRecruitmentInquiry"));
		    categoriesIds = new Long[] { CommonService.getCategoryById(CategoriesEnum.SOLDIERS.getCode()).getId() };
		    break;
		case 3:
		    setScreenTitle(getMessage("title_personsRecruitmentInquiry"));
		    categoriesIds = CommonService.getCivilCategoriesIdsArray();
		    // load categories list
		    categories = CommonService.getPersonsCategories();
		    break;
		default:
		    setServerSideErrorMessages(getMessage("error_URLError"));
		}

	    } else {
		setServerSideErrorMessages(getMessage("error_URLError"));
	    }

	    categoryId = new Long(-1);

	    decisionsPrivileges = SecurityService.getEmployeesDecisionsPrivileges(this.loginEmpData.getEmpId(), WFProcessesGroupsEnum.RECRUITMENTS.getCode(), mode);
	    adminUser = decisionsPrivileges.size() == 0 ? false : true;

	    regionId = getLoginEmpPhysicalRegionFlag(adminUser);

	    resetForm();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void search() {
	try {
	    if (!SecurityService.isDecisionPrivilegeGranted(this.loginEmpData, employeeId, decisionsPrivileges))
		throw new BusinessException("error_notAuthorized");

	    recruitmentsTransactions = RecruitmentsService.getRecruitmentTransactions(categoryId.longValue() == -1 ? categoriesIds : new Long[] { categoryId }, employeeId == null ? FlagsEnum.ALL.getCode() : employeeId, decisionNumber, basedOnOrderNumber, decisionDateFrom, decisionDateTo, graduationGroupNumber == null ? FlagsEnum.ALL.getCode() : graduationGroupNumber, graduationGroupPlace == null ? FlagsEnum.ALL.getCode() : graduationGroupPlace, regionId,
		    recruitmentType == FlagsEnum.ALL.getCode() ? null : new Integer[] { recruitmentType });

	} catch (BusinessException e) {
	    recruitmentsTransactions = new ArrayList<RecruitmentTransactionData>();
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	basedOnOrderNumber = null;
	decisionDateFrom = null;
	decisionDateTo = null;
	decisionNumber = null;
	employeeId = null;
	recruitmentType = FlagsEnum.ALL.getCode();
	graduationGroupNumber = null;
	graduationGroupPlace = null;
	recruitmentsTransactions = null;
    }

    public void print(RecruitmentTransactionData transaction) {
	try {
	    byte[] reportBytes = RecruitmentsService.getRecruitmentDecisionBytes(transaction);
	    this.print(reportBytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public int getMode() {
	return this.mode;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public String getBasedOnOrderNumber() {
	return basedOnOrderNumber;
    }

    public void setBasedOnOrderNumber(String basedOnOrderNumber) {
	this.basedOnOrderNumber = basedOnOrderNumber;
    }

    public Date getDecisionDateFrom() {
	return decisionDateFrom;
    }

    public void setDecisionDateFrom(Date decisionDateFrom) {
	this.decisionDateFrom = decisionDateFrom;
    }

    public Date getDecisionDateTo() {
	return decisionDateTo;
    }

    public void setDecisionDateTo(Date decisionDateTo) {
	this.decisionDateTo = decisionDateTo;
    }

    public Integer getGraduationGroupNumber() {
	return graduationGroupNumber;
    }

    public void setGraduationGroupNumber(Integer graduationGroupNumber) {
	this.graduationGroupNumber = graduationGroupNumber;
    }

    public Integer getGraduationGroupPlace() {
	return graduationGroupPlace;
    }

    public void setGraduationGroupPlace(Integer graduationGroupPlace) {
	this.graduationGroupPlace = graduationGroupPlace;
    }

    public long getRegionId() {
	return regionId;
    }

    public Long getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Long categoryId) {
	this.categoryId = categoryId;
    }

    public Integer getRecruitmentType() {
	return recruitmentType;
    }

    public void setRecruitmentType(Integer recruitmentType) {
	this.recruitmentType = recruitmentType;
    }

    public Long getEmployeeId() {
	return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
	this.employeeId = employeeId;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public List<Category> getCategories() {
	return categories;
    }

    public void setCategories(List<Category> categories) {
	this.categories = categories;
    }

    public List<RecruitmentTransactionData> getRecruitmentsTransactions() {
	return recruitmentsTransactions;
    }

    public void setRecruitmentsTransactions(List<RecruitmentTransactionData> recruitmentsTransactions) {
	this.recruitmentsTransactions = recruitmentsTransactions;
    }

    public List<GraduationGroupPlace> getOfficersGraduationGroupPlaces() {
	return officersGraduationGroupPlaces;
    }

    public void setOfficersGraduationGroupPlaces(List<GraduationGroupPlace> officersGraduationGroupPlaces) {
	this.officersGraduationGroupPlaces = officersGraduationGroupPlaces;
    }

    public boolean isAdminUser() {
	return adminUser;
    }
}
