package com.uganda.phoenix.api.middleware.api;

import com.uganda.phoenix.api.middleware.model.BaseResponse;
import com.uganda.phoenix.api.middleware.model.ClientRegistrationDetail;
import com.uganda.phoenix.api.middleware.model.KeyExchangeResponse;
import com.uganda.phoenix.api.middleware.services.KeyExchangeService;
import com.uganda.phoenix.api.middleware.services.RegistrationService;
import com.uganda.phoenix.api.middleware.utils.Constants;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("isw/auth")
public class AuthApi {

    private RegistrationService registrationService;
    private KeyExchangeService keyExchangeService;

    public AuthApi(RegistrationService registrationService, KeyExchangeService keyExchangeService) {

        this.registrationService = registrationService;
        this.keyExchangeService  = keyExchangeService;

    }

    @GetMapping("/generateKeys")
    public Map<String, String> clientRegistration() throws Exception {

        return registrationService.generateKeys();
    }

    @PostMapping("/registerClient")
    public String clientRegistration(@RequestBody ClientRegistrationDetail registrationDetail) throws Exception {
        return registrationService.doRegistration(registrationDetail);
    }

    @GetMapping("/keyExchange")
    public BaseResponse<KeyExchangeResponse> keyExchange() throws Exception {
        return keyExchangeService.doKeyExchange();
    }

    @GetMapping("/test")
    public String test() throws Exception {
        return Constants.MY_SERIAL_ID;
    }


}
