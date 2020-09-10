package com.beangame.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class InputThread extends Thread {

    private Socket socket;

    private BeanGame client;
    private ActionThread actionThread;

    private boolean stop;

    public InputThread(Socket socket, BeanGame client, ActionThread actionThread;) {
        this.socket = socket;
        this.client = client;
        this.actionThread = actionThread;
        stop = false;
    }

    public void run() {
        System.out.println("Connected");
        client.sendMessage(1000, "");
        while(!stop) {

            String text;

            try {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                text = reader.readLine();
                if(text != null) {
                    System.out.println("Received " + text);
                    actionThread.queueAction(text);
                }

            } catch (IOException e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void exit() {
        stop = true;
    }
}
