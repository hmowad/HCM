package com.code.exceptions;

public class BusinessException extends Exception {
    private static final long serialVersionUID = 1L;
    private Object[] params = null;

    public BusinessException(String message) {
	super(message);
    }

    public BusinessException(String message, Object[] params) {
	super(message);
	this.params = params;
    }

    public Object[] getParams() {
	return params;
    }

    public void setParams(Object[] params) {
	this.params = params;
    }
}