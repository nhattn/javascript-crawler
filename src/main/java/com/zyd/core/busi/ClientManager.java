package com.zyd.core.busi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        ArrayList<Client> list = new ArrayList<Client>(clients.values());
        Collections.sort(list, new Comparator<Client>() {
            public int compare(Client o1, Client o2) {
                return -o1.lastAccess.compareTo(o2.lastAccess);
            }
        });
        for (Client c : list) {
            buf.append(c.toString());
            buf.append("\n");
        }
        return buf.toString();

    }
}
