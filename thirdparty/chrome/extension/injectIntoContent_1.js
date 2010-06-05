function removeFrames() {
    if (!window.cr_iframes) {
        window.cr_iframes = [];
    }
    var iframes = window.cr_iframes;
    var d = document.getElementsByTagName('iframe');
    for ( var i = 0; i < d.length; i++) {
        var f = d[i];
        if (!f.src)
            continue;
        iframes.push( {
            id : f.id,
            name : f.name,
            src : f.src
        });
        f.src = null;
        f.parentNode.removeChild(f);
    }
}
removeFrames();
window._crifr = setInterval(function() {
    removeFrames(document);
}, 80);
