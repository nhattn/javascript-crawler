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
	}	
}
