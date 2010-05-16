function handlerProcess() {
    // CrUtil.removeFrames(document);
    // Crawler.log('processing url '+window.location.toString());
    var obj = {}, xpath, s, reg = HandlerHelper.getRegGroupFirstValue, t;
    s = XPath.single(document, "/html/body/div[@id='wrapper2']/div[1]/div[1]").textContent.toString().replace(
            /:\s*\n\s*/g, ':');
    var s2 = s;
    obj.size = reg(s, /面积:\s*(\S*)/);
    if (!parseInt(obj.size)) {
        delete obj.size;
    }
    obj.district5 = reg(s, /小区:\s*(.*)/);
    obj.address = reg(s, /地址:\s*(.*)/);
    obj.equipment = reg(s, /配置:\s*(.*)/);

    t = reg(s, /楼层:\s*(.*)/);
    obj.floor = reg(t, /([0-9]+)/);
    obj.totalFloor = reg(t, /总([0-9]+)层/);

    t = reg(s, /区域:\s*(.*\n.*)/);
    t = t.split('-');
    if (t.length == 1) {
        obj.district1 = t[0];
    } else if (t.length == 2) {
        obj.district1 = t[0];
        obj.district3 = t[1];
    } else {
        Crawler.error('house.detail - wrong number of parameter for 区域, raw text is : ' + t);
    }

    t = reg(s, /租金:\s*(.*)/);
    obj.price = reg(t, /(\S+)/);
    // obj.priceUnit = reg(t, /[\S+]\s+(\S+)[（|\(]?/);
    obj.priceUnit = '元/月';
    obj.paymentType = reg(t, /\((.*)\)/);
    obj.decoration = reg(s, /装修:\s*(.*)/);
    if (!obj.paymentType) {
        obj.paymentType = reg(t, /（(.*)）/);
    }

    t = reg(s2, /房型:\s*(.+)\s*/);
    if (!t)
        t = reg(s2, /户型:\s*(.+)\s*/);
    if (t) {
        t = t.split('-');
        if (t.length == 1) {
            t = t[0];
            if (t.indexOf('室') != -1 || t.indexOf('厅') != -1 || t.indexOf('卫') != -1) {
                obj.houseType = t;
            } else {
                Crawler.error('house.detail - wrong number of parameter for 户型, raw text is : ' + t);
            }
        } else if (t.length == 2) {
            obj.subRentalType = t[0];
            obj.houseType = t[1];
        } else {
            Crawler.error('house.detail - wrong number of parameter for 户型, raw text is : ' + t);
        }
    }

    // 详细描述
    xpath = "/html/body/div[@id='wrapper2']/div[@id='content']/div[2]//p";
    var arr = XPath.array(document, xpath), buf = [];
    for ( var i = 0; i < arr.length; i++) {
        var p = arr[i];
        if (!p.className || p.className.indexOf('text') >= 0) {
            buf[buf.length] = p.textContent.trim();
        }
    }
    obj.description2 = buf.join(' ');

    // 电话
    xpath = "/html/body/div[@id='wrapper2']/div[1]/div[2]/ul/li[2]";
    var node = XPath.single(document, xpath), tel = '';
    if (node.children && node.children.length != 0) {
        tel = CrUtil.encodeImage(node.children[0]);
    } else {
        tel = node.textContent;
    }
    obj.tel = tel;

    // GPS
    var lonlatUrl = null;
    if (window.write_frame) {
        lonlatUrl = window.write_frame.toString();
        // window.write_frame = function(){};
    } else {
        lonlatUrl = document.getElementById('traffic_iframe');
        if (lonlatUrl) {
            lonlatUrl = lonlatUrl.src;
        }
    }
    if (lonlatUrl) {
        var i = lonlatUrl.indexOf('latlng=');
        if (i != -1) {
            var j = lonlatUrl.indexOf('&', i);
            lonlatUrl = lonlatUrl.substring(i + 7, j).split(',');
            obj.la = lonlatUrl[0];
            obj.lo = lonlatUrl[1];
        } else {
            i = lonlatUrl.indexOf('lnglat=');
            var j = lonlatUrl.indexOf('&', i);
            lonlatUrl = lonlatUrl.substring(i + 7, j).split(',');
            obj.lo = lonlatUrl[0];
            obj.la = lonlatUrl[1];
        }
    }

    obj[CrGlobal.ParameterName_ObjectId] = CrGlobal.HouseObjectId;

    s = HandlerHelper.getRegGroupFirstValue(window.location.toString(), /.+\.ganji.com\/(fang[0-9]+)\/.*/)
    obj.rentalType = rentalTypeMap[s];

    s = HandlerHelper.getRegGroupFirstValue(window.location.toString(), /http:\/\/([a-z]+)\.ganji\.com/);
    obj.city = cityMap[s];

    obj.contact = XPath.single(document, "/html/body/div[@id='wrapper2']/div[1]/div[3]//span[1]").textContent;
    obj.description1 = XPath.single(document, "//div[@class='detail_title']/h1").textContent;
    for ( var p in obj) {
        if (obj[p]) {
            obj[p] = obj[p].trim();
        }
    }
    console.log(obj);
    HandlerHelper.postObject(obj, {
        action : 'Goto.Next.Link'
    });
}

var cityMap = {
    'sh' : '上海'
}

var rentalTypeMap = {
    'fang1' : '出租',
    'fang5' : '出售',
    'fang3' : '合租',
    'fang10' : '短租'
}
