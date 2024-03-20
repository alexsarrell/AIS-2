package org.aalpov.client_lab2;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ClientApi api = new ClientApi();
        ConsoleUserApi userApi = new ConsoleUserApi(api);
        userApi.init();
    }
}
