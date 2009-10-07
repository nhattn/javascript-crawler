Crawler = {
	version:'0.1',
	loadJSFile : function(fileurl, callback){
  		var sf=document.createElement('script');
  		sf.setAttribute("type","text/javascript");
  		sf.setAttribute("src", fileurl);
  		if(callback) sf.onload = callback;  
  		document.getElementsByTagName("head")[0].appendChild(sf);  			
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
	single : function(node, path){
		if(!node) node = document.documentElement;
		var r = document.evaluate(path, node, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null);
		if(r && r.singleNodeValue) {
			return r.singleNodeValue;
		}
		throw 'Invalid xpath:'+path;
	}
}

//var extfile = 'http://ajax.googleapis.com/ajax/libs/ext-core/3.0.0/ext-core.js';
var extfile = 'http://localhost:8080/crawler/js/ext-core-debug.js';

CWR = Crawler;
CWR.loadJSFile(extfile, function(){scriptloaded();});

function clog(txt){
	if(window.console) window.console.log(txt);
}

var metaInfo = {
    dataUrl : 'http://localhost:8080/crawler/service/crawler/booklist',
	path : "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[3]/div[1]/div",
	start : 3,
	stop : -1,
	mapping : [
		{path:'div[2]/a[1]', attr:'textContent', name:'book.cat1'},
		{path:'div[2]/a[2]', attr:'textContent', name:'book.cat2'},
		{path:'div[3]/span/a', attr:'textContent', name:'book.name'},
		{path:'div[3]/span/a', attr:'href', name:'book.allChapterLink'},
		{path:'div[3]/a', attr:'textContent', name:'chapter.link'},
		{path:'div[4]', attr:'textContent', name:'book.totalChar'},
		{path:'div[5]/a', attr:'textContent', name:'book.author'},
		{path:'div[6]', attr:'textContent', name:'book.updateTime'}						
	],
	nextAction : function(){
		var link = XPath.single(null, '//a[text()="ÏÂÒ»Ò³"]');		
		if(!link){
			return;
		}else{	
			eval(link.href);
		}		
	}
}

function scriptloaded(){	
    Ext.Ajax.request({
        url: "http://localhost:8080/crawler/service/crawler/booklist",
        success: function(){alert(1);},
        failure: function(){alert(2);},
        method: 'GET'        
     });    
    
    return;
    
    // return;
	var info = metaInfo;	
	var result = XPath.array(document.documentElement, metaInfo.path)			
	if (result){
		var start = (info.start>0)?info.start:0;
		var stop =0;
		if(info.stop>0){
			stop = info.stop
		}else if(info.stop == 0){
			stop = result.length;
		}else if(info.stop <0){
			stop = result.length + info.stop;
		}
		var books = [];		
	    for(var i=start;i<stop;i++){
	        var node = result[i];
	        var entry = parseMappedNode(node,info.mapping);
	        books.push(entry);
	        clog(objToString(entry));
	    }
	    postData(books, info.dataUrl);
	}
	return;
	if(info.nextAction){		
		info.nextAction();
	}
}

function postData(data, url){
    data = JSON.stringify(data);
    Ext.Ajax.request({
        url: url,
        success: function(){alert(1);},
        failure: function(){alert(2);},
        method: 'POST',
        params: { data: data }
     });
}

function parseMappedNode(node, mapping){
	var r = {};
	for(var i=0;i<mapping.length;i++){
		var m = mapping[i];
		var n = XPath.single(node, m.path);		
		r[m.name] = n[m.attr];		
	}
	return r;
}

function objToString(obj){
	var r = [];
	for(var i in obj){
		r.push(i);
		r.push('=');
		r.push(obj[i]);
		r.push(', ');
	}
	return r.join('');
}	