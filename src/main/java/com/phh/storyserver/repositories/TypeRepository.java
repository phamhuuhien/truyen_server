package com.phh.storyserver.repositories;

import com.phh.storyserver.models.Author;
import com.phh.storyserver.models.Type;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by phhien on 11/28/2016.
 */
public interface TypeRepository extends CrudRepository<Type, Integer> {
    Type findByName(final String name);
}
