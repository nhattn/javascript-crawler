function main() {
    (function() {
        var w = window.wrappedJSObject || window;
        w.alert = function(msg) {
            console.log(msg);
        };
        w.confirm = function() {
            console.log('confirm disabled.');
            console.log(arguments);
        };
    })();
}

if (!document.xmlVersion) {
    var script = document.createElement('script');
    script.appendChild(document.createTextNode('(' + main + ')();'));
    document.documentElement.appendChild(script);
}