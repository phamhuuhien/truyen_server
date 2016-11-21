package com.phh.storyserver.models;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by phhien on 11/21/2016.
 */
@Data
@Entity
public class Chap {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String content;

    @ManyToOne
    private Story story;
}
