package com.filexchange.server;

import com.filexchange.common.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (socket;
             DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
            String msg;
            while (true) {
                msg = dis.readUTF();
                if (msg.isBlank()) continue;
                logger.info("Received command: " + msg);
                var parts = msg.split(" ");
                var cmd = parts[0].trim().toUpperCase();


                CommandHandler commandHandler = new CommandHandler();

                switch (cmd) {
                    case Protocol.CMD_UPLOAD -> commandHandler.upload(dis, dos, parts);
                    case Protocol.CMD_DOWNLOAD -> commandHandler.download(dis,dos, parts);
                    case Protocol.CMD_LIST -> commandHandler.list(dos);
                    case Protocol.CMD_EXIT -> {
                        System.out.println("exit");
                        return;
                    }
                    default -> System.out.println("unknown command");
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Client handler error: " + e.getMessage(), e);
        }
    }
}
