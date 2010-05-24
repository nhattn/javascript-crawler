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

    removeGA: function(document){
        var hs = document.getElementsByTagName('script');
        for(var i=0;i<hs.length;i++){
            var h = hs[i];    
            if(h.src && h.src.indexOf('google-analytics')!=-1){
                h.parentNode.removeChild(h);
            }
            if(h.textContent && h.textContent.indexOf('google-analytics')!=-1){
                h.parentNode.removeChild(h);
            }
        }
    },

    /**
     * encode a image as base64, image is an html image element get by
     * document.getElementById
     */
    encodeImage : function(img) {
        netscape.security.PrivilegeManager.enablePrivilege("UniversalBrowserRead");
        var canvas = document.createElement("canvas");
        canvas.width = img.width;
        canvas.height = img.height;
        var ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0);
        var dataURL = canvas.toDataURL("image/png");
        return dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
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
    getAjaxReponseErrorString: function(r){
        if(r.responseText){
            return 'Server raw reponse : \n'+r.responseText;
        }else{
            var s = ['Ajax state :'];
            for(var i in r){
                s.push(i+' = '+r[i]);
            }
            return s.join('\n');
        }
    },
    
    objToString : function(obj){
        var r = [];
        for(var i in obj){
            r.push(i);
            r.push('=');
            r.push(obj[i]);
            r.push(', ');
        }
        return r.join('');
    },

    /**
     *  s is something like "小说类别：虚拟网游 总点击：4736 总推荐：419 总字数：228132 更新：2009年10月10日".
     *  extract the value for keys like "小说类别" or "总点击".
     */
    extract : function(s, key, defaultValue){
        var start, end;
        if(typeof defaultValue == 'undefined'){
            defaultValue = '';
        }
        s = CrUtil.killSpace(s);
        start = s.indexOf(key);
        if(start<0){
            return defaultValue;
        }
        
        start = start + key.length;
        var space =  /^\s$/;
        while(start<s.length && space.test(s.charAt(start++))){
            // skip all the spaces
        }       
        start--;
        end = start+1;
        var nspace =  /^\S$/;
        while(end<s.length && nspace.test(s.charAt(end++))){
            // skip all the none spaces
        }           
        var r = '';
        try{
            r = s.substring(start,end);
        }catch(e){
            Crawler.error("crawler_loader.extract:"+e+":"+e);
        }        
        return r.trim();
    },
    
    killSpace : function(s){
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
     */
    getBetween: function(s, start, end){
        if(!s || s.length == 0){
            return '';
        }
        var i = s.indexOf(start);
        i = i + start.length;        
        if(i==-1) return '';
        
        var j = s.indexOf(end, i+1);
        if(j==-1) return '';                
        return s.substring(i,j);        
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
    
    removeNewLine : function(s){
        if(s) return s.replace(/\s*\n\s*/g, ' ');        
    },
    
    trimAttributes: function(obj){
        for (var p in obj) {
            if (obj[p]  && obj[p].trim) {
                obj[p] = obj[p].trim();
            }
        }    
    },
    /**
     * shanghai.koubei.com -> koubei.com
     * www.ganji.com -> ganji.com
     */
    getShortestDomain: function(domain){
        domain = domain.toString();
        var end = domain.lastIndexOf('.');
        if(end == -1){
            return domain;
        }
        var start = domain.lastIndexOf('.', end-1);
        if(start == -1){
            return domain;
        }
        return domain.substring(start+1);
    },
    
    /**
      * extract parameter from a given http url.
      * url = http://ditu.koubei.com/map/fangdetailmap.html?city=2076&searchtype=2&centerx=12141764&centery=3117466&centername=%D6%D0%BB%AA%C3%C5%B4%F3%CF%C3&rentorsell=rent"
      * param = centerx
      * return = 12141764
      */
    extractParameter: function(url, param){
        if(!url || !param){
            return '';
        }
        param = param+'=';
        var start = url.indexOf(param);
        if(start == -1){
            return '';
        }
        var end = url.indexOf('&', start + 1);
        if(end == -1){
            end = url.length;
        }
        return url.substring(start + param.length, end);
    },
    
    deleteTokens: function(s, tokens){
        if(!s || !tokens || tokens.length ==0){
            return s;
        }
        for(var i=0;i<tokens.length;i++){            
            var t = tokens[i];
            if(t)
                s = s.replace(t, '');            
        }
        return s;
    }
    
        
}
