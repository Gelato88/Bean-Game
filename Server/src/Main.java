import java.io.*;
import java.net.*;
import java.util.*;
public class Main {

    final static int PORT = 6000;

    private static ServerSocket ss;
    private static boolean running;

    public static ArrayList<Player> players;
    private static ArrayList<Integer> discard;

    private static MessageThread messageThread;

    public static int currentTurnIndex;
    private static int nextCardIndex;
    private static int deckFinishedTimes;
    private static int waitingForTradePlants;
    private static int[] deck;

    private static boolean started;

    /* Waits for and assigns connections to clients
     *
     */
    public static void main(String[] args) {
        running = true;
        started = false;
        nextCardIndex = 0;
        waitingForTradePlants = 0;
        deckFinishedTimes = 0;
        discard = new ArrayList<>();
        messageThread = new MessageThread();
        messageThread.start();
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

    /* Sends a message with assigned code to all connected clients
     *
     */
    public static void sendToAll(int code, String str) {
        for(Player p : players) {
            p.sendMessage("" + code + str);
        }
    }

    /* Sends a message to all connected clients
     *
     */
    public static void sendToAll(String str) {
        for(Player p : players) {
            p.sendMessage(str);
        }
            System.out.println("Sent:     " + str);
    }

    /* Updates player count for all connected clients
     * Code 1000
     */
    public static void updatePlayerCount() {
        messageThread.queueMessage(1000, ""+players.size());
    }

    /* Starts the game
     * Code 1001
     */
    public static void startGame() {
        if(!started) {
            started = true;
            System.out.println("Starting game...");
            generateDeck();
            messageThread.queueMessage(1001, "");
            for (int i = 0; i < 5; i++) {
                for (int j = 1; j <= players.size(); j++) {
                    dealCard(j);
                }
            }
            currentTurnIndex = (int) (Math.random() * players.size());
            messageThread.queueMessage(3000, ""+(currentTurnIndex+1));
        }
    }

    /* Creates the deck
     * Originates from code 1001
     */
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
                deck = new int[110];
                System.out.println("ERROR: Attempted to generate deck with invalid number of players.");
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
        shuffleDeck();
    }

    /* Generates card values to add to the deck
     * Originates from code 1001
     */
    public static int addCardsToDeck(int cardVal, int startIndex, int number) {
        for(int i = 0; i < number; i++) {
            deck[startIndex + i] = cardVal;
        }
        return number;
    }

    /* Shuffles the deck
     * Originates from code 1001, 3001
     */
    public static void shuffleDeck() {
        for(int i = deck.length; i > 0; i--) {
            int ind = (int)(Math.random() * i);
            int temp = deck[ind];
            deck[ind] = deck[i-1];
            deck[i-1] = temp;
        }
        nextCardIndex = 0;
    }

    /* Deals a card to a player
     * Originates from code 1001, 3001
     */
    public static void dealCard(int player) {
        messageThread.queueMessage(2000, ""+player+deck[nextCardIndex++]);
        if(nextCardIndex >= deck.length) {
            if(deckFinishedTimes == 3) {
                endGame();
            } else {
                deck = new int[discard.size()];
                for (int i = 0; i < discard.size(); i++) {
                    deck[i] = discard.get(i);
                }
                discard.clear();
                shuffleDeck();
            }
        }
    }

    /* Sets a player's name
     * Code 1003
     */
    public static void setPlayerName(String info) {
        int playerNumber = Integer.parseInt(info.substring(0,1));
        String playerName = info.substring(1);
        players.get(playerNumber-1).setName(playerName);
    }

    /* Sends a requested player's name to a player
     * Code 1004
     */
    public static void sendPlayerName(String info) {
        int playerNumber = Integer.parseInt(info.substring(0,1));
        int playerRequested = Integer.parseInt(info.substring(1,2));
        String name = players.get(playerRequested-1).getName();
        messageThread.queueMessage(1005, ""+playerNumber+playerRequested+name);
    }

