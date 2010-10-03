function Couchdb(dbName, hostName) {
    this.db = dbName;
    this.host = hostName;
    this.init();
}

Ext.apply(Couchdb.prototype, {
    init : function() {
        this.url = 'http://' + this.host + '/' + this.db + '/';
    },
    create : function(doc, callback) {
        var url = this.url, method = 'PUT';
        if (typeof doc.id == 'undefined') {
            /* not giving an id, use auto assigned */
            method = 'POST';
        } else {
            url = url + doc.id;
            delete doc.id;
        }
        CrUtil.ajax( {
            url : url,
            method : method,
            params : JSON.stringify(doc)
        }, callback);
    },
    /**
     * callback(array_of_json_returned_from_couch_db)
     * 
     * if nothing wrong with an entry, it will be something like
     * [{"id":"1681565462","rev":"1-9998fc8bca7c33450c02e245928399e1"},{"id":"1819688303","rev":"1-bbb85.... }]
     * 
     * if something goes wrong, it will be
     * [{"id":"1681565462","error":"conflict","reason":"Document update conflict."},{"id":"1819688303",.... }]
     * 
     * or if there is http error, will call with null
     */
    createBulk : function(docs, callback) {
        CrUtil.ajax( {
            method : "POST",
            url : this.url + '_bulk_docs',
            params : JSON.stringify( {
                docs : docs
            })
        }, function(obj) {
            if (obj.success) {
                try {
                    callback(JSON.parse(obj.response.responseText));
                } catch (e) {
                    Crawler.error('error parsing couchdb results');
                }
            } else {
                Crawler.error('http failure when createBulk');
                callback(null);
            }
        });
    },
    get : function(spec, callback) {
        var url = this.url, isSingle = true;
        if (typeof spec == 'string') {
            url = url + spec;
        } else {
            url = url + spec.url;
            if (spec.params) {
                var values = [];
                for ( var p in spec.params) {
                    values.push(p + '=' + escape(spec.params[p]));
                }
                if (url.indexOf('?') == -1) {
                    url = url + '?' + values.join('&');
                } else {
                    url = url + '&' + values.join('&');
                }
            }
            isSingle = false;
        }
        var callerCallback = callback;
        CrUtil.ajax( {
            method : "GET",
            url : url
        }, function(obj) {
            if (obj.success) {
                callerCallback(JSON.parse(obj.response.responseText));
            } else {
                callerCallback(null);
            }
        });
    },
    del : function(docid, revid, callback) {

    },
    update : function(docId, newValue, callback) {
    }
});

function RecentPost() {
}

Ext.apply(RecentPost.prototype, {
    nextPage : function() {
        var node = XPath.single(null, './/a/em[contains(text(), "下一页")]');
        if (node) {
            CrUtil.clickNode.defer(1000, null, [ node ]);
        } else {
            CrUtil.clickNode.defer(1000, null, [ XPath.single(null, './/a/em[contains(text(), "1")]') ]);
        }
    },

    getLatestUser : function() {
        var links = XPath.array(null, '//p[@class="sms"]/a[1]');
        var uset = {}, time = new Date().getTime(), values = [];
        for ( var i = 0; i < links.length; i++) {
            var a = links[i];
            var uid = a.href.match(/([0-9]+)$/), userName = a.title;
            if (!uid || uid.length < 2) {
                Crawler.error('should ends with number: ' + a.href);
                continue;
            }
            uid = uid[1];
            if (uset[uid]) {
                continue;
            }
            uset[uid] = true;
            values.push( {
                _id : uid + '',
                name : userName,
                time : time
            });
        }
        return values;
    }
});

function type(type, obj) {
    if (obj.length && typeof obj[0] == 'object') {
        for ( var i = 0; i < obj.length; i++) {
            obj[i].type = type;
        }
    } else {
        obj.type = type;
    }
}

function handlerProcess() {
    var c = new Couchdb('sinat', 'localhost:5984');
    var recentPost = new RecentPost();
    var values = recentPost.getLatestUser();
    type('recent_post', values);    
    c.createBulk(values, function(obj) {
        var len = 0;
        for ( var i = 0; i < obj.length; i++) {
            if (!obj[i].error) {
                len++;
            }
        }
        console.log('created ' + len);
//        console.log(obj);
//        return;
        if (len == 0)
            recentPost.nextPage.defer(30000, recentPost);
        else
            recentPost.nextPage();
    });
}
