package com.uganda.phoenix.api.middleware.services;

import com.uganda.phoenix.api.middleware.model.*;
import com.uganda.phoenix.api.middleware.utils.*;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Map;

@Service
public class KeyExchangeService {


	public BaseResponse<KeyExchangeResponse> doKeyExchange() throws Exception {

		String endpointUrl = Constants.ROOT_LINK + "client/doKeyExchange";

		EllipticCurveUtils curveUtils = new EllipticCurveUtils("ECDH");
		KeyPair pair = curveUtils.generateKeypair();
		String privateKey = curveUtils.getPrivateKey(pair);
		String publicKey = curveUtils.getPublicKey(pair);

		KeyExchangeRequest request = KeyExchangeRequest.builder()
				.terminalId(Constants.TERMINAL_ID)
				.serialId(Constants.MY_SERIAL_ID)
				.requestReference(java.util.UUID.randomUUID().toString())
				.appVersion(Constants.APP_VERSION)
				.clientSessionPublicKey(publicKey)
				.build();
		String passwordHash = UtilMethods.hash512(Constants.ACCOUNT_PWD) + request.getRequestReference()
				+ Constants.MY_SERIAL_ID;
		request = KeyExchangeRequest.builder()
				.password(CryptoUtils.signWithPrivateKey(passwordHash))
				.build();

		Map<String, String> headers = AuthUtils.generateInterswitchAuth(Constants.POST_REQUEST, endpointUrl, "", "",
				"");
		String json = JSONDataTransform.marshall(request);

		String response = HttpUtil.postHTTPRequest(endpointUrl, headers, json);
		BaseResponse<KeyExchangeResponse> keyxchangeResponse = UtilMethods.unMarshallSystemResponseObject(response,
				KeyExchangeResponse.class);
		if (keyxchangeResponse.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {
			String clearServerSessionKey = CryptoUtils
					.decryptWithPrivate(keyxchangeResponse.getResponse().getServerSessionPublicKey());
			String terminalkey = new EllipticCurveUtils("ECDH").doECDH(privateKey,clearServerSessionKey);
			keyxchangeResponse.getResponse().setTerminalKey(terminalkey);

			if (! keyxchangeResponse.getResponse().getAuthToken().isEmpty())
				keyxchangeResponse.getResponse()
						.setAuthToken(CryptoUtils.decryptWithPrivate(keyxchangeResponse.getResponse().getAuthToken()));

			return keyxchangeResponse;
		} else {
			keyxchangeResponse.setResponseMessage(keyxchangeResponse.getResponseMessage() + " during Key Exchange, check that you are using correct credentials");
			return keyxchangeResponse;
		}
	}

}
