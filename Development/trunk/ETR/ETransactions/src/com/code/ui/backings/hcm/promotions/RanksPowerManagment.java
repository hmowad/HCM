package com.code.ui.backings.hcm.promotions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.promotions.RankPowerData;
import com.code.enums.MenuActionsEnum;
import com.code.enums.MenuCodesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.PromotionsService;
import com.code.services.security.SecurityService;
import com.code.ui.backings.base.BaseBacking;

@SuppressWarnings("serial")
@ManagedBean(name = "ranksPowerManagment")
@ViewScoped
public class RanksPowerManagment extends BaseBacking {

    private List<RankPowerData> powersData = new ArrayList<RankPowerData>();

    private boolean hasModifyPrivilege = false;

    public RanksPowerManagment() {
	try {
	    powersData = PromotionsService.adjustRanksPower();
	    hasModifyPrivilege = SecurityService.isEmployeeMenuActionGranted(this.loginEmpData.getEmpId(), MenuCodesEnum.JOBS_RANKS_POWER_MANAGMENT.getCode(), MenuActionsEnum.JOBS_RANKS_POWER_MANAGMENT_MODIFY.getCode());
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void modifyRankPower(int index, RankPowerData newRankPowerData) {
	try {
	    PromotionsService.modifyRanksPower(powersData.get(index), newRankPowerData, this.loginEmpData.getEmpId());
	    powersData = PromotionsService.adjustRanksPower();
	    this.setServerSideSuccessMessages(getMessage("notify_successOperation"));

	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public void printRankPowerReport() {
	try {
	    List<Integer> netVacantList = new ArrayList<Integer>();
	    List<Integer> allowedPromotionCountList = new ArrayList<Integer>();
	    List<Integer> loadedBalanceWithdrawnFromPromotionList = new ArrayList<Integer>();
	    for (int i = 0; i < powersData.size(); i++) {
		netVacantList.add(i, powersData.get(i).getNetVacant());
		allowedPromotionCountList.add(i, powersData.get(i).getAllowedPromotionCount());
		loadedBalanceWithdrawnFromPromotionList.add(i, powersData.get(i).getLoadedBalanceWithdrawnFromPromotion());
	    }
	    byte[] bytes = PromotionsService.getRankPowerReportDataBytes(netVacantList, allowedPromotionCountList, loadedBalanceWithdrawnFromPromotionList);

	    super.print(bytes);
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public List<RankPowerData> getPowersData() {
	return powersData;
    }

    public boolean isHasModifyPrivilege() {
	return hasModifyPrivilege;
    }

    public void setHasModifyPrivilege(boolean hasModifyPrivilege) {
	this.hasModifyPrivilege = hasModifyPrivilege;
    }

}
