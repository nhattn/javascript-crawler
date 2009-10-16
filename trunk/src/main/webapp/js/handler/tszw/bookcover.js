function handlerProcess(){
    var info = {
        dataUrl : Crawler.serverUrl + '/service/book',
        nextLinkPath: "/html/body/div[3]/div[@id='centerm']/div[@id='content']/div[1]/div[3]/a[1]"    
    };
    var path, value, book = {}, C=Crawler;    
    
    path = "/html/body/div[3]/div[@id='centerm']/div[@id='content']/div[1]/div[1]/p";
    value = XPath.single(null, path).textContent;
    if(!value) value = '';
    book.name = value.trim();
    
    path = "/html/body[1]/div[3]/div[@id='centerm']/div[@id='content']/div[1]/table/tbody/tr[1]/td[2]";
    value = XPath.single(null, path).textContent.trim().substring(5);
    book.author = value
    
    path = "/html/body/div[3]/div[@id='centerm']/div[@id='content']/div[1]/div[2]/p[1]";
    book.description = XPath.single(null, path).textContent;
    
    path = "/html/body/div[3]/div[@id='centerm']/div[@id='content']/div[1]/div[3]/a[1]";
    value = XPath.single(null, path).href;
    book.allChapterUrl = value    
    
    path = "/html/body/div[3]/div[@id='centerm']/div[@id='content']/div[1]/table/tbody";    
    value = XPath.single(null, path).textContent;    
    book.category = C.extract(value, '作品类别：');
    book.hit = C.extract(value, '总点击：');
    book.recomendation = C.extract(value, '总推荐：');
    book.totalChar = C.extract(value, '完成字数：');
    book.updateTime = C.extract(value, '更新时间：');    
    book.coverUrl = window.location.href;
    //C.clog(C.objToString(book));    
    var params = {data : Ext.util.JSON.encode(book)};
    
    var gotoChapterList = function(r, suc){
        return;
        if(!suc){
            try{Crawler.error("bookcover.gotoChapterList:"+r.responseText);}catch(e){}                
            Crawler.nextLink();
            return;
        }        
        try{            
            r = Ext.util.JSON.decode(r.responseText);
        }catch(e){
            Crawler.error('bookcover:'+e+':'+r.responseText);
            Crawler.nextLink();
            return;
        }
        if(r.result){
            // something changed, updateTime changed? go and update book
            if(XPath.single(null, info.nextLinkPath)){
                Crawler.action({action:'Goto.XPath.Link.Href',param1:info.nextLinkPath});
            }else{
                Crawler.nextLink();
            }            
        }else{
            // nothing changed, ignore, gotonextlink
            Crawler.nextLink();
        }
    }
    Crawler.log(params.data);
    Crawler.postData(params, info.dataUrl, gotoChapterList);
}

