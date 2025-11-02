package com.filexchange.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static void copyStream(InputStream in, OutputStream out, long filesize) throws IOException {
        int count;
        byte[] buffer = new byte[8192]; // chuck size is 8192 bytes
        int totalRead = 0;
        while (totalRead < filesize && (count = in.read(buffer, 0, (int) Math.min(buffer.length, filesize - totalRead))) != -1) {
            out.write(buffer, 0, count);
            totalRead += count;
        }
        out.flush();
    }

    public static StringBuffer list(String directoryPath) {
        File folder = new File(directoryPath);
        File[] listOfFiles = folder.listFiles();
        StringBuffer fileList = new StringBuffer();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    // remove unique identifier from filename
                    String originalFilename = file.getName().substring(file.getName().indexOf("_") + 1);
                    fileList.append(originalFilename).append(" (").append(file.length()).append(" bytes)").append("\n");
                }
            }
        } else {
            fileList.append("No files found.\n");
        }
        return fileList;
    }
}
