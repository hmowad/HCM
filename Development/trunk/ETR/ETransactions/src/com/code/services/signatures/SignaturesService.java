package com.code.services.signatures;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.signatures.Signature;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.enums.SignaturesTypesEnum;
import com.code.exceptions.BusinessException;
import com.code.exceptions.DatabaseException;
import com.code.services.BaseService;
import com.code.services.util.HijriDateService;

public class SignaturesService extends BaseService {

    private final static String endDateString = "01/01/1470";

    /*------------------------------------------------------Operations------------------------------------------------------*/
    public static Signature cloneSignature(int signatureStampType, String signatureDescription, Date newValidFromDate, long loginEmpId) throws BusinessException {
	Signature existingSignature = getNewestSignatureBySignatureDescription(signatureStampType, signatureDescription);
	validateClonedSignature(signatureStampType, existingSignature, signatureDescription, newValidFromDate);

	existingSignature.setValidToDate(HijriDateService.addSubHijriDays(newValidFromDate, -1));
	Signature newSignature = constructClonedSignature(signatureStampType, existingSignature, newValidFromDate);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    existingSignature.setSystemUser(loginEmpId + ""); // audit
	    DataAccess.updateEntity(existingSignature, session);
	    newSignature.setSystemUser(loginEmpId + ""); // audit
	    DataAccess.addEntity(newSignature, session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();
	    newSignature.setId(null);
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}

	return newSignature;
    }

