package com.filexchange.client;

import java.io.*;
import java.net.Socket;

public class FileClient {
    public static void main(String[] args) {
        String filePath = "/home/khabir/dows/FORD2.pdf";
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found");
            return;
        }

        System.out.printf("Sending filename: %s, size: %d", file.getName(), file.length());

        try (Socket conn = new Socket("localhost", 1024);
             DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
             FileInputStream f_in = new FileInputStream(file)) {

            dos.writeUTF(file.getName());
            dos.writeLong(file.length());
            int count;
            byte[] buffer = new byte[8192]; // chuck size is 8192 bytes
            while ((count = f_in.read(buffer)) != -1) {
                dos.write(buffer, 0, count);
            }
            System.out.println("File sent: " + file.getName());



        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
