function handlerProcess() {
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "/html/body/div[3]/div[@id='centerm']/div[@id='content']//a",
        nextPagePath: "//div[@id='pagelink']/strong//following-sibling::a[1]",
        regex : new RegExp('http://www\.tszw\.com/Article_[0-9]+\.html', 'i')
    };

    var links = XPath.array(null, info.path), reg = info.regex;
    var books = [];
    for ( var i = 0; i < links.length; i++) {
        var l = links[i].href.toString();
        if (reg.test(l)) {
            books.push(l);
        }
    }
    var params = {
        data : Ext.util.JSON.encode(books)
    };
    //Crawler.log(params.data);
    var callback = function(r,suc){        
        try{
            var obj = Ext.util.JSON.decode(r.responseText);
            if(obj.result == 0 ){
                // no new book added
                Crawler.nextLink();
                return;
            }
        }catch(e){
            // error eval response, goto nextLink
            Crawler.error('qidian.booklist:'+e+':'+r.responseText);
            Crawler.nextLink();            
            return;
        }
        
        if(XPath.single(null, info.nextPagePath)){                        
            Crawler.action({action:'Eval.XPath.Link.Href',param1:info.nextPagePath});
        }else{            
            Crawler.nextLink();
        }
        //Crawler.log(r.responseText);    
    };
    
    Crawler.log(params.data);    
    //Crawler.postData(params, info.dataUrl, function(r, suc){callback(r,suc);});
}
