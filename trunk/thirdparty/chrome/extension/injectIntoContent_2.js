if (window._crifr) {
    //clearInterval(window._crifr);
    //delete window._crifr;
    // keeps going on
}
if (window.cr_iframes && window.cr_iframes.length > 0) {
    var fs = window.cr_iframes;
    for ( var i = 0; i < fs.length; i++) {
        var f = fs[i];
        var value = JSON.stringify( {
            type : 'hidden',
            name : f.name,
            id : f.id,
            src : f.src
        });
        Content.createElement('input', document.body, {
            value : value,
            id : ('crframe__' + i),
            type : 'hidden'
        });
    }
}