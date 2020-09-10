package com.beangame.client;

import java.util.ArrayList;

public class ActionThread extends Thread {

    private BeanGame client;

    private ArrayList<String> actions;
    private boolean running;

    public ActionThread(BeanGame client) {
        this.client = client;
        actions = new ArrayList<>();
        running = true;
    }

    @Override
    public void run() {
        while(running) {
            if(actions.size() > 0) {
                String text = actions.get(0);
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
                actions.remove(0);
            }
        }
    }

    public void queueAction(String action) {
        actions.add(action);
    }

}
