package kz.medet.orderservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.medet.orderservice.dto.OrderDto;
import kz.medet.orderservice.dto.OrderResponse;
import kz.medet.orderservice.dto.ProductResponse;
import kz.medet.orderservice.dto.ProductWithOrderIdsDto;
import kz.medet.orderservice.entity.Order;
import kz.medet.orderservice.exceptions.CustomException;
import kz.medet.orderservice.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OrderResponseService {
    KafkaTemplate<String, Object> kafkaTemplate;
    OrderRepository orderRepository;

    ObjectMapper objectMapper;

    @KafkaListener(topics = "product.to.order.responses", groupId = "order-group")
    public void listenProductResponses(String response) {
        log.info("Message: {}", response);
        try {

            ProductResponse productResponse = objectMapper.readValue(response, ProductResponse.class);
            Order order = orderRepository.findById(productResponse.getOrderId()).orElseThrow(
                    () -> new CustomException("Order Not found with Id: " + productResponse.getOrderId()));
            OrderDto orderDto = new OrderDto();
            orderDto.setId(order.getId());
            orderDto.setTimeCreated(order.getTimeCreated());
            orderDto.setProducts(productResponse.getProducts());

            OrderResponse orderResponse = new OrderResponse(order.getCustomerId(), orderDto);
            kafkaTemplate.send("order.to.user.responses", orderResponse);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @KafkaListener(topics = "product.to.order.ProductCreated", groupId = "order-group")
    public void listenProductResponsesCreatedProduct(String response) {
        log.info("Message: {}", response);
        try {

            ProductWithOrderIdsDto productWithOrderIdsDto = objectMapper.readValue(response, ProductWithOrderIdsDto.class);
            Order order = orderRepository.findById(productWithOrderIdsDto.getOrderId()).orElseThrow(
                    () -> new CustomException("Order Not found with Id: " + productWithOrderIdsDto.getOrderId()));
            order.getProducts().add(productWithOrderIdsDto.getProductId());

            kafkaTemplate.send("order.to.user.responsesProductCreated", productWithOrderIdsDto.getProductId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
