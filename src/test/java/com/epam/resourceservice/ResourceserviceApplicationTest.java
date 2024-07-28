package com.epam.resourceservice;

import com.epam.resourceservice.controller.ResourceController;
import com.epam.resourceservice.service.AmazonS3Service;
import com.epam.resourceservice.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ResourceserviceApplicationTest extends BaseIntegrationTest {

    @Autowired
    ResourceController resourceController;

    @Autowired
    ResourceService resourceService;

    @Autowired
    AmazonS3Service amazonS3Service;

    @Test
    void contextLoads() {
        assertNotNull(resourceController);
        assertNotNull(resourceService);
        assertNotNull(amazonS3Service);
    }

}
