package com.code.ui.backings.hcm.jobs;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.specializations.MinorSpecializationDescriptionData;
import com.code.dal.orm.hcm.specializations.MinorSpecializationDescriptionDetailData;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.SpecializationsService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "minorSpecializationsDescriptions")
@ViewScoped
public class MinorSpecializationsDescriptions extends BaseBacking {

    private Long selectedMinorSpecializationId;

    private MinorSpecializationDescriptionData minorSpecializationDescription;
    private List<MinorSpecializationDescriptionDetailData> minorSpecializationDescriptionDetails;

    private final int pageSize = 10;

    public MinorSpecializationsDescriptions() {
	selectedMinorSpecializationId = null;

	minorSpecializationDescription = new MinorSpecializationDescriptionData();
	minorSpecializationDescriptionDetails = new ArrayList<MinorSpecializationDescriptionDetailData>();
    }

    // Load minor specialization description if exists.
    public void loadMinorSpecializationDescription() {
	try {
	    minorSpecializationDescription = SpecializationsService.getMinorSpecializationDescription(selectedMinorSpecializationId);
	    if (minorSpecializationDescription != null) {
		minorSpecializationDescriptionDetails = SpecializationsService.getMinorSpecializationDescriptionDetails(minorSpecializationDescription.getId());
	    } else {
		minorSpecializationDescription = new MinorSpecializationDescriptionData();
		minorSpecializationDescription.setMinorSpecializationId(selectedMinorSpecializationId);
		minorSpecializationDescriptionDetails = new ArrayList<MinorSpecializationDescriptionDetailData>();
		this.setServerSideErrorMessages(getMessage("error_missingMinorSpecializationDescription"));
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // Add new duty to the minor specialization description details list.
    public void addMinorSpecializationDescriptionDetail() {
	MinorSpecializationDescriptionDetailData minorSpecializationDescriptionDetail = new MinorSpecializationDescriptionDetailData();
	minorSpecializationDescriptionDetails.add(minorSpecializationDescriptionDetail);
    }

    // Delete duty from the minor specialization description details list.
    public void deleteMinorSpecializationDescriptionDetail(MinorSpecializationDescriptionDetailData minorSpecializationDescriptionDetail) {
	try {
	    if (minorSpecializationDescriptionDetail.getId() != null)
		SpecializationsService.deleteMinorSpecializationDescriptionDetail(minorSpecializationDescriptionDetails, minorSpecializationDescriptionDetail, this.loginEmpData.getEmpId());

	    minorSpecializationDescriptionDetails.remove(minorSpecializationDescriptionDetail);
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    if (minorSpecializationDescription.getId() == null)
		SpecializationsService.addMinorSpecializationDescription(minorSpecializationDescription, minorSpecializationDescriptionDetails, this.loginEmpData.getEmpId());
	    else
		SpecializationsService.modifyMinorSpecializationDescription(minorSpecializationDescription, minorSpecializationDescriptionDetails, this.loginEmpData.getEmpId());

	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void print() {
	try {
	    if (selectedMinorSpecializationId != null) {
		if (minorSpecializationDescription.getId() != null) {
		    byte[] bytes = SpecializationsService.getMinorSpecializationDescriptionReportBytes(selectedMinorSpecializationId);
		    super.print(bytes);
		} else {
		    super.setServerSideErrorMessages(getMessage("error_missingMinorSpecializationDescription"));
		}
	    } else {
		super.setServerSideErrorMessages(getMessage("error_minorSpecializationIsMandatory"));
	    }
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public Long getSelectedMinorSpecializationId() {
	return selectedMinorSpecializationId;
    }

    public void setSelectedMinorSpecializationId(Long selectedMinorSpecializationId) {
	this.selectedMinorSpecializationId = selectedMinorSpecializationId;
    }

    public MinorSpecializationDescriptionData getMinorSpecializationDescription() {
	return minorSpecializationDescription;
    }

    public void setMinorSpecializationDescription(MinorSpecializationDescriptionData minorSpecializationDescription) {
	this.minorSpecializationDescription = minorSpecializationDescription;
    }

    public List<MinorSpecializationDescriptionDetailData> getMinorSpecializationDescriptionDetails() {
	return minorSpecializationDescriptionDetails;
    }

    public void setMinorSpecializationDescriptionDetails(List<MinorSpecializationDescriptionDetailData> minorSpecializationDescriptionDetails) {
	this.minorSpecializationDescriptionDetails = minorSpecializationDescriptionDetails;
    }

    public int getPageSize() {
	return pageSize;
    }

}
