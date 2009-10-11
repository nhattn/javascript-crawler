Crawler = {
	version:'0.1',
	serverUrl : 'http://localhost:8080/crawler',
	handlerPath : '/js/handler',
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
	        success: function(r){callback(r,true); },
	        failure: function(r){callback(r,false);},
	        method: 'POST',
	        params: params        
	     });        
	},
	action : function(obj){
	    if(!obj || !obj.action) {
	        Crawler.clog('Error, no action specified');
	    }
	    
	    var act = obj.action;
	    if(act == 'Eval.XPath.Link'){
	        var link = XPath.single(null, obj.para1, XPathResult.STRING_TYPE);
	        if(!link){
	            Crawler.clog("Error, can not locate link for XPath: "+obj.para);
	        }else{  
	            eval(link.stringValue);
	        }       	        
	    }else if(act == 'Goto.Next.Link'){
	        // request a new link then go to that link
	        var url = Crawler.serverUrl + '/service/crawler/link?action=redirect';
	        window.location = url;  	        
	    }else if(act == 'No.Action'){
	        Crawler.clog('No action got from server.');
	    }else if(act == 'Goto.XPath.Link'){
            var link = XPath.single(null, obj.para1, XPathResult.STRING_TYPE);
            if(!link){
                Crawler.clog("Error, can not locate link for XPath: "+obj.para);
            }else{  
                window.location = link.stringValue;
            }  	        
	    }
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
	            alert(e);
	        }
	        //Crawler.clog(nextAction);
	        if(nextAction){
	            Crawler.action(nextAction);
	        }else{
	            // nothing to do, keep loop, TODO:
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
	    if(typeof defaultValue == undefined){
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
            alert(e);
        }        
        return r.trim();
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
		var nodes = XPath.iterator(node,path);
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
		var r = document.evaluate(path, node, null, type , null);
		
		if(r){
		    if(type == XPathResult.FIRST_ORDERED_NODE_TYPE && r.singleNodeValue) {
		        return r.singleNodeValue;
		    }else if(type == XPathResult.STRING_TYPE){
		        return r;
		    }
		}
		throw 'Invalid xpath:'+path;		
	}, 
	stringv : function(node,path){
	    return XPath.single(node, path, XPathResult.STRING_TYPE).stringValue;
	}
}

var extfile = 'http://ajax.googleapis.com/ajax/libs/ext-core/3.0.0/ext-core.js';

Crawler.loadJSFile(extfile, function(){loadHandler();});

var handlerMapping = [
{pattern:'http://[^\.]*\.qidian\.com/book/bookStore\.aspx', file:'qidian.booklist'},
{pattern:'http://www\.qidian\.com/Book/[^\.]*\.aspx', file:'qidian.bookcover'},
{pattern:'http://www\.qidian\.com/BookReader/[0-9]*\.aspx', file:'qidian.chapterlist'},
{pattern:'http://www\.qidian\.com/BookReader/[0-9]*,[0-9]*\.aspx', file:'qidian.chapter'}
];

function locateHandler(){
    var url = window.location.toString(), m = handlerMapping;
    for(var i=0;i<m.length;i++){
        var reg = new RegExp(m[i].pattern, 'i');
        if(reg.test(url) == true){            
            return m[i].file;
        }
    }
    return 'nomatch';
}

function loadHandler(){
    // find out which web site, based on mapping file, locate the js files.
    Ext.lib.Ajax.useDefaultXhrHeader=false;
    var file = Crawler.serverUrl+Crawler.handlerPath+'/'+locateHandler()+'.js';
    Crawler.loadJSFile(file, function(){try{handlerProcess();}catch(e){alert(e);}})
}
