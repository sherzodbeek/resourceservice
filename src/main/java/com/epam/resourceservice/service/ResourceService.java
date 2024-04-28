package com.epam.resourceservice.service;

import com.epam.resourceservice.dto.DeletedFilesDTO;
import com.epam.resourceservice.dto.UploadedFileDTO;
import com.epam.resourceservice.model.DownloadFileModel;

public interface ResourceService {

    UploadedFileDTO upload(byte[] file);

    DownloadFileModel download(Integer id);

    DeletedFilesDTO deleteFiles(String ids);
}
