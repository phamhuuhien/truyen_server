package com.phh.storyserver.repositories;

import com.phh.storyserver.models.Author;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by phhien on 11/25/2016.
 */
public interface AuthorRepository extends CrudRepository<Author, Integer> {
    Author findByName(final String name);
}
