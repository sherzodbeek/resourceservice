package com.epam.resourceservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SongMetadataDTO {

    private String name;
    private String artist;
    private String album;
    private String length;
    private Integer resourceId;
    private String year;
}
