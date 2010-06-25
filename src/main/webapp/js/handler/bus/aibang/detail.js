function handlerProcess() {
    var config = Config;
    var obj = {};
    obj.city = config.city;
    var stops = [];
    for ( var i = 0; i < config.stationMapXY.length; i++) {
        var info = config.stationMapXY[i];
        var gps = Vqp.transformer( [ info.x, info.y ]);
        var name = info.name;
        name = name.substring(0, name.lastIndexOf('第'));
        var stop = [ name, gps[0], gps[1] ].join('|');
        stops.push(stop);
    }
    obj.description = CrUtil.deleteTokens(XPath.stringv(null, "//p[@class='info1']/text()[2]"), '1-599路 ');
    obj.name = XPath.single(null, "//div[@class='title1']/span").textContent;
    CrUtil.trimAttributes(obj);
    obj[CrGlobal.ParameterName_ObjectId] = CrGlobal.BusObjectId;
    obj.stops = stops.join(',');
    HandlerHelper.postObject(obj);
    //    HandlerHelper.postObject(obj, {
    //        action : 'Run.Function',
    //        param1 : addReturnLink
    //    });
}

function addReturnLink() {
    var returnBus = XPath.single(document, "//div[@class='page']//div[@class='title1']//a[contains(text(),'返程')]").href;
    HandlerHelper.storeLinks( [ returnBus ]);
}