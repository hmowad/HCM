package com.code.ui.backings.hcm.units;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.UnitsService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "fieldUnitsManagement")
@ViewScoped
public class FieldUnitsManagement extends BaseBacking {

    private List<UnitData> fieldUnitsList;

    private Long fieldUnitId;
    private String fieldUnitHKey;

    private final int pageSize = 10;

    public FieldUnitsManagement() {
	try {
	    setScreenTitle(getMessage("title_fieldUnits"));
	    fieldUnitsList = UnitsService.getUnitsByIdsString(ETRConfigurationService.getFieldUnitsIds());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void addFieldUnit() {
	try {
	    UnitsService.addFieldUnit(fieldUnitId, fieldUnitHKey, this.loginEmpData.getEmpId());
	    fieldUnitsList.add(0, UnitsService.getUnitById(fieldUnitId));
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void deleteFieldUnit(UnitData unit) throws BusinessException {
	try {
	    UnitsService.deleteFieldUnit(unit.getId(), this.loginEmpData.getEmpId());
	    fieldUnitsList.remove(unit);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(this.getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    /**************************************************************************/

    public int getPageSize() {
	return pageSize;
    }

    public Long getFieldUnitId() {
	return fieldUnitId;
    }

    public void setFieldUnitId(Long fieldUnitId) {
	this.fieldUnitId = fieldUnitId;
    }

    public List<UnitData> getFieldUnitsList() {
	return fieldUnitsList;
    }

    public void setFieldUnitsList(List<UnitData> fieldUnitsList) {
	this.fieldUnitsList = fieldUnitsList;
    }

    public String getFieldUnitHKey() {
	return fieldUnitHKey;
    }

    public void setFieldUnitHKey(String fieldUnitHKey) {
	this.fieldUnitHKey = fieldUnitHKey;
    }

}
