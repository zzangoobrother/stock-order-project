package com.example.queueapi.documentation;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
@Testcontainers
public abstract class TestContainerSupport {
    private static final String REDIS_IMAGE = "redis:latest";
    private static final int REDIS_PORT = 6379;

    private static final GenericContainer REDIS;

    static {
        REDIS = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
                .withExposedPorts(REDIS_PORT)
                .withReuse(true);

        REDIS.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));

        registry.add("spring.data.cluster.redis.host1", REDIS::getHost);
        registry.add("spring.data.cluster.redis.port1", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));
    }
}
