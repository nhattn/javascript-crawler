var Page = {
    pageSize : 50,
    columns : [ 'lo', 'la', 'price', 'id', 'rentalType', 'subRentalType', 'paymentType', 'priceUnit', 'size', 'houseType', 'address', 'city',
            'district1', 'district3', 'district5', 'tel', 'contact', 'photo', 'floor', 'totalFloor', 'isAgent', 'equipment', 'decoration', 'ok',
            'referer', 'createTime', 'updateTime', 'hash', 'description1', 'description2' ],
    ignoreList :['不限', '全上海', '上海'],    
    searchOptions: [
        {title:'区域', name:'district1',  useLike:true,  inputWidth:100, options:[{title:'全上海', value:''}, '闵行', '浦东', '普陀', '徐汇', '黄浦', '长宁', '宝山', '松江', '闸北', '虹口', '静安', '杨浦', '青浦', '南汇', '嘉定', '卢湾', '贵龙园', '嘉兴']},
        {title:'价格', name:'price',      useLike:false, inputWidth:100, options:[{title:'不限', value:''}, {title:'1000以下', value:'-1000'}, {title:'1500以下', value:'-1500'},{title:'2000以下', value:'-2000'},{title:'3000以下', value:'-3000'},{title:'4000以下', value:'-4000'} ]},
        {title:'大小', name:'size',       useLike:false,  inputWidth:100, options:[{title:'不限', value:''}, {title:'50以下', value:'-50'}, {title:'100以下', value:'-100'},{title:'200以下', value:'-200'},{title:'200以上', value:'200-'}]},
        {title:'类型', name:'rentalType', useLike:false, inputWidth:100, options:[{title:'不限', value:''}, '出租', '合租', '出售']},
        {title:'房屋描述关键字', name:'description2', useLike:true, inputWidth:120, options:[]},
    ],            
    
    defaultParams : {
        objectid : 'House',
    },    
    
    init: function(){
        this.defaultParams.count = this.pageSize;
        for(var i=0;i<this.searchOptions.length;i++){
            this.ignoreList.push(this.searchOptions[i].title);
        }
    },
    
    buildSearchOptions: function(){
        var options = this.searchOptions, items = [];
        for(var i=0;i<options.length;i++){
            var op = options[i];
            var subItems = [
                {text:op.title, cls:'search_option_title'},
                {xtype:'textfield', emptyText:op.title, cls:'search_option_textfield', useLike:op.useLike, width:op.inputWidth||100, name: op.name, enableKeyEvents:true,
                    listeners:{
                        'keypress':function(f, e){
                            if(e.getKey()==13){e.preventDefault();this.doSearch();}
                            else {f.userSet = true;console.log(e);}
                        }, 
                        scope:this
                    }
                }
            ];            
            var name = op.name;  
            var hasSelected = false;
            for(var j=0;j<op.options.length;j++){
                var option = op.options[j], value, title, cls, html;
                if(typeof option == 'string'){
                    value = option; 
                    title = option;
                    cls = 'search_option';
                }else{
                    value = option.value;
                    title = option.title;
                    if(option.selected){
                        cls = 'search_option_selected';
                        hasSelected = true;                        
                    }else{
                        cls = 'search_option';
                    }                    
                }
                if(op.useLike&&value) value = '%'+value +'%';                                    
                subItems.push({xtype:'mlabel', text:title, cls:cls, value:value, name:name, listeners:{'click':this.labelClick, scope:this}});            
            }
            if(!hasSelected && subItems.length>2)
                subItems[2].cls='search_option_selected';            
            items.push({items:subItems});            
        }        
        return items;
    },
    
    labelClick: function(label){        
        if(label.el.hasClass('search_option_selected')) 
            return;        
        else{
            var previousSelected = this.getSelectedLabels()[label.name];
            if(previousSelected) {
                previousSelected.el.removeClass('search_option_selected');
                previousSelected.el.addClass('search_option');
            }
            label.el.addClass('search_option_selected');
            var input = label.ownerCt.el.query('.search_option_textfield');            
            if(input && input.length>0) {
                input = Ext.getCmp(input[0].id);
                input.setValue(label.text);
                input.pValue = label.value;
                input.userSet = false;
            }
        }
        this.doSearch();
    },
    
    getSelectedLabels: function(){
        var els = Ext.getBody().query('.search_option_selected'), r = {};        
        for(var i=0;i<els.length;i++){            
            var e = Ext.getCmp(els[i].id);
            r[e.name] = e;
        }
        return r;
    },
    
    getSearchForm: function(){
        var panel = new Ext.Panel({            
            id:'searchBox', defaults:{border:false, defaults: {xtype:'label', style:'padding-right:10px;'}, padding:5},
            items: this.buildSearchOptions()            
        });
        return panel;
        
        var panel = new Ext.Panel({
            defaults:{xtype:'label', style:'padding-right:10px;'}, padding:5,
            items: [{text:'区域', cls:'search_option_title'}, {text:'全上海', cls:'search_option_selected'}]
        });
        return panel;
    },
    
    getSouth: function(){
        var panel = new Ext.TabPanel({
            width:'100%', height:200, region:'south', title:'xx', split:true, collapsed:false,activeTab: 0, collapsible: true, 
            items:[
                {title:'查询', items:[this.searchForm], heigth:'100%'},
                {title:'记录详细信息', items:[
                    {xtype:'mlabel', text:'dkdk', value:'value1', 
                        listeners:{
                          'click':function(label){label.el.addClass('search_option_selected'); }
                        }
                    }
                ]}
            ]
        });
        return panel;
    },
    getCenter : function() {
        var headers = [];
        for ( var i = 0; i < this.columns.length; i++) {
            var c = this.columns[i];
            headers.push( {header : c, dataIndex : c,sortable : true});
        }
        var grid = new Ext.grid.GridPanel( {
            store : this.store, columns : headers, region:'center',
            bbar : new Ext.PagingToolbar( {
                pageSize : this.pageSize,
                store : this.store,
                displayInfo : true,
                displayMsg : 'Displaying  {0} - {1} of {2}',
                emptyMsg : "Nothing to display"      
            }),
            sm : new Ext.grid.RowSelectionModel( {
                singleSelect : true,
                listeners : {
                    rowselect : function(sm, row, rec) {                        
                        Page.south.expand();
                    }
                }
            })
        });
        return grid;
    },

    getStore : function() {
        var store = new Ext.data.Store( {
            autoLoad : false,
            remoteSort : true,
            url : '/service/object',
            restful : true,
            baseParams : this.defaultParams,
            reader : new Ext.data.XmlReader( {
                record : 'object',
                id : 'id'
            }, this.columns),
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
        return store;
    },
    
    buildUI: function(){
        this.init();
        this.store = this.getStore();
        this.searchForm = this.getSearchForm();
        this.center = this.getCenter();
        this.south = this.getSouth();        
        new Ext.Viewport( {
            layout : 'border',
            items : [ this.center , this.south]
        });
        this.doSearch();
    },
    
     search:function(params){
        if(!params){
            params = {start:0};
        }
        var nparams = {};
        Ext.apply(nparams, params);
        Ext.apply(nparams, this.defaultParams);
        this.store.baseParams = nparams;
        this.store.load();
    },
    
    getSearchParameters: function(){
        var inputs = Ext.getCmp('searchBox').el.query('.search_option_textfield'), params = {};
        var labels = this.getSelectedLabels();
        for(var i =0;i<inputs.length;i++){
            var c = Ext.getCmp(inputs[i].id);
            var value = c.userSet?c.getValue() : c.pValue;
            console.log(value);
            if(value && this.ignoreList.indexOf(value)==-1){
                params[c.name] = value;
            }
        }        
        return params;
    },
    
    doSearch:function(){
        var searchParameters = this.getSearchParameters();        
        this.search(searchParameters);
    }
}

Ext.onReady(Page.buildUI, Page);

Ext.MyLabel = Ext.extend(Ext.form.Label, {
    initComponent : function(){
        Ext.MyLabel.superclass.initComponent.call(this);
        this.addEvents('click');
    },    
    
    onRender : function(ct, position){
        Ext.MyLabel.superclass.onRender.call(this, ct, position);
        this.el.on('click', function(){this.fireEvent('click', this);}, this);
    }
});

Ext.reg('mlabel', Ext.MyLabel);