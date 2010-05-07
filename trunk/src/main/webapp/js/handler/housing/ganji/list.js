function handlerProcess() {
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "/html/body/div[@id='wrapper']/div[@id='content']/div[3]/dl/dt/a",
        nextPagePath: "/html/body/div[@id='wrapper']/div[@id='content']/div[3]/div/ul/li/a[@class='c']/parent::*//following-sibling::*[1]",
        regex : new RegExp('http://sh\.ganji\.com/fang1', 'i')
    };
    
    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
    console.log(links);
    HandlerHelper.storeLinks(links, {
        action : 'Eval.XPath.Link.Href',
        param1 : info.nextPagePath
    });    
}
