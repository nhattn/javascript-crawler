// ==UserScript==
// @name           xpath
// @namespace      test
// @include        *
// ==/UserScript==

function startWatcher() {
    // this line can be used to restart firefox 
//    netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
//    var Application = Components.classes["@mozilla.org/fuel/application;1"].getService(Components.interfaces.fuelIApplication);
//    alert(Application);
    if (window != window.top || !window.document || !window.document.body) {
        return;
    }    
    var wait_time = 10 * 1000;
    GM_setValue('startTime', (new Date()).getTime().toString());
    window.setInterval(function() {
        var startTime = parseInt(GM_getValue('startTime', null));
        var waitedHowLong = (new Date()).getTime() - startTime;
        if (waitedHowLong > wait_time) {
            window.location = 'http://' + GM_getValue('domain', null);
        }
    }, 500);
}

startWatcher();

window.addEventListener('load', function() {
    if (window != window.top || !window.document || !window.document.body) {
        return;
    }

    var domain = GM_getValue('domain', null);
    if (window.document.getElementById('crawler_set_url_reset')) {
        domain = window.location.host;
        GM_setValue('domain', domain);
    }

    if (!domain) {
        if (window.console) {
            window.console.log("Can not find server domain, do nothing.");
        } else {
            alert("Can not find server domain, do nothing.");
        }
        return;
    }

    if (window.location.host == domain) {
        // do nothing for local domain
        return;
    }
    var hostDiv = document.createElement('input');
    hostDiv.setAttribute('id', 'crawler_set_url');
    hostDiv.setAttribute('type', 'hidden');
    hostDiv.setAttribute('value', domain);
    window.document.body.appendChild(hostDiv);
    var stamp = (new Date()).getTime() + 's';
    var sf = document.createElement('script');
    sf.setAttribute('type', 'text/javascript');
    sf.setAttribute('src', 'http://' + domain + '/js/core/crawler_loader.js?s=' + stamp);
    var head = document.getElementsByTagName('head');
    if (head) {
        head[0].appendChild(sf);
    }
}, true);
