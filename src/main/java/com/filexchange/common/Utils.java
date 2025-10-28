package com.filexchange.common;

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
}
