package IPC;

//import semaphore.Semaphore;

public class Connection {
    private Server server = null;
    private Client client = null;
    private String serverName = null;
    private String clientName = null;
    //Semaphore semaphoreS = null;
    //Semaphore semaphoreC = null;


    public Connection(String P1, String P2) {
        serverName = P1;
        clientName = P2;
        System.out.println("Otwieram polaczenie");
        server = new Server(50000); //getProcessByName(P1).getPID() +
        client = new Client(50000); //getProcessByName(P2).getPID() +
        //semaphoreS = new Semaphore("ServerSemaphore");
        //semaphoreC = new Semaphore("ClientSemaphore");
        //try {
           // semaphoreS.P(getProcessByName(P1));
          //  semaphoreC.P(getProcessByName(P2));
      //  } catch (InterruptedException E) {
       //     System.out.println(E.toString());
       // }
    }

    public void sendMessage(String P1, String P2, String message) {
        if (P1.equals(serverName) && P2.equals(clientName)) client.send(message);
        else System.out.println("Nie ma takiego polaczenia");
    }

    public void readMessage(String Pname) {
        if (Pname.equals(serverName)) server.read();
        else System.out.println("Nie ma takiego polaczenia");
    }

    public void endConnection(String P1, String P2) {
        if (P1.equals(serverName) && P2.equals(clientName)) {
            try {
                //semaphoreS.V();
               // semaphoreC.V();
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
