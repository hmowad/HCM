package com.code.ui.backings.hcm.units;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.units.OrganizationTableDetailData;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.CategoriesEnum;
import com.code.enums.OperationsTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.OrganizationTablesService;
import com.code.services.util.CommonService;

@ManagedBean(name = "organizationTableModification")
@SessionScoped
public class OrganizationTableModification extends OrganizationTableBase {

    private List<OrganizationTableDetailData> selectedUnitsOriginalOrganizationTableDetails;
    private int selectedUnitOrganizationTableDetailsCount;

    protected void addFirstUnitToSelectedUnits() {
	try {
	    organizationTableDetails = OrganizationTablesService.getActiveOrganizationTableDetails(new Long[] { selectedUnitData.getId() }, selectedCategoryClass);
	    selectedUnitOrganizationTableDetailsCount = organizationTableDetails.size();

	    if (selectedUnitOrganizationTableDetailsCount == 0) {
		this.setServerSideErrorMessages(getMessage("error_unitHasNoOrganizationTable"));
		return;
	    }

	    // set the transient category of the organization table details.
	    for (int i = 0; i < organizationTableDetails.size(); ++i) {
		organizationTableDetails.get(i).setIndex(i);
		if (CategoriesEnum.SOLDIERS.getCode() == CommonService.getRankById(organizationTableDetails.get(i).getRankId()).getCategoryId().longValue())
		    organizationTableDetails.get(i).setCategoryId(CategoriesEnum.SOLDIERS.getCode());
		else
		    organizationTableDetails.get(i).setCategoryId((long) selectedCategoryClass);
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
	    selectOrganizationTableDetail(organizationTableDetails.get(0));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    protected void addUnitToSelectedUnits() {
	if (checkSelectedUnitWithinUnits(selectedUnits, selectedUnitData.getId())) {
	    this.setServerSideErrorMessages(getMessage("error_repeatedUnit"));
	}
	else if (checkSelectedUnitWithinUnits(removedUnits, selectedUnitData.getId())) {
	    selectedUnits.add(selectedUnitData);
	    removedUnits.remove(selectedUnitData);
	} else {
	    this.setServerSideErrorMessages(getMessage("error_orgTableWithDifferentDetails"));
	}
    }

    @Override
    public void deleteOrganizationTableDetail(OrganizationTableDetailData organizationTableDetailData) {
	if (organizationTableDetailData.getId() != null)
	    markOrganizationTableDetailsAsDeleted(organizationTableDetailData);

	super.deleteOrganizationTableDetail(organizationTableDetailData);
    }

    // Mark organization table detail and all the corresponding details at all original units as deleted.
    private void markOrganizationTableDetailsAsDeleted(OrganizationTableDetailData organizationTableDetail) {
	for (int i = organizationTableDetail.getIndex(); i < selectedUnitsOriginalOrganizationTableDetails.size(); i += selectedUnitOrganizationTableDetailsCount) {
	    selectedUnitsOriginalOrganizationTableDetails.get(i).setOperationType(OperationsTypesEnum.DELETE.getCode());
	}
    }

    public void saveDetail() {
	if (selectedOrganizationTableDetail != null) {
	    if (selectedOrganizationTableDetail.getId() != null)
		markOrganizationTableDetailsAsModified(selectedOrganizationTableDetail);

	    selectedOrganizationTableDetail.setRankDescription(CommonService.getRankById(selectedOrganizationTableDetail.getRankId()).getDescription());
	}
    }

    // Mark organization table detail and all the corresponding details at all original units as modified.
    private void markOrganizationTableDetailsAsModified(OrganizationTableDetailData organizationTableDetail) {
	OrganizationTableDetailData oldOrganizationTableDetail = selectedUnitsOriginalOrganizationTableDetails.get(organizationTableDetail.getIndex());

	if (organizationTableDetail.equals(oldOrganizationTableDetail))
	    return;

	for (int i = organizationTableDetail.getIndex(); i < selectedUnitsOriginalOrganizationTableDetails.size(); i += selectedUnitOrganizationTableDetailsCount) {
	    selectedUnitsOriginalOrganizationTableDetails.get(i).setOperationType(OperationsTypesEnum.UPDATE.getCode());
	}
    }

    @Override
    public void save() {
	try {
	    OrganizationTablesService.modifyOrganizationTable(decisionNumber, decisionDate, selectedCategoryClass, selectedUnits,
		    organizationTableDetails, selectedUnitsOriginalOrganizationTableDetails, selectedUnitOrganizationTableDetailsCount, this.loginEmpData.getEmpId());
	    super.save();
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}

    }
}
