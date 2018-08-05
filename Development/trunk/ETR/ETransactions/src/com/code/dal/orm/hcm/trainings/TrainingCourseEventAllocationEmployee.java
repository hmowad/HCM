package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.code.dal.audit.DeletableAuditEntity;
import com.code.dal.audit.InsertableAuditEntity;
import com.code.dal.audit.UpdatableAuditEntity;
import com.code.dal.orm.AuditEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingAllocationEmps_countConflictCoursesEventsDates",
		query = "select count(courseEvent.id) " +
			" from TrainingCourseEventAllocationEmployee trainingCourseEventAllocationEmployee ,TrainingCourseEventAllocation trainingCourseEventAllocation,TrainingCourseEvent courseEvent" +
			" where courseEvent.id = trainingCourseEventAllocation.courseEventId " +
			" and (trainingCourseEventAllocation.id = trainingCourseEventAllocationEmployee.allocationId)" +
			" and (trainingCourseEventAllocationEmployee.empId = :P_EMPLOYEE_ID) " +
			" and (to_date(:P_ACTUAL_END_DATE, 'MI/MM/YYYY')>= courseEvent.actualStartDate" +
			" and to_date(:P_ACTUAL_START_DATE, 'MI/MM/YYYY')<=courseEvent.actualEndDate)" +
			" and (:P_EXCLUDED_IDS_FLAG = -1 or courseEvent.id not in (:P_EXCLUDED_IDS) )") })
@Entity
@Table(name = "HCM_TRN_CRS_EVENTS_ALLOC_EMPS")
public class TrainingCourseEventAllocationEmployee extends AuditEntity implements InsertableAuditEntity, UpdatableAuditEntity, DeletableAuditEntity {
    private Long id;
    private Long allocationId;
    private Long empId;

    @SequenceGenerator(name = "HCMTrainingPlanSeq",
	    sequenceName = "HCM_TRN_PLAN_SEQ")
    @Id
    @GeneratedValue(generator = "HCMTrainingPlanSeq")
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Basic
    @Column(name = "ALLOCATION_ID")
    public Long getAllocationId() {
	return allocationId;
    }

    public void setAllocationId(Long allocationId) {
	this.allocationId = allocationId;
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
    }

    @Override
    public Long calculateContentId() {
	return id;
    }

    @Override
    public String calculateContent() {
	return "allocationId:" + allocationId + AUDIT_SEPARATOR +
		"empId:" + empId;
    }
}
