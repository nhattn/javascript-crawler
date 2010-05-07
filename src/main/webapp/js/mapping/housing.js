CrGlobal.handlerMapping = [    
    //ganji.com
    
    ////http://sh.ganji.com/fang1/   or http://sh.ganji.com/fang1/f384/
    {pattern:'http://sh\.ganji\.com/fang[0-9]+[/f[0-9]+/?]?$',                           file:'housing/ganji/list'},    
    ////http://sh.ganji.com/fang1/10050707_2950136.htm
    {pattern:'http://sh\.ganji\.com/fang[0-9]+/[^/|^\.]+\.html?$$',           file:'housing/ganji/detail'}
        
];