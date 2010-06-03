function handlerProcess() {
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "/html/body/div[3]/div[@id='centerm']/div[@id='content']//a",
        nextPagePath : "//div[@id='pagelink']/strong//following-sibling::a[1]",
        regex : 'http://www\.tszw\.com/Article_[0-9]+\.html'
    };

    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
    Crawler.log(links);
    HandlerHelper.postBookLinkList(links, {
        action : 'Goto.XPath.Link.Href',
        param1 : info.nextPagePath
    });
}
