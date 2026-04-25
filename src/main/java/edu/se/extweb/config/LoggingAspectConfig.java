package edu.se.extweb.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;

@Aspect
@Component
@Slf4j
public class LoggingAspectConfig {

    private final Map<Throwable, Boolean> loggedExceptions = Collections.synchronizedMap(new WeakHashMap<>());

    @Pointcut("execution(* edu.se.extweb..*.*(..))")
    public void methodsPointcut() {}

    @Before("methodsPointcut()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("Entering method: {}.{} with arguments: {}",
                className, methodName, Arrays.toString(args));

    }

    @AfterReturning(pointcut = "methodsPointcut()", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("Method {}.{} completed successfully with result: {} for arguments: {}",
                className, methodName, result, Arrays.toString(args));
    }

}
