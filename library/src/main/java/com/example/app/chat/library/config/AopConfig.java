package com.example.app.chat.library.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class AopConfig {

    @Around("within(com.example.app.chat.*.controller.*)")
    public Object logBefore(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        String methodName = pjp.getSignature().getName();
        LocalDateTime startTime = LocalDateTime.now();
        log.info("Method {} called with args: {}. Start time: {}", methodName, Arrays.toString(args), startTime);
        Object object;
        try {
            object = pjp.proceed();
        } catch (Throwable e) {
            LocalDateTime exceptionTime = LocalDateTime.now();
            log.error("Method {} failed with exception: {}. Exception time: {}", methodName, e.getMessage(), exceptionTime);
            throw e;
        }
        LocalDateTime endTime = LocalDateTime.now();
        long duration = Duration.between(startTime, endTime).toMillis();
        log.info("Method {} completed with result: {}. End time: {}. Duration: {} ms",
                 methodName, object, endTime, duration);
        return object;
    }
}
