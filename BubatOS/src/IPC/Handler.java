package IPC;


import java.util.ArrayList;
import java.util.List;

public class Handler {

       static List<Connection>xxx = new ArrayList<>();

    public static Connection getFromList(String P1, String P2) {
        for (Connection x : xxx) {
            if (x.serverName.equals(P1) && x.clientName.equals(P2)) return x;

        }
        return null;
    }
    public static void main(String[] args) {
        Connection x = new Connection("a", "b");
//        x.sendMessage("a", "b", "czesc");
//        x.sendMessage("a", "b", "siema");
//        x.sendMessage("a","b","czesc Lukasz");
//        x.readMessage("a");
//        x.readMessage("a");
//        x.readMessage("a");
//
//        x.endConnection("a", "b");
//        x.sendMessage("a","b","joł");
        xxx.add(x);
        x.endConnection("a","b");
        //xxx.getfromlist("a","b").
        getFromList("a","b").sendMessage("a","b","siema");
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            System.err.println(e);
        }
        getFromList("a","b").readMessage("a");

        //x.sendMessage("a","b","aaaaaaa");

       // x.readMessage("a");

    }

}
//TODO 02.01.2018 kosmetyka
