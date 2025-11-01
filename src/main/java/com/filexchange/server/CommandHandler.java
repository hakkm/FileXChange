package com.filexchange.server;

import com.filexchange.common.Protocol;
import com.filexchange.common.Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class CommandHandler {
    static final public int MAX_FILENAME_LENGTH = 255;
    static final public int MAX_FILESIZE = 10 * 1024 * 1024; // 10 MB
    static final public String STORAGE_PATH = "src/main/resources/server_repo/";
    // logger
    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());

    public void upload(InputStream in, OutputStream out, String[] parts) {
        // parse input: UPLOAD <filename> <filesize>
        if (parts.length < 3) {
            String response = Protocol.RESP_ERROR + " Missing filename or filesize";
            logger.info(response);
            try {
                out.write(response.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        String filesizeStr = parts[1].trim();
        long filesize;
        try {
            filesize = Long.parseLong(filesizeStr);
        } catch (NumberFormatException e) {
            String response = Protocol.RESP_ERROR + " Invalid filesize";
            logger.info(response);
            try {
                out.write(response.getBytes());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }

        // check for file size
        if (filesize < 0 || filesize > MAX_FILESIZE) {
            String response = Protocol.RESP_ERROR + " Filesize exceeds limit";
            logger.info(response);
            try {
                out.write(response.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // filename is the concat from 2 to end
        String filename = String.join(" ", java.util.Arrays.copyOfRange(parts, 2, parts.length)).trim();

        // check for filename length
        if (filename.isEmpty() || filename.length() > MAX_FILENAME_LENGTH) {
            String response = Protocol.RESP_ERROR + " Invalid filename length";
            logger.info(response);
            try {
                out.write(response.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // ready message
        String response = Protocol.RESP_READY;
        try {
            out.write(response.getBytes());
            logger.info(response);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // proceed to receive the file... + check for the file size to be < MAX_FILESIZE
        // give the filename a unique identifier to avoid overwriting
        // todo: don't miss to remove the unique identifier when listing files for download
        String uniqueFilename = System.currentTimeMillis() + "_" + filename;
        try (FileOutputStream fos = new FileOutputStream(STORAGE_PATH + uniqueFilename)) {
            logger.info("Trying to receive file: " + filename + ", size: " + filesize);
            // why i didn't check for actual filesize from the stream:
            //   because in Utils.copyStream() we only copy up to filesize bytes
            Utils.copyStream(in, fos, filesize);
            logger.info("File received: " + filename);
            try {
                String resp = Protocol.RESP_OK + " File uploaded successfully";
                logger.info(resp);
                out.write(resp.getBytes());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (IOException e) {
            try {
                String resp = Protocol.RESP_ERROR + " File transfer failed";
                logger.info(resp);
                out.write(resp.getBytes());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
