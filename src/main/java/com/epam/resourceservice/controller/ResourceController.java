package com.epam.resourceservice.controller;

import com.epam.resourceservice.model.DownloadFileModel;
import com.epam.resourceservice.dto.DeletedFilesDTO;
import com.epam.resourceservice.dto.UploadedFileDTO;
import com.epam.resourceservice.service.ResourceService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService service;

    public ResourceController(ResourceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UploadedFileDTO> uploadFile(MultipartFile file) {
        UploadedFileDTO upload = service.upload(file);
        return ResponseEntity.ok(upload);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        DownloadFileModel fileModel = service.download(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, fileModel.getContentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"" + fileModel.getFileName() + "\""))
                .body(new ByteArrayResource(fileModel.getBytes()));
    }

    @DeleteMapping
    public ResponseEntity<DeletedFilesDTO> deleteFile(@RequestParam("id") List<Long> ids) {
        DeletedFilesDTO deletedFilesDTO = service.deleteFiles(ids);
        return ResponseEntity.ok().body(deletedFilesDTO);
    }
}
