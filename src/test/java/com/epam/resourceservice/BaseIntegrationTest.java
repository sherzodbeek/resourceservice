package com.epam.resourceservice;

import org.junit.jupiter.api.AfterAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgreSQL = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("resource")
            .waitingFor(new WaitAllStrategy()
                    .withStrategy(Wait.forListeningPort())
                    .withStrategy(Wait.forLogMessage(".*database system is ready to accept connections.*\\s", 2)
                            .withStartupTimeout(Duration.ofSeconds(60))
                    )
            );

    @Container
    static final LocalStackContainer localStack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:s3-latest"));

    @Container
    static final RabbitMQContainer rabbitContainer =
            new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-alpine"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        postgreSQL.start();
        localStack.start();
        rabbitContainer.start();
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQL::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQL::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQL::getPassword);
        dynamicPropertyRegistry.add("config.aws.region", localStack::getRegion);
        dynamicPropertyRegistry.add("config.aws.s3.url", localStack::getEndpoint);
        dynamicPropertyRegistry.add("config.aws.s3.access-key", localStack::getAccessKey);
        dynamicPropertyRegistry.add("config.aws.s3.secret-key", localStack::getSecretKey);
        dynamicPropertyRegistry.add("spring.rabbitmq.host", rabbitContainer::getHost);
        dynamicPropertyRegistry.add("spring.rabbitmq.password", rabbitContainer::getAdminPassword);
        dynamicPropertyRegistry.add("spring.rabbitmq.port", rabbitContainer::getAmqpPort);
        dynamicPropertyRegistry.add("spring.rabbitmq.username", rabbitContainer::getAdminUsername);
    }

    @AfterAll
    static void tearDown() {
        postgreSQL.stop();
        localStack.stop();
        rabbitContainer.stop();
    }
}
