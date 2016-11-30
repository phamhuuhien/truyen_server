package com.phh.storyserver.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phh.storyserver.models.JsonStory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phhien on 11/30/2016.
 */
public class Utils {

    public static String getData(String urlString) {
        StringBuilder result = new StringBuilder();
        URL url = null;
        try {
            url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<JsonStory> getStories() {
        String url = "http://truyenserver.esy.es/stories.php";
        String json = getData(url);
        return (ArrayList<JsonStory>) new Gson().fromJson(json,
                new TypeToken<ArrayList<JsonStory>>() {
                }.getType());
    }
}
