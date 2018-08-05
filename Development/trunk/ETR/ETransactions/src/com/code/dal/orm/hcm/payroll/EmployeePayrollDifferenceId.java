package com.code.dal.orm.hcm.payroll;

import java.io.Serializable;

public class EmployeePayrollDifferenceId implements Serializable {

	private Long transactionType;
	private String regionCode;
	private Long transactionYear;
	private Long TransactionNo;

	public EmployeePayrollDifferenceId() {
	}

	public EmployeePayrollDifferenceId(Long transactionType, String regionCode,	Long transactionYear, Long TransactionNo) {
		this.transactionType = transactionType;
		this.regionCode = regionCode;
		this.transactionYear = transactionYear;
		this.TransactionNo = TransactionNo;
	}

	public Long getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(Long transactionType) {
		this.transactionType = transactionType;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public Long getTransactionYear() {
		return transactionYear;
	}

	public void setTransactionYear(Long transactionYear) {
		this.transactionYear = transactionYear;
	}

	public Long getTransactionNo() {
		return TransactionNo;
	}

	public void setTransactionNo(Long transactionNo) {
		TransactionNo = transactionNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((TransactionNo == null) ? 0 : TransactionNo.hashCode());
		result = prime * result + ((regionCode == null) ? 0 : regionCode.hashCode());
		result = prime * result	+ ((transactionType == null) ? 0 : transactionType.hashCode());
		result = prime * result	+ ((transactionYear == null) ? 0 : transactionYear.hashCode());
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
		if (TransactionNo == null) {
			if (other.TransactionNo != null)
				return false;
		} else if (!TransactionNo.equals(other.TransactionNo))
			return false;
		if (regionCode == null) {
			if (other.regionCode != null)
				return false;
		} else if (!regionCode.equals(other.regionCode))
			return false;
		if (transactionType == null) {
			if (other.transactionType != null)
				return false;
		} else if (!transactionType.equals(other.transactionType))
			return false;
		if (transactionYear == null) {
			if (other.transactionYear != null)
				return false;
		} else if (!transactionYear.equals(other.transactionYear))
			return false;
		return true;
	}
}
