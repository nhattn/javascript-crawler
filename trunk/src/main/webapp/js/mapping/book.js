CrGlobal.handlerMapping = [
    // qidian.com                      
    {pattern:'http://[^\.]+\.qidian\.com/book/bookStore\.aspx',         file:'book/qidian/booklist'},
    {pattern:'http://www\.qidian\.com/Book/[^\.]*\.aspx',               file:'book/qidian/bookcover'},
    {pattern:'http://www\.qidian\.com/BookReader/[0-9]*\.aspx',         file:'book/qidian/chapterlist'},
    {pattern:'http://www\.qidian\.com/BookReader/[0-9]*,[0-9]*\.aspx',  file:'book/qidian/chapter'},

    //tszw.com
    {pattern:'http://www\.tszw\.com/toplistlastupdate/[0-9]+/[0-9]+.html',         file:'book/tszw/booklist'},
    {pattern:'http://www\.tszw\.com/Article_[0-9]+\.html',                         file:'book/tszw/bookcover'},
    {pattern:'http://www\.tszw\.com/[0-9]+/[0-9]+[/index\.html]?',                 file:'book/tszw/chapterlist'},
        
    //17k.com    
    {pattern:'http://all\.17k\.com/[0-9|_]+\.html',         file:'book/www17k/booklist'},
    {pattern:'http://[^\.]+\.17k\.com/book/[0-9]+\.html',    file:'book/www17k/bookcover'},
    {pattern:'http://[^\.]+\.17k\.com/list/[0-9]+\.html',        file:'book/www17k/chapterlist'},
    
    //zhulang.com    
    {pattern:'http://s\.zhulang\.com/w_book_list\.php',        file:'book/zhulang/booklist'},
    {pattern:'http://www\.zhulang\.com/[0-9]+/index\.html',   file:'book/zhulang/bookcover'},
    {pattern:'http://book\.zhulang.\com/[0-9]+/index\.html',   file:'book/zhulang/chapterlist'},
    
    //readnovel.com
    {pattern:'http://www\.readnovel\.com/all\.html',                 file:'book/readnovel/alllist'},
    {pattern:'http://www\.readnovel\.com/archive/[0-9]+/[0-9]+',     file:'book/readnovel/monthlist'},
    {pattern:'http://www\.readnovel\.com/partlist/[0-9]+',           file:'book/readnovel/bookcover'}    
];
