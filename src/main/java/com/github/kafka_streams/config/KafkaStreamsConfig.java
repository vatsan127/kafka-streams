package com.github.kafka_streams.config;

import com.github.kafka_streams.avro.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

/**
 * Kafka Streams configuration and topology definition.
 * Uses Confluent Schema Registry with Avro serialization.
 * <p>
 * Default Serdes configured in application.yaml:
 * - Key: StringSerde
 * - Value: SpecificAvroSerde (auto-configured with schema.registry.url)
 */
@Configuration
@EnableKafkaStreams
@Slf4j
public class KafkaStreamsConfig {

    public static final String INPUT_TOPIC = "input-topic";
    public static final String OUTPUT_TOPIC = "output-topic";
    public static final String FILTERED_TOPIC = "filtered-topic";

    public static final String EMPLOYEE_TOPIC = "employee-topic";
    public static final String ENGINEERING_TOPIC = "engineering-employees";
    public static final String EMPLOYEE_WITH_BONUS_TOPIC = "employee-with-bonus";
    public static final String EMPLOYEE_BY_DEPT_TOPIC = "employee-by-dept";

    /**
     * Simple String stream topology.
     * Uses explicit String serdes (overrides default Avro serde).
     */
    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> stream = streamsBuilder.stream(
                INPUT_TOPIC,
                Consumed.with(Serdes.String(), Serdes.String())  // Override default for String topics
        );

        stream.peek((key, value) -> log.info("Received - Key: {}, Value: {}", key, value))
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        stream.filter((key, value) -> value.length() > 5)
                .peek((key, value) -> log.info("Filtered (length > 5) - Key: {}, Value: {}", key, value))
                .to(
                        FILTERED_TOPIC, Produced.with(Serdes.String(), Serdes.String()) // Explicitly mention the SERDE
                );

        return stream;
    }

    /**
     * Employee stream with Avro serialization.
     * Uses DEFAULT serdes from application.yaml - no explicit serde needed!
     */
    @Bean
    public KStream<String, Employee> employeeStream(StreamsBuilder streamsBuilder) {
        // No Consumed.with() - uses default serdes from application.yaml
        KStream<String, Employee> employeeStream = streamsBuilder.stream(EMPLOYEE_TOPIC);

        // Filter employees from Engineering department
        employeeStream
                .peek((key, emp) -> log.info("Received Employee - Key: {}, Name: {}, Dept: {}",
                        key, emp.getName(), emp.getDepartment()))
                .filter((key, emp) -> "Engineering".equalsIgnoreCase(emp.getDepartment().toString()))
                .peek((key, emp) -> log.info("Engineering Employee - Key: {}, Name: {}", key, emp.getName()))
                .to(ENGINEERING_TOPIC);  // No Produced.with() - uses defaults from application.yml

        // mapValues() - Give 10% bonus
        employeeStream
                .mapValues(emp -> Employee.newBuilder()
                        .setId(emp.getId())
                        .setName(emp.getName())
                        .setDepartment(emp.getDepartment())
                        .setDesignation(emp.getDesignation())
                        .setSalary(emp.getSalary() * 1.10)
                        .build())
                .peek((key, emp) -> log.info("With Bonus - Key: {}, Name: {}, Salary: {}",
                        key, emp.getName(), emp.getSalary()))
                .to(EMPLOYEE_WITH_BONUS_TOPIC);

        // map() - Re-key by department
        employeeStream
                .map((key, emp) -> KeyValue.pair(emp.getDepartment().toString(), emp))
                .peek((key, emp) -> log.info("By Dept - Key: {}, Name: {}", key, emp.getName()))
                .to(EMPLOYEE_BY_DEPT_TOPIC);

        return employeeStream;
    }
}
