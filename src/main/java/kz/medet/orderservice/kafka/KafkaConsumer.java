package kz.medet.orderservice.kafka;

import kz.medet.orderservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private final OrderService orderService;

    @Autowired
    public KafkaConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "Orders", groupId = "my_consumer")
    public void listen(String message){
        System.out.println("Received message = " + message);
        orderService.addOrderToCustomer(Long.parseLong(message));
    }
}
