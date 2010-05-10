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
	    CrGlobal.doAction = false;
	    alert(txt);
	},	
	
	/**
	 * perform an action based on specificed command and parameters.
	 * obj is the command object like such:
	 * {
	 *     action: specify the action to do
	 *     param1: first parameter if the action needs to take any parameters
	 *     param2: seocond..
	 *     param3: ... etc
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
    	    	alert(111);
    	        // xpath should point to the A node, will take out the href
    	        var link = XPath.single(null, obj.param1);
    	        if(!link || !link.href){
    	            Crawler.error("Error, can not locate link for XPath: "+obj.param1);    	            
    	        }else{  
    	           try{    	        	
    	        	   alert(link.href);
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
    	        setTimeout(function(){
    	        	window.location = url;
    	        }, CrGlobal.NextLinkWaitTime);
    	        
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