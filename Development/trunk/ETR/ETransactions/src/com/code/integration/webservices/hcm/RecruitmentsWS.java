package com.code.integration.webservices.hcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.code.dal.orm.hcm.employees.EmployeeData;
import com.code.dal.orm.hcm.employees.EmployeeQualificationsData;
import com.code.dal.orm.hcm.recruitments.RecruitmentWishData;
import com.code.enums.WSResponseStatusEnum;
import com.code.exceptions.BusinessException;
import com.code.integration.parameters.hcm.WSRecEmployee;
import com.code.integration.parameters.hcm.WSRecruitmentWish;
import com.code.integration.responses.WSResponseBase;
import com.code.services.BaseService;
import com.code.services.config.ETRConfigurationService;
import com.code.services.hcm.EmployeesService;
import com.code.services.hcm.RecruitmentsService;
import com.code.services.security.SecurityService;
import com.code.services.util.HijriDateService;

@WebService(targetNamespace = "http://integration.code.com/recruitments",
	portName = "RecruitmentsWSHttpPort")
public class RecruitmentsWS {

    @WebMethod(operationName = "migrateSoldiers")
    @WebResult(name = "response")
    public WSResponseBase migrateSoldiers(@WebParam(name = "authenticationCode") String authenticationCode, @WebParam(name = "soldiersData") List<WSRecEmployee> wsSoldiers) {

	WSResponseBase response = new WSResponseBase();

	try {
	    SecurityService.authenticateExternalService(ETRConfigurationService.getExternalServiceSoldiersRecruitmentsAuthCode(), authenticationCode);

	    List<EmployeeData> soldiersList = new ArrayList<EmployeeData>();
	    List<EmployeeQualificationsData> qualificationsList = new ArrayList<EmployeeQualificationsData>();

	    if (wsSoldiers != null && wsSoldiers.size() > 0) {
		for (WSRecEmployee wsSoldier : wsSoldiers) {
		    EmployeeData employee = new EmployeeData();
		    employee.setFirstName(wsSoldier.getFirstName());
		    employee.setSecondName(wsSoldier.getSecondName());
		    employee.setThirdName(wsSoldier.getThirdName());
		    employee.setLastName(wsSoldier.getLastName());
		    employee.setFirstNameEn(wsSoldier.getFirstNameEn());
		    employee.setSecondNameEn(wsSoldier.getSecondNameEn());
		    employee.setThirdNameEn(wsSoldier.getThirdNameEn());
		    employee.setLastNameEn(wsSoldier.getLastNameEn());
		    employee.setBirthPlace(wsSoldier.getBirthPlace());
		    employee.setBirthDate(HijriDateService.getHijriDate(wsSoldier.getBirthDate())); // TODO date format dd/MM/YYYY
		    employee.setSocialID(wsSoldier.getSocialID());
		    employee.setSocialIDIssueDate(HijriDateService.getHijriDate(wsSoldier.getSocialIDIssueDate())); // TODO date format dd/MM/YYYY
		    employee.setSocialIDExpiryDate(HijriDateService.getHijriDate(wsSoldier.getSocialIDExpiryDate()));
		    employee.setSocialIDIssuePlaceID(wsSoldier.getSocialIDIssuePlaceID());
		    employee.setBloodGroup(wsSoldier.getBloodGroup());
		    employee.setGender(wsSoldier.getGender());
		    employee.setSocialStatus(wsSoldier.getSocialStatus());
		    employee.setGeneralSpecialization(wsSoldier.getGeneralSpecialization());
		    employee.setRecruitmentRankId(wsSoldier.getRecruitmentRankId());
		    employee.setPrivateMobileNumber(wsSoldier.getMobileNumber());
		    employee.setEmail(wsSoldier.getEmail());
		    employee.setRecruitmentMinorSpecId(wsSoldier.getRecruitmentMinorSpecId());
		    employee.setRecTrainingUnitId(wsSoldier.getRecTrainingUnitId());
		    employee.setRecTrainingJoiningDate(HijriDateService.getHijriDate(wsSoldier.getRecTrainingJoiningDate())); // TODO date format dd/MM/YYYY

		    EmployeeQualificationsData employeeQualifications = new EmployeeQualificationsData();
		    employeeQualifications.setRecQualLevelId(wsSoldier.getRecQualLevelId());
		    employeeQualifications.setRecQualPercentage(wsSoldier.getRecQualPercentage());
		    employeeQualifications.setRecQualMinorSpecId(wsSoldier.getRecQualMinorSpecId());
		    employeeQualifications.setRecGraduationYear(wsSoldier.getRecGraduationYear());
		    employeeQualifications.setRecGraduationPlaceDetailId(wsSoldier.getRecGraduationPlaceDetailId());
		    employeeQualifications.setRecStudyCountryId(wsSoldier.getRecStudyCountryId());

		    soldiersList.add(employee);
		    qualificationsList.add(employeeQualifications);
		}
	    }

	    EmployeesService.migrateSoldiersData(soldiersList, qualificationsList);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), e.getParams()));
	} catch (Exception e) {
	    System.out.println("****************************************************** migrateSoldiers ******************************************************");
	    e.printStackTrace();
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage("error_general"));
	}

	return response;
    }

    @WebMethod(operationName = "migrateRecruitmentsWishesAndDegrees")
    @WebResult(name = "response")
    public WSResponseBase migrateRecruitmentsWishesAndDegrees(@WebParam(name = "authenticationCode") String authenticationCode, @WebParam(name = "recruitmentWishes") List<WSRecruitmentWish> wsRecWishes) {

	WSResponseBase response = new WSResponseBase();

	try {
	    SecurityService.authenticateExternalService(ETRConfigurationService.getExternalServiceSoldiersRecruitmentsAuthCode(), authenticationCode);

	    List<RecruitmentWishData> recruitmentWishes = new ArrayList<RecruitmentWishData>();
	    Map<Long, EmployeeData> empsHashMap = new HashMap<Long, EmployeeData>();

	    for (WSRecruitmentWish wsRecWish : wsRecWishes) {

		RecruitmentWishData recruitmentWishData = new RecruitmentWishData();
		EmployeeData empData = EmployeesService.getEmployeeBySocialID(wsRecWish.getSocialId());

		if (empData != null) {
		    empsHashMap.put(empData.getEmpId(), empData);
		    recruitmentWishData.setEmpId(empData.getEmpId());
		    recruitmentWishData.setEmpRecMinorSpecId(empData.getRecruitmentMinorSpecId());
		    recruitmentWishData.setEmpRecRankId(empData.getRecruitmentRankId());
		    recruitmentWishData.setEmpStatusId(empData.getStatusId());
		}

		recruitmentWishData.setEvaluationDegree(wsRecWish.getEvaluationDegree());
		recruitmentWishData.setRegionsFirstWishId(wsRecWish.getRegionsFirstWishId());
		recruitmentWishData.setRegionsSecondWishId(wsRecWish.getRegionsSecondWishId());
		recruitmentWishData.setRegionsThirdWishId(wsRecWish.getRegionsThirdWishId());
		recruitmentWishData.setRegionsFourthWishId(wsRecWish.getRegionsFourthWishId());
		recruitmentWishData.setRegionsFifthWishId(wsRecWish.getRegionsFifthWishId());
		recruitmentWishData.setRegionsSixthWishId(wsRecWish.getRegionsSixthWishId());
		recruitmentWishData.setRegionsSeventhWishId(wsRecWish.getRegionsSeventhWishId());
		recruitmentWishData.setRegionsEighthWishId(wsRecWish.getRegionsEighthWishId());
		recruitmentWishData.setRegionsNinthWishId(wsRecWish.getRegionsNinthWishId());
		recruitmentWishData.setRegionsTenthWishId(wsRecWish.getRegionsTenthWishId());
		recruitmentWishData.setRegionsEleventhWishId(wsRecWish.getRegionsEleventhWishId());

		recruitmentWishes.add(recruitmentWishData);
	    }

	    RecruitmentsService.migrateRecruitmentWishes(recruitmentWishes, empsHashMap);
	    response.setMessage(BaseService.getMessage("notify_successOperation"));
	} catch (BusinessException e) {
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getParameterizedMessage(e.getMessage(), e.getParams()));
	} catch (Exception e) {
	    System.out.println("****************************************************** migrateRecruitmentsWishesAndDegrees ******************************************************");
	    e.printStackTrace();
	    response.setStatus(WSResponseStatusEnum.FAILED.getCode());
	    response.setMessage(BaseService.getMessage("error_general"));
	}

	return response;
    }
}
