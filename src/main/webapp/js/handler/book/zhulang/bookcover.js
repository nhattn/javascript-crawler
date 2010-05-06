function handlerProcess(){     
    var info={
        nextLinkPath: "/html/body/div[4]/div[1]/div[@id='infoBox2']/div[@id='box4']/div/div/span[@id='bt_1']/a",
        mapping : [
            {name:'description', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[6]/div[1]/div[2]/div[3]/div[2]/div[5]/ul/li[last()]/a/font"},
            
            {name:'category',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[6]/div[1]/div[2]/div[1]/div[1]/a[2]"},
    
            {name:'hit',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[6]/div[1]/div[2]/div[3]/div[2]/div[1]",
                param2:/总点击：\s*(\S*)/i},
                
            {name:'recommendation',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[6]/div[1]/div[2]/div[3]/div[2]/div[1]",
                param2:/总投票：\s*(\S*)/i},
                      
            {name:'totalChar',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[6]/div[1]/div[2]/div[3]/div[2]/div[1]",
                param2:/全文长度：\s*(\S*)字/i},        
    
            {name:'author',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[6]/div[1]/div[2]/div[3]/div[2]/div[1]",
                param2:/作　　者：\s*(\S*)/i},        
            
            {name:'updateTime',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[6]/div[1]/div[2]/div[3]/div[2]/div[1]",
                param2:/最新更新：\s*(\S*)/i},                
                    
            {name:'name',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[6]/div[1]/div[2]/div[2]/strong",
                param2:/《(.+)》/i},
                
            {name:'coverUrl', op:'assign.value',  param1:window.location.toString()}
        ]
    };
    var book = HandlerHelper.parseBookCover(info.mapping);    
    Crawler.log(book);
    HandlerHelper.postBookCover(book, {
        action : 'Goto.XPath.Link.Href',
        param1 : info.nextLinkPath
    });  
}