package com.epam.resourceservice.feign;

import com.epam.resourceservice.dto.CreateSongMetadataResponse;
import com.epam.resourceservice.dto.SongMetadataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "${SONG_SERVICE_NAME}", url = "${SONG_SERVICE_URL}")
public interface SongServiceClient {

    @PostMapping
    CreateSongMetadataResponse createSongMetaData(@RequestBody SongMetadataDTO dto);
}
