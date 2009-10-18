Crawler = {
	version:'0.1',
	serverUrl : 'http://localhost:8080/crawler',
	handlerPath : '/js/handler',
	extFile : 'http://ajax.googleapis.com/ajax/libs/ext-core/3.0.0/ext-core.js',
	doAction : true,
	doAction : false,
	loadJSFile : function(fileurl, callback){
  		var sf=document.createElement('script');
  		sf.setAttribute("type","text/javascript");
  		sf.setAttribute("src", fileurl);
  		if(callback) sf.onload = callback;  
  		document.getElementsByTagName("head")[0].appendChild(sf);  			
	},
	clog : function(txt){
	    if(window.console) window.console.log(txt);
	},
	log : function(txt){
	    Crawler.clog(txt);
	},
	error: function(txt){
	    Crawler.clog('Error: '+txt);	    	    
	    alert(txt);
	},
	objToString : function(obj){
	    var r = [];
	    for(var i in obj){
	        r.push(i);
	        r.push('=');
	        r.push(obj[i]);
	        r.push(', ');
	    }
	    return r.join('');
	},
	
	postData : function(params, url, callback){
	    if(!callback) callback = Crawler.callback       
	    Ext.Ajax.request({
	        url: url,
	        success: function(r){try{callback(r,true);}catch(e){Crawler.error('crawler_loader.postdata:'+e);} },
	        failure: function(r){try{callback(r,false);}catch(e){Crawler.error('crawler_loader.postdata:'+e);} },
	        method: 'POST',
	        params: params        
	     });        
	},	
	action : function(obj){
	    if(!Crawler.doAction) return;
	    var processed = false;
	    if(!obj || !obj.action) {
	        Crawler.clog('Error, no action specified');	        
	    }	    	    
	    switch(obj.action){
    	    case 'Eval.XPath.Link.Href' :{
    	        // xpath should point to the A node, will take out the href
    	        var link = XPath.single(null, obj.param1);
    	        if(!link){
    	            Crawler.error("Error, can not locate link for XPath: "+obj.param1);    	            
    	        }else{  
    	           try{
    	               eval(link.href);
    	               processed = true;
    	           }catch(e){
    	               Crawler.error('Error, can not eval xpath link:'+link+':'+e);    	                   	               
    	           }
    	        }
    	        break;
    	    }
    	    case 'Goto.XPath.Link.Href' : {
                // xpath should point to the A node, will take out the href    	        
                var link = XPath.single(null, obj.param1);
                if(!link){
                    Crawler.clog("Error, can not locate link for XPath: "+obj.param1);                    
                }else{  
                    window.location = link.href;
                    processed = true;
                }      
                break;
            }    	    
    	    case 'Goto.Next.Link': {
    	        // request a new link then go to that link
    	        var url = Crawler.serverUrl + '/service/link?action=redirect';    	        
    	        window.location = url;
    	        // must return here, or there will be loops.
    	        return;
    	    }
    	    case 'No.Action' : {    	        
    	        break;
    	    }    	   
	    }//end switch
	    if(!processed){
	        Crawler.log("Crawler.action: Goto next link by default.");
	        Crawler.nextLink();
	    }
	},
	nextLink : function(){
	   Crawler.action({action:'Goto.Next.Link'});
	},
	callback : function(r, suc){
	    if(!suc){
	        Crawler.clog("failed");            
	    }else{            
	        //Crawler.clog(r.responseText);
	        var nextAction = null;
	        try{
	            nextAction = Ext.util.JSON.decode(r.responseText);
	        }catch(e){
	            Crawler.error("Crawler callback, can not execute next action:");
	            Crawler.error(r.responseText);
	        }	        
	        if(nextAction){
	            Crawler.action(nextAction);
	        }else{
	            Crawler.nextLink();
	        }
	    }        
	},	
		
	killSpace : function(s){
	    return s.replace(/\s+/g, ' ');
	},
	
	/**
	 *  s is something like "小说类别：虚拟网游 总点击：4736 总推荐：419 总字数：228132 更新：2009年10月10日".
	 *  extract the value for keys like "小说类别" or "总点击".
	 */
	extract : function(s, key, defaultValue){
	    var start, end;
	    if(typeof defaultValue == 'undefined'){
	        defaultValue = '';
	    }
	    s = Crawler.killSpace(s);
	    start = s.indexOf(key);
	    if(start<0){
	        return defaultValue;
	    }
	    
	    start = start + key.length;
	    var space =  /^\s$/;
	    while(start<s.length && space.test(s.charAt(start++))){
	        // skip all the spaces
	    }	    
	    start--;
	    end = start+1;
        var nspace =  /^\S$/;
        while(end<s.length && nspace.test(s.charAt(end++))){
            // skip all the none spaces
        }   	    
        var r = '';
        try{
            r = s.substring(start,end);
        }catch(e){
            Crawler.error("crawler_loader.extract:"+e+":"+e);
        }        
        return r.trim();
	},			
    locateHandler : function(){
        var url = window.location.toString(), m = handlerMapping;
        for(var i=0;i<m.length;i++){
            var reg = new RegExp(m[i].pattern, 'i');
            if(reg.test(url) == true){            
                return m[i].file;
            }
        }
        return 'nomatch';
    },
    loadHandler: function(){
        // find out which web site, based on mapping file, locate the js files.
        Ext.lib.Ajax.useDefaultXhrHeader=false;
        var file = Crawler.serverUrl+Crawler.handlerPath+'/'+ Crawler.locateHandler() +'.js?' + (new Date()).getTime() ;
        Crawler.loadJSFile(file, function(){
            try{
                if(typeof handlerPreprocess != 'undefined'){                    
                    handlerPreprocess();
                }
            }catch(e){
                Crawler.error(e);
            }
            try{
                handlerProcess();
            }catch(e){
                Crawler.error(e);
            }
        });
    }	
}

