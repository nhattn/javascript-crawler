function getEntries() {    
    var tables = XPath.array(null, '//table[@class="yuBaoTable"]');
    if (!tables || !tables.length) {
        return null;
    }
    var r = [];
    var location = window.location.toString().match(/([0-9]+)\.shtml/)[1];    
    for ( var i = 0; i < tables.length; i++) {        
        var table = tables[i];
        var rows = table.rows;
        var entry = {};
        for ( var j = 0; j < rows.length; j++) {            
            var row = rows[j];
            var cells = row.cells
            var startIndex = 0;
            if (cells.length == 7) {
                startIndex = 1;
            }            
            if (rows.length == 1 && i==0) {                
                entry['condition' + startIndex] = cells[startIndex + 2].innerText;
                entry['temp' + startIndex] = getNumber(cells[startIndex + 3].innerText);
                entry['wind' + startIndex] = cells[startIndex + 4].innerText;
                entry['strength' + startIndex] = cells[startIndex + 5].innerText;        
            } else {
                entry['condition' + (1 - startIndex)] = cells[startIndex + 2].innerText;
                entry['temp' + (1 - startIndex)] = getNumber(cells[startIndex + 3].innerText);
                entry['wind' + (1 - startIndex)] = cells[startIndex + 4].innerText;
                entry['strength' + (1 - startIndex)] = cells[startIndex + 5].innerText;              
            }
            entry.locationId=location;
            entry.castDate = getDate(i);
        }
        CrUtil.trimAttributes(entry);
        r.push(entry);
    }
    return r;
}

function getNumber(r) {
    if(!r){
        return '';
    }    
    return r.match(/\-?[0-9]+/)[0]
}

function getDate(offset) {
    var d = new Date();
    d.setDate(d.getDate() + offset);
    return CrUtil.dateString(d);
}

function handlerProcess() {
    var r = getEntries();
    if (r == null) {
        Crawler.nextLink();
    } else {
        r = {
            objectid : 'Weather',
            data : r
        };
        var data = {
            format : 'json',
            'jsondata' : Ext.util.JSON.encode(r),
            skipUrlCheck : true
        };
        HandlerHelper.postObject(data);
    }
}
