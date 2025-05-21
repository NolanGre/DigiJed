package org.example.digijed.logs;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(org.example.digijed.controllers..*)")
    private void anyControllerPointcut() {}

    @Before("anyControllerPointcut()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("Entering method: {} with arguments: {}", joinPoint.getSignature().toShortString()  , joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "anyControllerPointcut()", returning = "result")
    public void logAfterReturningMethod(JoinPoint joinPoint, Object result) {
        log.debug("Method {} executed successfully, returned: {}", joinPoint.getSignature().toShortString(), result);
    }
}
