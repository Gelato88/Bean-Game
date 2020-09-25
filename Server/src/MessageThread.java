import java.util.ArrayList;

public class MessageThread extends Thread{

    public static volatile ArrayList<String> messages;
    private boolean running;

    public MessageThread() {
        messages = new ArrayList<>();
        running = true;
    }

    @Override
    public void run() {
        while(running) {
            if(messages.size() > 0) {
                wait(300);
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
        System.out.println("Sending: " + code + message);
        messages.add(""+code+message);
    }

    public void queueMessage(String message) {
        System.out.println("Sending: " + message + " (uncoded)");
        messages.add(message);
    }
}
