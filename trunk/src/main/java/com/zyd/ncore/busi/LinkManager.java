package com.zyd.ncore.busi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.zyd.Config;
import com.zyd.web.dom.Book;

public class LinkManager {
    private static LinkManager instance = new LinkManager();
    private static HashSet<String> links = new HashSet<String>();
    private static HashMap<String, Book> linkWithBook = new HashMap<String, Book>();
    private static String IdlePageUrl = Config.ServerUrl + "/html/wait.html";

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

    public synchronized boolean addLink(String link, Book book) {
        if (links.add(link)) {
            linkWithBook.put(link, book);
            return true;
        }
        return false;
    }

    public synchronized Book getBookForLink(String link) {
        return linkWithBook.remove(link);
    }

    public synchronized boolean addLinks(Collection<String> nlinks) {
        return LinkManager.links.addAll(nlinks);
    }

    public synchronized boolean addLinks(Collection<String> nlinks, Book book) {
        boolean ret = false;
        for (String s : nlinks) {
            if (links.add(s)) {
                linkWithBook.put(s, book);
                ret = true;
            }
        }
        return ret;
    }

    public synchronized String nextLink() {
        Iterator<String> it = links.iterator();
        if (it.hasNext() == false) {
            return IdlePageUrl;
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
