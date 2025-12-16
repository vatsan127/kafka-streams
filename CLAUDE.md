# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.0 application for learning Apache Kafka Streams. Uses Java 21 and Maven.

## Build Commands

```bash
./mvnw clean install          # Build
./mvnw spring-boot:run        # Run
./mvnw test                   # Test
./mvnw test -Dtest=ClassName  # Single test
```

## Project Structure

```
src/main/java/com/github/kafka_streams/
├── KafkaStreamsApplication.java      # Entry point
├── config/
│   └── KafkaStreamsConfig.java       # Stream topologies (@EnableKafkaStreams)
├── controller/
│   └── MessageController.java        # REST endpoint for testing
├── model/
│   ├── Employee.java                 # Domain model
│   └── Order.java                    # Domain model
└── serde/
    ├── JsonSerde.java                # Generic JSON serde (production-ready)
    └── AppSerdes.java                # Singleton serde instances
```

## Kafka Topics

| Topic | Key | Value | Purpose |
|-------|-----|-------|---------|
| input-topic | String | String | Basic string input |
| output-topic | String | String | Basic string output |
| filtered-topic | String | String | Filtered strings (length > 5) |
| employee-topic | String | Employee | Employee JSON input |
| engineering-employees | String | Employee | Filtered Engineering dept |
| employee-with-bonus | String | Employee | Employees with 10% salary bonus |
| employee-by-dept | Department | Employee | Re-keyed by department |

## Key Patterns

### Serdes (Serializers/Deserializers)
- Custom `JsonSerde<T>` with shared static ObjectMapper (thread-safe)
- `AppSerdes` provides singleton instances: `AppSerdes.employee()`
- Spring's JsonSerde is deprecated in Spring Boot 4.0 - use custom implementation

### Stream Operations Implemented
- **filter()** - Keep records matching predicate
- **mapValues()** - Transform value only (no repartition)
- **map()** - Transform key+value (causes repartition if key changes)
- **peek()** - Side-effect logging without modifying stream

## Configuration (application.yaml)

Key Kafka Streams properties:
- `application-id` - Consumer group ID
- `processing.guarantee` - at_least_once / exactly_once_v2
- `num.stream.threads` - Parallelism
- `consumer.*` prefix for internal consumer config

## Learning Progress

Completed:
- Phase 1: Foundations (setup, config, basic topology, serdes)
- Phase 2.1: Filter
- Phase 2.2: Map/MapValues

Next:
- Phase 2.3: FlatMap
- Phase 2.4: Branch
- Phase 2.5: Merge
- Phase 3: Stateful Operations (GroupBy, Aggregate, KTable, Joins)
- Phase 4: Windowing
- Phase 5: State Stores & Interactive Queries
- Phase 6: Error Handling & Production (Schema Registry planned here)
- Phase 7: Advanced (Processor API, Testing)

## Notes

- Schema Registry (Confluent/Apicurio) deferred to Phase 6
- MessageController exists for REST-based testing
