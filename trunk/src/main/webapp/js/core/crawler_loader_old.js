/**
CrConfig.RemoteLogging = false;
CrConfig.doAction = false;
 **/
CrConfig.NextLinkWaitTime = 5000000;
CrGlobal = {
    handlerPath : '/js/handler',
    restartInterval : 50,
    setup : function() {
        var hostDiv = document.getElementById('crawler_set_url');
        if (!hostDiv) {
            throw 'no host div, crawler not set up propertly';
        }
        for ( var c in CrConfig) {
            CrGlobal[c] = CrConfig[c];
        }
        delete CrConfig;
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
        CrGlobal.loadJSFile(CrGlobal._chainFiles[CrGlobal._chainCurrent++], function() {
            CrGlobal._chainLoadFile();
        });
    },
    initJsUrls : function() {
        var verstring = '?v=' + CrGlobal.Version;
        var jsToLoad = [];
        if (CrGlobal.jsToLoad) {
            for ( var i = 0; i < CrGlobal.jsToLoad.length; i++) {
                var js = CrGlobal.jsToLoad[i];
                if (js.indexOf(':/') > 0) {
                    jsToLoad.push(js);
                } else {
                    jsToLoad.push(CrGlobal.serverUrl + js + verstring);
                }
            }
            delete CrGlobal.jsToLoad;
        }
        CrGlobal.jsToLoad = jsToLoad;
    }
}
CrGlobal.setup();
