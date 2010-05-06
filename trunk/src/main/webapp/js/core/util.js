CrUtil = {
	removeFrames : function(document) {
		var d = document.getElementsByTagName('iframe');
		for ( var i = 0; i < d.length; i++) {
			var f = d[i];
			f.parentNode.removeChild(f);
		}
	}
}