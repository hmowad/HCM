package com.code.ui.servlets;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.dal.orm.hcm.employees.EmployeePhoto;
import com.code.dal.orm.signatures.Signature;
import com.code.services.hcm.EmployeesService;
import com.code.services.signatures.SignaturesService;

@SuppressWarnings("serial")
public class ImageServlet extends HttpServlet {

    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	BufferedOutputStream output = null;
	try {
	    int mode = Integer.parseInt(request.getParameter("mode")); // 1 employee photo, 2 signature photo
	    long objectId = Long.parseLong(request.getParameter("objectId"));

	    byte[] objectImage = new byte[0];

	    switch (mode) {
	    case 1:
		EmployeePhoto empPhoto = EmployeesService.getEmployeePhotoByEmpId(objectId);
		objectImage = empPhoto.getPhoto();
		break;
	    case 2:
		Signature s = SignaturesService.getSignatureById(objectId);
		objectImage = s.getSignaturePhoto();
	    }

	    response.reset();
	    response.setBufferSize(DEFAULT_BUFFER_SIZE);
	    response.setContentLength(objectImage.length);
	    response.setHeader("Content-Disposition", "inline; filename=\"" + "img_" + objectId + "\"");

	    output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
	    output.write(objectImage);
	} catch (Exception e) {
	    response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
	    return;
	} finally {
	    close(output);
	}
    }

    private static void close(Closeable resource) {
	try {
	    if (resource != null)
		resource.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}