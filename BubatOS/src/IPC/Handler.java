package IPC;

public class Handler {

    public static void main(String[] args) {
        Connection x = new Connection("aaa", "bbb");
        x.sendMessage("aaa", "bbb", "czesc");
        x.sendMessage("aaa", "bbb", "siema");
        x.readMessage("aaa");
        x.readMessage("aaa");
        x.endConnection("aaa", "bbb");
    }

}
//TODO 02.01.2018 kosmetyka
