CrGlobal.handlerMapping = [    
    /***************************
        ganji.com
    ****************************/
    
    ////http://sh.ganji.com/fang1/   or http://sh.ganji.com/fang1/f384/
    {pattern:'http://sh\.ganji\.com/fang[0-9]+[/f[0-9]+/?]?$',                           file:'house/ganji/list'},
        
    ////http://sh.ganji.com/fang1/10050707_2950136.htm
    {pattern:'http://sh\.ganji\.com/fang[0-9]+/[^/|^\.]+\.html?$$',           file:'house/ganji/detail'},
    
    
    
    
    /**************************
        koubei.com
    ******************************/
   // http://shanghai.koubei.com/fang/listrentout--count-9999---pageNo-1#tab-fangchan
   {pattern:'shanghai\.koubei\.com/fang/list.+',     file:'house/koubei/list'},
   
   //http://shanghai.koubei.com/fang/detail-rent-r64860de0ce154ebc85647b0643944183.html
   {pattern:'shanghai\.koubei\.com/fang/detail.+',     file:'house/koubei/detail'}
            
];