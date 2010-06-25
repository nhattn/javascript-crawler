function handlerProcess() {
    var routes = kresult.split('|');
    for ( var i = 0; i < routes.length; i++) {
        var s = routes[i];
        if (s && s.trim().length != 0) {
            processRoute(eval(routes[i]));
        }
    }
}

function processRoute(r) {
    var obj = {};
    obj.name = r[0];
    obj.busSchedule = r[2];
    obj.busDescription = r[3];
    obj.busCompany = r[4];
    obj.busTotalDistance = r[5];
    obj.gps = cq.vp(r[6]);
    var stops = [];
    for ( var i = 7; i < r.length; i++) {
        stops.push(getBusStopInfo(r[i]));
    }
    obj.stops = stops;
    console.log(obj);
}

function getBusStopInfo(r) {
    var stop = {};
    r = r.split('|');
    stop.gps = cq.vp(r[0]);
    stop.name = r[1];
    if (r.length > 2) {
        r = r[2].split(',');
        if (r.length > 0) {
            var sameStop = [];
            for ( var i = 0; i < r.length; i++) {
                var ostop = r[i].split('@');
                sameStop.push(ostop[0]);
            }
            stop.sameStop = sameStop;
        }
    }
    return stop;
}
