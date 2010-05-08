function handlerProcess() {
	if(isTimeExpired()==true){
		Crawler.action({
			action:'Goto.Next.Link'
		});		
		return;
	}
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


function isTimeExpired(){
	var reg = /([0-9][0-9])-([0-9][0-9]) ([0-9][0-9]):([0-9][0-9])/;
	var arr = XPath.array(document, "//span[contains(@class,'time')]");
	var now = new Date(), odate = new Date();
	var maxDifference = 36000 * 1000;
	for(var i=0;i<arr.length;i++){
		var s = arr[i].textContent;		
		if(!s || s.trim().length ==0){
			continue;
		}			
		
		s = s.trim();
		var ts = s.match(reg);			
		if(!ts || ts.length!=5){
			continue;
		}
		odate.setFullYear(now.getFullYear());
		odate.setMonth(parseInt(ts[1])-1);
		odate.setDate(parseInt(ts[2]));
		odate.setHours(parseInt(ts[3]));
		odate.setMinutes(parseInt(ts[4]));
		if((now.getTime() - odate.getTime())>maxDifference){
			return true;
		}
	}
	return false;
}