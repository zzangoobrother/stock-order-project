package com.example.itemapi.documentation;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
@Testcontainers
public abstract class TestContainerSupport {
    private static final String MYSQL_IMAGE = "mysql:8";
    private static final String REDIS_IMAGE = "redis:latest";
    private static final int REDIS_PORT = 6379;
    private static final String KAFKA_IMAGE = "confluentinc/cp-kafka";
    private static final int KAFKA_PORT = 9092;

    private static final JdbcDatabaseContainer MYSQL;
    private static final GenericContainer REDIS;
    private static final ConfluentKafkaContainer KAFKA;

    static {
        MYSQL = new MySQLContainer(MYSQL_IMAGE);
        REDIS = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
                .withExposedPorts(REDIS_PORT)
                        .withReuse(true);
        KAFKA = new ConfluentKafkaContainer(DockerImageName.parse(KAFKA_IMAGE))
                .withExposedPorts(KAFKA_PORT)
                .withReuse(true);

        MYSQL.start();
        REDIS.start();
        KAFKA.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));

        registry.add("spring.data.cluster.redis.host1", REDIS::getHost);
        registry.add("spring.data.cluster.redis.port1", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));

        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);

        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }
}
