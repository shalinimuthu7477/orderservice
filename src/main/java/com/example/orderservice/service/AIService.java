package com.example.orderservice.service;

import com.example.orderservice.ai.OrderTools;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.exception.AIException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final ChatClient chatClient;
    private final OrderService orderService;
    private final OrderTools orderTools;


    public AIService(ChatClient chatClient, OrderService orderService, OrderTools orderTools) {
        this.chatClient = chatClient;
        this.orderService = orderService;
        this.orderTools = orderTools;
    }

    public String askAI(String prompt, String conversationId) {
        try {
            return chatClient
                    .prompt()
                    .system("""
                                    You are an AI assistant for an Order Management System.
                            
                                    Answer questions clearly and professionally.
                            
                                    Use the available tools whenever the user asks for
                                    actual order, customer, product, or inventory information.
                            
                                    Available tools:
                            
                                    - getOrder: Get complete order details.
                                    - getOrderStatus: Get the current order status.
                                    - getCustomer: Get customer details.
                                    - getProduct: Get product details.
                                    - getInventory: Get current inventory for a product.
                            
                                    You can use multiple tools when necessary to answer
                                    a single user question.
                            
                                    For example, if the user asks about an order and the
                                    availability of its products, first retrieve the order,
                                    then retrieve the required product and inventory information.
                            
                                    Use only information returned by the tools.
                            
                                    Never invent order, customer, product, or inventory data.
                            
                                    If information cannot be retrieved, clearly tell the user.
                            """)
                    .user(prompt)
                    .tools(orderTools)
                    .advisors(advisor -> advisor
                            .param(
                                    ChatMemory.CONVERSATION_ID,
                                    conversationId
                            ))
                    .call()
                    .content();
        } catch (Exception e) {
            throw new AIException(
                    "Unable to process the AI request at this time.",e);
        }
    }

    public String askAboutOrder(Long orderId, String question) {

        OrderResponse order = orderService.getOrder(orderId);

        String orderContext = """
                Order Information:
                Order ID: %s
                Customer ID: %s
                Status: %s
                Total Amount: %s
                Created At: %s
                Items: %s
                """.formatted(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getItems()
        );

        return chatClient
                .prompt()
                .system("""
                        You are an AI assistant for an Order Management System.

                        Answer the user's question using only the order information
                        provided in the context.

                        Do not invent or assume order information.

                        If the requested information is not available in the context,
                        clearly say that it is not available.
                        """)
                .user("""
                        %s

                        User Question:
                        %s
                        """.formatted(orderContext, question))
                .call()
                .content();
    }
}