function handlerProcess() {
    CrUtil.removeElementsByTagName('script');
    var s = XPath.single(document, "//div[@id='houseBaseInfo']").textContent.toString();
    var s1 = CrUtil.removeNewLine(XPath.single(document, "//div[@id='allInfo']").textContent.toString().replace('[访问小区主页]', ''));
    var s2 = CrUtil.removeNewLine(XPath.single(document, "//div[@id='communityInfo']").textContent.toString());
    var floors = HandlerHelper.getRegGroupFirstValue(s, /楼层：\s*(.*)/);
    var url = window.location.toString();
    var objInfo1 = [ {
        name : 'price',
        op : 'xpath.textcontent.regex',
        param1 : "//div[@id='houseBaseInfo']//span[contains(@class, 'price')]"
    }, {
        name : 'size',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /面积：\s*([0-9]+[\.[0-9]*]?)/
    }, {
        name : 'paymentType',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /付款条件：\s*(\S*)/
    }, {
        name : 'rentalLength',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /租期：\s*(\S*)/
    }, {
        name : 'downPayment',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /押金：\s*(\S*)/
    }, {
        name : 'subRentalType',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /类型：\s*(\S*)/
    }, {
        name : 'houseType',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /类型：\s*(\S*)/
    }, {
        name : 'yearBuild',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /建筑年代：\s*(\S*)/
    }, {
        name : 'decoration',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /装修：\s*(\S*)/
    }, {
        name : 'floor',
        op : 'xpath.text.regex',
        param1 : floors,
        param2 : /第([0-9]+)层/
    }, {
        name : 'totalFloor',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /总([0-9]+)层/
    }, {
        name : 'address',
        op : 'xpath.text.regex',
        param1 : s1,
        param2 : /房屋地址：\s*(\S*)\[?/
    }, {
        name : 'decoration',
        op : 'xpath.text.regex',
        param1 : s1,
        param2 : /装修程度：\s*(\S*)/
    }, {
        name : 'description1',
        op : 'xpath.textcontent.regex',
        param1 : "//div[@id='houseBaseInfo']//div[contains(@class,'houseName')]//h1[1]//text()[1]"
    }, {
        name : 'description2',
        op : 'xpath.textcontent.regex.if',
        param1 : "//div[@id='allInfo']/ul[2]/li/div"
    }, {
        name : 'contact',
        op : 'xpath.textcontent.regex',
        param1 : "//div[@id='houseBaseInfo']//div[contains(@class, 'contact-main')]/text()[2]"
    }, {
        name : 'isAgent',
        op : 'assign.value',
        param1 : '1'
    } ];

    var obj = HandlerHelper.parseObject(objInfo1);
    var priceUnit = XPath.single(document, "//div[@id='houseBaseInfo']");
    if (priceUnit && priceUnit.textContent) {
        priceUnit = priceUnit.textContent;
        if (priceUnit.indexOf('万元') != -1) {
            obj.priceUnit = '万';
        } else if (priceUnit.indexOf('元/月') != -1) {
            obj.priceUnit = '元/月';
        }
    }

    var equipments = XPath.array(document, "//div[contains(@class, 'infoItem')]//span[contains(@class, 'yes')]"), t = [];
    for ( var i = 0; i < equipments.length; i++) {
        t.push(equipments[i].textContent.trim());
    }
    obj.equipment = t.join(' ');

    var districts = CrUtil.getBetween(s1, '房屋区域：', '房屋地址：');
    if (districts && districts.split('-').length > 0) {
        districts = districts.split('-');
        if (districts[0])
            obj.district1 = districts[0];
        if (districts[1])
            obj.district3 = districts[1];
        if (districts[2])
            obj.district5 = districts[2];
    }

    s = HandlerHelper.getRegGroupFirstValue(url, /.*fang\/detail-([^-]+).*/)
    obj.rentalType = rentalTypeMap[s];

    s = HandlerHelper.getRegGroupFirstValue(url, /http:\/\/([a-z]+)\.koubei\.com/);
    obj.city = cityMap[s];

    var iframes = CrUtil.getFrameInfo();
    if (iframes && iframes.length > 0) {
        for ( var i = 0; i < iframes.length; i++) {
            var f = iframes[i];
            if (f.src && f.src.indexOf('centerx') > 0 && f.src.indexOf('centery') > 0) {
                var url = f.src;
                obj.lo = CrUtil.extractParameter(url, 'centerx');
                obj.la = CrUtil.extractParameter(url, 'centery');
                if (obj.lo && parseInt(obj.lo)) {
                    obj.lo = parseInt(obj.lo) / 100000;
                }
                if (obj.la && parseInt(obj.la)) {
                    obj.la = parseInt(obj.la) / 100000;
                }
            }
        }

    }
    CrUtil.trimAttributes(obj);

    if (!parseFloat(obj.price)) {
        delete obj.price;
    }
    if (!parseFloat(obj.size)) {
        delete obj.size;
    }
    if (!parseInt(obj.floor)) {
        delete obj.floor;
    }
    if (!parseInt(obj.totalFloor)) {
        delete obj.totalFloor;
    }

    obj[CrGlobal.ParameterName_ObjectId] = CrGlobal.HouseObjectId;

    var telImg = XPath.single(document, "//div[@id='houseBaseInfo']//span//img");
    if (telImg && telImg.src) {
        CrUtil.encodeImage2(telImg, function(r) {
            obj.tel = r;
            obj.telImageName = telImg.src + '&a.jpg';
            handlerProcess2(obj);
        });
    } else {
        handlerProcess2(obj);
    }
}

function handlerProcess2(obj) {
    //console.log(obj);
    HandlerHelper.postObject(obj, {
        action : 'Goto.Next.Link'
    });
}

var cityMap = {
    'shanghai' : '上海',
    'shenzhen' : '深圳',
    'beijing' : '北京',
    'tianjin' : '天津',
    'suzhou' : '苏州'
}

var rentalTypeMap = {
    'rent' : '出租',
    'sell' : '出售'
}
