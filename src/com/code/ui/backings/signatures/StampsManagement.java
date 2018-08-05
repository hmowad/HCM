package com.code.ui.backings.signatures;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.signatures.Signature;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.SignaturesTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.security.SecurityService;
import com.code.services.signatures.SignaturesService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "stampsManagement")
@ViewScoped
public class StampsManagement extends BaseBacking {

    private String signatureDescription;
    private Integer signatureStatus;

    private Signature selectedSignature;
    private List<Signature> signaturesList;
    private List<String> signatureDescriptionsList;

    private boolean newRecordFlag;
    private Integer newSignatureFlag;
    private String newSignatureDescription;
    private Date newValidFromDate;

    private Signature newSignature;

    private boolean adminFlag = false;
    private final int pageSize = 10;

    public StampsManagement() {
	try {
	    resetForm();
	    adminFlag = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.ETR_STAMPS_MANAGEMENT.getCode(), MenuActionsEnum.ETR_SIGNATURES_MANAGEMENT_ADMIN.getCode());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetForm() {
	if (isSignaturePhotoRequired()) {
	    setServerSideErrorMessages(getMessage("error_unfinishedNewStamp"));
	    return;
	}

	signatureDescription = null;
	signatureStatus = FlagsEnum.ALL.getCode();

	selectedSignature = null;
	signaturesList = new ArrayList<Signature>();

	newSignature = null;
	newRecordFlag = false;
	newSignatureFlag = FlagsEnum.OFF.getCode();
	newSignatureDescription = null;
	newValidFromDate = null;
	
	loadSignatureDescriptions();
    }

    private void loadSignatureDescriptions() {
	try {
	    signatureDescriptionsList = SignaturesService.searchSignaturesDescriptions(SignaturesTypesEnum.STAMP.getCode());
	} catch (BusinessException e) {
	    signatureDescriptionsList = new ArrayList<String>();
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void search() {
	if (isSignaturePhotoRequired()) {
	    setServerSideErrorMessages(getMessage("error_unfinishedNewStamp"));
	    return;
	}

	try {
	    signaturesList = SignaturesService.getSignatures(SignaturesTypesEnum.STAMP.getCode(), FlagsEnum.ALL.getCode(), signatureDescription, signatureStatus, null, null, FlagsEnum.ALL.getCode());
	    selectedSignature = null;
	    newSignature = null;
	    newRecordFlag = false;
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void selectSignature(Signature signature) {
	if (isSignaturePhotoRequired()) {
	    setServerSideErrorMessages(getMessage("error_unfinishedNewSignature"));
	    return;
	}

	selectedSignature = signature;
	newSignature = null;
	newRecordFlag = false;
    }

    public void addNewSignature() throws BusinessException {
	if (isSignaturePhotoRequired()) {
	    setServerSideErrorMessages(getMessage("error_unfinishedNewStamp"));
	    return;
	}

	newRecordFlag = true;
	selectedSignature = null;
	newSignature = null;
	newSignatureDescription = null;
	newValidFromDate = null;
    }

    public void createNewSignature() {
	newSignature = new Signature();
    }

    public void cloneSignature() {
	try {
	    newSignature = SignaturesService.cloneSignature(SignaturesTypesEnum.STAMP.getCode(), newSignatureDescription, newValidFromDate, this.loginEmpData.getEmpId());

	    // Warning with old dates
	    if (newValidFromDate.before(HijriDateService.getHijriSysDate()))
		super.setServerSideSuccessMessages(getMessage("notify_newSignatureStartDateIsOld"));

	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	    newSignature = null;
	}
    }

    public void saveSignature() {
	try {
	    SignaturesService.saveSignature(SignaturesTypesEnum.STAMP.getCode(), newSignature, newSignatureFlag == FlagsEnum.ON.getCode(), this.loginEmpData.getEmpId());
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void updatePhoto() {
	try {
	    newSignature.setSignaturePhoto(SignaturesService.getSignatureById(newSignature.getId()).getSignaturePhoto());
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private boolean isSignaturePhotoRequired() {
	if (newSignature != null && newSignature.getId() != null && newSignature.getSignaturePhoto() == null)
	    return true;
	return false;
    }

    public String getSignatureDescription() {
	return signatureDescription;
    }

    public void setSignatureDescription(String signatureDescription) {
	this.signatureDescription = signatureDescription;
    }

    public Integer getSignatureStatus() {
	return signatureStatus;
    }

    public void setSignatureStatus(Integer signatureStatus) {
	this.signatureStatus = signatureStatus;
    }

    public Signature getSelectedSignature() {
	return selectedSignature;
    }

    public void setSelectedSignature(Signature selectedSignature) {
	this.selectedSignature = selectedSignature;
    }

    public List<Signature> getSignaturesList() {
	return signaturesList;
    }

    public void setSignaturesList(List<Signature> signaturesList) {
	this.signaturesList = signaturesList;
    }

    public List<String> getSignatureDescriptionsList() {
	return signatureDescriptionsList;
    }

    public void setSignatureDescriptionsList(List<String> signatureDescriptionsList) {
	this.signatureDescriptionsList = signatureDescriptionsList;
    }

    public boolean isNewRecordFlag() {
	return newRecordFlag;
    }

    public void setNewRecordFlag(boolean newRecordFlag) {
	this.newRecordFlag = newRecordFlag;
    }

    public Integer getNewSignatureFlag() {
	return newSignatureFlag;
    }

    public void setNewSignatureFlag(Integer newSignatureFlag) {
	this.newSignatureFlag = newSignatureFlag;
    }

    public String getNewSignatureDescription() {
	return newSignatureDescription;
    }

    public void setNewSignatureDescription(String newSignatureDescription) {
	this.newSignatureDescription = newSignatureDescription;
    }

    public Date getNewValidFromDate() {
	return newValidFromDate;
    }

    public void setNewValidFromDate(Date newValidFromDate) {
	this.newValidFromDate = newValidFromDate;
    }

    public Signature getNewSignature() {
	return newSignature;
    }

    public void setNewSignature(Signature newSignature) {
	this.newSignature = newSignature;
    }

    public boolean hasPhoto(Signature signature) {
	return signature == null || signature.getId() == null || signature.getSignaturePhoto() == null ? false : true;
    }

    public boolean getAdminFlag() {
	return adminFlag;
    }

    public int getPageSize() {
	return pageSize;
    }
}
