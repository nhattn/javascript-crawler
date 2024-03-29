HandlerHelper = {
    /**
     * post an array of links to server side, then execute next action.
     * 
     * the target url is "CrGlobal.StoreLinkUrl", it will return a json object with the
     * number of links added like such : { result: 2 } meaning 2 of the links
     * posted were added.
     * 
     * linkArray is an js array of links to add, like
     * ['http://www.aaa.com/link1.html', '....'] nextAction is the next thing to
     * do after server returned response code.
     * 
     */
    storeLinks : function(linkArray, nextAction) {
        if (!linkArray || linkArray.length == 0) {
            Crawler.error('HandlerHelper.storeLinks-no link to store, will go to next page.');
            Crawler.nextLink();
            return;
        }

        var data = {
            'data' : Ext.util.JSON.encode(linkArray)
        };

        var callback = function(r, suc) {
            try {
                var obj = Ext.util.JSON.decode(r.responseText);
                if (obj.result == 0 || !nextAction) {
                    Crawler.nextLink();
                } else {
                    Crawler.action(nextAction);
                }
            } catch (e) {
                Crawler.error('HandlerHelper:' + e + ':' + r.responseText);
                Crawler.nextLink();
            }
        };
        CrUtil.postData(data, CrGlobal.StoreLinkUrl, function(r, suc) {
            callback(r, suc);
        });
    },

    /**
     * given an json object, post it to server to create an new object
     */
    postObject : function(params, nextAction) {
        var callback = function(r, suc) {
            if (!suc) {
                Crawler.error("HandlerHelper, postObject call back, request failure.\n" + CrUtil.getAjaxReponseErrorString(r));
                Crawler.nextLink();
                return;
            }
            try {
                r = Ext.util.JSON.decode(r.responseText);
                if (r.result && nextAction) {
                    Crawler.action(nextAction);
                } else {
                    Crawler.nextLink();
                }
            } catch (e) {
                Crawler.error("HandlerHelper, postObject call back has exception.\n" + CrUtil.getAjaxReponseErrorString(r));
                Crawler.nextLink();
            }
        }
        CrUtil.postData(params, CrGlobal.ObjectCreationUrl, callback);
    },

    /**
     * take an xpath specifying links, and a regular expression specifying which
     * links to take.
     * 
     * both xpath and reg should be strings. return an array of links.
     */
    getMatchLinks : function(xpath, reg) {
        if (typeof reg == 'string') {
            reg = new RegExp(reg, 'i');
        }
        var links = [];
        if (typeof xpath == 'string') {
            links = XPath.array(null, xpath);
        }
        var r = [];
        for ( var i = 0; i < links.length; i++) {
            var l = links[i].href.toString();
            if (reg.test(l)) {
                r.push(l);
            }
        }
        return r;
    },

    /**
     * perform a serious of operation based on commands and xpaths, return an
     * object with the parsed values mapping is an array of object, each of
     * which contains a specification of what to do: 
     * { 
     *   name: the attribute name to assign to after action is executed 	 
     *   param[1-9]: the parameters 
     *   op: the operation to execute, can be 
     *                1 run.func, this will run param1 as the function, 
     *                    and the rest of params as arguments to the function . 
     *
     *                2 xpath.textcontent.regex, this will call the extractFromXpathNodeText
     *                    function to extract the textContent of the node. optionallly can specify
     *                    a regex group to further process the textContent, then only the first
     *                    group value will be returned
     * 
     *                    param1 specifies the xpath of node whoes text content will be taken 
     *                    param2 is optional, a regular expression with group specification, the regex will
     *                    be applyed against the textContent, and the first group value will be
     *                    taken. 
     *                or, use xpath.textcontent.regex.if. this will only assign the value if the regex exists.
     *                or. use xpath.textcontent.regex.any. with this, you can pass more than one regex, and the value 
     *                         will be set to any of the xpath's text node that is not null.
     *                
     *                3 xpath.text.regex, extract a regex group value from a given text. Takes two params,
     *                   param1 the text, param2 the xpath that contains only 1 regex group, it's value will be assinged to object
     * 
     *                4 assign.value, this will simply assign the value of param1 to the
     *                    [name] attribute
     *                
     *                5 get.in.between, extract a text between two strings
     *                  param1 the text
     *                  param2 start text
     *                  param3 end text
     */
    parseObject : function(mapping) {
        var obj = {};
        for ( var i = 0; i < mapping.length; i++) {
            try {
                var m = mapping[i];
                switch (m.op) {
                case 'run.func':
                    obj[m.name] = m.param1.apply(null, HandlerHelper._getParams(m).slice(1)).trim();
                    break;
                case 'xpath.textcontent.regex':
                    obj[m.name] = HandlerHelper.extractFromXpathNodeText.apply(HandlerHelper, HandlerHelper._getParams(m)).trim();
                    break;
                case 'xpath.textcontent.regex.any':
                    obj[m.name] = HandlerHelper.extractFromXpathNodeTextAny.apply(HandlerHelper, HandlerHelper._getParams(m)).trim();
                    break;
                case 'xpath.textcontent.regex.if':
                    obj[m.name] = HandlerHelper.extractFromXpathNodeTextIf.apply(HandlerHelper, HandlerHelper._getParams(m)).trim();
                    break;
                case 'xpath.text.regex':
                    obj[m.name] = HandlerHelper.getRegGroupFirstValue.apply(HandlerHelper, HandlerHelper._getParams(m)).trim();
                    break;
                case 'assign.value':
                    obj[m.name] = m.param1;
                    break;
                case 'get.in.between':
                    obj[m.name] = HandlerHelper.getInBetween.apply(HandlerHelper, HandlerHelper._getParams(m)).trim();
                    break;
                default:
                    Crawler.error('wrong op' + m.op);
                }
            } catch (exc) {
                Crawler.error('Can not parse attribute: ' + m.name + ', exception is ' + exc);
            }
        }
        return obj;
    },
    getInBetween : function(s, start, end) {
        return CrUtil.getBetween(s, start, end);
    },
    extractFromXpathNodeTextAny : function() {
        var paths = arguments;
        for ( var i = 0; i < paths.length; i++) {
            var p = paths[i];
            var n = XPath.single(null, p);
            if (n != null && n.textContent != null) {
                return n.textContent;
            }
        }
        return null;
    },
    /**
     * will not through exception, return '' if there is nothing.
     */
    extractFromXpathNodeTextIf : function(xp, reg) {
        try {
            var n = XPath.single(null, xp);
            if (n && n.textContent) {
                var r = n.textContent;
                if (typeof reg == 'object') {
                    r = HandlerHelper.getRegGroupFirstValue(r, reg);
                }
                return r;
            }
        } catch (e) {
            Crawler.error('extractFromXpathNodeTextIf' + e);
        }
        return '';
    },

    extractFromXpathNodeText : function(xp, reg) {
        var r = XPath.single(null, xp);
        if (!r) {
            Crawler.warn('Can not get xpath node :' + xp);
            return;
        }
        r = r.textContent;
        if (typeof reg == 'object') {
            r = HandlerHelper.getRegGroupFirstValue(r, reg);
        }
        return r;
    },

    _getParams : function(obj) {
        var r = [];
        for ( var i = 1; i < 10; i++) {
            if (obj['param' + i]) {
                r.push(obj['param' + i]);
            } else {
                return r;
            }
        }
        return r;
    },

    /**
     * s - a string r - a regular expression with group specifications. only the
     * first matching group value will be returned
     */
    getRegGroupFirstValue : function(s, r) {
        if (typeof s != 'string' || typeof r == 'undefined') {
            Crawler.log('HandlerHelper: getRegGroupFirstValue:Can not match:' + s + ':' + r);
        }
        if (typeof r == 'string') {
            r = new RegExp(r, 'i');
        }
        var arr = s.match(r);
        if (arr && arr.length > 1)
            return arr[1];
        else {
            /* Crawler.log('HandlerHelper: getRegGroupFirstValue:Can not
            match:'+s+':'+r);
            return 'Can not match:'+s+':'+r;*/
            return '';
        }
    }
}
