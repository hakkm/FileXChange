package com.filexchange.server;

import com.filexchange.common.FileUploadCommand;
import com.filexchange.common.Protocol;
import com.filexchange.common.Utils;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandHandler {
    static final public String STORAGE_PATH = "src/main/resources/server_repo/";
    // logger
    private static final Logger logger = Logger.getLogger(CommandHandler.class.getName());

    private final CommandParser commandParser;

    public CommandHandler() {
        this.commandParser = new CommandParser();
    }

    public void upload(InputStream in, OutputStream out, String[] parts) {
        FileUploadCommand uploadCommand;

        try {
            uploadCommand = commandParser.parseUpdate(parts);
        } catch (Exception e) {
            String response = Protocol.RESP_ERROR + e.getMessage();
            logger.info(response);
            try {
                out.write(response.getBytes());
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Failed to send error message: " + ex.getMessage(), ex);
            }
            return;
        }
        assert uploadCommand != null;

        // ready message
        String response = Protocol.RESP_READY;
        try {
            out.write(response.getBytes());
            logger.info(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to send READY message: " + e.getMessage(), e);
            return;
        }

        // proceed to receive the file... + check for the file size to be < MAX_FILESIZE
        // give the filename a unique identifier to avoid overwriting
        // todo: don't miss to remove the unique identifier when listing files for download
        String uniqueFilename = System.currentTimeMillis() + "_" + uploadCommand.getFilename();
        try (FileOutputStream fos = new FileOutputStream(STORAGE_PATH + uniqueFilename)) {
            logger.info("Trying to receive file: " + uploadCommand.getFilename() + ", size: " + uploadCommand.getFilesize());
            // why i didn't check for actual filesize from the stream:
            //   because in Utils.copyStream() we only copy up to filesize bytes
            Utils.copyStream(in, fos, uploadCommand.getFilesize());
            logger.info("File received: " + uploadCommand.getFilename());
            try {
                String resp = Protocol.RESP_OK + " File uploaded successfully";
                logger.info(resp);
                out.write(resp.getBytes());
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to send upload success message: " + ex.getMessage(), ex);
            }

        } catch (IOException e) {
            try {
                String resp = Protocol.RESP_ERROR + " File transfer failed";
                logger.info(resp);
                out.write(resp.getBytes());
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to receive the file: " + e.getMessage(), e);
            }
        }
    }

    public void list(DataOutputStream dos) {
        // 1. list files in STORAGE_PATH
        StringBuffer fileList = Utils.list(STORAGE_PATH);
        // 2. send the list to the client
        try {
            String response = Protocol.RESP_LIST_PREFIX + "\n" + fileList;
            dos.writeUTF(response);
            logger.info("Sent file list to client.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send file list to client: " + e.getMessage(), e);
        }
    }
}
