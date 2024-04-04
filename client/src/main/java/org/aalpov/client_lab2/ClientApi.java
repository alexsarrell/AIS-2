package org.aalpov.client_lab2;

import java.net.*;
import java.io.*;
import java.nio.file.Files;

public class ClientApi {
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
        in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
    }

    public void sendFile(String path) throws IOException {
        try {
            File file = new File(path);

            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

            out.writeUTF(String.join("\n", Files.readAllLines(file.toPath())));

            String result = in.readUTF();
            System.out.println(result);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. Check your file path");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            out.close();
            in.close();
            clientSocket.close();
        }
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
