package IPC;

import java.io.*;
import java.net.Socket;


public class Client {
    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;

    public Client(int serverPort) {
        try {
            socket = new Socket("127.0.0.1", serverPort);
            start();
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
        }
    }


    public void send(String msg) {
        try {
            streamOut.writeUTF(msg);
            streamOut.flush();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    private void start() throws IOException {
        console = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
    }

    public void stop() {
        try {
            if (console != null) console.close();
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            System.out.println("Error closing ...");
        }
    }


}
