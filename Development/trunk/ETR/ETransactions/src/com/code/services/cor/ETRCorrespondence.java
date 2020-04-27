package com.code.services.cor;

import java.util.HashMap;
import java.util.Map;

import com.code.dal.CustomSession;
import com.code.dal.DataAccess;
import com.code.dal.orm.cor.ETRCor;
import com.code.dal.orm.cor.ETRCorOut;
import com.code.enums.FlagsEnum;
import com.code.enums.QueryNamesEnum;
import com.code.exceptions.BusinessException;
import com.code.services.util.HijriDateService;

public class ETRCorrespondence {
    private ETRCorrespondence() {
    }

    public static String[] doETRCorOut(String subject, CustomSession session) throws Exception {
	String[] etrCorOutInfo = generateETRCorSeqNumber();

	ETRCorOut etrCorOut = new ETRCorOut();
	etrCorOut.setHijriYear(Integer.parseInt(etrCorOutInfo[2]));
	etrCorOut.setSeq(Integer.parseInt(etrCorOutInfo[0]));
	etrCorOut.setSubject(subject);
	etrCorOut.setEvetDateString(etrCorOutInfo[1]);
	DataAccess.addEntity(etrCorOut, session);

	return etrCorOutInfo;
    }

    private static synchronized String[] generateETRCorSeqNumber() throws BusinessException {
	try {
	    String curHijriDate = HijriDateService.getHijriSysDateString();

	    ETRCor etrCor = getETRCor(Integer.parseInt(curHijriDate.split("/")[2]));
	    etrCor.setSeq(etrCor.getSeq() + 1);

	    updateETRCor(etrCor);

	    return new String[] { etrCor.getSeq() + "", curHijriDate, etrCor.getHijriYear() + "" };
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }

    private static void updateETRCor(ETRCor etrCor) throws Exception {

	Runnable etrCorUpdater = new Runnable() {
	    public void run() {
		try {
		    DataAccess.updateEntity(etrCor);

		} catch (Exception e) {
		    e.printStackTrace();
		    etrCor.setSeq(FlagsEnum.OFF.getCode());
		}
	    }
	};

	Thread etrCorUpdaterThread = new Thread(etrCorUpdater);
	etrCorUpdaterThread.start();
	etrCorUpdaterThread.join();

	if (etrCor.getSeq() == FlagsEnum.OFF.getCode())
	    throw new BusinessException("error_transactionDataError");
    }

    private static ETRCor getETRCor(int hijriYear) throws BusinessException {
	try {
	    Map<String, Object> qParams = new HashMap<String, Object>();
	    qParams.put("P_HIJRI_YEAR", hijriYear);
	    return DataAccess.executeNamedQuery(ETRCor.class, QueryNamesEnum.COR_GET_ETR_COR.getCode(), qParams).get(0);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException("error_general");
	}
    }
}