package com.phh.storyserver.controllers;

import java.util.concurrent.atomic.AtomicLong;

import com.phh.storyserver.models.Greeting;
import com.phh.storyserver.schedual.StoryClawer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    StoryClawer storyClawer;

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        storyClawer.reportCurrentTime();
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
}
