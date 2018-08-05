package com.code.integration.exceptions;

/**
 * The class {@code Exception} used to wrap any exception that has been thrown by any client.
 *
 */
public class ClientIntegrationException extends Exception {

    public ClientIntegrationException(String message) {
	super(message);
    }

}