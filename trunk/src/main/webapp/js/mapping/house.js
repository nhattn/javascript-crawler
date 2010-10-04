/** use this to test url 
 new RegExp('', 'i').test(window.location.toString())
***/

CrGlobal.handlerMapping = [
/***************************
    ganji.com
****************************/

/*** housing ***********/
/*  http://sh.ganji.com/fang1/   or http://sh.ganji.com/fang1/f384/*/
{
    pattern : 'http://.+\.ganji\.com/fang[0-9]+[/f[0-9]+/?]?$',
    file : 'house/ganji/list'
},

/*http://sh.ganji.com/fang1/10050707_2950136.htm*/
{
    pattern : 'http://.+\.ganji\.com/fang[0-9]+/[^/|^\.]+\.html?$',
    file : 'house/ganji/detail'
},

/**** train ticket ***************/
/*  http://sh.ganji.com/piao/sell/ or http://sh.ganji.com/piao/sell/f32/ */
{
    pattern : 'http://.+\.ganji\.com/piao/sell',
    file : 'huochepiao/ganji/list'
},

/*http://sh.ganji.com/piao/10100109_1416957.htm*/
{
    pattern : 'http://.+\.ganji\.com/piao/[0-9]+_[0-9]+',
    file : 'huochepiao/ganji/detail'
},

/**************************
    koubei.com
******************************/
/* http://shanghai.koubei.com/fang/listrentout--count-9999---pageNo-1#tab-fangchan */
/* http://shanghai.koubei.com/fang/listsell--count-9999---pageNo-3#tab-fangchan */
{
    pattern : '.+\.koubei\.com/fang/list.+',
    file : 'house/koubei/list'
},

/* http://shanghai.koubei.com/fang/li-rent-all.html */
/* http://shanghai.koubei.com/fang/li-sell-all.html */
{
    pattern : '.+\.koubei\.com/fang/li-.+',
    file : 'house/koubei/list'
},

/* http://shanghai.koubei.com/fang/detail-rent-r64860de0ce154ebc85647b0643944183.html */
{
    pattern : '.+\.koubei\.com/fang/detail.+',
    file : 'house/koubei/detail'
},

/****************************
  8684.cn
 ****************************/
{
    pattern : '[a-z]+\.8684\.cn/x_[0-9a-z]+',
    file : 'bus/8684/detail'
},

/***************************
 aibang.com    
 ****************************/

/* http://beijing.bus.aibang.com/line-%E5%8C%97%E4%BA%AC-101%E7%94%B5%E8%BD%A6(%E7%BA%A2%E5%BA%99%E8%B7%AF%E5%8F%A3%E4%B8%9C-%E7%99%BE%E4%B8%87%E5%BA%84%E8%A5%BF%E5%8F%A3) */
{
    pattern : '[a-z]+\.bus\.aibang\.com/line.+',
    file : 'bus/aibang/detail'
},

/* http://bus.aibang.com/bus/beijing/line_1.html */
{
    pattern : 'bus\.aibang\.com/bus/[a-z]+/line_.+',
    file : 'bus/aibang/list'
},

/*****************************************************
 *   Groupon
 ******************************************************/

/* http://www.tuan800.com/beijing */
{
    pattern : 'tuan800\.com/[a-z]+$',
    file : 'groupon/tuan800/list'
},
/** http://www.tuan800.com/deal/beijingjin_5413 */
{
    pattern : 'tuan800\.com/deal/*',
    file : 'groupon/tuan800/detail'
},

{
    pattern : 'www\.google\.com\.hk/movies\?',
    file : 'film/google/detail'
},

/** http://www.weather.com.cn/html/weather/101011100.shtml **/
{
    pattern : 'www\.weather\.com\.cn/html/weather/[0-9]+\.shtml',
    file : 'weather/detail'
},
/** http://search.huochepiao.com/chaxun/resultc.asp?txtCheci=5816 **/
{
    pattern : 'search\.huochepiao\.com\/chaxun\/resultc\.asp\\?txtCheci=',
    file : 'huochepiao/detail'
},
/** sina t **/
{
    pattern : 't\.sina\.com.\cn/pub/news\?',
    file : 'sina/news'
}, {
    pattern : 't\.sina\.com\.cn/[0-9]+$',
    file : 'sina/user'
}, {
    pattern : 't\.sina\.com\.cn/[0-9a-zA-z]+$',
    file : 'sina/user'
},

/***
 * 58.com
 */

{
    pattern : '58\.com/huochepiao/[a-z]+.html',
    file : 'huochepiao/58/list'
}, {
    pattern : '58\.com/huochepiao/[a-z]+_[a-z]+.html',
    file : 'huochepiao/58/detail'
}

];