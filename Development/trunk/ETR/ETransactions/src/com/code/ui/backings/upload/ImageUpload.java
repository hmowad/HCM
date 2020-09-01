package com.code.ui.backings.upload;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import com.code.dal.orm.hcm.employees.EmployeePhoto;
import com.code.dal.orm.signatures.Signature;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.signatures.SignaturesService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "imageUpload")
@ViewScoped
public class ImageUpload extends BaseBacking implements Serializable {
    private long objectId;
    private boolean isNew;
    private int mode;

    public ImageUpload() {
	super.init();
	objectId = Long.parseLong(getRequest().getParameter("objectId"));
	isNew = getRequest().getParameter("isNew").equals("1") ? true : false;
	mode = Integer.parseInt(getRequest().getParameter("mode")); // 1 employee photo, 2 signature photo
	setScreenTitle(getMessage("title_imageUpload"));
    }

    public void uploadListener(FileUploadEvent event) throws Exception {
	switch (mode) {
	case 1:
	    uploadEmployeePhoto(event);
	    break;
	case 2:
	    uploadSignaturePhoto(event);
	    break;
	}
    }

    private void uploadEmployeePhoto(FileUploadEvent event) throws IOException {
	UploadedFile file = event.getUploadedFile();
	byte[] content = file.getData();

	if (content == null || content.length == 0) // No File Upload.
	    return;

	try {
	    if (isNew) {
		EmployeePhoto photo = new EmployeePhoto();
		photo.setPhoto(content);
		photo.setId(objectId);
		photo.setCreatedDate(HijriDateService.getHijriSysDate());
		photo.setSystemUser(this.loginEmpData.getEmpId() + "");// For Auditing.
		EmployeesService.addEmployeePhoto(photo);
	    } else {
		EmployeePhoto photo = EmployeesService.getEmployeePhotoByEmpId(objectId);
		photo.setPhoto(content);
		photo.setUpdatedDate(HijriDateService.getHijriSysDate());
		photo.setSystemUser(this.loginEmpData.getEmpId() + "");// For Auditing.
		EmployeesService.updateEmployeePhoto(photo);
	    }
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    private void uploadSignaturePhoto(FileUploadEvent event) throws IOException {
	UploadedFile file = event.getUploadedFile();
	byte[] content = file.getData();

	if (content == null || content.length == 0) // No File Upload.
	    return;

	try {
	    Signature sign = SignaturesService.getSignatureById(objectId);
	    sign.setSignaturePhoto(content);
	    SignaturesService.modifySignaturePhoto(sign);

	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }
}