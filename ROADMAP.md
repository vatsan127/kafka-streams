# Kafka Streams Learning Roadmap

## Phase 1: Foundations
- [ ] **1.1** Setup local Kafka cluster (Docker Compose)
- [ ] **1.2** Configure Spring Boot Kafka Streams properties
- [ ] **1.3** Create first KStream - simple passthrough topology
- [ ] **1.4** Understand Serdes (Serializers/Deserializers)

## Phase 2: Stateless Operations
- [ ] **2.1** Filter - filter messages based on conditions
- [ ] **2.2** Map/MapValues - transform messages
- [ ] **2.3** FlatMap - one-to-many transformations
- [ ] **2.4** Branch - split stream into multiple streams
- [ ] **2.5** Merge - combine multiple streams

## Phase 3: Stateful Operations
- [ ] **3.1** GroupByKey/GroupBy - prepare for aggregations
- [ ] **3.2** Count/Reduce/Aggregate - basic aggregations
- [ ] **3.3** KTable basics - changelog streams
- [ ] **3.4** KStream-KTable joins
- [ ] **3.5** KTable-KTable joins
- [ ] **3.6** KStream-KStream joins (windowed)

## Phase 4: Windowing
- [ ] **4.1** Tumbling windows - fixed, non-overlapping
- [ ] **4.2** Hopping windows - fixed, overlapping
- [ ] **4.3** Sliding windows - for joins
- [ ] **4.4** Session windows - activity-based
- [ ] **4.5** Suppression - control downstream updates

## Phase 5: State Stores & Interactive Queries
- [ ] **5.1** State store types (RocksDB, In-Memory)
- [ ] **5.2** Custom state stores
- [ ] **5.3** Interactive queries - expose state via REST
- [ ] **5.4** Queryable state across instances

## Phase 6: Error Handling & Production Readiness
- [ ] **6.1** Deserialization error handlers
- [ ] **6.2** Production exception handlers
- [ ] **6.3** Dead letter queues
- [ ] **6.4** Exactly-once semantics (EOS)
- [ ] **6.5** Monitoring with metrics

## Phase 7: Advanced Topics
- [ ] **7.1** Custom processors (Processor API)
- [ ] **7.2** Punctuators - scheduled operations
- [ ] **7.3** Global KTables
- [ ] **7.4** Topology optimization
- [ ] **7.5** Testing with TopologyTestDriver

---

## Practical Projects (Build as we learn)
1. **Word Count** (Phase 2-3) - Classic streaming example
2. **User Activity Tracker** (Phase 3-4) - Sessions, aggregations
3. **Real-time Dashboard** (Phase 5) - Interactive queries + REST
4. **Order Processing** (Phase 6-7) - Production-grade pipeline
