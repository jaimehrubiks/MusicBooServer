/*
 * JAIME HIDALGO.
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.io.ErrorLogger;
import java.io.IOException;
import java.net.ServerSocket;

/**
 *
 * @author Jaime Hidalgo García
 */
public class Server {

    /**
     * @param args the command line arguments
     */

    private final int portNumber;

    public Server(int port) {
        this.portNumber = port;
    }

    public void run() {

        boolean listening = true;
        
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            
            while (listening) {
                Thread t = new Thread(new ServerThread(serverSocket.accept()));
                t.start();
            }
            
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber + " .Check port availability and internet connection");
            ErrorLogger.toFile("IOError", e.toString());
            System.exit(-1);
        }

    }
    
    public static void main(String[] args){
        
        boolean listening = true;
        
        if (args.length != 1){
            System.out.println("Usage: MusicBoo PORT");
            System.exit(0);
        }
        
        Server runningServer = new Server(Integer.parseInt( args[0] ));
        runningServer.run();
        
        while(listening){
            
        }
            
    }
    
}
