package com.epam.resourceservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.epam.resourceservice.service.impl.AmazonS3ServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmazonS3ServiceTest {

    @Mock
    AmazonS3 amazonS3;

    @InjectMocks
    AmazonS3ServiceImpl amazonS3Service;

    @Test
    void upload() {
        // given
        var bucketName = "testBucket";
        var filePath = "testFilePath";
        var metadata = new ObjectMetadata();
        var inputStream = new ByteArrayInputStream(new byte[]{});

        // when
        amazonS3Service.upload(bucketName, filePath, metadata, inputStream);

        //then
        verify(amazonS3).putObject(bucketName, filePath, inputStream, metadata);
    }

    @Test
    void download() {
        // given
        var res = new S3Object();
        when(amazonS3.getObject(any())).thenReturn(res);
        var getObjectRequest = new GetObjectRequest("name", "path");

        // when
        S3Object result = amazonS3Service.download(getObjectRequest);

        // then
        assertNotNull(result);
        assertEquals(result, res);
    }

    @Test
    void delete() {
        // given
        var deleteObjectRequest = new DeleteObjectRequest("name", "path");

        // when
        amazonS3Service.delete(deleteObjectRequest);

        //then
        verify(amazonS3).deleteObject(deleteObjectRequest);
    }
}