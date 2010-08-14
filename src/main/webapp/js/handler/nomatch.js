// called when there is no match for url
function handlerProcess() {
    Crawler.clog("Nothing to do for this url");
    Crawler.clog(window.location);
    Crawler.nextLink();
}
