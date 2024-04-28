package com.epam.resourceservice.feign;

import com.epam.resourceservice.dto.CreateSongMetadataResponse;
import com.epam.resourceservice.dto.SongMetadataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${song.service.name}", url = "${song.service.url}")
public interface SongServiceClient {

    @PostMapping
    CreateSongMetadataResponse createSongMetaData(@RequestBody SongMetadataDTO dto);
}
