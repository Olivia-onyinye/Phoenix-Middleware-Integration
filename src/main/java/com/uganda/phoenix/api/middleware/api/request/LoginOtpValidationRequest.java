package com.uganda.phoenix.api.middleware.api.request;

import com.uganda.phoenix.api.middleware.model.ClientTerminalRequest;

public class LoginOtpValidationRequest extends ClientTerminalRequest {

	private String otp;
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
}
