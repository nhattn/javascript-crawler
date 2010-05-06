function handlerProcess(){
    var info = {
        path:"/html/body/table/tbody//a",
        regex:['http://www\.tszw\.com/[0-9]+/[0-9]+/[0-9]+\.html'],
        prop:'href',
        volumePath:"/html/body/form[@id='form1']/center/div[@id='content']/div/strong",
        mapping:[
            {name:'name', op:'provided.node.textcontent'},     
            {name:'chapterUrl', op:'provided.node.property.regex', param1:'href'}          
        ],
        bookMapping:[
            {name:'name',   op:'xpath.node.textcontent', param1: "/html/body/div[@id='title']/h1"},
            {name:'author', op:'xpath.node.textcontent.regex.group', param1:"/html/body/div[@id='xiaoshuo']", param2:/小说作者：\s*(\S+)\s/}
        ]
    };
    var book = HandlerHelper.parseChapterList(info);
    HandlerHelper.postBookChapters(book);
}

