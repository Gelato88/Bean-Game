import java.net.ServerSocket;

public class Player {

    private int spot1Type;
    private int spot2Type;
    private int spot1Number;
    private int spot2Number;
    private int coins;
    private int cards;
    private int playerNumber;
    private ServerThread thread;

    public Player(ServerThread thread) {
        spot1Type = -1;
        spot2Type = -1;
        spot1Number = 0;
        spot2Number = 0;
        coins = 0;
        cards = 0;
        this.thread = thread;
        thread.start();
    }

    public int getSpot1Type() {
        return spot1Type;
    }

    public int getSpot2Type() {
        return spot2Type;
    }

    public int getSpot1Number() {
        return spot1Number;
    }

    public int getSpot2Number() {
        return spot2Number;
    }

    public int getCoins() {
        return coins;
    }

    public int getCards() {
        return cards;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setSpot1Type(int type) {
        spot1Type = type;
    }

    public void setSpot2Type(int type) {
        spot2Type = type;
    }

    public void setSpot1Number(int num) {
        spot1Number = num;
    }

    public void setSpot2Number(int num) {
        spot2Number = num;
    }

    public void setCoins(int num) {
        coins = num;
    }

    public void setCards(int num) {
        cards = num;
    }

    public void setPlayerNumber(int num) {
        playerNumber = num;
    }

    public void sendMessage(String str) {
        thread.sendMessage(str);
    }

    public void sendMessage(int code, String str) {
        thread.sendMessage("" + code + str);
    }

}
