CrUtil = {
    _frameCounter : 0,
    removeFrames : function(document) {
        CrUtil._frameCounter++;
        if (CrUtil._frameCounter > 20) {
            return;
        }
        var d = document.getElementsByTagName('iframe');
        for ( var i = 0; i < d.length; i++) {
            var f = d[i];
            f.src = null;
            f.parentNode.removeChild(f);
        }
        setTimeout(function() {
            CrUtil.removeFrames(document);
        }, 100);
    },
    _imageCounter : 0,
    /**
     * exce is an array, if the src path of the image contains any elements in the array, it will be ignored.
     */
    removeImages : function(exce) {
        CrUtil._imageCounter++;
        if (CrUtil._imageCounter > 20) {
            return;
        }
        var d = document.getElementsByTagName('img');
        for ( var i = d.length - 1; i > -1; i--) {
            var f = d[i];
            if (f.__skip == true) {
                continue;
            }
            if (exce) {
                var src = f.src;
                if (src) {
                    var matched = false;
                    for ( var j = 0; j < exce.length; j++) {
                        if (src.indexOf(exce[j]) != -1) {
                            f.__skip = true;
                            matched = true;
                            break;
                        } else {
                        }
                    }
                    if (matched == true) {
                        continue;
                    }
                }
            }
            f.parentNode.removeChild(f);
        }
        setTimeout(function() {
            CrUtil.removeImages(document);
        }, 80);
    },

    removeGA : function(document) {
        var hs = document.getElementsByTagName('script');
        for ( var i = 0; i < hs.length; i++) {
            var h = hs[i];
            if (h.src && h.src.indexOf('google-analytics') != -1) {
                h.parentNode.removeChild(h);
            }
            if (h.textContent && h.textContent.indexOf('google-analytics') != -1) {
                h.parentNode.removeChild(h);
            }
        }
    },

    removeElementsByTagName : function(tagName, parentNode) {
        if (!parentNode) {
            parentNode = document;
        }
        var els = parentNode.getElementsByTagName(tagName);
        for ( var i = els.length - 1; i > -1; i--) {
            var e = els[i];
            e.parentNode.removeChild(e);
        }
    },

    postData : function(params, url, callback) {
        if (!callback)
            callback = Crawler.callback
        var encoding = document.characterSet;
        params.encoding = encoding;
        Ext.Ajax.request( {
            url : url,
            success : function(r) {
                try {
                    callback(r, true);
                } catch (e) {
                    Crawler.error('util.postdata:' + e);
                }
            },
            failure : function(r) {
                try {
                    callback(r, false);
                } catch (e) {
                    Crawler.error('util.postdata:' + e);
                }
            },
            method : 'POST',
            params : params
        });
    },

    getAjaxReponseErrorString : function(r) {
        if (r.responseText) {
            return 'Server raw reponse : \n' + r.responseText;
        } else {
            var s = [ 'Ajax state :' ];
            for ( var i in r) {
                s.push(i + ' = ' + r[i]);
            }
            return s.join('\n');
        }
    },

    objToString : function(obj) {
        var r = [];
        for ( var i in obj) {
            r.push(i);
            r.push('=');
            r.push(obj[i]);
            r.push(', ');
        }
        return r.join('');
    },

    /**
     * s is something like "小说类别：虚拟网游 总点击：4736 总推荐：419 总字数：228132
     * 更新：2009年10月10日". extract the value for keys like "小说类别" or "总点击".
     */
    extract : function(s, key, defaultValue) {
        var start, end;
        if (typeof defaultValue == 'undefined') {
            defaultValue = '';
        }
        s = CrUtil.killSpace(s);
        start = s.indexOf(key);
        if (start < 0) {
            return defaultValue;
        }

        start = start + key.length;
        var space = /^\s$/;
        while (start < s.length && space.test(s.charAt(start++))) {
            /* skip all the spaces */
        }
        start--;
        end = start + 1;
        var nspace = /^\S$/;
        while (end < s.length && nspace.test(s.charAt(end++))) {
            /* skip all the none spaces */
        }
        var r = '';
        try {
            r = s.substring(start, end);
        } catch (e) {
            Crawler.error("crawler_loader.extract:" + e + ":" + e);
        }
        return r.trim();
    },

    killSpace : function(s) {
        return s.replace(/\s+/g, ' ');
    },

    setCookie : function(c_name, value, expiredays) {
        var exdate = new Date();
        exdate.setDate(exdate.getDate() + expiredays);
        document.cookie = c_name + "=" + escape(value) + ((expiredays == null) ? "" : ";expires=" + exdate.toUTCString());
    },

    getCookie : function(c_name) {
        if (document.cookie.length > 0) {
            c_start = document.cookie.indexOf(c_name + "=");
            if (c_start != -1) {
                c_start = c_start + c_name.length + 1;
                c_end = document.cookie.indexOf(";", c_start);
                if (c_end == -1)
                    c_end = document.cookie.length;
                return unescape(document.cookie.substring(c_start, c_end));
            }
        }
        return "";
    },
    /**
     * extract a string that is inside s, between start and end, excluding both.
     * s= 'aabbccddeeffgg', start = 'aa', end = 'ff' will return 'bbccddee'.
     * if startIndex is provided, will search from a specific index
     */
    getBetween : function(s, start, end, startIndex) {
        if (!s || s.length == 0) {
            return '';
        }
        var i = -1;
        if (startIndex) {
            i = s.indexOf(start, startIndex);
        } else {
            i = s.indexOf(start);
        }
        if (i == -1)
            return '';
        i = i + start.length;

        var j = s.length;
        if (end) {
            j = s.indexOf(end, i + 1);
            if (j == -1)
                return '';
        }
        return s.substring(i, j);
    },

    getRequest : function(url) {
        var AJAX = new XMLHttpRequest();
        if (AJAX) {
            AJAX.open("GET", url, false);
            AJAX.send(null);
            return AJAX.responseText;
        } else {
            return false;
        }
    },

    removeNewLine : function(s) {
        if (s)
            return s.replace(/\s*\n\s*/g, ' ');
    },

    trimAttributes : function(obj) {
        for ( var p in obj) {
            if (obj[p] && obj[p].trim) {
                obj[p] = CrUtil.normalizeValue(obj[p].trim());
            }
        }
    },

    normalizeValue : function(s) {
        if (!s)
            return s;
        if (typeof s != 'string')
            return s;
        return s.replace(/[ \f\t\v\u00A0\u2028\u2029&<>]+/g, ' ');
    },

    extractNumber : function(s) {
        if (!s)
            return '';
        if (typeof s != 'string')
            return '';
        var r = '';
        for ( var i = 0; i < s.length; i++) {
            var c = s[i];
            if ((c <= '9' && c >= '0') || c == '.') {
                r = r + c;
            }
        }
        return r;
    },

    /**
     * shanghai.koubei.com -> koubei.com www.ganji.com -> ganji.com
     */
    getShortestDomain : function(domain) {
        domain = domain.toString();
        var end = domain.lastIndexOf('.');
        if (end == -1) {
            return domain;
        }
        var start = domain.lastIndexOf('.', end - 1);
        if (start == -1) {
            return domain;
        }
        return domain.substring(start + 1);
    },

    /**
     * extract parameter from a given http url. url =
     * http://ditu.koubei.com/map/fangdetailmap.html?city=2076&searchtype=2&centerx=12141764&centery=3117466&centername=%D6%D0%BB%AA%C3%C5%B4%F3%CF%C3&rentorsell=rent"
     * param = centerx return = 12141764
     */
    extractParameter : function(param, url) {
        if (!url)
            url = window.location.toString();

        if (!param) {
            return '';
        }
        param = param + '=';
        var start = url.indexOf(param);
        if (start == -1) {
            return '';
        }
        var end = url.indexOf('&', start + 1);
        if (end == -1) {
            end = url.length;
        }
        return url.substring(start + param.length, end);
    },

    deleteTokens : function(s, tokens) {
        if (!s || !tokens || tokens.length == 0) {
            return s;
        }
        for ( var i = 0; i < tokens.length; i++) {
            var t = tokens[i];
            if (t)
                s = s.replace(t, '');
        }
        return s;
    },
    /*
     * delete anything that is after token.
     */
    deleteAfter : function(s, token) {
        var i = s.indexOf(token);
        if (i != -1) {
            return s.substring(0, i);
        }
        return s;
    },
    /**
     * requesting a service from the extension,
     * config is an object like this 
     * {
     *    action : 'name of the service',
     *    callback : function def that will be invoked, can only take one parameter, function(r){}
     *    any ther parameters..    
     * }
     * How does messaging work.
     * 
     * requester (client) set a "_cr_value_client" on document.body, with the parameter stringifed as value.
     * then initiate a "cr_message_client" event.
     * requester then listens on "cr_message_server" event, check  "_cr_value_server"
     *
     * 
     * service listens "cr_message_client" on document.body, get parameter from "cr_message_client", then do it's part, after it's done,
     * initiate a "cr_message_server" event, set the "_cr_value_server" property
     * 
     * callback function will be stored in 'cr_message_callback' 
     */
    requestService : function(config, callback) {
        CrUtil._clearService();
        var nconfig = {};
        var proxy = document.getElementById('crawler_messaging_proxy');
        var customEvent = document.createEvent('Event');
        customEvent.initEvent('cr_message_client', true, true);
        Ext.apply(nconfig, config);
        if (callback) {
            CrUtil.cr_message_callback = callback;
        }

        proxy.value = JSON.stringify(nconfig);
        proxy.addEventListener('cr_message_server', CrUtil._serviceEventListener, true);
        proxy.dispatchEvent(customEvent);
    },

    _clearService : function() {
        var proxy = document.getElementById('crawler_messaging_proxy');
        proxy.removeEventListener('cr_message_server', CrUtil._serviceEventListener, true);
        var values = [ 'cr_message_callback', 'cr_message_client', 'cr_message_server' ];
        for ( var i = 0; i < values.length; i++) {
            delete proxy[values[i]];
        }
    },

    _serviceEventListener : function() {
        var proxy = document.getElementById('crawler_messaging_proxy');
        var serverReturnedValue = proxy.value;
        if (CrUtil.cr_message_callback) {
            CrUtil.cr_message_callback(JSON.parse(serverReturnedValue));
        }
        CrUtil._clearService();
    },

    /**
     * encode a image as base64, image is an html image element get by
     * document.getElementById
     */
    encodeImage : function(img) {
        var canvas = document.createElement("canvas");
        canvas.width = img.width;
        canvas.height = img.height;
        var ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0);
        var dataURL = canvas.toDataURL("image/png");
        return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
    },

    encodeImage2 : function(imgDom, callback) {
        if (!imgDom.complete) {
            CrUtil.encodeImage2.defer(50, null, [ imageDom, callback ]);
            return;
        }

        CrUtil.requestService( {
            action : 'EncodeImage',
            src : imgDom.src,
            width : imgDom.width,
            height : imgDom.height
        }, callback);
    },

    /**
     * encode an array of images. 
     * passing in the dom array of img element, and a call back function like 
     * func(r){
     *  // r is an array of base64 encoded string of image data
     * }
     * result is the array you want to hold, same as r. can be null.
     */
    encodeImageArray : function(imageDoms, callbackFunc, result) {
        if (!result) {
            result = [];
        }

        if (result.length == imageDoms.length) {
            callbackFunc(result);
            return;
        }

        var serviceCallback = function(data) {
            result[result.length] = data;
            CrUtil.encodeImageArray(imageDoms, callbackFunc, result);
        };

        setTimeout(function() {
            CrUtil.encodeImage2(imageDoms[result.length], serviceCallback);
        }, 1);

    },

    ajax : function(config, callback) {
        var nconfig = {};
        Ext.apply(nconfig, config);
        nconfig.action = 'Ajax';
        CrUtil.requestService(nconfig, callback);
    },

    getFrameInfo : function() {
        var frames = [];
        for ( var i = 0; i < 20; i++) {
            var f = document.getElementById('crframe__' + i);
            if (f) {
                frames.push(JSON.parse(f.value));
            } else {
                break;
            }
        }
        return frames;
    },

    getFrameInfoById : function(id) {
        var frames = CrUtil.getFrameInfo();
        for ( var i = 0; i < frames.length; i++) {
            if (frames[i].id == id) {
                return frames[i];
            }
        }
        return null;
    },

    restartBrowser : function(homeurl) {
        CrUtil.requestService( {
            action : 'GoHome',
            url : homeurl
        });
    },

    getPersistentValue : function(name, callback) {
        CrUtil.requestService( {
            action : 'getValue',
            key : name
        }, callback);
    },

    setPersistentValue : function(name, value, callback) {
        CrUtil.requestService( {
            action : 'storeValue',
            key : name,
            value : value
        }, callback);
    },

    randomString : function() {
        return 'sid' + new Date().getTime();
    },
    guessTime : function(ts) {
        var d = new Date(), s;
        /* 06-23 17:23:23 */
        s = ts.match(/([0-9][0-9])-([0-9][0-9]) ([0-9][0-9]):([0-9][0-9]):([0-9][0-9])/);
        if (s && s.length == 6) {
            d.setMonth(parseInt(s[1], 10) - 1);
            d.setDate(parseInt(s[2], 10));
            d.setHours(parseInt(s[3], 10));
            d.setMinutes(parseInt(s[4], 10));
            d.setSeconds(parseInt(s[5], 10))
            return d;
        }

        /* 06-23 17:23 */
        s = ts.match(/([0-9][0-9])-([0-9][0-9]) ([0-9][0-9]):([0-9][0-9])/);
        if (s && s.length == 5) {
            d.setMonth(parseInt(s[1], 10) - 1);
            d.setDate(parseInt(s[2], 10));
            d.setHours(parseInt(s[3], 10));
            d.setMinutes(parseInt(s[4], 10));
            return d;
        }

        /* 06-23 */
        s = ts.match(/([0-9][0-9])-([0-9][0-9])/);
        if (s && s.length == 3) {
            d.setMonth(parseInt(s[1], 10) - 1);
            d.setDate(parseInt(s[2], 10));
            return d;
        }

        /* 1分钟前 */
        var minBefore = 0, hasMinMatch = false;
        s = ts.match(/([0-9]+)分钟/);
        if (s && s.length == 2) {
            minBefore = parseInt(s[1], 10);
            hasMinMatch = true;
        }

        s = ts.match(/([0-9]+)小时/);
        if (s && s.length == 2) {
            minBefore = parseInt(s[1], 10) * 60;
            hasMinMatch = true;
        }

        s = ts.match(/([0-9]+)天/);
        if (s && s.length == 2) {
            minBefore = parseInt(s[1], 10) * 1440;
            hasMinMatch = true;
        }
        if (hasMinMatch) {
            d.setMinutes(d.getMinutes() - minBefore);
            return d;
        }
        return null;
    },
    testGuessTime : function() {
        var s = [ '06-23 17:23:23', '06-23 17:23', '06-23', '1分钟前', '1小时前', '1天前' ];
        for ( var i = 0; i < s.length; i++) {
            alert(CrUtil.guessTime(s[i]));
        }
    },
    clickNode : function(node) {
        var e = document.createEvent('MouseEvents');
        e.initEvent('click', true, false)
        node.dispatchEvent(e);
    },

    dateString : function(date) {
        if (date == null)
            date = new Date();
        var r = [], s;
        r[r.length] = date.getFullYear();

        s = (date.getMonth() + 1) + '';
        if (s.length == 1) {
            s = '0' + s;
        }
        r[r.length] = s;

        s = date.getDate() + '';
        if (s.length == 1) {
            s = '0' + s;
        }
        r[r.length] = s;
        return r.join('-');
    },

    setCookie : function(name, value) {
        var Days = 30;
        var exp = new Date();
        exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
        document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
    },
    getCookie : function(name) {
        var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
        if (arr != null)
            return unescape(arr[2]);
        return null;

    },
    delCookie : function(name) {
        var exp = new Date();
        exp.setMonth(exp.getMonth() - 1);
        var cval = CrUtil.getCookie(name);
        if (cval != null)
            document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
    }
}
