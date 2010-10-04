function handlerProcess() {
    var arr = XPath.array(null, '//ul[@onmouseover]');
    if (!arr || arr.length == 0) {
        Crawler.nextLink();
        return;
    }
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "//div[@class='xslist']//a",
        regex : new RegExp('_', 'i')
    };

    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
    //    console.log(links);
    HandlerHelper.storeLinks(links, {
        action : 'Goto.Next.Link'
    });
}