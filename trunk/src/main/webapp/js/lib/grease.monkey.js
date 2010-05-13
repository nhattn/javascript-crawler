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