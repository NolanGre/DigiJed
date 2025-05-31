package org.example.digijed.services;

import lombok.RequiredArgsConstructor;
import org.example.digijed.models.Order;
import org.example.digijed.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> getAllOrders() {
        log.debug("Retrieving all orders");
        return orderRepository.findAll();
    }

    public Order getOrderById(long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order addOrder(Order order) {
        log.info("Saving order: {}", order.getId());
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long id, String newStatus) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));

        existingOrder.setStatus(newStatus);
        return orderRepository.save(existingOrder);
    }
}


