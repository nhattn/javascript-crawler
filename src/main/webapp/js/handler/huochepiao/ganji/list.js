function handlerProcess() {
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "//div[@class='list']//dl[@class='list_piao']//a",
        nextPagePath : "//a[contains(text(), '下一页')]",
        regex : new RegExp('/piao/[0-9]+_[0-9]+', 'i')
    };

    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
    HandlerHelper.storeLinks(links, {
        action : 'Goto.XPath.Link.Href',
        param1 : info.nextPagePath
    });
}

function getDate() {
    var r = '', date = new Date();
    var s = (date.getMonth() + 1) + '';
    if (s.length == 1) {
        r = r + '0' + s;
    } else {
        r = r + s;
    }
    s = (date.getDate() + 1) + '';
    if (s.length == 1) {
        r = r + '0' + s;
    } else {
        r = r + s;
    }
    return r;
}
