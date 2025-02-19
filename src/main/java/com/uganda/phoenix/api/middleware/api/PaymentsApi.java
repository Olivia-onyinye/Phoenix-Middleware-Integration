package com.uganda.phoenix.api.middleware.api;


import com.uganda.phoenix.api.middleware.api.request.PaymentRequest;
import com.uganda.phoenix.api.middleware.services.PaymentsService;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("isw/payments")
public class PaymentsApi {

    private PaymentsService paymentsService;

    public PaymentsApi(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @PostMapping("/validation")
    public String validateCustomer(@RequestBody PaymentRequest request) throws Exception {

        return paymentsService.validateCustomer(request);
    }

    @PostMapping("/pay")
    public String doPayment(@RequestBody PaymentRequest registrationDetail) throws Exception {

        return paymentsService.makePayment(registrationDetail);
    }

    @GetMapping("/checkStatus")
    public String checkStatus(@PathParam("requestReference") String requestReference) throws Exception {

        return paymentsService.checkStatus(requestReference);
    }

    @GetMapping("/balance")
    public String getBalance() throws Exception {

        return paymentsService.fetchBalance();
    }

    @GetMapping("/billerCategories")
    public String getBillerCategories() throws Exception {

        return paymentsService.getCategories();
    }

    @GetMapping("/categoryBillers")
    public String getBillersByCategory(@PathParam("categoryId") String categoryId) throws Exception {

        return paymentsService.getCategoryBillers(categoryId);
    }

    @GetMapping("/billerItems")
    public String getPaymentItemsByBiller(@PathParam("billerId") String billerId) throws Exception {

        return paymentsService.getBillerItems(billerId);
    }

}
