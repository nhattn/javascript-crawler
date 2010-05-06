HandlerHelper = {
    bookListUrl: Crawler.serverUrl + '/service/link',
    bookUrl:     Crawler.serverUrl + '/service/book',
    
    getMatchLinks: function(links, reg){
        if(typeof reg == 'string'){
            reg = new RegExp(reg,'i');
        }
        if(typeof links == 'string'){
            links = XPath.array(null, links);
        }
        var r = [];
        for(var i = 0; i < links.length; i++) {
            var l = links[i].href.toString();
            if (reg.test(l)) {
                r.push(l);
            }
        }        
        return r;
    },
    
    postBookLinkList: function(linkArray, nextAction){
        if(!linkArray || linkArray.length==0){
            Crawler.nextLink();  
            return;
        }
        var data = {'data': Ext.util.JSON.encode(linkArray)};        
        var callback = function(r,suc){                    
            try{
                var obj = Ext.util.JSON.decode(r.responseText);
                if(obj.result == 0 ){             
                    Crawler.nextLink();  
//                    Crawler.action(nextAction);
                }else{
                    Crawler.action(nextAction);
                }
            }catch(e){
                Crawler.error('HandlerHelper:'+e+':'+r.responseText);
                Crawler.nextLink();            
            }                                    
        };            
        Crawler.postData(data, HandlerHelper.bookListUrl, function(r, suc){callback(r,suc);});    
    },
    
    parseBookCover: function(mapping){    
        var book = {};
        for(var i=0;i<mapping.length;i++){
            var m = mapping[i];
            switch(m.op){
            case 'run.func':            
                book[m.name] = m.param1.apply(null, HandlerHelper.getParams(m).slice(1)).trim();
                break;
            case 'xpath.textcontent.regex':
                book[m.name] = HandlerHelper.extractFromXpathNodeText.apply(HandlerHelper, HandlerHelper.getParams(m)).trim();                 
                break;
            case 'assign.value':
                book[m.name] = m.param1;
                break;
            default:
                Crawler.error('wrong op'+m.op);
            }        
        }
        return book;
    },
    
    postBookCover: function(book, nextAction) {
        var params = {data : Ext.util.JSON.encode(book)};
        var callback = function(r, suc) {        
            if (!suc) {
                Crawler.error("postbookcover:" + r.responseText);
                Crawler.nextLink();
                return;
            }
            
            try {
                r = Ext.util.JSON.decode(r.responseText);                        
                if (r.result) {
                    Crawler.action(nextAction);
                }else{
                    Crawler.nextLink();
                }
            } catch (e) {
                Crawler.error('postbookcover:' + e + ':' + r.responseText);
                Crawler.nextLink();
            }        
        }    
        Crawler.postData(params, HandlerHelper.bookUrl, callback);
    },

    parseChapterList: function(info){
        var book = {};
        if(typeof info.book != 'undefined'){
            book = info.book;
        }
        var arr = XPath.array(null, info.path); 
        
        var links = [], chapters=[], regex = [], prop = info.prop, mapping = info.mapping;    
        for(var i=0;i<info.regex.length;i++){
            regex.push(new RegExp(info.regex[i],'i'));
        }
        for(var i=0;i<arr.length;i++){                
            var n = arr[i];
            var v = n[prop];
            if(regex.length>0){            
                for(var j=0;j<regex.length;j++){
                    if(regex[j].test(v)==true){
                        links.push(n);
                    }
                }
            }else{
                links.push(n);
            }
        }
        var volInfo = HandlerHelper.parseChapterListVolumeInfo(info.volumePath);
        for(var i=0;i<links.length;i++){
            var n = links[i];        
            var chapter = HandlerHelper.parseChapterEntry(n, mapping, volInfo);
            chapters.push(chapter);
        }    
        book.chapters = chapters;
        if(info.bookMapping && info.bookMapping.length!=0){
            for(var i=0;i<info.bookMapping.length;i++){
                HandlerHelper.mapObject(book, document, info.bookMapping[i]);
            }
        }
        return book;
    },

    parseChapterListVolumeInfo: function(xp){
        if(!xp) return [];
        var vols = [], arr = XPath.array(null,xp);        
        for(var i=0;i<arr.length;i++){
            var n = arr[i], obj = {};
            obj.name = n.textContent;
            // n can be a text node, which has no style and tag
            if(!n.tagName) {
                n = n.parentNode;
            }
            obj.xy = (new Ext.Element(n)).getXY();
            vols.push(obj);
        }
        return vols;    
    },

    parseChapterEntry: function(node, mapping, volInfo){    
        var chapter = {};
        for(var i=0;i<mapping.length;i++){
            var m = mapping[i];
            switch(m.op){
            case 'provided.node.textcontent':            
                chapter[m.name] = node.textContent;
                break;
            case 'provided.node.property.regex':
                var v = node[m.param1];
                if(m.param2){
                    chapter[m.name] = HandlerHelper.getRegGroup(v, m.param2);
                }else{
                    chapter[m.name] = v;
                }
                break;
            case 'assign.value':
                chapter[m.name] = m.param1;
                break;
            default:
                Crawler.error('wrong op'+m.op);
            }        
        }
        var xy = (new Ext.Element(node)).getXY();
        for(var i=0;i<volInfo.length;i++){
            var v = volInfo[i];
            if(xy[1]>v.xy[1]){
                chapter.volume=v.name;
            }
        }
        return chapter;
    },
    
    postBookChapters: function(book){
        HandlerHelper.postBookCover(book, {action:'Goto.Next.Link'});
    },

    getParams: function(obj){
        var r = [];
        for(var i=1;i<10;i++){
            if(obj['param'+i]){
                r.push(obj['param'+i]);
            }else{
                return r;
            }
        }
        return r;
    },
    extractFromXpathNodeText: function(xp, reg){
        var r = XPath.single(null, xp).textContent;        
        if(typeof reg == 'object'){
            r =  HandlerHelper.getRegGroup(r, reg);
        }
        return r;
    },
    getRegGroup: function(s, r){
        if(typeof s != 'string' || typeof r == 'undefined'){
            Crawler.log('HandlerHelper: getRegGroup:Can not match:'+s+':'+r);
        }
        if(typeof r == 'string'){
            r = new RegExp(r,'i');
        }
        var arr = s.match(r);        
        if(arr && arr.length>1)
            return arr[1];
        else{
            //Crawler.log('HandlerHelper: getRegGroup:Can not match:'+s+':'+r);
            //return 'Can not match:'+s+':'+r;
            return '';
        }
    },
    
    mapObject: function (obj, node, mapping){
        if(node) node = document.documentElement;
        var processed = false;
        switch(mapping.op){
        case 'xpath.node.textcontent':
            var n = XPath.single(node, mapping.param1);
            if(n){
                obj[mapping.name] = n.textContent;
                processed = true;
            }
            break;
        case 'xpath.node.textcontent.regex.group':
            var n = XPath.single(node, mapping.param1);
            if(n){
                n = n.textContent;
                obj[mapping.name] = HandlerHelper.getRegGroup(n, mapping.param2);            
                processed = true;
            }
            break;
        }    
        if(!processed){
            Crawler.log('mapObject error:'+arguments);
        }
    }    
    
}