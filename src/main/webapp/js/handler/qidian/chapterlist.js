function handlerProcess(){
    var info = {
        path:"/html/body/form[@id='form1']/center/div[@id='content']//a",
        regex: 'http://www\.qidian\.com/BookReader/[0-9]+,[0-9]+\.aspx',
        prop:'href',
        volumePath:"",
        mapping:[
            {name:'name', op:'provided.node.textcontent'},     
            //字数：3148  更新时间：2009-9-26 4:00:34
            {name:'chapterUrl', op:'provided.node.property.regex', param1:'href'},
            {name:'totalChar', op:'provided.node.property.regex', param1:'title', param2:/字数：\s*([0-9]+)/i},
            {name:'updateTime', op:'provided.node.property.regex', param1:'title', param2:/更新时间：\s*([0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]? [0-9][0-9]?:[0-9][0-9]:[0-9][0-9])/i}           
        ]
    };
    
    var arr = XPath.array(null, info.path);    
    var links = [], chapters=[], regex = new RegExp(info.regex,'i'), prop = info.prop, mapping = info.mapping;
    for(var i=0;i<arr.length;i++){                
        var n = arr[i];
        var v = n[prop];
        if(regex){
            if(regex.test(v)==true){
                links.push(n);
            }
        }else{
            links.push(n);
        }
    }
    for(var i=0;i<links.length;i++){
        var n = links[i];        
        var chapter = parseChapterList(n, mapping);
        chapters.push(chapter);
    }    
    Crawler.log(chapters);
}

function parseChapterList(node, mapping){    
    var chapter = {};
    for(var i=0;i<mapping.length;i++){
        var m = mapping[i];
        switch(m.op){
        case 'provided.node.textcontent':            
            chapter[m.name] = node.textContent;
            break;
        case 'provided.node.property.regex':
            var v = node[m.param1];
            if(m.param2){
                chapter[m.name] = HandlerHelper.getRegGroup(v, m.param2);
            }else{
                chapter[m.name] = v;
            }
            break;
        case 'assign.value':
            chapter[m.name] = m.param1;
            break;
        default:
            Crawler.error('wrong op'+m.op);
        }        
    }
    return chapter;
}

            
