package com.code.dal.orm.hcm.trainings;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.dal.orm.BaseEntity;

@NamedQueries({
	@NamedQuery(name = "hcm_trainingCourseEventAllocationEmpsData_searchTrainingCourseEventAllocationEmpData",
		query = " select a from TrainingCourseEventAllocationEmployeeData a " +
			" where (allocationId =:P_ALLOCATION_ID )")

})
@Entity
@Table(name = "HCM_VW_TRN_CRS_EVNTS_ALLOCEMPS")
public class TrainingCourseEventAllocationEmployeeData extends BaseEntity {

    private Long id;
    private Long allocationId;
    private Long empId;
    private String name;
    private String rankDesc;
    private String jobDesc;
    private String physicalUnitFullName;
    private TrainingCourseEventAllocationEmployee trainingCourseEventAllocationEmps;

    public TrainingCourseEventAllocationEmployeeData() {
	trainingCourseEventAllocationEmps = new TrainingCourseEventAllocationEmployee();
    }

    @Id
    @Column(name = "ID")
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
	this.trainingCourseEventAllocationEmps.setId(id);
    }

    @Basic
    @Column(name = "ALLOCATION_ID")
    public Long getAllocationId() {
	return allocationId;
    }

    public void setAllocationId(Long allocationId) {
	this.allocationId = allocationId;
	this.trainingCourseEventAllocationEmps.setAllocationId(allocationId);
    }

    @Basic
    @Column(name = "EMP_ID")
    public Long getEmpId() {
	return empId;
    }

    public void setEmpId(Long empId) {
	this.empId = empId;
	this.trainingCourseEventAllocationEmps.setEmpId(empId);
    }

    @Basic
    @Column(name = "NAME")
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Basic
    @Column(name = "RANK_DESC")
    public String getRankDesc() {
	return rankDesc;
    }

    public void setRankDesc(String rankDesc) {
	this.rankDesc = rankDesc;
    }

    @Basic
    @Column(name = "JOB_DESC")
    public String getJobDesc() {
	return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
	this.jobDesc = jobDesc;
    }

    @Basic
    @Column(name = "PHYSICAL_UNIT_FULL_NAME")
    public String getPhysicalUnitFullName() {
	return physicalUnitFullName;
    }

    public void setPhysicalUnitFullName(String physicalUnitFullName) {
	this.physicalUnitFullName = physicalUnitFullName;
    }

    @Transient
    public TrainingCourseEventAllocationEmployee getTrainingCourseEventAllocationEmployee() {
	return trainingCourseEventAllocationEmps;
    }

    public void setTrainingCourseEventAllocationEmps(TrainingCourseEventAllocationEmployee trainingCourseEventAllocationEmps) {
	this.trainingCourseEventAllocationEmps = trainingCourseEventAllocationEmps;
    }
}
