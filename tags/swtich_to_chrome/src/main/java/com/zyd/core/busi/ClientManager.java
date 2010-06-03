package com.zyd.core.busi;

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.zyd.core.dom.Client;

public class ClientManager {
    HashMap<String, Client> clients;

    public ClientManager() {
        clients = new HashMap<String, Client>();
    }

    public void logRequest(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        Client client = clients.get(ip);
        if (client == null) {
            client = new Client();
            client.ip = ip;
            clients.put(ip, client);
        }
        client.lastAccess = new Date();
        client.lastSite = request.getHeader("Referer");
        client.processedCount++;
    }

    public String getClientReport() {
        StringBuffer buf = new StringBuffer();
        for (Client c : clients.values()) {
            buf.append(c.toString());
            buf.append("\n");
        }
        return buf.toString();

    }
}
