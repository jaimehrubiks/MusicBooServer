/*
 * JAIME HIDALGO.
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.data.YoutubeVideo;
import com.io.TextFile;
import com.network.NetOperations;
import com.network.TCPsocket;
import java.net.Socket;
import java.util.List;


/**
 *
 * @author Jaime Hidalgo García
 */
class ServerThread implements Runnable {

    private final TCPsocket socket;
    
    public ServerThread(Socket accept) {
        socket = new TCPsocket(accept);
    }

    @Override
    public void run() {
        
        int op = socket.getBYTE();
        
        if(op == NetOperations.KEYWORD_SEARCH_QUERY)
            search_query();
        else if(op == NetOperations.VIDEOID_GET_QUERY)
            videoid_query();

        socket.closeConnection();
        
    }
    
    private void search_query(){
        
        String query = socket.getSTRING();
        
        Search mySearch = new Search();
        mySearch.setQuery(query);
        
        List<YoutubeVideo> videoList = mySearch.queryYoutube();
        
        
        try{
            System.out.println(videoList.size());
            socket.sendBYTE(videoList.size());
            for(YoutubeVideo video : videoList)
                socket.sendVIDEO(video);
            socket.closeConnection();
        } catch(Exception e){
            System.out.println(e.toString());
        }
        
        
        
    }
    
    private void videoid_query(){
        
        String query = socket.getSTRING();
        
        VideoID videoidquery = new VideoID();
        videoidquery.setQuery(query);
        
        List<YoutubeVideo> videoList  = videoidquery.queryYoutube();
        
        
        try{
            System.out.println(videoList.size());
            socket.sendBYTE(videoList.size());
            for(YoutubeVideo video : videoList)
                socket.sendVIDEO(video);
            socket.closeConnection();
        } catch(Exception e){
            System.out.println(e.toString());
        }
        
    }

    
    
    
}
