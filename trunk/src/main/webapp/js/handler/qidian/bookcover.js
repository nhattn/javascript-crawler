function handlerProcess(){
    var info = {
        dataUrl : Crawler.serverUrl + '/service/book',
        nextLinkPath: "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[1]/table/tbody/tr[3]/td/a"    
    };
    var path, value, book = {}, C=Crawler;    
    path = "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody/tr[1]/td/h1/b";
    value = XPath.single(null, path).textContent;
    if(!value) value = '';
    book.name = value.trim();
    
    path = "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody/tr[1]/td/h1/span/a";
    value = XPath.single(null, path).textContent;
    book.author = value
    
    path = "/html/body[1]/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody/tr[6]/td/node()";
    value = XPath.array(null, path);
    var d = [];
    for(var i=0;i<value.length;i++){
        var t = value[i];
        if(t.tagName == 'LABEL' || t.tagName == 'SCRIPT' || t.tagName == 'STYLE'){
            break;
        }
        t = t.textContent;
        if(typeof t == undefined) t = '';
        d.push(t);
    }
    book.description = C.killSpace(d.join(' '));
    
    path = "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[1]/table/tbody/tr[3]/td/a";
    value = XPath.single(null, path).href;
    book.allChapterUrl = value    
    
    path = "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody/tr[2]/td";    
    value = XPath.single(null, path).textContent;    
    book.category = C.extract(value, '小说类别：');
    book.hit = C.extract(value, '总点击：');
    book.recomendation = C.extract(value, '总推荐：');
    book.totalChar = C.extract(value, '总字数：');
    book.updateTime = C.extract(value, '更新：');    
    book.coverUrl = window.location.href;
    //C.clog(C.objToString(book));    
    var params = {data : Ext.util.JSON.encode(book)};
    
    var gotoChapterList = function(r, suc){
        if(!suc){
            try{Crawler.error("qidian.bookcover.gotoChapterList:"+r.responseText);}catch(e){}                
            Crawler.nextLink();
            return;
        }        
        try{            
            r = Ext.util.JSON.decode(r.responseText);
        }catch(e){
            Crawler.error('qidian.bookcover:'+e+':'+r.responseText);
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

