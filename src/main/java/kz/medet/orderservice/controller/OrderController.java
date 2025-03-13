package kz.medet.orderservice.controller;

import kz.medet.orderservice.dto.OrderDto;
import kz.medet.orderservice.dto.ProductDto;
import kz.medet.orderservice.entity.Order;
import kz.medet.orderservice.payload.response.MessageResponse;
import kz.medet.orderservice.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{customerId}")
    public ResponseEntity<MessageResponse> createOrder(@PathVariable Long customerId) {
        orderService.createOrder(customerId);
        return new ResponseEntity<>(new MessageResponse("Order created for Customer " + customerId), HttpStatus.CREATED);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<OrderDto> getOrdersByCustomerId(@PathVariable Long customerId) {
        OrderDto orderDto = orderService.getOrderByCustomerId(customerId);
        return ResponseEntity.ok(orderDto);
    }

    @PostMapping("/{orderId}/addProduct")
    public ResponseEntity<MessageResponse> addProductToOrder(@PathVariable Long orderId,
                                                             @RequestParam String productName,
                                                             @RequestParam double productPrice) {
        orderService.addProductToOrder(orderId, productName, productPrice);
        return new ResponseEntity<>(new MessageResponse("Product added to Order " + orderId), HttpStatus.CREATED);
    }

    @GetMapping("/orders/{orderId}/products")
    public ResponseEntity<List<ProductDto>> getProductsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getProductsByOrderId(orderId));
    }
}

