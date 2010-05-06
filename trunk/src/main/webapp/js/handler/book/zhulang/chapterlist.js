function handlerProcess(){
    var info = {
        path:"/html/body/div[@id='pagebody']/div/div[2]/div[1]/div[@id='content_left']/div[5]/div[@id='static']/div[5]//a",
        regex:[
           'book\.zhulang\.com/[0-9]+/[0-9]+\.html', 
           'http://vip\.zhulang\.com/v_read\.php'],
        prop:'href',
        volumePath:"/html/body/div[@id='pagebody']/div/div[2]/div[1]/div[@id='content_left']/div[5]/div[@id='static']/div[5]/ul/li[1]",
        mapping:[
            {name:'name', op:'provided.node.textcontent'},     
            {name:'chapterUrl', op:'provided.node.property.regex', param1:'href'},
            {name:'updateTime', op:'provided.node.property.regex', param1:'title', param2:/([0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]?)/i}           
        ],
        bookMapping:[
            {name:'name',   op:'xpath.node.textcontent', param1: "/html/body/div[@id='pagebody']/div/div[2]/div[1]/div[@id='content_left']/div[5]/div[@id='static']/div[1]/h1"},
            {name:'author', op:'xpath.node.textcontent.regex.group', 
                param1:"/html/body/div[@id='pagebody']/div/div[2]/div[1]/div[@id='content_left']/div[5]/div[@id='static']/div[3]",
                param2:/作者：\s*(\S+)/}
        ]    
    };
    var book = HandlerHelper.parseChapterList(info);
    Crawler.log(book);
    HandlerHelper.postBookChapters(book);
}
function handlerPreprocess(){
    var arr = XPath.array(null, "/html/body/div[@id='pagebody']/div/div[2]/div[1]/div[@id='content_left']/div[5]/div[@id='static']/div[5]/ul/li[1]");
    for(var i=0;i<arr.length;i++){
        var n = arr[i];
        var s = n.textContent;
        if(s.indexOf('VIP章节目录')>=0){
            n.parentNode.removeChild(n);
        }
        if(s.indexOf('[ 分卷阅读 ] ')>0){
            n.textContent = s.substring(0,s.indexOf('[ 分卷阅读 ]')); 
        }
    }
}