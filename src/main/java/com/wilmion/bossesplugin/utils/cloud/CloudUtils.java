package com.wilmion.bossesplugin.utils.cloud;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class CloudUtils {
    public static Boolean isInternetAvailable() {
        try (Socket socket = new Socket()) {
            Integer timeoutMs = 3000; // Ms for timeout
            InetSocketAddress socketAddress = new InetSocketAddress("www.google.com", 80);

            socket.connect(socketAddress, timeoutMs);

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
