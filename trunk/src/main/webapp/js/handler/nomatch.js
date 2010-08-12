// called when there is no match for url
function handlerProcess() {

}

function removeXPathNode(xpath) {
    var script = XPath.array(null, xpath);
    for ( var i = script.length - 1; i > -1; i--) {
        var s = script[i];
        s.parentNode.removeChild(s);
    }
}
function cleanUp() {
    removeXPathNode('//script');
    removeXPathNode('//style');
    removeXPathNode('//comment');
    removeXPathNode('//img');
//    removeXPathNode('//a');    
}

function normalizeLinks(){
    var obj = XPath.array(null, '//a');
    for ( var i = obj.length - 1; i > -1; i--) {
        var a = obj[i];
        a.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    }
}
normalizeLinks();
function cleanText(s) {
    if (!s || s == '')
        return '';
    s = CrUtil.removeNewLine(s);
    return CrUtil.killSpace(s);
}

function countSeperator(s) {
    if (!s)
        return 0;
    var tokens = [ ',', '\\.', '。', '，' ];
    var total = 0;
    for ( var i = 0; i < tokens.length; i++) {
        total = total + countChar(s, tokens[i])
    }
    return total;
}

function countChar(s, c) {
    var r = s.match(new RegExp(c, 'g'));
    if (r)
        return r.length;
    return 0;
}

function getPossibleMatchingDiv() {
    cleanUp();
    var sort = function(a, b) {
        return a.txtRatio - b.txtRatio;
    }
    var divs = document.getElementsByTagName('div'), arr = [];
    for ( var i = 0; i < divs.length; i++) {
        var div = divs[i], el = new Ext.Element(div);
        var w = el.getWidth(), h = el.getHeight(), txt = cleanText(div.textContent);
        var sep = countSeperator(txt), size = w * h;
        var txtRatio = txt.length / size;
        if (sep > 15 && w > 200 && h > 200) {
            arr.push( {
                txtRatio : txtRatio,
                div : div
            });
        }
    }
    arr.sort(sort);
    for(var i=0;i<arr.length;i++){
        console.log(arr[i].txtRatio);
        console.log(arr[i].div);
//        console.log(arr[i].div.textContent);        
    }
        
}

getPossibleMatchingDiv();

function showDivInfo(div) {
    var el = new Ext.Element(div);
    if (el.isVisible() == false || el.getWidth() < 150 || el.getHeight() < 150)
        return;
    var tx = div.textContent;
    if (!tx)
        tx = ' ';
    tx = CrUtil.removeNewLine(tx);
    tx = CrUtil.killSpace(tx);
    if (tx.length < 200 || countSeperator(tx) < 10)
        return;

    var s = [];
    s[s.length] = 'textLength:';
    s[s.length] = tx.length;
    s[s.length] = ', ';

    s[s.length] = 'htmlLength:';
    s[s.length] = div.innerHTML.length;
    s[s.length] = ', ';

    s[s.length] = 'char_per_unit:';
    s[s.length] = tx.length / (el.getWidth() * el.getHeight());
    s[s.length] = ', ';

    s[s.length] = 'text_per_html:';
    s[s.length] = tx.length / div.innerHTML.length;
    s[s.length] = ', ';

    s[s.length] = 'width:';
    s[s.length] = el.getWidth();
    s[s.length] = ', ';

    s[s.length] = 'height:';
    s[s.length] = el.getHeight();
    s[s.length] = ', ';
    //
    //    s[s.length] = 'left:';
    //    s[s.length] = el.getLeft();
    //    s[s.length] = ', ';
    //
    //    s[s.length] = 'top:';
    //    s[s.length] = el.getTop();
    //    s[s.length] = ', ';
    //
    //    s[s.length] = 'right:';
    //    s[s.length] = el.getRight();
    //    s[s.length] = ', ';
    //
    //    s[s.length] = 'bottom:';
    //    s[s.length] = el.getBottom();
    //    s[s.length] = ', ';

    s[s.length] = 'content: ';
    //    s[s.length] = tx; //tx.trim().substring(0, 100).trim();
    s[s.length] = tx.trim().substring(0, 100).trim();
    s[s.length] = ', ';

    s[s.length] = 'char count:';
    s[s.length] = countSeperator(tx);
    s[s.length] = ', ';

    console.log(s.join(' '));
    console.log(div.txtContent);
}

function getLeafDiv() {
    var divs = document.getElementsByTagName('div');
    for ( var i = 0; i < divs.length; i++) {
        var div = divs[i];
        //        if (XPath.array(div, './/div').length == 0) {
        //            var div = getPossibleMatch(div);
        //            if (div)
        showDivInfo(div);
        //        }
    }
}

function getPossibleMatch(div) {
    var r = null;
    var pcount = countSeperator(div.textContent);
    if (pcount > 10)
        return div;
    div = div.parentNode;
    for ( var i = 0; i < 3; i++) {
        if (div == null)
            return null;
        var count = countSeperator(div.textContent);
        var dif = count - pcount;
        //        console.log(dif+':'+pcount);
        if (dif > pcount && pcount > 10) {
            r = div;
            break;
        }
        pcount = count;
        div = div.parentNode;
    }
    // see if there is any p node
    if (r) {
        console.log(XPath.array(r, './/p').length);
        console.log(XPath.array(r, './/br').length);
        var ps = XPath.array(r, './/p').length + XPath.array(r, './/br').length;
        console.log(ps);
        if (ps < 2) {
            r = null;
        }
    }
    return r;
}
