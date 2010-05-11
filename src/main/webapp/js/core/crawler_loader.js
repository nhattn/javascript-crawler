CrGlobal = {
    version : '0.1',
    serverUrl : 'http://localhost:8080/crawler',    
 
    /**
     * whether to do remote logging or not
     */
//    RemoteLogging : true,
     RemoteLogging: false,    

    extFile : 'http://ajax.googleapis.com/ajax/libs/ext-core/3.0.0/ext-core.js',
    handlerPath : '/js/handler',

//    doAction : true,
     doAction : false,
    NextLinkWaitTime : 3 * 1000,
    ParameterName_ObjectId : 'objectid',
    HouseObjectId : 'House',
    HouseListMaxDifference: 3*3600,//5 * 24 * 3600 * 1000,
    
    
    setup : function() {
        CrGlobal.StoreLinkUrl = CrGlobal.serverUrl + '/service/link';
        CrGlobal.ObjectCreationUrl = CrGlobal.serverUrl + '/service/object';
        CrGlobal.RemoteLoggingUrl = CrGlobal.serverUrl + '/service/log';
        CrGlobal.chainLoad(jsToLoad);
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
        CrGlobal.loadJSFile(CrGlobal._chainFiles[CrGlobal._chainCurrent++], function() {
            CrGlobal._chainLoadFile();
        });
    }
}

var jsToLoad = [ CrGlobal.extFile, CrGlobal.serverUrl + '/js/core/crawler.js',
        CrGlobal.serverUrl + '/js/core/handler_helper.js', CrGlobal.serverUrl + '/js/core/xpath.js',
        CrGlobal.serverUrl + '/js/core/util.js', CrGlobal.serverUrl + '/js/mapping/house.js',
        CrGlobal.serverUrl + '/js/core/kickoff.js' ];

CrGlobal.setup();
