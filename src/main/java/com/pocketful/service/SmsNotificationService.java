package com.pocketful.service;

import com.pocketful.entity.*;

import okhttp3.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service
public class SmsNotificationService {
    @Value("${sms.account_sid}")
    private String ACCOUNT_SID;

    @Value("${sms.service_sid}")
    private String SERVICE_SID;

    @Value("${sms.auth_token}")
    private String AUTH_TOKEN;

    public void send(String template, Account account) throws RuntimeException {
        log.info("Sending notification: {}, {}", account.getPhoneNumber(), template);

        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String requestURL = String.format("https://notify.twilio.com/v1/Services/%s/Notifications", SERVICE_SID);
            SmsBinder binder = new SmsBinder(account.getPhoneNumber());

            RequestBody requestBody = new FormBody.Builder()
                    .add("ToBinding", objectMapper.writeValueAsString(binder))
                    .add("Body", template)
                    .build();

            Request request = new Request.Builder()
                    .url(requestURL)
                    .post(requestBody)
                    .header("Authorization", Credentials.basic(ACCOUNT_SID, AUTH_TOKEN))
                    .build();

            client.newCall(request).execute();
        } catch (Exception error) {
            log.error("Something wrong when sending notification to phone number: {}, {}, {}", account.getPhoneNumber(), template, error.getMessage());
        }
    }
}
