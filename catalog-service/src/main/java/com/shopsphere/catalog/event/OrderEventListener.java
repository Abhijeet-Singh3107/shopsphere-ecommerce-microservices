package com.shopsphere.catalog.event;

import com.shopsphere.catalog.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventListener {
    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Order placed event received: orderId={}, userEmail={}",
                event.getOrderId(), event.getUserEmail());
    }

    @RabbitListener(queues = RabbitMQConfig.STATUS_QUEUE)
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("Order status changed: orderId={}, userEmail={}, status={}",
                event.getOrderId(), event.getUserEmail(), event.getStatus());
    }
}
