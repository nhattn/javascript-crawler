Crawler = {
    serverUrl : CrGlobal.serverUrl,
    handlerPath : CrGlobal.handlerPath,
    extFile : CrGlobal.extFile,
    doAction : CrGlobal.doAction,
    loadJSFile : CrGlobal.loadJSFile,

    clog : function(txt) {
        if (CrGlobal.RemoteLogging) {
            /*Crawler.remoteLog('Clog', txt);*/
        } else {
            if (window.console)
                window.console.log(txt);
        }
    },

    log : function(txt) {
        if (CrGlobal.RemoteLogging) {
            /*Crawler.remoteLog('Log', txt);*/
        } else {
            Crawler.clog(txt);
        }
    },

    warn : function(txt) {
        if (CrGlobal.RemoteLogging) {
            Crawler.remoteLog('Warn', txt);
        } else {
            Crawler.clog(txt);
        }
    },

    error : function(txt) {
        CrGlobal.doAction = false;
        if (CrGlobal.RemoteLogging) {
            Crawler.remoteLog('Error', txt);
        } else {
            Crawler.clog('Error: ' + txt);
        }
    },

    attention : function(txt) {
        CrGlobal.doAction = false;
        if (CrGlobal.RemoteLogging) {
            Crawler.remoteLog('Attention', txt);
        } else {
            Crawler.clog('Attention: ' + txt);
        }
    },

    remoteLog : function(level, msg) {
        CrUtil.getRequest(CrGlobal.RemoteLoggingUrl + '?level=' + level + '&msg=' + msg);
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
    action : function(obj) {
        if (!Crawler.doAction)
            return;
        var processed = false;
        if (!obj || !obj.action) {
            Crawler.clog('Error, no action specified');
        }
        switch (obj.action) {
        case 'Eval.XPath.Link.Href': {
            /* xpath should point to the A node, will take out the href*/
            var link = XPath.single(null, obj.param1);
            if (!link || !link.href) {
                Crawler.error("Error, can not locate link for XPath1: " + obj.param1);
            } else {
                setTimeout(function() {
                    try {
                        eval(link.href);
                    } catch (e) {
                        Crawler.error('Error, can not eval xpath link:' + link + ':' + e);
                    }
                }, CrGlobal.NextLinkWaitTime);
                processed = true;
            }
            break;
        }
        case 'Goto.XPath.Link.Href': {
            /* xpath should point to the A node, will take out the href*/
            var link = XPath.single(null, obj.param1);
            if (!link || !link.href) {
                Crawler.error("Error, can not locate link for XPath2: " + obj.param1);
            } else {
                setTimeout(function() {
                    Crawler.gotoLink(link.href);
                }, CrGlobal.NextLinkWaitTime);
                processed = true;
            }
            break;
        }
        case 'Goto.Next.Link': {
            var pc = document.getElementById('crawler_page_counter');
            if (pc && (parseInt(pc.value) % CrGlobal.restartInterval == 0)) {
                setTimeout(function() {
                    CrUtil.restartBrowser(Crawler.serverUrl + '/service/link?action=redirect');
                }, CrGlobal.NextLinkWaitTime);
            } else {
                var nextLink = CrUtil.getRequest(Crawler.serverUrl + '/service/link?action=get');
                if (nextLink) {
                    try {
                        nextLink = JSON.parse(nextLink).result;
                    } catch (e) {
                        Crawler.error('Server returned wrong response for next link ' + nextLink);
                        nextLink = Crawler.serverUrl + '/service/link?action=redirect';
                    }
                } else {
                    nextLink = Crawler.serverUrl + '/service/link?action=redirect';
                }
                setTimeout(function() {
                    Crawler.gotoLink(nextLink);
                }, CrGlobal.NextLinkWaitTime);
            }
            /* must return here, or there will be loops. */
            return;
        }
        case 'Click.XPath.Node': {
            var node = XPath.single(null, obj.param1);
            if (node) {
                setTimeout(function() {
                    CrUtil.clickNode(node);
                }, CrGlobal.NextLinkWaitTime);

                processed = true;
            }
            break;
        }
        case 'Run.Function': {
            try {
                obj.param1();
                processed = true;
            } catch (e) {
                Crawler.log('Error while runing function: ' + e);
            }
            break;
        }
        case 'No.Action': {
            break;
        }
        }
        if (!processed) {
            Crawler.log("Crawler.action: Goto next link by default.");
            Crawler.nextLink();
        }
    },

    nextLink : function() {
        Crawler.action( {
            action : 'Goto.Next.Link'
        });
    },

    gotoLink : function(link) {
        var oldlink = window.location.toString();
        var newlink = link;
        window.location = link;
        setInterval(function() {
            if (window.location == oldlink) {
                window.location = newlink;
            }
        }, 3000)
    },

    callback : function(r, suc) {
        if (!suc) {
            Crawler.clog("failed");
        } else {
            var nextAction = null;
            try {
                nextAction = Ext.util.JSON.decode(r.responseText);
            } catch (e) {
                Crawler.error("Crawler callback, can not execute next action:");
                Crawler.error(r.responseText);
            }
            if (nextAction) {
                Crawler.action(nextAction);
            } else {
                Crawler.nextLink();
            }
        }
    },

    locateHandler : function() {
        var url = window.location.toString(), m = CrGlobal.handlerMapping;
        for ( var i = 0; i < m.length; i++) {
            var reg = new RegExp(m[i].pattern, 'i');
            if (reg.test(url) == true) {
                return m[i].file;
            }
        }
        return 'nomatch';
    },

    loadHandler : function() {
        /* find out which web site, based on mapping file, locate the js files.*/
        Ext.lib.Ajax.useDefaultXhrHeader = false;
        var file = Crawler.serverUrl + Crawler.handlerPath + '/' + Crawler.locateHandler() + '.js?v=' + CrGlobal.Version;
        Crawler.loadJSFile(file, function() {
            try {
                if (typeof handlerPreprocess != 'undefined') {
                    handlerPreprocess();
                }
            } catch (e) {
                Crawler.error(e);
            }
            try {
                handlerProcess();
            } catch (e) {
                Crawler.error(e);
            }
        });
    },

    /**
     * TODO:     ************************ this is not finished yet*****************
     */
    geocode : function(address, callback) {
        CrGlobal.loadJSFile(CrGlobal.googlemapFile, function() {
            Crawler._translateAddress(address, callback);
        });
    },

    _translateAddress : function(address, callback) {
        var geocoder = new google.maps.Geocoder();
        if (!geocoder)
            CrGlobal._translateAddress.defer(500);
        geocoder.geocode( {
            'address' : address,
            'language' : 'zh'
        }, function(results, status) {
            if (status == google.maps.GeocoderStatus.OK) {
                var r = results[0];
                var lng = r.geometry.location.lng();
                var lat = r.geometry.location.lat();
                if (callback) {
                    callback('ok', lng, lat);
                }
            } else {
                if (callback) {
                    callback('failed-status-' + status);
                }
            }
        });
    },

    _geocallback : function(a, b, c) {
        if (a == 'ok') {
            console.log(a + '  ' + b + '  ' + c);
        } else {
        }
    }

}
