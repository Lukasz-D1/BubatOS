package IPC;


import java.util.ArrayList;
import java.util.List;

public class Handler {

       static public List<Connection>xxx = new ArrayList<>();

    public static Connection getFromList(String P1, String P2) {
        for (Connection x : Handler.xxx) {
            if (x.serverName.equals(P1) && x.clientName.equals(P2)) return x;

        }
        return null;
    }
    
    public static Connection readfromlist(String P1)
    {
    	for(Connection x: Handler.xxx)
    	{
    		if(x.serverName.equals(P1)) return x;
    	}
    	return null;
    }

}
//TODO 02.01.2018 kosmetyka
