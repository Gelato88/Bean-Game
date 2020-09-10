package com.beangame.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class InputThread extends Thread {

    private Socket socket;

    private BeanGame client;

    private boolean stop;

    public InputThread(Socket socket, BeanGame client) {
        this.socket = socket;
        this.client = client;
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
                    switch(Integer.parseInt(text.substring(0, 4))) {
                        case 1000:
                            client.setPlayerCount(Integer.parseInt(text.substring(4)));
                            break;
                        case 1001:
                            client.startGame();
                            break;
                        case 1002:
                            client.setPlayerNumber(Integer.parseInt(text.substring(4)));
                            break;
                        case 1005:
                            if (client.playerNumber == Integer.parseInt(text.substring(4, 5))) {
                                client.setOpponentName(text.substring(5));
                            }
                            break;
                        case 2000:
                            if(client.playerNumber == Integer.parseInt(text.substring(4, 5))) {
                                client.getPlayer().addCard(Integer.parseInt(text.substring(5)));
                            } else {

                            }
                            break;
                        case 3000:
                            if(client.playerNumber == Integer.parseInt(text.substring(4))) {
                                client.startTurn();
                            }
                            client.setCurrentTurn(Integer.parseInt(text.substring(4)));
                            break;
                        case 3002:
                            if(!(client.playerNumber == Integer.parseInt(text.substring(4,5)))) {
                                client.opponentPlant(text.substring(4));
                            }
                            break;
                        case 3003:
                            if(!(client.playerNumber == Integer.parseInt(text.substring(4,5)))) {
                                client.opponentHarvest(text.substring(4));
                            }
                            break;
                        case 3005:
                            client.flipCard(text.substring(4));
                            break;
                        case 3006:
                            if(!(client.currentTurn == client.playerNumber)) {
                                client.hideFlipped(text.substring(4));
                            }
                            break;
                        case 3009:
                            if(client.currentTurn == client.playerNumber) {
                                client.getPlayer().endTurn();
                            }
                            break;
                        case 4000:
                            if(client.playerNumber != Integer.parseInt(text.substring(4,5))) {
                                client.showTrade(text.substring(4));
                                System.out.println(text);
                            }
                            break;
                        case 4001:
                            if(client.playerNumber == client.currentTurn) {
                                client.getTrade().confirm();
                            }
                            break;
                        case 4002:
                            if(client.playerNumber == client.currentTurn) {
                                client.getTrade().incrementWaiting(-1);
                            }
                            break;
                        case 4003:
                            client.showTradeOffer = false;
                            break;
                        case 4006:
                            client.getPlayer().hideFlipped(Integer.parseInt(text.substring(4)));
                            break;
                        case 5000:
                            client.endGame();
                            break;
                        case 9999:
                            System.exit(0);
                            break;
                    }
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
