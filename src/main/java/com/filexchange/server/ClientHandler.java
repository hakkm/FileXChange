package com.filexchange.server;

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
                int count;
                byte[] buffer = new byte[8192]; // chuck size is 8192 bytes
                int totalRead = 0;
                while (totalRead < filesize && (count = dis.read(buffer, 0, (int) Math.min(buffer.length, filesize - totalRead))) != -1) {
                    fos.write(buffer, 0, count);
                    totalRead += count;
                }
                System.out.println("File received: " + filename);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
