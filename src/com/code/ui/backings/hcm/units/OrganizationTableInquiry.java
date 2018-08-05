package com.code.ui.backings.hcm.units;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.organization.units.OrganizationTable;
import com.code.dal.orm.hcm.organization.units.OrganizationTableDetailData;
import com.code.dal.orm.hcm.organization.units.OrganizationTableTransactionData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.RegionsEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.OrganizationTablesService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.CommonService;
import com.code.ui.util.UnitTreeNode;

@ManagedBean(name = "organizationTableInquiry")
@SessionScoped
public class OrganizationTableInquiry extends UnitTreeBase {

    private List<Category> categoryClasses;
    private Integer selectedCategoryClass;

    private boolean viewAllLevelsJobsFlag;
    private int reportType;
    private int approvedFlag;

    private List<OrganizationTableDetailData> organizationTableDetails;
    private OrganizationTableDetailData selectedOrganizationTableDetail;

    private List<OrganizationTableTransactionData> organizationTableTransactions;

    private final int pageSize = 10;

    public void init() {
	super.init();

	categoryClasses = CommonService.getCategoryClasses();
	selectedCategoryClass = (int) CategoriesEnum.OFFICERS.getCode();

	viewAllLevelsJobsFlag = false;
	reportType = 10;
	approvedFlag = FlagsEnum.ALL.getCode();

	reset();

	searchOrganizationTables(false);
    }

    public void reset() {
	organizationTableDetails = new ArrayList<OrganizationTableDetailData>();
	selectedOrganizationTableDetail = new OrganizationTableDetailData();
	organizationTableTransactions = new ArrayList<OrganizationTableTransactionData>();
    }

    public void search() {
	super.search();

	searchOrganizationTables(false);
    }

    public void selectUnit(UnitTreeNode unitItem) {
	super.click(unitItem);

	searchOrganizationTables(true);
    }

    public void searchOrganizationTables(boolean showErrorMessage) {
	try {
	    if (isAuthorized()) {
		organizationTableDetails = OrganizationTablesService.getActiveOrganizationTableDetails(new Long[] { selectedUnitData.getId() }, selectedCategoryClass);
		organizationTableTransactions = OrganizationTablesService.getOrganizationTableTransactions(selectedUnitData.getId(), selectedCategoryClass);

		if (organizationTableDetails.size() == 0)
		    selectedOrganizationTableDetail = new OrganizationTableDetailData();
		else
		    selectedOrganizationTableDetail = organizationTableDetails.get(0);
	    } else {
		reset();
		if (showErrorMessage)
		    this.setServerSideErrorMessages(getMessage("error_selectedUnitIsNotInTheSameRegion"));
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public boolean isAuthorized() {
	return this.loginEmpData.getPhysicalRegionId().equals(selectedUnitData.getRegionId())
		|| this.loginEmpData.getPhysicalRegionId().equals(RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
    }

    public void selectOrganizationTableDetail(OrganizationTableDetailData organizationTableDetailData) {
	selectedOrganizationTableDetail = organizationTableDetailData;
    }

    public void print() {
	try {
	    if (!isAuthorized()) {
		this.setServerSideErrorMessages(getMessage("error_selectedUnitIsNotInTheSameRegion"));
		return;
	    }

	    if (reportType == 10 && organizationTableDetails.size() == 0) {
		super.setServerSideErrorMessages(getMessage("error_unitHasNoOrganizationTable"));
		return;
	    }

	    String reportTitle = "";
	    String decisionNumber = "";
	    String decisionDateString = "";

	    if (reportType == 10) {
		reportTitle = getMessage("label_organizationTable") + " - ";

		OrganizationTable organizationTable = OrganizationTablesService.getActiveOrganizationTables(new Long[] { selectedUnitData.getId() }, selectedCategoryClass).get(0);

		decisionNumber = organizationTable.getDecisionNumber();
		decisionDateString = organizationTable.getDecisionDateString();
	    } else if (reportType == 20)
		reportTitle = getMessage("label_organizationTableCurrentOfficialSituation") + " - ";

	    if (selectedCategoryClass.longValue() == CategoriesEnum.OFFICERS.getCode())
		reportTitle += getMessage("label_militaryType");
	    else
		reportTitle += CommonService.getCategoryById(selectedCategoryClass.longValue()).getDescription();

	    String selectedUnitHkeyPrefix;
	    if (viewAllLevelsJobsFlag)
		selectedUnitHkeyPrefix = UnitsService.getHKeyPrefix(selectedUnitData.gethKey());
	    else
		selectedUnitHkeyPrefix = selectedUnitData.gethKey();

	    byte[] bytes = OrganizationTablesService.getOrganizationTableReportsBytes(reportType, selectedCategoryClass, selectedUnitHkeyPrefix, approvedFlag,
		    reportTitle, decisionNumber, decisionDateString);
	    super.print(bytes);

	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<Category> getCategoryClasses() {
	return categoryClasses;
    }

    public Integer getSelectedCategoryClass() {
	return selectedCategoryClass;
    }

    public void setSelectedCategoryClass(Integer selectedCategoryClass) {
	this.selectedCategoryClass = selectedCategoryClass;
    }

    public boolean getViewAllLevelsJobsFlag() {
	return viewAllLevelsJobsFlag;
    }

    public void setViewAllLevelsJobsFlag(boolean viewAllLevelsJobsFlag) {
	this.viewAllLevelsJobsFlag = viewAllLevelsJobsFlag;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public int getApprovedFlag() {
	return approvedFlag;
    }

    public void setApprovedFlag(int approvedFlag) {
	this.approvedFlag = approvedFlag;
    }

    public List<OrganizationTableDetailData> getOrganizationTableDetails() {
	return organizationTableDetails;
    }

    public OrganizationTableDetailData getSelectedOrganizationTableDetail() {
	return selectedOrganizationTableDetail;
    }

    public void setSelectedOrganizationTableDetail(OrganizationTableDetailData selectedOrganizationTableDetail) {
	this.selectedOrganizationTableDetail = selectedOrganizationTableDetail;
    }

    public List<OrganizationTableTransactionData> getOrganizationTableTransactions() {
	return organizationTableTransactions;
    }

    public int getPageSize() {
	return pageSize;
    }

}
