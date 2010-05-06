function handlerProcess() {
    $(document).unbind('contextmenu');
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "/html/body/div[@id='war']/div/div/table/tbody//a",        
        regex : new RegExp('http://www\.readnovel\.com/archive/[0-9]+/[0-9]+', 'i')
    };
    
    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
    //Crawler.log(links);
    HandlerHelper.postBookLinkList(links, {action : 'Goto.Next.Link'});    
}
