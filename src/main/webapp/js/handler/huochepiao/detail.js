function handlerProcess() {
    var obj = getInfo();
    obj.lines = getLines();
    if (obj.lines && obj.lines.length > 0 && obj.lines[0].arriveAt == '始发站') {
        obj.lines[0].arriveAt = obj.leaveAt;
    }
    r = {
        objectid : 'Train',
        data : obj
    };
    //    console.log(r);
    var data = {
        format : 'json',
        'jsondata' : Ext.util.JSON.encode(r),
        skipUrlCheck : true
    };
    HandlerHelper.postObject(data);
}
function rep(x) {
    var r;
    if (typeof x == 'string')
        r = x;
    else if (x.textContent)
        r = x.textContent;
    else if (x == null || x == undefined)
        r = '';
    else
        throw 'error object in rep:' + x;

    r = r.trim();
    if (r == '-')
        r = 0;
    return r;

}
function rep2(x) {
    return x.replace(/\-/g, '0');
}

var hasOneTwoDeng = false;
function getInfo() {
    var r = {}, node = XPath.single, a, b;
    r.trainNum = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[1]/td[3]/a").textContent;
    r.name = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[1]/td[7]").textContent;
    r.totalTime = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[1]/td[5]").textContent;
    r.origin = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[2]/td[2]").textContent;
    r.dest = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[2]/td[4]").textContent;
    r.leaveAt = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[3]/td[2]").textContent;
    r.arriveAt = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[3]/td[4]").textContent;
    r.type = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[4]/td[2]").textContent;
    r.totalMile = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[4]/td[4]").textContent;
    a = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[2]/td[6]").textContent.trim();
    b = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[2]/td[8]").textContent.trim();
    r.zuo = rep(a) + '/' + rep(b);
    r.yingwo = rep2(node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[3]/td[6]").textContent);
    r.ruanwo = rep2(node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[4]/td[6]").textContent + '/'
            + node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[4]/td[8]").textContent);

    a = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[5]/td[3]");
    b = node(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[1]/tbody/tr[5]/td[5]");
    if (a && b) {
        hasOneTwoDeng = true;
        r.deng = rep2(a.textContent + '/' + b.textContent);
    } else {
        r.deng = '0/0';
    }
    CrUtil.trimAttributes(r);
    return r;
}

function getLines() {
    var table = XPath.single(null, "/html/body/table[3]/tbody/tr/td[1]/center/table[2]");
    var rows = table.rows, stations = [];
    for ( var i = 1; i < rows.length; i++) {
        var cells = rows[i].cells, obj = {};
        obj.seq = cells[1].textContent;
        obj.name = cells[2].textContent;
        obj.arriveAt = cells[4].textContent;
        obj.leaveAt = cells[5].textContent;
        obj.totalTime = cells[6].textContent;
        obj.totalMile = cells[7].textContent;
        if (hasOneTwoDeng == false) {
            obj.zuo = rep2(cells[8].textContent.trim() + '/' + (cells[9].textContent + '').trim());
            obj.yingwo = cells[10].textContent;
            obj.ruanwo = cells[11].textContent;
            obj.deng = '0/0';
        } else {
            obj.yingwo = cells[8].textContent;
            obj.ruanwo = cells[9].textContent;
            obj.deng = cells[10].textContent;
            obj.zuo = '0/0';
        }
        CrUtil.trimAttributes(obj);
        stations.push(obj);
    }
    return stations;
}
