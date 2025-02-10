package com.uganda.phoenix.api.middleware.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)public class BaseResponse<T> {
	protected String responseCode;
	protected String responseMessage;
	protected T response;
	
}
