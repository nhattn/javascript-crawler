XPath = {
    iterator : function(node, path) {
        if (!node)
            node = document.documentElement;
        return document.evaluate(path, node, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
    },
    array : function(node, path) {
        var r = [];
        var nodes = null;
        try {
            nodes = XPath.iterator(node, path);
        } catch (e) {
//            Crawler.warn('Wrong xpath:' + e + ':' + path);
            return null;
        }
        if (nodes) {
            var n = nodes.iterateNext();
            while (n) {
                r.push(n);
                n = nodes.iterateNext();
            }
        }
        return r;
    },
    single : function(node, path, type) {
        if (typeof type == 'undefined') {
            type = XPathResult.FIRST_ORDERED_NODE_TYPE;
        }
        if (!node) {
            node = document.documentElement;
        }
        var r = null;
        try {
            r = document.evaluate(path, node, null, type, null);
        } catch (e) {
//            Crawler.warn('Wrong xpath:' + e + ':' + path);
            return null;
        }
        if (r) {
            if (type == XPathResult.FIRST_ORDERED_NODE_TYPE && r.singleNodeValue) {
                return r.singleNodeValue;
            } else if (type == XPathResult.STRING_TYPE) {
                return r;
            }
        }
//        Crawler.warn('Wrong xpath:' + path);
        return null;
    },

    stringv : function(node, path) {
        return XPath.single(node, path, XPathResult.STRING_TYPE).stringValue;
    }
}
