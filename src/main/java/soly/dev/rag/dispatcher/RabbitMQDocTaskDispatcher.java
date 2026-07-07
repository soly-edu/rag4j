package soly.dev.rag.dispatcher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
/**
 * TODO 待实现
 */
@Component
@ConditionalOnProperty(name = "rag.dispatcher.type", havingValue = "RabbitMQ")
public class RabbitMQDocTaskDispatcher implements DocumentTaskDispatcher{

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQDocTaskDispatcher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void taskDispatch(String uniqueFilename, String namespace, String embeddingModel) {
        rabbitTemplate.convertAndSend("docTaskExchange", "docTaskRoutingKey", uniqueFilename);
    }
}
