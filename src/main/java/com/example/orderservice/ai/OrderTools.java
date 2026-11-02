package com.example.orderservice.ai;

import com.example.orderservice.client.CustomerClient;
import com.example.orderservice.client.InventoryClient;
import com.example.orderservice.client.ProductClient;
import com.example.orderservice.dto.CustomerResponse;
import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.ProductResponse;
import com.example.orderservice.service.OrderService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class OrderTools {

    private final OrderService orderService;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    public OrderTools(OrderService orderService, CustomerClient customerClient, ProductClient productClient, InventoryClient inventoryClient) {
        this.orderService = orderService;
        this.customerClient = customerClient;
        this.productClient = productClient;
        this.inventoryClient = inventoryClient;
    }

    @Tool(description = "Get complete information about an order using its order ID")
    public OrderResponse getOrder(Long orderId) {

        return orderService.getOrder(orderId);
    }

    @Tool(description = "Get the current status of an order using its order ID")
    public String getOrderStatus(Long orderId) {
        return orderService.getOrderStatus(orderId);
    }

    @Tool(description = "Get customer details using the customer ID")
    public CustomerResponse getCustomer(Long customerId) {
        return customerClient.getCustomer(customerId);
    }

    @Tool(description = "Get product details using the product ID")
    public ProductResponse getProduct(Long productId) {
        return productClient.getProduct(productId);
    }

    @Tool(description = "Get available inventory quantity for a product using the product ID")
    public InventoryResponse getInventory(Long productId) {
        return inventoryClient.getInventory(productId);
    }
}