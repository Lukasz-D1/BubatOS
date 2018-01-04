package IPC;

import semaphore.Semaphore;
import ProcessManagment.ProcessManagment;

import java.util.List;

public class Connection {
    private Server server = null;
    private Client client = null;
    protected String serverName = null;
    String clientName = null;
    private int serverPort;
    ProcessManagment ps;
    Semaphore semaphoreS = null;
    Semaphore semaphoreC = null;


    public Connection(String P1, String P2, ProcessManagment ps) {
        serverName = P1;
        clientName = P2;
        
        this.ps = ps;
        if(ps.getProcessByName(P1).getPID()!=0) 
        {
        	System.out.println("Otwieram polaczenie");
        	server = new Server(ps.getProcessByName(P1).getPID() +50000); //getProcessByName(P1).getPID() +
            client = new Client(ps.getProcessByName(P1).getPID() +50000); //getProcessByName(P2).getPID() +
            serverPort = 50000;
            //ConnectionList.connectionList.add(this);
            semaphoreS = new Semaphore("ServerSemaphore");
            semaphoreC = new Semaphore("ClientSemaphore");
        }
        
        
    }

    public void sendMessage(String P1, String P2, String message) {
        if (P1.equals(serverName) && P2.equals(clientName))
        	{
        	try {
                semaphoreS.P(ps.getProcessByName(P1));
                semaphoreC.P(ps.getProcessByName(P2));
            } catch (InterruptedException E) {
                System.out.println(E.toString());
            }
        	client.send(message);
        	try {
        		semaphoreC.V();
            	semaphoreS.V();
            } catch (InterruptedException E) {
                System.out.println(E.toString());
            }
        	
        	}
        else System.out.println("Nie ma takiego polaczenia");
    }

    public String readMessage(String Pname) {
    	//marcin - zmienione void na String
        if (Pname.equals(serverName)) 
        {
        	return server.read();
        }
        else
        {
        	System.out.println("Nie ma takiego polaczenia");
        	return "Nie ma takiego polaczenia";
        }
    }

    public void endConnection(String P1, String P2) {
        if (P1.equals(serverName) && P2.equals(clientName)) {
            try {
                semaphoreS.V();
               semaphoreC.V();
                finalize();
            } catch (Throwable e) {
                System.out.println(e.toString());
            }
        } else System.out.println("Nie ma takiego polaczenia");
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Zamykam polaczenie");
        super.finalize();
    }
}
