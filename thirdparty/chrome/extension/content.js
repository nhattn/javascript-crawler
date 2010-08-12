var Content = {
    MaxWaitTime : 60 * 1000,
    WatcherInterval : 500,
    service : chrome.extension.sendRequest,
    /**
     * config{
     *     'action' : 'EncodeImage', 
     *     'id': src url          
     *     'width': image width, integer in pixel
     *     'height': image height, integer in pixel
     * }
     */

    /**
     * config is the extjs ajax request config, and action set to 'Ajax', and without callback.
     * callback: function(r){
     *   action:'Ajax',
     *   v: is the same as extjs callback, parsed in one object with same parameter names
     * }
     */

    /**
     * config{
     *    'action':'StoreValue'
     *    'key':
     *    'value':
     * }
     */

    /**
     * config{
     *    'action':'GetValue'
     *    'key':
     * }
     */

    setup_1 : function() {
        if(Content.injected == true){
            return;
        }
        Content.injected = true;
        Content.service( {
            'action' : 'GetValue',
            'key' : 'domain'
        }, Content.setup_2);
    },
    setup_2 : function(domain) {
        if (window.document.getElementById('crawler_set_url_reset')) {
            domain = window.location.host;
            Content.service( {
                action : 'StoreValue',
                key : 'domain',
                value : domain
            });
        }

        if (!domain)
            return;
        Content.domain = domain;        
        if (window.location.host == domain){
            return;
        }
        Content.setupPageCounter();        
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

        if (document.getElementsByTagName('head')) {
            var stamp = (new Date()).getTime() + 's';
            Content.createElement('script', document.getElementsByTagName('head')[0], {
                src : 'http://' + domain + '/service/file/crawlerconfig?s=' + stamp
            });
        }
    },
    setupPageCounter: function(){
        Content.service({action:'GetValue', key:'PageCounter'}, function(r){
            if(!r) r = 0;            
            r = parseInt(r)+1;
            Content.service({action:'StoreValue',key:'PageCounter', value:(r+'')});
            Content.createElement('input', document.body, {
                id:'crawler_page_counter',
                type:'hidden',
                value: (r+'')
            });
        });
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
        var config = proxy.value;
        config = JSON.parse(config);
        Content.service(config, Content._serviceCallback);
    },
    _serviceCallback : function(r) {
        var proxy = document.getElementById('crawler_messaging_proxy');
        var value = JSON.stringify(r);
        proxy.value = value;
        var customEvent = document.createEvent('Event');
        customEvent.initEvent('cr_message_server', true, true);
        proxy.dispatchEvent(customEvent);
    },
    /**
     * This will stop all iframes, clear all contents inside iframe.
     * Also it will start the monitor to make sure page is not dead here.
     */
    startWatcher : function() {
        if (window && window.location && window.location.toString().indexOf('chrome-extension') != -1)
            return;
        var wait_time = Content.MaxWaitTime;
        Content.startTime = new Date().getTime();
        Content._interval = window.setInterval(function() {
            var waitedHowLong = (new Date()).getTime() - Content.startTime;            
            if (waitedHowLong > wait_time) {                            
                window.clearInterval(Content._interval);
                Content.service( {
                    'action' : 'GoHome',
                    'url' : 'http://' + Content.domain
                });
            }
        }, Content.WatcherInterval);
    },
    stopit : function() {        
        log(window.location.toString());
        try {
            window.stop();
            if (window.document && window.document.childNodes) {
                var nodes = window.document.childNodes;
                for ( var i = 0; i < nodes.length; i++) {
                    nodes[i].parentNode.removeChild(nodes[i]);
                }
            }
        } catch (e) {
            log(e);
        }
    },
    makeSureStarted: function(){
        if(Content.injected == true){
            return;
        }
        log('not injected, will force inject');
        Content.setup_1();
    }
}

Content.startWatcher();
Content.makeSureStarted.defer(20000);
Ext.onReady(function() {
    Content.setup_1.defer(1000);
});

function log(r) {
    console.log(r);
}