package IPC;

public class Handler {

    public static void main(String[] args) {
        Connection x = new Connection("a", "b");
        x.sendMessage("a", "b", "czesc");
        x.sendMessage("a", "b", "siema");
        x.sendMessage("a","b","czesc Lukasz");
        x.readMessage("a");
        x.readMessage("a");
        x.readMessage("a");

        x.endConnection("a", "b");
        x.sendMessage("a","b","joł");
    }

}
//TODO 02.01.2018 kosmetyka
