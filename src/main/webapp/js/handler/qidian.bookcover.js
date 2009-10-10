function handlerProcess(){
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
    book.allChapterLink = value    
    
    path = "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody/tr[2]/td";    
    value = XPath.single(null, path).textContent;    
    book.cat2 = C.extract(value, '小说类别：');
    book.hits = C.extract(value, '总点击：');
    book.recomendation = C.extract(value, '总推荐：');
    book.totalChar = C.extract(value, '总字数：');
    book.updateTime = C.extract(value, '更新：');    
    //C.clog(C.objToString(book));    
    var params = {data : Ext.util.JSON.encode(book)};
    Crawler.postData(params, metaInfo.dataUrl);
}

var metaInfo = {
    dataUrl : Crawler.serverUrl + '/service/crawler/book'        
}
