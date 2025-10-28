package com.filexchange.server;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    static final private short port = 1024;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket conn = server.accept();
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                String filename = dis.readUTF();
                long filesize = dis.readLong();
                System.out.printf("Receiving filename: %s, size: %d%n", filename, filesize);


                try (FileOutputStream fos = new FileOutputStream( "src/main/resources/server_repo/" + filename)) {
                    int count;
                    byte[] buffer = new byte[8192]; // chuck size is 8192 bytes
                    int totalRead = 0;
                    while (totalRead < filesize && (count = dis.read(buffer,0, (int) Math.min(buffer.length, filesize - totalRead))) != -1) {
                        fos.write(buffer, 0, count);
                        totalRead += count;
                    }
                    System.out.println("File received: " + filename);
                }
            }
        } catch (IOException e) {
            System.err.println("[!] Client handler error: " + e.getMessage());
            e.printStackTrace(); // debug
        }
    }
}
