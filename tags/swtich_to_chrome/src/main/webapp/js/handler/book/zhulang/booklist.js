function handlerProcess() {
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "/html/body/div[4]/div[3]/div/div[3]//a",
        nextPagePath : "/html/body/div[4]/div[3]/div/div[4]/form[@id='frmjumppage']/ul/li[4]/a",
        regex : 'http://www\.zhulang\.com/[0-9]+/index\.html'
    };

    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
    Crawler.log(links);
    HandlerHelper.postBookLinkList(links, {
        action : 'Goto.XPath.Link.Href',
        param1 : info.nextPagePath
    });
}
