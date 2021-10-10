package com.code.integration.webservicesclients.pushclient;

import java.io.IOException;
import java.util.Date;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.HttpHeaders;

public class PushNotificationLogger implements ClientRequestFilter, ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext request) throws IOException {
	System.out.println("$$$$$ ClientRequestFilter:ClientRequestContext: URI: " + request.getUri());
	System.out.println("$$$$$ ClientRequestFilter:ClientRequestContext: Header: " + HttpHeaders.AUTHORIZATION + ":" + request.getHeaderString(HttpHeaders.AUTHORIZATION));
	System.out.println("$$$$$ ClientRequestFilter:ClientRequestContext: Body: " + request.getEntity().toString());
	System.out.println("$$$$$ ClientRequestFilter:ClientRequestContext: Request Time: " + new Date());
    }

    @Override
    public void filter(ClientRequestContext request, ClientResponseContext response) throws IOException {
	System.out.println("##### ClientResponseFilter:ClientRequestContext: URI: " + request.getUri());
	System.out.println("##### ClientResponseFilter:ClientRequestContext: Header: " + HttpHeaders.AUTHORIZATION + ":" + request.getHeaderString(HttpHeaders.AUTHORIZATION));
	System.out.println("##### ClientResponseFilter:ClientRequestContext: Body: " + request.getEntity().toString());

	System.out.println("##################################################");
	System.out.println("##### ClientResponseFilter:ClientResponseContext: Header: " + response.toString());
	System.out.println("##### ClientRequestFilter:ClientRequestContext: Response Time: " + new Date());

	byte[] arr = new byte[5000];
	response.getEntityStream().read(arr);
	System.out.println("##### ClientResponseFilter:ClientResponseContext: Body: " + new String(arr));
    }

}
