function handlerProcess() {
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[3]/div[1]/div//a",
        nextPagePath: "//a[text()='обр╩рЁ']",
        regex : new RegExp('http://www\.qidian\.com/Book/[0-9]+\.aspx', 'i')
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
    
    //Crawler.log(params.data);    
    Crawler.postData(params, info.dataUrl, function(r, suc){callback(r,suc);});
}
