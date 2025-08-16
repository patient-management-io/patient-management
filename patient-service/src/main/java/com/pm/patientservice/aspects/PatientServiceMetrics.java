package com.pm.patientservice.aspects;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class PatientServiceMetrics {

    private static final Logger log = LoggerFactory.getLogger(PatientServiceMetrics.class);
    private final MeterRegistry meterRegistry;

    @Around("execution(* com.pm.patientservice.service.PatientService.getPatients(..))")
    public Object monitorGetPatients(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[REDIS]: Cache miss for patients - fetching from database");
        meterRegistry.counter("custom.redis.cache.miss", "cache", "patients").increment();
        return joinPoint.proceed();
    }
}
