package com.uganda.phoenix.api.middleware.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyExchangeRequest extends ClientTerminalRequest {
	protected String password;
	private String clientSessionPublicKey;
	
}
