// ==UserScript==
// @name           xpath
// @namespace      test
// @include        *
// ==/UserScript==

window.addEventListener('load',function() {
  var host = 'localhost:8080';
  var stamp=(new Date()).getTime()+'s';
  if(window!= window.top) {
  	return;
  }
  var sf=document.createElement('script');
  sf.setAttribute('type','text/javascript');
  sf.setAttribute('src', 'http://'+host+'/crawler/js/core/crawler_loader.js?s='+stamp);  
  document.getElementsByTagName('head')[0].appendChild(sf);  
},true);

