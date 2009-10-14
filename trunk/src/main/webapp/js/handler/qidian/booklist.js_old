function handlerProcess(){
    var metaInfo = {
            dataUrl : 'http://localhost:8080/crawler/service/crawler/booklist',
            path : "/html/body/form[@id='aspnetForm']/div[@id='mainContent']/div[3]/div[1]/div",
            start : 2,
            stop : -1,
            mapping : [
                {path:'div[2]/a[1]', attr:'textContent', type: 'book', name:'cat1'},
                {path:'div[2]/a[2]', attr:'textContent', type: 'book', name:'cat2'},
                {path:'div[3]/span/a', attr:'textContent', type: 'book', name:'name'},
                {path:'div[3]/span/a', attr:'href',  type: 'book', name:'nextLink'},
                {path:'div[4]', attr:'textContent',  type: 'book', name:'totalChar'},
                {path:'div[5]/a', attr:'textContent',  type: 'book', name:'author'},
                {path:'div[6]', attr:'textContent', type: 'book',  name:'updateTime'}                      
            ]
    }
    
    function parseMappedNode(node, mapping){
        var r = {};
        for(var i=0;i<mapping.length;i++){
            var m = mapping[i];
            var n = XPath.single(node, m.path);     
            if(m.type == 'book') {
                r[m.name] = n[m.attr];
            }
        }
        return r;
    }
    
    var info = metaInfo;    
    var result = XPath.array(document.documentElement, metaInfo.path);           
    if (result){
        var start = (info.start>0)?info.start:0;
        var stop =0;
        if(info.stop>0){
            stop = info.stop
        }else if(info.stop == 0){
            stop = result.length;
        }else if(info.stop <0){
            stop = result.length + info.stop;
        }
        var books = [];     
        for(var i=start;i<stop;i++){
            var node = result[i];
            var entry = parseMappedNode(node,info.mapping);
            books.push(entry);
        }
        var params = {data : Ext.util.JSON.encode(books)};
        //Crawler.log(params.data);        
        Crawler.postData(params, info.dataUrl);
    }    
}
