function handlerProcess() {
    if (isTimeExpired() == true) {
        Crawler.log('House list date expried, will stop process');
        Crawler.action( {
            action : 'Goto.Next.Link'
        });
        return;
    }
    var info = {
        dataUrl : Crawler.serverUrl + '/service/link',
        path : "/html/body/div[@id='wrapper']/div[@id='content']/div[3]/dl/dt/a",
        nextPagePath : "/html/body/div[@id='wrapper']/div[@id='content']/div[3]/div/ul/li/a[@class='c']/parent::*//following-sibling::*[1]/a",
        regex : new RegExp('http://.+\.ganji\.com/fang[0-9]+', 'i')
    };

    var links = HandlerHelper.getMatchLinks(info.path, info.regex);
    //    console.log(links);
    HandlerHelper.storeLinks(links, {
        action : 'Goto.XPath.Link.Href',
        param1 : info.nextPagePath
    });
}

function isTimeExpired() {
    var arr = XPath.array(document, "//span[contains(@class,'time')]"), now = new Date();
    var maxDifference = CrGlobal.HouseListMaxDifference[CrUtil.getShortestDomain(window.location.host)];
    var errorCount = 0;
    for ( var i = 0; i < arr.length; i++) {
        var s = arr[i].textContent;
        if (!s || s.trim().length == 0) {
            continue;
        }
        var oDate = CrUtil.guessTime(s.trim());
        if (!oDate) {
            CrUtil.log('Can not parse date string : ' + s);
            errorCount++;
            if (errorCount > 10) {
                Crawler.attention('Too many error times in list, the last one is: ' + s);
                return true;
            }
        } else if ((now.getTime() - oDate.getTime()) > maxDifference) {
            return true;
        }
    }
    return false;
}