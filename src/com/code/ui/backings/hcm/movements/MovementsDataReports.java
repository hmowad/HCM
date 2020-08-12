package com.code.ui.backings.hcm.movements;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.Rank;
import com.code.dal.orm.hcm.movements.MovementTransactionData;
import com.code.dal.orm.hcm.organization.Region;
import com.code.dal.orm.hcm.organization.units.UnitData;
import com.code.enums.FlagsEnum;
import com.code.enums.MovementTypesEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.UnitTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.MovementsService;
import com.code.services.hcm.UnitsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "movementsDataReports")
@ViewScoped
public class MovementsDataReports extends BaseBacking implements Serializable {

    private int mode;
    private List<Region> regionList;
    private List<Rank> ranks;

    private String employeeUnitFullName;
    private long employeeRegionId;
    private String employeeRegionDesc;
    private String movementUnitFullName;
    private long movementRegionId;
    private String movementRegionDesc;
    private String employeeMinorSpecDesc;
    private long employeeRankId;
    private String employeeRankDesc;
    private Date fromDate;
    private Date toDate;
    private int reasonType;
    private int verbalOrderFlag;
    private String location;

    private long empId;
    private String empName;
    private String decisionNumber;
    private Date decisionDate;

    private int reportType; // 1 for moves transactions, 2 for moves statistics, 3 for subjoin transactions, 4 for subjoin statistics, 5 for external movements transactions
    // 6 for joining document
    private boolean newDecision, extensionDecision, terminationDecision, cancellationDecision;

    private boolean moveDecision, subjoinDecision, officersAssignmentDecision;

