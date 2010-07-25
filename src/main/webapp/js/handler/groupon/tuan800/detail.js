var mapping = {};
function handlerProcess() {
    var body = document.body.textContent;

    var objInfo = [ {
        name : 'city',
        op : 'get.in.between',
        param1 : body,
        param2 : '当前城市【',
        param3 : '】'
    }, {
        name : 'website',
        op : 'xpath.textcontent.regex',
        param1 : "//div[@class='site_link']"
    }, {
        name : 'title',
        op : 'xpath.textcontent.regex',
        param1 : "//div[@class='title_link']//a"
    }, {
        name : 'price',
        op : 'xpath.textcontent.regex',
        param1 : "//div[@class='deal_info']//span[@class='price']"
    }, {
        name : 'oldPrice',
        op : 'xpath.textcontent.regex',
        param1 : "//div[@class='deal_info']//div[@class='price_infos']//span[1]"
    }, {
        name : 'description',
        op : 'xpath.textcontent.regex',
        param1 : "//div[@class='deal_info_text'][2]"
    } ];

    var obj = HandlerHelper.parseObject(objInfo);
    obj.price = CrUtil.extractNumber(obj.price);
    obj.oldPrice = CrUtil.extractNumber(obj.oldPrice);
    obj.url = window.location.toString();
    obj.objectid = 'GroupBuy';
    obj.title = CrUtil.deleteTokens(obj.title, '今日团购：');
    CrUtil.trimAttributes(obj);
    obj.websiteUrl = mapping[obj.website];
    if(!obj.websiteUrl){
        Crawler.attention('Groupbuy, website is not mapped:'+obj.website);
    }
    var addr = listAddress();
    if (addr.length == 0) {
        HandlerHelper.postObject(obj, {
            action : 'Goto.Next.Link'
        });
    } else {
        var objlist = [];
        for ( var i = 0; i < addr.length; i++) {
            var nobj = {};
            Ext.apply(nobj, addr[i]);
            Ext.apply(nobj, obj);
            nobj.skipUrlCheck = 'true';
            objlist.push(nobj);
        }
        addObject(0, objlist);
    }
}

function addObject(current, objlist) {
    var action = 'Run.Function';
    if (current == objlist.length - 1) {
        action = 'Goto.Next.Link';
    }

    HandlerHelper.postObject(objlist[current], {
        action : action,
        param1 : function() {
            addObject(current + 1, objlist);
        }
    });
}

function getGps() {
    var body = document.body.textContent;
    var startIndex = body.indexOf('addGoogleMarker(google_map,');
    var r = [];
    while (startIndex > 0) {
        var gps = '(' + CrUtil.getBetween(body, 'addGoogleMarker(google_map,', '});', startIndex) + '})';
        var gps = eval(gps);
        var obj = {};
        obj.la = gps.lat;
        obj.lo = gps.lng;
        r.push(obj);
        startIndex = body.indexOf('addGoogleMarker(google_map,', startIndex + 1);
    }
    return r;
}
function listAddress() {
    var infos = XPath.array(document, "//dl[@class='shop_basic_info']");
    if (!infos || infos.length == 0) {
        return [];
    }
    var addr = [];
    var gps = getGps(), gpsCount = 0;
    for ( var i = 0; i < infos.length; i++) {
        var s = infos[i].textContent, obj = {};
        if (infos[i].innerHTML.indexOf('showGmapInfo') > 0 || infos.length == 1) {
            if (gps && gps.length > gpsCount) {
                var pos = gps[gpsCount++];
                obj.la = pos.la;
                obj.lo = pos.lo;
            }
        }
        obj.tel = CrUtil.getBetween(s, '电话：', '\n  ');
        obj.address = CrUtil.getBetween(s, '详细地址：', '\n  ');
        CrUtil.trimAttributes(obj);
        addr.push(obj);
    }
    return addr;
}





