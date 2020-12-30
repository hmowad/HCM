package com.code.services.workflow.hcm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.promotions.PromotionReportData;
import com.code.dal.orm.hcm.promotions.PromotionReportDetailData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFPosition;
import com.code.dal.orm.workflow.WFTask;
import com.code.dal.orm.workflow.hcm.promotions.WFPromotion;
import com.code.enums.CategoriesEnum;
import com.code.enums.PromotionCandidateStatusEnum;
import com.code.enums.PromotionReportStatusEnum;
import com.code.enums.PromotionsTypesEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.RanksEnum;
import com.code.enums.RegionsEnum;
import com.code.enums.UnitTypesEnum;
import com.code.enums.WFActionFlagsEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFPositionsEnum;
import com.code.enums.WFTaskActionsEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.PromotionsService;
import com.code.services.util.CommonService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

/**
 * 
 * This class consists exclusively of static methods that operate on or return promotion work-flows.
 * 
 * Soldiers (exceptional and regular) promotion Work flow and Civilians (regular) promotion Work flow.
 * 
 * <p>
 * The methods of this class all throw a <tt>BusinessException</tt> if any problem occurred in sessionScope
 * 
 * <p>
 */
public class PromotionsWorkFlow extends BaseWorkFlow {

    /**
     * Private constructor to prevent instantiation
     */
    private PromotionsWorkFlow() {
    }

    /**
     * Initialize the promotion work-flow instance and do the following
     * 
     * <ul>
     * <li>Validate on promotion request data</li>
     * <li>Creates a work-flow instance for the process</li>
     * <li>Creates a work-flow task for the process</li>
     * </ul>
     * 
     * @param requester
     *            Employee that started the promotion request
     * @param promotionReportData
     *            Promotion report data
     * @param promotionReportDetails
     *            List of promotion report detail data that will be promoted
     * @param processId
     *            Process Id , it could be soldiers regular promotion, soldiers exceptional promotion , or civilians promotion
     * @param attachments
     *            Attachments sent with request
     * @param taskUrl
     *            URL of the work-flow tasks
     * @param loginEmpId
     *            Login employee id to enable auditing
     * @throws BusinessException
     *             if any exceptions or errors occurs
     * 
     * @see #addWFInstance(long, long, Date, Date, int, String, CustomSession)
     */
    public static void initPromotion(EmployeeData requester, PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetails, long processId, String taskUrl, List<EmployeeData> internalCopiesEmployees) throws BusinessException {

	PromotionsService.validatePromotionReport(promotionReportData, promotionReportDetails, promotionReportData.getId());

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    WFInstance instance = addWFInstance(processId, requester.getEmpId(), curDate, curHijriDate, WFInstanceStatusEnum.RUNNING.getCode(), null, getPromotionsReportsInstanceBeneficiariesIds(promotionReportDetails), session);

	    if (promotionReportData.getCategoryId().longValue() == CategoriesEnum.SOLDIERS.getCode() && promotionReportData.getScaleUpFlagBoolean()) {
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.PROMOTION_DEPARTMENT_MANAGER.getCode(), "1", session);
	    } else
		addWFTask(instance.getInstanceId(), getDelegate(requester.getManagerId(), processId, requester.getEmpId()), requester.getManagerId(), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);

	    promotionReportData.setInternalCopies(EmployeesService.getEmployeesIdsString(internalCopiesEmployees));
	    promotionReportData.setStatus(PromotionReportStatusEnum.UNDER_APPROVAL.getCode());
	    PromotionsService.addModifyPromotionReport(promotionReportData, requester.getEmpId(), session);

	    if (promotionReportData.getPromotionTypeId().longValue() == PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode()
		    || promotionReportData.getPromotionTypeId().longValue() == PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode())
		PromotionsService.addModifyPromotionReportDetails(promotionReportData.getId(), promotionReportDetails, requester.getEmpId(), session);