XPath = {
	version : '0.1',	
	iterator : function(node, path){
		if(!node) node = document.documentElement;
		return document.evaluate(path, node, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
	},	
	array : function(node,path){
		var r = [];
		var nodes = null;
		try{
		  nodes = XPath.iterator(node,path);
		}catch(e){		
            Crawler.error('Wrong xpath:'+e+':'+path);
            return null;        
		}
		if(nodes){
			var n = nodes.iterateNext();
			while(n){
				r.push(n);
				n = nodes.iterateNext();
			}
		}
		return r;
	},	
	single : function(node, path, type){
	    if(typeof type == 'undefined') {
	        type = XPathResult.FIRST_ORDERED_NODE_TYPE;
	    }
		if(!node) {
		    node = document.documentElement;
		}
		var r = null;
		try{
		  r = document.evaluate(path, node, null, type , null);
		}catch(e){
	        Crawler.error('Wrong xpath:'+e+':'+path);
		    return null;
		}
		if(r){
		    if(type == XPathResult.FIRST_ORDERED_NODE_TYPE && r.singleNodeValue) {
		        return r.singleNodeValue;
		    }else if(type == XPathResult.STRING_TYPE){
		        return r;
		    }
		}
		Crawler.error('Wrong xpath:'+path);
        return null;		
	}, 
	
	stringv : function(node,path){
	    return XPath.single(node, path, XPathResult.STRING_TYPE).stringValue;
	}
}

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

Crawler.loadJSFile(Crawler.extFile, function(){Crawler.loadHandler();});

var handlerMapping = [
    // qidian.com                      
    {pattern:'http://[^\.]+\.qidian\.com/book/bookStore\.aspx',         file:'qidian/booklist'},
    {pattern:'http://www\.qidian\.com/Book/[^\.]*\.aspx',               file:'qidian/bookcover'},
    {pattern:'http://www\.qidian\.com/BookReader/[0-9]*\.aspx',         file:'qidian/chapterlist'},
    {pattern:'http://www\.qidian\.com/BookReader/[0-9]*,[0-9]*\.aspx',  file:'qidian/chapter'},

    //tszw.com
    {pattern:'http://www\.tszw\.com/toplistlastupdate/[0-9]+/[0-9]+.html',         file:'tszw/booklist'},
    {pattern:'http://www\.tszw\.com/Article_[0-9]+\.html',                         file:'tszw/bookcover'},
    {pattern:'http://www\.tszw\.com/[0-9]+/[0-9]+[/index\.html]?',                 file:'tszw/chapterlist'},
        
    //17k.com    
    {pattern:'http://all\.17k\.com/[0-9|_]+\.html',         file:'www17k/booklist'},
    {pattern:'http://[^\.]+\.17k\.com/book/[0-9]+\.html',    file:'www17k/bookcover'},
    {pattern:'http://[^\.]+\.17k\.com/list/[0-9]+\.html',        file:'www17k/chapterlist'},
    
    //zhulang.com    
    {pattern:'http://s\.zhulang\.com/w_book_list\.php',        file:'zhulang/booklist'},
    {pattern:'http://www\.zhulang\.com/[0-9]+/index\.html',   file:'zhulang/bookcover'},
    {pattern:'http://book\.zhulang.\com/[0-9]+/index\.html',   file:'zhulang/chapterlist'},
    
    //readnovel.com
    {pattern:'http://www\.readnovel\.com/all\.html',                 file:'readnovel/alllist'},
    {pattern:'http://www\.readnovel\.com/archive/[0-9]+/[0-9]+',     file:'readnovel/monthlist'},
    {pattern:'http://www\.readnovel\.com/partlist/[0-9]+',           file:'readnovel/bookcover'}
    
];
    