package com.epam.resourceservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SongMetadataDTO {

    private String name;
    private String artist;

    public String getFullName() {
        return this.artist + " - " + this.name + ".mp3";
    }
}
