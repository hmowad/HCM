package com.code.ui.backings.hcm.retirements;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.dal.orm.hcm.retirements.DisclaimerTransactionData;
import com.code.dal.orm.workflow.WFTaskData;
import com.code.dal.orm.workflow.hcm.retirements.WFDisclaimerData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.RetirementsService;
import com.code.services.hcm.UnitsService;
import com.code.services.security.SecurityService;
import com.code.services.workflow.hcm.RetirementsWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "disclaimerDataView")
@ViewScoped
public class DisclaimerDataView extends BaseBacking {

    private List<WFTaskData> disclaimerTransactionWFTasks;
    private Boolean isEligible;

    public DisclaimerDataView() {
	try {
	    Long terminationTransactionId = null;
	    String terminationTransactionParam = getRequest().getParameter("terminationTransactionId");
	    if (terminationTransactionParam != null && !terminationTransactionParam.isEmpty()) {
		terminationTransactionId = Long.parseLong(terminationTransactionParam);
	    } else {
		throw new BusinessException("error_general");
	    }

	    isEmployeeEligible(terminationTransactionId);
	    if (isEligible) {
		WFDisclaimerData wfDisclaimerData = RetirementsWorkFlow.getWFDisclaimerDataByTerminationTransId(terminationTransactionId).get(0);
		disclaimerTransactionWFTasks = RetirementsWorkFlow.getNonNotificationWFInstanceTasksData(wfDisclaimerData.getInstanceId());

		WFTaskData esmTask = new WFTaskData();
		for (WFTaskData disclaimerTransactionWFTask : disclaimerTransactionWFTasks) {
		    if (disclaimerTransactionWFTask.getAssigneeWfRole().equals(WFTaskRolesEnum.EXTRA_SIGN_MANAGER.getCode()) && disclaimerTransactionWFTask.getAction().equals(WFTaskActionsEnum.SUPER_SIGN.getCode())) {
			esmTask = disclaimerTransactionWFTask;
			disclaimerTransactionWFTasks.remove(disclaimerTransactionWFTask);
			break;
		    }
		}
		disclaimerTransactionWFTasks.add(esmTask);

	    }

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	} catch (Exception e) {
	    this.setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public void isEmployeeEligible(Long terminationTransactionId) throws BusinessException {
	DisclaimerTransactionData disclaimerTransactionData = RetirementsService.getDisclaimerTransByTerminationTransId(terminationTransactionId);
	if ((SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.EMPS_OFFICER_FILE.getCode(), MenuActionsEnum.PRS_EMPS_FILE_MODIFY_OFFICERS.getCode()) && disclaimerTransactionData.getTransEmpCategoryId() == CategoriesEnum.OFFICERS.getCode())
		|| (SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.EMPS_SOLDIER_FILE.getCode(), MenuActionsEnum.PRS_EMPS_FILE_MODIFY_SOLDIERS.getCode()) && disclaimerTransactionData.getTransEmpCategoryId() == CategoriesEnum.SOLDIERS.getCode())) {
	    setIsEligible(true);
	} else if (loginEmpData.getIsManager() == FlagsEnum.ON.getCode()) {
	    UnitData unitData = UnitsService.getUnitByExactFullName(disclaimerTransactionData.getTransEmpUnitFullName());

	    List<UnitData> ancestorsUnits = UnitsService.getAncestorsUnitsByHKey(unitData.gethKey());
	    for (UnitData unit : ancestorsUnits) {
		if (loginEmpData.getEmpId().equals(unit.getPhysicalManagerId())) {
		    setIsEligible(true);
		    break;
		}
	    }
	} else if (disclaimerTransactionData.getEmpId().equals(loginEmpData.getEmpId())) {
	    setIsEligible(true);
	}
    }

    public List<WFTaskData> getDisclaimerTransactionWFTasks() {
	return disclaimerTransactionWFTasks;
    }

    public void setDisclaimerTransactionWFTasks(List<WFTaskData> disclaimerTransactionWFTasks) {
	this.disclaimerTransactionWFTasks = disclaimerTransactionWFTasks;
    }

    public Boolean getIsEligible() {
	return isEligible;
    }

    public void setIsEligible(Boolean isEligible) {
	this.isEligible = isEligible;
    }

}