package com.code.ui.backings.security;

import java.util.ArrayList;
import java.util.Calendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.HttpSession;

import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.vacations.VacationData;
import com.code.enums.CategoriesEnum;
import com.code.enums.FlagsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.SessionAttributesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.VacationsService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "home")
@RequestScoped
public class Home extends BaseBacking {

    private String inboxCount;
    private String notificationsCount;
    private Object[] delegationsCounts; // 0:Total From; 1:Total To; 2:Partial From; 3:Partial To
    private boolean socialAvailable;
    private boolean socialExpired;
    private VacationData lastVacation;
    private MovementTransactionData lastValidMovTrans;
    private MovementTransactionData lastValidSubjoinTran;
    private boolean showTimeLineMiniSearchFlag;

    public Home() {
	calculateInboxCount();
	calculateNotificationsCount();
	calcAlertsData();
	HttpSession session = getSession();
	if (session.getAttribute(SessionAttributesEnum.TIME_LINE_MINI_SEARCH_SHOW_FLAG.getCode()) != null) {
	    showTimeLineMiniSearchFlag = (boolean) session.getAttribute(SessionAttributesEnum.TIME_LINE_MINI_SEARCH_SHOW_FLAG.getCode());
	}
	session.setAttribute(SessionAttributesEnum.TIME_LINE_MINI_SEARCH_SHOW_FLAG.getCode(), false);
    }

    private void calculateInboxCount() {
	try {
	    inboxCount = " ( " + BaseWorkFlow.countWFTasks(this.loginEmpData.getEmpId(), FlagsEnum.OFF.getCode()) + " ) ";
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    inboxCount = " ( ) ";
	}
    }

    private void calculateNotificationsCount() {
	try {
	    notificationsCount = " ( " + BaseWorkFlow.countWFTasks(this.loginEmpData.getEmpId(), FlagsEnum.ON.getCode()) + " ) ";
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    notificationsCount = " ( ) ";
	}
    }

    public String getWelcomeMessage() {
	return ((loginEmpData.getCategoryId() != null
		&& (loginEmpData.getCategoryId().longValue() == CategoriesEnum.OFFICERS.getCode()
			|| loginEmpData.getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode())) ? (this.loginEmpData.getRankDesc() + " / ") : "")
		+ getLoginUsername();
    }

    public String getOutboxCount() {
	try {
	    return " ( " + BaseWorkFlow.getWFInstancesUnderProcessingCount(this.loginEmpData.getEmpId()) + " ) ";
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    return " ( ) ";
	}
    }

    public void calculateDelegationsCount() {
	try {
	    ArrayList<Object> returnedCounts = (ArrayList<Object>) BaseWorkFlow.countWFDelegations(this.loginEmpData.getEmpId());
	    delegationsCounts = (Object[]) (returnedCounts.get(0));
	    for (int i = 0; i < delegationsCounts.length; i++) {
		delegationsCounts[i] = " ( " + delegationsCounts[i] + " ) ";
	    }
	} catch (BusinessException e) {
	    e.printStackTrace();
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	    delegationsCounts = new Object[4];
	    for (int i = 0; i < delegationsCounts.length; i++) {
		delegationsCounts[i] = " ( ) ";
	    }
	}
    }

    public String getMedicalCenterURL() {
	return ETRConfigurationService.getMedicalCenterURL()
		+ ((SecurityService.getConfig("internalFlag").equals(FlagsEnum.ON.getCode() + "")) ? "" : encryptString(String.valueOf(super.loginEmpData.getSocialID())));
    }

    private String encryptString(String string) {
	// reverse String
	// if number < 5 add 5 to id
	// if number >= 5 sub 5 from it
	int number;
	StringBuffer encryptedString = new StringBuffer();
	String reversedString = (new StringBuffer(string).reverse()).toString();

	char[] chars = reversedString.toCharArray();
	for (int i = 0; i < chars.length; i++) {
	    number = (int) chars[i] - 48;
	    if (number <= 4)
		number = number + 5;
	    else
		number = number - 5;

	    encryptedString.append(number);
	}
	return encryptedString.toString();
    }

