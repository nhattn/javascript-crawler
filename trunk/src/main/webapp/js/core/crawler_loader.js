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
		if (CrGlobal._chainCurrent >= CrGlobal._chainFiles.length) {
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
		CrGlobal.jsToLoad = [ CrGlobal.extFile,
				CrGlobal.serverUrl + '/js/core/crawler.js',
				CrGlobal.serverUrl + '/service/file/crawlerconfig',
				CrGlobal.serverUrl + '/js/core/handler_helper.js',
				CrGlobal.serverUrl + '/js/core/xpath.js',
				CrGlobal.serverUrl + '/js/core/util.js',
				CrGlobal.serverUrl + '/js/mapping/house.js',
				CrGlobal.serverUrl + '/js/core/kickoff.js' ];
	}
}

CrGlobal.setup();
