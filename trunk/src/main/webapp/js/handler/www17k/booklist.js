function handlerProcess() {
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "/html/body/div[@id='list']/table/tbody//a",
        nextPagePath : "/html/body/div[@id='list']/div[2]/strong/following-sibling::a[1]",
        regex : 'http://.+\.17k\.com/book/[0-9]+.html'
    };

    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
    Crawler.log(links);
    HandlerHelper.postBookLinkList(links, {
        action : 'Goto.XPath.Link.Href',
        param1 : info.nextPagePath
    });
}
