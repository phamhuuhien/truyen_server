package com.phh.storyserver.schedual;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
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

    private static final String FOLDER_PATH = "D:\\storyFolder";
    private static final String WEB_LINK = "http://truyenyy.com/";
    private static final Logger log = LoggerFactory.getLogger(StoryClawer.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    @Scheduled(cron = "0 51 17 * * *")
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
        List<HashMap<String, String>> result = createFolder();
        result.stream().forEach(item -> {
            log.info("Claw Story: " + item.get("title"));
            String linkFirstChap = clawStory(item);
            clawChap(item, linkFirstChap);
            try {
                Thread.sleep(120000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        log.info("Finish");
    }

    private List<HashMap<String, String>> createFolder() {
        List<HashMap<String, String>> result = new ArrayList<>();
        File folder = new File(FOLDER_PATH);
        if(!folder.exists()) {
            folder.mkdir();
        }

        for(int i = 1; i <=1; i++) {
            try {
                String origin_url = WEB_LINK + "truyen-ngon-tinh/";
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

    private String clawStory(HashMap<String, String> params) {
        String url = WEB_LINK + params.get("link");
        String linkFirstChap = "";
        try {
            String filePath = FOLDER_PATH + "/" + params.get("link").substring(8) + "/desc.txt";
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath), "UTF-8"));
            Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
            String desc = doc.select("#desc_story p").toString();
            out.write("Description:" + desc);
            out.write(System.getProperty("line.separator"));

            Elements infor = doc.select(".lww");
            Elements infor1 = infor.get(0).select("p");
            infor1.stream().forEach(item -> {
                try {
                    out.write(item.text());
                    out.write(System.getProperty("line.separator"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            out.close();

            Elements chapList = doc.select("#dschuong div");
            linkFirstChap = chapList.get(0).select("a[href]").attr("href");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return linkFirstChap;
    }

    private void clawChap(HashMap<String, String> params, String linkFirstChap) {
        String linkChap = linkFirstChap;
        try {
            int i = 1;
            while(true) {
                log.info("Claw Chap: " + i);
                String filePath = FOLDER_PATH + "/" + params.get("link").substring(8) + "/chap" + i + ".txt";
                Document doc = Jsoup.connect(linkChap).userAgent("Mozilla").get();
                Writer out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(filePath), "UTF-8"));
                String chapContent = doc.select("#id_noidung_chuong").toString();
                log.info("chapContent length = " + chapContent.length());
                if(chapContent.length() < 400) {
                    Thread.sleep(120000);
                    continue;
                }
                out.write(chapContent);
                out.close();
                i++;
                Elements navigations = doc.select(".mobi-chuyentrang a");
                Elements lastNavigation = navigations.get(navigations.size() - 1).select("a[href]");
                linkChap = lastNavigation.attr("href");
                if(!lastNavigation.text().trim().equals("Sau")) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