	    addWFPromotion(promotionReportData.getId(), instance.getInstanceId(), session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    if (promotionReportData.getPromotionTypeId().longValue() == PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode()) {
		promotionReportData.setId(null);
		for (PromotionReportDetailData reportDetail : promotionReportDetails) {
		    reportDetail.setId(null);
		    reportDetail.setReportId(null);
		}
	    }

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    /**
     * Handles actions and reviews of reviewer employee and apply reviewer's changes in promotion data
     * 
     * @param requester
     *            Employee that started promotion process
     * @param report
     *            Promotion report data
     * @param promotionReportDetails
     *            List of promotion report detail data that will be promoted
     * @param instance
     *            Current promotion instance
     * @param reTask
     *            The reviewer employee task data
     * @param reviewerEmpId
     *            The reviewer employee id
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * @see WFInstanceStatusEnum
     */
    public static void doPromotionRE(EmployeeData requester, PromotionReportData report, List<PromotionReportDetailData> promotionReportDetails, WFInstance instance, WFTask reTask, List<EmployeeData> internalCopiesEmployees, boolean isApproved) throws BusinessException {
	if (isApproved)
	    PromotionsService.validatePromotionReport(report, promotionReportDetails, report.getId());

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    if (isApproved) {
		Date curDate = new Date();
		Date curHijriDate = HijriDateService.getHijriSysDate();

		if (report.getCategoryId() == CategoriesEnum.SOLDIERS.getCode() && report.getScaleUpFlagBoolean()) {
		    completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requester.getManagerId(), instance.getProcessId(), requester.getEmpId()), requester.getManagerId(), reTask.getTaskUrl(), WFTaskRolesEnum.PROMOTION_DEPARTMENT_MANAGER.getCode(), "1", session);
		} else
		    completeWFTask(reTask, WFTaskActionsEnum.REVIEW.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requester.getManagerId(), instance.getProcessId(), requester.getEmpId()), requester.getManagerId(), reTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);

		// set internal copies and change report status from CURRENT to UNDER_APPROVAL
		report.setInternalCopies(EmployeesService.getEmployeesIdsString(internalCopiesEmployees));
		PromotionsService.addModifyPromotionReport(report, reTask.getAssigneeId(), session);

