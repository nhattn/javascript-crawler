function handlerProcess(){
    var c = Crawler, value = '', volume = '', book = {}, chapters = [];
    var path = "/html/body/form[@id='form1']/center/div[@id='content']/div";
    var dateRegex = /\d+\-\d+\-\d+\s\d\d?:\d\d:\d\d/;
    var nodes = XPath.array(null, path);
    
    for(var i=0;i<nodes.length;i++){
        var n = nodes[i];
        if(n.className == 'chat'){
            volume = XPath.stringv(n, 'strong/text()');    
        }else if(n.className == 'list'){
            var links = XPath.array(n, './/a');
            for(var j=0;j<links.length;j++){
                var l = links[j], chapter = {}, s;
                s = l.title;
                chapter.chapterUrl = l.href;
                chapter.totalChar = c.extract(s, '×ÖÊý£º');
                chapter.updateTime = s.match(dateRegex)[0];
                chapter.name = l.textContent.trim();
                if(volume){
                    chapter.volume = volume;
                }
                chapters.push(chapter);
            }            
        }
    }
    
    book.name = XPath.stringv(null, "/html/body/form[@id='form1']/center/b/h1/text()");
    book.author = XPath.stringv(null, "/html/body/form[@id='form1']/center/table/tbody/tr/td[1]/a/b/text()");
    book.chapters = chapters;    
    var params = {data : Ext.util.JSON.encode(book)};
    c.log(params.data);
    Crawler.postData(params, metaInfo.dataUrl, function(){return;Crawler.nextLink();});
}

var metaInfo = {
    dataUrl : Crawler.serverUrl + '/service/book'        
}

//Crawler.action({action:'Goto.Next.Link'});