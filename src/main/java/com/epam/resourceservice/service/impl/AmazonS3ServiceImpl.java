package com.epam.resourceservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.epam.resourceservice.service.AmazonS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class AmazonS3ServiceImpl implements AmazonS3Service {

    private final AmazonS3 amazonS3;

    @Autowired
    public AmazonS3ServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public void upload(
            String bucketName,
            String filePath,
            ObjectMetadata objectMetadata,
            InputStream inputStream) {
        amazonS3.putObject(bucketName, filePath, inputStream, objectMetadata);
    }

    @Override
    public S3Object download(GetObjectRequest request) {
        return amazonS3.getObject(request);
    }

    @Override
    public void delete(DeleteObjectRequest deleteObjectRequest) {
        amazonS3.deleteObject(deleteObjectRequest);
    }
}
