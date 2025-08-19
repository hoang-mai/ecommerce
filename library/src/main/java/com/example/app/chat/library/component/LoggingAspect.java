package com.example.app.chat.library.component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

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

    @AfterReturning(value = "within(com.example.app.chat.*.exception.RestException)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint,Object result) {
        String methodName = joinPoint.getSignature().getName();
        LocalDateTime endTime = LocalDateTime.now();
        log.info("Method {} returned successfully with result: {}. End time: {}", methodName, result, endTime);
    }
}
