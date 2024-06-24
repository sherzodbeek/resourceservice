package com.epam.resourceservice.service.impl;

import com.amazonaws.services.s3.model.*;
import com.epam.resourceservice.dto.DeletedFilesDTO;
import com.epam.resourceservice.dto.SongMetadataDTO;
import com.epam.resourceservice.dto.UploadedFileDTO;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.BadRequestException;
import com.epam.resourceservice.exception.EmptyFileException;
import com.epam.resourceservice.exception.MetadataParserException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.model.DownloadFileModel;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.AmazonS3Service;
import com.epam.resourceservice.service.ResourceService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    @Value("${config.aws.s3.bucket-name}")
    private String bucketName;

    private static final String AUDIO_FILE_TYPE = "audio/mpeg";

    private final ResourceRepository resourceRepository;
    private final AmazonS3Service s3Service;

    public ResourceServiceImpl(ResourceRepository resourceRepository, AmazonS3Service s3Service) {
        this.resourceRepository = resourceRepository;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional
    public UploadedFileDTO upload(byte[] file) {
        if (file == null || file.length == 0)
            throw new EmptyFileException("Cannot upload empty file");

        SongMetadataDTO songMetadata = getSongMetadata(file);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.length);
        objectMetadata.setContentType(AUDIO_FILE_TYPE);

        String path = String.format("%s/%s", UUID.randomUUID(), songMetadata.getFullName());

        // Uploading file to s3
        log.info("{} file is being stored to S3 {} bucket", songMetadata.getFullName(), bucketName);
        s3Service.upload(bucketName, path, objectMetadata, new ByteArrayInputStream(file));

        Resource savedResource = resourceRepository.save(new Resource(songMetadata.getFullName(), path));

        return new UploadedFileDTO(savedResource.getId());
    }

    @Override
    public DownloadFileModel download(Integer id) {
        Resource resource = resourceRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("The resource with the specified id (%d) does not exist", id)));

        log.info("File {} is being retrieved from S3 {} bucket", resource.getPath(), bucketName);
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, resource.getPath());
        try(S3Object s3Object = s3Service.download(getObjectRequest)) {
            return DownloadFileModel.builder()
                    .fileName(resource.getName())
                    .contentType(AUDIO_FILE_TYPE)
                    .bytes(s3Object.getObjectContent().readAllBytes())
                    .build();
        } catch (IOException ex) {
            throw new AmazonS3Exception("Exception occurred while retrieving file from S3", ex);
        }
    }

    @Override
    public DeletedFilesDTO deleteFiles(String ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("IDs not provided");
        }

        List<Integer> deletingIds = Arrays
                .stream(ids.split(","))
                .map(Integer::valueOf)
                .toList();

        List<Resource> resources = resourceRepository.findAllById(deletingIds);

        resources.stream()
                .map(r -> new DeleteObjectRequest(bucketName, r.getPath()))
                .forEach(s3Service::delete);

        List<Integer> deletedIds = resources.stream().map(Resource::getId).toList();
        log.info("Files with id {} are being deleted from DB and S3", deletedIds);
        resourceRepository.deleteAll(resources);
        return DeletedFilesDTO.builder()
                .ids(deletedIds)
                .build();
    }

    private SongMetadataDTO getSongMetadata(byte[] resource) {
        try (InputStream input = new ByteArrayInputStream(resource)) {
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            ParseContext parseCtx = new ParseContext();
            Parser parser = new Mp3Parser();
            parser.parse(input, handler, metadata, parseCtx);

            return SongMetadataDTO.builder()
                    .name(metadata.get("dc:title"))
                    .artist(metadata.get("xmpDM:artist"))
                    .build();
        } catch (IOException | TikaException | SAXException e) {
            throw new MetadataParserException("Exception occurred while reading object metadata", e);
        }
    }
}
