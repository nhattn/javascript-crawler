function handlerProcess(){
    var info = {
        path:"/html/body/div[4]/div[1]//a",
        regex:[
           'books/.+/[0-9]+\.shtml$', 
           'showVipChapter'],
        prop:'href',
        volumePath:"/html/body/div[4]/div[1]//b",
        mapping:[
            {name:'name', op:'provided.node.textcontent'},     
            {name:'chapterUrl', op:'provided.node.property.regex', param1:'href'},
            {name:'totalChar', op:'provided.node.property.regex', param1:'title', param2:/字数：\s*([0-9]+)/i},
            {name:'updateTime', op:'provided.node.property.regex', param1:'title', param2:/更新日期：\s*([0-9][0-9][0-9][0-9]-[0-9][0-9]?-[0-9][0-9]? [0-9][0-9]?:[0-9][0-9]:[0-9][0-9])/i}           
        ],
        bookMapping:[
            {name:'name',   op:'xpath.node.textcontent', param1: "/html/body/div[4]/div[1]/div[@id='title']/h4/a"},
            {name:'author', op:'xpath.node.textcontent', param1:"/html/body/div[4]/div[1]/div[@id='title']/address/a[1]"}
        ]    
    };
    var book = HandlerHelper.parseChapterList(info);
//    Crawler.log(book);
    HandlerHelper.postBookChapters(book);
}
