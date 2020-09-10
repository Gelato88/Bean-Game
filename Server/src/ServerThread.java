import java.io.*;
import java.net.*;

public class ServerThread extends Thread {

    private Socket socket;
    private String name;
    private boolean running;

    private BufferedReader reader;
    private InputStream input;
    private OutputStream output;
    private PrintWriter writer;

    public ServerThread(Socket socket) {
        name = "test";
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
                System.out.println(name + ": " + text);

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
                        Main.playerPlanted(text.substring(4));
                        break;
                    case 3003:
                        Main.playerHarvested(text.substring(4));
                        break;
                    case 3004:
                        Main.flipCards();
                        break;
                    case 3006:
                        Main.sendToAll(3006, text.substring(4));
                        break;
                    case 3007:
                        Main.discardCards(text.substring(4));
                        break;
                    case 3008:
                        Main.checkEndTurn();
                        break;
                    case 4000:
                        Main.sendToAll(4000, text.substring(4));
                        break;
                    case 4001:
                        Main.sendToAll(4001, "");
                        Main.sendToAll(4003, "");
                        break;
                    case 4002:
                        Main.sendToAll(4002, "");
                        break;
                    case 4004:
                        Main.incrementWaitingForTradePlants(1);
                        break;
                    case 4005:
                        Main.incrementWaitingForTradePlants(-1);
                        break;
                    case 4006:
                        Main.sendToAll(4006, text.substring(4));
                        break;
                    case 9000:
                        Main.sendToAll(text.substring(4));
                        break;
                    case 9999:
                        close();
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

//    /* Asks for and assigns the user a name.
//     *
//     */
//    public void requestName() {
//        try {
//            writer.println("Server: Enter your name.");
//            name = reader.readLine();
//            System.out.println("Received name " + name + ".");
//            writer.println("Server: Received name " + name + ".");
//        } catch(IOException e) {
//            System.out.println("Server exception: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    public void close() {
        Main.sendToAll(9999, "");
        running = false;
        System.out.println("Closing...");
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
