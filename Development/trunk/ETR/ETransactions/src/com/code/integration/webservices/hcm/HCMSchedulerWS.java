package com.code.integration.webservices.hcm;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.code.services.hcm.MovementsService;
import com.code.services.hcm.MovementsWishesService;
import com.code.services.hcm.RaisesService;
import com.code.services.hcm.TerminationsService;
import com.code.services.workflow.BaseWorkFlow;

@WebService(targetNamespace = "http://integration.code.com/HCMScheduler",
	portName = "HCMSchedulerWSHttpPort")
public class HCMSchedulerWS {

    @WebMethod(operationName = "handleHCMDailyMidNightScheduler")
    public void handleHCMDailyMidNightScheduler() {

	try {
	    System.out.println("Calling executeScheduledMovementsTransactions()" + new Date());
	    MovementsService.executeScheduledTransactions();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    System.out.println("Calling executeScheduledMovementsWishesTransactions()" + new Date());
	    MovementsWishesService.cancelInvalidMovementsWishesTransactions();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    System.out.println("Calling executeScheduledServiceTerminationTransactions()" + new Date());
	    TerminationsService.executeScheduledServiceTerminationTransactions();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    System.out.println("Calling executeScheduledRaiseTransactions()" + new Date());
	    RaisesService.executeScheduledRaiseTransactions();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	try {
	    System.out.println("Calling handleUnassignedTasks()" + new Date());
	    BaseWorkFlow.handleUnassignedTasks();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
