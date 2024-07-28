package com.epam.resourceservice.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.epam.resourceservice.BaseIntegrationTest;
import com.epam.resourceservice.dto.DeletedFilesDTO;
import com.epam.resourceservice.dto.UploadedFileDTO;
import com.epam.resourceservice.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
class ResourceControllerTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    ResourceRepository resourceRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AmazonS3 amazonS3;

    @BeforeEach
    void setup() {
        resourceRepository.deleteAll();
        if (!amazonS3.doesBucketExistV2("song-bucket")) {
            amazonS3.createBucket("song-bucket");
        }
    }
    
    @Test
    void upload() throws IOException {
        InputStream inStream = getClass().getClassLoader().getResourceAsStream(
                "file/music.mp3");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<byte[]> entity = new HttpEntity<>(inStream.readAllBytes(), headers);

        ResponseEntity<UploadedFileDTO> response = restTemplate.postForEntity("http://localhost:" + port + "/api/resources", entity,
                UploadedFileDTO.class);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    void uploadWithEmptyData() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<byte[]> entity = new HttpEntity<>(new byte[]{}, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/api/resources", entity,
                String.class);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    void downloadFile() throws IOException {
        InputStream inStream = getClass().getClassLoader().getResourceAsStream(
                "file/music.mp3");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<byte[]> entity = new HttpEntity<>(inStream.readAllBytes(), headers);

        ResponseEntity<UploadedFileDTO> uploadedFile = restTemplate.postForEntity("http://localhost:" + port + "/api/resources", entity,
                UploadedFileDTO.class);

        assertNotNull(uploadedFile);
        assertNotNull(uploadedFile.getBody());

        ResponseEntity<Resource> responseEntity =
                restTemplate.getForEntity("http://localhost:" + port + "/api/resources/" + uploadedFile.getBody().getId(), org.springframework.core.io.Resource.class);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());assertNotNull(responseEntity.getBody());
    }

    @Test
    void downloadNotExistFile() {
        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity("http://localhost:" + port + "/api/resources/100", String.class);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }


    @Test
    void deleteFile() throws IOException {
        InputStream inStream = getClass().getClassLoader().getResourceAsStream(
                "file/music.mp3");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<byte[]> entity = new HttpEntity<>(inStream.readAllBytes(), headers);

        ResponseEntity<UploadedFileDTO> uploadedFile = restTemplate.postForEntity("http://localhost:" + port + "/api/resources", entity,
                UploadedFileDTO.class);

        assertNotNull(uploadedFile);
        assertNotNull(uploadedFile.getBody());

        Integer resourceId = uploadedFile.getBody().getId();


        HttpHeaders deleteRequestHeaders = new HttpHeaders();
        deleteRequestHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

        ResponseEntity<DeletedFilesDTO> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/api/resources?id=" + resourceId,
                HttpMethod.DELETE,
                new HttpEntity<>(deleteRequestHeaders),
                DeletedFilesDTO.class);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getIds());
        assertEquals(1, responseEntity.getBody().getIds().size());
        assertEquals(responseEntity.getBody().getIds().getFirst(), resourceId);
    }
}
