/**
 * The following task will be done here:
 * 1. moniter loading / completing event of tab. 
 *     When loading, will inject code into src to moniter iframe loading, remove all iframes. Assigning all removed iframes to 
 *      window.cr_iframes[{id:'iframeid', name:'iframename', src:'iframesrc'}] 
 *     
 *     
 * 2. receving service calls from background page
 * 
 * It will NOT do anything to the iframe other than removing it.
 */

var ServiceHandler = {
    /**
     * request : an object contains parameter 
     *    {
     *      action: xxx,
     *      value1: valueyy
     *    }
     * callback: a function to call after request is done, signature is like 
     *     function(r) {
     *       r is the only parameter allowed.
     *     }
     */
    handleRequest : function(request, sender, callback) {
        console.log('service request :');
        console.log(request);
        var func = request.action;
        if (!func) {
            log('Error, no action passed in ServiceHandler');
            return;
        }
        if (typeof func != 'string') {
            log('Error, invalid action, not a string. ' + func);
            return;
        }
        func = func.substring(0, 1).toLowerCase() + func.substring(1);
        var method = ServiceHandler[func];
        if (!method) {
            log('Error, no method in ServiceHandler. ' + func);
            return;
        }
        if (typeof method != 'function') {
            log('Error, invalid action, not a method . ' + method);
            return;
        }
        try {
            method(request, callback, sender);
        } catch (e) {
            log('Exception happend while executing service call in ServiceHanlder : ' + e);
        }
    },

    /**
     * request {
     *  src : the url of the image,
     *  width : the width of the image, int 
     *  height: height of the image ,
     *  
     *  callback: function(r){
     *     // r is the encoded url string
     *  }
     * }
     */
    encodeImage : function(request, callback) {
        var src = request.src, width = request.width, height = request.height;
        var img = ServiceHandler._createElement('img', document.body, {
            src : src,
            width : width,
            height : height
        });
        ServiceHandler._imageToBase64(img, width, height, callback);
    },

    _imageToBase64 : function(img, width, height, callback) {
        if (!img.complete) {
            ServiceHandler._imageToBase64.defer(50, null, [ img, width, height, callback ]);
            return;
        }
        var canvas = document.createElement("canvas");
        canvas.width = width;
        canvas.height = height;
        canvas.getContext("2d").drawImage(img, 0, 0);
        var r = canvas.toDataURL("image/png").replace(/^data:image\/(png|jpg);base64,/, "");
        img.parentNode.removeChild(img);
        if (callback) {
            try {
                callback(r);
            } catch (e) {
                log('Error in ServiceHandler encodeImage callback ' + e);
            }
        }
    },

    /**
     * request is an extjs ajax request parameter
     * callback will automatically assigned to request callback
     * callback: function(r){
     *  // r is the same as the extjs ajax callback signature parsed in one single object.
     *  r = {
     *      option:
     *      success: 
     *      reponse:
     *  }
     * }
     */
    ajax : function(request, callback) {
        var nrequest = {};
        Ext.apply(nrequest, request);
        if (callback)
            nrequest.callback = function(opt, suc, resp) {
                try {
                    callback( {
                        option : opt,
                        success : suc,
                        response : resp
                    });
                } catch (e) {
                    log('Error in ServiceHandler ajax callback ' + e);
                }
            }
        Ext.Ajax.request(nrequest);
    },

    goHome : function(request, callback, sender) {
        console.log('calling go home')
        var tabid = null;
        if (sender && sender.tab) {
            tabid = sender.tab.id;
        }
        chrome.tabs.create( {
            url : request.url,
            selected : false
        }, function() {
            chrome.tabs.remove(tabid);
        });
    },

    storeValue : function(request, callback) {
        var key = request.key, value = request.value;
        window.localStorage.removeItem(key);
        window.localStorage.setItem(key, value);
        if (callback)
            callback(true);
    },

    getValue : function(request, callback) {
        var key = request.key;
        var value = window.localStorage.getItem(key);
        if (callback) {
            try {
                callback(value);
            } catch (e) {
                log('Error in ServiceHandler getValue callback ' + e);
            }
        }
    },

    _createElement : function(tagName, parentNode, attributes) {
        if (!parentNode || !tagName || !attributes)
            return;
        var hostEl = document.createElement(tagName);
        for ( var i in attributes) {
            hostEl.setAttribute(i, attributes[i]);
        }
        parentNode.appendChild(hostEl);
        return hostEl;
    }
}

chrome.extension.onRequest.addListener(ServiceHandler.handleRequest);
chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
    if (changeInfo.status == 'loading') {
        try {
            chrome.tabs.executeScript(tab.id, {
                file : "injectIntoContent_1.js"
            });
        } catch (e) {
            log('Error injecting script to window during loading phase. Exception is ' + e + '. Window url is ' + tab.url);
        }
    } else {
        try {
            chrome.tabs.executeScript(tab.id, {
                file : "injectIntoContent_2.js"
            });
        } catch (e) {
            log('Error injecting script to window during complete phase. Exception is ' + e + '. Window url is ' + tab.url);
        }
    }
});

function log(t) {
    if (console) {
        console.log(t);
    }
}