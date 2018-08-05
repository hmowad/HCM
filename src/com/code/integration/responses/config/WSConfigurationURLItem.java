package com.code.integration.responses.config;

import java.io.Serializable;

public class WSConfigurationURLItem implements Serializable {

    private String key;
    private String url;

    public WSConfigurationURLItem() {
	super();
    }

    public WSConfigurationURLItem(String key, String url) {
	super();
	this.key = key;
	this.url = url;
    }

    public String getKey() {
	return key;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

}
