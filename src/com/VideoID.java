/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.data.YoutubeVideo;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Jaime
 */
public class VideoID {


    private static String PROPERTIES_FILENAME = "youtube.properties";
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
    private static YouTube youtube;

    //FIELDS
    private static final String APIKEY = "";
    private String query;
    private List<YoutubeVideo> response;

    public VideoID() {
    }

    public VideoID(String query) {
        this.query = query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<YoutubeVideo> queryYoutube() {

        response = new ArrayList<>();

        try {
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("musicBoo").build();

            List<Video>        videoResultList  = videoQUERY();

            if (videoResultList != null) {
                loadResponse( videoResultList.iterator() );
            }

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return response;
    }

    private List<Video> videoQUERY() throws IOException {

        YouTube.Videos.List videoList = youtube.videos().list("id,snippet,contentDetails");
        videoList.setKey(APIKEY);
        videoList.setId(query); //+IdUX6K7waag5Q  IdMzqzwa10Y6M
        videoList.setFields("items/id,items/snippet/title,items/snippet/thumbnails/default/url,items/contentDetails/duration");
        VideoListResponse videosResponse = videoList.execute();

        List<Video> videoResponseList = videosResponse.getItems();

        return videoResponseList;
    }

    /**
     * Initializes YouTube object to search for videos on YouTube
     * (Youtube.Search.List). The program then prints the names and thumbnails
     * of each of the videos (only first 50 videos).
     *
     * @param args command line args.
     */
    


    private void loadResponse( Iterator<Video> iteratorVideos ) {

        YoutubeVideo video;

        //getDuration()
        while ( iteratorVideos.hasNext() ) {
            
            Video videoData = iteratorVideos.next();

            // Double checks the kind is video.

                video = new YoutubeVideo();
                
                
                Thumbnail thumbnail = (Thumbnail) videoData.getSnippet().getThumbnails().get("default");
                
                video.setID(videoData.getId());
                video.setImage(thumbnail.getUrl());
                video.setTitle(videoData.getSnippet().getTitle());
                video.setDuration( getTimeFromString(  videoData.getContentDetails().getDuration()  ) );
                
                response.add(video);

            
        }

    }
    
//    private String FormatDuration(String origin){
////        PeriodFormatter formatter = ISOPeriodFormat.standard();
////        Period p = formatter.parsePeriod("PT1H1M13S");
//    }
    private String getTimeFromString(String duration) {
    // TODO Auto-generated method stub
    String time = "";
    boolean hourexists = false, minutesexists = false, secondsexists = false;
    if (duration.contains("H"))
        hourexists = true;
    if (duration.contains("M"))
        minutesexists = true;
    if (duration.contains("S"))
        secondsexists = true;
    if (hourexists) {
        String hour = "";
        hour = duration.substring(duration.indexOf("T") + 1,
                duration.indexOf("H"));
        if (hour.length() == 1)
            hour = "0" + hour;
        time += hour + ":";
    }
    if (minutesexists) {
        String minutes = "";
        if (hourexists)
            minutes = duration.substring(duration.indexOf("H") + 1,
                    duration.indexOf("M"));
        else
            minutes = duration.substring(duration.indexOf("T") + 1,
                    duration.indexOf("M"));
        if (minutes.length() == 1)
            minutes = "0" + minutes;
        time += minutes + ":";
    } else {
        time += "00:";
    }
    if (secondsexists) {
        String seconds = "";
        if (hourexists) {
            if (minutesexists)
                seconds = duration.substring(duration.indexOf("M") + 1,
                        duration.indexOf("S"));
            else
                seconds = duration.substring(duration.indexOf("H") + 1,
                        duration.indexOf("S"));
        } else if (minutesexists)
            seconds = duration.substring(duration.indexOf("M") + 1,
                    duration.indexOf("S"));
        else
            seconds = duration.substring(duration.indexOf("T") + 1,
                    duration.indexOf("S"));
        if (seconds.length() == 1)
            seconds = "0" + seconds;
        time += seconds;
    }
    return time;
}

    
    
    private String getFormattedNumber(float number){
        String pattern = "###,###.###";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        String format = decimalFormat.format(number);
        return format;
    }
    
}


