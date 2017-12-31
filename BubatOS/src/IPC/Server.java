package com.company;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class Server implements Runnable{

    private BufferedReader in;
    private PrintWriter out;
    private ServerSocket sock;
    private Socket client;
    private String message;
    private Queue<String> messageList = new LinkedList();

    @Override
    public void run()
    {
        startServer(9999);
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        write("SIEMA");

    }


    public void startServer(int ID)
    {
        try {
            sock = new ServerSocket(ID);
        }
        catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    public void read()
    {
        try {
            client = sock.accept();
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            if((message = in.readLine())!= null)
            {
                System.out.println(message);
                messageList.add(message);
            }
           sock.close();
        }catch (IOException ioe)
        {
            System.err.println(ioe);
        }
    }

    public void write(String letter)
    {
        try {
                while(true)
                {
                    client = sock.accept();
                    PrintWriter pout = new PrintWriter(client.getOutputStream(),true);
                    pout.print("cos");
                }

               // sock.close();



        }
        catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

//    public static void main(String[] args)  {
//        try {
//            BufferedReader in;
//            PrintWriter out;
//            ServerSocket sock;
//            Socket client;
//
//            sock = new ServerSocket(9090);
//            System.out.println("Listening");
//
//            while (true) {
//                client = sock.accept();
//                // we have a connection
//                PrintWriter pout = new PrintWriter(client.getOutputStream(), true);
//                //in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//
//                //System.out.println(pout);
//                in = new BufferedReader(new InputStreamReader(System.in));
//                String string = in.readLine();
//                pout.println(string);
//
//                client.close();
//
//            }
//
//        }
//        catch (IOException ioe) {
//            System.err.println(ioe);
//        }
//    }
}
