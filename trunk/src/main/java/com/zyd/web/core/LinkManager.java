package com.zyd.web.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class LinkManager {
    private static LinkManager instance = new LinkManager();
    private static HashSet<String> links = new HashSet<String>();

    private LinkManager() {
    }

    public static LinkManager getInstance() {
        return instance;
    }

    /**
     * add a link for processing.
     * @param link 
     * @return true if the link is not there already. false if link is there.
     */
    public synchronized boolean addLink(String link) {
        return links.add(link);
    }

    public synchronized String nextLink() {
        Iterator<String> it = links.iterator();
        if (it.hasNext() == false) {
            return null;
        }
        String l = it.next();
        links.remove(l);
        return l;
    }

    public synchronized void clearLinks() {
        links.clear();
    }

    public int getSize() {
        return links.size();
    }

    public List<String> getAllLinks() {
        return new ArrayList<String>(links);
    }
}
