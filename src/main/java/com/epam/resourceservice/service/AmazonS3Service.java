package com.epam.resourceservice.service;

import com.amazonaws.services.s3.model.*;

import java.io.InputStream;

public interface AmazonS3Service {

    void upload(
            String path,
            String fileName,
           ObjectMetadata objectMetadata,
            InputStream inputStream);

    S3Object download(GetObjectRequest getObjectRequest);

    void delete(DeleteObjectRequest deleteObjectRequest);
}
