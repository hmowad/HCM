package com.code.ui.backings.hcm.units;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.units.OrganizationTableDetailData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.OrganizationTablesService;

@ManagedBean(name = "organizationTableCancellation")
@SessionScoped
public class OrganizationTableCancellation extends OrganizationTableBase {

    private List<OrganizationTableDetailData> selectedUnitsOriginalOrganizationTableDetails;

    protected void addFirstUnitToSelectedUnits() {
	try {
	    organizationTableDetails = OrganizationTablesService.getActiveOrganizationTableDetails(new Long[] { selectedUnitData.getId() }, selectedCategoryClass);
	    int selectedUnitOrganizationTableDetailsCount = organizationTableDetails.size();

	    if (selectedUnitOrganizationTableDetailsCount == 0) {
		this.setServerSideErrorMessages(getMessage("error_unitHasNoOrganizationTable"));
		return;
	    }

	    // Get all the organization table details for all the units have the same organization tables details information.
	    selectedUnitsOriginalOrganizationTableDetails = OrganizationTablesService.getIdenticalOrganizationTableDetails
		    (organizationTableDetails.get(0).getOrganizationTableId(), selectedUnitOrganizationTableDetailsCount);

	    // Set the selected units which have the identical organization tables details according to the first selected unit.
	    for (int i = 0; i < selectedUnitsOriginalOrganizationTableDetails.size(); i += selectedUnitOrganizationTableDetailsCount) {
		UnitData unitData = new UnitData();
		unitData.setId(selectedUnitsOriginalOrganizationTableDetails.get(i).getUnitId());
		unitData.setFullName(selectedUnitsOriginalOrganizationTableDetails.get(i).getUnitFullName());
		unitData.setUnitTypeId(selectedUnitsOriginalOrganizationTableDetails.get(i).getUnitTypeId());
		selectedUnits.add(unitData);
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    protected void addUnitToSelectedUnits() {
	if (checkSelectedUnitWithinUnits(selectedUnits, selectedUnitData.getId()))
	    this.setServerSideErrorMessages(getMessage("error_repeatedUnit"));
	else if (checkSelectedUnitWithinUnits(removedUnits, selectedUnitData.getId())) {
	    selectedUnits.add(selectedUnitData);
	    removedUnits.remove(selectedUnitData);
	} else
	    this.setServerSideErrorMessages(getMessage("error_orgTableWithDifferentDetails"));
    }

    @Override
    public void save() {
	try {
	    OrganizationTablesService.cancelOrganizationTable(decisionNumber, decisionDate, selectedCategoryClass, selectedUnits,
		    selectedUnitsOriginalOrganizationTableDetails, loginEmpData.getEmpId());
	    super.save();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }
}
