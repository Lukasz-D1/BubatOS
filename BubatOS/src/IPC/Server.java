package IPC;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Server implements Runnable {
    private Queue<String> messageList = new LinkedList();

    private Socket socket = null;
    private ServerSocket server = null;
    private Thread thread = null;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;

    public Server(int port) {
        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
            start();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public void run() {
        while (thread != null) {
            try {
                System.out.println("Waiting for a client ...");
                socket = server.accept();
                System.out.println("Client accepted: " + socket);
                open();
                boolean done = false;
                while (!done) {
                    try {
                        String line = streamIn.readUTF();
                        messageList.add(line);
                        done = line.equals(".bye");
                    } catch (IOException ioe) {
                        done = true;
                    }
                }
                close();
            } catch (IOException ie) {
                System.out.println("Acceptance Error: " + ie);
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }

    public void open() throws IOException {
        streamIn = new DataInputStream(new
                BufferedInputStream(socket.getInputStream()));
    }

    public void close() throws IOException {
        if (socket != null) socket.close();
        if (streamIn != null) streamIn.close();
    }

    public void send(String msg) {
        try {
            streamOut.writeUTF(msg);
            streamOut.flush();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public String read() {
    	//marcin z void na string
        //System.out.println(messageList.poll());
    	String s = messageList.poll();
    	System.out.println("Odebralem wiadomosc: "+s);
        return s;
    }

}

