var pageSize = 50;
var columns = [  'lo', 'la', 'price', 'id','rentalType', 'subRentalType',  'paymentType', 'priceUnit', 'size', 'houseType', 'address', 'city',
        'district1', 'district3', 'district5', 'tel', 'contact', 'photo', 'floor', 'totalFloor', 'isAgent',
        'equipment', 'decoration', 'ok', 'referer', 'createTime', 'updateTime', 'hash' , 'description1', 'description2'];

function loadit() {
    var store = new Ext.data.Store( {
        autoLoad : false,
        remoteSort : true,
        url : '/service/object',
        restful : true,
        baseParams : {
            objectid : 'House'
        },
        reader : new Ext.data.XmlReader( {
            record : 'object',
            id : 'id'
        }, columns),
        paramNames : {
            start : 'start',
            limit : 'count',
            sort : 'orderBy',
            dir : 'order'
        },
        getTotalCount : function() {
            return 9000000;
        }
    });

    var headers = [];
    for ( var i = 0; i < columns.length; i++) {
        var c = columns[i];
        headers.push( {
            header : c,
            dataIndex : c,
            sortable : true
        });
    }
    var grid = new Ext.grid.GridPanel( {
        store : store,
        columns : headers,
        bbar : new Ext.PagingToolbar( {
            pageSize : pageSize,
            store : store,
             displayInfo : true,
            displayMsg : 'Displaying  {0} - {1} of {2}',
            emptyMsg : "Nothing to display"      
        }),
        sm : new Ext.grid.RowSelectionModel( {
            singleSelect : true,
            listeners : {
                rowselect : function(sm, row, rec) {
                    console.log(rec);
                    Ext.getCmp("form1").getForm().loadRecord(rec);
                }
            }
        })

    });
    var fields = [];
    for ( var i = 0; i < columns.length; i++) {
        var c = columns[i];
        fields.push( {
            fieldLabel : c,
            name : c
        });
    }
    fields[fields.length-1].xtype='textarea';
    fields[fields.length-1].width =160;
    fields[fields.length-1].height =100;
    fields[fields.length-2].xtype='textarea';
    fields[fields.length-2].width =160;
    fields[fields.length-2].height =80;
    
    var form = new Ext.FormPanel( {
        id : 'form1',
        frame : true,
        defaultType : 'textfield',
        labelWidth : 90,
        width : 300,                
        items : fields,
        autoScroll:true,
        buttons : [ {
            text : 'Search',
            handler : function(button, event) {
                var values = form.getForm().getValues();
                var name = validQueryParam(values);
                if(name){
                    alert(name + ' is not in a valid format, fix or just leave it blank');
                    return false;
                }
                store.load( {
                    params : values
                });
            }
        } ]
    });

    grid.region = 'center';
    form.region = 'west';
    form.collapsible = true;
    form.split = true;
    new Ext.Viewport( {
        layout : 'border',
        items : [ grid, form ]
    });

    store.load( {
        params : {
            start : 0,
            count : pageSize
        }
    });
}


function validQueryParam(params){
    var names = ['price', 'lo', 'la'];
    for(var i=0;i<names.length;i++){
        var s = params[names[i]];
        if(s && s.length>0){
            if(s.indexOf('-')==-1){
                return names[i];
            }
        }
    }
    return null;
}

Ext.onReady(loadit);