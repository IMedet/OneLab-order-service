package kz.medet.orderservice.aop;

import org.aspectj.lang.annotation.Pointcut;

public class MyPointCuts {

    @Pointcut("execution(* kz.medet.orderservice.services.OrderResponseService.listenProductResponses(..))")
    public void listenProductResponsesMethod(){}

    @Pointcut("execution(* kz.medet.orderservice.services.OrderResponseService.listenProductResponsesCreatedProduct(..))")
    public void listenProductResponsesCreatedProductMethod(){}

    @Pointcut("execution(* kz.medet.orderservice.services.OrderService.addOrderToCustomer(..))")
    public void addOrderToCustomerMethod(){}

    @Pointcut("execution(* kz.medet.orderservice.services.OrderServiceWithProducts.listenUserOrders(..))")
    public void listenUserOrdersMethod(){}

    @Pointcut("execution(* kz.medet.orderservice.services.OrderServiceWithProducts.listenUserOrdersCreateProduct(..))")
    public void listenUserOrdersCreateProductMethod(){}
}

