package com.company;

public class Main {



    public static void main(String[] args) {

        Server server = new Server();
        Client client = new Client();

        Thread t1 = new Thread(server);
        Thread t2 = new Thread(client);

        t1.start();
        t2.start();

        try{
            t1.join();
            t2.join();
        }
        catch (InterruptedException e)
        {
            System.out.println(e);
        }

    }
}
