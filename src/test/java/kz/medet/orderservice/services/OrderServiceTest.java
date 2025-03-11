package kz.medet.orderservice.services;

import kz.medet.orderservice.entity.Order;
import kz.medet.orderservice.kafka.KafkaProducer;
import kz.medet.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private OrderService orderService;

    @Test
    void addOrderToCustomer_shouldSaveOrderAndSendKafkaMessage() {
        Long customerId = 1L;
        Long orderId = 100L;

        Order savedOrder = new Order();
        savedOrder.setId(orderId);
        savedOrder.setCustomerId(customerId);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        orderService.addOrderToCustomer(customerId);

        verify(orderRepository, times(1)).save(any(Order.class));

    }
}
