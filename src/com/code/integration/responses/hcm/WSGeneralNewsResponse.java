package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.generalNews.GeneralNews;
import com.code.integration.responses.WSResponseBase;

public class WSGeneralNewsResponse extends WSResponseBase {

    private List<GeneralNews> generalNews;

    @XmlElementWrapper(name = "generalNews")
    @XmlElement(name = "news")
    public List<GeneralNews> getGeneralNews() {
	return generalNews;
    }

    public void setGeneralNews(List<GeneralNews> generalNews) {
	this.generalNews = generalNews;
    }

}
