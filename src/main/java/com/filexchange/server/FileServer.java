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
                new Thread(new ClientHandler(conn)).start();
            }
        } catch (IOException e) {
            System.err.println("[!] Client handler error: " + e.getMessage());
            e.printStackTrace(); // debug
        }
    }
}