    public static void saveSignature(int signatureStampType, Signature signature, boolean newSignatureFlag, long loginEmpId) throws BusinessException {
	signature.setValidToDateString(endDateString);
	validateSignature(signatureStampType, signature, newSignatureFlag);

	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    if (SignaturesTypesEnum.STAMP.getCode() == signatureStampType) {
		signature.setName(signature.getSignDesc());
		signature.setTitleDesc(signature.getSignDesc());
	    }

	    signature.setSystemUser(loginEmpId + ""); // audit
	    if (signature.getId() == null)
		DataAccess.addEntity(signature, session);
	    else
		DataAccess.updateEntity(signature, session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();
	    e.printStackTrace();

	    if (newSignatureFlag)
		signature.setId(null);

	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    public static void modifySignaturePhoto(Signature signature) throws BusinessException {
	CustomSession session = DataAccess.getSession();
	try {
	    session.beginTransaction();

	    DataAccess.updateEntity(signature, session);

	    session.commitTransaction();
	} catch (Exception e) {
	    session.rollbackTransaction();

	    e.printStackTrace();
	    throw new BusinessException("error_general");
	} finally {
	    session.close();
	}
    }

    private static Signature constructClonedSignature(int signatureStampType, Signature existingSignature, Date newValidFromDate) {
	Signature newSignature = new Signature();
	newSignature.setSignType(existingSignature.getSignType());
	newSignature.setSignDesc(existingSignature.getSignDesc());
	newSignature.setValidFromDate(newValidFromDate);
	newSignature.setValidToDateString(endDateString);
	newSignature.setSignaturePhoto(existingSignature.getSignaturePhoto());

	if (SignaturesTypesEnum.SIGNATURE.getCode() == signatureStampType) {
	    newSignature.setName(existingSignature.getName());
	    newSignature.setTitleDesc(existingSignature.getTitleDesc());

	    newSignature.setRankDesc(existingSignature.getRankDesc());
	    newSignature.setEnName(existingSignature.getEnName());
	    newSignature.setEnRankDesc(existingSignature.getEnRankDesc());
	    newSignature.setEnTitleDesc(existingSignature.getEnTitleDesc());
	    newSignature.setEmpId(existingSignature.getEmpId());

	} else if (SignaturesTypesEnum.STAMP.getCode() == signatureStampType) {
	    newSignature.setName(existingSignature.getSignDesc());
	    newSignature.setTitleDesc(existingSignature.getSignDesc());

	    newSignature.setUnitId(existingSignature.getUnitId());
	    newSignature.setUnitFullName(existingSignature.getUnitFullName());

	}

	return newSignature;
    }

    /*------------------------------------------------------Validations-----------------------------------------------------*/
    private static void validateSignature(int signatureStampType, Signature signature, boolean newSignatureFlag) throws BusinessException {

	if (signature.getSignType() == null)
	    throw new BusinessException(signatureStampType == SignaturesTypesEnum.SIGNATURE.getCode() ? "error_signatureTypeIsMandatory" : "error_stampTypeIsMandatory");

	if (signature.getSignDesc() == null || signature.getSignDesc().trim().length() == 0)
	    throw new BusinessException(signatureStampType == SignaturesTypesEnum.SIGNATURE.getCode() ? "error_signatureDescriptionIsMandatory" : "error_stampDescriptionIsMandatory");

	if (signature.getValidFromDate() == null)
	    throw new BusinessException(signatureStampType == SignaturesTypesEnum.SIGNATURE.getCode() ? "error_signatureStartDateIsMandatory" : "error_stampStartDateIsMandatory");

	if (newSignatureFlag && countSignatures(signature.getSignType(), null, signature.getId() == null ? FlagsEnum.ALL.getCode() : signature.getId()) != 0)
	    throw new BusinessException(signatureStampType == SignaturesTypesEnum.SIGNATURE.getCode() ? "error_signatureTypeIsRepeated" : "error_stampTypeIsRepeated");
	if (newSignatureFlag && countSignatures(FlagsEnum.ALL.getCode(), signature.getSignDesc(), signature.getId() == null ? FlagsEnum.ALL.getCode() : signature.getId()) != 0)
	    throw new BusinessException(signatureStampType == SignaturesTypesEnum.SIGNATURE.getCode() ? "error_signatureDescriptionRepeated" : "error_stampDescriptionRepeated");

	if (SignaturesTypesEnum.SIGNATURE.getCode() == signatureStampType) {
	    if (signature.getName() == null || signature.getName().trim().length() == 0)
		throw new BusinessException("error_signatureOwnerNameIsMandatory");

	    if (signature.getTitleDesc() == null || signature.getTitleDesc().trim().length() == 0)
		throw new BusinessException("error_signatureOwnerTitleIsMandatory");

	    if (signature.getEmpId() == null)
		throw new BusinessException("error_employeeMandatory");
	}
    }

    private static void validateClonedSignature(int signatureStampType, Signature existingSignature, String signatureDescription, Date newValidFromDate) throws BusinessException {

	if (existingSignature == null)
	    throw new BusinessException("error_general");

	if (signatureDescription == null || signatureDescription.trim().isEmpty())
	    throw new BusinessException(signatureStampType == SignaturesTypesEnum.SIGNATURE.getCode() ? "error_signatureDescriptionIsMandatory" : "error_stampDescriptionIsMandatory");

	if (newValidFromDate == null)
	    throw new BusinessException(signatureStampType == SignaturesTypesEnum.SIGNATURE.getCode() ? "error_newSignatureStartDateIsMandatory" : "error_newStampStartDateIsMandatory");

	if (!HijriDateService.isValidHijriDate(newValidFromDate))
	    throw new BusinessException(signatureStampType == SignaturesTypesEnum.SIGNATURE.getCode() ? "error_newSignatureStartDateInvalid" : "error_newStampStartDateInvalid");

	if (HijriDateService.hijriDateDiffByHijriYear(newValidFromDate, HijriDateService.getHijriSysDate()) > 1)
	    throw new BusinessException(signatureStampType == SignaturesTypesEnum.SIGNATURE.getCode() ? "error_newSignatureStartDateTooOld" : "error_newStampStartDateTooOld");

	if (!existingSignature.getValidFromDate().before(newValidFromDate))
	    throw new BusinessException(signatureStampType == SignaturesTypesEnum.SIGNATURE.getCode() ? "error_newSignatureStartDateBeforeExistingStartDate" : "error_newStampStartDateBeforeExistingStartDate");
    }

    /*------------------------------------------------------Queries---------------------------------------------------------*/
    public static List<String> searchSignaturesDescriptions(int signatureStampType) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_SIGN_STAMP_TYPE", signatureStampType);

	    return DataAccess.executeNamedQuery(String.class, QueryNamesEnum.ETR_GET_DISTINCT_SIGNATURES_DESCRIPTIONS.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static Signature getSignatureById(long signId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_SIGN_ID", signId);
	    return DataAccess.executeNamedQuery(Signature.class, QueryNamesEnum.ETR_GET_SIGNATURE_BY_ID.getCode(), qParams).get(0);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    public static List<Signature> getSignatures(int signatureStampType, int signatureType, String signatureDescription,
	    int signatureStatus, String name, String titleDesc, long excludedId) throws BusinessException {
	return searchSignatures(signatureStampType, signatureType, signatureDescription, signatureStatus, name, titleDesc, excludedId);
    }

    private static Signature getNewestSignatureBySignatureDescription(int signatureStampType, String signatureDescription) throws BusinessException {
	if (signatureDescription == null || signatureDescription.trim().isEmpty())
	    return null;
	List<Signature> signaturesList = searchSignatures(signatureStampType, FlagsEnum.ALL.getCode(), signatureDescription, FlagsEnum.ON.getCode(), null, null, FlagsEnum.ALL.getCode());
	return (signaturesList == null || signaturesList.isEmpty()) ? null : signaturesList.get(0);
    }

    private static int countSignatures(int signatureType, String signatureDescription, long excludedId) throws BusinessException {
	return getSignatures(FlagsEnum.ALL.getCode(), signatureType, signatureDescription, FlagsEnum.ALL.getCode(), null, null, excludedId).size();
    }

    private static List<Signature> searchSignatures(int signatureStampType, int signatureType, String signatureDescription,
	    int signatureStatus, String name, String titleDesc, long excludedId) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();

	    qParams.put("P_SIGN_STAMP_TYPE", signatureStampType);

	    qParams.put("P_SIGN_TYPE", signatureType);
	    qParams.put("P_SIGN_DESC", (signatureDescription == null || signatureDescription.equals("")) ? FlagsEnum.ALL.getCode() + "" : signatureDescription);
	    qParams.put("P_SIGN_STATUS", signatureStatus);

	    qParams.put("P_NAME", (name == null || name.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + name + "%");
	    qParams.put("P_TITLE_DESC", (titleDesc == null || titleDesc.equals("")) ? FlagsEnum.ALL.getCode() + "" : "%" + titleDesc + "%");

	    qParams.put("P_EXCLUDED_ID", excludedId);

	    return DataAccess.executeNamedQuery(Signature.class, QueryNamesEnum.ETR_SEARCH_SIGNATURES.getCode(), qParams);
	} catch (DatabaseException e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

}
