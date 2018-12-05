package com.code.integration.webservicesclients.promotiondrugtest;

import java.util.Hashtable;

import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;

/**
 *
 */
public class DrugsTestJMSClient {

    public static void sendTextMessage(String inputMessage) throws BusinessException {
	QueueConnection queueConnection = null;
	QueueSession queueSession = null;
	MessageProducer messageProducer = null;
	Logger log = Logger.getLogger(DrugsTestJMSClient.class.getName());
	try {
	    log.info("-- start of sendTextMessage() method --");
	    InitialContext context = getInitialContext();
	    QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) context.lookup(getDrugsTestJMSFactory());
	    queueConnection = queueConnectionFactory.createQueueConnection();
	    log.info("-- initialize connection factory --");
	    queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
	    Queue queue = (Queue) context.lookup(getDrugsLookUpJMSQueue());
	    messageProducer = queueSession.createProducer(queue);
	    queueConnection.start();
	    log.info("-- queue started with message --" + inputMessage);
	    TextMessage message = queueSession.createTextMessage();
	    message.setText(inputMessage);
	    messageProducer.send(message);
	    log.info("-- message sent --");
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_promotionConnectionToInfoSysFailed");
	} finally {
	    try {
		messageProducer.close();
		queueSession.close();
		queueConnection.close();
	    } catch (Exception e) {
		e.printStackTrace();
		throw new BusinessException("error_promotionConnectionToInfoSysFailed");
	    }
	}

    }

    private static InitialContext getInitialContext() throws NamingException {
	Hashtable<String, String> envelope = new Hashtable<String, String>();
	envelope.put(Context.INITIAL_CONTEXT_FACTORY, getDrugsJNDIFactory());
	envelope.put(Context.PROVIDER_URL, getDrugsTestJMSServerURL());
	return new InitialContext(envelope);
    }

    private static String getDrugsTestJMSServerURL() {
	return ETRConfigurationService.getDrugsTestConfigInfo()[0];
    }

    private static String getDrugsTestJMSFactory() {
	return ETRConfigurationService.getDrugsTestConfigInfo()[1];
    }

    private static String getDrugsLookUpJMSQueue() {
	return ETRConfigurationService.getDrugsTestConfigInfo()[2];
    }

    private static String getDrugsJNDIFactory() {
	return ETRConfigurationService.getDrugsTestConfigInfo()[3];
    }

}