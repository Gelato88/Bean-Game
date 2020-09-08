package com.beangame.client;

import java.net.Socket;
import java.util.Scanner;

public class Terminal extends Thread {

    Socket socket;
    BeanGame client;

    Scanner scanner;


    public Terminal(Socket socket, BeanGame client) {
        this.socket = socket;
        this.client = client;

        scanner = new Scanner(System.in);
    }

    public void run() {
        while(true) {
            String s = scanner.nextLine();
            client.sendMessage(s);
        }
    }





}
