package com.example.abhishekkoranne.news;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    private static final  String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils(){
    }

    public static List<News> fetchNewsData(String requestUrl){
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG, "Error making HTTP REQUEST!",e);
        }
        List<News> news = extractFeatureFromJson(jsonResponse);
        return news;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;
        try{
            url = new URL(stringUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG,"Error building URL!",e);
        }
        return url;
    }
    private static String makeHttpRequest(URL url)throws IOException{
        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* in milliSecond */);
            urlConnection.setReadTimeout(15000 /* in milliSecond */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode()==200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else {
                Log.e(LOG_TAG,"Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG ,"Error retrieving json Response",e);
        }
        finally {
            if (urlConnection != null ){
                urlConnection.disconnect();
            }if (inputStream !=null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    private static List<News> extractFeatureFromJson(String newsJSON){
        if (TextUtils.isEmpty(newsJSON)){
            return null;
        }
        List<News> allNews = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");
            JSONArray newsArray = responseObject.getJSONArray("results");

            for (int i = 0; i < newsArray.length(); i++){
                JSONObject currentNews = newsArray.getJSONObject(i);
                String newsTitle = currentNews.getString("webTitle");
                String newsTopic = currentNews.getString("sectionName");
                JSONArray tags = currentNews.getJSONArray("tags");
                StringBuilder sb = new StringBuilder();
                for (int j=0; j<tags.length(); j++) {
                    JSONObject authors = tags.getJSONObject(j);
                    sb.append(authors.getString("webTitle")+ " ");
                }
                String publisherName =sb.toString();
                String newsUrl = currentNews.getString("webUrl");
                String newsDate = currentNews.getString("webPublicationDate");

                News news = new News(newsTitle,newsTopic,publisherName,newsUrl,newsDate);
                allNews.add(news);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the NEWS JSON results", e);
        }
        return allNews;
    }

}
