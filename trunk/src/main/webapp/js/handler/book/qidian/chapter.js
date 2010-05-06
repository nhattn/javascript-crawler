function handlerProcess(){
    document.onselectstart = null;
    document.oncontextmenu = null;

    var c = Crawler, x = XPath, value = '', volume = '', book = {}, chapters = [], chapter = {}, path, nodes;
    path = "/html/body/form[@id='form1']/table[1]/tbody/tr[2]/td[1]/table/tbody/tr/td/div[1]/span[@id='lbChapterName']/text()";
    value = x.stringv(null, path);
    chapter.name = value;
    
    path = "/html/body/form[@id='form1']/table[1]/tbody/tr[2]/td[1]/table/tbody/tr/td/div[@id='clickeye_content']/div[@id='content']/node()";
    nodes = x.array(null, path);
    value = [];
    for(var i=0;i<nodes.length;i++){
        var s = nodes[i].textContent;
        if(s && s.length>0)
            value.push(c.killSpace(s));
    }
    chapter.content = value.join('\n');
    chapter.linkBookByUrl = 'y';
    
    //c.log(chapter.content);
    var params = {data : Ext.util.JSON.encode(chapter)};
    Crawler.postData(params, metaInfo.dataUrl);    
}

var metaInfo = {
    dataUrl : Crawler.serverUrl + '/service/crawler/chapter'        
}

//Crawler.action({action:'Goto.Next.Link'});