package com.uganda.phoenix.api.middleware.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public class ClientRegistrationResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2922295347736640220L;
	private String transactionReference;
	private String authToken;

	private String serverSessionPublicKey;
}
