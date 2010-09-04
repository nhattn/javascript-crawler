function handlerProcess() {
    CrUtil.removeImages(['showphone']);
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
                obj.lng = CrUtil.extractParameter(url, 'centerx');
                obj.lat = CrUtil.extractParameter(url, 'centery');
                if (obj.lng && parseInt(obj.lng)) {
                    obj.lng = parseInt(obj.lng) / 100000;
                }
                if (obj.lat && parseInt(obj.lat)) {
                    obj.lat = parseInt(obj.lat) / 100000;
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
    createObject(obj);
    return;
    
    var houseImages = XPath.array(null, "//div[@id='picInfo']//img");
    var agentImages = XPath.single(null, "//div[@class='person']//img");
    if (!houseImages || houseImages.length == 0) {
        houseImages = [];
    }
    if (agentImages) {
        houseImages.push(agentImages);
        obj.hasAgentImage = true;
    }
    if (!houseImages || houseImages.length == 0) {
        createObject(obj);
    } else {
        processImage(obj, houseImages);
    }
}

function processImage(obj, imgs) {
    CrUtil.encodeImageArray(imgs, function(r) {
        var houseImageLen = (obj.hasAgentImage) ? (r.length - 1) : (r.length);
        var imageCount = 0;
        for ( var i = 0; i < houseImageLen; i++) {
            obj['imageData' + i] = r[i];
            obj['imageField' + i] = 'photo';
            obj['imageSuffix' + i] = 'jpg';
            imageCount++;
            if (i >= 2) {
                break;
            }
        }
        if (obj.hasAgentImage) {
            delete obj.hasAgentImage;
            obj['imageData' + imageCount] = r[r.length - 1];
            obj['imageField' + imageCount] = 'agentPhoto';
            obj['imageSuffix' + imageCount] = 'jpg';
            imageCount++;
        }
        obj.imageCount = imageCount;
        createObject(obj);
    });
}
Ext.Ajax.timeout = 60000;

function createObject(obj) {
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
    'suzhou' : '苏州',
    'chengdu' : '成都',
    'wuhan' : '武汉',
    'guangzhou' : '广州',
    'hangzhou' : '杭州',
    'xian' : '西安',
    'chongqing' : '重庆',
    'nanjing' : '南京',
    'fuzhou' : '福州'
}

var rentalTypeMap = {
    'rent' : '出租',
    'sell' : '出售'
}
