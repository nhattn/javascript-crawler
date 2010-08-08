package com.zyd.linkmanager;

public interface LinkManager {
    /**
     * based on the url, the the Link object.
     * returns null if the url is not stored
     * @param url
     * @return
     */
    public Link getLink(String url);

    /**
     * add a new link to store. returns the Link added, will have an assigned id.
     * @param url
     * @return
     */
    public Link addLink(String url);

    /**
     * returns next link from each linkStore one by one. 
     * @return
     */
    public Link roundRobinNextLink();

    /**
     * finished processing the link
     * @param url
     * @return
     */
    public Link linkFinished(String url);

}
