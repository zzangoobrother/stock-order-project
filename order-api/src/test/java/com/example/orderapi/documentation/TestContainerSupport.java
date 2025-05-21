package com.example.orderapi.documentation;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
@Testcontainers
public abstract class TestContainerSupport {
    private static final String MYSQL_IMAGE = "mysql:8";
    private static final String KAFKA_IMAGE = "confluentinc/cp-kafka";
    private static final int KAFKA_PORT = 9092;

    private static final JdbcDatabaseContainer MYSQL;
    private static final ConfluentKafkaContainer KAFKA;

    static {
        MYSQL = new MySQLContainer(MYSQL_IMAGE);
        KAFKA = new ConfluentKafkaContainer(DockerImageName.parse(KAFKA_IMAGE))
                .withExposedPorts(KAFKA_PORT)
                .withReuse(true);

        MYSQL.start();
        KAFKA.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);

        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }
}
