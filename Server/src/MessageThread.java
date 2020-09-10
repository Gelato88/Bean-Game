import java.util.ArrayList;

public class MessageThread extends Thread{

    public ArrayList<String> messages;
    public boolean running;

    public MessageThread() {
        messages = new ArrayList<>();
        running = true;
    }

    @Override
    public void run() {
        while(running) {
            if(messages.size() > 0) {
                wait(200);
                Main.sendToAll(messages.get(0));
                messages.remove(0);
            }
        }
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch(InterruptedException e) {
        }
    }

    public void queueMessage(int code, String message) {
        System.out.println("sending: " + code + message);
        messages.add(""+code+message);
    }

    public void queueMessage(String message) {
        System.out.println("sending: " + message + " (uncoded)");
        messages.add(message);
    }
}
