package com.code.ui.backings.worklist;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.exceptions.BusinessException;
import com.code.services.workflow.DashBoardWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "wfApprovalDashBoard")
@ViewScoped
public class WFApprovalDashBoard extends BaseBacking {

    private final static String COLLECTIVE_APPROVAL_NAVIGATION = "COLLECTIVE_APPROVAL_";

    private List<Object> dashBoardData;

    private int mode = -1; // 1 means acceptance and 2 means approval

    public WFApprovalDashBoard() {
	super();
	try {
	    if (getRequest().getParameter("mode") != null)
		this.mode = Integer.parseInt(getRequest().getParameter("mode"));

	    if (mode == 1)
		this.setScreenTitle(getMessage("title_wfAcceptanceDashBoard"));
	    else if (mode == 2)
		this.setScreenTitle(getMessage("title_wfApprovalDashBoard"));
	    else
		this.setServerSideErrorMessages(getMessage("error_general"));

	    dashBoardData = DashBoardWorkFlow.getDashBoardData(mode, this.loginEmpData.getEmpId());

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String navigate(String processGroupId) {
	getRequest().setAttribute("mode", mode);
	return COLLECTIVE_APPROVAL_NAVIGATION + processGroupId;
    }

    public List<Object> getDashBoardData() {
	return dashBoardData;
    }

    public void setDashBoardData(List<Object> dashBoardData) {
	this.dashBoardData = dashBoardData;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

}