		// TODO: revise that adding if condition to restrict the call to service to case of exceptional only
		if (report.getPromotionTypeId().longValue() == PromotionsTypesEnum.EXCEPTIONAL_PROMOTION.getCode() || report.getPromotionTypeId().longValue() == PromotionsTypesEnum.PROMOTION_CANCELLATION.getCode())
		    PromotionsService.addModifyPromotionReportDetails(report.getId(), promotionReportDetails, reTask.getAssigneeId(), session);

	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, reTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
		PromotionsService.updatePromotionReportStatus(report, PromotionReportStatusEnum.CLOSED.getCode(), session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    /**
     * This method is used to review soldiers promotion only by promotion department.
     * 
     * It is exclusively used for scalup promotion.
     * 
     * @param requester
     *            Employee that started the promotion process
     * @param instance
     *            Current promotion instance
     * @param pdmTask
     *            Promotion department manager task data
     * @param approvalFlag
     *            Flag to determine if request is approved, rejected, or returned to reviewer
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see WFInstanceStatusEnum
     */
    public static void doPromotionPDM(EmployeeData requester, WFInstance instance, WFTask pdmTask, int approvalFlag) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		WFPosition position = getWFPosition(WFPositionsEnum.HUMAN_RESOURCES_ORGANIZATION_UNIT_MANAGER.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
		EmployeeData adminOrganizationManager = EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId());
		completeWFTask(pdmTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(adminOrganizationManager.getEmpId(), instance.getProcessId(), requester.getEmpId()), adminOrganizationManager.getEmpId(), pdmTask.getTaskUrl(), WFTaskRolesEnum.ADMINISTRATIVE_ORGANIZATION_MANAGER.getCode(), "1", session);
	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		completeWFTask(pdmTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requester.getEmpId(), instance.getProcessId(), requester.getEmpId()), requester.getEmpId(), pdmTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1");
	    }
	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    if (e instanceof BusinessException)
		throw (BusinessException) e;

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    /**
     * This method is used to review soldiers promotion only by organization department.
     * 
     * It is exclusively used for scalup promotion.
     * 
     * @param requester
     *            Employee that started the promotion process
     * @param report
     *            Promotion report data
     * @param reportDetails
     *            List of promotion report detail data that will be promoted
     * @param instance
     *            Current promotion instance
     * @param aomTask
     *            Administration organization manager task data
     * @param approvalFlag
     *            Flag to determine if request is approved, rejected, or returned to reviewer
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see WFInstanceStatusEnum
     */
    public static void doPromotionAOM(EmployeeData requester, PromotionReportData report, WFInstance instance, WFTask aomTask, int approvalFlag) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		EmployeeData curDM = EmployeesService.getEmployeeData(aomTask.getOriginalId());
		if (curDM.getUnitTypeCode().intValue() == UnitTypesEnum.GENERAL_DEPARTMENT.getCode()) {
		    EmployeeData requesterManager = EmployeesService.getEmployeeData(requester.getManagerId());
		    completeWFTask(aomTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requesterManager.getManagerId(), instance.getProcessId(), requester.getEmpId()), requesterManager.getManagerId(), aomTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		} else {
		    completeWFTask(aomTask, WFTaskActionsEnum.APPROVE.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), aomTask.getTaskUrl(), WFTaskRolesEnum.ADMINISTRATIVE_ORGANIZATION_MANAGER.getCode(), "1", session);
		}
	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		completeWFTask(aomTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requester.getEmpId(), instance.getProcessId(), requester.getEmpId()), requester.getEmpId(), aomTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1");
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    session.rollbackTransaction();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    /**
     * Handles actions of sign manager for the promotion report such as :
     * <ul>
     * <li>Approval of the report send it to next sign manager</li>
     * <li>Return back the report to reviewer employee</li>
     * <li>The rejection of the report close it</li>
     * </ul>
     * 
     * @param requester
     *            Employee that started promotion process
     * @param report
     *            Promotion report data
     * @param reportDetails
     *            List of promotion report detail data that will be promoted
     * @param instance
     *            Current promotion instance
     * @param smTask
     *            Sign manager task data
     * @param approvalFlag
     *            Flag to determine if request is approved, rejected, or returned to reviewer
     * @param processId
     *            Process Id
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see WFInstanceStatusEnum
     */
    public static void doPromotionSM(EmployeeData requester, PromotionReportData report, WFInstance instance, WFTask smTask, int approvalFlag) throws BusinessException {

	List<PromotionReportDetailData> reportDetails = PromotionsService.getPromotionReportDetailsDataByReportId(report.getId());
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    if (approvalFlag == WFActionFlagsEnum.APPROVE.getCode()) {
		EmployeeData curDM = EmployeesService.getEmployeeData(smTask.getOriginalId());

		if (curDM.getUnitTypeCode().intValue() == UnitTypesEnum.PRESIDENCY.getCode() || (curDM.getUnitTypeCode().intValue() >= UnitTypesEnum.ASSISTANT.getCode() && report.getCategoryId() == CategoriesEnum.PERSONS.getCode() && report.getRankId() > RanksEnum.TENTH.getCode())) {
		    closePromotionWorkFlow(requester, instance, report, reportDetails, smTask, session);
		} else {
		    if (EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getUnitTypeCode().intValue() >= UnitTypesEnum.PRESIDENCY.getCode()) {
			if (report.getCategoryId() == CategoriesEnum.PERSONS.getCode()) {
			    for (PromotionReportDetailData reportDetail : reportDetails) {
				if (reportDetail.getNewRankId().longValue() == RanksEnum.TENTH.getCode() || reportDetail.getNewRankId().longValue() <= RanksEnum.FOURTEENTH.getCode()) {
				    curDM.setManagerId(EmployeesService.getEmployeeDirectManager(curDM.getEmpId()).getEmpId());
				    break;
				} else if (reportDetail.getNewRankId().longValue() == RanksEnum.ELEVENTH.getCode() || reportDetail.getNewRankId().longValue() == RanksEnum.TWELFTH.getCode() || reportDetail.getNewRankId().longValue() == RanksEnum.THIRTEENTH.getCode()) {
				    curDM.setManagerId(getVicePresidencyId());
				    break;
				}
			    }
			}
		    }
		    completeWFTask(smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(curDM.getManagerId(), instance.getProcessId(), requester.getEmpId()), curDM.getManagerId(), smTask.getTaskUrl(), WFTaskRolesEnum.SIGN_MANAGER.getCode(), "1", session);
		}

	    } else if (approvalFlag == WFActionFlagsEnum.RETURN_REVIEWER.getCode()) {
		completeWFTask(smTask, WFTaskActionsEnum.RETURN_REVIEWER.getCode(), curDate, curHijriDate, instance.getInstanceId(), getDelegate(requester.getEmpId(), instance.getProcessId(), requester.getEmpId()), requester.getEmpId(), smTask.getTaskUrl(), WFTaskRolesEnum.REVIEWER_EMP.getCode(), "1");
	    } else {
		closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.REJECT.getCode(), null, session);
		PromotionsService.updatePromotionReportStatus(report, PromotionReportStatusEnum.CLOSED.getCode(), session);
	    }

	    session.commitTransaction();
	} catch (BusinessException e) {
	    session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    e.printStackTrace();
	    session.rollbackTransaction();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    // -------------------------------------------------------------------------------------------------------------

    /**
     * Close the promotion workflow after the last sign manager approve the request, it do the following :
     * 
     * <ul>
     * <li>Closes the workflow</li>
     * <li>Do the promotion integration between the promotion report data and the promotion transaction data</li>
     * <li>Send notifications to copies</li>
     * </ul>
     * 
     * @param requester
     *            Employee that started promotion process
     * @param instance
     *            Current promotion instance
     * @param report
     *            Promotion report data
     * @param reportDetails
     *            List of promotion report detail data that will be promoted
     * @param smTask
     *            Current sign manager task being handled
     * @param session
     *            Parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * @throws DatabaseException
     *             If any database exceptions or errors occurs
     * 
     * @see WFInstanceStatusEnum
     */
    private static void closePromotionWorkFlow(EmployeeData requester, WFInstance instance, PromotionReportData promotion, List<PromotionReportDetailData> promotionDetails, WFTask smTask, CustomSession session) throws BusinessException, DatabaseException {

	PromotionsService.doPromotionTransactionEffect(promotion, promotionDetails, getWFProcess(instance.getProcessId()).getProcessName(), smTask.getOriginalId(), null, session);
	PromotionsService.doPromotionReportAndDetailsEffect(promotion, promotionDetails, session);

	List<Long> categoriesIds = new ArrayList<Long>();
	List<Long> beneficairyEmployeesIds = new ArrayList<Long>();
	List<Long> additionalIds = new ArrayList<Long>();

	categoriesIds.add(CommonService.getRankById(promotion.getRankId()).getCategoryId());

	for (PromotionReportDetailData promotionReportDetailData : promotionDetails) {
	    if (promotionReportDetailData.getStatus().equals(PromotionCandidateStatusEnum.PROMOTED.getCode())) {
		beneficairyEmployeesIds.add(promotionReportDetailData.getEmpId());
		additionalIds.add(promotionReportDetailData.getEmpId());
	    }
	}

	additionalIds.addAll(splitStringToLongList(promotion.getInternalCopies()));

	Long[] notificationsEmpsIds = computeInstanceNotifications(categoriesIds, 1, instance.getProcessId(), beneficairyEmployeesIds, additionalIds);
	closeWFInstanceByAction(requester.getEmpId(), instance, smTask, WFTaskActionsEnum.SUPER_SIGN.getCode(), notificationsEmpsIds, session);
    }

    public static void handlePromotionsInvalidation(Long[] instancesIds, CustomSession session) throws BusinessException {
	try {
	    List<Long> promotionReportsIds = getPromotionsReportsIdsByInstancesIds(instancesIds);
	    Long[] reportsIds = new Long[promotionReportsIds.size()];
	    reportsIds = promotionReportsIds.isEmpty() ? null : promotionReportsIds.toArray(reportsIds);

	    PromotionsService.closePromotionReports(reportsIds, session);

	} catch (BusinessException e) {
	    e.printStackTrace();
	    throw e;
	}
    }

    /**
     * Get vice presidency id by vice president position
     * 
     * @return Vice president id
     * 
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see WFPositionsEnum
     */
    private static long getVicePresidencyId() throws BusinessException {
	WFPosition position = getWFPosition(WFPositionsEnum.VICE_PRESIDENT.getCode(), RegionsEnum.GENERAL_DIRECTORATE_OF_BORDER_GUARDS.getCode());
	return EmployeesService.getEmployeeByPosition(position.getUnitId(), position.getEmpId()).getEmpId();
    }

    /**
     * Add workflow promotion
     * 
     * @param reportId
     *            Promotion report id
     * @param promotionDetails
     *            List of promotion report detail data
     * @param instanceId
     *            Promotion Instance Id
     * @param useSession
     *            Optional parameter used to access the database if no other session opened
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see PromotionReportStatusEnum
     */
    private static void addWFPromotion(Long reportId, long instanceId, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();

	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    WFPromotion wfPromotion = new WFPromotion();
	    wfPromotion.setInstanceId(instanceId);
	    wfPromotion.setReportId(reportId);
	    DataAccess.addEntity(wfPromotion, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    /**
     * Get the promotion workflow by specified instance
     * 
     * @return The promotion workflow using the instance id
     * @param instanceId
     *            Instance id
     * @throws BusinessException
     *             If any exceptions or errors occurs
     */
    public static WFPromotion getWFPromotionByInstanceId(long instanceId) throws BusinessException {

	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCE_ID", instanceId);
	    List<WFPromotion> result = DataAccess.executeNamedQuery(WFPromotion.class, QueryNamesEnum.WF_GET_WFPROMOTION_BY_INSTANCE_ID.getCode(), qParams);
	    if (result.isEmpty())
		return null;
	    return result.get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /**
     * Get the workflow promotion tasks using task assignee employee and assignee role.
     * 
     * @param assigneeId
     *            Assignee employee id
     * @param assigneeWfRole
     *            Assignee employee workflow role
     * @return List of workflow promotion tasks
     * @throws BusinessException
     *             If any exceptions or errors occurs
     * 
     * @see WFTaskRolesEnum
     */
    public static List<Object> getWFpromotionsTasks(Long assigneeId, String assigneeWfRole) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_ASSIGNEE_ID", assigneeId);
	    qParams.put("P_ASSIGNEE_WF_ROLE", assigneeWfRole);
	    return DataAccess.executeNamedQuery(Object.class, QueryNamesEnum.WF_GET_WFPROMOTIONS_TASKS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static List<Long> getPromotionsReportsIdsByInstancesIds(Long[] instancesIds) throws BusinessException {

	if (instancesIds == null || instancesIds.length == 0)
	    return new ArrayList<Long>();
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_INSTANCES_IDS", instancesIds);
	    return DataAccess.executeNamedQuery(Long.class, QueryNamesEnum.WF_GET_PROMOTIONS_REPORTS_IDS_BY_INSTANCES_IDS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    /********************************** Beneficiaries Operations **********************************/
    public static void enrollSoldierPromotionReportDetails(WFInstance instance, Integer enrollNumber, PromotionReportData promotionReportData, List<PromotionReportDetailData> promotionReportDetailDataList, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();
	    PromotionsService.enrollSoldierPromotionReportDetails(enrollNumber, promotionReportData, promotionReportDetailDataList, loginEmpId, session);
	    if (instance != null) {
		List<Long> instanceBeneficiariesIds = getPromotionsReportsInstanceBeneficiariesIds(promotionReportDetailDataList);
		if (instanceBeneficiariesIds != null && !instanceBeneficiariesIds.isEmpty())
		    updateWFInstanceBeneficiaries(instance.getInstanceId(), instanceBeneficiariesIds, session);
	    }
	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}

    }

    public static PromotionReportDetailData addPromotionReportDetail(WFInstance instance, long empId, PromotionReportData promotionReportData, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    List<PromotionReportDetailData> promotionReportDetailDataListBeneficiaries = new ArrayList<PromotionReportDetailData>();
	    promotionReportDetailDataListBeneficiaries.add(PromotionsService.addPromotionReportDetail(empId, promotionReportData, loginEmpId, session));

	    if (instance != null && promotionReportData.getCategoryId().equals(CategoriesEnum.PERSONS.getCode()))
		addWFInstanceBeneficiaries(instance.getInstanceId(), getPromotionsReportsInstanceBeneficiariesIds(promotionReportDetailDataListBeneficiaries), session);

	    if (!isOpenedSession)
		session.commitTransaction();
	    return promotionReportDetailDataListBeneficiaries.get(0);
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void savePromotionReportDetail(WFInstance instance, Long promotionReportId, PromotionReportDetailData promotionReportDetailData, Long categoryId, Long oldPromotionReportDetailStatus, Long newPromotionReportDetailStatus, long loginEmpId, CustomSession... useSession) throws BusinessException {
	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (instance != null) {
		// Here we are NOT using getPromotionsReportsInstanceBeneficiariesIds(), as we are saving a detail to be uncandidate and using this method will return an empty list
		List<Long> instanceBeneficiariesIds = new ArrayList<Long>();
		instanceBeneficiariesIds.add(promotionReportDetailData.getEmpId());
		if ((oldPromotionReportDetailStatus.equals(PromotionCandidateStatusEnum.CANDIDATE.getCode()) || oldPromotionReportDetailStatus.equals(PromotionCandidateStatusEnum.CANDIDATE_SEQUENTIALLY.getCode()) || oldPromotionReportDetailStatus.equals(PromotionCandidateStatusEnum.PRECEDENTED_POINTS.getCode())) && newPromotionReportDetailStatus.equals(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode()))
		    deleteWFInstanceBeneficiaries(instance.getInstanceId(), instanceBeneficiariesIds, session);
		else if (oldPromotionReportDetailStatus.equals(PromotionCandidateStatusEnum.NON_CANDIDATE.getCode()) && (newPromotionReportDetailStatus.equals(PromotionCandidateStatusEnum.CANDIDATE.getCode()) || newPromotionReportDetailStatus.equals(PromotionCandidateStatusEnum.CANDIDATE_SEQUENTIALLY.getCode()) || newPromotionReportDetailStatus.equals(PromotionCandidateStatusEnum.PRECEDENTED_POINTS.getCode())))
		    addWFInstanceBeneficiaries(instance.getInstanceId(), instanceBeneficiariesIds, session);
	    }

	    if (categoryId.equals(CategoriesEnum.SOLDIERS.getCode()))
		PromotionsService.addModifyPromotionSoldiersReportDetail(promotionReportId, promotionReportDetailData, loginEmpId, session);
	    else if (categoryId.equals(CategoriesEnum.PERSONS.getCode()))
		PromotionsService.addModifyPromotionReportDetail(promotionReportId, promotionReportDetailData, loginEmpId, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    public static void deletePromotionReportDetail(WFInstance instance, PromotionReportDetailData promotionReportDetailData, List<PromotionReportDetailData> promotionReportDetailDataList, long loginEmpData, boolean isCollective, CustomSession... useSession) throws BusinessException {

	boolean isOpenedSession = isSessionOpened(useSession);
	CustomSession session = isOpenedSession ? useSession[0] : DataAccess.getSession();
	try {
	    if (!isOpenedSession)
		session.beginTransaction();

	    if (instance != null) {
		List<PromotionReportDetailData> promotionReportDetailDataListBeneficiaries = new ArrayList<PromotionReportDetailData>();
		if (isCollective)
		    promotionReportDetailDataListBeneficiaries = promotionReportDetailDataList;
		else
		    promotionReportDetailDataListBeneficiaries.add(promotionReportDetailData);

		List<Long> instanceBeneficiariesIds = getPromotionsReportsInstanceBeneficiariesIds(promotionReportDetailDataListBeneficiaries);
		if (instanceBeneficiariesIds != null && !instanceBeneficiariesIds.isEmpty())
		    deleteWFInstanceBeneficiaries(instance.getInstanceId(), instanceBeneficiariesIds, session);
	    }

	    if (isCollective)
		PromotionsService.deletePromotionReportDetails(promotionReportDetailDataList, loginEmpData, session);
	    else
		PromotionsService.deletePromotionReportDetail(promotionReportDetailData, promotionReportDetailDataList, loginEmpData, session);

	    if (!isOpenedSession)
		session.commitTransaction();
	} catch (BusinessException e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    throw e;
	} catch (Exception e) {
	    if (!isOpenedSession)
		session.rollbackTransaction();
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    if (!isOpenedSession)
		session.close();
	}
    }

    private static List<Long> getPromotionsReportsInstanceBeneficiariesIds(List<PromotionReportDetailData> promotionReportDetails) throws BusinessException {

	List<Long> instanceBeneficiariesIds = new ArrayList<Long>();
	for (PromotionReportDetailData promotionReportDetailDataItr : promotionReportDetails) {
	    if (promotionReportDetailDataItr.getStatus() != null && (promotionReportDetailDataItr.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE.getCode()) || promotionReportDetailDataItr.getStatus().equals(PromotionCandidateStatusEnum.CANDIDATE_SEQUENTIALLY.getCode()) || promotionReportDetailDataItr.getStatus().equals(PromotionCandidateStatusEnum.PRECEDENTED_POINTS.getCode()))) {
		instanceBeneficiariesIds.add(promotionReportDetailDataItr.getEmpId());
	    }
	}

	return instanceBeneficiariesIds;
    }

}
