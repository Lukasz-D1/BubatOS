package com.company;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;


public class Client implements Runnable{

    private BufferedReader in;
    private PrintWriter out;
    private Socket sock;
    private String message;
    private Queue<String> messageList = new LinkedList();

    @Override
    public void run()
    {
        startClient(9999);
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        read();
    }

    public void startClient(int ID)
    {
        try {
            sock = new Socket("127.0.0.1",ID);
        }
        catch (IOException ioe)
        {
            System.err.println(ioe);
        }
    }

    public void read()
    {
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            message = in.readLine();

            //while((message = in.readLine())!= null)
            System.out.println(message);
               // messageList.add(message);

            sock.close();
        }catch (IOException ioe)
        {
            System.err.println(ioe);
        }
    }

    public void write(String letter)
    {
        try {
            PrintWriter pout = new PrintWriter(sock.getOutputStream(),true);
            pout.print(letter);
        }
        catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

//    public static void main(String[] args)  {
//        try {
//            BufferedReader in;
//           // PrintWriter out;
//            Socket sock = new Socket("127.0.0.1",9090);
//
//
//            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
//            //out = new PrintWriter(sock.getOutputStream(), true);
//
//
//            String line;
//            while((line = in.readLine()) != null)
//                System.out.println(line);
//
//            sock.close();
//        }
//        catch (IOException ioe) {
//            System.err.println(ioe);
//        }
//    }


}
