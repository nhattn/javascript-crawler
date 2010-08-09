package com.zyd.linkmanager;

public interface LinkManager extends com.zyd.core.busi.WorkerThread.Job {
    /**
     * @param url
     * @return a link in the processing queue based on it's url
     */
    public Link getProcessingLink(String url);

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

    public Link getLink(String url);

    /**
     * If link is finished with an error.
     * @param url
     * @param state
     * @param msg
     * @return
     */
    public Link linkFinishedError(String url, int state, String msg);

    /**
     * if a link is in the processing queue for too long, mark it as failed.
     * @return
     */
    public int cleanExpiredProcessingLink();
    
    /**
     * depending on how many links is left unprocessed, return link refresh interval for client
     * @return
     */
    public int getSuggestedLinkRefreshInterval();
}
