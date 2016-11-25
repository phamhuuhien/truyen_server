package com.phh.storyserver.schedual;

import com.phh.storyserver.models.Author;
import com.phh.storyserver.models.Chap;
import com.phh.storyserver.models.Story;
import com.phh.storyserver.repositories.AuthorRepository;
import com.phh.storyserver.repositories.ChapRepository;
import com.phh.storyserver.repositories.StoryRepository;
import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private ChapRepository chapRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
        List<HashMap<String, String>> result = createFolder();
        result.stream().forEach(item -> {
            log.info("Claw Story: " + item.get("title"));
            Pair data = clawStory(item);
            clawChap(item, (String)data.getKey(), (Story)data.getValue());
//            try {
//                Thread.sleep(120000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
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

    private Pair<String, Story> clawStory(HashMap<String, String> params) {
        String url = WEB_LINK + params.get("link");
        String linkFirstChap = "";
        Story story = new Story();
        try {
            String folderPath = FOLDER_PATH + "/" + params.get("link").substring(8);
            String filePath = folderPath + "/desc.txt";
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath), "UTF-8"));
            Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
            String title = doc.select(".rofx h1").get(0).text();
            out.write("Title:" + title);
            out.write(System.getProperty("line.separator"));
            String desc = doc.select("#desc_story p").toString();
            out.write("Description:" + desc);
            out.write(System.getProperty("line.separator"));

            Elements infor = doc.select(".lww");
            Elements infor1 = infor.get(0).select("p");
            int i = 0;
            infor1.stream().forEach(item -> {
                try {
                    String text = item.text();
                    if(text.indexOf("Tác Giả") != -1) {
                        String authorName = text.split(":")[1].trim();
                        Author author = authorRepository.findByName(authorName);
                        if(author == null) {
                            author = new Author();
                            author.setName(authorName);
                            authorRepository.save(author);
                        }
                    }
                    out.write(item.text());
                    out.write(System.getProperty("line.separator"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            out.close();

            String linkImage = doc.select(".thumbnail img").attr("src");
            log.info("link image = " + linkImage);
            saveImageToFile(linkImage, folderPath);

            Elements chapList = doc.select("#dschuong div");
            linkFirstChap = chapList.get(0).select("a[href]").attr("href");


            story.setName(title);
            story.setDes(desc);
            storyRepository.save(story);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Pair<>(linkFirstChap, story);
    }

    private void clawChap(HashMap<String, String> params, String linkFirstChap, Story story) {
        String linkChap = linkFirstChap;
        try {
            int i = 1;
            while(true) {
                log.info("Claw Chap: " + i);
                String filePath = FOLDER_PATH + "/" + params.get("link").substring(8) + "/chap" + i + ".txt";
                Document doc = Jsoup.connect(linkChap).userAgent("Mozilla").get();
                Writer out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(filePath), "UTF-8"));
                String title = doc.select("#noidungtruyen h1").text();
                String chapContent = doc.select("#id_noidung_chuong").toString();
                log.info("chapContent length = " + chapContent.length());
                if(chapContent.length() < 400) {
                    Thread.sleep(120000);
                    continue;
                }
                out.write(title);
                out.write(System.getProperty("line.separator"));
                out.write(chapContent);
                out.close();
                i++;
                Elements navigations = doc.select(".mobi-chuyentrang a");
                Elements lastNavigation = navigations.get(navigations.size() - 1).select("a[href]");
                linkChap = lastNavigation.attr("href");
                if(!lastNavigation.text().trim().equals("Sau")) {
                    break;
                }

                Chap chap = new Chap();
                chap.setName(title);
                chap.setContent(chapContent);
                chap.setStory(story);
                chapRepository.save(chap);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveImageToFile(String link, String folderPath) {
        try {
            URL url = new URL("http:" + link);
            HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
            httpcon.addRequestProperty("User-Agent", "");
            BufferedImage image = ImageIO.read(httpcon.getInputStream());
            File outputfile = new File(folderPath + "/cover_image.jpg");
            ImageIO.write(image, "jpg", outputfile);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
