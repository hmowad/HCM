package com.code.dal.orm.hcm.payroll;

import java.io.Serializable;
import java.util.Date;

public class EmployeePayrollDifferenceId implements Serializable {

	private Long employeedId;
	private Long elementId;
	private String decesionNumber;
	private Date decesionDate;

	public EmployeePayrollDifferenceId() {
	}

	public EmployeePayrollDifferenceId(Long employeedId, Long elementId, String decesionNumber, Date decesionDate, Long summarySerial) {
		this.employeedId = employeedId;
		this.elementId = elementId;
		this.decesionNumber = decesionNumber;
		this.decesionDate = decesionDate;
	}

	public Long getEmployeedId() {
	    return employeedId;
	}

	public void setEmployeedId(Long employeedId) {
	    this.employeedId = employeedId;
	}

	public Long getElementId() {
	    return elementId;
	}

	public void setElementId(Long elementId) {
	    this.elementId = elementId;
	}

	public String getDecesionNumber() {
	    return decesionNumber;
	}

	public void setDecesionNumber(String decesionNumber) {
	    this.decesionNumber = decesionNumber;
	}

	public Date getDecesionDate() {
	    return decesionDate;
	}

	public void setDecesionDate(Date decesionDate) {
	    this.decesionDate = decesionDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((employeedId == null) ? 0 : employeedId.hashCode());
		result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
		result = prime * result	+ ((decesionNumber == null) ? 0 : decesionNumber.hashCode());
		result = prime * result	+ ((decesionDate == null) ? 0 : decesionDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayrollDifferenceId other = (EmployeePayrollDifferenceId) obj;
		if (employeedId == null) {
			if (other.employeedId != null)
				return false;
		} else if (!employeedId.equals(other.employeedId))
			return false;
		
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		
		if (decesionNumber == null) {
			if (other.decesionNumber != null)
				return false;
		} else if (!decesionNumber.equals(other.decesionNumber))
			return false;
		
		if (decesionDate == null) {
			if (other.decesionDate != null)
				return false;
		} else if (!decesionDate.equals(other.decesionDate))
			return false;
		
		return true;
	}
}
