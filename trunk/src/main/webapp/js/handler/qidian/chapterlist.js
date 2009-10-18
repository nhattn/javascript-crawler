function handlerProcess(){
    var info = {
        path:"/html/body/form[@id='form1']/center/div[@id='content']//a",
        regex:[
           'www\.qidian\.com/BookReader/[0-9]+,[0-9]+\.aspx', 
           'vipreader\.qidian\.com/BookReader/vip,[0-9]+,[0-9]+\.aspx'],
        prop:'href',
        volumePath:"/html/body/form[@id='form1']/center/div[@id='content']/div/strong",
        mapping:[
            {name:'name', op:'provided.node.textcontent'},     
            {name:'chapterUrl', op:'provided.node.property.regex', param1:'href'},
            {name:'totalChar', op:'provided.node.property.regex', param1:'title', param2:/字数：\s*([0-9]+)/i},
            {name:'updateTime', op:'provided.node.property.regex', param1:'title', param2:/更新时间：\s*([0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]? [0-9][0-9]?:[0-9][0-9]:[0-9][0-9])/i}           
        ],
        bookMapping:[
            {name:'name',   op:'xpath.node.textcontent', param1: "/html/body/form[@id='form1']/center/b/h1"},
            {name:'author', op:'xpath.node.textcontent', param1:"/html/body/form[@id='form1']/center/table/tbody/tr/td[1]/a/b"}
        ]    
    };
    var book = HandlerHelper.parseChapterList(info);
    Crawler.log(book);
    HandlerHelper.postBookChapters(book);
}
