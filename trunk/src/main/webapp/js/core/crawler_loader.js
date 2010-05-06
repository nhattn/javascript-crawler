CrGlobal = {
	version : '0.1',
	serverUrl : 'http://localhost:8080/crawler',
	/* this is relative to the current context */
	handlerPath : '/js/handler',
	extFile : 'http://ajax.googleapis.com/ajax/libs/ext-core/3.0.0/ext-core.js',	
	doAction : true,
	doAction : false,
	
	loadJSFile : function(fileurl, callback) {
		var sf = document.createElement('script');
		sf.setAttribute("type", "text/javascript");
		sf.setAttribute("src", fileurl);
		if (callback)
			sf.onload = callback;
		document.getElementsByTagName("head")[0].appendChild(sf);
	},
	
	chainLoad : function(files){
		CrGlobal._chainFiles = files;
		CrGlobal._chainCurrent = 0;	
		CrGlobal._chainLoadFile();
	},
	
	_chainLoadFile: function(){
		if(CrGlobal._chainCurrent >= CrGlobal._chainFiles.length){
			delete CrGlobal._chainFiles;
			delete CrGlobal._chainCurrent;			
			return;
		}
		CrGlobal.loadJSFile(CrGlobal._chainFiles[CrGlobal._chainCurrent++], function(){
			CrGlobal._chainLoadFile();
		});
	}
}

var jsToLoad = [
    CrGlobal.extFile,                
    CrGlobal.serverUrl + '/js/core/crawler.js',
    CrGlobal.serverUrl + '/js/core/handler_helper.js',
    CrGlobal.serverUrl + '/js/core/xpath.js',
    CrGlobal.serverUrl + '/js/core/util.js',
    CrGlobal.serverUrl + '/js/mapping/housing.js',
    CrGlobal.serverUrl + '/js/core/kickoff.js' 
];

CrGlobal.chainLoad(jsToLoad);
