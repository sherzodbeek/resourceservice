package com.epam.resourceservice.cucumber;

import com.amazonaws.services.s3.AmazonS3;
import com.epam.resourceservice.dto.UploadedFileDTO;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

public class ResourceControllerStepDefsIntegrationTest extends CucumberConfig {

    private ResponseEntity<UploadedFileDTO> response;

    @LocalServerPort
    private int port;

    @Autowired
    AmazonS3 amazonS3;

    @BeforeStep
    public void createBucket() {
        if (!amazonS3.doesBucketExistV2("song-bucket")) {
            amazonS3.createBucket("song-bucket");
        }
    }

    @When("^the client calls /api/resources")
    public void the_client_sends_post_request_to_resources() throws Throwable {
        InputStream inStream = getClass().getClassLoader().getResourceAsStream(
                "file/music.mp3");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<byte[]> entity = new HttpEntity<>(inStream.readAllBytes(), headers);

        response = new RestTemplate().postForEntity("http://localhost:" + port + "/api/resources", entity,
                UploadedFileDTO.class);
    }

    @Then("^the client receives status code of (\\d+)$")
    public void the_client_receives_status_code_of(int statusCode)  {
        final HttpStatusCode currentStatusCode = response.getStatusCode();
        assertThat(currentStatusCode.value(), is(statusCode));
    }

    @And("^the client receives created file id")
    public void the_client_receives_created_file_id() {
        assertNotNull(response.getBody());
    }
}