    /* Changes turn to the next player
     * Code 3001
     */
    public static void changeTurn() {
        dealCard(currentTurnIndex+1);
        dealCard(currentTurnIndex+1);
        dealCard(currentTurnIndex+1);
        if(currentTurnIndex < players.size()-1) {
            currentTurnIndex++;
        } else {
            currentTurnIndex = 0;
        }
        messageThread.queueMessage(3000, ""+(currentTurnIndex+1));
    }

    /* Updates information for all players when a player plants a card
     * Code 3002
     */
    public static void plant(String info) {
        int player = Integer.parseInt(info.substring(0,1));
        int spot = Integer.parseInt(info.substring(1,2));
        int cardVal = Integer.parseInt(info.substring(2,4));
        int cards = Integer.parseInt(info.substring(4));
        Player p = players.get(player-1);
        if(spot == 1) {
            p.setSpot1Type(cardVal);
            p.setSpot1Number(cards);
        } else if(spot == 2) {
            p.setSpot2Type(cardVal);
            p.setSpot2Number(cards);
        } else {
            System.out.println("ERROR: Received a plant at an invalid spot: " + info);
        }
        messageThread.queueMessage(3002, info);
    }

    /* Updates information for all players when a player harvests a spot
     * Code 3003
     */
    public static void harvest(String info) {
        int player = Integer.parseInt(info.substring(0,1));
        int spot = Integer.parseInt(info.substring(1,2));
        int coins = Integer.parseInt(info.substring(2));
        Player p = players.get(player-1);
        if(spot == 1) {
            p.setSpot1Type(-1);
            p.setSpot1Number(0);
        } else if(spot == 2) {
            p.setSpot2Type(-1);
            p.setSpot2Number(0);
        } else {
            System.out.println("ERROR: Received a harvest at an invalid spot: " + info);
        }
        p.setCoins(coins);
        messageThread.queueMessage(3003, info);
    }

    /* Flips two cards
     * Code 3004
     */
    public static void flipCards() {
        messageThread.queueMessage(3005, ""+deck[nextCardIndex++]);
        messageThread.queueMessage(3005, ""+deck[nextCardIndex++]);
    }

    /* Plants a card that was flipped
     * Code 3006
     */
    public static void plantFromFlipped(String info) {
        messageThread.queueMessage(3006, info);
    }

    /* Adds cards to the discard
     * Code 3007
     */
    public static void discardCards(String info) {
        int card = Integer.parseInt(info.substring(0, 2));
        int number = Integer.parseInt(info.substring(2));
        for(int i = 0; i < number; i++) {
            discard.add(card);
        }
    }

    /* Checks if there are oustanding cards in players' traded hands
     * Code 3008
     */
    public static void checkEndTurn() {
        if(waitingForTradePlants <= 0) {
            messageThread.queueMessage(3009, "");
        }
    }

    /* Sends a trade request
     * Code 4000
     */
    public static void sendTrade(String info) {
        messageThread.queueMessage(4000, info);
    }

    /* Confirms a trade offer and closes all trade windows
     * Code 4001
     */
    public static void acceptTrade(String info) {
        messageThread.queueMessage(4001, "");
        messageThread.queueMessage(4003, "");
    }

    /* Rejects a trade offer
     * Code 4002
     */
    public static void rejectTrade(String info) {
        messageThread.queueMessage(4002, "");
    }

    /* Tracks if players have cards in their traded hands
     * Code 4004, 4005
     */
    public static void incrementWaitingForTradePlants(int delta) {
        waitingForTradePlants += delta;
        if(waitingForTradePlants <= 0) {
            waitingForTradePlants = 0;
        }
    }

    /* Removes a flipped card
     * Code 4006
     */
    public static void removeFlippedCard(String info) {
        messageThread.queueMessage(4006, info);
    }

    /* Sends a message to all players
     * Code 9000
     */
    public static void sendFromConsole(String info) {
        messageThread.queueMessage(info);
    }

    /* Ends the game
     * Originates from 1001, 3001
     */
    public static void endGame() {
        messageThread.queueMessage(5000, "");
    }

}