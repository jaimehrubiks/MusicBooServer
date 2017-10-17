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
public class Search {

    /**
     * Global instance properties filename.
     */
    private static String PROPERTIES_FILENAME = "youtube.properties";

    /**
     * Global instance of the HTTP transport.
     */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    /**
     * Global instance of the max number of videos we want returned (50 = upper
     * limit per page).
     */
    private static final long NUMBER_OF_VIDEOS_RETURNED = 15;

    /**
     * Global instance of Youtube object to make all API requests.
     */
    private static YouTube youtube;

    //FIELDS
    private static final String APIKEY = "";
    private String query;
    private List<YoutubeVideo> response;

    public Search() {
    }

    public Search(String query) {
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

            List<SearchResult> searchResultList = searchQUERY();
            List<Video>        videoResultList  = videoQUERY(searchResultList);

            if (searchResultList != null) {
                loadResponse(searchResultList.iterator(),videoResultList.iterator(), query);
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

    private List<SearchResult> searchQUERY() throws IOException {
        YouTube.Search.List search = youtube.search().list("id,snippet");

        search.setKey(APIKEY);
        search.setQ(query);
        search.setType("video");
        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url,snippet/channelTitle)");
        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();

        return searchResultList;
    }

    private List<Video> videoQUERY(List<SearchResult> search) throws IOException {

        Iterator<SearchResult> searchIt = search.iterator();
        StringBuilder sb = new StringBuilder();
        if (searchIt.hasNext()) {
            sb.append(searchIt.next().getId().getVideoId());
        }
        while (searchIt.hasNext()) {
            sb.append(",").append(searchIt.next().getId().getVideoId());
        }

        YouTube.Videos.List videoList = youtube.videos().list("contentDetails,statistics");
        videoList.setKey(APIKEY);
        //videosList.setPart("contentDetails/duration");
        videoList.setId(sb.toString()); //+IdUX6K7waag5Q  IdMzqzwa10Y6M
        //videosList.setMaxResults(5L);
        videoList.setFields("items/contentDetails/duration,items/statistics/viewCount");
        VideoListResponse videosResponse = videoList.execute();
        
        //System.out.println("aa"+sb.toString());

        List<Video> videoResponseList = videosResponse.getItems();
        //System.out.println(videosResponse.getItems().get(0).getContentDetails().getDuration() );
        return videoResponseList;
    }

    /**
     * Initializes YouTube object to search for videos on YouTube
     * (Youtube.Search.List). The program then prints the names and thumbnails
     * of each of the videos (only first 50 videos).
     *
     * @param args command line args.
     */
    


    private void loadResponse(Iterator<SearchResult> iteratorSearchResults, Iterator<Video> iteratorVideos, String query) {

        YoutubeVideo video;

        //getDuration()
        while (iteratorSearchResults.hasNext() && iteratorVideos.hasNext() ) {

            SearchResult singleVideo = iteratorSearchResults.next();
            Video videoStats = iteratorVideos.next();
            ResourceId rId = singleVideo.getId();

            // Double checks the kind is video.
            if (rId.getKind().equals("youtube#video")) {

                video = new YoutubeVideo();

                Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");

                video.setID(rId.getVideoId());
                video.setTitle(singleVideo.getSnippet().getTitle());
                video.setImage(thumbnail.getUrl());
                video.setDuration( getTimeFromString(  videoStats.getContentDetails().getDuration()  ) );
                video.setUploader(  singleVideo.getSnippet().getChannelTitle()   );
                video.setHits(  getFormattedNumber( videoStats.getStatistics().getViewCount().floatValue()  ) );

                response.add(video);

            }
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


