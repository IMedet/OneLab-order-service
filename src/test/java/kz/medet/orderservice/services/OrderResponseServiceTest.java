package kz.medet.orderservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.medet.orderservice.dto.*;
import kz.medet.orderservice.repository.OrderRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.medet.orderservice.entity.Order;
import kz.medet.orderservice.exceptions.CustomException;
import kz.medet.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderResponseServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderResponseService orderResponseService;

    private Order order;
    private ProductResponse productResponse;
    private ProductWithOrderIdsDto productWithOrderIdsDto;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setCustomerId(123L);

        productResponse = new ProductResponse();
        productResponse.setOrderId(1L);

        productWithOrderIdsDto = new ProductWithOrderIdsDto();
        productWithOrderIdsDto.setOrderId(1L);
    }

    @Test
    void listenProductResponses_WhenOrderExists_ShouldSendKafkaMessage() throws Exception {
        String jsonResponse = "{}";
        when(objectMapper.readValue(jsonResponse, ProductResponse.class)).thenReturn(productResponse);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderResponseService.listenProductResponses(jsonResponse);

        verify(kafkaTemplate).send(eq("order.to.user.responses"), any(OrderResponse.class));
    }

    @Test
    void listenProductResponses_WhenOrderNotFound_ShouldNotThrowExceptionButLogError() throws Exception {
        String jsonResponse = "{}";
        when(objectMapper.readValue(jsonResponse, ProductResponse.class)).thenReturn(productResponse);
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> orderResponseService.listenProductResponses(jsonResponse));

        verify(kafkaTemplate, never()).send(any(), any());
    }


    @Test
    void listenProductResponsesCreatedProduct_WhenOrderExists_ShouldAddProductToOrder() throws Exception {
        String jsonResponse = "{}";
        when(objectMapper.readValue(jsonResponse, ProductWithOrderIdsDto.class)).thenReturn(productWithOrderIdsDto);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderResponseService.listenProductResponsesCreatedProduct(jsonResponse);

        verify(kafkaTemplate, never()).send(any(), any());
    }


    @Test
    void listenProductResponsesCreatedProduct_WhenOrderNotFound_ShouldNotThrowExceptionButLogError() throws Exception {
        String jsonResponse = "{}";
        when(objectMapper.readValue(jsonResponse, ProductWithOrderIdsDto.class)).thenReturn(productWithOrderIdsDto);
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> orderResponseService.listenProductResponsesCreatedProduct(jsonResponse));

        verify(kafkaTemplate, never()).send(any(), any());
    }

}
