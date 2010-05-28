function handlerProcess() {
	var s1 = CrUtil.removeNewLine(XPath.single(document,
			"/html/body/div[@id='wrapper2']/div[1]/div[1]").textContent).replace('(', ' (').replace('（', ' (').replace('）', ')').replace('function', '    \nfunction');    
	s1 = CrUtil.deleteTokens(s1,['短信发送至手机', '收藏该房源']);	
    var fangType = HandlerHelper.getRegGroupFirstValue(window.location.toString(), /.+\.ganji.com\/(fang[0-9]+)\/.*/)
        
    var objInfo = [
        {name: 'size', op:'xpath.text.regex', param1:s1, param2:/面积:\s*(\S*)/},
        {name: 'address', op:'xpath.text.regex', param1:s1, param2:/地址:\s*(\S*)/},
        {name: 'equipment', op:'xpath.text.regex', param1:s1, param2:/配置:\s*([\S+\ ?]+)/},
        {name: 'floor', op:'xpath.text.regex', param1:s1, param2:/第([0-9]+)层/},
        {name: 'totalFloor', op:'xpath.text.regex', param1:s1, param2:/总([0-9]+)层/},        
        {name: 'decoration', op:'xpath.text.regex', param1:s1, param2:/装修:\s*(\S*)/},
        {name: 'contact', op:'xpath.textcontent.regex', param1:"/html/body/div[@id='wrapper2']/div[1]/div[3]//span[1]"},
        {name: 'description1', op:'xpath.textcontent.regex', param1:"//div[@class='detail_title']/h1"}
    ];           
        
    if(fangType == 'fang1'|| fangType == 'fang3'){
        objInfo.push({name: 'district5', op:'xpath.text.regex', param1:s1, param2:/小区:\s*(\S*)/});
        objInfo.push({name: 'price', op:'xpath.text.regex', param1:s1, param2:/租金:\s*(\S+)/});
        objInfo.push({name: 'priceUnit', op:'xpath.text.regex', param1:s1, param2:/租金:\s*\S+\s+(\S+)/});
        objInfo.push({name: 'paymentType', op:'xpath.text.regex', param1:s1, param2:/租金:\s*\S+\s+\S+\s*\((\S+)\)/});
    }else if(fangType == 'fang5'){        
        objInfo.push({name: 'price', op:'xpath.text.regex', param1:s1, param2:/售价:\s*(\S+)/});
        objInfo.push({name: 'priceUnit', op:'xpath.text.regex', param1:s1, param2:/售价:\s*\S+\s+(\S+)/});        
    }
    
    var obj = HandlerHelper.parseObject(objInfo);
    
    if (!parseInt(obj.size)) {
        delete obj.size;
    }
    if (!parseInt(obj.price)) {
        delete obj.size;
    }
    
    if(fangType == 'fang1'|| fangType == 'fang3'){
    	var district = HandlerHelper.getRegGroupFirstValue(s1, /区域:\s*(.*)地址:/);
    	district = district.split('-');
    	if (district.length == 1) {
    		obj.district1 = district[0];
    	} else if (district.length == 2) {
    		obj.district1 = district[0];
    		obj.district3 = district[1];
    	} else {
    		Crawler.error('house.detail - wrong number of parameter for distrit, raw text is : ' + t);
    	}    
    }else if(fangType == 'fang5'){
        var district = HandlerHelper.getRegGroupFirstValue(s1, /小区:\s*(.*)地址:/);        
        district = district.split('-');
        if (district.length == 1) {
            obj.district1 = district[0];
        } else if (district.length == 2) {
            obj.district3 = district[0];
            obj.district5 = district[1];
        } else {
            Crawler.error('house.detail - wrong number of parameter for distrit, raw text is : ' + t);
        }
    }


	var houseType = CrUtil.getBetween(s1, '型:', ':');
	if (houseType && houseType.length>2) {
		houseType = houseType.substring(0, houseType.length-2);
		houseType = houseType.split('-');
		if (houseType.length == 1) {
			houseType = houseType[0];
			if (houseType.indexOf('室') != -1 || houseType.indexOf('厅') != -1
					|| houseType.indexOf('卫') != -1) {
				obj.houseType = houseType;
			} else {
				Crawler.error('house.detail.1 - wrong number of parameter for HouseType, raw text is : ' + houseType);
			}
		} else if (houseType.length == 2) {
			obj.subRentalType = houseType[0];
			obj.houseType = houseType[1];
		} else {
		    obj.subRentalType = houseType[0];
            obj.houseType = houseType[1];
			//Crawler.error('house.detail.2 - wrong number of parameter for HouseType, raw text is : ' + houseType);
		}
	}
	
	// descripion2
	var arr = XPath.array(document, "/html/body/div[@id='wrapper2']/div[@id='content']/div[2]//p"), buf = [];
    for ( var i = 0; i < arr.length; i++) {
        var p = arr[i];
        if (!p.className || p.className.indexOf('text') >= 0) {
            buf[buf.length] = p.textContent.trim();
        }
    }
    var txt = buf.join(' ');
    if (!txt || txt.trim().length == 0) {
        txt = XPath.single(document, "/html/body/div[@id='wrapper2']/div[@id='content']/div[2]//div[@class='tuiguang_text']").textContent;
    }
    obj.description2 = txt;
    
    
    // tel
    xpath = "/html/body/div[@id='wrapper2']/div[1]/div[2]/ul/li[2]";
    var node = XPath.single(document, xpath), tel = '';
    if (node.children && node.children.length != 0) {
        tel = CrUtil.encodeImage(node.children[0]);
        obj.telImageName = node.children[0].src;
    } else {
        tel = node.textContent;
    }
    obj.tel = tel;    
    
	// GPS
	var lonlatUrl = null;
	if (window.write_frame) {
		lonlatUrl = window.write_frame.toString();
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
	
    obj.rentalType = rentalTypeMap[fangType];
    
    var s = HandlerHelper.getRegGroupFirstValue(window.location.toString(), /http:\/\/([a-z]+)\.ganji\.com/);
    obj.city = cityMap[s];
    
    CrUtil.trimAttributes(obj);   
    if (!parseInt(obj.size)) {
        delete obj.size;
    }    
    
    obj[CrGlobal.ParameterName_ObjectId] = CrGlobal.HouseObjectId;
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
