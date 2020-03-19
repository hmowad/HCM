package com.code.ui.backings.minisearch;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.setup.AdminDecision;
import com.code.exceptions.BusinessException;
import com.code.services.integration.PayrollEngineService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "adminDecisionsMiniSearch")
@ViewScoped
public class AdminDecisionsMiniSearch extends BaseBacking {

    private int rowsCount = 10;
    private List<AdminDecision> adminDecisionList;

    public AdminDecisionsMiniSearch() {
	try {
	    adminDecisionList = PayrollEngineService.getAllAdminDecisions();
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage("error_general"));
	}
    }

    public int getRowsCount() {
	return rowsCount;
    }

    public void setRowsCount(int rowsCount) {
	this.rowsCount = rowsCount;
    }

    public List<AdminDecision> getAdminDecisionList() {
	return adminDecisionList;
    }

    public void setAdminDecisionList(List<AdminDecision> adminDecisionList) {
	this.adminDecisionList = adminDecisionList;
    }

}
