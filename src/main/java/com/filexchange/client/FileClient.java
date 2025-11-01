package com.filexchange.client;

import com.filexchange.common.Protocol;
import com.filexchange.common.Utils;
import jdk.jshell.execution.Util;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Logger;

public class FileClient {
    private final Logger logger = Logger.getLogger(FileClient.class.getName());
    private final String host;
    private final short port;

    public FileClient(String host, short port) {
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        String host = "127.0.0.1";
        short port = 1024;
        new FileClient(host, port).start();
    }

    public void start() {

//        String filePath = "/home/khabir/dows/FORD2.pdf";
//        File file = new File(filePath);
//        if (!file.exists()) {
//            System.out.println("File not found");
//            return;
//        }
//
//        System.out.printf("Sending filename: %s, size: %d", file.getName(), file.length());

        try (Socket conn = new Socket(host, port);
             DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
             Scanner sc = new Scanner(System.in)) {
            logger.info("Connected to server " + host + ":" + port);

            while (true) {
                System.out.print("client> ");
                String command = sc.nextLine();
                if (command == null) break;
                String[] parts = command.split(" ", 2);
                String cmd = parts[0].trim().toUpperCase();
                switch (cmd) {
                    case Protocol.CMD_EXIT -> {
                        logger.info("Exiting...");
                        return;
                    }
                    case Protocol.CMD_UPLOAD -> {
                        if (parts.length < 2) {
                            System.out.println("Usage: UPLOAD <local_path>");
                            break;
                        }
                        Path path = Path.of(parts[1].trim());
                        if (!Files.exists(path) || !Files.isRegularFile(path)) {
                            System.out.println("Local file does not exist");
                            break;
                        }
                        var file = path.toFile();
                        try (FileInputStream f_in = new FileInputStream(file)) {
                            System.out.printf("Sending filename: %s, size: %d%n", file.getName(), file.length());
                            String request = Protocol.CMD_UPLOAD + " " + file.length() + " " + file.getName();
                            dos.writeUTF(request);
                            Utils.copyStream(f_in, dos, file.length());
                            System.out.println("File sent: " + file.getName());
                        }
                    }
                    default -> {
                        System.out.println("Unknown command: " + cmd);
                        continue;
                    }
                }
            }


        } catch (IOException e) {
            logger.severe("Client error: " + e.getMessage());
        }
    }
}
