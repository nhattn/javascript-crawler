function handlerProcess() {
    $(document).unbind('contextmenu');
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "/html/body/div[@id='content']/table/tbody/tr/td/table/tbody/tr[2]/td/table/tbody//a",        
        regex : new RegExp('http://www\.readnovel\.com/partlist/[0-9]+', 'i')
    };
    
    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
    //Crawler.log(links);
    HandlerHelper.postBookLinkList(links, {action : 'Goto.Next.Link'});    
}
