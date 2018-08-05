package com.code.services.workflow.generalnotifications;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.workflow.WFInstance;
import com.code.dal.orm.workflow.WFTask;
import com.code.enums.FlagsEnum;
import com.code.enums.WFInstanceStatusEnum;
import com.code.enums.WFProcessesEnum;
import com.code.enums.WFTaskRolesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.util.HijriDateService;
import com.code.services.workflow.BaseWorkFlow;

public class GeneralNotificationsWorkFlow extends BaseWorkFlow {

    public static void sendNotificationMessages(long requesterId, long originalRequesterId, long regionId, String unitHkey, long categoryId, String notificationMessage, String attachments, String taskUrl) throws BusinessException {

	List<EmployeeData> employees = EmployeesService.getEmployeesByPhysicalUnitHkeyNameAndStatusesID(null, null, null, null, unitHkey, null, categoryId == FlagsEnum.ALL.getCode() ? null : new Long[] { categoryId }, regionId, FlagsEnum.ALL.getCode(), FlagsEnum.ALL.getCode());

	validateNotificationMessagesData(notificationMessage, employees);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();

	    List<Long> employeesIds = new ArrayList<Long>();
	    for (int i = 0; i < employees.size(); i++)
		employeesIds.add(employees.get(i).getEmpId());

	    WFInstance instance = addWFInstance(WFProcessesEnum.NOTIFICATION_MESSAGE.getCode(), requesterId, curDate, curHijriDate, WFInstanceStatusEnum.DONE.getCode(), attachments, employeesIds, session);

	    String originalRequesterIdString = (requesterId == originalRequesterId) ? null : (originalRequesterId + "");

	    for (int i = 0; i < employeesIds.size(); i++) {
		WFTask task = addWFTask(instance.getInstanceId(), getDelegate(employeesIds.get(i), instance.getProcessId(), requesterId), employeesIds.get(i), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.NOTIFICATION.getCode(), "1." + (i + 1), session);
		task.setFlexField1(notificationMessage);
		task.setFlexField2(originalRequesterIdString);
		DataAccess.updateEntity(task, session);
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

    public static void sendGeneralMessages(String notifiersIds, String generalMessage) throws BusinessException {

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    Date curDate = new Date();
	    Date curHijriDate = HijriDateService.getHijriSysDate();
	    String taskUrl = ETRConfigurationService.getGeneralMessagesUrl() + ETRConfigurationService.getFollowingProcessesMenuId();
	    long requesterId = ETRConfigurationService.getETransactionsRequesterId();

	    List<Long> notifiersIdsList = splitStringToLongList(notifiersIds);

	    WFInstance instance = addWFInstance(WFProcessesEnum.GENERAL_MESSAGE.getCode(), requesterId, curDate, curHijriDate, WFInstanceStatusEnum.DONE.getCode(), null, notifiersIdsList, session);

	    for (int i = 0; i < notifiersIdsList.size(); i++) {
		WFTask task = addWFTask(instance.getInstanceId(), getDelegate(notifiersIdsList.get(i), instance.getProcessId(), requesterId), notifiersIdsList.get(i), curDate, curHijriDate, taskUrl, WFTaskRolesEnum.NOTIFICATION.getCode(), "1." + (i + 1), session);
		task.setFlexField1(generalMessage);
		DataAccess.updateEntity(task, session);
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

    private static void validateNotificationMessagesData(String notificationMessage, List<EmployeeData> employees) throws BusinessException {
	if (notificationMessage == null || notificationMessage.trim().isEmpty())
	    throw new BusinessException("error_messageMandatory");

	if (employees == null || employees.isEmpty())
	    throw new BusinessException("error_noEmployeesFound");
    }

}
