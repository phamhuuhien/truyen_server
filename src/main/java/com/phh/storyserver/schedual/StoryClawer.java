package com.phh.storyserver.schedual;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by phhien on 10/21/2016.
 */
@Component
public class StoryClawer {

    private static final Logger log = LoggerFactory.getLogger(StoryClawer.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    @Scheduled(cron = "0 51 17 * * *")
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
        List<HashMap<String, String>> result = createFolder();

    }

    private List<HashMap<String, String>> createFolder() {
        List<HashMap<String, String>> result = new ArrayList<>();
        File folder = new File("D:\\storyFolder");
        if(!folder.exists()) {
            folder.mkdir();
        }

        for(int i = 1; i <=1; i++) {
            try {
                String origin_url = "http://truyenyy.com/truyen-ngon-tinh/";
                String url = origin_url + "?page=" + i;
                Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
                List<Element> titles = doc.select(".truyen-item");
                titles.stream().forEach(t -> {
                    HashMap<String, String> item = new HashMap<String, String>();
                    String link = t.select(".media-body a").attr("href");
                    String title = t.select(".media-heading").text();
                    item.put("link", link);
                    item.put("title", title);
                    result.add(item);
                    File storyFolder = new File(folder.getAbsolutePath() + "/" + link.substring(8));
                    if(!storyFolder.exists()) {
                        storyFolder.mkdir();
                    }


                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
