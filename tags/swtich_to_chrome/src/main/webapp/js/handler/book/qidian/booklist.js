function handlerProcess() {
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[3]/div[1]/div//a",
        nextPagePath: "//a[text()='обр╩рЁ']",
        regex : new RegExp('http://www\.qidian\.com/Book/[0-9]+\.aspx', 'i')
    };
    
    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
    HandlerHelper.postBookLinkList(links, {
        action : 'Eval.XPath.Link.Href',
        param1 : info.nextPagePath
    });    
}
