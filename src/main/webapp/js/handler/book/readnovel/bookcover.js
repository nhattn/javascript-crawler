function handlerProcess(){     
    $(document).unbind('contextmenu');    
    var info={
        nextLinkPath: "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[2]/div[1]/div[2]/table[1]/tbody/tr/td[1]/table/tbody/tr[3]/td/a",
        mapping : [
            {name:'description', op:'xpath.textcontent.regex', 
                param1:"/html/body/div[@id='war']/div[@id='main']/div[1]/div[@id='content']/div[1]/ul/li[2]",
                param2:/书籍简介：\s*(\S*)/i},
            
            {name:'category',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[@id='war']/div[@id='main']/div[1]/div[@id='content']/div[1]/ul/li[1]/ul", 
                param2:/类型：\s*(\S*)点击：/i},
    
            {name:'hit',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[@id='war']/div[@id='main']/div[1]/div[@id='content']/div[1]/ul/li[1]/ul", 
                param2:/点击：\s*(\S*)推荐：/i},
    
            {name:'recommendation',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[@id='war']/div[@id='main']/div[1]/div[@id='content']/div[1]/ul/li[1]/ul", 
                param2:/推荐：\s*(\S*)/i},
    
            {name:'totalChar',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[@id='war']/div[@id='main']/div[1]/div[@id='content']/div[1]/ul/li[1]/ul", 
                param2:/总字数：\s*(\S*)/i},        
    
            {name:'author',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[@id='war']/div[@id='main']/div[1]/div[@id='content']/div[1]/ul/li[1]/ul", 
                param2:/作者：\s*(\S*)/i},        
                
            {name:'name',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[@id='war']/div[@id='main']/div[1]/div[@id='content']/h1/a"},
                
            {name:'coverUrl', op:'assign.value',  param1:window.location.toString()}
        ]
    };
    var book = HandlerHelper.parseBookCover(info.mapping);
    
    var chapterInfo = {
        book:book,
        path:"/html/body/div[@id='war']/div[@id='main']/div[1]/div[@id='content']//a",
        regex:['http://www\.readnovel\.com/novel/[0-9]+/[0-9]+.html','javascript:subText'],
        prop:'href',
        volumePath:"/html/body/div[@id='war']/div[@id='main']/div[1]/div[@id='content']/div/h2/text()",
        mapping:[
            {name:'name', op:'provided.node.textcontent'},     
            {name:'chapterUrl', op:'provided.node.property.regex', param1:'href'}
        ]  
    };    
    HandlerHelper.parseChapterList(chapterInfo);
    Crawler.log(book);
    HandlerHelper.postBookCover(book, {action:'Goto.Next.Link'});  
}

function handlerPreprocess(){
    var links = document.links;
    for(var i=0;i<links.length; i++){
        var l = links[i];
        if(l.textContent=='订阅') l.href='';
    }
}

