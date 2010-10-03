function handlerProcess() {
    CrUtil.removeElementsByTagName('script');
    var s = CrUtil.removeNewLine(XPath.single(null, '//div[@class="mainBox"]').textContent);
    s = s.replace('给发布人留言', '  ');
    var objInfo1 = [ {
        name : 'trainNum',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /车次:\s*(\S+)/
    }, {
        name : 'ticketDate',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /发车时间:\s*(\S+)/
    }, {
        name : 'type',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /车票类型:\s*(\S+)/
    }, {
        name : 'ticketCount',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /数量:\s*(\S+)/
    }, {
        name : 'place',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /取票地点:\s*(\S+)/
    }, {
        name : 'contact',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /联系人:\s*(\S+)/
    }, {
        name : 'tel',
        op : 'xpath.text.regex',
        param1 : s,
        param2 : /电话:\s*(\S+)/
    }, {
        name : 'note',
        op : 'xpath.textcontent.regex',
        param1 : "//div[@class='mainBox detailInfo']"
    } ];

    var obj = HandlerHelper.parseObject(objInfo1);

    var route = XPath.single(null, '//div[@class="detal_left border_right"]//strong').textContent.split('-->');
    obj.origin = route[0];
    obj.dest = route[1];
    obj[CrGlobal.ParameterName_ObjectId] = CrGlobal.TrainTicketObjectId;
    if (obj.note)
        obj.note = obj.note.replace('详细信息', '');
    if (obj.note.length > 480)
        obj.note = obj.note.substring(0, 480) + '...';
    if (obj.ticketCount)
        obj.ticketCount = obj.ticketCount.replace('张', '');
    CrUtil.trimAttributes(obj);
    HandlerHelper.postObject(obj, {
        action : 'Goto.Next.Link'
    });
}
