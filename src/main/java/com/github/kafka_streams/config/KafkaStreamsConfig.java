package com.github.kafka_streams.config;

import com.github.kafka_streams.model.Employee;
import com.github.kafka_streams.serde.AppSerdes;
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
     * Defines the Kafka Streams topology.
     * <p>
     * This is a simple passthrough topology:
     * - Reads from INPUT_TOPIC
     * - Logs each message
     * - Writes to OUTPUT_TOPIC unchanged
     * <p>
     * Topology: input-topic --> [peek/log] --> output-topic
     */
    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> stream = streamsBuilder.stream(
                INPUT_TOPIC,
                Consumed.with(Serdes.String(), Serdes.String())
        );

        // Process: log each message (peek doesn't modify the stream)
        stream
                .peek((key, value) -> log.info("Received - Key: {}, Value: {}", key, value))
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        // filter() - keeps records where predicate returns true
        stream
                .filter((key, value) -> value.length() > 5)
                .peek((key, value) -> log.info("Filtered (length > 5) - Key: {}, Value: {}", key, value))
                .to(FILTERED_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        return stream;
    }
    
    @Bean
    public KStream<String, Employee> employeeStream(StreamsBuilder streamsBuilder) {
        KStream<String, Employee> employeeStream = streamsBuilder.stream(
                EMPLOYEE_TOPIC,
                Consumed.with(Serdes.String(), AppSerdes.employee())
        );

        // Filter employees from Engineering department
        employeeStream
                .peek((key, emp) -> log.info("Received Employee - Key: {}, Name: {}, Dept: {}", key, emp.getName(), emp.getDepartment()))
                .filter((key, emp) -> "Engineering".equalsIgnoreCase(emp.getDepartment()))
                .peek((key, emp) -> log.info("Engineering Employee - Key: {}, Name: {}", key, emp.getName()))
                .to(ENGINEERING_TOPIC, Produced.with(Serdes.String(), AppSerdes.employee()));

        // ============ MAP VALUES EXAMPLE ============
        // mapValues() - transforms only the value, key remains unchanged
        // Use case: Give 10% bonus to all employees
        employeeStream
                .mapValues(emp -> Employee.builder()
                        .id(emp.getId())
                        .name(emp.getName())
                        .department(emp.getDepartment())
                        .designation(emp.getDesignation())
                        .salary(emp.getSalary() * 1.10)  // 10% bonus
                        .build())
                .peek((key, emp) -> log.info("With Bonus - Key: {}, Name: {}, Salary: {}", key, emp.getName(), emp.getSalary()))
                .to(EMPLOYEE_WITH_BONUS_TOPIC, Produced.with(Serdes.String(), AppSerdes.employee()));

        // ============ MAP EXAMPLE ============
        // map() - transforms both key and value
        // Use case: Re-key by department (useful for grouping/joining later)
        // WARNING: Changing key causes repartitioning (network shuffle)
        employeeStream
                .map((key, emp) -> KeyValue.pair(
                        emp.getDepartment(),  // New key = department
                        emp                    // Value unchanged
                ))
                .peek((key, emp) -> log.info("By Dept - Key: {}, Name: {}", key, emp.getName()))
                .to(EMPLOYEE_BY_DEPT_TOPIC, Produced.with(Serdes.String(), AppSerdes.employee()));

        return employeeStream;
    }
}
