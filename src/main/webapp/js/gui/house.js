var pageSize = 50;
var columns = [  'lng', 'lat', 'price', 'id','rentalType', 'subRentalType',  'paymentType', 'priceUnit', 'size', 'houseType', 'address', 'city',
        'district1', 'district3', 'district5', 'tel', 'contact', 'photo', 'floor', 'totalFloor', 'isAgent', 'agentPhoto',
        'equipment', 'decoration', 'ok', 'referer', 'createTime', 'updateTime', 'hash' , 'description1', 'description2'];

var defaultParams = {
    objectid : 'House',
    count : pageSize
};

function loadit() {
    var store = new Ext.data.Store( {
        autoLoad : false,
        remoteSort : true,
        url : '/service/object',
        restful : true,
        baseParams : defaultParams,
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
                    Ext.getCmp("form1").getForm().loadRecord(rec);
                    var photo = rec.data.photo, agentPhoto=rec.data.agentPhoto;                    
                    var imagePanel = Ext.getCmp("imagePanel");                    
                    imagePanel.removeAll(true);
                    if(!photo && !agentPhoto){                    
                        imagePanel.collapse();
                        return;
                    }
                    
                    imagePanel.expand();                    
                    var elements = [];       
                    if(agentPhoto && agentPhoto.length!=0){
                        elements.push({xtype:'panel', html:'<img src="/service/image?name='+agentPhoto+'">'});
                    }
                    if(photo && photo.length!=0){
                        photo = photo.split(';');
                        for(var i=0;i<photo.length;i++){
                            elements.push({xtype:'panel', html:'<img src="/service/image?name='+photo[i]+'">'});
                        }
                    }                    
                    imagePanel.add(elements);
                    imagePanel.doLayout();
                    
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
                var values = form.getForm().getValues(), param={};
                Ext.apply(param, defaultParams);
                Ext.apply(param, values);
                store.baseParams = param;
                store.load();                
            }
        } ]
    });
    
    var imagePanel = new Ext.Panel({
        id:'imagePanel',
        width:300,
        height:'100%',
        autoScroll:true,        
        collapsed:true
    });
        
    
    grid.region = 'center';
    form.region = 'west';
    imagePanel.region = 'east';
    
    form.collapsible = true;
    form.split = true;
    
    imagePanel.collapsible = true;
    imagePanel.split = true;
    
    new Ext.Viewport( {
        layout : 'border',
        items : [ grid, form, imagePanel ]
    });

    store.load( {
        params : {
            start : 0            
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