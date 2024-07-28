package com.epam.resourceservice.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.epam.resourceservice.dto.DeletedFilesDTO;
import com.epam.resourceservice.dto.UploadedFileDTO;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.BadRequestException;
import com.epam.resourceservice.exception.EmptyFileException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.model.DownloadFileModel;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.impl.ResourceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    ResourceRepository resourceRepository;

    @Mock
    AmazonS3Service amazonS3Service;

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    ResourceServiceImpl resourceService;

    @Test
    void upload() throws IOException {
        // given
        Resource resource = new Resource(1, "test", "test");
        InputStream inStream = getClass().getClassLoader().getResourceAsStream(
                "file/music.mp3");
        when(resourceRepository.save(any())).thenReturn(resource);

        // when
        UploadedFileDTO result = resourceService.upload(inStream.readAllBytes());

        // then
        assertNotNull(result);
        assertEquals(resource.getId(), result.getId());
    }

    @Test
    void uploadThrowExceptionWhenFileIsEmpty() {
        // then
        assertThrows(EmptyFileException.class, () -> resourceService.upload(new byte[]{}));
    }

    @Test
    void download() {
        // given
        var resource = new Resource(1, "test", "test");
        when(resourceRepository.findById(any())).thenReturn(Optional.of(resource));
        var s3Object = new S3Object();
        s3Object.setObjectContent(getClass().getClassLoader().getResourceAsStream(
                "file/music.mp3"));
        when(amazonS3Service.download(any())).thenReturn(s3Object);

        // when
        DownloadFileModel result = resourceService.download(1);

        // then
        assertNotNull(result);
        assertEquals(resource.getName(), result.getFileName());
    }

    @Test
    void downloadThrowExceptionWhenFileNotFound() {
        // given
        when(resourceRepository.findById(any())).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFoundException.class, () -> resourceService.download(1));
    }


    @Test
    void downloadThrowAmazonS3ExceptionFromAmazonS3() throws IOException {
        // given
        var resource = new Resource(1, "test", "test");
        when(resourceRepository.findById(any())).thenReturn(Optional.of(resource));
        var s3Object = mock(S3Object.class);
        var s3ObjectInputStream = mock(com.amazonaws.services.s3.model.S3ObjectInputStream.class);

        when(amazonS3Service.download(any())).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        when(s3ObjectInputStream.readAllBytes()).thenThrow(IOException.class);

        // then
        assertThrows(AmazonS3Exception.class, () -> resourceService.download(1));
    }

    @Test
    void deleteThrowBadRequestExceptionWhenIdsAreNull() {
        // then
        assertThrows(BadRequestException.class, () -> resourceService.deleteFiles(null));
    }


    @Test
    void delete() {
        // given
        var resource = new Resource(1, "test", "test");
        when(resourceRepository.findAllById(any())).thenReturn(List.of(resource));

        // when
        DeletedFilesDTO deletedFilesDTO = resourceService.deleteFiles("1,2,3");

        // then
        assertNotNull(deletedFilesDTO);
        assertEquals(resource.getId(), deletedFilesDTO.getIds().getFirst());
    }
}