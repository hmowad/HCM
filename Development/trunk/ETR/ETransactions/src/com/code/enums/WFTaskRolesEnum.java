package com.code.enums;

public enum WFTaskRolesEnum {
    REQUESTER("Requester"),
    EXCHANGE_EMP("ExchangeEmp"),

    DIRECT_MANAGER("DirectManager"),
    REPLACEMENT_DIRECT_MANAGER("ReplacementDirectManager"),

    MANAGER_REDIRECT("ManagerRedirect"),
    MANAGER_REDIRECT_TO_SECRUITY("ManagerRedirectToSecurity"),
    MANAGER_REDIRECT_TO_SPECIALIST("ManagerRedirectToSpecialist"),
    REVIEWER_EMP_REDIRECT("ReviewerEmpRedirect"),
    SECONDARY_MANAGER_REDIRECT("SecondaryManagerRedirect"),
    REPLACEMENT_SECONDARY_MANAGER_REDIRECT("ReplacementSecondaryManagerRedirect"),
    EXTRA_SECONDARY_MANAGER_REDIRECT("ExtraSecondaryManagerRedirect"),

    SECONDARY_REVIEWER_EMP("SecondaryReviewerEmp"),
    REPLACEMENT_SECONDARY_REVIEWER_EMP("ReplacementSecondaryReviewerEmp"),
    REVIEWER_EMP("ReviewerEmp"),
    EXTRA_SECONDARY_REVIEWER_EMP("ExtraSecondaryReviewerEmp"),

    SECONDARY_SIGN_MANAGER("SecondarySignManager"),
    SIGN_MANAGER("SignManager"),
    REPLACEMENT_SECONDARY_SIGN_MANAGER("ReplacementSecondarySignManager"),
    EXTRA_SECONDARY_SIGN_MANAGER("ExtraSecondarySignManager"),

    PLANNING_ORGANIZATION_MANAGER("PlanningOrganizationManager"),
    ADMINISTRATIVE_ORGANIZATION_MANAGER("AdminstrativeOrganizationManger"),
    PROMOTION_DEPARTMENT_MANAGER("PromotionDepartmentManager"),
    SECURITY_EMP("SecurityEmp"),
    MEDICAL_EMP("MedicalEmp"),

    NOTIFICATION("Notification"),
    HISTORY("History"),
    VIEWER("Viewer"),

    PREPARATOR("Preparator"),
    COMMITTEE_SECRETARY("CommitteeSecretary"),
    COMMITTEE_PRESIDENT("CommitteePresident"),
    COMMITTEE_MEMBER("CommitteeMember"),
    EXTRA_SIGN_MANAGER("ExtraSignManager");

    private String code;

    private WFTaskRolesEnum(String code) {
	this.code = code;
    }

    public String getCode() {
	return code;
    }
}
