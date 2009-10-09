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
	    if(!callback) callback = function(){}	        
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
	        var link = XPath.single(null, obj.para, XPathResult.STRING_TYPE);
	        if(!link){
	            Crawler.clog("Error, can not locate link for XPath: "+obj.para);
	        }else{  
	            eval(link.stringValue);
	        }       	        
	    }else if(act == "Goto.Next.Link"){
	        // request a new link then go to that link
	        var url = Crawler.serverUrl + '/link?action=redirect';
	        window.location = url;  	        
	    }
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
	}
}

var extfile = 'http://ajax.googleapis.com/ajax/libs/ext-core/3.0.0/ext-core.js';

Crawler.loadJSFile(extfile, function(){loadHandler();});

var handlerMapping = [
{pattern:'http://[^\.]*\.qidian\.com/book/bookStore\.aspx', file:'qidian.booklist'},
{pattern:'http://www\.qidian\.com/Book/[^\.]*\.aspx', file:'qidian.bookcover'}

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
    Crawler.loadJSFile(file, function(){handlerProcess();})
}
