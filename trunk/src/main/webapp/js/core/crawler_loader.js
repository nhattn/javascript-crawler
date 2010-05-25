CrGlobal = {
	debug : true,
	version : '0.1',
	RemoteLogging : true,
	doAction : true,
	handlerPath : '/js/handler',
	NextLinkWaitTime : 20 * 1000,
	ParameterName_ObjectId : 'objectid',
	HouseObjectId : 'House',
	/**
	 * how long before now should we go for grabbing house list
	 */
	HouseListMaxDifference : {
		'ganji.com' : 12 * 3600 * 1000,
		'koubei.com' : 12 * 3600 * 1000
	},

	extFile : 'http://ajax.googleapis.com/ajax/libs/ext-core/3.0.0/ext-core.js',
	googlemapFile : 'http://maps.google.com/maps/api/js?sensor=false',
	setup : function() {
		var hostDiv = document.getElementById('crawler_set_url');
		if (!hostDiv) {
			alert('error, no host div');
			return;
		}

		CrGlobal.serverUrl = 'http://' + hostDiv.value;
		CrGlobal.StoreLinkUrl = CrGlobal.serverUrl + '/service/link';
		CrGlobal.ObjectCreationUrl = CrGlobal.serverUrl + '/service/object';
		CrGlobal.RemoteLoggingUrl = CrGlobal.serverUrl + '/service/log';
		CrGlobal.initJsUrls();
		CrGlobal.chainLoad(CrGlobal.jsToLoad);
	},

	loadJSFile : function(fileurl, callback) {
		var sf = document.createElement('script');
		sf.setAttribute("type", "text/javascript");
		sf.setAttribute("src", fileurl);
		if (callback)
			sf.onload = callback;
		document.getElementsByTagName("head")[0].appendChild(sf);
	},

	chainLoad : function(files) {
		CrGlobal._chainFiles = files;
		CrGlobal._chainCurrent = 0;
		CrGlobal._chainLoadFile();
	},

	_chainLoadFile : function() {
	    
		if (!CrGlobal || !CrGlobal._chainFiles || !CrGlobal._chainFiles.length || CrGlobal._chainCurrent >= CrGlobal._chainFiles.length) {
			delete CrGlobal._chainFiles;
			delete CrGlobal._chainCurrent;
			return;
		}
		CrGlobal.loadJSFile(CrGlobal._chainFiles[CrGlobal._chainCurrent++],
				function() {
					CrGlobal._chainLoadFile();
				});
	},

	initJsUrls : function() {
	    var stamp = '?s='+(new Date()).getTime() + 's';
	    
		CrGlobal.jsToLoad = [ CrGlobal.extFile,
				CrGlobal.serverUrl + '/js/core/crawler.js'+stamp,
				CrGlobal.serverUrl + '/service/file/crawlerconfig'+stamp,
				CrGlobal.serverUrl + '/js/core/handler_helper.js'+stamp,
				CrGlobal.serverUrl + '/js/core/xpath.js'+stamp,
				CrGlobal.serverUrl + '/js/core/util.js'+stamp,
				CrGlobal.serverUrl + '/js/mapping/house.js'+stamp,
				CrGlobal.serverUrl + '/js/core/kickoff.js'+stamp ];
	}
}

CrGlobal.setup();
