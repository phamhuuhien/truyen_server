package com.phh.storyserver.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by phhien on 11/21/2016.
 */
@Setter
@Getter
@Entity
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String des;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "types")
    private Set<Story> stories = new HashSet<>();
}
