function handlerProcess(){   
	CrUtil.removeFrames(document);  
    var info={
        nextLinkPath: "/html/body/div[4]/div[1]/div[@id='infoBox2']/div[@id='box4']/div/div/span[@id='bt_1']/a",
        mapping : [        
    		{name:'subRentalType', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[4]/dl[1]/dd",
                param2: /(\S+) -/i
            },        
            {name:'price', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[1]/dl/dd/span"
            },
            {name:'priceUnit', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[1]/dl/dd/text()",
                param2: /(\S+)\//i
            },   
            {name:'size', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[1]/dl/dd/span"
            },
            {name:'houseType', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[4]/dl[1]/dd",
                param2: /- (\S+)/i
            },
            {name:'address', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[3]/dl[2]/dd"
            },
            {name:'district1', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[3]/dl[1]/dd/a[1]"
            },
	 		{name:'district3', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[3]/dl[1]/dd/a[2]"
            },
            {name:'district5', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[2]/dl[2]/dd"
            },
            {name:'contact', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[3]//span[1]"                        
            },
			{name:'description1', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/div[1]/h1"
            },			       
            {name:'equipment', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[6]/dl/dd"
            },
            {name:'decoration', op:'xpath.textcontent.regex',                         
                param1:"/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[4]/dl[2]/dd"
            },                                   
            {name:'url', op:'assign.value',  param1:window.location.toString()}
            /*,
            {name:'time', op:'xpath.textcontent.regex',                         
                param1:""
            },
            {name:'photo', op:'xpath.textcontent.regex',                         
                param1:""
            },         
			{name:'floor', op:'xpath.textcontent.regex',                         
                param1:""
            },   
            {name:'isAgent', op:'xpath.textcontent.regex',                         
                param1:""
            },*/            
        ]
    };
    var obj = HandlerHelper.parseObject(info.mapping);
    
    var paymentTypePath = "/html/body/div[@id='wrapper2']/div[1]/div[1]/ul[1]/li[1]/dl/dd";    			           
    var s = HandlerHelper.extractFromXpathNodeText(paymentTypePath);
    obj.paymentType = HandlerHelper.getRegGroupFirstValue(s, /[\(|£¨](\S+)[\)|£©]/);

	var desc2Path = "/html/body/div[@id='wrapper2']/div[@id='content']/div[2]//p";
	var arr = XPath.array(document, desc2Path), buf=[];	
	for(var i=0;i<arr.length;i++){
		var p = arr[i];				
		if(!p.className || p.className.indexOf('text')>=0){			
			buf[buf.length] = p.textContent.trim();
		} 		
	}
	obj.description2 = buf.join(' ');
	
	var telPath = "/html/body/div[@id='wrapper2']/div[1]/div[2]/ul/li[2]";
	var node = XPath.single(document, telPath), tel='';
	if(node.children && node.children.length !=0){
		tel = CrUtil.encodeImage(node.children[0]);
	}else{	
		tel = node.textContent;
	}
	obj.tel = tel;		
	obj[CrGlobal.ParameterName_AppId] = CrGlobal.HousingAppId;
    console.log(obj);
    
    HandlerHelper.postObject(obj);
}
