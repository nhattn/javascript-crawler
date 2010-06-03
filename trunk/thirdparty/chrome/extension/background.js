var ServiceHandler = {
    handleRequest : function(request, sender, callback) {
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
            method(request, callback)
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
        request.callback = function(opt, suc, resp) {
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
        Ext.Ajax.request(request);
    },

    goHome : function(request, callback) {
        try {
            log("going to inject");
            chrome.tabs.executeScript(null, {
                code : "window.location='http://www.yahoo.com'"
            });
        } catch (e) {
            console.log('error1:' + e)
        }
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
        if (callback)
            callback(value);
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

function log(t) {
    if (console) {
        console.log(t);
    }
}