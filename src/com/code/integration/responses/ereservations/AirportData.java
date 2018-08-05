package com.code.integration.responses.ereservations;

import java.io.Serializable;

public class AirportData implements Serializable {

    private Long id;
    private String code;
    private String name;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

}
