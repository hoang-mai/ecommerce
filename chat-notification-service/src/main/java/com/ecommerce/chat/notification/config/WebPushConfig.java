package com.ecommerce.chat.notification.config;

import lombok.Getter;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.GeneralSecurityException;
import java.security.Security;

@Configuration
@Getter
public class WebPushConfig {

    @Value("${web-push.vapid-public-key}")
    private String vapidPublicKey;

    @Value("${web-push.vapid-private-key}")
    private String vapidPrivateKey;

    @Value("${web-push.subject:mailto:admin@example.com}")
    private String subject;

    @Bean
    public PushService pushService() throws GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());

        PushService pushService = new PushService();
        pushService.setPublicKey(vapidPublicKey);
        pushService.setPrivateKey(vapidPrivateKey);
        pushService.setSubject(subject);

        return pushService;
    }
}

