package com.code.ui.backings.hcm.employees;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.MedicalStaffLevel;
import com.code.dal.orm.hcm.MedicalStaffRank;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeDataExtraTransactionData;
import com.code.dal.orm.hcm.payroll.Degree;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PayrollsService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "medicalStaffData")
@ViewScoped
public class MedicalStaffData extends BaseBacking {
    private EmployeeData selectedEmployee;
    private List<EmployeeDataExtraTransactionData> employeeDataExtraTransactionDataList;
    private int pageSize = 10;
    private List<MedicalStaffRank> medicalStaffRanks;
    private List<MedicalStaffLevel> medicalStaffLevels;
    private List<Degree> medicalStaffDegrees;

    public MedicalStaffData() {
	try {
	    selectedEmployee = new EmployeeData();
	    employeeDataExtraTransactionDataList = new ArrayList<EmployeeDataExtraTransactionData>();
	    medicalStaffRanks = CommonService.getAllMedicalStaffRanks();
	    medicalStaffLevels = CommonService.getAllMedicalStaffLevels();
	    medicalStaffDegrees = PayrollsService.getAllDegrees();
	} catch (Exception e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void getEmployeeExtraData() {
	try {
	    selectedEmployee = EmployeesService.getEmployeeData(selectedEmployee.getEmpId());
	    EmployeesService.validateSelectedEmployeeForExtraTransaction(selectedEmployee);
	    employeeDataExtraTransactionDataList = new ArrayList<EmployeeDataExtraTransactionData>();
	    EmployeesService.getMedicalStaffExtraTransactionDataList(selectedEmployee.getEmpId(), employeeDataExtraTransactionDataList);
	} catch (BusinessException e) {
	    selectedEmployee = new EmployeeData();
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addEmployeeExtraData() {
	EmployeeDataExtraTransactionData addedEmployeeDataExtraTransactionData = new EmployeeDataExtraTransactionData();
	employeeDataExtraTransactionDataList.add(0, addedEmployeeDataExtraTransactionData);
    }

    public void saveEmployeeExtraData(EmployeeDataExtraTransactionData addedEmployeeDataExtraTransactionData, int index) {
	try {
	    EmployeesService.addEmployeeDataExtraTransaction(selectedEmployee, addedEmployeeDataExtraTransactionData);
	    employeeDataExtraTransactionDataList.set(index, EmployeesService.getEmployeeDataExtraTransactionByDecisionNumber(addedEmployeeDataExtraTransactionData.getDecisionNumber()).get(0));
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public EmployeeData getSelectedEmployee() {
	return selectedEmployee;
    }

    public void setSelectedEmployee(EmployeeData selectedEmployee) {
	this.selectedEmployee = selectedEmployee;
    }

    public List<EmployeeDataExtraTransactionData> getEmployeeDataExtraTransactionDataList() {
	return employeeDataExtraTransactionDataList;
    }

    public void setEmployeeDataExtraTransactionDataList(List<EmployeeDataExtraTransactionData> employeeDataExtraTransactionDataList) {
	this.employeeDataExtraTransactionDataList = employeeDataExtraTransactionDataList;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public List<MedicalStaffRank> getMedicalStaffRanks() {
	return medicalStaffRanks;
    }

    public void setMedicalStaffRanks(List<MedicalStaffRank> medicalStaffRanks) {
	this.medicalStaffRanks = medicalStaffRanks;
    }

    public List<MedicalStaffLevel> getMedicalStaffLevels() {
	return medicalStaffLevels;
    }

    public void setMedicalStaffLevels(List<MedicalStaffLevel> medicalStaffLevels) {
	this.medicalStaffLevels = medicalStaffLevels;
    }

    public List<Degree> getMedicalStaffDegrees() {
	return medicalStaffDegrees;
    }

    public void setMedicalStaffDegrees(List<Degree> medicalStaffDegrees) {
	this.medicalStaffDegrees = medicalStaffDegrees;
    }

}
