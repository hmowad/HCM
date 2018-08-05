package com.code.ui.backings.hcm.movements;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.exceptions.BusinessException;
import com.code.services.hcm.MovementsWishesService;
import com.code.services.workflow.hcm.MovementsWishesWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "movementsWishesConfiguration")
@ViewScoped
public class MovementsWishesConfiguration extends BaseBacking implements Serializable {

    boolean openFlag;
    int minServicePeriod;
    String countRunningWorkflowsMessage;

    public MovementsWishesConfiguration() {
	setScreenTitle(getMessage("title_movementsWishesConfiguration"));
	try {
	    countRunningWorkflowsMessage = getParameterizedMessage("q_saveWithDeleteMovementWishesProgress", new Object[] { MovementsWishesWorkFlow.countMovementWishesRunningWorkflows() });
	    openFlag = MovementsWishesService.getMovementWishesOpenFlag();
	    minServicePeriod = MovementsWishesService.getMovementWishesMinServicePeriod();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void save() {
	try {
	    MovementsWishesWorkFlow.setMovementWishesConfig(minServicePeriod, openFlag, this.loginEmpData.getEmpId());
	    super.setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public boolean isOpenFlag() {
	return openFlag;
    }

    public void updateOpenFlag() {
	openFlag = !openFlag;
    }

    public int getMinServicePeriod() {
	return minServicePeriod;
    }

    public void setMinServicePeriod(int minServicePeriod) {
	this.minServicePeriod = minServicePeriod;
    }

    public String getCountRunningWorkflowsMessage() {
	return countRunningWorkflowsMessage;
    }

}
