package kz.medet.orderservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class OrderControllerLoggingAspect {

    @Before("execution(* kz.medet.orderservice.controller.OrderController.*(..))")
    public void logBeforeMethod(JoinPoint joinPoint) {
        log.info("[START] Method: {} | Arguments: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "execution(* kz.medet.orderservice.controller.OrderController.*(..))", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        log.info("[SUCCESS] Method: {} | Returned: {}", joinPoint.getSignature().toShortString(), result);
    }

    @AfterThrowing(value = "execution(* kz.medet.orderservice.controller.OrderController.*(..))", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Exception exception) {
        log.error("[ERROR] Method: {} | Exception: {}", joinPoint.getSignature().toShortString(), exception.getMessage(), exception);
    }

    @Around("execution(* kz.medet.orderservice.controller.OrderController.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            log.error("[FAILED] Method: {} | Execution Time: {} ms | Exception: {}", joinPoint.getSignature().toShortString(), (System.currentTimeMillis() - start), e.getMessage());
            throw e;
        }
        long executionTime = System.currentTimeMillis() - start;
        log.info("[COMPLETED] Method: {} | Execution Time: {} ms", joinPoint.getSignature().toShortString(), executionTime);
        return result;
    }
}