package com.esoft.common.config.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {
    // setup logger
    private final Logger logger = Logger.getLogger(getClass().getName());

    // setup pointcut declarations
    @Pointcut("execution(* com.esoft.*.service.controller.*.*(..))")
    private void forControllerPackage() {
    }

    // do the same for service and dao
    @Pointcut("execution(* com.esoft.*.service.service.*.*(..))")
    private void forServicePackage() {
    }

    @Pointcut("execution(* com.esoft.*.service.repository.*.*(..))")
    private void forRepositoryPackage() {
    }

    @Pointcut("forControllerPackage() || forServicePackage() || forRepositoryPackage()")
    private void forAppFlow() {
    }

    // add @Before advice
    @Before("forAppFlow()")
    public void before(JoinPoint theJoinPoint) {
        String theMethod = theJoinPoint.getSignature().toShortString();
        logger.info("Before calling method: " + theMethod +
                ". Arguments: " + Arrays.toString(theJoinPoint.getArgs()));
    }

    // add @AfterReturning advice
    @AfterReturning(pointcut = "forAppFlow()", returning = "theResult")
    public void after(JoinPoint theJoinPoint, Object theResult) {
        // display the method we are returning from
        String theMethod = theJoinPoint.getSignature().toShortString();
        logger.info("After returning from method: " + theMethod + ". Result: " + theResult);
    }
}
