# Application Protocol

We'll define a text based protocol for communication. User interacts with the client through a command line interface (
CLI). The client and server communicate using plain text commands and responses.

## Commands

### Upload File

1. User write command `UPLOAD <filename>` in CLI, then
2. Client sends `UPLOAD <filename> <filesize>\n` to the server
3. Then Server responds with `READY\n` if ready to receive the file. Or `ERROR <message>\n` if there was an error. (include
bigger than max size)
4. Then Client sends the file content in binary format.
5. Then Server responds with `SUCCESS\n` if the file is received successfully, or `ERROR <message>\n` if there was an
error.

### Download File

1. User write command `DOWNLOAD <filename>` in CLI, then
2. Client sends `DOWNLOAD <filename>\n` to the server.
3. Then Server responds with `OK <filesize>\n` if the file exists, or `ERROR <message>\n` if there was an error.
4. Then Client sends `READY\n` to indicate it's ready to receive the file.
5. Then Server sends the file content in binary format.
6. Then Client responds with `SUCCESS\n` if the file is received successfully, or `ERROR <message>\n` if there was an
error.

### List Files

1. User write command `LIST` in CLI, then
2. Client sends `LIST\n` to the server.
3. Then Server responds with `FILES <file1> <file2> ... <fileN>\n` listing all available files, or `ERROR <message>\n` if
there was an error.

### Delete File

1. User write command `DELETE <filename>` in CLI, then
2. Client sends `DELETE <filename>\n` to the server.
3. Then Server responds with `SUCCESS\n` if the file was deleted successfully, or `ERROR <message>\n` if there was an
error.

### Quit Connection

1. User write command `QUIT` in CLI, then
2. Client sends `QUIT\n` to the server.
3. Then Server responds with `BYE\n` and closes the connection.