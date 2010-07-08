function handlerProcess() {
	if(isTimeExpired()==true){		
		Crawler.log('House list date expried, will stop process');
		Crawler.action({
			action:'Goto.Next.Link'
		});		
		return;
	}	
	
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "//div[@id='bd']//a[@class='yk-av']",
        nextPagePath: "//div[@id='page-num']//strong/following-sibling::a[1]",
        regex : new RegExp('fang/detail', 'i')
    };    
    
    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
//    console.log(links);
    HandlerHelper.storeLinks(links, {
        action : 'Goto.XPath.Link.Href',
        param1 : info.nextPagePath
    });
}




function isTimeExpired() {  
    var arr = XPath.array(document, "//p[@class='subInfo']//span"), now = new Date();    
    var maxDifference = CrGlobal.HouseListMaxDifference[CrUtil.getShortestDomain(window.location.host)];
    var errorCount = 0;
    for ( var i = 0; i < arr.length; i++) {
        var s = arr[i].textContent;
        if (!s || s.trim().length == 0) {
            continue;
        }        
        var oDate = CrUtil.guessTime(s.trim());
        if (!oDate) {
            errorCount++;
            if (errorCount > 10) {
                Crawler.attention('Too many error times in list, the last one is: ' + s);
                return true;
            }
        }
        if ((now.getTime() - oDate.getTime()) > maxDifference) {
            return true;
        }        
    }
    return false;
}