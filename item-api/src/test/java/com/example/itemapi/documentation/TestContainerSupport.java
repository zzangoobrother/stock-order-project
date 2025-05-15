package com.example.itemapi.documentation;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
@Testcontainers
public abstract class TestContainerSupport {
    private static final String MYSQL_IMAGE = "mysql:8";
    private static final String REDIS_IMAGE = "redis:latest";
    private static final int REDIS_PORT = 6379;

    private static final JdbcDatabaseContainer MYSQL;
    private static final GenericContainer REDIS;

    static {
        MYSQL = new MySQLContainer(MYSQL_IMAGE);
        REDIS = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
                .withExposedPorts(REDIS_PORT)
                        .withReuse(true);

        MYSQL.start();
        REDIS.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));

        registry.add("spring.data.cluster.redis.host1", REDIS::getHost);
        registry.add("spring.data.cluster.redis.port1", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));
        registry.add("spring.data.cluster.redis.host2", REDIS::getHost);
        registry.add("spring.data.cluster.redis.port2", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));
        registry.add("spring.data.cluster.redis.host3", REDIS::getHost);
        registry.add("spring.data.cluster.redis.port3", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));
        registry.add("spring.data.cluster.redis.host4", REDIS::getHost);
        registry.add("spring.data.cluster.redis.port4", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));
        registry.add("spring.data.cluster.redis.host5", REDIS::getHost);
        registry.add("spring.data.cluster.redis.port5", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));
        registry.add("spring.data.cluster.redis.host6", REDIS::getHost);
        registry.add("spring.data.cluster.redis.port6", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));

        System.out.println("시작");
        System.out.println(MYSQL.getJdbcUrl());
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }
}
