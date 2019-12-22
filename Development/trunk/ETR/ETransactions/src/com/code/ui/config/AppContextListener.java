package com.code.ui.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.code.dal.DataAccess;
import com.code.integration.webservicesclients.payroll.PayrollRestClient;
import com.code.integration.webservicesclients.pushclient.PushNotificationRestClient;
import com.code.services.config.ETRConfigurationService;
import com.code.services.integration.PayrollEngineService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.util.Log4jService;

public class AppContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
	DataAccess.init();
	ETRConfigurationService.init();
	CommonService.init();
	HijriDateService.init();
	PushNotificationRestClient.init();
	Log4jService.init();
	PayrollEngineService.init();
	PayrollRestClient.init();
    }

    public void contextDestroyed(ServletContextEvent event) {
	DataAccess.destroy();
	PushNotificationRestClient.destroy();
	PayrollRestClient.destroy();
    }
}
