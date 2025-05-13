package org.example.digijed.services;

import lombok.RequiredArgsConstructor;
import org.example.digijed.models.OrderItem;
import org.example.digijed.repositories.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public OrderItem addItemToOrder(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }
}
