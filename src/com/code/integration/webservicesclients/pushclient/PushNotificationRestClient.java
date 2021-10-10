package com.code.integration.webservicesclients.pushclient;

import javax.json.Json;
import javax.json.JsonObject;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.code.enums.FlagsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;

public class PushNotificationRestClient {

    private static Client client;

    private static String serverUrl;
    private static String autherizationHeaderValue;

    public static void init() {
	try {
	    client = ClientBuilder.newBuilder().register(new PushNotificationLogger()).sslContext(getSSLContext()).build();

	    String pushNotificationServerConfigurations = ETRConfigurationService.getPushNotificationServerConfigurations();
	    String[] pushNotificationServerConfigurationsArray = pushNotificationServerConfigurations.split(",");

	    serverUrl = pushNotificationServerConfigurationsArray[0];
	    autherizationHeaderValue = "key=" + pushNotificationServerConfigurationsArray[1];

	} catch (Exception e) {
	    System.out.println("### ERROR_PUSH_CLIENT : Could not establish push server rest client.");
	    e.printStackTrace();
	}
    }

    public static void pushNotification(Long taskId, long notifierId, String assigneeWfRole) {
	String message = BaseService.getParameterizedMessage("notify_newTask", taskId + "");
	if (WFTaskRolesEnum.NOTIFICATION.getCode().equals(assigneeWfRole))
	    message = BaseService.getParameterizedMessage("notify_newNotification", taskId + "");

	pushNotificationMessage(notifierId, message);
    }

    public static void pushNotificationMessage(long notifierId, String message) {
	try {
	    if ((FlagsEnum.ON.getCode() + "").equals(ETRConfigurationService.getMobilePushFlag())) {
		// Construct the message object.
		JsonObject messageObject = Json.createObjectBuilder()
			.add("notification", Json.createObjectBuilder()
				.add("body", message)
				.add("sound", "default")
				.add("badge", 1))
			.add("to", "/topics/" + notifierId)
			.build();

		// push the notification
		client.target(serverUrl)
			.request()
			.header(HttpHeaders.AUTHORIZATION, autherizationHeaderValue)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
			.async()
			.post(Entity.entity(messageObject, MediaType.APPLICATION_JSON + "; charset=utf-8"), new InvocationCallback<Response>() {

			    @Override
			    public void completed(Response response) {
				if (response.getStatus() != Status.OK.getStatusCode())
				    System.out.println("### ERROR_PUSH_CLIENT : Message has not been accepted.");

				response.close();
			    }

			    @Override
			    public void failed(Throwable throwable) {
				System.out.println("### ERROR_PUSH_CLIENT : Faild to push the message.");
				throwable.printStackTrace();
			    }
			});
	    }
	} catch (Exception e) {
	    System.out.println("### ERROR_PUSH_CLIENT : GENERAL ERROR.");
	    e.printStackTrace();
	}
    }

    public static void destroy() {
	try {
	    client.close();
	} catch (Exception e) {
	    System.out.println("### ERROR_PUSH_CLIENT : Could not destroy push server rest client.");
	    e.printStackTrace();
	}
    }

    // To bypass HTTPS connection.
    private static SSLContext getSSLContext() {
	javax.net.ssl.TrustManager x509 = new javax.net.ssl.X509TrustManager() {
	    @Override
	    public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
		return;
	    }

	    @Override
	    public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
		return;
	    }

	    @Override
	    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	    }
	};
	SSLContext ctx = null;
	try {
	    ctx = SSLContext.getInstance("SSL");
	    ctx.init(null, new javax.net.ssl.TrustManager[] { x509 }, null);
	} catch (java.security.GeneralSecurityException ex) {
	    System.out.println("### ERROR_PUSH_CLIENT : GENERAL SECURITY EXCEPTION.");
	}
	return ctx;
    }

}
