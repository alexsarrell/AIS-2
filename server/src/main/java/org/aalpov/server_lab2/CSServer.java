package org.aalpov.server_lab2;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CSServer {

    private final ServerSocket serverSocket;
    private final ExecutorService executor;
    protected static int maxFileSize;
    protected static String storagePath;

    public CSServer(int port, int threadPoolSize, int sz, String sp) throws IOException {
        serverSocket = new ServerSocket(port);
        executor = Executors.newFixedThreadPool(threadPoolSize);
        maxFileSize = sz;
        storagePath = sp;
    }

    public void start() throws IOException {
        while (true) {
            var socket = serverSocket.accept();
            System.out.println("Connection has started");
            executor.submit(() -> {
                try {
                    new CSHandler(socket);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class CSHandler {

        private DataInputStream in;
        private FileOutputStream out;
        private DataOutputStream clientInput;

        public CSHandler(Socket socket) throws IOException, InterruptedException {

            try (socket) {
                Logger.logConnection();

                String path = getStorage(socket);
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream(), maxFileSize));
                out = new FileOutputStream(path + "\\output.txt");
                clientInput = new DataOutputStream(socket.getOutputStream());

                var res = in.readUTF();
                System.out.println("Result: " + res);
                clientInput.writeUTF("File is successfully uploaded");
                out.write(res.getBytes(StandardCharsets.UTF_8));
            } catch (Exception ex) {
                ex.printStackTrace();
                assert clientInput != null;
                clientInput.writeUTF("Internal server error: " + ex.getMessage());
            } finally {
                in.close();
                out.close();
            }
        }

        private String getStorage(Socket socket) {
            String client = ((InetSocketAddress)
                    socket.getRemoteSocketAddress()).getAddress().getHostAddress();
            String dir = storagePath + "\\" + client.hashCode();
            new File(dir).mkdir();
            return dir;
        }

        private static class Logger {
            static void logConnection() {
                System.out.println(String.format("[DEBUG] Start processing connection in thread [%s]", Thread.currentThread().getName()));
            }
        }
    }
}
