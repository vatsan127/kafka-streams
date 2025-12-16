package com.github.kafka_streams.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Sample domain object to demonstrate custom Serdes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String orderId;
    private String customerId;
    private String product;
    private int quantity;
    private BigDecimal price;
    private Instant timestamp;
}
