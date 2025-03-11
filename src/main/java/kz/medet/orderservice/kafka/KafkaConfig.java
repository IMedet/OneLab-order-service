package kz.medet.orderservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic newTopic(){
        return TopicBuilder.name("OrderResponse").build();
    }

    @Bean
    public NewTopic newTopic2(){
        return TopicBuilder.name("order.to.user.responses").build();
    }

    @Bean
    public NewTopic newTopic3(){
        return TopicBuilder.name("order.to.product.requests").build();
    }


    @Bean
    public NewTopic newTopic5(){
        return TopicBuilder.name("order.to.product.createProduct").build();
    }

    @Bean
    public NewTopic newTopic6(){
        return TopicBuilder.name("order.to.user.responsesProductCreated").build();
    }
}
