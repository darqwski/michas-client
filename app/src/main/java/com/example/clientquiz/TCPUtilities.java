package com.example.clientquiz;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPUtilities {
    public static TCPUtilities currentSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public boolean canRead(){
        return in != null;
    }
    public void startConnection(String ip, int port)  {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            currentSocket=this;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String msg) {
        Log.wtf("SendMessage",msg);
        out.println(msg);
        return null;
    }

    public String receiveMessage() {
        try {
            String received = in.readLine();
            if(received!=null)
                return received;
        } catch (IOException e) {
            e.printStackTrace();
            return "No received message";
        }
        return null;
    }

    public void stopConnection()  {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
