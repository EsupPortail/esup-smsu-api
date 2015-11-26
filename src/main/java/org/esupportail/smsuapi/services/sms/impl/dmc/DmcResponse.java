package org.esupportail.smsuapi.services.sms.impl.dmc;

public class DmcResponse {

	private Boolean success;
	
	private String errorCode;
	
	private String errorDetail;
	
	private Boolean fatal;
	
	private Boolean invalidParams;
	
	private String response;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDetail() {
		return errorDetail;
	}

	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}

	public Boolean getFatal() {
		return fatal;
	}

	public void setFatal(Boolean fatal) {
		this.fatal = fatal;
	}

	public Boolean getInvalidParams() {
		return invalidParams;
	}

	public void setInvalidParams(Boolean invalidParams) {
		this.invalidParams = invalidParams;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "DmcResponse [success=" + success + ", errorCode=" + errorCode + ", errorDetail=" + errorDetail
				+ ", fatal=" + fatal + ", invalidParams=" + invalidParams + ", response=" + response + "]";
	}

}
