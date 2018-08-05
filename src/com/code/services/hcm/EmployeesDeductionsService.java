package com.code.services.hcm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.movements.MovementTransaction;
import com.code.dal.orm.hcm.recruitments.RecruitmentTransaction;
import com.code.dal.orm.hcm.terminations.TerminationTransaction;
import com.code.enums.EmployeesDeductionsEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.util.HijriDateService;

public class EmployeesDeductionsService {

    /**
     * Constructs a newly allocated <code>EmployeesDeductionsService</code> object.
     */
    private EmployeesDeductionsService() {
    }

    /**
     * Calculates the employee deducted days.
     * 
     * @param empId
     *            the employee ID to calculate his deducted days
     * @param fromDate
     *            the <code>Date</code> to start the calculation from it, in mm/MM/yyyy format
     * @param toDate
     *            the <code>Date</code> to end the calculation at it, in mm/MM/yyyy format
     * @return an integer containing the deducted days number
     * @throws DatabaseException
     *             if a database exception occurs
     */
    protected static int deductedDays(EmployeeData employeeData, Date fromDate, Date toDate) throws BusinessException {
	int deductedDays = 0;
	int deducted = 0; // XDay
	int deductedCut = 0; // XDay1

	List<Object[]> allTransactions = getEmployeeInOutTransactions(employeeData.getEmpId(), fromDate, toDate);
	if (!allTransactions.isEmpty()) {
	    int i = 0;
	    if (((Integer) allTransactions.get(0)[0]).equals(EmployeesDeductionsEnum.IN.getCode())) {
		deducted = HijriDateService.gregDateDiff(fromDate, (Date) allTransactions.get(0)[1]);
		i++;
	    }

	    for (; i < allTransactions.size(); i++) {
		if (((Integer) allTransactions.get(i)[0]).equals(EmployeesDeductionsEnum.IN.getCode()))
		    deductedCut += HijriDateService.gregDateDiff((Date) allTransactions.get(i)[1], toDate);
		else
		    deducted += HijriDateService.gregDateDiff((Date) allTransactions.get(i)[1], toDate);
	    }

	    deductedDays = Math.abs(deducted - deductedCut);
	}

	return deductedDays;
    }

    private static List<Object[]> getEmployeeInOutTransactions(long employeeId, Date fromDate, Date toDate) throws BusinessException {

	List<RecruitmentTransaction> recruitmentTransactions = RecruitmentsService.getRecruitmentTransactionsByEmployeeIdAndRecDate(employeeId, fromDate, toDate);
	List<TerminationTransaction> terminationTransactions = TerminationsService.getEmployeeTerminationTransactions(employeeId, fromDate, toDate);
	List<MovementTransaction> movementTransactions = MovementsService.getExternalMovementTransactions(employeeId, fromDate, toDate);

	List<Object[]> allTransactions = new ArrayList<Object[]>();

	for (RecruitmentTransaction recruitmentTransaction : recruitmentTransactions) {
	    Object[] objectArray = new Object[2];
	    objectArray[0] = EmployeesDeductionsEnum.IN.getCode();
	    objectArray[1] = recruitmentTransaction.getRecruitmentDate();

	    allTransactions.add(objectArray);
	}

	for (TerminationTransaction terminationTransaction : terminationTransactions) {
	    Object[] objectArray = new Object[2];
	    objectArray[0] = EmployeesDeductionsEnum.OUT.getCode();
	    objectArray[1] = terminationTransaction.getServiceTerminationDate();

	    allTransactions.add(objectArray);
	}

	for (MovementTransaction movementTransaction : movementTransactions) {
	    Object[] objectArray = new Object[2];
	    objectArray[0] = EmployeesDeductionsEnum.OUT.getCode();
	    objectArray[1] = movementTransaction.getExecutionDate();

	    allTransactions.add(objectArray);
	}

	Collections.sort(allTransactions, new Comparator<Object[]>() {
	    @Override
	    public int compare(Object[] object1, Object[] object2) {
		return ((Date) object1[1]).compareTo((Date) object2[1]);
	    }
	});

	return allTransactions;
    }

}
