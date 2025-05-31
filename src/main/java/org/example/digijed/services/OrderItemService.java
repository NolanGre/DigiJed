package org.example.digijed.services;

import lombok.RequiredArgsConstructor;
import org.example.digijed.models.OrderItem;
import org.example.digijed.repositories.OrderItemRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        log.debug("Fetching order items for order ID: {}", orderId);
        return orderItemRepository.findByOrderId(orderId);
    }

    public OrderItem addItemToOrder(OrderItem orderItem) {
        log.info("Adding new item to order ID: {}", orderItem.getOrder().getId());
        return orderItemRepository.save(orderItem);
    }
}
