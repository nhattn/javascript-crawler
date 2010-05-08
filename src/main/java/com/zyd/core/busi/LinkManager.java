package com.zyd.core.busi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.zyd.Config;

public class LinkManager {

    private HashSet<String> links = new HashSet<String>();

    /**
     * stores processed links
     */
    private HashSet<String> plinks = new HashSet<String>();

    /**
     * store links currently been proccessed
     */
    private HashSet<String> clinks = new HashSet<String>();

    private LinkManager() {
    }

    public synchronized boolean addLink(String link) {
        if (plinks.contains(link)) {
            return false;
        }
        return links.add(link);
    }

    public synchronized boolean addLinks(List<String> nlinks) {
        int added = 0;
        for (int i = 0, len = nlinks.size(); i < len; i++) {
            if (addLink(nlinks.get(i)) == true) {
                added++;
            }
        }
        return added != 0;
    }

    public synchronized String nextLink() {
        Iterator<String> it = links.iterator();
        if (it.hasNext() == false) {
            return Config.IdlePageUrl;
        }
        String l = it.next();
        links.remove(l);
        clinks.add(l);
        return l;
    }

    public synchronized void deleteAllLinks() {
        links.clear();
        plinks.clear();
    }

    public int getSize() {
        return links.size();
    }

    public List<String> getAllLinks() {
        return new ArrayList<String>(links);
    }

    public boolean isLinkProcessed(String link) {
        return plinks.contains(link);
    }

    public synchronized void linkProcessed(String link) {
        plinks.add(link);
        clinks.remove(link);
    }
}
