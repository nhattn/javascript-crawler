function handlerProcess(){     
    var info={
        nextLinkPath: "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[1]/table/tbody/tr[3]/td/a",
        mapping : [
            {name:'description', op:'run.func', param1:parseDesc,  
                param2:"/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody/tr[6]/td/node()"},
            
            {name:'category',    op:'xpath.textcontent.regex', 
                param1:"/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody", 
                param2:/小说类别：\s*(\S*)/i},
    
            {name:'hit',    op:'xpath.textcontent.regex', 
                param1:"/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody", 
                param2:/总点击：\s*(\S*)/i},
    
            {name:'recommendation',    op:'xpath.textcontent.regex', 
                param1:"/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody", 
                param2:/总推荐：\s*(\S*)/i},
    
            {name:'totalChar',    op:'xpath.textcontent.regex', 
                param1:"/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody", 
                param2:/总字数：\s*(\S*)/i},       
                                             
            {name:'updateTime',    op:'xpath.textcontent.regex', 
                param1:"/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody", 
                param2:/更新：\s*(\S*)/i},       
    
            {name:'author',    op:'xpath.textcontent.regex', 
                param1:"/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody", 
                param2:/小说作者：\s*(\S*)/i},        
                
            {name:'name',    op:'xpath.textcontent.regex', 
                param1:"/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[2]/table/tbody/tr[1]/td/h1/b"},
                
            {name:'coverUrl', op:'assign.value',  param1:window.location.toString()}
        ]
    };
    var book = HandlerHelper.parseBookCover(info.mapping);
    HandlerHelper.postBookCover(book, {action:'Goto.XPath.Link.Href', param1:info.nextLinkPath});
}


function parseDesc(xp){
    var nodes, r = [], excludes = ['style', 'script', 'table'];
    nodes = XPath.array(null, xp);
    for(var i=0;i<nodes.length;i++){
        var n = nodes[i];
        if(n.tagName && excludes.indexOf(n.tagName.toLowerCase())>=0){
            continue;
        }
        r.push(n.textContent);        
    }
    r = r.join(' ');
    r = Crawler.killSpace(r);    
    return r;
}
