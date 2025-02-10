package com.uganda.phoenix.api.middleware.services;

import com.uganda.phoenix.api.middleware.api.request.LoginResponse;
import com.uganda.phoenix.api.middleware.model.*;
import com.uganda.phoenix.api.middleware.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class RegistrationService {


	/**
	 * @return Map<String,String>
	 * @throws SystemApiException
	 *
	 * This method generates for you a key pair, the private and public keys.
	 * Use the generated values to update your public key and private key in application.properties
	 */
	public Map<String,String>  generateKeys() throws SystemApiException {

	 KeyPair pair = CryptoUtils.generateKeyPair();
     String privateKey = Base64.encodeBase64String(pair.getPrivate().getEncoded());
     String publicKey =  Base64.encodeBase64String(pair.getPublic().getEncoded());
	 Map<String, String> keyPair = new HashMap<>();
	 keyPair.put("publicKey", publicKey);
	 keyPair.put("privateKey", privateKey);
	 return  keyPair;

	}

	/**
	 * @return Map<String,String>
	 * @throws SystemApiException
	 *
	 * This method attempts to do client registration for you given the required data
	 * Make sure you generated the keys first, from the method above (generateKeys)
	 * Use the Client Secret that will be returned if  complete registration is successful
	 * as your new CLIENT_SECRET in constants
	 *
	 */
	 public String doRegistration(ClientRegistrationDetail registrationDetail) throws Exception {

	     String privateKey = Constants.PRIKEY;
         String publicKey  = Constants.PUBKEY;

		 log.info("privateKey IS: {}", privateKey);
		 log.info("publicKey IS: {}" , publicKey);
		 log.info("URL: {}", Constants.ROOT_LINK);

		EllipticCurveUtils curveUtils = new EllipticCurveUtils("ECDH");
		KeyPair keyPair = curveUtils.generateKeypair();
		String curvePrivateKey = curveUtils.getPrivateKey(keyPair);
		String curvePublicKey  = curveUtils.getPublicKey(keyPair);

		String resonse = clientRegistrationRequest(publicKey,curvePublicKey,privateKey,registrationDetail);
		
		BaseResponse<ClientRegistrationResponse> registrationResponse = UtilMethods.unMarshallSystemResponseObject(resonse,  ClientRegistrationResponse.class);

		if(!registrationResponse.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {
			//If it failed, show message
			return registrationResponse.getResponseMessage();
		} else {
			//Registration was successful, extract needed values to continue to complete registration

			String decryptedSessionKey = CryptoUtils.decryptWithPrivate(registrationResponse.getResponse().getServerSessionPublicKey(),privateKey);
			String terminalKey = curveUtils.doECDH(curvePrivateKey,decryptedSessionKey);

			log.info("==============sessionKey/terminalKey==============");
			log.info("sessionKey: {} ",terminalKey);

			String authToken =  CryptoUtils.decryptWithPrivate(registrationResponse.getResponse().getAuthToken(),privateKey);
			String transactionReference = registrationResponse.getResponse().getTransactionReference();
			String otp = "";

			String finalResponse =  completeRegistration(terminalKey,authToken,transactionReference, otp,privateKey);
			BaseResponse<LoginResponse> response = UtilMethods.unMarshallSystemResponseObject(finalResponse,  LoginResponse.class);

			if(response.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {

				//Complete registration was successful, extract returned new Secret
				if(response.getResponse().getClientSecret() != null  && response.getResponse().getClientSecret().length() > 5) {

					String clientSecret = CryptoUtils.decryptWithPrivate(response.getResponse().getClientSecret() ,privateKey);
					log.info("New ClientSecret: {}" ,clientSecret);
					//return the New secret
					return "Successful, New Client Secret: " + clientSecret;
				}
			} else {
				return response.getResponseMessage();
			}
			return "Registration Failed";
		}

	 }
	 
	 private String clientRegistrationRequest(String publicKey,String clientSessionPublicKey,String privateKey,ClientRegistrationDetail setup) throws Exception{
		 String registrationEndpointUrl = Constants.ROOT_LINK + "client/clientRegistration";
		 setup = ClientRegistrationDetail.builder()
				 .serialId(Constants.MY_SERIAL_ID)
				 .terminalId(Constants.TERMINAL_ID)
				 .publicKey(publicKey)
				 .gprsCoordinate("")
				 .clientSessionPublicKey(clientSessionPublicKey)
				 .build();
		  log.info("Request: {}",setup);

		  Map<String,String> headers = AuthUtils.generateInterswitchAuth(Constants.POST_REQUEST, registrationEndpointUrl,"","","",privateKey);
		  String json= JSONDataTransform.marshall(setup);
		  log.info("Request json: {}",json);
		 return HttpUtil.postHTTPRequest( registrationEndpointUrl, headers, json);
	  }
	 
	 private String completeRegistration(String terminalKey,String authToken,String transactionReference,String otp,String privateKey) throws Exception{
		 String registrationCompletionEndpointUrl = Constants.ROOT_LINK   + "client/completeClientRegistration";

		 String passwordHash = UtilMethods.hash512(Constants.ACCOUNT_PWD);
		 CompleteClientRegistration completeReg= CompleteClientRegistration.builder()
				 .terminalId(Constants.TERMINAL_ID)
				 .serialId(Constants.MY_SERIAL_ID)
				 .otp(CryptoUtils.encrypt(otp,terminalKey))
				 .requestReference(java.util.UUID.randomUUID().toString())
				 .password(CryptoUtils.encrypt(passwordHash,terminalKey))
				 .transactionReference(transactionReference)
				 .appVersion(Constants.APP_VERSION)
				 .gprsCoordinate("").build();

		  Map<String,String> headers = AuthUtils.generateInterswitchAuth(Constants.POST_REQUEST, registrationCompletionEndpointUrl,
				  "",authToken,terminalKey,privateKey);
		  String json= JSONDataTransform.marshall(completeReg);

		  return HttpUtil.postHTTPRequest( registrationCompletionEndpointUrl, headers, json);
	 }


}
