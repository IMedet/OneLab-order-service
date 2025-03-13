package kz.medet.orderservice.services;

import kz.medet.orderservice.clients.ProductServiceClient;
import kz.medet.orderservice.dto.OrderDto;
import kz.medet.orderservice.dto.ProductDto;
import kz.medet.orderservice.entity.Order;
import kz.medet.orderservice.exceptions.CustomException;
import kz.medet.orderservice.kafka.KafkaProducer;
import kz.medet.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaProducer kafkaProducer;

    private final ProductServiceClient productServiceClient;


    @Autowired
    public OrderService(OrderRepository orderRepository, KafkaProducer kafkaProducer, ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.kafkaProducer = kafkaProducer;
        this.productServiceClient=productServiceClient;
    }

//    @Transactional(propagation = Propagation.REQUIRED)
//    public void addOrderToCustomer(Long customerId) {
//        Order order = new Order();
//        order.setCustomerId(customerId);
//        orderRepository.save(order);
//        kafkaProducer.sendMessage(order.getId());
//    }

    @Transactional
    public void createOrder(Long customerId) {
        Order order = new Order();
        order.setCustomerId(customerId);
        orderRepository.save(order);
    }


    @Transactional
    public OrderDto getOrderByCustomerId(Long customerId) {
        Order order = orderRepository.findByCustomerId(customerId).
                orElseThrow(() -> new CustomException("Order not found by customerId: " + customerId));
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setTimeCreated(order.getTimeCreated());

        return orderDto;
    }

    @Transactional
    public void addProductToOrder(Long orderId, String productName, double productPrice) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        ProductDto productDto = productServiceClient.createProduct(productName, productPrice);

        order.getProducts().add(productDto.getId());
        orderRepository.save(order);
    }

    @Transactional
    public List<ProductDto> getProductsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        List<Long> productIds = order.getProducts();

        return productServiceClient.getProductsByIds(productIds);
    }



//
//    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
//    public List<ProductDto> getAllProductsOfOrder(Long orderId) {
//        OrderDto orderDto = orderRepository.findById(orderId).orElseThrow(
//                () -> new ResourceNotFoundException("Order", "OrderId", orderId));
//        return orderDto.getProducts();
//    }
}
