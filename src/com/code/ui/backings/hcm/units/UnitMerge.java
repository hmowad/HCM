package com.code.ui.backings.hcm.units;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.UnitsService;
import com.code.services.util.HijriDateService;
import com.code.ui.util.UnitTreeNode;

@SuppressWarnings("serial")
@ManagedBean(name = "unitMerge")
@SessionScoped
public class UnitMerge extends UnitTreeBase implements Serializable {
    private String decNumber;
    private Date decDate;
    private List<UnitData> mergedUnitList;
    private int pageSize = 10;
    private Long unitId;
    private Map<Long, Long> listIdMap;

    public UnitMerge() {
        super();
    }

    public void init() {
        try {
            super.init();
            decDate = HijriDateService.getHijriSysDate();
            decNumber = "";
            mergedUnitList = new ArrayList<UnitData>();
            listIdMap = new HashMap<Long, Long>();
        } catch (BusinessException e) {
            this.setServerSideErrorMessages(getMessage("error_general"));
        }
    }

    public void click(UnitTreeNode unitItem) {
        super.click(unitItem);
        decNumber = "";
        mergedUnitList = new ArrayList<UnitData>();
        listIdMap = new HashMap<Long, Long>();
    }

    public void search() {
        super.search();
        mergedUnitList = new ArrayList<UnitData>();
    }

    public void merge() {
        try {
            if (mergedUnitList.size() != 0) {
                UnitsService.mergeUnit(selectedUnitData, mergedUnitList, decNumber, decDate, this.loginEmpData.getEmpId());
                this.init();
                this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
            } else {
                this.setServerSideErrorMessages(getMessage("error_atLeastAddOne"));
            }

        } catch (BusinessException e) {
            this.setServerSideErrorMessages(getMessage(e.getMessage()));
        }
    }

    public void addUnitToTable() {
        try {
            if (selectedUnitData.getUnitTypeCode().intValue() == UnitTypesEnum.REGION_COMMANDER.getCode()) {
                this.setServerSideErrorMessages(getMessage("error_InvalidUnitToAdd"));
                return;
            }

            if (listIdMap.get(unitId) == null) {
                mergedUnitList.add(UnitsService.getUnitById(unitId));
                listIdMap.put(unitId, unitId);
            } else {
                this.setServerSideErrorMessages(getMessage("notify_duplicatedUnit"));
            }

        } catch (BusinessException e) {
            this.setServerSideErrorMessages(getMessage(e.getMessage()));
        }
    }

    public void removeUnitFromTable(UnitData unit) {
        mergedUnitList.remove(unit);
        listIdMap.remove(unit.getId());
        this.setServerSideSuccessMessages(getMessage("notify_successOperation"));
    }

    public String getDecNumber() {
        return decNumber;
    }

    public void setDecNumber(String decNumber) {
        this.decNumber = decNumber;
    }

    public Date getDecDate() {
        return decDate;
    }

    public void setDecDate(Date decDate) {
        this.decDate = decDate;
    }

    public List<UnitData> getMergedUnitList() {
        return mergedUnitList;
    }

    public void setMergedUnitList(List<UnitData> mergedUnitList) {
        this.mergedUnitList = mergedUnitList;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
