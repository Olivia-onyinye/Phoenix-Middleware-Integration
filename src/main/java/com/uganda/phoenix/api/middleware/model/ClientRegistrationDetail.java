package com.uganda.phoenix.api.middleware.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@Value
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown=true)
public class ClientRegistrationDetail{

	String terminalId;
	String appVersion;
	String serialId;
	String requestReference;
	String gprsCoordinate;
	String name;
	String phoneNumber;
	String nin;
	String gender;
	String emailAddress;
	String ownerPhoneNumber;
	String publicKey;
	String clientSessionPublicKey;

	
	
   
}

