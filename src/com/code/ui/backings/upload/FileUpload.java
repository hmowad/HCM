package com.code.ui.backings.upload;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import com.code.services.BaseService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "fileUpload")
@SessionScoped
public class FileUpload extends BaseBacking {
    private String attachements;
    private String uploadContextPath;
    private String uploadPathKey;

    public void init() {
	super.init();
	attachements = null;

	HttpServletRequest req = getRequest();
	uploadContextPath = req.getContextPath();
	uploadPathKey = req.getParameter("uploadPathKey");
    }

    public void uploadListener(FileUploadEvent event) throws Exception {
	if (this.attachements == null || this.attachements.equals(""))
	    this.attachements = uploadFile(event);
	else
	    this.attachements += "," + uploadFile(event);
    }

    private String uploadFile(FileUploadEvent event) throws IOException {
	UploadedFile file = event.getUploadedFile();
	byte[] content = file.getData();
	if (content == null || content.length == 0) { // No File Upload.
	    return "";
	}

	String inputFilePath = file.getName();
	String inputFileName = inputFilePath.substring(inputFilePath.lastIndexOf('\\') + 1, inputFilePath.length());

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	String dateFormatted = sdf.format(new Date());

	String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + "_" + dateFormatted + inputFileName.substring(inputFileName.lastIndexOf('.'), inputFileName.length());
	String fileURL = uploadContextPath + BaseService.getConfig(uploadPathKey) + outputFileName;
	fileURL = fileURL.replaceAll(" ", "%20");

	String uploadPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("") + BaseService.getConfig(uploadPathKey);
	outputFileName = uploadPath + outputFileName;
	BufferedOutputStream bufferedOutput = null;

	try {
	    bufferedOutput = new BufferedOutputStream(new FileOutputStream(outputFileName));
	    bufferedOutput.write(content);

	    return fileURL;
	} catch (Exception e) {
	    return "";
	} finally {
	    if (bufferedOutput != null) {
		bufferedOutput.flush();
		bufferedOutput.close();
	    }
	}
    }

    public String getAttachements() {
	return attachements;
    }

    public void setAttachements(String attachements) {
	this.attachements = attachements;
    }
}