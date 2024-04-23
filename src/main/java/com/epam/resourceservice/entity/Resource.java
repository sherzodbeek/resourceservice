package com.epam.resourceservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "resource", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(name = "name")
    String name;

    @Lob
    @Column(name = "content")
    byte[] content;


    public Resource(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }
}


