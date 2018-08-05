package com.code.ui.backings.hcm.units;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.units.OrganizationTableDetailData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.CategoriesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.OrganizationTablesService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

@ManagedBean(name = "organizationTableConstruction")
@SessionScoped
public class OrganizationTableConstruction extends OrganizationTableBase {

    private boolean completionFlag;

    @Override
    public void reset() {
	super.reset();
	completionFlag = false;
    }

    protected void addFirstUnitToSelectedUnits() {
	try {
	    organizationTableDetails = OrganizationTablesService.getActiveOrganizationTableDetails(new Long[] { selectedUnitData.getId() }, selectedCategoryClass);
	    int selectedUnitOrganizationTableDetailsCount = organizationTableDetails.size();

	    // set the transient category of the organization table details.
	    for (OrganizationTableDetailData detail : organizationTableDetails) {
		if (CategoriesEnum.SOLDIERS.getCode() == CommonService.getRankById(detail.getRankId()).getCategoryId().longValue())
		    detail.setCategoryId(CategoriesEnum.SOLDIERS.getCode());
		else
		    detail.setCategoryId((long) selectedCategoryClass);
	    }
	    // Construction Mode.
	    if (selectedUnitOrganizationTableDetailsCount == 0) {
		selectedUnits.add(selectedUnitData);
	    } else { // Completion Mode.
		completionFlag = true;

		// Get all the organization table details for all the units have the same organization tables details information.
		List<OrganizationTableDetailData> selectedUnitsOriginalOrganizationTableDetails = OrganizationTablesService.getIdenticalOrganizationTableDetails
			(organizationTableDetails.get(0).getOrganizationTableId(), selectedUnitOrganizationTableDetailsCount);

		// Set decision number, decision date
		decisionNumber = selectedUnitsOriginalOrganizationTableDetails.get(0).getDecisionNumber();
		decisionDate = selectedUnitsOriginalOrganizationTableDetails.get(0).getDecisionDate();

		// Set the selected units which have the identical organization tables details according to the first selected unit.
		for (int i = 0; i < selectedUnitsOriginalOrganizationTableDetails.size(); i += selectedUnitOrganizationTableDetailsCount) {
		    UnitData unitData = new UnitData();
		    unitData.setId(selectedUnitsOriginalOrganizationTableDetails.get(i).getUnitId());
		    unitData.setFullName(selectedUnitsOriginalOrganizationTableDetails.get(i).getUnitFullName());
		    unitData.setUnitTypeId(selectedUnitsOriginalOrganizationTableDetails.get(i).getUnitTypeId());
		    selectedUnits.add(unitData);
		}
		selectOrganizationTableDetail(organizationTableDetails.get(0));
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    protected void addUnitToSelectedUnits() {
	try {
	    // validate that the selected unit has not been added before.
	    if (checkSelectedUnitWithinUnits(selectedUnits, selectedUnitData.getId())) {
		this.setServerSideErrorMessages(getMessage("error_repeatedUnit"));
		return;
	    }

	    if (completionFlag) {
		if (checkSelectedUnitWithinUnits(removedUnits, selectedUnitData.getId())) {
		    selectedUnits.add(selectedUnitData);
		    removedUnits.remove(selectedUnitData);
		} else {
		    this.setServerSideErrorMessages(getMessage("error_orgTableWithDifferentDetails"));
		    return;
		}
	    } else {
		// validate that the selected unit has the same type.
		if (!selectedUnitData.getUnitTypeId().equals(selectedUnits.get(0).getUnitTypeId())) {
		    this.setServerSideErrorMessages(getMessage("error_allUnitsShouldHaveSameType"));
		    return;
		}
		// validate that the selected unit doesn't have active organization table.
		if (OrganizationTablesService.isUnitHasActiveOrganizationTableForCategoryClass(selectedUnitData.getId(), selectedCategoryClass)) {
		    this.setServerSideErrorMessages(getMessage("error_unitHasOrganizationTable"));
		    return;
		}
		selectedUnits.add(selectedUnitData);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    @Override
    public void deleteUnit(UnitData detail) {
	selectedUnits.remove(detail);
	if (completionFlag)
	    removedUnits.add(detail);

	if (selectedUnits.isEmpty())
	    reset();
    }

    public void saveDetail() {
	if (selectedOrganizationTableDetail != null)
	    selectedOrganizationTableDetail.setRankDescription(CommonService.getRankById(selectedOrganizationTableDetail.getRankId()).getDescription());
    }

    @Override
    public void save() {
	try {
	    OrganizationTablesService.constructOrganizationTable(completionFlag, decisionNumber, decisionDate, selectedCategoryClass, selectedUnits,
		    organizationTableDetails, this.loginEmpData.getEmpId());
	    super.save();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public boolean getCompletionFlag() {
	return completionFlag;
    }

    public String getDecisionDateString() {
	return HijriDateService.getHijriDateString(decisionDate);
    }
}
