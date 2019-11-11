package com.code.integration.parameters.extensionrequest;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public abstract class WFBaseParam implements Serializable {

	private boolean selected;
	private boolean newRow;
	private boolean toBeDeleted;
	private String warningMessage;
	private String equationString;

	public boolean isNewRow() {
		return newRow;
	}

	public void setNewRow(boolean newRow) {
		this.newRow = newRow;
	}

	public boolean getSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String convertGregDateTOString(Date gregDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return ((gregDate == null) ? null : sdf.format(gregDate));
	}

	public static Date convertStringTOGregDate(String dateString) {
		if (dateString == null || "".equals(dateString))
			return null;
		else {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				return sdf.parse(dateString);
			} catch (ParseException e) {
				return null;
			}
		}
	}

	public boolean isToBeDeleted() {
		return toBeDeleted;
	}

	public void setToBeDeleted(boolean toBeDeleted) {
		this.toBeDeleted = toBeDeleted;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

	public String getEquationString() {
		return equationString;
	}

	public void setEquationString(String equationString) {
		this.equationString = equationString;
	}

}
