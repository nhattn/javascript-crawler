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
    console.log(links);
    HandlerHelper.storeLinks(links, {
        action : 'Goto.XPath.Link.Href',
        param1 : info.nextPagePath
    });
}


function isTimeExpired(){
	var rHour = /([0-9]+)小时/;
	var rMinutes = /([0-9]+)分钟/;
	var rDay = /([0-9]+)天/;
	
	var arr = XPath.array(document, "//p[@class='subInfo']//span");
	var now = new Date(), odate = new Date();	
	var maxDifference = CrGlobal.HouseListMaxDifference[CrUtil.getShortestDomain(window.location.host)];
	var difference = 0;
	for(var i=0;i<arr.length;i++){
	    var text = arr[i].textContent;
	    var s = HandlerHelper.getRegGroupFirstValue(text, rMinutes);
	    if(!s || s.length == 0){
	       s = HandlerHelper.getRegGroupFirstValue(text, rHour);
	       if(!s || s.length == 0){
               s = HandlerHelper.getRegGroupFirstValue(text, rDay);
               if(!s || s.length == 0){
                   continue;
               }else{
                   s = parseInt(s) * 24 * 3600 * 1000;
               }
           }else{
               s = parseInt(s) * 3600 * 1000;
           }
        
	    }else{
	       s = parseInt(s) * 60 * 1000;
	    }	    
	    if(s>maxDifference){
	       return true;
	    }	    	   
	}
	return false;
}