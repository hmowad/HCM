package com.code.ui.backings.hcm.martyrs;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.martyrs.MartyrdomReason;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MartyrsService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "martyrdomReasonsManagement")
@ViewScoped
public class MartyrdomReasonsManagement extends BaseBacking {

    private List<MartyrdomReason> martyrdomReasonsList;
    private int pageSize = 10;
    private int pageNum = 1;

    public MartyrdomReasonsManagement() {
	try {
	    martyrdomReasonsList = MartyrsService.getMartyrdomReasons();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}

    }

    public void save(MartyrdomReason martyrdomReason) {
	try {
	    martyrdomReason.setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
	    if (martyrdomReason.getId() == null)
		MartyrsService.addMartyrdomReason(martyrdomReason);
	    else
		MartyrsService.updateMartyrdomReason(martyrdomReason);

	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void delete(MartyrdomReason martyrdomReason) {
	try {
	    if (martyrdomReason.getId() != null) {
		martyrdomReason.setSystemUser(this.loginEmpData.getEmpId() + ""); // For Auditing.
		MartyrsService.deleteMartyrdomReason(martyrdomReason);
	    }
	    martyrdomReasonsList.remove(martyrdomReason);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void add() {
	martyrdomReasonsList.add(0, new MartyrdomReason());
	pageNum = 1;
    }

    public List<MartyrdomReason> getMartyrdomReasonsList() {
	return martyrdomReasonsList;
    }

    public void setMartyrdomReasonsList(List<MartyrdomReason> martyrdomReasonsList) {
	this.martyrdomReasonsList = martyrdomReasonsList;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public int getPageNum() {
	return pageNum;
    }

    public void setPageNum(int pageNum) {
	this.pageNum = pageNum;
    }

}
