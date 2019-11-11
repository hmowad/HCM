package com.code.integration.parameters.extensionrequest;

import java.io.Serializable;

import com.code.enums.FlagsEnum;

@SuppressWarnings("serial")
public class WFProcess implements Serializable{
	private Long id;
	private String arabicName;
	private String latinName;
	private Long processGroupId;
	private String approveCallback;
	private String rejectCallback;
	private String cancelCallback;
	private Integer enableFlag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getArabicName() {
		return arabicName;
	}

	public void setArabicName(String arabicName) {
		this.arabicName = arabicName;
	}

	public String getLatinName() {
		return latinName;
	}

	public void setLatinName(String latinName) {
		this.latinName = latinName;
	}

	public Long getProcessGroupId() {
		return processGroupId;
	}

	public void setProcessGroupId(Long processGroupId) {
		this.processGroupId = processGroupId;
	}

	public String getApproveCallback() {
		return approveCallback;
	}

	public void setApproveCallback(String approveCallback) {
		this.approveCallback = approveCallback;
	}

	public String getRejectCallback() {
		return rejectCallback;
	}

	public void setRejectCallback(String rejectCallback) {
		this.rejectCallback = rejectCallback;
	}

	public String getCancelCallback() {
		return cancelCallback;
	}

	public void setCancelCallback(String cancelCallback) {
		this.cancelCallback = cancelCallback;
	}

	public Integer getEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(Integer enableFlag) {
		this.enableFlag = enableFlag;
	}

	public Boolean getEnableFlagBoolean() {
		return this.enableFlag.equals(FlagsEnum.ON.getCode());
	}

	public void setEnableFlagBoolean(Boolean enableFlagBoolean) {
		if (enableFlagBoolean)
			this.enableFlag = FlagsEnum.ON.getCode();
		else
			this.enableFlag = FlagsEnum.OFF.getCode();
	}

}