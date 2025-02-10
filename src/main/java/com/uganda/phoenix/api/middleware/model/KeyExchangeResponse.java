package com.uganda.phoenix.api.middleware.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public class KeyExchangeResponse   {
	private String authToken;
	private String serverSessionPublicKey;
	private String expireTime;
	private boolean requiresOtp;
	private String terminalKey;
}
