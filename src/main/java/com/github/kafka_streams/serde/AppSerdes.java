package com.github.kafka_streams.serde;

import com.github.kafka_streams.model.Employee;
import org.apache.kafka.common.serialization.Serde;

/**
 * Centralized Serde instances for the application.
 * Serdes are created once and reused across all streams.
 */
public final class AppSerdes {

    private AppSerdes() {} // Prevent instantiation

    // Lazy initialization holder pattern - thread-safe singleton
    private static class EmployeeSerdeHolder {
        static final Serde<Employee> INSTANCE = new JsonSerde<>(Employee.class);
    }

    public static Serde<Employee> employee() {
        return EmployeeSerdeHolder.INSTANCE;
    }

}
