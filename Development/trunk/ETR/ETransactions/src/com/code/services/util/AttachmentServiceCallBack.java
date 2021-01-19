package com.code.services.util;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.richfaces.json.JSONObject;

import com.code.dal.orm.hcm.attachments.EmployeeFileAttachmentDetail;
import com.code.enums.FlagsEnum;

@Path("/AttachmentServiceCallBack")
public class AttachmentServiceCallBack {
    public static final String SERVICE_URL = "/rest/AttachmentServiceCallBack/updateAttachmentId";

    @POST
    @Path("/updateAttachmentId")
    @Produces({ MediaType.APPLICATION_JSON })
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response updateAttachmentId(String data) {
	JSONObject result = null;
	try {
	    result = new JSONObject(data);
	    String token = result.getString("TOKEN");
	    String contentId = result.getString("CONTENT_ID");
	    String fileName = result.getString("FILE_NAME");

	    if (!contentId.equals(FlagsEnum.ALL.getCode())) {

		EmployeeFileAttachmentDetail attachmentDetail = new EmployeeFileAttachmentDetail();
		attachmentDetail.setAttachmentId((Long.parseLong(token)));
		attachmentDetail.setContentId(contentId);
		attachmentDetail.setFileName(fileName);
		EmployeeFileAttachmentService.addEmployeeFileAttachmentDetail(attachmentDetail);
	    }
	    result = new JSONObject();
	    result.put("RESULT", "SUCCESS");
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return Response.status(Status.OK).entity(result.toString()).build();
    }
}
