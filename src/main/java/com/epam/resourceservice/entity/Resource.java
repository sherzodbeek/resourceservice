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
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "path")
    String path;

    public Resource(String name, String path) {
        this.name = name;
        this.path = path;
    }
}


