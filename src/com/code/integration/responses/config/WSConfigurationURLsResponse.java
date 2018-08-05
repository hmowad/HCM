package com.code.integration.responses.config;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.integration.responses.WSResponseBase;

public class WSConfigurationURLsResponse extends WSResponseBase {

    private List<WSConfigurationURLItem> configurationURLs;

    @XmlElementWrapper(name = "configurationURLs")
    @XmlElement(name = "configurationURL")
    public List<WSConfigurationURLItem> getConfigurationURLs() {
	return configurationURLs;
    }

    public void setConfigurationURLs(List<WSConfigurationURLItem> configurationURLs) {
	this.configurationURLs = configurationURLs;
    }

}
