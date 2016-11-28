package com.phh.storyserver.models;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by phhien on 11/21/2016.
 */
@Data
@Entity
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int author_id;
    private String name;
    private String des;
    private String status;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "story_type", joinColumns = @JoinColumn(name = "story_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "type_id", referencedColumnName = "id"))
    private Set<Type> types;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    private Set<Chap> chaps;
}
