package com.code.services.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.code.dal.orm.workflow.WFProcessGroup;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFProcessesGroupsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;

public class DashBoardWorkFlow extends BaseWorkFlow {

    private final static String[] ACCEPTANCE_ASSIGNEE_WF_ROLES = new String[] {
	    WFTaskRolesEnum.DIRECT_MANAGER.getCode()
    };

    private final static String[] APPROVAL_ASSIGNEE_WF_ROLES = new String[] {
	    WFTaskRolesEnum.SIGN_MANAGER.getCode(),
	    WFTaskRolesEnum.EXTRA_SIGN_MANAGER.getCode(),
	    WFTaskRolesEnum.SECONDARY_SIGN_MANAGER.getCode(),
	    WFTaskRolesEnum.EXTRA_SECONDARY_SIGN_MANAGER.getCode()
    };

    private final static Long[] ACCEPTANCE_PROCESS_GROUPS = new Long[] {
	    (long) WFProcessesGroupsEnum.VACATIONS.getCode(),
	    (long) WFProcessesGroupsEnum.MISSIONS.getCode(),
	    (long) WFProcessesGroupsEnum.TRAINING_AND_SCHOLARSHIP.getCode()
    };

    private final static Long[] APPROVAL_PROCESS_GROUPS = new Long[] {
	    (long) WFProcessesGroupsEnum.VACATIONS.getCode(),
	    (long) WFProcessesGroupsEnum.RECRUITMENTS.getCode(),
	    (long) WFProcessesGroupsEnum.MOVEMENTS.getCode(),
	    (long) WFProcessesGroupsEnum.MISSIONS.getCode(),
	    (long) WFProcessesGroupsEnum.PROMOTIONS.getCode(),
	    (long) WFProcessesGroupsEnum.TERMINATIONS.getCode(),
	    (long) WFProcessesGroupsEnum.TRAINING_AND_SCHOLARSHIP.getCode(),
	    (long) WFProcessesGroupsEnum.RETIREMENTS.getCode()
    };

    private final static Long[] EXCLUDED_PROCESS_IDS = new Long[] {
	    // Service Termination
	    WFProcessesEnum.SOLDIERS_TERMINATION_CANCELLATION.getCode(),
	    WFProcessesEnum.CIVILIANS_TERMINATION_CANCELLATION.getCode(),
	    WFProcessesEnum.CIVILIANS_TERMINATION_MOVEMENT.getCode(),

	    // Training
	    WFProcessesEnum.TRAINING_PLAN_INITIATION.getCode(),
	    WFProcessesEnum.TRAINING_PLAN_FINALIZATION.getCode(),
	    WFProcessesEnum.TRAINING_PLAN_APPROVAL.getCode(),
	    WFProcessesEnum.TRAINING_PLAN_NEEDS_REQUEST.getCode(),
	    WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_REQUEST.getCode(),
	    WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST.getCode(),
	    WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_POSTPONE_REQUEST.getCode(),
	    WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_CANCEL_REQUEST.getCode(),
	    WFProcessesEnum.MILITARY_INTERNAL_COURSE_EVENT_RESULTS.getCode(),
	    WFProcessesEnum.ADD_MILITARY_TRAINING_COURSE_NAME_REQUEST.getCode(),
	    WFProcessesEnum.ADD_CIVILLAIN_TRAINING_COURSE_NAME_REQUEST.getCode()
    };

    /**
     * private constructor to prevent instantiation
     */
    private DashBoardWorkFlow() {
    }

    /**
     * @param actionTypeFlag
     *            1 for Acceptance and 2 for Approval
     * @param employeeId
     *            the employee to get his dashboard data
     * @return a list of objects where each object contains the process group Id and name, and the tasks count for this process group
     * @throws BusinessException
     */
    public static List<Object> getDashBoardData(int actionTypeFlag, long employeeId) throws BusinessException {

	List<Object> dashBoardData = new ArrayList<Object>();
	String[] assigneeWfRoles = null;
	Long[] actionProcessesGroups = null;
	try {
	    if (actionTypeFlag == 1) {
		assigneeWfRoles = ACCEPTANCE_ASSIGNEE_WF_ROLES;
		actionProcessesGroups = ACCEPTANCE_PROCESS_GROUPS;
	    } else if (actionTypeFlag == 2) {
		assigneeWfRoles = APPROVAL_ASSIGNEE_WF_ROLES;
		actionProcessesGroups = APPROVAL_PROCESS_GROUPS;
	    } else {
		throw new BusinessException("error_general");
	    }

	    dashBoardData = getWFProcessesGroupsApprovalCounts(employeeId, assigneeWfRoles, actionProcessesGroups, EXCLUDED_PROCESS_IDS);

	    List<Long> actionProcessesGroupsList = Arrays.asList(actionProcessesGroups);
	    List<WFProcessGroup> processGroups = getWFProcessesGroups();

	    int i = 0;
	    for (WFProcessGroup processGroup : processGroups) {
		if (!actionProcessesGroupsList.contains(processGroup.getProcessGroupId()))
		    continue;
		else if (i >= dashBoardData.size() || !processGroup.getProcessGroupId().equals((Long) ((Object[]) dashBoardData.get(i))[0]))
		    dashBoardData.add(i, new Object[] { processGroup.getProcessGroupId(), processGroup.getProcessGroupName(), 0L });

		i++;
	    }
	} catch (BusinessException e) {
	    throw new BusinessException(getMessage(e.getMessage()));
	}
	return dashBoardData;
    }
}
