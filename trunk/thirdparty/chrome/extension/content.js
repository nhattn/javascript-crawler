var Content = {
    MaxWaitTime : 60 * 1000,
    CheckInterval : 500,
    /**
     * config{
     *     id: img id,
     *     //or
     *     dom: img dom
     * }
     * callback: function(r){
     *     r is the encoded string
     * }
     */
    encodeImage : function(config, callback) {
        var imgDom = null;
        if (config.id) {
            imgDom = document.getElementById(config.id);
        }
        if (!imgDom) {
            imgDom = config.dom;
        }
        if (!imgDom) {
            log('no dom object');
            return;
        }
        if (!imgDom.complete) {
            Content.encodeImage.defer(50, null, [ config, callback ]);
            return;
        }
        chrome.extension.sendRequest( {
            'action' : 'EncodeImage',
            'src' : imgDom.src,
            'width' : imgDom.width,
            'height' : imgDom.height
        }, callback);
    },

    /*
     * config is the extjs ajax request config, without callback.
     * callback: function(r){
     *  // r is the same as extjs callback
     * }
     */
    ajax : function(config, callback) {
        var request = {};
        Ext.apply(request, config);
        request.action = 'Ajax';
        chrome.extension.sendRequest(request, function(r) {
            if (callback) {
                callback(r);
            }
        });
    },
    storeValue : function(key, value) {
        chrome.extension.sendRequest( {
            action : 'StoreValue',
            key : key,
            value : value
        });
    },
    getValue : function(key, callback) {
        chrome.extension.sendRequest( {
            action : 'GetValue',
            key : key
        }, callback);
    },
    setup_1 : function() {
        try {
            if (window != window.top || !window.document || !window.document.body || !window.location) {
                Content.stopit();
                return;
            }
        } catch (ex) {
            Content.stopit();
            return;
        }
        Content.getValue('domain', Content.setup_2);
    },

    setup_2 : function(domain) {
        if (window.document.getElementById('crawler_set_url_reset')) {
            domain = window.location.host;
            Content.storeValue('domain', domain);
        }

        if (!domain)
            return;

        Content.domain = domain;
        if (window.location.host == domain)
            return;

        Content.createElement('input', window.document.body, {
            id : 'crawler_set_url',
            type : 'hidden',
            value : domain
        });

        Content.createElement('input', window.document.body, {
            id : 'crawler_messaging_proxy',
            type : 'hidden',
            value : ''
        });

        Content.setupClientProxy()

        var stamp = (new Date()).getTime() + 's';
        if (document.getElementsByTagName('head')) {
            Content.createElement('script', document.getElementsByTagName('head')[0], {
                src : 'http://' + domain + '/service/file/crawlerconfig?s=' + stamp
            });
        }
    },

    stopit : function() {
        try {
            window.stop();
            if (window.document && window.document.childNodes) {
                var nodes = window.document.childNodes;
                for ( var i = 0; i < nodes.length; i++) {
                    nodes[i].parentNode.removeChild(nodes[i]);
                }
            }
        } catch (e) {
        }
    },

    createElement : function(tagName, parentNode, attributes) {
        if (!parentNode || !tagName || !attributes)
            return;
        var hostEl = document.createElement(tagName);
        for ( var i in attributes) {
            hostEl.setAttribute(i, attributes[i]);
        }
        parentNode.appendChild(hostEl);
    },

    setupClientProxy : function() {
        var proxy = document.getElementById('crawler_messaging_proxy');
        proxy.addEventListener('cr_message_client', Content._serviceEventListener, true);
    },

    _serviceEventListener : function() {
        var proxy = document.getElementById('crawler_messaging_proxy');
        var value = proxy.value;
        value = JSON.parse(value);
        if (value.action == 'EncodeImage') {
            Content.encodeImage(value, Content._serviceCallback);
        } else if (value.action == 'Ajax') {
            Content.ajax(value, Content._serviceCallback)
        }
    },
    _serviceCallback : function(r) {
        var proxy = document.getElementById('crawler_messaging_proxy');
        var value = JSON.stringify(r);
        proxy.value = value;
        var customEvent = document.createEvent('Event');
        customEvent.initEvent('cr_message_server', true, true);
        proxy.dispatchEvent(customEvent);
    },
    startWatcher : function() {
        try {
            if (window != window.top || !window.document || !window.document.body || !window.location) {
                console.log('inframe');
                setInterval(function() {
                    if (Content.stopit)
                        Content.stopit();
                }, 50);
                Content.stopit();
                return;
            }
        } catch (ex) {
            Content.stopit();
            return;
        }
        var wait_time = Content.MaxWaitTime;
        Content.startTime = new Date().getTime();
        Content._interval = window.setInterval(function() {
            var waitedHowLong = (new Date()).getTime() - Content.startTime;
            if (waitedHowLong > wait_time) {
                window.clearInterval(Content._interval);
                try {
                    chrome.extension.sendRequest( {
                        'action' : 'GoHome'
                    });
                } catch (e) {
                    log('error 2' + e);
                }

                /*
                chrome.tabs.executeScript(null, {
                    code : "document.body.bgColor='red'"
                });
                window.location = 'http://' + Content.domain;
                */
            }
        }, Content.CheckInterval);
    }
}

function log(r) {
    console.log(r);
}
Ext.onReady(function() {
    Content.startWatcher();
    Content.setup_1();
});

/*
Content.encodeImage(document.getElementsByTagName('img')[0], function(c) {
    console.log(c)
});

Content.ajax('http://www.sina.com.cn', 'GET', function(opt, suc, response) {
    console.log(arguments);
    console.log(response.responseText);
})


  Content.storeValue('a', 'b');
  Content.getValue('a', function(c) {
    console.log(c);
})
*/
