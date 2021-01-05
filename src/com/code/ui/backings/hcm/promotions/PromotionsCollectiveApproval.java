package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.promotions.PromotionReportData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.promotions.WFPromotion;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PromotionsService;
import com.code.services.workflow.hcm.PromotionsWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "PromotionsCollectiveApproval")
@ViewScoped
public class PromotionsCollectiveApproval extends BaseBacking {

    // object[0] - WFPromotion
    // object[1] - WFTask
    // object[2] - WFInstance
    // object[3] - processName
    // object[4] - requester
    // object[5] - delegatingName
    private List<Object> promotionsTasks;

    private boolean selectAll;
    private int promotionsTasksListSize;

    private final static String taskUrlParam = "&taskId=";
    private final static int pageSize = 10;
    private int mode = -1; // 1 means acceptance and 2 means approval

    public PromotionsCollectiveApproval() {
	super();
	if (getRequest().getAttribute("mode") != null)
	    this.mode = Integer.parseInt(getRequest().getAttribute("mode").toString());

	if (mode == 1)
	    this.setScreenTitle(getMessage("title_promotionsCollectiveAcceptance"));
	else if (mode == 2)
	    this.setScreenTitle(getMessage("title_promotionsCollectiveApproval"));
	else
	    setServerSideErrorMessages(getMessage("error_URLError"));
	// Load the promotions tasks data
	searchPromotionTasks();
    }

    // Load the promotions tasks data
    private void searchPromotionTasks() {
	try {
	    selectAll = false;
	    promotionsTasks = (ArrayList<Object>) PromotionsWorkFlow.getWFpromotionsTasks(this.loginEmpData.getEmpId(), mode == 1 ? WFTaskRolesEnum.ADMINISTRATIVE_ORGANIZATION_MANAGER.getCode() : WFTaskRolesEnum.SIGN_MANAGER.getCode());
	    if (mode == 1)
		promotionsTasks.addAll((ArrayList<Object>) PromotionsWorkFlow.getWFpromotionsTasks(this.loginEmpData.getEmpId(), WFTaskRolesEnum.PROMOTION_DEPARTMENT_MANAGER.getCode()));
	    promotionsTasksListSize = promotionsTasks.size();
	} catch (BusinessException e) {
	    promotionsTasks = new ArrayList<Object>();
	    promotionsTasksListSize = 0;
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    // called from the xhtml on change of the selectAll checkbox
    public void selectUnselectAllRows() {
	for (int i = 0; i < promotionsTasks.size(); i++) {
	    ((WFPromotion) (((Object[]) promotionsTasks.get(i))[0])).setSelected(selectAll);
	}
    }

    // Loop over the selected tasks for approval
    // called from the xhtml when "Approve" button clicked
    public void doPromotionsCollective() {
	String unsuccessfulTaskIdsIfAny = "";
	String comma = "";
	int unsuccessfulTasksCount = 0;

	for (Object obj : promotionsTasks) {
	    WFPromotion promotionRequest = (WFPromotion) (((Object[]) obj)[0]);
	    WFTask task = null;
	    if (!promotionRequest.getSelected())
		continue;

	    try {
		task = (WFTask) (((Object[]) obj)[1]);
		WFInstance instance = (WFInstance) (((Object[]) obj)[2]);
		EmployeeData requester = (EmployeeData) (((Object[]) obj)[4]);
		PromotionReportData report = PromotionsService.getPromotionReportDataById(promotionRequest.getReportId());
		if (mode == 1) {
		    if (task.getAssigneeWfRole().equals(WFTaskRolesEnum.PROMOTION_DEPARTMENT_MANAGER.getCode()))
			PromotionsWorkFlow.doPromotionPDM(requester, report, instance, task, WFActionFlagsEnum.APPROVE.getCode());
		    else if (task.getAssigneeWfRole().equals(WFTaskRolesEnum.ADMINISTRATIVE_ORGANIZATION_MANAGER.getCode()))
			PromotionsWorkFlow.doPromotionAOM(requester, report, instance, task, WFActionFlagsEnum.APPROVE.getCode());
		} else if (mode == 2)
		    PromotionsWorkFlow.doPromotionSM(requester, report, instance, task, WFActionFlagsEnum.APPROVE.getCode());
	    } catch (BusinessException e) {
		unsuccessfulTaskIdsIfAny += comma + task.getTaskId();
		unsuccessfulTasksCount++;
		comma = ", ";
	    }
	}

	if (unsuccessfulTasksCount > 0)
	    this.setServerSideErrorMessages(getParameterizedMessage("error_thereAreErrorsForNOfTasks", new Object[] { unsuccessfulTasksCount, unsuccessfulTaskIdsIfAny }));
	else
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));

	// Call the search method here to Reload the tasks
	searchPromotionTasks();
    }

    public List<Object> getPromotionsTasks() {
	return promotionsTasks;
    }

    public void setPromotionsTasks(List<Object> promotionsTasks) {
	this.promotionsTasks = promotionsTasks;
    }

    public boolean isSelectAll() {
	return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
	this.selectAll = selectAll;
    }

    public int getPromotionsTasksListSize() {
	return promotionsTasksListSize;
    }

    public void setPromotionsTasksListSize(int promotionsTasksListSize) {
	this.promotionsTasksListSize = promotionsTasksListSize;
    }

    public String getTaskurlparam() {
	return taskUrlParam;
    }

    public int getPagesize() {
	return pageSize;
    }

    public int getMode() {
	return mode;
    }

    public void setMode(int mode) {
	this.mode = mode;
    }

}
