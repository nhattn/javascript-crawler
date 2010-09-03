function getFilms() {
    var r = [];
    var theaters = XPath.array(null, '//div[@class="theater"]');
    if (!theaters || theaters.length == 0) {
        return null;
    }
    for ( var i = 0; i < theaters.length; i++) {
        var theater = theaters[i];
        var movies = XPath.array(theater, './/div[@class="movie"]');
        var theaterName = nodeText(XPath.single(theater, './/div//h2'));
        var theaterAddress = nodeText(XPath.single(theater, './/div[@class="info"]'));
        var filmList = [];
        var theaterObject = {
            theaterName : theaterName,
            address : theaterAddress,
            city : getCity(),
            films : filmList
        };
        r[r.length] = theaterObject;
        for ( var j = 0; j < movies.length; j++) {
            var movie = movies[j];
            var name = nodeText(XPath.single(movie, './/div[@class="name"]'));
            var info = nodeText(XPath.single(movie, './/span[@class="info"]'));
            var times = nodeText(XPath.single(movie, './/div[@class="times"]'));
            filmList[filmList.length] = {
                name : name,
                description : info,
                showTime : times,
                showDate : getDate()
            };
        }
    }
    return r;
}

function getCity() {
    return decodeURIComponent(CrUtil.extractParameter('near'))
}

function getDate() {
    var offset = parseInt(CrUtil.extractParameter('date'));
    var date = new Date();
    date.setDate(date.getDate() + offset);
    return CrUtil.dateString(date);
}

function nodeText(node) {
    return (node == null) ? '' : node.innerText;
}

function handlerProcess() {
    var r = getFilms();
    if (r == null) {
        Crawler.nextLink();
    } else {
        r = {
            objectid : 'Film',
            data : r
        };
        var data = {
            format : 'json',
            'jsondata' : Ext.util.JSON.encode(r),
            skipUrlCheck : true
        };
        HandlerHelper.postObject(data, {
            action : 'Click.XPath.Node',
            param1 : '//div[@id="navbar"]//img[contains(@src,"next")]'
        });
    }
}
