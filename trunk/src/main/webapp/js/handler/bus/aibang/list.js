function handlerProcess() {
    var busLink = XPath.array(document, "//div[@class='hy']//a");
    var cityLink = XPath.array(document, "//div[@class='right_city']//a");
    var lineLink = XPath.array(document, "//div[@class='hy_bd']//p[@class='title0'][1]//a");
    var linkList = [ busLink, cityLink, lineLink ];
    var links = [];
    for ( var j = 0; j < linkList.length; j++) {
        var linkGroup = linkList[j];
        for ( var i = 0; i < linkGroup.length; i++) {
            var url = linkGroup[i].href;
            if (url.endsWith('#')) {
                url = url.substring(0, url.length - 1);
            }
            links.push(url);
        }
    }
    HandlerHelper.storeLinks(links);
}
