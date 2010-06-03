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
        path : "/html/body/div[@id='wrapper']/div[@id='content']/div[3]/dl/dt/a",
        nextPagePath: "/html/body/div[@id='wrapper']/div[@id='content']/div[3]/div/ul/li/a[@class='c']/parent::*//following-sibling::*[1]/a",
        regex : new RegExp('http://sh\.ganji\.com/fang[0-9]+', 'i')
    };    
    
    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
    console.log(links);
    HandlerHelper.storeLinks(links, {
        action : 'Goto.XPath.Link.Href',
        param1 : info.nextPagePath
    });    
}


/**
  * it will go down the list, 
  * if it sees a '更新' , will return false,
  * if it sees a time that is not expired, will return false.
  * if it sees a time that is expired, will return true.
  * if it goes down the list and see nothing meet the conditions before, will return false.
  */
function isTimeExpired(){
	var reg = /([0-9][0-9])-([0-9][0-9]) ([0-9][0-9]):([0-9][0-9])/;
	var arr = XPath.array(document, "//span[contains(@class,'time')]");
	var now = new Date(), odate = new Date();
    var maxDifference = CrGlobal.HouseListMaxDifference[CrUtil.getShortestDomain(window.location.host)];
	for(var i=0;i<arr.length;i++){
		var s = arr[i].textContent;		
		if(!s || s.trim().length ==0){
			continue;
		}			
		
		s = s.trim();
		if(s.indexOf('更新')!=-1){
		    return false;
		}
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
		}else{
		    return false;
		}
	}
	return false;
}