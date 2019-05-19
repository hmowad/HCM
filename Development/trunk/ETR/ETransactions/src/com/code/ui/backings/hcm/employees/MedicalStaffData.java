package com.code.ui.backings.hcm.employees;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeExtraTransactionData;
import com.code.dal.orm.hcm.employees.medicalstuff.EmployeeMedicalStaffData;
import com.code.dal.orm.hcm.employees.medicalstuff.MedicalStaffLevel;
import com.code.dal.orm.hcm.employees.medicalstuff.MedicalStaffRank;
import com.code.dal.orm.hcm.payroll.Degree;
import com.code.enums.TransactionClassesEnum;
import com.code.enums.TransactionTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PayrollsService;
import com.code.services.util.CommonService;
import com.code.ui.backings.base.BaseBacking;

@ManagedBean(name = "medicalStaffData")
@ViewScoped
public class MedicalStaffData extends BaseBacking {
    private EmployeeData selectedEmployee;
    private List<EmployeeExtraTransactionData> employeeExtraTransactionDataList;
    private int pageSize = 10;
    private List<MedicalStaffRank> medicalStaffRanks;
    private List<MedicalStaffLevel> medicalStaffLevels;
    private List<Degree> medicalStaffDegrees;

    public MedicalStaffData() {
	try {
	    selectedEmployee = new EmployeeData();
	    employeeExtraTransactionDataList = new ArrayList<EmployeeExtraTransactionData>();
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
	    employeeExtraTransactionDataList = new ArrayList<EmployeeExtraTransactionData>();
	    employeeExtraTransactionDataList = EmployeesService.getMedicalStaffExtraTransactionDataList(selectedEmployee.getEmpId());
	} catch (BusinessException e) {
	    selectedEmployee = new EmployeeData();
	    setServerSideErrorMessages(getMessage(e.getMessage()));
	}
    }

    public void addEmployeeExtraData() {
	EmployeeExtraTransactionData addedEmployeeExtraTransactionData = new EmployeeExtraTransactionData();
	addedEmployeeExtraTransactionData.setEmpId(selectedEmployee.getEmpId());
	employeeExtraTransactionDataList.add(0, addedEmployeeExtraTransactionData);
    }

    public void saveEmployeeExtraData(EmployeeExtraTransactionData addedEmployeeExtraTransactionData, int index) {
	try {
	    EmployeeMedicalStaffData employeeMedicalStaffData = new EmployeeMedicalStaffData();
	    EmployeesService.constructEmployeeMedicalStaffData(addedEmployeeExtraTransactionData, employeeMedicalStaffData);
	    addedEmployeeExtraTransactionData.setTransactionTypeId(CommonService.getTransactionTypeByCodeAndClass(TransactionTypesEnum.EMPLOYEE_MEDICAL_STAFF_DATA.getCode(), TransactionClassesEnum.EMPLOYEES.getCode()).getId());
	    EmployeesService.addEmployeeDataExtraTransaction(selectedEmployee, addedEmployeeExtraTransactionData, employeeMedicalStaffData);
	    employeeExtraTransactionDataList.set(index, addedEmployeeExtraTransactionData);
	    setServerSideSuccessMessages(getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    setServerSideErrorMessages(getParameterizedMessage(e.getMessage(), e.getParams()));
	}
    }

    public EmployeeData getSelectedEmployee() {
	return selectedEmployee;
    }

    public void setSelectedEmployee(EmployeeData selectedEmployee) {
	this.selectedEmployee = selectedEmployee;
    }

    public List<EmployeeExtraTransactionData> getEmployeeExtraTransactionDataList() {
	return employeeExtraTransactionDataList;
    }

    public void setEmployeeExtraTransactionDataList(List<EmployeeExtraTransactionData> employeeExtraTransactionDataList) {
	this.employeeExtraTransactionDataList = employeeExtraTransactionDataList;
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
