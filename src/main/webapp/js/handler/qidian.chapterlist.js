function handlerProcess(){
    var c = Crawler, value = '', volume = '', book = {}, chapters = [];
    var path = "/html/body/form[@id='form1']/center/div[@id='content']/div";
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
                chapter.link = l.href;
                chapter.totalChar = c.extract(s, '字数：');
                chapter.updateTime = c.extract(s, '更新时间：');
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
    book.linkWithChapterUrl = 'y';
    //c.log(book);
    var params = {data : Ext.util.JSON.encode(book)};
    Crawler.postData(params, metaInfo.dataUrl);
}

var metaInfo = {
    dataUrl : Crawler.serverUrl + '/service/crawler/chapterlist'        
}

//Crawler.action({action:'Goto.Next.Link'});