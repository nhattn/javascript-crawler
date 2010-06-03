var crawler_info = {
    dataPoint:[
        {name:'dp_bookList', type:'xpath', resultType:'array', value:'/html/body/form[@id='aspnetForm']/div[@id='mainContent']//a'}
    ],
    object: [
        {name:'obj_bookLinks', value: {}},        
    ],    
    mapping:[
       [
        {dataPoint:'dp_bookList', op:{name:'get.property', value:'href'}},
        {name:'links', object:'obj_bookLinks', op:{name:'match.xpath', value:'http://www\.qidian\.com/Book/[0-9]+\.aspx'}}
       ]
    ]    
}

function handlerProcess(){
    if(typeof crawler_info != undefined){
        autoProcess(info);
    }
}

function autoProcess(info){
    
}

function initDataPoint(dataPoint){
    
}

function processMappingEntry(entry){
    
}