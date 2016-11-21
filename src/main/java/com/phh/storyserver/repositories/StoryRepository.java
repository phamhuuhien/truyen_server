package com.phh.storyserver.repositories;

import com.phh.storyserver.models.Story;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by phhien on 11/21/2016.
 */
public interface StoryRepository extends CrudRepository<Story, Integer> {
}
