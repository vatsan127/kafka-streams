package com.github.kafka_streams.controller;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.kafka_streams.config.KafkaStreamsConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller to send test messages to Kafka.
 * Useful for testing the streams topology.
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping
    public String sendMessage(
            @RequestParam(required = false) String key,
            @RequestParam String value) {

        log.info("Sending to {} - Key: {}, Value: {}", KafkaStreamsConfig.INPUT_TOPIC, key, value);

        kafkaTemplate.send(KafkaStreamsConfig.INPUT_TOPIC, key, value);

        return String.format("Sent to %s: [%s] = %s", KafkaStreamsConfig.INPUT_TOPIC, key, value);
    }
}
