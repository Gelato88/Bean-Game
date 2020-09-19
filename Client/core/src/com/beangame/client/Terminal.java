package com.beangame.client;

import java.util.Scanner;

/* Available when in dev mode. Allows sending messages to the server.
 *
 */
public class Terminal extends Thread {

    private BeanGame client;
    private Scanner scanner;

    public Terminal(BeanGame client) {
        this.client = client;
        scanner = new Scanner(System.in);
    }

    /* Waits for input and sends to the server
     *
     */
    public void run() {
        while(true) {
            String s = scanner.nextLine();
            client.sendMessage(s);
        }
    }
}
