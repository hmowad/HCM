package com.code.ui.backings.hcm.units;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.code.dal.orm.hcm.Category;
import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.organization.units.OrganizationTableDetailData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.GeneralSpecializationsEnum;
import com.code.enums.JobTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.util.UnitTreeNode;

public abstract class OrganizationTableBase extends UnitTreeBase {

    private List<Category> categoryClasses;
    private List<Rank> ranks; // not used in Cancellation

    protected Integer selectedCategoryClass;
    protected String decisionNumber;
    protected Date decisionDate;

    protected List<UnitData> selectedUnits;
    protected List<UnitData> removedUnits; // removed units from originally selected units so the user can retrieve from these units only.

    // not used in cancellation
    private String selectedBasicJobsNamesIds;
    private String selectedBasicJobsNamesNames;
    private String selectedBasicJobNamesCategoriesIds;

    protected List<OrganizationTableDetailData> organizationTableDetails;
    protected OrganizationTableDetailData selectedOrganizationTableDetail; // not used in Cancellation

    private boolean done;
    private final int pageSize = 10;

    public final void initialize() {
	super.init();
	categoryClasses = CommonService.getCategoryClasses();
	selectedCategoryClass = (int) CategoriesEnum.OFFICERS.getCode();

	reset();
    }

    public void reset() {
	try {
	    decisionNumber = null;
	    decisionDate = HijriDateService.getHijriSysDate();
	    ranks = CommonService.getRanks(null, new Long[] { (long) selectedCategoryClass });

	    if (CategoriesEnum.OFFICERS.getCode() == selectedCategoryClass.longValue()
		    || CategoriesEnum.SOLDIERS.getCode() == selectedCategoryClass.longValue())
		ranks.remove(ranks.size() - 1);

	    selectedUnits = new ArrayList<UnitData>();
	    removedUnits = new ArrayList<UnitData>();

	    organizationTableDetails = new ArrayList<OrganizationTableDetailData>();

	    selectedOrganizationTableDetail = new OrganizationTableDetailData();
	    selectedOrganizationTableDetail.setApprovedFlag(FlagsEnum.OFF.getCode());

	    done = false;
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public final void selectUnit(UnitTreeNode unitItem) {
	super.click(unitItem);

	if (selectedUnits.isEmpty())
	    addFirstUnitToSelectedUnits();
	else
	    addUnitToSelectedUnits();
    }

    protected abstract void addFirstUnitToSelectedUnits();

    protected abstract void addUnitToSelectedUnits();

    protected final boolean checkSelectedUnitWithinUnits(List<UnitData> units, Long unitId) {
	for (UnitData unitData : units) {
	    if (unitData.getId().equals(unitId))
		return true;
	}
	return false;
    }

    public void deleteUnit(UnitData detail) {
	selectedUnits.remove(detail);
	removedUnits.add(detail);

	if (selectedUnits.isEmpty())
	    reset();
    }

    public final void addOrganizationTableDetail() {
	String[] basicJobsNamesIds = selectedBasicJobsNamesIds.split(",");
	String[] basicJobsNamesNames = selectedBasicJobsNamesNames.split(",");
	String[] basicJobNamesCategoriesIds = selectedBasicJobNamesCategoriesIds.split(",");

	for (int i = 0; i < basicJobsNamesIds.length; i++) {
	    // initialize data
	    OrganizationTableDetailData organizationTableDetailData = new OrganizationTableDetailData();
	    organizationTableDetailData.setBasicJobName(basicJobsNamesNames[i]);

	    organizationTableDetailData.setCategoryId(Long.parseLong(basicJobNamesCategoriesIds[i]));

	    organizationTableDetailData.setGeneralSpecialization(GeneralSpecializationsEnum.OVERLAND.getCode());
	    organizationTableDetailData.setGeneralType(JobTypesEnum.NORMAL.getCode());

	    if (CategoriesEnum.SOLDIERS.getCode() == organizationTableDetailData.getCategoryId().longValue())
		organizationTableDetailData.setApprovedFlag(FlagsEnum.OFF.getCode());
	    else
		organizationTableDetailData.setApprovedFlag(FlagsEnum.ON.getCode());

	    // Add to detail list
	    organizationTableDetails.add(organizationTableDetailData);
	}

	if (basicJobsNamesIds.length > 0) {
	    selectOrganizationTableDetail(organizationTableDetails.get(0));
	}
    }

    public final void selectOrganizationTableDetail(OrganizationTableDetailData organizationTableDetailData) {
	try {
	    if (CategoriesEnum.OFFICERS.getCode() == selectedCategoryClass && organizationTableDetailData.getBasicJobName() != null) {

		ranks = CommonService.getRanks(null, new Long[] { organizationTableDetailData.getCategoryId().longValue() });
		if (CategoriesEnum.OFFICERS.getCode() == organizationTableDetailData.getCategoryId().longValue()
			|| CategoriesEnum.SOLDIERS.getCode() == organizationTableDetailData.getCategoryId().longValue())
		    ranks.remove(ranks.size() - 1);
	    }

	    selectedOrganizationTableDetail = organizationTableDetailData;

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    // not used in cancellation.
    public void deleteOrganizationTableDetail(OrganizationTableDetailData organizationTableDetailData) {
	organizationTableDetails.remove(organizationTableDetailData);

	if (organizationTableDetails.isEmpty())
	    selectOrganizationTableDetail(new OrganizationTableDetailData());
	else if (selectedOrganizationTableDetail.equals(organizationTableDetailData))
	    selectOrganizationTableDetail(organizationTableDetails.get(0));
    }

    public void save() {
	done = true;
	this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
    }

    // Setters and Getters

    public List<Category> getCategoryClasses() {
	return categoryClasses;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public void setRanks(List<Rank> ranks) {
	this.ranks = ranks;
    }

    public Integer getSelectedCategoryClass() {
	return selectedCategoryClass;
    }

    public void setSelectedCategoryClass(Integer selectedCategoryClass) {
	this.selectedCategoryClass = selectedCategoryClass;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    public List<UnitData> getSelectedUnits() {
	return selectedUnits;
    }

    public void setSelectedUnits(List<UnitData> selectedUnits) {
	this.selectedUnits = selectedUnits;
    }

    public String getSelectedBasicJobsNamesIds() {
	return selectedBasicJobsNamesIds;
    }

    public void setSelectedBasicJobsNamesIds(String selectedBasicJobsNamesIds) {
	this.selectedBasicJobsNamesIds = selectedBasicJobsNamesIds;
    }

    public String getSelectedBasicJobsNamesNames() {
	return selectedBasicJobsNamesNames;
    }

    public void setSelectedBasicJobsNamesNames(String selectedBasicJobsNamesNames) {
	this.selectedBasicJobsNamesNames = selectedBasicJobsNamesNames;
    }

    public String getSelectedBasicJobNamesCategoriesIds() {
	return selectedBasicJobNamesCategoriesIds;
    }

    public void setSelectedBasicJobNamesCategoriesIds(String selectedBasicJobNamesCategoriesIds) {
	this.selectedBasicJobNamesCategoriesIds = selectedBasicJobNamesCategoriesIds;
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

    public boolean isDone() {
	return done;
    }

    public int getPageSize() {
	return pageSize;
    }
}