    public MovementsDataReports() {
	try {
	    if (getRequest().getParameter("mode") != null) {
		mode = Integer.parseInt(getRequest().getParameter("mode"));
		switch (mode) {
		case 1:
		    setScreenTitle(getMessage("title_officersMovementsReports"));
		    break;
		case 2:
		    setScreenTitle(getMessage("title_soldiersMovementsReports"));
		    break;
		case 3:
		    setScreenTitle(getMessage("title_civiliansMovementsReports"));
		    break;
		default:
		    break;
		}
	    }

	    regionList = CommonService.getAllRegions();
	    ranks = CommonService.getRanks(null, getCategoriesIdsArrayByMode(mode));

	    if (mode == 1) {
		for (int i = 0; i < ranks.size(); i++) {
		    if (ranks.get(i).getId() == RanksEnum.CADET.getCode())
			ranks.remove(i);
		}
	    }

	    if (mode == 2) {
		for (int i = 0; i < ranks.size(); i++) {
		    if (ranks.get(i).getId() == RanksEnum.STUDENT_SOLDIER.getCode())
			ranks.remove(i);
		}
	    }

	    reportType = 1;

	    resetForm();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void resetForm() {
	try {
	    employeeRegionId = FlagsEnum.ALL.getCode();
	    movementRegionId = FlagsEnum.ALL.getCode();
	    reasonType = FlagsEnum.ALL.getCode();
	    verbalOrderFlag = FlagsEnum.ALL.getCode();
	    fromDate = toDate = HijriDateService.getHijriSysDate();
	    newDecision = extensionDecision = terminationDecision = cancellationDecision = true;
	    moveDecision = subjoinDecision = officersAssignmentDecision = true;
	    employeeRegionDesc = "";
	    movementRegionDesc = "";
	    employeeRankId = FlagsEnum.ALL.getCode();
	    employeeRankDesc = "";
	    employeeMinorSpecDesc = "";
	    location = "";

	    empId = FlagsEnum.ALL.getCode();
	    empName = decisionNumber = null;
	    decisionDate = null;

	    resetMovementUnit();
	    resetEmployeeUnit();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetMovementUnit() {
	try {
	    UnitData movementUnit;
	    if (movementRegionId == FlagsEnum.ALL.getCode()) {
		movementUnit = null;
	    } else if (movementRegionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
		movementUnit = UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0);
	    } else {
		movementUnit = UnitsService.getUnitsByTypeAndRegion(UnitTypesEnum.REGION_COMMANDER.getCode(), movementRegionId).get(0);
	    }

	    movementUnitFullName = movementUnit == null ? null : movementUnit.getFullName();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void resetEmployeeUnit() {
	try {
	    UnitData employeeUnit;
	    if (employeeRegionId == FlagsEnum.ALL.getCode()) {
		employeeUnit = null;
	    } else if (employeeRegionId == RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode()) {
		employeeUnit = UnitsService.getUnitsByType(UnitTypesEnum.PRESIDENCY.getCode()).get(0);
	    } else {
		employeeUnit = UnitsService.getUnitsByTypeAndRegion(UnitTypesEnum.REGION_COMMANDER.getCode(), employeeRegionId).get(0);
	    }

	    employeeUnitFullName = employeeUnit == null ? null : employeeUnit.getFullName();
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void print() {
	try {
	    byte[] bytes = null;

	    for (Region region : regionList) {
		if (region.getId().longValue() == employeeRegionId) {
		    employeeRegionDesc = region.getDescription();
		    break;
		}
	    }

	    if (reportType != 5) {
		for (Region region : regionList) {
		    if (region.getId().longValue() == movementRegionId) {
			movementRegionDesc = region.getDescription();
			break;
		    }
		}

		for (Rank rank : ranks) {
		    if (rank.getId().longValue() == employeeRankId) {
			employeeRankDesc = rank.getDescription();
			break;
		    }
		}
	    }

	    if (reportType == 1 || reportType == 2) {
		bytes = MovementsService.getMoveReportsBytes(mode, movementUnitFullName, movementRegionDesc, employeeUnitFullName, employeeRegionDesc, employeeRankDesc, employeeMinorSpecDesc, fromDate, toDate, reasonType, reportType == 2 ? true : false);
	    } else if (reportType == 3 || reportType == 4) {
		bytes = MovementsService.getSubjoinReportsBytes(mode, movementUnitFullName, movementRegionDesc, employeeUnitFullName, employeeRegionDesc, employeeRankDesc, employeeMinorSpecDesc, fromDate, toDate, newDecision, extensionDecision, terminationDecision, cancellationDecision, reasonType, reportType == 4 ? true : false);
	    } else if (reportType == 5) {
		String movementTypesIds = "";
		if (moveDecision)
		    movementTypesIds += MovementTypesEnum.MOVE.getCode() + ",";
		if (subjoinDecision)
		    movementTypesIds += MovementTypesEnum.SUBJOIN.getCode() + ",";
		if (officersAssignmentDecision)
		    movementTypesIds += MovementTypesEnum.ASSIGNMENT.getCode() + ",";
		if (!movementTypesIds.isEmpty())
		    movementTypesIds = movementTypesIds.substring(0, movementTypesIds.length() - 1);

		bytes = MovementsService.getExternalMovementsReportsBytes(mode, location, employeeUnitFullName, employeeRegionDesc, fromDate, toDate, movementTypesIds);
	    } else if (reportType == 6) {
		MovementTransactionData movement = MovementsService.getMovementTransactionForJoiningReport(empId, decisionNumber, decisionDate);
		if (movement != null)
		    bytes = MovementsService.getJoiningDocumentBytes(movement.getId(), movement.getMovementTypeId());
		else
		    bytes = MovementsService.getJoiningDocumentBytes(FlagsEnum.ALL.getCode(), MovementTypesEnum.MOVE.getCode());

	    } else if (reportType == 7) {
		resetMovementUnit();
		resetEmployeeUnit();
		bytes = MovementsService.getReportOfSubjoinedEmployeesAccordingToTheirSubjoinEndDateBytes(movementUnitFullName, employeeUnitFullName, movementRegionDesc, employeeRegionDesc, fromDate, toDate);
	    } else if (reportType == 8) {
		bytes = MovementsService.getAssignmentReportsBytes(mode, movementUnitFullName, movementRegionDesc, employeeUnitFullName, employeeRegionDesc, employeeRankDesc, employeeMinorSpecDesc, fromDate, toDate, verbalOrderFlag);
	    }
	    print(bytes);
	} catch (BusinessException e) {
	    this.setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void setCancellationDecision(boolean cancellationDecision) {
	this.cancellationDecision = cancellationDecision;
    }

    public int getMode() {
	return mode;
    }

    public List<Region> getRegionList() {
	return regionList;
    }

    public void setRegionList(List<Region> regionList) {
	this.regionList = regionList;
    }

    public List<Rank> getRanks() {
	return ranks;
    }

    public void setRanks(List<Rank> ranks) {
	this.ranks = ranks;
    }

    public String getEmployeeUnitFullName() {
	return employeeUnitFullName;
    }

    public void setEmployeeUnitFullName(String employeeUnitFullName) {
	this.employeeUnitFullName = employeeUnitFullName;
    }

    public long getEmployeeRegionId() {
	return employeeRegionId;
    }

    public void setEmployeeRegionId(long employeeRegionId) {
	this.employeeRegionId = employeeRegionId;
    }

    public String getMovementUnitFullName() {
	return movementUnitFullName;
    }

    public void setMovementUnitFullName(String movementUnitFullName) {
	this.movementUnitFullName = movementUnitFullName;
    }

    public long getMovementRegionId() {
	return movementRegionId;
    }

    public void setMovementRegionId(long movementRegionId) {
	this.movementRegionId = movementRegionId;
    }

    public String getEmployeeMinorSpecDesc() {
	return employeeMinorSpecDesc;
    }

    public void setEmployeeMinorSpecDesc(String employeeMinorSpecDesc) {
	this.employeeMinorSpecDesc = employeeMinorSpecDesc;
    }

    public long getEmployeeRankId() {
	return employeeRankId;
    }

    public void setEmployeeRankId(long employeeRankId) {
	this.employeeRankId = employeeRankId;
    }

    public Date getFromDate() {
	return fromDate;
    }

    public void setFromDate(Date fromDate) {
	this.fromDate = fromDate;
    }

    public Date getToDate() {
	return toDate;
    }

    public void setToDate(Date toDate) {
	this.toDate = toDate;
    }

    public int getReasonType() {
	return reasonType;
    }

    public void setReasonType(int reasonType) {
	this.reasonType = reasonType;
    }

    public int getVerbalOrderFlag() {
	return verbalOrderFlag;
    }

    public void setVerbalOrderFlag(int verbalOrderFlag) {
	this.verbalOrderFlag = verbalOrderFlag;
    }

    public boolean isNewDecision() {
	return newDecision;
    }

    public void setNewDecision(boolean newDecision) {
	this.newDecision = newDecision;
    }

    public boolean isExtensionDecision() {
	return extensionDecision;
    }

    public void setExtensionDecision(boolean extensionDecision) {
	this.extensionDecision = extensionDecision;
    }

    public boolean isTerminationDecision() {
	return terminationDecision;
    }

    public void setTerminationDecision(boolean terminationDecision) {
	this.terminationDecision = terminationDecision;
    }

    public boolean isCancellationDecision() {
	return cancellationDecision;
    }

    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    public int getReportType() {
	return reportType;
    }

    public void setReportType(int reportType) {
	this.reportType = reportType;
    }

    public boolean isMoveDecision() {
	return moveDecision;
    }

    public void setMoveDecision(boolean moveDecision) {
	this.moveDecision = moveDecision;
    }

    public boolean isSubjoinDecision() {
	return subjoinDecision;
    }

    public void setSubjoinDecision(boolean subjoinDecision) {
	this.subjoinDecision = subjoinDecision;
    }

    public boolean isOfficersAssignmentDecision() {
	return officersAssignmentDecision;
    }

    public void setOfficersAssignmentDecision(boolean officersAssignmentDecision) {
	this.officersAssignmentDecision = officersAssignmentDecision;
    }

    public long getEmpId() {
	return empId;
    }

    public void setEmpId(long empId) {
	this.empId = empId;
    }

    public String getDecisionNumber() {
	return decisionNumber;
    }

    public void setDecisionNumber(String decisionNumber) {
	this.decisionNumber = decisionNumber;
    }

    public Date getDecisionDate() {
	return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
	this.decisionDate = decisionDate;
    }

    public String getEmpName() {
	return empName;
    }

    public void setEmpName(String empName) {
	this.empName = empName;
    }

}
