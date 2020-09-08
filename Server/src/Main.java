import java.io.*;
import java.net.*;
import java.util.*;
public class Main {

    final static int PORT = 6000;

    private static ServerSocket ss;
    private static boolean running;
    public static ArrayList<Player> players;
    public static Player currentTurn;
    public static int currentTurnIndex;
    private static int[] deck;
    private static ArrayList<Integer> discard;
    private static int nextCardIndex;
    private static int deckFinsihedTimes;
    private static int waitingForTradePlants;


    public static void main(String[] args) {
        running = true;
        nextCardIndex = 0;
        waitingForTradePlants = 0;
        discard = new ArrayList<>();
        deckFinsihedTimes = 0;
        try {
            ss = new ServerSocket(PORT);
            players = new ArrayList<>();
            System.out.println("Listening on port " + PORT);
            while(running) {
                Socket socket = ss.accept();
                players.add(new Player(new ServerThread(socket)));
                players.get(players.size()-1).setPlayerNumber(players.size());
                players.get(players.size()-1).sendMessage(1002, ""+players.size());
                System.out.println("A client has connected. (Player " + players.size() + ")");

            }
        } catch(IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
//
//    public static void quit() {
//        running = false;
//        try {
//            for (ServerThread thread : players) {
//                thread.close();
//                ss.close();
//                System.exit(-1);
//            }
//        } catch(IOException e) {
//            System.out.println("Server exception: " +  e.getMessage());
//            e.printStackTrace();
//        }
//    }

    public static void sendToAll(int code, String str) {
        wait(500);
        System.out.println("sending: " + code + str);
        for(Player p : players) {
            p.sendMessage("" + code + str);
        }
    }

    public static void generateDeck() {
        switch(players.size()) {
            case 3:
                deck = new int[150];
                break;
            case 4:
            case 5:
                deck = new int[130];
                break;
            case 6:
            case 7:
                deck = new int[144];
                break;
            default:
                System.out.println("Server exception: Invalid number of players.");
        }
        int startIndex = 0;
        startIndex += addCardsToDeck(0, startIndex, 10);
        startIndex += addCardsToDeck(1, startIndex, 20);
        startIndex += addCardsToDeck(2, startIndex, 18);
        if(players.size() == 4 || players.size() == 5) {
            startIndex += addCardsToDeck(3, startIndex, 4);
        }
        if(players.size() == 3 || players.size() == 6 || players.size() == 7) {
            startIndex += addCardsToDeck(4, startIndex, 24);
        }
        if(players.size() == 3 || players.size() == 4 || players.size() == 5) {
            startIndex += addCardsToDeck(5, startIndex, 6);
        }
        startIndex += addCardsToDeck(6, startIndex, 14);
        startIndex += addCardsToDeck(7, startIndex, 8);
        startIndex += addCardsToDeck(8, startIndex, 12);
        startIndex += addCardsToDeck(9, startIndex, 16);
        startIndex += addCardsToDeck(10, startIndex, 22);
        printDeck();
        shuffleDeck();
        printDeck();
    }

    public static void startGame() {
        generateDeck();
        System.out.println("Starting game...");
        sendToAll(1001, "");
        for(int i = 0; i < 6; i++) {
            for (int j = 1; j <= players.size(); j++) {
                dealCard(j);
            }
        }
        currentTurnIndex = (int)(Math.random() * players.size());
        currentTurn = players.get(currentTurnIndex);
        sendToAll(3000, ""+(currentTurnIndex+1));
    }

    public static int addCardsToDeck(int cardVal, int startIndex, int number) {
        for(int i = 0; i < number; i++) {
            deck[startIndex + i] = cardVal;
        }
        return number;
    }

    public static void shuffleDeck() {
        for(int i = deck.length; i > 0; i--) {
            int ind = (int)(Math.random() * i);
            int temp = deck[ind];
            deck[ind] = deck[i-1];
            deck[i-1] = temp;
        }
    }

    public static void printDeck() {
        System.out.println("Deck size: " + deck.length);
        for(int i = 0; i < deck.length; i++) {
            System.out.print(deck[i] + " ");
        }
        System.out.println();
    }

    public static void dealCard(int player) {
        sendToAll(2000, "" + player + deck[nextCardIndex++]);
        if(nextCardIndex >= deck.length) {
            if(deckFinsihedTimes == 3) {
                endGame();
            }
            deck = new int[discard.size()];
            for(int i = 0; i < discard.size(); i++) {
                deck[i] = discard.get(i);
            }
            discard.clear();
            shuffleDeck();
        }
    }

    public static void updatePlayerCount() {
        sendToAll(1000, ""+ players.size());
    }

    public static void playerPlanted(String info) {
        int player = Integer.parseInt(info.substring(0,1));
        int spot = Integer.parseInt(info.substring(1,2));
        int cardVal = Integer.parseInt(info.substring(2,4));
        int cards = Integer.parseInt(info.substring(4));
        Player p = players.get(player-1);
        if(spot == 1) {
            p.setSpot1Type(cardVal);
            p.setSpot1Number(cards);
        } else {
            p.setSpot2Type(cardVal);
            p.setSpot2Number(cards);
        }
        sendToAll(3002,  info);
    }

    public static void playerHarvested(String info) {
        int player = Integer.parseInt(info.substring(0,1));
        int spot = Integer.parseInt(info.substring(1,2));
        int coins = Integer.parseInt(info.substring(2));
        Player p = players.get(player-1);
        if(spot == 1) {
            p.setSpot1Type(-1);
            p.setSpot1Number(0);
        } else {
            p.setSpot2Type(-1);
            p.setSpot2Number(0);
        }
        p.setCoins(coins);
        sendToAll(3003, info);
    }

    public static void discardCards(String info) {
        int card = Integer.parseInt(info.substring(0, 2));
        int number = Integer.parseInt(info.substring(2));
        for(int i = 0; i < number; i++) {
            discard.add(card);
        }
    }

    public static void flipCards() {
        sendToAll(3005, ""+deck[nextCardIndex++]);
        sendToAll(3005, ""+deck[nextCardIndex++]);
    }

    public static void changeTurn() {
        dealCard(currentTurnIndex+1);
        dealCard(currentTurnIndex+1);
        dealCard(currentTurnIndex+1);
        if(currentTurnIndex < players.size()-1) {
            currentTurnIndex++;
        } else {
            currentTurnIndex = 0;
        }
        currentTurn = players.get(currentTurnIndex);
        sendToAll(3000, ""+(currentTurnIndex+1));
    }

    public static void incrementWaitingForTradePlants(int delta) {
        waitingForTradePlants += delta;
        if(waitingForTradePlants <= 0) {
            waitingForTradePlants = 0;
        }
    }

    public static void checkEndTurn() {
        if(waitingForTradePlants <= 0) {
            sendToAll(3009, "");
        }
    }

    public static void endGame() {
        sendToAll(5000, "");
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch(InterruptedException e) {

        }
    }
}