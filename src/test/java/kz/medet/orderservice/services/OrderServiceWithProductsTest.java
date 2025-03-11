package kz.medet.orderservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.medet.orderservice.repository.OrderRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.medet.orderservice.dto.CreateProductDto;
import kz.medet.orderservice.dto.OrderRequest;
import kz.medet.orderservice.entity.Order;
import kz.medet.orderservice.exceptions.CustomException;
import kz.medet.orderservice.repository.OrderRepository;
import kz.medet.orderservice.services.OrderServiceWithProducts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceWithProductsTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderServiceWithProducts orderServiceWithProducts;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setCustomerId(100L);
    }

    @Test
    void listenUserOrders_WhenOrderExists_ShouldSendKafkaMessage() {
        when(orderRepository.findByCustomerId(100L)).thenReturn(Optional.of(order));

        orderServiceWithProducts.listenUserOrders("100");

        verify(kafkaTemplate).send(eq("order.to.product.requests"), any(OrderRequest.class));
    }

    @Test
    void listenUserOrders_WhenOrderNotFound_ShouldThrowException() {
        when(orderRepository.findByCustomerId(100L)).thenReturn(Optional.empty());

        CustomException thrown = assertThrows(CustomException.class, () ->
                orderServiceWithProducts.listenUserOrders("100"));

        assertEquals("Order not found with customer Id: 100", thrown.getMessage());
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void listenUserOrdersCreateProduct_WhenOrderExists_ShouldSendKafkaMessage() throws Exception {
        String jsonMessage = "{}";
        CreateProductDto createProductDto = new CreateProductDto();
        createProductDto.setOrderId(1L);

        when(objectMapper.readValue(jsonMessage, CreateProductDto.class)).thenReturn(createProductDto);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderServiceWithProducts.listenUserOrdersCreateProduct(jsonMessage);

        verify(kafkaTemplate).send(eq("order.to.product.createProduct"), any(CreateProductDto.class));
    }

    @Test
    void listenUserOrdersCreateProduct_WhenOrderNotFound_ShouldNotSendKafkaMessage() throws Exception {
        String jsonMessage = "{}";
        CreateProductDto createProductDto = new CreateProductDto();
        createProductDto.setOrderId(1L);

        when(objectMapper.readValue(jsonMessage, CreateProductDto.class)).thenReturn(createProductDto);
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> orderServiceWithProducts.listenUserOrdersCreateProduct(jsonMessage));
        verify(kafkaTemplate, never()).send(any(), any());
    }
}
