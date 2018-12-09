package com.code.ui.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.code.dal.DataAccess;
import com.code.integration.webservicesclients.pushclient.PushNotificationRestClient;
import com.code.services.config.ETRConfigurationService;
import com.code.services.log4j.Log4jService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;

public class AppContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
	DataAccess.init();
	ETRConfigurationService.init();
	CommonService.init();
	HijriDateService.init();
	PushNotificationRestClient.init();
	Log4jService.init();
    }

    public void contextDestroyed(ServletContextEvent event) {
	DataAccess.destroy();
	PushNotificationRestClient.destroy();
    }
}
