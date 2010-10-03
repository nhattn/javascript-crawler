function log(s){
    console.log(s);
}
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

function MainAccount(userId, couchdb) {
    this.userId = userId;
    this.c = couchdb;
    this.source = 1425496239;
}
Ext.apply(MainAccount.prototype, {
    updateFollowerList : function() {
        var _self = this;
        CrUtil.ajax( {
            url : 'http://api.t.sina.com.cn/followers/ids.json',
            method : 'GET',
            params : {
                user_id : this.userId,
                source : this.source
            }
        }, function(r) {
            _self._followerGot(r.option, r.success, r.response);
        });
    },
    _followerGot : function(opt, suc, resp) {        
        if (!suc) {
            console.log('Request failure, can not get follower list');
            return;
        }
        var followers = JSON.parse(resp.responseText);
        if(!followers.ids || followers.ids.length==0){            
            return;
        }
        console.log(followers);
    }        
});

function FollowUser() {
    this.c = new Couchdb('sinat', 'localhost:5984');
}
Ext.apply(FollowUser.prototype, {
    nextUser : function() {
        var lastIndex = CrUtil.getCookie('sinat_last_user');
        if (lastIndex == null) {
            lastIndex = -1;
        }
        lastIndex++;
        var _self = this;
        this.c.get( {
            url : '_design/myview/_view/tofollow',
            params : {
                limit : 1,
                skip : lastIndex
            }
        }, function(r) {
            if (!r) {
                console.log('Error, nothing returned, will do nothing else');
                return;
            }
            if (r.offset >= r.total_rows) {
                console.log('Rows exhuasted, will do nothing');
                return;
            }
            CrUtil.setCookie('sinat_last_user', lastIndex + '', 100);
            console.log('index ' + lastIndex);
            Crawler.gotoLink('http://t.sina.com.cn/' + r.rows[0].value);
        });
    },
    kickOff : function() {
        var node = XPath.single(null, "//em[contains(text(), '加关注')]");
        if (node && document.body.textContent.indexOf('没有开始微博') == -1) {
            console.log('going to follow this user');
            CrUtil.clickNode(node);
        } else {
            console.log('not following this user');
        }
        this.nextUser.defer(2000, this);
    }
});

function handlerProcess() {
    var c = new Couchdb('sinat', 'localhost:5984');
    var m = new MainAccount('1825789952', c);
    m.updateFollowerList();
}

function massFollow() {
    var uids = [];
    for ( var i = 0; i < c.length; i++) {
        uids.push(c[i].id);
    }
    console.log(uids.length);

    App.doRequest( {
        uid : uids.join(","),
        fromuid : '1825789952'
    }, "/attention/aj_addfollow.php", function() {
        console.log(arguments);
    }, function() {
        console.log(arguments);
    });
}