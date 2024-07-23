package com.epam.resourceservice.cucumber;

import com.epam.resourceservice.BaseIntegrationTest;
import com.epam.resourceservice.ResourceserviceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = { ResourceserviceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CucumberConfig extends BaseIntegrationTest {

}
