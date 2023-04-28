package org.example.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleSessionManager implements SessionManager {

    private final Map<Integer, String> sessionRegistry = new ConcurrentHashMap<>();

    @Override
    public String getSessionId(int orderId) {
        return sessionRegistry.computeIfAbsent(orderId,
            integer -> UUID.randomUUID().toString().split("-")[0]);
    }
}
