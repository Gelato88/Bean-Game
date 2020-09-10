import java.io.*;
import java.net.*;

public class ServerThread extends Thread {

    private Socket socket;
    private boolean running;

    private BufferedReader reader;
    private InputStream input;
    private OutputStream output;
    private PrintWriter writer;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        running = true;
        try {
            input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
            String text;
            do {
                text = reader.readLine();
                System.out.println("Received: " + text);

                switch(Integer.parseInt(text.substring(0, 4))) {
                    case 1000:
                        Main.updatePlayerCount();
                        break;
                    case 1001:
                        Main.startGame();
                        break;
                    case 1003:
                        Main.setPlayerName(text.substring(4));
                        break;
                    case 1004:
                        Main.sendPlayerName(text.substring(4));
                        break;
                    case 3001:
                        Main.changeTurn();
                        break;
                    case 3002:
                        Main.plant(text.substring(4));
                        break;
                    case 3003:
                        Main.harvest(text.substring(4));
                        break;
                    case 3004:
                        Main.flipCards();
                        break;
                    case 3006:
                        Main.plantFromFlipped(text.substring(4));
                        break;
                    case 3007:
                        Main.discardCards(text.substring(4));
                        break;
                    case 3008:
                        Main.checkEndTurn();
                        break;
                    case 4000:
                        Main.sendTrade(text.substring(4));
                        break;
                    case 4001:
                        Main.acceptTrade("");
                        break;
                    case 4002:
                        Main.rejectTrade("");
                        break;
                    case 4004:
                        Main.incrementWaitingForTradePlants(1);
                        break;
                    case 4005:
                        Main.incrementWaitingForTradePlants(-1);
                        break;
                    case 4006:
                        Main.removeFlippedCard(text.substring(4));
                        break;
                    case 9000:
                        Main.sendFromConsole(text.substring(4));
                        break;
                    case 9999:
                        close();
                        break;
                    default:
                        System.out.println("ERROR: Received an invalid message code");
                        break;
                }
            } while(running);
            socket.close();
        } catch(IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
        System.exit(0);
    }

    /* Ends this thread
     *
     */
    public void close() {
        System.out.println("Closing...");
        Main.sendToAll(9999, "");
        running = false;
    }

    /* Sends a message to this user.
     *
     */
    public void sendMessage(String str) {
        try {
            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            writer.println(str);
        } catch(IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
