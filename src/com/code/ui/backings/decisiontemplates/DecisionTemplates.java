package com.code.ui.backings.decisiontemplates;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.DecisionTemplate;
import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.missions.MissionData;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.promotions.PromotionTransactionData;
import com.code.dal.orm.hcm.recruitments.RecruitmentTransactionData;
import com.code.dal.orm.hcm.terminations.TerminationTransactionData;
import com.code.dal.orm.hcm.vacations.Vacation;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.decisiontemplates.DecisionTemplatesService;
import com.code.services.hcm.MissionsService;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.PromotionsService;
import com.code.services.hcm.RecruitmentsService;
import com.code.services.hcm.TerminationsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "decisionTemplates")
@ViewScoped
public class DecisionTemplates extends BaseBacking {

    // For Search
    private String decisionDesc;
    private Integer categoryId;
    private Integer reportClass;
    private Integer transactionClass;
    private Integer transactionBussinessType;
    private Date validFromDate;
    private Date validToDate;
    private List<Category> categories;

    private List<DecisionTemplate> templates;
    private DecisionTemplate selectedTemplate;

    private boolean isAdmin;
    private int pageSize = 5;

    public DecisionTemplates() {
	super();
	resetSearch();
	categories = CommonService.getAllCategories();
	try {
	    isAdmin = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.ETR_DECISION_TEMPLATES.getCode(), MenuActionsEnum.ETR_DECISION_TEMPLATES_ADMIN.getCode());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetSearch() {
	decisionDesc = "";
	categoryId = transactionClass = FlagsEnum.ALL.getCode();
	reportClass = transactionBussinessType = null;
	validFromDate = validToDate = null;
	templates = new ArrayList<DecisionTemplate>();
	selectedTemplate = null;
    }

    public void search() {
	try {
	    templates = DecisionTemplatesService.getDecisionTemplates(decisionDesc, categoryId, reportClass == null ? FlagsEnum.ALL.getCode() : reportClass, transactionClass, transactionBussinessType == null ? FlagsEnum.ALL.getCode() : transactionBussinessType, validFromDate, validToDate);
	    selectedTemplate = null;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void saveTemplate() {
	try {
	    DecisionTemplatesService.updateDecisionTemplate(selectedTemplate);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print(DecisionTemplate template) {
	if (template.getSampleTransactionId() != null) {
	    try {
		if (template.getTransactionClass().intValue() == TransactionClassesEnum.MOVEMENTS.getCode()) {
		    MovementTransactionData movement = MovementsService.getMovementTransactionById(template.getSampleTransactionId());
		    byte[] bytes = MovementsService.getMovementDecisionBytes(movement);
		    super.print(bytes);
		} else if (template.getTransactionClass().intValue() == TransactionClassesEnum.RECRUITMENT.getCode()) {
		    RecruitmentTransactionData transaction = RecruitmentsService.getRecruitmentTransactionById(template.getSampleTransactionId());
		    byte[] bytes = RecruitmentsService.getRecruitmentDecisionBytes(transaction);
		    super.print(bytes);
		} else if (template.getTransactionClass().intValue() == TransactionClassesEnum.PROMOTIONS.getCode()) {
		    PromotionTransactionData promotion = PromotionsService.getPromotionTransactionById(template.getSampleTransactionId());
		    byte[] bytes = PromotionsService.getPromotionBytes(promotion, null, null);
		    super.print(bytes);
		} else if (template.getTransactionClass().intValue() == TransactionClassesEnum.MISSIONS.getCode()) {
		    MissionData mission = MissionsService.getMissionsById(template.getSampleTransactionId());
		    if (template.getTransactionBusinessType() == TransactionTypesEnum.MISSION_DECISION.getCode()) {
			byte[] bytes = MissionsService.getMissionDecisionBytes(mission.getId(), mission.getCategoryMode());
			super.print(bytes);
		    } else if (template.getTransactionBusinessType() == TransactionTypesEnum.MISSION_PAYMENT.getCode()) {
			byte[] bytes = MissionsService.getMissionPaymentDecisionBytes(mission);
			super.printRTF(bytes);
		    } else if (template.getTransactionBusinessType() == TransactionTypesEnum.MISSION_FINANCIAL_LINK.getCode()) {
			byte[] bytes = MissionsService.getMissionFinancialLinkBytes(mission);
			super.printRTF(bytes);
		    }
		} else if (template.getTransactionClass().intValue() == TransactionClassesEnum.VACATIONS.getCode()) {
		    Vacation vaction = VacationsService.getVacationById(template.getSampleTransactionId());
		    byte[] bytes = VacationsService.getVacationDecisionBytes(vaction.getVacationId(), vaction.getVacationTypeId(), vaction.getTransactionEmpCategoryId());
		    super.print(bytes);
		} else if (template.getTransactionClass().intValue() == TransactionClassesEnum.TERMINATIONS.getCode()) {
		    TerminationTransactionData terminationTransactionData = TerminationsService.getTerminationTransactionById(template.getSampleTransactionId());
		    byte[] bytes = TerminationsService.getTerminationDecisionBytes(terminationTransactionData.getId(), terminationTransactionData.getCategoryId(), terminationTransactionData.getTransactionTypeCode());
		    super.print(bytes);
		}
	    } catch (BusinessException e) {
		setServerSideErrorMessages(getMessage(e.getMessage()));
	    }
	}
    }

    public String getDecisionDesc() {
	return decisionDesc;
    }

    public void setDecisionDesc(String decisionDesc) {
	this.decisionDesc = decisionDesc;
    }

    public Integer getCategoryId() {
	return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
	this.categoryId = categoryId;
    }

    public Integer getReportClass() {
	return reportClass;
    }

    public void setReportClass(Integer reportClass) {
	this.reportClass = reportClass;
    }

    public Integer getTransactionClass() {
	return transactionClass;
    }

    public void setTransactionClass(Integer transactionClass) {
	this.transactionClass = transactionClass;
    }

    public Integer getTransactionBussinessType() {
	return transactionBussinessType;
    }

    public void setTransactionBussinessType(Integer transactionBussinessType) {
	this.transactionBussinessType = transactionBussinessType;
    }

    public Date getValidFromDate() {
	return validFromDate;
    }

    public void setValidFromDate(Date validFromDate) {
	this.validFromDate = validFromDate;
    }

    public Date getValidToDate() {
	return validToDate;
    }

    public void setValidToDate(Date validToDate) {
	this.validToDate = validToDate;
    }

    public List<Category> getCategories() {
	return categories;
    }

    public void setCategories(List<Category> categories) {
	this.categories = categories;
    }

    public List<DecisionTemplate> getTemplates() {
	return templates;
    }

    public void setTemplates(List<DecisionTemplate> templates) {
	this.templates = templates;
    }

    public DecisionTemplate getSelectedTemplate() {
	return selectedTemplate;
    }

    public void selectTemplate(DecisionTemplate template) {
	this.selectedTemplate = template;
    }

    public boolean isAdmin() {
	return isAdmin;
    }

    public int getPageSize() {
	return pageSize;
    }

}
