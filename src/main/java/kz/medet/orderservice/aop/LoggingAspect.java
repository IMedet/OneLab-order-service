package kz.medet.orderservice.aop;

import kz.medet.orderservice.dto.CreateProductDto;
import kz.medet.orderservice.dto.OrderRequest;
import kz.medet.orderservice.dto.OrderResponse;
import kz.medet.orderservice.dto.ProductResponse;
import kz.medet.orderservice.exceptions.CustomException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("kz.medet.orderservice.aop.MyPointCuts.listenProductResponsesMethod()")
    public void beforeListenProductResponses(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        logger.info("Before: обработка сообщения Kafka listenProductResponses, метод: " + methodSignature.getName());
    }

    @After("kz.medet.orderservice.aop.MyPointCuts.listenProductResponsesCreatedProductMethod()")
    public void afterListenProductResponsesCreatedProduct(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        logger.info("After: обработка сообщения Kafka listenProductResponsesCreatedProduct, метод: " + methodSignature.getName());
    }

    @Around("kz.medet.orderservice.aop.MyPointCuts.addOrderToCustomerMethod()")
    public void aroundAddOrderToCustomer(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        logger.info("Before: создание нового заказа через addOrderToCustomer");
        try {
            proceedingJoinPoint.proceed();
            logger.info("After: заказ успешно создан и сохранен");
        } catch (Exception e) {
            logger.error("Ошибка при добавлении заказа к пользователю: " + e.getMessage(), e);
        }
    }

    @AfterReturning(value = "kz.medet.orderservice.aop.MyPointCuts.listenUserOrdersMethod()", returning = "orderRequest")
    public void afterReturningListenUserOrders(OrderRequest orderRequest) {
        logger.info("AfterReturning: Запрос заказов пользователя успешно обработан, данные: " + orderRequest);
    }

    @AfterThrowing(value = "kz.medet.orderservice.aop.MyPointCuts.listenUserOrdersCreateProductMethod()", throwing = "exception")
    public void afterThrowingListenUserOrdersCreateProduct(Throwable exception) {
        logger.warn("AfterThrowing: ошибка при обработке сообщения Kafka listenUserOrdersCreateProduct: " + exception.getMessage());
    }
}

