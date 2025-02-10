package com.uganda.phoenix.api.middleware.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@Value
@Jacksonized
public class KeyExchangeRequest {
	String terminalId;
	String appVersion;
	String serialId;
	String requestReference;
	String password;
	String clientSessionPublicKey;
	
}
