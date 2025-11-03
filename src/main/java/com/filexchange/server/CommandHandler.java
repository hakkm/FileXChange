package com.filexchange.server;

import com.filexchange.common.FileDownloadCommand;
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

    public void download(DataInputStream dis, DataOutputStream dos, String[] parts) {
        //
//        3. Then Server responds with `OK <filesize>\n` if the file exists, or `ERROR <message>\n` if there was an error.
        FileDownloadCommand fdc;
        try {
            fdc = Utils.isFileAvailableForDownload(parts[1].trim(), STORAGE_PATH);
            assert fdc != null;
            String ok = Protocol.RESP_OK + " " + fdc.getFilesize();
            dos.writeUTF(ok);
        } catch (FileNotFoundException e) {
            String response = Protocol.RESP_ERROR + " File not found";
            logger.info(response);
            try {
                dos.writeUTF(response);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to send error message: " + ex.getMessage(), ex);
            }
            return;
        } catch (IOException e) {
            String response = Protocol.RESP_ERROR + " Internal server error";
            logger.info(response);
            try {
                dos.writeUTF(response);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to send error message: " + ex.getMessage(), ex);
            }
            return;
        }

//        4. Then Client sends `READY\n` to indicate it's ready to receive the file.
        try {
            String clientReady = dis.readUTF();
            if (!clientReady.equals(Protocol.RESP_READY)) {
                String response = Protocol.RESP_ERROR + " Client not ready";
                logger.info(response);
                dos.writeUTF(response);
                return;
            }
        } catch (IOException e) {
            String response = Protocol.RESP_ERROR + " Failed to read client readiness";
            logger.info(response);
            try {
                dos.writeUTF(response);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to send error message: " + ex.getMessage(), ex);
            }
            return;
        }
//        5. Then Server sends the file content in binary format.
        try (FileInputStream fis = new FileInputStream(fdc.getFilepath())) {
            Utils.copyStream(fis, dos, fdc.getFilesize());
            logger.info("File sent: " + fdc.getFilename());
        } catch (IOException e) {
            String response = Protocol.RESP_ERROR + " File transfer failed";
            logger.info(response);
            try {
                dos.writeUTF(response);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to send error message: " + ex.getMessage(), ex);
            }
            return;
        }
//        6. Then Client responds with `SUCCESS\n` if the file is received successfully, or `ERROR <message>\n` if there was an
        try {
            String clientResponse = dis.readUTF();
            if (clientResponse.equals(Protocol.RESP_SUCCESS)) {
                logger.info("Client confirmed successful file receipt: " + fdc.getFilename());
            } else {
                logger.info("Client reported error after file transfer: " + clientResponse);
            }
        } catch (IOException e) {
            String response = Protocol.RESP_ERROR + " Failed to read client response after file transfer";
            logger.info(response);
            try {
                dos.writeUTF(response);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to send error message: " + ex.getMessage(), ex);
            }
        }
    }
}
