package com.epam.resourceservice.service.impl;

import com.epam.resourceservice.dto.CreateSongMetadataResponse;
import com.epam.resourceservice.dto.DeletedFilesDTO;
import com.epam.resourceservice.dto.SongMetadataDTO;
import com.epam.resourceservice.dto.UploadedFileDTO;
import com.epam.resourceservice.entity.Resource;
import com.epam.resourceservice.exception.NotValidFileTypeException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.exception.SongServiceException;
import com.epam.resourceservice.feign.SongServiceClient;
import com.epam.resourceservice.model.DownloadFileModel;
import com.epam.resourceservice.repository.ResourceRepository;
import com.epam.resourceservice.service.ResourceService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    private static final String AUDIO_FILE_TYPE = "audio/mpeg";
    private static final Logger log = LoggerFactory.getLogger(ResourceServiceImpl.class);

    private final ResourceRepository resourceRepository;
    private final SongServiceClient songServiceClient;

    public ResourceServiceImpl(ResourceRepository resourceRepository, SongServiceClient songServiceClient) {
        this.resourceRepository = resourceRepository;
        this.songServiceClient = songServiceClient;
    }

    @Override
    @Transactional
    public UploadedFileDTO upload(MultipartFile file) {
        if (file.isEmpty())
            throw new RuntimeException("Cannot upload empty file");

        validateContentType(file.getContentType());

        Resource savedFile;

        try {
            savedFile = resourceRepository.save(
                    new Resource(file.getOriginalFilename(), file.getBytes()));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        sendMetaDataToSongService(savedFile);

        return new UploadedFileDTO(savedFile.getId());
    }

    private void validateContentType(String contentType) {
        if (contentType == null || !contentType.equals(AUDIO_FILE_TYPE)) {
            throw new NotValidFileTypeException("Validation failed or request body is invalid MP3");
        }
    }

    private void sendMetaDataToSongService(Resource file) {
        SongMetadataDTO songMetadata = getSongMetadata(file);

        try {
            CreateSongMetadataResponse response = songServiceClient.createSongMetaData(songMetadata);
            log.info("SongMetadata with {} id has been created for {}", response.getId(), file.getId());
        } catch (FeignException ex) {
            throw new SongServiceException(ex.getMessage(), ex.status(),
                    StandardCharsets.UTF_8.decode(ex.responseBody().get()).toString());
        }
    }

    @Override
    public DownloadFileModel download(Long id) {
        Resource resource = resourceRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("The resource with the specified id (%d) does not exist", id)));

        return DownloadFileModel.builder()
                .fileName(resource.getName())
                .contentType(AUDIO_FILE_TYPE)
                .bytes(resource.getContent())
                .build();
    }

    @Override
    public DeletedFilesDTO deleteFiles(List<Long> ids) {
        List<Resource> resources = resourceRepository.findAllById(ids);
        List<Long> deletedIds = resources.stream().map(Resource::getId).toList();
        resourceRepository.deleteAll(resources);
        return DeletedFilesDTO.builder()
                .ids(deletedIds)
                .build();
    }

    private SongMetadataDTO getSongMetadata(Resource resource) {
        try (InputStream input = new ByteArrayInputStream(resource.getContent())) {
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            ParseContext parseCtx = new ParseContext();
            Parser parser = new Mp3Parser();
            parser.parse(input, handler, metadata, parseCtx);

            String duration = metadata.get("xmpDM:duration");
            if (duration != null) {
                int d = (int) Double.parseDouble(duration);
                duration = (d / 60) + ":" + (d % 60);
            }

            return SongMetadataDTO.builder()
                    .name(metadata.get("dc:title"))
                    .artist(metadata.get("xmpDM:artist"))
                    .album(metadata.get("xmpDM:album"))
                    .length(duration)
                    .resourceId(resource.getId())
                    .year(metadata.get("xmpDM:releaseDate"))
                    .build();
        } catch (IOException | TikaException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
