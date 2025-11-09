package com.ecommerce.library.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final ObjectMapper objectMapper;

    @Around("within(com.ecommerce.*.controller.*)")
    public Object logBefore(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        String methodName = pjp.getSignature().getName();
        LocalDateTime startTime = LocalDateTime.now();
        Object[] loggableArgs = Arrays.stream(args)
                .map(arg -> {
                    if (arg instanceof MultipartFile file) {
                        return String.format("MultipartFile[name=%s, size=%d, contentType=%s]",
                                file.getOriginalFilename(), file.getSize(), file.getContentType());
                    }
                    if (arg instanceof MultipartFile[] files) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("MultipartFile[");
                        for (int i = 0; i < files.length; i++) {
                            MultipartFile f = files[i];
                            sb.append(String.format("{name=%s, size=%d, contentType=%s}",
                                    f.getOriginalFilename(), f.getSize(), f.getContentType()));
                            if (i < files.length - 1) {
                                sb.append(", ");
                            }
                        }
                        sb.append("]");
                        return sb.toString();
                    }
                    if (arg instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof MultipartFile) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("List<MultipartFile>[");
                        for (int i = 0; i < list.size(); i++) {
                            MultipartFile f = (MultipartFile) list.get(i);
                            sb.append(String.format("{name=%s, size=%d, contentType=%s}",
                                    f.getOriginalFilename(), f.getSize(), f.getContentType()));
                            if (i < list.size() - 1) {
                                sb.append(", ");
                            }
                        }
                        sb.append("]");
                        return sb.toString();
                    }
                    return arg;
                })
                .toArray();

        log.info("Method {} called with args: {}. Start time: {}", methodName, objectMapper.writeValueAsString(loggableArgs), startTime);

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
                 methodName, objectMapper.writeValueAsString(object), endTime, duration);
        return object;
    }

    @AfterReturning(value = "within(com.ecommerce.*.exception.RestException)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        LocalDateTime endTime = LocalDateTime.now();
        log.info("Method {} returned successfully with result: {}. End time: {}", methodName, result, endTime);
    }
}
