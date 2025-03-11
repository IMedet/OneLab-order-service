package kz.medet.orderservice.services;

import kz.medet.orderservice.entity.Order;
import kz.medet.orderservice.kafka.KafkaConsumer;
import kz.medet.orderservice.kafka.KafkaProducer;
import kz.medet.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaProducer kafkaProducer;

    @Autowired
    public OrderService(OrderRepository orderRepository, KafkaProducer kafkaProducer) {
        this.orderRepository = orderRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addOrderToCustomer(Long customerId) {
        Order order = new Order();
        order.setCustomerId(customerId);
        orderRepository.save(order);
        kafkaProducer.sendMessage(order.getId());
    }


//
//    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
//    public List<ProductDto> getAllProductsOfOrder(Long orderId) {
//        OrderDto orderDto = orderRepository.findById(orderId).orElseThrow(
//                () -> new ResourceNotFoundException("Order", "OrderId", orderId));
//        return orderDto.getProducts();
//    }
}