    public void calcAlertsData() {
	try {
	    if (loginEmpData.getSocialIDExpiryDate() != null) {
		if (HijriDateService.getHijriSysDate().after(loginEmpData.getSocialIDExpiryDate())) {
		    socialExpired = true;
		    socialAvailable = false;
		} else {
		    int diffDays = Math.abs(HijriDateService.hijriDateDiff(HijriDateService.getHijriSysDate(), loginEmpData.getSocialIDExpiryDate()));
		    if (diffDays <= ETRConfigurationService.getSocialIdRenewalPeriod()) {
			socialAvailable = true;
			socialExpired = false;
		    }
		}
	    }
	    lastVacation = VacationsService.getLastVacationWithoutJoiningDate(loginEmpData.getEmpId());
	    lastValidMovTrans = MovementsService.getLastValidMovementTransactionForJoiningDate(loginEmpData.getEmpId(), MovementTypesEnum.MOVE.getCode());
	    lastValidSubjoinTran = MovementsService.getLastValidMovementTransactionForJoiningDate(loginEmpData.getEmpId(), MovementTypesEnum.SUBJOIN.getCode());
	} catch (BusinessException e) {
	    super.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public String getVersionNumber() {
	return BaseService.getConfig("versionNumber");
    }

    public String getVersionYear() {
	return getParameterizedMessage("label_copyright", Calendar.getInstance().get(Calendar.YEAR) + "");
    }

    public String getInboxLink() {
	return getRequest().getContextPath() + "/WorkList/WFInbox.jsf?menuType=2&role=1&init=1&mode=0&rootOpened=" + ETRConfigurationService.getFollowingProcessesMenuId();
    }

    public String getNotificationLink() {
	return getRequest().getContextPath() + "/WorkList/WFInbox.jsf?menuType=2&role=2&init=1&mode=0&rootOpened=" + ETRConfigurationService.getFollowingProcessesMenuId();
    }

    public String getOutboxLink() {
	return getRequest().getContextPath() + "/WorkList/WFOutbox.jsf?menuType=2&init=1&B=1&rootOpened=" + ETRConfigurationService.getFollowingProcessesMenuId();
    }

    public String getTotalDelegationsFromLink() {
	return getRequest().getContextPath() + "/WorkList/WFDelegationFromManagement.jsf?menuType=2&rootOpened=" + ETRConfigurationService.getDelegationsMenuId() + "#totalDelegationTableId";
    }

    public String getPartialDelegationsFromLink() {
	return getRequest().getContextPath() + "/WorkList/WFDelegationFromManagement.jsf?menuType=2&rootOpened=" + ETRConfigurationService.getDelegationsMenuId() + "#partialDelegationTableId";
    }

    public String getTotalDelegationsToLink() {
	return getRequest().getContextPath() + "/WorkList/WFDelegationToManagement.jsf?menuType=2&rootOpened=" + ETRConfigurationService.getDelegationsMenuId() + "#totalDelegationTableId";
    }

    public String getPartialDelegationsToLink() {
	return getRequest().getContextPath() + "/WorkList/WFDelegationToManagement.jsf?menuType=2&rootOpened=" + ETRConfigurationService.getDelegationsMenuId() + "#partialDelegationTableId";
    }

    public String getDirectManger() {
	return super.loginEmpData.getManagerName();
    }

    public String getEmpRank() {
	return super.loginEmpData.getRankDesc();
    }

    public String getEmpJob() {
	return super.loginEmpData.getJobDesc();
    }

    public String getOfficeExtension() {
	return super.loginEmpData.getPhoneExt();
    }

    public String getEmpIdentity() {
	return super.loginEmpData.getSocialID();
    }

    public String getEmpMobile() {
	return super.loginEmpData.getOfficialMobileNumber();
    }

    public String getEmpStartDate() {
	return super.loginEmpData.getRecruitmentDateString();
    }

    public String getEmpEmail() {
	return super.loginEmpData.getEmail();
    }

    public Long getCategoryId() {
	return super.loginEmpData.getCategoryId();
    }

    public String getInboxCount() {
	return inboxCount;
    }

    public String getNotificationsCount() {
	return notificationsCount;
    }

    public Object[] getDelegationsCounts() {
	return delegationsCounts;
    }

    public boolean isSocialAvailable() {
	return socialAvailable;
    }

    public void setSocialAvailable(boolean socialAvailable) {
	this.socialAvailable = socialAvailable;
    }

    public boolean isSocialExpired() {
	return socialExpired;
    }

    public void setSocialExpired(boolean socialExpired) {
	this.socialExpired = socialExpired;
    }

    public VacationData getLastVacation() {
	return lastVacation;
    }

    public void setLastVacation(VacationData lastVacation) {
	this.lastVacation = lastVacation;
    }

    public MovementTransactionData getLastValidMovTrans() {
	return lastValidMovTrans;
    }

    public void setLastValidMovTrans(MovementTransactionData lastValidMovTrans) {
	this.lastValidMovTrans = lastValidMovTrans;
    }

    public MovementTransactionData getLastValidSubjoinTran() {
	return lastValidSubjoinTran;
    }

    public void setLastValidSubjoinTran(MovementTransactionData lastValidSubjoinTran) {
	this.lastValidSubjoinTran = lastValidSubjoinTran;
    }

    public boolean isShowTimeLineMiniSearchFlag() {
	return showTimeLineMiniSearchFlag;
    }

    public void setShowTimeLineMiniSearchFlag(boolean showTimeLineMiniSearchFlag) {
	this.showTimeLineMiniSearchFlag = showTimeLineMiniSearchFlag;
    }

}
