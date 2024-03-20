package org.aalpov.client_lab2;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleUserApi {

    private final ClientApi clientApi;

    public ConsoleUserApi(ClientApi clientApi) {
        this.clientApi = clientApi;
    }

    public void init() throws IOException {
        try {
            Scanner in = new Scanner(System.in);
            var listener = new UserInputListener(in, this);
            listener.listen();
        } finally {
            clientApi.stopConnection();
        }
    }

    private void sendFile(String host, String path) throws IOException {
        String[] uri = host.split(":");

        String url = uri[0];
        int port = Integer.parseInt(uri[1]);

        clientApi.startConnection(url, port);
        clientApi.sendFile(path);
    }

    private record UserInputListener(Scanner scanner, ConsoleUserApi api) {
        private static final String EXIT_COMMAND = "exit";
        private static final String NEW_FILE_COMMAND = "-u file";
        private static final String CHANGE_HOST_COMMAND = "-u host";
        private static final String SEND_COMMAND = "send";
        private static final String HOST_REGEX = "\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}:\\d{1,5}";
        private static final Pattern HOST_PATTERN = Pattern.compile(HOST_REGEX);
        private static String host;
        private static String path;

        public void listen() throws IOException {

            updateHost();
            updatePath();

            do {
                switch (scanner.nextLine()) {
                    case EXIT_COMMAND -> {
                        return;
                    }
                    case NEW_FILE_COMMAND -> updatePath();
                    case CHANGE_HOST_COMMAND -> updateHost();
                    case SEND_COMMAND -> api.sendFile(host, path);
                }
            } while (true);
        }

        private void updateHost() {
            String result;
            while (true) {
                System.out.println("Enter server host:");
                result = scanner.nextLine();
                if (HOST_PATTERN.matcher(result).matches()) {
                    break;
                } else {
                    System.out.println("Host must satisfy the following pattern [ip:port]");
                }
            }
            host = result;
        }

        private void updatePath() {
            String result;
            while (true) {
                System.out.println("Enter absolute file path:");
                result = scanner.nextLine();
                if (new File(result).exists()) {
                    break;
                } else {
                    System.out.println("File not found, check your path and try again");
                }
            }
            path = result;
        }
    }
}
