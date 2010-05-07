CrUtil = {
	removeFrames : function(document) {
		var d = document.getElementsByTagName('iframe');
		for ( var i = 0; i < d.length; i++) {
			var f = d[i];
			f.parentNode.removeChild(f);
		}
		setTimeout(function() {
			CrUtil.removeFrames(document);
		}, 1000);
	},

	/**
	 * encode a image as base64,
	 * image is an html image element get by document.getElementById
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
	
	postData : function(params, url, callback){
	    if(!callback) callback = Crawler.callback
	    var encoding = document.characterSet;
	    params.encoding = encoding;	    
	    Ext.Ajax.request({
	        url: url,
	        success: function(r){try{callback(r,true);}catch(e){Crawler.error('util.postdata:'+e);} },
	        failure: function(r){try{callback(r,false);}catch(e){Crawler.error('util.postdata:'+e);} },
	        method: 'POST',
	        params: params        
	     });        
	}	
}