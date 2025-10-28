package com.filexchange.server;

import com.filexchange.common.Utils;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (socket;
             DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            String filename = dis.readUTF();
            long filesize = dis.readLong();
            System.out.printf("Receiving filename: %s, size: %d%n", filename, filesize);


            try (FileOutputStream fos = new FileOutputStream("src/main/resources/server_repo/" + filename)) {
                Utils.copyStream(dis, fos, filesize);
                System.out.println("File received: " + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
