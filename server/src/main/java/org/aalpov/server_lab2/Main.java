package org.aalpov.server_lab2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException { bootRun(args); }

    private static void bootRun(String[] args) throws IOException {
        // if (args.length == 0) return;

        if (args.length % 2 != 0) {
            throw new IllegalArgumentException(String.format("Failed to parse provided args %s", String.join(", ", args)));
        }

        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i+=2) {
            params.put(args[i], args[i + 1]);
        }

        var port = Integer.parseInt(params.getOrDefault("--port", "10010"));
        var threadPoolSize = Integer.parseInt(params.getOrDefault("-ts","10"));
        var maxFileSize = Integer.parseInt(params.getOrDefault("-mx", "20000"));
        var path = params.getOrDefault("--path", "C:\\Users\\alexs\\IdeaProjects\\lab2\\client");

        CSServer server = new CSServer(port, threadPoolSize, maxFileSize, path);
        server.start();
    }
}
