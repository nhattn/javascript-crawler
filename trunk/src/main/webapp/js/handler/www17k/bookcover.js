function handlerProcess(){     
    var info={
        nextLinkPath: "/html/body/div[4]/div[1]/div[@id='infoBox2']/div[@id='box4']/div/div/span[@id='bt_1']/a",
        mapping : [
            {name:'description', op:'xpath.textcontent.regex', 
                param1:"/html/body/div[4]/div[1]/div[@id='infoBox2']/div[@id='tab1']/div[@id='fragment-0']/p/a"},
            
            {name:'category',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[4]/div[1]/div[@id='infoBox2']/div[@id='tab1']/div[@id='fragment-1']/table/tbody/tr[1]/td[1]/a"},
    
            {name:'hit',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[4]/div[1]/div[@id='infoBox2']/div[@id='box4']/p/big"},
      
            {name:'totalChar',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[4]/div[1]/div[@id='infoBox2']/div[@id='box4']/p/em"},        
    
            {name:'author',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[4]/div[1]/div[@id='infoBox2']/div[@id='bookTitle']/address/a[1]"},        
                
            {name:'name',    op:'xpath.textcontent.regex', 
                param1:"/html/body/div[4]/div[1]/div[@id='infoBox2']/div[@id='bookTitle']/h1/a"},
                
            {name:'coverUrl', op:'assign.value',  param1:window.location.toString()}
        ]
    };
    var book = HandlerHelper.parseBookCover(info.mapping);    
//    Crawler.log(book);
    HandlerHelper.postBookCover(book, {
        action : 'Goto.XPath.Link.Href',
        param1 : info.nextLinkPath
    });  
}