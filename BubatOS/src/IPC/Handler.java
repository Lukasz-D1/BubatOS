package com.company;

public class Handler {

    public static void main(String[] args) {
        Server server = null;
        server = new Server(9999);
        Client client = null;
        client = new Client( 9999);
        client.send("SSS");
        client.send("XXX");
        client.send("AAA");
        System.out.println(server.getMessageList());
    }
}

/* TODO: 31.12.2017
*  Interejs dla innych;
 * Zabezpieczenie semaforem;
*  Użyć funkcji zarządzających procesami w konstruktorach i funkcjach send(), read();
*  Kosmetyka.
* */

