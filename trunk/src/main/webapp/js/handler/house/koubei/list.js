function handlerProcess() {
	if(isTimeExpired()==true && false){		
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
    HandlerHelper.storeLinks(links, {
        action : 'Goto.XPath.Link.Href',
        param1 : info.nextPagePath
    });
}


function isTimeExpired(){
	var reg = /([0-9][0-9])-([0-9][0-9]) ([0-9][0-9]):([0-9][0-9])/;
	var arr = XPath.array(document, "//span[contains(@class,'time')]");
	var now = new Date(), odate = new Date();
	var maxDifference = CrGlobal.HouseListMaxDifference;
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
		odate.setMonth(parseInt(ts[1],10)-1);
		odate.setDate(parseInt(ts[2],10));
		odate.setHours(parseInt(ts[3],10));
		odate.setMinutes(parseInt(ts[4],10));
		//console.log(now.getTime() - odate.getTime()+'  '+s+'  '+maxDifference);
		if((now.getTime() - odate.getTime())>maxDifference){
			return true;
		}
	}
	return false;
}