Crawler = {
	version : CrGlobal.version,
	serverUrl : CrGlobal.serverUrl,
	handlerPath : CrGlobal.handlerPath,
	extFile : CrGlobal.extFile,
	doAction : CrGlobal.doAction,	
	loadJSFile : CrGlobal.loadJSFile,
	
	clog : function(txt){
	    if(window.console) window.console.log(txt);
	},
	log : function(txt){
	    Crawler.clog(txt);
	},
	error: function(txt){
	    Crawler.clog('Error: '+txt);	    	    
	    //alert(txt);
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
	
	/**
	 * perform an action based on specificed command and parameters.
	 * obj is the command object like such:
	 * {
	 *     action: specify the action to do
	 *     param1: first parameter if the action needs to take any parameters
	 *     param2: seocond..
	 *     param3: ... etc
	 *     
	 * }
	 */
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
        var url = window.location.toString(), m = CrGlobal.handlerMapping;
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