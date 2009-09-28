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

CWR = Crawler;
CWR.loadJSFile('http://ajax.googleapis.com/ajax/libs/ext-core/3.0.0/ext-core.js', function(){scriptloaded();});
function clog(txt){
	if(window.console) window.console.log(txt);
}



var metaInfo = {
	path : "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[3]/div[1]/div",
	start : 3,
	stop : -1,
	mapping : [
		{path:'div[2]/a[1]', attr:'novel:cat1'},
		{path:'div[2]/a[2]', attr:'novel:cat2'},
		{path:'div[3]/span/a', attr:'novel:name'},
		{path:'div[3]/a', attr:'chapter:fullname'},
		{path:'div[4]', attr:'novel:totalchar'},
		{path:'div[5]/a', attr:'novel:author'},
		{path:'div[6]', attr:'novel:updatetime'}						
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
	var info = metaInfo;	
	var result = XPath.array(document.documentElement, metaInfo.path)			
	if (result){
		var start = (info.start>0)?info.start:0;
		var stop =0;
		if(info.stop>0){
			stop = info.stop
		}else if(info.stop == 0){
			stop = result.length;
		}else{
			stop = result.length + info.stop;
		}
	    for(var i=start;i<stop;i++){
	        var node = result[i];
	        clog(objToString(parseMappedNode(node,info.mapping)));
	    }
	}
	if(info.nextAction){		
		info.nextAction();
	}
}

function parseMappedNode(node, mapping){
	var r = {};
	for(var i=0;i<mapping.length;i++){
		var m = mapping[i];
		var n = XPath.single(node, m.path);		
		r[m.attr] = n.textContent;		
	}
	return r;
}

function objToString(obj){
	var r = [];
	for(var i in obj){
		r.push(i);
		r.push(obj[i]);
		r.push(', ');
	}
	return r.join('');
}	