var rawmapping = 
    [﻿['糯米团','http://www.nuomi.com'],
 ['美团','http://www.meituan.com'],
 ['58同城','http://t.58.com'],
 ['爱帮团','http://tuan.aibang.com'],
 ['饭统饭团','http://tuan.fantong.com'],
 ['24券','http://www.24quan.com'],
 ['拉手','http://www.lashou.com'],
 ['新浪团','http://life.sina.com.cn/tuan'],
 ['爱家团','http://ihome.sohu.com/today'],
 ['联合购买网','http://www.cobuy.net'],
 ['团好网','http://www.tuanok.com'],
 ['1039团','http://www.bj1039.com'],
 ['万酷团','http://www.wancoo.com'],
 ['北青团购','http://tuangou.ynet.com'],
 ['第一团购网','http://www.tuan001.com'],
 ['酷团','http://kutuan.com'],
 ['摄团','http://t.sheying001.com'],
 ['团委会','http://www.tuanweihui.com/Index.html'],
 ['优体优团','http://t.yoti.cn'],
 ['网团网','http://www.nettuan.com'],
 ['生活帮','http://shequ.soufun.com/g'],
 ['窝窝团','http://www.55tuan.com'],
 ['拼客网','http://www.17p.com'],
 ['团酷','http://www.tuanku.com'],
 ['大洋团','http://www.dytuan.com'],
 ['一起网','http://yiqi001.com/index.php'],
 ['满座','http://www.manzuo.com'],
 ['口碑团购','http://promotion.koubei.com/s/youhui/fengqiang.htm'],
 ['拼团','http://www.pintuan.com'],
 ['魔力团','http://www.molituan.com'],
 ['Like团','http://liketuan.com'],
 ['可购可乐','http://www.cogo-cola.com'],
 ['葫芦团','http://www.hulutuan.com'],
 ['珍惜','http://www.zhenxi.com'],
 ['愉悦团','http://tuan.yuyue.com'],
 ['喜爱团','http://www.xiaituan.com'],
 ['团一把','http://www.tuanyiba.com/index.php?m=Index&a=index&'],
 ['品团','http://www.ppptuan.com'],
 ['簇团网','http://www.cutuan.cn'],
 ['抱团乐','http://www.baotuanle.com'],
 ['团咪咪','http://tuanmimi.com/index.php'],
 ['健团网','http://tuan.51fit.com'],
 ['顶团','http://www.toptuan.com'],
 ['好特会','http://www.haotehui.com'],
 ['联团网','http://liantuan001.com'],
 ['快乐团','http://www.kltuan.com'],
 ['敢买网','http://www.thankbuy.com'],
 ['黄金团','http://huangjintuan.com'],
 ['久久合购','http://www.99hego.com'],
 ['团购王','http://www.go.cn'],
 ['好买都市网','http://www.ihaomy.com'],
 ['梧桐团','http://t.wutoo.cn/index.php'],
 ['赶趟儿','http://www.gantang.net/index.php'],
 ['站台团购','http://www.zhantaitg.com/index.php'],
 ['折学系','http://www.zhexuexi.com'],
 ['零点团购','http://www.tg00.com'],
 ['九两半','http://9liangban.com/index.php'],
 ['每团网','http://www.meituan.hk'],
 ['易起买','http://www.17buy.com'],
 ['团聚网','http://www.tuanju365.com'],
 ['找乐网乐团','http://www.t.zhaole.net'],
 ['嘀嗒团','http://didatuan.com'],
 ['秒货网','http://www.miaohoo.com'],
 ['ours团','http://www.ourspass.com/groupon'],
 ['巨乐团','http://juletuan.com'],
 ['团动网','http://www.tuaning.cn'],
 ['优乐团','http://www.youletuan.com'],
 ['哈尔滨团购网','http://www.0451tuan.com'],
 ['踩团网','http://www.maxtuan.com'],
 ['青岛团','http://www.qingdaotuan.com'],
 ['福团网','http://www.futuan.com/index.php?m=Index&a=index&ci'],
 ['大众点评团','http://t.dianping.com'],
 ['搜福团','http://www.fzsou.com'],
 ['拍手网','http://www.paishou.com'],
 ['酷团网','http://www.cooltuan.com'],
 ['5151团','http://www.5151tuan.com'],
 ['团单网','http://www.tuandan.com'],
 ['壹圈网','http://www.1-ooo.com'],
 ['团拜网','http://www.tuanbai.cn'],
 ['济团网','http://www.jinantuan.com'],
 ['淘玩购','http://www.twangou.com'],
 ['赶团网','http://www.runtuan.com'],
 ['维团网','http://www.wituan.com'],
 ['聚惠网','http://juhui-net.com/main/index.php'],
 ['团团网','http://www.tuantuanwang.com'],
 ['水浒团','http://www.shuihutuan.com'],
 ['哇塞团购网','http://www.waasaa.com'],
 ['团团360','http://www.tuantuan360.com'],
 ['一起买（深圳）','http://www.yiqimai.com.cn'],
 ['1团网','http://www.1tuan.com'],
 ['西安团','http://www.tuan029.com'],
 ['团一团','http://www.tuan1tuan.com'],
 ['蓉团网','http://www.rotuan.com'],
 ['每团吧','http://www.meituan8.com'],
 ['UP团','http://www.uptuan.com'],
 ['郑团网','http://tuan.my0371.com'],
 ['27团','http://www.27tuan.com'],
 ['武汉团购网','http://www.tg027.com'],
 ['洗米弄','http://www.ximilong.com'],
 ['泡泡团','http://www.popotuan.com'],
 ['闪团网（厦门）','http://www.shantuan.cn'],
 ['千百惠','http://www.groupon365.com'],
 ['蜜团网','http://mtw365.com'],
 ['聚团吧','http://www.jutuanba.com'],
 ['绕团网','http://www.raotuan.com'],
 ['团团赚','http://www.t543.com'],
 ['卡卡团','http://www.kakatuan.com'],
 ['团乐购','http://www.tuan188.com'],
 ['聚齐网','http://www.juqi.com'],
 ['趁热团','http://www.chenre.net'],
 ['186团购网','http://www.186tg.com'],
 ['草编团','http://tg.6159.com'],
 ['葫团','http://www.hld360.com'],
 ['买尚网','http://www.buyup.cn'],
 ['如意团','http://www.ruyituan.com'],
 ['抄底团','http://chaodtuan.com'],
 ['叮当团','http://www.ding-shopping.com'],
 ['贝贝团','http://beibeituan.com/index.php'],
 ['爱去网','http://www.2727.com/pai'],
 ['e精美团购','http://www.ejingmei.com'],
 ['哇噢团','http://www.waotuan.com'],
 ['称职团','http://www.chenzhituan.com'],
 ['围团网','http://www.wetuan.com'],
 ['蒂噢网','http://www.diioo.com'],
 ['陈太太','http://www.chentt.com'],
 ['姐妹团','http://www.sistuan.com'],
 ['楊淩团','http://www.yang-ling.cn'],
 ['快团网','http://www.fastuan.com'],
 ['聚划算','http://ju.taobao.com'],
 ['YOKA优享团','http://tuan.yoka.com'],
 ['猛买网','http://www.mengmai.com'],
 ['折扣团','http://www.zktuan.com'],
 ['QQ商城团购','http://shop.qq.com/act/tuan'],
 ['517户外','http://tuan.517huwai.com/Tuangou'],
 ['17团团','http://www.17tuantuan.com'],
 ['楚团网','http://www.hubeir.com'],
 ['爱爱团','http://www.aiaituan.com'],
 ['德赛团','http://www.dejindianqi.com'],
 ['爱尚护肤团','http://tuan.isunnet.com'],
 ['豆买团','http://doomop.com'],
 ['爱着数','http://www.izoso.com'],
 ['米薇网','http://www.idoutuan.com'],
 ['白菜团','http://www.baicaituan.com'],
 ['囤团','http://play.2010.sina.com.cn/tuan.php'],
 ['百货团','http://tuan.baihuo.com'],
 ['GGGDA','http://www.gggda.com'],
 ['捞面网','http://lowmin.com'],
 ['百丽团','http://bailituan.com'],
 ['茶苑网','http://www.teaparks.com'],
 ['今日团购','http://jr.teamgocn.com'],
 ['潘多拉','http://www.ipandoro.com'],
 ['PClady快团','http://tuan.pclady.com.cn'],
 ['宠团网','http://www.c2sj.com'],
 ['热团','http://www.retuan.com'],
 ['酷拼网','http://www.kupin365.com'],
 ['日日团','http://www.rirituan.com'],
 ['一米团','http://www.1mituan.com'],
 ['淘街客','http://www.taojieke.com'],
 ['犀牛秒杀团','http://www.xnms.com.cn'],
 ['我趣团','http://www.woqutuan.com'],
 ['喜团网','http://www.xituan.com'],
 ['西团网','http://www.gxtuan.com'],
 ['团老大','http://www.tuanlaoda.com'],
 ['卓团网','http://www.zhuotuan.com'],
 ['团密网','http://www.tuanmiss.com'],
 ['123团','http://www.123tuan.com/index.php'],
 ['爱恋网','http://love.igold.com.cn'],
 ['12579团','http://www.12579.cn'],
 ['彩蛋网','http://www.caidan365.com'],
 ['阿拉团','http://tg.shanghaidz.com'],
 ['17买好','http://www.17mh.com'],
 ['搭配网','http://www.dapeiwang.com/eshop.html'],
 ['得买','http://www.deimai.com'],
 ['百代团','http://baidait.com/index.php'],
 ['合众网','http://www.ietbn.cn'],
 ['东团网','http://www.gezib.cn'],
 ['败妆网','http://byzhuang.com'],
 ['火团网','http://huotuanwang.com'],
 ['靓丽团','http://llituan.com/index.php'],
 ['不如团','http://www.burutuan.com/index.php'],
 ['格鲁团','http://www.gelu.com'],
 ['良品团购','http://liangpin.dekind.cn'],
 ['闺蜜团','http://www.misstuan.com'],
 ['零六团','http://06.com.cn/index.php'],
 ['可爱团','http://deartuan.com'],
 ['MasaMaso','http://www.masamaso.com'],
 ['乐达','http://www.ledalife.com/t'],
 ['乐活团','http://www.lohastuan.com'],
 ['宁夏乐购团','http://www.zhaoyou.net.cn/tg'],
 ['漂亮团','http://pltuan.com'],
 ['尚品酷邦','http://www.qupon.cn'],
 ['牵手团','http://b2btuan.com'],
 ['圣蔻团','http://www.stcoo.com.cn/group/groupbuy.aspx'],
 ['乐团','http://leetuan.com'],
 ['茄团','http://www.qietuan.com/index.php'],
 ['四零零团','http://www.400tuan.com/index.php'],
 ['完美生活','http://www.wanmeish.com'],
 ['人人团','http://www.renrenpop.com'],
 ['鲜花团','http://3t.net.cn/index.php'],
 ['团购儿','http://www.tuangouer.com/index.php'],
 ['校内美团','http://lfeed.com.cn'],
 ['团花儿','http://www.tuanhua.net'],
 ['寻常百姓','http://www.xcbxj.cn'],
 ['全客团','http://tuan.quanke.net'],
 ['扬格团','http://tg.younger365.com'],
 ['团拍','http://www.tuanpai.com'],
 ['婴团','http://t.babyschool.com.cn'],
 ['团拼网','http://www.tuanpin.com'],
 ['一起买啦','http://17maila.com'],
 ['团秀网','http://www.tuanxiu.cn/index.php'],
 ['郑周团','http://tuan.zzbbs.com'],
 ['优活女性团购','http://www.51youhuo.com'],
 ['酒立方','http://tuangou.jiulifang.com/index.php'],
 ['运动团','http://www.ydtuan.com'],
 ['96团','http://www.96tuan.com'],
 ['快抢团','http://www.kqtuan.com'],
 ['发现深圳','http://www.gotofun.cn/buy'],
 ['艺珍珠','http://www.epc8848.cn/c/Promo/PearlTuan.aspx'],
 ['乐拼团购网','http://www.chnpin.com'],
 ['groupdown','http://groupdown.com/index.php'],
 ['优势团','http://www.14tuan.com'],
 ['乐淘','http://www.letao.com'],
 ['乖团','http://www.guaituan.com'],
 ['珠海团','http://www.51pingtai.com/team/index.php'],
 ['海南团','http://www.hainantuangou.com'],
 ['玫瑰团','http://www.meiguituan.com'],
 ['疯狂卖客','http://www.crazymike.cn'],
 ['绵团网','http://www.tg0816.com'],
 ['嘎嘎团','http://www.gagatuan.com'],
 ['能豆王','http://www.nengdou.com'],
 ['很团网','http://www.hentuan.com'],
 ['沙沙团','http://www.sstuan.com'],
 ['双飞团','http://shuangfei365.com'],
 ['火拼团','http://kela.cn/activ/201004huopinmeizuan/index.php'],
 ['团多多','http://www.tuandodo.com'],
 ['团团儿','http://www.tuantuaner.com'],
 ['28团','http://www8.45588.cn'],
 ['策策团','http://www.cecetuan.com'],
 ['贝太生活购','http://go.beitaichufang.com'],
 ['现在团','http://www.xianzaituan.com'],
 ['一齐儿团','http://www.172tuan.com'],
 ['国际团','http://fb.cityweekend.com.cn'],
 ['高清第一团购网','http://go.hd001.org'],
 ['好购团','http://tuan.haogou.cc'],
 ['家有团购','http://t.jiayougo.com/index.php'],
 ['实在','http://www.sszz.net'],
 ['团促网','http://tuancu.com/index.php'],
 ['VC团','http://www.vctuan.com/index.php'],
 ['沃美','http://www.wowmei.com'],
 ['嘀嗒团',  'http://didatuan.com'],
 ['团宝网', 'http://www.groupon.cn'],
 ['找折网', 'http://www.zhaozhe.com'],
 ['悦团网', 'http://www.17yuetuan.com/index.php'],
  ['阿丫团', 'http://www.ayatuan.com/index.php'],
  ['YOUNG团', 'http://www.youngtuan.com/index.php'],
  ['可可团', 'http://www.cocotuan.com'],
  ['赛团网', 'http://www.saituan.com'],
  ['芝麻团','http://zmtuan.9shequ.cn'],
  ['F团','http://www.ftuan.com']
 ];

 
for(var i=0;i<rawmapping.length;i++){
    var el = rawmapping[i];
    mapping[el[0]] = el[1];
}