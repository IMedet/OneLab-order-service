package kz.medet.orderservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.medet.orderservice.dto.*;
import kz.medet.orderservice.entity.Order;
import kz.medet.orderservice.exceptions.CustomException;
import kz.medet.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
public class OrderServiceWithProducts {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public OrderServiceWithProducts(KafkaTemplate<String, Object> kafkaTemplate,
                                    OrderRepository orderRepository,
                                    ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user.to.order.requests", groupId = "order-group")
    @Transactional
    public void listenUserOrders(String userId) {
        Order order = orderRepository.findByCustomerId(Long.valueOf(userId)).orElseThrow(() -> new CustomException("Order not found with customer Id: " + userId));
        kafkaTemplate.send("order.to.product.requests", new OrderRequest(order.getId(), order.getProducts()));
    }


    @KafkaListener(topics = "user.to.order.createProduct", groupId = "order-group")
    @Transactional
    public void listenUserOrdersCreateProduct(String message) {
        log.info("Message: {}", message);

        try {
            CreateProductDto createProductDto = objectMapper.readValue(message, CreateProductDto.class);
            Order order = orderRepository.findById(createProductDto.getOrderId()).orElseThrow(
                    () -> new CustomException("Order Not found with Id: " + createProductDto.getOrderId()));

            kafkaTemplate.send("order.to.product.createProduct", createProductDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
