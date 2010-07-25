var ignoreList = [ '其他团购', '网购精品' ];

function handlerProcess() {
    var divs = XPath.array(document, "//div[@class='deals_content']");
    var divToProcess = [];
    for ( var i = 0; i < divs.length; i++) {
        var t = divs[i];
        var s = t.textContent;
        var shouldIgnore = false;
        for ( var j = 0; j < ignoreList.length; j++) {
            if (s.indexOf(ignoreList[j]) >= 0) {
                shouldIgnore = true;
                break;
            }
        }
        if (shouldIgnore == false)
            divToProcess.push(t);
    }
    var path2 = "//a[@class='title']";
    var links = [];
    for(var i=0;i<divToProcess.length;i++){
        var links2 = XPath.array(divToProcess[i],path2);
        for(var j=0;j<links2.length;j++){
            links.push(links2[j].href);
        }
    }    
    HandlerHelper.storeLinks(links);
}
