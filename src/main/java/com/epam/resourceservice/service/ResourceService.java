package com.epam.resourceservice.service;

import com.epam.resourceservice.model.DownloadFileModel;
import com.epam.resourceservice.dto.DeletedFilesDTO;
import com.epam.resourceservice.dto.UploadedFileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {

    UploadedFileDTO upload(MultipartFile multipartFile);

    DownloadFileModel download(Long id);

    DeletedFilesDTO deleteFiles(List<Long> ids);
}
